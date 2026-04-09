/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.service.root

import android.content.Context
import android.content.Intent
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import com.github.yumelira.yumebox.service.RootTunService
import com.github.yumelira.yumebox.service.common.util.appContextOrSelf
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RootTunStartCoordinator {
    suspend fun start(
        context: Context,
        request: RootTunStartRequest,
        callerTag: String,
        fallbackCode: RuntimeGatewayErrorCode,
        fallbackMessage: String,
    ): RootTunOperationResult {
        val appContext = context.appContextOrSelf
        val startupLogStore = RootTunStartupLogStore(appContext)
        val stateStore = RootTunStateStore(appContext)
        val startedAt = System.currentTimeMillis()

        startupLogStore.append(
            "ROOT_TUN $callerTag: prepare=${System.currentTimeMillis() - startedAt}ms"
        )
        stateStore.updateStatus(
            stateStore
                .snapshot()
                .copy(
                    state = RootTunState.Starting,
                    running = true,
                    runtimeReady = false,
                    controllerReady = true,
                    startedAt = startedAt,
                    lastErrorCode = null,
                    lastError = null,
                )
        )

        val foregroundStartAt = System.currentTimeMillis()
        RootTunService.start(appContext)
        startupLogStore.append(
            "ROOT_TUN $callerTag: fgService=${System.currentTimeMillis() - foregroundStartAt}ms"
        )

        val result =
            runCatching {
                    RootTunRemoteClient.remoteCall(appContext) { service ->
                        val resultJson = service.startRootTun(RootTunJson.encode(request))
                        RootTunJson.decode<RootTunOperationResult>(resultJson)
                    }
                }
                .getOrElse { error ->
                    startupLogStore.append(
                        "ROOT_TUN $callerTag: total=${System.currentTimeMillis() - startedAt}ms failed=${error.message}"
                    )
                    return error.toRootTunOperationResult(
                        fallbackCode = fallbackCode,
                        fallbackMessage = fallbackMessage,
                    )
                }
        startupLogStore.append(
            "ROOT_TUN $callerTag: remoteStart=${System.currentTimeMillis() - foregroundStartAt}ms"
        )
        if (!result.success) {
            startupLogStore.append(
                "ROOT_TUN $callerTag: total=${System.currentTimeMillis() - startedAt}ms"
            )
            return result
        }

        return try {
            stateStore.updateStatus(queryStatus(appContext))
            startupLogStore.append(
                "ROOT_TUN $callerTag: total=${System.currentTimeMillis() - startedAt}ms"
            )
            result
        } catch (error: Throwable) {
            val rollbackResult =
                withContext(Dispatchers.IO) {
                    runCatching {
                            val resultJson = RootTunRemoteClient.bind(appContext).stopRootTun()
                            RootTunJson.decode<RootTunOperationResult>(resultJson)
                        }
                        .getOrNull()
                }
            runCatching { RootTunService.stop(appContext) }
            runCatching { RootService.stop(Intent(appContext, RootTunRootService::class.java)) }
            RootTunRemoteClient.disconnect()

            val message = buildString {
                append(error.message ?: "failed to query RootTun status after start")
                rollbackResult
                    ?.error
                    ?.takeIf { it.isNotBlank() }
                    ?.let { append(" | rollback: ").append(it) }
            }
            startupLogStore.append(
                "ROOT_TUN $callerTag: total=${System.currentTimeMillis() - startedAt}ms failed=$message"
            )
            RootTunOperationResult(
                success = false,
                errorCode = RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED,
                error = message,
            )
        }
    }

    private suspend fun queryStatus(context: Context): RootTunStatus {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<RootTunStatus>(service.queryStatus())
        }
    }

    private fun Throwable.toRootTunOperationResult(
        fallbackCode: RuntimeGatewayErrorCode,
        fallbackMessage: String,
    ): RootTunOperationResult {
        val gateway = this as? RuntimeGatewayException
        val message =
            gateway?.message?.takeIf { it.isNotBlank() }
                ?: message?.takeIf { it.isNotBlank() }
                ?: fallbackMessage
        return RootTunOperationResult(
            success = false,
            errorCode = gateway?.code ?: fallbackCode,
            error = message,
        )
    }
}

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
import com.github.yumelira.yumebox.core.model.ConnectionSnapshot
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import com.github.yumelira.yumebox.service.RootTunService
import com.github.yumelira.yumebox.service.common.util.appContextOrSelf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object RootTunServiceBridge {
    suspend fun start(context: Context): RootTunOperationResult {
        val appContext = context.appContextOrSelf
        val request = RootTunStartRequest(source = "service.bridge.start")
        val result =
            runCatching {
                    withContext(Dispatchers.IO) {
                        val service = RootTunRemoteClient.bind(appContext)
                        val resultJson = service.startRootTun(RootTunJson.encode(request))
                        RootTunJson.decode<RootTunOperationResult>(resultJson)
                    }
                }
                .getOrElse { error ->
                    return error.toRootTunOperationResult(
                        fallbackCode = RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
                        fallbackMessage = "RootTun service bridge start failed",
                    )
                }
        if (result.success) {
            RootTunService.start(appContext)
        }
        return result
    }

    suspend fun stop(context: Context): RootTunOperationResult {
        val result =
            RootTunRemoteClient.remoteCall(
                context = context,
                onBinderFailure = { RootTunOperationResult(success = true) },
            ) { service ->
                val resultJson = service.stopRootTun()
                RootTunJson.decode<RootTunOperationResult>(resultJson)
            }
        RootTunRemoteClient.disconnect()
        return result
    }

    suspend fun queryStatus(context: Context): RootTunStatus {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<RootTunStatus>(service.queryStatus())
        }
    }

    suspend fun queryTrafficNow(context: Context): Long {
        return RootTunRemoteClient.remoteCall(context) { service -> service.queryTrafficNow() }
    }

    suspend fun queryTrafficTotal(context: Context): Long {
        return RootTunRemoteClient.remoteCall(context) { service -> service.queryTrafficTotal() }
    }

    suspend fun queryConnections(context: Context): ConnectionSnapshot {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<ConnectionSnapshot>(service.queryConnectionsJson())
        }
    }

    suspend fun closeConnection(context: Context, id: String): Boolean {
        return RootTunRemoteClient.remoteCall(context) { service -> service.closeConnection(id) }
    }

    suspend fun closeAllConnections(context: Context) {
        RootTunRemoteClient.remoteCall(context) { service -> service.closeAllConnections() }
    }

    private fun Throwable.toRootTunOperationResult(
        fallbackCode: RuntimeGatewayErrorCode,
        fallbackMessage: String,
    ): RootTunOperationResult {
        val gateway = this as? RuntimeGatewayException
        return RootTunOperationResult(
            success = false,
            errorCode = gateway?.code ?: fallbackCode,
            error =
                gateway?.message?.takeIf { it.isNotBlank() }
                    ?: message?.takeIf { it.isNotBlank() }
                    ?: fallbackMessage,
        )
    }
}

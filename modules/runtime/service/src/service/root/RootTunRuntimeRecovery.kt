/*
 * This file is part of MonadBox - A customized edition of YumeBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.service.root

import android.content.Context
import android.content.Intent
import android.os.DeadObjectException
import android.os.IInterface
import android.os.RemoteException
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.service.RootTunService
import com.github.nomadboxlab.monadbox.service.StatusProvider
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.TimeoutCancellationException

object RootTunRuntimeRecovery {
    const val TRANSITION_STALE_TIMEOUT_MS = 10_000L

    private val bindingFailureMarkers =
        listOf("root tun binder is null", "root tun service returned null binding", "binding died")

    fun isBinderAlive(service: IInterface?): Boolean {
        val remote = service?.asBinder() ?: return false
        return remote.isBinderAlive && remote.pingBinder()
    }

    fun isBinderConnectionFailure(error: Throwable): Boolean {
        return generateSequence(error) { it.cause }
            .any { cause ->
                cause is DeadObjectException ||
                    cause is RemoteException ||
                    cause is TimeoutCancellationException ||
                    (cause is IllegalStateException &&
                        bindingFailureMarkers.any { marker ->
                            cause.message?.contains(marker, ignoreCase = true) == true
                        })
            }
    }

    fun binderFailureReason(error: Throwable): String {
        return generateSequence(error) { it.cause }
            .mapNotNull { cause -> cause.message?.trim()?.takeIf(String::isNotEmpty) }
            .firstOrNull() ?: "RootTun IPC disconnected"
    }

    fun isStaleTransition(
        status: RootTunStatus,
        nowMillis: Long = System.currentTimeMillis(),
    ): Boolean {
        if (!status.state.isRecovering) return false
        val startedAt = status.startedAt ?: return true
        if (startedAt <= 0L) return true
        return nowMillis - startedAt >= TRANSITION_STALE_TIMEOUT_MS
    }

    fun staleTransitionReason(
        status: RootTunStatus,
        nowMillis: Long = System.currentTimeMillis(),
    ): String {
        val elapsed = status.startedAt?.let { nowMillis - it }?.coerceAtLeast(0L)
        val elapsedText = elapsed?.let { "${it / 1000}s" } ?: "unknown"
        return "RootTun ${status.state.name} did not reach a terminal state within " +
            "${TRANSITION_STALE_TIMEOUT_MS / 1000}s (elapsed=$elapsedText)"
    }

    fun recoverStaleTransition(
        context: Context,
        status: RootTunStatus = RootTunStateStore(context.appContextOrSelf).snapshot(),
        nowMillis: Long = System.currentTimeMillis(),
    ): RootTunStatus {
        if (!isStaleTransition(status, nowMillis)) return status

        val appContext = context.appContextOrSelf
        val stateStore = RootTunStateStore(appContext)
        val message = staleTransitionReason(status, nowMillis)
        stateStore.markIdle(
            error = message,
            errorCode = RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
        )
        StatusProvider.markRuntimeStopped(ProxyMode.RootTun)
        runCatching { RootTunService.stop(appContext) }
        runCatching { RootService.stop(Intent(appContext, RootTunRootService::class.java)) }
        runCatching {
            appContext.sendBroadcast(
                Intent(Intents.actionClashStopped(appContext.packageName))
                    .setPackage(appContext.packageName)
                    .putExtra(Intents.EXTRA_STOP_REASON, message)
            )
        }
        return stateStore.snapshot()
    }

    fun handleBinderGone(context: Context, reason: String?) {
        val appContext = context.appContextOrSelf
        val stateStore = RootTunStateStore(appContext)
        val previous = stateStore.snapshot()
        val hadRuntime = previous.state.isActive || previous.runtimeReady
        val message = reason?.takeIf { it.isNotBlank() } ?: previous.lastError

        if (hadRuntime || !message.isNullOrBlank()) {
            stateStore.markIdle(
                error = message,
                errorCode = RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
            )
        }

        StatusProvider.markRuntimeStopped(ProxyMode.RootTun)
        runCatching { RootTunService.stop(appContext) }
        runCatching { RootService.stop(Intent(appContext, RootTunRootService::class.java)) }

        if (!hadRuntime) return

        runCatching {
            appContext.sendBroadcast(
                Intent(Intents.actionClashStopped(appContext.packageName))
                    .setPackage(appContext.packageName)
                    .putExtra(Intents.EXTRA_STOP_REASON, message)
            )
        }
    }
}

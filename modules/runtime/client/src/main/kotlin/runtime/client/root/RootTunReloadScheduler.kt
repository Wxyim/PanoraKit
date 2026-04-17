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

package com.github.nomadboxlab.monadbox.runtime.client.root

import android.content.Context
import android.content.Intent
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.root.RootTunOperationResult
import com.github.nomadboxlab.monadbox.service.root.RootTunRuntimeRecovery
import com.github.nomadboxlab.monadbox.service.root.RootTunStateStore
import java.io.Closeable
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*
import timber.log.Timber

internal val ROOT_TUN_RELOAD_RETRY_DELAYS_MS: List<Long> = listOf(0L, 250L, 500L, 1000L)

internal fun shouldRunRootTunReloadAgain(
    dirtyWhileRunning: Boolean,
    pendingReasonCount: Int,
): Boolean = dirtyWhileRunning || pendingReasonCount > 0

internal fun normalizeRootTunReloadFailure(result: RootTunOperationResult): Pair<String, String> {
    val errorCode = (result.errorCode ?: RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED).name
    val error = result.error?.takeIf { it.isNotBlank() } ?: "root runtime reload failed"
    return errorCode to error
}

enum class RootTunReloadReason {
    PROFILE_CHANGED,
    PROFILE_OVERRIDE_CHANGED,
    SESSION_OVERRIDE_CHANGED,
    ROOT_TUN_CONFIG_CHANGED,
}

interface RootTunReloadDispatcher {
    fun schedule(reason: RootTunReloadReason)
}

class RootTunReloadScheduler(context: Context, appScope: CoroutineScope) :
    RootTunReloadDispatcher, Closeable {
    private val appContext = context.appContextOrSelf
    private val scope = CoroutineScope(appScope.coroutineContext + SupervisorJob())
    private val lock = Any()
    private var debounceJob: Job? = null
    private var reloadJob: Job? = null
    private val pendingReasons = linkedSetOf<RootTunReloadReason>()
    private var dirtyWhileRunning = false

    override fun schedule(reason: RootTunReloadReason) {
        synchronized(lock) {
            pendingReasons += reason
            if (reloadJob?.isActive == true) {
                dirtyWhileRunning = true
                return
            }
            debounceJob?.cancel()
            debounceJob =
                scope.launch {
                    delay(DEBOUNCE_MS.milliseconds)
                    runReload()
                }
        }
    }

    private suspend fun runReload() {
        val currentJob = currentCoroutineContext()[Job]
        val reasons =
            synchronized(lock) {
                if (reloadJob?.isActive == true) {
                    dirtyWhileRunning = true
                    return
                }
                val copied = pendingReasons.toSet()
                pendingReasons.clear()
                reloadJob = currentJob
                copied
            }
        if (reasons.isEmpty()) {
            clearReloadJob(currentJob)
            return
        }

        val state =
            RootTunRuntimeRecovery.recoverStaleTransition(
                context = appContext,
                status = RootTunStateStore(appContext).snapshot(),
            )
        if (!state.state.isActive && !state.runtimeReady) {
            clearReloadJob(currentJob)
            return
        }

        val result = syncAndReload(reasons)
        if (!result.success) {
            notifyFailure(result)
        }

        val shouldRunAgain =
            synchronized(lock) {
                val rerun = shouldRunRootTunReloadAgain(dirtyWhileRunning, pendingReasons.size)
                dirtyWhileRunning = false
                if (reloadJob == currentJob) {
                    reloadJob = null
                }
                rerun
            }
        if (shouldRunAgain) {
            delay(DEBOUNCE_MS.milliseconds)
            runReload()
        }
    }

    private suspend fun syncAndReload(reasons: Set<RootTunReloadReason>): RootTunOperationResult {
        Timber.i("RootTun reload: reasons=%s", reasons.joinToString(","))
        return retryReload()
    }

    private suspend fun retryReload(): RootTunOperationResult {
        var lastResult = RootTunOperationResult(success = true)
        for ((index, backoffMs) in ROOT_TUN_RELOAD_RETRY_DELAYS_MS.withIndex()) {
            if (backoffMs > 0L) {
                delay(backoffMs.milliseconds)
            }
            lastResult = RootTunController.reload(appContext)
            if (lastResult.success) {
                return lastResult
            }
            Timber.w(
                "RootTun reload failed attempt=${index + 1}: code=${lastResult.errorCode ?: RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED} error=${lastResult.error}"
            )
        }
        return lastResult
    }

    private fun notifyFailure(result: RootTunOperationResult) {
        val (errorCode, error) = normalizeRootTunReloadFailure(result)
        runCatching {
            appContext.sendBroadcast(
                Intent(Intents.actionRootRuntimeFailed(appContext.packageName))
                    .setPackage(appContext.packageName)
                    .putExtra(Intents.EXTRA_ERROR_CODE, errorCode)
                    .putExtra(Intents.EXTRA_ERROR_MESSAGE, error)
                    .putExtra("error", "$errorCode: $error")
            )
        }
    }

    override fun close() {
        synchronized(lock) {
            debounceJob?.cancel()
            debounceJob = null
            reloadJob?.cancel()
            reloadJob = null
            pendingReasons.clear()
            dirtyWhileRunning = false
        }
        scope.cancel()
    }

    private fun clearReloadJob(currentJob: Job?) {
        synchronized(lock) {
            if (reloadJob == currentJob) {
                reloadJob = null
            }
        }
    }

    private val DEBOUNCE_MS = 100L
}

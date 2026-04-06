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

package com.github.yumelira.yumebox.runtime.client.root

import android.content.Context
import android.content.Intent
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.service.common.constants.Intents
import com.github.yumelira.yumebox.service.common.util.appContextOrSelf
import com.github.yumelira.yumebox.service.root.RootTunOperationResult
import com.github.yumelira.yumebox.service.root.RootTunStateStore
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*
import timber.log.Timber

object RootTunReloadScheduler {
    enum class Reason {
        PROFILE_CHANGED,
        PROFILE_OVERRIDE_CHANGED,
        SESSION_OVERRIDE_CHANGED,
        ROOT_TUN_CONFIG_CHANGED,
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val lock = Any()
    private var debounceJob: Job? = null
    private var reloadJob: Job? = null
    private val pendingReasons = linkedSetOf<Reason>()
    private var dirtyWhileRunning = false
    @Volatile private var suppressNestedSchedule = false

    fun isInternalOverrideSyncInProgress(): Boolean = suppressNestedSchedule

    fun schedule(context: Context, reason: Reason) {
        val appContext = context.appContextOrSelf
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
                    runReload(appContext)
                }
        }
    }

    private suspend fun runReload(context: Context) {
        val reasons =
            synchronized(lock) {
                if (reloadJob?.isActive == true) {
                    dirtyWhileRunning = true
                    return
                }
                val copied = pendingReasons.toSet()
                pendingReasons.clear()
                copied
            }
        if (reasons.isEmpty()) {
            return
        }

        val state = RootTunStateStore(context).snapshot()
        if (!state.state.isActive && !state.runtimeReady) {
            return
        }

        synchronized(lock) {
            reloadJob =
                scope.launch {
                    val result = syncAndReload(context, reasons)
                    if (!result.success) {
                        notifyFailure(context, result)
                    }

                    val shouldRunAgain =
                        synchronized(lock) {
                            val rerun = dirtyWhileRunning || pendingReasons.isNotEmpty()
                            dirtyWhileRunning = false
                            rerun
                        }
                    if (shouldRunAgain) {
                        delay(DEBOUNCE_MS.milliseconds)
                        runReload(context)
                    }
                }
        }
    }

    private suspend fun syncAndReload(
        context: Context,
        reasons: Set<Reason>,
    ): RootTunOperationResult {
        Timber.i("RootTun reload: reasons=%s", reasons.joinToString(","))
        return retryReload(context)
    }

    private suspend fun retryReload(context: Context): RootTunOperationResult {
        val delays = longArrayOf(0L, 250L, 500L, 1000L)
        var lastResult = RootTunOperationResult(success = true)
        for (index in delays.indices) {
            if (delays[index] > 0L) {
                delay(delays[index].milliseconds)
            }
            lastResult = RootTunController.reload(context)
            if (lastResult.success) {
                return lastResult
            }
            Timber.w(
                "RootTun reload failed attempt=${index + 1}: code=${lastResult.errorCode ?: RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED} error=${lastResult.error}"
            )
        }
        return lastResult
    }

    private fun notifyFailure(context: Context, result: RootTunOperationResult) {
        val errorCode = (result.errorCode ?: RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED).name
        val error = result.error?.takeIf { it.isNotBlank() } ?: "root runtime reload failed"
        runCatching {
            context.sendBroadcast(
                Intent(Intents.actionRootRuntimeFailed(context.packageName))
                    .setPackage(context.packageName)
                    .putExtra(Intents.EXTRA_ERROR_CODE, errorCode)
                    .putExtra(Intents.EXTRA_ERROR_MESSAGE, error)
                    .putExtra("error", "$errorCode: $error")
            )
        }
    }

    private const val DEBOUNCE_MS = 100L
}

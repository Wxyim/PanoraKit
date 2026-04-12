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

package com.github.yumelira.yumebox.presentation.runtime

import android.content.Intent
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.presentation.component.GlobalDialogPresenter
import com.github.yumelira.yumebox.presentation.component.RuntimeFailureDialogPresenter
import com.github.yumelira.yumebox.remote.VpnPermissionRequired
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeControlCoordinator
import com.github.yumelira.yumebox.runtime.client.RuntimeMutationResult
import com.github.yumelira.yumebox.runtime.client.RuntimeStateMapper
import dev.oom_wg.purejoy.mlang.MLang
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow

sealed interface RuntimeActionOutcome<out T> {
    data class Success<T>(val value: T) : RuntimeActionOutcome<T>

    data class PermissionRequired(val intent: Intent) : RuntimeActionOutcome<Nothing>

    data object FailureHandled : RuntimeActionOutcome<Nothing>
}

class HandledRuntimeActionFailure : IllegalStateException("Runtime action failure already handled")

sealed interface RuntimeActionFailurePresentation {
    data class Start(val targetMode: ProxyMode, val fallbackMessage: String) :
        RuntimeActionFailurePresentation

    data class Runtime(val fallbackMessage: String, val targetMode: ProxyMode? = null) :
        RuntimeActionFailurePresentation

    data class Global(
        val message: (String) -> String,
        val title: String = MLang.Component.Message.Error,
    ) : RuntimeActionFailurePresentation

    data object None : RuntimeActionFailurePresentation
}

fun <T> RuntimeActionOutcome<T>.getOrThrowHandled(): T {
    return when (this) {
        is RuntimeActionOutcome.Success -> value
        is RuntimeActionOutcome.PermissionRequired -> {
            GlobalDialogPresenter.showError(MLang.NetworkSettings.Error.VpnDenied)
            throw HandledRuntimeActionFailure()
        }
        RuntimeActionOutcome.FailureHandled -> throw HandledRuntimeActionFailure()
    }
}

class RuntimeActionExecutor(
    private val proxyFacade: ProxyFacade,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
) {
    val isMutating: StateFlow<Boolean> = runtimeControlCoordinator.isMutating
    val activeOperation: StateFlow<String?> = runtimeControlCoordinator.activeOperation

    suspend fun startProxy(
        operation: String,
        mode: ProxyMode,
        profileId: UUID? = null,
        fallbackMessage: String,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(
            presentation =
                RuntimeActionFailurePresentation.Start(
                    targetMode = mode,
                    fallbackMessage = fallbackMessage,
                )
        ) {
            runtimeControlCoordinator.startProxy(
                operation = operation,
                profileId = profileId,
                mode = mode,
            )
        }
    }

    suspend fun stopProxy(
        operation: String,
        mode: ProxyMode? = null,
        presentation: RuntimeActionFailurePresentation.Global,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.stopProxy(operation = operation, mode = mode)
        }
    }

    suspend fun reloadCurrentProfile(
        operation: String,
        presentation: RuntimeActionFailurePresentation.Global,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.reloadCurrentProfile(operation)
        }
    }

    suspend fun reloadIfActiveProfile(
        operation: String,
        profileId: UUID,
        presentation: RuntimeActionFailurePresentation =
            RuntimeActionFailurePresentation.Runtime(
                fallbackMessage = MLang.ProfilesVM.Error.Unknown
            ),
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.reloadIfActiveProfile(operation, profileId)
        }
    }

    suspend fun activateProfile(
        operation: String,
        profileId: UUID,
        enabled: Boolean,
        presentation: RuntimeActionFailurePresentation,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.activateProfile(
                operation = operation,
                profileId = profileId,
                enabled = enabled,
            )
        }
    }

    suspend fun applyConfigChange(
        operation: String,
        persist: suspend () -> Unit,
        rollback: suspend () -> Unit = {},
        shouldRestart: (ProxyMode) -> Boolean = { true },
        presentation: RuntimeActionFailurePresentation,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.applyConfigChange(
                operation = operation,
                persist = persist,
                rollback = rollback,
                shouldRestart = shouldRestart,
            )
        }
    }

    fun resolveDialogMode(): ProxyMode {
        val snapshot = proxyFacade.runtimeSnapshot.value
        return RuntimeStateMapper.modeForOwner(snapshot.owner) ?: snapshot.targetMode
    }

    fun showGlobalError(message: String, title: String = MLang.Component.Message.Error) {
        GlobalDialogPresenter.showError(message = message, title = title)
    }

    private suspend fun <T> runAction(
        presentation: RuntimeActionFailurePresentation,
        block: suspend () -> T,
    ): RuntimeActionOutcome<T> {
        return try {
            RuntimeActionOutcome.Success(block())
        } catch (error: VpnPermissionRequired) {
            RuntimeActionOutcome.PermissionRequired(error.intent)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            presentFailure(error, presentation)
            RuntimeActionOutcome.FailureHandled
        }
    }

    private fun presentFailure(error: Exception, presentation: RuntimeActionFailurePresentation) {
        when (presentation) {
            is RuntimeActionFailurePresentation.Start -> {
                RuntimeFailureDialogPresenter.showStartFailure(
                    reason = error.runtimeGatewayMessage(presentation.fallbackMessage),
                    targetMode = presentation.targetMode,
                )
            }

            is RuntimeActionFailurePresentation.Runtime -> {
                RuntimeFailureDialogPresenter.showRuntimeFailure(
                    reason = error.runtimeGatewayMessage(presentation.fallbackMessage),
                    targetMode = presentation.targetMode ?: resolveDialogMode(),
                )
            }

            is RuntimeActionFailurePresentation.Global -> {
                val reason = error.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)
                GlobalDialogPresenter.showError(
                    message = presentation.message(reason),
                    title = presentation.title,
                )
            }

            RuntimeActionFailurePresentation.None -> Unit
        }
    }
}

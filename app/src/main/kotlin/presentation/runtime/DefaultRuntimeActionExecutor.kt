/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.runtime

import com.github.nomadboxlab.monadbox.domain.model.ProxyMode
import com.github.nomadboxlab.monadbox.remote.VpnPermissionRequired
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationResult
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeStateMapper
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeFailurePresenter
import dev.oom_wg.purejoy.mlang.MLang
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow

class DefaultRuntimeActionExecutor(
    private val proxyFacade: ProxyFacade,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
    private val runtimeFailurePresenter: RuntimeFailurePresenter,
) : RuntimeActionExecutor {
    override val isMutating: StateFlow<Boolean> = runtimeControlCoordinator.isMutating
    override val activeOperation: StateFlow<String?> = runtimeControlCoordinator.activeOperation

    override suspend fun startProxy(
        operation: String,
        mode: ProxyMode,
        profileId: UUID?,
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

    override suspend fun stopProxy(
        operation: String,
        mode: ProxyMode?,
        presentation: RuntimeActionFailurePresentation.Global,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.stopProxy(operation = operation, mode = mode)
        }
    }

    override suspend fun reloadCurrentProfile(
        operation: String,
        presentation: RuntimeActionFailurePresentation.Global,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.reloadCurrentProfile(operation)
        }
    }

    override suspend fun reloadIfActiveProfile(
        operation: String,
        profileId: UUID,
        presentation: RuntimeActionFailurePresentation,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        return runAction(presentation = presentation) {
            runtimeControlCoordinator.reloadIfActiveProfile(operation, profileId)
        }
    }

    override suspend fun activateProfile(
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

    override suspend fun applyConfigChange(
        operation: String,
        persist: suspend () -> Unit,
        rollback: suspend () -> Unit,
        shouldRestart: (ProxyMode) -> Boolean,
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

    override fun resolveDialogMode(): ProxyMode {
        val snapshot = proxyFacade.runtimeSnapshot.value
        return RuntimeStateMapper.modeForOwner(snapshot.owner) ?: snapshot.targetMode
    }

    override fun showGlobalError(message: String, title: String) {
        runtimeFailurePresenter.showGlobalError(message = message, title = title)
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
                val reason = error.runtimeGatewayMessage(presentation.fallbackMessage)
                runtimeFailurePresenter.showStartFailure(
                    reason = reason,
                    targetMode = presentation.targetMode,
                )
            }

            is RuntimeActionFailurePresentation.Runtime -> {
                runtimeFailurePresenter.showRuntimeFailure(
                    reason = error.runtimeGatewayMessage(presentation.fallbackMessage),
                    targetMode = presentation.targetMode ?: resolveDialogMode(),
                )
            }

            is RuntimeActionFailurePresentation.Global -> {
                val reason = error.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)
                runtimeFailurePresenter.showGlobalError(
                    message = presentation.message(reason),
                    title = presentation.title,
                )
            }

            RuntimeActionFailurePresentation.None -> Unit
        }
    }
}

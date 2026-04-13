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

package com.github.yumelira.yumebox.presentation.diagnostic

import android.content.Intent
import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.repository.ActiveProfileOverrideReloader
import com.github.yumelira.yumebox.data.repository.OverrideConfigRepository
import com.github.yumelira.yumebox.data.repository.ProvidersRepository
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionExecutor
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionOutcome
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeControlCoordinator
import com.github.yumelira.yumebox.runtime.client.RuntimeMutationResult
import com.github.yumelira.yumebox.runtime.client.RuntimeMutationStatus
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class DiagnosticActionEffect {
    Execute,
    Inspect,
    Export,
}

enum class DiagnosticActionRisk {
    Low,
    Medium,
    High,
}

enum class DiagnosticActionCommand {
    RefreshPage,
    StartRuntime,
    ReloadRuntime,
    RestartRuntime,
    RefreshSources,
    OpenRuntimeHealth,
    OpenRuleSetInspector,
    OpenSnapshotHistory,
    OpenExplanationChain,
    OpenRawTrace,
    OpenSourceRegistry,
    OpenProviders,
    OpenLogs,
    CopyRawTrace,
}

data class DiagnosticRemediationAction(
    val actionId: String,
    val title: String,
    val summary: String,
    val command: DiagnosticActionCommand,
    val effect: DiagnosticActionEffect,
    val risk: DiagnosticActionRisk = DiagnosticActionRisk.Low,
    val isPrimary: Boolean = false,
    val profileId: String? = null,
    val requestedMode: ProxyMode? = null,
    val requiresConfirmation: Boolean = false,
    val confirmationMessage: String? = null,
)

data class DiagnosticRemediationPlan(
    val headline: String = "",
    val summary: String? = null,
    val actions: List<DiagnosticRemediationAction> = emptyList(),
)

enum class DiagnosticActionFeedbackStatus {
    Success,
    Info,
    Warning,
    Pending,
    Failed,
}

data class DiagnosticActionFeedback(
    val title: String,
    val message: String,
    val status: DiagnosticActionFeedbackStatus,
)

data class DiagnosticActionUiState(
    val activeActionId: String? = null,
    val feedback: DiagnosticActionFeedback? = null,
)

enum class DiagnosticNavigationTarget {
    RuntimeHealth,
    RuleSetInspector,
    SnapshotHistory,
    ExplanationChain,
    RawTrace,
    SourceRegistry,
    Providers,
    Logs,
}

sealed interface DiagnosticActionEvent {
    data class Navigate(val target: DiagnosticNavigationTarget) : DiagnosticActionEvent

    data class CopyText(val text: String) : DiagnosticActionEvent
}

data class DiagnosticActionExecutionResult(
    val feedback: DiagnosticActionFeedback,
    val shouldRefresh: Boolean = false,
    val permissionIntent: Intent? = null,
)

class DiagnosticRemediationCoordinator(
    private val proxyFacade: ProxyFacade,
    private val runtimeActionExecutor: RuntimeActionExecutor,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
) {

    suspend fun execute(action: DiagnosticRemediationAction): DiagnosticActionExecutionResult {
        return when (action.command) {
            DiagnosticActionCommand.StartRuntime -> executeStartRuntime(action)
            DiagnosticActionCommand.ReloadRuntime -> executeReloadRuntime(action)
            DiagnosticActionCommand.RestartRuntime -> executeRestartRuntime(action)
            DiagnosticActionCommand.RefreshSources -> executeRefreshSources(action)
            else ->
                DiagnosticActionExecutionResult(
                    feedback =
                        DiagnosticActionFeedback(
                            title = action.title,
                            message =
                                DiagnosticLang.DetailPages.Remediation.ResultActionUnsupported,
                            status = DiagnosticActionFeedbackStatus.Info,
                        )
                )
        }
    }

    private suspend fun executeStartRuntime(
        action: DiagnosticRemediationAction
    ): DiagnosticActionExecutionResult {
        val outcome =
            runtimeActionExecutor.startProxy(
                operation = "diagnostic:start-runtime",
                profileId = action.profileId?.takeIf(String::isNotBlank)?.let(UUID::fromString),
                mode = action.requestedMode ?: runtimeActionExecutor.resolveDialogMode(),
                fallbackMessage = DiagnosticLang.DetailPages.Remediation.StartRuntimeFailure,
            )
        return mapRuntimeOutcome(action, outcome)
    }

    private suspend fun executeReloadRuntime(
        action: DiagnosticRemediationAction
    ): DiagnosticActionExecutionResult {
        val outcome =
            runtimeActionExecutor.reloadCurrentProfile(
                operation = "diagnostic:reload-runtime",
                presentation =
                    RuntimeActionFailurePresentation.Global(
                        message = { reason ->
                            DiagnosticLang.DetailPages.Remediation.ReloadRuntimeFailure +
                                ": " +
                                reason
                        }
                    ),
            )
        return mapRuntimeOutcome(action, outcome)
    }

    private suspend fun executeRestartRuntime(
        action: DiagnosticRemediationAction
    ): DiagnosticActionExecutionResult {
        val outcome =
            runtimeActionExecutor.applyConfigChange(
                operation = "diagnostic:restart-runtime",
                persist = {},
                shouldRestart = { true },
                presentation =
                    RuntimeActionFailurePresentation.Start(
                        targetMode = runtimeActionExecutor.resolveDialogMode(),
                        fallbackMessage =
                            DiagnosticLang.DetailPages.Remediation.RestartRuntimeFailure,
                    ),
            )
        return mapRuntimeOutcome(action, outcome)
    }

    private suspend fun executeRefreshSources(
        action: DiagnosticRemediationAction
    ): DiagnosticActionExecutionResult =
        withContext(Dispatchers.IO) {
            val remoteOverrides =
                runCatching { overrideConfigRepository.listRemoteResources() }
                    .getOrDefault(emptyList())
            val runtimeRunning = proxyFacade.isRunning.value
            val providers =
                if (runtimeRunning) {
                    providersRepository.queryProviders().getOrDefault(emptyList()).filter {
                        it.vehicleType == Provider.VehicleType.HTTP
                    }
                } else {
                    emptyList()
                }

            if (providers.isEmpty() && remoteOverrides.isEmpty()) {
                return@withContext DiagnosticActionExecutionResult(
                    feedback =
                        DiagnosticActionFeedback(
                            title = action.title,
                            message = DiagnosticLang.DetailPages.Remediation.ResultSourcesEmpty,
                            status = DiagnosticActionFeedbackStatus.Info,
                        )
                )
            }

            val failedItems = mutableListOf<String>()
            var refreshedCount = 0

            runtimeControlCoordinator.runSerialized("diagnostic:refresh-sources") {
                if (providers.isNotEmpty()) {
                    providersRepository
                        .updateAllProviders(providers)
                        .onSuccess { result ->
                            refreshedCount += providers.size - result.failedProviders.size
                            failedItems += result.failedProviders
                        }
                        .onFailure { error ->
                            failedItems +=
                                error.message
                                    ?: DiagnosticLang.DetailPages.Remediation.ResultActionFailed
                        }
                }

                remoteOverrides.forEach { resource ->
                    runCatching {
                            overrideConfigRepository.refreshRemoteResource(resource.id)
                            activeProfileOverrideReloader.reapplyActiveProfileIfUsingOverride(
                                resource.id
                            )
                        }
                        .onSuccess { refreshedCount += 1 }
                        .onFailure { failedItems += resource.name }
                }
            }

            val feedback =
                when {
                    refreshedCount == 0 && failedItems.isNotEmpty() ->
                        DiagnosticActionFeedback(
                            title = action.title,
                            message = DiagnosticLang.DetailPages.Remediation.ResultActionFailed,
                            status = DiagnosticActionFeedbackStatus.Failed,
                        )
                    failedItems.isEmpty() && runtimeRunning ->
                        DiagnosticActionFeedback(
                            title = action.title,
                            message =
                                DiagnosticLang.DetailPages.Remediation.ResultSourcesRefreshed
                                    .format(refreshedCount),
                            status = DiagnosticActionFeedbackStatus.Success,
                        )
                    failedItems.isEmpty() ->
                        DiagnosticActionFeedback(
                            title = action.title,
                            message =
                                DiagnosticLang.DetailPages.Remediation.ResultSourcesDeferred.format(
                                    refreshedCount
                                ),
                            status = DiagnosticActionFeedbackStatus.Warning,
                        )
                    else ->
                        DiagnosticActionFeedback(
                            title = action.title,
                            message =
                                DiagnosticLang.DetailPages.Remediation.ResultSourcesPartial.format(
                                    refreshedCount,
                                    failedItems.size,
                                ),
                            status = DiagnosticActionFeedbackStatus.Warning,
                        )
                }

            DiagnosticActionExecutionResult(feedback = feedback, shouldRefresh = true)
        }

    private fun mapRuntimeOutcome(
        action: DiagnosticRemediationAction,
        outcome: RuntimeActionOutcome<RuntimeMutationResult>,
    ): DiagnosticActionExecutionResult {
        return when (outcome) {
            is RuntimeActionOutcome.Success -> {
                val feedback =
                    DiagnosticActionFeedback(
                        title = action.title,
                        message = outcome.value.status.toDiagnosticMessage(),
                        status = outcome.value.status.toFeedbackStatus(),
                    )
                DiagnosticActionExecutionResult(
                    feedback = feedback,
                    shouldRefresh = outcome.value.status != RuntimeMutationStatus.Deferred,
                )
            }

            is RuntimeActionOutcome.PermissionRequired ->
                DiagnosticActionExecutionResult(
                    feedback =
                        DiagnosticActionFeedback(
                            title = action.title,
                            message =
                                DiagnosticLang.DetailPages.Remediation.ResultPermissionPending,
                            status = DiagnosticActionFeedbackStatus.Pending,
                        ),
                    permissionIntent = outcome.intent,
                )

            RuntimeActionOutcome.FailureHandled ->
                DiagnosticActionExecutionResult(
                    feedback =
                        DiagnosticActionFeedback(
                            title = action.title,
                            message = DiagnosticLang.DetailPages.Remediation.ResultActionFailed,
                            status = DiagnosticActionFeedbackStatus.Failed,
                        )
                )
        }
    }

    private fun RuntimeMutationStatus.toDiagnosticMessage(): String {
        return when (this) {
            RuntimeMutationStatus.Deferred -> DiagnosticLang.DetailPages.Remediation.ResultDeferred
            RuntimeMutationStatus.Reloaded -> DiagnosticLang.DetailPages.Remediation.ResultReloaded
            RuntimeMutationStatus.Restarted ->
                DiagnosticLang.DetailPages.Remediation.ResultRestarted
            RuntimeMutationStatus.Started -> DiagnosticLang.DetailPages.Remediation.ResultStarted
            RuntimeMutationStatus.Stopped -> DiagnosticLang.DetailPages.Remediation.ResultApplied
            RuntimeMutationStatus.Updated -> DiagnosticLang.DetailPages.Remediation.ResultApplied
        }
    }

    private fun RuntimeMutationStatus.toFeedbackStatus(): DiagnosticActionFeedbackStatus {
        return when (this) {
            RuntimeMutationStatus.Deferred -> DiagnosticActionFeedbackStatus.Info
            RuntimeMutationStatus.Reloaded,
            RuntimeMutationStatus.Restarted,
            RuntimeMutationStatus.Started,
            RuntimeMutationStatus.Stopped,
            RuntimeMutationStatus.Updated -> DiagnosticActionFeedbackStatus.Success
        }
    }
}

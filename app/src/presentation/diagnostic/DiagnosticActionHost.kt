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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class DiagnosticActionHostState(
    val uiState: DiagnosticActionUiState,
    val onAction: (DiagnosticRemediationAction) -> Unit,
)

@Composable
fun rememberDiagnosticActionHost(
    onNavigate: (DiagnosticNavigationTarget) -> Unit,
    onRefresh: (() -> Unit)? = null,
    resolveCopyPayload: (DiagnosticRemediationAction) -> String? = { null },
    onCopyText: ((String) -> Unit)? = null,
): DiagnosticActionHostState {
    val remediationCoordinator = koinInject<DiagnosticRemediationCoordinator>()
    val vpnPermissionCoordinator =
        koinInject<com.github.yumelira.yumebox.presentation.runtime.VpnPermissionCoordinator>()
    val scope = rememberCoroutineScope()
    val navigateHandler by rememberUpdatedState(onNavigate)
    val refreshHandler by rememberUpdatedState(onRefresh)
    val copyPayloadResolver by rememberUpdatedState(resolveCopyPayload)
    val copyHandler by rememberUpdatedState(onCopyText)
    var actionUiState by remember { mutableStateOf(DiagnosticActionUiState()) }

    fun dispatch(action: DiagnosticRemediationAction) {
        if (actionUiState.activeActionId != null) return

        scope.launch {
            when (action.command) {
                DiagnosticActionCommand.RefreshPage -> {
                    actionUiState =
                        DiagnosticActionUiState(
                            feedback =
                                DiagnosticActionFeedback(
                                    title = action.title,
                                    message =
                                        DiagnosticLang.DetailPages.Remediation.ResultRefreshed,
                                    status = DiagnosticActionFeedbackStatus.Info,
                                )
                        )
                    refreshHandler?.invoke()
                }

                DiagnosticActionCommand.CopyRawTrace -> {
                    val payload = copyPayloadResolver(action)
                    actionUiState =
                        DiagnosticActionUiState(
                            feedback =
                                DiagnosticActionFeedback(
                                    title = action.title,
                                    message =
                                        if (payload.isNullOrBlank()) {
                                            DiagnosticLang.DetailPages.Remediation
                                                .ResultActionUnsupported
                                        } else {
                                            copyHandler?.invoke(payload)
                                            DiagnosticLang.DetailPages.Remediation.ResultCopied
                                        },
                                    status =
                                        if (payload.isNullOrBlank()) {
                                            DiagnosticActionFeedbackStatus.Info
                                        } else {
                                            DiagnosticActionFeedbackStatus.Success
                                        },
                                )
                        )
                }

                DiagnosticActionCommand.OpenRuntimeHealth,
                DiagnosticActionCommand.OpenRuleSetInspector,
                DiagnosticActionCommand.OpenSnapshotHistory,
                DiagnosticActionCommand.OpenExplanationChain,
                DiagnosticActionCommand.OpenRawTrace,
                DiagnosticActionCommand.OpenSourceRegistry,
                DiagnosticActionCommand.OpenProviders,
                DiagnosticActionCommand.OpenLogs -> {
                    navigateHandler(action.command.toNavigationTarget())
                }

                else -> {
                    actionUiState = actionUiState.copy(activeActionId = action.actionId)
                    val result = remediationCoordinator.execute(action)
                    actionUiState =
                        DiagnosticActionUiState(activeActionId = null, feedback = result.feedback)

                    result.permissionIntent?.let { intent ->
                        vpnPermissionCoordinator.requestPermission(intent) { dispatch(action) }
                        return@launch
                    }

                    if (result.shouldRefresh) {
                        refreshHandler?.invoke()
                    }
                }
            }
        }
    }

    return remember(actionUiState) {
        DiagnosticActionHostState(uiState = actionUiState, onAction = ::dispatch)
    }
}

private fun DiagnosticActionCommand.toNavigationTarget(): DiagnosticNavigationTarget {
    return when (this) {
        DiagnosticActionCommand.OpenRuntimeHealth -> DiagnosticNavigationTarget.RuntimeHealth
        DiagnosticActionCommand.OpenRuleSetInspector -> DiagnosticNavigationTarget.RuleSetInspector
        DiagnosticActionCommand.OpenSnapshotHistory -> DiagnosticNavigationTarget.SnapshotHistory
        DiagnosticActionCommand.OpenExplanationChain -> DiagnosticNavigationTarget.ExplanationChain
        DiagnosticActionCommand.OpenRawTrace -> DiagnosticNavigationTarget.RawTrace
        DiagnosticActionCommand.OpenSourceRegistry -> DiagnosticNavigationTarget.SourceRegistry
        DiagnosticActionCommand.OpenProviders -> DiagnosticNavigationTarget.Providers
        DiagnosticActionCommand.OpenLogs -> DiagnosticNavigationTarget.Logs
        else -> error("Unsupported navigation command: $this")
    }
}

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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionCommand
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionEvent
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionFeedback
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionFeedbackStatus
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionUiState
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticDetailRepository
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticRemediationAction
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticRemediationCoordinator
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticNavigationTarget
import com.github.yumelira.yumebox.presentation.diagnostic.ExplanationChainDetail
import com.github.yumelira.yumebox.presentation.diagnostic.RawTraceDetail
import com.github.yumelira.yumebox.presentation.diagnostic.RuleSetInspectorDetail
import com.github.yumelira.yumebox.presentation.diagnostic.RuntimeHealthDetail
import com.github.yumelira.yumebox.presentation.diagnostic.SourceRegistryOverviewDetail
import com.github.yumelira.yumebox.presentation.diagnostic.SnapshotHistoryDetail
import com.github.yumelira.yumebox.presentation.runtime.VpnPermissionCoordinator
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class DiagnosticDetailActionViewModel<T>(
    private val remediationCoordinator: DiagnosticRemediationCoordinator,
    private val vpnPermissionCoordinator: VpnPermissionCoordinator,
) : ViewModel() {

    private val _actionUiState = MutableStateFlow(DiagnosticActionUiState())
    val actionUiState: StateFlow<DiagnosticActionUiState> = _actionUiState.asStateFlow()

    private val _events = MutableSharedFlow<DiagnosticActionEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<DiagnosticActionEvent> = _events.asSharedFlow()

    fun refresh() {
        viewModelScope.launch {
            updateLoading(true)
            try {
                applyLoadedDetail(loadDetail())
            } finally {
                updateLoading(false)
            }
        }
    }

    fun onAction(action: DiagnosticRemediationAction) {
        if (_actionUiState.value.activeActionId != null) return

        viewModelScope.launch {
            when (action.command) {
                DiagnosticActionCommand.RefreshPage -> {
                    _actionUiState.value =
                        DiagnosticActionUiState(
                            feedback =
                                DiagnosticActionFeedback(
                                    title = action.title,
                                    message = DiagnosticLang.DetailPages.Remediation.ResultRefreshed,
                                    status = DiagnosticActionFeedbackStatus.Info,
                                ),
                        )
                    refresh()
                }

                DiagnosticActionCommand.CopyRawTrace -> {
                    val payload = resolveCopyPayload(action, currentDetail())
                    if (payload.isNullOrBlank()) {
                        _actionUiState.value =
                            DiagnosticActionUiState(
                                feedback =
                                    DiagnosticActionFeedback(
                                        title = action.title,
                                        message = DiagnosticLang.DetailPages.Remediation.ResultActionUnsupported,
                                        status = DiagnosticActionFeedbackStatus.Info,
                                    ),
                            )
                    } else {
                        _events.tryEmit(DiagnosticActionEvent.CopyText(payload))
                        _actionUiState.value =
                            DiagnosticActionUiState(
                                feedback =
                                    DiagnosticActionFeedback(
                                        title = action.title,
                                        message = DiagnosticLang.DetailPages.Remediation.ResultCopied,
                                        status = DiagnosticActionFeedbackStatus.Success,
                                    ),
                            )
                    }
                }

                DiagnosticActionCommand.OpenRuntimeHealth,
                DiagnosticActionCommand.OpenRuleSetInspector,
                DiagnosticActionCommand.OpenSnapshotHistory,
                DiagnosticActionCommand.OpenExplanationChain,
                DiagnosticActionCommand.OpenRawTrace,
                DiagnosticActionCommand.OpenSourceRegistry,
                DiagnosticActionCommand.OpenProviders,
                DiagnosticActionCommand.OpenLogs -> {
                    _events.emit(DiagnosticActionEvent.Navigate(action.command.toNavigationTarget()))
                }

                else -> executeMutationAction(action)
            }
        }
    }

    protected open fun resolveCopyPayload(
        action: DiagnosticRemediationAction,
        detail: T,
    ): String? = null

    protected abstract suspend fun loadDetail(): T

    protected abstract fun currentDetail(): T

    protected abstract fun applyLoadedDetail(detail: T)

    protected abstract fun updateLoading(isLoading: Boolean)

    private suspend fun executeMutationAction(action: DiagnosticRemediationAction) {
        _actionUiState.value = _actionUiState.value.copy(activeActionId = action.actionId)
        val result = remediationCoordinator.execute(action)
        _actionUiState.value =
            DiagnosticActionUiState(activeActionId = null, feedback = result.feedback)

        result.permissionIntent?.let { intent ->
            vpnPermissionCoordinator.requestPermission(intent) { onAction(action) }
            return
        }

        if (result.shouldRefresh) {
            refresh()
        }
    }

    private fun DiagnosticActionCommand.toNavigationTarget(): DiagnosticNavigationTarget {
        return when (this) {
            DiagnosticActionCommand.OpenRuntimeHealth -> DiagnosticNavigationTarget.RuntimeHealth
            DiagnosticActionCommand.OpenRuleSetInspector ->
                DiagnosticNavigationTarget.RuleSetInspector
            DiagnosticActionCommand.OpenSnapshotHistory ->
                DiagnosticNavigationTarget.SnapshotHistory
            DiagnosticActionCommand.OpenExplanationChain ->
                DiagnosticNavigationTarget.ExplanationChain
            DiagnosticActionCommand.OpenRawTrace -> DiagnosticNavigationTarget.RawTrace
            DiagnosticActionCommand.OpenSourceRegistry -> DiagnosticNavigationTarget.SourceRegistry
            DiagnosticActionCommand.OpenProviders -> DiagnosticNavigationTarget.Providers
            DiagnosticActionCommand.OpenLogs -> DiagnosticNavigationTarget.Logs
            else -> error("Unsupported navigation command: $this")
        }
    }
}

data class RuntimeHealthDetailUiState(
    val isLoading: Boolean = false,
    val detail: RuntimeHealthDetail = RuntimeHealthDetail(),
)

data class RuleSetInspectorUiState(
    val isLoading: Boolean = false,
    val detail: RuleSetInspectorDetail = RuleSetInspectorDetail(),
)

data class SnapshotHistoryUiState(
    val isLoading: Boolean = false,
    val detail: SnapshotHistoryDetail = SnapshotHistoryDetail(),
)

data class ExplanationChainUiState(
    val isLoading: Boolean = false,
    val detail: ExplanationChainDetail = ExplanationChainDetail(),
)

data class RawTraceUiState(
    val isLoading: Boolean = false,
    val detail: RawTraceDetail = RawTraceDetail(),
)

data class SourceRegistryOverviewUiState(
    val isLoading: Boolean = false,
    val detail: SourceRegistryOverviewDetail = SourceRegistryOverviewDetail(),
)

class RuntimeHealthDetailViewModel(
    private val diagnosticDetailRepository: DiagnosticDetailRepository,
    remediationCoordinator: DiagnosticRemediationCoordinator,
    vpnPermissionCoordinator: VpnPermissionCoordinator,
) : DiagnosticDetailActionViewModel<RuntimeHealthDetail>(remediationCoordinator, vpnPermissionCoordinator) {

    private val _uiState = MutableStateFlow(RuntimeHealthDetailUiState())
    val uiState: StateFlow<RuntimeHealthDetailUiState> = _uiState.asStateFlow()

    override suspend fun loadDetail(): RuntimeHealthDetail {
        return diagnosticDetailRepository.loadRuntimeHealthDetail()
    }

    override fun currentDetail(): RuntimeHealthDetail = _uiState.value.detail

    override fun applyLoadedDetail(detail: RuntimeHealthDetail) {
        _uiState.value = RuntimeHealthDetailUiState(isLoading = false, detail = detail)
    }

    override fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}

class RuleSetInspectorViewModel(
    private val diagnosticDetailRepository: DiagnosticDetailRepository,
    remediationCoordinator: DiagnosticRemediationCoordinator,
    vpnPermissionCoordinator: VpnPermissionCoordinator,
) : DiagnosticDetailActionViewModel<RuleSetInspectorDetail>(remediationCoordinator, vpnPermissionCoordinator) {

    private val _uiState = MutableStateFlow(RuleSetInspectorUiState())
    val uiState: StateFlow<RuleSetInspectorUiState> = _uiState.asStateFlow()

    override suspend fun loadDetail(): RuleSetInspectorDetail {
        return diagnosticDetailRepository.loadRuleSetInspectorDetail()
    }

    override fun currentDetail(): RuleSetInspectorDetail = _uiState.value.detail

    override fun applyLoadedDetail(detail: RuleSetInspectorDetail) {
        _uiState.value = RuleSetInspectorUiState(isLoading = false, detail = detail)
    }

    override fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}

class SnapshotHistoryViewModel(
    private val diagnosticDetailRepository: DiagnosticDetailRepository,
    remediationCoordinator: DiagnosticRemediationCoordinator,
    vpnPermissionCoordinator: VpnPermissionCoordinator,
) : DiagnosticDetailActionViewModel<SnapshotHistoryDetail>(remediationCoordinator, vpnPermissionCoordinator) {

    private val _uiState = MutableStateFlow(SnapshotHistoryUiState())
    val uiState: StateFlow<SnapshotHistoryUiState> = _uiState.asStateFlow()

    override suspend fun loadDetail(): SnapshotHistoryDetail {
        return diagnosticDetailRepository.loadSnapshotHistoryDetail()
    }

    override fun currentDetail(): SnapshotHistoryDetail = _uiState.value.detail

    override fun applyLoadedDetail(detail: SnapshotHistoryDetail) {
        _uiState.value = SnapshotHistoryUiState(isLoading = false, detail = detail)
    }

    override fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}

class ExplanationChainDetailViewModel(
    private val diagnosticDetailRepository: DiagnosticDetailRepository,
    remediationCoordinator: DiagnosticRemediationCoordinator,
    vpnPermissionCoordinator: VpnPermissionCoordinator,
) : DiagnosticDetailActionViewModel<ExplanationChainDetail>(remediationCoordinator, vpnPermissionCoordinator) {

    private val _uiState = MutableStateFlow(ExplanationChainUiState())
    val uiState: StateFlow<ExplanationChainUiState> = _uiState.asStateFlow()

    override suspend fun loadDetail(): ExplanationChainDetail {
        return diagnosticDetailRepository.loadExplanationChainDetail()
    }

    override fun currentDetail(): ExplanationChainDetail = _uiState.value.detail

    override fun applyLoadedDetail(detail: ExplanationChainDetail) {
        _uiState.value = ExplanationChainUiState(isLoading = false, detail = detail)
    }

    override fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}

class RawTraceDetailViewModel(
    private val diagnosticDetailRepository: DiagnosticDetailRepository,
    remediationCoordinator: DiagnosticRemediationCoordinator,
    vpnPermissionCoordinator: VpnPermissionCoordinator,
) : DiagnosticDetailActionViewModel<RawTraceDetail>(remediationCoordinator, vpnPermissionCoordinator) {

    private val _uiState = MutableStateFlow(RawTraceUiState())
    val uiState: StateFlow<RawTraceUiState> = _uiState.asStateFlow()

    override suspend fun loadDetail(): RawTraceDetail {
        return diagnosticDetailRepository.loadRawTraceDetail()
    }

    override fun currentDetail(): RawTraceDetail = _uiState.value.detail

    override fun applyLoadedDetail(detail: RawTraceDetail) {
        _uiState.value = RawTraceUiState(isLoading = false, detail = detail)
    }

    override fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    override fun resolveCopyPayload(action: DiagnosticRemediationAction, detail: RawTraceDetail): String? {
        if (action.command != DiagnosticActionCommand.CopyRawTrace) {
            return null
        }

        return buildString {
            appendLine("${DiagnosticLang.DetailPages.RawTrace.TraceId}: ${detail.traceId}")
            appendLine(
                "${DiagnosticLang.DetailPages.RawTrace.RuntimePhase}: ${detail.runtimePhase.name}"
            )
            detail.rawSections.forEach { section ->
                appendLine()
                appendLine(section.title)
                section.lines.forEach(::appendLine)
            }
        }.trim().takeIf(String::isNotBlank)
    }
}

class SourceRegistryOverviewViewModel(
    private val diagnosticDetailRepository: DiagnosticDetailRepository,
    remediationCoordinator: DiagnosticRemediationCoordinator,
    vpnPermissionCoordinator: VpnPermissionCoordinator,
) : DiagnosticDetailActionViewModel<SourceRegistryOverviewDetail>(remediationCoordinator, vpnPermissionCoordinator) {

    private val _uiState = MutableStateFlow(SourceRegistryOverviewUiState())
    val uiState: StateFlow<SourceRegistryOverviewUiState> = _uiState.asStateFlow()

    override suspend fun loadDetail(): SourceRegistryOverviewDetail {
        return diagnosticDetailRepository.loadSourceRegistryOverviewDetail()
    }

    override fun currentDetail(): SourceRegistryOverviewDetail = _uiState.value.detail

    override fun applyLoadedDetail(detail: SourceRegistryOverviewDetail) {
        _uiState.value = SourceRegistryOverviewUiState(isLoading = false, detail = detail)
    }

    override fun updateLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}
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

package com.github.yumelira.yumebox.screen.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.domain.model.ErrorCategory
import com.github.yumelira.yumebox.domain.model.ErrorImpact
import com.github.yumelira.yumebox.domain.model.ErrorPhase
import com.github.yumelira.yumebox.domain.model.ErrorRetryability
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.DiagnosticBannerState
import com.github.yumelira.yumebox.presentation.component.InfoSettingRow
import com.github.yumelira.yumebox.presentation.component.orFallback
import com.github.yumelira.yumebox.presentation.component.toDisplayLabel
import com.github.yumelira.yumebox.presentation.component.toSemanticTone
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticNavigationTarget
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticRemediationPanel
import com.github.yumelira.yumebox.presentation.diagnostic.buildSourceEntryRemediationPlan
import com.github.yumelira.yumebox.presentation.diagnostic.refreshSourcesAction
import com.github.yumelira.yumebox.presentation.diagnostic.rememberDiagnosticActionHost
import com.github.yumelira.yumebox.presentation.screen.ProvidersContent
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import com.github.yumelira.yumebox.presentation.util.buildExternalResourceDiagnostics
import com.github.yumelira.yumebox.presentation.viewmodel.ProvidersViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SourceRegistryOverviewScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination<RootGraph>
fun ProvidersScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<ProvidersViewModel>()
    val providers by viewModel.providers.collectAsStateWithLifecycle()
    val remoteOverrides by viewModel.remoteOverrides.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = AppTheme.spacing
    val externalDiagnostics =
        remember(providers, remoteOverrides) {
            buildExternalResourceDiagnostics(
                providers = providers,
                remoteOverrides = remoteOverrides,
            )
        }
    val remediationPlan =
        remember(externalDiagnostics) {
            buildSourceEntryRemediationPlan(
                hasSources = externalDiagnostics.subscriptionSources.isNotEmpty(),
                needsAttention =
                    externalDiagnostics.staleCount > 0 || externalDiagnostics.pendingCount > 0,
            )
        }
    val structuredError =
        remember(uiState.structuredError, uiState.error) {
            uiState.structuredError.orFallback(
                message = uiState.error,
                category = ErrorCategory.Runtime,
                phase = ErrorPhase.Running,
                impact = ErrorImpact.FeatureUnavailable,
                retryability = ErrorRetryability.Retryable,
            )
        }
    val diagnosticActions =
        rememberDiagnosticActionHost(
            onNavigate = { target ->
                when (target) {
                    DiagnosticNavigationTarget.SourceRegistry -> {
                        navigator.navigate(SourceRegistryOverviewScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    else -> Unit
                }
            },
            onRefresh = viewModel::refreshProviders,
        )

    ProvidersContent(
        navigator = navigator,
        viewModel = viewModel,
        onRefreshSourcesRequest = {
            diagnosticActions.onAction(refreshSourcesAction(isPrimary = true))
        },
        refreshSourcesInProgress =
            diagnosticActions.uiState.activeActionId == refreshSourcesAction().actionId,
        diagnosticContent = { diagnostics, _ ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                ProviderDiagnosticSummaryCard(
                    banner = diagnostics.toBannerState(),
                    structuredError = structuredError,
                )
                DiagnosticRemediationPanel(
                    plan = remediationPlan,
                    actionUiState = diagnosticActions.uiState,
                    onAction = diagnosticActions.onAction,
                )
            }
        },
    )
}

@Composable
private fun ProviderDiagnosticSummaryCard(
    banner: DiagnosticBannerState,
    structuredError: com.github.yumelira.yumebox.domain.model.StructuredError?,
) {
    Card {
        InfoSettingRow(
            title = banner.headline,
            summary = banner.subtitle,
            valueLabel = banner.tone.toProviderStateLabel(),
            tone = banner.tone,
            badgeLeadingDot = banner.tone == SemanticTone.Warning || banner.tone == SemanticTone.Danger,
            showDivider = structuredError != null,
        )
        structuredError?.let { error ->
            InfoSettingRow(
                title = error.userVisibleMessage,
                summary = error.technicalDetail ?: error.rawCause,
                valueLabel = error.impact.toDisplayLabel(),
                tone = error.toSemanticTone(),
                showDivider = false,
            )
        }
    }
}

private fun SemanticTone.toProviderStateLabel(): String {
    return when (this) {
        SemanticTone.Success,
        SemanticTone.Brand -> dev.oom_wg.purejoy.mlang.DiagnosticLang.DetailPages.Common.Ready
        SemanticTone.Info -> dev.oom_wg.purejoy.mlang.DiagnosticLang.DetailPages.Common.Waiting
        SemanticTone.Warning -> dev.oom_wg.purejoy.mlang.DiagnosticLang.DetailPages.Common.Attention
        SemanticTone.Danger -> dev.oom_wg.purejoy.mlang.DiagnosticLang.DetailPages.Remediation.Failed
        SemanticTone.Neutral -> dev.oom_wg.purejoy.mlang.DiagnosticLang.DetailPages.Common.NotAvailable
    }
}

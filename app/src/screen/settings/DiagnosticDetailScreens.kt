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

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.ByteFormatter
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.domain.model.ExplanationChain
import com.github.yumelira.yumebox.domain.model.ExplanationStep
import com.github.yumelira.yumebox.domain.model.HealthCheckItem
import com.github.yumelira.yumebox.domain.model.HealthCheckSeverity
import com.github.yumelira.yumebox.domain.model.RuleSet
import com.github.yumelira.yumebox.domain.model.RuleSetOrigin
import com.github.yumelira.yumebox.domain.model.StructuredError
import com.github.yumelira.yumebox.domain.model.SnapshotType
import com.github.yumelira.yumebox.domain.model.SourceType
import com.github.yumelira.yumebox.domain.model.SyncState
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.CollectFlowWithLifecycle
import com.github.yumelira.yumebox.presentation.component.DiagnosticBannerState
import com.github.yumelira.yumebox.presentation.component.InfoSettingRow
import com.github.yumelira.yumebox.presentation.component.NavigationBackIcon
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.component.toDisplayLabel
import com.github.yumelira.yumebox.presentation.component.toSemanticTone
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionCommand
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionEvent
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticActionUiState
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticNavigationTarget
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticRemediationAction
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticRemediationPlan
import com.github.yumelira.yumebox.presentation.diagnostic.DiagnosticRemediationPanel
import com.github.yumelira.yumebox.presentation.diagnostic.ExplanationChainDetail
import com.github.yumelira.yumebox.presentation.diagnostic.RawTraceDetail
import com.github.yumelira.yumebox.presentation.diagnostic.RawTraceSection
import com.github.yumelira.yumebox.presentation.diagnostic.RuleSetInspectorDetail
import com.github.yumelira.yumebox.presentation.diagnostic.RuntimeHealthDetail
import com.github.yumelira.yumebox.presentation.diagnostic.SourceRegistryItem
import com.github.yumelira.yumebox.presentation.diagnostic.SourceRegistryOverviewDetail
import com.github.yumelira.yumebox.presentation.diagnostic.SourceRegistryRole
import com.github.yumelira.yumebox.presentation.diagnostic.SnapshotHistoryDetail
import com.github.yumelira.yumebox.presentation.theme.LocalSpacing
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ExplanationChainDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ProvidersScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RawTraceDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RuleSetInspectorScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RuntimeHealthDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SourceRegistryOverviewScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SnapshotHistoryScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import dev.oom_wg.purejoy.mlang.MLang
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class MetricCardModel(
    val title: String,
    val value: String,
    val tone: SemanticTone,
    val summary: String? = null,
)

private data class DetailInfoEntry(
    val label: String,
    val value: String,
)

private enum class DiagnosticDetailPage {
    RuntimeHealth,
    RuleSetInspector,
    SnapshotHistory,
    ExplanationChain,
    RawTrace,
    SourceRegistry,
}

@Composable
@Destination<RootGraph>
fun RuntimeHealthDetailScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<RuntimeHealthDetailViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }
    HandleDiagnosticActionEvents(events = viewModel.events, navigator = navigator)

    DiagnosticDetailScaffold(
        title = DiagnosticLang.DetailPages.RuntimeHealth.Title,
        navigator = navigator,
        overview = {
        val detail = uiState.detail
        diagnosticOverviewItems(
            page = DiagnosticDetailPage.RuntimeHealth,
            keyPrefix = "runtime",
            banner =
                DiagnosticBannerState(
                    headline = detail.profileName ?: DiagnosticLang.DetailPages.RuntimeHealth.Headline,
                    subtitle = buildRuntimeBannerSubtitle(detail),
                    tone = buildRuntimeBannerTone(detail),
                ),
            structuredError = detail.structuredError,
            remediationPlan = detail.remediationPlan,
            actionUiState = actionUiState,
            onAction = viewModel::onAction,
            metricCards = buildRuntimeMetricCards(detail),
            infoEntries = buildRuntimeInfoEntries(detail),
        )
        },
        evidence = {
            val detail = uiState.detail
            item("runtime_checks_title") { SmallTitle(DiagnosticLang.DetailPages.Common.Evidence) }
            item("runtime_checks") { HealthCheckCard(items = detail.healthReport.items) }
        },
    )
}

@Composable
@Destination<RootGraph>
fun RuleSetInspectorScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<RuleSetInspectorViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }
    HandleDiagnosticActionEvents(events = viewModel.events, navigator = navigator)

    DiagnosticDetailScaffold(
        title = DiagnosticLang.DetailPages.RuleSetInspector.Title,
        navigator = navigator,
        overview = {
        val detail = uiState.detail
        diagnosticOverviewItems(
            page = DiagnosticDetailPage.RuleSetInspector,
            keyPrefix = "ruleset",
            banner =
                DiagnosticBannerState(
                    headline = detail.profileName ?: DiagnosticLang.DetailPages.RuleSetInspector.Headline,
                    subtitle = buildRuleSetBannerSubtitle(detail),
                    tone = buildRuleSetBannerTone(detail),
                ),
            structuredError = detail.structuredError,
            remediationPlan = detail.remediationPlan,
            actionUiState = actionUiState,
            onAction = viewModel::onAction,
            metricCards = buildRuleSetMetricCards(detail),
            infoEntries = buildRuleSetInfoEntries(detail),
        )
        },
        evidence = {
        val detail = uiState.detail
        val groupedRuleSets = detail.ruleSets.groupBy(RuleSet::origin)
        if (groupedRuleSets.isEmpty()) {
            item("ruleset_empty") { EmptyStateCard(message = DiagnosticLang.DetailPages.RuleSetInspector.NoRuleSets) }
        } else {
            RuleSetOrigin.entries.forEach { origin ->
                val ruleSets = groupedRuleSets[origin].orEmpty()
                if (ruleSets.isNotEmpty()) {
                    item("ruleset_group_title_${origin.name}") {
                        SmallTitle(origin.toDisplayLabel())
                    }
                    item("ruleset_group_${origin.name}") {
                        RuleSetGroupCard(ruleSets = ruleSets)
                    }
                }
            }
        }
        },
    )
}

@Composable
@Destination<RootGraph>
fun SnapshotHistoryScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<SnapshotHistoryViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }
    HandleDiagnosticActionEvents(events = viewModel.events, navigator = navigator)

    DiagnosticDetailScaffold(
        title = DiagnosticLang.DetailPages.SnapshotHistory.Title,
        navigator = navigator,
        overview = {
        val detail = uiState.detail
        diagnosticOverviewItems(
            page = DiagnosticDetailPage.SnapshotHistory,
            keyPrefix = "snapshot",
            banner =
                DiagnosticBannerState(
                    headline = detail.profileName ?: DiagnosticLang.DetailPages.SnapshotHistory.Headline,
                    subtitle = buildSnapshotBannerSubtitle(detail),
                    tone = buildSnapshotBannerTone(detail),
                ),
            structuredError = detail.structuredError,
            remediationPlan = detail.remediationPlan,
            actionUiState = actionUiState,
            onAction = viewModel::onAction,
            metricCards = buildSnapshotMetricCards(detail),
        )
        },
        evidence = {
        val detail = uiState.detail
        item("snapshot_current_title") { SmallTitle(DiagnosticLang.DetailPages.Common.CurrentView) }
        item("snapshot_current") {
            val currentSnapshot = detail.snapshots.firstOrNull { it.snapshotId == detail.currentSnapshotId }
            if (currentSnapshot != null) {
                DetailInfoCard(entries = buildCurrentSnapshotEntries(currentSnapshot))
            } else {
                EmptyStateCard(message = DiagnosticLang.DetailPages.SnapshotHistory.NoSnapshots)
            }
        }
        item("snapshot_history_title") { SmallTitle(DiagnosticLang.DetailPages.Common.Evidence) }
        item("snapshot_history") {
            SnapshotTimelineCard(
                detail = detail,
            )
        }
        },
    )
}

@Composable
@Destination<RootGraph>
fun ExplanationChainDetailScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<ExplanationChainDetailViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }
    HandleDiagnosticActionEvents(events = viewModel.events, navigator = navigator)

    DiagnosticDetailScaffold(
        title = DiagnosticLang.DetailPages.ExplanationChain.Title,
        navigator = navigator,
        overview = {
        val detail = uiState.detail
        diagnosticOverviewItems(
            page = DiagnosticDetailPage.ExplanationChain,
            keyPrefix = "explanation",
            banner =
                DiagnosticBannerState(
                    headline = detail.profileName ?: DiagnosticLang.DetailPages.ExplanationChain.Headline,
                    subtitle = buildExplanationBannerSubtitle(detail),
                    tone = buildExplanationBannerTone(detail),
                ),
            structuredError = detail.structuredError,
            remediationPlan = detail.remediationPlan,
            actionUiState = actionUiState,
            onAction = viewModel::onAction,
            metricCards = buildExplanationMetricCards(detail),
            infoEntries = buildExplanationInfoEntries(detail),
        )
        },
        evidence = {
        val detail = uiState.detail
        item("explanation_steps_title") { SmallTitle(DiagnosticLang.DetailPages.Common.Evidence) }
        if (detail.chain.steps.isEmpty()) {
            item("explanation_empty") {
                EmptyStateCard(message = DiagnosticLang.DetailPages.ExplanationChain.NoChain)
            }
        } else {
            item("explanation_steps") { ExplanationChainStepCard(chain = detail.chain) }
        }
        },
    )
}

@Composable
@Destination<RootGraph>
fun RawTraceDetailScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<RawTraceDetailViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }
    HandleDiagnosticActionEvents(events = viewModel.events, navigator = navigator)

    DiagnosticDetailScaffold(
        title = DiagnosticLang.DetailPages.RawTrace.Title,
        navigator = navigator,
        overview = {
        val detail = uiState.detail
        diagnosticOverviewItems(
            page = DiagnosticDetailPage.RawTrace,
            keyPrefix = "raw_trace",
            banner =
                DiagnosticBannerState(
                    headline = detail.profileName ?: DiagnosticLang.DetailPages.RawTrace.Headline,
                    subtitle = buildRawTraceBannerSubtitle(detail),
                    tone = buildRawTraceBannerTone(detail),
                ),
            structuredError = detail.structuredError,
            remediationPlan = detail.remediationPlan,
            actionUiState = actionUiState,
            onAction = viewModel::onAction,
            metricCards = buildRawTraceMetricCards(detail),
            infoEntries = buildRawTraceInfoEntries(detail),
        )
        },
        evidence = {
        val detail = uiState.detail
        item("raw_trace_payload_title") {
            SmallTitle(DiagnosticLang.DetailPages.RawTrace.RawPayload)
        }
        item("raw_trace_payload") { RawTraceSectionsCard(sections = detail.rawSections) }
        },
    )
}

@Composable
@Destination<RootGraph>
fun SourceRegistryOverviewScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<SourceRegistryOverviewViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionUiState by viewModel.actionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.refresh() }
    HandleDiagnosticActionEvents(events = viewModel.events, navigator = navigator)

    DiagnosticDetailScaffold(
        title = DiagnosticLang.DetailPages.SourceRegistry.Title,
        navigator = navigator,
        overview = {
        val detail = uiState.detail
        diagnosticOverviewItems(
            page = DiagnosticDetailPage.SourceRegistry,
            keyPrefix = "source_registry",
            banner =
                DiagnosticBannerState(
                    headline =
                        detail.items.firstOrNull { it.isEffective }?.source?.name
                            ?: detail.profileName
                            ?: DiagnosticLang.DetailPages.SourceRegistry.Headline,
                    subtitle = buildSourceRegistryBannerSubtitle(detail),
                    tone = buildSourceRegistryBannerTone(detail),
                ),
            structuredError = detail.structuredError,
            remediationPlan = detail.remediationPlan,
            actionUiState = actionUiState,
            onAction = viewModel::onAction,
            metricCards = buildSourceRegistryMetricCards(detail),
            infoEntries = buildSourceRegistryInfoEntries(detail),
        )
        },
        evidence = {
        val detail = uiState.detail
        if (detail.items.isEmpty()) {
            item("source_registry_empty") {
                EmptyStateCard(message = DiagnosticLang.DetailPages.SourceRegistry.NoSources)
            }
        } else {
            SourceRegistryRole.entries.forEach { role ->
                val items = detail.items.filter { it.role == role }
                if (items.isNotEmpty()) {
                    item("source_registry_title_${role.name}") { SmallTitle(role.toDisplayLabel()) }
                    item("source_registry_group_${role.name}") {
                        SourceRegistryGroupCard(items = items)
                    }
                }
            }
        }
        },
    )
}

@Composable
private fun DiagnosticDetailScaffold(
    title: String,
    navigator: DestinationsNavigator,
    overview: LazyListScope.() -> Unit,
    evidence: LazyListScope.() -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                val spacing = LocalSpacing.current
                if (adaptiveInfo.prefersTwoPaneContent) {
                    Row(
                        modifier =
                            Modifier.adaptiveContentWidth(adaptiveInfo.preferredTwoPaneMaxWidth)
                                .padding(horizontal = spacing.gutter),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                        verticalAlignment = Alignment.Top,
                    ) {
                        ScreenLazyColumn(
                            modifier = Modifier.weight(0.44f),
                            scrollBehavior = scrollBehavior,
                            innerPadding = innerPadding,
                            contentPadding =
                                PaddingValues(
                                    top = innerPadding.calculateTopPadding() + spacing.xl,
                                    bottom = innerPadding.calculateBottomPadding() + spacing.xl,
                                ),
                            enableGlobalScroll = false,
                            content = overview,
                        )
                        ScreenLazyColumn(
                            modifier = Modifier.weight(0.56f),
                            scrollBehavior = scrollBehavior,
                            innerPadding = innerPadding,
                            contentPadding =
                                PaddingValues(
                                    top = innerPadding.calculateTopPadding() + spacing.xl,
                                    bottom = innerPadding.calculateBottomPadding() + spacing.xl,
                                ),
                            enableGlobalScroll = false,
                            content = evidence,
                        )
                    }
                } else {
                    ScreenLazyColumn(
                        modifier = Modifier.adaptiveContentWidth(adaptiveInfo.preferredSinglePaneMaxWidth),
                        scrollBehavior = scrollBehavior,
                        innerPadding = innerPadding,
                    ) {
                        overview()
                        evidence()
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleDiagnosticActionEvents(
    events: Flow<DiagnosticActionEvent>,
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val clipboardManager =
        context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager

    CollectFlowWithLifecycle(flow = events) { event ->
        when (event) {
            is DiagnosticActionEvent.CopyText -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("diagnostic-trace", event.text))
            }

            is DiagnosticActionEvent.Navigate -> {
                when (event.target) {
                    DiagnosticNavigationTarget.RuntimeHealth -> {
                        navigator.navigate(RuntimeHealthDetailScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.RuleSetInspector -> {
                        navigator.navigate(RuleSetInspectorScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.SnapshotHistory -> {
                        navigator.navigate(SnapshotHistoryScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.ExplanationChain -> {
                        navigator.navigate(ExplanationChainDetailScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.RawTrace -> {
                        navigator.navigate(RawTraceDetailScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.SourceRegistry -> {
                        navigator.navigate(SourceRegistryOverviewScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.Providers -> {
                        navigator.navigate(ProvidersScreenDestination) {
                            launchSingleTop = true
                        }
                    }

                    DiagnosticNavigationTarget.Logs -> {
                        navigator.navigate(LogScreenDestination) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.diagnosticOverviewItems(
    page: DiagnosticDetailPage,
    keyPrefix: String,
    banner: DiagnosticBannerState,
    structuredError: StructuredError?,
    remediationPlan: DiagnosticRemediationPlan,
    actionUiState: DiagnosticActionUiState,
    onAction: (DiagnosticRemediationAction) -> Unit,
    metricCards: List<MetricCardModel>,
    infoEntries: List<DetailInfoEntry> = emptyList(),
) {
    val visibleRemediationPlan = remediationPlan.filteredFor(page)
    item("${keyPrefix}_summary_title") { SmallTitle(DiagnosticLang.DetailPages.Common.StateSummary) }
    item("${keyPrefix}_summary") {
        DiagnosticStateSummaryCard(
            banner = banner,
            structuredError = structuredError,
        )
    }
    if (visibleRemediationPlan.actions.isNotEmpty() || actionUiState.feedback != null) {
        item("${keyPrefix}_actions_title") { SmallTitle(DiagnosticLang.DetailPages.Common.RepairLoop) }
        item("${keyPrefix}_actions") {
            DiagnosticRemediationPanel(
                plan = visibleRemediationPlan,
                actionUiState = actionUiState,
                onAction = onAction,
            )
        }
    }
    item("${keyPrefix}_metrics_title") { SmallTitle(DiagnosticLang.DetailPages.Common.Signals) }
    item("${keyPrefix}_metrics") {
        DetailSignalCard(cards = metricCards)
    }
    if (infoEntries.isNotEmpty()) {
        item("${keyPrefix}_info_title") { SmallTitle(DiagnosticLang.DetailPages.Common.Identity) }
        item("${keyPrefix}_info") { DetailInfoCard(entries = infoEntries) }
    }
}

private fun DiagnosticRemediationPlan.filteredFor(page: DiagnosticDetailPage): DiagnosticRemediationPlan {
    val visibleCommands =
        when (page) {
            DiagnosticDetailPage.RuntimeHealth ->
                setOf(
                    DiagnosticActionCommand.StartRuntime,
                    DiagnosticActionCommand.ReloadRuntime,
                    DiagnosticActionCommand.RestartRuntime,
                    DiagnosticActionCommand.RefreshSources,
                    DiagnosticActionCommand.OpenExplanationChain,
                    DiagnosticActionCommand.OpenRawTrace,
                    DiagnosticActionCommand.OpenSourceRegistry,
                    DiagnosticActionCommand.OpenLogs,
                    DiagnosticActionCommand.RefreshPage,
                )
            DiagnosticDetailPage.RuleSetInspector ->
                setOf(
                    DiagnosticActionCommand.ReloadRuntime,
                    DiagnosticActionCommand.RefreshSources,
                    DiagnosticActionCommand.OpenSourceRegistry,
                    DiagnosticActionCommand.OpenExplanationChain,
                    DiagnosticActionCommand.RefreshPage,
                )
            DiagnosticDetailPage.SnapshotHistory ->
                setOf(
                    DiagnosticActionCommand.OpenRuntimeHealth,
                    DiagnosticActionCommand.OpenRawTrace,
                    DiagnosticActionCommand.RefreshPage,
                )
            DiagnosticDetailPage.ExplanationChain ->
                setOf(
                    DiagnosticActionCommand.StartRuntime,
                    DiagnosticActionCommand.ReloadRuntime,
                    DiagnosticActionCommand.RestartRuntime,
                    DiagnosticActionCommand.RefreshSources,
                    DiagnosticActionCommand.OpenRuleSetInspector,
                    DiagnosticActionCommand.OpenSourceRegistry,
                    DiagnosticActionCommand.OpenRawTrace,
                    DiagnosticActionCommand.RefreshPage,
                )
            DiagnosticDetailPage.RawTrace ->
                setOf(
                    DiagnosticActionCommand.CopyRawTrace,
                    DiagnosticActionCommand.OpenLogs,
                    DiagnosticActionCommand.OpenExplanationChain,
                    DiagnosticActionCommand.ReloadRuntime,
                    DiagnosticActionCommand.RestartRuntime,
                    DiagnosticActionCommand.RefreshPage,
                )
            DiagnosticDetailPage.SourceRegistry ->
                setOf(
                    DiagnosticActionCommand.RefreshSources,
                    DiagnosticActionCommand.OpenProviders,
                    DiagnosticActionCommand.ReloadRuntime,
                    DiagnosticActionCommand.OpenRuleSetInspector,
                    DiagnosticActionCommand.RefreshPage,
                )
        }

    return copy(actions = actions.filter { it.command in visibleCommands }.distinctBy { it.command })
}

@Composable
private fun DiagnosticStateSummaryCard(
    banner: DiagnosticBannerState,
    structuredError: StructuredError?,
) {
    Card {
        InfoSettingRow(
            title = banner.headline,
            summary = banner.subtitle,
            valueLabel = banner.tone.toStateLabel(),
            tone = banner.tone,
            badgeLeadingDot = banner.tone.requiresAttentionDot(),
            showDivider = structuredError != null,
        )
        structuredError?.let { error ->
            InfoSettingRow(
                title = error.userVisibleMessage,
                summary = error.technicalDetail ?: error.rawCause,
                valueLabel = error.impact.toDisplayLabel(),
                tone = error.toSemanticTone(),
            )
            InfoSettingRow(
                title = error.phase.toDisplayLabel(),
                summary = error.category.name,
                valueLabel = error.retryability.toDisplayLabel(),
                tone = error.toSemanticTone(),
                badgeLeadingDot = error.toSemanticTone().requiresAttentionDot(),
                showDivider = false,
            )
        }
    }
}

@Composable
private fun DetailSignalCard(
    cards: List<MetricCardModel>,
) {
    if (cards.isEmpty()) {
        return
    }

    Card {
        cards.forEachIndexed { index, card ->
            InfoSettingRow(
                title = card.title,
                summary = card.summary,
                valueLabel = card.value,
                tone = card.tone,
                badgeLeadingDot = card.tone.requiresAttentionDot(),
                showDivider = index != cards.lastIndex,
            )
        }
    }
}

@Composable
private fun DetailInfoCard(entries: List<DetailInfoEntry>) {
    if (entries.isEmpty()) {
        return
    }

    Card {
        entries.forEachIndexed { index, entry ->
            InfoSettingRow(
                title = entry.label,
                summary = entry.value,
                showDivider = index != entries.lastIndex,
            )
        }
    }
}

@Composable
private fun HealthCheckCard(items: List<HealthCheckItem>) {
    if (items.isEmpty()) {
        return
    }

    Card {
        items.forEachIndexed { index, item ->
            InfoSettingRow(
                title = item.label,
                summary = item.detail,
                valueLabel = item.severity.toBadgeLabel(),
                tone = item.severity.toSemanticTone(),
                badgeLeadingDot = item.severity != HealthCheckSeverity.Ok,
                showDivider = index != items.lastIndex,
            )
        }
    }
}

@Composable
private fun RuleSetGroupCard(ruleSets: List<RuleSet>) {
    Card {
        ruleSets.forEachIndexed { index, ruleSet ->
            InfoSettingRow(
                title = ruleSet.name,
                summary = buildRuleSetRowSummary(ruleSet),
                valueLabel = ruleSet.origin.toDisplayLabel(),
                tone = ruleSet.origin.toTone(),
                badgeLeadingDot = true,
                showDivider = index != ruleSets.lastIndex,
            )
        }
    }
}

@Composable
private fun SnapshotTimelineCard(detail: SnapshotHistoryDetail) {
    val snapshots = detail.snapshots
    if (snapshots.isEmpty()) {
        EmptyStateCard(message = DiagnosticLang.DetailPages.SnapshotHistory.NoSnapshots)
        return
    }

    Card {
        snapshots.forEachIndexed { index, snapshot ->
            val isCurrent = snapshot.snapshotId == detail.currentSnapshotId
            InfoSettingRow(
                title = snapshot.label,
                summary =
                    formatLongTimestamp(snapshot.createdAtMillis) +
                        " · " +
                        buildSnapshotSubtitle(snapshot),
                valueLabel =
                    if (isCurrent) {
                        DiagnosticLang.DetailPages.Common.Current
                    } else {
                        snapshot.snapshotType.toDisplayLabel()
                    },
                tone = if (isCurrent) SemanticTone.Brand else SemanticTone.Info,
                badgeLeadingDot = isCurrent,
                showDivider = index != snapshots.lastIndex,
            )
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card {
        InfoSettingRow(
            title = message,
            summary = DiagnosticLang.DetailPages.Common.NotAvailable,
            showDivider = false,
        )
    }
}

@Composable
private fun DetailCardDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 70.dp, end = 18.dp),
        thickness = 0.5.dp,
        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.16f),
    )
}

private fun buildRuntimeBannerSubtitle(detail: RuntimeHealthDetail): String {
    val payloadReady = listOf(
        detail.runtimeSnapshot.profileReady,
        detail.runtimeSnapshot.groupsReady,
        detail.runtimeSnapshot.trafficReady,
    ).count { it }
    return "${detail.runtimeSnapshot.phase.toDisplayLabel()} · " +
        DiagnosticLang.DetailPages.RuntimeHealth.PayloadReadyFormat.format(payloadReady, 3)
}

private fun buildRuntimeBannerTone(detail: RuntimeHealthDetail): SemanticTone {
    return when (detail.runtimeSnapshot.phase) {
        RuntimePhase.Running -> SemanticTone.Success
        RuntimePhase.Starting,
        RuntimePhase.Stopping -> SemanticTone.Info
        RuntimePhase.Failed,
        RuntimePhase.Idle -> SemanticTone.Neutral
    }
}

private fun buildRuntimeMetricCards(detail: RuntimeHealthDetail): List<MetricCardModel> {
    val runtime = detail.runtimeSnapshot
    val payloadReady = listOf(runtime.profileReady, runtime.groupsReady, runtime.trafficReady).count { it }
    return listOf(
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Lifecycle,
            value = runtime.phase.toDisplayLabel(),
            tone = detail.healthReport.overallSeverity.toSemanticTone(),
            summary = runtime.owner.toDisplayLabel(),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Payload,
            value = "$payloadReady/3",
            tone = if (runtime.payloadReady) SemanticTone.Success else SemanticTone.Warning,
            summary = DiagnosticLang.DetailPages.RuntimeHealth.PayloadReadyFormat.format(payloadReady, 3),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Sources,
            value = detail.externalResources.subscriptionSources.size.toString(),
            tone = buildSourceTone(detail),
            summary = buildSourceSummary(detail),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Generation,
            value = runtime.generation.toString(),
            tone = SemanticTone.Info,
            summary = shortenHash(runtime.effectiveFingerprint),
        ),
    )
}

private fun buildRuntimeInfoEntries(detail: RuntimeHealthDetail): List<DetailInfoEntry> {
    return buildList {
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RuntimeHealth.ActiveProfile,
                value = detail.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RuntimeHealth.Owner,
                value = detail.runtimeSnapshot.owner.toDisplayLabel(),
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RuntimeHealth.Mode,
                value = detail.runtimeSnapshot.targetMode.toDisplayLabel(),
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.Common.ConfigFingerprint,
                value = detail.runtimeSnapshot.effectiveFingerprint ?: DiagnosticLang.DetailPages.Common.NotAvailable,
            )
        )
        detail.sourcePath?.let { path ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.SourcePath,
                    value = path,
                )
            )
        }
    }
}

private fun buildRuleSetBannerSubtitle(detail: RuleSetInspectorDetail): String {
    if (detail.ruleSets.isEmpty()) {
        return detail.sourceLabel.ifBlank { DiagnosticLang.DetailPages.RuleSetInspector.NoRuleSets }
    }
    val matcherCount = detail.ruleSets.sumOf(RuleSet::matcherCount)
    return DiagnosticLang.DetailPages.RuleSetInspector.Overview.format(detail.ruleSets.size, matcherCount)
}

private fun buildRuleSetBannerTone(detail: RuleSetInspectorDetail): SemanticTone {
    return when {
        detail.ruleSets.isEmpty() -> SemanticTone.Neutral
        detail.ruleSets.any { it.origin == RuleSetOrigin.Provider } -> SemanticTone.Info
        else -> SemanticTone.Brand
    }
}

private fun buildRuleSetMetricCards(detail: RuleSetInspectorDetail): List<MetricCardModel> {
    val matcherCount = detail.ruleSets.sumOf(RuleSet::matcherCount)
    val providerCount = detail.ruleSets.count { it.origin == RuleSetOrigin.Provider }
    return listOf(
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuleSetInspector.TotalRuleSets,
            value = detail.ruleSets.size.toString(),
            tone = if (detail.ruleSets.isEmpty()) SemanticTone.Neutral else SemanticTone.Info,
            summary = detail.sourceLabel.ifBlank { DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable },
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuleSetInspector.TotalMatchers,
            value = matcherCount.toString(),
            tone = SemanticTone.Success,
            summary = DiagnosticLang.DetailPages.RuleSetInspector.Matchers,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuleSetInspector.ProviderCount,
            value = providerCount.toString(),
            tone = SemanticTone.Brand,
            summary = DiagnosticLang.DetailPages.RuleSetInspector.Provider,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RuleSetInspector.OverlayRules,
            value = detail.overlayRuleCount.toString(),
            tone = if (detail.overlayRuleCount > 0) SemanticTone.Warning else SemanticTone.Neutral,
            summary = DiagnosticLang.DetailPages.RuleSetInspector.Override,
        ),
    )
}

private fun buildRuleSetInfoEntries(detail: RuleSetInspectorDetail): List<DetailInfoEntry> {
    return buildList {
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RuntimeHealth.ActiveProfile,
                value = detail.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RuleSetInspector.SourceRuntime,
                value = detail.sourceLabel.ifBlank { DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable },
            )
        )
        detail.configHash?.let { hash ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.ConfigFingerprint,
                    value = hash,
                )
            )
        }
        detail.sourcePath?.let { path ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.SourcePath,
                    value = path,
                )
            )
        }
    }
}

private fun buildRuleSetSummary(ruleSet: RuleSet): String {
    return buildString {
        append(DiagnosticLang.DetailPages.RuleSetInspector.Matchers)
        append(": ")
        append(ruleSet.matcherCount)
        append(" · ")
        append(DiagnosticLang.DetailPages.RuleSetInspector.CompoundMatchers)
        append(": ")
        append(ruleSet.compoundMatcherCount)
    }
}

private fun buildRuleSetRowSummary(ruleSet: RuleSet): String {
    return (listOf(buildRuleSetSummary(ruleSet)) + buildRuleSetDetails(ruleSet))
        .joinToString(separator = "\n")
}

private fun buildRuleSetDetails(ruleSet: RuleSet): List<String> {
    val targetSummary =
        ruleSet.targetDistribution.entries
            .sortedByDescending { it.value }
            .take(3)
            .joinToString(separator = " · ") { "${it.key}:${it.value}" }

    return buildList {
        if (targetSummary.isNotBlank()) {
            add("${DiagnosticLang.DetailPages.RuleSetInspector.Targets}: $targetSummary")
        }
        ruleSet.providerUrl?.let { url ->
            add("${DiagnosticLang.DetailPages.RuleSetInspector.ProviderUrl}: $url")
        }
        ruleSet.providerBehavior?.let { behavior ->
            add("${DiagnosticLang.DetailPages.RuleSetInspector.ProviderBehavior}: $behavior")
        }
        val previewMatchers = ruleSet.matchers.take(2)
        previewMatchers.forEach { matcher ->
            add(matcher.rawText)
        }
        val remainingCount = ruleSet.matchers.size - previewMatchers.size
        if (remainingCount > 0) {
            add(DiagnosticLang.DetailPages.RuleSetInspector.RemainingMatchers.format(remainingCount))
        }
    }
}

private fun buildExplanationBannerSubtitle(detail: ExplanationChainDetail): String {
    return if (detail.chain.steps.isEmpty()) {
        DiagnosticLang.DetailPages.ExplanationChain.NoChain
    } else {
        DiagnosticLang.DetailPages.ExplanationChain.Overview.format(
            detail.chain.steps.size,
            detail.chain.steps.count { !it.matched },
        )
    }
}

private fun buildExplanationBannerTone(detail: ExplanationChainDetail): SemanticTone {
    return when {
        detail.chain.steps.isEmpty() -> SemanticTone.Neutral
        detail.chain.isSuccess -> SemanticTone.Success
        else -> SemanticTone.Info
    }
}

private fun buildExplanationMetricCards(detail: ExplanationChainDetail): List<MetricCardModel> {
    val blockedCount = detail.chain.steps.count { !it.matched }
    return listOf(
        MetricCardModel(
            title = DiagnosticLang.DetailPages.ExplanationChain.StageCount,
            value = detail.chain.steps.size.toString(),
            tone = if (detail.chain.steps.isEmpty()) SemanticTone.Neutral else SemanticTone.Info,
            summary = detail.chain.summary.takeIf { it.isNotBlank() },
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.ExplanationChain.BlockedStages,
            value = blockedCount.toString(),
            tone = if (blockedCount > 0) SemanticTone.Warning else SemanticTone.Success,
            summary = detail.chain.failedStep?.label,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.ExplanationChain.RecentEvents,
            value = detail.recentEvents.size.toString(),
            tone = SemanticTone.Info,
            summary = detail.recentEvents.lastOrNull()?.message,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.Common.ConfigFingerprint,
            value = shortenHash(detail.configHash),
            tone = if (detail.configHash != null) SemanticTone.Brand else SemanticTone.Neutral,
            summary = detail.chain.conclusion,
        ),
    )
}

private fun buildExplanationInfoEntries(detail: ExplanationChainDetail): List<DetailInfoEntry> {
    return buildList {
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.ExplanationChain.Profile,
                value = detail.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.ExplanationChain.ChainId,
                value = detail.chain.chainId,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.ExplanationChain.Conclusion,
                value = detail.chain.conclusion,
            )
        )
        detail.configHash?.let { hash ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.ConfigFingerprint,
                    value = hash,
                )
            )
        }
        detail.sourcePath?.let { path ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.SourcePath,
                    value = path,
                )
            )
        }
    }
}

@Composable
private fun ExplanationChainStepCard(chain: ExplanationChain) {
    Card {
        chain.steps.forEachIndexed { index, step ->
            val tone =
                when {
                    !step.matched -> SemanticTone.Warning
                    index == chain.steps.lastIndex && chain.isSuccess -> SemanticTone.Success
                    else -> SemanticTone.Info
                }
            InfoSettingRow(
                title = step.label,
                summary = buildExplanationStepRowSummary(step),
                valueLabel =
                    if (step.matched) {
                        DiagnosticLang.DetailPages.ExplanationChain.Matched
                    } else {
                        DiagnosticLang.DetailPages.ExplanationChain.Blocked
                    },
                tone = tone,
                badgeLeadingDot = !step.matched,
                showDivider = index != chain.steps.lastIndex,
            )
        }
    }
}

private fun buildExplanationStepDetails(step: ExplanationStep): List<String> {
    return buildList {
        step.input?.takeIf { it.isNotBlank() }?.let { input ->
            add("${DiagnosticLang.DetailPages.ExplanationChain.Input}: $input")
        }
        step.output?.takeIf { it.isNotBlank() }?.let { output ->
            add("${DiagnosticLang.DetailPages.ExplanationChain.Output}: $output")
        }
        step.detail?.takeIf { it.isNotBlank() }?.let(::add)
    }
}

private fun buildExplanationStepRowSummary(step: ExplanationStep): String {
    return (listOf(step.stage) + buildExplanationStepDetails(step))
        .filter(String::isNotBlank)
        .joinToString(separator = "\n")
}

private fun buildRawTraceBannerSubtitle(detail: RawTraceDetail): String {
    return DiagnosticLang.DetailPages.RawTrace.SectionOverview.format(
        detail.rawSections.size,
        detail.rawSections.sumOf { it.lines.size },
    )
}

private fun buildRawTraceBannerTone(detail: RawTraceDetail): SemanticTone {
    return when {
        detail.steps.isEmpty() -> SemanticTone.Neutral
        detail.rawSections.size > 1 -> SemanticTone.Info
        else -> SemanticTone.Info
    }
}

private fun buildRawTraceMetricCards(detail: RawTraceDetail): List<MetricCardModel> {
    val failureCount = detail.recentEvents.count { it.isFailure }
    val latestEvent = detail.recentEvents.lastOrNull()
    return listOf(
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RawTrace.EventCount,
            value = detail.recentEvents.size.toString(),
            tone = if (detail.recentEvents.isEmpty()) SemanticTone.Neutral else SemanticTone.Info,
            summary = detail.steps.firstOrNull()?.label,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RawTrace.FailureCount,
            value = failureCount.toString(),
            tone = if (failureCount > 0) SemanticTone.Warning else SemanticTone.Success,
            summary = detail.structuredError?.userVisibleMessage,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RawTrace.RawSections,
            value = detail.rawSections.size.toString(),
            tone = SemanticTone.Brand,
            summary = buildRawTraceBannerSubtitle(detail),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.RawTrace.LatestEvent,
            value = formatShortTimestamp(latestEvent?.timestamp),
            tone = SemanticTone.Info,
            summary = detail.runtimePhase.toDisplayLabel(),
        ),
    )
}

private fun buildRawTraceInfoEntries(detail: RawTraceDetail): List<DetailInfoEntry> {
    return buildList {
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.ExplanationChain.Profile,
                value = detail.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RawTrace.TraceId,
                value = detail.traceId,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.RawTrace.RuntimePhase,
                value = detail.runtimePhase.toDisplayLabel(),
            )
        )
        detail.configHash?.let { hash ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.ConfigFingerprint,
                    value = hash,
                )
            )
        }
    }
}

@Composable
private fun RawTraceSectionsCard(sections: List<RawTraceSection>) {
    if (sections.isEmpty()) {
        EmptyStateCard(message = DiagnosticLang.DetailPages.RawTrace.NoEvents)
        return
    }

    val spacing = LocalSpacing.current

    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            sections.forEachIndexed { index, section ->
                InfoSettingRow(
                    title = section.title,
                    summary = DiagnosticLang.DetailPages.RawTrace.SectionOverview.format(1, section.lines.size),
                    valueLabel = section.lines.size.toString(),
                    tone = SemanticTone.Info,
                    showDivider = false,
                )
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = spacing.gutter, vertical = spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        Text(
                            text = section.lines.joinToString(separator = "\n").ifBlank {
                                DiagnosticLang.DetailPages.Common.NotAvailable
                            },
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            fontSize = MiuixTheme.textStyles.body2.fontSize,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
                if (index != sections.lastIndex) {
                    DetailCardDivider()
                }
            }
        }
    }
}

private fun buildSourceRegistryBannerSubtitle(detail: SourceRegistryOverviewDetail): String {
    return DiagnosticLang.DetailPages.SourceRegistry.RegistryOverview.format(
        detail.items.size,
        detail.items.count { it.source.isRemote },
    )
}

private fun buildSourceRegistryBannerTone(detail: SourceRegistryOverviewDetail): SemanticTone {
    return when {
        detail.items.isEmpty() -> SemanticTone.Neutral
        detail.items.any { it.isEffective } -> SemanticTone.Brand
        else -> SemanticTone.Info
    }
}

private fun buildSourceRegistryMetricCards(
    detail: SourceRegistryOverviewDetail,
): List<MetricCardModel> {
    val remoteCount = detail.items.count { it.source.isRemote }
    val localCount = detail.items.size - remoteCount
    val effectiveCount = detail.items.count { it.isEffective }
    return listOf(
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SourceRegistry.TotalSources,
            value = detail.items.size.toString(),
            tone = if (detail.items.isEmpty()) SemanticTone.Neutral else SemanticTone.Info,
            summary = buildSourceRegistryBannerSubtitle(detail),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SourceRegistry.RemoteSources,
            value = remoteCount.toString(),
            tone = if (remoteCount > 0) SemanticTone.Warning else SemanticTone.Neutral,
            summary = DiagnosticLang.DetailPages.SourceRegistry.RemoteOverride,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SourceRegistry.LocalSources,
            value = localCount.toString(),
            tone = if (localCount > 0) SemanticTone.Success else SemanticTone.Neutral,
            summary = DiagnosticLang.DetailPages.SourceRegistry.ProfileConfig,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SourceRegistry.EffectiveSources,
            value = effectiveCount.toString(),
            tone = if (effectiveCount > 0) SemanticTone.Brand else SemanticTone.Neutral,
            summary = detail.items.firstOrNull { it.isEffective }?.source?.name,
        ),
    )
}

private fun buildSourceRegistryInfoEntries(
    detail: SourceRegistryOverviewDetail,
): List<DetailInfoEntry> {
    return buildList {
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.ExplanationChain.Profile,
                value = detail.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
            )
        )
        detail.effectiveFingerprint?.let { fingerprint ->
            add(
                DetailInfoEntry(
                    label = DiagnosticLang.DetailPages.Common.ConfigFingerprint,
                    value = fingerprint,
                )
            )
        }
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.Common.LatestCapture,
                value = formatLongTimestamp(detail.generatedAtMillis),
            )
        )
    }
}

@Composable
private fun SourceRegistryGroupCard(items: List<SourceRegistryItem>) {
    Card {
        items.forEachIndexed { index, item ->
            InfoSettingRow(
                title =
                    if (item.isEffective) {
                        "${item.source.name} · ${DiagnosticLang.DetailPages.SourceRegistry.Effective}"
                    } else {
                        item.source.name
                    },
                summary = buildSourceRegistryRowSummary(item),
                valueLabel = item.source.syncState.toDisplayLabel(),
                tone = item.source.syncState.toTone(),
                badgeLeadingDot = item.source.syncState != SyncState.Succeeded || item.isEffective,
                showDivider = index != items.lastIndex,
            )
        }
    }
}

private fun buildSourceRegistryDetails(item: SourceRegistryItem): List<String> {
    return buildList {
        item.source.url?.takeIf { it.isNotBlank() }?.let(::add)
        item.source.localPath?.takeIf { it.isNotBlank() }?.let(::add)
        add("${DiagnosticLang.DetailPages.SourceRegistry.SourceType}: ${item.source.sourceType.toDisplayLabel()}")
        add("${DiagnosticLang.DetailPages.SourceRegistry.SyncState}: ${item.source.syncState.toDisplayLabel()}")
        item.source.owner.label.takeIf { it.isNotBlank() }?.let { owner ->
            add("${DiagnosticLang.DetailPages.SourceRegistry.Owner}: $owner")
        }
        item.itemCount?.let { count ->
            add("${DiagnosticLang.DetailPages.SourceRegistry.ItemCount}: $count")
        }
        item.fingerprint?.let { fingerprint ->
            add("${DiagnosticLang.DetailPages.Common.ConfigFingerprint}: ${shortenHash(fingerprint)}")
        }
        if (item.source.syncIntervalSeconds > 0L) {
            add("${DiagnosticLang.DetailPages.SourceRegistry.Interval}: ${item.source.syncIntervalSeconds}s")
        }
    }
}

private fun buildSourceRegistryRowSummary(item: SourceRegistryItem): String {
    val headline =
        item.note ?: item.source.url ?: item.source.localPath ?: item.role.toDisplayLabel()
    return (listOf(headline) + buildSourceRegistryDetails(item))
        .filter(String::isNotBlank)
        .joinToString(separator = "\n")
}

private fun buildSnapshotBannerSubtitle(detail: SnapshotHistoryDetail): String {
    return if (detail.snapshots.isEmpty()) {
        DiagnosticLang.DetailPages.SnapshotHistory.NoSnapshots
    } else {
        DiagnosticLang.DetailPages.SnapshotHistory.Overview.format(detail.snapshots.size)
    }
}

private fun buildSnapshotBannerTone(detail: SnapshotHistoryDetail): SemanticTone {
    return when {
        detail.currentSnapshotId != null -> SemanticTone.Brand
        detail.snapshots.isEmpty() -> SemanticTone.Neutral
        else -> SemanticTone.Info
    }
}

private fun buildSnapshotMetricCards(detail: SnapshotHistoryDetail): List<MetricCardModel> {
    val profilesCovered = detail.snapshots.map { it.profileId }.distinct().size
    val currentSnapshot = detail.snapshots.firstOrNull { it.snapshotId == detail.currentSnapshotId }
    val latestSnapshot = detail.snapshots.firstOrNull()
    return listOf(
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SnapshotHistory.TotalSnapshots,
            value = detail.snapshots.size.toString(),
            tone = if (detail.snapshots.isEmpty()) SemanticTone.Neutral else SemanticTone.Info,
            summary = DiagnosticLang.DetailPages.SnapshotHistory.Overview.format(detail.snapshots.size),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SnapshotHistory.ProfilesCovered,
            value = profilesCovered.toString(),
            tone = SemanticTone.Success,
            summary = DiagnosticLang.DetailPages.RuntimeHealth.ActiveProfile,
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.SnapshotHistory.CurrentFingerprint,
            value = shortenHash(currentSnapshot?.configHash),
            tone = if (currentSnapshot != null) SemanticTone.Brand else SemanticTone.Neutral,
            summary = currentSnapshot?.snapshotType?.toDisplayLabel(),
        ),
        MetricCardModel(
            title = DiagnosticLang.DetailPages.Common.LatestCapture,
            value = formatShortTimestamp(latestSnapshot?.createdAtMillis),
            tone = SemanticTone.Warning,
            summary = latestSnapshot?.let { ByteFormatter.format(it.configSizeBytes) },
        ),
    )
}

private fun buildCurrentSnapshotEntries(snapshot: com.github.yumelira.yumebox.domain.model.WorkspaceSnapshot): List<DetailInfoEntry> {
    return buildList {
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.SnapshotHistory.CurrentFingerprint,
                value = snapshot.configHash,
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.Common.LatestCapture,
                value = formatLongTimestamp(snapshot.createdAtMillis),
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.SnapshotHistory.TotalSnapshots,
                value = snapshot.snapshotType.toDisplayLabel(),
            )
        )
        add(
            DetailInfoEntry(
                label = DiagnosticLang.DetailPages.Common.SourcePath,
                value = snapshot.metadata["source_path"] ?: DiagnosticLang.DetailPages.Common.NotAvailable,
            )
        )
    }
}

private fun buildSnapshotSubtitle(snapshot: com.github.yumelira.yumebox.domain.model.WorkspaceSnapshot): String {
    return DiagnosticLang.DetailPages.SnapshotHistory.SnapshotSummary.format(
        snapshot.snapshotType.toDisplayLabel(),
        ByteFormatter.format(snapshot.configSizeBytes),
        shortenHash(snapshot.configHash),
    )
}

private fun buildSourceTone(detail: RuntimeHealthDetail): SemanticTone {
    return when {
        detail.externalResources.staleCount > 0 -> SemanticTone.Warning
        detail.externalResources.pendingCount > 0 -> SemanticTone.Info
        detail.externalResources.subscriptionSources.isEmpty() -> SemanticTone.Neutral
        else -> SemanticTone.Success
    }
}

private fun buildSourceSummary(detail: RuntimeHealthDetail): String {
    return when {
        detail.externalResources.subscriptionSources.isEmpty() -> DiagnosticLang.DetailPages.RuntimeHealth.SourcesEmpty
        detail.externalResources.staleCount > 0 || detail.externalResources.pendingCount > 0 ->
            DiagnosticLang.DetailPages.RuntimeHealth.SourcesAttention.format(
                detail.externalResources.staleCount,
                detail.externalResources.pendingCount,
            )
        else ->
            DiagnosticLang.DetailPages.RuntimeHealth.SourcesHealthy.format(
                detail.externalResources.subscriptionSources.size
            )
    }
}

private fun HealthCheckSeverity.toBadgeLabel(): String {
    return when (this) {
        HealthCheckSeverity.Ok -> DiagnosticLang.DetailPages.Common.Ready
        HealthCheckSeverity.Info -> DiagnosticLang.DetailPages.Common.Waiting
        HealthCheckSeverity.Warning,
        HealthCheckSeverity.Error,
        HealthCheckSeverity.Critical -> DiagnosticLang.DetailPages.Common.Attention
    }
}

private fun RuleSetOrigin.toDisplayLabel(): String {
    return when (this) {
        RuleSetOrigin.Inline -> DiagnosticLang.DetailPages.RuleSetInspector.Inline
        RuleSetOrigin.Override -> DiagnosticLang.DetailPages.RuleSetInspector.Override
        RuleSetOrigin.Provider -> DiagnosticLang.DetailPages.RuleSetInspector.Provider
        RuleSetOrigin.SubRule -> DiagnosticLang.DetailPages.RuleSetInspector.SubRule
    }
}

private fun RuleSetOrigin.toTone(): SemanticTone {
    return when (this) {
        RuleSetOrigin.Inline -> SemanticTone.Success
        RuleSetOrigin.Override -> SemanticTone.Warning
        RuleSetOrigin.Provider -> SemanticTone.Info
        RuleSetOrigin.SubRule -> SemanticTone.Brand
    }
}

private fun SnapshotType.toDisplayLabel(): String {
    return when (this) {
        SnapshotType.AutoSave -> DiagnosticLang.DetailPages.SnapshotHistory.AutoSave
        SnapshotType.ManualSave -> DiagnosticLang.DetailPages.SnapshotHistory.ManualSave
        SnapshotType.PreChange -> DiagnosticLang.DetailPages.SnapshotHistory.PreChange
        SnapshotType.PostImport -> DiagnosticLang.DetailPages.SnapshotHistory.PostImport
        SnapshotType.Rollback -> DiagnosticLang.DetailPages.SnapshotHistory.Rollback
    }
}

private fun RuntimeOwner.toDisplayLabel(): String {
    return when (this) {
        RuntimeOwner.None -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerNone
        RuntimeOwner.LocalTun -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerLocalTun
        RuntimeOwner.LocalHttp -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerLocalHttp
        RuntimeOwner.RootTun -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerRootTun
    }
}

private fun ProxyMode.toDisplayLabel(): String {
    return when (this) {
        ProxyMode.Tun -> DiagnosticLang.DetailPages.RuntimeHealth.ModeTun
        ProxyMode.RootTun -> DiagnosticLang.DetailPages.RuntimeHealth.ModeRootTun
        ProxyMode.Http -> DiagnosticLang.DetailPages.RuntimeHealth.ModeHttp
    }
}

private fun RuntimePhase.toDisplayLabel(): String {
    return when (this) {
        RuntimePhase.Idle -> DiagnosticLang.DetailPages.RuntimeHealth.IdleShort
        RuntimePhase.Starting -> DiagnosticLang.DetailPages.RuntimeHealth.StartingShort
        RuntimePhase.Running -> DiagnosticLang.Phase.Running
        RuntimePhase.Stopping -> DiagnosticLang.DetailPages.RuntimeHealth.StoppingShort
        RuntimePhase.Failed -> DiagnosticLang.DetailPages.RuntimeHealth.FailedShort
    }
}

private fun SemanticTone.toStateLabel(): String {
    return when (this) {
        SemanticTone.Success,
        SemanticTone.Brand -> DiagnosticLang.DetailPages.Common.Ready
        SemanticTone.Info -> DiagnosticLang.DetailPages.Common.Waiting
        SemanticTone.Warning -> DiagnosticLang.DetailPages.Common.Attention
        SemanticTone.Danger -> DiagnosticLang.DetailPages.Remediation.Failed
        SemanticTone.Neutral -> DiagnosticLang.DetailPages.Common.NotAvailable
    }
}

private fun SemanticTone.requiresAttentionDot(): Boolean {
    return this == SemanticTone.Warning || this == SemanticTone.Danger || this == SemanticTone.Brand
}

private fun SourceRegistryRole.toDisplayLabel(): String {
    return when (this) {
        SourceRegistryRole.RuntimeConfig -> DiagnosticLang.DetailPages.SourceRegistry.RuntimeConfig
        SourceRegistryRole.ProfileConfig -> DiagnosticLang.DetailPages.SourceRegistry.ProfileConfig
        SourceRegistryRole.RuleProvider -> DiagnosticLang.DetailPages.SourceRegistry.RuleProvider
        SourceRegistryRole.ProxyProvider -> DiagnosticLang.DetailPages.SourceRegistry.ProxyProvider
        SourceRegistryRole.RemoteOverride -> DiagnosticLang.DetailPages.SourceRegistry.RemoteOverride
    }
}

private fun SyncState.toDisplayLabel(): String {
    return when (this) {
        SyncState.Idle -> DiagnosticLang.DetailPages.SourceRegistry.StateWaiting
        SyncState.Syncing -> DiagnosticLang.DetailPages.SourceRegistry.StateSyncing
        SyncState.Succeeded -> DiagnosticLang.DetailPages.SourceRegistry.StateReady
        SyncState.Failed -> DiagnosticLang.DetailPages.SourceRegistry.StateFailed
        SyncState.Stale -> DiagnosticLang.DetailPages.SourceRegistry.StateStale
    }
}

private fun SyncState.toTone(): SemanticTone {
    return when (this) {
        SyncState.Idle -> SemanticTone.Neutral
        SyncState.Syncing -> SemanticTone.Info
        SyncState.Succeeded -> SemanticTone.Success
        SyncState.Failed -> SemanticTone.Danger
        SyncState.Stale -> SemanticTone.Warning
    }
}

private fun SourceType.toDisplayLabel(): String {
    return when (this) {
        SourceType.RemoteUrl -> DiagnosticLang.DetailPages.SourceRegistry.TypeRemoteUrl
        SourceType.LocalFile -> DiagnosticLang.DetailPages.SourceRegistry.TypeLocalFile
        SourceType.InlineContent -> DiagnosticLang.DetailPages.SourceRegistry.TypeInlineContent
        SourceType.RuleProvider -> DiagnosticLang.DetailPages.SourceRegistry.TypeRuleProvider
        SourceType.ProxyProvider -> DiagnosticLang.DetailPages.SourceRegistry.TypeProxyProvider
    }
}

private fun formatShortTimestamp(timestamp: Long?): String {
    if (timestamp == null || timestamp <= 0L) {
        return DiagnosticLang.DetailPages.Common.NotAvailable
    }
    return SHORT_TIME_FORMATTER.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}

private fun formatLongTimestamp(timestamp: Long?): String {
    if (timestamp == null || timestamp <= 0L) {
        return DiagnosticLang.DetailPages.Common.NotAvailable
    }
    return LONG_TIME_FORMATTER.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}

private fun shortenHash(hash: String?): String {
    if (hash.isNullOrBlank()) {
        return DiagnosticLang.DetailPages.Common.NotAvailable
    }
    return if (hash.length <= 10) hash else hash.take(10)
}

private val SHORT_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
private val LONG_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

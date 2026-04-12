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

package com.github.yumelira.yumebox.screen.traffic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.formatBytes
import com.github.yumelira.yumebox.data.model.StatisticsTimeRange
import com.github.yumelira.yumebox.feature.meta.presentation.component.ConnectionDetailSheet
import com.github.yumelira.yumebox.feature.meta.presentation.component.toDisplayAddress
import com.github.yumelira.yumebox.feature.meta.presentation.viewmodel.RecentRequestRecord
import com.github.yumelira.yumebox.feature.meta.presentation.viewmodel.TargetSiteRecord
import com.github.yumelira.yumebox.feature.meta.presentation.viewmodel.TrafficStatisticsViewModel
import com.github.yumelira.yumebox.presentation.component.NavigationBackIcon
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.component.TrafficBarChart
import com.github.yumelira.yumebox.presentation.component.appClickable
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object TrafficStatisticsMetrics {
    val TopPadding = 16.dp
    val BottomPadding = 32.dp
    val CardHorizontalPadding = 16.dp
    val CardSpacing = 14.dp
    val CardInnerPadding = 16.dp
    val SummarySpacing = 14.dp
    val ChartInfoSpacing = 10.dp
    val SummaryBlockHeight = 66.dp
    val SummaryValueFontSize = 26.sp
    val ChartHeight = 132.dp
    val SelectedLabelHeight = 24.dp
    val SectionSpacing = 14.dp
    val RecentRequestCardPadding = 16.dp
    val RecentRequestItemPadding = 12.dp
    val RecentRequestItemSpacing = 10.dp
    val RecentRequestChipCorner = 100.dp
}

private enum class TrafficDetailSection {
    RecentRequests,
    TargetSites,
}

@Destination<RootGraph>
@Composable
fun TrafficStatisticsScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<TrafficStatisticsViewModel>()
    val scrollBehavior = MiuixScrollBehavior()

    val todaySummary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val weekSummary by viewModel.weekSummary.collectAsStateWithLifecycle()
    val trafficDifference by viewModel.trafficDifference.collectAsStateWithLifecycle()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsStateWithLifecycle()
    val chartItems by viewModel.chartItems.collectAsStateWithLifecycle()
    val todayTimeContext by viewModel.todayTimeContext.collectAsStateWithLifecycle()
    val selectedBarIndex by viewModel.selectedBarIndex.collectAsStateWithLifecycle()
    val recentRequests by viewModel.recentRequests.collectAsStateWithLifecycle()
    val targetSites by viewModel.targetSites.collectAsStateWithLifecycle()
    var selectedDetailSectionName by rememberSaveable {
        mutableStateOf(TrafficDetailSection.RecentRequests.name)
    }
    var selectedConnectionId by rememberSaveable { mutableStateOf<String?>(null) }
    var showConnectionDetail by rememberSaveable { mutableStateOf(false) }
    val selectedDetailSection =
        remember(selectedDetailSectionName) {
            runCatching { TrafficDetailSection.valueOf(selectedDetailSectionName) }
                .getOrDefault(TrafficDetailSection.RecentRequests)
        }
    val selectedConnection =
        remember(selectedConnectionId, recentRequests) {
            recentRequests.firstOrNull { it.connection.id == selectedConnectionId }?.connection
        }

    LaunchedEffect(showConnectionDetail, selectedConnectionId, selectedConnection) {
        if (showConnectionDetail && selectedConnectionId != null && selectedConnection == null) {
            showConnectionDetail = false
            selectedConnectionId = null
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.TrafficStatistics.Title,
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
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
            val contentMaxWidth = adaptiveInfo.preferredTwoPaneMaxWidth
            ScreenLazyColumn(
                scrollBehavior = scrollBehavior,
                innerPadding = innerPadding,
                topPadding = TrafficStatisticsMetrics.TopPadding,
                bottomPadding = TrafficStatisticsMetrics.BottomPadding,
                modifier = Modifier.adaptiveContentWidth(contentMaxWidth),
            ) {
                item {
                    BoxWithConstraints(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(
                                    horizontal = TrafficStatisticsMetrics.CardHorizontalPadding
                                )
                    ) {
                        val availableAdaptiveInfo =
                            rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                        val useWideLayout = availableAdaptiveInfo.prefersTwoPaneContent
                        if (useWideLayout) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(TrafficStatisticsMetrics.CardSpacing),
                                verticalAlignment = Alignment.Top,
                            ) {
                                OverviewCard(
                                    selectedTimeRange = selectedTimeRange,
                                    todaySummary = todaySummary.total,
                                    weekSummary = weekSummary,
                                    trafficDifference = trafficDifference,
                                    chartItems = chartItems,
                                    todayTimeContext = todayTimeContext,
                                    selectedBarIndex = selectedBarIndex,
                                    onRangeSelected = viewModel::setTimeRange,
                                    onBarSelected = { index ->
                                        viewModel.setSelectedBarIndex(
                                            if (selectedBarIndex == index) -1 else index
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    selectorModifier = Modifier.widthIn(min = 200.dp, max = 248.dp),
                                )
                                DetailsCard(
                                    selectedDetailSection = selectedDetailSection,
                                    recentRequests = recentRequests,
                                    targetSites = targetSites,
                                    onSectionSelected = { selectedDetailSectionName = it.name },
                                    onRecentRequestClick = { request ->
                                        selectedConnectionId = request.connection.id
                                        showConnectionDetail = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    selectorModifier = Modifier.widthIn(min = 240.dp, max = 320.dp),
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement =
                                    Arrangement.spacedBy(TrafficStatisticsMetrics.CardSpacing),
                            ) {
                                OverviewCard(
                                    selectedTimeRange = selectedTimeRange,
                                    todaySummary = todaySummary.total,
                                    weekSummary = weekSummary,
                                    trafficDifference = trafficDifference,
                                    chartItems = chartItems,
                                    todayTimeContext = todayTimeContext,
                                    selectedBarIndex = selectedBarIndex,
                                    onRangeSelected = viewModel::setTimeRange,
                                    onBarSelected = { index ->
                                        viewModel.setSelectedBarIndex(
                                            if (selectedBarIndex == index) -1 else index
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    selectorModifier = Modifier.widthIn(min = 180.dp, max = 220.dp),
                                )
                                DetailsCard(
                                    selectedDetailSection = selectedDetailSection,
                                    recentRequests = recentRequests,
                                    targetSites = targetSites,
                                    onSectionSelected = { selectedDetailSectionName = it.name },
                                    onRecentRequestClick = { request ->
                                        selectedConnectionId = request.connection.id
                                        showConnectionDetail = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    selectorModifier = Modifier.widthIn(min = 220.dp, max = 280.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        ConnectionDetailSheet(
            show = showConnectionDetail && selectedConnection != null,
            connectionInfo = selectedConnection,
            onDismiss = { showConnectionDetail = false },
            onDismissFinished = { selectedConnectionId = null },
        )
    }
}

@Composable
private fun OverviewCard(
    selectedTimeRange: StatisticsTimeRange,
    todaySummary: Long,
    weekSummary: Long,
    trafficDifference: Long,
    chartItems: List<com.github.yumelira.yumebox.presentation.component.BarChartItem>,
    todayTimeContext: String,
    selectedBarIndex: Int,
    onRangeSelected: (StatisticsTimeRange) -> Unit,
    onBarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectorModifier: Modifier = Modifier,
) {
    top.yukonga.miuix.kmp.basic.Card(modifier = modifier) {
        Column(modifier = Modifier.padding(TrafficStatisticsMetrics.CardInnerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = MLang.TrafficStatistics.OverviewTitle,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                )
                TimeRangeSelector(
                    selectedRange = selectedTimeRange,
                    onRangeSelected = onRangeSelected,
                    modifier = selectorModifier,
                )
            }

            Spacer(modifier = Modifier.height(TrafficStatisticsMetrics.SummarySpacing))

            CompactTrafficSummary(
                selectedTimeRange = selectedTimeRange,
                todaySummary = todaySummary,
                weekSummary = weekSummary,
                trafficDifference = trafficDifference,
            )

            val selectedLabelText =
                if (selectedBarIndex >= 0 && chartItems.isNotEmpty()) {
                    chartItems
                        .getOrNull(selectedBarIndex)
                        ?.takeIf { it.label.isNotEmpty() }
                        ?.let { "${it.label}: ${formatBytes(it.value)}" }
                } else null
            val timeContextText =
                todayTimeContext.takeIf {
                    selectedTimeRange == StatisticsTimeRange.TODAY && it.isNotBlank()
                }

            Spacer(modifier = Modifier.height(TrafficStatisticsMetrics.ChartInfoSpacing))

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .heightIn(min = TrafficStatisticsMetrics.SelectedLabelHeight),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (timeContextText != null) {
                    Text(
                        text = timeContextText,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    AnimatedContent(
                        targetState = selectedLabelText,
                        transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                        label = "selected_bar_label",
                    ) { labelText ->
                        if (labelText != null) {
                            Text(
                                text = labelText,
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(modifier = Modifier.fillMaxWidth().height(TrafficStatisticsMetrics.ChartHeight)) {
                TrafficBarChart(
                    items = chartItems,
                    selectedIndex = selectedBarIndex,
                    onItemClick = { index -> onBarSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun DetailsCard(
    selectedDetailSection: TrafficDetailSection,
    recentRequests: List<RecentRequestRecord>,
    targetSites: List<TargetSiteRecord>,
    onSectionSelected: (TrafficDetailSection) -> Unit,
    onRecentRequestClick: (RecentRequestRecord) -> Unit,
    modifier: Modifier = Modifier,
    selectorModifier: Modifier = Modifier,
) {
    top.yukonga.miuix.kmp.basic.Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(TrafficStatisticsMetrics.RecentRequestCardPadding),
            verticalArrangement = Arrangement.spacedBy(TrafficStatisticsMetrics.SectionSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = MLang.TrafficStatistics.Detail.Title,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                )
                Text(
                    text =
                        when (selectedDetailSection) {
                            TrafficDetailSection.RecentRequests ->
                                MLang.TrafficStatistics.RecentRequests.Count.format(
                                    recentRequests.size
                                )
                            TrafficDetailSection.TargetSites ->
                                MLang.TrafficStatistics.TargetSites.Count.format(targetSites.size)
                        },
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.primary,
                )
            }

            DetailSectionSelector(
                selectedSection = selectedDetailSection,
                onSectionSelected = onSectionSelected,
                modifier = selectorModifier,
            )

            AnimatedContent(
                targetState = selectedDetailSection,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                label = "traffic_detail_section",
            ) { detailSection ->
                when (detailSection) {
                    TrafficDetailSection.RecentRequests -> {
                        if (recentRequests.isEmpty()) {
                            Text(
                                text = MLang.TrafficStatistics.RecentRequests.Empty,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        } else {
                            Column(
                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        TrafficStatisticsMetrics.RecentRequestItemSpacing
                                    )
                            ) {
                                recentRequests.forEach { request ->
                                    RecentRequestItem(
                                        record = request,
                                        onClick = { onRecentRequestClick(request) },
                                    )
                                }
                            }
                        }
                    }

                    TrafficDetailSection.TargetSites -> {
                        if (targetSites.isEmpty()) {
                            Text(
                                text = MLang.TrafficStatistics.TargetSites.Empty,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        } else {
                            Column(
                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        TrafficStatisticsMetrics.RecentRequestItemSpacing
                                    )
                            ) {
                                targetSites.forEach { site -> TargetSiteItem(record = site) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSectionSelector(
    selectedSection: TrafficDetailSection,
    onSectionSelected: (TrafficDetailSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TrafficDetailSection.entries.forEach { section ->
            val isSelected = section == selectedSection
            val label =
                when (section) {
                    TrafficDetailSection.RecentRequests ->
                        MLang.TrafficStatistics.RecentRequests.Title
                    TrafficDetailSection.TargetSites -> MLang.TrafficStatistics.TargetSites.Title
                }
            Surface(
                modifier =
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { onSectionSelected(section) },
                        ),
                color =
                    if (isSelected) {
                        MiuixTheme.colorScheme.primary
                    } else {
                        MiuixTheme.colorScheme.surfaceVariant
                    },
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MiuixTheme.textStyles.footnote1,
                        color =
                            if (isSelected) {
                                MiuixTheme.colorScheme.onPrimary
                            } else {
                                MiuixTheme.colorScheme.onSurface
                            },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun TargetSiteItem(record: TargetSiteRecord) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
        color = MiuixTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(TrafficStatisticsMetrics.RecentRequestItemPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = record.displayName,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text =
                            formatConnectionTime(
                                Instant.ofEpochMilli(record.lastSeenAt).toString()
                            ),
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
                Text(
                    text = formatBytes(record.totalBytes),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.primary,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                RequestChip(
                    text =
                        MLang.TrafficStatistics.TargetSites.Upload.format(
                            formatBytes(record.totalUpload)
                        ),
                    color = MiuixTheme.colorScheme.primary,
                )
                RequestChip(
                    text =
                        MLang.TrafficStatistics.TargetSites.Download.format(
                            formatBytes(record.totalDownload)
                        ),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
    }
}

@Composable
private fun TimeRangeSelector(
    selectedRange: StatisticsTimeRange,
    onRangeSelected: (StatisticsTimeRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatisticsTimeRange.entries.forEach { range ->
            val isSelected = range == selectedRange
            Surface(
                modifier =
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .selectable(
                            selected = isSelected,
                            role = Role.Tab,
                            onClick = { onRangeSelected(range) },
                        ),
                color =
                    if (isSelected) {
                        MiuixTheme.colorScheme.primary
                    } else {
                        MiuixTheme.colorScheme.surfaceVariant
                    },
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = range.label,
                        style = MiuixTheme.textStyles.footnote1,
                        color =
                            if (isSelected) {
                                MiuixTheme.colorScheme.onPrimary
                            } else {
                                MiuixTheme.colorScheme.onSurface
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactTrafficSummary(
    selectedTimeRange: StatisticsTimeRange,
    todaySummary: Long,
    weekSummary: Long,
    trafficDifference: Long,
) {
    val displayTotal =
        when (selectedTimeRange) {
            StatisticsTimeRange.TODAY -> todaySummary
            StatisticsTimeRange.WEEK -> weekSummary
        }

    val differenceText =
        when (selectedTimeRange) {
            StatisticsTimeRange.TODAY ->
                when {
                    trafficDifference > 0 ->
                        MLang.TrafficStatistics.Compare.MoreThanYesterday.format(
                            formatBytes(trafficDifference)
                        )
                    trafficDifference < 0 ->
                        MLang.TrafficStatistics.Compare.LessThanYesterday.format(
                            formatBytes(kotlin.math.abs(trafficDifference))
                        )
                    else -> MLang.TrafficStatistics.Compare.SameAsYesterday
                }

            StatisticsTimeRange.WEEK -> MLang.TrafficStatistics.Compare.WeekStats
        }

    Column(
        modifier = Modifier.heightIn(min = TrafficStatisticsMetrics.SummaryBlockHeight),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text =
                when (selectedTimeRange) {
                    StatisticsTimeRange.TODAY -> MLang.TrafficStatistics.Summary.TodayTraffic
                    StatisticsTimeRange.WEEK -> MLang.TrafficStatistics.Summary.WeekTraffic
                },
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Text(
            text = formatBytes(displayTotal),
            style =
                MiuixTheme.textStyles.headline1.copy(
                    fontSize = TrafficStatisticsMetrics.SummaryValueFontSize
                ),
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = differenceText,
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
private fun RecentRequestItem(record: RecentRequestRecord, onClick: () -> Unit) {
    val connection = record.connection
    val displayAddress = remember(connection) { connection.toDisplayAddress() }
    val totalTraffic = connection.upload + connection.download

    Surface(
        modifier =
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).appClickable(onClick = onClick),
        color = MiuixTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(TrafficStatisticsMetrics.RecentRequestItemPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text =
                            displayAddress.title.ifBlank {
                                MLang.TrafficStatistics.RecentRequests.UnknownRequest
                            },
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (record.sourceAppName.isNotBlank()) {
                        Text(
                            text =
                                record.sourcePackageName
                                    ?.takeIf { it.isNotBlank() && it != record.sourceAppName }
                                    ?.let { "${record.sourceAppName} · $it" }
                                    ?: record.sourceAppName,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = formatConnectionTime(connection.start),
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Text(
                        text = formatBytes(totalTraffic),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.primary,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val topLevelGroupName = record.topLevelGroupName
                    val bottomNodeName = record.bottomNodeName
                    RequestChip(
                        text =
                            if (record.isActive) MLang.TrafficStatistics.Status.Active
                            else MLang.TrafficStatistics.Status.Closed,
                        color =
                            if (record.isActive) {
                                MiuixTheme.colorScheme.primary
                            } else {
                                MiuixTheme.colorScheme.onSurfaceVariantSummary
                            },
                    )
                    if (!topLevelGroupName.isNullOrBlank()) {
                        RequestChip(
                            text = topLevelGroupName,
                            color = MiuixTheme.colorScheme.primary,
                        )
                    }
                    if (!bottomNodeName.isNullOrBlank()) {
                        RequestChip(
                            text = bottomNodeName,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestChip(text: String, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = text,
        style = MiuixTheme.textStyles.footnote1,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier =
            Modifier.clip(RoundedCornerShape(TrafficStatisticsMetrics.RecentRequestChipCorner))
                .background(color.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

private fun formatConnectionTime(start: String): String {
    if (start.isBlank()) return "--"
    return runCatching {
            val instant = OffsetDateTime.parse(start).toInstant()
            val now = Instant.now()
            val durationSeconds = java.time.Duration.between(instant, now).seconds
            when {
                durationSeconds < 60 -> MLang.TrafficStatistics.RelativeTime.JustNow
                durationSeconds < 3600 ->
                    MLang.TrafficStatistics.RelativeTime.MinutesAgo.format(durationSeconds / 60)
                durationSeconds < 86400 ->
                    MLang.TrafficStatistics.RelativeTime.HoursAgo.format(durationSeconds / 3600)
                else -> {
                    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
                    localDateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
                }
            }
        }
        .getOrDefault("--")
}

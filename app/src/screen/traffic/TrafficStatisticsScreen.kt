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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.common.util.formatBytes
import com.github.yumelira.yumebox.core.model.ConnectionInfo
import com.github.yumelira.yumebox.data.model.StatisticsTimeRange
import com.github.yumelira.yumebox.feature.meta.presentation.component.ConnectionDetailSheet
import com.github.yumelira.yumebox.feature.meta.presentation.viewmodel.RecentRequestRecord
import com.github.yumelira.yumebox.feature.meta.presentation.viewmodel.TrafficStatisticsViewModel
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.component.TrafficBarChart
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.serialization.json.jsonPrimitive
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private object TrafficStatisticsMetrics {
    val TopPadding = 16.dp
    val BottomPadding = 32.dp
    val CardHorizontalPadding = 16.dp
    val CardSpacing = 14.dp
    val CardInnerPadding = 16.dp
    val SummarySpacing = 14.dp
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

@Destination<RootGraph>
@Composable
fun TrafficStatisticsScreen() {
    val viewModel = koinViewModel<TrafficStatisticsViewModel>()
    val scrollBehavior = MiuixScrollBehavior()

    val todaySummary by viewModel.todaySummary.collectAsState()
    val weekSummary by viewModel.weekSummary.collectAsState()
    val trafficDifference by viewModel.trafficDifference.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
    val chartItems by viewModel.chartItems.collectAsState()
    val selectedBarIndex by viewModel.selectedBarIndex.collectAsState()
    val recentRequests by viewModel.recentRequests.collectAsState()
    var selectedConnection by remember { mutableStateOf<ConnectionInfo?>(null) }
    var showConnectionDetail by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.TrafficStatistics.Title,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = innerPadding,
            topPadding = TrafficStatisticsMetrics.TopPadding,
            bottomPadding = TrafficStatisticsMetrics.BottomPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                top.yukonga.miuix.kmp.basic.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TrafficStatisticsMetrics.CardHorizontalPadding)
                ) {
                    Column(
                        modifier = Modifier.padding(TrafficStatisticsMetrics.CardInnerPadding)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = MLang.TrafficStatistics.OverviewTitle,
                                style = MiuixTheme.textStyles.body1,
                                color = MiuixTheme.colorScheme.onSurface
                            )
                            TimeRangeSelector(
                                selectedRange = selectedTimeRange,
                                onRangeSelected = { viewModel.setTimeRange(it) },
                                modifier = Modifier.widthIn(min = 180.dp, max = 220.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(TrafficStatisticsMetrics.SummarySpacing))

                        CompactTrafficSummary(
                            selectedTimeRange = selectedTimeRange,
                            todaySummary = todaySummary.total,
                            weekSummary = weekSummary,
                            trafficDifference = trafficDifference,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TrafficStatisticsMetrics.ChartHeight)
                        ) {
                            TrafficBarChart(
                                items = chartItems,
                                selectedIndex = selectedBarIndex,
                                onItemClick = { index ->
                                    viewModel.setSelectedBarIndex(
                                        if (selectedBarIndex == index) -1 else index
                                    )
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TrafficStatisticsMetrics.SelectedLabelHeight),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (selectedBarIndex >= 0 && chartItems.isNotEmpty()) {
                                val selectedItem = chartItems.getOrNull(selectedBarIndex)
                                if (selectedItem != null && selectedItem.label.isNotEmpty()) {
                                    Text(
                                        text = "${selectedItem.label}: ${formatBytes(selectedItem.value)}",
                                        style = MiuixTheme.textStyles.footnote1,
                                        color = MiuixTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(TrafficStatisticsMetrics.CardSpacing))
            }

            item {
                top.yukonga.miuix.kmp.basic.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TrafficStatisticsMetrics.CardHorizontalPadding)
                ) {
                    Column(
                        modifier = Modifier.padding(TrafficStatisticsMetrics.RecentRequestCardPadding),
                        verticalArrangement = Arrangement.spacedBy(TrafficStatisticsMetrics.SectionSpacing)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = MLang.TrafficStatistics.RecentRequests.Title,
                                    style = MiuixTheme.textStyles.body1,
                                    color = MiuixTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = MLang.TrafficStatistics.RecentRequests.Summary,
                                    style = MiuixTheme.textStyles.footnote1,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                            Text(
                                text = MLang.TrafficStatistics.RecentRequests.Count.format(recentRequests.size),
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.primary
                            )
                        }

                        if (recentRequests.isEmpty()) {
                            Text(
                                text = MLang.TrafficStatistics.RecentRequests.Empty,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(TrafficStatisticsMetrics.RecentRequestItemSpacing)
                            ) {
                                recentRequests.forEach { request ->
                                    RecentRequestItem(
                                        record = request,
                                        onClick = {
                                            selectedConnection = request.connection
                                            showConnectionDetail = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        ConnectionDetailSheet(
            show = showConnectionDetail,
            connectionInfo = selectedConnection,
            onDismiss = { showConnectionDetail = false },
            onDismissFinished = { selectedConnection = null }
        )
    }
}

@Composable
private fun TimeRangeSelector(
    selectedRange: StatisticsTimeRange,
    onRangeSelected: (StatisticsTimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatisticsTimeRange.entries.forEach { range ->
            val isSelected = range == selectedRange
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onRangeSelected(range) },
                color = if (isSelected) {
                    MiuixTheme.colorScheme.primary
                } else {
                    MiuixTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = range.label,
                        style = MiuixTheme.textStyles.footnote1,
                        color = if (isSelected) {
                            MiuixTheme.colorScheme.onPrimary
                        } else {
                            MiuixTheme.colorScheme.onSurface
                        }
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
    val displayTotal = when (selectedTimeRange) {
        StatisticsTimeRange.TODAY -> todaySummary
        StatisticsTimeRange.WEEK -> weekSummary
    }

    val differenceText = when (selectedTimeRange) {
        StatisticsTimeRange.TODAY -> when {
            trafficDifference > 0 -> MLang.TrafficStatistics.Compare.MoreThanYesterday.format(
                formatBytes(trafficDifference)
            )
            trafficDifference < 0 -> MLang.TrafficStatistics.Compare.LessThanYesterday.format(
                formatBytes(kotlin.math.abs(trafficDifference))
            )
            else -> MLang.TrafficStatistics.Compare.SameAsYesterday
        }

        StatisticsTimeRange.WEEK -> MLang.TrafficStatistics.Compare.WeekStats
    }

    Column(
        modifier = Modifier.heightIn(min = TrafficStatisticsMetrics.SummaryBlockHeight),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = when (selectedTimeRange) {
                StatisticsTimeRange.TODAY -> MLang.TrafficStatistics.Summary.TodayTraffic
                StatisticsTimeRange.WEEK -> MLang.TrafficStatistics.Summary.WeekTraffic
            },
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )
        Text(
            text = formatBytes(displayTotal),
            style = MiuixTheme.textStyles.headline1.copy(
                fontSize = TrafficStatisticsMetrics.SummaryValueFontSize,
            ),
            color = MiuixTheme.colorScheme.onSurface
        )
        Text(
            text = differenceText,
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )
    }
}

@Composable
private fun RecentRequestItem(
    record: RecentRequestRecord,
    onClick: () -> Unit,
) {
    val connection = record.connection
    val host = remember(connection.metadata) {
        connection.metadata["host"]?.jsonPrimitive?.content.orEmpty()
    }
    val process = remember(connection.metadata) {
        connection.metadata["process"]?.jsonPrimitive?.content.orEmpty()
    }
    val destinationPort = remember(connection.metadata) {
        connection.metadata["destinationPort"]?.jsonPrimitive?.content.orEmpty()
    }
    val sourceIp = remember(connection.metadata) {
        connection.metadata["sourceIP"]?.jsonPrimitive?.content.orEmpty()
    }
    val sourcePort = remember(connection.metadata) {
        connection.metadata["sourcePort"]?.jsonPrimitive?.content.orEmpty()
    }
    val displayHost = remember(host, destinationPort, sourceIp, sourcePort) {
        when {
            host.isNotEmpty() && destinationPort.isNotEmpty() -> "$host:$destinationPort"
            host.isNotEmpty() -> host
            sourceIp.isNotEmpty() && sourcePort.isNotEmpty() -> "$sourceIp:$sourcePort"
            else -> MLang.TrafficStatistics.RecentRequests.UnknownRequest
        }
    }
    val totalTraffic = connection.upload + connection.download

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = MiuixTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(TrafficStatisticsMetrics.RecentRequestItemPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = displayHost,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (process.isNotBlank()) {
                        Text(
                            text = process,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatConnectionTime(connection.start),
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Text(
                        text = formatBytes(totalTraffic),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val topLevelGroupName = record.topLevelGroupName
                    val bottomNodeName = record.bottomNodeName
                    RequestChip(
                        text = if (record.isActive) MLang.TrafficStatistics.Status.Active else MLang.TrafficStatistics.Status.Closed,
                        color = if (record.isActive) {
                            MiuixTheme.colorScheme.primary
                        } else {
                            MiuixTheme.colorScheme.onSurfaceVariantSummary
                        }
                    )
                    if (!topLevelGroupName.isNullOrBlank()) {
                        RequestChip(
                            text = topLevelGroupName,
                            color = MiuixTheme.colorScheme.primary
                        )
                    }
                    if (!bottomNodeName.isNullOrBlank()) {
                        RequestChip(
                            text = bottomNodeName,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestChip(
    text: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Text(
        text = text,
        style = MiuixTheme.textStyles.footnote1,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(TrafficStatisticsMetrics.RecentRequestChipCorner))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
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
            durationSeconds < 3600 -> MLang.TrafficStatistics.RelativeTime.MinutesAgo.format(durationSeconds / 60)
            durationSeconds < 86400 -> MLang.TrafficStatistics.RelativeTime.HoursAgo.format(durationSeconds / 3600)
            else -> {
                val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
                localDateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
            }
        }
    }.getOrDefault("--")
}

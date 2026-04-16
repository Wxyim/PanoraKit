/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import com.github.nomadboxlab.monadbox.common.AppConstants
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.repository.IpMonitoringState
import com.github.nomadboxlab.monadbox.domain.model.TrafficData
import com.github.nomadboxlab.monadbox.presentation.component.LocalNavigator
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.component.combinePaddingValues
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.HomePagerLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomePager(
    mainInnerPadding: PaddingValues,
    trafficNow: TrafficData,
    runtimeVisualState: HomeRuntimeVisualState,
    displayRunning: Boolean,
    isToggling: Boolean,
    profilesLoaded: Boolean,
    hasProfiles: Boolean,
    hasEnabledProfile: Boolean,
    recommendedProfile: com.github.nomadboxlab.monadbox.service.runtime.entity.Profile?,
    currentProfileName: String?,
    currentTunnelMode: TunnelState.Mode,
    selectedServer: HomeSelectedServerState?,
    ipMonitoringState: IpMonitoringState,
    speedHistory: SpeedHistoryBuffer,
    proxyMode: ProxyMode,
    uiError: String?,
    uiMessage: String?,
    onConsumeError: () -> Unit = {},
    onConsumeMessage: () -> Unit = {},
    onProxyToggleRequest:
        (
            isRunning: Boolean,
            recommendedProfile: com.github.nomadboxlab.monadbox.service.runtime.entity.Profile?,
            proxyMode: com.github.nomadboxlab.monadbox.data.model.ProxyMode,
        ) -> Unit =
        { _, _, _ ->
        },
    onModeSwitchRequest: () -> Unit = {},
    onModeBadgeBoundsChanged: (Rect) -> Unit = {},
) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(uiError) {
        uiError?.let {
            context.toast(it, Toast.LENGTH_LONG)
            onConsumeError()
        }
    }

    androidx.compose.runtime.LaunchedEffect(uiMessage) { uiMessage?.let { onConsumeMessage() } }

    val scrollBehavior = MiuixScrollBehavior()

    val canStartProxy =
        profilesLoaded && hasEnabledProfile && recommendedProfile != null && !isToggling
    val canToggleProxy =
        profilesLoaded && hasProfiles && !isToggling && (displayRunning || canStartProxy)
    val hasTrafficHistory = speedHistory.size > 0

    val handleProxyToggle = {
        if (!hasEnabledProfile || recommendedProfile == null) {
            context.toast(MLang.ProfilesVM.Error.ProfileNotExist)
        } else {
            onProxyToggleRequest(displayRunning, recommendedProfile, proxyMode)
        }
    }

    Scaffold(topBar = { TopBar(title = MLang.Home.Title, scrollBehavior = scrollBehavior) }) {
        innerPadding ->
        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = combinePaddingValues(innerPadding, mainInnerPadding),
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    BoxWithConstraints(
                        modifier =
                            Modifier.fillMaxWidth()
                                .widthIn(max = HomePagerLayoutDefaults.ContentMaxWidth)
                                .padding(horizontal = AppConstants.UI.DEFAULT_HORIZONTAL_PADDING)
                    ) {
                        val availableAdaptiveInfo =
                            rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                        val contentScale =
                            (maxWidth / HomePagerLayoutDefaults.ContentScaleReferenceWidth)
                                .coerceIn(
                                    HomePagerLayoutDefaults.ContentScaleMin,
                                    HomePagerLayoutDefaults.ContentScaleMax,
                                )
                        val sectionSpacing =
                            if (availableAdaptiveInfo.prefersTwoPaneContent) {
                                HomePagerLayoutDefaults.WideSectionSpacing
                            } else {
                                (AppConstants.UI.DEFAULT_VERTICAL_SPACING * contentScale).coerceIn(
                                    HomePagerLayoutDefaults.CompactSectionSpacingMin,
                                    AppConstants.UI.DEFAULT_VERTICAL_SPACING,
                                )
                            }
                        val infoSpacing =
                            if (availableAdaptiveInfo.prefersTwoPaneContent) {
                                HomePagerLayoutDefaults.InfoSpacingWide
                            } else {
                                (HomePagerLayoutDefaults.InfoSpacingBase * contentScale).coerceIn(
                                    HomePagerLayoutDefaults.InfoSpacingMin,
                                    HomePagerLayoutDefaults.InfoSpacingBase,
                                )
                            }
                        val chartHeight =
                            when {
                                displayRunning ->
                                    (AppConstants.UI.SPEED_CHART_HEIGHT * contentScale).coerceIn(
                                        HomePagerLayoutDefaults.RunningChartHeightMin,
                                        AppConstants.UI.SPEED_CHART_HEIGHT,
                                    )
                                hasTrafficHistory ->
                                    (AppConstants.UI.SPEED_CHART_HEIGHT * 0.82f).coerceIn(
                                        HomePagerLayoutDefaults.HistoryChartHeightMin,
                                        HomePagerLayoutDefaults.HistoryChartHeightMax,
                                    )
                                else -> HomePagerLayoutDefaults.IdleChartHeight
                            }
                        val useWideLayout = availableAdaptiveInfo.prefersTwoPaneContent
                        val visibleTraffic = if (displayRunning) trafficNow else TrafficData.ZERO

                        if (useWideLayout) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(sectionSpacing),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1.04f),
                                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                                    ) {
                                        TrafficDisplay(
                                            trafficNow = visibleTraffic,
                                            profileName = currentProfileName,
                                            tunnelMode = currentTunnelMode,
                                            runtimeVisualState = runtimeVisualState,
                                            canStartProxy = canStartProxy,
                                            isRunning = displayRunning,
                                            proxyMode = proxyMode,
                                            onStatusCapsuleClick =
                                                handleProxyToggle.takeIf { canToggleProxy },
                                            onModeBadgeClick = onModeSwitchRequest,
                                            onModeBadgeBoundsChanged = onModeBadgeBoundsChanged,
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(0.96f),
                                        verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(infoSpacing)
                                        ) {
                                            NodeInfoDisplay(
                                                selectedServer = selectedServer,
                                                tunnelMode = currentTunnelMode,
                                            )
                                            IpInfoDisplay(state = ipMonitoringState)
                                        }

                                        HomeTrafficChartSection(
                                            speedHistory = speedHistory,
                                            isRunning = displayRunning,
                                            chartHeight = chartHeight,
                                            onClick = {
                                                navigator.navigate(
                                                    TrafficStatisticsScreenDestination
                                                ) {
                                                    launchSingleTop = true
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                            ) {
                                TrafficDisplay(
                                    trafficNow = visibleTraffic,
                                    profileName = currentProfileName,
                                    tunnelMode = currentTunnelMode,
                                    runtimeVisualState = runtimeVisualState,
                                    canStartProxy = canStartProxy,
                                    isRunning = displayRunning,
                                    proxyMode = proxyMode,
                                    onStatusCapsuleClick =
                                        handleProxyToggle.takeIf { canToggleProxy },
                                    onModeBadgeClick = onModeSwitchRequest,
                                    onModeBadgeBoundsChanged = onModeBadgeBoundsChanged,
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(sectionSpacing)) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(infoSpacing)
                                    ) {
                                        NodeInfoDisplay(
                                            selectedServer = selectedServer,
                                            tunnelMode = currentTunnelMode,
                                        )
                                        IpInfoDisplay(state = ipMonitoringState)
                                    }

                                    HomeTrafficChartSection(
                                        speedHistory = speedHistory,
                                        isRunning = displayRunning,
                                        chartHeight = chartHeight,
                                        onClick = {
                                            navigator.navigate(TrafficStatisticsScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(AppTheme.spacing.xxxl)) }
        }
    }
}

@Composable
private fun HomeTrafficChartSection(
    speedHistory: SpeedHistoryBuffer,
    isRunning: Boolean,
    chartHeight: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HomePagerLayoutDefaults.ChartSectionSpacing)
    ) {
        if (!isRunning) {
            Text(
                text = MLang.TrafficStatistics.Title,
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }

        SpeedChart(
            speedHistory = speedHistory,
            isRunning = isRunning,
            chartHeight = chartHeight,
            onClick = onClick,
        )
    }
}

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



package com.github.yumelira.yumebox.screen.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.common.AppConstants
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.TunnelState
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.repository.IpMonitoringState
import com.github.yumelira.yumebox.domain.model.TrafficData
import com.github.yumelira.yumebox.presentation.component.LocalNavigator
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.component.combinePaddingValues
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold

@Composable
fun HomePager(
    mainInnerPadding: PaddingValues,
    trafficNow: TrafficData,
    displayRunning: Boolean,
    isToggling: Boolean,
    profilesLoaded: Boolean,
    hasProfiles: Boolean,
    hasEnabledProfile: Boolean,
    recommendedProfile: com.github.yumelira.yumebox.service.runtime.entity.Profile?,
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
    onProxyToggleRequest: (isRunning: Boolean, recommendedProfile: com.github.yumelira.yumebox.service.runtime.entity.Profile?, proxyMode: com.github.yumelira.yumebox.data.model.ProxyMode) -> Unit = { _, _, _ -> },
    onModeSwitchRequest: () -> Unit = {},
    onModeBadgeBoundsChanged: (Rect) -> Unit = {},
) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    androidx.compose.runtime.LaunchedEffect(uiError) {
        uiError?.let {
            context.toast(it, Toast.LENGTH_LONG)
            onConsumeError()
        }
    }

    androidx.compose.runtime.LaunchedEffect(uiMessage) {
        uiMessage?.let {
            onConsumeMessage()
        }
    }

    val scrollBehavior = MiuixScrollBehavior()

    val isProxyEnabled = profilesLoaded && hasProfiles && !isToggling
    val toggleInteractionSource = remember { MutableInteractionSource() }
    val modeInteractionSource = remember { MutableInteractionSource() }
    val statsInteractionSource = remember { MutableInteractionSource() }
    val gestureSurfaceHeight = (
        configuration.screenHeightDp.dp -
            mainInnerPadding.calculateTopPadding() -
            mainInnerPadding.calculateBottomPadding()
        ).coerceAtLeast(360.dp)
    val upperHalfHeight = gestureSurfaceHeight * 0.5f

    val handleProxyToggle = {
        if (!hasEnabledProfile || recommendedProfile == null) {
            context.toast(MLang.ProfilesVM.Error.ProfileNotExist)
        } else {
            onProxyToggleRequest(displayRunning, recommendedProfile, proxyMode)
        }
    }

    Scaffold(
        topBar = { TopBar(title = MLang.Home.Title, scrollBehavior = scrollBehavior) },
    ) { innerPadding ->
        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = combinePaddingValues(innerPadding, mainInnerPadding),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = gestureSurfaceHeight)
                        .padding(horizontal = AppConstants.UI.DEFAULT_HORIZONTAL_PADDING)
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val contentScale = (maxWidth / 390.dp).coerceIn(0.9f, 1f)
                        val sectionSpacing = (AppConstants.UI.DEFAULT_VERTICAL_SPACING * contentScale)
                            .coerceIn(18.dp, AppConstants.UI.DEFAULT_VERTICAL_SPACING)
                        val infoSpacing = (16.dp * contentScale).coerceIn(12.dp, 16.dp)
                        val chartHeight = (AppConstants.UI.SPEED_CHART_HEIGHT * contentScale)
                            .coerceIn(112.dp, AppConstants.UI.SPEED_CHART_HEIGHT)

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
                        ) {

                            TrafficDisplay(
                                trafficNow = if (displayRunning) {
                                    trafficNow
                                } else {
                                    TrafficData.ZERO
                                },
                                profileName = currentProfileName,
                                tunnelMode = currentTunnelMode,
                                isRunning = displayRunning,
                                proxyMode = proxyMode,
                                onModeBadgeBoundsChanged = onModeBadgeBoundsChanged,
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(sectionSpacing)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(infoSpacing)) {
                                    NodeInfoDisplay(
                                        selectedServer = selectedServer,
                                        tunnelMode = currentTunnelMode,
                                    )
                                    IpInfoDisplay(state = ipMonitoringState)
                                }

                                SpeedChart(
                                    speedHistory = speedHistory,
                                    isRunning = displayRunning,
                                    chartHeight = chartHeight,
                                    onClick = {
                                        navigator.navigate(TrafficStatisticsScreenDestination) {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(upperHalfHeight)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        enabled = isProxyEnabled,
                                        interactionSource = toggleInteractionSource,
                                        indication = null,
                                        onClick = handleProxyToggle,
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = modeInteractionSource,
                                        indication = null,
                                        onClick = onModeSwitchRequest,
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .height(gestureSurfaceHeight - upperHalfHeight)
                                .clickable(
                                    interactionSource = statsInteractionSource,
                                    indication = null,
                                    onClick = {
                                        navigator.navigate(TrafficStatisticsScreenDestination) {
                                            launchSingleTop = true
                                        }
                                    },
                                )
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

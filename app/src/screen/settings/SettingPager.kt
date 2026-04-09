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

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.BuildConfig
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.*
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AppSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MetaFeatureScreenDestination
import com.ramcosta.composedestinations.generated.destinations.NetworkSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object SettingsPageMetrics {
    val EntryIconSlotSize = 24.dp
    val EntryIconContainerSize = 36.dp
    val EntryIconContainerCornerRadius = 16.dp
    val EntryIconSize = 22.dp
    val EntryIconOuterStartPadding = 4.dp
    val EntryIconOuterEndPadding = 16.dp
    val VersionBadgeHeight = 22.dp
    val VersionBadgeEndPadding = 12.dp
    val VersionBadgeHorizontalPadding = 12.dp
    val VersionBadgeFontSize = 12.sp
}

@Composable
private fun CircularIcon(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: Float = 1f,
) {
    Box(
        modifier =
            modifier
                .padding(
                    start = SettingsPageMetrics.EntryIconOuterStartPadding,
                    end = SettingsPageMetrics.EntryIconOuterEndPadding,
                )
                .requiredSize(SettingsPageMetrics.EntryIconSlotSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier.layout { measurable, _ ->
                        val containerSize = SettingsPageMetrics.EntryIconContainerSize.roundToPx()
                        val parentSize = SettingsPageMetrics.EntryIconSlotSize.roundToPx()
                        val offset = (containerSize - parentSize) / 2

                        val placeable =
                            measurable.measure(
                                androidx.compose.ui.unit.Constraints.fixed(
                                    containerSize,
                                    containerSize,
                                )
                            )
                        layout(parentSize, parentSize) { placeable.place(-offset, -offset) }
                    }
                    .size(SettingsPageMetrics.EntryIconContainerSize)
                    .clip(RoundedCornerShape(SettingsPageMetrics.EntryIconContainerCornerRadius))
                    .background(MiuixTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = MiuixTheme.colorScheme.onPrimary,
                modifier =
                    Modifier.size(SettingsPageMetrics.EntryIconSize)
                        .graphicsLayer(
                            scaleX = iconSize,
                            scaleY = iconSize,
                            transformOrigin = TransformOrigin.Center,
                        ),
            )
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun SettingPager(mainInnerPadding: PaddingValues) {
    val scrollBehavior = MiuixScrollBehavior()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    val versionInfo = remember { BuildConfig.VERSION_NAME }

    Scaffold(topBar = { TopBar(title = MLang.Settings.Title, scrollBehavior = scrollBehavior) }) {
        innerPadding ->
        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = combinePaddingValues(innerPadding, mainInnerPadding),
        ) {
            item {
                SmallTitle(MLang.Settings.Section.UiSettings)
                Card {
                    SuperArrow(
                        title = MLang.Settings.UiSettings.App,
                        summary = MLang.Settings.UiSettings.AppSummary,
                        onClick = {
                            navigator.navigate(AppSettingsScreenDestination) {
                                launchSingleTop = true
                            }
                        },
                        startAction = {
                            CircularIcon(imageVector = Yume.`Settings-2`, contentDescription = null)
                        },
                    )
                    SuperArrow(
                        title = MLang.Settings.UiSettings.Network,
                        summary = MLang.Settings.UiSettings.NetworkSummary,
                        onClick = {
                            navigator.navigate(NetworkSettingsScreenDestination) {
                                launchSingleTop = true
                            }
                        },
                        startAction = {
                            CircularIcon(imageVector = Yume.`Wifi-cog`, contentDescription = null)
                        },
                    )
                    SuperArrow(
                        title = MLang.Settings.UiSettings.Override,
                        summary = MLang.Settings.UiSettings.OverrideSummary,
                        onClick = {
                            navigator.navigate(OverrideScreenDestination) { launchSingleTop = true }
                        },
                        startAction = {
                            CircularIcon(imageVector = Yume.`Git-merge`, contentDescription = null)
                        },
                    )
                    SuperArrow(
                        title = MLang.Settings.UiSettings.MetaFeatures,
                        summary = MLang.Settings.UiSettings.MetaFeaturesSummary,
                        onClick = {
                            navigator.navigate(MetaFeatureScreenDestination) {
                                launchSingleTop = true
                            }
                        },
                        startAction = {
                            CircularIcon(imageVector = Yume.Meta, contentDescription = null)
                        },
                    )
                }
            }
            item {
                SmallTitle(MLang.Settings.Section.More)

                Card {
                    SuperArrow(
                        title = MLang.Settings.More.TrafficStatistics,
                        summary = MLang.Settings.More.TrafficStatisticsSummary,
                        onClick = {
                            navigator.navigate(TrafficStatisticsScreenDestination) {
                                launchSingleTop = true
                            }
                        },
                        startAction = {
                            CircularIcon(
                                imageVector = Yume.`Chart-column`,
                                contentDescription = null,
                            )
                        },
                    )
                    SuperArrow(
                        title = MLang.Settings.More.Logs,
                        summary = MLang.Settings.More.LogsSummary,
                        onClick = {
                            navigator.navigate(LogScreenDestination) { launchSingleTop = true }
                        },
                        startAction = {
                            CircularIcon(imageVector = Yume.Activity, contentDescription = null)
                        },
                    )
                    SuperArrow(
                        title = MLang.Settings.More.About,
                        summary = MLang.Settings.More.AboutSummary,
                        onClick = {
                            navigator.navigate(AboutScreenDestination) { launchSingleTop = true }
                        },
                        startAction = {
                            CircularIcon(imageVector = Yume.Github, contentDescription = null)
                        },
                        endActions = { VersionBadge(versionInfo) },
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionBadge(versionInfo: String?) {
    Surface(
        color = MiuixTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        modifier =
            Modifier.height(SettingsPageMetrics.VersionBadgeHeight)
                .padding(end = SettingsPageMetrics.VersionBadgeEndPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(horizontal = SettingsPageMetrics.VersionBadgeHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = versionInfo ?: "Unknown",
                style =
                    MiuixTheme.textStyles.footnote1.copy(
                        fontSize = SettingsPageMetrics.VersionBadgeFontSize,
                        fontWeight = FontWeight.Bold,
                    ),
                color = MiuixTheme.colorScheme.primary,
            )
        }
    }
}

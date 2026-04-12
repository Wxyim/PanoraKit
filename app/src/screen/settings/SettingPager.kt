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
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.BuildConfig
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.*
import com.github.yumelira.yumebox.presentation.theme.LocalSpacing
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AppSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MetaFeatureScreenDestination
import com.ramcosta.composedestinations.generated.destinations.NetworkSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object SettingsPageMetrics {
    val SinglePaneMaxWidth = 760.dp
    val TwoPaneMaxWidth = 1280.dp
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun SettingPager(mainInnerPadding: PaddingValues) {
    val scrollBehavior = MiuixScrollBehavior()
    val navigator = LocalNavigator.current

    val versionInfo = remember { BuildConfig.VERSION_NAME }

    Scaffold(topBar = { TopBar(title = MLang.Settings.Title, scrollBehavior = scrollBehavior) }) {
        innerPadding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
            val useTwoPaneLayout = availableAdaptiveInfo.prefersTwoPaneContent
            val sectionSpacing = LocalSpacing.current.xl
            val contentMaxWidth =
                when {
                    availableAdaptiveInfo.isExpandedWidth -> SettingsPageMetrics.TwoPaneMaxWidth
                    availableAdaptiveInfo.isMediumWidth -> SettingsPageMetrics.SinglePaneMaxWidth
                    else -> Dp.Unspecified
                }

            ScreenLazyColumn(
                scrollBehavior = scrollBehavior,
                innerPadding = combinePaddingValues(innerPadding, mainInnerPadding),
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        val contentModifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = LocalSpacing.current.gutter)
                                .let { modifier ->
                                    if (contentMaxWidth != Dp.Unspecified) {
                                        modifier.widthIn(max = contentMaxWidth)
                                    } else {
                                        modifier
                                    }
                                }

                        if (useTwoPaneLayout) {
                            Row(
                                modifier = contentModifier,
                                horizontalArrangement = Arrangement.spacedBy(sectionSpacing),
                                verticalAlignment = Alignment.Top,
                            ) {
                                SettingsSection(
                                    title = MLang.Settings.Section.UiSettings,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    UiSettingsCard(
                                        navigator = navigator,
                                        modifier = Modifier.fillMaxWidth(),
                                        applyHorizontalPadding = false,
                                    )
                                }
                                SettingsSection(
                                    title = MLang.Settings.Section.More,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    MoreSettingsCard(
                                        navigator = navigator,
                                        versionInfo = versionInfo,
                                        modifier = Modifier.fillMaxWidth(),
                                        applyHorizontalPadding = false,
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = contentModifier,
                                verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                            ) {
                                SettingsSection(title = MLang.Settings.Section.UiSettings) {
                                    UiSettingsCard(
                                        navigator = navigator,
                                        modifier = Modifier.fillMaxWidth(),
                                        applyHorizontalPadding = false,
                                    )
                                }

                                SettingsSection(title = MLang.Settings.Section.More) {
                                    MoreSettingsCard(
                                        navigator = navigator,
                                        versionInfo = versionInfo,
                                        modifier = Modifier.fillMaxWidth(),
                                        applyHorizontalPadding = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.md),
    ) {
        SmallTitle(title)
        content()
    }
}

@Composable
private fun UiSettingsCard(
    navigator: com.ramcosta.composedestinations.navigation.DestinationsNavigator,
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean = true,
) {
    Card(modifier = modifier, applyHorizontalPadding = applyHorizontalPadding) {
        SettingsRow(
            title = MLang.Settings.UiSettings.App,
            summary = MLang.Settings.UiSettings.AppSummary,
            imageVector = Yume.`Settings-2`,
            tone = SemanticTone.Neutral,
            onClick = {
                navigator.navigate(AppSettingsScreenDestination) { launchSingleTop = true }
            },
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.UiSettings.Network,
            summary = MLang.Settings.UiSettings.NetworkSummary,
            imageVector = Yume.`Wifi-cog`,
            tone = SemanticTone.Info,
            onClick = {
                navigator.navigate(NetworkSettingsScreenDestination) { launchSingleTop = true }
            },
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.UiSettings.Override,
            summary = MLang.Settings.UiSettings.OverrideSummary,
            imageVector = Yume.`Git-merge`,
            tone = SemanticTone.Warning,
            onClick = { navigator.navigate(OverrideScreenDestination) { launchSingleTop = true } },
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.UiSettings.MetaFeatures,
            summary = MLang.Settings.UiSettings.MetaFeaturesSummary,
            imageVector = Yume.Meta,
            tone = SemanticTone.Brand,
            onClick = {
                navigator.navigate(MetaFeatureScreenDestination) { launchSingleTop = true }
            },
        )
    }
}

@Composable
private fun MoreSettingsCard(
    navigator: com.ramcosta.composedestinations.navigation.DestinationsNavigator,
    versionInfo: String?,
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean = true,
) {
    Card(modifier = modifier, applyHorizontalPadding = applyHorizontalPadding) {
        SettingsRow(
            title = MLang.Settings.More.TrafficStatistics,
            summary = MLang.Settings.More.TrafficStatisticsSummary,
            imageVector = Yume.`Chart-column`,
            tone = SemanticTone.Info,
            onClick = {
                navigator.navigate(TrafficStatisticsScreenDestination) { launchSingleTop = true }
            },
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.More.Logs,
            summary = MLang.Settings.More.LogsSummary,
            imageVector = Yume.Activity,
            tone = SemanticTone.Neutral,
            onClick = { navigator.navigate(LogScreenDestination) { launchSingleTop = true } },
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.More.About,
            summary = MLang.Settings.More.AboutSummary,
            imageVector = Yume.Github,
            tone = SemanticTone.Brand,
            onClick = { navigator.navigate(AboutScreenDestination) { launchSingleTop = true } },
            endContent = { VersionBadge(versionInfo) },
        )
    }
}

@Composable
private fun VersionBadge(versionInfo: String?) {
    StatusBadge(text = versionInfo ?: "Unknown", tone = SemanticTone.Brand, compact = true)
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 70.dp, end = 18.dp),
        thickness = 0.5.dp,
        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.16f),
    )
}

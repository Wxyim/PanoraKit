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

package com.github.nomadboxlab.monadbox.feature.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.domain.app.AppInfo
import com.github.nomadboxlab.monadbox.presentation.component.Card
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import com.github.nomadboxlab.monadbox.presentation.component.SettingsRow
import com.github.nomadboxlab.monadbox.presentation.component.SmallTitle
import com.github.nomadboxlab.monadbox.presentation.component.StatusBadge
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.component.combinePaddingValues
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Activity
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Chart-column`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Git-merge`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Github
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Meta
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Scan-eye`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Settings-2`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.UserKey
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Wifi-cog`
import com.github.nomadboxlab.monadbox.presentation.theme.LocalSpacing
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import dev.oom_wg.purejoy.mlang.MLang
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Navigation callbacks consumed by [SettingPagerBody]. The app module's thin shell supplies lambdas
 * that dispatch to the compose-destinations generated `*Destination` objects (which can only be
 * referenced from :app because KSP emits them into the app module's generated source set).
 */
class SettingPagerNavigation(
    val onNavigateAppSettings: () -> Unit,
    val onNavigateNetworkSettings: () -> Unit,
    val onNavigateOverride: () -> Unit,
    val onNavigateMetaFeature: () -> Unit,
    val onNavigateAccessControl: () -> Unit,
    val onNavigateTrafficStatistics: () -> Unit,
    val onNavigateLog: () -> Unit,
    val onNavigateAbout: () -> Unit,
)

@SuppressLint("LocalContextResourcesRead")
@Composable
fun SettingPagerBody(mainInnerPadding: PaddingValues, navigation: SettingPagerNavigation) {
    val scrollBehavior = MiuixScrollBehavior()

    val appInfo = koinInject<AppInfo>()
    val versionInfo = remember(appInfo) { appInfo.versionName }

    Scaffold(topBar = { TopBar(title = MLang.Settings.Title, scrollBehavior = scrollBehavior) }) {
        innerPadding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
            val useTwoPaneLayout = availableAdaptiveInfo.prefersTwoPaneContent
            val sectionSpacing = LocalSpacing.current.xl
            val contentMaxWidth =
                if (useTwoPaneLayout) {
                    availableAdaptiveInfo.preferredTwoPaneMaxWidth
                } else {
                    availableAdaptiveInfo.preferredSinglePaneMaxWidth
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
                            Modifier.adaptiveContentWidth(contentMaxWidth)
                                .padding(horizontal = LocalSpacing.current.gutter)

                        if (useTwoPaneLayout) {
                            Row(
                                modifier = contentModifier,
                                horizontalArrangement = Arrangement.spacedBy(sectionSpacing),
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(sectionSpacing),
                                ) {
                                    SettingsSection(title = MLang.Settings.Section.UiSettings) {
                                        UiSettingsCard(
                                            navigation = navigation,
                                            modifier = Modifier.fillMaxWidth(),
                                            applyHorizontalPadding = false,
                                        )
                                    }
                                    SettingsSection(title = MLang.Settings.Section.Advanced) {
                                        AdvancedSettingsCard(
                                            navigation = navigation,
                                            modifier = Modifier.fillMaxWidth(),
                                            applyHorizontalPadding = false,
                                        )
                                    }
                                }
                                SettingsSection(
                                    title = MLang.Settings.Section.More,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    MoreSettingsCard(
                                        navigation = navigation,
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
                                        navigation = navigation,
                                        modifier = Modifier.fillMaxWidth(),
                                        applyHorizontalPadding = false,
                                    )
                                }

                                SettingsSection(title = MLang.Settings.Section.Advanced) {
                                    AdvancedSettingsCard(
                                        navigation = navigation,
                                        modifier = Modifier.fillMaxWidth(),
                                        applyHorizontalPadding = false,
                                    )
                                }

                                SettingsSection(title = MLang.Settings.Section.More) {
                                    MoreSettingsCard(
                                        navigation = navigation,
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
    navigation: SettingPagerNavigation,
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean = true,
) {
    Card(modifier = modifier, applyHorizontalPadding = applyHorizontalPadding) {
        SettingsRow(
            title = MLang.Settings.UiSettings.App,
            summary = MLang.Settings.UiSettings.AppSummary,
            imageVector = MonadIcons.`Settings-2`,
            tone = SemanticTone.Neutral,
            onClick = navigation.onNavigateAppSettings,
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.UiSettings.Network,
            summary = MLang.Settings.UiSettings.NetworkSummary,
            imageVector = MonadIcons.`Wifi-cog`,
            tone = SemanticTone.Info,
            onClick = navigation.onNavigateNetworkSettings,
        )
    }
}

@Composable
private fun AdvancedSettingsCard(
    navigation: SettingPagerNavigation,
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean = true,
) {
    Card(modifier = modifier, applyHorizontalPadding = applyHorizontalPadding) {
        SettingsRow(
            title = MLang.Settings.UiSettings.Override,
            summary = MLang.Settings.UiSettings.OverrideSummary,
            imageVector = MonadIcons.`Git-merge`,
            tone = SemanticTone.Warning,
            onClick = navigation.onNavigateOverride,
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.UiSettings.MetaFeatures,
            summary = MLang.Settings.UiSettings.MetaFeaturesSummary,
            imageVector = MonadIcons.Meta,
            tone = SemanticTone.Brand,
            onClick = navigation.onNavigateMetaFeature,
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.NetworkSettings.ProxyOptions.ManageAccessControlTitle,
            summary = MLang.NetworkSettings.ProxyOptions.ManageAccessControlSummary,
            imageVector = MonadIcons.UserKey,
            tone = SemanticTone.Info,
            onClick = navigation.onNavigateAccessControl,
        )
    }
}

@Composable
private fun MoreSettingsCard(
    navigation: SettingPagerNavigation,
    versionInfo: String?,
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean = true,
) {
    Card(modifier = modifier, applyHorizontalPadding = applyHorizontalPadding) {
        SettingsRow(
            title = MLang.Settings.More.TrafficStatistics,
            summary = MLang.Settings.More.TrafficStatisticsSummary,
            imageVector = MonadIcons.`Chart-column`,
            tone = SemanticTone.Info,
            onClick = navigation.onNavigateTrafficStatistics,
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.More.Logs,
            summary = MLang.Settings.More.LogsSummary,
            imageVector = MonadIcons.Activity,
            tone = SemanticTone.Neutral,
            onClick = navigation.onNavigateLog,
        )
        SettingsDivider()
        SettingsRow(
            title = MLang.Settings.More.About,
            summary = MLang.Settings.More.AboutSummary,
            imageVector = MonadIcons.Github,
            tone = SemanticTone.Brand,
            onClick = navigation.onNavigateAbout,
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

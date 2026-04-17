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

package com.github.nomadboxlab.monadbox.screen.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.github.nomadboxlab.monadbox.feature.settings.SettingPagerBody
import com.github.nomadboxlab.monadbox.feature.settings.SettingPagerNavigation
import com.github.nomadboxlab.monadbox.presentation.component.LocalNavigator
import com.ramcosta.composedestinations.generated.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AccessControlScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AppSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ConnectionScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.MetaFeatureScreenDestination
import com.ramcosta.composedestinations.generated.destinations.NetworkSettingsScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination

@Composable
fun SettingPager(mainInnerPadding: PaddingValues) {
    val navigator = LocalNavigator.current
    SettingPagerBody(
        mainInnerPadding = mainInnerPadding,
        navigation =
            SettingPagerNavigation(
                onNavigateAppSettings = {
                    navigator.navigate(AppSettingsScreenDestination) { launchSingleTop = true }
                },
                onNavigateNetworkSettings = {
                    navigator.navigate(NetworkSettingsScreenDestination) { launchSingleTop = true }
                },
                onNavigateOverride = {
                    navigator.navigate(OverrideScreenDestination) { launchSingleTop = true }
                },
                onNavigateMetaFeature = {
                    navigator.navigate(MetaFeatureScreenDestination) { launchSingleTop = true }
                },
                onNavigateAccessControl = {
                    navigator.navigate(AccessControlScreenDestination) { launchSingleTop = true }
                },
                onNavigateTrafficStatistics = {
                    navigator.navigate(TrafficStatisticsScreenDestination) {
                        launchSingleTop = true
                    }
                },
                onNavigateConnection = {
                    navigator.navigate(ConnectionScreenDestination) { launchSingleTop = true }
                },
                onNavigateLog = {
                    navigator.navigate(LogScreenDestination) { launchSingleTop = true }
                },
                onNavigateAbout = {
                    navigator.navigate(AboutScreenDestination) { launchSingleTop = true }
                },
            ),
    )
}

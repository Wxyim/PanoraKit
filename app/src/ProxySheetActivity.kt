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

package com.github.yumelira.yumebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.presentation.theme.ProvideAndroidPlatformTheme
import com.github.yumelira.yumebox.presentation.theme.YumeTheme
import com.github.yumelira.yumebox.presentation.theme.rememberAdaptiveSpacing
import com.github.yumelira.yumebox.screen.settings.AppSettingsViewModel
import org.koin.androidx.compose.koinViewModel

class ProxySheetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(true)
        @Suppress("DEPRECATION") overridePendingTransition(0, 0)

        setContent {
            val appSettingsViewModel = koinViewModel<AppSettingsViewModel>()
            val themeMode = appSettingsViewModel.themeMode.state.collectAsStateWithLifecycle().value
            val themeSeedColorArgb =
                appSettingsViewModel.themeSeedColorArgb.state.collectAsStateWithLifecycle().value
            val pageScale = appSettingsViewModel.pageScale.state.collectAsStateWithLifecycle().value

            ProvideAndroidPlatformTheme {
                val systemDensity = LocalDensity.current
                val scaledDensity =
                    Density(
                        density = systemDensity.density,
                        fontScale = systemDensity.fontScale * pageScale,
                    )
                CompositionLocalProvider(LocalDensity provides scaledDensity) {
                    val adaptiveSpacing = rememberAdaptiveSpacing(pageScale = pageScale)
                    YumeTheme(themeMode = themeMode, themeSeedColorArgb = themeSeedColorArgb, spacing = adaptiveSpacing) {
                        ProxySheetContent(
                            onDismiss = {
                                finish()
                                @Suppress("DEPRECATION") overridePendingTransition(0, 0)
                            }
                        )
                    }
                }
            }
        }
    }
}

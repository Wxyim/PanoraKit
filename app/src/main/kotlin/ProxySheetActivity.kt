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

package com.github.nomadboxlab.monadbox

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.feature.settings.AppSettingsViewModel
import com.github.nomadboxlab.monadbox.presentation.theme.MonadTheme
import com.github.nomadboxlab.monadbox.presentation.theme.ProvideAndroidPlatformTheme
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAdaptiveSpacing
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import org.koin.androidx.compose.koinViewModel

class ProxySheetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
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
                    remember(systemDensity, pageScale) {
                        Density(
                            density = systemDensity.density,
                            fontScale = systemDensity.fontScale * pageScale,
                        )
                    }
                CompositionLocalProvider(LocalDensity provides scaledDensity) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val windowAdaptiveInfo =
                            rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                        val adaptiveSpacing =
                            rememberAdaptiveSpacing(
                                windowAdaptiveInfo = windowAdaptiveInfo,
                                pageScale = pageScale,
                            )
                        MonadTheme(
                            themeMode = themeMode,
                            themeSeedColorArgb = themeSeedColorArgb,
                            spacing = adaptiveSpacing,
                            windowAdaptiveInfo = windowAdaptiveInfo,
                        ) {
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
}

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

package com.github.nomadboxlab.monadbox.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.github.nomadboxlab.monadbox.data.model.ThemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

internal val LocalPlatformSystemUiEffect = compositionLocalOf<@Composable () -> Unit> { {} }

@Composable
fun MonadTheme(
    themeMode: ThemeMode? = null,
    themeSeedColorArgb: Long = DEFAULT_THEME_SEED_ARGB,
    spacing: Spacing = DefaultSpacing,
    radii: Radii = DefaultRadii,
    strokes: Strokes = DefaultStrokes,
    elevations: Elevations = DefaultElevations,
    typography: AppTypography = AppTypography(),
    windowAdaptiveInfo: WindowAdaptiveInfo = WindowAdaptiveInfo(),
    pageMetrics: PageMetrics? = null,
    content: @Composable () -> Unit,
) {
    LocalPlatformSystemUiEffect.current()
    val effectivePageMetrics = pageMetrics ?: rememberAdaptivePageMetrics(windowAdaptiveInfo)
    val effectiveThemeMode = themeMode ?: ThemeMode.Auto
    val isDark =
        when (effectiveThemeMode) {
            ThemeMode.Auto -> isSystemInDarkTheme()
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        }
    val colors =
        remember(isDark, themeSeedColorArgb) {
            colorSchemeFromSeed(colorFromArgb(themeSeedColorArgb), isDark)
        }
    val semanticColors =
        remember(isDark, colors) {
            appSemanticColors(
                isDark = isDark,
                primary = colors.primary,
                error = colors.error,
                errorContainer = colors.errorContainer,
                surfaceVariant = colors.surfaceVariant,
                onSurface = colors.onSurface,
            )
        }

    CompositionLocalProvider(
        LocalSpacing provides spacing,
        LocalRadii provides radii,
        LocalStrokes provides strokes,
        LocalElevations provides elevations,
        LocalPageMetrics provides effectivePageMetrics,
        LocalAppTypography provides typography,
        LocalSemanticColors provides semanticColors,
        LocalWindowAdaptiveInfo provides windowAdaptiveInfo,
    ) {
        MiuixTheme(colors = colors) { content() }
    }
}

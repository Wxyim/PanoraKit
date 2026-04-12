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

package com.github.yumelira.yumebox.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Returns an adaptive [Spacing] instance tuned for the current window size class:
 * - **Compact**: narrow phone or split-window layouts → `screenH = 12 dp`
 * - **Medium**: landscape phones / foldables / narrow tablets → `screenH = 24 dp`
 * - **Expanded**: wide tablets or desktop-like windows → constrain content toward a ~680 dp column.
 */
@Composable
fun rememberAdaptiveSpacing(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    pageScale: Float = 1f,
): Spacing {
    val normalizedScale = pageScale.coerceIn(0.8f, 1.2f)
    return remember(
        windowAdaptiveInfo.widthSizeClass,
        windowAdaptiveInfo.windowWidth,
        normalizedScale,
    ) {
        val baseSpacing =
            when (windowAdaptiveInfo.widthSizeClass) {
                androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> {
                    val effectiveWidth =
                        if (windowAdaptiveInfo.windowWidth > 0.dp) {
                            windowAdaptiveInfo.windowWidth
                        } else {
                            840.dp
                        }
                    val inset = ((effectiveWidth - 680.dp) / 2f).coerceAtLeast(32.dp)
                    Spacing(screenH = inset, gutter = 24.dp)
                }
                androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium ->
                    Spacing(screenH = 24.dp, gutter = 20.dp)
                else -> Spacing()
            }
        baseSpacing.scaleBy(normalizedScale)
    }
}

data class Spacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val gutter: Dp = 16.dp,
    val screenH: Dp = 12.dp,
    val screenV: Dp = 12.dp,
)

data class Radii(
    val none: Dp = 0.dp,
    val sm: Dp = 4.dp,
    val md: Dp = 8.dp,
    val lg: Dp = 12.dp,
    val xl: Dp = 16.dp,
    val xxl: Dp = 24.dp,
    val pill: Dp = 999.dp,
)

data class Strokes(
    val thin: Dp = 0.35.dp,
    val default: Dp = 0.8.dp,
    val medium: Dp = 1.dp,
    val thick: Dp = 1.5.dp,
)

data class Elevations(
    val none: Dp = 0.dp,
    val low: Dp = 1.dp,
    val medium: Dp = 3.dp,
    val high: Dp = 6.dp,
    val overlay: Dp = 12.dp,
    val navigationBar: Dp = 22.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalRadii = staticCompositionLocalOf { Radii() }
val LocalStrokes = staticCompositionLocalOf { Strokes() }
val LocalElevations = staticCompositionLocalOf { Elevations() }

object AppTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current

    val radii: Radii
        @Composable get() = LocalRadii.current

    val strokes: Strokes
        @Composable get() = LocalStrokes.current

    val elevations: Elevations
        @Composable get() = LocalElevations.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current
}

private fun Spacing.scaleBy(scale: Float): Spacing {
    if (scale == 1f) return this
    return copy(
        xxs = xxs * scale,
        xs = xs * scale,
        sm = sm * scale,
        md = md * scale,
        lg = lg * scale,
        xl = xl * scale,
        xxl = xxl * scale,
        xxxl = xxxl * scale,
        gutter = gutter * scale,
        screenH = screenH * scale,
        screenV = screenV * scale,
    )
}

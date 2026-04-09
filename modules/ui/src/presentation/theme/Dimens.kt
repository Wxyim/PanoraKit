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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Returns an adaptive [Spacing] instance tuned for the current window width:
 *
 * - **Compact  (< 600 dp)**: phones in portrait or 16:9–21:9 tall phones → `screenH = 12 dp`
 * - **Medium   (600–840 dp)**: large-phone landscape / small tablet portrait → `screenH = 24 dp`
 * - **Expanded (> 840 dp)**: tablets / foldables → `screenH` computed as
 *   `max(32, (screenWidth − 680) / 2)` so content stays within a ~680 dp column.
 */
@Composable
fun rememberAdaptiveSpacing(pageScale: Float = 1f): Spacing {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val normalizedScale = pageScale.coerceIn(0.8f, 1.2f)
    return remember(screenWidthDp, normalizedScale) {
        val baseSpacing =
            when {
                screenWidthDp >= 840 -> {
                    val inset = max(32, (screenWidthDp - 680) / 2)
                    Spacing(screenH = inset.dp, gutter = 24.dp)
                }
                screenWidthDp >= 600 -> Spacing(screenH = 24.dp, gutter = 20.dp)
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
    val pill: Dp = 999.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
val LocalRadii = staticCompositionLocalOf { Radii() }

object AppTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current

    val radii: Radii
        @Composable get() = LocalRadii.current
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

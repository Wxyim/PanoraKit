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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.theme

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class WindowAdaptiveInfo(
    val widthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    val heightSizeClass: WindowHeightSizeClass = WindowHeightSizeClass.Medium,
    val windowWidth: Dp = 0.dp,
    val windowHeight: Dp = 0.dp,
) {
    val isCompactWidth: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Compact

    val isMediumWidth: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Medium

    val isExpandedWidth: Boolean
        get() = widthSizeClass == WindowWidthSizeClass.Expanded

    val useRailNavigation: Boolean
        get() = !isCompactWidth

    val useVerticalPageSwitch: Boolean
        get() = useRailNavigation

    val prefersDockedPrimaryAction: Boolean
        get() = !isCompactWidth

    /**
     * Whether the available width supports comfortable two-pane content (two equal columns).
     *
     * Requires at least 1080dp so each column gets ≥520dp of usable width. This prevents cramped
     * two-column layouts on phones in landscape (~840-1000dp) where settings-style content with
     * titles, summaries, and controls would truncate or wrap excessively.
     */
    val prefersTwoPaneContent: Boolean
        get() = windowWidth >= 1080.dp

    val preferredBottomSheetMaxWidth: Dp
        get() =
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> 640.dp
                WindowWidthSizeClass.Medium -> 720.dp
                WindowWidthSizeClass.Expanded -> 840.dp
                else -> 720.dp
            }

    /**
     * Preferred max width for single-pane scrolling content (settings, logs, detail pages).
     *
     * Returns [Dp.Unspecified] for compact widths where content should fill available space.
     */
    val preferredSinglePaneMaxWidth: Dp
        get() =
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> Dp.Unspecified
                WindowWidthSizeClass.Medium -> 720.dp
                WindowWidthSizeClass.Expanded -> 840.dp
                else -> Dp.Unspecified
            }

    /**
     * Preferred max width for two-pane content containers (settings hub, dashboards).
     *
     * Returns [Dp.Unspecified] for compact widths where content should fill available space.
     */
    val preferredTwoPaneMaxWidth: Dp
        get() =
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> Dp.Unspecified
                WindowWidthSizeClass.Medium -> 760.dp
                WindowWidthSizeClass.Expanded -> 1280.dp
                else -> Dp.Unspecified
            }
}

val LocalWindowAdaptiveInfo = staticCompositionLocalOf { WindowAdaptiveInfo() }

@Composable
fun rememberAvailableWindowAdaptiveInfo(maxWidth: Dp, maxHeight: Dp = 0.dp): WindowAdaptiveInfo {
    return remember(maxWidth, maxHeight) {
        WindowAdaptiveInfo(
            widthSizeClass = calculateAdaptiveWidthSizeClass(maxWidth),
            heightSizeClass = calculateAdaptiveHeightSizeClass(maxHeight),
            windowWidth = maxWidth,
            windowHeight = maxHeight,
        )
    }
}

fun calculateAdaptiveWidthSizeClass(width: Dp): WindowWidthSizeClass {
    return when {
        width < 600.dp -> WindowWidthSizeClass.Compact
        width < 840.dp -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
}

fun calculateAdaptiveHeightSizeClass(height: Dp): WindowHeightSizeClass {
    return when {
        height <= 0.dp -> WindowHeightSizeClass.Medium
        height < 480.dp -> WindowHeightSizeClass.Compact
        height < 900.dp -> WindowHeightSizeClass.Medium
        else -> WindowHeightSizeClass.Expanded
    }
}

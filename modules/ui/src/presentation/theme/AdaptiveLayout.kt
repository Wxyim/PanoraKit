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

    val prefersTwoPaneContent: Boolean
        get() = isExpandedWidth

    val preferredBottomSheetMaxWidth: Dp
        get() =
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> 640.dp
                WindowWidthSizeClass.Medium -> 720.dp
                WindowWidthSizeClass.Expanded -> 840.dp
                else -> 720.dp
            }
}

val LocalWindowAdaptiveInfo = staticCompositionLocalOf { WindowAdaptiveInfo() }

@Composable
fun rememberAvailableWindowAdaptiveInfo(
    maxWidth: Dp,
    maxHeight: Dp = 0.dp,
): WindowAdaptiveInfo {
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
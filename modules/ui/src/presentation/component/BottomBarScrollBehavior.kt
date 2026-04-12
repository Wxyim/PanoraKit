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

package com.github.yumelira.yumebox.presentation.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Stable
class BottomBarScrollBehavior(private val scrollThresholdPx: Float) {
    var isBottomBarVisible by mutableStateOf(true)
        private set

    var isAutoHideEnabled by mutableStateOf(true)

    private var lastToggleTime = 0L
    private val toggleDelay = 150L

    private var accumulatedScroll = 0f

    val nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!isAutoHideEnabled) return Offset.Zero
                if (source != NestedScrollSource.UserInput) return Offset.Zero

                val delta = consumed.y
                if (kotlin.math.abs(delta) < 0.5f) return Offset.Zero

                if ((accumulatedScroll > 0 && delta < 0) || (accumulatedScroll < 0 && delta > 0)) {
                    accumulatedScroll = 0f
                }

                accumulatedScroll += delta

                if (kotlin.math.abs(accumulatedScroll) >= scrollThresholdPx) {
                    if (accumulatedScroll < 0) hideBottomBar() else showBottomBar()
                    accumulatedScroll = 0f
                }
                return Offset.Zero
            }
        }

    fun showBottomBar(force: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        if (!isBottomBarVisible && (force || currentTime - lastToggleTime >= toggleDelay)) {
            isBottomBarVisible = true
            lastToggleTime = currentTime
        }
    }

    fun hideBottomBar(force: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        if (isBottomBarVisible && (force || currentTime - lastToggleTime >= toggleDelay)) {
            isBottomBarVisible = false
            lastToggleTime = currentTime
        }
    }
}

@Composable
fun rememberBottomBarScrollBehavior(autoHideEnabled: Boolean = true): BottomBarScrollBehavior {
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { 24.dp.toPx() }
    return remember(autoHideEnabled, scrollThresholdPx) {
        BottomBarScrollBehavior(scrollThresholdPx = scrollThresholdPx).apply {
            isAutoHideEnabled = autoHideEnabled
        }
    }
}

@Composable
fun BottomBarScrollBehavior.withLazyListState(listState: LazyListState): BottomBarScrollBehavior {
    val isAtTop by remember {
        derivedStateOf {
            !listState.canScrollBackward ||
                (listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset <= 1)
        }
    }

    LaunchedEffect(isAtTop, isAutoHideEnabled) {
        if (isAtTop || !isAutoHideEnabled) {
            showBottomBar(force = true)
        }
    }

    val isScrollingUp by remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
                val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.first()
                firstVisibleItem.index == 0 && firstVisibleItem.offset == 0
            } else false
        }
    }

    LaunchedEffect(isScrollingUp, isAutoHideEnabled) {
        if (isScrollingUp || !isAutoHideEnabled) {
            showBottomBar(force = true)
        }
    }

    return this
}

val LocalBottomBarScrollBehavior = compositionLocalOf<BottomBarScrollBehavior?> { null }

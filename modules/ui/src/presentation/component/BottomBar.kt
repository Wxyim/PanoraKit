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

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Arrow-down-up`
import com.github.yumelira.yumebox.presentation.icon.yume.Bolt
import com.github.yumelira.yumebox.presentation.icon.yume.House
import com.github.yumelira.yumebox.presentation.icon.yume.`Package-check`
import com.github.yumelira.yumebox.presentation.theme.AnimationSpecs
import com.kyant.shapes.Capsule
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid
import kotlin.math.max
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme

val LocalPagerState = compositionLocalOf<PagerState> { error("LocalPagerState is not provided") }
val LocalHandlePageChange =
    compositionLocalOf<(Int) -> Unit> { error("LocalHandlePageChange is not provided") }
val LocalNavigator =
    compositionLocalOf<DestinationsNavigator> { error("LocalNavigator is not provided") }
val LocalBottomBarLiquidState = compositionLocalOf<LiquidState?> { null }

@Composable
fun BottomBarContent(isVisible: Boolean = true) {
    val navigationState = rememberPrimaryNavigationState()
    val bottomBarScrollBehavior = LocalBottomBarScrollBehavior.current
    val liquidState = LocalBottomBarLiquidState.current
    val bottomBarVisible = isVisible && (bottomBarScrollBehavior?.isBottomBarVisible ?: true)
    val isLightTheme = !isSystemInDarkTheme()
    val animatedScale by
        animateFloatAsState(
            targetValue = if (bottomBarVisible) 1f else AnimationSpecs.Proxy.VisibilityTargetScale,
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.Proxy.VisibilityDuration,
                    easing = AnimationSpecs.EmphasizedDecelerate,
                ),
            label = "bottom_bar_scale",
        )
    val animatedAlpha by
        animateFloatAsState(
            targetValue = if (bottomBarVisible) 1f else 0f,
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.Proxy.VisibilityFadeDuration,
                    easing = AnimationSpecs.EmphasizedDecelerate,
                ),
            label = "bottom_bar_alpha",
        )
    val density = LocalDensity.current
    val bottomSafeInset =
        with(density) {
            val navBottom = WindowInsets.navigationBars.getBottom(this)
            val gestureBottom = WindowInsets.systemGestures.getBottom(this)
            max(navBottom, gestureBottom).toDp()
        }

    val selectedColor = MiuixTheme.colorScheme.primary
    val unselectedColor = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.72f)
    val containerColor =
        if (isLightTheme) {
            MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
        } else {
            MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.86f)
        }
    val indicatorContainerColor = selectedColor.copy(alpha = 0.16f)
    val containerBorderColor =
        if (isLightTheme) {
            selectedColor.copy(alpha = 0.10f)
        } else {
            selectedColor.copy(alpha = 0.18f)
        }
    val innerHighlightColor =
        if (isLightTheme) {
            White.copy(alpha = 0.62f)
        } else {
            White.copy(alpha = 0.14f)
        }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val horizontalInset = (maxWidth * 0.10f).coerceIn(18.dp, 40.dp)

        BottomNavigationBar(
            selectedIndex = navigationState.page,
            tabsCount = BottomBarDestination.entries.size,
            liquidState = liquidState,
            containerColor = containerColor,
            indicatorContainerColor = indicatorContainerColor,
            containerBorderColor = containerBorderColor,
            innerHighlightColor = innerHighlightColor,
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        start = horizontalInset,
                        end = horizontalInset,
                        top = 8.dp,
                        bottom = bottomSafeInset + 14.dp,
                    )
                    .graphicsLayer {
                        alpha = animatedAlpha
                        scaleX = animatedScale
                        scaleY = animatedScale
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    },
        ) {
            BottomBarDestination.entries.forEachIndexed { index, destination ->
                val selected = navigationState.page == index
                val itemColor: Color =
                    if (selected) selectedColor else unselectedColor
                BottomNavigationTabItem(
                    selected = selected,
                    enabled = bottomBarVisible,
                    onClick = { navigationState.onItemClick(index) },
                ) {
                    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = null,
                            tint = itemColor,
                        )
                    }
                    BasicText(
                        destination.label(),
                        style =
                            TextStyle(
                                color = itemColor,
                                fontSize = 11.sp,
                                fontWeight =
                                    if (navigationState.page == index) FontWeight.SemiBold
                                    else FontWeight.Medium,
                            ),
                    )
                }
            }
        }
    }
}

enum class BottomBarDestination(val icon: ImageVector) {
    Home(Yume.House),
    Proxy(Yume.`Arrow-down-up`),
    Config(Yume.`Package-check`),
    Setting(Yume.Bolt);

    @Composable
    fun label(): String =
        when (this) {
            Home -> MLang.Component.BottomBar.Home
            Proxy -> MLang.Component.BottomBar.Proxy
            Config -> MLang.Component.BottomBar.Config
            Setting -> MLang.Component.BottomBar.Setting
        }
}

@Composable
fun SideRailContent(modifier: Modifier = Modifier) {
    val navigationState = rememberPrimaryNavigationState()
    val isLightTheme = !isSystemInDarkTheme()
    val selectedColor = MiuixTheme.colorScheme.primary
    val unselectedColor = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val containerColor =
        if (isLightTheme) {
            MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)
        } else {
            MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f)
        }
    val indicatorContainerColor = selectedColor.copy(alpha = 0.14f)
    val borderColor =
        if (isLightTheme) {
            selectedColor.copy(alpha = 0.08f)
        } else {
            selectedColor.copy(alpha = 0.16f)
        }

    Column(
        modifier =
            modifier
                .selectableGroup()
                .shadow(
                    elevation = 22.dp,
                    shape = Capsule(),
                    ambientColor =
                        if (isLightTheme) {
                            Black.copy(alpha = 0.14f)
                        } else {
                            Black.copy(alpha = 0.34f)
                        },
                    spotColor =
                        if (isLightTheme) {
                            Black.copy(alpha = 0.10f)
                        } else {
                            Black.copy(alpha = 0.24f)
                        },
                )
                .width(92.dp)
                .clip(Capsule())
                .background(containerColor, Capsule())
                .border(width = 0.8.dp, color = borderColor, shape = Capsule())
                .padding(horizontal = 10.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BottomBarDestination.entries.forEachIndexed { index, destination ->
            val selected = navigationState.page == index
            val itemColor = if (selected) selectedColor else unselectedColor

            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .semantics(mergeDescendants = true) { this.selected = selected }
                        .clip(Capsule())
                        .background(
                            color = if (selected) indicatorContainerColor else Color.Transparent,
                            shape = Capsule(),
                        )
                        .clickable(
                            role = Role.Tab,
                            onClick = { navigationState.onItemClick(index) },
                        )
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = null,
                    tint = itemColor,
                    modifier = Modifier.size(22.dp),
                )
                BasicText(
                    destination.label(),
                    style =
                        TextStyle(
                            color = itemColor,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        ),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedIndex: Int,
    tabsCount: Int,
    liquidState: LiquidState?,
    containerColor: Color,
    indicatorContainerColor: Color,
    containerBorderColor: Color,
    innerHighlightColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val isLightTheme = !isSystemInDarkTheme()
    val density = LocalDensity.current
    val safeSelectedIndex = selectedIndex.coerceIn(0, tabsCount - 1)

    val contentInset = 4.dp
    val contentInsetPx = with(density) { (contentInset * 2).toPx() }

    var indicatorIndex by remember { mutableIntStateOf(safeSelectedIndex) }
    val indicatorPosition = remember { Animatable(safeSelectedIndex.toFloat()) }
    val indicatorScale = remember { Animatable(1f) }

    LaunchedEffect(safeSelectedIndex) {
        if (indicatorIndex == safeSelectedIndex) return@LaunchedEffect
        launch {
            indicatorPosition.animateTo(
                targetValue = safeSelectedIndex.toFloat(),
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
            )
        }
        launch {
            indicatorScale.animateTo(0.88f, tween(90, easing = FastOutSlowInEasing))
            indicatorScale.animateTo(1f, tween(170, easing = FastOutSlowInEasing))
        }
        indicatorIndex = safeSelectedIndex
    }

    BoxWithConstraints(
        modifier =
            modifier
                .shadow(
                    elevation = 24.dp,
                    shape = Capsule(),
                    ambientColor =
                        if (isLightTheme) {
                            Black.copy(alpha = 0.18f)
                        } else {
                            Black.copy(alpha = 0.38f)
                        },
                    spotColor =
                        if (isLightTheme) {
                            Black.copy(alpha = 0.14f)
                        } else {
                            Black.copy(alpha = 0.28f)
                        },
                )
                .height(60.dp)
                .clip(Capsule())
                .then(
                    if (liquidState != null) {
                        Modifier.liquid(liquidState) {
                                frost = 3.dp
                                refraction = 0.5f
                                curve = 0.6f
                                edge = 0.4f
                                dispersion = 0f
                                saturation = 1.2f
                                contrast = 1.1f
                            }
                            .background(
                                if (isLightTheme) White.copy(alpha = 0.9f)
                                else Black.copy(alpha = 0.88f),
                                Capsule(),
                            )
                    } else {
                        Modifier.background(containerColor, Capsule())
                    }
                ),
        contentAlignment = Alignment.CenterStart,
    ) {
        val tabWidth = (constraints.maxWidth.toFloat() - contentInsetPx) / tabsCount

        Box(
            modifier = Modifier.matchParentSize().padding(contentInset),
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                Modifier.graphicsLayer {
                        translationX = indicatorPosition.value * tabWidth
                        scaleX = indicatorScale.value
                        scaleY = indicatorScale.value
                    }
                    .height(52.dp)
                    .fillMaxWidth(1f / tabsCount)
                    .background(indicatorContainerColor, Capsule())
            )

            Row(
                Modifier.height(52.dp).fillMaxWidth().selectableGroup(),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }

        Box(
            Modifier.matchParentSize()
                .border(
                    width = 0.8.dp,
                    color = containerBorderColor,
                    shape = Capsule(),
                )
        )

        Box(
            Modifier.matchParentSize()
                .padding(1.dp)
                .border(
                    width = 0.35.dp,
                    color = innerHighlightColor,
                    shape = Capsule(),
                )
        )
    }
}

@Composable
private fun RowScope.BottomNavigationTabItem(
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .semantics(mergeDescendants = true) { this.selected = selected }
            .clip(Capsule())
            .clickable(
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            .fillMaxHeight()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

private data class PrimaryNavigationState(val page: Int, val onItemClick: (Int) -> Unit)

@Composable
private fun rememberPrimaryNavigationState(): PrimaryNavigationState {
    val pagerState = LocalPagerState.current
    val handlePageChange = LocalHandlePageChange.current
    val page by
        remember(pagerState) {
            derivedStateOf {
                if (pagerState.isScrollInProgress) pagerState.targetPage else pagerState.currentPage
            }
        }
    var lastPageClickAt by remember { mutableLongStateOf(0L) }

    val onItemClick: (Int) -> Unit = onItemClick@{ index ->
        val now = SystemClock.elapsedRealtime()
        if (now - lastPageClickAt < 180L) return@onItemClick
        lastPageClickAt = now

        if (pagerState.isScrollInProgress) return@onItemClick
        if (index == pagerState.currentPage && !pagerState.isScrollInProgress) return@onItemClick
        handlePageChange(index)
    }

    return PrimaryNavigationState(page = page, onItemClick = onItemClick)
}

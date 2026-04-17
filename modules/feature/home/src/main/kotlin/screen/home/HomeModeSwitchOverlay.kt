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

package com.github.nomadboxlab.monadbox.feature.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import com.github.nomadboxlab.monadbox.presentation.theme.HomeModeSwitchOverlayLayoutDefaults
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeModeSwitchOverlay(
    visible: Boolean,
    currentMode: TunnelState.Mode,
    anchorBounds: Rect?,
    onDismiss: () -> Unit,
    onSelectMode: (TunnelState.Mode) -> Unit,
) {
    val modeItems =
        listOf(
            TunnelState.Mode.Rule to MLang.Home.Profile.Rule,
            TunnelState.Mode.Direct to MLang.Home.Profile.Direct,
            TunnelState.Mode.Global to MLang.Home.Profile.Global,
        )

    val visibilityState = remember { MutableTransitionState(false) }
    LaunchedEffect(visible) { visibilityState.targetState = visible }
    if (!visibilityState.currentState && !visibilityState.targetState) return

    val transition =
        rememberTransition(transitionState = visibilityState, label = "home_mode_overlay")
    val overlayAlpha =
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 180 else 220, easing = FastOutSlowInEasing)
            },
            label = "home_mode_overlay_alpha",
        ) { shown ->
            if (shown) 0.12f else 0f
        }
    val panelAlpha =
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 140 else 100, easing = FastOutSlowInEasing)
            },
            label = "home_mode_panel_alpha",
        ) { shown ->
            if (shown) 1f else 0f
        }
    val panelScale =
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 180 else 140, easing = FastOutSlowInEasing)
            },
            label = "home_mode_panel_scale",
        ) { shown ->
            if (shown) 1f else 0.92f
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha.value))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        onDismiss()
                    }
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val horizontalMargin = HomeModeSwitchOverlayLayoutDefaults.HorizontalMargin
            val anchorWidth = anchorBounds?.let { bounds -> with(density) { bounds.width.toDp() } }
            val anchorHeight =
                anchorBounds?.let { bounds -> with(density) { bounds.height.toDp() } }
            val panelWidth =
                ((anchorWidth ?: HomeModeSwitchOverlayLayoutDefaults.FallbackAnchorWidth) *
                        HomeModeSwitchOverlayLayoutDefaults.PanelWidthFactor)
                    .coerceIn(
                        HomeModeSwitchOverlayLayoutDefaults.PanelMinWidth,
                        HomeModeSwitchOverlayLayoutDefaults.PanelMaxWidth,
                    )
            val panelSpacing =
                ((anchorHeight ?: HomeModeSwitchOverlayLayoutDefaults.FallbackAnchorHeight) *
                        HomeModeSwitchOverlayLayoutDefaults.PanelSpacingFactor)
                    .coerceIn(
                        HomeModeSwitchOverlayLayoutDefaults.PanelSpacingMin,
                        HomeModeSwitchOverlayLayoutDefaults.PanelSpacingMax,
                    )
            val panelX =
                anchorBounds?.let { bounds ->
                    with(density) {
                        val anchored = bounds.right.toDp() - panelWidth
                        anchored.coerceIn(
                            horizontalMargin,
                            maxWidth - panelWidth - horizontalMargin,
                        )
                    }
                } ?: horizontalMargin
            val panelY =
                anchorBounds?.let { bounds ->
                    with(density) { bounds.bottom.toDp() + panelSpacing }
                } ?: HomeModeSwitchOverlayLayoutDefaults.PanelFallbackTop

            Surface(
                modifier =
                    Modifier.absoluteOffset(x = panelX, y = panelY)
                        .graphicsLayer {
                            alpha = panelAlpha.value
                            scaleX = panelScale.value
                            scaleY = panelScale.value
                            transformOrigin = TransformOrigin(1f, 0f)
                        }
                        .widthIn(min = panelWidth, max = panelWidth)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        ),
                shape = RoundedCornerShape(HomeModeSwitchOverlayLayoutDefaults.PanelCornerRadius),
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier =
                        Modifier.selectableGroup()
                            .padding(
                                vertical = HomeModeSwitchOverlayLayoutDefaults.PanelVerticalPadding
                            )
                ) {
                    modeItems.forEach { (mode, label) ->
                        HomeModeMenuItem(
                            text = label,
                            selected = mode == currentMode,
                            onClick = {
                                if (mode != currentMode) onSelectMode(mode)
                                onDismiss()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeModeMenuItem(text: String, selected: Boolean, onClick: () -> Unit) {
    val primary = MiuixTheme.colorScheme.primary
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val containerColor by
        animateColorAsState(
            targetValue =
                when {
                    selected && isPressed -> primary.copy(alpha = 0.14f)
                    selected -> primary.copy(alpha = 0.10f)
                    isPressed -> primary.copy(alpha = 0.05f)
                    else -> Color.Transparent
                },
            animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
            label = "ModeMenuItemContainerColor",
        )
    val itemScale by
        animateFloatAsState(
            targetValue = if (isPressed) 0.985f else 1f,
            animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
            label = "ModeMenuItemScale",
        )

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(HomeModeSwitchOverlayLayoutDefaults.ItemCornerRadius),
        modifier =
            Modifier.padding(
                    horizontal = HomeModeSwitchOverlayLayoutDefaults.ItemOuterHorizontalPadding,
                    vertical = HomeModeSwitchOverlayLayoutDefaults.ItemOuterVerticalPadding,
                )
                .scale(itemScale),
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(min = HomeModeSwitchOverlayLayoutDefaults.ItemMinHeight)
                    .selectable(
                        selected = selected,
                        interactionSource = interactionSource,
                        role = Role.RadioButton,
                        onClick = onClick,
                    )
                    .padding(
                        horizontal = HomeModeSwitchOverlayLayoutDefaults.ItemInnerHorizontalPadding,
                        vertical = HomeModeSwitchOverlayLayoutDefaults.ItemInnerVerticalPadding,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style =
                    MiuixTheme.textStyles.body2.copy(
                        fontSize = HomeModeSwitchOverlayLayoutDefaults.ItemTextSize,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    ),
                color = if (selected) primary else MiuixTheme.colorScheme.onSurface,
            )
            if (selected) {
                Icon(
                    imageVector = MonadIcons.Check,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(HomeModeSwitchOverlayLayoutDefaults.ItemIconSize),
                )
            }
        }
    }
}

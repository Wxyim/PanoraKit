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

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.theme.AnimationSpecs
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object OverrideFabLayoutDefaults {
    val CornerRadius = 26.dp
    val ShadowElevation = 14.dp
    val ContentHorizontalPadding = 14.dp
    val ContentVerticalPadding = 11.dp
    val ExtendedMinWidth = 176.dp
    val ExtendedMaxWidth = 232.dp
    val ExtendedMinHeight = 58.dp
    val IconContainerSize = 38.dp
    val IconSpacing = 10.dp
}

@Stable
class OverrideFabController internal constructor() {
    var isHiddenByScroll by mutableStateOf(false)
        private set

    fun onScrollDirectionChanged(hidden: Boolean) {
        isHiddenByScroll = hidden
    }
}

@Composable
fun rememberOverrideFabController(): OverrideFabController {
    return remember { OverrideFabController() }
}

@Composable
fun OverrideAnimatedFab(
    controller: OverrideFabController,
    visible: Boolean,
    imageVector: ImageVector,
    contentDescription: String,
    extraBottomPadding: Dp = 0.dp,
    label: String? = null,
    supportingText: String? = null,
    onClick: () -> Unit,
) {
    val fabVisibilityState = remember { MutableTransitionState(false) }
    val hiddenByScroll = controller.isHiddenByScroll
    val actualVisible = visible && !hiddenByScroll
    val bottomBarOverlayPadding = LocalBottomBarOverlayPadding.current
    val spacing = AppTheme.spacing
    val strokes = AppTheme.strokes
    fabVisibilityState.targetState = actualVisible

    AnimatedVisibility(
        visibleState = fabVisibilityState,
        enter =
            slideInVertically(
                animationSpec =
                    tween(
                        durationMillis = AnimationSpecs.Proxy.VisibilityDuration,
                        easing = AnimationSpecs.EmphasizedDecelerate,
                    ),
                initialOffsetY = { it / 2 },
            ) +
                scaleIn(
                    initialScale = AnimationSpecs.Proxy.VisibilityInitialScale,
                    animationSpec =
                        tween(
                            durationMillis = AnimationSpecs.Proxy.VisibilityDuration,
                            easing = LinearEasing,
                        ),
                ) +
                fadeIn(
                    animationSpec =
                        tween(
                            durationMillis = AnimationSpecs.Proxy.VisibilityFadeDuration,
                            easing = AnimationSpecs.EnterEasing,
                        )
                ),
        exit =
            slideOutVertically(
                animationSpec =
                    tween(
                        durationMillis = AnimationSpecs.Proxy.VisibilityDuration,
                        easing = AnimationSpecs.EmphasizedAccelerate,
                    ),
                targetOffsetY = { it / 2 },
            ) +
                scaleOut(
                    targetScale = AnimationSpecs.Proxy.VisibilityTargetScale,
                    animationSpec =
                        tween(
                            durationMillis = AnimationSpecs.Proxy.VisibilityDuration,
                            easing = LinearEasing,
                        ),
                ) +
                fadeOut(
                    animationSpec =
                        tween(
                            durationMillis = AnimationSpecs.Proxy.VisibilityFadeDuration,
                            easing = AnimationSpecs.ExitEasing,
                        )
                ),
        label = "override_shared_fab_visibility",
    ) {
        val fabModifier =
            Modifier.navigationBarsPadding()
                .padding(
                    end = spacing.xl,
                    bottom = spacing.lg + bottomBarOverlayPadding + extraBottomPadding,
                )

        if (label.isNullOrBlank()) {
            FloatingActionButton(modifier = fabModifier, onClick = onClick) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    tint = MiuixTheme.colorScheme.onPrimary,
                )
            }
        } else {
            val colorScheme = MiuixTheme.colorScheme
            val isLightTheme = !isSystemInDarkTheme()
            val shape = RoundedCornerShape(OverrideFabLayoutDefaults.CornerRadius)
            val containerColor =
                colorScheme.surfaceVariant.copy(alpha = if (isLightTheme) 0.96f else 0.9f)
            val borderColor = colorScheme.primary.copy(alpha = if (isLightTheme) 0.12f else 0.22f)
            val labelColor = colorScheme.onSurface
            val supportingColor = colorScheme.onSurfaceVariantSummary.copy(alpha = 0.82f)

            Box(
                modifier =
                    fabModifier
                        .shadow(
                            elevation = OverrideFabLayoutDefaults.ShadowElevation,
                            shape = shape,
                            ambientColor = Color.Black.copy(alpha = 0.16f),
                            spotColor = Color.Black.copy(alpha = 0.12f),
                        )
                        .clip(shape)
                        .background(containerColor, shape)
                        .border(width = strokes.default, color = borderColor, shape = shape)
                        .appClickable(onClick = onClick)
                        .padding(
                            horizontal = OverrideFabLayoutDefaults.ContentHorizontalPadding,
                            vertical = OverrideFabLayoutDefaults.ContentVerticalPadding,
                        )
                        .widthIn(
                            min = OverrideFabLayoutDefaults.ExtendedMinWidth,
                            max = OverrideFabLayoutDefaults.ExtendedMaxWidth,
                        )
                        .heightIn(min = OverrideFabLayoutDefaults.ExtendedMinHeight)
            ) {
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(OverrideFabLayoutDefaults.IconSpacing),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier.size(OverrideFabLayoutDefaults.IconContainerSize)
                                .clip(CircleShape)
                                .background(colorScheme.primary.copy(alpha = 0.13f), CircleShape),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = contentDescription,
                            tint = colorScheme.primary,
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                        Text(
                            text = label,
                            color = labelColor,
                            fontWeight = FontWeight.SemiBold,
                            style = MiuixTheme.textStyles.body1,
                        )

                        if (!supportingText.isNullOrBlank()) {
                            Text(
                                text = supportingText,
                                color = supportingColor,
                                style = MiuixTheme.textStyles.footnote1,
                            )
                        }
                    }
                }
            }
        }
    }
}

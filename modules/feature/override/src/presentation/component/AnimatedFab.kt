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

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.presentation.theme.AnimationSpecs
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
                .padding(end = 20.dp, bottom = 16.dp + extraBottomPadding)

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
            val shape = RoundedCornerShape(26.dp)
            val containerColor =
                colorScheme.surfaceVariant.copy(alpha = if (isLightTheme) 0.96f else 0.9f)
            val borderColor = colorScheme.primary.copy(alpha = if (isLightTheme) 0.12f else 0.22f)
            val labelColor = colorScheme.onSurface
            val supportingColor = colorScheme.onSurfaceVariantSummary.copy(alpha = 0.82f)

            Box(
                modifier =
                    fabModifier
                        .shadow(
                            elevation = 14.dp,
                            shape = shape,
                            ambientColor =
                                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.16f),
                            spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.12f),
                        )
                        .clip(shape)
                        .background(containerColor, shape)
                        .border(width = 0.8.dp, color = borderColor, shape = shape)
                        .clickable(onClick = onClick)
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                        .widthIn(min = 176.dp, max = 232.dp)
                        .heightIn(min = 58.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier.size(38.dp)
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

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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

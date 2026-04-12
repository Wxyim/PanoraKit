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

package com.github.yumelira.yumebox.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.common.util.formatBytesForDisplay
import com.github.yumelira.yumebox.core.model.TunnelState
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.domain.model.TrafficData
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.*
import com.github.yumelira.yumebox.presentation.theme.AnimationSpecs
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val DOWNLOAD_SPEED_VALUE_PLACEHOLDER = "8888"
private const val SPEED_UNIT_PLACEHOLDER = "GB/s"
private const val UPLOAD_SPEED_PLACEHOLDER = "8888 GB/s"

private enum class HomeControlTone {
    Primary,
    Secondary,
    Muted,
}

@Composable
fun TrafficDisplay(
    trafficNow: TrafficData,
    profileName: String?,
    tunnelMode: TunnelState.Mode?,
    runtimeVisualState: HomeRuntimeVisualState,
    canStartProxy: Boolean,
    isRunning: Boolean,
    proxyMode: ProxyMode,
    onStatusCapsuleClick: (() -> Unit)? = null,
    onModeBadgeClick: (() -> Unit)? = null,
    onModeBadgeBoundsChanged: (Rect) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth)
        val metrics = remember(maxWidth) { HomeTrafficMetrics.from(maxWidth) }
        val shouldEmphasizeProfileSelection =
            runtimeVisualState == HomeRuntimeVisualState.Idle && !canStartProxy
        val controlColumnWidth =
            when {
                availableAdaptiveInfo.isExpandedWidth ->
                    (maxWidth * 0.34f).coerceIn(224.dp, 292.dp)
                availableAdaptiveInfo.isMediumWidth ->
                    (maxWidth * 0.40f).coerceIn(196.dp, 244.dp)
                else -> (maxWidth * 0.46f).coerceIn(176.dp, 212.dp)
            }

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(top = metrics.topPadding, bottom = metrics.bottomPadding),
            verticalArrangement = Arrangement.spacedBy(metrics.capsuleSectionSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(metrics.sectionSpacing),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(metrics.uploadSectionSpacing),
                ) {
                    DownloadSection(downloadSpeed = trafficNow.download, metrics = metrics)
                    UploadSection(uploadSpeed = trafficNow.upload, metrics = metrics)
                }

                Column(
                    modifier =
                        Modifier.widthIn(min = controlColumnWidth, max = controlColumnWidth)
                            .animateContentSize(
                                tween(
                                    AnimationSpecs.DURATION_FAST,
                                    easing = AnimationSpecs.EmphasizedDecelerate,
                                )
                            ),
                    verticalArrangement = Arrangement.spacedBy(metrics.controlStackSpacing),
                    horizontalAlignment = Alignment.End,
                ) {
                    ProfileModeBadge(
                        profileName = profileName,
                        tunnelMode = tunnelMode,
                        metrics = metrics,
                        tone =
                            if (shouldEmphasizeProfileSelection) {
                                HomeControlTone.Primary
                            } else {
                                HomeControlTone.Secondary
                            },
                        onClick = onModeBadgeClick,
                        onBoundsChanged = onModeBadgeBoundsChanged,
                    )

                    ProxyStatusCapsule(
                        runtimeVisualState = runtimeVisualState,
                        canStartProxy = canStartProxy,
                        tunnelMode = tunnelMode,
                        proxyMode = proxyMode,
                        metrics = metrics,
                        tone =
                            if (canStartProxy || runtimeVisualState != HomeRuntimeVisualState.Idle) {
                                HomeControlTone.Primary
                            } else {
                                HomeControlTone.Muted
                            },
                        onClick = onStatusCapsuleClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadSection(
    downloadSpeed: Long,
    metrics: HomeTrafficMetrics,
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = "DOWNLOAD",
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = metrics.labelFontSize),
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )

        SpeedValue(speed = downloadSpeed, metrics = metrics)
    }
}

@Composable
private fun ProfileModeBadge(
    profileName: String?,
    tunnelMode: TunnelState.Mode?,
    metrics: HomeTrafficMetrics,
    tone: HomeControlTone,
    onClick: (() -> Unit)?,
    onBoundsChanged: (Rect) -> Unit,
) {
    val primary = MiuixTheme.colorScheme.primary
    val accentColor =
        if (tone == HomeControlTone.Primary) {
            primary
        } else {
            MiuixTheme.colorScheme.onSurface
        }
    val headline = tunnelMode.toDisplayName()
    val controlDescription = listOf(profileName ?: MLang.Home.Profile.NoProfile, headline).joinToString(", ")
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val containerColor by
        animateColorAsState(
            targetValue = accentColor.copy(alpha = tone.containerAlpha(onClick != null, isPressed)),
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.DURATION_INSTANT,
                    easing = AnimationSpecs.StandardEasing,
                ),
            label = "ModeBadgeContainerColor",
        )
    val controlScale by
        animateFloatAsState(
            targetValue =
                if (onClick != null && isPressed) {
                    metrics.controlPressedScale
                } else {
                    1f
                },
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.DURATION_INSTANT,
                    easing = AnimationSpecs.StandardEasing,
                ),
            label = "ModeBadgeScale",
        )
    Box(
        modifier = Modifier.fillMaxWidth().heightIn(min = metrics.controlTouchTargetHeight),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(metrics.controlCornerRadius),
            modifier =
                Modifier.fillMaxWidth()
                    .scale(controlScale)
                    .heightIn(min = metrics.modeBadgeHeight)
                    .semantics(mergeDescendants = true) {
                        contentDescription = controlDescription
                    }
                    .onGloballyPositioned { coordinates ->
                        onBoundsChanged(coordinates.boundsInRoot())
                    }
                    .let { baseModifier ->
                        if (onClick != null) {
                            baseModifier.clickable(
                                role = Role.Button,
                                interactionSource = interactionSource,
                                onClickLabel = controlDescription,
                                onClick = onClick,
                            )
                        } else {
                            baseModifier
                        }
                    },
        ) {
            HomeControlTextBlock(
                caption = profileName ?: MLang.Home.Profile.NoProfile,
                headline = headline,
                headlineLeadingIcon = tunnelMode.toDisplayIcon(),
                metrics = metrics,
                accentColor = accentColor,
                modifier =
                    Modifier.padding(
                        horizontal = metrics.controlHorizontalPadding,
                        vertical = metrics.controlVerticalPadding,
                    ),
                trailing = {
                    Icon(
                        imageVector = Yume.chevron,
                        contentDescription = null,
                        tint = accentColor.copy(alpha = 0.70f),
                        modifier = Modifier.size(metrics.controlChevronSize),
                    )
                },
            )
        }
    }
}

@Composable
private fun SpeedValue(speed: Long, metrics: HomeTrafficMetrics) {
    val (value, unit) = formatBytesForDisplay(speed)
    val valueStyle =
        MiuixTheme.textStyles.headline1.copy(
            fontSize = metrics.trafficFontSize,
            lineHeight = metrics.trafficFontSize,
            letterSpacing = metrics.trafficLetterSpacing,
        )
    val unitStyle = MiuixTheme.textStyles.title2.copy(fontSize = metrics.trafficUnitFontSize)
    val primary = MiuixTheme.colorScheme.primary

    Row(verticalAlignment = Alignment.Bottom) {
        ReservedMetricText(
            text = value,
            placeholder = DOWNLOAD_SPEED_VALUE_PLACEHOLDER,
            style = valueStyle,
            color = primary,
        )
        ReservedMetricText(
            text = unit,
            placeholder = SPEED_UNIT_PLACEHOLDER,
            style = unitStyle,
            color = primary.copy(alpha = 0.5f),
            modifier =
                Modifier.padding(
                    bottom = metrics.unitBottomPadding,
                    start = metrics.unitStartPadding,
                ),
        )
    }
}

@Composable
private fun UploadSection(uploadSpeed: Long, metrics: HomeTrafficMetrics) {
    val (value, unit) = formatBytesForDisplay(uploadSpeed)
    val valueStyle = MiuixTheme.textStyles.title2.copy(fontSize = metrics.uploadValueFontSize)

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(metrics.uploadSectionSpacing),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(metrics.uploadValueSpacing),
        ) {
            Text(
                text = "UPLOAD",
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = metrics.labelFontSize),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            ReservedMetricText(
                text = "$value $unit",
                placeholder = UPLOAD_SPEED_PLACEHOLDER,
                style = valueStyle,
                color = MiuixTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ReservedMetricText(
    text: String,
    placeholder: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomStart) {
        Text(text = placeholder, style = style, color = color.copy(alpha = 0f), maxLines = 1)
        Text(text = text, style = style, color = color, maxLines = 1, overflow = TextOverflow.Clip)
    }
}

@Composable
private fun ProxyStatusCapsule(
    runtimeVisualState: HomeRuntimeVisualState,
    canStartProxy: Boolean,
    tunnelMode: TunnelState.Mode?,
    proxyMode: ProxyMode,
    metrics: HomeTrafficMetrics,
    tone: HomeControlTone,
    onClick: (() -> Unit)?,
) {
    val primary = MiuixTheme.colorScheme.primary
    val statusHeadline =
        when {
            runtimeVisualState == HomeRuntimeVisualState.Idle && !canStartProxy ->
                MLang.Home.Profile.NoProfile
            runtimeVisualState == HomeRuntimeVisualState.Starting -> MLang.Home.Status.Starting
            runtimeVisualState == HomeRuntimeVisualState.Running -> MLang.Home.Status.Running
            runtimeVisualState == HomeRuntimeVisualState.Stopping -> MLang.Home.Status.Stopping
            else -> MLang.Home.Status.TapToStart
        }
    val statusIcon =
        when {
            runtimeVisualState == HomeRuntimeVisualState.Idle && !canStartProxy ->
                tunnelMode.toDisplayIcon()
            runtimeVisualState == HomeRuntimeVisualState.Starting -> Yume.Play
            runtimeVisualState == HomeRuntimeVisualState.Running -> Yume.Activity
            runtimeVisualState == HomeRuntimeVisualState.Stopping -> Yume.Square
            else -> Yume.Rocket
        }
    val contentColor =
        if (runtimeVisualState == HomeRuntimeVisualState.Idle && !canStartProxy) {
            MiuixTheme.colorScheme.onSurfaceVariantSummary
        } else {
            primary
        }
    val controlDescription = listOf(proxyMode.toTransportLabel(), statusHeadline).joinToString(", ")
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val containerColor by
        animateColorAsState(
            targetValue = contentColor.copy(alpha = tone.containerAlpha(onClick != null, isPressed)),
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.DURATION_INSTANT,
                    easing = AnimationSpecs.StandardEasing,
                ),
            label = "StatusCapsuleContainerColor",
        )
    val controlScale by
        animateFloatAsState(
            targetValue =
                if (onClick != null && isPressed) {
                    metrics.controlPressedScale
                } else {
                    1f
                },
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.DURATION_INSTANT,
                    easing = AnimationSpecs.StandardEasing,
                ),
            label = "StatusCapsuleScale",
        )
    Box(
        modifier = Modifier.fillMaxWidth().heightIn(min = metrics.controlTouchTargetHeight),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(metrics.controlCornerRadius),
            modifier =
                Modifier.fillMaxWidth()
                    .scale(controlScale)
                    .heightIn(min = metrics.statusCapsuleHeight)
                    .semantics(mergeDescendants = true) {
                        contentDescription = controlDescription
                    }
                    .let { baseModifier ->
                        if (onClick != null) {
                            baseModifier.clickable(
                                role = Role.Button,
                                interactionSource = interactionSource,
                                onClickLabel = controlDescription,
                                onClick = onClick,
                            )
                        } else {
                            baseModifier
                        }
                    }
                    .animateContentSize(
                        animationSpec =
                            tween(
                                AnimationSpecs.DURATION_FAST,
                                easing = AnimationSpecs.EmphasizedDecelerate,
                            ),
                    ),
        ) {
            AnimatedContent(
                targetState = runtimeVisualState,
                transitionSpec = {
                    (slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec =
                                tween(
                                    AnimationSpecs.DURATION_FAST,
                                    easing = AnimationSpecs.EmphasizedDecelerate,
                                ),
                        ) +
                            fadeIn(
                                tween(
                                    AnimationSpecs.DURATION_FAST,
                                    easing = AnimationSpecs.EnterEasing,
                                )
                            ))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { -it / 2 },
                                animationSpec =
                                    tween(
                                        AnimationSpecs.DURATION_INSTANT,
                                        easing = AnimationSpecs.EmphasizedAccelerate,
                                    ),
                            ) +
                                fadeOut(
                                    tween(
                                        AnimationSpecs.DURATION_INSTANT,
                                        easing = AnimationSpecs.ExitEasing,
                                    )
                                )
                        )
                },
                label = "CapsuleStateTransition",
            ) {
                HomeControlTextBlock(
                    caption = proxyMode.toTransportLabel(),
                    headline = statusHeadline,
                    headlineLeadingIcon = statusIcon,
                    metrics = metrics,
                    accentColor = contentColor,
                    modifier =
                        Modifier.fillMaxWidth().padding(
                            horizontal = metrics.controlHorizontalPadding,
                            vertical = metrics.controlVerticalPadding,
                        ),
                )
            }
        }
    }
}

@Composable
private fun HomeControlTextBlock(
    caption: String,
    headline: String,
    metrics: HomeTrafficMetrics,
    modifier: Modifier = Modifier,
    headlineLeadingIcon: ImageVector? = null,
    accentColor: Color = MiuixTheme.colorScheme.primary,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(metrics.controlInnerSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = caption,
                style =
                    MiuixTheme.textStyles.footnote1.copy(
                        fontSize = metrics.modeCaptionTextSize,
                        fontWeight = FontWeight.Medium,
                    ),
                color = accentColor.copy(alpha = 0.76f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(metrics.controlInnerSpacing),
            ) {
                if (headlineLeadingIcon != null) {
                    Icon(
                        imageVector = headlineLeadingIcon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(metrics.controlIconSize),
                    )
                }
                Text(
                    text = headline,
                    style =
                        MiuixTheme.textStyles.body2.copy(
                            fontSize = metrics.controlTextSize,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = accentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        trailing?.invoke()
    }
}

private fun HomeControlTone.containerAlpha(enabled: Boolean, pressed: Boolean): Float =
    when (this) {
        HomeControlTone.Primary ->
            when {
                enabled && pressed -> 0.22f
                enabled -> 0.16f
                else -> 0.10f
            }

        HomeControlTone.Secondary ->
            when {
                enabled && pressed -> 0.10f
                enabled -> 0.05f
                else -> 0.03f
            }

        HomeControlTone.Muted ->
            when {
                enabled && pressed -> 0.08f
                enabled -> 0.04f
                else -> 0.025f
            }
    }

private fun TunnelState.Mode?.toDisplayName(): String =
    when (this) {
        TunnelState.Mode.Direct -> MLang.Home.Profile.Direct
        TunnelState.Mode.Global -> MLang.Home.Profile.Global
        TunnelState.Mode.Rule -> MLang.Home.Profile.Rule
        else -> MLang.Home.Profile.Rule
    }

private fun TunnelState.Mode?.toDisplayIcon(): ImageVector =
    when (this) {
        TunnelState.Mode.Direct -> Yume.ShieldCheck
        TunnelState.Mode.Global -> Yume.Cloud
        TunnelState.Mode.Rule -> Yume.List
        else -> Yume.List
    }

private fun ProxyMode.toTransportLabel(): String =
    when (this) {
        ProxyMode.Tun -> "VPN"
        ProxyMode.RootTun -> "TUN"
        ProxyMode.Http -> "HTTP"
    }

private data class HomeTrafficMetrics(
    val topPadding: Dp,
    val bottomPadding: Dp,
    val sectionSpacing: Dp,
    val capsuleSectionSpacing: Dp,
    val controlStackSpacing: Dp,
    val labelFontSize: androidx.compose.ui.unit.TextUnit,
    val trafficFontSize: androidx.compose.ui.unit.TextUnit,
    val trafficLetterSpacing: androidx.compose.ui.unit.TextUnit,
    val trafficUnitFontSize: androidx.compose.ui.unit.TextUnit,
    val unitBottomPadding: Dp,
    val unitStartPadding: Dp,
    val uploadSectionSpacing: Dp,
    val uploadValueSpacing: Dp,
    val uploadValueFontSize: androidx.compose.ui.unit.TextUnit,
    val capsuleHeight: Dp,
    val capsuleHorizontalPadding: Dp,
    val capsuleInnerSpacing: Dp,
    val capsuleIconSpacing: Dp,
    val capsuleIconSize: Dp,
    val capsuleDotSize: Dp,
    val capsuleTextSize: androidx.compose.ui.unit.TextUnit,
    val controlTouchTargetHeight: Dp,
    val controlCornerRadius: Dp,
    val controlHorizontalPadding: Dp,
    val controlVerticalPadding: Dp,
    val controlInnerSpacing: Dp,
    val controlIconSize: Dp,
    val controlChevronSize: Dp,
    val controlTextSize: androidx.compose.ui.unit.TextUnit,
    val modeCaptionTextSize: androidx.compose.ui.unit.TextUnit,
    val controlPressedScale: Float,
    val modeBadgeHeight: Dp,
    val modeBadgeMinWidth: Dp,
    val modeBadgeMaxWidth: Dp,
    val statusCapsuleHeight: Dp,
    val statusCapsuleMinWidth: Dp,
) {
    companion object {
        fun from(width: Dp): HomeTrafficMetrics {
            val scale = (width / 390.dp).coerceIn(0.9f, 1f)
            return HomeTrafficMetrics(
                topPadding = (36.dp * scale).coerceIn(30.dp, 36.dp),
                bottomPadding = (8.dp * scale).coerceIn(6.dp, 8.dp),
                sectionSpacing = (16.dp * scale).coerceIn(12.dp, 16.dp),
                capsuleSectionSpacing = (12.dp * scale).coerceIn(8.dp, 12.dp),
                controlStackSpacing = (12.dp * scale).coerceIn(10.dp, 12.dp),
                labelFontSize = (14f * scale).coerceIn(13f, 14f).sp,
                trafficFontSize = (96f * scale).coerceIn(80f, 96f).sp,
                trafficLetterSpacing = (-3f * scale).coerceIn(-3f, -2f).sp,
                trafficUnitFontSize = (24f * scale).coerceIn(20f, 24f).sp,
                unitBottomPadding = (14.dp * scale).coerceIn(10.dp, 14.dp),
                unitStartPadding = (8.dp * scale).coerceIn(6.dp, 8.dp),
                uploadSectionSpacing = (10.dp * scale).coerceIn(8.dp, 10.dp),
                uploadValueSpacing = (12.dp * scale).coerceIn(10.dp, 12.dp),
                uploadValueFontSize = (20f * scale).coerceIn(18f, 20f).sp,
                capsuleHeight = (28.dp * scale).coerceIn(26.dp, 28.dp),
                capsuleHorizontalPadding = (12.dp * scale).coerceIn(10.dp, 12.dp),
                capsuleInnerSpacing = (8.dp * scale).coerceIn(6.dp, 8.dp),
                capsuleIconSpacing = (6.dp * scale).coerceIn(5.dp, 6.dp),
                capsuleIconSize = (12.dp * scale).coerceIn(11.dp, 12.dp),
                capsuleDotSize = (4.dp * scale).coerceIn(3.dp, 4.dp),
                capsuleTextSize = (12f * scale).coerceIn(11f, 12f).sp,
                controlTouchTargetHeight = (56.dp * scale).coerceIn(52.dp, 56.dp),
                controlCornerRadius = (22.dp * scale).coerceIn(20.dp, 22.dp),
                controlHorizontalPadding = (18.dp * scale).coerceIn(16.dp, 18.dp),
                controlVerticalPadding = (10.dp * scale).coerceIn(8.dp, 10.dp),
                controlInnerSpacing = (10.dp * scale).coerceIn(8.dp, 10.dp),
                controlIconSize = (18.dp * scale).coerceIn(16.dp, 18.dp),
                controlChevronSize = (16.dp * scale).coerceIn(14.dp, 16.dp),
                controlTextSize = (15f * scale).coerceIn(14f, 15f).sp,
                modeCaptionTextSize = (12f * scale).coerceIn(11f, 12f).sp,
                controlPressedScale = 0.985f,
                modeBadgeHeight = (52.dp * scale).coerceIn(48.dp, 52.dp),
                modeBadgeMinWidth = (184.dp * scale).coerceIn(170.dp, 184.dp),
                modeBadgeMaxWidth = (248.dp * scale).coerceIn(220.dp, 248.dp),
                statusCapsuleHeight = (52.dp * scale).coerceIn(48.dp, 52.dp),
                statusCapsuleMinWidth = (164.dp * scale).coerceIn(148.dp, 164.dp),
            )
        }
    }
}

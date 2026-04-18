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

package com.github.nomadboxlab.monadbox.feature.home

import androidx.compose.animation.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.nomadboxlab.monadbox.common.util.formatBytesForDisplay
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.domain.model.TrafficData
import com.github.nomadboxlab.monadbox.presentation.component.TestTags
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.*
import com.github.nomadboxlab.monadbox.presentation.theme.AnimationSpecs
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.HomeTrafficMetrics
import com.github.nomadboxlab.monadbox.presentation.theme.HomeTrafficMetricsDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.presentation.theme.rememberHomeTrafficMetrics
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
        val metrics = rememberHomeTrafficMetrics(maxWidth)
        val shouldEmphasizeProfileSelection =
            runtimeVisualState == HomeRuntimeVisualState.Idle && !canStartProxy
        val controlColumnWidth =
            when {
                availableAdaptiveInfo.isExpandedWidth ->
                    (maxWidth * 0.34f).coerceIn(
                        HomeTrafficMetricsDefaults.ControlColumnExpandedMin,
                        HomeTrafficMetricsDefaults.ControlColumnExpandedMax,
                    )
                availableAdaptiveInfo.isMediumWidth ->
                    (maxWidth * 0.40f).coerceIn(
                        HomeTrafficMetricsDefaults.ControlColumnMediumMin,
                        HomeTrafficMetricsDefaults.ControlColumnMediumMax,
                    )
                else ->
                    (maxWidth * 0.46f).coerceIn(
                        HomeTrafficMetricsDefaults.ControlColumnCompactMin,
                        HomeTrafficMetricsDefaults.ControlColumnCompactMax,
                    )
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
                            if (
                                canStartProxy || runtimeVisualState != HomeRuntimeVisualState.Idle
                            ) {
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
private fun DownloadSection(downloadSpeed: Long, metrics: HomeTrafficMetrics) {
    Column(
        modifier = Modifier.testTag(TestTags.Home.DownloadSpeed),
        horizontalAlignment = Alignment.Start,
    ) {
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
    val controlDescription =
        listOf(profileName ?: MLang.Home.Profile.NoProfile, headline).joinToString(", ")
    val interactionSource = remember { MutableInteractionSource() }
    val containerColor by
        animateColorAsState(
            targetValue =
                accentColor.copy(alpha = tone.containerAlpha(onClick != null, pressed = false)),
            animationSpec =
                tween(
                    durationMillis = AnimationSpecs.DURATION_INSTANT,
                    easing = AnimationSpecs.StandardEasing,
                ),
            label = "ModeBadgeContainerColor",
        )
    val controlScale by
        animateFloatAsState(
            targetValue = 1f,
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
                    .testTag(TestTags.Home.ProfileModeBadge)
                    .semantics(mergeDescendants = true) { contentDescription = controlDescription }
                    .onGloballyPositioned { coordinates ->
                        onBoundsChanged(coordinates.boundsInRoot())
                    }
                    .let { baseModifier ->
                        if (onClick != null) {
                            baseModifier.clickable(
                                role = Role.Button,
                                interactionSource = interactionSource,
                                indication = null,
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
                        imageVector = MonadIcons.chevron,
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
        modifier = Modifier.testTag(TestTags.Home.UploadSpeed),
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
    val statusHeadline = statusHeadlineFor(runtimeVisualState, canStartProxy)
    val statusIcon = statusIconFor(runtimeVisualState, canStartProxy, tunnelMode)
    val contentColor =
        if (runtimeVisualState == HomeRuntimeVisualState.Idle && !canStartProxy) {
            MiuixTheme.colorScheme.onSurfaceVariantSummary
        } else {
            primary
        }
    val controlDescription = listOf(proxyMode.toTransportLabel(), statusHeadline).joinToString(", ")
    val runtimeStateDescription = statusHeadline
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val containerColor by
        animateColorAsState(
            targetValue =
                contentColor.copy(alpha = tone.containerAlpha(onClick != null, isPressed)),
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
                    .testTag(TestTags.Home.StatusCapsule)
                    .semantics(mergeDescendants = true) {
                        contentDescription = controlDescription
                        stateDescription = runtimeStateDescription
                        liveRegion = LiveRegionMode.Polite
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
                            )
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
            ) { animatedState ->
                val animatedHeadline = statusHeadlineFor(animatedState, canStartProxy)
                val animatedIcon = statusIconFor(animatedState, canStartProxy, tunnelMode)
                HomeControlTextBlock(
                    caption = proxyMode.toTransportLabel(),
                    headline = animatedHeadline,
                    headlineLeadingIcon = animatedIcon,
                    metrics = metrics,
                    accentColor = contentColor,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(
                                horizontal = metrics.controlHorizontalPadding,
                                vertical = metrics.controlVerticalPadding,
                            ),
                )
            }
        }
    }
}

private fun statusHeadlineFor(state: HomeRuntimeVisualState, canStartProxy: Boolean): String {
    return when {
        state == HomeRuntimeVisualState.Idle && !canStartProxy -> MLang.Home.Profile.NoProfile
        state == HomeRuntimeVisualState.Starting -> MLang.Home.Status.Starting
        state == HomeRuntimeVisualState.Running -> MLang.Home.Status.Running
        state == HomeRuntimeVisualState.Stopping -> MLang.Home.Status.Stopping
        else -> MLang.Home.Status.TapToStart
    }
}

private fun statusIconFor(
    state: HomeRuntimeVisualState,
    canStartProxy: Boolean,
    tunnelMode: TunnelState.Mode?,
): ImageVector {
    return when {
        state == HomeRuntimeVisualState.Idle && !canStartProxy -> tunnelMode.toDisplayIcon()
        state == HomeRuntimeVisualState.Starting -> MonadIcons.Play
        state == HomeRuntimeVisualState.Running -> MonadIcons.Activity
        state == HomeRuntimeVisualState.Stopping -> MonadIcons.Square
        else -> MonadIcons.Rocket
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
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
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
        TunnelState.Mode.Direct -> MonadIcons.ShieldCheck
        TunnelState.Mode.Global -> MonadIcons.Cloud
        TunnelState.Mode.Rule -> MonadIcons.List
        else -> MonadIcons.List
    }

private fun ProxyMode.toTransportLabel(): String =
    when (this) {
        ProxyMode.Tun -> "VPN"
        ProxyMode.RootTun -> "TUN"
        ProxyMode.Http -> "HTTP"
    }

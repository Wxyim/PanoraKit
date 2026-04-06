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
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.common.AppConstants
import com.github.yumelira.yumebox.common.util.formatBytesForDisplay
import com.github.yumelira.yumebox.core.model.TunnelState
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.domain.model.TrafficData
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.*
import com.github.yumelira.yumebox.presentation.theme.AnimationSpecs
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun TrafficDisplay(
    trafficNow: TrafficData,
    profileName: String?,
    tunnelMode: TunnelState.Mode?,
    isRunning: Boolean,
    proxyMode: ProxyMode,
    onModeBadgeBoundsChanged: (Rect) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        val metrics = remember(maxWidth) { HomeTrafficMetrics.from(maxWidth) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = metrics.topPadding, bottom = metrics.bottomPadding),
            verticalArrangement = Arrangement.spacedBy(metrics.sectionSpacing)
        ) {
            DownloadSection(
                downloadSpeed = trafficNow.download,
                profileName = profileName,
                tunnelMode = tunnelMode,
                metrics = metrics,
                onModeBadgeBoundsChanged = onModeBadgeBoundsChanged,
            )

            UploadSection(
                uploadSpeed = trafficNow.upload,
                metrics = metrics,
            )

            Column(
                modifier = Modifier.animateContentSize(
                    tween(
                        AnimationSpecs.DURATION_FAST,
                        easing = AnimationSpecs.EmphasizedDecelerate
                    )
                ),
                verticalArrangement = Arrangement.spacedBy(metrics.capsuleSectionSpacing)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(metrics.capsuleSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProxyStatusCapsule(
                        isRunning = isRunning,
                        metrics = metrics,
                    )
                    AnimatedVisibility(
                        visible = isRunning,
                        enter = slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = tween(AnimationSpecs.DURATION_FAST, easing = AnimationSpecs.EmphasizedDecelerate)
                        ) + fadeIn(tween(AnimationSpecs.DURATION_FAST, easing = AnimationSpecs.EnterEasing)),
                        exit = slideOutHorizontally(
                            targetOffsetX = { it / 2 },
                            animationSpec = tween(AnimationSpecs.DURATION_INSTANT, easing = AnimationSpecs.EmphasizedAccelerate)
                        ) + fadeOut(tween(AnimationSpecs.DURATION_INSTANT, easing = AnimationSpecs.ExitEasing))
                    ) {
                        ProxyTypeCapsule(
                            proxyMode = proxyMode,
                            metrics = metrics,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadSection(
    downloadSpeed: Long,
    profileName: String?,
    tunnelMode: TunnelState.Mode?,
    metrics: HomeTrafficMetrics,
    onModeBadgeBoundsChanged: (Rect) -> Unit,
) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DOWNLOAD",
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = metrics.labelFontSize),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )

            ProfileModeBadge(
                profileName = profileName,
                tunnelMode = tunnelMode,
                metrics = metrics,
                onBoundsChanged = onModeBadgeBoundsChanged,
            )
        }

        SpeedValue(speed = downloadSpeed, metrics = metrics)
    }
}

@Composable
private fun ProfileModeBadge(
    profileName: String?,
    tunnelMode: TunnelState.Mode?,
    metrics: HomeTrafficMetrics,
    onBoundsChanged: (Rect) -> Unit,
) {
    Surface(
        color = MiuixTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .height(metrics.capsuleHeight)
            .onGloballyPositioned { coordinates ->
                onBoundsChanged(coordinates.boundsInRoot())
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = metrics.capsuleHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(metrics.capsuleInnerSpacing)
        ) {
            Text(
                text = profileName ?: MLang.Home.Profile.NoProfile,
                style = MiuixTheme.textStyles.footnote1.copy(
                    fontSize = metrics.capsuleTextSize,
                    fontWeight = FontWeight.Bold
                ),
                color = MiuixTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .size(metrics.capsuleDotSize)
                    .background(MiuixTheme.colorScheme.primary, CircleShape)
            )

            Text(
                text = tunnelMode.toDisplayName(),
                style = MiuixTheme.textStyles.footnote1.copy(
                    fontSize = metrics.capsuleTextSize,
                    fontWeight = FontWeight.Bold
                ),
                color = MiuixTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SpeedValue(speed: Long, metrics: HomeTrafficMetrics) {
    val (value, unit) = formatBytesForDisplay(speed)
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = value,
            style = MiuixTheme.textStyles.headline1.copy(
                fontSize = metrics.trafficFontSize,
                lineHeight = metrics.trafficFontSize,
                letterSpacing = metrics.trafficLetterSpacing
            ),
            color = MiuixTheme.colorScheme.primary
        )
        Text(
            text = unit,
            style = MiuixTheme.textStyles.title2.copy(
                fontSize = metrics.trafficUnitFontSize
            ),
            color = MiuixTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = metrics.unitBottomPadding, start = metrics.unitStartPadding)
        )
    }
}

@Composable
private fun UploadSection(uploadSpeed: Long, metrics: HomeTrafficMetrics) {
    val (value, unit) = formatBytesForDisplay(uploadSpeed)
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(metrics.uploadSectionSpacing)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(metrics.uploadValueSpacing)
        ) {
            Text(
                text = "UPLOAD",
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = metrics.labelFontSize),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            Text(
                text = "$value $unit",
                style = MiuixTheme.textStyles.title2.copy(fontSize = metrics.uploadValueFontSize),
                color = MiuixTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProxyTypeCapsule(proxyMode: ProxyMode, metrics: HomeTrafficMetrics) {
    val primary = MiuixTheme.colorScheme.primary
    Surface(
        color = primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(metrics.capsuleHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = metrics.capsuleHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(metrics.capsuleIconSpacing)
        ) {
            Icon(
                imageVector = when (proxyMode) {
                    ProxyMode.Tun -> Yume.PlaneTakeoff
                    ProxyMode.RootTun -> Yume.Tun
                    ProxyMode.Http -> Yume.Wifi
                },
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(metrics.capsuleIconSize)
            )
            Text(
                text = when (proxyMode) {
                    ProxyMode.Tun -> "VPN"
                    ProxyMode.RootTun -> "TUN"
                    ProxyMode.Http -> "HTTP"
                },
                style = MiuixTheme.textStyles.footnote1.copy(
                    fontSize = metrics.capsuleTextSize,
                    fontWeight = FontWeight.Bold
                ),
                color = primary
            )
        }
    }
}

@Composable
private fun ProxyStatusCapsule(
    isRunning: Boolean,
    metrics: HomeTrafficMetrics,
) {
    val primary = MiuixTheme.colorScheme.primary
    Surface(
        color = primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .height(metrics.capsuleHeight)
            .animateContentSize(tween(AnimationSpecs.DURATION_FAST, easing = AnimationSpecs.EmphasizedDecelerate))
    ) {
        AnimatedContent(
            targetState = isRunning,
            transitionSpec = {
                (slideInHorizontally(
                    initialOffsetX = { it / 2 },
                    animationSpec = tween(AnimationSpecs.DURATION_FAST, easing = AnimationSpecs.EmphasizedDecelerate)
                ) + fadeIn(tween(AnimationSpecs.DURATION_FAST, easing = AnimationSpecs.EnterEasing))
                ).togetherWith(
                    slideOutHorizontally(
                        targetOffsetX = { -it / 2 },
                        animationSpec = tween(AnimationSpecs.DURATION_INSTANT, easing = AnimationSpecs.EmphasizedAccelerate)
                    ) + fadeOut(tween(AnimationSpecs.DURATION_INSTANT, easing = AnimationSpecs.ExitEasing))
                )
            },
            label = "CapsuleStateTransition"
        ) { running ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = metrics.capsuleHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(metrics.capsuleIconSpacing)
            ) {
                Icon(
                    imageVector = if (running) Yume.Activity else Yume.Rocket,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(metrics.capsuleIconSize)
                )
                Text(
                    text = if (running) MLang.Home.Status.Running else MLang.Home.Status.TapToStart,
                    style = MiuixTheme.textStyles.footnote1.copy(
                        fontSize = metrics.capsuleTextSize,
                        fontWeight = FontWeight.Bold
                    ),
                    color = primary
                )
            }
        }
    }
}

private fun TunnelState.Mode?.toDisplayName(): String = when (this) {
    TunnelState.Mode.Direct -> MLang.Home.Profile.Direct
    TunnelState.Mode.Global -> MLang.Home.Profile.Global
    TunnelState.Mode.Rule -> MLang.Home.Profile.Rule
    else -> MLang.Home.Profile.Rule
}

private data class HomeTrafficMetrics(
    val topPadding: Dp,
    val bottomPadding: Dp,
    val sectionSpacing: Dp,
    val capsuleSectionSpacing: Dp,
    val capsuleSpacing: Dp,
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
) {
    companion object {
        fun from(width: Dp): HomeTrafficMetrics {
            val scale = (width / 390.dp).coerceIn(0.9f, 1f)
            return HomeTrafficMetrics(
                topPadding = (60.dp * scale).coerceIn(48.dp, 60.dp),
                bottomPadding = (16.dp * scale).coerceIn(12.dp, 16.dp),
                sectionSpacing = (24.dp * scale).coerceIn(18.dp, 24.dp),
                capsuleSectionSpacing = (18.dp * scale).coerceIn(14.dp, 18.dp),
                capsuleSpacing = (8.dp * scale).coerceIn(6.dp, 8.dp),
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
            )
        }
    }
}

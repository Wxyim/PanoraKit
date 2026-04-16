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

package com.github.nomadboxlab.monadbox.screen.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.github.nomadboxlab.monadbox.common.AppConstants
import com.github.nomadboxlab.monadbox.presentation.component.appClickable
import com.github.nomadboxlab.monadbox.presentation.theme.SpeedChartLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.TrafficChartConfig
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val SPEED_CHART_SAMPLE_LIMIT = AppConstants.Limits.SPEED_HISTORY_SIZE
private const val SPEED_CHART_IDLE_SCROLL_DURATION_MS = 900
private const val SPEED_CHART_CALM_SCROLL_DURATION_MS = 1500
private const val SPEED_CHART_BAR_ALPHA = 0.75f
private const val SPEED_CHART_IDLE_WAVE_AMPLITUDE = 0.022f
private const val SPEED_CHART_IDLE_WAVE_SPAN = 4f
private const val SPEED_CHART_CALM_WAVE_AMPLITUDE = 0.012f
private const val SPEED_CHART_CALM_WAVE_SPAN = 3.2f
private const val SPEED_CHART_CALM_VARIANCE_THRESHOLD = 0.028f

@Composable
fun SpeedChart(
    speedHistory: SpeedHistoryBuffer,
    isRunning: Boolean,
    onClick: () -> Unit,
    chartHeight: Dp = AppConstants.UI.SPEED_CHART_HEIGHT,
    modifier: Modifier = Modifier,
) {
    val chartColor = MiuixTheme.colorScheme.primary
    val chartAccessibilityLabel = MLang.TrafficStatistics.Title
    val fractions =
        remember(speedHistory.version, speedHistory.size, speedHistory.head) {
            buildSpeedChartFractions(speedHistory = speedHistory)
        }
    val isCalmRunning =
        remember(speedHistory.version, speedHistory.size, speedHistory.head, isRunning) {
            isRunning && isCalmTraffic(speedHistory)
        }
    val idleTransition = rememberInfiniteTransition(label = "speed_chart_idle")
    val idlePhase by
        idleTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = SPEED_CHART_IDLE_SCROLL_DURATION_MS,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "speed_chart_idle_phase",
        )
    val calmPhase by
        idleTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = SPEED_CHART_CALM_SCROLL_DURATION_MS,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "speed_chart_calm_phase",
        )

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(chartHeight)
                .clip(RoundedCornerShape(AppConstants.UI.CARD_CORNER_RADIUS))
                .semantics { contentDescription = chartAccessibilityLabel }
                .appClickable(
                    role = Role.Button,
                    onClickLabel = chartAccessibilityLabel,
                    onClick = onClick,
                )
    ) {
        val barGapPx = SpeedChartLayoutDefaults.BarGap.toPx()
        val chartBarCount = SPEED_CHART_SAMPLE_LIMIT

        val totalGapWidth = barGapPx * (chartBarCount - 1)
        val barWidthPx = ((size.width - totalGapWidth) / chartBarCount).coerceAtLeast(0f)
        if (barWidthPx <= 0f) {
            return@Canvas
        }

        val barColor = chartColor.copy(alpha = SPEED_CHART_BAR_ALPHA)

        if (!isRunning) {
            drawIdleBars(
                fractions = fractions,
                barWidthPx = barWidthPx,
                barGapPx = barGapPx,
                barColor = barColor,
                wavePhase = idlePhase * chartBarCount,
            )
        } else if (isCalmRunning) {
            drawCalmBars(
                fractions = fractions,
                barWidthPx = barWidthPx,
                barGapPx = barGapPx,
                barColor = barColor,
                wavePhase = calmPhase * chartBarCount,
            )
        } else {
            drawStaticBars(
                fractions = fractions,
                barWidthPx = barWidthPx,
                barGapPx = barGapPx,
                barColor = barColor,
            )
        }
    }
}

private fun isCalmTraffic(speedHistory: SpeedHistoryBuffer): Boolean {
    if (speedHistory.size <= 0 || speedHistory.samples.isEmpty()) return true
    val size = speedHistory.size.coerceAtMost(speedHistory.samples.size)
    val copyLength = minOf(size, SPEED_CHART_SAMPLE_LIMIT)
    val startOffset = size - copyLength
    val fractions =
        List(copyLength) { index ->
            TrafficChartConfig.calculateBarFraction(speedHistory.sampleAt(startOffset + index))
        }
    val minFraction = fractions.minOrNull() ?: TrafficChartConfig.MIN_VISIBLE_HEIGHT
    val maxFraction = fractions.maxOrNull() ?: TrafficChartConfig.MIN_VISIBLE_HEIGHT
    return maxFraction - minFraction <= SPEED_CHART_CALM_VARIANCE_THRESHOLD
}

internal fun buildSpeedChartFractions(
    speedHistory: SpeedHistoryBuffer,
    sampleLimit: Int = SPEED_CHART_SAMPLE_LIMIT,
): FloatArray {
    require(sampleLimit > 0) { "sampleLimit must be greater than 0" }

    val fractions = FloatArray(sampleLimit) { TrafficChartConfig.MIN_VISIBLE_HEIGHT }
    if (speedHistory.size <= 0 || speedHistory.samples.isEmpty()) return fractions

    val size = speedHistory.size.coerceAtMost(speedHistory.samples.size)
    val copyLength = minOf(size, sampleLimit)
    val sourceStart = size - copyLength
    val offset = sampleLimit - copyLength
    repeat(copyLength) { index ->
        fractions[offset + index] =
            TrafficChartConfig.calculateBarFraction(speedHistory.sampleAt(sourceStart + index))
    }
    return fractions
}

private fun SpeedHistoryBuffer.sampleAt(offsetFromOldest: Int): Long {
    if (samples.isEmpty() || size <= 0) return 0L
    val capacity = samples.size
    val normalizedSize = size.coerceAtMost(capacity)
    val oldestIndex = (head - normalizedSize + capacity) % capacity
    val wrappedIndex = (oldestIndex + offsetFromOldest).mod(capacity)
    return samples[wrappedIndex]
}

private fun DrawScope.drawStaticBars(
    fractions: FloatArray,
    barWidthPx: Float,
    barGapPx: Float,
    barColor: Color,
) {
    val barCornerRadius = createBarCornerRadius(barWidthPx)
    for (index in fractions.indices) {
        val barLeftPx = index * (barWidthPx + barGapPx)
        if (barLeftPx >= size.width || barLeftPx + barWidthPx <= 0f) {
            continue
        }
        drawChartBar(
            leftPx = barLeftPx,
            fraction = fractions[index],
            barWidthPx = barWidthPx,
            barColor = barColor,
            barCornerRadius = barCornerRadius,
        )
    }
}

private fun DrawScope.drawIdleBars(
    fractions: FloatArray,
    barWidthPx: Float,
    barGapPx: Float,
    barColor: Color,
    wavePhase: Float,
) {
    val barCornerRadius = createBarCornerRadius(barWidthPx)
    for (index in fractions.indices) {
        val barLeftPx = index * (barWidthPx + barGapPx)
        if (barLeftPx >= size.width || barLeftPx + barWidthPx <= 0f) {
            continue
        }
        drawChartBar(
            leftPx = barLeftPx,
            fraction = applyIdleWave(fraction = fractions[index], index = index, phase = wavePhase),
            barWidthPx = barWidthPx,
            barColor = barColor,
            barCornerRadius = barCornerRadius,
        )
    }
}

private fun DrawScope.drawCalmBars(
    fractions: FloatArray,
    barWidthPx: Float,
    barGapPx: Float,
    barColor: Color,
    wavePhase: Float,
) {
    val barCornerRadius = createBarCornerRadius(barWidthPx)
    val baselineHeightPx = size.height * (TrafficChartConfig.MIN_VISIBLE_HEIGHT + 0.004f)
    drawRoundRect(
        color = barColor.copy(alpha = 0.16f),
        topLeft = Offset(0f, size.height - baselineHeightPx),
        size = Size(size.width, baselineHeightPx),
        cornerRadius = CornerRadius(x = barCornerRadius.x, y = barCornerRadius.y),
    )

    for (index in fractions.indices) {
        val barLeftPx = index * (barWidthPx + barGapPx)
        if (barLeftPx >= size.width || barLeftPx + barWidthPx <= 0f) {
            continue
        }

        val animatedFraction =
            applyWave(
                fraction = fractions[index],
                index = index,
                phase = wavePhase,
                sampleLimit = SPEED_CHART_SAMPLE_LIMIT,
                span = SPEED_CHART_CALM_WAVE_SPAN,
                amplitude = SPEED_CHART_CALM_WAVE_AMPLITUDE,
            )
        val distance =
            wrappedDistance(
                index = index,
                phase = wavePhase,
                sampleLimit = SPEED_CHART_SAMPLE_LIMIT,
            )
        val highlight = (1f - distance / SPEED_CHART_CALM_WAVE_SPAN).coerceIn(0f, 1f)
        drawChartBar(
            leftPx = barLeftPx,
            fraction = animatedFraction,
            barWidthPx = barWidthPx,
            barColor = barColor.copy(alpha = SPEED_CHART_BAR_ALPHA - 0.12f + highlight * 0.14f),
            barCornerRadius = barCornerRadius,
        )
    }
}

private fun DrawScope.createBarCornerRadius(barWidthPx: Float): CornerRadius {
    val cornerRadiusPx = SpeedChartLayoutDefaults.BarCornerRadius.toPx()
    return CornerRadius(
        x = cornerRadiusPx.coerceAtMost(barWidthPx / 2f),
        y = cornerRadiusPx.coerceAtMost(size.height / 2f),
    )
}

private fun DrawScope.drawChartBar(
    leftPx: Float,
    fraction: Float,
    barWidthPx: Float,
    barColor: Color,
    barCornerRadius: CornerRadius,
) {
    val clampedFraction = fraction.coerceIn(TrafficChartConfig.MIN_VISIBLE_HEIGHT, 1f)
    val barHeightPx = size.height * clampedFraction
    drawRoundRect(
        color = barColor,
        topLeft = Offset(x = leftPx, y = size.height - barHeightPx),
        size = Size(width = barWidthPx, height = barHeightPx),
        cornerRadius = barCornerRadius,
    )
}

private fun applyIdleWave(fraction: Float, index: Int, phase: Float): Float {
    return applyWave(
        fraction = fraction,
        index = index,
        phase = phase,
        sampleLimit = SPEED_CHART_SAMPLE_LIMIT,
        span = SPEED_CHART_IDLE_WAVE_SPAN,
        amplitude = SPEED_CHART_IDLE_WAVE_AMPLITUDE,
    )
}

private fun applyWave(
    fraction: Float,
    index: Int,
    phase: Float,
    sampleLimit: Int,
    span: Float,
    amplitude: Float,
): Float {
    val normalized = (1f - wrappedDistance(index, phase, sampleLimit) / span).coerceIn(0f, 1f)
    val wave = normalized * normalized
    return fraction + wave * amplitude
}

private fun wrappedDistance(index: Int, phase: Float, sampleLimit: Int): Float {
    val distance = kotlin.math.abs(index - phase)
    return minOf(distance, distance + sampleLimit, kotlin.math.abs(index + sampleLimit - phase))
}

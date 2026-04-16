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

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.yumelira.yumebox.domain.model.ErrorCategory
import com.github.yumelira.yumebox.domain.model.ErrorImpact
import com.github.yumelira.yumebox.domain.model.ErrorPhase
import com.github.yumelira.yumebox.domain.model.ErrorRetryability
import com.github.yumelira.yumebox.domain.model.ExplanationChain
import com.github.yumelira.yumebox.domain.model.HealthCheckSeverity
import com.github.yumelira.yumebox.domain.model.HealthReport
import com.github.yumelira.yumebox.domain.model.StructuredError
import com.github.yumelira.yumebox.domain.model.StructuredLogEntry
import dev.oom_wg.purejoy.mlang.MLangStatus

data class DiagnosticBannerState(
    val headline: String,
    val subtitle: String? = null,
    val tone: SemanticTone = SemanticTone.Neutral,
    val icon: ImageVector? = null,
)

fun StructuredError.toSemanticTone(): SemanticTone {
    return when (impact) {
        ErrorImpact.None -> SemanticTone.Info
        ErrorImpact.Degraded -> SemanticTone.Warning
        ErrorImpact.FeatureUnavailable -> SemanticTone.Warning
        ErrorImpact.ServiceDown -> SemanticTone.Danger
        ErrorImpact.DataLoss -> SemanticTone.Danger
    }
}

fun ErrorPhase.toDisplayLabel(): String {
    return when (this) {
        ErrorPhase.Init -> MLangStatus.Phase.Init
        ErrorPhase.Preparing -> MLangStatus.Phase.Preparing
        ErrorPhase.Connecting -> MLangStatus.Phase.Connecting
        ErrorPhase.Running -> MLangStatus.Phase.Running
        ErrorPhase.Reloading -> MLangStatus.Phase.Reloading
        ErrorPhase.Stopping -> MLangStatus.Phase.Stopping
        ErrorPhase.Saving -> MLangStatus.Phase.Saving
        ErrorPhase.Importing -> MLangStatus.Phase.Importing
        ErrorPhase.Exporting -> MLangStatus.Phase.Exporting
        ErrorPhase.Compiling -> MLangStatus.Phase.Compiling
        ErrorPhase.Validating -> MLangStatus.Phase.Validating
    }
}

fun ErrorImpact.toDisplayLabel(): String {
    return when (this) {
        ErrorImpact.None -> MLangStatus.Impact.None
        ErrorImpact.Degraded -> MLangStatus.Impact.Degraded
        ErrorImpact.FeatureUnavailable -> MLangStatus.Impact.FeatureUnavailable
        ErrorImpact.ServiceDown -> MLangStatus.Impact.ServiceDown
        ErrorImpact.DataLoss -> MLangStatus.Impact.DataLoss
    }
}

fun ErrorRetryability.toDisplayLabel(): String {
    return when (this) {
        ErrorRetryability.Retryable -> MLangStatus.Retryability.Retryable
        ErrorRetryability.RetryableAfterAction -> MLangStatus.Retryability.RetryableAfterAction
        ErrorRetryability.NonRetryable -> MLangStatus.Retryability.NonRetryable
    }
}

fun HealthCheckSeverity.toSemanticTone(): SemanticTone {
    return when (this) {
        HealthCheckSeverity.Ok -> SemanticTone.Success
        HealthCheckSeverity.Info -> SemanticTone.Info
        HealthCheckSeverity.Warning -> SemanticTone.Warning
        HealthCheckSeverity.Error -> SemanticTone.Danger
        HealthCheckSeverity.Critical -> SemanticTone.Danger
    }
}

fun HealthReport.toBannerState(
    headline: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
): DiagnosticBannerState {
    val attentionCount = warningCount + errorCount
    return DiagnosticBannerState(
        headline = headline,
        subtitle = subtitle ?: attentionSummary(attentionCount),
        tone = overallSeverity.toSemanticTone(),
        icon = icon,
    )
}

fun StructuredError?.orFallback(
    message: String?,
    category: ErrorCategory = ErrorCategory.Unknown,
    phase: ErrorPhase = ErrorPhase.Running,
    impact: ErrorImpact = ErrorImpact.Degraded,
    retryability: ErrorRetryability = ErrorRetryability.RetryableAfterAction,
    technicalDetail: String? = null,
): StructuredError? {
    if (this != null) return this

    val visibleMessage = message?.trim().orEmpty()
    if (visibleMessage.isEmpty()) return null

    return StructuredError(
        category = category,
        phase = phase,
        impact = impact,
        retryability = retryability,
        userVisibleMessage = visibleMessage,
        technicalDetail = technicalDetail ?: visibleMessage,
    )
}

fun ExplanationChain.toTraceEntries(): List<TraceEntry> {
    return steps.mapIndexed { index, step ->
        val tone =
            when {
                !step.matched -> SemanticTone.Warning
                index == steps.lastIndex && isSuccess -> SemanticTone.Success
                else -> SemanticTone.Info
            }

        TraceEntry(
            stage = step.stage,
            label = step.label,
            detail = step.detail.orContent(step.output).orContent(step.input),
            tone = tone,
            isTerminal = index == steps.lastIndex,
        )
    }
}

fun StructuredLogEntry.toTraceEntry(): TraceEntry {
    val stage = phase.contentOr(status).contentOr(action)
    val detail =
        listOfNotNull(
                message.takeIf { it.isNotBlank() },
                errorCategory.takeIf { !it.isNullOrBlank() },
                detail.takeIf { !it.isNullOrBlank() },
            )
            .distinct()
            .joinToString(" · ")
            .takeIf { it.isNotBlank() }

    return TraceEntry(
        stage = stage,
        label = action.contentOr(status),
        detail = detail,
        tone = if (isFailure) SemanticTone.Danger else SemanticTone.Info,
        isTerminal = isFailure,
    )
}

private fun attentionSummary(attentionCount: Int): String {
    return if (attentionCount > 0) {
        MLangStatus.AttentionItems.format(attentionCount)
    } else {
        MLangStatus.NoActiveIssues
    }
}

private fun String?.orContent(fallback: String?): String? {
    return this?.takeIf { it.isNotBlank() } ?: fallback?.takeIf { it.isNotBlank() }
}

private fun String?.contentOr(fallback: String): String {
    return this?.takeIf { it.isNotBlank() } ?: fallback
}

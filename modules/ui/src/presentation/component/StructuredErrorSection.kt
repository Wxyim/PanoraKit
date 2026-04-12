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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.domain.model.ErrorImpact
import com.github.yumelira.yumebox.domain.model.ErrorRetryability
import com.github.yumelira.yumebox.domain.model.StructuredError
import com.github.yumelira.yumebox.domain.model.SuggestedAction
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Activity
import com.github.yumelira.yumebox.presentation.icon.yume.Bolt
import com.github.yumelira.yumebox.presentation.icon.yume.Cancel
import com.github.yumelira.yumebox.presentation.icon.yume.ShieldMinus
import com.github.yumelira.yumebox.presentation.icon.yume.Zap
import com.github.yumelira.yumebox.presentation.icon.yume.chevron
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Three-tier structured error display following the explainability specification:
 * - **L1 (Surface):** Conclusion first — [userVisibleMessage] shown as headline.
 * - **L2 (Middle):** Reason — phase, impact, and suggested actions shown on expand.
 * - **L3 (Advanced):** Raw trace — [technicalDetail] and [rawCause] available on demand.
 *
 * Error template: Phase → Root Cause → Impact → Suggested Action
 */
@Composable
fun StructuredErrorSection(
    error: StructuredError,
    modifier: Modifier = Modifier,
    onAction: ((SuggestedAction) -> Unit)? = null,
) {
    val tone = error.toSemanticTone()
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = true)
    val shape = RoundedCornerShape(20.dp)
    var showDetail by remember { mutableStateOf(false) }

    val icon = error.toIcon()
    val phaseLabel = error.phase.toDisplayLabel()
    val impactLabel = error.impact.toDisplayLabel()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = error.userVisibleMessage
                    stateDescription = tone.name.lowercase()
                }
                .clip(shape)
                .background(style.containerColor, shape)
                .border(AppTheme.strokes.default, style.borderColor, shape)
    ) {
        // L1: Conclusion — always visible
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .clickable { showDetail = !showDetail }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                tint = style.contentColor,
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = error.userVisibleMessage,
                    modifier = Modifier.semantics { heading() },
                    color = style.contentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!showDetail) {
                    Text(
                        text = phaseLabel + " · " + impactLabel,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Icon(
                modifier =
                    Modifier.size(18.dp).graphicsLayer {
                        rotationZ = if (showDetail) -90f else 90f
                    },
                imageVector = Yume.chevron,
                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                contentDescription = null,
            )
        }

        // L2: Reason — shown on expand
        AnimatedVisibility(visible = showDetail) {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
                        .padding(start = 36.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ErrorDetailRow(label = phaseLabel, value = impactLabel)

                if (error.isRetryable) {
                    val retryLabel =
                        when (error.retryability) {
                            ErrorRetryability.Retryable -> "Retryable"
                            ErrorRetryability.RetryableAfterAction -> "Retryable after action"
                            ErrorRetryability.NonRetryable -> "Non-retryable"
                        }
                    Text(
                        text = retryLabel,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 13.sp,
                    )
                }

                // Suggested actions
                for (action in error.suggestedActions) {
                    AppCommandButton(
                        title = action.label,
                        imageVector = Yume.Bolt,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = onAction != null,
                        onClick = { onAction?.invoke(action) },
                        tone = SemanticTone.Brand,
                    )
                }

                // L3: Technical detail — within the expanded section
                val detail = error.technicalDetail
                if (!detail.isNullOrBlank()) {
                    Text(
                        text = detail,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(text = "→", color = MiuixTheme.colorScheme.onSurfaceVariantSummary, fontSize = 13.sp)
        Text(
            text = value,
            color = MiuixTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun StructuredError.toSemanticTone(): SemanticTone {
    return when (impact) {
        ErrorImpact.None -> SemanticTone.Info
        ErrorImpact.Degraded -> SemanticTone.Warning
        ErrorImpact.FeatureUnavailable -> SemanticTone.Warning
        ErrorImpact.ServiceDown -> SemanticTone.Danger
        ErrorImpact.DataLoss -> SemanticTone.Danger
    }
}

private fun StructuredError.toIcon(): ImageVector {
    return when (impact) {
        ErrorImpact.None -> Yume.Activity
        ErrorImpact.Degraded -> Yume.Zap
        ErrorImpact.FeatureUnavailable -> Yume.Cancel
        ErrorImpact.ServiceDown -> Yume.ShieldMinus
        ErrorImpact.DataLoss -> Yume.ShieldMinus
    }
}

private fun com.github.yumelira.yumebox.domain.model.ErrorPhase.toDisplayLabel(): String {
    return when (this) {
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Init -> "Init"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Preparing -> "Preparing"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Connecting -> "Connecting"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Running -> "Running"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Reloading -> "Reloading"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Stopping -> "Stopping"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Saving -> "Saving"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Importing -> "Importing"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Exporting -> "Exporting"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Compiling -> "Compiling"
        com.github.yumelira.yumebox.domain.model.ErrorPhase.Validating -> "Validating"
    }
}

private fun ErrorImpact.toDisplayLabel(): String {
    return when (this) {
        ErrorImpact.None -> "No impact"
        ErrorImpact.Degraded -> "Degraded"
        ErrorImpact.FeatureUnavailable -> "Feature unavailable"
        ErrorImpact.ServiceDown -> "Service down"
        ErrorImpact.DataLoss -> "Data loss risk"
    }
}

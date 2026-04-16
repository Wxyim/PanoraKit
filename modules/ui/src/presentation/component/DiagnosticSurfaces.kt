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

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.nomadboxlab.monadbox.domain.model.ErrorCategory
import com.github.nomadboxlab.monadbox.domain.model.ErrorImpact
import com.github.nomadboxlab.monadbox.domain.model.ErrorPhase
import com.github.nomadboxlab.monadbox.domain.model.ErrorRetryability
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
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

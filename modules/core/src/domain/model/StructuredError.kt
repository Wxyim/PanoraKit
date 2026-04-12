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

package com.github.yumelira.yumebox.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorCategory {
    Configuration,
    Network,
    Permission,
    Runtime,
    Storage,
    Platform,
    Authentication,
    Validation,
    Unknown,
}

@Serializable
enum class ErrorPhase {
    Init,
    Preparing,
    Connecting,
    Running,
    Reloading,
    Stopping,
    Saving,
    Importing,
    Exporting,
    Compiling,
    Validating,
}

@Serializable
enum class ErrorImpact {
    None,
    Degraded,
    FeatureUnavailable,
    ServiceDown,
    DataLoss,
}

@Serializable
enum class ErrorRetryability {
    Retryable,
    RetryableAfterAction,
    NonRetryable,
}

@Serializable
data class SuggestedAction(
    val actionId: String,
    val label: String,
    val description: String? = null,
)

@Serializable
data class StructuredError(
    val category: ErrorCategory,
    val phase: ErrorPhase,
    val impact: ErrorImpact,
    val retryability: ErrorRetryability,
    val suggestedActions: List<SuggestedAction> = emptyList(),
    val rawCause: String? = null,
    val userVisibleMessage: String,
    val technicalDetail: String? = null,
    val correlationId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val isRetryable: Boolean
        get() = retryability != ErrorRetryability.NonRetryable

    val isCritical: Boolean
        get() = impact == ErrorImpact.ServiceDown || impact == ErrorImpact.DataLoss

    companion object {
        fun configuration(
            phase: ErrorPhase,
            userVisibleMessage: String,
            rawCause: String? = null,
            impact: ErrorImpact = ErrorImpact.FeatureUnavailable,
            retryability: ErrorRetryability = ErrorRetryability.RetryableAfterAction,
            suggestedActions: List<SuggestedAction> = emptyList(),
        ) =
            StructuredError(
                category = ErrorCategory.Configuration,
                phase = phase,
                impact = impact,
                retryability = retryability,
                suggestedActions = suggestedActions,
                rawCause = rawCause,
                userVisibleMessage = userVisibleMessage,
            )

        fun network(
            phase: ErrorPhase,
            userVisibleMessage: String,
            rawCause: String? = null,
            impact: ErrorImpact = ErrorImpact.Degraded,
            retryability: ErrorRetryability = ErrorRetryability.Retryable,
            suggestedActions: List<SuggestedAction> = emptyList(),
        ) =
            StructuredError(
                category = ErrorCategory.Network,
                phase = phase,
                impact = impact,
                retryability = retryability,
                suggestedActions = suggestedActions,
                rawCause = rawCause,
                userVisibleMessage = userVisibleMessage,
            )

        fun permission(
            phase: ErrorPhase,
            userVisibleMessage: String,
            rawCause: String? = null,
            suggestedActions: List<SuggestedAction> = emptyList(),
        ) =
            StructuredError(
                category = ErrorCategory.Permission,
                phase = phase,
                impact = ErrorImpact.FeatureUnavailable,
                retryability = ErrorRetryability.RetryableAfterAction,
                suggestedActions = suggestedActions,
                rawCause = rawCause,
                userVisibleMessage = userVisibleMessage,
            )

        fun runtime(
            phase: ErrorPhase,
            userVisibleMessage: String,
            rawCause: String? = null,
            impact: ErrorImpact = ErrorImpact.ServiceDown,
            retryability: ErrorRetryability = ErrorRetryability.Retryable,
            suggestedActions: List<SuggestedAction> = emptyList(),
        ) =
            StructuredError(
                category = ErrorCategory.Runtime,
                phase = phase,
                impact = impact,
                retryability = retryability,
                suggestedActions = suggestedActions,
                rawCause = rawCause,
                userVisibleMessage = userVisibleMessage,
            )

        fun fromThrowable(
            throwable: Throwable,
            phase: ErrorPhase = ErrorPhase.Running,
            category: ErrorCategory = ErrorCategory.Unknown,
            impact: ErrorImpact = ErrorImpact.Degraded,
            retryability: ErrorRetryability = ErrorRetryability.Retryable,
            userVisibleMessage: String? = null,
        ) =
            StructuredError(
                category = category,
                phase = phase,
                impact = impact,
                retryability = retryability,
                rawCause = throwable.stackTraceToString().take(512),
                userVisibleMessage =
                    userVisibleMessage
                        ?: throwable.localizedMessage
                        ?: throwable.message
                        ?: "An unexpected error occurred",
                technicalDetail = throwable.message,
            )
    }
}

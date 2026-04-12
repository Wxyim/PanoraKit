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

package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.core.controller.ControllerError
import com.github.yumelira.yumebox.domain.model.ErrorCategory
import com.github.yumelira.yumebox.domain.model.ErrorImpact
import com.github.yumelira.yumebox.domain.model.ErrorPhase
import com.github.yumelira.yumebox.domain.model.ErrorRetryability
import com.github.yumelira.yumebox.domain.model.StructuredError
import com.github.yumelira.yumebox.domain.model.SuggestedAction
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException

fun RuntimeGatewayException.toStructuredError(): StructuredError {
    val (category, phase, impact, retryability) = classifyGatewayError(code)
    return StructuredError(
        category = category,
        phase = phase,
        impact = impact,
        retryability = retryability,
        rawCause = cause?.message,
        userVisibleMessage = message ?: "Runtime error",
        technicalDetail = "${code.name}: ${message ?: ""}",
    )
}

fun ControllerError.toStructuredError(): StructuredError =
    when (this) {
        is ControllerError.Unauthorized ->
            StructuredError(
                category = ErrorCategory.Authentication,
                phase = ErrorPhase.Connecting,
                impact = ErrorImpact.FeatureUnavailable,
                retryability = ErrorRetryability.RetryableAfterAction,
                rawCause = cause?.message,
                userVisibleMessage = message ?: "Controller authentication failed",
                suggestedActions =
                    listOf(
                        SuggestedAction(
                            actionId = "check_secret",
                            label = "Check controller secret",
                        )
                    ),
            )

        is ControllerError.PortConflict ->
            StructuredError(
                category = ErrorCategory.Runtime,
                phase = ErrorPhase.Init,
                impact = ErrorImpact.ServiceDown,
                retryability = ErrorRetryability.RetryableAfterAction,
                rawCause = cause?.message,
                userVisibleMessage = message ?: "Port $port is already in use",
                suggestedActions =
                    listOf(
                        SuggestedAction(actionId = "change_port", label = "Change controller port")
                    ),
            )

        is ControllerError.Unavailable ->
            StructuredError(
                category = ErrorCategory.Network,
                phase = ErrorPhase.Connecting,
                impact = ErrorImpact.ServiceDown,
                retryability = ErrorRetryability.Retryable,
                rawCause = cause?.message,
                userVisibleMessage = message ?: "Controller is not reachable",
            )

        is ControllerError.Unknown ->
            StructuredError(
                category = ErrorCategory.Unknown,
                phase = ErrorPhase.Running,
                impact = ErrorImpact.Degraded,
                retryability = ErrorRetryability.Retryable,
                rawCause = cause?.message,
                userVisibleMessage = message ?: "Unknown controller error",
            )
    }

fun Throwable.toStructuredError(phase: ErrorPhase = ErrorPhase.Running): StructuredError =
    when (this) {
        is RuntimeGatewayException -> toStructuredError()
        is ControllerError -> toStructuredError()
        else -> StructuredError.fromThrowable(this, phase = phase)
    }

private data class GatewayErrorClassification(
    val category: ErrorCategory,
    val phase: ErrorPhase,
    val impact: ErrorImpact,
    val retryability: ErrorRetryability,
)

private fun classifyGatewayError(code: RuntimeGatewayErrorCode): GatewayErrorClassification =
    when (code) {
        RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
        RuntimeGatewayErrorCode.CLIENT_INIT_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Runtime,
                ErrorPhase.Init,
                ErrorImpact.ServiceDown,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.CLIENT_OPERATION_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Runtime,
                ErrorPhase.Running,
                ErrorImpact.Degraded,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Runtime,
                ErrorPhase.Running,
                ErrorImpact.ServiceDown,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Configuration,
                ErrorPhase.Compiling,
                ErrorImpact.FeatureUnavailable,
                ErrorRetryability.RetryableAfterAction,
            )

        RuntimeGatewayErrorCode.RUNTIME_START_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Runtime,
                ErrorPhase.Init,
                ErrorImpact.ServiceDown,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Runtime,
                ErrorPhase.Reloading,
                ErrorImpact.Degraded,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Runtime,
                ErrorPhase.Stopping,
                ErrorImpact.Degraded,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Platform,
                ErrorPhase.Init,
                ErrorImpact.ServiceDown,
                ErrorRetryability.RetryableAfterAction,
            )

        RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Platform,
                ErrorPhase.Reloading,
                ErrorImpact.Degraded,
                ErrorRetryability.Retryable,
            )

        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Configuration,
                ErrorPhase.Reloading,
                ErrorImpact.Degraded,
                ErrorRetryability.NonRetryable,
            )

        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_SNAPSHOT_MISSING ->
            GatewayErrorClassification(
                ErrorCategory.Storage,
                ErrorPhase.Preparing,
                ErrorImpact.FeatureUnavailable,
                ErrorRetryability.RetryableAfterAction,
            )

        RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Configuration,
                ErrorPhase.Compiling,
                ErrorImpact.FeatureUnavailable,
                ErrorRetryability.RetryableAfterAction,
            )

        RuntimeGatewayErrorCode.RUNTIME_CONFIG_PREVIEW_FAILED ->
            GatewayErrorClassification(
                ErrorCategory.Configuration,
                ErrorPhase.Validating,
                ErrorImpact.None,
                ErrorRetryability.Retryable,
            )
    }

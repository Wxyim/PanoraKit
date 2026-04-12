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

package com.github.yumelira.yumebox.service.runtime.session

import com.github.yumelira.yumebox.core.model.RootTunConfig
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import kotlinx.serialization.Serializable

@Serializable
data class RuntimeSpec(
    val owner: RuntimeOwner,
    val profileUuid: String,
    val profileName: String,
    val profileDir: String,
    val runtimeConfigPath: String = "",
    val overridePaths: List<String> = emptyList(),
    val rootTunConfig: RootTunConfig? = null,
    val staticPlanFingerprint: String = "",
    val transportFingerprint: String = "",
    val effectiveFingerprint: String = "",
    val profileFingerprint: String = "",
)

@Serializable
enum class RuntimeFailureCategory {
    Configuration,
    Permission,
    Network,
    Runtime,
    SystemIntegration,
    Security,
    Unknown,
}

@Serializable
enum class RuntimeFailurePhase {
    Preparing,
    Permission,
    ConfigurationCompile,
    RuntimeStart,
    RuntimeReload,
    RuntimeStop,
    RootTunnel,
    Unknown,
}

@Serializable
enum class RuntimeFailureImpact {
    RuntimeUnavailable,
    ConfigurationNotApplied,
    TrafficInterrupted,
    DiagnosticsOnly,
}

@Serializable
enum class RuntimeFailureRetryability {
    Retryable,
    UserActionRequired,
    NotRetryable,
    Unknown,
}

@Serializable
data class RuntimeFailure(
    val code: RuntimeGatewayErrorCode,
    val message: String,
    val category: RuntimeFailureCategory = code.defaultCategory(),
    val phase: RuntimeFailurePhase = code.defaultPhase(),
    val impact: RuntimeFailureImpact = code.defaultImpact(),
    val retryability: RuntimeFailureRetryability = code.defaultRetryability(),
    val suggestedAction: String = code.defaultSuggestedAction(),
    val rawCause: String = message,
    val userMessage: String = message,
)

@Serializable
data class RuntimeOperationResult(
    val success: Boolean,
    val errorCode: RuntimeGatewayErrorCode? = null,
    val error: String? = null,
) {
    fun messageOr(defaultMessage: String): String {
        return error?.takeIf { it.isNotBlank() } ?: defaultMessage
    }

    fun codeOr(defaultCode: RuntimeGatewayErrorCode): RuntimeGatewayErrorCode {
        return errorCode ?: defaultCode
    }

    fun toException(
        defaultCode: RuntimeGatewayErrorCode,
        defaultMessage: String,
    ): RuntimeGatewayException {
        return RuntimeGatewayException(
            code = codeOr(defaultCode),
            message = messageOr(defaultMessage),
        )
    }

    companion object {
        fun ok(): RuntimeOperationResult = RuntimeOperationResult(success = true)

        fun fail(failure: RuntimeFailure): RuntimeOperationResult {
            return RuntimeOperationResult(
                success = false,
                errorCode = failure.code,
                error = failure.message,
            )
        }
    }
}

internal fun Throwable.toRuntimeFailure(
    fallbackCode: RuntimeGatewayErrorCode,
    fallbackMessage: String,
): RuntimeFailure {
    val runtimeError = this as? RuntimeGatewayException
    val code = runtimeError?.code ?: fallbackCode
    val message =
        runtimeError?.message?.takeIf { it.isNotBlank() }
            ?: this.toDiagnosticMessage(fallbackMessage)
    return RuntimeFailure(
        code = code,
        message = message,
        rawCause = rootCause().toDiagnosticMessage(fallbackMessage),
        userMessage = message,
    )
}

internal fun Throwable.toDiagnosticMessage(fallbackMessage: String): String {
    val root = rootCause()
    val rootType = root::class.java.name
    val rootMessage = root.message?.trim().orEmpty()
    val localMessage = message?.trim().orEmpty()

    val preferred =
        when {
            localMessage.isNotBlank() && localMessage != this::class.java.name -> localMessage
            rootMessage.isNotBlank() && rootMessage != rootType -> rootMessage
            else -> fallbackMessage
        }

    val normalized = if (preferred.contains(":")) preferred else "$rootType: $preferred"
    return normalized.take(512)
}

private fun Throwable.rootCause(): Throwable {
    var current: Throwable = this
    while (current.cause != null && current.cause !== current) {
        current = current.cause!!
    }
    return current
}

fun RuntimeFailure.runtimeGatewayMessage(defaultMessage: String): String {
    val readableMessage =
        userMessage.takeIf { it.isNotBlank() }
            ?: message.takeIf { it.isNotBlank() }
            ?: defaultMessage
    return "${code.name}: $readableMessage"
}

private fun RuntimeGatewayErrorCode.defaultCategory(): RuntimeFailureCategory =
    when (this) {
        RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_PREVIEW_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_SNAPSHOT_MISSING ->
            RuntimeFailureCategory.Configuration
        RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
        RuntimeGatewayErrorCode.CLIENT_INIT_FAILED,
        RuntimeGatewayErrorCode.CLIENT_OPERATION_FAILED -> RuntimeFailureCategory.SystemIntegration
        RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED ->
            RuntimeFailureCategory.SystemIntegration
        RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED -> RuntimeFailureCategory.Runtime
    }

private fun RuntimeGatewayErrorCode.defaultPhase(): RuntimeFailurePhase =
    when (this) {
        RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED -> RuntimeFailurePhase.Preparing
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_PREVIEW_FAILED ->
            RuntimeFailurePhase.ConfigurationCompile
        RuntimeGatewayErrorCode.RUNTIME_START_FAILED -> RuntimeFailurePhase.RuntimeStart
        RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED -> RuntimeFailurePhase.RuntimeReload
        RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED -> RuntimeFailurePhase.RuntimeStop
        RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_SNAPSHOT_MISSING,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED -> RuntimeFailurePhase.RootTunnel
        RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
        RuntimeGatewayErrorCode.CLIENT_INIT_FAILED,
        RuntimeGatewayErrorCode.CLIENT_OPERATION_FAILED -> RuntimeFailurePhase.Unknown
    }

private fun RuntimeGatewayErrorCode.defaultImpact(): RuntimeFailureImpact =
    when (this) {
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_PREVIEW_FAILED ->
            RuntimeFailureImpact.DiagnosticsOnly
        RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_SNAPSHOT_MISSING ->
            RuntimeFailureImpact.ConfigurationNotApplied
        RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED -> RuntimeFailureImpact.TrafficInterrupted
        else -> RuntimeFailureImpact.RuntimeUnavailable
    }

private fun RuntimeGatewayErrorCode.defaultRetryability(): RuntimeFailureRetryability =
    when (this) {
        RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_CONFIG_PREVIEW_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_SNAPSHOT_MISSING ->
            RuntimeFailureRetryability.UserActionRequired
        RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
        RuntimeGatewayErrorCode.CLIENT_INIT_FAILED,
        RuntimeGatewayErrorCode.CLIENT_OPERATION_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED,
        RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
        RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED,
        RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED ->
            RuntimeFailureRetryability.Retryable
    }

private fun RuntimeGatewayErrorCode.defaultSuggestedAction(): String =
    when (defaultRetryability()) {
        RuntimeFailureRetryability.UserActionRequired ->
            "Review the active profile and override configuration, then apply again."
        RuntimeFailureRetryability.Retryable ->
            "Retry the operation. If it fails again, export diagnostics with sanitized logs."
        RuntimeFailureRetryability.NotRetryable -> "Change the configuration before trying again."
        RuntimeFailureRetryability.Unknown ->
            "Check diagnostics and retry after the runtime state is stable."
    }

@Serializable
data class RuntimeLogChunk(val nextSeq: Long = 0L, val items: List<String> = emptyList())

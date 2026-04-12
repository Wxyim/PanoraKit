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

import androidx.compose.runtime.Composable
import com.github.yumelira.yumebox.common.util.ToastDialogBridge
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private enum class RuntimeFailureCategory {
    Syntax,
    Remote,
    Network,
    Permission,
    Profile,
    RuntimeService,
    RuntimeControl,
    Environment,
    Unknown,
}

private data class RuntimeFailureDialogContent(
    val title: String,
    val detail: String,
)

private data class RuntimeFailureDialogSignal(
    val phase: RuntimePhase,
    val lastError: String?,
    val generation: Long,
    val targetMode: ProxyMode,
)

object RuntimeFailureDialogPresenter {
    fun showStartFailure(reason: String, targetMode: ProxyMode) {
        val content = buildStartFailureContent(reason, targetMode) ?: return
        ToastDialogBridge.show(message = content.detail, title = content.title)
    }

    fun showRuntimeFailure(reason: String, targetMode: ProxyMode) {
        val content = buildRuntimeFailureContent(reason, targetMode) ?: return
        ToastDialogBridge.show(message = content.detail, title = content.title)
    }

    private fun buildRuntimeFailureContent(
        reason: String,
        targetMode: ProxyMode,
    ): RuntimeFailureDialogContent? {
        if (reason.isBlank()) return null
        return if (targetMode == ProxyMode.Tun) {
            buildStartFailureContent(reason, targetMode)
        } else {
            RuntimeFailureDialogContent(
                title = MLang.Component.Message.Error,
                detail = reason,
            )
        }
    }

    private fun buildStartFailureContent(
        reason: String,
        targetMode: ProxyMode,
    ): RuntimeFailureDialogContent? {
        if (reason.isBlank()) return null
        val detail =
            if (targetMode == ProxyMode.Tun) {
                buildStructuredFailureReason(reason)
            } else {
                MLang.Home.Message.StartFailed.format(reason)
            }
        return RuntimeFailureDialogContent(
            title = MLang.Home.Message.StartFailedDialogTitle,
            detail = detail,
        )
    }

    private fun buildStructuredFailureReason(reason: String): String {
        val category = classify(reason)
        return when (category) {
            RuntimeFailureCategory.Syntax ->
                MLang.Home.Message.StartFailedSyntaxReason.format(reason)

            RuntimeFailureCategory.Remote ->
                MLang.Home.Message.StartFailedRemoteReason.format(reason)

            RuntimeFailureCategory.Network ->
                MLang.Home.Message.StartFailedNetworkReason.format(reason)

            RuntimeFailureCategory.Permission ->
                MLang.Home.Message.StartFailedPermissionReason.format(reason)

            RuntimeFailureCategory.Profile ->
                MLang.Home.Message.StartFailedProfileReason.format(reason)

            RuntimeFailureCategory.RuntimeService ->
                MLang.Home.Message.StartFailedRuntimeServiceReason.format(reason)

            RuntimeFailureCategory.RuntimeControl ->
                MLang.Home.Message.StartFailedRuntimeControlReason.format(reason)

            RuntimeFailureCategory.Environment ->
                MLang.Home.Message.StartFailedEnvironmentReason.format(reason)

            RuntimeFailureCategory.Unknown ->
                MLang.Home.Message.StartFailedUnknownReason.format(reason)
        }
    }

    private fun classify(reason: String): RuntimeFailureCategory {
        val normalized = reason.lowercase()

        fun containsAny(vararg tokens: String): Boolean {
            return tokens.any { token -> normalized.contains(token) }
        }

        val code =
            RuntimeGatewayErrorCode.entries.firstOrNull { gatewayCode ->
                normalized.startsWith(gatewayCode.name.lowercase())
            }

        if (
            containsAny(
                "provider fetch failed",
                "subscription",
                "fetch configuration",
                "fetch providers",
                "remote resource",
                "download",
            )
        ) {
            return RuntimeFailureCategory.Remote
        }

        if (containsAny("yaml", "syntax", "parse", "decode", "line", "column")) {
            return RuntimeFailureCategory.Syntax
        }

        if (containsAny("unauthorized", "permission", "denied", "forbidden", "access")) {
            return RuntimeFailureCategory.Permission
        }

        if (
            containsAny(
                "timeout",
                "timed out",
                "connection reset",
                "refused",
                "unreachable",
                "network",
                "dns",
                "tls",
                "ssl",
                "certificate",
                "host",
            )
        ) {
            return RuntimeFailureCategory.Network
        }

        if (containsAny("profile", "not found", "no profile", "active profile")) {
            return RuntimeFailureCategory.Profile
        }

        if (
            containsAny(
                "port",
                "address already in use",
                "bind",
                "device",
                "tun",
                "vpn",
                "resource busy",
                "not supported",
            )
        ) {
            return RuntimeFailureCategory.Environment
        }

        return when (code) {
            RuntimeGatewayErrorCode.RUNTIME_CONFIG_COMPILE_FAILED,
            RuntimeGatewayErrorCode.RUNTIME_CONFIG_PREVIEW_FAILED,
            -> RuntimeFailureCategory.Syntax

            RuntimeGatewayErrorCode.CLIENT_NOT_CONNECTED,
            RuntimeGatewayErrorCode.CLIENT_INIT_FAILED,
            RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
            RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED,
            -> RuntimeFailureCategory.RuntimeService

            RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
            RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
            RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED,
            RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED,
            RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
            RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED,
            RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED,
            RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_SNAPSHOT_MISSING,
            -> RuntimeFailureCategory.RuntimeControl

            RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED -> RuntimeFailureCategory.Profile
            RuntimeGatewayErrorCode.CLIENT_OPERATION_FAILED -> RuntimeFailureCategory.Unknown
            null -> RuntimeFailureCategory.Unknown
        }
    }
}

object GlobalDialogPresenter {
    fun showError(message: String, title: String = MLang.Component.Message.Error) {
        if (message.isBlank()) return
        ToastDialogBridge.show(message = message, title = title)
    }
}

@Composable
fun RuntimeFailureDialogEffect(runtimeSnapshot: StateFlow<RuntimeSnapshot>) {
    CollectFlowWithLifecycle(
        flow =
            runtimeSnapshot
                .map { snapshot ->
                    RuntimeFailureDialogSignal(
                        phase = snapshot.phase,
                        lastError = snapshot.lastError,
                        generation = snapshot.generation,
                        targetMode = snapshot.targetMode,
                    )
                }
                .distinctUntilChanged(),
    ) { signal ->
        val lastError = signal.lastError
        if (signal.phase == RuntimePhase.Failed && !lastError.isNullOrBlank()) {
            RuntimeFailureDialogPresenter.showRuntimeFailure(
                reason = lastError,
                targetMode = signal.targetMode,
            )
        }
    }
}
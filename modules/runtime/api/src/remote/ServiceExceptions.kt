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

package com.github.nomadboxlab.monadbox.remote

import android.content.Intent

class VpnPermissionRequired(val intent: Intent) : Exception("VPN permission required")

enum class RuntimeGatewayErrorCode {
    CLIENT_NOT_CONNECTED,
    CLIENT_INIT_FAILED,
    CLIENT_OPERATION_FAILED,
    ROOT_RUNTIME_DISCONNECTED,
    ROOT_RUNTIME_QUERY_FAILED,
    RUNTIME_SPEC_BUILD_FAILED,
    RUNTIME_START_FAILED,
    RUNTIME_RELOAD_FAILED,
    RUNTIME_RESTART_FAILED,
    RUNTIME_STOP_FAILED,
    ROOT_TUN_START_FAILED,
    ROOT_TUN_RELOAD_FAILED,
    ROOT_TUN_CONFIG_ROLLBACK_FAILED,
    ROOT_TUN_CONFIG_SNAPSHOT_MISSING,
    RUNTIME_CONFIG_COMPILE_FAILED,
    RUNTIME_CONFIG_PREVIEW_FAILED,
}

class RuntimeGatewayException(
    val code: RuntimeGatewayErrorCode,
    message: String,
    cause: Throwable? = null,
) : IllegalStateException(message, cause)

fun Throwable.runtimeGatewayMessage(defaultMessage: String): String {
    return when (this) {
        is RuntimeGatewayException -> {
            val detail = message?.takeIf { it.isNotBlank() } ?: defaultMessage
            "${code.name}: $detail"
        }

        else -> message?.takeIf { it.isNotBlank() } ?: defaultMessage
    }
}

fun Throwable.asRuntimeGatewayException(
    code: RuntimeGatewayErrorCode,
    defaultMessage: String,
): RuntimeGatewayException {
    return when (this) {
        is RuntimeGatewayException -> this
        else ->
            RuntimeGatewayException(
                code = code,
                message = runtimeGatewayMessage(defaultMessage),
                cause = this,
            )
    }
}

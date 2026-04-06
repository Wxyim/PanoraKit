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

@Serializable data class RuntimeFailure(val code: RuntimeGatewayErrorCode, val message: String)

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
            ?: this.message?.takeIf { it.isNotBlank() }
            ?: fallbackMessage
    return RuntimeFailure(code = code, message = message)
}

@Serializable
data class RuntimeLogChunk(val nextSeq: Long = 0L, val items: List<String> = emptyList())

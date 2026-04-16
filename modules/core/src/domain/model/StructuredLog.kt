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

package com.github.nomadboxlab.monadbox.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class LogLevel {
    UserVisible,
    Operational,
    Diagnostic,
    Security,
    Failure,
}

@Serializable
data class StructuredLogEntry(
    val timestamp: Long,
    val level: LogLevel,
    val objectId: String? = null,
    val action: String,
    val phase: String? = null,
    val status: String,
    val correlationId: String? = null,
    val configVersion: String? = null,
    val errorCategory: String? = null,
    val message: String,
    val detail: String? = null,
) {
    val isUserVisible: Boolean
        get() = level == LogLevel.UserVisible || level == LogLevel.Failure

    val isFailure: Boolean
        get() = level == LogLevel.Failure

    companion object {
        fun userVisible(action: String, status: String, message: String, objectId: String? = null) =
            StructuredLogEntry(
                timestamp = System.currentTimeMillis(),
                level = LogLevel.UserVisible,
                objectId = objectId,
                action = action,
                status = status,
                message = message,
            )

        fun operational(
            action: String,
            status: String,
            message: String,
            objectId: String? = null,
            phase: String? = null,
            correlationId: String? = null,
        ) =
            StructuredLogEntry(
                timestamp = System.currentTimeMillis(),
                level = LogLevel.Operational,
                objectId = objectId,
                action = action,
                phase = phase,
                status = status,
                correlationId = correlationId,
                message = message,
            )

        fun failure(
            action: String,
            message: String,
            objectId: String? = null,
            phase: String? = null,
            errorCategory: String? = null,
            detail: String? = null,
        ) =
            StructuredLogEntry(
                timestamp = System.currentTimeMillis(),
                level = LogLevel.Failure,
                objectId = objectId,
                action = action,
                phase = phase,
                status = "failed",
                errorCategory = errorCategory,
                message = message,
                detail = detail,
            )
    }
}

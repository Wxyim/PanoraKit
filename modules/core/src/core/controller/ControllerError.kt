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

package com.github.yumelira.yumebox.core.controller

/**
 * Typed error hierarchy for Mihomo Controller API failures. Shared across modules so feature layers
 * can react to specific error conditions.
 */
sealed class ControllerError(message: String, cause: Throwable? = null) :
    Exception(message, cause) {
    /** Controller returned HTTP 401 — secret mismatch. */
    class Unauthorized(cause: Throwable? = null) :
        ControllerError("Controller API returned 401 Unauthorized", cause)

    /** The configured controller port is already bound by another process. */
    class PortConflict(val port: Int, cause: Throwable? = null) :
        ControllerError("Port $port is already in use", cause)

    /** Controller API is not reachable (connection refused, timeout, etc.). */
    class Unavailable(cause: Throwable? = null) :
        ControllerError("Controller API is not reachable", cause)

    /** Any other controller failure with a descriptive message. */
    class Unknown(message: String, cause: Throwable? = null) : ControllerError(message, cause)
}

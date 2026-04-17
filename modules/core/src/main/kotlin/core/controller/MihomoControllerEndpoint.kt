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
 */

package com.github.nomadboxlab.monadbox.core.controller

import com.github.nomadboxlab.monadbox.core.model.UiConfiguration

object MihomoControllerEndpoint {
    data class Diagnostics(
        val expectedPort: Int? = null,
        val expectedSecret: String? = null,
        val runtimeControllerUrl: String? = null,
        val runtimeSecret: String = "",
        val runtimeConfigSource: String = "",
        val runtimeConfigPath: String = "",
    ) {
        val runtimePort: Int? = runtimeControllerUrl?.substringAfterLast(':', "")?.toIntOrNull()

        fun configsUrl(
            fallbackPort: Int? = expectedPort,
            fallbackHost: String = "127.0.0.1",
        ): String? {
            val controllerUrl =
                runtimeControllerUrl
                    ?: fallbackPort?.let { buildControllerUrl(port = it, host = fallbackHost) }
            return controllerUrl?.let { "$it/configs" }
        }

        fun summary(): String {
            return buildString {
                append("controller=")
                append(runtimeControllerUrl ?: "unavailable")
                append(" secretLength=")
                append(runtimeSecret.length)
                runtimeConfigSource
                    .takeIf { it.isNotBlank() }
                    ?.let {
                        append(" source=")
                        append(it)
                    }
                runtimeConfigPath
                    .takeIf { it.isNotBlank() }
                    ?.let {
                        append(" path=")
                        append(it)
                    }
                expectedPort?.let {
                    append(" expectedPort=")
                    append(it)
                }
                expectedSecret?.let {
                    append(" expectedSecretLength=")
                    append(it.length)
                }
            }
        }
    }

    fun bearerAuthorization(secret: String): String {
        return "Bearer $secret"
    }

    fun buildControllerUrl(port: Int, host: String = "127.0.0.1", secure: Boolean = false): String {
        val scheme = if (secure) "https" else "http"
        return "$scheme://$host:$port"
    }

    fun resolveControllerUrl(configuration: UiConfiguration): String? {
        val httpAddress =
            configuration.externalController
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.let { normalizeControllerAddress(it, secure = false) }
        if (httpAddress != null) return httpAddress

        return configuration.externalControllerTls
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { normalizeControllerAddress(it, secure = true) }
    }

    fun diagnostics(
        configuration: UiConfiguration?,
        expectedPort: Int? = null,
        expectedSecret: String? = null,
    ): Diagnostics {
        return Diagnostics(
            expectedPort = expectedPort,
            expectedSecret = normalizeSecret(expectedSecret),
            runtimeControllerUrl = configuration?.let(::resolveControllerUrl),
            runtimeSecret = normalizeSecret(configuration?.secret),
            runtimeConfigSource = configuration?.configSource?.trim().orEmpty(),
            runtimeConfigPath = configuration?.configPath?.trim().orEmpty(),
        )
    }

    private fun normalizeControllerAddress(address: String, secure: Boolean): String {
        if (
            address.startsWith("http://", ignoreCase = true) ||
                address.startsWith("https://", ignoreCase = true)
        ) {
            return address.trimEnd('/')
        }

        val normalizedHost =
            address
                .substringBeforeLast(":", missingDelimiterValue = address)
                .ifBlank { "127.0.0.1" }
                .let { host ->
                    when (host) {
                        "0.0.0.0",
                        "::",
                        "*" -> "127.0.0.1"
                        else -> host
                    }
                }
        val port = address.substringAfterLast(":", missingDelimiterValue = "")
        val scheme = if (secure) "https" else "http"
        return if (port.isNotBlank()) "$scheme://$normalizedHost:$port"
        else "$scheme://$normalizedHost"
    }

    private fun extractControllerPort(configuration: UiConfiguration?): Int? {
        val raw =
            configuration?.externalController?.trim()?.takeIf { it.isNotEmpty() }
                ?: configuration?.externalControllerTls?.trim().orEmpty()
        if (raw.isEmpty()) return null
        return raw.substringAfterLast(':', "").toIntOrNull()
    }

    private fun normalizeSecret(secret: String?): String {
        return secret?.trim().orEmpty()
    }
}

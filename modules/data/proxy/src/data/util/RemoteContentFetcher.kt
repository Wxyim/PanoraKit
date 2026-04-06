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

package com.github.yumelira.yumebox.data.util

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

enum class RemoteFetchFailureReason {
    InvalidScheme,
    HttpsRequired,
    HttpError,
    RedirectMissingLocation,
    TooManyRedirects,
    ContentTooLarge,
}

class RemoteFetchException(
    val reason: RemoteFetchFailureReason,
    val httpCode: Int? = null,
    val maxMegabytes: Int? = null,
) : IllegalStateException(buildMessage(reason, httpCode, maxMegabytes))

object RemoteContentFetcher {
    private const val MAX_REDIRECTS = 5

    fun fetchText(
        urlString: String,
        maxBytes: Int,
        allowInsecureHttpNonLocalhost: Boolean = false,
        userAgent: String = "MonadBox",
    ): String {
        var targetUrl =
            parseAndValidate(
                urlString = urlString,
                allowInsecureHttpNonLocalhost = allowInsecureHttpNonLocalhost,
            )
        val maxMegabytes = (maxBytes / (1024 * 1024)).coerceAtLeast(1)

        repeat(MAX_REDIRECTS + 1) { redirectCount ->
            val connection =
                (targetUrl.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15_000
                    readTimeout = 30_000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", userAgent)
                    instanceFollowRedirects = false
                }

            try {
                connection.connect()
                val statusCode = connection.responseCode

                when {
                    statusCode in 200..299 -> {
                        return connection.inputStream.use { input ->
                            input
                                .readBytesWithLimit(maxBytes, maxMegabytes)
                                .toString(Charsets.UTF_8)
                        }
                    }

                    statusCode in 300..399 -> {
                        if (redirectCount >= MAX_REDIRECTS) {
                            throw RemoteFetchException(RemoteFetchFailureReason.TooManyRedirects)
                        }
                        val location =
                            connection
                                .getHeaderField("Location")
                                ?.trim()
                                ?.takeIf(String::isNotBlank)
                                ?: throw RemoteFetchException(
                                    RemoteFetchFailureReason.RedirectMissingLocation
                                )
                        targetUrl = URL(targetUrl, location)
                        validateUrl(
                            url = targetUrl,
                            allowInsecureHttpNonLocalhost = allowInsecureHttpNonLocalhost,
                        )
                    }

                    else ->
                        throw RemoteFetchException(
                            reason = RemoteFetchFailureReason.HttpError,
                            httpCode = statusCode,
                        )
                }
            } finally {
                connection.disconnect()
            }
        }

        throw RemoteFetchException(RemoteFetchFailureReason.TooManyRedirects)
    }

    fun extractDecodedFileName(urlString: String): String? {
        val pathSegment =
            runCatching { URL(urlString.trim()).path.substringAfterLast('/') }
                .getOrDefault(urlString.substringBefore('?').substringAfterLast('/'))

        val normalized = pathSegment.trim()
        if (normalized.isBlank()) return null

        // URLDecoder follows application/x-www-form-urlencoded semantics,
        // so keep literal plus signs intact for path segments.
        val decoded =
            runCatching { URLDecoder.decode(normalized.replace("+", "%2B"), Charsets.UTF_8.name()) }
                .getOrDefault(normalized)
        return decoded.trim().takeIf(String::isNotBlank)
    }

    private fun parseAndValidate(urlString: String, allowInsecureHttpNonLocalhost: Boolean): URL {
        val normalized = urlString.trim()
        val parsed = URL(normalized)
        validateUrl(url = parsed, allowInsecureHttpNonLocalhost = allowInsecureHttpNonLocalhost)
        return parsed
    }

    private fun validateUrl(url: URL, allowInsecureHttpNonLocalhost: Boolean) {
        val protocol = url.protocol.lowercase()
        if (protocol != "https" && protocol != "http") {
            throw RemoteFetchException(RemoteFetchFailureReason.InvalidScheme)
        }

        if (
            protocol == "http" && !allowInsecureHttpNonLocalhost && !isAllowedInsecureHost(url.host)
        ) {
            throw RemoteFetchException(RemoteFetchFailureReason.HttpsRequired)
        }
    }

    private fun isAllowedInsecureHost(host: String?): Boolean {
        val normalized = host?.lowercase()?.trim().orEmpty()
        return normalized == "localhost" || normalized == "127.0.0.1" || normalized == "::1"
    }

    private fun java.io.InputStream.readBytesWithLimit(
        maxBytes: Int,
        maxMegabytes: Int,
    ): ByteArray {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val output = ByteArrayOutputStream()
        var total = 0
        while (true) {
            val read = read(buffer)
            if (read <= 0) break
            total += read
            if (total > maxBytes) {
                throw RemoteFetchException(
                    reason = RemoteFetchFailureReason.ContentTooLarge,
                    maxMegabytes = maxMegabytes,
                )
            }
            output.write(buffer, 0, read)
        }
        return output.toByteArray()
    }
}

private fun buildMessage(
    reason: RemoteFetchFailureReason,
    httpCode: Int?,
    maxMegabytes: Int?,
): String {
    return when (reason) {
        RemoteFetchFailureReason.InvalidScheme -> "Only http:// or https:// URLs are supported"
        RemoteFetchFailureReason.HttpsRequired ->
            "Only https:// URLs are allowed for remote resources (except localhost)"
        RemoteFetchFailureReason.HttpError -> "Request failed: HTTP ${httpCode ?: -1}"
        RemoteFetchFailureReason.RedirectMissingLocation ->
            "Redirect response is missing Location header"
        RemoteFetchFailureReason.TooManyRedirects -> "Too many redirects"
        RemoteFetchFailureReason.ContentTooLarge ->
            "Remote content exceeds size limit: ${maxMegabytes ?: 0}MB"
    }
}

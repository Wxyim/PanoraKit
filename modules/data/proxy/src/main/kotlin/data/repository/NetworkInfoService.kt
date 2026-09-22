/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.github.nomadboxlab.monadbox.core.util.NetworkInterfaces
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber

private const val ACCESS_LOCAL_NETWORK_PERMISSION = "android.permission.ACCESS_LOCAL_NETWORK"

/**
 * Android 17 (target SDK 37+) restricts local network discovery through
 * [NetworkInterface.getNetworkInterfaces] behind ACCESS_LOCAL_NETWORK. On older
 * platforms the permission does not exist and the query is always allowed.
 */
private const val ACCESS_LOCAL_NETWORK_SDK_INT = 37

@Serializable
data class IpInfo(val ip: String, @SerialName("country_code") val countryCode: String? = null)

sealed class IpMonitoringState {
    data class Success(
        val localIp: String?,
        val externalIp: IpInfo?,
        val isProxyActive: Boolean = false,
    ) : IpMonitoringState()

    data class Error(val message: String) : IpMonitoringState()

    object Loading : IpMonitoringState()
}

class NetworkInfoService(
    private val context: Context,
    private val appSettings: AppSettingsStorage,
) : Closeable {
    private val json = Json { ignoreUnknownKeys = true }

    // This service is application-scoped. Keep the manually queried value here
    // so recreating the home destination does not discard it while the VPN is active.
    private val externalIpCache = MutableStateFlow<IpInfo?>(null)
    val externalIp: StateFlow<IpInfo?> = externalIpCache.asStateFlow()

    private fun newLookupClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .callTimeout(5, TimeUnit.SECONDS)
            .build()

    private val _refreshTrigger =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    // Lookup clients are created per request and closed after each query, so
    // there is no shared connection pool to tear down here.
    override fun close() = Unit

    fun triggerRefresh() {
        _refreshTrigger.tryEmit(Unit)
    }

    fun cacheExternalIp(value: IpInfo?) {
        externalIpCache.value = value
    }

    fun clearExternalIp() {
        externalIpCache.value = null
    }

    suspend fun getLocalIp(): String? {
        if (!hasLocalNetworkAccess()) {
            Timber.w("ACCESS_LOCAL_NETWORK denied; local IP discovery degraded")
            return null
        }
        return NetworkInterfaces.getLocalIpAddress()
    }

    private fun hasLocalNetworkAccess(): Boolean {
        if (Build.VERSION.SDK_INT < ACCESS_LOCAL_NETWORK_SDK_INT) {
            return true
        }
        return context.checkSelfPermission(ACCESS_LOCAL_NETWORK_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED
    }

    /**
     * Look up the external IP from a user-configured URL.
     *
     * Privacy: this is **never** called automatically. The home screen has a "查询" button that calls
     * this only on user tap. The URL is whatever the user has set in `Settings -> Network -> 外部 IP
     * 查询 URL`. An empty URL means the feature is disabled.
     *
     * Returns `null` if the URL is empty/invalid or the request fails.
     */
    suspend fun queryExternalIp(): IpInfo? {
        val url = appSettings.externalIpLookupUrl.value.trim()
        if (url.isEmpty()) return null
        if (!isAllowedExternalIpLookupUrl(url)) return null
        return withContext(Dispatchers.IO) {
            // Each query uses a brand-new OkHttpClient whose connection pool
            // starts empty, so the request always opens a fresh TCP/TLS
            // connection that follows the current routing mode. The Android
            // HttpURLConnection engine that Ktor wraps keeps a process-wide
            // keep-alive pool, so reusing it after a Rule <-> Direct switch
            // would replay the old egress IP.
            val client = newLookupClient()
            try {
                val request = Request.Builder().url(url).header("Connection", "close").build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        null
                    } else {
                        val body = response.body.string().trim().orEmpty()
                        ExternalIpResponseParser.parse(body = body, json = json)
                    }
                }
            } catch (e: Exception) {
                Timber.w(e, "Failed to query external IP")
                null
            } finally {
                // OkHttp 5 removed OkHttpClient.close(); release the per-request
                // dispatcher thread pool and connection pool directly instead.
                client.dispatcher.executorService.shutdown()
                client.connectionPool.evictAll()
            }
        }
    }

    /**
     * Stream the local IP and (optionally) a previously-queried external IP.
     *
     * Privacy: this flow **never** calls any third-party endpoint. The external IP is only ever
     * populated by a manual `queryExternalIp()` invocation from the home screen, gated on a
     * non-empty user-configured URL.
     */
    fun startIpMonitoring(
        isProxyActiveFlow: Flow<Boolean>,
        externalIpFlow: Flow<IpInfo?> = flowOf(null),
    ): Flow<IpMonitoringState> =
        combine(externalIpFlow, isProxyActiveFlow) { externalIp, isProxyActive ->
            try {
                val localIp = getLocalIp()
                IpMonitoringState.Success(
                    localIp = localIp,
                    externalIp = externalIp,
                    isProxyActive = isProxyActive,
                )
            } catch (e: Exception) {
                IpMonitoringState.Error(e.message ?: "Unknown error")
            }
        }
}

/**
 * Single source of truth for which external IP lookup URLs are accepted.
 *
 * HTTPS is allowed for any host. Cleartext HTTP is allowed only for loopback
 * so power users can self-host an on-device lookup endpoint. Settings screens
 * use the same rule when validating user input (allowing an unchanged legacy
 * value to be re-saved), so a brand-new URL is never silently rejected later at
 * query time.
 */
fun isAllowedExternalIpLookupUrl(url: String): Boolean {
    val lower = url.lowercase()
    val isHttps =
        when {
            lower.startsWith("https://") -> true
            lower.startsWith("http://") -> false
            else -> return false
        }
    val authority = lower.substringAfter("://").substringBefore('/')
    val host = extractUrlHost(authority)
    if (host.isBlank()) {
        return false
    }
    if (isHttps) {
        return true
    }
    // Allow cleartext only to loopback so power users can self-host on-device.
    return host == "127.0.0.1" || host == "localhost" || host == "::1"
}

/**
 * Extracts the host from a URL authority (the part between the scheme and the
 * first path separator), ignoring userinfo (e.g. `user:pass@` in
 * `https://user:pass@example.com/`) and bracketed IPv6 literals.
 */
private fun extractUrlHost(authority: String): String {
    // Strip userinfo: the host starts after the last '@'.
    val withoutUserInfo = authority.substringAfterLast('@', authority)
    // Bracketed IPv6 literal, e.g. [::1].
    if (withoutUserInfo.startsWith("[")) {
        return withoutUserInfo.substringAfter('[').substringBefore(']')
    }
    return withoutUserInfo.substringBefore(':')
}

internal object ExternalIpResponseParser {
    fun parse(body: String, json: Json): IpInfo? {
        val normalizedBody = body.trim()
        if (normalizedBody.isEmpty()) return null

        try {
            return json.decodeFromString<IpInfo>(normalizedBody)
        } catch (error: Exception) {
            // Fall through to plain-text and key=value response parsing.
            Timber.d(error, "External IP response is not JSON; continuing with fallback parsing")
        }

        parseKeyValueTrace(normalizedBody)?.let {
            return it
        }
        return normalizedBody.takeIf(::isLikelyIpLiteral)?.let { IpInfo(ip = it) }
    }

    private fun parseKeyValueTrace(body: String): IpInfo? {
        val values =
            body
                .lineSequence()
                .mapNotNull { line ->
                    val separatorIndex = line.indexOf('=')
                    if (separatorIndex <= 0) return@mapNotNull null

                    val key = line.substring(0, separatorIndex).trim().lowercase()
                    val value = line.substring(separatorIndex + 1).trim()
                    if (key.isEmpty() || value.isEmpty()) return@mapNotNull null
                    key to value
                }
                .toMap()

        val ip = values["ip"]?.takeIf(::isLikelyIpLiteral) ?: return null
        val countryCode = values["country_code"] ?: values["loc"]
        return IpInfo(ip = ip, countryCode = countryCode)
    }

    private fun isLikelyIpLiteral(value: String): Boolean {
        val candidate = value.trim()
        if (candidate.isEmpty() || candidate.any(Char::isWhitespace)) return false
        return isLikelyIpv4(candidate) || isLikelyIpv6(candidate)
    }

    private fun isLikelyIpv4(value: String): Boolean {
        val segments = value.split('.')
        if (segments.size != 4) return false
        return segments.all { segment ->
            segment.isNotEmpty() &&
                segment.length <= 3 &&
                segment.all(Char::isDigit) &&
                segment.toIntOrNull() in 0..255
        }
    }

    private fun isLikelyIpv6(value: String): Boolean {
        if (!value.contains(':')) return false
        return value.all {
            it.isDigit() || it in 'a'..'f' || it in 'A'..'F' || it == ':' || it == '.'
        }
    }
}

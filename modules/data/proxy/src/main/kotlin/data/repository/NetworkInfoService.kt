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

import com.github.nomadboxlab.monadbox.core.util.NetworkInterfaces
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import java.io.Closeable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    private val appSettings: AppSettingsStorage,
) : Closeable {
    private val json = Json { ignoreUnknownKeys = true }

    private val httpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }
        install(ContentNegotiation) { json(json) }
    }

    private val _refreshTrigger =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    override fun close() {
        httpClient.close()
    }

    fun triggerRefresh() {
        _refreshTrigger.tryEmit(Unit)
    }

    suspend fun getLocalIp(): String? {
        return NetworkInterfaces.getLocalIpAddress()
    }

    /**
     * Look up the external IP from a user-configured URL.
     *
     * Privacy: this is **never** called automatically. The home screen has a
     * "查询" button that calls this only on user tap. The URL is whatever the
     * user has set in `Settings -> Network -> 外部 IP 查询 URL`. An empty URL
     * means the feature is disabled.
     *
     * Returns `null` if the URL is empty/invalid or the request fails.
     */
    suspend fun queryExternalIp(): IpInfo? {
        val url = appSettings.externalIpLookupUrl.value.trim()
        if (url.isEmpty()) return null
        if (!isAllowedExternalIpUrl(url)) return null
        return try {
            val response = httpClient.get(url)
            val body = response.bodyAsText().trim()
            // Try structured JSON first, then plain-text IP (ip.gs, api.ipify.org, etc.).
            try {
                json.decodeFromString<IpInfo>(body)
            } catch (_: Exception) {
                body.takeIf { it.isNotEmpty() }?.let { IpInfo(ip = it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun isAllowedExternalIpUrl(url: String): Boolean {
        val lower = url.lowercase()
        if (lower.startsWith("https://")) return true
        // Allow cleartext only to loopback so power users can self-host on-device.
        if (lower.startsWith("http://")) {
            val host = lower.removePrefix("http://").substringBefore('/').substringBefore(':')
            return host == "127.0.0.1" || host == "localhost" || host == "::1"
        }
        return false
    }

    /**
     * Stream the local IP and (optionally) a previously-queried external IP.
     *
     * Privacy: this flow **never** calls any third-party endpoint. The external
     * IP is only ever populated by a manual `queryExternalIp()` invocation
     * from the home screen, gated on a non-empty user-configured URL.
     */
    fun startIpMonitoring(
        isProxyActiveFlow: Flow<Boolean>,
        externalIpFlow: Flow<IpInfo?> = flowOf(null),
    ): Flow<IpMonitoringState> = combine(
        externalIpFlow.onStart { emit(null) },
        isProxyActiveFlow,
    ) { externalIp, isProxyActive ->
        try {
            val localIp = getLocalIp()
            IpMonitoringState.Success(localIp = localIp, externalIp = externalIp, isProxyActive = isProxyActive)
        } catch (e: Exception) {
            IpMonitoringState.Error(e.message ?: "Unknown error")
        }
    }
}

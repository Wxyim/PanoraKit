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

package com.github.nomadboxlab.monadbox.data.repository

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.data.model.TargetSiteTrafficUsage
import com.github.nomadboxlab.monadbox.data.store.TrafficStatisticsStore
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber

class TargetSiteTrafficCollector(
    private val connectionActivityRepository: ConnectionActivityRepository,
    private val trafficStatisticsStore: TrafficStatisticsStore,
    private val runtimeStateReader: RuntimeStateReader,
    private val scope: CoroutineScope,
) {
    private data class SiteIdentity(val key: String, val displayName: String)

    private data class ConnectionBaseline(
        val siteKey: String,
        val displayName: String,
        val upload: Long,
        val download: Long,
    )

    private val baselines = linkedMapOf<String, ConnectionBaseline>()
    private var collectJob: Job? = null
    private var runtimeStateJob: Job? = null

    init {
        start()
    }

    private fun start() {
        if (collectJob?.isActive == true) return
        if (runtimeStateJob?.isActive != true) {
            runtimeStateJob =
                scope.launch {
                    runtimeStateReader.isRuntimeRunning.collectLatest { isRunning ->
                        if (isRunning) {
                            baselines.clear()
                            trafficStatisticsStore.clearTargetSiteUsages()
                        } else {
                            baselines.clear()
                        }
                    }
                }
        }
        collectJob =
            scope.launch {
                connectionActivityRepository.activeConnections.collectLatest { activeConnections ->
                    runCatching { applySnapshot(activeConnections) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            Timber.w(error, "Failed to collect target site traffic")
                        }
                }
            }
    }

    private fun applySnapshot(activeConnections: List<ConnectionInfo>) {
        val now = System.currentTimeMillis()
        val nextIds = activeConnections.asSequence().map(ConnectionInfo::id).toSet()
        val deltas = linkedMapOf<String, TargetSiteTrafficUsage>()

        activeConnections.forEach { connection ->
            val site =
                resolveSite(connection)
                    ?: run {
                        baselines.remove(connection.id)
                        return@forEach
                    }
            val currentUpload = connection.upload.coerceAtLeast(0L)
            val currentDownload = connection.download.coerceAtLeast(0L)
            val previous = baselines[connection.id]

            val (uploadDelta, downloadDelta) =
                when {
                    previous == null -> currentUpload to currentDownload
                    previous.siteKey != site.key -> {
                        baselines[connection.id] =
                            ConnectionBaseline(
                                siteKey = site.key,
                                displayName = site.displayName,
                                upload = currentUpload,
                                download = currentDownload,
                            )
                        return@forEach
                    }
                    currentUpload < previous.upload || currentDownload < previous.download ->
                        currentUpload to currentDownload
                    else ->
                        (currentUpload - previous.upload) to (currentDownload - previous.download)
                }

            if (uploadDelta > 0 || downloadDelta > 0) {
                val currentDelta =
                    deltas[site.key]
                        ?: TargetSiteTrafficUsage(
                            siteKey = site.key,
                            displayName = site.displayName,
                            totalUpload = 0L,
                            totalDownload = 0L,
                            lastSeenAt = now,
                        )
                deltas[site.key] =
                    currentDelta.copy(
                        displayName = site.displayName,
                        totalUpload = currentDelta.totalUpload + uploadDelta,
                        totalDownload = currentDelta.totalDownload + downloadDelta,
                        lastSeenAt = now,
                    )
            }

            baselines[connection.id] =
                ConnectionBaseline(
                    siteKey = site.key,
                    displayName = site.displayName,
                    upload = currentUpload,
                    download = currentDownload,
                )
        }

        baselines.keys.retainAll(nextIds)
        trafficStatisticsStore.recordTargetSiteTrafficBatch(deltas.values, seenAt = now)
    }

    private fun resolveSite(connection: ConnectionInfo): SiteIdentity? {
        val metadata = connection.metadata
        val host =
            normalizeEndpoint(metadata["host"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty())
        if (host.isNotBlank()) {
            return if (host.isIpLiteral()) {
                SiteIdentity(key = "ip:${host.lowercase()}", displayName = host)
            } else {
                SiteIdentity(key = "host:${host.lowercase()}", displayName = host)
            }
        }

        val destinationIp =
            normalizeEndpoint(
                metadata["destinationIP"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
            )
        if (destinationIp.isNotBlank()) {
            return SiteIdentity(
                key = "ip:${destinationIp.lowercase()}",
                displayName = destinationIp,
            )
        }

        return null
    }

    private fun normalizeEndpoint(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return ""

        if (trimmed.startsWith('[')) {
            val bracketEnd = trimmed.indexOf(']')
            if (bracketEnd > 1) {
                return trimmed.substring(1, bracketEnd).trim().trimEnd('.')
            }
        }

        if (trimmed.count { it == ':' } == 1) {
            val hostPart = trimmed.substringBeforeLast(':').trim()
            val portPart = trimmed.substringAfterLast(':').trim()
            if (hostPart.isNotBlank() && portPart.all(Char::isDigit)) {
                return hostPart.trimEnd('.')
            }
        }

        return trimmed.trimEnd('.')
    }

    private fun String.isIpLiteral(): Boolean = isIpv4Literal() || isIpv6Literal()

    private fun String.isIpv4Literal(): Boolean {
        val parts = split('.')
        if (parts.size != 4) return false
        return parts.all { part ->
            part.isNotBlank() &&
                part.all(Char::isDigit) &&
                part.toIntOrNull()?.let { value -> value in 0..255 } == true
        }
    }

    private fun String.isIpv6Literal(): Boolean {
        val candidate = substringBefore('%')
        if (candidate.count { it == ':' } < 2) return false
        return candidate.all { character ->
            character.isDigit() ||
                character.lowercaseChar() in 'a'..'f' ||
                character == ':' ||
                character == '.'
        }
    }

    fun stop() {
        collectJob?.cancel()
        collectJob = null
        runtimeStateJob?.cancel()
        runtimeStateJob = null
        baselines.clear()
    }
}

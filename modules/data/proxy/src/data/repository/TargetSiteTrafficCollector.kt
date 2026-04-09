package com.github.yumelira.yumebox.data.repository

import com.github.yumelira.yumebox.core.model.ConnectionInfo
import com.github.yumelira.yumebox.data.model.TargetSiteTrafficUsage
import com.github.yumelira.yumebox.data.store.TrafficStatisticsStore
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
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
    private val proxyFacade: ProxyFacade,
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
                    proxyFacade.isRunning.collectLatest { isRunning ->
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

            if (
                previous == null ||
                    previous.siteKey != site.key ||
                    currentUpload < previous.upload ||
                    currentDownload < previous.download
            ) {
                baselines[connection.id] =
                    ConnectionBaseline(
                        siteKey = site.key,
                        displayName = site.displayName,
                        upload = currentUpload,
                        download = currentDownload,
                    )
                return@forEach
            }

            val uploadDelta = currentUpload - previous.upload
            val downloadDelta = currentDownload - previous.download
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
                previous.copy(
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
        val host = metadata["host"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        if (host.isNotBlank()) {
            return SiteIdentity(key = "host:${host.lowercase()}", displayName = host)
        }

        val destinationIp =
            metadata["destinationIP"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        if (destinationIp.isNotBlank()) {
            return SiteIdentity(
                key = "ip:${destinationIp.lowercase()}",
                displayName = destinationIp,
            )
        }

        return null
    }

    fun stop() {
        collectJob?.cancel()
        collectJob = null
        runtimeStateJob?.cancel()
        runtimeStateJob = null
        baselines.clear()
    }
}

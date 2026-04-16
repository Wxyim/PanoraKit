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

import com.github.nomadboxlab.monadbox.data.store.TrafficStatisticsStore
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import kotlinx.coroutines.*
import timber.log.Timber

class TrafficStatisticsCollector(
    private val proxyFacade: ProxyFacade,
    private val trafficStatisticsStore: TrafficStatisticsStore,
    private val scope: CoroutineScope,
) {
    companion object {
        private const val TAG = "TrafficStatisticsCollector"
        private const val COLLECTION_INTERVAL_MS = 5000L
    }

    private var collectionJob: Job? = null
    private var monitoringJob: Job? = null
    private var lastTotalUpload: Long = 0L
    private var lastTotalDownload: Long = 0L
    private var lastProfileId: String? = null
    private var lastSampleAt: Long = 0L

    init {
        startCollection()
    }

    private fun startCollection() {
        collectionJob?.cancel()
        collectionJob =
            scope.launch {
                proxyFacade.isRunning.collect { isRunning ->
                    if (isRunning) {
                        monitoringJob?.cancel()
                        monitoringJob = startTrafficMonitoring()
                    } else {
                        monitoringJob?.cancel()
                        monitoringJob = null
                        resetLastValues()
                    }
                }
            }
    }

    private fun startTrafficMonitoring(): Job {
        return scope.launch {
            lastTotalUpload = trafficStatisticsStore.getLastTrafficUpload()
            lastTotalDownload = trafficStatisticsStore.getLastTrafficDownload()
            lastProfileId = trafficStatisticsStore.getLastProfileId()
            lastSampleAt = trafficStatisticsStore.getLastTrafficTimestamp()

            while (isActive && proxyFacade.isRunning.value) {
                runCatching {
                        collectTrafficData()
                        delay(COLLECTION_INTERVAL_MS)
                    }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Timber.tag(TAG).e(e, "Traffic collection failed")
                        delay(COLLECTION_INTERVAL_MS)
                    }
            }
        }
    }

    private fun collectTrafficData() {
        val collectedAt = System.currentTimeMillis()
        val trafficValue = proxyFacade.trafficTotal.value
        val trafficData =
            com.github.nomadboxlab.monadbox.domain.model.TrafficData.from(trafficValue)
        val currentUpload = trafficData.upload
        val currentDownload = trafficData.download
        val currentProfile = proxyFacade.currentProfile.value
        val currentProfileId = currentProfile?.uuid?.toString()
        val currentProfileName = currentProfile?.name

        if (lastTotalUpload == 0L && lastTotalDownload == 0L) {
            resetBaseline(currentUpload, currentDownload, currentProfileId, collectedAt)
            return
        }

        if (currentProfileId != lastProfileId) {
            resetBaseline(currentUpload, currentDownload, currentProfileId, collectedAt)
            return
        }

        if (currentUpload < lastTotalUpload || currentDownload < lastTotalDownload) {
            resetBaseline(currentUpload, currentDownload, currentProfileId, collectedAt)
            return
        }

        if (lastSampleAt <= 0L || collectedAt <= lastSampleAt) {
            resetBaseline(currentUpload, currentDownload, currentProfileId, collectedAt)
            return
        }

        val uploadDelta = currentUpload - lastTotalUpload
        val downloadDelta = currentDownload - lastTotalDownload

        if (uploadDelta > 0 || downloadDelta > 0) {
            trafficStatisticsStore.recordTraffic(
                uploadDelta,
                downloadDelta,
                currentProfileId,
                currentProfileName,
                windowStartMillis = lastSampleAt,
                windowEndMillis = collectedAt,
            )
        }

        lastTotalUpload = currentUpload
        lastTotalDownload = currentDownload
        lastSampleAt = collectedAt
        trafficStatisticsStore.setLastTraffic(
            currentUpload,
            currentDownload,
            currentProfileId,
            timestamp = collectedAt,
        )
    }

    private fun resetLastValues() {
        lastTotalUpload = 0L
        lastTotalDownload = 0L
        lastProfileId = null
        lastSampleAt = 0L
    }

    private fun resetBaseline(
        currentUpload: Long,
        currentDownload: Long,
        currentProfileId: String?,
        collectedAt: Long,
    ) {
        lastTotalUpload = currentUpload
        lastTotalDownload = currentDownload
        lastProfileId = currentProfileId
        lastSampleAt = collectedAt
        trafficStatisticsStore.setLastTraffic(
            currentUpload,
            currentDownload,
            currentProfileId,
            timestamp = collectedAt,
        )
    }

    fun stop() {
        collectionJob?.cancel()
        collectionJob = null
        monitoringJob?.cancel()
        monitoringJob = null
    }
}

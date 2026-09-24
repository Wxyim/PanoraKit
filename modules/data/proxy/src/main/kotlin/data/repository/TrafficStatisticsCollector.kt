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
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
import kotlinx.coroutines.*
import timber.log.Timber

class TrafficStatisticsCollector(
    private val runtimeStateReader: RuntimeStateReader,
    private val trafficStatisticsStore: TrafficStatisticsStore,
    private val scope: CoroutineScope,
) {
    companion object {
        private const val TAG = "TrafficStatisticsCollector"
        private const val COLLECTION_INTERVAL_MS = 5000L

        /**
         * Sentinel for "no traffic baseline persisted yet". A real runtime total can legitimately
         * be 0, so 0 cannot be used as the no-baseline marker (it would reset the baseline on the
         * next sample and discard the first delta).
         */
        private const val NO_BASELINE = -1L

        /**
         * Traffic deltas are accumulated in memory and flushed to MMKV in one batched write every
         * [FLUSH_INTERVAL_MS] (and on runtime stop / collector stop) instead of persisting on every
         * 5s sample. Each flush serializes the full daily-summaries and profile-usages maps, so
         * batching cuts both CPU and flash cost roughly 6x while bounding crash-loss to at most one
         * flush window.
         */
        private const val FLUSH_INTERVAL_MS = 30_000L
    }

    private var collectionJob: Job? = null
    private var monitoringJob: Job? = null
    private var lastTotalUpload: Long = NO_BASELINE
    private var lastTotalDownload: Long = NO_BASELINE
    private var lastProfileId: String? = null
    private var lastSampleAt: Long = 0L

    // Deltas accumulated between flushes. Guarded by pendingLock so a runtime-stop
    // flush from the collection coroutine never races an in-flight sample.
    private val pendingLock = Any()
    private var pendingUpload: Long = 0L
    private var pendingDownload: Long = 0L
    private var pendingWindowStart: Long = 0L
    private var pendingWindowEnd: Long = 0L
    private var pendingProfileId: String? = null
    private var pendingProfileName: String? = null
    private var lastFlushAt: Long = 0L

    init {
        startCollection()
    }

    private fun startCollection() {
        collectionJob?.cancel()
        collectionJob =
            scope.launch {
                runtimeStateReader.isRuntimeRunning.collect { isRunning ->
                    if (isRunning) {
                        monitoringJob?.cancel()
                        monitoringJob = startTrafficMonitoring()
                    } else {
                        monitoringJob?.cancel()
                        monitoringJob?.join()
                        monitoringJob = null
                        flushPending()
                        resetLastValues()
                    }
                }
            }
    }

    private fun startTrafficMonitoring(): Job {
        return scope.launch {
            // A persisted 0 total is a real baseline (the runtime was sampled with zero traffic),
            // not "no baseline": keeping it lets the next sample capture the whole gap from 0.
            lastTotalUpload = trafficStatisticsStore.getLastTrafficUpload()
            lastTotalDownload = trafficStatisticsStore.getLastTrafficDownload()
            lastProfileId = trafficStatisticsStore.getLastProfileId()
            lastSampleAt = trafficStatisticsStore.getLastTrafficTimestamp()
            synchronized(pendingLock) {
                pendingUpload = 0L
                pendingDownload = 0L
                pendingWindowStart = 0L
                pendingWindowEnd = 0L
                lastFlushAt = System.currentTimeMillis()
            }

            while (isActive && runtimeStateReader.isRuntimeRunning.value) {
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
            // Runtime stopped (or the job was cancelled): persist any buffered deltas.
            flushPending()
        }
    }

    private fun collectTrafficData() {
        val collectedAt = System.currentTimeMillis()
        val trafficValue = runtimeStateReader.runtimeTrafficTotal.value
        val trafficData =
            com.github.nomadboxlab.monadbox.domain.model.TrafficData.from(trafficValue)
        val currentUpload = trafficData.upload
        val currentDownload = trafficData.download
        val currentProfile = runtimeStateReader.currentRuntimeProfile.value
        val currentProfileId = currentProfile?.id
        val currentProfileName = currentProfile?.name

        if (lastTotalUpload == NO_BASELINE && lastTotalDownload == NO_BASELINE) {
            resetBaseline(currentUpload, currentDownload, currentProfileId, collectedAt)
            return
        }

        // A null profile means the runtime profile has not been resolved yet (e.g. the app process
        // was recreated while the service kept running). Do not treat that as a profile switch:
        // resetting the baseline here discards the unrecorded traffic gap between the persisted
        // baseline and the current totals.
        if (currentProfileId != null && currentProfileId != lastProfileId) {
            resetBaseline(currentUpload, currentDownload, currentProfileId, collectedAt)
            return
        }

        // The counter went backwards, so the core was restarted. A drop to exactly (0, 0) can also
        // mean the app process's traffic snapshot has not been populated yet after a restart, so
        // defer the reset in that case instead of throwing away the gap.
        if (
            (currentUpload < lastTotalUpload || currentDownload < lastTotalDownload) &&
                !(currentUpload == 0L && currentDownload == 0L)
        ) {
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
            synchronized(pendingLock) {
                if (pendingUpload == 0L && pendingDownload == 0L) {
                    pendingWindowStart = lastSampleAt
                }
                pendingUpload += uploadDelta
                pendingDownload += downloadDelta
                pendingWindowEnd = collectedAt
                pendingProfileId = currentProfileId ?: lastProfileId
                pendingProfileName = currentProfileName
            }
            lastTotalUpload = currentUpload
            lastTotalDownload = currentDownload
            lastSampleAt = collectedAt
        }

        if (shouldFlush(collectedAt)) {
            flushPending()
        }
    }

    private fun shouldFlush(collectedAt: Long): Boolean {
        synchronized(pendingLock) {
            return (pendingUpload > 0L || pendingDownload > 0L) &&
                collectedAt - lastFlushAt >= FLUSH_INTERVAL_MS
        }
    }

    private fun flushPending() {
        var upload = 0L
        var download = 0L
        var windowStart = 0L
        var windowEnd = 0L
        var profileId: String? = null
        var profileName: String? = null
        synchronized(pendingLock) {
            if (pendingUpload <= 0L && pendingDownload <= 0L) return
            upload = pendingUpload
            download = pendingDownload
            windowStart = pendingWindowStart
            windowEnd = pendingWindowEnd
            profileId = pendingProfileId
            profileName = pendingProfileName
            pendingUpload = 0L
            pendingDownload = 0L
            pendingWindowStart = 0L
            pendingWindowEnd = 0L
            lastFlushAt = System.currentTimeMillis()
        }
        val start = windowStart.takeIf { it > 0L } ?: lastSampleAt
        val end = windowEnd.coerceAtLeast(start)
        trafficStatisticsStore.recordTraffic(
            upload,
            download,
            profileId,
            profileName,
            windowStartMillis = start,
            windowEndMillis = end,
        )
        trafficStatisticsStore.setLastTraffic(
            lastTotalUpload,
            lastTotalDownload,
            profileId,
            timestamp = System.currentTimeMillis(),
        )
    }

    private fun resetLastValues() {
        lastTotalUpload = NO_BASELINE
        lastTotalDownload = NO_BASELINE
        lastProfileId = null
        lastSampleAt = 0L
    }

    private fun resetBaseline(
        currentUpload: Long,
        currentDownload: Long,
        currentProfileId: String?,
        collectedAt: Long,
    ) {
        // Attribute any deltas still buffered under the previous profile/totals
        // before switching the baseline.
        flushPending()
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
        flushPending()
        collectionJob?.cancel()
        collectionJob = null
        monitoringJob?.cancel()
        monitoringJob = null
    }
}

package com.github.nomadboxlab.monadbox.debug

import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Characterization replica of the batching/attribution math in `TrafficStatisticsCollector` +
 * `TrafficStatisticsStore`.
 *
 * The real collector is backed by MMKV, which cannot be loaded in the unit-test JVM, so these tests
 * mirror the production algorithms exactly. Keep this file in sync whenever the collector
 * sample/flush logic or the store attribution math changes.
 *
 * Regression covered: after the app process is recreated while the VPN service keeps running, the
 * collector must record the whole un-flushed traffic gap (including while the runtime profile is
 * still being resolved) instead of resetting the baseline and discarding it.
 */
class TrafficPipelineReplicaTest {

    enum class TimeSlot(val startHour: Int, val endHour: Int, val label: String) {
        SLOT_0_4(0, 4, "0-4"),
        SLOT_4_8(4, 8, "4-8"),
        SLOT_8_12(8, 12, "8-12"),
        SLOT_12_16(12, 16, "12-16"),
        SLOT_16_20(16, 20, "16-20"),
        SLOT_20_24(20, 24, "20-24");

        companion object {
            fun fromHour(hour: Int): TimeSlot = entries.first { hour >= it.startHour && hour < it.endHour }
        }
    }

    data class TrafficSlotData(val slotIndex: Int, val upload: Long, val download: Long) {
        val total: Long get() = upload + download
    }

    data class DailyTrafficSummary(
        val dateMillis: Long,
        val totalUpload: Long,
        val totalDownload: Long,
        val hourlyData: Map<Int, TrafficSlotData> = emptyMap(),
    ) {
        val total: Long get() = totalUpload + totalDownload
        companion object { val EMPTY = DailyTrafficSummary(0L, 0L, 0L) }
    }

    private data class Segment(val timestampMillis: Long, val durationMillis: Long)

    private class StoreReplica {
        private val dailySummaries = mutableMapOf<Long, DailyTrafficSummary>()

        fun summaries(): Map<Long, DailyTrafficSummary> = dailySummaries.toMap()

        fun recordTraffic(uploadDelta: Long, downloadDelta: Long, windowStartMillis: Long, windowEndMillis: Long) {
            if (uploadDelta <= 0 && downloadDelta <= 0) return
            val safeEnd = if (windowEndMillis > 0L) windowEndMillis else System.currentTimeMillis()
            val safeStart = when {
                windowStartMillis <= 0L -> safeEnd
                windowStartMillis > safeEnd -> safeEnd
                else -> windowStartMillis
            }
            val segments = buildSegments(safeStart, safeEnd)
            val totalDuration = segments.sumOf(Segment::durationMillis).coerceAtLeast(1L)
            var remainingUpload = uploadDelta
            var remainingDownload = downloadDelta
            segments.forEachIndexed { index, segment ->
                val segmentUpload =
                    if (index == segments.lastIndex) remainingUpload
                    else ((uploadDelta * segment.durationMillis) / totalDuration).coerceIn(0L, remainingUpload)
                val segmentDownload =
                    if (index == segments.lastIndex) remainingDownload
                    else ((downloadDelta * segment.durationMillis) / totalDuration).coerceIn(0L, remainingDownload)
                applyDelta(segment.timestampMillis, segmentUpload, segmentDownload)
                remainingUpload -= segmentUpload
                remainingDownload -= segmentDownload
            }
        }

        private fun applyDelta(timestampMillis: Long, uploadDelta: Long, downloadDelta: Long) {
            if (uploadDelta <= 0 && downloadDelta <= 0) return
            val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
            val dayKey = dayKey(calendar)
            val slotIndex = TimeSlot.fromHour(calendar.get(Calendar.HOUR_OF_DAY)).ordinal
            val daySummary = dailySummaries[dayKey] ?: DailyTrafficSummary(dayKey, 0L, 0L)
            val hourlyData = daySummary.hourlyData.toMutableMap()
            val currentSlot = hourlyData[slotIndex] ?: TrafficSlotData(slotIndex, 0L, 0L)
            hourlyData[slotIndex] =
                currentSlot.copy(upload = currentSlot.upload + uploadDelta, download = currentSlot.download + downloadDelta)
            dailySummaries[dayKey] =
                daySummary.copy(
                    totalUpload = daySummary.totalUpload + uploadDelta,
                    totalDownload = daySummary.totalDownload + downloadDelta,
                    hourlyData = hourlyData,
                )
        }

        fun getTodaySummary(now: Long = System.currentTimeMillis()): DailyTrafficSummary {
            val calendar = Calendar.getInstance().apply { timeInMillis = now }
            return dailySummaries[dayKey(calendar)] ?: DailyTrafficSummary.EMPTY
        }

        fun getTodayHourlyData(now: Long): List<TrafficSlotData> {
            val summary = getTodaySummary(now)
            return TimeSlot.entries.map { slot ->
                summary.hourlyData[slot.ordinal] ?: TrafficSlotData(slot.ordinal, 0L, 0L)
            }
        }

        private fun buildSegments(startMillis: Long, endMillis: Long): List<Segment> {
            if (endMillis <= startMillis) return listOf(Segment(endMillis, 1L))
            val segments = mutableListOf<Segment>()
            var cursor = startMillis
            while (cursor < endMillis) {
                val nextBoundary = nextBoundary(cursor)
                val segmentEnd = minOf(nextBoundary, endMillis)
                segments += Segment(cursor, (segmentEnd - cursor).coerceAtLeast(1L))
                cursor = segmentEnd
            }
            return segments.ifEmpty { listOf(Segment(endMillis, 1L)) }
        }

        private fun nextBoundary(timeMillis: Long): Long {
            val calendar = Calendar.getInstance().apply { timeInMillis = timeMillis }
            val slot = TimeSlot.fromHour(calendar.get(Calendar.HOUR_OF_DAY))
            val boundary = calendar.clone() as Calendar
            if (slot.endHour >= 24) {
                boundary.add(Calendar.DAY_OF_YEAR, 1)
                boundary.set(Calendar.HOUR_OF_DAY, 0)
            } else {
                boundary.set(Calendar.HOUR_OF_DAY, slot.endHour)
            }
            boundary.set(Calendar.MINUTE, 0)
            boundary.set(Calendar.SECOND, 0)
            boundary.set(Calendar.MILLISECOND, 0)
            return boundary.timeInMillis.coerceAtLeast(timeMillis + 1L)
        }

        private fun dayKey(calendar: Calendar): Long {
            val cal = calendar.clone() as Calendar
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }

    private class CollectorReplica(private val store: StoreReplica) {
        private var lastTotalUpload = NO_BASELINE
        private var lastTotalDownload = NO_BASELINE
        private var lastProfileId: String? = null
        private var lastSampleAt = 0L
        private var pendingUpload = 0L
        private var pendingDownload = 0L
        private var pendingWindowStart = 0L
        private var pendingWindowEnd = 0L
        private var lastFlushAt = 0L
        private val flushInterval = 30_000L

        fun start() {
            lastFlushAt = System.currentTimeMillis()
        }

        /** Simulates loading the persisted baseline after a process restart. */
        fun restoreBaseline(upload: Long, download: Long, profileId: String?, lastSampleAt: Long) {
            lastTotalUpload = upload
            lastTotalDownload = download
            lastProfileId = profileId
            this.lastSampleAt = lastSampleAt
            lastFlushAt = System.currentTimeMillis()
        }

        fun sample(currentUpload: Long, currentDownload: Long, profileId: String?, collectedAt: Long) {
            if (lastTotalUpload == NO_BASELINE && lastTotalDownload == NO_BASELINE) {
                resetBaseline(currentUpload, currentDownload, profileId, collectedAt)
                return
            }
            if (profileId != null && profileId != lastProfileId) {
                resetBaseline(currentUpload, currentDownload, profileId, collectedAt)
                return
            }
            if (
                (currentUpload < lastTotalUpload || currentDownload < lastTotalDownload) &&
                    !(currentUpload == 0L && currentDownload == 0L)
            ) {
                resetBaseline(currentUpload, currentDownload, profileId, collectedAt)
                return
            }
            if (lastSampleAt <= 0L || collectedAt <= lastSampleAt) {
                resetBaseline(currentUpload, currentDownload, profileId, collectedAt)
                return
            }
            val uploadDelta = currentUpload - lastTotalUpload
            val downloadDelta = currentDownload - lastTotalDownload
            if (uploadDelta > 0 || downloadDelta > 0) {
                if (pendingUpload == 0L && pendingDownload == 0L) {
                    pendingWindowStart = lastSampleAt
                }
                pendingUpload += uploadDelta
                pendingDownload += downloadDelta
                pendingWindowEnd = collectedAt
                lastTotalUpload = currentUpload
                lastTotalDownload = currentDownload
                lastSampleAt = collectedAt
            }
            if (shouldFlush(collectedAt)) flush()
        }

        private fun shouldFlush(collectedAt: Long): Boolean =
            (pendingUpload > 0L || pendingDownload > 0L) && collectedAt - lastFlushAt >= flushInterval

        fun flush() {
            if (pendingUpload <= 0L && pendingDownload <= 0L) return
            val upload = pendingUpload
            val download = pendingDownload
            val start = pendingWindowStart.takeIf { it > 0L } ?: lastSampleAt
            val end = pendingWindowEnd.coerceAtLeast(start)
            pendingUpload = 0L
            pendingDownload = 0L
            pendingWindowStart = 0L
            pendingWindowEnd = 0L
            lastFlushAt = System.currentTimeMillis()
            store.recordTraffic(upload, download, start, end)
        }

        private fun resetBaseline(currentUpload: Long, currentDownload: Long, profileId: String?, collectedAt: Long) {
            flush()
            lastTotalUpload = currentUpload
            lastTotalDownload = currentDownload
            lastProfileId = profileId
            lastSampleAt = collectedAt
        }

        companion object {
            private const val NO_BASELINE = -1L
        }
    }

    @Test
    fun normalForegroundSessionRecordsData() {
        val store = StoreReplica()
        val collector = CollectorReplica(store)
        collector.start()

        val t0 = System.currentTimeMillis()
        var upload = 0L
        var download = 0L
        var t = t0
        collector.sample(0L, 0L, "P", t)
        repeat(20) { i ->
            t += 5_000L
            upload += 100_000L
            download += 200_000L
            collector.sample(upload, download, "P", t)
        }
        collector.flush()

        val today = store.getTodaySummary(t)
        assertEquals(2_000_000L, today.totalUpload)
        assertEquals(4_000_000L, today.totalDownload)
        assertTrue(store.getTodayHourlyData(t).any { it.total > 0L })
    }

    @Test
    fun restartWhileServiceRunning_capturesWholeGap() {
        val store = StoreReplica()
        val collector = CollectorReplica(store)
        collector.start()

        val t0 = System.currentTimeMillis()
        var t = t0
        // First session: baseline established at 100 KB, a little traffic recorded and flushed.
        collector.sample(100_000L, 50_000L, "P", t)
        t += 5_000L
        collector.sample(150_000L, 80_000L, "P", t)
        collector.flush()
        assertEquals(50_000L, store.getTodaySummary(t).totalUpload)

        // Simulate the app process being killed while the service keeps running: the persisted
        // baseline is 150 KB, and hours later the runtime totals have advanced to 2 MB.
        val gapStart = t
        val collector2 = CollectorReplica(store)
        collector2.restoreBaseline(150_000L, 80_000L, "P", gapStart)
        t += 3_600_000L
        // First sample after restart: profile not resolved yet (null), totals jump to the gap end.
        collector2.sample(2_000_000L, 1_000_000L, null, t)
        t += 5_000L
        // Profile resolves; more traffic keeps flowing.
        collector2.sample(2_100_000L, 1_050_000L, "P", t)
        collector2.flush()

        val today = store.getTodaySummary(t)
        // 50 KB (session 1) + 1.95 MB (gap + follow-up) must all be recorded.
        assertEquals(2_000_000L, today.totalUpload)
        assertEquals(1_000_000L, today.totalDownload)
        assertTrue(store.getTodayHourlyData(t).any { it.total > 0L })
    }
}

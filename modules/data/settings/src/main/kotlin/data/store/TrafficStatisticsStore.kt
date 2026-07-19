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

package com.github.nomadboxlab.monadbox.data.store

import android.util.Log
import com.github.nomadboxlab.monadbox.data.model.DailyTrafficSummary
import com.github.nomadboxlab.monadbox.data.model.ProfileTrafficUsage
import com.github.nomadboxlab.monadbox.data.model.TimeSlot
import com.github.nomadboxlab.monadbox.data.model.TrafficSlotData
import com.tencent.mmkv.MMKV
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * Cross-process MMKV-backed store.
 *
 * Shared between the app process and the `:runtime:service` VPN process via
 * `MMKV.MULTI_PROCESS_MODE` (see `MMKVProvider`). Must NOT be migrated to Room or single-process
 * Preferences DataStore — neither offers multi-process coherence. Any future replacement requires a
 * ContentProvider or AIDL shim. See `docs/REFACTOR_ROADMAP.md` Phase 7 step 3.
 */
class TrafficStatisticsStore(private val mmkv: MMKV) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private const val TAG = "TrafficStatisticsStore"
        private const val KEY_DAILY_SUMMARIES = "daily_summaries"
        private const val KEY_PROFILE_USAGES = "profile_usages"
        private const val KEY_LAST_TRAFFIC_UPLOAD = "last_traffic_upload"
        private const val KEY_LAST_TRAFFIC_DOWNLOAD = "last_traffic_download"
        private const val KEY_LAST_PROFILE_ID = "last_profile_id"
        private const val KEY_LAST_TRAFFIC_TIMESTAMP = "last_traffic_timestamp"
        private const val MAX_DAYS_TO_KEEP = 90
    }

    private val _dailySummaries = MutableStateFlow<Map<Long, DailyTrafficSummary>>(emptyMap())
    val dailySummaries: StateFlow<Map<Long, DailyTrafficSummary>> = _dailySummaries.asStateFlow()

    private val _profileUsages = MutableStateFlow<Map<String, ProfileTrafficUsage>>(emptyMap())
    val profileUsages: StateFlow<Map<String, ProfileTrafficUsage>> = _profileUsages.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        mmkv.decodeString(KEY_DAILY_SUMMARIES)?.let { jsonStr ->
            runCatching {
                val summaries: Map<Long, DailyTrafficSummary> = json.decodeFromString(jsonStr)
                _dailySummaries.value = summaries
            }
        }

        mmkv.decodeString(KEY_PROFILE_USAGES)?.let { jsonStr ->
            runCatching {
                val usages: Map<String, ProfileTrafficUsage> = json.decodeFromString(jsonStr)
                _profileUsages.value = usages
            }
        }
    }

    private fun saveDailySummaries() {
        runCatching {
            val jsonStr = json.encodeToString(_dailySummaries.value)
            mmkv.encode(KEY_DAILY_SUMMARIES, jsonStr)
        }
    }

    private fun saveProfileUsages() {
        runCatching {
            val jsonStr = json.encodeToString(_profileUsages.value)
            mmkv.encode(KEY_PROFILE_USAGES, jsonStr)
        }
    }


    fun recordTraffic(
        uploadDelta: Long,
        downloadDelta: Long,
        profileId: String? = null,
        profileName: String? = null,
        windowStartMillis: Long = System.currentTimeMillis(),
        windowEndMillis: Long = windowStartMillis,
    ) {
        if (uploadDelta <= 0 && downloadDelta <= 0) return

        val safeEnd = windowEndMillis.takeIf { it > 0L } ?: System.currentTimeMillis()
        val safeStart =
            when {
                windowStartMillis <= 0L -> safeEnd
                windowStartMillis > safeEnd -> safeEnd
                else -> windowStartMillis
            }
        val currentSummaries = _dailySummaries.value.toMutableMap()
        val segments = buildTrafficAttributionSegments(safeStart, safeEnd)
        val totalDuration =
            segments.sumOf(TrafficAttributionSegment::durationMillis).coerceAtLeast(1L)
        var remainingUpload = uploadDelta
        var remainingDownload = downloadDelta

        segments.forEachIndexed { index, segment ->
            val segmentUpload =
                if (index == segments.lastIndex) {
                    remainingUpload
                } else {
                    ((uploadDelta * segment.durationMillis) / totalDuration).coerceIn(
                        0L,
                        remainingUpload,
                    )
                }
            val segmentDownload =
                if (index == segments.lastIndex) {
                    remainingDownload
                } else {
                    ((downloadDelta * segment.durationMillis) / totalDuration).coerceIn(
                        0L,
                        remainingDownload,
                    )
                }
            applyTrafficDelta(
                data = currentSummaries,
                timestampMillis = segment.timestampMillis,
                uploadDelta = segmentUpload,
                downloadDelta = segmentDownload,
            )
            remainingUpload -= segmentUpload
            remainingDownload -= segmentDownload
        }

        _dailySummaries.value = cleanOldData(currentSummaries)
        saveDailySummaries()

        if (!profileId.isNullOrBlank() && !profileName.isNullOrBlank()) {
            recordProfileTraffic(profileId, profileName, uploadDelta, downloadDelta)
        }
    }

    fun recordProfileTraffic(
        profileId: String,
        profileName: String,
        uploadDelta: Long,
        downloadDelta: Long,
    ) {
        if (uploadDelta <= 0 && downloadDelta <= 0) return

        val currentUsages = _profileUsages.value.toMutableMap()
        val currentUsage =
            currentUsages[profileId]
                ?: ProfileTrafficUsage(
                    profileId = profileId,
                    profileName = profileName,
                    totalUpload = 0L,
                    totalDownload = 0L,
                )

        val updatedUsage =
            currentUsage.copy(
                profileName = profileName,
                totalUpload = currentUsage.totalUpload + uploadDelta,
                totalDownload = currentUsage.totalDownload + downloadDelta,
            )

        currentUsages[profileId] = updatedUsage
        _profileUsages.value = currentUsages
        saveProfileUsages()
    }

    fun getTodaySummary(): DailyTrafficSummary {
        val todayKey = getDayKey(Calendar.getInstance())
        val summary = _dailySummaries.value[todayKey] ?: DailyTrafficSummary.EMPTY
        return sanitizeTodaySummaryIfNeeded(todayKey, summary)
    }

    fun getYesterdaySummary(): DailyTrafficSummary {
        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayKey = getDayKey(calendar)
        return _dailySummaries.value[yesterdayKey] ?: DailyTrafficSummary.EMPTY
    }

    fun getDailySummaries(days: Int): List<DailyTrafficSummary> {
        val result = mutableListOf<DailyTrafficSummary>()
        val todayKey = getDayKey(Calendar.getInstance())

        repeat(days) { i ->
            val targetCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val dayKey = getDayKey(targetCalendar)
            val summary =
                _dailySummaries.value[dayKey]
                    ?: DailyTrafficSummary(
                        dateMillis = dayKey,
                        totalUpload = 0L,
                        totalDownload = 0L,
                    )
            result.add(
                if (dayKey == todayKey) sanitizeTodaySummaryIfNeeded(dayKey, summary) else summary
            )
        }

        return result.reversed()
    }

    fun getTodayHourlyData(): List<TrafficSlotData> {
        val todaySummary = getTodaySummary()
        return TimeSlot.entries.map { slot ->
            todaySummary.hourlyData[slot.ordinal] ?: TrafficSlotData(slot.ordinal, 0L, 0L)
        }
    }

    fun getProfileUsagesSorted(): List<ProfileTrafficUsage> {
        return _profileUsages.value.values.sortedByDescending { it.totalBytes }
    }

    fun clearAll() {
        _dailySummaries.value = emptyMap()
        _profileUsages.value = emptyMap()
        mmkv.remove(KEY_DAILY_SUMMARIES)
        mmkv.remove(KEY_PROFILE_USAGES)
    }

    fun setLastTraffic(
        upload: Long,
        download: Long,
        profileId: String? = null,
        timestamp: Long = System.currentTimeMillis(),
    ) {
        mmkv.encode(KEY_LAST_TRAFFIC_UPLOAD, upload)
        mmkv.encode(KEY_LAST_TRAFFIC_DOWNLOAD, download)
        mmkv.encode(KEY_LAST_TRAFFIC_TIMESTAMP, timestamp)
        if (profileId != null) {
            mmkv.encode(KEY_LAST_PROFILE_ID, profileId)
        } else {
            mmkv.remove(KEY_LAST_PROFILE_ID)
        }
    }

    fun getLastTrafficUpload(): Long = mmkv.decodeLong(KEY_LAST_TRAFFIC_UPLOAD, 0L)

    fun getLastTrafficDownload(): Long = mmkv.decodeLong(KEY_LAST_TRAFFIC_DOWNLOAD, 0L)

    fun getLastProfileId(): String? = mmkv.decodeString(KEY_LAST_PROFILE_ID)

    fun getLastTrafficTimestamp(): Long = mmkv.decodeLong(KEY_LAST_TRAFFIC_TIMESTAMP, 0L)

    fun flushNow() {
        // The current store writes eagerly; keep this no-op for API symmetry.
    }

    private fun getDayKey(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun applyTrafficDelta(
        data: MutableMap<Long, DailyTrafficSummary>,
        timestampMillis: Long,
        uploadDelta: Long,
        downloadDelta: Long,
    ) {
        if (uploadDelta <= 0 && downloadDelta <= 0) return

        val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val dayKey = getDayKey(calendar)
        val slotIndex = TimeSlot.fromHour(calendar.get(Calendar.HOUR_OF_DAY)).ordinal
        val daySummary =
            data[dayKey]
                ?: DailyTrafficSummary(
                    dateMillis = dayKey,
                    totalUpload = 0L,
                    totalDownload = 0L,
                    hourlyData = emptyMap(),
                )
        val hourlyData = daySummary.hourlyData.toMutableMap()
        val currentSlot = hourlyData[slotIndex] ?: TrafficSlotData(slotIndex, 0L, 0L)
        hourlyData[slotIndex] =
            currentSlot.copy(
                upload = currentSlot.upload + uploadDelta,
                download = currentSlot.download + downloadDelta,
            )
        data[dayKey] =
            daySummary.copy(
                totalUpload = daySummary.totalUpload + uploadDelta,
                totalDownload = daySummary.totalDownload + downloadDelta,
                hourlyData = hourlyData,
            )
    }

    private fun sanitizeTodaySummaryIfNeeded(
        todayKey: Long,
        summary: DailyTrafficSummary,
    ): DailyTrafficSummary {
        if (summary == DailyTrafficSummary.EMPTY || summary.hourlyData.isEmpty()) return summary

        val calendar = Calendar.getInstance()
        val currentSlot = TimeSlot.fromHour(calendar.get(Calendar.HOUR_OF_DAY))
        val currentSlotIndex = currentSlot.ordinal
        val sanitizedHourlyData = summary.hourlyData.filterKeys { it <= currentSlotIndex }
        if (sanitizedHourlyData.size == summary.hourlyData.size) return summary

        val futureSlots =
            summary.hourlyData.keys
                .filter { it > currentSlotIndex }
                .sorted()
                .mapNotNull { index -> TimeSlot.entries.getOrNull(index)?.label }
        Log.w(
            TAG,
            "sanitizeTodaySummaryIfNeeded: now=${calendar.time} tz=${calendar.timeZone.id} " +
                "currentSlot=${currentSlot.label} futureSlots=$futureSlots dayKey=$todayKey",
        )

        val sanitizedSummary =
            summary.copy(
                totalUpload = sanitizedHourlyData.values.sumOf(TrafficSlotData::upload),
                totalDownload = sanitizedHourlyData.values.sumOf(TrafficSlotData::download),
                hourlyData = sanitizedHourlyData,
            )
        val updatedSummaries = _dailySummaries.value.toMutableMap()
        updatedSummaries[todayKey] = sanitizedSummary
        _dailySummaries.value = updatedSummaries
        saveDailySummaries()
        return sanitizedSummary
    }

    private fun buildTrafficAttributionSegments(
        startMillis: Long,
        endMillis: Long,
    ): List<TrafficAttributionSegment> {
        if (endMillis <= startMillis) {
            return listOf(
                TrafficAttributionSegment(timestampMillis = endMillis, durationMillis = 1L)
            )
        }

        val segments = mutableListOf<TrafficAttributionSegment>()
        var cursor = startMillis
        while (cursor < endMillis) {
            val nextBoundary = nextTimeSlotBoundary(cursor)
            val segmentEnd = minOf(nextBoundary, endMillis)
            segments +=
                TrafficAttributionSegment(
                    timestampMillis = cursor,
                    durationMillis = (segmentEnd - cursor).coerceAtLeast(1L),
                )
            cursor = segmentEnd
        }

        return segments.ifEmpty {
            listOf(TrafficAttributionSegment(timestampMillis = endMillis, durationMillis = 1L))
        }
    }

    private fun nextTimeSlotBoundary(timeMillis: Long): Long {
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

    private data class TrafficAttributionSegment(
        val timestampMillis: Long,
        val durationMillis: Long,
    )

    private fun cleanOldData(
        data: MutableMap<Long, DailyTrafficSummary>
    ): Map<Long, DailyTrafficSummary> {
        val cutoffTime = System.currentTimeMillis() - (MAX_DAYS_TO_KEEP * 24 * 60 * 60 * 1000L)
        return data.filterKeys { it >= cutoffTime }
    }
}

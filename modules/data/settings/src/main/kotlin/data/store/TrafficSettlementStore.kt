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
import com.tencent.mmkv.MMKV
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Cross-process MMKV-backed slot for the final traffic settlement of a runtime session.
 *
 * The core's counters are cumulative and are zeroed on teardown (`Clash.reset` ->
 * `ResetStatistic`), while the collector's input total only advances while the client poll loop
 * runs — which suspends while the app is backgrounded. Stopping the runtime from the background
 * (quick-settings tile, notification action) used to discard every byte the collector had not read
 * yet.
 *
 * The runtime stages its last cumulative counters here right before the teardown; the
 * traffic-statistics collector consumes the settlement when it observes the stop (or on its next
 * start), diffs it against its own baseline and records the gap with the true stop timestamp.
 *
 * Shared between the app process and the runtime service process via `MMKV.MULTI_PROCESS_MODE`
 * (same store file as [TrafficStatisticsStore]) — must stay MMKV-backed.
 */
class TrafficSettlementStore(private val mmkv: MMKV) {

    @Serializable
    data class Settlement(
        val upload: Long,
        val download: Long,
        val timestampMillis: Long,
        val profileId: String? = null,
        val profileName: String? = null,
    )

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Stages (overwrites) the final counters of the session that is about to be torn down. */
    fun stage(settlement: Settlement) {
        runCatching {
                mmkv.encode(
                    KEY_FINAL_SETTLEMENT,
                    json.encodeToString(Settlement.serializer(), settlement),
                )
            }
            .onFailure { error -> Log.w(TAG, "Failed to stage traffic settlement", error) }
    }

    /** Returns the staged settlement and clears it, or null when nothing is pending. */
    fun consume(): Settlement? {
        val raw = mmkv.decodeString(KEY_FINAL_SETTLEMENT) ?: return null
        mmkv.removeValueForKey(KEY_FINAL_SETTLEMENT)
        return runCatching { json.decodeFromString(Settlement.serializer(), raw) }
            .onFailure { error -> Log.w(TAG, "Failed to decode traffic settlement", error) }
            .getOrNull()
    }

    companion object {
        private const val TAG = "TrafficSettlementStore"
        private const val KEY_FINAL_SETTLEMENT = "final_settlement"
    }
}

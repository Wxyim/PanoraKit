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
 */
package com.github.nomadboxlab.monadbox.service.runtime.session

import android.os.SystemClock
import com.github.nomadboxlab.monadbox.core.model.TrafficSnapshot

/** Shares the foreground poller's existing sample with service notifications in this process. */
object ForegroundTrafficSnapshotCache {
    private data class Entry(val snapshot: TrafficSnapshot, val sampledAtElapsedMs: Long)

    @Volatile private var latest: Entry? = null
    @Volatile private var foregroundPollerActive = false

    fun publish(snapshot: TrafficSnapshot) {
        latest = Entry(snapshot, SystemClock.elapsedRealtime())
        foregroundPollerActive = true
    }

    fun markForegroundPollerInactive() {
        foregroundPollerActive = false
    }

    fun clear() {
        foregroundPollerActive = false
        latest = null
    }

    fun recentSnapshot(maxAgeMs: Long = MAX_AGE_MS): TrafficSnapshot? {
        if (!foregroundPollerActive) return null
        val entry = latest ?: return null
        val ageMs = SystemClock.elapsedRealtime() - entry.sampledAtElapsedMs
        return entry.snapshot.takeIf { ageMs >= 0L && ageMs <= maxAgeMs }
    }

    private const val MAX_AGE_MS = 500L
}

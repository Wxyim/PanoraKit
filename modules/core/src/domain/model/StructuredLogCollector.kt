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
 *
 */

package com.github.nomadboxlab.monadbox.domain.model

/**
 * In-memory collector for [StructuredLogEntry] instances.
 *
 * Key operational paths emit structured entries here so that:
 * - User-visible and failure entries are surfaced in the log viewer
 * - The debug export bundle can include the most recent failure chain
 * - Diagnostic entries are available on demand without polluting the user-visible layer
 */
class StructuredLogCollector(private val maxEntries: Int = DEFAULT_MAX_ENTRIES) {

    companion object {
        private const val DEFAULT_MAX_ENTRIES = 512
    }

    private val lock = Any()
    private val ring = ArrayDeque<StructuredLogEntry>(maxEntries)

    fun append(entry: StructuredLogEntry) {
        synchronized(lock) {
            if (ring.size >= maxEntries) {
                ring.removeFirst()
            }
            ring.addLast(entry)
        }
    }

    fun snapshot(): List<StructuredLogEntry> {
        synchronized(lock) {
            return ring.toList()
        }
    }

    fun recentFailures(limit: Int = 50): List<StructuredLogEntry> {
        synchronized(lock) {
            return ring.filter { it.isFailure }.takeLast(limit)
        }
    }

    fun recentUserVisible(limit: Int = 100): List<StructuredLogEntry> {
        synchronized(lock) {
            return ring.filter { it.isUserVisible }.takeLast(limit)
        }
    }

    fun entriesByLevel(level: LogLevel, limit: Int = 100): List<StructuredLogEntry> {
        synchronized(lock) {
            return ring.filter { it.level == level }.takeLast(limit)
        }
    }

    fun clear() {
        synchronized(lock) { ring.clear() }
    }
}

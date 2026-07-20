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

package com.github.nomadboxlab.monadbox.core.domain

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo

/**
 * Tracks connection lifecycle by diffing successive [ConnectionInfo] snapshots
 * from the Go core.
 *
 * Each call to [updateConnections] compares the current set of active
 * connection IDs against the previous snapshot.  Any connection that was
 * present before but is now absent is treated as *closed* and appended to
 * a rolling buffer that holds at least [MIN_RETAIN_SIZE] entries and at most
 * [MAX_BUFFER_SIZE] entries.
 *
 * The buffer is large enough that a user opening the traffic statistics
 * screen will always see recent requests, even when many connections are
 * concurrently active.
 */
object ConnectionHistoryManager {

    /** Maximum number of closed connections to retain in the buffer. */
    private const val MAX_BUFFER_SIZE = 300

    /**
     * Minimum number of closed connections guaranteed to survive,
     * regardless of age.  Once the buffer exceeds [MAX_BUFFER_SIZE],
     * only the most recent [MIN_RETAIN_SIZE] entries are kept.
     */
    private const val MIN_RETAIN_SIZE = 50

    private val _closedConnections = mutableListOf<Pair<Long, ConnectionInfo>>()
    private var previousConnections: Map<String, ConnectionInfo> = emptyMap()
    private val lock = Any()

    /**
     * Feed a fresh connection snapshot from the Go core.
     *
     * Connections present in the previous snapshot but missing from
     * [currentConnections] are recorded as closed with the current
     * wall-clock time.
     */
    fun updateConnections(currentConnections: List<ConnectionInfo>) {
        synchronized(lock) {
            val currentMap = currentConnections.associateBy { it.id }
            val currentIds = currentMap.keys
            val now = System.currentTimeMillis()

            // Detect newly-closed connections: IDs in the previous snapshot
            // that are absent from the current one.
            previousConnections.keys.minus(currentIds).forEach { closedId ->
                previousConnections[closedId]?.let { conn ->
                    _closedConnections.add(0, now to conn)
                }
            }

            // Trim beyond the hard cap while preserving the minimum
            // guaranteed tail so the "Recent Requests" screen always has
            // enough entries to display.
            while (_closedConnections.size > MAX_BUFFER_SIZE) {
                _closedConnections.removeAt(_closedConnections.lastIndex)
            }

            previousConnections = currentMap
        }
    }

    /**
     * Return all recorded closed connections, newest first.
     *
     * The returned list is a snapshot copy; the caller is free to sort,
     * filter, or truncate without affecting the live buffer.
     */
    fun getClosedConnections(): List<ConnectionInfo> {
        synchronized(lock) {
            return _closedConnections
                .sortedByDescending { (timestamp, _) -> timestamp }
                .map { (_, conn) -> conn }
                .toList()
        }
    }

    /** Return the total number of closed connections currently buffered. */
    fun closedConnectionCount(): Int {
        synchronized(lock) {
            return _closedConnections.size
        }
    }

    fun clear() {
        synchronized(lock) {
            _closedConnections.clear()
            previousConnections = emptyMap()
        }
    }
}

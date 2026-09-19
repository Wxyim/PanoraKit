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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Tracks connection lifecycle by diffing successive [ConnectionInfo] snapshots from the Go core.
 *
 * Each call to [updateConnections] compares the current set of active connection IDs against the
 * previous snapshot. Any connection that was present before but is now absent is treated as
 * *closed* and appended to a rolling buffer that holds at least [MIN_RETAIN_SIZE] entries and at
 * most [MAX_BUFFER_SIZE] entries.
 *
 * The buffer is large enough that a user opening the traffic statistics screen will always see
 * recent requests, even when many connections are concurrently active.
 */
object ConnectionHistoryManager {

    /** Maximum number of closed connections to retain in the buffer. */
    private const val MAX_BUFFER_SIZE = 300

    /**
     * Minimum number of closed connections guaranteed to survive, regardless of age. Once the
     * buffer exceeds [MAX_BUFFER_SIZE], only the most recent [MIN_RETAIN_SIZE] entries are kept.
     */
    private const val MIN_RETAIN_SIZE = 50

    private val _closedConnections = mutableListOf<Pair<Long, ConnectionInfo>>()
    private var previousConnections: Map<String, ConnectionInfo> = emptyMap()
    private var closedRevision = 0L
    private val lock = Any()

    /**
     * Feed a fresh connection snapshot from the Go core.
     *
     * Connections present in the previous snapshot but missing from [currentConnections] are
     * recorded as closed with the current wall-clock time.
     */
    fun updateConnections(currentConnections: List<ConnectionInfo>) {
        synchronized(lock) {
            val now = System.currentTimeMillis()

            // Connections still considered live by the core (not flagged closed).
            // Closed-flagged entries are retained briefly by the core so that
            // short-lived requests survive the poll cadence; they must not be
            // kept as "previous" or they would never be recorded as closed.
            val liveMap = currentConnections.filterNot { it.closed }.associateBy { it.id }
            val liveIds = liveMap.keys

            // Detect newly-closed connections: IDs in the previous snapshot
            // that are absent from the current live set.
            // Stash the close timestamp so downstream consumers can calculate
            // accurate connection duration (closeTime → start instead of now → start).
            previousConnections.keys.minus(liveIds).forEach { closedId ->
                previousConnections[closedId]?.let { conn ->
                    recordClosed(conn, now)
                }
            }

            // Connections the core flagged closed are recorded immediately. They
            // linger in the snapshot for a short grace period, so deduplicate by id.
            currentConnections.filter { it.closed }.forEach { conn ->
                if (_closedConnections.none { (_, existing) -> existing.id == conn.id }) {
                    recordClosed(conn, now)
                }
            }

            // Trim beyond the hard cap while preserving the minimum
            // guaranteed tail so the "Recent Requests" screen always has
            // enough entries to display.
            while (_closedConnections.size > MAX_BUFFER_SIZE) {
                _closedConnections.removeAt(_closedConnections.lastIndex)
            }

            previousConnections = liveMap
        }
    }

    /** Append a closed connection (newest first) and bump the revision. */
    private fun recordClosed(conn: ConnectionInfo, now: Long) {
        val enriched =
            conn.copy(
                metadata =
                    JsonObject(
                        conn.metadata.toMutableMap().apply {
                            put("_closeTimeMs", JsonPrimitive(now))
                        }
                    )
            )
        _closedConnections.add(0, now to enriched)
        closedRevision++
    }

    /**
     * Return all recorded closed connections, newest first.
     *
     * The returned list is a snapshot copy; the caller is free to sort, filter, or truncate without
     * affecting the live buffer.
     */
    fun getClosedConnections(): List<ConnectionInfo> {
        synchronized(lock) {
            // Entries are inserted newest-first, so avoid sorting the full
            // history on every one-second connection poll.
            return _closedConnections.map { (_, conn) -> conn }.toList()
        }
    }

    /** Return the total number of closed connections currently buffered. */
    fun closedConnectionCount(): Int {
        synchronized(lock) {
            return _closedConnections.size
        }
    }

    /** Monotonic revision for consumers that only need to refresh on changes. */
    fun closedConnectionsRevision(): Long {
        synchronized(lock) {
            return closedRevision
        }
    }

    fun clear() {
        synchronized(lock) {
            _closedConnections.clear()
            previousConnections = emptyMap()
            closedRevision++
        }
    }
}

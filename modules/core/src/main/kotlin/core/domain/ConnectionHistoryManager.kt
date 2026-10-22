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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

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

    /**
     * Closed entries by id, used to upgrade a row when attribution arrives after the first
     * snapshot.
     *
     * The native UID enrichment is asynchronous: a closed connection can be delivered to the app
     * before its owner lookup completes. Keeping the latest entry here lets [recordClosed] replace
     * an incomplete copy (for example one with `uid == 0`) when a later poll carries the enriched
     * metadata, without scanning the whole history just to detect the duplicate.
     */
    private val closedConnectionsById = mutableMapOf<String, Pair<Long, ConnectionInfo>>()
    private var previousConnections: Map<String, ConnectionInfo> = emptyMap()
    private var closedRevision = 0L
    private val lock = Any()

    /**
     * Feed a fresh connection snapshot from the Go core.
     *
     * Connections present in the previous snapshot but missing from [currentConnections] are
     * recorded as closed, using the close time the core reports when it has one.
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

            // Connections the core flagged closed are recorded first: the core stamps them with
            // the moment it saw the connection leave, and one poll can carry a whole batch of them
            // when the app was backgrounded (the core holds undelivered closes back for a while).
            // Recording those first keeps their real close times; the diff below then only has to
            // cover closes the core could not report. Both paths deduplicate by id in
            // [recordClosed], and the core reports retained closes oldest-first, so prepending
            // keeps the buffer newest-first.
            currentConnections.filter { it.closed }.forEach { conn -> recordClosed(conn, now) }

            // Detect newly-closed connections: IDs in the previous snapshot
            // that are absent from the current live set. This is the fallback for a connection the
            // core no longer lists at all (its retained entry already expired), so the close is
            // stamped with the poll time.
            previousConnections.keys.minus(liveIds).forEach { closedId ->
                previousConnections[closedId]?.let { conn -> recordClosed(conn, now) }
            }

            // Trim beyond the hard cap while preserving the minimum
            // guaranteed tail so the "Recent Requests" screen always has
            // enough entries to display.
            while (_closedConnections.size > MAX_BUFFER_SIZE) {
                val evicted = _closedConnections.removeAt(_closedConnections.lastIndex)
                closedConnectionsById.remove(evicted.second.id)
            }

            previousConnections = liveMap
        }
    }

    /**
     * Append a closed connection (newest first) and bump the revision, or upgrade an existing row
     * when a later snapshot carries a better app identity.
     *
     * The same id can be reported repeatedly: the core keeps a closed connection in its snapshot
     * for a while, and the diff against [previousConnections] can also report it. The first copy
     * may be a stale active snapshot with `uid == 0`; a later copy can contain the UID resolved by
     * the native enrichment pass. We therefore merge instead of dropping duplicates.
     */
    private fun recordClosed(conn: ConnectionInfo, observedAt: Long) {
        val existing = closedConnectionsById[conn.id]
        if (existing != null) {
            upgradeClosedIfBetter(conn, existing)
            return
        }
        // Prefer the core's close time: a batch of closes from a backgrounded period arrives long
        // after the fact, and stamping those with the poll time would report every one of them as
        // ending just now (wrong durations, wrong ordering). Falls back to the observation time for
        // connections the core could not report.
        val closedAt = conn.closedAt.takeIf { it > 0L } ?: observedAt
        val enriched = conn.withCloseTime(closedAt)
        val entry = closedAt to enriched
        _closedConnections.add(0, entry)
        closedConnectionsById[conn.id] = entry
        closedRevision++
    }

    /**
     * Merge a repeated close observation without ever downgrading an already-attributed row.
     *
     * The core reports a core-flagged close before the diff fallback in [updateConnections]. The
     * fallback can still hold the previous poll's active copy, so blindly replacing on duplicate
     * would overwrite an enriched core snapshot with an older `uid == 0` one. An identity score
     * keeps the richer copy; an authoritative close time from the core is still allowed through.
     */
    private fun upgradeClosedIfBetter(conn: ConnectionInfo, existing: Pair<Long, ConnectionInfo>) {
        val (existingClosedAt, existingInfo) = existing
        val newScore = identityScore(conn.metadata)
        val oldScore = identityScore(existingInfo.metadata)
        val authoritativeClosedAt = conn.closedAt.takeIf { it > 0L }
        val closeTimeChanged =
            authoritativeClosedAt != null && authoritativeClosedAt != existingClosedAt
        if (newScore <= oldScore && !closeTimeChanged) return

        val base = if (newScore >= oldScore) conn else existingInfo
        val closedAt = authoritativeClosedAt ?: existingClosedAt
        val upgraded = base.withCloseTime(closedAt)
        val index = _closedConnections.indexOfFirst { it.second.id == conn.id }
        if (index < 0) return
        _closedConnections[index] = closedAt to upgraded
        closedConnectionsById[conn.id] = closedAt to upgraded
        if (closeTimeChanged) {
            _closedConnections.sortByDescending { it.first }
        }
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
            closedConnectionsById.clear()
            previousConnections = emptyMap()
            closedRevision++
        }
    }
}

private fun ConnectionInfo.withCloseTime(closedAt: Long): ConnectionInfo =
    copy(
        metadata =
            JsonObject(
                metadata.toMutableMap().apply { put("_closeTimeMs", JsonPrimitive(closedAt)) }
            )
    )

/**
 * How much of an app identity this metadata copy carries.
 *
 * This deliberately mirrors the information the app-side AppIdentityResolver can use, rather than
 * comparing raw JSON maps: enrichment only adds fields, so a higher score means the row can become
 * more specific without ever replacing an enriched row with the stale active copy the diff fallback
 * may still hold.
 */
private fun identityScore(metadata: JsonObject): Int {
    var score = 0
    if (metadata.firstNonBlankValue("type").equals("Inner", ignoreCase = true)) {
        score += 16
    }
    if (metadata.firstPositiveUid() != null) {
        score += 8
    }
    if (
        metadata
            .firstNonBlankValue(
                "process",
                "processName",
                "process-name",
                "process_name",
                "appProcess",
            )
            .isNotBlank()
    ) {
        score += 4
    }
    if (
        metadata
            .firstNonBlankValue("packageName", "package", "package-name", "package_name")
            .isNotBlank()
    ) {
        score += 2
    }
    if (
        metadata.firstNonBlankValue("sourceIP", "sourceIp", "source-ip", "source_ip").isNotBlank()
    ) {
        score += 1
    }
    return score
}

private fun JsonObject.firstNonBlankValue(vararg keys: String): String {
    keys.forEach { key ->
        val value = this[key]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        if (value.isNotBlank()) return value
    }
    return ""
}

private fun JsonObject.firstPositiveUid(): Int? {
    listOf("uid", "sourceUid", "source_uid", "Uid", "UID").forEach { key ->
        val primitive = this[key]?.jsonPrimitive ?: return@forEach
        primitive.intOrNull?.let { if (it > 0) return it }
        primitive.longOrNull?.let { if (it in 1..Int.MAX_VALUE.toLong()) return it.toInt() }
        primitive.contentOrNull?.trim()?.toIntOrNull()?.let { if (it > 0) return it }
    }
    return null
}

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
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.OffsetDateTime
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

    /**
     * How long after the transport starts a TUN connection may still carry a pre-VPN physical
     * source address. Such a connection is a candidate for replacement by the app's new socket.
     */
    private const val STARTUP_LEGACY_WINDOW_MS = 8_000L

    /**
     * The longest gap between the legacy first datagram and the same app reconnecting through the
     * TUN source. Keep this bounded: a much later physical-source connection is not startup noise
     * and must remain visible.
     */
    private const val STARTUP_LEGACY_RECONCILE_WINDOW_MS = 30_000L

    /** Do not keep scanning the live/history snapshots after the startup transition has settled. */
    private const val STARTUP_RECONCILE_LIFETIME_MS = 90_000L

    /**
     * Clock tolerance for the transport-start boundary. The runtime snapshot can reach the app a
     * little after the Go core has already accepted its first packet.
     */
    private const val STARTUP_TIMESTAMP_TOLERANCE_MS = 5_000L

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
    private var startupSession: StartupSession? = null
    private val supersededConnectionIds = mutableSetOf<String>()
    private var closedRevision = 0L
    private val lock = Any()

    private data class StartupSession(
        val startedAtMillis: Long,
        val tunPrefixes: List<TunPrefix>,
        val localSourceAddresses: Set<String>,
    ) {
        private val tunAddressCache = mutableMapOf<String, Boolean>()

        fun containsTunAddress(raw: String): Boolean =
            raw.isNotBlank() &&
                tunAddressCache.getOrPut(raw) { tunPrefixes.any { it.contains(raw) } }

        fun isLocalSourceAddress(raw: String): Boolean =
            localSourceAddresses.isEmpty() || raw in localSourceAddresses
    }

    private data class TunPrefix(val network: ByteArray, val prefixLength: Int) {
        fun contains(raw: String): Boolean {
            val address =
                runCatching { InetAddress.getByName(raw.substringBefore('%')) }.getOrNull()
                    ?: return false
            if (address.address.size != network.size) return false

            val fullBytes = prefixLength / 8
            val remainingBits = prefixLength % 8
            for (index in 0 until fullBytes) {
                if (address.address[index] != network[index]) return false
            }
            if (remainingBits == 0) return true

            val mask = (0xff shl (8 - remainingBits)) and 0xff
            return (address.address[fullBytes].toInt() and mask) ==
                (network[fullBytes].toInt() and mask)
        }
    }

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

            // A socket that existed before the VPN came up can send its first datagram through
            // TUN with its old physical source. Once the same app reconnects through the TUN
            // source, the startup artifact is redundant and would otherwise make the recent
            // request list look like traffic escaped the tunnel.
            reconcileStartupLegacyLocked(currentConnections)

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
        if (conn.id in supersededConnectionIds) return
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
        val newScore = identityScore(conn)
        val oldScore = identityScore(existingInfo)
        val authoritativeClosedAt = conn.closedAt.takeIf { it > 0L }
        val closeTimeChanged =
            authoritativeClosedAt != null && authoritativeClosedAt != existingClosedAt
        val durationImproved = conn.durationMs >= 0L && existingInfo.durationMs < 0L
        if (newScore <= oldScore && !closeTimeChanged && !durationImproved) return

        val base = if (newScore >= oldScore) conn else existingInfo
        val closedAt = authoritativeClosedAt ?: existingClosedAt
        val durationMs = if (conn.durationMs >= 0L) conn.durationMs else existingInfo.durationMs
        val upgraded = base.withCloseTime(closedAt).copy(durationMs = durationMs)
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

    /**
     * Start a fresh recent-request session and enable startup legacy-session reconciliation.
     *
     * @param startedAtMillis wall-clock time at which the app observed the runtime become ready.
     * @param tunPrefixes CIDR ranges assigned to the TUN interface(s).
     * @param localSourceAddresses physical addresses owned by this device at session start.
     */
    fun beginSession(
        startedAtMillis: Long,
        tunPrefixes: List<String>,
        localSourceAddresses: Set<String> = localAddressSnapshot(),
    ) {
        synchronized(lock) {
            resetLocked()
            startupSession =
                StartupSession(
                    startedAtMillis = startedAtMillis,
                    tunPrefixes = tunPrefixes.mapNotNull(::parseTunPrefix),
                    localSourceAddresses = localSourceAddresses,
                )
        }
    }

    fun clear() {
        synchronized(lock) {
            resetLocked()
            startupSession = null
        }
    }

    private fun resetLocked() {
        _closedConnections.clear()
        closedConnectionsById.clear()
        previousConnections = emptyMap()
        supersededConnectionIds.clear()
        closedRevision++
    }

    /**
     * Remove a startup-era physical-source row when the same app later reaches the same endpoint
     * through the TUN source. Live replacements count too: the old row should disappear as soon as
     * the app has visibly reconnected, without waiting for the replacement to close.
     *
     * The match is deliberately conservative:
     * - only connections whose mihomo type is `Tun`;
     * - only rows started in the short post-start window with a source outside the TUN prefixes;
     * - same owner (UID/package/process) and destination, or destination-only when the legacy row
     *   has no resolved owner; if the server port changes during the reconnect, the owner+address
     *   fallback still matches the same destination address;
     * - the TUN-sourced replacement must be at least as new and arrive within
     *   [STARTUP_LEGACY_RECONCILE_WINDOW_MS].
     */
    private fun reconcileStartupLegacyLocked(currentConnections: List<ConnectionInfo>) {
        val session = startupSession ?: return
        if (session.tunPrefixes.isEmpty()) return
        if (System.currentTimeMillis() > session.startedAtMillis + STARTUP_RECONCILE_LIFETIME_MS) {
            return
        }

        val seenIds = HashSet<String>()
        val legacyCandidates = ArrayList<ConnectionInfo>()
        val tunReplacementStartsByPrimaryKey = HashMap<String, MutableList<Long>>()
        val tunReplacementStartsByTargetKey = HashMap<String, MutableList<Long>>()
        val tunReplacementStartsByOwnerTargetAddressKey = HashMap<String, MutableList<Long>>()
        val tunReplacementStartsByTargetAddressKey = HashMap<String, MutableList<Long>>()

        fun consider(conn: ConnectionInfo) {
            if (!seenIds.add(conn.id)) return
            if (!conn.isTunConnection()) return

            val startedAt = conn.startedAtMillis() ?: return
            val sourceIp = conn.sourceIp()
            if (sourceIp.isBlank()) return
            val targetKey = conn.connectionTargetKey()
            val targetAddressKey = conn.connectionTargetAddressKey()
            val ownerKey = conn.connectionOwnerKey()
            val primaryKey = ownerKey?.let { owner -> targetKey?.let { "$owner|$it" } }

            if (session.containsTunAddress(sourceIp)) {
                primaryKey?.let { key ->
                    tunReplacementStartsByPrimaryKey.getOrPut(key) { mutableListOf() } += startedAt
                }
                targetKey?.let { key ->
                    tunReplacementStartsByTargetKey.getOrPut(key) { mutableListOf() } += startedAt
                }
                ownerKey?.let { owner ->
                    targetAddressKey?.let { key ->
                        tunReplacementStartsByOwnerTargetAddressKey.getOrPut("$owner|$key") {
                            mutableListOf()
                        } += startedAt
                    }
                }
                targetAddressKey?.let { key ->
                    tunReplacementStartsByTargetAddressKey.getOrPut(key) { mutableListOf() } +=
                        startedAt
                }
                return
            }

            val earliest = session.startedAtMillis - STARTUP_TIMESTAMP_TOLERANCE_MS
            val latest = session.startedAtMillis + STARTUP_LEGACY_WINDOW_MS
            if (startedAt in earliest..latest && session.isLocalSourceAddress(sourceIp)) {
                legacyCandidates += conn
            }
        }

        _closedConnections.forEach { consider(it.second) }
        currentConnections.forEach(::consider)
        if (legacyCandidates.isEmpty()) return

        for (legacy in legacyCandidates) {
            if (legacy.id in supersededConnectionIds) continue
            val legacyStartedAt = legacy.startedAtMillis() ?: continue
            val targetKey = legacy.connectionTargetKey() ?: continue
            val targetAddressKey = legacy.connectionTargetAddressKey() ?: targetKey
            val ownerKey = legacy.connectionOwnerKey()
            val exactReplacementStarts =
                if (ownerKey != null) {
                    tunReplacementStartsByPrimaryKey["$ownerKey|$targetKey"]
                } else {
                    // A short-lived socket can close before Android publishes its UID. The
                    // destination match still catches the common app-reconnect case; rows with a
                    // known owner stay on the stricter owner+destination path.
                    tunReplacementStartsByTargetKey[targetKey]
                }

            val replacementAt =
                earliestReplacementAt(exactReplacementStarts, legacyStartedAt)
                    ?: if (ownerKey != null) {
                        earliestReplacementAt(
                            tunReplacementStartsByOwnerTargetAddressKey[
                                "$ownerKey|$targetAddressKey"],
                            legacyStartedAt,
                        )
                    } else {
                        // Ports can change when the app opens a fresh socket after the VPN comes
                        // up. For an unattributed startup row, the same network and destination
                        // address are still enough to identify the replacement.
                        earliestReplacementAt(
                            tunReplacementStartsByTargetAddressKey[targetAddressKey],
                            legacyStartedAt,
                        )
                    }
                    ?: continue

            if (replacementAt - legacyStartedAt > STARTUP_LEGACY_RECONCILE_WINDOW_MS) continue

            supersededConnectionIds += legacy.id
            removeClosedLocked(legacy.id)
        }
    }

    private fun removeClosedLocked(id: String): Boolean {
        val index = _closedConnections.indexOfFirst { it.second.id == id }
        if (index < 0) return false
        _closedConnections.removeAt(index)
        closedConnectionsById.remove(id)
        closedRevision++
        return true
    }

    private fun earliestReplacementAt(starts: List<Long>?, notBefore: Long): Long? =
        starts?.filter { it >= notBefore }?.minOrNull()

    private fun parseTunPrefix(raw: String): TunPrefix? {
        val value = raw.trim()
        val slash = value.lastIndexOf('/')
        if (slash <= 0 || slash == value.lastIndex) return null
        val address =
            runCatching { InetAddress.getByName(value.substring(0, slash)) }.getOrNull()
                ?: return null
        val prefixLength =
            value.substring(slash + 1).toIntOrNull()?.takeIf { it in 0..(address.address.size * 8) }
                ?: return null
        return TunPrefix(network = address.address, prefixLength = prefixLength)
    }
}

private fun ConnectionInfo.withCloseTime(closedAt: Long): ConnectionInfo =
    copy(
        metadata =
            JsonObject(
                metadata.toMutableMap().apply { put("_closeTimeMs", JsonPrimitive(closedAt)) }
            )
    )

private fun ConnectionInfo.isTunConnection(): Boolean =
    metadata.firstNonBlankValue("type").equals("Tun", ignoreCase = true)

private fun ConnectionInfo.sourceIp(): String =
    metadata
        .firstNonBlankValue("sourceIP", "sourceIp", "source-ip", "source_ip")
        .substringBefore('%')

private fun localAddressSnapshot(): Set<String> =
    runCatching {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .flatMap { networkInterface -> networkInterface.inetAddresses.toList() }
                .mapNotNull { it.hostAddress?.substringBefore('%') }
                .toSet()
        }
        .getOrDefault(emptySet())

private fun ConnectionInfo.startedAtMillis(): Long? {
    val raw = start.trim()
    if (raw.isEmpty()) return null
    return runCatching { OffsetDateTime.parse(raw).toInstant().toEpochMilli() }.getOrNull()
}

/**
 * The endpoint half of a startup-session match. Host is used only when mihomo could not populate
 * the destination IP (for example a proxy-inbound request), while TUN UDP connections normally
 * carry the concrete IP.
 */
private fun ConnectionInfo.connectionTargetKey(): String? {
    val network = metadata.firstNonBlankValue("network").lowercase()
    val destination =
        metadata.firstNonBlankValue("destinationIP", "destinationIp").ifBlank {
            metadata.firstNonBlankValue("host")
        }
    val port = metadata.firstNonBlankValue("destinationPort")
    if (network.isEmpty() || destination.isEmpty() || port.isEmpty()) return null
    return "$network|$destination|$port"
}

/**
 * Destination address without the port, used as a fallback when a socket reconnects to the same
 * server on a different port.
 */
private fun ConnectionInfo.connectionTargetAddressKey(): String? {
    val network = metadata.firstNonBlankValue("network").lowercase()
    val destination =
        metadata.firstNonBlankValue("destinationIP", "destinationIp").ifBlank {
            metadata.firstNonBlankValue("host")
        }
    if (network.isEmpty() || destination.isEmpty()) return null
    return "$network|$destination"
}

/**
 * A stable owner identifier when mihomo has resolved one. UID is preferred because a package can
 * have several processes; process/package fallbacks cover the early snapshots before UID lookup.
 */
private fun ConnectionInfo.connectionOwnerKey(): String? {
    metadata.firstPositiveUid()?.let {
        return "uid:$it"
    }
    metadata
        .firstNonBlankValue("packageName", "package", "package-name", "package_name")
        .takeIf { it.isNotEmpty() }
        ?.let {
            return "package:$it"
        }
    metadata
        .firstNonBlankValue("process", "processName", "process-name", "process_name", "appProcess")
        .takeIf { it.isNotEmpty() }
        ?.let {
            return "process:$it"
        }
    return null
}

/**
 * How much of an app identity this connection copy carries.
 *
 * This deliberately mirrors the information the app-side AppIdentityResolver can use, rather than
 * comparing raw JSON maps: enrichment only adds fields (or resolves the system flag), so a higher
 * score means the row can become more specific without ever replacing an enriched row with the
 * stale active copy the diff fallback may still hold.
 */
private fun identityScore(connection: ConnectionInfo): Int {
    val metadata = connection.metadata
    var score = 0
    // A late UID lookup can prove the traffic belongs to the kernel/root after the first copy was
    // delivered as "Unknown App". That is an identity upgrade, so it must outrank the stale copy.
    if (connection.system) {
        score += 16
    }
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

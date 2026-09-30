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
 */

package com.github.nomadboxlab.monadbox.service.common.util

import java.io.File
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Fallback UID resolver that reads [procfs] when [ConnectivityManager.getConnectionOwnerUid]
 * returns -1.
 *
 * Resolution is two-tier: port cache → on-demand procfs read. The port cache avoids redundant file
 * reads for short-lived sockets that appear in rapid succession. No background threads — procfs is
 * only read when a JNI callback calls [resolveByProtocol] and the port is not already cached.
 *
 * The procfs read matches a socket by its full local address first and falls back to the rows that
 * share the source port (see [resolveFromProcFs]) — an address-only match can never find the two
 * shapes Android sockets take most often, an unconnected UDP socket and a dual-stack socket.
 */
internal object ProcFsUidResolver {
    /**
     * Port-level UID cache for short-lived sockets that disappear from procfs before an on-demand
     * read can find them. Key: source port, Value: Pair(resolutionTimestamp, uid)
     */
    private val portUidCache = ConcurrentHashMap<Int, Pair<Long, Int>>()

    private const val PORT_CACHE_TTL_MS = 5_000L

    private val WHITESPACE = Regex("\\s+")

    /** One procfs table plus the data-row columns we need, resolved once from its header. */
    private class ProcNetTable(
        val path: String,
        val localAddrIndex: Int,
        val uidIndex: Int,
        val inodeIndex: Int,
    )

    /** What one table scan found for a 4-tuple: an exact match and/or a port-only answer. */
    private class ProcNetMatch {
        var exactUid = -1
        var portUid = -1

        /**
         * True when rows sharing the port disagreed about the owner. The port answer is then
         * unusable: the same port can be open in several apps, and a wrong owner is worse than
         * none.
         */
        var portAmbiguous = false
    }

    private var tcpTable: ProcNetTable? = null
    private var tcp6Table: ProcNetTable? = null
    private var udpTable: ProcNetTable? = null
    private var udp6Table: ProcNetTable? = null
    private var tablesLoaded = false

    // ═══════════════════════════════════════════════════════════════
    //  Protocol dispatch
    // ═══════════════════════════════════════════════════════════════

    private const val IPPROTO_TCP = 6
    private const val IPPROTO_UDP = 17

    fun resolveByProtocol(protocol: Int, source: InetSocketAddress): Int =
        when (protocol) {
            IPPROTO_TCP -> resolveTcpUid(source)
            IPPROTO_UDP -> resolveUdpUid(source)
            else -> resolveUdpUid(source)
        }

    // ═══════════════════════════════════════════════════════════════
    //  UDP resolution
    // ═══════════════════════════════════════════════════════════════

    fun resolveUdpUid(source: InetSocketAddress): Int {
        val port = source.port

        // 1. Port cache — successful past resolutions with TTL.
        portUidCache[port]?.let { (timestamp, uid) ->
            if (System.currentTimeMillis() - timestamp < PORT_CACHE_TTL_MS) {
                return uid
            }
        }

        // 2. Direct on-demand procfs read.
        val uid = resolveFromProcFs(source, tcp = false)
        if (uid > 0) {
            portUidCache[port] = Pair(System.currentTimeMillis(), uid)
            if (portUidCache.size > 2000) {
                prunePortCache()
            }
        }
        return uid
    }

    // ═══════════════════════════════════════════════════════════════
    //  TCP resolution
    // ═══════════════════════════════════════════════════════════════

    fun resolveTcpUid(source: InetSocketAddress): Int {
        val port = source.port

        // 1. Port cache.
        portUidCache[port]?.let { (timestamp, uid) ->
            if (System.currentTimeMillis() - timestamp < PORT_CACHE_TTL_MS) {
                return uid
            }
        }

        // 2. Direct on-demand procfs read.
        val uid = resolveFromProcFs(source, tcp = true)
        if (uid > 0) {
            portUidCache[port] = Pair(System.currentTimeMillis(), uid)
            if (portUidCache.size > 2000) {
                prunePortCache()
            }
        }
        return uid
    }

    /**
     * Resolve [source] against the procfs tables.
     *
     * A row is matched by its full local address first. When no row matches exactly, rows sharing
     * the source port are considered instead:
     * - an unconnected UDP socket is listed as `0.0.0.0:<port>` while the tunnel reports the
     *   concrete source address, so an address-only match can never succeed;
     * - a dual-stack socket (what Java opens for an IPv4 endpoint) is listed in the v6 table as a
     *   v4-mapped address, so the v4 table alone is not enough for an IPv4 source.
     *
     * A port-only answer is used only when every row with that port agrees on the UID.
     */
    private fun resolveFromProcFs(source: InetSocketAddress, tcp: Boolean): Int {
        val port = source.port
        val candidates: List<Pair<ProcNetTable, String>>
        when (val address = source.address) {
            is Inet6Address ->
                candidates =
                    listOfNotNull(
                        table(tcp, v6 = true)?.let {
                            it to formatLocalAddress(address.address, port)
                        }
                    )
            is Inet4Address -> {
                val exact = formatLocalAddress(address.address, port)
                val mapped = formatLocalAddress(v4Mapped(address.address), port)
                candidates =
                    listOfNotNull(
                        table(tcp, v6 = false)?.let { it to exact },
                        table(tcp, v6 = true)?.let { it to mapped },
                    )
            }
            else -> return -1
        }

        var portUid = -1
        var ambiguous = false
        candidates.forEach { (table, exactKey) ->
            val match = scanProcNet(table, exactKey, port)
            if (match.exactUid > 0) {
                return match.exactUid
            }
            if (match.portAmbiguous) {
                ambiguous = true
            }
            if (match.portUid > 0) {
                if (portUid < 0) {
                    portUid = match.portUid
                } else if (portUid != match.portUid) {
                    ambiguous = true
                }
            }
        }
        return if (ambiguous) -1 else portUid
    }

    // ═══════════════════════════════════════════════════════════════
    //  Shared infra
    // ═══════════════════════════════════════════════════════════════

    private fun scanProcNet(table: ProcNetTable, exactKey: String, port: Int): ProcNetMatch {
        val match = ProcNetMatch()
        val portSuffix = String.format(Locale.ROOT, "%04X", port)
        runCatching {
            File(table.path).bufferedReader().use { reader ->
                if (reader.readLine() == null) {
                    return@use
                }
                for (line in reader.lineSequence()) {
                    val fields = line.trim().split(WHITESPACE)
                    if (fields.size <= table.uidIndex || fields.size <= table.localAddrIndex) {
                        continue
                    }
                    val local = fields[table.localAddrIndex]
                    if (!local.endsWith(portSuffix, ignoreCase = true)) {
                        continue
                    }
                    // Ownerless rows (TIME_WAIT, inode 0) report uid 0; a port match on one would
                    // shadow the live row that still owns the connection.
                    if (
                        table.inodeIndex in 0 until fields.size && fields[table.inodeIndex] == "0"
                    ) {
                        continue
                    }
                    val uid = fields[table.uidIndex].toIntOrNull()?.takeIf { it > 0 } ?: continue
                    if (local.equals(exactKey, ignoreCase = true)) {
                        match.exactUid = uid
                        break
                    }
                    if (match.portUid < 0) {
                        match.portUid = uid
                    } else if (match.portUid != uid) {
                        match.portAmbiguous = true
                    }
                }
            }
        }
        return match
    }

    /**
     * Format IP + port as the `local_address` column: IP little-endian hex, port big-endian hex.
     */
    private fun formatLocalAddress(ip: ByteArray, port: Int): String {
        val sb = StringBuilder(ip.size * 2 + 5)
        var i = 0
        while (i < ip.size) {
            val groupEnd = minOf(i + 4, ip.size)
            for (j in groupEnd - 1 downTo i) {
                sb.append(String.format(Locale.ROOT, "%02X", ip[j].toInt() and 0xFF))
            }
            i = groupEnd
        }
        sb.append(':')
        sb.append(String.format(Locale.ROOT, "%04X", port))
        return sb.toString()
    }

    /** The `::ffff:a.b.c.d` byte form a dual-stack socket is listed under in the v6 tables. */
    private fun v4Mapped(ip: ByteArray): ByteArray {
        if (ip.size == 16) {
            return ip
        }
        return ByteArray(16).also {
            it[10] = 0xFF.toByte()
            it[11] = 0xFF.toByte()
            System.arraycopy(ip, 0, it, 12, 4)
        }
    }

    // ── Table index helpers ───────────────────────────────────────

    private fun table(tcp: Boolean, v6: Boolean): ProcNetTable? {
        ensureTables()
        return when {
            tcp -> if (v6) tcp6Table else tcpTable
            v6 -> udp6Table
            else -> udpTable
        }
    }

    @Synchronized
    private fun ensureTables() {
        if (tablesLoaded) {
            return
        }
        tcpTable = parseTable("/proc/net/tcp")
        tcp6Table = parseTable("/proc/net/tcp6")
        udpTable = parseTable("/proc/net/udp")
        udp6Table = parseTable("/proc/net/udp6")
        // Latch only once a table was actually readable: an early failure (or a transient one)
        // must not disable the fallback for the lifetime of the process.
        tablesLoaded =
            tcpTable != null || tcp6Table != null || udpTable != null || udp6Table != null
    }

    private fun parseTable(path: String): ProcNetTable? {
        return runCatching {
                File(path).bufferedReader().use { reader ->
                    val header = reader.readLine() ?: return@use null
                    val columns = header.trim().split(WHITESPACE)
                    val localIdx = columns.indexOf("local_address")
                    val uidIdx = columns.indexOf("uid")
                    if (localIdx < 0 || uidIdx < 0) {
                        return@use null
                    }
                    ProcNetTable(path, localIdx, uidIdx, columns.indexOf("inode"))
                }
            }
            .getOrNull()
    }

    private fun prunePortCache() {
        val now = System.currentTimeMillis()
        val iterator = portUidCache.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value.first > PORT_CACHE_TTL_MS) {
                iterator.remove()
            }
        }
    }
}

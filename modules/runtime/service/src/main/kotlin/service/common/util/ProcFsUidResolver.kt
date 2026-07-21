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
import java.util.concurrent.ConcurrentHashMap

/**
 * Fallback UID resolver that reads [procfs] when [ConnectivityManager.getConnectionOwnerUid]
 * returns -1.
 *
 * Resolution is two-tier: port cache → on-demand procfs read.  The port cache avoids
 * redundant file reads for short-lived sockets that appear in rapid succession.
 * No background threads — procfs is only read when a JNI callback calls
 * [resolveByProtocol] and the port is not already cached.
 */
internal object ProcFsUidResolver {
    // ── UDP indices ────────────────────────────────────────────────
    private var udpLocalAddrIndex = -1
    private var udpUidIndex = -1
    private var udp6LocalAddrIndex = -1
    private var udp6UidIndex = -1

    // ── TCP indices ────────────────────────────────────────────────
    private var tcpLocalAddrIndex = -1
    private var tcpUidIndex = -1
    private var tcp6LocalAddrIndex = -1
    private var tcp6UidIndex = -1

    /**
     * Port-level UID cache for short-lived sockets that disappear from
     * procfs before an on-demand read can find them.
     * Key: source port, Value: Pair(resolutionTimestamp, uid)
     */
    private val portUidCache = ConcurrentHashMap<Int, Pair<Long, Int>>()

    private const val PORT_CACHE_TTL_MS = 5_000L

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
        val uid = resolveUdpUidInternal(source)
        if (uid > 0) {
            portUidCache[port] = Pair(System.currentTimeMillis(), uid)
            if (portUidCache.size > 2000) {
                prunePortCache()
            }
        }
        return uid
    }

    private fun resolveUdpUidInternal(source: InetSocketAddress): Int {
        val address = source.address
        val port = source.port
        return when (address) {
            is Inet6Address -> {
                ensureUdp6Indices()
                queryProcFs("/proc/net/udp6", address.address, port,
                    udp6LocalAddrIndex, udp6UidIndex)
            }
            is Inet4Address -> {
                ensureUdpIndices()
                queryProcFs("/proc/net/udp", address.address, port,
                    udpLocalAddrIndex, udpUidIndex)
            }
            else -> -1
        }
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
        val uid = resolveTcpUidInternal(source)
        if (uid > 0) {
            portUidCache[port] = Pair(System.currentTimeMillis(), uid)
            if (portUidCache.size > 2000) {
                prunePortCache()
            }
        }
        return uid
    }

    private fun resolveTcpUidInternal(source: InetSocketAddress): Int {
        val address = source.address
        val port = source.port
        return when (address) {
            is Inet6Address -> {
                ensureTcp6Indices()
                queryProcFs("/proc/net/tcp6", address.address, port,
                    tcp6LocalAddrIndex, tcp6UidIndex)
            }
            is Inet4Address -> {
                ensureTcpIndices()
                queryProcFs("/proc/net/tcp", address.address, port,
                    tcpLocalAddrIndex, tcpUidIndex)
            }
            else -> -1
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  Shared infra
    // ═══════════════════════════════════════════════════════════════

    private fun queryProcFs(
        path: String,
        ip: ByteArray,
        port: Int,
        localAddrIdx: Int,
        uidIdx: Int,
    ): Int {
        if (localAddrIdx < 0 || uidIdx < 0) return -1
        val localHex = formatLocalAddress(ip, port)
        return runCatching {
            File(path).bufferedReader().use { reader ->
                reader.readLine() ?: return@use -1
                reader.lineSequence().forEach { line ->
                    val fields = line.trim().split("\\s+".toRegex())
                    if (fields.size > maxOf(localAddrIdx, uidIdx) &&
                        fields[localAddrIdx].equals(localHex, ignoreCase = true)
                    ) {
                        return@use fields[uidIdx].toIntOrNull() ?: -1
                    }
                }
                -1
            }
        }.getOrDefault(-1)
    }

    /** Format IP + port as the `local_address` column: IP little-endian hex, port big-endian hex. */
    private fun formatLocalAddress(ip: ByteArray, port: Int): String {
        val sb = StringBuilder(ip.size * 2 + 5)
        var i = 0
        while (i < ip.size) {
            val groupEnd = minOf(i + 4, ip.size)
            for (j in groupEnd - 1 downTo i) {
                sb.append(String.format("%02X", ip[j].toInt() and 0xFF))
            }
            i = groupEnd
        }
        sb.append(':')
        sb.append(String.format("%04X", port))
        return sb.toString()
    }

    // ── Index helpers ─────────────────────────────────────────────

    private fun ensureUdpIndices() {
        if (udpLocalAddrIndex >= 0 && udpUidIndex >= 0) return
        val (local, uid) = parseIndices("/proc/net/udp")
        if (local >= 0 && uid >= 0) {
            udpLocalAddrIndex = local
            udpUidIndex = uid
        }
    }

    private fun ensureUdp6Indices() {
        if (udp6LocalAddrIndex >= 0 && udp6UidIndex >= 0) return
        val (local, uid) = parseIndices("/proc/net/udp6")
        if (local >= 0 && uid >= 0) {
            udp6LocalAddrIndex = local
            udp6UidIndex = uid
        }
    }

    private fun ensureTcpIndices() {
        if (tcpLocalAddrIndex >= 0 && tcpUidIndex >= 0) return
        val (local, uid) = parseIndices("/proc/net/tcp")
        if (local >= 0 && uid >= 0) {
            tcpLocalAddrIndex = local
            tcpUidIndex = uid
        }
    }

    private fun ensureTcp6Indices() {
        if (tcp6LocalAddrIndex >= 0 && tcp6UidIndex >= 0) return
        val (local, uid) = parseIndices("/proc/net/tcp6")
        if (local >= 0 && uid >= 0) {
            tcp6LocalAddrIndex = local
            tcp6UidIndex = uid
        }
    }

    private fun parseIndices(path: String): Pair<Int, Int> {
        return runCatching {
            File(path).bufferedReader().use { reader ->
                val header = reader.readLine() ?: return@use Pair(-1, -1)
                val columns = header.trim().split("\\s+".toRegex())
                val localIdx = columns.indexOfFirst { it == "local_address" }
                val uidIdx = columns.indexOfFirst { it == "uid" }
                Pair(localIdx, uidIdx)
            }
        }.getOrDefault(Pair(-1, -1))
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

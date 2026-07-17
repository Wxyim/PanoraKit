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

/**
 * Fallback UID resolver that reads [procfs] when [ConnectivityManager.getConnectionOwnerUid]
 * returns -1 (common for UDP sockets).
 *
 * Mirrors the Go-side [[platform.QuerySocketUidFromProcFs]] logic but runs on the Kotlin
 * side so it can serve as a fallback without native recompilation.
 */
internal object ProcFsUidResolver {
    private var udpLocalAddrIndex = -1
    private var udpUidIndex = -1
    private var udp6LocalAddrIndex = -1
    private var udp6UidIndex = -1

    /**
     * Try to resolve the UID of a UDP socket via `/proc/net/udp[6]`.
     *
     * @return UID of the owning process, or -1 when the socket is not found or procfs is
     *   unavailable.
     */
    fun resolveUdpUid(source: InetSocketAddress): Int {
        val address = source.address
        val port = source.port
        return when (address) {
            is Inet6Address -> {
                ensureUdp6Indices()
                queryProcFs("/proc/net/udp6", address.address, port, udp6LocalAddrIndex, udp6UidIndex)
            }
            is Inet4Address -> {
                ensureUdpIndices()
                queryProcFs("/proc/net/udp", address.address, port, udpLocalAddrIndex, udpUidIndex)
            }
            else -> -1
        }
    }

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
                // Skip header line
                reader.readLine() ?: return@use -1
                reader.lineSequence().forEach { line ->
                    // Split preserving the column gaps while being robust to
                    // kernel version differences in whitespace.
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

    /**
     * Format IP + port as the `local_address` column in `/proc/net/`:
     *   - IP: native-endian hex (little-endian on ARM/Android)
     *   - Port: big-endian hex
     */
    private fun formatLocalAddress(ip: ByteArray, port: Int): String {
        val sb = StringBuilder(ip.size * 2 + 5)
        // Native-endian: reverse each 4-byte group for little-endian
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

    /**
     * Parse the `/proc/net/` header to find column positions.
     *
     * Kernel versions differ in the columns they expose (`tx_queue`, `rx_queue`,
     * `tr`, `tm->when`, `retrnsmt` are optional), so column indices must be
     * resolved dynamically from the header.  We use the actual (data-line)
     * column index because header and data rows share the same layout — no
     * normalisation needed.
     */
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
}

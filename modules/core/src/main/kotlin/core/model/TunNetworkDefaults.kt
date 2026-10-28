/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * MonadBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.core.model

import java.net.InetAddress

/**
 * Addresses assigned to MonadBox-owned TUN devices.
 *
 * Keep these values in one place so startup connection reconciliation and both TUN transports agree
 * on what "inside the tunnel" means. The VPN transport uses the same addresses for its
 * `VpnService.Builder`, while root mode serialises them into [RootTunConfig].
 */
object TunNetworkDefaults {
    const val IPV4_GATEWAY = "172.19.0.1"
    const val IPV4_PREFIX_LENGTH = 30
    const val IPV4_CIDR = "$IPV4_GATEWAY/$IPV4_PREFIX_LENGTH"

    const val IPV6_GATEWAY = "fdfe:dcba:9876::1"
    const val IPV6_PREFIX_LENGTH = 126
    const val IPV6_CIDR = "$IPV6_GATEWAY/$IPV6_PREFIX_LENGTH"

    const val IPV4_PORTAL = "172.19.0.2"
    const val IPV6_PORTAL = "fdfe:dcba:9876::2"

    val CIDRS: List<String> = listOf(IPV4_CIDR, IPV6_CIDR)

    private data class Prefix(val network: ByteArray, val prefixLength: Int) {
        fun contains(address: ByteArray): Boolean {
            if (address.size != network.size) return false

            val fullBytes = prefixLength / 8
            val remainingBits = prefixLength % 8
            for (index in 0 until fullBytes) {
                if (address[index] != network[index]) return false
            }
            if (remainingBits == 0) return true

            val mask = (0xff shl (8 - remainingBits)) and 0xff
            return (address[fullBytes].toInt() and mask) == (network[fullBytes].toInt() and mask)
        }
    }

    private val prefixes: List<Prefix> by lazy {
        CIDRS.mapNotNull { raw ->
            val value = raw.trim()
            val slash = value.lastIndexOf('/')
            if (slash <= 0 || slash == value.lastIndex) return@mapNotNull null
            val network =
                runCatching { InetAddress.getByName(value.substring(0, slash)) }.getOrNull()
                    ?: return@mapNotNull null
            val prefixLength =
                value.substring(slash + 1).toIntOrNull()?.takeIf {
                    it in 0..(network.address.size * 8)
                } ?: return@mapNotNull null
            Prefix(network = network.address, prefixLength = prefixLength)
        }
    }

    /** True when [raw] is one of the addresses or belongs to one of the TUN subnets. */
    fun isTunnelAddress(raw: String): Boolean {
        val value = raw.trim().substringBefore('%')
        if (value.isEmpty()) return false
        val address = runCatching { InetAddress.getByName(value) }.getOrNull() ?: return false
        return prefixes.any { it.contains(address.address) }
    }
}

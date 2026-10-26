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
}

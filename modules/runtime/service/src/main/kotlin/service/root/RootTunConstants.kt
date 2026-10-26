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

package com.github.nomadboxlab.monadbox.service.root

import com.github.nomadboxlab.monadbox.core.model.TunNetworkDefaults

internal object RootTunConstants {
    const val IF_NAME = "monad"
    const val MTU = 1500

    const val INET4 = TunNetworkDefaults.IPV4_CIDR
    const val INET6 = TunNetworkDefaults.IPV6_CIDR

    const val FAKE_IP_RANGE = "198.18.0.1/16"
    const val FAKE_IP_RANGE6 = "fc00::/18"

    val DEFAULT_DNS_HIJACK = listOf("any:53", "tcp://any:53")
}

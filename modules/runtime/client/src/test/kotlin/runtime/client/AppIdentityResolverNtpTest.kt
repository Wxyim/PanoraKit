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

package com.github.nomadboxlab.monadbox.runtime.client

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppIdentityResolverNtpTest {
    @Test
    fun udpDestinationPort123IsNtp() {
        assertTrue(
            buildJsonObject {
                    put("network", "udp")
                    put("destinationPort", "123")
                }
                .isNtpConnection()
        )
    }

    @Test
    fun tcpDestinationPort123IsNotNtp() {
        assertFalse(
            buildJsonObject {
                    put("network", "tcp")
                    put("destinationPort", "123")
                }
                .isNtpConnection()
        )
    }

    @Test
    fun otherUdpPortIsNotNtp() {
        assertFalse(
            buildJsonObject {
                    put("network", "udp")
                    put("destinationPort", "23000")
                }
                .isNtpConnection()
        )
    }
}

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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppIdentityResolverRemoteSourceTest {
    @Test
    fun tunIpv6GatewayIsNotTreatedAsARemoteDevice() {
        assertFalse(isRemoteSourceAddress("fdfe:dcba:9876::1"))
    }

    @Test
    fun tunIpv6PortalIsNotTreatedAsARemoteDevice() {
        assertFalse(isRemoteSourceAddress("fdfe:dcba:9876::2"))
    }

    @Test
    fun tunIpv4GatewayIsNotTreatedAsARemoteDevice() {
        assertFalse(isRemoteSourceAddress("172.19.0.1"))
    }

    @Test
    fun unrelatedGlobalIpv6IsStillTreatedAsRemote() {
        assertTrue(isRemoteSourceAddress("2001:db8::1"))
    }
}

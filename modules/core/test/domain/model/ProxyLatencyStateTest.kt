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

package com.github.nomadboxlab.monadbox.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProxyLatencyStateTest {
    @Test
    fun fromObservedDelay_treats65535AsTimeoutSentinel() {
        val state = ProxyLatencyState.fromObservedDelay(65_535)

        assertTrue(state == ProxyLatencyState.Timeout)
    }

    @Test
    fun fromResolvedDelay_keeps65535AsRegularSnapshotValueUntilObserved() {
        val state = ProxyLatencyState.fromResolvedDelay(65_535)

        assertTrue(state is ProxyLatencyState.Available)
        assertEquals(65_535, (state as ProxyLatencyState.Available).delayMs)
    }

    @Test
    fun normalizeSnapshotDelay_turnsTimeoutSentinelIntoUnknownSnapshot() {
        assertEquals(0, ProxyLatencyState.normalizeSnapshotDelay(65_535))
    }
}

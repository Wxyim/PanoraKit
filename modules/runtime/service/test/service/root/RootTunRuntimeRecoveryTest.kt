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

package com.github.nomadboxlab.monadbox.service.root

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RootTunRuntimeRecoveryTest {
    @Test
    fun startingTransitionBecomesStaleAfterTenSeconds() {
        val status = RootTunStatus(state = RootTunState.Starting, startedAt = 1_000L)

        assertTrue(RootTunRuntimeRecovery.isStaleTransition(status, nowMillis = 11_000L))
    }

    @Test
    fun stoppingTransitionYoungerThanTenSecondsIsNotStale() {
        val status = RootTunStatus(state = RootTunState.Stopping, startedAt = 1_000L)

        assertFalse(RootTunRuntimeRecovery.isStaleTransition(status, nowMillis = 10_999L))
    }

    @Test
    fun terminalAndRunningStatesAreNeverStaleTransitions() {
        assertFalse(
            RootTunRuntimeRecovery.isStaleTransition(
                RootTunStatus(state = RootTunState.Running, startedAt = 1_000L),
                nowMillis = 99_000L,
            )
        )
        assertFalse(
            RootTunRuntimeRecovery.isStaleTransition(
                RootTunStatus(state = RootTunState.Idle),
                nowMillis = 99_000L,
            )
        )
    }

    @Test
    fun recoveringStateWithoutTimestampIsStale() {
        assertTrue(
            RootTunRuntimeRecovery.isStaleTransition(
                RootTunStatus(state = RootTunState.Starting),
                nowMillis = 1_000L,
            )
        )
    }
}

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

package com.github.nomadboxlab.monadbox.service

import com.github.nomadboxlab.monadbox.service.root.RootTunState
import com.github.nomadboxlab.monadbox.service.root.RootTunStatus
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProxyTileRuntimeStateTest {
    @Test
    fun rootTunRecoveringStatesRemainTransitioningForTile() {
        assertEquals(
            RuntimePhase.Starting,
            rootTunTilePhase(RootTunStatus(state = RootTunState.Starting)),
        )
        assertEquals(
            RuntimePhase.Stopping,
            rootTunTilePhase(RootTunStatus(state = RootTunState.Stopping)),
        )
    }

    @Test
    fun rootTunRuntimeReadyFallbackMapsIdleStatusToRunning() {
        assertEquals(
            RuntimePhase.Running,
            rootTunTilePhase(RootTunStatus(state = RootTunState.Idle, runtimeReady = true)),
        )
    }

    @Test
    fun tileTransitionGuardOnlyBlocksRecoveringPhases() {
        assertTrue(isTileTransitioning(RuntimePhase.Starting))
        assertTrue(isTileTransitioning(RuntimePhase.Stopping))
        assertFalse(isTileTransitioning(RuntimePhase.Running))
        assertFalse(isTileTransitioning(RuntimePhase.Failed))
        assertFalse(isTileTransitioning(RuntimePhase.Idle))
    }
}

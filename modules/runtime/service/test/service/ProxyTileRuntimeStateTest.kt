package com.github.yumelira.yumebox.service

import com.github.yumelira.yumebox.service.root.RootTunState
import com.github.yumelira.yumebox.service.root.RootTunStatus
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
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

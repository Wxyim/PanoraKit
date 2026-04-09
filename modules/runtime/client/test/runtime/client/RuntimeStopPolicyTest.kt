package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import org.junit.Assert.assertEquals
import org.junit.Test

class RuntimeStopPolicyTest {
    @Test
    fun resolvesStartingPhaseAsStaleStop() {
        assertEquals(
            RuntimeStopResolution.IgnoreAsStale,
            resolveRuntimeStopResolution(RuntimePhase.Starting, null),
        )
    }

    @Test
    fun resolvesIdleWithoutReasonAsRedundant() {
        assertEquals(
            RuntimeStopResolution.SkipAsRedundant,
            resolveRuntimeStopResolution(RuntimePhase.Idle, ""),
        )
    }

    @Test
    fun resolvesOtherStatesToIdleTransition() {
        assertEquals(
            RuntimeStopResolution.TransitionToIdle,
            resolveRuntimeStopResolution(RuntimePhase.Running, null),
        )
    }
}

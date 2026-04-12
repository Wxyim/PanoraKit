package com.github.yumelira.yumebox.service.root

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

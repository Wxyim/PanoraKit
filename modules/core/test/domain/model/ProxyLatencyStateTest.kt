package com.github.yumelira.yumebox.domain.model

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

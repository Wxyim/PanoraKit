package com.github.nomadboxlab.monadbox.service.root

import com.github.nomadboxlab.monadbox.core.model.RuntimeDataSnapshot
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class RootTunPayloadCacheTest {
    private val cache = RootTunPayloadCache()

    @Test
    fun servesCachedPayloadWhileStampIsUnchanged() {
        val stamp = longArrayOf(11L, 1L)
        val snapshot = RuntimeDataSnapshot()
        cache.put(stamp, snapshot)

        assertSame(snapshot, cache.get(longArrayOf(11L, 1L)))
    }

    @Test
    fun missesWhenRevisionAdvances() {
        cache.put(longArrayOf(11L, 1L), RuntimeDataSnapshot())

        assertNull(cache.get(longArrayOf(11L, 2L)))
    }

    @Test
    fun missesWhenAnchorChanges() {
        cache.put(longArrayOf(11L, 1L), RuntimeDataSnapshot())

        // A restarted root process reports a fresh anchor even though its revision starts over.
        assertNull(cache.get(longArrayOf(12L, 1L)))
    }

    @Test
    fun missesWhenNothingIsCachedOrCacheWasCleared() {
        assertNull(cache.get(longArrayOf(11L, 1L)))

        cache.put(longArrayOf(11L, 1L), RuntimeDataSnapshot())
        cache.clear()

        assertNull(cache.get(longArrayOf(11L, 1L)))
    }

    @Test
    fun replacesTheCachedPayloadWhenTheStampMovesOn() {
        val first = RuntimeDataSnapshot()
        val second = RuntimeDataSnapshot(proxyGroups = emptyList())
        cache.put(longArrayOf(11L, 1L), first)
        cache.put(longArrayOf(11L, 2L), second)

        assertNull(cache.get(longArrayOf(11L, 1L)))
        assertSame(second, cache.get(longArrayOf(11L, 2L)))
    }
}

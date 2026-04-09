package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.core.model.ProxyGroup
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.service.root.RootTunState
import com.github.yumelira.yumebox.service.root.RootTunStatus
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProxyFacadeRuntimeStateTest {
    @Test
    fun publishRuntimeSnapshotNormalizesRunningFlag() {
        val state =
            ProxyFacadeRuntimeState(
                initialMode = ProxyMode.Tun,
                initialRootTunStatus = RootTunStatus(),
            )

        state.publishRuntimeSnapshot(RuntimeSnapshot(phase = RuntimePhase.Running, running = false))

        assertTrue(state.runtimeSnapshot.value.running)
        assertTrue(state.isRunning.value)
    }

    @Test
    fun clearRuntimePayloadKeepsGroupsWhenRequested() {
        val state =
            ProxyFacadeRuntimeState(
                initialMode = ProxyMode.Tun,
                initialRootTunStatus = RootTunStatus(),
            )

        state.setCurrentProfile(sampleProfile())
        state.setProxyGroups(listOf(sampleGroup()))
        state.setTrafficNow(12L)
        state.setTrafficTotal(34L)

        state.clearRuntimePayload(resetGroups = false)

        assertNull(state.currentProfile.value)
        assertEquals(1, state.proxyGroups.value.size)
        assertEquals(0L, state.trafficNow.value)
        assertEquals(0L, state.trafficTotal.value)
    }

    @Test
    fun ownerPolicyMapsConfiguredModesAndDetectedStates() {
        assertEquals(RuntimeOwner.LocalTun, ProxyFacadeOwnerPolicy.ownerForMode(ProxyMode.Tun))
        assertEquals(RuntimeOwner.LocalHttp, ProxyFacadeOwnerPolicy.ownerForMode(ProxyMode.Http))
        assertEquals(
            RuntimeOwner.RootTun,
            ProxyFacadeOwnerPolicy.detectActiveOwner(
                rootActive = true,
                localTunActive = true,
                localHttpActive = true,
            ),
        )
        assertEquals(
            ProxyMode.Http,
            ProxyFacadeOwnerPolicy.modeForOwner(RuntimeOwner.None, ProxyMode.Http),
        )
    }

    @Test
    fun previewCacheFallsBackOnlyForMatchingProfileFingerprint() {
        val cache = ProxyFacadePreviewCache()
        val profile = sampleProfile()
        val groups = listOf(sampleGroup(name = "fallback"))
        val runningSnapshot =
            RuntimeSnapshot(phase = RuntimePhase.Running, effectiveFingerprint = "fingerprint-a")

        cache.backfill(
            profile = profile,
            groups = groups,
            runtimeSnapshot = runningSnapshot,
            rootTunStatus = RootTunStatus(state = RootTunState.Running),
        )

        val cached =
            cache.fallback(
                snapshot =
                    RuntimeSnapshot(
                        phase = RuntimePhase.Idle,
                        effectiveFingerprint = "fingerprint-a",
                    ),
                profile = profile,
                rootTunStatus = RootTunStatus(state = RootTunState.Idle),
            )
        val mismatched =
            cache.fallback(
                snapshot =
                    RuntimeSnapshot(
                        phase = RuntimePhase.Idle,
                        effectiveFingerprint = "fingerprint-b",
                    ),
                profile = profile,
                rootTunStatus = RootTunStatus(state = RootTunState.Idle),
            )

        assertEquals(groups, cached)
        assertNull(mismatched)
    }

    private fun sampleProfile(): Profile {
        return Profile(
            uuid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            name = "Test",
            type = Profile.Type.File,
            source = "local",
            active = true,
            interval = 0L,
            upload = 0L,
            download = 0L,
            total = 0L,
            expire = 0L,
            updatedAt = 1234L,
        )
    }

    private fun sampleGroup(name: String = "main"): ProxyGroup {
        return ProxyGroup(
            name = name,
            type = Proxy.Type.Selector,
            proxies = emptyList(),
            now = "DIRECT",
        )
    }
}

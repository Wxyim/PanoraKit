package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RuntimeTransitionPolicyTest {
    @Test
    fun resolveStartedOwnerHonorsPriority() {
        assertEquals(
            RuntimeOwner.RootTun,
            RuntimeTransitionPolicy.resolveStartedOwner(
                forceOwner = RuntimeOwner.RootTun,
                currentOwner = RuntimeOwner.LocalTun,
                detectedOwner = RuntimeOwner.LocalHttp,
            ),
        )

        assertEquals(
            RuntimeOwner.LocalTun,
            RuntimeTransitionPolicy.resolveStartedOwner(
                forceOwner = null,
                currentOwner = RuntimeOwner.LocalTun,
                detectedOwner = RuntimeOwner.LocalHttp,
            ),
        )

        assertEquals(
            RuntimeOwner.LocalHttp,
            RuntimeTransitionPolicy.resolveStartedOwner(
                forceOwner = null,
                currentOwner = RuntimeOwner.None,
                detectedOwner = RuntimeOwner.LocalHttp,
            ),
        )
    }

    @Test
    fun normalizeSnapshotAlignsRunningFlagWithPhase() {
        val inconsistent = RuntimeSnapshot(phase = RuntimePhase.Running, running = false)
        val normalized = RuntimeTransitionPolicy.normalizeSnapshot(inconsistent)
        assertTrue(normalized.running)
    }

    @Test
    fun resolveFailureMessageFallsBackDeterministically() {
        assertEquals(
            "ROOT_RUNTIME_DISCONNECTED: root runtime failed",
            RuntimeTransitionPolicy.resolveFailureMessage(
                error = null,
                errorCode = RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
            ),
        )

        assertEquals(
            "ROOT_RUNTIME_QUERY_FAILED: root runtime failed",
            RuntimeTransitionPolicy.resolveFailureMessage(error = null, errorCode = null),
        )
    }

    @Test
    fun shouldRefreshPayloadReflectsSnapshotReadiness() {
        val runningReady =
            RuntimeSnapshot(phase = RuntimePhase.Running, profileReady = true, groupsReady = true)
        assertFalse(
            RuntimeTransitionPolicy.shouldRefreshPayload(
                snapshot = runningReady,
                groupsEmpty = false,
                profileMissing = false,
            )
        )

        val runningNotReady = runningReady.copy(profileReady = false, targetMode = ProxyMode.Http)
        assertTrue(
            RuntimeTransitionPolicy.shouldRefreshPayload(
                snapshot = runningNotReady,
                groupsEmpty = false,
                profileMissing = false,
            )
        )
    }
}

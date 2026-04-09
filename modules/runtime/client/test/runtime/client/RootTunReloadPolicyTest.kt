package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.runtime.client.root.ROOT_TUN_RELOAD_RETRY_DELAYS_MS
import com.github.yumelira.yumebox.runtime.client.root.normalizeRootTunReloadFailure
import com.github.yumelira.yumebox.runtime.client.root.shouldRunRootTunReloadAgain
import com.github.yumelira.yumebox.service.root.RootTunOperationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RootTunReloadPolicyTest {
    @Test
    fun exposesExpectedRetryBackoffSchedule() {
        assertEquals(listOf(0L, 250L, 500L, 1000L), ROOT_TUN_RELOAD_RETRY_DELAYS_MS)
    }

    @Test
    fun rerunsWhenDirtyOrPendingReasonsExist() {
        assertTrue(shouldRunRootTunReloadAgain(dirtyWhileRunning = true, pendingReasonCount = 0))
        assertTrue(shouldRunRootTunReloadAgain(dirtyWhileRunning = false, pendingReasonCount = 1))
        assertFalse(shouldRunRootTunReloadAgain(dirtyWhileRunning = false, pendingReasonCount = 0))
    }

    @Test
    fun normalizesReloadFailureDefaults() {
        assertEquals(
            RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED.name to "root runtime reload failed",
            normalizeRootTunReloadFailure(RootTunOperationResult(success = false)),
        )
    }

    @Test
    fun preservesExplicitReloadFailureDetails() {
        assertEquals(
            RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED.name to "binder gone",
            normalizeRootTunReloadFailure(
                RootTunOperationResult(
                    success = false,
                    errorCode = RuntimeGatewayErrorCode.ROOT_RUNTIME_DISCONNECTED,
                    error = "binder gone",
                )
            ),
        )
    }
}

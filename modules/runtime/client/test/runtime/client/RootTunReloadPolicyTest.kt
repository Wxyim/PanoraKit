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

package com.github.nomadboxlab.monadbox.runtime.client

import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.runtime.client.root.ROOT_TUN_RELOAD_RETRY_DELAYS_MS
import com.github.nomadboxlab.monadbox.runtime.client.root.normalizeRootTunReloadFailure
import com.github.nomadboxlab.monadbox.runtime.client.root.shouldRunRootTunReloadAgain
import com.github.nomadboxlab.monadbox.service.root.RootTunOperationResult
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

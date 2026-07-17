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

import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.domain.model.ProductLifecycleState
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeOwner
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
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
    fun startedSnapshot_marksLocalOwnersTransportReady() {
        val localSnapshot =
            RuntimeTransitionPolicy.startedSnapshot(
                currentSnapshot = RuntimeSnapshot(phase = RuntimePhase.Starting),
                owner = RuntimeOwner.LocalTun,
                targetMode = ProxyMode.Tun,
            )

        assertEquals(RuntimePhase.Running, localSnapshot.phase)
        assertTrue(localSnapshot.configReady)
        assertTrue(localSnapshot.transportReady)

        val rootSnapshot =
            RuntimeTransitionPolicy.startedSnapshot(
                currentSnapshot = RuntimeSnapshot(phase = RuntimePhase.Starting),
                owner = RuntimeOwner.RootTun,
                targetMode = ProxyMode.RootTun,
            )

        assertFalse(rootSnapshot.configReady)
        assertFalse(rootSnapshot.transportReady)
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

    @Test
    fun lifecycleState_mapsRuntimeReadinessIntoProductState() {
        assertEquals(
            ProductLifecycleState.Preparing,
            RuntimeStateMapper.lifecycleState(RuntimeSnapshot(phase = RuntimePhase.Starting)),
        )
        assertEquals(
            ProductLifecycleState.Degraded,
            RuntimeStateMapper.lifecycleState(RuntimeSnapshot(phase = RuntimePhase.Running)),
        )
        assertEquals(
            ProductLifecycleState.Active,
            RuntimeStateMapper.lifecycleState(
                RuntimeSnapshot(
                    phase = RuntimePhase.Running,
                    profileReady = true,
                    groupsReady = true,
                    trafficReady = true,
                    configReady = true,
                    transportReady = true,
                )
            ),
        )
    }
}

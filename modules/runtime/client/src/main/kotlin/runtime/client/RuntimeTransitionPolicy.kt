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
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeOwner
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot

internal object RuntimeTransitionPolicy {
    fun resolveStartedOwner(
        forceOwner: RuntimeOwner?,
        currentOwner: RuntimeOwner,
        detectedOwner: RuntimeOwner,
    ): RuntimeOwner {
        return forceOwner ?: currentOwner.takeIf { it != RuntimeOwner.None } ?: detectedOwner
    }

    fun startedSnapshot(
        currentSnapshot: RuntimeSnapshot,
        owner: RuntimeOwner,
        targetMode: ProxyMode,
    ): RuntimeSnapshot {
        return currentSnapshot.copy(
            owner = owner,
            phase = RuntimePhase.Running,
            targetMode = targetMode,
            lastError = null,
        )
    }

    fun normalizeSnapshot(snapshot: RuntimeSnapshot): RuntimeSnapshot {
        return if (snapshot.running == snapshot.phase.running) {
            snapshot
        } else {
            snapshot.copy(running = snapshot.phase.running)
        }
    }

    fun resolveFailureMessage(error: String?, errorCode: RuntimeGatewayErrorCode?): String {
        return error?.takeIf { it.isNotBlank() }
            ?: errorCode?.let { "${it.name}: root runtime failed" }
            ?: "${RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED.name}: root runtime failed"
    }

    fun shouldRefreshPayload(
        snapshot: RuntimeSnapshot,
        groupsEmpty: Boolean,
        profileMissing: Boolean,
    ): Boolean {
        return snapshot.phase == RuntimePhase.Running &&
            (!snapshot.profileReady || !snapshot.groupsReady || groupsEmpty || profileMissing)
    }
}

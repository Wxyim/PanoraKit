package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot

internal object RuntimeTransitionPolicy {
    fun resolveStartedOwner(
        forceOwner: RuntimeOwner?,
        currentOwner: RuntimeOwner,
        detectedOwner: RuntimeOwner,
    ): RuntimeOwner {
        return forceOwner
            ?: currentOwner.takeIf { it != RuntimeOwner.None }
            ?: detectedOwner
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
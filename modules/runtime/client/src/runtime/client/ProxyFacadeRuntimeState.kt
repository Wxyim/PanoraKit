package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.core.model.Traffic
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.domain.model.ProxyGroupInfo
import com.github.yumelira.yumebox.service.root.RootTunStatus
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private fun <T> MutableStateFlow<T>.setIfChanged(newValue: T): Boolean {
    if (value == newValue) return false
    value = newValue
    return true
}

internal class ProxyFacadeRuntimeState(
    initialMode: ProxyMode,
    initialRootTunStatus: RootTunStatus,
) {
    private val rootTunStatusMutable = MutableStateFlow(initialRootTunStatus)
    val rootTunStatus: StateFlow<RootTunStatus> = rootTunStatusMutable.asStateFlow()

    private val runtimeSnapshotMutable =
        MutableStateFlow(RuntimeStateMapper.idleSnapshot(initialMode))
    val runtimeSnapshot: StateFlow<RuntimeSnapshot> = runtimeSnapshotMutable.asStateFlow()

    private val isRunningMutable = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = isRunningMutable.asStateFlow()

    private val proxyGroupsMutable = MutableStateFlow<List<ProxyGroupInfo>>(emptyList())
    val proxyGroups: StateFlow<List<ProxyGroupInfo>> = proxyGroupsMutable.asStateFlow()

    private val currentProfileMutable = MutableStateFlow<Profile?>(null)
    val currentProfile: StateFlow<Profile?> = currentProfileMutable.asStateFlow()

    private val trafficNowMutable = MutableStateFlow(0L)
    val trafficNow: StateFlow<Traffic> = trafficNowMutable.asStateFlow()

    private val trafficTotalMutable = MutableStateFlow(0L)
    val trafficTotal: StateFlow<Traffic> = trafficTotalMutable.asStateFlow()

    private var generationCounter = 0L

    fun applyRootTunStatus(status: RootTunStatus) {
        rootTunStatusMutable.setIfChanged(status)
    }

    fun publishRuntimeSnapshot(snapshot: RuntimeSnapshot) {
        val normalized = RuntimeTransitionPolicy.normalizeSnapshot(snapshot)
        runtimeSnapshotMutable.setIfChanged(normalized)
        isRunningMutable.setIfChanged(normalized.running)
    }

    fun nextGeneration(): Long {
        generationCounter += 1L
        return generationCounter
    }

    fun clearRuntimePayload(resetGroups: Boolean = true) {
        currentProfileMutable.setIfChanged(null)
        if (resetGroups) {
            proxyGroupsMutable.setIfChanged(emptyList())
        }
        trafficNowMutable.setIfChanged(0L)
        trafficTotalMutable.setIfChanged(0L)
    }

    fun setCurrentProfile(profile: Profile?) {
        currentProfileMutable.setIfChanged(profile)
    }

    fun setProxyGroups(groups: List<ProxyGroupInfo>) {
        proxyGroupsMutable.setIfChanged(groups)
    }

    fun setTrafficNow(traffic: Long) {
        trafficNowMutable.setIfChanged(traffic)
    }

    fun setTrafficTotal(traffic: Long) {
        trafficTotalMutable.setIfChanged(traffic)
    }

    fun setIsRunning(isRunning: Boolean) {
        isRunningMutable.setIfChanged(isRunning)
    }

    fun updateProfileReady(profile: Profile?) {
        val snapshot = runtimeSnapshotMutable.value
        publishRuntimeSnapshot(
            snapshot.copy(
                profileReady = profile != null,
                profileUuid = profile?.uuid?.toString() ?: snapshot.profileUuid,
                profileName = profile?.name ?: snapshot.profileName,
            )
        )
    }

    fun updateGroupsReady(ready: Boolean) {
        publishRuntimeSnapshot(runtimeSnapshotMutable.value.copy(groupsReady = ready))
    }

    fun updateTrafficReady() {
        if (!runtimeSnapshotMutable.value.trafficReady) {
            publishRuntimeSnapshot(runtimeSnapshotMutable.value.copy(trafficReady = true))
        }
    }
}

internal object ProxyFacadeOwnerPolicy {
    fun ownerForMode(mode: ProxyMode): RuntimeOwner {
        return when (mode) {
            ProxyMode.Tun -> RuntimeOwner.LocalTun
            ProxyMode.Http -> RuntimeOwner.LocalHttp
            ProxyMode.RootTun -> RuntimeOwner.RootTun
        }
    }

    fun modeForOwner(owner: RuntimeOwner, configuredMode: ProxyMode): ProxyMode {
        return when (owner) {
            RuntimeOwner.LocalTun -> ProxyMode.Tun
            RuntimeOwner.LocalHttp -> ProxyMode.Http
            RuntimeOwner.RootTun -> ProxyMode.RootTun
            RuntimeOwner.None -> configuredMode
        }
    }

    fun detectActiveOwner(
        rootActive: Boolean,
        localTunActive: Boolean,
        localHttpActive: Boolean,
    ): RuntimeOwner {
        return when {
            rootActive -> RuntimeOwner.RootTun
            localTunActive -> RuntimeOwner.LocalTun
            localHttpActive -> RuntimeOwner.LocalHttp
            else -> RuntimeOwner.None
        }
    }
}

internal class ProxyFacadePreviewCache {
    private data class Key(
        val profileId: UUID,
        val profileUpdatedAt: Long,
        val excludeNotSelectable: Boolean,
        val overrideSignature: String,
    )

    private data class Entry(val key: Key, val groups: List<ProxyGroupInfo>)

    private var entry: Entry? = null

    fun backfill(
        profile: Profile?,
        groups: List<ProxyGroupInfo>,
        runtimeSnapshot: RuntimeSnapshot,
        rootTunStatus: RootTunStatus,
    ) {
        val currentProfile = profile ?: return
        val key =
            previewCacheKey(
                profile = currentProfile,
                excludeNotSelectable = false,
                runtimeSnapshot = runtimeSnapshot,
                rootTunStatus = rootTunStatus,
            )
        val existing = entry
        if (existing != null && existing.key == key && existing.groups == groups) return
        entry = Entry(key = key, groups = groups)
    }

    fun fallback(
        snapshot: RuntimeSnapshot,
        profile: Profile?,
        rootTunStatus: RootTunStatus,
    ): List<ProxyGroupInfo>? {
        if (snapshot.phase.running) return null
        val currentProfile = profile ?: return entry?.groups
        val key =
            previewCacheKey(
                profile = currentProfile,
                excludeNotSelectable = false,
                runtimeSnapshot = snapshot,
                rootTunStatus = rootTunStatus,
            )
        return entry?.takeIf { it.key == key }?.groups
    }

    private fun previewCacheKey(
        profile: Profile,
        excludeNotSelectable: Boolean,
        runtimeSnapshot: RuntimeSnapshot,
        rootTunStatus: RootTunStatus,
    ): Key {
        val overrideSignature =
            runtimeSnapshot.effectiveFingerprint?.takeIf { it.isNotBlank() }
                ?: rootTunStatus.overrideFingerprint?.takeIf { it.isNotBlank() }
                ?: "profile-${profile.updatedAt}"
        return Key(
            profileId = profile.uuid,
            profileUpdatedAt = profile.updatedAt,
            excludeNotSelectable = excludeNotSelectable,
            overrideSignature = overrideSignature,
        )
    }
}

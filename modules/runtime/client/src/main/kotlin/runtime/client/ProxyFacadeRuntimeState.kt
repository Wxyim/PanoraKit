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

import com.github.nomadboxlab.monadbox.core.model.Traffic
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo
import com.github.nomadboxlab.monadbox.service.root.RootTunStatus
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeOwner
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
import java.io.File
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private fun <T> MutableStateFlow<T>.setIfChanged(newValue: T): Boolean {
    if (value == newValue) return false
    value = newValue
    return true
}

private val proxyFacadePreviewCacheJson =
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

enum class ProxyGroupsLoadState {
    NotLoaded,
    Loading,
    Ready,
    Stale,
    Empty,
    Error,
}

internal class ProxyFacadeRuntimeState(
    initialMode: ProxyMode,
    initialRootTunStatus: RootTunStatus,
) {
    private val stateLock = Any()
    private val rootTunStatusMutable = MutableStateFlow(initialRootTunStatus)
    val rootTunStatus: StateFlow<RootTunStatus> = rootTunStatusMutable.asStateFlow()

    private val runtimeSnapshotMutable =
        MutableStateFlow(RuntimeStateMapper.idleSnapshot(initialMode))
    val runtimeSnapshot: StateFlow<RuntimeSnapshot> = runtimeSnapshotMutable.asStateFlow()

    private val isRunningMutable = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = isRunningMutable.asStateFlow()

    private val proxyGroupsMutable = MutableStateFlow<List<ProxyGroupInfo>>(emptyList())
    val proxyGroups: StateFlow<List<ProxyGroupInfo>> = proxyGroupsMutable.asStateFlow()

    private val proxyGroupsLoadStateMutable =
        MutableStateFlow(ProxyGroupsLoadState.NotLoaded)
    val proxyGroupsLoadState: StateFlow<ProxyGroupsLoadState> =
        proxyGroupsLoadStateMutable.asStateFlow()

    private val currentProfileMutable = MutableStateFlow<Profile?>(null)
    val currentProfile: StateFlow<Profile?> = currentProfileMutable.asStateFlow()
    private var proxyGroupsProfileId: UUID? = null
    private var proxyGroupsProfileUpdatedAt: Long? = null

    private val trafficNowMutable = MutableStateFlow(0L)
    val trafficNow: StateFlow<Traffic> = trafficNowMutable.asStateFlow()

    private val trafficTotalMutable = MutableStateFlow(0L)
    val trafficTotal: StateFlow<Traffic> = trafficTotalMutable.asStateFlow()

    private var generationCounter = 0L
    private var proxyGroupsPreviewEpoch = 0L

    fun applyRootTunStatus(status: RootTunStatus) {
        rootTunStatusMutable.setIfChanged(status)
    }

    fun publishRuntimeSnapshot(snapshot: RuntimeSnapshot) {
        synchronized(stateLock) {
            val normalized = RuntimeTransitionPolicy.normalizeSnapshot(snapshot)
            runtimeSnapshotMutable.setIfChanged(normalized)
            isRunningMutable.setIfChanged(normalized.running)
        }
    }

    fun nextGeneration(): Long {
        return synchronized(stateLock) {
            generationCounter += 1L
            generationCounter
        }
    }

    fun currentPreviewEpoch(): Long {
        return synchronized(stateLock) { proxyGroupsPreviewEpoch }
    }

    fun clearRuntimePayload(resetGroups: Boolean = true) {
        synchronized(stateLock) {
            currentProfileMutable.setIfChanged(null)
            if (resetGroups) {
                proxyGroupsMutable.setIfChanged(emptyList())
                proxyGroupsLoadStateMutable.setIfChanged(ProxyGroupsLoadState.NotLoaded)
                proxyGroupsProfileId = null
                proxyGroupsProfileUpdatedAt = null
            }
            trafficNowMutable.setIfChanged(0L)
            trafficTotalMutable.setIfChanged(0L)
        }
    }

    fun setCurrentProfile(profile: Profile?) {
        synchronized(stateLock) {
            if (
                profile != null &&
                    proxyGroupsMutable.value.isNotEmpty() &&
                    (proxyGroupsProfileId == null ||
                        proxyGroupsProfileId != profile.uuid ||
                        proxyGroupsProfileUpdatedAt != profile.updatedAt)
            ) {
                proxyGroupsMutable.setIfChanged(emptyList())
                proxyGroupsLoadStateMutable.setIfChanged(ProxyGroupsLoadState.NotLoaded)
                proxyGroupsProfileId = null
                proxyGroupsProfileUpdatedAt = null
            }
            currentProfileMutable.setIfChanged(profile)
        }
    }

    fun canKeepProxyGroupsFor(profile: Profile): Boolean {
        return synchronized(stateLock) {
            proxyGroupsProfileId == profile.uuid &&
                proxyGroupsProfileUpdatedAt == profile.updatedAt &&
                proxyGroupsMutable.value.isNotEmpty()
        }
    }

    fun setProxyGroups(groups: List<ProxyGroupInfo>) {
        synchronized(stateLock) {
            proxyGroupsMutable.setIfChanged(groups)
            currentProfileMutable.value?.let { profile ->
                proxyGroupsProfileId = profile.uuid
                proxyGroupsProfileUpdatedAt = profile.updatedAt
            }
            proxyGroupsLoadStateMutable.setIfChanged(
                if (groups.isEmpty()) ProxyGroupsLoadState.Empty
                else ProxyGroupsLoadState.Ready
            )
        }
    }

    /**
     * Clears only the rendered proxy-group preview. Persisted selector
     * selections live in SelectionDao and are intentionally not touched here.
     */
    fun clearProxyGroupsForPreview() {
        synchronized(stateLock) {
            proxyGroupsMutable.setIfChanged(emptyList())
            proxyGroupsLoadStateMutable.setIfChanged(ProxyGroupsLoadState.NotLoaded)
            proxyGroupsProfileId = null
            proxyGroupsProfileUpdatedAt = null
            // Invalidate any refresh that started before this clear. Rendered
            // groups are mode-specific; a result captured under the previous
            // mode must not resurrect the stale preview.
            proxyGroupsPreviewEpoch += 1L
        }
    }

    fun markProxyGroupsLoading() {
        synchronized(stateLock) {
            proxyGroupsLoadStateMutable.setIfChanged(
                if (proxyGroupsMutable.value.isEmpty()) {
                    ProxyGroupsLoadState.Loading
                } else {
                    ProxyGroupsLoadState.Stale
                }
            )
        }
    }

    fun markProxyGroupsError() {
        synchronized(stateLock) {
            proxyGroupsLoadStateMutable.setIfChanged(
                if (proxyGroupsMutable.value.isEmpty()) {
                    ProxyGroupsLoadState.Error
                } else {
                    ProxyGroupsLoadState.Stale
                }
            )
        }
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
        synchronized(stateLock) {
            val snapshot = runtimeSnapshotMutable.value
            publishRuntimeSnapshot(
                snapshot.copy(
                    profileReady = profile != null,
                    profileUuid = profile?.uuid?.toString() ?: snapshot.profileUuid,
                    profileName = profile?.name ?: snapshot.profileName,
                )
            )
        }
    }

    fun updateGroupsReady(ready: Boolean) {
        synchronized(stateLock) {
            publishRuntimeSnapshot(runtimeSnapshotMutable.value.copy(groupsReady = ready))
        }
    }

    fun updateTrafficReady() {
        synchronized(stateLock) {
            if (!runtimeSnapshotMutable.value.trafficReady) {
                publishRuntimeSnapshot(runtimeSnapshotMutable.value.copy(trafficReady = true))
            }
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

internal class ProxyFacadePreviewCache(private val diskFile: File? = null) {
    private data class Key(
        val profileId: UUID,
        val profileUpdatedAt: Long,
        val excludeNotSelectable: Boolean,
        val overrideSignature: String,
    )

    private data class Entry(val key: Key, val groups: List<ProxyGroupInfo>)

    @Serializable
    private data class PersistedEntry(
        val profileId: String,
        val profileUpdatedAt: Long,
        val overrideSignature: String,
        val groups: List<ProxyGroupInfo>,
    )

    private var entry: Entry? = null

    @Synchronized
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
        persist(key, groups)
    }

    @Synchronized
    fun restore(
        profile: Profile?,
        runtimeSnapshot: RuntimeSnapshot,
        rootTunStatus: RootTunStatus,
    ): List<ProxyGroupInfo>? {
        val currentProfile = profile ?: return null
        val key =
            previewCacheKey(
                profile = currentProfile,
                excludeNotSelectable = false,
                runtimeSnapshot = runtimeSnapshot,
                rootTunStatus = rootTunStatus,
            )
        entry
            ?.takeIf {
                it.key.profileId == key.profileId &&
                    it.key.profileUpdatedAt == key.profileUpdatedAt &&
                    overrideSignatureMatches(
                        it.key.overrideSignature,
                        key.overrideSignature,
                        key.profileUpdatedAt,
                    )
            }
            ?.let { return it.groups }

        val file = diskFile ?: return null
        val persisted =
            runCatching {
                proxyFacadePreviewCacheJson.decodeFromString<PersistedEntry>(file.readText())
            }.getOrNull() ?: return null
        if (
            persisted.profileId != key.profileId.toString() ||
                persisted.profileUpdatedAt != key.profileUpdatedAt ||
                !overrideSignatureMatches(
                    persisted.overrideSignature,
                    key.overrideSignature,
                    key.profileUpdatedAt,
                )
        ) {
            return null
        }

        val restored = Entry(key = key, groups = persisted.groups)
        entry = restored
        return restored.groups
    }

    @Synchronized
    fun fallback(
        snapshot: RuntimeSnapshot,
        profile: Profile?,
        rootTunStatus: RootTunStatus,
    ): List<ProxyGroupInfo>? {
        // During local runtime startup the old preview is still valid for the
        // same profile/fingerprint. Keep it visible until runtime payload is
        // available instead of replacing it with a transient empty response.
        if (snapshot.phase.running && snapshot.groupsReady) return null
        val currentProfile = profile ?: return null
        val key =
            previewCacheKey(
                profile = currentProfile,
                excludeNotSelectable = false,
                runtimeSnapshot = snapshot,
                rootTunStatus = rootTunStatus,
            )
        return entry
            ?.takeIf {
                it.key.profileId == key.profileId &&
                    it.key.profileUpdatedAt == key.profileUpdatedAt &&
                    overrideSignatureMatches(
                        it.key.overrideSignature,
                        key.overrideSignature,
                        key.profileUpdatedAt,
                    )
            }
            ?.groups
    }

    @Synchronized
    fun invalidate() {
        entry = null
        diskFile?.let { file ->
            runCatching {
                file.delete()
                File(file.parentFile, "${file.name}.tmp").delete()
            }
        }
    }

    private fun persist(key: Key, groups: List<ProxyGroupInfo>) {
        val file = diskFile ?: return
        runCatching {
            file.parentFile?.mkdirs()
            val payload =
                PersistedEntry(
                    profileId = key.profileId.toString(),
                    profileUpdatedAt = key.profileUpdatedAt,
                    overrideSignature = key.overrideSignature,
                    groups = groups,
                )
            val encoded = proxyFacadePreviewCacheJson.encodeToString(payload)
            val temp = File(file.parentFile, "${file.name}.tmp")
            temp.writeText(encoded)
            if (!temp.renameTo(file)) {
                temp.delete()
            }
        }
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

    private fun overrideSignatureMatches(
        cached: String,
        requested: String,
        profileUpdatedAt: Long,
    ): Boolean {
        // Before the runtime starts there is no compiled fingerprint yet. A
        // profile-version match is still safe for the short preview window;
        // the following refresh replaces it with the current configuration.
        return cached == requested || requested == "profile-$profileUpdatedAt"
    }
}

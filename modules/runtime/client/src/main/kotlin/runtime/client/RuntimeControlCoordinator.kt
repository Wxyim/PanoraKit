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
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

enum class RuntimeMutationStatus {
    Deferred,
    Reloaded,
    Restarted,
    Started,
    Stopped,
    Updated,
}

data class RuntimeMutationResult(
    val status: RuntimeMutationStatus,
    val effectiveMode: ProxyMode?,
    val runtimeRunning: Boolean,
)

class RuntimeControlCoordinator(
    private val proxyFacade: ProxyFacade,
    private val profilesRepository: ProfilesRepository,
    private val networkSettingsStorage:
        com.github.nomadboxlab.monadbox.data.store.NetworkSettingsStorage,
) {
    private val mutationMutex = Mutex()
    private val activeOperationMutable = MutableStateFlow<String?>(null)
    private val isMutatingMutable = MutableStateFlow(false)

    val activeOperation: StateFlow<String?> = activeOperationMutable.asStateFlow()
    val isMutating: StateFlow<Boolean> = isMutatingMutable.asStateFlow()

    suspend fun <T> runSerialized(operation: String, block: suspend () -> T): T {
        return mutationMutex.withLock {
            activeOperationMutable.value = operation
            isMutatingMutable.value = true
            try {
                block()
            } finally {
                activeOperationMutable.value = null
                isMutatingMutable.value = false
            }
        }
    }

    suspend fun startProxy(
        operation: String,
        profileId: UUID? = null,
        mode: ProxyMode = networkSettingsStorage.proxyMode.value,
    ): RuntimeMutationResult {
        return runSerialized(operation) {
            val previousActiveProfile = profilesRepository.queryActiveProfile()
            try {
                if (profileId != null && previousActiveProfile?.uuid != profileId) {
                    profilesRepository.setActiveProfile(profileId)
                }
                proxyFacade.startProxy(mode)
                RuntimeMutationResult(
                    status = RuntimeMutationStatus.Started,
                    effectiveMode = mode,
                    runtimeRunning = true,
                )
            } catch (error: Exception) {
                if (profileId != null && previousActiveProfile?.uuid != profileId) {
                    rollbackActiveProfile(previousActiveProfile)
                }
                throw error
            }
        }
    }

    suspend fun stopProxy(operation: String, mode: ProxyMode? = null): RuntimeMutationResult {
        return runSerialized(operation) {
            proxyFacade.stopProxy(mode)
            RuntimeMutationResult(
                status = RuntimeMutationStatus.Stopped,
                effectiveMode = null,
                runtimeRunning = false,
            )
        }
    }

    suspend fun applyConfigChange(
        operation: String,
        persist: suspend () -> Unit,
        rollback: suspend () -> Unit = {},
        shouldRestart: (ProxyMode) -> Boolean = { true },
    ): RuntimeMutationResult {
        return runSerialized(operation) {
            persist()
            try {
                val targetMode = networkSettingsStorage.proxyMode.value
                if (!isRuntimeRunning()) {
                    return@runSerialized RuntimeMutationResult(
                        status = RuntimeMutationStatus.Deferred,
                        effectiveMode = targetMode,
                        runtimeRunning = false,
                    )
                }

                if (!shouldRestart(targetMode)) {
                    return@runSerialized RuntimeMutationResult(
                        status = RuntimeMutationStatus.Updated,
                        effectiveMode = resolveCurrentMode(),
                        runtimeRunning = true,
                    )
                }

                proxyFacade.startProxy(targetMode)
                RuntimeMutationResult(
                    status = RuntimeMutationStatus.Restarted,
                    effectiveMode = targetMode,
                    runtimeRunning = true,
                )
            } catch (error: Exception) {
                rollback()
                throw error
            }
        }
    }

    suspend fun activateProfile(
        operation: String,
        profileId: UUID,
        enabled: Boolean,
    ): RuntimeMutationResult {
        return runSerialized(operation) {
            val previousActiveProfile = profilesRepository.queryActiveProfile()
            val wasRunning = isRuntimeRunning()
            val resumeMode = resolveCurrentMode()

            if (wasRunning) {
                proxyFacade.stopProxy(resumeMode)
            }

            try {
                if (enabled) {
                    profilesRepository.setActiveProfile(profileId)
                } else {
                    val currentActive =
                        previousActiveProfile?.takeIf { it.uuid == profileId }
                            ?: profilesRepository.queryProfileByUUID(profileId)?.takeIf {
                                it.active
                            }
                    if (currentActive != null) {
                        profilesRepository.clearActiveProfile(currentActive)
                    }
                }

                if (enabled && wasRunning) {
                    proxyFacade.startProxy(resumeMode)
                    return@runSerialized RuntimeMutationResult(
                        status = RuntimeMutationStatus.Restarted,
                        effectiveMode = resumeMode,
                        runtimeRunning = true,
                    )
                }

                RuntimeMutationResult(
                    status =
                        if (enabled) RuntimeMutationStatus.Updated
                        else RuntimeMutationStatus.Stopped,
                    effectiveMode = if (enabled && wasRunning) resumeMode else null,
                    runtimeRunning = enabled && wasRunning,
                )
            } catch (error: Exception) {
                rollbackActiveProfile(previousActiveProfile)
                if (wasRunning && previousActiveProfile != null) {
                    runCatching { proxyFacade.startProxy(resumeMode) }
                }
                throw error
            }
        }
    }

    suspend fun reloadCurrentProfile(operation: String): RuntimeMutationResult {
        return runSerialized(operation) {
            if (!isRuntimeRunning()) {
                return@runSerialized RuntimeMutationResult(
                    status = RuntimeMutationStatus.Deferred,
                    effectiveMode = networkSettingsStorage.proxyMode.value,
                    runtimeRunning = false,
                )
            }

            try {
                proxyFacade.reloadCurrentProfile().getOrThrow()
            } catch (e: Exception) {
                runCatching { proxyFacade.stopProxy() }
                throw e
            }
            RuntimeMutationResult(
                status = RuntimeMutationStatus.Reloaded,
                effectiveMode = resolveCurrentMode(),
                runtimeRunning = true,
            )
        }
    }

    suspend fun reloadIfActiveProfile(operation: String, profileId: UUID): RuntimeMutationResult {
        return runSerialized(operation) {
            val activeProfile = profilesRepository.queryActiveProfile()
            if (activeProfile?.uuid != profileId || !isRuntimeRunning()) {
                return@runSerialized RuntimeMutationResult(
                    status = RuntimeMutationStatus.Deferred,
                    effectiveMode = resolveCurrentMode(),
                    runtimeRunning = isRuntimeRunning(),
                )
            }

            try {
                proxyFacade.reloadCurrentProfile().getOrThrow()
            } catch (e: Exception) {
                runCatching { proxyFacade.stopProxy() }
                throw e
            }
            RuntimeMutationResult(
                status = RuntimeMutationStatus.Reloaded,
                effectiveMode = resolveCurrentMode(),
                runtimeRunning = true,
            )
        }
    }

    private fun isRuntimeRunning(): Boolean {
        return RuntimeStateMapper.isActuallyRunning(proxyFacade.runtimeSnapshot.value)
    }

    private fun resolveCurrentMode(): ProxyMode {
        val snapshot = proxyFacade.runtimeSnapshot.value
        return RuntimeStateMapper.modeForOwner(snapshot.owner)
            ?: networkSettingsStorage.proxyMode.value
    }

    private suspend fun rollbackActiveProfile(previousActiveProfile: Profile?) {
        if (previousActiveProfile != null) {
            profilesRepository.setActiveProfile(previousActiveProfile.uuid)
            return
        }

        val currentActiveProfile = profilesRepository.queryActiveProfile()
        if (currentActiveProfile != null) {
            profilesRepository.clearActiveProfile(currentActiveProfile)
        }
    }
}

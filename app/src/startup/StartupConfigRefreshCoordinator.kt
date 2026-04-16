package com.github.yumelira.yumebox.startup

import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.data.repository.ActiveProfileOverrideReloader
import com.github.yumelira.yumebox.data.repository.OverrideConfigRepository
import com.github.yumelira.yumebox.data.repository.OverrideResolver
import com.github.yumelira.yumebox.data.repository.ProvidersRepository
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeControlCoordinator
import com.github.yumelira.yumebox.runtime.client.RuntimeStateMapper
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import timber.log.Timber

class StartupConfigRefreshCoordinator(
    private val profilesRepository: ProfilesRepository,
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val overrideResolver: OverrideResolver,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
    private val proxyFacade: ProxyFacade,
) {

    suspend fun refreshProfileAndBoundOverridesOnStart(): Profile? {
        val activeProfile =
            runCatching { profilesRepository.queryActiveProfile(ensureDefault = true) }
                .onFailure { Timber.w(it, "Startup refresh: failed to load active profile") }
                .getOrNull()
                ?: return null

        runtimeControlCoordinator.runSerialized("startup:refresh-profile-and-overrides") {
            refreshActiveProfileIfNeeded(activeProfile)
            refreshBoundRemoteOverrides(activeProfile)
        }
        return activeProfile
    }

    suspend fun refreshRuntimeProvidersIfAvailableOnStart() {
        if (!RuntimeStateMapper.isActuallyRunning(proxyFacade.runtimeSnapshot.value)) {
            return
        }

        runtimeControlCoordinator.runSerialized("startup:refresh-runtime-providers") {
            val httpProviders =
                providersRepository.queryProviders().getOrDefault(emptyList()).filter {
                    it.vehicleType == Provider.VehicleType.HTTP
                }

            if (httpProviders.isEmpty()) {
                Timber.d("Startup refresh: no HTTP providers to update")
                return@runSerialized
            }

            providersRepository
                .updateAllProviders(httpProviders)
                .onSuccess { result ->
                    if (result.failedProviders.isEmpty()) {
                        Timber.i(
                            "Startup refresh: updated %d HTTP providers",
                            httpProviders.size,
                        )
                    } else {
                        Timber.w(
                            "Startup refresh: updated HTTP providers with failures=%s",
                            result.failedProviders.joinToString(),
                        )
                    }
                }
                .onFailure { error ->
                    Timber.w(error, "Startup refresh: failed to update HTTP providers")
                }
        }
    }

    private suspend fun refreshActiveProfileIfNeeded(activeProfile: Profile) {
        if (activeProfile.type != Profile.Type.Url) {
            Timber.d(
                "Startup refresh: skip remote profile update for type=%s",
                activeProfile.type,
            )
            return
        }

        runCatching { profilesRepository.updateProfile(activeProfile.uuid) }
            .onSuccess {
                Timber.i("Startup refresh: updated active remote profile=%s", activeProfile.uuid)
            }
            .onFailure { error ->
                Timber.w(error, "Startup refresh: failed to update active remote profile")
            }
    }

    private suspend fun refreshBoundRemoteOverrides(activeProfile: Profile) {
        val overrideIds =
            runCatching { overrideResolver.resolveIds(activeProfile.uuid) }
                .onFailure {
                    Timber.w(it, "Startup refresh: failed to resolve active profile overrides")
                }
                .getOrDefault(emptyList())
                .distinct()

        if (overrideIds.isEmpty()) {
            return
        }

        val remoteResourcesById =
            runCatching { overrideConfigRepository.listRemoteResources().associateBy { it.id } }
                .onFailure {
                    Timber.w(it, "Startup refresh: failed to enumerate remote override resources")
                }
                .getOrDefault(emptyMap())

        var refreshedCount = 0
        val failedResources = mutableListOf<String>()
        overrideIds.forEach { overrideId ->
            val resource = remoteResourcesById[overrideId] ?: return@forEach
            runCatching {
                    overrideConfigRepository.refreshRemoteResource(overrideId)
                    activeProfileOverrideReloader.reapplyActiveProfileIfUsingOverride(overrideId)
                }
                .onSuccess { refreshedCount += 1 }
                .onFailure { error ->
                    failedResources += resource.name
                    Timber.w(
                        error,
                        "Startup refresh: failed to update remote override=%s",
                        overrideId,
                    )
                }
        }

        if (refreshedCount > 0 || failedResources.isNotEmpty()) {
            Timber.i(
                "Startup refresh: remote overrides refreshed=%d failed=%s",
                refreshedCount,
                failedResources.joinToString(),
            )
        }
    }
}
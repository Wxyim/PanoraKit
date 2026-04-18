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
 *
 */

package com.github.nomadboxlab.monadbox.presentation.usecase

import com.github.nomadboxlab.monadbox.core.model.Provider
import com.github.nomadboxlab.monadbox.data.repository.ActiveProfileOverrideReloader
import com.github.nomadboxlab.monadbox.data.repository.OverrideConfigRepository
import com.github.nomadboxlab.monadbox.data.repository.ProvidersRepository
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator
import dev.oom_wg.purejoy.mlang.MLang

data class RefreshRuntimeProvidersResult(
    val hasSources: Boolean,
    val refreshedCount: Int = 0,
    val failedItems: List<String> = emptyList(),
)

class RefreshRuntimeProvidersUseCase(
    private val proxyFacade: ProxyFacade,
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
) {
    suspend fun refreshAllSources(
        onSourceRefreshed: suspend (String) -> Unit = {}
    ): Result<RefreshRuntimeProvidersResult> {
        return runCatching {
            runtimeControlCoordinator.runSerialized("providers:update-all-sources") {
                refreshAllSourcesInternal(onSourceRefreshed)
            }
        }
    }

    suspend fun updateRemoteOverride(id: String): Result<Unit> {
        return runCatching {
            runtimeControlCoordinator.runSerialized("providers:update-remote-override:$id") {
                refreshRemoteOverrideOrThrow(id)
            }
        }
    }

    suspend fun updateProvider(provider: Provider): Result<Unit> {
        return runCatching {
            runtimeControlCoordinator.runSerialized(
                "providers:update-provider:${provider.type}:${provider.name}"
            ) {
                providersRepository.updateProvider(provider).getOrThrow()
                if (proxyFacade.isRunning.value) {
                    runCatching { proxyFacade.reloadCurrentProfile() }
                }
            }
        }
    }

    fun providerKey(provider: Provider): String = "${provider.type}_${provider.name}"

    fun remoteOverrideKey(id: String): String = "override_$id"

    private suspend fun refreshAllSourcesInternal(
        onSourceRefreshed: suspend (String) -> Unit
    ): RefreshRuntimeProvidersResult {
        val remoteOverrides =
            runCatching { overrideConfigRepository.listRemoteResources() }.getOrDefault(emptyList())
        val httpProviders =
            if (proxyFacade.isRunning.value) {
                providersRepository.queryProviders().getOrDefault(emptyList()).filter {
                    it.vehicleType == Provider.VehicleType.HTTP
                }
            } else {
                emptyList()
            }

        if (httpProviders.isEmpty() && remoteOverrides.isEmpty()) {
            return RefreshRuntimeProvidersResult(hasSources = false)
        }

        val failedItems = mutableListOf<String>()
        var refreshedCount = 0

        httpProviders.forEach { provider ->
            providersRepository
                .updateProvider(provider)
                .onSuccess { refreshedCount += 1 }
                .onFailure { failedItems += provider.name }
            onSourceRefreshed(providerKey(provider))
        }

        remoteOverrides.forEach { resource ->
            runCatching {
                    overrideConfigRepository.refreshRemoteResource(resource.id)
                    activeProfileOverrideReloader.reapplyActiveProfileIfUsingOverride(resource.id)
                }
                .onSuccess { refreshedCount += 1 }
                .onFailure { failedItems += resource.name }
            onSourceRefreshed(remoteOverrideKey(resource.id))
        }

        if (refreshedCount > 0 && proxyFacade.isRunning.value) {
            runCatching { proxyFacade.reloadCurrentProfile() }
        }

        return RefreshRuntimeProvidersResult(
            hasSources = true,
            refreshedCount = refreshedCount,
            failedItems = failedItems,
        )
    }

    private suspend fun refreshRemoteOverrideOrThrow(id: String) {
        val previousConfig =
            overrideConfigRepository.getById(id)
                ?: throw IllegalStateException(MLang.Override.Save.Failed)
        val previousMetadata = overrideConfigRepository.getMetadata(id)
        try {
            overrideConfigRepository.refreshRemoteResource(id = id)
            if (!activeProfileOverrideReloader.reapplyActiveProfileIfUsingOverride(id)) {
                overrideConfigRepository.restoreConfigState(previousConfig, previousMetadata)
                throw IllegalStateException(MLang.Override.Save.ApplyFailed)
            }
        } catch (error: Exception) {
            throw error
        }
    }
}

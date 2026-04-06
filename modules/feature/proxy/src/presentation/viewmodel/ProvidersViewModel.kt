/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.data.repository.AppSettingsRepository
import com.github.yumelira.yumebox.data.repository.OverrideConfigRepository
import com.github.yumelira.yumebox.data.repository.ProvidersRepository
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProvidersViewModel(
    private val proxyFacade: ProxyFacade,
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers.asStateFlow()

    private val _uiState = MutableStateFlow(ProvidersUiState())
    val uiState: StateFlow<ProvidersUiState> = _uiState.asStateFlow()

    private val _remoteOverrides = MutableStateFlow<List<RemoteOverrideResource>>(emptyList())
    val remoteOverrides: StateFlow<List<RemoteOverrideResource>> = _remoteOverrides.asStateFlow()

    val isRunning: StateFlow<Boolean> = proxyFacade.isRunning

    fun refreshProviders() {
        viewModelScope.launch {
            refreshRemoteOverrides()
            if (!proxyFacade.isRunning.value) {
                _providers.value = emptyList()
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            val result = providersRepository.queryProviders()
            result
                .onSuccess { providerList -> _providers.value = providerList.sorted() }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error =
                                MLang.Providers.Message.FetchFailed.format(
                                    e.message ?: MLang.Providers.Message.UnknownError
                                )
                        )
                    }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refreshRemoteOverrides() {
        viewModelScope.launch {
            runCatching { overrideConfigRepository.listRemoteResources() }
                .onSuccess { resources ->
                    _remoteOverrides.value = resources

                    val now = System.currentTimeMillis()
                    val staleResources =
                        resources.filter { resource ->
                            val dueAt =
                                resource.lastUpdatedAt + resource.updateIntervalSeconds * 1_000L
                            now >= dueAt
                        }

                    if (staleResources.isNotEmpty()) {
                        val failedResources = mutableListOf<String>()
                        staleResources.forEach { stale ->
                            runCatching {
                                    overrideConfigRepository.refreshRemoteResource(
                                        id = stale.id,
                                        allowInsecureHttpNonLocalhost =
                                            appSettingsRepository.allowNonLocalhostHttpRemote.value,
                                    )
                                }
                                .onFailure { failedResources += stale.name }
                        }
                        _remoteOverrides.value = overrideConfigRepository.listRemoteResources()

                        if (failedResources.isNotEmpty()) {
                            _uiState.update {
                                it.copy(
                                    error =
                                        MLang.Providers.Message.UpdateFailedResources.format(
                                            failedResources.joinToString(", ")
                                        )
                                )
                            }
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error =
                                MLang.Providers.Message.FetchFailed.format(
                                    e.message ?: MLang.Providers.Message.UnknownError
                                )
                        )
                    }
                }
        }
    }

    fun updateRemoteOverride(resource: RemoteOverrideResource) {
        val key = remoteOverrideKey(resource.id)
        viewModelScope.launch {
            _uiState.update { it.copy(updatingProviders = it.updatingProviders + key) }
            runCatching {
                    overrideConfigRepository.refreshRemoteResource(
                        id = resource.id,
                        allowInsecureHttpNonLocalhost =
                            appSettingsRepository.allowNonLocalhostHttpRemote.value,
                    )
                }
                .onSuccess {
                    refreshRemoteOverrides()
                    _uiState.update {
                        it.copy(
                            message = MLang.Providers.Message.UpdateSuccess.format(resource.name)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error =
                                MLang.Providers.Message.UpdateFailed.format(
                                    e.message ?: MLang.Providers.Message.UnknownError
                                )
                        )
                    }
                }
            _uiState.update { it.copy(updatingProviders = it.updatingProviders - key) }
        }
    }

    fun updateProvider(provider: Provider) {
        val providerKey = "${provider.type}_${provider.name}"
        viewModelScope.launch {
            _uiState.update { it.copy(updatingProviders = it.updatingProviders + providerKey) }
            val result = providersRepository.updateProvider(provider)
            result
                .onSuccess {
                    refreshProviders()
                    _uiState.update {
                        it.copy(
                            message = MLang.Providers.Message.UpdateSuccess.format(provider.name)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error =
                                MLang.Providers.Message.UpdateFailed.format(
                                    e.message ?: MLang.Providers.Message.UnknownError
                                )
                        )
                    }
                }
            _uiState.update { it.copy(updatingProviders = it.updatingProviders - providerKey) }
        }
    }

    fun updateAllProviders() {
        viewModelScope.launch {
            val httpProviders =
                _providers.value.filter { it.vehicleType == Provider.VehicleType.HTTP }
            val remoteOverrides = _remoteOverrides.value
            if (httpProviders.isEmpty() && remoteOverrides.isEmpty()) return@launch

            _uiState.update { it.copy(isUpdatingAll = true) }
            val providerKeys =
                httpProviders.map { "${it.type}_${it.name}" }.toSet() +
                    remoteOverrides.map { remoteOverrideKey(it.id) }
            _uiState.update { it.copy(updatingProviders = providerKeys) }

            val failedItems = mutableListOf<String>()

            if (httpProviders.isNotEmpty()) {
                val providerResult = providersRepository.updateAllProviders(httpProviders)
                providerResult
                    .onSuccess { updateResult -> failedItems += updateResult.failedProviders }
                    .onFailure { e ->
                        failedItems += e.message ?: MLang.Providers.Message.UnknownError
                    }
            }

            remoteOverrides.forEach { resource ->
                runCatching {
                        overrideConfigRepository.refreshRemoteResource(
                            id = resource.id,
                            allowInsecureHttpNonLocalhost =
                                appSettingsRepository.allowNonLocalhostHttpRemote.value,
                        )
                    }
                    .onFailure { failedItems += resource.name }
            }

            refreshProviders()

            if (failedItems.isEmpty()) {
                _uiState.update { it.copy(message = MLang.Providers.Message.AllUpdated) }
            } else {
                _uiState.update {
                    it.copy(
                        error =
                            MLang.Providers.Message.UpdateFailedResources.format(
                                failedItems.joinToString(", ")
                            )
                    )
                }
            }

            _uiState.update { it.copy(isUpdatingAll = false, updatingProviders = emptySet()) }
        }
    }

    private fun remoteOverrideKey(id: String): String = "override_$id"

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun uploadProviderFile(context: Context, provider: Provider, uri: Uri) {
        val providerKey = "${provider.type}_${provider.name}"
        viewModelScope.launch {
            _uiState.update { it.copy(updatingProviders = it.updatingProviders + providerKey) }

            val result = providersRepository.uploadProviderFile(context, provider, uri)
            result
                .onSuccess {
                    refreshProviders()
                    _uiState.update {
                        it.copy(
                            message = MLang.Providers.Message.UploadSuccess.format(provider.name)
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error =
                                MLang.Providers.Message.UploadFailed.format(
                                    e.message ?: MLang.Providers.Message.UnknownError
                                )
                        )
                    }
                }

            _uiState.update { it.copy(updatingProviders = it.updatingProviders - providerKey) }
        }
    }

    data class ProvidersUiState(
        val isLoading: Boolean = false,
        val isUpdatingAll: Boolean = false,
        val updatingProviders: Set<String> = emptySet(),
        val message: String? = null,
        val error: String? = null,
    )
}

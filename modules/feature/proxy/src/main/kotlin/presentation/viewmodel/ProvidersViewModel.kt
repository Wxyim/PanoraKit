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

package com.github.nomadboxlab.monadbox.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nomadboxlab.monadbox.core.model.Provider
import com.github.nomadboxlab.monadbox.data.repository.OverrideConfigRepository
import com.github.nomadboxlab.monadbox.data.repository.ProvidersRepository
import com.github.nomadboxlab.monadbox.domain.model.ErrorCategory
import com.github.nomadboxlab.monadbox.domain.model.ErrorImpact
import com.github.nomadboxlab.monadbox.domain.model.ErrorPhase
import com.github.nomadboxlab.monadbox.domain.model.ErrorRetryability
import com.github.nomadboxlab.monadbox.domain.model.RemoteOverrideResource
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
import com.github.nomadboxlab.monadbox.presentation.usecase.RefreshRuntimeProvidersUseCase
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
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
    private val refreshRuntimeProvidersUseCase: RefreshRuntimeProvidersUseCase,
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
                    showError(
                        message =
                            MLang.Providers.Message.FetchFailed.format(
                                e.message ?: MLang.Providers.Message.UnknownError
                            ),
                        rawCause = e.message,
                        phase = ErrorPhase.Running,
                    )
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refreshRemoteOverrides() {
        viewModelScope.launch {
            runCatching { overrideConfigRepository.listRemoteResources() }
                .onSuccess { resources -> _remoteOverrides.value = resources }
                .onFailure { e ->
                    showError(
                        message =
                            MLang.Providers.Message.FetchFailed.format(
                                e.message ?: MLang.Providers.Message.UnknownError
                            ),
                        rawCause = e.message,
                        phase = ErrorPhase.Reloading,
                    )
                }
        }
    }

    fun refreshAllSources() {
        viewModelScope.launch {
            if (_uiState.value.isUpdatingAll) {
                return@launch
            }

            val providerKeys =
                if (proxyFacade.isRunning.value) {
                    _providers.value
                        .filter { it.vehicleType == Provider.VehicleType.HTTP }
                        .map { "${it.type}_${it.name}" }
                        .toSet()
                } else {
                    emptySet()
                }
            val overrideKeys =
                _remoteOverrides.value
                    .map { refreshRuntimeProvidersUseCase.remoteOverrideKey(it.id) }
                    .toSet()
            val allUpdatingKeys = providerKeys + overrideKeys

            _uiState.update {
                it.copy(
                    isUpdatingAll = true,
                    updatingProviders = it.updatingProviders + allUpdatingKeys,
                )
            }
            refreshRuntimeProvidersUseCase
                .refreshAllSources { refreshedKey ->
                    _uiState.update {
                        it.copy(updatingProviders = it.updatingProviders - refreshedKey)
                    }
                }
                .onSuccess { result ->
                    refreshProviders()
                    when {
                        !result.hasSources -> {
                            showMessage(MLang.Providers.Empty.NoProvidersHint)
                        }

                        result.failedItems.isEmpty() -> Unit

                        result.refreshedCount > 0 -> {
                            showError(
                                message =
                                    MLang.Providers.Message.UpdateFailedResources.format(
                                        result.failedItems.take(5).joinToString()
                                    ),
                                rawCause = result.failedItems.joinToString(),
                                phase = ErrorPhase.Reloading,
                                impact = ErrorImpact.Degraded,
                            )
                        }

                        else -> {
                            showError(
                                message =
                                    MLang.Providers.Message.UpdateFailed.format(
                                        result.failedItems.firstOrNull()
                                            ?: MLang.Providers.Message.UnknownError
                                    ),
                                rawCause = result.failedItems.joinToString(),
                                phase = ErrorPhase.Reloading,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    showError(
                        message =
                            MLang.Providers.Message.UpdateFailed.format(
                                error.message ?: MLang.Providers.Message.UnknownError
                            ),
                        rawCause = error.message,
                        phase = ErrorPhase.Reloading,
                    )
                }
            _uiState.update {
                it.copy(
                    isUpdatingAll = false,
                    updatingProviders = it.updatingProviders - allUpdatingKeys,
                )
            }
        }
    }

    fun updateRemoteOverride(resource: RemoteOverrideResource) {
        val key = refreshRuntimeProvidersUseCase.remoteOverrideKey(resource.id)
        viewModelScope.launch {
            _uiState.update { it.copy(updatingProviders = it.updatingProviders + key) }
            refreshRuntimeProvidersUseCase
                .updateRemoteOverride(resource.id)
                .onSuccess { refreshRemoteOverrides() }
                .onFailure { e ->
                    showError(
                        message =
                            MLang.Providers.Message.UpdateFailed.format(
                                e.message ?: MLang.Providers.Message.UnknownError
                            ),
                        rawCause = e.message,
                        phase = ErrorPhase.Reloading,
                    )
                }
            _uiState.update { it.copy(updatingProviders = it.updatingProviders - key) }
        }
    }

    fun updateProvider(provider: Provider) {
        val providerKey = refreshRuntimeProvidersUseCase.providerKey(provider)
        viewModelScope.launch {
            _uiState.update { it.copy(updatingProviders = it.updatingProviders + providerKey) }
            val result = refreshRuntimeProvidersUseCase.updateProvider(provider)
            result
                .onSuccess { refreshProviders() }
                .onFailure { e ->
                    showError(
                        message =
                            MLang.Providers.Message.UpdateFailed.format(
                                e.message ?: MLang.Providers.Message.UnknownError
                            ),
                        rawCause = e.message,
                        phase = ErrorPhase.Reloading,
                    )
                }
            _uiState.update { it.copy(updatingProviders = it.updatingProviders - providerKey) }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun uploadProviderFile(context: Context, provider: Provider, uri: Uri) {
        val providerKey = refreshRuntimeProvidersUseCase.providerKey(provider)
        viewModelScope.launch {
            _uiState.update { it.copy(updatingProviders = it.updatingProviders + providerKey) }

            val result = providersRepository.uploadProviderFile(context, provider, uri)
            result
                .onSuccess {
                    val applyResult =
                        if (proxyFacade.isRunning.value) {
                            refreshRuntimeProvidersUseCase.updateProvider(provider)
                        } else {
                            Result.success(Unit)
                        }

                    applyResult
                        .onSuccess {
                            refreshProviders()
                            showMessage(MLang.Providers.Message.UploadSuccess.format(provider.name))
                        }
                        .onFailure { e ->
                            showError(
                                message =
                                    MLang.Providers.Message.UpdateFailed.format(
                                        e.message ?: MLang.Providers.Message.UnknownError
                                    ),
                                rawCause = e.message,
                                phase = ErrorPhase.Saving,
                            )
                        }
                }
                .onFailure { e ->
                    showError(
                        message =
                            MLang.Providers.Message.UploadFailed.format(
                                e.message ?: MLang.Providers.Message.UnknownError
                            ),
                        rawCause = e.message,
                        phase = ErrorPhase.Saving,
                        retryability = ErrorRetryability.RetryableAfterAction,
                    )
                }

            _uiState.update { it.copy(updatingProviders = it.updatingProviders - providerKey) }
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { current -> current.copy(message = message, structuredError = null) }
    }

    private fun showError(
        message: String,
        rawCause: String?,
        phase: ErrorPhase,
        category: ErrorCategory = ErrorCategory.Runtime,
        impact: ErrorImpact = ErrorImpact.FeatureUnavailable,
        retryability: ErrorRetryability = ErrorRetryability.Retryable,
    ) {
        _uiState.update { current ->
            current.copy(
                error = message,
                structuredError =
                    StructuredError(
                        category = category,
                        phase = phase,
                        impact = impact,
                        retryability = retryability,
                        rawCause = rawCause,
                        userVisibleMessage = message,
                        technicalDetail = rawCause,
                    ),
            )
        }
    }

    data class ProvidersUiState(
        val isLoading: Boolean = false,
        val isUpdatingAll: Boolean = false,
        val updatingProviders: Set<String> = emptySet(),
        val message: String? = null,
        val error: String? = null,
        val structuredError: StructuredError? = null,
    )
}

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

package com.github.yumelira.yumebox.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.data.repository.OverrideConfigRepository
import com.github.yumelira.yumebox.data.repository.ProvidersRepository
import com.github.yumelira.yumebox.domain.model.StructuredLogCollector
import com.github.yumelira.yumebox.domain.model.StructuredLogEntry
import com.github.yumelira.yumebox.presentation.util.ExternalResourceDiagnostics
import com.github.yumelira.yumebox.presentation.util.buildExternalResourceDiagnostics
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MetaFeatureUiState(
    val isLoading: Boolean = false,
    val recentFailures: List<StructuredLogEntry> = emptyList(),
    val externalResources: ExternalResourceDiagnostics = ExternalResourceDiagnostics(),
    val runtimeSnapshot: RuntimeSnapshot = RuntimeSnapshot(),
)

class MetaFeatureViewModel(
    private val structuredLogCollector: StructuredLogCollector,
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val proxyFacade: ProxyFacade,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetaFeatureUiState())
    val uiState: StateFlow<MetaFeatureUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val nextState =
                withContext(Dispatchers.IO) {
                    val providers = providersRepository.queryProviders().getOrDefault(emptyList())
                    val remoteOverrides =
                        runCatching { overrideConfigRepository.listRemoteResources() }
                            .getOrDefault(emptyList())
                    MetaFeatureUiState(
                        isLoading = false,
                        recentFailures = structuredLogCollector.recentFailures(limit = 6),
                        runtimeSnapshot = proxyFacade.runtimeSnapshot.value,
                        externalResources =
                            buildExternalResourceDiagnostics(
                                providers = providers,
                                remoteOverrides = remoteOverrides,
                            ),
                    )
                }
            _uiState.value = nextState
        }
    }
}

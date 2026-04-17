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

package com.github.nomadboxlab.monadbox.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nomadboxlab.monadbox.data.repository.OverrideConfigRepository
import com.github.nomadboxlab.monadbox.data.repository.ProvidersRepository
import com.github.nomadboxlab.monadbox.presentation.meta.EffectiveRuleSummaryRepository
import com.github.nomadboxlab.monadbox.presentation.util.ExternalResourceDiagnostics
import com.github.nomadboxlab.monadbox.presentation.util.buildExternalResourceDiagnostics
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
import dev.oom_wg.purejoy.mlang.MLangStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EffectiveRuleSummaryState(
    val count: Int? = null,
    val summary: String = MLangStatus.Common.NotAvailable,
)

data class MetaFeatureUiState(
    val isLoading: Boolean = false,
    val externalResources: ExternalResourceDiagnostics = ExternalResourceDiagnostics(),
    val runtimeSnapshot: RuntimeSnapshot = RuntimeSnapshot(),
    val effectiveRules: EffectiveRuleSummaryState = EffectiveRuleSummaryState(),
)

class MetaFeatureViewModel(
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val proxyFacade: ProxyFacade,
    private val effectiveRuleSummaryRepository: EffectiveRuleSummaryRepository,
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
                    val effectiveRules = effectiveRuleSummaryRepository.load().toUiState()
                    MetaFeatureUiState(
                        isLoading = false,
                        runtimeSnapshot = proxyFacade.runtimeSnapshot.value,
                        externalResources =
                            buildExternalResourceDiagnostics(
                                providers = providers,
                                remoteOverrides = remoteOverrides,
                            ),
                        effectiveRules = effectiveRules,
                    )
                }
            _uiState.value = nextState
        }
    }
}

private fun com.github.nomadboxlab.monadbox.presentation.meta.EffectiveRuleSummary.toUiState():
    EffectiveRuleSummaryState {
    return EffectiveRuleSummaryState(count = count, summary = summary)
}

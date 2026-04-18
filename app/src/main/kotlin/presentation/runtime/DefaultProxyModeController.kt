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

package com.github.nomadboxlab.monadbox.presentation.runtime

import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.repository.OverrideRepository
import com.github.nomadboxlab.monadbox.data.store.ProxyDisplaySettingsStore
import com.github.nomadboxlab.monadbox.feature.proxy.api.ProxyModeControlUiState
import com.github.nomadboxlab.monadbox.feature.proxy.api.ProxyModeController
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator
import dev.oom_wg.purejoy.mlang.MLang
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DefaultProxyModeController(
    private val overrideRepository: OverrideRepository,
    private val proxyFacade: ProxyFacade,
    private val proxyDisplaySettingsStore: ProxyDisplaySettingsStore,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
    private val scope: CoroutineScope,
) : ProxyModeController {
    private val _uiState = MutableStateFlow(ProxyModeControlUiState())
    override val uiState: StateFlow<ProxyModeControlUiState> = _uiState.asStateFlow()

    private val _currentMode = MutableStateFlow(proxyDisplaySettingsStore.proxyMode.value)
    override val currentMode: StateFlow<TunnelState.Mode> = _currentMode.asStateFlow()

    init {
        scope.launch {
            proxyDisplaySettingsStore.proxyMode.state.collect { mode ->
                if (!proxyFacade.isRunning.value) {
                    _currentMode.value = mode
                }
            }
        }
    }

    override fun patchMode(mode: TunnelState.Mode) {
        if (_uiState.value.isLoading) return

        val previousMode = proxyDisplaySettingsStore.proxyMode.value
        proxyDisplaySettingsStore.proxyMode.set(mode)
        scope.launch {
            setLoading(true)
            val result = runCatching {
                runtimeControlCoordinator.runSerialized("home:tunnel-mode") {
                    val previousOverride =
                        overrideRepository
                            .updateProfile { current -> current }
                            .getOrElse { error ->
                                error(error.message ?: MLang.Proxy.Mode.SwitchFailed)
                            }
                    val persistError =
                        overrideRepository.updateProfile { it.copy(mode = mode) }.exceptionOrNull()
                    if (persistError != null) {
                        error(persistError.message ?: MLang.Proxy.Mode.SwitchFailed)
                    }

                    val reloadError = proxyFacade.reloadCurrentProfile().exceptionOrNull()
                    if (reloadError != null) {
                        overrideRepository.updateProfile { previousOverride }.getOrThrow()
                        error(reloadError.message ?: MLang.Proxy.Mode.SwitchFailed)
                    }
                }
            }

            val error = result.exceptionOrNull()
            if (error == null) {
                refreshCurrentTunnelMode()
                delay(500.milliseconds)
                showMessage(MLang.Proxy.Mode.Switched.format(mode.toModeName()))
            } else {
                proxyDisplaySettingsStore.proxyMode.set(previousMode)
                refreshCurrentTunnelMode()
                showError(MLang.Proxy.Mode.SwitchFailed.format(error.message))
            }
            setLoading(false)
        }
    }

    override fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    override fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private suspend fun refreshCurrentTunnelMode() {
        val nextMode =
            if (!proxyFacade.isRunning.value) {
                proxyDisplaySettingsStore.proxyMode.value
            } else {
                runCatching { proxyFacade.queryTunnelState().mode }
                    .getOrElse { proxyDisplaySettingsStore.proxyMode.value }
            }
        _currentMode.value = nextMode
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update { it.copy(isLoading = loading) }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    private fun showError(error: String) {
        _uiState.update { it.copy(error = error) }
    }

    private fun TunnelState.Mode.toModeName(): String =
        when (this) {
            TunnelState.Mode.Direct -> MLang.Proxy.Mode.Direct
            TunnelState.Mode.Global -> MLang.Proxy.Mode.Global
            TunnelState.Mode.Rule -> MLang.Proxy.Mode.Rule
            else -> MLang.Proxy.Mode.Unknown
        }
}

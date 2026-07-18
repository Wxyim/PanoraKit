/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.repository.AppSettingsRepository
import com.github.nomadboxlab.monadbox.data.repository.OverrideRepository
import com.github.nomadboxlab.monadbox.data.store.ProxyDisplaySettingsStore
import com.github.nomadboxlab.monadbox.domain.model.*
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator
import dev.oom_wg.purejoy.mlang.MLang
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal fun resolveHealthCheckTargets(
    currentGroups: List<ProxyGroupInfo>,
    requestedGroupName: String?,
    activeTestingGroupNames: Set<String>,
): Set<String> {
    val requestedTargets =
        if (requestedGroupName != null) {
            linkedSetOf(requestedGroupName)
        } else {
            currentGroups.mapTo(linkedSetOf()) { it.name }
        }
    return requestedTargets.filterNotTo(linkedSetOf()) { it in activeTestingGroupNames }
}

class ProxyViewModel(
    private val overrideRepository: OverrideRepository,
    private val proxyFacade: ProxyFacade,
    private val proxyDisplaySettingsStore: ProxyDisplaySettingsStore,
    private val appSettingsRepository: AppSettingsRepository,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
) : ViewModel() {
    private companion object {
        const val PROXY_REFRESH_IDLE_MS = 1500L
        const val PROXY_REFRESH_PREVIEW_MS = 10_000L
        const val PROXY_TESTING_SORT_HOLD_MS = 2200L
    }

    private val _uiState = MutableStateFlow(ProxyUiState())
    val uiState: StateFlow<ProxyUiState> = _uiState.asStateFlow()

    private val _testingGroupNames = MutableStateFlow<Set<String>>(emptySet())
    val testingGroupNames: StateFlow<Set<String>> = _testingGroupNames.asStateFlow()

    private val _testingProxyNames = MutableStateFlow<Set<String>>(emptySet())
    val testingProxyNames: StateFlow<Set<String>> = _testingProxyNames.asStateFlow()

    private val _groupOriginalOrder = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    private val configuredMode: StateFlow<TunnelState.Mode> =
        proxyDisplaySettingsStore.proxyMode.state.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TunnelState.Mode.Rule,
        )

    private val _currentMode = MutableStateFlow(proxyDisplaySettingsStore.proxyMode.value)
    val currentMode: StateFlow<TunnelState.Mode> = _currentMode.asStateFlow()

    val isRunning: StateFlow<Boolean> = proxyFacade.isRunning

    val groupStyle: StateFlow<ProxyGroupStyle> =
        proxyDisplaySettingsStore.groupStyle.state.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ProxyGroupStyle.FLOATING,
        )

    val sortMode: StateFlow<ProxySortMode> =
        proxyDisplaySettingsStore.sortMode.state.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ProxySortMode.DEFAULT,
        )

    val showHiddenGroups: StateFlow<Boolean> =
        proxyDisplaySettingsStore.showHiddenGroups.state.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false,
        )

    val singleNodeTest: StateFlow<Boolean> =
        appSettingsRepository.singleNodeTest.state.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            true,
        )

    val allProxyGroups: StateFlow<List<ProxyGroupInfo>> = proxyFacade.proxyGroups

    private val hiddenGroupNames: StateFlow<Set<String>> =
        allProxyGroups
            .map { groups ->
                groups.asSequence().filter { it.type.group && it.hidden }.map { it.name }.toSet()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val proxyGroups: StateFlow<List<ProxyGroupInfo>> =
        combine(allProxyGroups, showHiddenGroups, hiddenGroupNames) {
                groups,
                showHidden,
                hiddenNames ->
                val strategyGroups = groups.filter { it.type.group }
                if (showHidden) {
                    strategyGroups
                } else {
                    strategyGroups.filterNot { it.name in hiddenNames }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var screenActive = false
    private var externalSelectionSyncJob: Job? = null
    private var previewSyncJob: Job? = null
    private var tunnelModeSyncJob: Job? = null

    init {
        proxyFacade.warmUpProxyGroups()
        viewModelScope.launch {
            configuredMode.collect { mode ->
                if (!proxyFacade.isRunning.value) {
                    _currentMode.value = mode
                }
            }
        }
        viewModelScope.launch {
            allProxyGroups.collect { groups ->
                _groupOriginalOrder.update { current -> updateGroupOrderCache(current, groups) }
            }
        }
    }

    val sortedProxyGroups: StateFlow<List<ProxyGroupInfo>> =
        combine(allProxyGroups, proxyGroups, hiddenGroupNames, sortMode, _groupOriginalOrder) {
                allGroups,
                groups,
                hiddenNames,
                mode,
                originalOrderCache ->
                val groupMetadataByName = allGroups.filter { it.type.group }.associateBy { it.name }
                groups.map { group ->
                    val resolvedProxies =
                        group.proxies.map { proxy ->
                            val matchingGroup = groupMetadataByName[proxy.name] ?: return@map proxy
                            proxy.copy(
                                hidden = proxy.hidden || matchingGroup.hidden,
                                icon = proxy.icon ?: matchingGroup.icon,
                            )
                        }
                    val originalOrder = originalOrderCache[group.name].orEmpty()
                    val sortedProxies =
                        sortProxies(
                            proxies = resolvedProxies,
                            sortMode = mode,
                            originalOrder = originalOrder,
                        )
                    group.copy(
                        hidden = group.name in hiddenNames,
                        icon = group.icon ?: groupMetadataByName[group.name]?.icon,
                        proxies = sortedProxies,
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun ensureCoreLoaded(isActive: Boolean) {
        if (screenActive == isActive) return
        screenActive = isActive
        if (isActive) {
            startExternalSelectionSync()
            startPreviewSync()
            startTunnelModeSync()
        } else {
            stopExternalSelectionSync()
            stopPreviewSync()
            stopTunnelModeSync()
        }
    }

    private fun startPreviewSync() {
        if (previewSyncJob?.isActive == true) return
        previewSyncJob =
            viewModelScope.launch {
                while (true) {
                    if (!proxyFacade.isRunning.value) {
                        runCatching { proxyFacade.refreshProxyGroups() }
                            .onFailure { error -> if (error is CancellationException) throw error }
                    }
                    delay(PROXY_REFRESH_PREVIEW_MS.milliseconds)
                }
            }
    }

    private fun stopPreviewSync() {
        previewSyncJob?.cancel()
        previewSyncJob = null
    }

    fun patchMode(mode: TunnelState.Mode) {
        if (_uiState.value.isLoading) return
        val previousMode = proxyDisplaySettingsStore.proxyMode.value
        proxyDisplaySettingsStore.proxyMode.set(mode)
        viewModelScope.launch {
            setLoading(true)
            runCatching {
                    runtimeControlCoordinator.runSerialized("proxy:tunnel-mode") {
                        val previousOverride =
                            overrideRepository
                                .updateProfile { current -> current }
                                .getOrElse { error ->
                                    error(error.message ?: MLang.Proxy.Mode.SwitchFailed)
                                }
                        val persistError =
                            overrideRepository
                                .updateProfile { it.copy(mode = mode) }
                                .exceptionOrNull()
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
                .onSuccess {
                    refreshCurrentTunnelMode()
                    delay(500.milliseconds)
                    showMessage(MLang.Proxy.Mode.Switched.format(mode.toModeName()))
                }
                .onFailure { error ->
                    proxyDisplaySettingsStore.proxyMode.set(previousMode)
                    refreshCurrentTunnelMode()
                    showError(MLang.Proxy.Mode.SwitchFailed.format(error.message))
                }
            setLoading(false)
        }
    }

    fun testDelay(groupName: String? = null, showStartMessage: Boolean = true) {
        viewModelScope.launch {
            if (!proxyFacade.isRunning.value) {
                showError(MLang.Providers.Empty.NotRunning)
                return@launch
            }
            val currentGroups = proxyGroups.value
            val testingTargets: Set<String> =
                resolveHealthCheckTargets(
                    currentGroups = currentGroups,
                    requestedGroupName = groupName,
                    activeTestingGroupNames = _testingGroupNames.value,
                )
            if (testingTargets.isEmpty()) {
                return@launch
            }
            setLoading(true)
            _testingGroupNames.update { it + testingTargets }
            if (showStartMessage && groupName != null) {
                showMessage(MLang.Proxy.Testing.Group.format(groupName))
            }

            val result = runCatching {
                if (groupName != null) {
                    proxyFacade.healthCheck(groupName, refreshAfter = false)
                } else {
                    var firstError: Throwable? = null
                    currentGroups
                        .asSequence()
                        .filter { it.name in testingTargets }
                        .forEach { group ->
                            runCatching {
                                    proxyFacade.healthCheck(group.name, refreshAfter = false)
                                }
                                .onFailure { error ->
                                    if (firstError == null) {
                                        firstError = error
                                    }
                                }
                        }
                    firstError?.let { throw it }
                }
            }

            setLoading(false)

            if (testingTargets.isNotEmpty()) {
                delay(PROXY_TESTING_SORT_HOLD_MS)
                _testingGroupNames.update { it - testingTargets }
            }

            result.exceptionOrNull()?.let { error ->
                showError(
                    MLang.Proxy.Testing.Failed.format(
                        error.localizedMessage ?: error.javaClass.simpleName
                    )
                )
            }
        }
    }

    fun setGroupStyle(style: ProxyGroupStyle) {
        proxyDisplaySettingsStore.groupStyle.set(style)
    }

    fun setSortMode(mode: ProxySortMode) {
        proxyDisplaySettingsStore.sortMode.set(mode)
    }

    fun setShowHiddenGroups(show: Boolean) {
        proxyDisplaySettingsStore.showHiddenGroups.set(show)
    }

    fun selectProxy(groupName: String, proxyName: String) {
        viewModelScope.launch {
            runCatching {
                    val success = proxyFacade.selectProxy(groupName, proxyName)
                    if (!success) {
                        showError(MLang.Proxy.Selection.Failed)
                    }
                }
                .onFailure { error -> showError(MLang.Proxy.Selection.Error.format(error.message)) }
        }
    }

    fun testProxyDelay(proxyName: String) {
        viewModelScope.launch {
            if (!proxyFacade.isRunning.value) {
                showError(MLang.Providers.Empty.NotRunning)
                return@launch
            }
            _testingProxyNames.update { it + proxyName }
            val result = runCatching { proxyFacade.healthCheckProxy(proxyName) }
            delay(500.milliseconds)
            _testingProxyNames.update { it - proxyName }
            result.exceptionOrNull()?.let { error ->
                showError(
                    MLang.Proxy.Testing.Failed.format(
                        error.localizedMessage ?: error.javaClass.simpleName
                    )
                )
            }
        }
    }

    private fun sortProxies(
        proxies: List<Proxy>,
        sortMode: ProxySortMode,
        originalOrder: List<String>,
    ): List<Proxy> =
        when (sortMode) {
            ProxySortMode.DEFAULT -> reorderByNameSequence(proxies, originalOrder)
            ProxySortMode.BY_NAME -> {
                val originalIndex =
                    originalOrder.withIndex().associate { (index, name) -> name to index }
                proxies.sortedWith(
                    compareBy<Proxy>(
                        { it.name.lowercase() },
                        { originalIndex[it.name] ?: Int.MAX_VALUE },
                    )
                )
            }
            ProxySortMode.BY_LATENCY -> {
                val originalIndex =
                    originalOrder.withIndex().associate { (index, name) -> name to index }
                proxies.sortedWith(
                    compareBy<Proxy>(
                        { proxy -> proxy.delay.toProxyLatencyState().sortBucket },
                        { proxy -> proxy.delay.toProxyLatencyState().sortValue },
                        { proxy -> originalIndex[proxy.name] ?: Int.MAX_VALUE },
                    )
                )
            }
        }

    private fun reorderByNameSequence(
        proxies: List<Proxy>,
        orderedNames: List<String>,
    ): List<Proxy> {
        if (proxies.isEmpty()) return proxies
        if (orderedNames.isEmpty()) return proxies

        val proxyByName = proxies.associateBy { it.name }
        val consumed = HashSet<String>(proxies.size)
        val reordered = ArrayList<Proxy>(proxies.size)

        orderedNames.forEach { name ->
            val proxy = proxyByName[name] ?: return@forEach
            reordered += proxy
            consumed += name
        }
        proxies.forEach { proxy -> if (consumed.add(proxy.name)) reordered += proxy }
        return reordered
    }

    private fun updateGroupOrderCache(
        current: Map<String, List<String>>,
        groups: List<ProxyGroupInfo>,
    ): Map<String, List<String>> {
        val next = current.toMutableMap()
        var changed = false
        val activeGroupNames = groups.mapTo(HashSet(groups.size)) { it.name }

        if (next.keys.removeAll { it !in activeGroupNames }) {
            changed = true
        }

        groups.forEach { group ->
            val latestNames = group.proxies.map { it.name }
            val previous = next[group.name]
            val merged =
                if (previous == null) {
                    latestNames
                } else {
                    mergeStableOrder(previous, latestNames)
                }
            if (previous != merged) {
                next[group.name] = merged
                changed = true
            }
        }

        return if (changed) next else current
    }

    private fun mergeStableOrder(
        previousOrder: List<String>,
        latestNames: List<String>,
    ): List<String> {
        if (previousOrder.isEmpty()) return latestNames
        if (latestNames.isEmpty()) return emptyList()

        val latestSet = latestNames.toHashSet()
        val merged = ArrayList<String>(latestNames.size)
        previousOrder.forEach { name -> if (name in latestSet) merged += name }
        latestNames.forEach { name -> if (name !in merged) merged += name }
        return merged
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

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun startExternalSelectionSync() {
        if (externalSelectionSyncJob?.isActive == true) return
        externalSelectionSyncJob =
            viewModelScope.launch {
                while (true) {
                    runCatching { proxyFacade.refreshProxyGroups() }
                        .onFailure { error -> if (error is CancellationException) throw error }
                    val delayMillis =
                        when {
                            !proxyFacade.isRunning.value -> PROXY_REFRESH_PREVIEW_MS
                            _testingGroupNames.value.isNotEmpty() -> 250
                            else -> PROXY_REFRESH_IDLE_MS
                        }
                    delay(delayMillis.milliseconds)
                }
            }
    }

    private fun stopExternalSelectionSync() {
        externalSelectionSyncJob?.cancel()
        externalSelectionSyncJob = null
    }

    private fun startTunnelModeSync() {
        if (tunnelModeSyncJob?.isActive == true) return
        tunnelModeSyncJob =
            viewModelScope.launch {
                refreshCurrentTunnelMode()
                while (true) {
                    val delayMillis =
                        if (proxyFacade.isRunning.value) {
                            PROXY_REFRESH_IDLE_MS
                        } else {
                            PROXY_REFRESH_PREVIEW_MS
                        }
                    delay(delayMillis.milliseconds)
                    refreshCurrentTunnelMode()
                }
            }
    }

    private fun stopTunnelModeSync() {
        tunnelModeSyncJob?.cancel()
        tunnelModeSyncJob = null
    }

    private suspend fun refreshCurrentTunnelMode() {
        val nextMode =
            if (!proxyFacade.isRunning.value) {
                configuredMode.value
            } else {
                runCatching { proxyFacade.queryTunnelState().mode }
                    .getOrElse { configuredMode.value }
            }
        if (_currentMode.value != nextMode) {
            _currentMode.value = nextMode
        }
    }

    override fun onCleared() {
        stopExternalSelectionSync()
        stopPreviewSync()
        stopTunnelModeSync()
        super.onCleared()
    }

    private fun TunnelState.Mode.toModeName(): String =
        when (this) {
            TunnelState.Mode.Direct -> MLang.Proxy.Mode.Direct
            TunnelState.Mode.Global -> MLang.Proxy.Mode.Global
            TunnelState.Mode.Rule -> MLang.Proxy.Mode.Rule
            else -> MLang.Proxy.Mode.Unknown
        }

    data class ProxyUiState(
        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null,
        val structuredError: StructuredError? = null,
    )
}

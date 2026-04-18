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

package com.github.nomadboxlab.monadbox.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccessMode
import com.github.nomadboxlab.monadbox.data.model.AccessControlMode
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.store.NetworkSettingsStorage
import com.github.nomadboxlab.monadbox.feature.settings.usecase.ResolveAccessControlAppsUseCase
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionOutcome
import com.github.nomadboxlab.monadbox.presentation.runtime.VpnPermissionCoordinator
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeStateMapper
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AccessControlViewModel(
    private val networkSettingsStorage: NetworkSettingsStorage,
    private val proxyFacade: ProxyFacade,
    private val runtimeActionExecutor: RuntimeActionExecutor,
    private val vpnPermissionCoordinator: VpnPermissionCoordinator,
    private val resolveAccessControlAppsUseCase: ResolveAccessControlAppsUseCase,
) : ViewModel() {
    data class ImportResult(val addedCount: Int, val ignoredCount: Int, val totalCount: Int)

    private val _uiState = MutableStateFlow(AccessControlUiState())
    val uiState: StateFlow<AccessControlUiState> = _uiState.asStateFlow()
    val filteredApps: StateFlow<List<AccessControlAppInfo>> =
        _uiState
            .map { state ->
                AccessControlFilter.filterApps(
                    apps = state.apps,
                    query = state.searchQuery,
                    showSystemApps = state.showSystemApps,
                    sortMode = state.sortMode,
                    selectedFirst = state.selectedFirst,
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private var applyPackagesJob: Job? = null

    val isApplying: StateFlow<Boolean> = runtimeActionExecutor.isMutating

    init {
        checkAndLoad()
    }

    private fun checkAndLoad() {
        val accessState = resolveAccessControlAppsUseCase.resolveAccessState()
        when (accessState.mode) {
            InstalledAppsAccessMode.Full -> loadApps()
            InstalledAppsAccessMode.PermissionRequired -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        needsMiuiPermission = accessState.requiresMiuiPermission,
                        canBrowseApps = false,
                    )
                }
            }
            InstalledAppsAccessMode.ManualOnly -> {
                _uiState.update {
                    it.copy(isLoading = false, needsMiuiPermission = false, canBrowseApps = false)
                }
            }
        }
    }

    fun onPermissionResult() {
        checkAndLoad()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, canBrowseApps = true) }

            val selectedPackages = networkSettingsStorage.accessControlPackages.value
            val appsResult = runCatching {
                resolveAccessControlAppsUseCase.loadInstalledApps(selectedPackages)
            }
            val apps =
                appsResult.getOrElse {
                    val accessState = resolveAccessControlAppsUseCase.resolveAccessState()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            needsMiuiPermission = accessState.requiresMiuiPermission,
                            canBrowseApps = false,
                        )
                    }
                    return@launch
                }

            _uiState.update { state ->
                state.copy(isLoading = false, apps = apps, selectedPackages = selectedPackages)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state -> state.copy(searchQuery = query) }
    }

    fun onManualPackageNameChange(value: String) {
        _uiState.update { state -> state.copy(manualPackageName = value) }
    }

    fun onSortModeChange(mode: AccessControlSortMode) {
        _uiState.update { state -> state.copy(sortMode = mode) }
    }

    fun onSelectedFirstChange(selectedFirst: Boolean) {
        _uiState.update { state -> state.copy(selectedFirst = selectedFirst) }
    }

    fun onShowSystemAppsChange(show: Boolean) {
        _uiState.update { state -> state.copy(showSystemApps = show) }
    }

    fun onAppSelectionChange(packageName: String, selected: Boolean) {
        if (runtimeActionExecutor.isMutating.value) return
        _uiState.update { state ->
            val newSelectedPackages =
                if (selected) {
                    state.selectedPackages + packageName
                } else {
                    state.selectedPackages - packageName
                }
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }

        persistSelectionAndApply()
    }

    fun selectAll() {
        if (runtimeActionExecutor.isMutating.value) return
        if (!_uiState.value.canBrowseApps) return
        _uiState.update { state ->
            val visiblePackages = AccessControlSelection.visiblePackages(state)
            AccessControlSelection.updateSelection(state, state.selectedPackages + visiblePackages)
        }

        persistSelectionAndApply()
    }

    fun deselectAll() {
        if (runtimeActionExecutor.isMutating.value) return
        if (!_uiState.value.canBrowseApps) return
        _uiState.update { state ->
            val visiblePackages = AccessControlSelection.visiblePackages(state)
            AccessControlSelection.updateSelection(state, state.selectedPackages - visiblePackages)
        }

        persistSelectionAndApply()
    }

    fun invertSelection() {
        if (runtimeActionExecutor.isMutating.value) return
        if (!_uiState.value.canBrowseApps) return
        _uiState.update { state ->
            val allPackages = AccessControlSelection.visiblePackages(state)
            val newSelectedPackages = state.selectedPackages.toMutableSet()
            allPackages.forEach { pkg ->
                if (newSelectedPackages.contains(pkg)) newSelectedPackages.remove(pkg)
                else newSelectedPackages.add(pkg)
            }
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }
        persistSelectionAndApply()
    }

    fun selectChinaAppsInCurrentList(): Int {
        return if (_uiState.value.canBrowseApps) {
            applyRegionalSelectionInCurrentList(selectChina = true)
        } else {
            retainRegionalSelectionFromSelectedPackages(selectChina = true)
        }
    }

    fun selectNonChinaAppsInCurrentList(): Int {
        return if (_uiState.value.canBrowseApps) {
            applyRegionalSelectionInCurrentList(selectChina = false)
        } else {
            retainRegionalSelectionFromSelectedPackages(selectChina = false)
        }
    }

    private fun applyRegionalSelectionInCurrentList(selectChina: Boolean): Int {
        var selectedCount = 0
        _uiState.update { state ->
            val currentVisibleApps =
                AccessControlFilter.filterApps(
                    apps = state.apps,
                    query = state.searchQuery,
                    showSystemApps = state.showSystemApps,
                    sortMode = state.sortMode,
                    selectedFirst = state.selectedFirst,
                )
            val currentPackages = currentVisibleApps.mapTo(mutableSetOf()) { it.packageName }
            val targetPackages =
                currentVisibleApps
                    .filter { it.isChinaApp == selectChina }
                    .mapTo(mutableSetOf()) { it.packageName }
            selectedCount = targetPackages.size

            val newSelectedPackages =
                state.selectedPackages.minus(currentPackages).plus(targetPackages)
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }
        persistSelectionAndApply()
        return selectedCount
    }

    private fun retainRegionalSelectionFromSelectedPackages(selectChina: Boolean): Int {
        var selectedCount = 0
        _uiState.update { state ->
            val retainedPackages =
                state.selectedPackages.filterTo(linkedSetOf()) { packageName ->
                    AccessControlClassifier.isChinaPackage(packageName) == selectChina
                }
            selectedCount = retainedPackages.size
            AccessControlSelection.updateSelection(state, retainedPackages)
        }
        persistSelectionAndApply()
        return selectedCount
    }

    fun clearSelectedPackages(): Int {
        val cleared = _uiState.value.selectedPackages.size
        _uiState.update { state -> AccessControlSelection.updateSelection(state, emptySet()) }
        persistSelectionAndApply()
        return cleared
    }

    fun exportPackages(): String {
        return _uiState.value.selectedPackages.joinToString("\n")
    }

    fun importPackages(text: String): ImportResult {
        if (runtimeActionExecutor.isMutating.value) {
            return ImportResult(addedCount = 0, ignoredCount = 0, totalCount = 0)
        }
        val packages =
            text
                .lines()
                .map(AccessControlSelection::normalizeManualPackageName)
                .filter { it.isNotEmpty() }
                .toSet()
        val totalCount = text.lines().count { it.isNotBlank() }
        val previousPackages = _uiState.value.selectedPackages
        var validPackages = emptySet<String>()

        _uiState.update { state ->
            validPackages =
                if (state.canBrowseApps) {
                    packages.intersect(state.apps.map { it.packageName }.toSet())
                } else {
                    packages
                }
            val newSelectedPackages = state.selectedPackages + validPackages
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }

        persistSelectionAndApply()
        val addedCount = (_uiState.value.selectedPackages - previousPackages).size
        val ignoredCount = (totalCount - validPackages.size).coerceAtLeast(0)
        return ImportResult(
            addedCount = addedCount,
            ignoredCount = ignoredCount,
            totalCount = totalCount,
        )
    }

    fun addManualPackage(): Boolean {
        if (runtimeActionExecutor.isMutating.value) return false
        val normalized =
            AccessControlSelection.normalizeManualPackageName(_uiState.value.manualPackageName)
        if (normalized.isEmpty()) {
            return false
        }
        _uiState.update { state ->
            AccessControlSelection.updateSelection(
                state.copy(manualPackageName = ""),
                state.selectedPackages + normalized,
            )
        }
        persistSelectionAndApply()
        return true
    }

    private fun persistSelectionAndApply() {
        networkSettingsStorage.accessControlPackages.set(_uiState.value.selectedPackages)

        applyPackagesJob?.cancel()
        applyPackagesJob =
            viewModelScope.launch {
                delay(50L)

                val snapshot = proxyFacade.runtimeSnapshot.value
                if (snapshot.phase != RuntimePhase.Running) return@launch
                val activeMode = RuntimeStateMapper.modeForOwner(snapshot.owner) ?: return@launch
                val configuredMode = networkSettingsStorage.proxyMode.value
                if (activeMode != configuredMode) return@launch
                if (activeMode == ProxyMode.Http) return@launch
                if (networkSettingsStorage.accessControlMode.value == AccessControlMode.ALLOW_ALL)
                    return@launch

                val outcome =
                    runtimeActionExecutor.applyConfigChange(
                        operation = "access-control:packages",
                        persist = {},
                        shouldRestart = { mode ->
                            mode != ProxyMode.Http &&
                                networkSettingsStorage.accessControlMode.value !=
                                    AccessControlMode.ALLOW_ALL
                        },
                        presentation =
                            RuntimeActionFailurePresentation.Runtime(
                                fallbackMessage = "Failed to apply access control packages",
                                targetMode = activeMode,
                            ),
                    )
                when (outcome) {
                    is RuntimeActionOutcome.Success -> Unit
                    is RuntimeActionOutcome.PermissionRequired -> {
                        vpnPermissionCoordinator.requestPermission(outcome.intent) {
                            persistSelectionAndApply()
                        }
                    }
                    RuntimeActionOutcome.FailureHandled -> Unit
                }
            }
    }
}

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

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.data.model.AccessControlMode
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.store.NetworkSettingsStorage
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeStateMapper
import com.github.yumelira.yumebox.service.root.RootPackageShell
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn

class AccessControlViewModel(
    application: Application,
    private val networkSettingsStorage: NetworkSettingsStorage,
    private val proxyFacade: ProxyFacade,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AccessControlUiState())
    val uiState: StateFlow<AccessControlUiState> = _uiState.asStateFlow()
    val filteredApps: StateFlow<List<AccessControlAppInfo>> = _uiState
        .map { state ->
            AccessControlFilter.filterApps(
                apps = state.apps,
                query = state.searchQuery,
                showSystemApps = state.showSystemApps,
                sortMode = state.sortMode,
                selectedFirst = state.selectedFirst
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private var applyPackagesJob: Job? = null

    init {
        checkAndLoad()
    }

    private fun checkAndLoad() {
        val context = getApplication<Application>()
        val permission = "com.android.permission.GET_INSTALLED_APPS"

        if (RootPackageShell.hasRootAccess()) {
            loadApps()
            return
        }

        val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            loadApps()
        } else {
            val isMiui = runCatching {
                val permissionInfo = context.packageManager.getPermissionInfo(permission, 0)
                permissionInfo.packageName == "com.lbe.security.miui"
            }.getOrElse { false }

            if (isMiui) {
                _uiState.update { it.copy(needsMiuiPermission = true, isLoading = false) }
            } else {
                loadApps()
            }
        }
    }

    fun onPermissionResult() {
        _uiState.update { it.copy(needsMiuiPermission = false) }
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val selectedPackages = networkSettingsStorage.accessControlPackages.value
            val apps = runCatching {
                withContext(Dispatchers.IO) {
                    loadInstalledApps(selectedPackages)
                }
            }.getOrElse {
                _uiState.update { state -> state.copy(isLoading = false, needsMiuiPermission = true) }
                return@launch
            }

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    apps = apps,
                    selectedPackages = selectedPackages,
                )
            }
        }
    }

    private fun loadInstalledApps(selectedPackages: Set<String>): List<AccessControlAppInfo> {
        return AccessControlAppLoader.loadInstalledApps(getApplication(), selectedPackages)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
            )
        }
    }

    fun onSortModeChange(mode: AccessControlSortMode) {
        _uiState.update { state ->
            state.copy(
                sortMode = mode,
            )
        }
    }

    fun onSelectedFirstChange(selectedFirst: Boolean) {
        _uiState.update { state ->
            state.copy(
                selectedFirst = selectedFirst,
            )
        }
    }

    fun onShowSystemAppsChange(show: Boolean) {
        _uiState.update { state ->
            state.copy(
                showSystemApps = show,
            )
        }
    }

    fun onAppSelectionChange(packageName: String, selected: Boolean) {
        _uiState.update { state ->
            val newSelectedPackages = if (selected) {
                state.selectedPackages + packageName
            } else {
                state.selectedPackages - packageName
            }
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }

        persistSelectionAndApply()
    }

    fun selectAll() {
        _uiState.update { state ->
            val visiblePackages = AccessControlSelection.visiblePackages(state)
            AccessControlSelection.updateSelection(state, state.selectedPackages + visiblePackages)
        }

        persistSelectionAndApply()
    }

    fun deselectAll() {
        _uiState.update { state ->
            val visiblePackages = AccessControlSelection.visiblePackages(state)
            AccessControlSelection.updateSelection(state, state.selectedPackages - visiblePackages)
        }

        persistSelectionAndApply()
    }

    fun invertSelection() {
        _uiState.update { state ->
            val allPackages = AccessControlSelection.visiblePackages(state)
            val newSelectedPackages = state.selectedPackages.toMutableSet()
            allPackages.forEach { pkg ->
                if (newSelectedPackages.contains(pkg)) newSelectedPackages.remove(pkg) else newSelectedPackages.add(pkg)
            }
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }
        persistSelectionAndApply()
    }

    fun selectChinaAppsInCurrentList(): Int {
        return applyRegionalSelectionInCurrentList(selectChina = true)
    }

    fun selectNonChinaAppsInCurrentList(): Int {
        return applyRegionalSelectionInCurrentList(selectChina = false)
    }

    private fun applyRegionalSelectionInCurrentList(selectChina: Boolean): Int {
        var selectedCount = 0
        _uiState.update { state ->
            val currentVisibleApps = AccessControlFilter.filterApps(
                apps = state.apps,
                query = state.searchQuery,
                showSystemApps = state.showSystemApps,
                sortMode = state.sortMode,
                selectedFirst = state.selectedFirst,
            )
            val currentPackages = currentVisibleApps.mapTo(mutableSetOf()) { it.packageName }
            val targetPackages = currentVisibleApps
                .filter { it.isChinaApp == selectChina }
                .mapTo(mutableSetOf()) { it.packageName }
            selectedCount = targetPackages.size

            val newSelectedPackages = state.selectedPackages
                .minus(currentPackages)
                .plus(targetPackages)
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }
        persistSelectionAndApply()
        return selectedCount
    }

    fun exportPackages(): String {
        return _uiState.value.selectedPackages.joinToString("\n")
    }

    fun importPackages(text: String): Int {
        val packages = text.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        _uiState.update { state ->
            val validPackages = packages.intersect(state.apps.map { it.packageName }.toSet())
            val newSelectedPackages = state.selectedPackages + validPackages
            AccessControlSelection.updateSelection(state, newSelectedPackages)
        }

        persistSelectionAndApply()
        return packages.intersect(_uiState.value.apps.map { it.packageName }.toSet()).size
    }

    private fun persistSelectionAndApply() {
        networkSettingsStorage.accessControlPackages.set(_uiState.value.selectedPackages)

        applyPackagesJob?.cancel()
        applyPackagesJob = viewModelScope.launch {
            delay(350L)

            if (!proxyFacade.isRunning.value) return@launch
            val activeMode = RuntimeStateMapper.modeForOwner(proxyFacade.runtimeSnapshot.value.owner)
            if (activeMode == ProxyMode.Http) return@launch
            if (networkSettingsStorage.accessControlMode.value == AccessControlMode.ALLOW_ALL) return@launch

            runCatching {
                proxyFacade.startProxy(activeMode ?: networkSettingsStorage.proxyMode.value)
            }
        }
    }

}

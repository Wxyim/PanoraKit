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
import com.github.yumelira.yumebox.core.model.RootTunDnsMode
import com.github.yumelira.yumebox.data.model.AccessControlMode
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.model.TunStack
import com.github.yumelira.yumebox.data.repository.AppSettingsRepository
import com.github.yumelira.yumebox.data.store.Preference
import com.github.yumelira.yumebox.data.store.NetworkSettingsStorage
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeStateMapper
import com.github.yumelira.yumebox.service.root.RootPackageShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NetworkSettingsUiState(
    val serviceState: ServiceState = ServiceState.Stopped,
    val configuredMode: ProxyMode = ProxyMode.Tun,
    val effectiveMode: ProxyMode = ProxyMode.Tun,
    val needsRestart: Boolean = false,
    val showServiceOptions: Boolean = true,
    val showTunOnlyOptions: Boolean = true,
    val showAccessControlMode: Boolean = true,
    val showRootTunAdvanced: Boolean = false,
    val showFakeIpRange: Boolean = false,
)

enum class ServiceState {
    Running, Stopped
}

private object RootTunDraftFormatter {
    const val DefaultRootTunIfName = "Yume"
    const val DefaultFakeIpRange = "198.18.0.1/16"
    const val DefaultFakeIpRange6 = "fc00::/18"

    fun normalizeInterfaceName(value: String): String {
        return value.trim().ifBlank { DefaultRootTunIfName }
    }

    fun normalizeMtu(value: String): Int? {
        return value.trim().toIntOrNull()?.takeIf { it > 0 }
    }

    fun normalizeAndroidUsers(value: String): List<Int> {
        return value
            .split(',', '\n')
            .map(String::trim)
            .filter(String::isNotEmpty)
            .mapNotNull(String::toIntOrNull)
            .filter { it >= 0 }
            .distinct()
            .sorted()
            .ifEmpty { listOf(0, 10) }
    }

    fun normalizeRouteExcludes(value: String): List<String> {
        return value
            .split(',', '\n')
            .map(String::trim)
            .filter(String::isNotEmpty)
    }

    fun normalizeFakeIpRange(value: String): String {
        return value.trim().ifBlank { DefaultFakeIpRange }
    }

    fun normalizeFakeIpRange6(value: String): String {
        return value.trim().ifBlank { DefaultFakeIpRange6 }
    }
}

private object NetworkSettingsUiStateFactory {
    fun create(
        serviceState: ServiceState,
        configuredMode: ProxyMode,
        runtimeSnapshot: com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot,
        dnsMode: RootTunDnsMode,
    ): NetworkSettingsUiState {
        val effectiveMode = RuntimeStateMapper.resolveDisplayMode(runtimeSnapshot, configuredMode)
        val activeMode = RuntimeStateMapper.modeForOwner(runtimeSnapshot.owner)
        return NetworkSettingsUiState(
            serviceState = serviceState,
            configuredMode = configuredMode,
            effectiveMode = effectiveMode,
            needsRestart = serviceState == ServiceState.Running && activeMode != configuredMode,
            showServiceOptions = configuredMode != ProxyMode.Http,
            showTunOnlyOptions = configuredMode == ProxyMode.Tun,
            showAccessControlMode = configuredMode != ProxyMode.Http,
            showRootTunAdvanced = configuredMode == ProxyMode.RootTun,
            showFakeIpRange = configuredMode == ProxyMode.RootTun && dnsMode == RootTunDnsMode.FakeIp,
        )
    }
}

class NetworkSettingsViewModel(
    application: Application,
    networkSettingsStorage: NetworkSettingsStorage,
    appSettingsRepository: AppSettingsRepository,
    private val proxyFacade: ProxyFacade,
) : AndroidViewModel(application) {
    companion object {
        private const val MIUI_GET_INSTALLED_APPS_PERMISSION = "com.android.permission.GET_INSTALLED_APPS"
        private const val RESTART_DEBOUNCE_DELAY_MS = 300L
    }

    private var restartJob: Job? = null

    val proxyMode: Preference<ProxyMode> = networkSettingsStorage.proxyMode
    val bypassPrivateNetwork: Preference<Boolean> = networkSettingsStorage.bypassPrivateNetwork
    val dnsHijack: Preference<Boolean> = networkSettingsStorage.dnsHijack
    val allowBypass: Preference<Boolean> = networkSettingsStorage.allowBypass
    val enableIPv6: Preference<Boolean> = networkSettingsStorage.enableIPv6
    val systemProxy: Preference<Boolean> = networkSettingsStorage.systemProxy
    val tunStack: Preference<TunStack> = networkSettingsStorage.tunStack
    val rootTunAutoRoute: Preference<Boolean> = networkSettingsStorage.rootTunAutoRoute
    val rootTunStrictRoute: Preference<Boolean> = networkSettingsStorage.rootTunStrictRoute
    val rootTunAutoRedirect: Preference<Boolean> = networkSettingsStorage.rootTunAutoRedirect
    val rootTunDnsMode: Preference<RootTunDnsMode> = networkSettingsStorage.rootTunDnsMode
    val allowNonLocalhostHttpRemote: Preference<Boolean> = appSettingsRepository.allowNonLocalhostHttpRemote
    val accessControlMode: Preference<AccessControlMode> = networkSettingsStorage.accessControlMode

    private val rootTunIfName = networkSettingsStorage.rootTunIfName
    private val rootTunMtu = networkSettingsStorage.rootTunMtu
    private val rootTunIncludeAndroidUser = networkSettingsStorage.rootTunIncludeAndroidUser
    private val rootTunRouteExcludeAddress = networkSettingsStorage.rootTunRouteExcludeAddress
    private val rootTunFakeIpRange = networkSettingsStorage.rootTunFakeIpRange
    private val rootTunFakeIpRange6 = networkSettingsStorage.rootTunFakeIpRange6

    private val _rootTunIfNameDraft = MutableStateFlow(rootTunIfName.value)
    val rootTunIfNameDraft: StateFlow<String> = _rootTunIfNameDraft.asStateFlow()

    private val _rootTunMtuDraft = MutableStateFlow(rootTunMtu.value.toString())
    val rootTunMtuDraft: StateFlow<String> = _rootTunMtuDraft.asStateFlow()

    private val _rootTunIncludeAndroidUserDraft = MutableStateFlow(
        rootTunIncludeAndroidUser.value.joinToString(", ")
    )
    val rootTunIncludeAndroidUserDraft: StateFlow<String> = _rootTunIncludeAndroidUserDraft.asStateFlow()

    private val _rootTunRouteExcludeAddressDraft = MutableStateFlow(
        rootTunRouteExcludeAddress.value.joinToString("\n")
    )
    val rootTunRouteExcludeAddressDraft: StateFlow<String> = _rootTunRouteExcludeAddressDraft.asStateFlow()

    private val _rootTunFakeIpRangeDraft = MutableStateFlow(rootTunFakeIpRange.value)
    val rootTunFakeIpRangeDraft: StateFlow<String> = _rootTunFakeIpRangeDraft.asStateFlow()

    private val _rootTunFakeIpRange6Draft = MutableStateFlow(rootTunFakeIpRange6.value)
    val rootTunFakeIpRange6Draft: StateFlow<String> = _rootTunFakeIpRange6Draft.asStateFlow()

    private val _errors = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    private val runtimeSnapshot = proxyFacade.runtimeSnapshot

    val serviceState: StateFlow<ServiceState> = runtimeSnapshot
        .map { snapshot -> if (RuntimeStateMapper.isActuallyRunning(snapshot)) ServiceState.Running else ServiceState.Stopped }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ServiceState.Stopped)

    val currentProxyMode: StateFlow<ProxyMode> = proxyMode.state

    val uiState: StateFlow<NetworkSettingsUiState> = combine(
        serviceState,
        currentProxyMode,
        runtimeSnapshot,
        rootTunDnsMode.state,
    ) { serviceState, configuredMode, snapshot, dnsMode ->
        NetworkSettingsUiStateFactory.create(serviceState, configuredMode, snapshot, dnsMode)
    }
        .distinctUntilChanged()
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkSettingsUiState(),
    )

    fun onProxyModeChange(mode: ProxyMode) {
        proxyMode.set(mode)
    }

    fun onBypassPrivateNetworkChange(enabled: Boolean) {
        updatePreference(bypassPrivateNetwork, enabled)
    }

    fun onDnsHijackChange(enabled: Boolean) {
        updatePreference(dnsHijack, enabled)
    }

    fun onAllowBypassChange(enabled: Boolean) {
        updatePreference(allowBypass, enabled)
    }

    fun onEnableIPv6Change(enabled: Boolean) {
        updatePreference(enableIPv6, enabled)
    }

    fun onSystemProxyChange(enabled: Boolean) {
        updatePreference(systemProxy, enabled)
    }

    fun onTunStackChange(stack: TunStack) {
        updatePreference(tunStack, stack)
    }

    fun onRootTunAutoRouteChange(enabled: Boolean) {
        updatePreference(rootTunAutoRoute, enabled)
    }

    fun onRootTunStrictRouteChange(enabled: Boolean) {
        updatePreference(rootTunStrictRoute, enabled)
    }

    fun onRootTunAutoRedirectChange(enabled: Boolean) {
        updatePreference(rootTunAutoRedirect, enabled)
    }

    fun onRootTunDnsModeChange(mode: RootTunDnsMode) {
        updatePreference(rootTunDnsMode, mode)
    }

    fun onAccessControlModeChange(mode: AccessControlMode) {
        if (!canUseAccessControlMode(mode)) {
            _errors.tryEmit("Root 模式下该访问控制模式需要完整应用列表访问权限，请先授权或切换为仅允许指定应用。")
            return
        }
        updatePreference(accessControlMode, mode)
    }

    fun onAllowNonLocalhostHttpRemoteChange(enabled: Boolean) {
        updatePreference(allowNonLocalhostHttpRemote, enabled)
    }

    private fun canUseAccessControlMode(mode: AccessControlMode): Boolean {
        if (proxyMode.value != ProxyMode.RootTun) return true
        if (mode == AccessControlMode.ALLOW_SPECIFIC) return true
        return hasFullPackageAccess()
    }

    private fun hasFullPackageAccess(): Boolean {
        if (RootPackageShell.hasRootAccess()) return true

        val context = getApplication<Application>()
        val permissionExists = runCatching {
            context.packageManager.getPermissionInfo(MIUI_GET_INSTALLED_APPS_PERMISSION, 0)
            true
        }.getOrDefault(false)

        if (!permissionExists) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            MIUI_GET_INSTALLED_APPS_PERMISSION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onRootTunIfNameDraftChange(value: String) {
        _rootTunIfNameDraft.value = value
    }

    fun commitRootTunIfName() {
        val normalized = RootTunDraftFormatter.normalizeInterfaceName(_rootTunIfNameDraft.value)
        _rootTunIfNameDraft.value = normalized
        updatePreference(rootTunIfName, normalized)
    }

    fun onRootTunMtuDraftChange(value: String) {
        _rootTunMtuDraft.value = value
    }

    fun commitRootTunMtu() {
        val parsed = RootTunDraftFormatter.normalizeMtu(_rootTunMtuDraft.value) ?: return
        _rootTunMtuDraft.value = parsed.toString()
        updatePreference(rootTunMtu, parsed)
    }

    fun onRootTunIncludeAndroidUserDraftChange(value: String) {
        _rootTunIncludeAndroidUserDraft.value = value
    }

    fun commitRootTunIncludeAndroidUser() {
        val normalized = RootTunDraftFormatter.normalizeAndroidUsers(_rootTunIncludeAndroidUserDraft.value)
        _rootTunIncludeAndroidUserDraft.value = normalized.joinToString(", ")
        updatePreference(rootTunIncludeAndroidUser, normalized)
    }

    fun onRootTunRouteExcludeAddressDraftChange(value: String) {
        _rootTunRouteExcludeAddressDraft.value = value
    }

    fun commitRootTunRouteExcludeAddress() {
        val normalized = RootTunDraftFormatter.normalizeRouteExcludes(_rootTunRouteExcludeAddressDraft.value)
        _rootTunRouteExcludeAddressDraft.value = normalized.joinToString("\n")
        updatePreference(rootTunRouteExcludeAddress, normalized)
    }

    fun onRootTunFakeIpRangeDraftChange(value: String) {
        _rootTunFakeIpRangeDraft.value = value
    }

    fun commitRootTunFakeIpRange() {
        val normalized = RootTunDraftFormatter.normalizeFakeIpRange(_rootTunFakeIpRangeDraft.value)
        _rootTunFakeIpRangeDraft.value = normalized
        updatePreference(rootTunFakeIpRange, normalized)
    }

    fun onRootTunFakeIpRange6DraftChange(value: String) {
        _rootTunFakeIpRange6Draft.value = value
    }

    fun commitRootTunFakeIpRange6() {
        val normalized = RootTunDraftFormatter.normalizeFakeIpRange6(_rootTunFakeIpRange6Draft.value)
        _rootTunFakeIpRange6Draft.value = normalized
        updatePreference(rootTunFakeIpRange6, normalized)
    }

    fun startService(mode: ProxyMode) {
        viewModelScope.launch {
            switchService(mode).onFailure { error ->
                _errors.tryEmit(error.runtimeGatewayMessage("Failed to start proxy service"))
            }
        }
    }

    fun restartService() {
        viewModelScope.launch {
            if (!RuntimeStateMapper.isActuallyRunning(runtimeSnapshot.value)) return@launch
            switchService(proxyMode.value).onFailure { error ->
                _errors.tryEmit(error.runtimeGatewayMessage("Failed to restart proxy service"))
            }
        }
    }

    private suspend fun switchService(mode: ProxyMode): Result<Unit> = runCatching {
        proxyMode.set(mode)
        withContext(Dispatchers.IO) {
            proxyFacade.startProxy(mode)
        }
    }

    private fun updateServiceConfig() {
        restartJob?.cancel()
        restartJob = viewModelScope.launch {
            delay(RESTART_DEBOUNCE_DELAY_MS)
            if (RuntimeStateMapper.isActuallyRunning(runtimeSnapshot.value)) {
                restartService()
            }
        }
    }

    private fun <T> updatePreference(preference: Preference<T>, value: T) {
        if (preference.value == value) return
        preference.set(value)
        updateServiceConfig()
    }

}

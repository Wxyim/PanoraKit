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



package com.github.yumelira.yumebox.screen.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.core.model.TunnelState
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.repository.IpMonitoringState
import com.github.yumelira.yumebox.data.repository.NetworkInfoService
import com.github.yumelira.yumebox.data.repository.ProxyChainResolver
import com.github.yumelira.yumebox.data.store.NetworkSettingsStorage
import com.github.yumelira.yumebox.data.store.ProxyDisplaySettingsStore
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeStateMapper
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

data class HomeSelectedServerState(
    val groupName: String?,
    val name: String?,
    val delay: Int?,
)

data class SpeedHistoryBuffer(
    val samples: LongArray,
    val head: Int,
    val size: Int,
    val version: Long,
) {
    companion object {
        fun create(capacity: Int): SpeedHistoryBuffer {
            val normalized = capacity.coerceAtLeast(1)
            return SpeedHistoryBuffer(
                samples = LongArray(normalized),
                head = 0,
                size = 0,
                version = 0L,
            )
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isStartingProxy: Boolean = false,
    val loadingProgress: String? = null,
    val message: String? = null,
    val error: String? = null,
)

private object HomeSpeedSampler {
    fun sampleTraffic(
        phase: RuntimePhase,
        trafficReady: Boolean,
        latestTraffic: Long,
        previousSample: Long,
    ): Long {
        return when {
            phase == RuntimePhase.Idle || phase == RuntimePhase.Failed -> 0L
            phase == RuntimePhase.Starting && !trafficReady -> previousSample
            phase.running -> {
                val traffic = com.github.yumelira.yumebox.domain.model.TrafficData.from(latestTraffic)
                (traffic.upload + traffic.download).coerceAtLeast(0L)
            }

            else -> 0L
        }
    }

    fun latestSample(state: SpeedHistoryBuffer): Long {
        if (state.size <= 0 || state.samples.isEmpty()) return 0L
        val capacity = state.samples.size
        val lastIndex = if (state.head == 0) capacity - 1 else state.head - 1
        return state.samples[lastIndex]
    }

    fun appendSample(
        state: SpeedHistoryBuffer,
        sample: Long,
    ): SpeedHistoryBuffer {
        if (state.samples.isEmpty()) return state
        val capacity = state.samples.size
        state.samples[state.head] = sample
        return state.copy(
            head = (state.head + 1) % capacity,
            size = (state.size + 1).coerceAtMost(capacity),
            version = state.version + 1,
        )
    }

    fun reset(state: SpeedHistoryBuffer): SpeedHistoryBuffer {
        if (state.size == 0) return state
        state.samples.fill(0L)
        return state.copy(
            head = 0,
            size = 0,
            version = state.version + 1,
        )
    }
}

private object HomeProxySelectionResolver {
    fun resolveGlobalDisplayGroup(groups: List<com.github.yumelira.yumebox.domain.model.ProxyGroupInfo>): com.github.yumelira.yumebox.domain.model.ProxyGroupInfo? {
        return listOf("GLOBAL", "Global", "Proxy")
            .firstNotNullOfOrNull { candidate ->
                groups.firstOrNull { group ->
                    group.name.equals(candidate, ignoreCase = true) && isDisplayableSelectionName(group.now)
                }
            }
    }

    fun resolveFirstStrategyGroup(
        groups: List<com.github.yumelira.yumebox.domain.model.ProxyGroupInfo>,
        visibleGroupNames: Set<String>,
    ): com.github.yumelira.yumebox.domain.model.ProxyGroupInfo? {
        groups.firstOrNull { group ->
            group.name in visibleGroupNames &&
                group.type.group &&
                isDisplayableSelectionName(group.now)
        }?.let { return it }

        return groups.firstOrNull { group ->
            group.type.group && isDisplayableSelectionName(group.now)
        }
    }

    fun buildSelectedServerState(
        mainGroup: com.github.yumelira.yumebox.domain.model.ProxyGroupInfo,
        groups: List<com.github.yumelira.yumebox.domain.model.ProxyGroupInfo>,
        resolveEndNode: (String, List<com.github.yumelira.yumebox.domain.model.ProxyGroupInfo>) -> com.github.yumelira.yumebox.core.model.Proxy?,
    ): HomeSelectedServerState {
        val selectedProxy = mainGroup.proxies.firstOrNull { it.name == mainGroup.now && !it.type.group }
        val resolvedProxy = mainGroup.now
            .takeIf(::isDisplayableSelectionName)
            ?.let { resolveEndNode(it, groups) }
        val displayName = resolvedProxy?.name
            ?: selectedProxy?.name
            ?: mainGroup.now.takeIf(::isDisplayableSelectionName)

        return HomeSelectedServerState(
            groupName = mainGroup.name.takeIf(::isDisplayableSelectionName),
            name = displayName ?: MLang.Home.Profile.Direct,
            delay = normalizeDisplayDelay(resolvedProxy?.delay)
                ?: normalizeDisplayDelay(selectedProxy?.delay),
        )
    }

    private fun normalizeDisplayDelay(delay: Int?): Int? = when {
        delay == null -> null
        delay < 0 -> delay
        delay == 0 -> null
        delay in 1..3000 -> delay
        else -> null
    }

    private fun isDisplayableSelectionName(name: String?): Boolean {
        return !name.isNullOrBlank() && name != "-"
    }
}

class HomeViewModel(
    application: Application,
    private val proxyFacade: ProxyFacade,
    private val profilesRepository: ProfilesRepository,
    private val networkInfoService: NetworkInfoService,
    private val proxyChainResolver: ProxyChainResolver,
    private val proxyDisplaySettingsStore: ProxyDisplaySettingsStore,
    private val networkSettingsStorage: NetworkSettingsStorage,
) : AndroidViewModel(application) {


    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _recommendedProfile = MutableStateFlow<Profile?>(null)
    val recommendedProfile: StateFlow<Profile?> = _recommendedProfile.asStateFlow()

    private val _profilesLoaded = MutableStateFlow(false)
    val profilesLoaded: StateFlow<Boolean> = _profilesLoaded.asStateFlow()

    val hasEnabledProfile: Flow<Boolean> = profiles.map { list ->
        list.any { it.active }
    }.distinctUntilChanged()

    val runtimeSnapshot = proxyFacade.runtimeSnapshot
    val isRunning = runtimeSnapshot
        .map(RuntimeStateMapper::isActuallyRunning)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RuntimeStateMapper.isActuallyRunning(runtimeSnapshot.value))
    val currentProfile = proxyFacade.currentProfile
    val trafficNow = proxyFacade.trafficNow
    val proxyGroups = proxyFacade.proxyGroups

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _displayRunning = MutableStateFlow(false)
    val displayRunning: StateFlow<Boolean> = _displayRunning.asStateFlow()

    private val _isToggling = MutableStateFlow(false)
    val isToggling: StateFlow<Boolean> = _isToggling.asStateFlow()

    private val _proxyMode = MutableStateFlow(ProxyMode.Tun)
    val proxyMode: StateFlow<ProxyMode> = _proxyMode.asStateFlow()

    private val _vpnPrepareIntent = MutableSharedFlow<Intent>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val vpnPrepareIntent = _vpnPrepareIntent.asSharedFlow()

    private val _speedHistory = MutableStateFlow(SpeedHistoryBuffer.create(24))
    val speedHistory: StateFlow<SpeedHistoryBuffer> = _speedHistory.asStateFlow()
    private var speedSamplingJob: Job? = null

    private val currentTunnelMode: StateFlow<TunnelState.Mode> =
        proxyDisplaySettingsStore.proxyMode.state
            .stateIn(viewModelScope, SharingStarted.Eagerly, TunnelState.Mode.Rule)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val visibleProxyGroupNames: StateFlow<Set<String>> =
        runtimeSnapshot
            .map { snapshot ->
                if (RuntimeStateMapper.isActuallyRunning(snapshot)) snapshot.generation else null
            }
            .distinctUntilChanged()
            .flatMapLatest { runningGeneration ->
                if (runningGeneration == null) {
                    flowOf(emptySet())
                } else {
                    flow {
                        val names = runCatching {
                            proxyFacade.queryProxyGroupNames(excludeNotSelectable = true).toSet()
                        }.getOrDefault(emptySet())
                        emit(names)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val selectedServer: StateFlow<HomeSelectedServerState?> =
        combine(runtimeSnapshot, proxyGroups, currentTunnelMode, visibleProxyGroupNames) { snapshot, groups, tunnelMode, visibleGroupNames ->
            if (!RuntimeStateMapper.isActuallyRunning(snapshot) || groups.isEmpty()) return@combine null

            when (tunnelMode) {
                TunnelState.Mode.Direct -> HomeSelectedServerState(
                    groupName = null,
                    name = MLang.Home.Profile.Direct,
                    delay = null,
                )

                TunnelState.Mode.Global -> {
                    val mainGroup = HomeProxySelectionResolver.resolveGlobalDisplayGroup(groups)
                        ?: HomeProxySelectionResolver.resolveFirstStrategyGroup(groups, visibleGroupNames)
                        ?: return@combine null
                    HomeProxySelectionResolver.buildSelectedServerState(mainGroup, groups, proxyChainResolver::resolveEndNode)
                }

                TunnelState.Mode.Rule,
                TunnelState.Mode.Script,
                -> {
                    val mainGroup = HomeProxySelectionResolver.resolveFirstStrategyGroup(groups, visibleGroupNames)
                        ?: return@combine null
                    HomeProxySelectionResolver.buildSelectedServerState(mainGroup, groups, proxyChainResolver::resolveEndNode)
                }
            }
        }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ipMonitoringState: StateFlow<IpMonitoringState> = isRunning.flatMapLatest { running ->
        if (running) {
            networkInfoService.startIpMonitoring(isRunning)
        } else {
            flowOf(IpMonitoringState.Loading)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IpMonitoringState.Loading)

    init {
        refreshProfiles()
        syncDisplayState()
        observeRuntimeFailures()
        syncProxyModeState()
        observeProfileChanges()
        startSpeedSampling()
    }

    fun setScreenActive(active: Boolean) {
        // Sampling is always-on in background; keep API for call-site compatibility.
    }

    private fun refreshProfiles() {
        viewModelScope.launch {
            try {
                val allProfiles = profilesRepository.queryAllProfiles()
                val active = profilesRepository.queryActiveProfile()
                _profiles.value = allProfiles
                _recommendedProfile.value = active
                _profilesLoaded.value = true
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh profiles")
                _profilesLoaded.value = true
            }
        }
    }

    private fun observeProfileChanges() {
        viewModelScope.launch {
            proxyFacade.currentProfile
                .map { profile -> profile?.uuid to profile?.updatedAt }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    refreshProfiles()
                }
        }
    }

    private fun syncDisplayState() {
        viewModelScope.launch {
            runtimeSnapshot.collect { snapshot ->
                val runningOrStarting = RuntimeStateMapper.isRunningOrStarting(snapshot)
                if (snapshot.phase == RuntimePhase.Idle || snapshot.phase == RuntimePhase.Failed) {
                    _speedHistory.update { previous -> HomeSpeedSampler.reset(previous) }
                }
                val isStarting = snapshot.phase == RuntimePhase.Starting
                val loadingProgress = if (isStarting) MLang.Home.Message.Preparing else null
                _uiState.update { current ->
                    if (current.isStartingProxy == isStarting && current.loadingProgress == loadingProgress) {
                        current
                    } else {
                        current.copy(
                            isStartingProxy = isStarting,
                            loadingProgress = loadingProgress,
                        )
                    }
                }
                when (snapshot.phase) {
                    RuntimePhase.Starting -> {
                        _displayRunning.value = true
                    }

                    RuntimePhase.Stopping -> {
                        if (!_isToggling.value) {
                            _displayRunning.value = false
                        }
                    }

                    else -> {
                        _displayRunning.value = runningOrStarting
                        _isToggling.value = false
                    }
                }
            }
        }
    }

    private fun syncProxyModeState() {
        viewModelScope.launch {
            runtimeSnapshot
                .map { snapshot -> RuntimeStateMapper.resolveDisplayMode(snapshot, networkSettingsStorage.proxyMode.value) }
                .distinctUntilChanged()
                .collect { mode ->
                    _proxyMode.value = mode
                }
        }
    }

    private fun observeRuntimeFailures() {
        viewModelScope.launch {
            runtimeSnapshot
                .drop(1)
                .map { snapshot -> Triple(snapshot.phase, snapshot.lastError, snapshot.generation) }
                .distinctUntilChanged()
                .collect { (phase, lastError, _) ->
                    if (phase == RuntimePhase.Failed && !lastError.isNullOrBlank()) {
                        showError(lastError)
                    }
                }
        }
    }

    fun refreshProxyMode() {
        val configuredMode = networkSettingsStorage.proxyMode.value
        _proxyMode.value = RuntimeStateMapper.resolveDisplayMode(runtimeSnapshot.value, configuredMode)
    }



    suspend fun reloadProfile() {
        try {
            setLoading(true)

            val activeProfile = profilesRepository.queryActiveProfile()
            if (activeProfile == null) {
                showError(MLang.Home.Message.ConfigSwitchFailed.format(MLang.ProfilesVM.Error.ProfileNotExist))
                return
            }

            profilesRepository.updateProfile(activeProfile.uuid)

            profilesRepository.setActiveProfile(activeProfile.uuid)
            showMessage(MLang.Home.Message.ConfigSwitched)
        } catch (e: Exception) {
            Timber.e(e, "Failed to reload profile")
            showError(MLang.Home.Message.ConfigSwitchFailed.format(e.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)))
        } finally {
            setLoading(false)
        }
    }

    fun isCurrentProfile(profileId: java.util.UUID): Boolean {
        return currentProfile.value?.uuid == profileId
    }

    fun startProxy(profileId: String, mode: ProxyMode? = null) {
        if (_isToggling.value) return

        viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            _isToggling.value = true
            _displayRunning.value = true
            _uiState.update {
                it.copy(
                    isStartingProxy = true,
                    loadingProgress = MLang.Home.Message.Preparing
                )
            }

            try {
                val proxyMode = mode ?: networkSettingsStorage.proxyMode.value
                _proxyMode.value = proxyMode
                Timber.d("Home startProxy kickoff: mode=$proxyMode profileId=$profileId")

                withContext(Dispatchers.IO) {
                    if (profileId.isNotBlank()) {
                        profilesRepository.setActiveProfile(java.util.UUID.fromString(profileId))
                    }

                    proxyFacade.startProxy(proxyMode)
                }

                Timber.i("Home startProxy completed in ${System.currentTimeMillis() - startedAt}ms, mode=$proxyMode")
            } catch (e: com.github.yumelira.yumebox.remote.VpnPermissionRequired) {

                _uiState.update { it.copy(isStartingProxy = false, loadingProgress = null) }
                _vpnPrepareIntent.emit(e.intent)
                _displayRunning.value = false
                _isToggling.value = false
                Timber.i("VPN permission required")
            } catch (e: Exception) {
                _displayRunning.value = false
                _isToggling.value = false
                _uiState.update { it.copy(isStartingProxy = false, loadingProgress = null) }
                Timber.e(e, "Failed to start proxy")
                showError(MLang.Home.Message.StartFailed.format(e.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)))
            }
        }
    }

    suspend fun stopProxy() {
        if (_isToggling.value) return

        _isToggling.value = true
        _displayRunning.value = false
        setLoading(true)

        try {
            withContext(Dispatchers.IO) {
                proxyFacade.stopProxy()
            }
            _isToggling.value = false
        } catch (e: Exception) {
            _displayRunning.value = true
            _isToggling.value = false
            Timber.e(e, "Failed to stop proxy")
            showError(MLang.Home.Message.StopFailed.format(e.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)))
        }

        setLoading(false)
    }

    private fun startSpeedSampling() {
        if (speedSamplingJob?.isActive == true) return
        speedSamplingJob = viewModelScope.launch {
            while (kotlin.coroutines.coroutineContext[Job]?.isActive != false) {
                try {
                    val snapshot = runtimeSnapshot.value
                    val latestTraffic = proxyFacade.trafficNow.value
                    val phase = snapshot.phase
                    val trafficReady = snapshot.trafficReady
                    _speedHistory.update { previous ->
                        val sample = HomeSpeedSampler.sampleTraffic(
                            phase = phase,
                            trafficReady = trafficReady,
                            latestTraffic = latestTraffic,
                            previousSample = HomeSpeedSampler.latestSample(previous),
                        )
                        HomeSpeedSampler.appendSample(
                            state = previous,
                            sample = sample,
                        )
                    }
                } catch (e: Exception) {
                    Timber.w(e, "Speed sampling loop failed")
                }
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    private fun stopSpeedSampling() {
        speedSamplingJob?.cancel()
        speedSamplingJob = null
    }

    private fun setLoading(loading: Boolean) = _uiState.update { current ->
        if (current.isLoading == loading) current else current.copy(isLoading = loading)
    }
    private fun showMessage(message: String) = _uiState.update { current ->
        if (current.message == message) current else current.copy(message = message)
    }
    private fun showError(error: String) = _uiState.update { current ->
        if (current.error == error) current else current.copy(error = error)
    }
    fun consumeMessage() = _uiState.update { it.copy(message = null) }
    fun consumeError() = _uiState.update { it.copy(error = null) }

    override fun onCleared() {
        stopSpeedSampling()
        super.onCleared()
    }
}

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

package com.github.nomadboxlab.monadbox.feature.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.repository.IpInfo
import com.github.nomadboxlab.monadbox.data.repository.IpMonitoringState
import com.github.nomadboxlab.monadbox.data.repository.NetworkInfoService
import com.github.nomadboxlab.monadbox.data.repository.ProxyChainResolver
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import com.github.nomadboxlab.monadbox.data.store.NetworkSettingsStorage
import com.github.nomadboxlab.monadbox.data.store.ProxyDisplaySettingsStore
import com.github.nomadboxlab.monadbox.domain.model.ErrorImpact
import com.github.nomadboxlab.monadbox.domain.model.ErrorPhase
import com.github.nomadboxlab.monadbox.domain.model.ErrorRetryability
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
import com.github.nomadboxlab.monadbox.feature.home.usecase.RefreshHomeEntryDataUseCase
import com.github.nomadboxlab.monadbox.feature.home.usecase.ReloadHomeProfileUseCase
import com.github.nomadboxlab.monadbox.feature.home.usecase.StartHomeProxyUseCase
import com.github.nomadboxlab.monadbox.feature.home.usecase.StopHomeProxyUseCase
import com.github.nomadboxlab.monadbox.presentation.component.GlobalDialogPresenter
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionOutcome
import com.github.nomadboxlab.monadbox.presentation.runtime.VpnPermissionCoordinator
import com.github.nomadboxlab.monadbox.remote.VpnPermissionRequired
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeStateMapper
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeFailurePresenter
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import dev.oom_wg.purejoy.mlang.MLang
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

data class HomeSelectedServerState(val groupName: String?, val name: String?, val delay: Int?)

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

@Stable
data class HomeUiState(
    val isLoading: Boolean = false,
    val isStartingProxy: Boolean = false,
    val loadingProgress: String? = null,
    val message: String? = null,
    val error: String? = null,
    val structuredError: StructuredError? = null,
)

enum class HomeRuntimeVisualState {
    Idle,
    Starting,
    Running,
    Stopping,
}

data class HomeProfilesState(
    val profiles: List<Profile> = emptyList(),
    val recommendedProfile: Profile? = null,
    val profilesLoaded: Boolean = false,
)

@Stable
data class HomeChromeState(
    val ui: HomeUiState = HomeUiState(),
    val runtimeVisualState: HomeRuntimeVisualState = HomeRuntimeVisualState.Idle,
    val displayRunning: Boolean = false,
    val isToggling: Boolean = false,
    val proxyMode: ProxyMode = ProxyMode.Tun,
)

@Stable
private data class RuntimeUiSnapshot(
    val ipState: IpMonitoringState,
    val speedHistory: SpeedHistoryBuffer,
    val trafficNow: Long,
    val externalIpEnabled: Boolean,
    val externalIpQuerying: Boolean,
)

data class HomeScreenState(
    val ui: HomeUiState = HomeUiState(),
    val runtimeVisualState: HomeRuntimeVisualState = HomeRuntimeVisualState.Idle,
    val displayRunning: Boolean = false,
    val isToggling: Boolean = false,
    val proxyMode: ProxyMode = ProxyMode.Tun,
    val profiles: List<Profile> = emptyList(),
    val recommendedProfile: Profile? = null,
    val profilesLoaded: Boolean = false,
    val hasEnabledProfile: Boolean = false,
    val currentProfile: Profile? = null,
    val selectedServer: HomeSelectedServerState? = null,
    val ipMonitoringState: IpMonitoringState = IpMonitoringState.Loading,
    val isExternalIpLookupEnabled: Boolean = false,
    val isExternalIpQuerying: Boolean = false,
    val speedHistory: SpeedHistoryBuffer = SpeedHistoryBuffer.create(24),
    val trafficNow: Long = 0L,
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
                val traffic =
                    com.github.nomadboxlab.monadbox.domain.model.TrafficData.from(latestTraffic)
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

    fun appendSample(state: SpeedHistoryBuffer, sample: Long): SpeedHistoryBuffer {
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
        return state.copy(head = 0, size = 0, version = state.version + 1)
    }
}

private data class HomeTrafficSample(
    val phase: RuntimePhase,
    val trafficReady: Boolean,
    val latestTraffic: Long,
)

private object HomeProxySelectionResolver {
    fun resolveGlobalDisplayGroup(
        groups: List<com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo>
    ): com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo? {
        return listOf("GLOBAL", "Global", "Proxy").firstNotNullOfOrNull { candidate ->
            groups.firstOrNull { group ->
                group.name.equals(candidate, ignoreCase = true) &&
                    isDisplayableSelectionName(group.now)
            }
        }
    }

    fun resolveFirstStrategyGroup(
        groups: List<com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo>,
        visibleGroupNames: Set<String>,
    ): com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo? {
        groups
            .firstOrNull { group ->
                group.name in visibleGroupNames &&
                    group.type.group &&
                    isDisplayableSelectionName(group.now)
            }
            ?.let {
                return it
            }

        return groups.firstOrNull { group ->
            group.type.group && isDisplayableSelectionName(group.now)
        }
    }

    fun buildSelectedServerState(
        mainGroup: com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo,
        groups: List<com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo>,
        resolveEndNode:
            (
                String, List<com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo>,
            ) -> com.github.nomadboxlab.monadbox.core.model.Proxy?,
    ): HomeSelectedServerState {
        val selectedProxy =
            mainGroup.proxies.firstOrNull { it.name == mainGroup.now && !it.type.group }
        val resolvedProxy =
            mainGroup.now.takeIf(::isDisplayableSelectionName)?.let { resolveEndNode(it, groups) }
        val displayName =
            resolvedProxy?.name
                ?: selectedProxy?.name
                ?: mainGroup.now.takeIf(::isDisplayableSelectionName)

        val directSelectionDelay = mainGroup.proxies.firstOrNull { it.name == mainGroup.now }?.delay

        return HomeSelectedServerState(
            groupName = mainGroup.name.takeIf(::isDisplayableSelectionName),
            name = displayName ?: MLang.Home.Profile.Direct,
            delay =
                normalizeDisplayDelay(directSelectionDelay)
                    ?: normalizeDisplayDelay(resolvedProxy?.delay),
        )
    }

    private fun normalizeDisplayDelay(delay: Int?): Int? =
        when {
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

private object HomeProfileResolver {
    fun resolveDisplayProfile(runningProfile: Profile?, activeProfile: Profile?): Profile? {
        return runningProfile ?: activeProfile
    }
}

class HomeViewModel(
    private val proxyFacade: ProxyFacade,
    private val networkInfoService: NetworkInfoService,
    private val proxyChainResolver: ProxyChainResolver,
    private val proxyDisplaySettingsStore: ProxyDisplaySettingsStore,
    private val networkSettingsStorage: NetworkSettingsStorage,
    private val appSettings: AppSettingsStorage,
    private val vpnPermissionCoordinator: VpnPermissionCoordinator,
    private val runtimeFailurePresenter: RuntimeFailurePresenter,
    private val refreshHomeEntryDataUseCase: RefreshHomeEntryDataUseCase,
    private val reloadHomeProfileUseCase: ReloadHomeProfileUseCase,
    private val startHomeProxyUseCase: StartHomeProxyUseCase,
    private val stopHomeProxyUseCase: StopHomeProxyUseCase,
) : ViewModel() {

    private val profilesStateMutable = MutableStateFlow(HomeProfilesState())
    val profilesState: StateFlow<HomeProfilesState> = profilesStateMutable.asStateFlow()

    // Privacy: external IP is only ever queried when the user taps the button.
    // This state flow publishes the result of the most recent manual query.
    private val externalIpQueryInFlight = MutableStateFlow(false)
    val isExternalIpQuerying: StateFlow<Boolean> = externalIpQueryInFlight.asStateFlow()

    private val externalIpCache = MutableStateFlow<IpInfo?>(null)

    val isExternalIpLookupEnabled: StateFlow<Boolean> =
        appSettings.externalIpLookupUrl.state
            .map { it.isNotBlank() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val chromeStateMutable = MutableStateFlow(HomeChromeState())
    val chromeState: StateFlow<HomeChromeState> = chromeStateMutable.asStateFlow()

    val runtimeSnapshot = proxyFacade.runtimeSnapshot
    val currentProfile = proxyFacade.currentProfile
    val trafficNow = proxyFacade.trafficNow
    val proxyGroups = proxyFacade.proxyGroups
    private val confirmedCurrentProfile: StateFlow<Profile?> =
        combine(runtimeSnapshot, currentProfile) { snapshot, profile ->
                if (RuntimeStateMapper.isActuallyRunning(snapshot) && snapshot.profileReady) {
                    profile
                } else {
                    null
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isRunning: StateFlow<Boolean> =
        runtimeSnapshot
            .map(RuntimeStateMapper::isActuallyRunning)
            .distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                RuntimeStateMapper.isActuallyRunning(runtimeSnapshot.value),
            )

    private val currentTunnelMode: StateFlow<TunnelState.Mode> =
        proxyDisplaySettingsStore.proxyMode.state.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            TunnelState.Mode.Rule,
        )

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
                        val names =
                            runCatching {
                                    proxyFacade
                                        .queryProxyGroupNames(excludeNotSelectable = true)
                                        .toSet()
                                }
                                .getOrDefault(emptySet())
                        emit(names)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val selectedServer: StateFlow<HomeSelectedServerState?> =
        combine(runtimeSnapshot, proxyGroups, currentTunnelMode, visibleProxyGroupNames) {
                snapshot,
                groups,
                tunnelMode,
                visibleGroupNames ->
                if (!RuntimeStateMapper.isActuallyRunning(snapshot) || groups.isEmpty()) {
                    return@combine null
                }

                when (tunnelMode) {
                    TunnelState.Mode.Direct ->
                        HomeSelectedServerState(
                            groupName = null,
                            name = MLang.Home.Profile.Direct,
                            delay = null,
                        )

                    TunnelState.Mode.Global -> {
                        val mainGroup =
                            HomeProxySelectionResolver.resolveGlobalDisplayGroup(groups)
                                ?: HomeProxySelectionResolver.resolveFirstStrategyGroup(
                                    groups,
                                    visibleGroupNames,
                                )
                                ?: return@combine null
                        HomeProxySelectionResolver.buildSelectedServerState(
                            mainGroup,
                            groups,
                            proxyChainResolver::resolveEndNode,
                        )
                    }

                    TunnelState.Mode.Rule,
                    TunnelState.Mode.Script -> {
                        val mainGroup =
                            HomeProxySelectionResolver.resolveFirstStrategyGroup(
                                groups,
                                visibleGroupNames,
                            ) ?: return@combine null
                        HomeProxySelectionResolver.buildSelectedServerState(
                            mainGroup,
                            groups,
                            proxyChainResolver::resolveEndNode,
                        )
                    }
                }
            }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val speedHistory: StateFlow<SpeedHistoryBuffer> =
        combine(runtimeSnapshot, trafficNow) { snapshot, latestTraffic ->
                HomeTrafficSample(
                    phase = snapshot.phase,
                    trafficReady = snapshot.trafficReady,
                    latestTraffic = latestTraffic,
                )
            }
            .scan(SpeedHistoryBuffer.create(24)) { previous, sample ->
                if (sample.phase == RuntimePhase.Idle || sample.phase == RuntimePhase.Failed) {
                    HomeSpeedSampler.reset(previous)
                } else {
                    HomeSpeedSampler.appendSample(
                        state = previous,
                        sample =
                            HomeSpeedSampler.sampleTraffic(
                                phase = sample.phase,
                                trafficReady = sample.trafficReady,
                                latestTraffic = sample.latestTraffic,
                                previousSample = HomeSpeedSampler.latestSample(previous),
                            ),
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                SpeedHistoryBuffer.create(24),
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val ipMonitoringState: StateFlow<IpMonitoringState> =
        isRunning
            .flatMapLatest { running ->
                if (running) {
                    networkInfoService.startIpMonitoring(isRunning, externalIpCache)
                } else {
                    flowOf(IpMonitoringState.Loading)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                IpMonitoringState.Loading,
            )

    /**
     * Trigger a one-shot external IP lookup against the user-configured URL.
     *
     * Privacy: this is the **only** code path that ever calls
     * `NetworkInfoService.queryExternalIp()`. There is no auto-poll, no 10 s
     * tick, and the call only happens when the user taps the "查询" button.
     */
    fun queryExternalIp() {
        viewModelScope.launch {
            // When VPN is on, wait for transport to be fully ready so the
            // request actually goes through the proxy tunnel, not the raw
            // underlying network (which would return the real IP).
            val snapshot = runtimeSnapshot.value
            if (snapshot.running && !snapshot.transportReady) return@launch
            externalIpQueryInFlight.value = true
            try {
                val info = networkInfoService.queryExternalIp()
                externalIpCache.value = info
            } finally {
                externalIpQueryInFlight.value = false
            }
        }
    }

    val hasEnabledProfile: StateFlow<Boolean> =
        profilesState
            .map { state -> state.profiles.any { it.active } }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val screenState: StateFlow<HomeScreenState> =
        combine(
                combine(chromeState, profilesState) { chrome, profiles -> chrome to profiles },
                combine(confirmedCurrentProfile, selectedServer) { currentProfile, selectedServer ->
                    currentProfile to selectedServer
                },
                combine(
                    ipMonitoringState,
                    speedHistory,
                    trafficNow,
                    isExternalIpLookupEnabled,
                    isExternalIpQuerying,
                ) { ipState, sh, tn, externalIpEnabled, externalIpQuerying ->
                    RuntimeUiSnapshot(
                        ipState = ipState,
                        speedHistory = sh,
                        trafficNow = tn,
                        externalIpEnabled = externalIpEnabled,
                        externalIpQuerying = externalIpQuerying,
                    )
                },
            ) { chromeAndProfiles, profileAndServer, runtimeUi ->
                val (chrome, profiles) = chromeAndProfiles
                val (runningProfile, selectedServer) = profileAndServer
                val displayProfile =
                    HomeProfileResolver.resolveDisplayProfile(
                        runningProfile = runningProfile,
                        activeProfile = profiles.recommendedProfile,
                    )
                HomeScreenState(
                    ui = chrome.ui,
                    runtimeVisualState = chrome.runtimeVisualState,
                    displayRunning = chrome.displayRunning,
                    isToggling = chrome.isToggling,
                    proxyMode = chrome.proxyMode,
                    profiles = profiles.profiles,
                    recommendedProfile = profiles.recommendedProfile,
                    profilesLoaded = profiles.profilesLoaded,
                    hasEnabledProfile = profiles.profiles.any { it.active },
                    currentProfile = displayProfile,
                    selectedServer = selectedServer,
                    ipMonitoringState = runtimeUi.ipState,
                    isExternalIpLookupEnabled = runtimeUi.externalIpEnabled,
                    isExternalIpQuerying = runtimeUi.externalIpQuerying,
                    speedHistory = runtimeUi.speedHistory,
                    trafficNow = runtimeUi.trafficNow,
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeScreenState())

    init {
        refreshProfiles()
        refreshHomeEntryData()
        syncDisplayState()
        syncProxyModeState()
        observeProfileChanges()
    }

    fun setScreenActive(active: Boolean) {
        if (!active) return
        refreshProxyMode()
        refreshHomeEntryData()
    }

    private fun refreshProfiles() {
        viewModelScope.launch {
            try {
                profilesStateMutable.value = refreshHomeEntryDataUseCase.refreshProfiles()
            } catch (e: Exception) {
                Timber.e(e, "Failed to refresh profiles")
                profilesStateMutable.update { current -> current.copy(profilesLoaded = true) }
            }
        }
    }

    private fun refreshHomeEntryData() {
        refreshProfiles()
        viewModelScope.launch {
            runCatching { refreshHomeEntryDataUseCase.refreshRuntimePreview() }
                .onFailure { error -> Timber.d(error, "Skipped home entry preview refresh") }
        }
    }

    private fun observeProfileChanges() {
        viewModelScope.launch {
            currentProfile
                .map { profile -> profile?.uuid to profile?.updatedAt }
                .distinctUntilChanged()
                .drop(1)
                .collect { refreshProfiles() }
        }
    }

    private fun syncDisplayState() {
        viewModelScope.launch {
            runtimeSnapshot.collect { snapshot ->
                val runningOrStarting = RuntimeStateMapper.isRunningOrStarting(snapshot)
                val isStarting = snapshot.phase == RuntimePhase.Starting
                val loadingProgress = if (isStarting) MLang.Home.Message.Preparing else null
                chromeStateMutable.update { current ->
                    val nextUi =
                        if (
                            current.ui.isStartingProxy == isStarting &&
                                current.ui.loadingProgress == loadingProgress
                        ) {
                            current.ui
                        } else {
                            current.ui.copy(
                                isStartingProxy = isStarting,
                                loadingProgress = loadingProgress,
                            )
                        }

                    when (snapshot.phase) {
                        RuntimePhase.Starting ->
                            current.copy(
                                ui = nextUi,
                                runtimeVisualState = HomeRuntimeVisualState.Starting,
                                displayRunning = true,
                            )

                        RuntimePhase.Stopping ->
                            current.copy(
                                ui = nextUi,
                                runtimeVisualState = HomeRuntimeVisualState.Stopping,
                                displayRunning = false,
                            )

                        else ->
                            current.copy(
                                ui = nextUi,
                                runtimeVisualState =
                                    if (snapshot.phase == RuntimePhase.Running) {
                                        HomeRuntimeVisualState.Running
                                    } else {
                                        HomeRuntimeVisualState.Idle
                                    },
                                displayRunning = runningOrStarting,
                                isToggling = false,
                            )
                    }
                }
            }
        }
    }

    private fun syncProxyModeState() {
        viewModelScope.launch {
            runtimeSnapshot
                .map { snapshot ->
                    RuntimeStateMapper.resolveDisplayMode(
                        snapshot,
                        networkSettingsStorage.proxyMode.value,
                    )
                }
                .distinctUntilChanged()
                .collect(::setDisplayProxyMode)
        }
    }

    fun refreshProxyMode() {
        setDisplayProxyMode(
            RuntimeStateMapper.resolveDisplayMode(
                runtimeSnapshot.value,
                networkSettingsStorage.proxyMode.value,
            )
        )
    }

    suspend fun reloadProfile() {
        try {
            setLoading(true)

            when (val outcome = reloadHomeProfileUseCase()) {
                is RuntimeActionOutcome.PermissionRequired -> {
                    vpnPermissionCoordinator.requestPermission(outcome.intent) {
                        viewModelScope.launch { reloadProfile() }
                    }
                    return
                }

                RuntimeActionOutcome.FailureHandled -> return

                is RuntimeActionOutcome.Success -> {
                    if (outcome.value.runtimeRunning) {
                        reconcileRuntimeState(expectRunning = true)
                    }
                }
            }
            showMessage(MLang.Home.Message.ConfigSwitched)
        } catch (e: Exception) {
            Timber.e(e, "Failed to reload profile")
            if (e !is VpnPermissionRequired) {
                GlobalDialogPresenter.showError(
                    MLang.Home.Message.ConfigSwitchFailed.format(
                        e.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)
                    )
                )
            }
        } finally {
            setLoading(false)
        }
    }

    fun isCurrentProfile(profileId: UUID): Boolean {
        return currentProfile.value?.uuid == profileId
    }

    fun startProxy(profileId: String, mode: ProxyMode? = null) {
        if (chromeState.value.isToggling) return

        viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            updateChromeForStartRequest()
            val proxyMode = startHomeProxyUseCase.resolveMode(mode)

            try {
                setDisplayProxyMode(proxyMode)

                when (
                    val outcome = startHomeProxyUseCase(profileId = profileId, mode = proxyMode)
                ) {
                    is RuntimeActionOutcome.PermissionRequired -> {
                        chromeStateMutable.update { current ->
                            current.copy(
                                isToggling = false,
                                ui =
                                    current.ui.copy(isStartingProxy = false, loadingProgress = null),
                            )
                        }
                        vpnPermissionCoordinator.requestPermission(outcome.intent) {
                            startProxy(profileId = profileId, mode = proxyMode)
                        }
                        Timber.i("VPN permission required")
                        return@launch
                    }

                    RuntimeActionOutcome.FailureHandled -> {
                        chromeStateMutable.update { current ->
                            current.copy(
                                isToggling = false,
                                ui =
                                    current.ui.copy(isStartingProxy = false, loadingProgress = null),
                            )
                        }
                        return@launch
                    }

                    is RuntimeActionOutcome.Success -> {
                        reconcileRuntimeState(expectRunning = true)
                    }
                }

                Timber.i(
                    "Home startProxy completed in ${System.currentTimeMillis() - startedAt}ms, mode=$proxyMode"
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                chromeStateMutable.update { current ->
                    current.copy(
                        isToggling = false,
                        ui = current.ui.copy(isStartingProxy = false, loadingProgress = null),
                    )
                }
                Timber.e(e, "Failed to start proxy")
                if (e !is VpnPermissionRequired) {
                    val failureReason = e.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)
                    runtimeFailurePresenter.showStartFailure(
                        reason = failureReason,
                        targetMode = proxyMode,
                    )
                }
            }
        }
    }

    suspend fun stopProxy() {
        if (chromeState.value.isToggling) return

        chromeStateMutable.update { current -> current.copy(isToggling = true) }
        setLoading(true)

        try {
            val outcome = stopHomeProxyUseCase()
            if (outcome !is RuntimeActionOutcome.Success) {
                chromeStateMutable.update { current -> current.copy(isToggling = false) }
                setLoading(false)
                return
            }
            reconcileRuntimeState(expectRunning = false)
            chromeStateMutable.update { current -> current.copy(isToggling = false) }
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            chromeStateMutable.update { current -> current.copy(isToggling = false) }
            Timber.e(e, "Failed to stop proxy")
            if (e !is VpnPermissionRequired) {
                GlobalDialogPresenter.showError(
                    MLang.Home.Message.StopFailed.format(
                        e.runtimeGatewayMessage(MLang.ProfilesVM.Error.Unknown)
                    )
                )
            }
        }

        setLoading(false)
    }

    private suspend fun reconcileRuntimeState(expectRunning: Boolean) {
        val converged =
            withTimeoutOrNull(7_000L) {
                runtimeSnapshot
                    .map { snapshot ->
                        if (expectRunning) {
                            snapshot.phase == RuntimePhase.Running ||
                                snapshot.phase == RuntimePhase.Failed
                        } else {
                            snapshot.phase == RuntimePhase.Idle ||
                                snapshot.phase == RuntimePhase.Failed
                        }
                    }
                    .first { it }
            } != null

        if (!converged) {
            runCatching {
                    refreshHomeEntryData()
                    refreshProxyMode()
                }
                .onFailure { error ->
                    Timber.d(error, "Home runtime reconciliation refresh skipped")
                }
        }

        val snapshot = runtimeSnapshot.value
        val isStarting = snapshot.phase == RuntimePhase.Starting
        chromeStateMutable.update { current ->
            current.copy(
                isToggling = false,
                ui =
                    current.ui.copy(
                        isStartingProxy = isStarting,
                        loadingProgress = if (isStarting) MLang.Home.Message.Preparing else null,
                    ),
            )
        }
    }

    private fun updateChromeForStartRequest() {
        chromeStateMutable.update { current ->
            current.copy(
                isToggling = true,
                ui =
                    current.ui.copy(
                        isStartingProxy = true,
                        loadingProgress = MLang.Home.Message.Preparing,
                    ),
            )
        }
    }

    private fun setDisplayProxyMode(mode: ProxyMode) {
        chromeStateMutable.update { current ->
            if (current.proxyMode == mode) current else current.copy(proxyMode = mode)
        }
    }

    private fun setLoading(loading: Boolean) {
        chromeStateMutable.update { current ->
            if (current.ui.isLoading == loading) {
                current
            } else {
                current.copy(ui = current.ui.copy(isLoading = loading))
            }
        }
    }

    private fun showMessage(message: String) {
        chromeStateMutable.update { current ->
            if (current.ui.message == message) {
                current
            } else {
                current.copy(ui = current.ui.copy(message = message, structuredError = null))
            }
        }
    }

    private fun showError(error: String) {
        chromeStateMutable.update { current ->
            if (current.ui.error == error) {
                current
            } else {
                current.copy(
                    ui =
                        current.ui.copy(
                            error = error,
                            structuredError =
                                StructuredError.runtime(
                                    phase =
                                        when (current.runtimeVisualState) {
                                            HomeRuntimeVisualState.Idle -> ErrorPhase.Init
                                            HomeRuntimeVisualState.Starting -> ErrorPhase.Preparing
                                            HomeRuntimeVisualState.Running -> ErrorPhase.Running
                                            HomeRuntimeVisualState.Stopping -> ErrorPhase.Stopping
                                        },
                                    userVisibleMessage = error,
                                    rawCause = error,
                                    impact =
                                        when (current.runtimeVisualState) {
                                            HomeRuntimeVisualState.Running -> ErrorImpact.Degraded
                                            HomeRuntimeVisualState.Starting ->
                                                ErrorImpact.FeatureUnavailable
                                            HomeRuntimeVisualState.Stopping ->
                                                ErrorImpact.FeatureUnavailable
                                            HomeRuntimeVisualState.Idle ->
                                                ErrorImpact.FeatureUnavailable
                                        },
                                    retryability = ErrorRetryability.RetryableAfterAction,
                                ),
                        )
                )
            }
        }
    }

    fun consumeMessage() {
        chromeStateMutable.update { current ->
            if (current.ui.message == null) {
                current
            } else {
                current.copy(ui = current.ui.copy(message = null))
            }
        }
    }

    fun consumeError() {
        chromeStateMutable.update { current ->
            if (current.ui.error == null) {
                current
            } else {
                current.copy(ui = current.ui.copy(error = null))
            }
        }
    }
}

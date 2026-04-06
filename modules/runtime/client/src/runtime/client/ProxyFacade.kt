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



package com.github.yumelira.yumebox.runtime.client

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import com.github.yumelira.yumebox.core.StoreIds
import com.github.yumelira.yumebox.core.controller.ControllerError
import com.github.yumelira.yumebox.core.controller.MihomoControllerEndpoint
import com.github.yumelira.yumebox.core.model.ProxyGroup
import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.core.model.ProxySort
import com.github.yumelira.yumebox.core.model.Traffic
import com.github.yumelira.yumebox.core.model.TunnelState
import com.github.yumelira.yumebox.core.model.UiConfiguration
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.store.MMKVProvider
import com.github.yumelira.yumebox.data.store.NetworkSettingsStorage
import com.github.yumelira.yumebox.domain.model.ProxyGroupInfo
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import com.github.yumelira.yumebox.remote.VpnPermissionRequired
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.root.RootTunController
import com.github.yumelira.yumebox.service.ClashService
import com.github.yumelira.yumebox.service.StatusProvider
import com.github.yumelira.yumebox.service.TunService
import com.github.yumelira.yumebox.service.common.constants.Intents
import com.github.yumelira.yumebox.service.common.util.appContextOrSelf
import com.github.yumelira.yumebox.service.root.RootTunState
import com.github.yumelira.yumebox.service.root.RootTunStateStore
import com.github.yumelira.yumebox.service.root.RootTunStatus
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.github.yumelira.yumebox.service.runtime.session.RuntimeServiceLauncher
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

private fun <T> MutableStateFlow<T>.setIfChanged(newValue: T): Boolean {
    if (value == newValue) return false
    value = newValue
    return true
}

internal data class ProxyGroupMetadata(
    val hidden: Boolean = false,
    val icon: String? = null,
)

internal class MihomoControllerClient(
    private val json: Json,
) {
    fun fetchProxyGroupMetadata(configuration: UiConfiguration): Map<String, ProxyGroupMetadata> {
        return parseProxyGroupMetadata(requestJson(configuration, "/proxies"))
    }

    private fun requestJson(
        configuration: UiConfiguration,
        path: String,
    ): String {
        val controllerUrl = resolveControllerUrl(configuration)
            ?: error("Controller API unavailable")
        requireLoopbackController(controllerUrl)
        val normalizedPath = if (path.startsWith('/')) path else "/$path"
        val connection = (URL("$controllerUrl$normalizedPath").openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
            setRequestProperty("Accept", "application/json")
            configuration.secret
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.let { setRequestProperty("Authorization", MihomoControllerEndpoint.bearerAuthorization(it)) }
        }

        return try {
            val body = (if (connection.responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            })?.bufferedReader()?.use { it.readText() }.orEmpty()

            if (connection.responseCode !in 200..299) {
                when (connection.responseCode) {
                    401 -> throw ControllerError.Unauthorized()
                    else -> throw ControllerError.Unknown("Controller API ${connection.responseCode}: $body")
                }
            }

            body
        } catch (e: ControllerError) {
            throw e
        } catch (e: java.net.ConnectException) {
            throw ControllerError.Unavailable(e)
        } finally {
            connection.disconnect()
        }
    }

    private fun resolveControllerUrl(configuration: UiConfiguration): String? {
        return MihomoControllerEndpoint.resolveControllerUrl(configuration)
    }

    /** Reject non-loopback controller URLs to prevent SSRF via a malicious config file. */
    private fun requireLoopbackController(rawUrl: String) {
        val host = runCatching { URL(rawUrl).host }.getOrElse { "" }
        val normalizedHost = host.trimStart('[').trimEnd(']').lowercase()
        val isLoopback = normalizedHost == "localhost" ||
            normalizedHost == "127.0.0.1" ||
            normalizedHost == "::1"
        require(isLoopback) { "Controller must resolve to a loopback address (got: $normalizedHost)" }
    }

    private fun parseProxyGroupMetadata(body: String): Map<String, ProxyGroupMetadata> {
        val root = json.parseToJsonElement(body).jsonObject
        val proxies = root["proxies"]?.jsonObject ?: return emptyMap()
        return proxies.mapNotNull { (name, payload) ->
            val metadata = payload.toProxyGroupMetadata() ?: return@mapNotNull null
            name to metadata
        }.toMap()
    }

    private fun JsonElement.toProxyGroupMetadata(): ProxyGroupMetadata? {
        val obj = this as? JsonObject ?: return null
        val type = obj["type"]?.jsonPrimitive?.content ?: return null
        val isGroup = runCatching { Proxy.Type.valueOf(type).group }.getOrDefault(false)
        if (!isGroup) return null

        val hiddenValue = obj["hidden"]?.jsonPrimitive?.content
        val iconValue = obj["icon"]?.jsonPrimitive?.content
        return ProxyGroupMetadata(
            hidden = hiddenValue?.toBooleanStrictOrNull() ?: false,
            icon = iconValue?.takeIf { value -> value.isNotBlank() },
        )
    }
}

class ProxyFacade(
    private val context: Context,
    private val networkSettingsStorage: NetworkSettingsStorage,
) {
    private data class PreviewCacheKey(
        val profileId: UUID,
        val profileUpdatedAt: Long,
        val excludeNotSelectable: Boolean,
        val overrideSignature: String,
    )

    private data class PreviewCacheEntry(
        val key: PreviewCacheKey,
        val groups: List<ProxyGroupInfo>,
    )

    private val appContext: Context = context.appContextOrSelf
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }
    private val controllerClient = MihomoControllerClient(json)
    private val rootTunStateStore by lazy { RootTunStateStore(appContext) }
    private val _rootTunStatus = MutableStateFlow(rootTunStateStore.snapshot())
    val rootTunStatus: StateFlow<RootTunStatus> = _rootTunStatus.asStateFlow()
    private val _runtimeSnapshot = MutableStateFlow(
        RuntimeStateMapper.idleSnapshot(networkSettingsStorage.proxyMode.value),
    )
    val runtimeSnapshot: StateFlow<RuntimeSnapshot> = _runtimeSnapshot.asStateFlow()

    private val actionServiceRecreated: String get() = Intents.actionServiceRecreated(appContext.packageName)
    private val actionClashStarted: String get() = Intents.actionClashStarted(appContext.packageName)
    private val actionClashStopped: String get() = Intents.actionClashStopped(appContext.packageName)
    private val actionClashRequestStop: String get() = Intents.actionClashRequestStop(appContext.packageName)
    private val actionProfileChanged: String get() = Intents.actionProfileChanged(appContext.packageName)
    private val actionProfileLoaded: String get() = Intents.actionProfileLoaded(appContext.packageName)
    private val actionOverrideChanged: String get() = Intents.actionOverrideChanged(appContext.packageName)
    private val actionRootRuntimeFailed: String get() = Intents.actionRootRuntimeFailed(appContext.packageName)

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _proxyGroups = MutableStateFlow<List<ProxyGroupInfo>>(emptyList())
    val proxyGroups: StateFlow<List<ProxyGroupInfo>> = _proxyGroups.asStateFlow()

    private val _currentProfile = MutableStateFlow<Profile?>(null)
    val currentProfile: StateFlow<Profile?> = _currentProfile.asStateFlow()

    private val _trafficNow = MutableStateFlow(0L)
    val trafficNow: StateFlow<Traffic> = _trafficNow.asStateFlow()

    private val _trafficTotal = MutableStateFlow(0L)
    val trafficTotal: StateFlow<Traffic> = _trafficTotal.asStateFlow()

    private var trafficPollingJob: Job? = null
    private var previewCache: PreviewCacheEntry? = null
    private var previewWarmupJob: Job? = null
    private val refreshProxyGroupsMutex = Mutex()
    private val operationMutex = Mutex()
    private var generationCounter = 0L

    private val serviceEventsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action ?: return) {
                actionClashStarted -> {
                    scope.launch { handleRuntimeStarted() }
                }

                actionClashStopped -> {
                    scope.launch { handleRuntimeStopped(intent.getStringExtra(Intents.EXTRA_STOP_REASON)) }
                }

                actionProfileLoaded,
                actionProfileChanged,
                actionOverrideChanged,
                actionServiceRecreated -> {
                    scope.launch {
                        if (_runtimeSnapshot.value.phase == RuntimePhase.Running) {
                            refreshAllSafely()
                        } else {
                            refreshPreviewStateSafely()
                        }
                    }
                }

                actionRootRuntimeFailed -> {
                    val code = intent.getStringExtra(Intents.EXTRA_ERROR_CODE)
                    val error = intent.getStringExtra(Intents.EXTRA_ERROR_MESSAGE)
                        ?: intent.getStringExtra("error")
                    val composed = listOfNotNull(
                        code?.takeIf { it.isNotBlank() },
                        error?.takeIf { it.isNotBlank() },
                    ).joinToString(separator = ": ")
                        .ifBlank { "ROOT_RUNTIME_QUERY_FAILED: root runtime failed" }
                    Timber.w("Root runtime failed: $composed")
                    scope.launch { handleRuntimeFailure(composed, code) }
                }
            }
        }
    }

    init {
        registerServiceEventReceiver()
        initializeRuntimeSnapshot()
    }

    fun warmUpProxyGroups() {
        if (previewWarmupJob?.isActive == true) return
        previewWarmupJob = scope.launch {
            runCatching { refreshProxyGroups() }
                .onFailure { error -> Timber.d(error, "Warm up proxy groups skipped") }
        }
    }

    suspend fun startProxy(mode: ProxyMode = networkSettingsStorage.proxyMode.value) {
        Timber.i("Start proxy: mode=$mode")

        if (mode == ProxyMode.Tun && StatusProvider.isTunStarting() && !StatusProvider.isRuntimeActive(ProxyMode.Tun)) {
            // Recover from stale startup marker left by interrupted fast toggles.
            StatusProvider.clearTunStarting()
        }

        ServiceClient.connect(appContext)

        val activeProfile = ServiceClient.profile().queryActive()
        check(activeProfile != null) { "No profile selected" }

        if (mode == ProxyMode.Tun) {
            val vpnIntent = VpnService.prepare(context)
            if (vpnIntent != null) {
                throw VpnPermissionRequired(vpnIntent)
            }
        }

        operationMutex.withLock {
            val targetOwner = ownerForMode(mode)
            val currentOwner = detectActiveOwner().takeIf { it != RuntimeOwner.None } ?: _runtimeSnapshot.value.owner
            if (currentOwner != RuntimeOwner.None) {
                stopProxyInternal(targetMode = mode)
            }

            val generation = nextGeneration()

            clearRuntimeState(resetGroups = false)
            _currentProfile.setIfChanged(activeProfile)
            publishRuntimeSnapshot(
                RuntimeSnapshot(
                    owner = targetOwner,
                    phase = RuntimePhase.Starting,
                    targetMode = mode,
                    profileReady = true,
                    profileUuid = activeProfile.uuid.toString(),
                    profileName = activeProfile.name,
                    startedAt = System.currentTimeMillis(),
                    generation = generation,
                ),
            )

            when (targetOwner) {
                RuntimeOwner.RootTun -> {
                    startRootTun().getOrThrow()
                    handleRuntimeStarted(forceOwner = RuntimeOwner.RootTun)
                }
                RuntimeOwner.LocalTun,
                RuntimeOwner.LocalHttp -> startLocalRuntime(mode)
                RuntimeOwner.None -> Unit
            }
        }
    }

    suspend fun stopProxy(mode: ProxyMode? = null) {
        val targetMode = mode ?: networkSettingsStorage.proxyMode.value

        operationMutex.withLock {
            stopProxyInternal(targetMode)
        }
    }

    suspend fun queryProxyGroupNames(excludeNotSelectable: Boolean = false): List<String> {
        connectCurrentBackend()
        return ServiceClient.clash().queryProxyGroupNames(excludeNotSelectable)
    }

    suspend fun queryProfileProxyGroups(excludeNotSelectable: Boolean = false): List<ProxyGroup> {
        connectCurrentBackend()
        return ServiceClient.clash().queryProfileProxyGroups(excludeNotSelectable)
    }

    suspend fun queryProxyGroup(name: String, sort: ProxySort = ProxySort.Default): ProxyGroup {
        connectCurrentBackend()
        return ServiceClient.clash().queryProxyGroup(name, sort)
    }

    suspend fun selectProxy(group: String, proxyName: String): Boolean {
        Timber.d("Select proxy: group=$group proxy=$proxyName")
        connectCurrentBackend()
        val ok = ServiceClient.clash().patchSelector(group, proxyName)
        if (ok) {
            delay(200.milliseconds)
            refreshProxyGroups()
        }
        return ok
    }

    suspend fun healthCheck(group: String, refreshAfter: Boolean = true) {
        connectCurrentBackend()
        Timber.d("Health check request: group=%s refreshAfter=%s", group, refreshAfter)
        ServiceClient.clash().healthCheck(group)
        Timber.d("Health check dispatched: group=%s", group)
        if (refreshAfter) {
            repeat(4) {
                delay(1000.milliseconds)
                refreshProxyGroups()
            }
        } else {
            scope.launch {
                delay(300.milliseconds)
                repeat(3) {
                    runCatching { refreshProxyGroups() }
                    delay(500.milliseconds)
                }
            }
        }
    }

    suspend fun healthCheckProxy(proxyName: String): Int {
        connectCurrentBackend()
        Timber.d("Health check proxy request: proxy=%s", proxyName)
        val delay = ServiceClient.clash().healthCheckProxy(proxyName)
        Timber.d("Health check proxy done: proxy=%s delay=%s", proxyName, delay)
        refreshProxyGroups()
        return delay
    }

    suspend fun queryTunnelState(): TunnelState {
        connectCurrentBackend()
        return ServiceClient.clash().queryTunnelState()
    }



    suspend fun queryTrafficTotal(): Long {
        if (!_runtimeSnapshot.value.running) {
            _trafficTotal.setIfChanged(0L)
            return 0L
        }
        connectCurrentBackend()
        val traffic = ServiceClient.clash().queryTrafficTotal()
        _trafficTotal.setIfChanged(traffic)
        updateTrafficReady()
        return traffic
    }

    suspend fun queryTrafficNow(): Long {
        if (!_runtimeSnapshot.value.running) {
            _trafficNow.setIfChanged(0L)
            return 0L
        }
        connectCurrentBackend()
        val traffic = ServiceClient.clash().queryTrafficNow()
        _trafficNow.setIfChanged(traffic)
        updateTrafficReady()
        return traffic
    }

    suspend fun reloadCurrentProfile(): Result<Unit> {
        return try {
            val profileManager = ServiceClient.profile()
            val currentProfile = profileManager.queryActive()
            if (currentProfile != null) {
                profileManager.setActive(currentProfile)
                _currentProfile.setIfChanged(currentProfile)
                delay(600.milliseconds)
                refreshAll()
            }
            Result.success(Unit)
        } catch (e: ControllerError) {
            Timber.w(e, "Failed to reload current profile")
            Result.failure(e)
        } catch (e: Exception) {
            Timber.e(e, "Failed to reload current profile")
            Result.failure(ControllerError.Unknown(e.runtimeGatewayMessage("reload failed"), e))
        }
    }

    fun updateServiceState(isRunning: Boolean) {
        _isRunning.setIfChanged(isRunning)
    }

    private fun normalizeProxyGroups(groups: List<ProxyGroup>): List<ProxyGroupInfo> {
        var normalized: MutableList<ProxyGroupInfo>? = null
        groups.forEachIndexed { index, group ->
            val normalizedGroup = if (group.now.isBlank()) group.copy(now = "-") else group
            if (normalized == null && normalizedGroup !== group) {
                normalized = ArrayList(groups.size)
                var head = 0
                while (head < index) {
                    normalized?.add(groups[head])
                    head += 1
                }
            }
            normalized?.add(normalizedGroup)
        }
        return normalized ?: groups
    }

    suspend fun refreshProxyGroups() {
        refreshProxyGroupsMutex.withLock {
            val snapshot = _runtimeSnapshot.value
            val groups = withContext(Dispatchers.IO) {
                try {
                    if (!snapshot.running) {
                        return@withContext queryPreviewProxyGroups()
                    }

                    if (snapshot.owner == RuntimeOwner.RootTun && !isRootSessionActive()) {
                        throw ControllerError.Unavailable()
                    }

                    connectCurrentBackend()
                    val groups = normalizeProxyGroups(
                        ServiceClient.clash().queryAllProxyGroups(excludeNotSelectable = false),
                    )
                    enrichProxyGroupsFromController(groups, ServiceClient.clash().queryConfiguration())
                } catch (e: ControllerError) {
                    Timber.w(e, "Failed to refresh proxy groups: ${e.message}")
                    null
                } catch (e: Exception) {
                    Timber.e(e, "Failed to refresh proxy groups: ${e.runtimeGatewayMessage("unknown")}")
                    null
                }
            }

            groups?.let {
                _proxyGroups.setIfChanged(it)
                updateGroupsReady(it.isNotEmpty())
                backfillPreviewCache(it)
            } ?: fallbackPreviewGroups(snapshot)?.let { cached ->
                _proxyGroups.setIfChanged(cached)
                updateGroupsReady(cached.isNotEmpty())
            }
        }
    }

    suspend fun refreshCurrentProfile() {
        when {
            _runtimeSnapshot.value.owner == RuntimeOwner.RootTun &&
                _runtimeSnapshot.value.phase == RuntimePhase.Running -> {
                val status = currentRootTunStatus()
                applyRootTunStatus(status)
                refreshRootCurrentProfile(status)
            }

            else -> {
                try {
                    val profile = ServiceClient.profile().queryActive()
                    _currentProfile.setIfChanged(profile)
                    updateProfileReady(profile)
                } catch (e: ControllerError) {
                    Timber.w(e, "Failed to refresh current profile: ${e.message}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to refresh current profile: ${e.runtimeGatewayMessage("unknown")}")
                }
            }
        }
    }

    suspend fun refreshAll() {
        refreshCurrentProfile()
        refreshProxyGroups()
        if (_runtimeSnapshot.value.phase == RuntimePhase.Running) {
            queryTrafficNow()
            queryTrafficTotal()
        } else {
            _trafficNow.setIfChanged(0L)
            _trafficTotal.setIfChanged(0L)
        }
    }

    private suspend fun startLocalRuntime(mode: ProxyMode) {
        when (mode) {
            ProxyMode.Tun,
            ProxyMode.Http -> RuntimeServiceLauncher.start(
                context = appContext,
                mode = mode,
                source = RuntimeServiceLauncher.SOURCE_UI,
            )
            ProxyMode.RootTun -> error("local runtime does not support RootTun")
        }
    }

    private suspend fun startRootTun(): Result<Unit> {
        return runCatching {
            val result = RootTunController.start(appContext)
            if (!result.success) {
                throw result.toException(
                    defaultCode = RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
                    defaultMessage = "RootTun start failed",
                )
            }
            applyRootTunStatus(rootTunStateStore.snapshot())
        }
    }

    private suspend fun stopRootTun(): Result<Unit> {
        return runCatching {
            val result = RootTunController.stop(appContext)
            if (!result.success) {
                throw result.toException(
                    defaultCode = RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED,
                    defaultMessage = "RootTun stop failed",
                )
            }
            applyRootTunStatus(rootTunStateStore.snapshot())
        }
    }

    private suspend fun triggerStop(owner: RuntimeOwner) {
        when (owner) {
            RuntimeOwner.RootTun -> {
                stopRootTun().getOrThrow()
            }

            RuntimeOwner.LocalTun,
            RuntimeOwner.LocalHttp -> {
                withContext(Dispatchers.IO) {
                    runCatching {
                        ServiceClient.connect(appContext)
                        ServiceClient.clash().requestStop()
                    }.onFailure {
                        appContext.sendBroadcast(Intent(actionClashRequestStop).setPackage(appContext.packageName))
                    }
                    context.stopService(Intent(context, TunService::class.java))
                    context.stopService(Intent(context, ClashService::class.java))
                    StatusProvider.clearTunStarting()
                }
            }

            RuntimeOwner.None -> Unit
        }
    }

    private suspend fun stopProxyInternal(targetMode: ProxyMode) {
        val owner = detectActiveOwner().takeIf { it != RuntimeOwner.None } ?: _runtimeSnapshot.value.owner
        val generation = nextGeneration()

        if (owner == RuntimeOwner.None) {
            transitionToIdle(
                configuredMode = targetMode,
                generation = generation,
                lastError = null,
            )
            return
        }

        publishRuntimeSnapshot(
            _runtimeSnapshot.value.copy(
                owner = owner,
                phase = RuntimePhase.Stopping,
                targetMode = targetMode,
                profileReady = false,
                groupsReady = false,
                trafficReady = false,
                lastError = null,
                generation = generation,
            ),
        )

        triggerStop(owner)

        transitionToIdle(
            configuredMode = targetMode,
            generation = generation,
            lastError = null,
        )
    }

    private fun startTrafficPolling() {
        if (trafficPollingJob?.isActive == true) return
        trafficPollingJob = scope.launch {
            var tick = 0
            while (true) {
                val snapshot = _runtimeSnapshot.value
                if (!snapshot.running) {
                    delay(1000L.milliseconds)
                    continue
                }

                try {
                    connectCurrentBackend()
                    queryTrafficNow()
                    if (tick % 5 == 0) {
                        queryTrafficTotal()
                    }
                } catch (e: ControllerError) {
                    Timber.d(e, "Traffic polling skipped: ${e.message}")
                } catch (e: Exception) {
                    Timber.d(e, "Traffic polling skipped")
                }
                tick++

                if (tick % 3 == 0 && shouldRefreshRuntimePayload()) {
                    refreshAllSafely()
                }

                delay(1000L.milliseconds)
            }
        }
    }

    private fun stopTrafficPolling() {
        trafficPollingJob?.cancel()
        trafficPollingJob = null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerServiceEventReceiver() {
        val filter = IntentFilter().apply {
            addAction(actionClashStarted)
            addAction(actionClashStopped)
            addAction(actionProfileChanged)
            addAction(actionProfileLoaded)
            addAction(actionOverrideChanged)
            addAction(actionServiceRecreated)
            addAction(actionRootRuntimeFailed)
        }
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appContext.registerReceiver(serviceEventsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                appContext.registerReceiver(serviceEventsReceiver, filter)
            }
        }.onFailure { error ->
            Timber.w(error, "Failed to register service event receiver")
        }
    }

    private fun initializeRuntimeSnapshot() {
        val configuredMode = networkSettingsStorage.proxyMode.value
        clearLegacyRuntimeCaches()
        val rootStatus = resolveInitialRootTunStatus()
        applyRootTunStatus(rootStatus)
        val owner = when {
            rootStatus.state.isActive || rootStatus.runtimeReady -> RuntimeOwner.RootTun
            isLocalSessionActive(ProxyMode.Tun) -> RuntimeOwner.LocalTun
            isLocalSessionActive(ProxyMode.Http) -> RuntimeOwner.LocalHttp
            else -> RuntimeOwner.None
        }

        if (owner == RuntimeOwner.None) {
            clearRuntimeState(resetGroups = false)
            publishRuntimeSnapshot(RuntimeStateMapper.idleSnapshot(configuredMode))
            scope.launch { refreshPreviewStateSafely() }
            return
        }

        publishRuntimeSnapshot(
            RuntimeSnapshot(
                owner = owner,
                phase = if (owner == RuntimeOwner.RootTun) rootPhase(rootStatus) else RuntimePhase.Running,
                targetMode = modeForOwner(owner),
                profileReady = owner == RuntimeOwner.RootTun && !rootStatus.profileUuid.isNullOrBlank(),
                profileUuid = rootStatus.profileUuid.takeIf { owner == RuntimeOwner.RootTun },
                profileName = rootStatus.profileName.takeIf { owner == RuntimeOwner.RootTun },
                lastError = if (owner == RuntimeOwner.RootTun) rootStatus.composedError() else null,
                startedAt = rootStatus.startedAt.takeIf { owner == RuntimeOwner.RootTun },
            ),
        )
        startTrafficPolling()
        scope.launch { refreshAllSafely() }
    }

    private fun detectActiveOwner(): RuntimeOwner {
        return when {
            isRootSessionActive() -> RuntimeOwner.RootTun
            isLocalSessionActive(ProxyMode.Tun) -> RuntimeOwner.LocalTun
            isLocalSessionActive(ProxyMode.Http) -> RuntimeOwner.LocalHttp
            else -> RuntimeOwner.None
        }
    }

    private fun resolveInitialRootTunStatus(): RootTunStatus {
        val persisted = rootTunStateStore.snapshot()
        if (!persisted.state.isActive && !persisted.runtimeReady) {
            return persisted
        }


        rootTunStateStore.markIdle("app restart cleanup")
        return rootTunStateStore.snapshot()
    }

    private fun isRootSessionActive(): Boolean {
        val status = _rootTunStatus.value
        return status.state.isActive || status.runtimeReady
    }

    private fun isLocalSessionActive(mode: ProxyMode?): Boolean {
        if (mode == null) return false
        return StatusProvider.isRuntimeActive(mode)
    }

    private suspend fun handleRuntimeStarted(forceOwner: RuntimeOwner? = null) {
        val currentSnapshot = _runtimeSnapshot.value
        val owner = forceOwner
            ?: currentSnapshot.owner.takeIf { it != RuntimeOwner.None }
            ?: detectActiveOwner()
        if (owner == RuntimeOwner.None) return

        publishRuntimeSnapshot(
            currentSnapshot.copy(
                owner = owner,
                phase = RuntimePhase.Running,
                targetMode = modeForOwner(owner),
                lastError = null,
            ),
        )
        startTrafficPolling()
        refreshAllSafely()
    }

    private suspend fun handleRuntimeStopped(reason: String?) {
        val configuredMode = networkSettingsStorage.proxyMode.value
        val generation = nextGeneration()

        if (!isRootSessionActive()) {
            val status = rootTunStateStore.snapshot()
            if (status.state.isActive) {
                rootTunStateStore.markIdle(
                    error = reason ?: status.lastError,
                    errorCode = status.lastErrorCode,
                )
            }
            applyRootTunStatus(rootTunStateStore.snapshot())
        }

        transitionToIdle(
            configuredMode = configuredMode,
            generation = generation,
            lastError = reason,
        )
    }

    private fun handleRuntimeFailure(error: String?, errorCodeRaw: String? = null) {
        val generation = nextGeneration()
        val errorCode = errorCodeRaw?.let { raw ->
            runCatching { com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode.valueOf(raw) }.getOrNull()
        }
        if (!isRootSessionActive()) {
            rootTunStateStore.markIdle(error = error, errorCode = errorCode)
            applyRootTunStatus(rootTunStateStore.snapshot())
        }
        val normalizedError = error?.takeIf { it.isNotBlank() }
            ?: errorCode?.let { "${it.name}: root runtime failed" }
            ?: "${RuntimeGatewayErrorCode.ROOT_RUNTIME_QUERY_FAILED.name}: root runtime failed"
        transitionToIdle(
            configuredMode = networkSettingsStorage.proxyMode.value,
            generation = generation,
            lastError = normalizedError,
        )
    }

    private fun transitionToIdle(
        configuredMode: ProxyMode,
        generation: Long,
        lastError: String?,
    ) {
        clearRuntimeState(resetGroups = false)
        publishRuntimeSnapshot(
            RuntimeStateMapper.idleSnapshot(
                configuredMode = configuredMode,
                generation = generation,
                lastError = lastError,
            ),
        )
        stopTrafficPolling()
        scope.launch { refreshPreviewStateSafely() }
    }

    private suspend fun refreshAllSafely() {
        if (_runtimeSnapshot.value.phase != RuntimePhase.Running) {
            return
        }
        runCatching { refreshAll() }
            .onFailure { error -> Timber.d(error, "Refresh runtime data skipped") }
    }

    private suspend fun refreshPreviewStateSafely() {
        runCatching {
            refreshCurrentProfile()
            refreshProxyGroups()
        }.onFailure { error ->
            Timber.d(error, "Refresh preview data skipped")
        }
    }

    private fun shouldRefreshRuntimePayload(): Boolean {
        val snapshot = _runtimeSnapshot.value
        return snapshot.phase == RuntimePhase.Running &&
            (!snapshot.profileReady || !snapshot.groupsReady || _proxyGroups.value.isEmpty() || _currentProfile.value == null)
    }

    private suspend fun currentRootTunStatus(): RootTunStatus {
        return runCatching { RootTunController.queryStatus(appContext) }
            .getOrElse { _rootTunStatus.value }
    }

    private fun clearLegacyRuntimeCaches() {
        StatusProvider.clearLegacyStateFiles()
        val rootStatus = rootTunStateStore.snapshot()
        if (!rootStatus.state.isActive && !rootStatus.runtimeReady) {
            runCatching {
                rootTunStateStore.clear()
            }
            applyRootTunStatus(rootTunStateStore.snapshot())
        } else {
            applyRootTunStatus(rootStatus)
        }
        if (!StatusProvider.serviceRunning) {
            runCatching {
                MMKV.mmkvWithID(StoreIds.RUNTIME_SNAPSHOT, MMKV.MULTI_PROCESS_MODE)?.clearAll()
            }
        }
    }

    private fun applyRootTunStatus(status: RootTunStatus) {
        _rootTunStatus.setIfChanged(status)
    }

    private fun publishRuntimeSnapshot(snapshot: RuntimeSnapshot) {
        val normalized = if (snapshot.running == snapshot.phase.running) {
            snapshot
        } else {
            snapshot.copy(running = snapshot.phase.running)
        }
        _runtimeSnapshot.setIfChanged(normalized)
        _isRunning.setIfChanged(normalized.running)
    }

    private fun nextGeneration(): Long {
        generationCounter += 1L
        return generationCounter
    }

    private fun ownerForMode(mode: ProxyMode): RuntimeOwner {
        return when (mode) {
            ProxyMode.Tun -> RuntimeOwner.LocalTun
            ProxyMode.Http -> RuntimeOwner.LocalHttp
            ProxyMode.RootTun -> RuntimeOwner.RootTun
        }
    }

    private fun modeForOwner(owner: RuntimeOwner): ProxyMode {
        return when (owner) {
            RuntimeOwner.LocalTun -> ProxyMode.Tun
            RuntimeOwner.LocalHttp -> ProxyMode.Http
            RuntimeOwner.RootTun -> ProxyMode.RootTun
            RuntimeOwner.None -> networkSettingsStorage.proxyMode.value
        }
    }

    private suspend fun connectCurrentBackend() {
        ServiceClient.connect(appContext)
    }

    private suspend fun refreshRootCurrentProfile(status: RootTunStatus) {
        runCatching {
            connectCurrentBackend()
            val profile = status.profileUuid
                ?.takeIf { it.isNotBlank() }
                ?.let { uuid -> ServiceClient.profile().queryByUUID(UUID.fromString(uuid)) }
                ?: ServiceClient.profile().queryActive()

            if (profile != null) {
                _currentProfile.setIfChanged(profile)
            }
            updateProfileReady(profile)
        }.onFailure { error ->
            Timber.d(error, "Failed to refresh root current profile")
        }
    }

    private suspend fun queryPreviewProxyGroups(): List<ProxyGroupInfo> {
        val cachedProfile = _currentProfile.value
        val activeProfile = cachedProfile ?: ServiceClient.profile().queryActive().also {
            _currentProfile.setIfChanged(it)
            updateProfileReady(it)
        }

        if (activeProfile == null) {
            return emptyList()
        }
        connectCurrentBackend()
        val groups = normalizeProxyGroups(
            ServiceClient.clash().queryProfileProxyGroups(excludeNotSelectable = false),
        )

        return groups
    }

    private fun backfillPreviewCache(groups: List<ProxyGroupInfo>) {
        val profile = _currentProfile.value ?: return
        val key = previewCacheKey(profile, excludeNotSelectable = false)
        val existing = previewCache
        if (existing != null && existing.key == key && existing.groups == groups) return
        previewCache = PreviewCacheEntry(key, groups)
    }

    private fun fallbackPreviewGroups(snapshot: RuntimeSnapshot): List<ProxyGroupInfo>? {
        if (snapshot.phase == RuntimePhase.Running) return null
        val profile = _currentProfile.value ?: return previewCache?.groups
        val key = previewCacheKey(profile, excludeNotSelectable = false)
        return previewCache?.takeIf { it.key == key }?.groups
    }

    private fun previewCacheKey(
        profile: Profile,
        excludeNotSelectable: Boolean,
    ): PreviewCacheKey {
        val overrideSignature = _runtimeSnapshot.value.effectiveFingerprint
            ?.takeIf { it.isNotBlank() }
            ?: _rootTunStatus.value.overrideFingerprint?.takeIf { it.isNotBlank() }
            ?: "profile-${profile.updatedAt}"
        return PreviewCacheKey(
            profileId = profile.uuid,
            profileUpdatedAt = profile.updatedAt,
            excludeNotSelectable = excludeNotSelectable,
            overrideSignature = overrideSignature,
        )
    }

    private fun enrichProxyGroupsFromController(
        groups: List<ProxyGroupInfo>,
        configuration: UiConfiguration,
    ): List<ProxyGroupInfo> {
        if (groups.isEmpty()) return groups
        val metadataByName = try {
            fetchProxyGroupMetadata(configuration)
        } catch (e: ControllerError) {
            Timber.d(e, "Failed to query controller proxies metadata: ${e.message}")
            return groups
        } catch (e: Exception) {
            Timber.d(e, "Failed to query controller proxies metadata")
            return groups
        }

        if (metadataByName.isEmpty()) return groups

        var enriched: MutableList<ProxyGroupInfo>? = null
        groups.forEachIndexed { index, group ->
            val metadata = metadataByName[group.name]
            val nextGroup = if (metadata == null) {
                group
            } else {
                val nextIcon = group.icon ?: metadata.icon
                if (group.hidden == metadata.hidden && group.icon == nextIcon) {
                    group
                } else {
                    group.copy(hidden = metadata.hidden, icon = nextIcon)
                }
            }
            if (enriched == null && nextGroup !== group) {
                enriched = ArrayList(groups.size)
                var head = 0
                while (head < index) {
                    enriched?.add(groups[head])
                    head += 1
                }
            }
            enriched?.add(nextGroup)
        }
        return enriched ?: groups
    }

    private fun fetchProxyGroupMetadata(configuration: UiConfiguration): Map<String, ProxyGroupMetadata> {
        return controllerClient.fetchProxyGroupMetadata(configuration)
    }

    private fun rootPhase(status: RootTunStatus): RuntimePhase {
        return when (status.state) {
            RootTunState.Idle -> RuntimePhase.Idle
            RootTunState.Starting -> RuntimePhase.Starting
            RootTunState.Running -> RuntimePhase.Running
            RootTunState.Stopping -> RuntimePhase.Stopping
            RootTunState.Failed -> RuntimePhase.Failed
        }
    }

    private fun clearRuntimeState(resetGroups: Boolean = true) {
        _currentProfile.setIfChanged(null)
        if (resetGroups) {
            _proxyGroups.setIfChanged(emptyList())
        }
        _trafficNow.setIfChanged(0L)
        _trafficTotal.setIfChanged(0L)
    }

    private fun updateProfileReady(profile: Profile?) {
        val snapshot = _runtimeSnapshot.value
        publishRuntimeSnapshot(
            snapshot.copy(
                profileReady = profile != null,
                profileUuid = profile?.uuid?.toString() ?: snapshot.profileUuid,
                profileName = profile?.name ?: snapshot.profileName,
            ),
        )
    }

    private fun updateGroupsReady(ready: Boolean) {
        publishRuntimeSnapshot(_runtimeSnapshot.value.copy(groupsReady = ready))
    }

    private fun updateTrafficReady() {
        if (!_runtimeSnapshot.value.trafficReady) {
            publishRuntimeSnapshot(_runtimeSnapshot.value.copy(trafficReady = true))
        }
    }
}

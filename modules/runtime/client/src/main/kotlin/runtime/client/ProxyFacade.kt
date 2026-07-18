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

package com.github.nomadboxlab.monadbox.runtime.client

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.core.controller.ControllerError
import com.github.nomadboxlab.monadbox.core.controller.MihomoControllerEndpoint
import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.core.model.ProxyGroup
import com.github.nomadboxlab.monadbox.core.model.ProxySort
import com.github.nomadboxlab.monadbox.core.model.Traffic
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.core.model.UiConfiguration
import com.github.nomadboxlab.monadbox.data.store.NetworkSettingsStorage
import com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo
import com.github.nomadboxlab.monadbox.domain.model.ProxyLatencyState
import com.github.nomadboxlab.monadbox.domain.model.ProxyMode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayException
import com.github.nomadboxlab.monadbox.remote.ServiceClient
import com.github.nomadboxlab.monadbox.remote.VpnPermissionRequired
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.runtime.client.root.RootTunController
import com.github.nomadboxlab.monadbox.service.ClashService
import com.github.nomadboxlab.monadbox.service.StatusProvider
import com.github.nomadboxlab.monadbox.service.TunService
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.root.RootTunRuntimeRecovery
import com.github.nomadboxlab.monadbox.service.root.RootTunState
import com.github.nomadboxlab.monadbox.service.root.RootTunStateStore
import com.github.nomadboxlab.monadbox.service.root.RootTunStatus
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import com.github.nomadboxlab.monadbox.service.runtime.session.RuntimeServiceLauncher
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeOwner
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
import com.tencent.mmkv.MMKV
import java.io.Closeable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber

internal data class ProxyGroupMetadata(val hidden: Boolean = false, val icon: String? = null)

private enum class StopTerminalOutcome {
    ReceiptObserved,
    ReconciledStaleMarker,
    TimedOut,
}

private data class StopSignalConsistency(
    val snapshotTerminal: Boolean,
    val statusStoreStopped: Boolean,
    val processStopped: Boolean,
) {
    val staleMarkerDetected: Boolean
        get() = !snapshotTerminal && statusStoreStopped && processStopped
}

private data class RuntimeEventActions(
    val serviceRecreated: String,
    val clashStarted: String,
    val clashStopped: String,
    val clashRequestStop: String,
    val profileChanged: String,
    val profileLoaded: String,
    val overrideChanged: String,
    val rootRuntimeFailed: String,
) {
    companion object {
        fun forPackage(packageName: String): RuntimeEventActions {
            return RuntimeEventActions(
                serviceRecreated = Intents.actionServiceRecreated(packageName),
                clashStarted = Intents.actionClashStarted(packageName),
                clashStopped = Intents.actionClashStopped(packageName),
                clashRequestStop = Intents.actionClashRequestStop(packageName),
                profileChanged = Intents.actionProfileChanged(packageName),
                profileLoaded = Intents.actionProfileLoaded(packageName),
                overrideChanged = Intents.actionOverrideChanged(packageName),
                rootRuntimeFailed = Intents.actionRootRuntimeFailed(packageName),
            )
        }
    }
}

private class ProxyFacadeEventBus(
    private val appContext: Context,
    private val actions: RuntimeEventActions,
    private val onClashStarted: () -> Unit,
    private val onClashStopped: (String?) -> Unit,
    private val onRefreshRequested: () -> Unit,
    private val onRootRuntimeFailed: (String, String?) -> Unit,
) {
    @Volatile private var registered = false

    private val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action ?: return) {
                    actions.clashStarted -> onClashStarted()
                    actions.clashStopped ->
                        onClashStopped(intent.getStringExtra(Intents.EXTRA_STOP_REASON))
                    actions.profileLoaded,
                    actions.profileChanged,
                    actions.overrideChanged,
                    actions.serviceRecreated -> onRefreshRequested()
                    actions.rootRuntimeFailed -> {
                        val code = intent.getStringExtra(Intents.EXTRA_ERROR_CODE)
                        val error =
                            intent.getStringExtra(Intents.EXTRA_ERROR_MESSAGE)
                                ?: intent.getStringExtra("error")
                        val composed =
                            listOfNotNull(
                                    code?.takeIf { it.isNotBlank() },
                                    error?.takeIf { it.isNotBlank() },
                                )
                                .joinToString(separator = ": ")
                                .ifBlank { "ROOT_RUNTIME_QUERY_FAILED: root runtime failed" }
                        onRootRuntimeFailed(composed, code)
                    }
                }
            }
        }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun start() {
        if (registered) return
        val filter =
            IntentFilter().apply {
                addAction(actions.clashStarted)
                addAction(actions.clashStopped)
                addAction(actions.profileChanged)
                addAction(actions.profileLoaded)
                addAction(actions.overrideChanged)
                addAction(actions.serviceRecreated)
                addAction(actions.rootRuntimeFailed)
            }
        runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    appContext.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
                } else {
                    appContext.registerReceiver(receiver, filter)
                }
            }
            .onFailure { error -> Timber.w(error, "Failed to register service event receiver") }
            .onSuccess { registered = true }
    }

    fun stop() {
        if (!registered) return
        runCatching { appContext.unregisterReceiver(receiver) }
        registered = false
    }
}

private class ProxyFacadeTrafficPoller(
    private val scope: CoroutineScope,
    private val onTick: suspend (Int) -> Unit,
) {
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return
        job =
            scope.launch {
                var tick = 0
                while (isActive) {
                    onTick(tick)
                    tick++
                }
            }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}

internal fun isLoopbackControllerHost(host: String): Boolean {
    val normalizedHost = host.trimStart('[').trimEnd(']').lowercase()
    return normalizedHost == "localhost" || normalizedHost == "127.0.0.1" || normalizedHost == "::1"
}

internal class MihomoControllerClient(private val json: Json) {
    fun fetchProxyGroupMetadata(configuration: UiConfiguration): Map<String, ProxyGroupMetadata> {
        return parseProxyGroupMetadata(requestJson(configuration, "/proxies"))
    }

    private fun requestJson(configuration: UiConfiguration, path: String): String {
        val controllerUrl =
            resolveControllerUrl(configuration) ?: error("Controller API unavailable")
        requireLoopbackController(controllerUrl)
        val normalizedPath = if (path.startsWith('/')) path else "/$path"
        val connection =
            (URL("$controllerUrl$normalizedPath").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("Accept", "application/json")
                configuration.secret
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        setRequestProperty(
                            "Authorization",
                            MihomoControllerEndpoint.bearerAuthorization(it),
                        )
                    }
            }

        return try {
            val body =
                (if (connection.responseCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    })
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    .orEmpty()

            if (connection.responseCode !in 200..299) {
                when (connection.responseCode) {
                    401 -> throw ControllerError.Unauthorized()
                    else ->
                        throw ControllerError.Unknown(
                            "Controller API ${connection.responseCode}: $body"
                        )
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
        require(isLoopbackControllerHost(host)) {
            "Controller must resolve to a loopback address (got: ${host.trimStart('[').trimEnd(']').lowercase()})"
        }
    }

    private fun parseProxyGroupMetadata(body: String): Map<String, ProxyGroupMetadata> {
        val root = json.parseToJsonElement(body).jsonObject
        val proxies = root["proxies"]?.jsonObject ?: return emptyMap()
        return proxies
            .mapNotNull { (name, payload) ->
                val metadata = payload.toProxyGroupMetadata() ?: return@mapNotNull null
                name to metadata
            }
            .toMap()
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
    appScope: CoroutineScope,
) : Closeable {
    private val appContext: Context = context.appContextOrSelf
    private val actions = RuntimeEventActions.forPackage(appContext.packageName)
    private val scope = CoroutineScope(appScope.coroutineContext + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }
    private val controllerClient = MihomoControllerClient(json)
    private val rootTunStateStore by lazy { RootTunStateStore(appContext) }
    private val runtimeState =
        ProxyFacadeRuntimeState(
            initialMode = networkSettingsStorage.proxyMode.value,
            initialRootTunStatus = rootTunStateStore.snapshot(),
        )
    val rootTunStatus: StateFlow<RootTunStatus> = runtimeState.rootTunStatus
    val runtimeSnapshot: StateFlow<RuntimeSnapshot> = runtimeState.runtimeSnapshot
    val isRunning: StateFlow<Boolean> = runtimeState.isRunning
    val proxyGroups: StateFlow<List<ProxyGroupInfo>> = runtimeState.proxyGroups
    val currentProfile: StateFlow<Profile?> = runtimeState.currentProfile
    val trafficNow: StateFlow<Traffic> = runtimeState.trafficNow
    val trafficTotal: StateFlow<Traffic> = runtimeState.trafficTotal

    private val _runtimeFailureEvents =
        MutableSharedFlow<RuntimeFailureEvent>(
            replay = 0,
            extraBufferCapacity = 8,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val runtimeFailureEvents: SharedFlow<RuntimeFailureEvent> = _runtimeFailureEvents.asSharedFlow()

    private val _proxySelectionEvents =
        MutableSharedFlow<String>(
            replay = 0,
            extraBufferCapacity = 4,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val proxySelectionEvents: SharedFlow<String> = _proxySelectionEvents.asSharedFlow()

    private val previewCache = ProxyFacadePreviewCache()
    private val latencyObservations = ProxyLatencyObservationStore()
    private var previewWarmupJob: Job? = null
    private val refreshProxyGroupsMutex = Mutex()
    private val operationMutex = Mutex()
    private val eventBus =
        ProxyFacadeEventBus(
            appContext = appContext,
            actions = actions,
            onClashStarted = { scope.launch { handleRuntimeStarted() } },
            onClashStopped = { reason -> scope.launch { handleRuntimeStopped(reason) } },
            onRefreshRequested = {
                scope.launch {
                    if (runtimeSnapshot.value.phase == RuntimePhase.Running) {
                        refreshAllSafely()
                    } else {
                        refreshPreviewStateSafely()
                    }
                }
            },
            onRootRuntimeFailed = { composed, code ->
                Timber.w("Root runtime failed: $composed")
                scope.launch { handleRuntimeFailure(composed, code) }
            },
        )
    private val trafficPoller =
        ProxyFacadeTrafficPoller(scope = scope) { tick ->
            val snapshot = runtimeSnapshot.value
            if (!snapshot.running) {
                delay(1000L.milliseconds)
                return@ProxyFacadeTrafficPoller
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

            if (tick % 3 == 0 && shouldRefreshRuntimePayload()) {
                refreshAllSafely()
            }

            delay(1000L.milliseconds)
        }

    init {
        eventBus.start()
        initializeRuntimeSnapshot()
    }

    fun warmUpProxyGroups() {
        if (previewWarmupJob?.isActive == true) return
        previewWarmupJob =
            scope.launch {
                runCatching { refreshProxyGroups() }
                    .onFailure { error -> Timber.d(error, "Warm up proxy groups skipped") }
            }
    }

    suspend fun startProxy(mode: ProxyMode = networkSettingsStorage.proxyMode.value) {
        Timber.i("Start proxy: mode=$mode")

        if (
            mode == ProxyMode.Tun &&
                StatusProvider.isTunStarting() &&
                !StatusProvider.isRuntimeActive(ProxyMode.Tun)
        ) {
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
            val targetOwner = ProxyFacadeOwnerPolicy.ownerForMode(mode)
            val currentOwner =
                detectActiveOwner().takeIf { it != RuntimeOwner.None }
                    ?: runtimeSnapshot.value.owner
            if (currentOwner != RuntimeOwner.None) {
                stopProxyInternal(targetMode = mode)
            }

            val generation = runtimeState.nextGeneration()

            runtimeState.clearRuntimePayload(resetGroups = false)
            runtimeState.setCurrentProfile(activeProfile)
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
                )
            )

            try {
                when (targetOwner) {
                    RuntimeOwner.RootTun -> {
                        startRootTun().getOrThrow()
                        handleRuntimeStarted(forceOwner = RuntimeOwner.RootTun)
                    }
                    RuntimeOwner.LocalTun,
                    RuntimeOwner.LocalHttp -> {
                        startLocalRuntime(mode)
                        awaitLocalStartTerminal(owner = targetOwner, mode = mode)
                    }
                    RuntimeOwner.None -> Unit
                }
            } catch (e: Exception) {
                val failureReason = e.message
                transitionToIdle(
                    configuredMode = mode,
                    generation = generation,
                    lastError = failureReason,
                )
                if (!failureReason.isNullOrBlank()) {
                    _runtimeFailureEvents.tryEmit(RuntimeFailureEvent(failureReason, mode))
                }
                throw e
            }
        }
    }

    suspend fun stopProxy(mode: ProxyMode? = null) {
        val targetMode = mode ?: networkSettingsStorage.proxyMode.value
        operationMutex.withLock {
            val generation = runtimeState.nextGeneration()
            try {
                stopProxyInternal(targetMode)
            } catch (e: Exception) {
                transitionToIdle(
                    configuredMode = targetMode,
                    generation = generation,
                    lastError = e.message,
                )
                throw e
            }
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
            // When the core is not running, update the UI optimistically
            // so the selection is visible immediately — no need to wait for
            // the full config re-compilation (previewGroups can be slow).
            val snapshot = runtimeSnapshot.value
            if (!snapshot.running) {
                val current = proxyGroups.value.toMutableList()
                val index = current.indexOfFirst { it.name == group }
                if (index >= 0) {
                    current[index] = current[index].copy(now = proxyName)
                    runtimeState.setProxyGroups(current)
                }
            }
            if (snapshot.running) {
                delay(200.milliseconds)
                // Stale external IP — notify listeners so the home screen can
                // clear its cached IP and prompt the user to re-query.
                _proxySelectionEvents.tryEmit(group)
            }
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
                refreshProxyGroups(captureObservedGroupNames = setOf(group))
            }
        } else {
            scope.launch {
                delay(300.milliseconds)
                repeat(3) {
                    runCatching { refreshProxyGroups(captureObservedGroupNames = setOf(group)) }
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
        latencyObservations.bind(latencyScopeKey())
        latencyObservations.record(proxyName, delay)
        refreshProxyGroups()
        return delay
    }

    suspend fun queryTunnelState(): TunnelState {
        connectCurrentBackend()
        return ServiceClient.clash().queryTunnelState()
    }

    suspend fun queryTrafficTotal(): Long {
        if (!runtimeSnapshot.value.running) {
            runtimeState.setTrafficTotal(0L)
            return 0L
        }
        connectCurrentBackend()
        val traffic = ServiceClient.clash().queryTrafficTotal()
        runtimeState.setTrafficTotal(traffic)
        runtimeState.updateTrafficReady()
        return traffic
    }

    suspend fun queryTrafficNow(): Long {
        if (!runtimeSnapshot.value.running) {
            runtimeState.setTrafficNow(0L)
            return 0L
        }
        connectCurrentBackend()
        val traffic = ServiceClient.clash().queryTrafficNow()
        runtimeState.setTrafficNow(traffic)
        runtimeState.updateTrafficReady()
        return traffic
    }

    suspend fun reloadCurrentProfile(): Result<Unit> {
        return try {
            val profileManager = ServiceClient.profile()
            val currentProfile = profileManager.queryActive()
            if (currentProfile != null) {
                triggerRuntimeReload(currentProfile)
                runtimeState.setCurrentProfile(currentProfile)
                runCatching { refreshAll() }
                    .onFailure { error -> Timber.d(error, "Reload refresh skipped") }
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
        runtimeState.setIsRunning(isRunning)
    }

    private fun normalizeProxyGroups(groups: List<ProxyGroup>): List<ProxyGroupInfo> {
        var normalized: MutableList<ProxyGroupInfo>? = null
        groups.forEachIndexed { index, group ->
            val normalizedNow = if (group.now.isBlank()) "-" else group.now
            val normalizedProxies = normalizeControllerProxyDelays(group.proxies)
            val normalizedGroup =
                if (normalizedNow == group.now && normalizedProxies === group.proxies) {
                    group
                } else {
                    group.copy(now = normalizedNow, proxies = normalizedProxies)
                }
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

    suspend fun refreshProxyGroups(captureObservedGroupNames: Set<String> = emptySet()) {
        refreshProxyGroupsMutex.withLock {
            val snapshot = runtimeSnapshot.value
            val latencyScopeKey = latencyScopeKey()
            val groups =
                withContext(Dispatchers.IO) {
                    try {
                        if (!snapshot.running) {
                            latencyObservations.clear()
                            return@withContext queryPreviewProxyGroups()
                        }

                        if (snapshot.owner == RuntimeOwner.RootTun && !isRootSessionActive()) {
                            throw ControllerError.Unavailable()
                        }

                        connectCurrentBackend()
                        val rawGroups =
                            ServiceClient.clash().queryAllProxyGroups(excludeNotSelectable = false)
                        if (captureObservedGroupNames.isNotEmpty()) {
                            latencyObservations.bind(latencyScopeKey)
                            rawGroups
                                .asSequence()
                                .filter { it.name in captureObservedGroupNames }
                                .forEach { latencyObservations.recordObservedProxies(it.proxies) }
                        }
                        val groups = normalizeProxyGroups(rawGroups)
                        latencyObservations.bind(latencyScopeKey)
                        latencyObservations.merge(
                            enrichProxyGroupsFromController(
                                groups,
                                ServiceClient.clash().queryConfiguration(),
                            )
                        )
                    } catch (e: ControllerError) {
                        Timber.w(e, "Failed to refresh proxy groups: ${e.message}")
                        null
                    } catch (e: Exception) {
                        Timber.e(
                            e,
                            "Failed to refresh proxy groups: ${e.runtimeGatewayMessage("unknown")}",
                        )
                        null
                    }
                }

            groups?.let {
                runtimeState.setProxyGroups(it)
                runtimeState.updateGroupsReady(it.isNotEmpty())
                previewCache.backfill(
                    profile = currentProfile.value,
                    groups = it,
                    runtimeSnapshot = runtimeSnapshot.value,
                    rootTunStatus = rootTunStatus.value,
                )
            }
                ?: previewCache
                    .fallback(
                        snapshot = snapshot,
                        profile = currentProfile.value,
                        rootTunStatus = rootTunStatus.value,
                    )
                    ?.let { cached ->
                        runtimeState.setProxyGroups(cached)
                        runtimeState.updateGroupsReady(cached.isNotEmpty())
                    }
        }
    }

    suspend fun refreshCurrentProfile() {
        when {
            runtimeSnapshot.value.owner == RuntimeOwner.RootTun &&
                runtimeSnapshot.value.phase == RuntimePhase.Running -> {
                val status = currentRootTunStatus()
                applyRootTunStatus(status)
                refreshRootCurrentProfile(status)
            }

            else -> {
                try {
                    val profile = ServiceClient.profile().queryActive()
                    runtimeState.setCurrentProfile(profile)
                    runtimeState.updateProfileReady(profile)
                } catch (e: ControllerError) {
                    Timber.w(e, "Failed to refresh current profile: ${e.message}")
                } catch (e: Exception) {
                    Timber.e(
                        e,
                        "Failed to refresh current profile: ${e.runtimeGatewayMessage("unknown")}",
                    )
                }
            }
        }
    }

    suspend fun refreshAll() {
        refreshCurrentProfile()
        refreshProxyGroups()
        if (runtimeSnapshot.value.phase == RuntimePhase.Running) {
            queryTrafficNow()
            queryTrafficTotal()
        } else {
            runtimeState.setTrafficNow(0L)
            runtimeState.setTrafficTotal(0L)
        }
    }

    private suspend fun startLocalRuntime(mode: ProxyMode) {
        when (mode) {
            ProxyMode.Tun,
            ProxyMode.Http ->
                RuntimeServiceLauncher.start(
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
                        }
                        .onFailure {
                            appContext.sendBroadcast(
                                Intent(actions.clashRequestStop).setPackage(appContext.packageName)
                            )
                        }
                    StatusProvider.clearTunStarting()
                }
            }

            RuntimeOwner.None -> Unit
        }
    }

    private suspend fun stopProxyInternal(targetMode: ProxyMode) {
        val owner =
            detectActiveOwner().takeIf { it != RuntimeOwner.None } ?: runtimeSnapshot.value.owner
        val generation = runtimeState.nextGeneration()

        if (owner == RuntimeOwner.None) {
            transitionToIdle(configuredMode = targetMode, generation = generation, lastError = null)
            return
        }

        publishRuntimeSnapshot(
            runtimeSnapshot.value.copy(
                owner = owner,
                phase = RuntimePhase.Stopping,
                targetMode = targetMode,
                profileReady = false,
                groupsReady = false,
                trafficReady = false,
                lastError = null,
                generation = generation,
            )
        )

        triggerStop(owner)

        when (awaitStopTerminal(owner)) {
            StopTerminalOutcome.ReceiptObserved -> {
                finalizeStopIfSnapshotNotTerminal(owner)
            }

            StopTerminalOutcome.ReconciledStaleMarker -> {
                Timber.w("Stop receipt missing; reconciled stale stop marker for ${owner.name}")
                reconcileStaleOwnerState(owner)
                finalizeStopIfSnapshotNotTerminal(owner)
            }

            StopTerminalOutcome.TimedOut -> {
                val consistency = collectStopSignalConsistency(owner)
                throw RuntimeGatewayException(
                    code = RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED,
                    message =
                        "Timed out waiting for stop receipt " +
                            "(owner=${owner.name}, " +
                            "snapshotTerminal=${consistency.snapshotTerminal}, " +
                            "statusStoreStopped=${consistency.statusStoreStopped}, " +
                            "processStopped=${consistency.processStopped})",
                )
            }
        }
    }

    private fun startTrafficPolling() {
        trafficPoller.start()
    }

    private fun stopTrafficPolling() {
        trafficPoller.stop()
    }

    override fun close() {
        previewWarmupJob?.cancel()
        previewWarmupJob = null
        stopTrafficPolling()
        eventBus.stop()
        scope.cancel()
    }

    private fun initializeRuntimeSnapshot() {
        val configuredMode = networkSettingsStorage.proxyMode.value
        clearLegacyRuntimeCaches()
        val rootStatus = resolveInitialRootTunStatus()
        applyRootTunStatus(rootStatus)
        val owner =
            ProxyFacadeOwnerPolicy.detectActiveOwner(
                rootActive = rootStatus.state.isActive || rootStatus.runtimeReady,
                localTunActive = isLocalSessionActive(ProxyMode.Tun),
                localHttpActive = isLocalSessionActive(ProxyMode.Http),
            )

        if (owner == RuntimeOwner.None) {
            runtimeState.clearRuntimePayload(resetGroups = false)
            publishRuntimeSnapshot(RuntimeStateMapper.idleSnapshot(configuredMode))
            scope.launch { refreshPreviewStateSafely() }
            return
        }

        publishRuntimeSnapshot(
            RuntimeSnapshot(
                owner = owner,
                phase =
                    if (owner == RuntimeOwner.RootTun) rootPhase(rootStatus)
                    else RuntimePhase.Running,
                targetMode =
                    ProxyFacadeOwnerPolicy.modeForOwner(
                        owner,
                        networkSettingsStorage.proxyMode.value,
                    ),
                profileReady =
                    owner == RuntimeOwner.RootTun && !rootStatus.profileUuid.isNullOrBlank(),
                profileUuid = rootStatus.profileUuid.takeIf { owner == RuntimeOwner.RootTun },
                profileName = rootStatus.profileName.takeIf { owner == RuntimeOwner.RootTun },
                lastError = if (owner == RuntimeOwner.RootTun) rootStatus.composedError() else null,
                startedAt = rootStatus.startedAt.takeIf { owner == RuntimeOwner.RootTun },
            )
        )
        startTrafficPolling()
        scope.launch { verifyInitialRootRuntimeState(owner, rootStatus) }
        scope.launch { refreshAllSafely() }
    }

    private fun detectActiveOwner(): RuntimeOwner {
        return ProxyFacadeOwnerPolicy.detectActiveOwner(
            rootActive = isRootSessionActive(),
            localTunActive = isLocalSessionActive(ProxyMode.Tun),
            localHttpActive = isLocalSessionActive(ProxyMode.Http),
        )
    }

    private fun resolveInitialRootTunStatus(): RootTunStatus {
        return recoverRootTunStatus(rootTunStateStore.snapshot())
    }

    private fun recoverRootTunStatus(
        status: RootTunStatus = rootTunStateStore.snapshot()
    ): RootTunStatus {
        val recovered = RootTunRuntimeRecovery.recoverStaleTransition(appContext, status)
        applyRootTunStatus(recovered)
        return recovered
    }

    private fun isRootSessionActive(): Boolean {
        val status = recoverRootTunStatus()
        return status.state.isActive || status.runtimeReady
    }

    private fun isLocalSessionActive(mode: ProxyMode?): Boolean {
        if (mode == null) return false
        return StatusProvider.isRuntimeActive(mode)
    }

    private suspend fun handleRuntimeStarted(forceOwner: RuntimeOwner? = null) {
        val currentSnapshot = runtimeSnapshot.value
        val owner =
            RuntimeTransitionPolicy.resolveStartedOwner(
                forceOwner = forceOwner,
                currentOwner = currentSnapshot.owner,
                detectedOwner = detectActiveOwner(),
            )
        if (owner == RuntimeOwner.None) return

        publishRuntimeSnapshot(
            RuntimeTransitionPolicy.startedSnapshot(
                currentSnapshot = currentSnapshot,
                owner = owner,
                targetMode =
                    ProxyFacadeOwnerPolicy.modeForOwner(
                        owner,
                        networkSettingsStorage.proxyMode.value,
                    ),
            )
        )
        startTrafficPolling()
        refreshAllSafely()
    }

    private suspend fun handleRuntimeStopped(reason: String?) {
        when (resolveRuntimeStopResolution(runtimeSnapshot.value.phase, reason)) {
            RuntimeStopResolution.IgnoreAsStale -> {
                Timber.d("handleRuntimeStopped: ignoring stale stop event (phase=Starting)")
                return
            }

            RuntimeStopResolution.SkipAsRedundant -> {
                Timber.d("handleRuntimeStopped: skipping redundant idle transition")
                return
            }

            RuntimeStopResolution.TransitionToIdle -> Unit
        }

        if (!reason.isNullOrBlank()) {
            _runtimeFailureEvents.tryEmit(
                RuntimeFailureEvent(reason, networkSettingsStorage.proxyMode.value)
            )
        }

        val configuredMode = networkSettingsStorage.proxyMode.value
        val generation = runtimeState.nextGeneration()

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
        val generation = runtimeState.nextGeneration()
        val errorCode =
            errorCodeRaw?.let { raw ->
                runCatching {
                        com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode.valueOf(raw)
                    }
                    .getOrNull()
            }
        if (!isRootSessionActive()) {
            rootTunStateStore.markIdle(error = error, errorCode = errorCode)
            applyRootTunStatus(rootTunStateStore.snapshot())
        }
        val normalizedError = RuntimeTransitionPolicy.resolveFailureMessage(error, errorCode)
        if (normalizedError.isNotBlank()) {
            _runtimeFailureEvents.tryEmit(
                RuntimeFailureEvent(normalizedError, networkSettingsStorage.proxyMode.value)
            )
        }
        transitionToIdle(
            configuredMode = networkSettingsStorage.proxyMode.value,
            generation = generation,
            lastError = normalizedError,
        )
    }

    private fun transitionToIdle(configuredMode: ProxyMode, generation: Long, lastError: String?) {
        latencyObservations.clear()
        runtimeState.clearRuntimePayload(resetGroups = false)
        publishRuntimeSnapshot(
            RuntimeStateMapper.idleSnapshot(
                configuredMode = configuredMode,
                generation = generation,
                lastError = lastError,
            )
        )
        stopTrafficPolling()
        scope.launch { refreshPreviewStateSafely() }
    }

    private suspend fun refreshAllSafely() {
        if (runtimeSnapshot.value.phase != RuntimePhase.Running) {
            return
        }
        runCatching { refreshAll() }
            .onFailure { error -> Timber.d(error, "Refresh runtime data skipped") }
    }

    private suspend fun refreshPreviewStateSafely() {
        runCatching {
                refreshCurrentProfile()
                refreshProxyGroups()
            }
            .onFailure { error -> Timber.d(error, "Refresh preview data skipped") }
    }

    private fun shouldRefreshRuntimePayload(): Boolean {
        return RuntimeTransitionPolicy.shouldRefreshPayload(
            snapshot = runtimeSnapshot.value,
            groupsEmpty = proxyGroups.value.isEmpty(),
            profileMissing = currentProfile.value == null,
        )
    }

    private suspend fun currentRootTunStatus(): RootTunStatus {
        val status =
            runCatching { RootTunController.queryStatus(appContext) }
                .getOrElse { rootTunStatus.value }
        return recoverRootTunStatus(status)
    }

    private fun clearLegacyRuntimeCaches() {
        StatusProvider.clearLegacyStateFiles()
        val rootStatus = rootTunStateStore.snapshot()
        if (!rootStatus.state.isActive && !rootStatus.runtimeReady) {
            runCatching { rootTunStateStore.clear() }
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
        runtimeState.applyRootTunStatus(status)
    }

    private fun publishRuntimeSnapshot(snapshot: RuntimeSnapshot) {
        runtimeState.publishRuntimeSnapshot(snapshot)
    }

    private suspend fun connectCurrentBackend() {
        ServiceClient.connect(appContext)
    }

    private suspend fun refreshRootCurrentProfile(status: RootTunStatus) {
        runCatching {
                connectCurrentBackend()
                val profile =
                    status.profileUuid
                        ?.takeIf { it.isNotBlank() }
                        ?.let { uuid -> ServiceClient.profile().queryByUUID(UUID.fromString(uuid)) }
                        ?: ServiceClient.profile().queryActive()

                if (profile != null) {
                    runtimeState.setCurrentProfile(profile)
                }
                runtimeState.updateProfileReady(profile)
            }
            .onFailure { error -> Timber.d(error, "Failed to refresh root current profile") }
    }

    private suspend fun triggerRuntimeReload(currentProfile: Profile) {
        val owner =
            detectActiveOwner().takeIf { it != RuntimeOwner.None } ?: runtimeSnapshot.value.owner
        when (owner) {
            RuntimeOwner.RootTun -> {
                val result = RootTunController.reload(appContext)
                if (!result.success) {
                    throw result.toException(
                        defaultCode = RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED,
                        defaultMessage = "RootTun reload failed",
                    )
                }
                applyRootTunStatus(rootTunStateStore.snapshot())
            }

            RuntimeOwner.LocalTun,
            RuntimeOwner.LocalHttp -> {
                ServiceClient.profile().setActive(currentProfile)
            }
            RuntimeOwner.None -> Unit
        }
    }

    private suspend fun awaitStopTerminal(owner: RuntimeOwner): StopTerminalOutcome {
        repeat(STOP_WAIT_RETRY_COUNT) {
            val consistency = collectStopSignalConsistency(owner)
            if (consistency.snapshotTerminal) {
                return StopTerminalOutcome.ReceiptObserved
            }
            if (
                owner == RuntimeOwner.RootTun &&
                    consistency.statusStoreStopped &&
                    consistency.processStopped
            ) {
                return StopTerminalOutcome.ReceiptObserved
            }
            if (consistency.staleMarkerDetected) {
                return StopTerminalOutcome.ReconciledStaleMarker
            }
            delay(STOP_WAIT_RETRY_DELAY_MS.milliseconds)
        }

        val consistency = collectStopSignalConsistency(owner)
        if (consistency.snapshotTerminal) {
            return StopTerminalOutcome.ReceiptObserved
        }
        if (consistency.staleMarkerDetected) {
            return StopTerminalOutcome.ReconciledStaleMarker
        }
        return StopTerminalOutcome.TimedOut
    }

    private suspend fun awaitLocalStartTerminal(owner: RuntimeOwner, mode: ProxyMode) {
        check(owner == RuntimeOwner.LocalTun || owner == RuntimeOwner.LocalHttp) {
            "awaitLocalStartTerminal only supports local owners"
        }

        repeat(START_WAIT_RETRY_COUNT) {
            val snapshot = runtimeSnapshot.value
            when (snapshot.phase) {
                RuntimePhase.Running -> return
                RuntimePhase.Failed,
                RuntimePhase.Idle -> {
                    Timber.w(
                        "awaitLocalStartTerminal: detected phase=${snapshot.phase} lastError=${snapshot.lastError}"
                    )
                    throw RuntimeGatewayException(
                        code = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                        message =
                            snapshot.lastError
                                ?: "runtime moved to ${snapshot.phase.name} during start",
                    )
                }
                RuntimePhase.Starting,
                RuntimePhase.Stopping -> Unit
            }
            delay(START_WAIT_RETRY_DELAY_MS.milliseconds)
        }

        val processStarted = !isOwnerProcessStopped(owner)
        val ownerActive = isOwnerActive(owner)
        if (processStarted || ownerActive) {
            Timber.w(
                "Start receipt missing; reconciling stale starting marker for ${owner.name} (mode=${mode.name})"
            )
            handleRuntimeStarted(forceOwner = owner)
            if (runtimeSnapshot.value.phase == RuntimePhase.Running) {
                return
            }
        }

        throw RuntimeGatewayException(
            code = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
            message =
                "Timed out waiting for start receipt " +
                    "(owner=${owner.name}, mode=${mode.name}, " +
                    "ownerActive=$ownerActive, processStarted=$processStarted)",
        )
    }

    private suspend fun collectStopSignalConsistency(owner: RuntimeOwner): StopSignalConsistency {
        val snapshotPhase = runtimeSnapshot.value.phase
        return StopSignalConsistency(
            snapshotTerminal =
                snapshotPhase == RuntimePhase.Idle || snapshotPhase == RuntimePhase.Failed,
            statusStoreStopped = !isOwnerActive(owner),
            processStopped = isOwnerProcessStopped(owner),
        )
    }

    private fun isOwnerProcessStopped(owner: RuntimeOwner): Boolean {
        return when (owner) {
            RuntimeOwner.RootTun -> {
                val status = recoverRootTunStatus()
                !status.state.isActive && !status.runtimeReady
            }

            RuntimeOwner.LocalTun -> !isServiceRunning(TunService::class.java)
            RuntimeOwner.LocalHttp -> !isServiceRunning(ClashService::class.java)
            RuntimeOwner.None -> true
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
        return runCatching {
                @Suppress("DEPRECATION")
                activityManager.getRunningServices(Int.MAX_VALUE).any { runningService ->
                    runningService.service.className == serviceClass.name
                }
            }
            .getOrDefault(false)
    }

    private suspend fun finalizeStopIfSnapshotNotTerminal(owner: RuntimeOwner) {
        val phase = runtimeSnapshot.value.phase
        if (phase == RuntimePhase.Idle || phase == RuntimePhase.Failed) return
        val reason =
            when (owner) {
                RuntimeOwner.RootTun -> rootTunStateStore.snapshot().composedError()
                else -> null
            }
        handleRuntimeStopped(reason)
    }

    private fun reconcileStaleOwnerState(owner: RuntimeOwner) {
        when (owner) {
            RuntimeOwner.LocalTun -> {
                StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                StatusProvider.clearTunStarting()
            }

            RuntimeOwner.LocalHttp -> StatusProvider.markRuntimeStopped(ProxyMode.Http)
            RuntimeOwner.RootTun -> applyRootTunStatus(rootTunStateStore.snapshot())
            RuntimeOwner.None -> Unit
        }
    }

    private fun isOwnerActive(owner: RuntimeOwner): Boolean {
        return when (owner) {
            RuntimeOwner.RootTun -> isRootSessionActive()
            RuntimeOwner.LocalTun -> isLocalSessionActive(ProxyMode.Tun)
            RuntimeOwner.LocalHttp -> isLocalSessionActive(ProxyMode.Http)
            RuntimeOwner.None -> false
        }
    }

    private suspend fun verifyInitialRootRuntimeState(
        owner: RuntimeOwner,
        persistedStatus: RootTunStatus,
    ) {
        if (owner != RuntimeOwner.RootTun) return
        if (!persistedStatus.state.isActive && !persistedStatus.runtimeReady) return

        runCatching { RootTunController.queryStatus(appContext) }
            .onSuccess { status ->
                applyRootTunStatus(status)
                if (status.state == RootTunState.Failed || status.state == RootTunState.Idle) {
                    handleRuntimeStopped(status.composedError())
                } else {
                    handleRuntimeStarted(forceOwner = RuntimeOwner.RootTun)
                }
            }
            .onFailure { error ->
                Timber.w(error, "Initial RootTun reconciliation failed")
                RootTunRuntimeRecovery.handleBinderGone(
                    appContext,
                    RootTunRuntimeRecovery.binderFailureReason(error),
                )
                handleRuntimeStopped(error.runtimeGatewayMessage("RootTun reconciliation failed"))
            }
    }

    private suspend fun queryPreviewProxyGroups(): List<ProxyGroupInfo> {
        val cachedProfile = currentProfile.value
        val activeProfile =
            cachedProfile
                ?: ServiceClient.profile().queryActive().also {
                    runtimeState.setCurrentProfile(it)
                    runtimeState.updateProfileReady(it)
                }

        if (activeProfile == null) {
            return emptyList()
        }
        connectCurrentBackend()
        val groups =
            normalizeProxyGroups(
                ServiceClient.clash().queryProfileProxyGroups(excludeNotSelectable = false)
            )

        return groups
    }

    private fun latencyScopeKey(): String? {
        return currentProfile.value?.uuid?.toString()
            ?: runtimeSnapshot.value.profileUuid?.takeIf { it.isNotBlank() }
    }

    private fun normalizeControllerProxyDelays(proxies: List<Proxy>): List<Proxy> {
        var normalized: MutableList<Proxy>? = null
        proxies.forEachIndexed { index, proxy ->
            val normalizedDelay = ProxyLatencyState.normalizeSnapshotDelay(proxy.delay)
            val normalizedProxy =
                if (normalizedDelay == proxy.delay) {
                    proxy
                } else {
                    proxy.copy(delay = normalizedDelay)
                }
            if (normalized == null && normalizedProxy !== proxy) {
                normalized = ArrayList(proxies.size)
                var head = 0
                while (head < index) {
                    normalized?.add(proxies[head])
                    head += 1
                }
            }
            normalized?.add(normalizedProxy)
        }
        return normalized ?: proxies
    }

    private fun enrichProxyGroupsFromController(
        groups: List<ProxyGroupInfo>,
        configuration: UiConfiguration,
    ): List<ProxyGroupInfo> {
        if (groups.isEmpty()) return groups
        val metadataByName =
            try {
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
            val nextGroup =
                if (metadata == null) {
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

    private fun fetchProxyGroupMetadata(
        configuration: UiConfiguration
    ): Map<String, ProxyGroupMetadata> {
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

    private companion object {
        private const val START_WAIT_RETRY_COUNT = 80
        private const val START_WAIT_RETRY_DELAY_MS = 125L
        private const val STOP_WAIT_RETRY_COUNT = 80
        private const val STOP_WAIT_RETRY_DELAY_MS = 125L
    }
}

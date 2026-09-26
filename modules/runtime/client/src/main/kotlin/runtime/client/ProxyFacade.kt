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
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.core.controller.ControllerError
import com.github.nomadboxlab.monadbox.core.controller.MihomoControllerEndpoint
import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.core.model.ProxyGroup
import com.github.nomadboxlab.monadbox.core.model.ProxySort
import com.github.nomadboxlab.monadbox.core.model.Traffic
import com.github.nomadboxlab.monadbox.core.model.TrafficSnapshot
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
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
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
    val clashStarted: String,
    val clashStopped: String,
    val clashRequestStop: String,
    val profileChanged: String,
    val profileLoaded: String,
    val overrideChanged: String,
    val rootRuntimeFailed: String,
    val accessControlApplyFailed: String,
) {
    companion object {
        fun forPackage(packageName: String): RuntimeEventActions {
            return RuntimeEventActions(
                clashStarted = Intents.actionClashStarted(packageName),
                clashStopped = Intents.actionClashStopped(packageName),
                clashRequestStop = Intents.actionClashRequestStop(packageName),
                profileChanged = Intents.actionProfileChanged(packageName),
                profileLoaded = Intents.actionProfileLoaded(packageName),
                overrideChanged = Intents.actionOverrideChanged(packageName),
                rootRuntimeFailed = Intents.actionRootRuntimeFailed(packageName),
                accessControlApplyFailed = Intents.actionAccessControlApplyFailed(packageName),
            )
        }
    }
}

private class ProxyFacadeEventBus(
    private val appContext: Context,
    private val actions: RuntimeEventActions,
    private val onClashStarted: () -> Unit,
    private val onClashStopped: (String?) -> Unit,
    private val onProfileLoaded: (String?) -> Unit,
    private val onRefreshRequested: () -> Unit,
    private val onRootRuntimeFailed: (String, String?) -> Unit,
    private val onAccessControlApplyFailed: (String) -> Unit,
) {
    @Volatile private var registered = false

    private val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action ?: return) {
                    actions.clashStarted -> onClashStarted()
                    actions.clashStopped ->
                        onClashStopped(intent.getStringExtra(Intents.EXTRA_STOP_REASON))
                    actions.profileLoaded ->
                        onProfileLoaded(intent.getStringExtra(Intents.EXTRA_UUID))
                    actions.profileChanged,
                    actions.overrideChanged -> onRefreshRequested()
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
                    actions.accessControlApplyFailed -> {
                        val error =
                            intent.getStringExtra(Intents.EXTRA_ERROR_MESSAGE)
                                ?: intent.getStringExtra("error")
                        onAccessControlApplyFailed(
                            error?.takeIf { it.isNotBlank() } ?: "Failed to apply access control"
                        )
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
                addAction(actions.rootRuntimeFailed)
                addAction(actions.accessControlApplyFailed)
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

private class AppForegroundObserver(
    private val context: Context,
    private val onWakeRequested: () -> Unit = {},
) {
    private val startedActivityCount = java.util.concurrent.atomic.AtomicInteger(0)
    private val callback =
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                startedActivityCount.incrementAndGet()
                onWakeRequested()
            }

            override fun onActivityStopped(activity: Activity) {
                startedActivityCount.decrementAndGet()
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

            override fun onActivityResumed(activity: Activity) = Unit

            override fun onActivityPaused(activity: Activity) = Unit

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityDestroyed(activity: Activity) = Unit
        }

    private val screenReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> onWakeRequested()
                    Intent.ACTION_SCREEN_OFF -> Unit
                }
            }
        }

    val isForeground: Boolean
        get() = startedActivityCount.get() > 0

    fun start() {
        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(callback)
        val filter =
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.applicationContext.registerReceiver(
                        screenReceiver,
                        filter,
                        Context.RECEIVER_NOT_EXPORTED,
                    )
                } else {
                    context.applicationContext.registerReceiver(screenReceiver, filter)
                }
            }
            .onFailure { error -> Timber.w(error, "Failed to register screen receiver") }
    }

    fun stop() {
        (context.applicationContext as? Application)?.unregisterActivityLifecycleCallbacks(callback)
        runCatching { context.applicationContext.unregisterReceiver(screenReceiver) }
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

    /**
     * Restart the poller immediately so it can break out of a long background delay (e.g. the app
     * returned to the foreground or the screen turned on) and resume the active cadence without
     * waiting for the next scheduled tick.
     */
    fun kick() {
        val active = job ?: return
        if (!active.isActive) return
        job = null
        active.cancel()
        start()
    }
}

internal fun isLoopbackControllerHost(host: String): Boolean {
    val normalizedHost = host.trimStart('[').trimEnd(']').lowercase()
    return normalizedHost == "localhost" || normalizedHost == "127.0.0.1" || normalizedHost == "::1"
}

internal class MihomoControllerClient(private val json: Json) {
    private val httpClient =
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .callTimeout(6, TimeUnit.SECONDS)
            .build()

    fun fetchProxyGroupMetadata(configuration: UiConfiguration): Map<String, ProxyGroupMetadata> {
        return parseProxyGroupMetadata(requestJson(configuration, "/proxies"))
    }

    private fun requestJson(configuration: UiConfiguration, path: String): String {
        val controllerUrl =
            resolveControllerUrl(configuration) ?: error("Controller API unavailable")
        requireLoopbackController(controllerUrl)
        val normalizedPath = if (path.startsWith('/')) path else "/$path"
        val requestBuilder =
            Request.Builder()
                .url(URL("$controllerUrl$normalizedPath"))
                .header("Accept", "application/json")
        configuration.secret
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                requestBuilder.header(
                    "Authorization",
                    MihomoControllerEndpoint.bearerAuthorization(it),
                )
            }

        return try {
            httpClient.newCall(requestBuilder.build()).execute().use { response ->
                val body = response.body.string().orEmpty()
                if (!response.isSuccessful) {
                    when (response.code) {
                        401 -> throw ControllerError.Unauthorized()
                        else ->
                            throw ControllerError.Unknown("Controller API ${response.code}: $body")
                    }
                }
                body
            }
        } catch (e: ControllerError) {
            throw e
        } catch (e: java.net.ConnectException) {
            throw ControllerError.Unavailable(e)
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
    val proxyGroupsLoadState: StateFlow<ProxyGroupsLoadState> = runtimeState.proxyGroupsLoadState
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

    private val _proxyPageVisible = MutableStateFlow(false)
    val proxyPageVisible: StateFlow<Boolean> = _proxyPageVisible.asStateFlow()

    fun setProxyPageVisible(visible: Boolean) {
        _proxyPageVisible.value = visible
    }

    private val previewCache =
        ProxyFacadePreviewCache(appContext.filesDir.resolve("proxy-groups-preview.json"))
    private val powerManager by lazy {
        appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private val appForegroundObserver =
        AppForegroundObserver(appContext, onWakeRequested = { kickTrafficPoller() })
    @Volatile private var lastPayloadRefreshAt = 0L
    private val latencyObservations = ProxyLatencyObservationStore()
    private var previewWarmupJob: Job? = null
    private val refreshProxyGroupsMutex = Mutex()
    private var runtimeReconcileJob: Job? = null

    private data class RefreshFlight(val previewEpoch: Long, val deferred: Deferred<Unit>)

    private val refreshFlightMutex = Mutex()
    private var refreshInFlight: RefreshFlight? = null
    private var localStopDrain: Deferred<Boolean>? = null
    private val operationMutex = Mutex()
    private val runtimeTransitionLock = Any()
    private val eventBus =
        ProxyFacadeEventBus(
            appContext = appContext,
            actions = actions,
            onClashStarted = { scope.launch { handleRuntimeStarted() } },
            onClashStopped = { reason -> scope.launch { handleRuntimeStopped(reason) } },
            onProfileLoaded = { profileUuid ->
                scope.launch { handleRuntimeProfileLoaded(profileUuid) }
            },
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
            onAccessControlApplyFailed = { reason ->
                scope.launch { handleAccessControlApplyFailed(reason) }
            },
        )
    private val trafficPoller =
        ProxyFacadeTrafficPoller(scope = scope) { _ ->
            val snapshot = runtimeSnapshot.value
            val active =
                snapshot.running && powerManager.isInteractive && appForegroundObserver.isForeground
            if (!active) {
                delay(BACKGROUND_POLL_MS.milliseconds)
                return@ProxyFacadeTrafficPoller
            }

            try {
                connectCurrentBackend()
                queryTrafficSnapshot()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ControllerError) {
                Timber.d(e, "Traffic polling skipped: ${e.message}")
            } catch (e: Exception) {
                Timber.d(e, "Traffic polling skipped")
            }

            val now = SystemClock.elapsedRealtime()
            val payloadRefreshInterval =
                if (proxyPageVisible.value) PROXY_PAGE_PAYLOAD_REFRESH_INTERVAL_MS
                else PAYLOAD_REFRESH_INTERVAL_MS
            if (
                now - lastPayloadRefreshAt >= payloadRefreshInterval &&
                    shouldRefreshRuntimePayload()
            ) {
                lastPayloadRefreshAt = now
                // Traffic was already queried above in this tick. Keep the
                // metadata refresh from issuing the same JNI/Binder calls again.
                refreshAllSafely(includeTraffic = false)
            }

            delay(ACTIVE_POLL_MS.milliseconds)
        }

    init {
        appForegroundObserver.start()
        eventBus.start()
        initializeRuntimeSnapshot()
    }

    fun warmUpProxyGroups() {
        if (previewWarmupJob?.isActive == true) return
        previewWarmupJob =
            scope.launch {
                runCatching { refreshPreviewStateSafely() }
                    .onFailure { error -> Timber.d(error, "Warm up proxy groups skipped") }
            }
    }

    /**
     * Rebuilds the stopped-runtime preview after the routing mode changes. This invalidates only
     * rendered proxy groups; SelectionDao remains the source of truth for offline node selections.
     */
    fun refreshPreviewForModeChange() {
        val snapshot = runtimeSnapshot.value
        // A live (or starting) runtime reloads its core on a mode change and
        // serves the preview from the runtime payload; rebuilding it here
        // would only race the start sequence.
        if (snapshot.phase == RuntimePhase.Running || snapshot.phase == RuntimePhase.Starting) {
            return
        }
        // Drop the previous mode's preview synchronously. Deferring the clear
        // behind the refresh lock keeps stale groups on screen until any
        // in-flight preview query releases it (the config preview compile can
        // take seconds). Clearing up front shows the loading state right away;
        // the epoch bump also discards refresh results captured under the
        // previous mode.
        runtimeState.clearProxyGroupsForPreview()
        previewCache.invalidate()
        previewWarmupJob?.cancel()
        previewWarmupJob = scope.launch { refreshPreviewStateSafely() }
    }

    suspend fun startProxy(mode: ProxyMode = networkSettingsStorage.proxyMode.value) {
        Timber.i("Start proxy: mode=$mode")
        cancelRuntimeReconcile()

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

        // VPN consent is only required before the first establishment. When the TUN session is
        // already active (restarting to pick up routing/parameter changes), calling prepare() can
        // revoke the live connection on some OEMs and always re-prompts for consent, so skip it.
        if (mode == ProxyMode.Tun && !isLocalSessionActive(ProxyMode.Tun)) {
            val vpnIntent = VpnService.prepare(context)
            if (vpnIntent != null) {
                throw VpnPermissionRequired(vpnIntent)
            }
        }

        operationMutex.withLock {
            // A local service may have marked the runtime stopped while its
            // Android Service instance is still finishing onDestroy. Do not
            // overlap a new start with that teardown.
            awaitPendingLocalStopDrain()

            val targetOwner = ProxyFacadeOwnerPolicy.ownerForMode(mode)
            val currentOwner =
                detectActiveOwner().takeIf { it != RuntimeOwner.None }
                    ?: runtimeSnapshot.value.owner
            if (currentOwner != RuntimeOwner.None) {
                stopProxyInternal(targetMode = mode)
                awaitPendingLocalStopDrain()
            }

            val generation =
                synchronized(runtimeTransitionLock) {
                    val nextGeneration = runtimeState.nextGeneration()

                    val keepPreviewGroups = runtimeState.canKeepProxyGroupsFor(activeProfile)
                    runtimeState.clearRuntimePayload(resetGroups = !keepPreviewGroups)
                    if (keepPreviewGroups) {
                        runtimeState.markProxyGroupsLoading()
                    }
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
                            generation = nextGeneration,
                        )
                    )
                    nextGeneration
                }

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
                // A failure here may come from stopProxyInternal while tearing
                // down a previously running runtime. Only finalize to Idle when
                // the runtime genuinely went inactive; otherwise keep the honest
                // Running state so the UI never claims the VPN is off while it
                // is still running.
                reconcileStopFailure(
                    targetMode = mode,
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
        cancelRuntimeReconcile()
        val targetMode = mode ?: networkSettingsStorage.proxyMode.value
        operationMutex.withLock {
            val generation = synchronized(runtimeTransitionLock) { runtimeState.nextGeneration() }
            try {
                stopProxyInternal(targetMode)
            } catch (e: Exception) {
                // A stop failure must not be presented as a successful teardown:
                // stopProxyInternal keeps the snapshot honest (restores Running
                // when the service is still alive). Only finalize to Idle when
                // the runtime genuinely went away, so the home toggle reflects
                // reality.
                reconcileStopFailure(
                    targetMode = targetMode,
                    generation = generation,
                    lastError = e.message,
                )
                throw e
            }
        }
    }

    /**
     * Applies a per-app access-control change without tearing the VPN down. The running local TUN
     * service re-establishes its VPN parameters in place (seamless handover), so the connection is
     * never dropped and no VPN permission re-prompt is needed. No-op when the local TUN runtime is
     * not the active owner; failures are reported asynchronously via [runtimeFailureEvents].
     */
    fun reestablishForAccessControl() {
        val snapshot = runtimeSnapshot.value
        if (snapshot.owner != RuntimeOwner.LocalTun || snapshot.phase != RuntimePhase.Running) {
            return
        }
        appContext.sendBroadcast(
            Intent(Intents.ACTION_ACCESS_CONTROL_CHANGED).setPackage(appContext.packageName)
        )
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
                // Stale external IP — notify listeners so the home screen can
                // clear its cached IP and prompt the user to re-query.
                _proxySelectionEvents.tryEmit(group)
                delay(200.milliseconds)
            }
            refreshProxyGroups()
        }
        return ok
    }

    /**
     * Applies a routing-mode change to a live runtime via the fast path. Returns false when the
     * runtime is not running or the underlying core rejected the update, in which case the caller
     * should fall back to the legacy full-config reload.
     */
    suspend fun patchMode(mode: TunnelState.Mode): Boolean {
        if (!isRunning.value) return false
        connectCurrentBackend()
        return runCatching { ServiceClient.clash().patchMode(mode) }.getOrDefault(false)
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

    suspend fun queryTrafficSnapshot(): TrafficSnapshot {
        if (!runtimeSnapshot.value.running) {
            runtimeState.setTrafficNow(0L)
            runtimeState.setTrafficTotal(0L)
            return TrafficSnapshot(0L, 0L)
        }
        connectCurrentBackend()
        val traffic = ServiceClient.clash().queryTrafficSnapshot()
        runtimeState.setTrafficNow(traffic.now)
        runtimeState.setTrafficTotal(traffic.total)
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
                    normalized.add(groups[head])
                    head += 1
                }
            }
            normalized?.add(normalizedGroup)
        }
        return normalized ?: groups
    }

    suspend fun refreshProxyGroups(captureObservedGroupNames: Set<String> = emptySet()) {
        if (captureObservedGroupNames.isEmpty()) {
            val previewEpoch = runtimeState.currentPreviewEpoch()
            val request =
                refreshFlightMutex.withLock {
                    refreshInFlight?.takeIf {
                        it.deferred.isActive && it.previewEpoch == previewEpoch
                    }
                        ?: RefreshFlight(
                                previewEpoch = previewEpoch,
                                deferred =
                                    scope.async(Dispatchers.Default) {
                                        refreshProxyGroupsInternal(emptySet())
                                    },
                            )
                            .also { refreshInFlight = it }
                }
            try {
                request.deferred.await()
            } finally {
                refreshFlightMutex.withLock {
                    if (refreshInFlight === request && request.deferred.isCompleted) {
                        refreshInFlight = null
                    }
                }
            }
            return
        }

        refreshProxyGroupsInternal(captureObservedGroupNames)
    }

    private suspend fun refreshProxyGroupsInternal(captureObservedGroupNames: Set<String>) {
        refreshProxyGroupsMutex.withLock {
            runtimeState.markProxyGroupsLoading()
            val snapshot = runtimeSnapshot.value
            val latencyScopeKey = latencyScopeKey()
            val profileAtRequest = currentProfile.value
            val previewEpochAtRequest = runtimeState.currentPreviewEpoch()
            var runtimeConfiguration: UiConfiguration? = null
            val groups =
                withContext(Dispatchers.IO) {
                    try {
                        if (!snapshot.running && snapshot.phase != RuntimePhase.Starting) {
                            latencyObservations.clear()
                            return@withContext queryPreviewProxyGroups()
                        }

                        if (snapshot.owner == RuntimeOwner.RootTun && !isRootSessionActive()) {
                            throw ControllerError.Unavailable()
                        }

                        connectCurrentBackend()
                        val runtimeData = ServiceClient.clash().queryRuntimeDataSnapshot()
                        runtimeConfiguration = runtimeData.configuration
                        val rawGroups = runtimeData.proxyGroups
                        if (captureObservedGroupNames.isNotEmpty()) {
                            latencyObservations.bind(latencyScopeKey)
                            rawGroups
                                .asSequence()
                                .filter { it.name in captureObservedGroupNames }
                                .forEach { latencyObservations.recordObservedProxies(it.proxies) }
                        }
                        val groups = normalizeProxyGroups(rawGroups)
                        latencyObservations.bind(latencyScopeKey)
                        latencyObservations.merge(groups)
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

            val latestProfile = currentProfile.value
            if (
                runtimeSnapshot.value.generation != snapshot.generation ||
                    latestProfile?.uuid != profileAtRequest?.uuid ||
                    latestProfile?.updatedAt != profileAtRequest?.updatedAt ||
                    runtimeState.currentPreviewEpoch() != previewEpochAtRequest
            ) {
                return
            }

            when {
                groups != null && groups.isNotEmpty() -> {
                    runtimeState.setProxyGroups(groups)
                    runtimeState.updateGroupsReady(true)
                    withContext(Dispatchers.IO) {
                        previewCache.backfill(
                            profile = currentProfile.value,
                            groups = groups,
                            runtimeSnapshot = runtimeSnapshot.value,
                            rootTunStatus = rootTunStatus.value,
                        )
                    }
                    runtimeConfiguration?.let { configuration ->
                        scheduleProxyGroupMetadataEnrichment(groups, configuration)
                    }
                }

                groups != null &&
                    groups.isEmpty() &&
                    (snapshot.phase == RuntimePhase.Idle ||
                        snapshot.phase == RuntimePhase.Failed) -> {
                    runtimeState.setProxyGroups(emptyList())
                    runtimeState.updateGroupsReady(false)
                }

                else -> {
                    val cached =
                        previewCache.fallback(
                            snapshot = snapshot,
                            profile = currentProfile.value,
                            rootTunStatus = rootTunStatus.value,
                        )
                    if (!cached.isNullOrEmpty()) {
                        runtimeState.setProxyGroups(cached)
                        runtimeState.markProxyGroupsLoading()
                    } else {
                        runtimeState.markProxyGroupsError()
                    }
                }
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
                    connectCurrentBackend()
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

    suspend fun refreshAll(includeTraffic: Boolean = true) {
        refreshCurrentProfile()
        restoreCachedPreviewGroups()
        refreshProxyGroups()
        if (includeTraffic && runtimeSnapshot.value.phase == RuntimePhase.Running) {
            queryTrafficSnapshot()
        } else if (includeTraffic) {
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
            synchronized(runtimeTransitionLock) {
                detectActiveOwner().takeIf { it != RuntimeOwner.None }
                    ?: runtimeSnapshot.value.owner
            }
        val generation = synchronized(runtimeTransitionLock) { runtimeState.nextGeneration() }

        if (owner == RuntimeOwner.None) {
            // The in-memory status may be stale while a local service process is
            // still alive (e.g. the service was recreated without marking itself
            // started). Ask the leftover service to stop before declaring Idle;
            // otherwise the toggle turns off while the VPN keeps running.
            if (!drainStaleLocalServiceIfPresent()) {
                throw RuntimeGatewayException(
                    code = RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED,
                    message =
                        "Timed out stopping a leftover local runtime " +
                            "(tunRunning=${isServiceRunning(TunService::class.java)}, " +
                            "httpRunning=${isServiceRunning(ClashService::class.java)})",
                )
            }
            transitionToIdle(configuredMode = targetMode, generation = generation, lastError = null)
            return
        }

        val preStopSnapshot = runtimeSnapshot.value
        synchronized(runtimeTransitionLock) {
            publishRuntimeSnapshot(
                preStopSnapshot.copy(
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
        }

        triggerStop(owner)

        when (awaitStopTerminal(owner)) {
            StopTerminalOutcome.ReceiptObserved -> {
                finalizeStopIfSnapshotNotTerminal(owner)
                scheduleLocalStopDrain(owner)
            }

            StopTerminalOutcome.ReconciledStaleMarker -> {
                Timber.w("Stop receipt missing; reconciled stale stop marker for ${owner.name}")
                reconcileStaleOwnerState(owner)
                finalizeStopIfSnapshotNotTerminal(owner)
                scheduleLocalStopDrain(owner)
            }

            StopTerminalOutcome.TimedOut -> {
                val consistency = collectStopSignalConsistency(owner)
                if (!handleStopTimeout(owner, preStopSnapshot)) {
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
    }

    private fun scheduleLocalStopDrain(owner: RuntimeOwner) {
        if (owner != RuntimeOwner.LocalTun && owner != RuntimeOwner.LocalHttp) return
        if (isOwnerProcessStopped(owner)) {
            localStopDrain = null
            return
        }

        localStopDrain?.cancel()
        localStopDrain =
            scope.async(Dispatchers.Default) {
                repeat(STOP_WAIT_RETRY_COUNT) {
                    if (isOwnerProcessStopped(owner)) return@async true
                    delay(STOP_WAIT_RETRY_DELAY_MS.milliseconds)
                }
                if (isOwnerProcessStopped(owner)) return@async true
                // The service is still alive after the grace window: force-close it
                // so the VPN cannot stay up behind an already-idle toggle.
                Timber.w("Local stop drain timed out; force-closing ${owner.name}")
                forceCloseLocalRuntime(owner)
                repeat(STOP_FORCE_CLOSE_RETRY_COUNT) {
                    if (isOwnerProcessStopped(owner)) return@async true
                    delay(STOP_WAIT_RETRY_DELAY_MS.milliseconds)
                }
                isOwnerProcessStopped(owner)
            }
    }

    private suspend fun awaitPendingLocalStopDrain() {
        val drain = localStopDrain ?: return
        localStopDrain = null
        if (!drain.await()) {
            throw RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                message = "Previous local runtime is still stopping",
            )
        }
    }

    private fun startTrafficPolling() {
        trafficPoller.start()
    }

    private fun stopTrafficPolling() {
        trafficPoller.stop()
    }

    /**
     * Wakes the traffic poller out of a long background delay when the app returns to the
     * foreground or the screen turns on, so refreshes resume immediately.
     */
    private fun kickTrafficPoller() {
        trafficPoller.kick()
    }

    override fun close() {
        cancelRuntimeReconcile()
        previewWarmupJob?.cancel()
        previewWarmupJob = null
        localStopDrain?.cancel()
        localStopDrain = null
        stopTrafficPolling()
        appForegroundObserver.stop()
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
            val expectedOwner = expectedLocalOwnerFromPersistedState()
            if (expectedOwner != RuntimeOwner.None) {
                // The service process may have been killed overnight. The persisted
                // marker says a local runtime should be running, but the START_STICKY
                // recreation may not have landed yet. Show an honest reconciling state
                // and wait briefly instead of declaring the VPN off while the system
                // is about to bring it back up.
                runtimeState.clearRuntimePayload(resetGroups = false)
                publishRuntimeSnapshot(
                    RuntimeSnapshot(
                        owner = expectedOwner,
                        phase = RuntimePhase.Starting,
                        targetMode =
                            ProxyFacadeOwnerPolicy.modeForOwner(
                                expectedOwner,
                                configuredMode,
                            ),
                        generation = runtimeState.nextGeneration(),
                    )
                )
                scope.launch { refreshPreviewStateSafely() }
                runtimeReconcileJob = scope.launch { reconcileExpectedLocalRuntime(expectedOwner) }
                return
            }
            runtimeState.clearRuntimePayload(resetGroups = false)
            publishRuntimeSnapshot(RuntimeStateMapper.idleSnapshot(configuredMode))
            scope.launch { refreshPreviewStateSafely() }
            return
        }

        val initialPhase =
            if (owner == RuntimeOwner.RootTun) rootPhase(rootStatus) else RuntimePhase.Running
        publishRuntimeSnapshot(
            RuntimeSnapshot(
                owner = owner,
                phase = initialPhase,
                targetMode =
                    ProxyFacadeOwnerPolicy.modeForOwner(
                        owner,
                        networkSettingsStorage.proxyMode.value,
                    ),
                profileReady =
                    owner == RuntimeOwner.RootTun && !rootStatus.profileUuid.isNullOrBlank(),
                profileUuid = rootStatus.profileUuid.takeIf { owner == RuntimeOwner.RootTun },
                profileName = rootStatus.profileName.takeIf { owner == RuntimeOwner.RootTun },
                configReady = initialPhase == RuntimePhase.Running,
                transportReady = initialPhase == RuntimePhase.Running,
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

    private fun expectedLocalOwnerFromPersistedState(): RuntimeOwner {
        return ProxyFacadeOwnerPolicy.expectedOwnerForPersistedMode(
            StatusProvider.persistedRuntimeMode()
        )
    }

    /**
     * Waits a bounded window for a sticky-restarted local service to appear after the process was
     * recreated. If it comes back, the snapshot is advanced to Running (the later
     * started/profileLoaded broadcasts refine it further without downgrading it). If it never comes
     * back, the stale persisted marker is dropped and the snapshot is finalized to Idle so the UI
     * never shows a phantom VPN.
     */
    private suspend fun reconcileExpectedLocalRuntime(owner: RuntimeOwner) {
        val serviceClass =
            when (owner) {
                RuntimeOwner.LocalTun -> TunService::class.java
                RuntimeOwner.LocalHttp -> ClashService::class.java
                else -> return
            }
        val expectedMode =
            ProxyFacadeOwnerPolicy.modeForOwner(owner, networkSettingsStorage.proxyMode.value)

        repeat(LOCAL_RECONCILE_RETRY_COUNT) {
            if (isLocalServiceLive(serviceClass, expectedMode)) {
                val advanced =
                    synchronized(runtimeTransitionLock) {
                        val snapshot = runtimeSnapshot.value
                        // The started/profileLoaded broadcasts may already have advanced the
                        // snapshot to Running; never downgrade it back to Starting.
                        if (snapshot.owner == owner && snapshot.phase == RuntimePhase.Starting) {
                            publishRuntimeSnapshot(
                                snapshot.copy(
                                    phase = RuntimePhase.Running,
                                    configReady = true,
                                    transportReady = true,
                                    generation = runtimeState.nextGeneration(),
                                )
                            )
                            true
                        } else {
                            false
                        }
                    }
                if (advanced) {
                    startTrafficPolling()
                    scope.launch { refreshAllSafely() }
                }
                return
            }
            delay(LOCAL_RECONCILE_RETRY_DELAY_MS.milliseconds)
        }

        synchronized(runtimeTransitionLock) {
            StatusProvider.markRuntimeStopped(expectedMode)
            val snapshot = runtimeSnapshot.value
            if (snapshot.owner == owner && snapshot.phase == RuntimePhase.Starting) {
                transitionToIdle(
                    configuredMode = networkSettingsStorage.proxyMode.value,
                    generation = runtimeState.nextGeneration(),
                    lastError = null,
                )
            }
        }
    }

    private fun cancelRuntimeReconcile() {
        runtimeReconcileJob?.cancel()
        runtimeReconcileJob = null
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
        val owner =
            synchronized(runtimeTransitionLock) {
                val currentSnapshot = runtimeSnapshot.value
                val resolvedOwner =
                    RuntimeTransitionPolicy.resolveStartedOwner(
                        forceOwner = forceOwner,
                        currentOwner = currentSnapshot.owner,
                        detectedOwner = detectActiveOwner(),
                    )
                if (resolvedOwner == RuntimeOwner.None) {
                    return@synchronized RuntimeOwner.None
                }

                val started =
                    RuntimeTransitionPolicy.startedSnapshot(
                        currentSnapshot = currentSnapshot,
                        owner = resolvedOwner,
                        targetMode =
                            ProxyFacadeOwnerPolicy.modeForOwner(
                                resolvedOwner,
                                networkSettingsStorage.proxyMode.value,
                            ),
                    )
                // clashStarted only means that the process/TUN exists. Keep the client
                // in Starting until the service publishes its profile-loaded boundary
                // after config loading and transport establishment. Never downgrade a
                // session that is already Running: the startup reconcile may have
                // advanced it before this broadcast was delivered.
                publishRuntimeSnapshot(
                    started.copy(
                        phase =
                            if (currentSnapshot.phase == RuntimePhase.Running) {
                                RuntimePhase.Running
                            } else {
                                RuntimePhase.Starting
                            }
                    )
                )
                resolvedOwner
            }
        if (owner == RuntimeOwner.None) return
        startTrafficPolling()
        // Payload refresh starts independently after this boundary; it is not
        // part of the local start receipt.
    }

    private suspend fun handleRuntimeProfileLoaded(profileUuid: String?) {
        val shouldRefresh =
            synchronized(runtimeTransitionLock) {
                val snapshot = runtimeSnapshot.value
                val profileMatches =
                    profileUuid.isNullOrBlank() ||
                        snapshot.profileUuid.isNullOrBlank() ||
                        snapshot.profileUuid == profileUuid
                if (!profileMatches) {
                    Timber.d(
                        "Ignoring profileLoaded for stale profile=%s current=%s",
                        profileUuid,
                        snapshot.profileUuid,
                    )
                    false
                } else if (
                    snapshot.phase == RuntimePhase.Starting ||
                        snapshot.phase == RuntimePhase.Running
                ) {
                    // A profileLoaded callback is only allowed to advance a session
                    // that is still alive. This closes the stop/profileLoaded race
                    // where an old callback could resurrect Idle as Running.
                    val ownerActive =
                        snapshot.owner != RuntimeOwner.None && isOwnerActive(snapshot.owner)
                    if (!ownerActive) {
                        val generation = runtimeState.nextGeneration()
                        transitionToIdle(
                            configuredMode = networkSettingsStorage.proxyMode.value,
                            generation = generation,
                            lastError = null,
                        )
                        false
                    } else {
                        publishRuntimeSnapshot(
                            snapshot.copy(
                                phase = RuntimePhase.Running,
                                profileReady = true,
                                profileUuid = profileUuid ?: snapshot.profileUuid,
                                configReady = true,
                                transportReady = true,
                            )
                        )
                        true
                    }
                } else {
                    false
                }
            }
        // The service has already crossed the transport/config boundary. Do
        // not make the start receipt wait for the first full payload query;
        // refresh it independently and let groupsReady/trafficReady advance
        // when each piece arrives.
        if (shouldRefresh) {
            scope.launch { refreshAllSafely() }
        }
    }

    private suspend fun handleRuntimeStopped(reason: String?) {
        synchronized(runtimeTransitionLock) {
            when (resolveRuntimeStopResolution(runtimeSnapshot.value.phase, reason)) {
                RuntimeStopResolution.IgnoreAsStale -> {
                    // A stop without a reason can be an old broadcast delivered
                    // after a new start entered Starting. Keep the existing policy,
                    // but do not leave a genuinely dead owner stuck in Starting.
                    val owner = runtimeSnapshot.value.owner
                    if (owner == RuntimeOwner.None || isOwnerActive(owner)) {
                        Timber.d("handleRuntimeStopped: ignoring stale stop event (phase=Starting)")
                        return@synchronized
                    }
                    Timber.w(
                        "handleRuntimeStopped: owner is inactive; reconciling Starting to Idle"
                    )
                }

                RuntimeStopResolution.SkipAsRedundant -> {
                    Timber.d("handleRuntimeStopped: skipping redundant idle transition")
                    return@synchronized
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
    }

    private fun handleRuntimeFailure(error: String?, errorCodeRaw: String? = null) {
        val generation = synchronized(runtimeTransitionLock) { runtimeState.nextGeneration() }
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

    /**
     * Surfaces an in-place access-control re-establish failure from the running service. The
     * session itself is left untouched (the service stays up), so unlike [handleRuntimeFailure]
     * this never transitions the snapshot to Idle.
     */
    private fun handleAccessControlApplyFailed(reason: String) {
        Timber.w("Access control apply failed: $reason")
        _runtimeFailureEvents.tryEmit(
            RuntimeFailureEvent(reason, networkSettingsStorage.proxyMode.value)
        )
    }

    private fun transitionToIdle(configuredMode: ProxyMode, generation: Long, lastError: String?) {
        synchronized(runtimeTransitionLock) {
            latencyObservations.clear()
            runtimeState.clearRuntimePayload(resetGroups = false)
            publishRuntimeSnapshot(
                RuntimeStateMapper.idleSnapshot(
                    configuredMode = configuredMode,
                    generation = generation,
                    lastError = lastError,
                )
            )
        }
        stopTrafficPolling()
        scope.launch { refreshPreviewStateSafely() }
    }

    private suspend fun refreshAllSafely(includeTraffic: Boolean = true) {
        if (
            runtimeSnapshot.value.phase != RuntimePhase.Running &&
                runtimeSnapshot.value.phase != RuntimePhase.Starting
        ) {
            return
        }
        runCatching { refreshAll(includeTraffic = includeTraffic) }
            .onFailure { error -> Timber.d(error, "Refresh runtime data skipped") }
    }

    private suspend fun refreshPreviewStateSafely() {
        runCatching {
                refreshCurrentProfile()
                restoreCachedPreviewGroups()
                refreshProxyGroups()
            }
            .onFailure { error -> Timber.d(error, "Refresh preview data skipped") }
    }

    private suspend fun restoreCachedPreviewGroups() {
        val requestSnapshot = runtimeSnapshot.value
        val profile = currentProfile.value ?: return
        val previewEpochAtRequest = runtimeState.currentPreviewEpoch()
        if (proxyGroups.value.isNotEmpty()) return
        val cached =
            withContext(Dispatchers.IO) {
                previewCache.restore(
                    profile = profile,
                    runtimeSnapshot = requestSnapshot,
                    rootTunStatus = rootTunStatus.value,
                )
            } ?: return
        if (
            runtimeSnapshot.value.generation == requestSnapshot.generation &&
                currentProfile.value?.uuid == profile.uuid &&
                currentProfile.value?.updatedAt == profile.updatedAt &&
                runtimeState.currentPreviewEpoch() == previewEpochAtRequest &&
                proxyGroups.value.isEmpty()
        ) {
            runtimeState.setProxyGroups(cached)
            // The cache is a usable preview, but the runtime payload is still
            // pending. Keep the load state stale until the live query succeeds.
            runtimeState.markProxyGroupsLoading()
        }
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
                MMKV.mmkvWithID(StoreIds.RUNTIME_SNAPSHOT, MMKV.MULTI_PROCESS_MODE).clearAll()
            }
        }
    }

    private fun applyRootTunStatus(status: RootTunStatus) {
        runtimeState.applyRootTunStatus(status)
    }

    private fun publishRuntimeSnapshot(snapshot: RuntimeSnapshot) {
        synchronized(runtimeTransitionLock) { runtimeState.publishRuntimeSnapshot(snapshot) }
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
            if (isStopTerminal(owner, consistency)) {
                return StopTerminalOutcome.ReceiptObserved
            }
            if (consistency.staleMarkerDetected) {
                return StopTerminalOutcome.ReconciledStaleMarker
            }
            delay(STOP_WAIT_RETRY_DELAY_MS.milliseconds)
        }

        val consistency = collectStopSignalConsistency(owner)
        if (isStopTerminal(owner, consistency)) return StopTerminalOutcome.ReceiptObserved
        if (consistency.staleMarkerDetected) {
            return StopTerminalOutcome.ReconciledStaleMarker
        }
        return StopTerminalOutcome.TimedOut
    }

    /**
     * Re-requests the stop and waits a short grace window. A missed stop receipt is often caused by
     * a slow service-side teardown (Doze, a busy main looper, a wedged native call) rather than the
     * runtime actually stopping. If the owner is still alive afterwards, restore the last Running
     * snapshot so the UI never claims the VPN is off while it is still running.
     *
     * Returns true when the runtime reached a terminal state during the grace window; false when
     * the owner is still alive and the caller must surface a stop failure.
     */
    private suspend fun handleStopTimeout(
        owner: RuntimeOwner,
        preStopSnapshot: RuntimeSnapshot,
    ): Boolean {
        runCatching { triggerStop(owner) }
        repeat(STOP_TIMEOUT_GRACE_RETRY_COUNT) {
            val consistency = collectStopSignalConsistency(owner)
            if (isStopTerminal(owner, consistency)) {
                finalizeStopIfSnapshotNotTerminal(owner)
                scheduleLocalStopDrain(owner)
                return true
            }
            delay(STOP_TIMEOUT_GRACE_RETRY_DELAY_MS.milliseconds)
        }

        // The service-side teardown is wedged (stuck reload/start, busy main
        // looper, or a native call that never returned). Force-close the native
        // tunnel directly from this process and destroy the service, then give
        // it a short window to reach a terminal state. Closing the TUN fd makes
        // Android tear the VPN down even if SessionRuntime is stuck.
        if (isOwnerActive(owner)) {
            forceCloseLocalRuntime(owner)
            repeat(STOP_FORCE_CLOSE_RETRY_COUNT) {
                val consistency = collectStopSignalConsistency(owner)
                if (isStopTerminal(owner, consistency)) {
                    finalizeStopIfSnapshotNotTerminal(owner)
                    scheduleLocalStopDrain(owner)
                    return true
                }
                delay(STOP_TIMEOUT_GRACE_RETRY_DELAY_MS.milliseconds)
            }
        }

        if (isOwnerActive(owner)) {
            synchronized(runtimeTransitionLock) {
                val snapshot = runtimeSnapshot.value
                if (snapshot.phase == RuntimePhase.Stopping) {
                    publishRuntimeSnapshot(
                        preStopSnapshot.copy(
                            owner = owner,
                            phase = RuntimePhase.Running,
                            generation = runtimeState.nextGeneration(),
                            lastError = null,
                        )
                    )
                }
            }
            Timber.w("Stop timeout: ${owner.name} is still active after forced close")
            return false
        } else {
            finalizeStopIfSnapshotNotTerminal(owner)
            scheduleLocalStopDrain(owner)
            return true
        }
    }

    /**
     * Last-resort forced close: closes the native tunnel/listener from this process (the local
     * runtime shares the process, so [Clash] calls here are effective and bounded) and then
     * destroys the runtime service. Used only after the normal stop request and a grace window both
     * failed to tear the VPN down.
     */
    private suspend fun forceCloseLocalRuntime(owner: RuntimeOwner) {
        withContext(Dispatchers.IO) {
            when (owner) {
                RuntimeOwner.LocalTun -> {
                    runCatching { Clash.stopTun() }
                    runCatching { Clash.stopLocalProxyHttpListener() }
                    runCatching { Clash.reset() }
                    runCatching {
                        appContext.stopService(Intent(appContext, TunService::class.java))
                    }
                }

                RuntimeOwner.LocalHttp -> {
                    runCatching { Clash.stopLocalProxyHttpListener() }
                    runCatching { Clash.stopTun() }
                    runCatching { Clash.reset() }
                    runCatching {
                        appContext.stopService(Intent(appContext, ClashService::class.java))
                    }
                }

                RuntimeOwner.RootTun -> {
                    // The root runtime lives in its own process; re-requesting
                    // the stop through the binder is the only local lever.
                    runCatching { triggerStop(RuntimeOwner.RootTun) }
                }

                RuntimeOwner.None -> Unit
            }
        }
    }

    /**
     * When the in-memory status says no owner but a local service process is still alive, the
     * client and service are desynced (e.g. the service was recreated without marking itself
     * started). The persisted marker is consulted too, because a sticky-restarted service may not
     * be visible in the process list yet when the app process was recreated. Asking the leftover
     * service to stop prevents a silent no-op that would leave the VPN running behind an "off"
     * toggle. Returns true when no local service remains alive.
     */
    private suspend fun drainStaleLocalServiceIfPresent(): Boolean {
        val tunAlive = isServiceRunning(TunService::class.java)
        val httpAlive = isServiceRunning(ClashService::class.java)
        val persistedMode = StatusProvider.persistedRuntimeMode()
        val staleOwner =
            when {
                tunAlive -> RuntimeOwner.LocalTun
                httpAlive -> RuntimeOwner.LocalHttp
                persistedMode == ProxyMode.Tun -> RuntimeOwner.LocalTun
                persistedMode == ProxyMode.Http -> RuntimeOwner.LocalHttp
                else -> return true
            }
        val staleMode =
            ProxyFacadeOwnerPolicy.modeForOwner(
                staleOwner,
                networkSettingsStorage.proxyMode.value,
            )
        val serviceClass =
            if (staleOwner == RuntimeOwner.LocalTun) {
                TunService::class.java
            } else {
                ClashService::class.java
            }

        if (!isLocalServiceLive(serviceClass, staleMode)) {
            // The service may still be pending its START_STICKY recreation. Wait for
            // it to appear so the VPN cannot come back up behind an "off" toggle.
            var appeared = false
            repeat(LOCAL_RECONCILE_RETRY_COUNT) {
                if (isLocalServiceLive(serviceClass, staleMode)) {
                    appeared = true
                    return@repeat
                }
                delay(LOCAL_RECONCILE_RETRY_DELAY_MS.milliseconds)
            }
            if (!appeared) {
                // Nothing is (or will be) running: drop the stale marker and leave.
                StatusProvider.markRuntimeStopped(staleMode)
                StatusProvider.clearPersistedRuntimeMode()
                return true
            }
        }

        runCatching { triggerStop(staleOwner) }
        repeat(STOP_WAIT_RETRY_COUNT) {
            if (isOwnerProcessStopped(staleOwner)) {
                StatusProvider.markRuntimeStopped(staleMode)
                StatusProvider.clearPersistedRuntimeMode()
                return true
            }
            delay(STOP_WAIT_RETRY_DELAY_MS.milliseconds)
        }
        if (isOwnerProcessStopped(staleOwner)) {
            StatusProvider.markRuntimeStopped(staleMode)
            StatusProvider.clearPersistedRuntimeMode()
            return true
        }
        // The service is wedged: force-close so the VPN cannot survive the stop.
        forceCloseLocalRuntime(staleOwner)
        repeat(STOP_FORCE_CLOSE_RETRY_COUNT) {
            if (isOwnerProcessStopped(staleOwner)) {
                StatusProvider.markRuntimeStopped(staleMode)
                StatusProvider.clearPersistedRuntimeMode()
                return true
            }
            delay(STOP_WAIT_RETRY_DELAY_MS.milliseconds)
        }
        return isOwnerProcessStopped(staleOwner)
    }

    /**
     * Finalizes a failed stop to Idle only when the runtime genuinely went inactive. When the owner
     * or a leftover local service process is still alive the snapshot is kept honest
     * (Running/Stopping) so the UI does not claim the VPN is off while it is still running.
     */
    private fun reconcileStopFailure(targetMode: ProxyMode, generation: Long, lastError: String?) {
        val owner = runtimeSnapshot.value.owner
        val stillActive =
            (owner != RuntimeOwner.None && isOwnerActive(owner)) ||
                isServiceRunning(TunService::class.java) ||
                isServiceRunning(ClashService::class.java)
        synchronized(runtimeTransitionLock) {
            val snapshot = runtimeSnapshot.value
            if (snapshot.phase == RuntimePhase.Idle || snapshot.phase == RuntimePhase.Failed) {
                return
            }
            if (stillActive) {
                return
            }
            transitionToIdle(
                configuredMode = targetMode,
                generation = generation,
                lastError = lastError,
            )
        }
    }

    private fun isStopTerminal(owner: RuntimeOwner, consistency: StopSignalConsistency): Boolean {
        if (consistency.snapshotTerminal) return true

        return when (owner) {
            // Local services mark StatusProvider stopped only after their
            // SessionRuntime has completed the stop/destroy path. The Android
            // service entry can remain visible briefly while onDestroy and
            // foreground-service teardown finish, so waiting for the process
            // list as well can produce a false stop timeout.
            RuntimeOwner.LocalTun,
            RuntimeOwner.LocalHttp -> consistency.statusStoreStopped
            RuntimeOwner.RootTun -> consistency.statusStoreStopped && consistency.processStopped
            RuntimeOwner.None -> true
        }
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
                "Start receipt missing; accepting active local runtime for ${owner.name} " +
                    "(mode=${mode.name}) and waiting for profileLoaded"
            )
            handleRuntimeStarted(forceOwner = owner)
            // The service sends profileLoaded after configuration and
            // transport establishment; payload refresh is asynchronous.
            return
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

    /**
     * The service process list (getRunningServices) is unreliable on some OEMs, so a live runtime
     * is also detected through the in-process StatusProvider marker that the service sets in
     * onCreate. The marker is authoritative and immediately visible within this same process.
     */
    private fun isLocalServiceLive(serviceClass: Class<*>, mode: ProxyMode): Boolean {
        return isServiceRunning(serviceClass) || StatusProvider.isRuntimeActive(mode)
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
        connectCurrentBackend()
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
                    normalized.add(proxies[head])
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
                    enriched.add(groups[head])
                    head += 1
                }
            }
            enriched?.add(nextGroup)
        }
        return enriched ?: groups
    }

    private fun scheduleProxyGroupMetadataEnrichment(
        baseGroups: List<ProxyGroupInfo>,
        configuration: UiConfiguration,
    ) {
        val startSnapshot = runtimeSnapshot.value
        val profileAtStart = currentProfile.value
        scope.launch(Dispatchers.IO) {
            val enriched = enrichProxyGroupsFromController(baseGroups, configuration)
            val latestSnapshot = runtimeSnapshot.value
            if (
                latestSnapshot.generation != startSnapshot.generation ||
                    proxyGroups.value != baseGroups ||
                    latestSnapshot.phase == RuntimePhase.Idle ||
                    latestSnapshot.phase == RuntimePhase.Failed ||
                    latestSnapshot.phase == RuntimePhase.Stopping ||
                    currentProfile.value?.uuid != profileAtStart?.uuid ||
                    currentProfile.value?.updatedAt != profileAtStart?.updatedAt
            ) {
                return@launch
            }

            runtimeState.setProxyGroups(enriched)
            withContext(Dispatchers.IO) {
                previewCache.backfill(
                    profile = currentProfile.value,
                    groups = enriched,
                    runtimeSnapshot = latestSnapshot,
                    rootTunStatus = rootTunStatus.value,
                )
            }
        }
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
        private const val ACTIVE_POLL_MS = 2_000L
        private const val BACKGROUND_POLL_MS = 60_000L
        private const val PAYLOAD_REFRESH_INTERVAL_MS = 4_000L
        private const val PROXY_PAGE_PAYLOAD_REFRESH_INTERVAL_MS = 2_000L
        private const val START_WAIT_RETRY_COUNT = 80
        private const val START_WAIT_RETRY_DELAY_MS = 125L
        private const val STOP_WAIT_RETRY_COUNT = 80
        private const val STOP_WAIT_RETRY_DELAY_MS = 125L
        private const val STOP_TIMEOUT_GRACE_RETRY_COUNT = 20
        private const val STOP_TIMEOUT_GRACE_RETRY_DELAY_MS = 125L
        private const val STOP_FORCE_CLOSE_RETRY_COUNT = 20
        private const val LOCAL_RECONCILE_RETRY_COUNT = 40
        private const val LOCAL_RECONCILE_RETRY_DELAY_MS = 125L
    }
}

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

package com.github.nomadboxlab.monadbox.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.github.nomadboxlab.monadbox.core.model.LogMessage
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.common.log.Log
import com.github.nomadboxlab.monadbox.service.common.util.CoreRuntimeConfig
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.common.util.initializeServiceGlobal
import com.github.nomadboxlab.monadbox.service.notification.ServiceNotificationManager
import com.github.nomadboxlab.monadbox.service.runtime.session.*
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
import com.github.nomadboxlab.monadbox.service.runtime.util.cancelAndJoinBlocking
import com.github.nomadboxlab.monadbox.service.runtime.util.sendAccessControlApplyFailed
import com.github.nomadboxlab.monadbox.service.runtime.util.sendClashStarted
import com.github.nomadboxlab.monadbox.service.runtime.util.sendClashStopped
import com.github.nomadboxlab.monadbox.service.runtime.util.sendProfileLoaded
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TunService : VpnService(), CoroutineScope {
    private val serviceJob = SupervisorJob()

    override val coroutineContext: CoroutineContext = Dispatchers.Default + serviceJob

    private var reason: String? = null
    private val notificationManager by lazy {
        ServiceNotificationManager(this, ServiceNotificationManager.VPN_CONFIG)
    }
    private val startupLogStore by lazy {
        RuntimeStartupLogStore(this, RuntimeStartupLogStore.Scope.LOCAL_TUN)
    }
    private var notificationJob: Job? = null
    private lateinit var runtime: SessionRuntime
    private var reloadJob: Job? = null
    @Volatile private var reloadRequested = false
    private var accessControlJob: Job? = null

    private val runtimeEventsReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action ?: return) {
                    Intents.ACTION_PROFILE_CHANGED,
                    Intents.ACTION_OVERRIDE_CHANGED -> scheduleReload()
                    Intents.ACTION_ACCESS_CONTROL_CHANGED -> scheduleAccessControlApply()
                    Intents.ACTION_CLASH_REQUEST_STOP -> {
                        reason = intent.getStringExtra(Intents.EXTRA_STOP_REASON)
                        startupLogStore.append(
                            "LOCAL_TUN stop request received reason=${reason ?: "manual"}"
                        )
                        launch {
                            try {
                                val stopResult =
                                    if (this@TunService::runtime.isInitialized) {
                                        runtime.stop(reason)
                                    } else {
                                        RuntimeOperationResult.ok()
                                    }
                                if (!stopResult.success) {
                                    val failure =
                                        stopResult.toException(
                                            defaultCode =
                                                RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED,
                                            defaultMessage = "tun runtime stop failed",
                                        )
                                    reason = failure.runtimeGatewayMessage("tun runtime stop failed")
                                    startupLogStore.append(
                                        "LOCAL_TUN failed=${failure.code.name}:${failure.message}"
                                    )
                                } else {
                                    startupLogStore.append("LOCAL_TUN stop request handled")
                                }
                            } finally {
                                // Always tear the service down, even if the runtime stop
                                // threw; otherwise the VPN session survives behind an
                                // "off" toggle and the system keeps showing the VPN icon.
                                stopSelf()
                            }
                        }
                    }
                }
            }
        }

    override fun onCreate() {
        super.onCreate()
        runCatching {
                initializeServiceGlobal(appContextOrSelf)
                startupLogStore.append("LOCAL_TUN service: onCreate begin")

                notificationManager.createChannel()
                startForeground(
                    ServiceNotificationManager.VPN_CONFIG.notificationId,
                    notificationManager.createInitialNotification(),
                )
                startupLogStore.append("LOCAL_TUN service: startForeground done")

                StatusProvider.clearLegacyStateFiles()
                StatusProvider.markRuntimeStarted(ProxyMode.Tun)
                CoreRuntimeConfig.applyCustomUserAgentIfPresent(this)

                runtime =
                    SessionRuntime(
                        host =
                            object : RuntimeHost {
                                override val context = this@TunService
                                override val mode: ProxyMode = ProxyMode.Tun

                                override fun onStarting(spec: RuntimeSpec) = Unit

                                override fun onStarted(spec: RuntimeSpec) {
                                    StatusProvider.markRuntimeStarted(ProxyMode.Tun)
                                    sendClashStarted()
                                }

                                override fun onStopped(reason: String?) {
                                    this@TunService.reason = reason
                                    StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                                    sendClashStopped(reason)
                                }

                                override fun onProfileLoaded(profileUuid: String) {
                                    sendProfileLoaded(UUID.fromString(profileUuid))
                                }

                                override fun onSnapshotChanged(snapshot: RuntimeSnapshot) = Unit

                                override fun onLogReady(ready: Boolean) = Unit

                                override fun onLogItem(log: LogMessage) = Unit

                                override fun reportFailure(error: RuntimeFailure) {
                                    reason = "${error.code.name}: ${error.message}"
                                    startupLogStore.append(
                                        "LOCAL_TUN failed=${error.code.name}:${error.message}"
                                    )
                                    StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                                    sendClashStopped(reason)
                                    Log.e("Tun runtime failed: ${error.code.name} ${error.message}")
                                    stopSelf()
                                }
                            },
                        transport = VpnTunTransport(this),
                        scope = this,
                    )

                registerRuntimeReceiver()
                startupLogStore.append("LOCAL_TUN service: receiver registered")
                launch {
                    runCatching {
                            startupLogStore.append("LOCAL_TUN spec: create begin")
                            val spec = SessionRuntimeSpecFactory(appContextOrSelf).createTunSpec()
                            startupLogStore.append(
                                "LOCAL_TUN spec: create done profile=${spec.profileUuid} overrides=${spec.overridePaths.size}"
                            )
                            val result = runtime.start(spec)
                            check(result.success) {
                                result
                                    .toException(
                                        defaultCode = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                                        defaultMessage = "tun runtime start failed",
                                    )
                                    .runtimeGatewayMessage("tun runtime start failed")
                            }
                        }
                        .onFailure { error ->
                            reason = error.runtimeGatewayMessage("tun runtime start failed")
                            startupLogStore.append("LOCAL_TUN failed=$reason")
                            StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                            sendClashStopped(reason)
                            stopSelf()
                        }
                }
            }
            .onFailure { error ->
                reason = error.runtimeGatewayMessage("tun runtime start failed")
                startupLogStore.append("LOCAL_TUN failed=$reason")
                StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                sendClashStopped(reason)
                stopSelf()
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (notificationJob?.isActive != true) {
            notificationJob = notificationManager.startTrafficUpdate(this)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(runtimeEventsReceiver) }
        reloadJob?.cancel()
        reloadJob = null
        accessControlJob?.cancel()
        accessControlJob = null
        notificationManager.stopTrafficUpdate()
        notificationJob = null

        if (this::runtime.isInitialized) {
            runtime.destroy()
        }

        // Always close the native tunnel/listener directly on destruction. SessionRuntime's
        // stopInternal early-returns when its phase is already Idle, and in that desync case
        // (e.g. a system-restarted service whose in-memory state was already reset) the TUN fd
        // would otherwise stay open in this still-alive process, keeping the VPN up behind an
        // already-"off" toggle while the status bar still shows the VPN icon.
        runCatching { com.github.nomadboxlab.monadbox.core.Clash.stopTun() }
        runCatching { com.github.nomadboxlab.monadbox.core.Clash.stopLocalProxyHttpListener() }

        StatusProvider.markRuntimeStopped(ProxyMode.Tun)
        sendClashStopped(reason)
        startupLogStore.append("LOCAL_TUN destroy")
        Log.i("TunService destroyed: ${reason ?: "successfully"}")

        cancelAndJoinBlocking()
        super.onDestroy()
    }

    // TRIM_MEMORY_RUNNING_CRITICAL is deprecated in API 36 but its value is stable.
    @Suppress("DEPRECATION")
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // RUNNING_* low-level trims fire frequently on a foreground service; only force
        // native GC once pressure is critical or the process is a kill candidate.
        if (level >= TRIM_MEMORY_RUNNING_CRITICAL) {
            com.github.nomadboxlab.monadbox.core.Clash.forceGc()
        }
    }

    private fun registerRuntimeReceiver() {
        registerRuntimeEventsReceiver(runtimeEventsReceiver)
    }

    /**
     * Coalesces profile/override change requests into a single in-flight reload.
     *
     * A single save already emits several change broadcasts back to back (PROFILE_CHANGED from the
     * save itself, OVERRIDE_CHANGED from the override reapply, and PROFILE_CHANGED again from the
     * client-side active-profile reload). The previous implementation cancelled the in-flight
     * reload on every broadcast, which could abort a reload that was already recompiling (and, in
     * the worst case, tear the service down) or silently drop the latest config. Instead, a
     * pending request just marks the flag; the running loop re-reads the latest committed state and
     * reloads again once the current one finishes, so the new config always reaches the core.
     */
    private fun scheduleReload() {
        if (reloadJob?.isActive == true) {
            reloadRequested = true
            return
        }
        reloadJob =
            launch {
                while (true) {
                    reloadRequested = false
                    startupLogStore.append("LOCAL_TUN spec: reload create begin")
                    val spec =
                        runCatching { SessionRuntimeSpecFactory(appContextOrSelf).createTunSpec() }
                            .getOrElse { error ->
                                reason = error.runtimeGatewayMessage("tun runtime spec refresh failed")
                                startupLogStore.append("LOCAL_TUN failed=$reason")
                                Log.w("Tun runtime spec refresh failed: $reason")
                                StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                                sendClashStopped(reason)
                                stopSelf()
                                return@launch
                            }
                    startupLogStore.append(
                        "LOCAL_TUN spec: reload create done profile=${spec.profileUuid} overrides=${spec.overridePaths.size}"
                    )

                    val result = runtime.reload(spec)
                    if (!result.success) {
                        val failure =
                            result.toException(
                                defaultCode = RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
                                defaultMessage = "tun runtime reload failed",
                            )
                        reason = failure.runtimeGatewayMessage("tun runtime reload failed")
                        startupLogStore.append(
                            "LOCAL_TUN failed=${failure.code.name}:${failure.message}"
                        )
                        Log.w("Tun runtime reload failed: ${failure.code.name} ${failure.message}")
                        StatusProvider.markRuntimeStopped(ProxyMode.Tun)
                        sendClashStopped(reason)
                        stopSelf()
                        return@launch
                    }

                    if (!reloadRequested) {
                        return@launch
                    }
                }
            }
    }

    /**
     * Applies an access-control (per-app allow/deny) change on the live session by re-establishing
     * the VPN parameters in place. The service is intentionally left running: Android replaces the
     * active VPN session seamlessly, so a failed re-establish keeps the previous session intact.
     * The caller is only told about the failure via [sendAccessControlApplyFailed].
     */
    private fun scheduleAccessControlApply() {
        if (!this::runtime.isInitialized) return
        accessControlJob?.cancel()
        accessControlJob = launch {
            startupLogStore.append("LOCAL_TUN access control: reestablish begin")
            val spec =
                runCatching { SessionRuntimeSpecFactory(appContextOrSelf).createTunSpec() }
                    .getOrElse { error ->
                        val message = error.runtimeGatewayMessage("tun access control spec failed")
                        startupLogStore.append("LOCAL_TUN failed=$message")
                        Log.w("Tun access control spec failed: $message")
                        sendAccessControlApplyFailed(message)
                        return@launch
                    }

            val result = runtime.reestablishTransport(spec)
            if (!result.success) {
                val failure =
                    result.toException(
                        defaultCode = RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
                        defaultMessage = "tun access control reestablish failed",
                    )
                val message = failure.runtimeGatewayMessage("tun access control reestablish failed")
                startupLogStore.append("LOCAL_TUN failed=$message")
                Log.w("Tun access control reestablish failed: $message")
                sendAccessControlApplyFailed(message)
            }
        }
    }
}

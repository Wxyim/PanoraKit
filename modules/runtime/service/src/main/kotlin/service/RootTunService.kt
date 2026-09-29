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

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.nomadboxlab.monadbox.common.util.formatBytes
import com.github.nomadboxlab.monadbox.common.util.formatSpeed
import com.github.nomadboxlab.monadbox.core.model.TrafficSnapshot
import com.github.nomadboxlab.monadbox.core.util.decodeTrafficValue
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.runtime.service.R
import com.github.nomadboxlab.monadbox.service.common.constants.Components
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.root.RootTunRuntimeRecovery
import com.github.nomadboxlab.monadbox.service.root.RootTunServiceBridge
import com.github.nomadboxlab.monadbox.service.root.RootTunState
import com.github.nomadboxlab.monadbox.service.root.RootTunStateStore
import com.github.nomadboxlab.monadbox.service.root.RootTunStatus
import com.github.nomadboxlab.monadbox.service.runtime.util.sendClashStarted
import com.github.nomadboxlab.monadbox.service.runtime.util.sendClashStopped
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.*
import timber.log.Timber

class RootTunService : BaseService() {
    private val stateStore by lazy { RootTunStateStore(appContextOrSelf) }
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val powerManager by lazy { getSystemService(PowerManager::class.java) }
    private var notificationJob: Job? = null
    // Narrowed to String so the "did the text change" check compares text, not identity.
    @Volatile private var lastPostedTitle: String? = null
    @Volatile private var lastPostedContent: String? = null

    /**
     * Conflated "the screen just came back on" signal used by [awaitNotificationTick].
     *
     * While the screen is off the status line is not rendered at all and the loop waits
     * [SCREEN_OFF_NOTIFICATION_DELAY_MS], so the first tick after unlocking has to be pulled
     * forward instead of leaving a stale traffic line on screen for up to half a minute.
     */
    private val screenOnSignals = Channel<Unit>(Channel.CONFLATED)

    @Volatile private var screenReceiverRegistered = false

    private val screenReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_SCREEN_ON) screenOnSignals.trySend(Unit)
            }
        }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        registerScreenReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                launch { runCatching { RootTunServiceBridge.stop(appContextOrSelf) } }
                return START_NOT_STICKY
            }

            ACTION_START,
            null -> {
                val cachedStatus =
                    RootTunRuntimeRecovery.recoverStaleTransition(
                        context = appContextOrSelf,
                        status = stateStore.snapshot(),
                    )
                if (!cachedStatus.state.isActive && !cachedStatus.state.isRecovering) {
                    stopSelf()
                    return START_NOT_STICKY
                }

                val initialTitle =
                    cachedStatus.profileName ?: MLang.Service.Notification.UnknownProfile
                val initialContent = describeStatus(cachedStatus)
                val notification = buildNotification(initialTitle, initialContent)
                // The foreground notification is posted by the system, not through
                // postNotification, so seed the diff baseline here; otherwise the first loop tick
                // would rebuild and re-post the exact same notification.
                rememberPostedNotification(initialTitle, initialContent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }

                if (notificationJob?.isActive != true) {
                    notificationJob =
                        launch(Dispatchers.Default) {
                            var startedBroadcastSent = false
                            var unreachableCount = 0
                            var lastStatus = cachedStatus

                            while (isActive) {
                                val recoveredStatus =
                                    RootTunRuntimeRecovery.recoverStaleTransition(
                                        context = appContextOrSelf,
                                        status = stateStore.snapshot(),
                                    )
                                if (
                                    !recoveredStatus.state.isActive &&
                                        !recoveredStatus.state.isRecovering
                                ) {
                                    syncStatus(recoveredStatus)
                                    stopSelf()
                                    break
                                }

                                val snapshotResult = runCatching {
                                    RootTunServiceBridge.queryStatus(appContextOrSelf)
                                }
                                val snapshot = snapshotResult.getOrNull()
                                if (snapshot == null) {
                                    unreachableCount++
                                    val error = snapshotResult.exceptionOrNull()
                                    val fallbackStatus =
                                        stateStore.snapshot().takeIf {
                                            it.state != RootTunState.Idle ||
                                                !it.profileName.isNullOrBlank() ||
                                                !it.lastError.isNullOrBlank()
                                        } ?: lastStatus
                                    val title =
                                        fallbackStatus.profileName
                                            ?: MLang.Service.Notification.UnknownProfile
                                    val content =
                                        if (unreachableCount >= 3) {
                                            describeStatus(
                                                fallbackStatus.copy(
                                                    lastErrorCode =
                                                        fallbackStatus.lastErrorCode
                                                            ?: RuntimeGatewayErrorCode
                                                                .ROOT_RUNTIME_DISCONNECTED,
                                                    lastError =
                                                        fallbackStatus.lastError
                                                            ?: error?.message
                                                            ?: "State unavailable",
                                                )
                                            )
                                        } else {
                                            error?.message ?: "Waiting for reconnect"
                                        }
                                    postNotification(title, content)
                                    if (
                                        !fallbackStatus.state.isActive &&
                                            !fallbackStatus.state.isRecovering
                                    ) {
                                        stopSelf()
                                        break
                                    }
                                    awaitNotificationTick()
                                    continue
                                }

                                unreachableCount = 0
                                lastStatus = snapshot
                                syncStatus(snapshot)

                                if (
                                    snapshot.state == RootTunState.Running && !startedBroadcastSent
                                ) {
                                    sendClashStarted()
                                    startedBroadcastSent = true
                                }

                                if (
                                    snapshot.state == RootTunState.Idle ||
                                        snapshot.state == RootTunState.Failed
                                ) {
                                    postNotification(
                                        snapshot.profileName
                                            ?: MLang.Service.Notification.UnknownProfile,
                                        describeStatus(snapshot),
                                    )
                                    stopSelf()
                                    break
                                }

                                if (powerManager.isInteractive) {
                                    // The screen is the only consumer of this line, and building it
                                    // costs a cross-process traffic query into the root runtime, so
                                    // skip both while the screen is off.
                                    val profileName =
                                        snapshot.profileName
                                            ?: MLang.Service.Notification.UnknownProfile
                                    val content =
                                        if (snapshot.state == RootTunState.Running) {
                                            buildTrafficContent()
                                        } else {
                                            describeStatus(snapshot)
                                        }
                                    postNotification(profileName, content)
                                }
                                awaitNotificationTick()
                            }
                        }
                }

                return START_STICKY
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        notificationJob?.cancel()
        notificationJob = null
        unregisterScreenReceiver()

        val snapshot = stateStore.snapshot()
        if (!snapshot.state.isActive) {
            StatusProvider.markRuntimeStopped(ProxyMode.RootTun)
            sendClashStopped(snapshot.lastError)
        }

        super.onDestroy()
    }

    private suspend fun buildTrafficContent(): String {
        val traffic =
            runCatching { RootTunServiceBridge.queryTrafficSnapshot(appContextOrSelf) }
                .getOrDefault(TrafficSnapshot(0L, 0L))
        val now = traffic.now
        val total = traffic.total

        val upNow = decodeTrafficValue(now ushr 32)
        val downNow = decodeTrafficValue(now and 0xFFFFFFFFL)
        val upTotal = decodeTrafficValue(total ushr 32)
        val downTotal = decodeTrafficValue(total and 0xFFFFFFFFL)

        val speedStr = "↓ ${formatSpeed(downNow)} ↑ ${formatSpeed(upNow)}"
        val totalStr =
            MLang.Service.Notification.TrafficFormat.format(formatBytes(upTotal + downTotal))
        return "$speedStr | $totalStr"
    }

    private fun buildNotification(title: CharSequence, content: CharSequence): Notification {
        val contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent().apply {
                    component = Components.PROXY_SHEET_ACTIVITY
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                    )
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val stopIntent =
            PendingIntent.getService(
                this,
                1,
                Intent(this, RootTunService::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_logo_service)
            .setColor(getColor(R.color.color_clash))
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, MLang.Service.Tile.ClickToStopProxy, stopIntent)
            .build()
    }

    private fun createChannel() {
        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(CHANNEL_NAME)
                .build()
        )
    }

    @SuppressLint("MissingPermission")
    private fun postNotification(title: CharSequence, content: CharSequence) {
        if (!canPostNotifications()) return
        val titleText = title.toString()
        val contentText = content.toString()
        if (titleText == lastPostedTitle && contentText == lastPostedContent) return
        val posted =
            runCatching {
                    notificationManager.notify(NOTIFICATION_ID, buildNotification(title, content))
                }
                .isSuccess
        if (posted) {
            rememberPostedNotification(titleText, contentText)
        }
    }

    /**
     * Records the notification text that last reached the notification manager.
     *
     * The status loop re-renders the notification every few seconds; when the profile name and the
     * rendered traffic/status line are unchanged, rebuilding the builder, both [PendingIntent]s and
     * calling `notify()` is pure waste.
     */
    private fun rememberPostedNotification(title: String, content: String) {
        lastPostedTitle = title
        lastPostedContent = content
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun resolveNotificationDelay(): Long =
        if (powerManager.isInteractive) SCREEN_ON_NOTIFICATION_DELAY_MS
        else SCREEN_OFF_NOTIFICATION_DELAY_MS

    /**
     * Waits for the next status tick, but lets a screen-on broadcast end the wait early.
     *
     * While the screen is on this is a plain [delay]. While the screen is off the wait is stretched
     * to [SCREEN_OFF_NOTIFICATION_DELAY_MS] because the content is neither rebuilt nor re-posted in
     * that state (see the `isInteractive` check in the loop); the loop is still needed to notice
     * the runtime stopping, and reporting that up to half a minute late is cheaper than waking the
     * CPU every few seconds for a line the user cannot see.
     */
    private suspend fun awaitNotificationTick() {
        withTimeoutOrNull(resolveNotificationDelay()) { screenOnSignals.receive() }
    }

    private fun registerScreenReceiver() {
        if (screenReceiverRegistered) return
        runCatching {
                // Same registration shape as ServiceNotificationManager's screen receiver: a
                // protected system broadcast, so no export flag is required on API 34+.
                registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
            }
            .onSuccess { screenReceiverRegistered = true }
            .onFailure { error -> Timber.tag(TAG).w(error, "Screen receiver registration failed") }
    }

    private fun unregisterScreenReceiver() {
        if (!screenReceiverRegistered) return
        runCatching { unregisterReceiver(screenReceiver) }
        screenReceiverRegistered = false
    }

    private fun syncStatus(status: RootTunStatus) {
        if (status.state.isActive) {
            StatusProvider.markRuntimeStarted(ProxyMode.RootTun)
        } else {
            StatusProvider.markRuntimeStopped(ProxyMode.RootTun)
        }
    }

    private fun describeStatus(status: RootTunStatus): String {
        return when (status.state) {
            RootTunState.Starting -> "Starting..."
            RootTunState.Running -> MLang.Service.Notification.Running
            RootTunState.Stopping -> "Stopping..."
            RootTunState.Failed -> "Failed: ${status.composedError() ?: "unknown error"}"
            RootTunState.Idle -> "Stopped"
        }
    }

    companion object {
        private const val TAG = "RootTunService"
        private const val ACTION_START = "com.github.nomadboxlab.monadbox.ROOT_TUN_SERVICE_START"
        private const val ACTION_STOP = "com.github.nomadboxlab.monadbox.ROOT_TUN_SERVICE_STOP"
        private const val NOTIFICATION_ID = 1003
        private const val CHANNEL_ID = "clash_root_tun_service"
        private const val CHANNEL_NAME = "Clash RootTun Service"
        private const val SCREEN_ON_NOTIFICATION_DELAY_MS = 4000L
        private const val SCREEN_OFF_NOTIFICATION_DELAY_MS = 30_000L

        fun start(context: Context) {
            val intent = Intent(context, RootTunService::class.java).setAction(ACTION_START)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, RootTunService::class.java))
        }
    }
}

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

package com.github.nomadboxlab.monadbox.service.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.PowerManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.nomadboxlab.monadbox.common.util.formatBytes
import com.github.nomadboxlab.monadbox.common.util.formatSpeed
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.core.util.decodeTrafficValue
import com.github.nomadboxlab.monadbox.runtime.service.R
import com.github.nomadboxlab.monadbox.service.common.constants.Components
import com.github.nomadboxlab.monadbox.service.runtime.config.ServiceStore
import com.github.nomadboxlab.monadbox.service.runtime.records.ImportedDao
import com.tencent.mmkv.MMKV
import dev.oom_wg.purejoy.mlang.MLang
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*

class ServiceNotificationManager(private val service: Service, private val config: Config) {
    data class Config(val notificationId: Int, val channelId: String, val channelName: String)

    private val serviceStore by lazy { ServiceStore() }
    private val settingsStore by lazy {
        MMKV.mmkvWithID(StoreIds.SETTINGS, MMKV.MULTI_PROCESS_MODE)
    }
    private val notificationManager by lazy { NotificationManagerCompat.from(service) }
    private val powerManager by lazy { service.getSystemService(Service.POWER_SERVICE) as PowerManager }

    // ── Update lifecycle ──────────────────────────────────────────

    private var updateJob: Job? = null
    private var hostScope: CoroutineScope? = null

    /**
     * Receives screen-on/off broadcasts so we can suspend traffic-speed
     * polling while the screen is off and resume it when the user turns
     * the screen back on.
     */
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> cancelUpdateJob()
                Intent.ACTION_SCREEN_ON -> hostScope?.let { startTrafficUpdate(it) }
            }
        }
    }

    @Volatile private var receiverRegistered = false

    // ── Channel & initial notification ─────────────────────────────

    fun createChannel() {
        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                    config.channelId,
                    NotificationManagerCompat.IMPORTANCE_LOW,
                )
                .setName(config.channelName)
                .build()
        )
    }

    fun createInitialNotification(): Notification {
        return buildRunningNotification()
    }

    // ── Traffic update ─────────────────────────────────────────────

    /**
     * Starts a screen-aware traffic-speed update loop.
     *
     * Updates are posted every [ACTIVE_POLL_MS] while the screen is on
     * *and* the user has enabled the traffic-speed notification.
     * When the screen is off, or the traffic display is disabled, the
     * loop cancels itself — no background polling, no CPU wakeups.
     *
     * The loop is automatically restarted when the screen turns on
     * (via [screenReceiver]).
     */
    @SuppressLint("MissingPermission")
    fun startTrafficUpdate(scope: CoroutineScope): Job {
        hostScope = scope
        registerScreenReceiver()
        cancelUpdateJob()

        if (!powerManager.isInteractive) return Job()  // screen off → no work
        if (!shouldShowTrafficNotification()) return Job()  // traffic display disabled → no work

        val job = scope.launch(Dispatchers.Default) {
            while (isActive) {
                if (canPostNotifications()) {
                    runCatching {
                        notificationManager.notify(
                            config.notificationId,
                            buildRunningNotification(),
                        )
                    }
                }
                // Re-check conditions each cycle so we stop promptly when
                // traffic display is toggled off while the screen stays on.
                if (!shouldShowTrafficNotification()) {
                    cancelUpdateJob()
                    return@launch
                }
                delay(ACTIVE_POLL_MS)
            }
        }
        updateJob = job
        return job
    }

    /** Cancels the update loop without touching the host scope or receiver. */
    private fun cancelUpdateJob() {
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * Call from the hosting service's [Service.onDestroy] to tear down
     * the screen receiver and release the scope reference.
     */
    fun stopTrafficUpdate() {
        cancelUpdateJob()
        unregisterScreenReceiver()
        hostScope = null
    }

    // ── Receiver registration ─────────────────────────────────────

    private fun registerScreenReceiver() {
        if (receiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        service.registerReceiver(screenReceiver, filter)
        receiverRegistered = true
    }

    private fun unregisterScreenReceiver() {
        if (!receiverRegistered) return
        runCatching { service.unregisterReceiver(screenReceiver) }
        receiverRegistered = false
    }

    // ── Notification building ─────────────────────────────────────

    @SuppressLint("MissingPermission")
    private fun canPostNotifications(): Boolean {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(service, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun buildRunningNotification(): Notification {
        val profileName = resolveProfileName()
        if (!shouldShowTrafficNotification()) {
            return buildNotification(profileName, MLang.Service.Notification.Running)
        }

        val now = runCatching { Clash.queryTrafficNow() }.getOrDefault(0L)
        val total = runCatching { Clash.queryTrafficTotal() }.getOrDefault(0L)

        val upNow = decodeTrafficValue(now ushr 32)
        val downNow = decodeTrafficValue(now and 0xFFFFFFFFL)
        val upTotal = decodeTrafficValue(total ushr 32)
        val downTotal = decodeTrafficValue(total and 0xFFFFFFFFL)

        val speedStr = "↓ ${formatSpeed(downNow)} ↑ ${formatSpeed(upNow)}"
        val totalStr =
            MLang.Service.Notification.TrafficFormat.format(formatBytes(upTotal + downTotal))
        return buildNotification(profileName, "$speedStr | $totalStr")
    }

    private fun buildNotification(title: CharSequence, content: CharSequence): Notification {
        val contentIntent =
            PendingIntent.getActivity(
                service,
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

        return NotificationCompat.Builder(service, config.channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_logo_service)
            .setColor(service.getColor(R.color.color_clash))
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun resolveProfileName(): String {
        val active = serviceStore.activeProfile ?: return MLang.Service.Notification.UnknownProfile
        return ImportedDao.queryByUUID(active)?.name?.takeIf { it.isNotBlank() }
            ?: MLang.Service.Notification.UnknownProfile
    }

    private fun shouldShowTrafficNotification(): Boolean {
        val settings = settingsStore
        if (settings.containsKey("showTrafficNotification")) {
            return settings.decodeBool("showTrafficNotification", true)
        }
        return serviceStore.showTrafficNotification
    }

    companion object {
        /** Polling interval when screen is on and traffic display is enabled. */
        private val ACTIVE_POLL_MS = 2000L.milliseconds

        val VPN_CONFIG =
            Config(
                notificationId = 1001,
                channelId = "clash_vpn_service",
                channelName = "Clash VPN Service",
            )

        val HTTP_CONFIG =
            Config(
                notificationId = 1002,
                channelId = "clash_http_service",
                channelName = "Clash HTTP Service",
            )
    }
}

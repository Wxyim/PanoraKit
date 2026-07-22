/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.service.runtime.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccess
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.service.common.compat.registerReceiverCompat
import com.github.nomadboxlab.monadbox.service.root.RootPackageShell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

internal class RuntimeInstalledAppsPublisher(context: Context, private val scope: CoroutineScope) {
    private val appContext = context.applicationContext
    private var receiver: BroadcastReceiver? = null
    private var publishJob: Job? = null

    // Last known-good mapping, used as fallback when a subsequent publish
    // returns empty (e.g. PackageManager transiently unavailable after
    // force-stop).  Never let Go's installedAppsUid be replaced with an
    // empty map — that blinds UID resolution for every connection.
    @Volatile
    private var lastKnownMappings: List<Pair<Int, String>> = emptyList()

    fun start() {
        if (receiver != null) {
            publishNow()
            return
        }

        val packageReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    RootPackageShell.invalidateCaches()
                    // Debounced: a burst of package events (e.g. system auto-update
                    // rolling through 20 apps) collapses into a single re-publish.
                    // The final state of the install list is the only thing the
                    // mihomo routing table needs.
                    schedulePublish()
                }
            }

        val filter =
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
            }

        appContext.registerReceiverCompat(packageReceiver, filter)
        receiver = packageReceiver
        // Initial sync bypasses the debounce: we want mihomo to know the current
        // list as soon as the session is up, not 300 ms later.
        publishNow()
    }

    fun stop() {
        publishJob?.cancel()
        publishJob = null
        receiver?.let { registered ->
            receiver = null
            runCatching { appContext.unregisterReceiver(registered) }
        }
        RootPackageShell.invalidateCaches()
        lastKnownMappings = emptyList()
    }

    /**
     * Schedule a debounced re-publish. Multiple calls within
     * [PUBLISH_DEBOUNCE_MS] collapse into a single JNI push. The most recent
     * call wins; earlier ones are cancelled.
     */
    private fun schedulePublish() {
        publishJob?.cancel()
        publishJob =
            scope.launch(Dispatchers.IO) {
                delay(PUBLISH_DEBOUNCE_MS)
                publish()
            }
    }

    /**
     * Publish immediately, bypassing the debounce window. Reserved for the
     * initial sync on session start, where latency matters and the calling
     * thread is already a background coroutine — we publish synchronously so
     * that the UID→package map is populated before the VPN TUN starts
     * accepting connections.
     */
    private fun publishNow() {
        publishJob?.cancel()
        publishJob = null
        publish()
    }

    private fun publish() {
        val fresh = resolveMappings()
        val mappings =
            if (fresh.isNotEmpty()) {
                lastKnownMappings = fresh
                fresh
            } else if (lastKnownMappings.isNotEmpty()) {
                Timber.w(
                    "RuntimeInstalledAppsPublisher resolved 0 mappings; " +
                        "falling back to last-known (%s entries)",
                    lastKnownMappings.size,
                )
                lastKnownMappings
            } else {
                Timber.w("RuntimeInstalledAppsPublisher resolved 0 mappings; no fallback — skipping")
                return
            }
        Clash.notifyInstalledAppsChanged(mappings)
        Timber.d(
            "RuntimeInstalledAppsPublisher published %s app uid mappings",
            mappings.size,
        )
    }

    private fun resolveMappings(): List<Pair<Int, String>> {
        RootPackageShell.queryPackageUidMap()
            ?.takeIf { it.isNotEmpty() }
            ?.let(InstalledAppUidMappings::fromRootPackageUidMap)
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                Timber.d("RuntimeInstalledAppsPublisher: root shell returned %s entries", it.size)
                return it
            }

        val accessState = InstalledAppsAccess.resolve(appContext)
        if (!accessState.canEnumerateInstalledApps) {
            Timber.w(
                "RuntimeInstalledAppsPublisher: cannot enumerate (mode=%s); returning empty",
                accessState.mode,
            )
            return emptyList()
        }

        return runCatching {
                installedPackages()
                    .mapNotNull { info ->
                        val uid = info.applicationInfo?.uid ?: return@mapNotNull null
                        InstalledAppUidEntry(
                            packageName = info.packageName,
                            uid = uid,
                            sharedUserId = info.sharedUserId,
                        )
                    }
                    .let(InstalledAppUidMappings::fromEntries)
            }
            .onFailure { error ->
                Timber.w(error, "Failed to enumerate installed apps for runtime attribution")
            }
            .getOrDefault(emptyList())
    }

    private fun installedPackages(): List<PackageInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION") appContext.packageManager.getInstalledPackages(0)
        }
    }

    private companion object {
        // 300 ms is short enough to be invisible to a user watching the proxy
        // state, and long enough to absorb typical broadcast bursts from
        // auto-update (Google Play typically fans out 5–30 PACKAGE_REPLACED
        // events within ~100 ms when a batch update lands).
        private const val PUBLISH_DEBOUNCE_MS = 300L
    }
}

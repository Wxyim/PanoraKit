package com.github.yumelira.yumebox.service.runtime.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.github.yumelira.yumebox.common.util.InstalledAppsAccess
import com.github.yumelira.yumebox.core.Clash
import com.github.yumelira.yumebox.service.common.compat.registerReceiverCompat
import com.github.yumelira.yumebox.service.root.RootPackageShell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

internal class RuntimeInstalledAppsPublisher(context: Context, private val scope: CoroutineScope) {
    private val appContext = context.applicationContext
    private var receiver: BroadcastReceiver? = null
    private var publishJob: Job? = null

    fun start() {
        if (receiver != null) {
            publishAsync()
            return
        }

        val packageReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    RootPackageShell.invalidateCaches()
                    publishAsync()
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
        publishAsync()
    }

    fun stop() {
        publishJob?.cancel()
        publishJob = null
        receiver?.let { registered ->
            receiver = null
            runCatching { appContext.unregisterReceiver(registered) }
        }
        RootPackageShell.invalidateCaches()
    }

    private fun publishAsync() {
        publishJob?.cancel()
        publishJob =
            scope.launch(Dispatchers.IO) {
                val mappings = resolveMappings()
                Clash.notifyInstalledAppsChanged(mappings)
                Timber.d(
                    "RuntimeInstalledAppsPublisher published %s app uid mappings",
                    mappings.size,
                )
            }
    }

    private fun resolveMappings(): List<Pair<Int, String>> {
        RootPackageShell.queryPackageUidMap()
            ?.takeIf { it.isNotEmpty() }
            ?.let(InstalledAppUidMappings::fromRootPackageUidMap)
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                return it
            }

        if (!InstalledAppsAccess.resolve(appContext).canEnumerateInstalledApps) {
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
}

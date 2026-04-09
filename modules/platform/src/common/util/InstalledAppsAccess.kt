package com.github.yumelira.yumebox.common.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class InstalledAppsAccessMode {
    Full,
    PermissionRequired,
    ManualOnly,
}

data class InstalledAppsAccessState(
    val mode: InstalledAppsAccessMode,
    val requiresMiuiPermission: Boolean,
) {
    val canEnumerateInstalledApps: Boolean
        get() = mode == InstalledAppsAccessMode.Full
}

object InstalledAppsAccess {
    const val MiuiPermission = "com.android.permission.GET_INSTALLED_APPS"

    fun resolve(context: Context): InstalledAppsAccessState {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return InstalledAppsAccessState(
                mode = InstalledAppsAccessMode.Full,
                requiresMiuiPermission = false,
            )
        }

        val hasQueryAllPackages =
            context.packageManager.checkPermission(
                Manifest.permission.QUERY_ALL_PACKAGES,
                context.packageName,
            ) == PackageManager.PERMISSION_GRANTED

        if (hasQueryAllPackages) {
            return InstalledAppsAccessState(
                mode = InstalledAppsAccessMode.Full,
                requiresMiuiPermission = false,
            )
        }

        val permissionExists =
            runCatching {
                    context.packageManager.getPermissionInfo(MiuiPermission, 0)
                    true
                }
                .getOrDefault(false)

        if (!permissionExists) {
            return InstalledAppsAccessState(
                mode = InstalledAppsAccessMode.ManualOnly,
                requiresMiuiPermission = false,
            )
        }

        val granted =
            ContextCompat.checkSelfPermission(context, MiuiPermission) ==
                PackageManager.PERMISSION_GRANTED

        return InstalledAppsAccessState(
            mode =
                if (granted) InstalledAppsAccessMode.Full
                else InstalledAppsAccessMode.PermissionRequired,
            requiresMiuiPermission = !granted,
        )
    }
}

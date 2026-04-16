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

package com.github.nomadboxlab.monadbox.common.util

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

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

    /**
     * Cached successful result of the runtime query-all-packages verification.
     * A failed check is intentionally not cached: on a fresh install some ROMs
     * briefly return a filtered PackageManager result while package visibility
     * is still settling. Caching that transient failure breaks first VPN start.
     */
    @Volatile
    private var verifiedFullAccess: Boolean? = null

    fun resolve(context: Context): InstalledAppsAccessState {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return InstalledAppsAccessState(
                mode = InstalledAppsAccessMode.Full,
                requiresMiuiPermission = false,
            )
        }

        // On MIUI/HyperOS devices, QUERY_ALL_PACKAGES may be auto-granted at the
        // platform level but the actual installed-app enumeration is still gated by
        // MIUI's own GET_INSTALLED_APPS permission. Check MIUI permission FIRST so
        // that the onboarding screen correctly reports the real authorization state.
        val miuiPermissionExists =
            runCatching {
                    context.packageManager.getPermissionInfo(MiuiPermission, 0)
                    true
                }
                .getOrDefault(false)

        if (miuiPermissionExists) {
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

        val hasQueryAllPackages =
            context.packageManager.checkPermission(
                Manifest.permission.QUERY_ALL_PACKAGES,
                context.packageName,
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasQueryAllPackages) {
            return InstalledAppsAccessState(
                mode = InstalledAppsAccessMode.ManualOnly,
                requiresMiuiPermission = false,
            )
        }

        // On many ROMs (ColorOS, OriginOS, HarmonyOS, etc.), checkPermission may
        // return GRANTED while the system still filters package queries. Verify
        // by actually enumerating installed apps and checking the count.
        if (isQueryAllPackagesEffective(context)) {
            return InstalledAppsAccessState(
                mode = InstalledAppsAccessMode.Full,
                requiresMiuiPermission = false,
            )
        }

        return InstalledAppsAccessState(
            mode = InstalledAppsAccessMode.ManualOnly,
            requiresMiuiPermission = false,
        )
    }

    /**
     * Verify that QUERY_ALL_PACKAGES actually allows full app enumeration.
     *
     * On Android 11+, a filtered list typically returns 20–60 packages while a
     * genuinely unfiltered list returns 150+. We use 80 as a conservative
     * threshold that safely distinguishes the two.
     *
     * The result is cached for the process lifetime — permissions cannot change
     * at runtime without killing the process.
     */
    private fun isQueryAllPackagesEffective(context: Context): Boolean {
        verifiedFullAccess?.let { return it }
        val result =
            runCatching {
                    val apps = context.packageManager.getInstalledApplications(0)
                    apps.size >= MIN_EXPECTED_APP_COUNT
                }
                .getOrDefault(false)
        if (result) {
            verifiedFullAccess = true
        }
        return result
    }

    /** Invalidate the cached verification (useful for testing). */
    fun invalidateVerificationCache() {
        verifiedFullAccess = null
    }

    private const val MIN_EXPECTED_APP_COUNT = 80
}

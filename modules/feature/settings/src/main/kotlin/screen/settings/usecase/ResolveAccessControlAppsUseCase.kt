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
 *
 */

package com.github.nomadboxlab.monadbox.feature.settings.usecase

import android.app.Application
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccess
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccessMode
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccessState
import com.github.nomadboxlab.monadbox.feature.settings.AccessControlAppInfo
import com.github.nomadboxlab.monadbox.feature.settings.AccessControlAppLoader
import com.github.nomadboxlab.monadbox.service.root.RootPackageShell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResolveAccessControlAppsUseCase(private val application: Application) {
    /**
     * Resolve the Android-level access state for enumerating installed apps.
     *
     * Root access is deliberately NOT factored in here: on Android 11+,
     * [PackageManager.getInstalledApplications] silently returns filtered
     * results when QUERY_ALL_PACKAGES is absent, even when root is available—
     * the call runs in the app process, not via a root shell. Root is instead
     * used as a fallback at load-time in [AccessControlAppLoader].
     */
    fun resolveAccessState(): InstalledAppsAccessState {
        return InstalledAppsAccess.resolve(application)
    }

    fun hasFullPackageAccess(): Boolean {
        return resolveAccessState().canEnumerateInstalledApps ||
            RootPackageShell.hasRootAccess()
    }

    suspend fun loadInstalledApps(selectedPackages: Set<String>): List<AccessControlAppInfo> =
        withContext(Dispatchers.IO) {
            AccessControlAppLoader.loadInstalledApps(application, selectedPackages)
        }
}

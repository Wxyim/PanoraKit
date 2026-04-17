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

package com.github.nomadboxlab.monadbox.service.clash.module

import android.app.Service
import android.content.Intent
import android.content.pm.PackageInfo
import com.github.nomadboxlab.monadbox.common.util.InstalledAppsAccess
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.service.root.RootPackageShell
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay

class AppListCacheModule(service: Service) : Module<Unit>(service) {
    private fun PackageInfo.uniqueUidName(): String =
        if (sharedUserId?.isNotBlank() == true) sharedUserId ?: packageName else packageName

    private fun reload() {
        if (
            !RootPackageShell.hasRootAccess() &&
                !InstalledAppsAccess.resolve(service).canEnumerateInstalledApps
        ) {
            Clash.notifyInstalledAppsChanged(emptyList())
            return
        }
        val packages =
            service.packageManager
                .getInstalledPackages(0)
                .mapNotNull { info -> info.applicationInfo?.let { appInfo -> info to appInfo.uid } }
                .groupBy { (info, _) -> info.uniqueUidName() }
                .map { (_, v) ->
                    val (info, uid) = v[0]

                    if (v.size == 1) {

                        uid to info.packageName
                    } else {
                        uid to info.uniqueUidName()
                    }
                }

        Clash.notifyInstalledAppsChanged(packages)
    }

    override suspend fun run() {
        val packageChanged =
            receiveBroadcast(Channel.CONFLATED) {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            }

        while (true) {
            reload()

            packageChanged.receive()

            delay(10_000L.milliseconds)
        }
    }
}

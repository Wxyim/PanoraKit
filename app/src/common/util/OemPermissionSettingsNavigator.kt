/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.common.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.github.yumelira.yumebox.core.StoreIds
import com.tencent.mmkv.MMKV
import timber.log.Timber

object OemPermissionSettingsNavigator {

    private const val STATS_PREFIX = "oem_settings_nav"

    fun openBackgroundPermissionSettings(context: Context): Boolean {
        val packageName = context.packageName
        val manufacturer = Build.MANUFACTURER.lowercase().ifBlank { "unknown" }
        val candidates = mutableListOf<CandidateIntent>()
        val stats = MMKV.mmkvWithID(StoreIds.SERVICE_CACHE, MMKV.MULTI_PROCESS_MODE)
        incrementCounter(stats, "${STATS_PREFIX}.attempt.total")
        incrementCounter(stats, "${STATS_PREFIX}.attempt.$manufacturer")

        when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> {
                candidates += candidateOf(
                    tag = "miui_autostart",
                    component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity",
                    ),
                )
                candidates += candidateOf(
                    tag = "miui_perm_editor",
                    action = "miui.intent.action.APP_PERM_EDITOR",
                    packageName = "com.miui.securitycenter",
                    extras = mapOf("extra_pkgname" to packageName),
                )
            }

            manufacturer.contains("oppo") || manufacturer.contains("oneplus") || manufacturer.contains("realme") || manufacturer.contains("oplus") -> {
                candidates += candidateOf(
                    tag = "coloros_startup",
                    component = ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity",
                    ),
                )
                candidates += candidateOf(
                    tag = "oplus_startup",
                    component = ComponentName(
                        "com.oplus.safecenter",
                        "com.oplus.safecenter.permission.startup.StartupAppListActivity",
                    ),
                )
            }

            manufacturer.contains("vivo") || manufacturer.contains("iqoo") -> {
                candidates += candidateOf(
                    tag = "iqoo_whitelist",
                    component = ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity",
                    ),
                )
                candidates += candidateOf(
                    tag = "vivo_bg_startup",
                    component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity",
                    ),
                )
            }

            manufacturer.contains("honor") || manufacturer.contains("huawei") -> {
                candidates += candidateOf(
                    tag = "honor_protect",
                    component = ComponentName(
                        "com.hihonor.systemmanager",
                        "com.hihonor.systemmanager.optimize.process.ProtectActivity",
                    ),
                )
                candidates += candidateOf(
                    tag = "huawei_protect",
                    component = ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity",
                    ),
                )
            }
        }

        candidates += CandidateIntent(
            tag = "battery_optimization_settings",
            intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS),
        )
        candidates += CandidateIntent(
            tag = "app_details",
            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(android.net.Uri.fromParts("package", packageName, null)),
        )

        for ((index, candidate) in candidates.withIndex()) {
            val launchIntent = candidate.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val resolved = launchIntent.resolveActivity(context.packageManager) != null
            if (!resolved) {
                Timber.d(
                    "OEM nav unresolved manufacturer=%s candidate=%s index=%d",
                    manufacturer,
                    candidate.tag,
                    index,
                )
                continue
            }
            val launched = runCatching {
                context.startActivity(launchIntent)
                true
            }.onFailure {
                Timber.w(
                    it,
                    "OEM nav launch failed manufacturer=%s candidate=%s index=%d",
                    manufacturer,
                    candidate.tag,
                    index,
                )
            }.getOrDefault(false)
            if (launched) {
                incrementCounter(stats, "${STATS_PREFIX}.success.total")
                incrementCounter(stats, "${STATS_PREFIX}.success.$manufacturer")
                stats?.encode("${STATS_PREFIX}.last.success", "$manufacturer:${candidate.tag}")
                return true
            }
        }

        incrementCounter(stats, "${STATS_PREFIX}.failure.total")
        incrementCounter(stats, "${STATS_PREFIX}.failure.$manufacturer")
        stats?.encode("${STATS_PREFIX}.last.failure", "$manufacturer:${candidates.joinToString("|") { it.tag }}")
        Timber.w(
            "OEM nav failed manufacturer=%s candidates=%s",
            manufacturer,
            candidates.joinToString(",") { it.tag },
        )
        return false
    }

    private fun incrementCounter(mmkv: MMKV?, key: String) {
        if (mmkv == null) return
        val value = mmkv.decodeInt(key, 0) + 1
        mmkv.encode(key, value)
    }

    private fun candidateOf(
        tag: String,
        action: String? = null,
        component: ComponentName? = null,
        packageName: String? = null,
        extras: Map<String, String> = emptyMap(),
    ): CandidateIntent {
        return CandidateIntent(
            tag = tag,
            intent = intentOf(
                action = action,
                component = component,
                packageName = packageName,
                extras = extras,
            ),
        )
    }

    private data class CandidateIntent(
        val tag: String,
        val intent: Intent,
    )

    private fun intentOf(
        action: String? = null,
        component: ComponentName? = null,
        packageName: String? = null,
        extras: Map<String, String> = emptyMap(),
    ): Intent {
        val intent = if (action != null) Intent(action) else Intent()
        component?.let { intent.component = it }
        packageName?.let { intent.setPackage(it) }
        extras.forEach { (key, value) -> intent.putExtra(key, value) }
        return intent
    }

    fun dumpStats(): Map<String, Int> {
        val mmkv = MMKV.mmkvWithID(StoreIds.SERVICE_CACHE, MMKV.MULTI_PROCESS_MODE) ?: return emptyMap()
        val keys = mmkv.allKeys()?.filter { it.startsWith("$STATS_PREFIX.") } ?: return emptyMap()
        return keys.associateWith { mmkv.decodeInt(it, 0) }
    }
}

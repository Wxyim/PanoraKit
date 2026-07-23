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

package com.github.nomadboxlab.monadbox.runtime.client

import android.content.Context
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class AppIdentity(val appKey: String, val packageName: String? = null, val appName: String)

class AppIdentityResolver(context: Context) {
    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager
    private val packageCache = ConcurrentHashMap<String, AppIdentity>()
    private val labelCache = ConcurrentHashMap<String, String>()
    private val uidCache = ConcurrentHashMap<Int, String?>()

    fun resolve(metadata: JsonObject): AppIdentity {
        val explicitPackageName =
            metadata.firstNonBlankValue("packageName", "package", "package-name", "package_name")
        val processName =
            metadata.firstNonBlankValue(
                "process",
                "processName",
                "process-name",
                "process_name",
                "appProcess",
            )
        val uid = metadata.firstUidValue("uid", "sourceUid", "source_uid", "Uid", "UID")
        val fallbackHost =
            metadata.firstNonBlankValue(
                "host",
                "destinationIP",
                "destinationIp",
                "destination-ip",
                "destination_ip",
                "dnsName",
                "dns-name",
                "dns_name",
            )
        return resolve(
            explicitPackageName = explicitPackageName,
            processName = processName,
            uid = uid,
            fallbackHost = fallbackHost,
        )
    }

    fun resolve(
        explicitPackageName: String,
        processName: String,
        uid: Int?,
        fallbackHost: String = "",
    ): AppIdentity {
        val cacheKey = buildString {
            append(explicitPackageName)
            append('|')
            append(processName)
            append('|')
            append(uid ?: "")
        }
        packageCache[cacheKey]?.let {
            return it
        }

        val identity = resolveIdentity(explicitPackageName, processName, uid, fallbackHost)

        // Cache only a confirmed installed package. UID/process fallbacks can
        // be produced while the VPN's first UID lookup is still warming up;
        // caching those results would prevent a later successful resolution.
        if (identity.appKey.startsWith("package:")) {
            packageCache[cacheKey] = identity
        }
        return identity
    }

    private fun resolveIdentity(
        explicitPackageName: String,
        processName: String,
        uid: Int?,
        fallbackHost: String,
    ): AppIdentity {
        val packageName =
            findInstalledPackage(explicitPackageName)
                ?: resolveByUid(uid)
                ?: resolveByProcess(processName)

        return when {
            packageName != null ->
                AppIdentity(
                    appKey = "package:$packageName",
                    packageName = packageName,
                    appName = resolveLabel(packageName).ifBlank { packageName },
                )
            explicitPackageName.isNotBlank() ->
                AppIdentity(
                    appKey = "package-hint:$explicitPackageName",
                    packageName = explicitPackageName,
                    appName = explicitPackageName,
                )
            uid != null && uid > 0 ->
                AppIdentity(
                    appKey = "uid:$uid",
                    packageName = null,
                    appName = processName.ifBlank { "UID $uid" },
                )
            processName.isNotBlank() ->
                AppIdentity(
                    appKey = "process:$processName",
                    packageName = null,
                    appName = processName,
                )
            fallbackHost.isNotBlank() ->
                AppIdentity(
                    appKey = UNKNOWN_APP_KEY,
                    packageName = null,
                    appName = fallbackHost,
                )
            else ->
                AppIdentity(
                    appKey = UNKNOWN_APP_KEY,
                    packageName = null,
                    appName = UNKNOWN_APP_NAME,
                )
        }
    }

    private fun resolveByUid(uid: Int?): String? {
        if (uid == null || uid <= 0) return null
        uidCache[uid]?.let { return it }

        val packageName =
            packageManager.getPackagesForUid(uid)?.firstNotNullOfOrNull(::findInstalledPackage)
        if (packageName != null) {
            uidCache[uid] = packageName
        }
        return packageName
    }

    private fun resolveByProcess(processName: String): String? {
        if (processName.isBlank()) return null

        val processBaseName = processName.substringAfterLast('/').trim()
        val processBaseWithoutSuffix = processBaseName.substringBefore(':').trim()

        val candidates =
            buildList {
                    add(processName)
                    add(processName.substringBefore(':'))
                    if (processBaseName.isNotEmpty()) {
                        add(processBaseName)
                    }
                    if (processBaseWithoutSuffix.isNotEmpty()) {
                        add(processBaseWithoutSuffix)
                    }
                }
                .map(String::trim)
                .filter(String::isNotEmpty)
                .distinct()

        candidates.firstNotNullOfOrNull(::findInstalledPackage)?.let {
            return it
        }

        return null
    }

    private fun findInstalledPackage(packageName: String): String? {
        if (packageName.isBlank()) return null
        return runCatching {
                packageManager.getApplicationInfo(packageName, 0)
                packageName
            }
            .getOrNull()
    }

    private fun resolveLabel(packageName: String): String {
        labelCache[packageName]?.let {
            return it
        }
        val label =
            runCatching {
                    val info = packageManager.getApplicationInfo(packageName, 0)
                    packageManager.getApplicationLabel(info).toString().trim()
                }
                .getOrDefault(packageName)
        labelCache[packageName] = label
        return label
    }

    /** Resolve a display label for the given package name, with caching.
     *  Returns the application label (e.g. "微信") or falls back to [packageName] itself. */
    fun resolveAppLabel(packageName: String): String {
        if (packageName.isBlank()) return UNKNOWN_APP_NAME
        return resolveLabel(packageName).ifBlank { packageName }
    }

    /**
     * Extract the package name from mihomo metadata, with UID fallback.
     * Uses mihomo's `packageName` field first; if absent, resolves UID via
     * [android.content.pm.PackageManager.getPackagesForUid].
     * Returns null when neither source yields a result.
     */
    fun resolvePackageFromMetadata(metadata: JsonObject): String? {
        // 1. Mihomo-provided package name.
        //    Mihomo's Metadata struct has NO dedicated "packageName" field; when
        //    FindPackageName succeeds it writes the Android package name into the
        //    "process" field. Check "process" first, then legacy aliases.
        val pkg = metadata.firstNonBlankValue(
            "process", "packageName", "package", "package-name", "package_name",
        )
        if (pkg.isNotBlank() && findInstalledPackage(pkg) != null) return pkg

        // 2. UID fallback for non-root / procfs-unavailable cases.
        val uid = metadata.firstUidValue("uid", "sourceUid", "source_uid", "Uid", "UID")
        if (uid != null && uid > 0) {
            resolveByUid(uid)?.let { return it }
        }

        // 3. Process name fallback — split and match each segment against installed packages.
        val process = metadata.firstNonBlankValue(
            "process", "processName", "process-name", "process_name", "appProcess",
        )
        if (process.isNotBlank()) {
            resolveByProcess(process)?.let { return it }
        }

        return null
    }

    /** Fallback display name when package resolution fails: process name → UID → "Unknown App". */
    fun resolveFallbackDisplayName(metadata: JsonObject): String {
        return metadata.firstNonBlankValue(
            "process", "processName", "process-name", "process_name", "appProcess",
        ).ifBlank {
            metadata.firstUidValue("uid", "sourceUid", "source_uid", "Uid", "UID")
                ?.let { "UID $it" }.orEmpty()
        }.ifBlank { UNKNOWN_APP_NAME }
    }

    companion object {
        const val UNKNOWN_APP_KEY = "unknown"
        const val UNKNOWN_APP_NAME = "Unknown App"
    }
}

private fun JsonObject.firstNonBlankValue(vararg keys: String): String {
    keys.forEach { key ->
        val value = this[key]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        if (value.isNotBlank()) return value
    }
    return ""
}

private fun JsonObject.firstUidValue(vararg keys: String): Int? {
    keys.forEach { key ->
        val primitive = this[key]?.jsonPrimitive ?: return@forEach
        primitive.intOrNull?.let { if (it > 0) return it }
        primitive.longOrNull?.let { if (it in 1..Int.MAX_VALUE.toLong()) return it.toInt() }
        primitive.contentOrNull?.trim()?.toIntOrNull()?.let { if (it > 0) return it }
    }
    return null
}

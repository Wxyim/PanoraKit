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

package com.github.nomadboxlab.monadbox.data.repository

import android.app.Application
import android.net.Uri
import android.os.Build
import com.github.nomadboxlab.monadbox.domain.model.StructuredLogEntry
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Builds a standardised debug export ZIP bundle containing:
 * 1. **metadata.txt** — app version, platform version, device model, build type
 * 2. **state_snapshot.txt** — key runtime state at export time
 * 3. **recent_failures.txt** — last N structured failure entries
 * 4. **sanitized_logs.txt** — recent log entries with sensitive tokens redacted
 * 5. **config_version.txt** — current configuration version identifier
 */
class DebugExportBundleBuilder(
    private val application: Application,
    private val logRepository: LogRepository,
    private val structuredLogCollector: StructuredLogCollector,
) {

    companion object {
        private const val MAX_SANITIZED_LOG_LINES = 2000
        private const val MAX_FAILURE_ENTRIES = 100

        private val SENSITIVE_PATTERNS =
            listOf(
                Regex("""(password\s*[:=]\s*)\S+""", RegexOption.IGNORE_CASE),
                Regex("""(token\s*[:=]\s*)\S+""", RegexOption.IGNORE_CASE),
                Regex("""(secret\s*[:=]\s*)\S+""", RegexOption.IGNORE_CASE),
                Regex("""(authorization\s*[:=]\s*)\S+""", RegexOption.IGNORE_CASE),
                Regex("""(api[_-]?key\s*[:=]\s*)\S+""", RegexOption.IGNORE_CASE),
                Regex("""(private[_-]?key\s*[:=]\s*)\S+""", RegexOption.IGNORE_CASE),
            )
    }

    /**
     * Write a debug export ZIP to [targetUri].
     *
     * @param appVersionName e.g. `BuildConfig.VERSION_NAME`
     * @param appVersionCode e.g. `BuildConfig.VERSION_CODE`
     * @param buildType e.g. `BuildConfig.BUILD_TYPE`
     * @param runtimeStateSummary caller-provided key/value summary of runtime state
     * @param configVersionId current configuration version or profile UUID
     */
    suspend fun exportToUri(
        targetUri: Uri,
        appVersionName: String,
        appVersionCode: Int,
        buildType: String,
        runtimeStateSummary: Map<String, String> = emptyMap(),
        configVersionId: String? = null,
    ): Boolean =
        withContext(Dispatchers.IO) {
            try {
                application.contentResolver.openOutputStream(targetUri)?.use { rawOutput ->
                    ZipOutputStream(rawOutput).use { zip ->
                        writeMetadata(zip, appVersionName, appVersionCode, buildType)
                        writeStateSnapshot(zip, runtimeStateSummary)
                        writeRecentFailures(zip)
                        writeSanitizedLogs(zip)
                        writeConfigVersion(zip, configVersionId)
                    }
                } ?: return@withContext false
                true
            } catch (_: IOException) {
                false
            } catch (_: SecurityException) {
                false
            }
        }

    fun suggestedFileName(): String {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "monadbox_debug_$ts.zip"
    }

    private fun writeMetadata(
        zip: ZipOutputStream,
        appVersionName: String,
        appVersionCode: Int,
        buildType: String,
    ) {
        zip.putNextEntry(ZipEntry("metadata.txt"))
        val sb = StringBuilder()
        sb.appendLine("=== Debug Export Metadata ===")
        sb.appendLine("export_time=${System.currentTimeMillis()}")
        sb.appendLine("app_version_name=$appVersionName")
        sb.appendLine("app_version_code=$appVersionCode")
        sb.appendLine("build_type=$buildType")
        sb.appendLine("android_sdk=${Build.VERSION.SDK_INT}")
        sb.appendLine("android_release=${Build.VERSION.RELEASE}")
        sb.appendLine("device_model=${Build.MODEL}")
        sb.appendLine("device_manufacturer=${Build.MANUFACTURER}")
        sb.appendLine("device_product=${Build.PRODUCT}")
        sb.appendLine("supported_abis=${Build.SUPPORTED_ABIS.joinToString(",")}")
        zip.write(sb.toString().toByteArray())
        zip.closeEntry()
    }

    private fun writeStateSnapshot(zip: ZipOutputStream, runtimeStateSummary: Map<String, String>) {
        zip.putNextEntry(ZipEntry("state_snapshot.txt"))
        val sb = StringBuilder()
        sb.appendLine("=== Runtime State Snapshot ===")
        if (runtimeStateSummary.isEmpty()) {
            sb.appendLine("(no runtime state provided)")
        } else {
            runtimeStateSummary.forEach { (key, value) -> sb.appendLine("$key=$value") }
        }
        zip.write(sb.toString().toByteArray())
        zip.closeEntry()
    }

    private fun writeRecentFailures(zip: ZipOutputStream) {
        zip.putNextEntry(ZipEntry("recent_failures.txt"))
        val failures = structuredLogCollector.recentFailures(MAX_FAILURE_ENTRIES)
        val sb = StringBuilder()
        sb.appendLine("=== Recent Failure Chain (${failures.size} entries) ===")
        failures.forEach { entry -> sb.appendLine(formatStructuredEntry(entry)) }
        if (failures.isEmpty()) {
            sb.appendLine("(no recent failures)")
        }
        zip.write(sb.toString().toByteArray())
        zip.closeEntry()
    }

    private suspend fun writeSanitizedLogs(zip: ZipOutputStream) {
        zip.putNextEntry(ZipEntry("sanitized_logs.txt"))
        val sb = StringBuilder()
        sb.appendLine("=== Sanitized Logs ===")

        val entries = logRepository.readTempLogEntries(MAX_SANITIZED_LOG_LINES)
        entries.forEach { entry ->
            val sanitized = sanitizeLine("[${entry.time}] [${entry.level.name}] ${entry.message}")
            sb.appendLine(sanitized)
        }

        if (entries.isEmpty()) {
            sb.appendLine("(no log entries; recording may be inactive)")
        }
        zip.write(sb.toString().toByteArray())
        zip.closeEntry()
    }

    private fun writeConfigVersion(zip: ZipOutputStream, configVersionId: String?) {
        zip.putNextEntry(ZipEntry("config_version.txt"))
        val sb = StringBuilder()
        sb.appendLine("=== Configuration Version ===")
        sb.appendLine("config_version_id=${configVersionId ?: "(unknown)"}")
        zip.write(sb.toString().toByteArray())
        zip.closeEntry()
    }

    private fun formatStructuredEntry(entry: StructuredLogEntry): String {
        return buildString {
            append("[${entry.timestamp}]")
            append(" [${entry.level.name}]")
            append(" action=${entry.action}")
            append(" status=${entry.status}")
            entry.phase?.let { append(" phase=$it") }
            entry.objectId?.let { append(" objectId=$it") }
            entry.correlationId?.let { append(" correlationId=$it") }
            entry.configVersion?.let { append(" configVersion=$it") }
            entry.errorCategory?.let { append(" errorCategory=$it") }
            append(" | ${sanitizeLine(entry.message)}")
            entry.detail?.let { append(" | detail=${sanitizeLine(it)}") }
        }
    }

    private fun sanitizeLine(line: String): String {
        var result = line
        SENSITIVE_PATTERNS.forEach { pattern ->
            result =
                pattern.replace(result) { matchResult ->
                    val prefix = matchResult.groupValues[1]
                    "$prefix[REDACTED]"
                }
        }
        return result
    }
}

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

import android.app.Application
import com.github.yumelira.yumebox.data.model.CleanupPolicy
import com.github.yumelira.yumebox.data.repository.AppSettingsRepository
import com.github.yumelira.yumebox.data.repository.LogRepository
import com.github.yumelira.yumebox.service.runtime.records.ImportedDao
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class StorageCleanupManager(
    private val application: Application,
    private val appSettingsRepository: AppSettingsRepository,
    private val logRepository: LogRepository,
) {

    suspend fun runColdStartCleanup(): ColdStartCleanupResult =
        withContext(Dispatchers.IO) {
            val orphanRemovedCount = reclaimOrphanImportedProfileDirs()
            val processingRemovedCount = cleanupProcessingWorkspaceOnColdStart()
            ColdStartCleanupResult(
                orphanImportedDirsRemoved = orphanRemovedCount,
                processingArtifactsRemoved = processingRemovedCount,
            )
        }

    suspend fun runAutoCleanupIfNeeded(now: Long = System.currentTimeMillis()): CleanupResult? =
        withContext(Dispatchers.IO) {
            if (!appSettingsRepository.cleanupAutoEnabled.value) {
                return@withContext null
            }
            val intervalMs =
                appSettingsRepository.cleanupIntervalHours.value.coerceIn(
                    MIN_INTERVAL_HOURS,
                    MAX_INTERVAL_HOURS,
                ) * ONE_HOUR_MS
            val lastRunAt = appSettingsRepository.cleanupLastRunAt.value
            if (lastRunAt > 0L && now - lastRunAt < intervalMs) {
                return@withContext null
            }
            runCleanupInternal(force = false, now = now)
        }

    suspend fun runCleanupNow(now: Long = System.currentTimeMillis()): CleanupResult =
        withContext(Dispatchers.IO) { runCleanupInternal(force = false, now = now) }

    private suspend fun runCleanupInternal(force: Boolean, now: Long): CleanupResult {
        val thresholdMb =
            appSettingsRepository.cleanupThresholdMb.value.coerceIn(
                MIN_THRESHOLD_MB,
                MAX_THRESHOLD_MB,
            )
        val thresholdBytes = thresholdMb * MB_BYTES
        val policyConfig = resolvePolicyConfig(appSettingsRepository.cleanupPolicy.value)
        val beforeBytes = calculateStorageBytes()

        if (!force && beforeBytes < thresholdBytes) {
            return CleanupResult(
                executed = false,
                beforeBytes = beforeBytes,
                afterBytes = beforeBytes,
                freedBytes = 0L,
                thresholdBytes = thresholdBytes,
                archiveFileName = null,
                orphanImportedDirsRemoved = 0,
            )
        }

        val archiveFileName =
            runCatching { logRepository.persistReadableLogsForCleanup() }
                .onFailure { Timber.w(it, "cleanup: failed to persist readable logs") }
                .getOrNull()

        val orphanRemovedCount =
            runCatching { reclaimOrphanImportedProfileDirs() }
                .onFailure { Timber.w(it, "cleanup: failed to reclaim orphan imported dirs") }
                .getOrDefault(0)

        runCatching { reduceCacheFootprint(thresholdBytes, policyConfig) }
            .onFailure { Timber.w(it, "cleanup: failed to trim cacheDir") }

        val afterBytes = calculateStorageBytes()
        appSettingsRepository.cleanupLastRunAt.set(now)
        return CleanupResult(
            executed = true,
            beforeBytes = beforeBytes,
            afterBytes = afterBytes,
            freedBytes = (beforeBytes - afterBytes).coerceAtLeast(0L),
            thresholdBytes = thresholdBytes,
            archiveFileName = archiveFileName,
            orphanImportedDirsRemoved = orphanRemovedCount,
        )
    }

    private fun reclaimOrphanImportedProfileDirs(): Int {
        val importedRoot = File(application.filesDir, IMPORTED_DIR_NAME)
        val importedDirs = importedRoot.listFiles { file -> file.isDirectory } ?: return 0
        if (importedDirs.isEmpty()) return 0

        val knownProfileIds = ImportedDao.queryAllUUIDs().toHashSet()
        var removed = 0
        importedDirs.forEach { dir ->
            val uuid = dir.name.toUUIDOrNull() ?: return@forEach
            if (!knownProfileIds.contains(uuid)) {
                if (dir.deleteRecursively()) {
                    removed += 1
                }
            }
        }
        return removed
    }

    private fun cleanupProcessingWorkspaceOnColdStart(): Int {
        val processingRoot = File(application.filesDir, PROCESSING_DIR_NAME)
        val entries = processingRoot.listFiles() ?: return 0
        var removed = 0

        // `processing` is a dedicated transient workspace for profile update/validation.
        entries.forEach { entry ->
            if (entry.deleteRecursively()) {
                removed += 1
            }
        }
        return removed
    }

    private fun reduceCacheFootprint(thresholdBytes: Long, policyConfig: PolicyConfig) {
        val cacheRoot = application.cacheDir
        val cacheFiles =
            cacheRoot.walkTopDown().filter { it.isFile }.sortedBy { it.lastModified() }.toList()
        if (cacheFiles.isEmpty()) return

        val targetBytes = (thresholdBytes * policyConfig.targetUsageRatio).toLong()
        var totalBytes = calculateStorageBytes()
        if (totalBytes <= thresholdBytes) return

        val evictable =
            if (cacheFiles.size > policyConfig.retainRecentCacheFileCount) {
                cacheFiles.dropLast(policyConfig.retainRecentCacheFileCount)
            } else {
                emptyList()
            }

        for (file in evictable) {
            if (totalBytes <= targetBytes) break
            val length = runCatching { file.length() }.getOrDefault(0L)
            if (file.delete()) {
                totalBytes -= length
            }
        }

        // If still over target, continue trimming from the newest segment conservatively.
        if (totalBytes > targetBytes) {
            for (file in cacheFiles.asReversed()) {
                if (totalBytes <= targetBytes) break
                if (!file.exists()) continue
                val length = runCatching { file.length() }.getOrDefault(0L)
                if (file.delete()) {
                    totalBytes -= length
                }
            }
        }
    }

    private fun String.toUUIDOrNull(): UUID? {
        return runCatching { UUID.fromString(this) }.getOrNull()
    }

    private fun resolvePolicyConfig(policy: CleanupPolicy): PolicyConfig {
        return when (policy) {
            CleanupPolicy.Aggressive ->
                PolicyConfig(targetUsageRatio = 0.70, retainRecentCacheFileCount = 8)
            CleanupPolicy.Balanced ->
                PolicyConfig(targetUsageRatio = 0.85, retainRecentCacheFileCount = 32)
            CleanupPolicy.Conservative ->
                PolicyConfig(targetUsageRatio = 0.95, retainRecentCacheFileCount = 96)
        }
    }

    private fun calculateStorageBytes(): Long {
        return directorySize(application.filesDir) + directorySize(application.cacheDir)
    }

    private fun directorySize(directory: File): Long {
        if (!directory.exists()) return 0L
        return runCatching { directory.walkTopDown().filter { it.isFile }.sumOf { it.length() } }
            .getOrDefault(0L)
    }

    data class CleanupResult(
        val executed: Boolean,
        val beforeBytes: Long,
        val afterBytes: Long,
        val freedBytes: Long,
        val thresholdBytes: Long,
        val archiveFileName: String?,
        val orphanImportedDirsRemoved: Int,
    )

    data class ColdStartCleanupResult(
        val orphanImportedDirsRemoved: Int,
        val processingArtifactsRemoved: Int,
    )

    private data class PolicyConfig(
        val targetUsageRatio: Double,
        val retainRecentCacheFileCount: Int,
    )

    private companion object {
        private const val IMPORTED_DIR_NAME = "imported"
        private const val PROCESSING_DIR_NAME = "processing"
        private const val MB_BYTES = 1024L * 1024L
        private const val ONE_HOUR_MS = 60L * 60L * 1000L
        private const val MIN_THRESHOLD_MB = 64
        private const val MAX_THRESHOLD_MB = 4096
        private const val MIN_INTERVAL_HOURS = 1
        private const val MAX_INTERVAL_HOURS = 48
    }
}

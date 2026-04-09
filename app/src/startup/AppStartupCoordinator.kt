package com.github.yumelira.yumebox.startup

import android.app.Application
import com.github.yumelira.yumebox.common.util.PlatformIdentifier
import com.github.yumelira.yumebox.common.util.StorageCleanupManager
import com.github.yumelira.yumebox.data.repository.ConnectionActivityRepository
import com.github.yumelira.yumebox.data.repository.TargetSiteTrafficCollector
import com.github.yumelira.yumebox.data.repository.TrafficStatisticsCollector
import com.github.yumelira.yumebox.data.store.AppSettingsStorage
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.tukaani.xz.XZInputStream
import timber.log.Timber

class AppStartupCoordinator(
    private val application: Application,
    private val koin: Koin,
    private val appScope: CoroutineScope,
    private val appSettingsStorage: AppSettingsStorage,
) {
    companion object {
        private const val TEMP_FILE_PREFIX = "temp_"
        private const val TEMP_FILE_SUFFIX = ".yaml"
        private const val TEMP_FILE_STALE_MS = 24L * 60L * 60L * 1000L
    }

    @Volatile private var deferredStartupInitialized = false

    fun ensureDeferredStartupInitialized() {
        if (
            !shouldInitializeDeferredStartup(
                initialSetupCompleted = appSettingsStorage.initialSetupCompleted.value,
                alreadyInitialized = deferredStartupInitialized,
            )
        ) {
            return
        }

        synchronized(this) {
            if (deferredStartupInitialized) {
                return
            }
            deferredStartupInitialized = true
        }

        appScope.launch(Dispatchers.IO) {
            cleanupLegacyData()
            runColdStartStorageCleanup()
            extractGeoFiles()
            koin
                .get<StorageCleanupScheduler>()
                .sync(enabled = appSettingsStorage.cleanupAutoEnabled.value)
        }
        appScope.launch {
            val proxyFacade = koin.get<ProxyFacade>()
            proxyFacade.warmUpProxyGroups()
            koin.get<RuntimeLogRecordingCoordinator>().start()
            koin.get<TrafficStatisticsCollector>()
            koin.get<ConnectionActivityRepository>()
            koin.get<TargetSiteTrafficCollector>()
            PlatformIdentifier.getPlatformIdentifier()
        }
    }

    private fun cleanupLegacyData() {
        arrayOf("substore", "nodejs", "substore-static").forEach { dir ->
            File(application.filesDir, dir).let { file ->
                if (file.exists()) {
                    file.deleteRecursively()
                }
            }
        }
        cleanupStaleTempDownloads()
    }

    private fun cleanupStaleTempDownloads() {
        val now = System.currentTimeMillis()
        runCatching {
            application.cacheDir
                .listFiles { file ->
                    isStaleTempDownloadCandidate(
                        fileName = file.name,
                        isRegularFile = file.isFile,
                        lastModifiedAt = file.lastModified(),
                        now = now,
                        staleAfterMs = TEMP_FILE_STALE_MS,
                    )
                }
                ?.forEach { it.delete() }
        }
    }

    private fun extractGeoFiles() {
        val clashDir = File(application.filesDir, "clash").apply { mkdirs() }
        val geoFiles = listOf("geoip.metadb", "geosite.dat", "ASN.mmdb")
        val failedFiles = mutableListOf<String>()

        geoFiles.forEach { filename ->
            val targetFile = File(clashDir, filename)
            if (!targetFile.exists()) {
                try {
                    if (!extractCompressedAssetIfExists("$filename.xz", targetFile)) {
                        application.assets.open(filename).use { input ->
                            targetFile.outputStream().use { output -> input.copyTo(output) }
                        }
                    }
                } catch (_: IOException) {
                    failedFiles += filename
                }
            }
        }

        if (failedFiles.isNotEmpty()) {
            Timber.w("Failed to extract geo files: ${failedFiles.joinToString()}")
        }
    }

    private fun extractCompressedAssetIfExists(assetName: String, targetFile: File): Boolean {
        return try {
            application.assets.open(assetName).use { input ->
                XZInputStream(input.buffered()).use { xzInput ->
                    targetFile.outputStream().buffered().use { output -> xzInput.copyTo(output) }
                }
            }
            true
        } catch (_: IOException) {
            false
        }
    }

    private suspend fun runColdStartStorageCleanup() {
        runCatching {
                val manager = koin.get<StorageCleanupManager>()
                val result = manager.runColdStartCleanup()
                if (result.orphanImportedDirsRemoved > 0 || result.processingArtifactsRemoved > 0) {
                    Timber.i(
                        "Cold start cleanup finished: orphanImported=%d processingArtifacts=%d",
                        result.orphanImportedDirsRemoved,
                        result.processingArtifactsRemoved,
                    )
                }
            }
            .onFailure { error -> Timber.w(error, "Cold start cleanup failed") }
    }
}

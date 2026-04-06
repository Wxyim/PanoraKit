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

package com.github.yumelira.yumebox

import android.app.Application
import com.github.nomadboxlab.monadbox.BuildConfig
import com.github.yumelira.yumebox.common.runtime.StartupGate
import com.github.yumelira.yumebox.common.util.AppLanguageManager
import com.github.yumelira.yumebox.common.util.PlatformIdentifier
import com.github.yumelira.yumebox.common.util.StorageCleanupManager
import com.github.yumelira.yumebox.core.Global
import com.github.yumelira.yumebox.data.repository.ConnectionActivityRepository
import com.github.yumelira.yumebox.data.repository.TrafficStatisticsCollector
import com.github.yumelira.yumebox.data.store.AppSettingsStorage
import com.github.yumelira.yumebox.di.APPLICATION_SCOPE_NAME
import com.github.yumelira.yumebox.di.appModule
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.tencent.mmkv.MMKV
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.tukaani.xz.XZInputStream
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var instance: App
            private set

        private const val TEMP_FILE_PREFIX = "temp_"
        private const val TEMP_FILE_SUFFIX = ".yaml"
        private const val TEMP_FILE_STALE_MS = 24L * 60L * 60L * 1000L
        private const val CLEANUP_POLL_INTERVAL_MS = 15L * 60L * 1000L
    }

    private lateinit var koin: Koin
    private lateinit var appScope: CoroutineScope
    private lateinit var appSettingsStorage: AppSettingsStorage

    @Volatile private var deferredStartupInitialized = false

    @Volatile private var cleanupSchedulerStarted = false

    override fun onCreate() {
        super.onCreate()

        instance = this
        if (BuildConfig.DEBUG && Timber.forest().isEmpty()) {
            Timber.plant(Timber.DebugTree())
        }

        StartupGate.verify(this)

        Global.init(this)
        MMKV.initialize(this)

        val koinApp = startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        koin = koinApp.koin
        appSettingsStorage = koin.get()
        appScope = koin.get(named(APPLICATION_SCOPE_NAME))

        AppLanguageManager.apply(appSettingsStorage.appLanguage.value)
        ensureDeferredStartupInitialized()
    }

    fun ensureDeferredStartupInitialized() {
        if (
            !::appSettingsStorage.isInitialized || !appSettingsStorage.initialSetupCompleted.value
        ) {
            return
        }
        if (deferredStartupInitialized) {
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
            ensureCleanupSchedulerStarted()
        }
        appScope.launch {
            koin.get<ProxyFacade>().warmUpProxyGroups()
            koin.get<TrafficStatisticsCollector>()
            koin.get<ConnectionActivityRepository>()
            PlatformIdentifier.getPlatformIdentifier()
        }
    }

    private fun cleanupLegacyData() {
        // Cleanup old Sub-store data and its legacy node.js env
        arrayOf(
                "substore",
                "nodejs", // Only if explicitly used by substore
                "substore-static",
            )
            .forEach { dir ->
                File(filesDir, dir).let { file -> if (file.exists()) file.deleteRecursively() }
            }
        cleanupStaleTempDownloads()
    }

    private fun cleanupStaleTempDownloads() {
        val now = System.currentTimeMillis()
        runCatching {
            cacheDir
                .listFiles { file ->
                    file.isFile &&
                        file.name.startsWith(TEMP_FILE_PREFIX) &&
                        file.name.endsWith(TEMP_FILE_SUFFIX) &&
                        now - file.lastModified() >= TEMP_FILE_STALE_MS
                }
                ?.forEach { it.delete() }
        }
    }

    private fun extractGeoFiles() {
        val clashDir = File(filesDir, "clash").apply { mkdirs() }
        val geoFiles = listOf("geoip.metadb", "geosite.dat", "ASN.mmdb")
        val failedFiles = mutableListOf<String>()

        geoFiles.forEach { filename ->
            val targetFile = File(clashDir, filename)
            if (!targetFile.exists()) {
                try {
                    if (!extractCompressedAssetIfExists("$filename.xz", targetFile)) {
                        assets.open(filename).use { input ->
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
            assets.open(assetName).use { input ->
                XZInputStream(input.buffered()).use { xzInput ->
                    targetFile.outputStream().buffered().use { output -> xzInput.copyTo(output) }
                }
            }
            true
        } catch (_: IOException) {
            false
        }
    }

    private fun ensureCleanupSchedulerStarted() {
        if (cleanupSchedulerStarted) return
        synchronized(this) {
            if (cleanupSchedulerStarted) return
            cleanupSchedulerStarted = true
        }

        appScope.launch(Dispatchers.IO) {
            val manager = koin.get<StorageCleanupManager>()
            while (true) {
                runCatching { manager.runAutoCleanupIfNeeded() }
                delay(CLEANUP_POLL_INTERVAL_MS)
            }
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

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

package com.github.nomadboxlab.monadbox

import android.app.Application
import com.github.nomadboxlab.monadbox.common.runtime.StartupGate
import com.github.nomadboxlab.monadbox.core.Global
import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import com.github.nomadboxlab.monadbox.data.store.LegacyMmkvImporter
import com.github.nomadboxlab.monadbox.data.store.LegacyProfileLinksImporter
import com.github.nomadboxlab.monadbox.di.APPLICATION_IO_SCOPE_NAME
import com.github.nomadboxlab.monadbox.di.APPLICATION_SCOPE_NAME
import com.github.nomadboxlab.monadbox.di.appModule
import com.github.nomadboxlab.monadbox.feature.settings.AppLanguageManager
import com.github.nomadboxlab.monadbox.startup.AppStartupCoordinator
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import timber.log.Timber

class App : Application() {

    companion object {
        private const val TEMP_FILE_PREFIX = "temp_"
        private const val TEMP_FILE_SUFFIX = ".yaml"
        private const val TEMP_FILE_STALE_MS = 24L * 60L * 60L * 1000L
        private const val CLEANUP_POLL_INTERVAL_MS = 15L * 60L * 1000L
    }

    private lateinit var koin: Koin
    private lateinit var appSettingsStorage: AppSettingsStorage
    private lateinit var startupCoordinator: AppStartupCoordinator

    override fun onCreate() {
        super.onCreate()

        LocaleBootstrap.install(this)

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

        Timber.plant(com.github.nomadboxlab.monadbox.startup.StructuredLoggingTree(koin.get()))

        startupCoordinator =
            AppStartupCoordinator(
                application = this,
                koin = koin,
                appScope = koin.get(named(APPLICATION_SCOPE_NAME)),
                appSettingsStorage = appSettingsStorage,
            )

        AppLanguageManager.apply(appSettingsStorage.appLanguage.value)

        // One-shot import of legacy MMKV-backed prefs into DataStore-backed stores.
        val importer = koin.get<LegacyMmkvImporter>()
        val profileLinksImporter = koin.get<LegacyProfileLinksImporter>()
        val ioScope = koin.get<CoroutineScope>(named(APPLICATION_IO_SCOPE_NAME))
        ioScope.launch {
            importer.importIfNeeded()
            profileLinksImporter.importIfNeeded()
        }
        ensureDeferredStartupInitialized()
    }

    fun ensureDeferredStartupInitialized() {
        if (!::appSettingsStorage.isInitialized) {
            return
        }
        startupCoordinator.ensureDeferredStartupInitialized()
    }
}

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
import com.github.yumelira.yumebox.core.Global
import com.github.yumelira.yumebox.data.store.AppSettingsStorage
import com.github.yumelira.yumebox.di.APPLICATION_SCOPE_NAME
import com.github.yumelira.yumebox.di.appModule
import com.github.yumelira.yumebox.startup.AppStartupCoordinator
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
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
    private lateinit var appSettingsStorage: AppSettingsStorage
    private lateinit var startupCoordinator: AppStartupCoordinator

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
        startupCoordinator =
            AppStartupCoordinator(
                application = this,
                koin = koin,
                appScope = koin.get(named(APPLICATION_SCOPE_NAME)),
                appSettingsStorage = appSettingsStorage,
            )

        AppLanguageManager.apply(appSettingsStorage.appLanguage.value)
        ensureDeferredStartupInitialized()
    }

    fun ensureDeferredStartupInitialized() {
        if (!::appSettingsStorage.isInitialized) {
            return
        }
        startupCoordinator.ensureDeferredStartupInitialized()
    }
}

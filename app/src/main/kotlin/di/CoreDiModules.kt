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

package com.github.nomadboxlab.monadbox.di

import com.github.nomadboxlab.monadbox.common.util.StorageCleanupManager
import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.data.repository.*
import com.github.nomadboxlab.monadbox.data.store.*
import com.github.nomadboxlab.monadbox.domain.app.AppInfo
import com.github.nomadboxlab.monadbox.domain.deeplink.DeepLinkBus
import com.github.nomadboxlab.monadbox.runtime.client.AppIdentityResolver
import com.github.nomadboxlab.monadbox.runtime.client.ProfilesProvider
import com.github.nomadboxlab.monadbox.runtime.client.ProfilesRepository
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationCoordinator
import com.github.nomadboxlab.monadbox.runtime.client.root.RootTunReloadDispatcher
import com.github.nomadboxlab.monadbox.runtime.client.root.RootTunReloadScheduler
import com.github.nomadboxlab.monadbox.runtime.client.usecase.AutoStartProxyUseCase
import com.github.nomadboxlab.monadbox.startup.RuntimeLogRecordingCoordinator
import com.github.nomadboxlab.monadbox.startup.StartupConfigRefreshCoordinator
import com.github.nomadboxlab.monadbox.startup.StorageCleanupScheduler
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APPLICATION_SCOPE_NAME = "applicationScope"
const val APPLICATION_IO_SCOPE_NAME = "applicationIoScope"

val appFoundationModule = module {
    single<CoroutineScope>(named(APPLICATION_SCOPE_NAME)) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<CoroutineScope>(named(APPLICATION_IO_SCOPE_NAME)) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    single { MMKVProvider() }
    single<MMKV>(named(StoreIds.PROFILES)) { get<MMKVProvider>().getMMKV(StoreIds.PROFILES) }
    single<MMKV>(named(StoreIds.SETTINGS)) { get<MMKVProvider>().getMMKV(StoreIds.SETTINGS) }
    single<MMKV>(named(StoreIds.NETWORK_SETTINGS)) {
        get<MMKVProvider>().getMMKV(StoreIds.NETWORK_SETTINGS)
    }
    single<MMKV>(named(StoreIds.PROXY_DISPLAY)) {
        get<MMKVProvider>().getMMKV(StoreIds.PROXY_DISPLAY)
    }
    single<MMKV>(named(StoreIds.TRAFFIC_STATISTICS)) {
        get<MMKVProvider>().getMMKV(StoreIds.TRAFFIC_STATISTICS)
    }
    single<MMKV>(named(StoreIds.PROFILE_LINKS)) {
        get<MMKVProvider>().getMMKV(StoreIds.PROFILE_LINKS)
    }
    single<MMKV>(named(StoreIds.SERVICE_CACHE)) {
        get<MMKVProvider>().getMMKV(StoreIds.SERVICE_CACHE)
    }
    single<MMKV>(named(StoreIds.OVERRIDE_BINDINGS)) {
        get<MMKVProvider>().getMMKV(StoreIds.OVERRIDE_BINDINGS)
    }

    single { AppSettingsStorage(get<MMKV>(named(StoreIds.SETTINGS))) }
    single { NetworkSettingsStorage(get(named(StoreIds.NETWORK_SETTINGS))) }
    single(named(StoreIds.PROFILE_LINKS)) {
        DataStoreFactory.create(androidContext(), StoreIds.PROFILE_LINKS)
    }
    single {
        com.github.nomadboxlab.monadbox.data.persistence.MonadBoxDatabase.create(androidContext())
    }
    single {
        get<com.github.nomadboxlab.monadbox.data.persistence.MonadBoxDatabase>().profileLinkDao()
    }
    single {
        ProfileLinksStorage(
            dataStore = get(named(StoreIds.PROFILE_LINKS)),
            writeScope = get(named(APPLICATION_IO_SCOPE_NAME)),
            dao = get(),
        )
    }
    single {
        LegacyProfileLinksImporter(
            dataStore = get(named(StoreIds.PROFILE_LINKS)),
            mmkv = get(named(StoreIds.PROFILE_LINKS)),
            dao = get(),
        )
    }
    single(named(StoreIds.PROXY_DISPLAY)) {
        DataStoreFactory.create(androidContext(), StoreIds.PROXY_DISPLAY)
    }
    single {
        ProxyDisplaySettingsStore(
            dataStore = get(named(StoreIds.PROXY_DISPLAY)),
            writeScope = get(named(APPLICATION_IO_SCOPE_NAME)),
        )
    }
    single {
        LegacyMmkvImporter(
            dataStore = get(named(StoreIds.PROXY_DISPLAY)),
            mmkv = get(named(StoreIds.PROXY_DISPLAY)),
        )
    }
    single { TrafficStatisticsStore(get(named(StoreIds.TRAFFIC_STATISTICS))) }

    single { DeepLinkBus() }

    single {
        AppInfo(
            versionName = com.github.nomadboxlab.monadbox.BuildConfig.VERSION_NAME,
            versionCode = com.github.nomadboxlab.monadbox.BuildConfig.VERSION_CODE,
            buildType = com.github.nomadboxlab.monadbox.BuildConfig.BUILD_TYPE,
            mihomoVersion = com.github.nomadboxlab.monadbox.BuildConfig.MIHOMO_VERSION,
            isDebug = com.github.nomadboxlab.monadbox.BuildConfig.DEBUG,
        )
    }
}

val appDataRuntimeModule = module {
    single { AppSettingsRepository(get()) }
    single { LogRepository(androidApplication(), get()) }
    single<LogProvider> { get<LogRepository>() }
    single { com.github.nomadboxlab.monadbox.domain.model.StructuredLogCollector() }
    single { DebugExportBundleBuilder(androidApplication(), get(), get()) }
    single { StorageCleanupManager(androidApplication(), get(), get(), get()) }
    single { StorageCleanupScheduler(androidContext()) }
    single<com.github.nomadboxlab.monadbox.feature.settings.CleanupController> {
        com.github.nomadboxlab.monadbox.common.util.CleanupControllerImpl(get(), get())
    }
    single { NetworkInfoService() }
    single { ProxyChainResolver() }
    single { OverrideRepository(androidContext(), get()) }
    single<OverrideProvider> { get<OverrideRepository>() }
    single { ProvidersRepository(androidContext()) }
    single<ProvidersProvider> { get<ProvidersRepository>() }

    single { OverrideConfigRepository(androidContext()) }
    single<OverrideConfigProvider> { get<OverrideConfigRepository>() }

    single { ProfileBindingRepository(androidContext()) }
    single<ProfileBindingProvider> { get<ProfileBindingRepository>() }

    single { OverrideResolver(get(), get()) }
    single { RootTunReloadScheduler(androidContext(), get(named(APPLICATION_SCOPE_NAME))) }
    single<RootTunReloadDispatcher> { get<RootTunReloadScheduler>() }
    single { RuntimeMutationCoordinator(androidContext(), get()) }
    single { OverrideService(get(), get()) }
    single { ActiveProfileOverrideReloader(get(), get(), get()) }
    single { StartupConfigRefreshCoordinator(get(), get(), get(), get(), get(), get(), get()) }

    single { com.github.nomadboxlab.monadbox.remote.ServiceClient }
    single { ProxyFacade(androidContext(), get(), get(named(APPLICATION_SCOPE_NAME))) }
    single { ProfilesRepository(androidContext(), get()) }
    single<ProfilesProvider> { get<ProfilesRepository>() }
    single { RuntimeControlCoordinator(get(), get(), get()) }
    single {
        AutoStartProxyUseCase(
            profilesRepository = get(),
            runtimeControlCoordinator = get(),
            proxyFacade = get(),
            isServiceRunning = {
                com.github.nomadboxlab.monadbox.service.StatusProvider.serviceRunning
            },
        )
    }
    single { AppIdentityResolver(androidContext()) }

    single { TrafficStatisticsCollector(get(), get(), get(named(APPLICATION_IO_SCOPE_NAME))) }
    single { ConnectionActivityRepository(get(), get(named(APPLICATION_SCOPE_NAME))) }
    single<ConnectionActivityProvider> { get<ConnectionActivityRepository>() }
    single {
        TargetSiteTrafficCollector(get(), get(), get(), get(named(APPLICATION_IO_SCOPE_NAME)))
    }
    single { RuntimeLogRecordingCoordinator(androidApplication(), get()) }
}

val coreDiModules: List<Module> = listOf(appFoundationModule, appDataRuntimeModule)

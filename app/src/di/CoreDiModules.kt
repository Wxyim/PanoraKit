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

package com.github.yumelira.yumebox.di

import com.github.yumelira.yumebox.common.util.StorageCleanupManager
import com.github.yumelira.yumebox.core.StoreIds
import com.github.yumelira.yumebox.data.repository.*
import com.github.yumelira.yumebox.data.store.*
import com.github.yumelira.yumebox.runtime.client.AppIdentityResolver
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
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
    single { ProfileLinksStorage(get(named(StoreIds.PROFILE_LINKS))) }
    single { ProxyDisplaySettingsStore(get(named(StoreIds.PROXY_DISPLAY))) }
    single { TrafficStatisticsStore(get(named(StoreIds.TRAFFIC_STATISTICS))) }
}

val appDataRuntimeModule = module {
    single { AppSettingsRepository(get()) }
    single { LogRepository(androidApplication(), get()) }
    single { StorageCleanupManager(androidApplication(), get(), get()) }
    single { NetworkInfoService() }
    single { ProxyChainResolver() }
    single { OverrideRepository(androidContext(), get()) }
    single { ProvidersRepository(androidContext()) }

    single { OverrideConfigRepository(androidContext()) }
    single<OverrideConfigProvider> { get<OverrideConfigRepository>() }

    single { ProfileBindingRepository(androidContext()) }
    single<ProfileBindingProvider> { get<ProfileBindingRepository>() }

    single { OverrideResolver(get(), get()) }
    single { OverrideService(androidContext(), get()) }
    single { ActiveProfileOverrideReloader(get(), get(), get()) }

    single { com.github.yumelira.yumebox.remote.ServiceClient }
    single { ProxyFacade(androidContext(), get()) }
    single { ProfilesRepository(androidContext()) }
    single { AppIdentityResolver(androidContext()) }

    single { TrafficStatisticsCollector(get(), get(), get(named(APPLICATION_IO_SCOPE_NAME))) }
    single {
        AppTrafficStatisticsCollector(
            androidContext(),
            get(),
            get(),
            get(),
            get(named(APPLICATION_IO_SCOPE_NAME)),
        )
    }
    single { ConnectionActivityRepository(get(), get(named(APPLICATION_SCOPE_NAME))) }
}

val coreDiModules: List<Module> = listOf(appFoundationModule, appDataRuntimeModule)

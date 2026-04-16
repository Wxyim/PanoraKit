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

import com.github.yumelira.yumebox.data.repository.LogRecordGateway
import com.github.yumelira.yumebox.presentation.meta.EffectiveRuleSummaryRepository
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionExecutor
import com.github.yumelira.yumebox.presentation.runtime.VpnPermissionCoordinator
import com.github.yumelira.yumebox.screen.home.HomeViewModel
import com.github.yumelira.yumebox.screen.log.LogViewModel
import com.github.yumelira.yumebox.screen.profiles.ProfilesViewModel
import com.github.yumelira.yumebox.screen.settings.AccessControlViewModel
import com.github.yumelira.yumebox.screen.settings.AppSettingsViewModel
import com.github.yumelira.yumebox.screen.settings.MetaFeatureViewModel
import com.github.yumelira.yumebox.screen.settings.NetworkSettingsViewModel
import com.github.yumelira.yumebox.service.LogRecordServiceGateway
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appIntegrationModule = module {
    single<LogRecordGateway> { LogRecordServiceGateway() }
    single { EffectiveRuleSummaryRepository(androidApplication(), get(), get()) }
    single { RuntimeActionExecutor(get(), get()) }
    single { VpnPermissionCoordinator(get(named(APPLICATION_SCOPE_NAME))) }
}

val appViewModelModule = module {
    viewModel { AppSettingsViewModel(get(), get(), get()) }
    viewModel {
        HomeViewModel(
            androidApplication(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        ProfilesViewModel(androidApplication(), get(), get(), get(), get(), get(), get(), get())
    }
    viewModel { NetworkSettingsViewModel(androidApplication(), get(), get(), get(), get(), get()) }
    viewModel { AccessControlViewModel(androidApplication(), get(), get(), get(), get()) }
    viewModel { LogViewModel(get(), get(), get()) }
    viewModel { MetaFeatureViewModel(get(), get(), get(), get()) }
}

val appModule: List<Module> =
    coreDiModules +
        listOf(appIntegrationModule, appViewModelModule) +
        featureProxyModules +
        featureOverrideModules +
        featureMetaModules

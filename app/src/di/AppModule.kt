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

import com.github.nomadboxlab.monadbox.data.repository.LogRecordGateway
import com.github.nomadboxlab.monadbox.presentation.meta.EffectiveRuleSummaryRepository
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.VpnPermissionCoordinator
import com.github.nomadboxlab.monadbox.screen.home.HomeViewModel
import com.github.nomadboxlab.monadbox.screen.log.LogViewModel
import com.github.nomadboxlab.monadbox.screen.profiles.ProfilesViewModel
import com.github.nomadboxlab.monadbox.screen.settings.AccessControlViewModel
import com.github.nomadboxlab.monadbox.screen.settings.AppSettingsViewModel
import com.github.nomadboxlab.monadbox.screen.settings.MetaFeatureViewModel
import com.github.nomadboxlab.monadbox.screen.settings.NetworkSettingsViewModel
import com.github.nomadboxlab.monadbox.service.LogRecordServiceGateway
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

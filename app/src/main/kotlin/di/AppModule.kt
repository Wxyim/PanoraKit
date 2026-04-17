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
import com.github.nomadboxlab.monadbox.feature.home.di.homeDiModule
import com.github.nomadboxlab.monadbox.feature.log.di.logDiModule
import com.github.nomadboxlab.monadbox.feature.profiles.di.profilesDiModule
import com.github.nomadboxlab.monadbox.feature.settings.di.settingsDiModule
import com.github.nomadboxlab.monadbox.presentation.meta.EffectiveRuleSummaryRepository
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.VpnPermissionCoordinator
import com.github.nomadboxlab.monadbox.service.LogRecordServiceGateway
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appIntegrationModule = module {
    single<LogRecordGateway> { LogRecordServiceGateway() }
    single { EffectiveRuleSummaryRepository(androidApplication(), get(), get()) }
    single { RuntimeActionExecutor(get(), get()) }
    single { VpnPermissionCoordinator(get(named(APPLICATION_SCOPE_NAME))) }
}

/**
 * Aggregation of per-screen view-model modules. Each screen now owns its own Koin module file
 * co-located with the screen folder, as a structural precursor to extracting the screen into its
 * own Gradle module.
 */
val screenViewModelModules: List<Module> =
    listOf(homeDiModule, profilesDiModule, settingsDiModule, logDiModule)

val appModule: List<Module> =
    coreDiModules +
        listOf(appIntegrationModule, onboardingDiModule) +
        screenViewModelModules +
        featureProxyModules +
        featureOverrideModules +
        featureMetaModules

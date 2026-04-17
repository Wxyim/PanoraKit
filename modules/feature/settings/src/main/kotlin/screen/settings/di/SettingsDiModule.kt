/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.feature.settings.di

import com.github.nomadboxlab.monadbox.feature.settings.AccessControlViewModel
import com.github.nomadboxlab.monadbox.feature.settings.AppSettingsViewModel
import com.github.nomadboxlab.monadbox.feature.settings.MetaFeatureViewModel
import com.github.nomadboxlab.monadbox.feature.settings.NetworkSettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsDiModule = module {
    viewModel { AppSettingsViewModel(get(), get()) }
    viewModel { NetworkSettingsViewModel(androidApplication(), get(), get(), get(), get(), get()) }
    viewModel { AccessControlViewModel(androidApplication(), get(), get(), get(), get()) }
    viewModel { MetaFeatureViewModel(get(), get(), get(), get()) }
}

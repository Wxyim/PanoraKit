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

package com.github.nomadboxlab.monadbox.feature.home.di

import com.github.nomadboxlab.monadbox.feature.home.HomeViewModel
import com.github.nomadboxlab.monadbox.feature.home.usecase.RefreshHomeEntryDataUseCase
import com.github.nomadboxlab.monadbox.feature.home.usecase.ReloadHomeProfileUseCase
import com.github.nomadboxlab.monadbox.feature.home.usecase.StartHomeProxyUseCase
import com.github.nomadboxlab.monadbox.feature.home.usecase.StopHomeProxyUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeDiModule = module {
    single { RefreshHomeEntryDataUseCase(get(), get()) }
    single { ReloadHomeProfileUseCase(get(), get()) }
    single { StartHomeProxyUseCase(androidApplication(), get(), get(), get(), get()) }
    single { StopHomeProxyUseCase(get()) }

    viewModel {
        HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
}

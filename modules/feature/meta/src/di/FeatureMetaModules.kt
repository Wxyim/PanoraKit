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

import com.github.nomadboxlab.monadbox.data.repository.ConnectionActivityRepository
import com.github.nomadboxlab.monadbox.data.repository.ProxyChainResolver
import com.github.nomadboxlab.monadbox.data.store.TrafficStatisticsStore
import com.github.nomadboxlab.monadbox.feature.meta.presentation.viewmodel.ConnectionViewModel
import com.github.nomadboxlab.monadbox.feature.meta.presentation.viewmodel.TrafficStatisticsViewModel
import com.github.nomadboxlab.monadbox.runtime.client.AppIdentityResolver
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureMetaViewModelModule = module {
    viewModel { ConnectionViewModel(get<ConnectionActivityRepository>()) }
    viewModel {
        TrafficStatisticsViewModel(
            get<TrafficStatisticsStore>(),
            get<ConnectionActivityRepository>(),
            get<ProxyFacade>(),
            get<ProxyChainResolver>(),
            get<AppIdentityResolver>(),
        )
    }
}

val featureMetaModules = listOf(featureMetaViewModelModule)

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
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionAppIdentityLookup
import com.github.nomadboxlab.monadbox.feature.meta.api.TrafficStatisticsExplorer
import com.github.nomadboxlab.monadbox.feature.meta.presentation.contract.DefaultTrafficStatisticsExplorer
import com.github.nomadboxlab.monadbox.feature.meta.presentation.contract.RuntimeConnectionAppIdentityLookup
import com.github.nomadboxlab.monadbox.runtime.client.AppIdentityResolver
import org.koin.dsl.module

val featureMetaContractModule = module {
    single<TrafficStatisticsExplorer> {
        DefaultTrafficStatisticsExplorer(
            get<TrafficStatisticsStore>(),
            get<ConnectionActivityRepository>(),
            get<AppIdentityResolver>(),
        )
    }
    single<ConnectionAppIdentityLookup> {
        RuntimeConnectionAppIdentityLookup(get<AppIdentityResolver>())
    }
}

val featureMetaModules = listOf(featureMetaContractModule)

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

package com.github.nomadboxlab.monadbox.runtime.client

import android.content.Context
import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.core.model.Provider
import com.github.nomadboxlab.monadbox.remote.ServiceClient
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeConnectionReader
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeProfileRef
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeProviderGateway
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProxyFacadeRuntimeStateReader(proxyFacade: ProxyFacade, scope: CoroutineScope) :
    RuntimeStateReader {
    override val isRuntimeRunning: StateFlow<Boolean> = proxyFacade.isRunning

    override val runtimeTrafficTotal: StateFlow<Long> = proxyFacade.trafficTotal

    override val currentRuntimeProfile: StateFlow<RuntimeProfileRef?> =
        proxyFacade.currentProfile
            .map { profile ->
                profile?.let { RuntimeProfileRef(id = it.uuid.toString(), name = it.name) }
            }
            .stateIn(scope, SharingStarted.Eagerly, null)
}

class ServiceClientRuntimeConnectionReader(private val context: Context) : RuntimeConnectionReader {
    override suspend fun queryConnections(): List<ConnectionInfo> {
        ServiceClient.connect(context)
        return ServiceClient.clash().queryConnections().connections.orEmpty()
    }
}

class ServiceClientRuntimeProviderGateway(private val context: Context) : RuntimeProviderGateway {
    override suspend fun queryProviders(): List<Provider> {
        ServiceClient.connect(context)
        return ServiceClient.clash().queryProviders()
    }

    override suspend fun updateProvider(type: Provider.Type, name: String) {
        ServiceClient.connect(context)
        ServiceClient.clash().updateProvider(type, name)
    }
}

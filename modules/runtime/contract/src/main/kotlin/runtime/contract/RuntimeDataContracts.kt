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

package com.github.nomadboxlab.monadbox.runtime.contract

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.core.model.Provider
import kotlinx.coroutines.flow.StateFlow

data class RuntimeProfileRef(val id: String, val name: String?)

interface RuntimeActiveProfileReader {
    suspend fun queryActiveRuntimeProfile(): RuntimeProfileRef?
}

interface RuntimeOverrideChangeNotifier {
    fun notifyOverrideBindingsChanged()
}

interface RuntimeStateReader {
    val isRuntimeRunning: StateFlow<Boolean>

    /**
     * True while the app has a started activity and the screen is on.
     *
     * Consumers use this to gate polling that only feeds the UI, so a backgrounded or screen-off
     * app does not keep querying the core.
     */
    val isAppActive: StateFlow<Boolean>

    val runtimeTrafficTotal: StateFlow<Long>
    val currentRuntimeProfile: StateFlow<RuntimeProfileRef?>

    /**
     * Forces a fresh read of the runtime's cumulative traffic counters into [runtimeTrafficTotal].
     *
     * The flow is normally advanced by the facade's own poll loop, which suspends while the app is
     * backgrounded or the screen is off ([isAppActive] false). Consumers that must observe the
     * exact total at a wake boundary — e.g. the traffic-statistics catch-up right after the app
     * returns — call this instead of waiting for the next poll tick.
     */
    suspend fun refreshTrafficTotal()
}

interface RuntimeConnectionReader {
    suspend fun queryConnections(): List<ConnectionInfo>
}

interface RuntimeProviderGateway {
    suspend fun queryProviders(): List<Provider>

    suspend fun updateProvider(type: Provider.Type, name: String)
}

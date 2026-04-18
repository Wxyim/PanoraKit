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

package com.github.nomadboxlab.monadbox.feature.meta.presentation.contract

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.data.repository.ConnectionActivityRepository
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionExplorer
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionSort
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionState
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

class DefaultConnectionExplorer(
    private val connectionActivityRepository: ConnectionActivityRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : ConnectionExplorer {
    private val mutableState = MutableStateFlow(ConnectionState())

    override val state: StateFlow<ConnectionState> = mutableState.asStateFlow()

    override val filteredConnections: StateFlow<List<ConnectionInfo>> =
        combine(
                state,
                connectionActivityRepository.activeConnections,
                connectionActivityRepository.closedConnections,
            ) { currentState, activeConnections, closedConnections ->
                val connections =
                    when (currentState.selectedTab) {
                        ConnectionTab.ACTIVE -> activeConnections
                        ConnectionTab.CLOSED -> closedConnections
                    }

                val filtered =
                    if (currentState.searchQuery.isEmpty()) {
                        connections
                    } else {
                        val query = currentState.searchQuery.lowercase()
                        connections.filter { conn ->
                            val host =
                                conn.metadata["host"]?.jsonPrimitive?.content?.lowercase() ?: ""
                            val process =
                                conn.metadata["process"]?.jsonPrimitive?.content?.lowercase() ?: ""
                            val chains = conn.chains.joinToString(" ").lowercase()
                            val rule = conn.rule.lowercase()

                            host.contains(query) ||
                                process.contains(query) ||
                                chains.contains(query) ||
                                rule.contains(query)
                        }
                    }

                if (currentState.selectedTab == ConnectionTab.ACTIVE) {
                    when (currentState.sortBy) {
                        ConnectionSort.Time -> filtered.sortedByDescending { it.start }
                        ConnectionSort.Upload -> filtered.sortedByDescending { it.upload }
                        ConnectionSort.Download -> filtered.sortedByDescending { it.download }
                        ConnectionSort.Host ->
                            filtered.sortedBy { it.metadata["host"]?.jsonPrimitive?.content ?: "" }
                    }
                } else {
                    filtered
                }
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        scope.launch {
            connectionActivityRepository.activeConnections.collect { activeConnections ->
                mutableState.update {
                    it.copy(activeConnections = activeConnections, isLoading = false, error = null)
                }
            }
        }
    }

    override fun setSearchQuery(query: String) {
        mutableState.update { it.copy(searchQuery = query) }
    }

    override fun setSortBy(sort: ConnectionSort) {
        mutableState.update { it.copy(sortBy = sort) }
    }

    override fun setTab(tab: ConnectionTab) {
        mutableState.update { it.copy(selectedTab = tab) }
    }

    override fun clearError() {
        mutableState.update { it.copy(error = null) }
    }
}

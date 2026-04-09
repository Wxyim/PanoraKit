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

package com.github.yumelira.yumebox.data.repository

import com.github.yumelira.yumebox.core.domain.ConnectionHistoryManager
import com.github.yumelira.yumebox.core.model.ConnectionInfo
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ConnectionActivityRepository(
    private val proxyFacade: ProxyFacade,
    private val scope: CoroutineScope,
) {
    companion object {
        private const val POLL_INTERVAL_MS = 1000L
    }

    private val _activeConnections = MutableStateFlow<List<ConnectionInfo>>(emptyList())
    val activeConnections: StateFlow<List<ConnectionInfo>> = _activeConnections.asStateFlow()

    private val _closedConnections =
        MutableStateFlow(ConnectionHistoryManager.getClosedConnections())
    val closedConnections: StateFlow<List<ConnectionInfo>> = _closedConnections.asStateFlow()

    private var monitorJob: Job? = null

    init {
        start()
    }

    private fun start() {
        if (monitorJob?.isActive == true) return
        monitorJob =
            scope.launch {
                proxyFacade.isRunning.collectLatest { isRunning ->
                    if (!isRunning) {
                        _activeConnections.value = emptyList()
                        _closedConnections.value = ConnectionHistoryManager.getClosedConnections()
                        return@collectLatest
                    }

                    // Start a fresh recent-request session when runtime enters running state.
                    ConnectionHistoryManager.clear()
                    _closedConnections.value = emptyList()

                    while (proxyFacade.isRunning.value) {
                        runCatching {
                                val snapshot = ServiceClient.clash().queryConnections()
                                val connections = snapshot.connections ?: emptyList()
                                ConnectionHistoryManager.updateConnections(connections)
                                _activeConnections.value = connections
                                _closedConnections.value =
                                    ConnectionHistoryManager.getClosedConnections()
                            }
                            .onFailure { error ->
                                if (error is CancellationException) throw error
                                if (error is RuntimeGatewayException) {
                                    Timber.d(
                                        "Connection polling skipped: ${error.runtimeGatewayMessage("runtime unavailable")}"
                                    )
                                } else {
                                    Timber.w(error, "Failed to refresh connection activity")
                                }
                            }
                        delay(POLL_INTERVAL_MS)
                    }
                }
            }
    }
}

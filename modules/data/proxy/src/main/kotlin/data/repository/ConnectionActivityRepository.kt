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

package com.github.nomadboxlab.monadbox.data.repository

import com.github.nomadboxlab.monadbox.core.domain.ConnectionHistoryManager
import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayException
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeConnectionReader
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
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
    private val runtimeStateReader: RuntimeStateReader,
    private val runtimeConnectionReader: RuntimeConnectionReader,
    private val scope: CoroutineScope,
) : ConnectionActivityProvider {
    companion object {
        private const val POLL_INTERVAL_MS = 5000L
    }

    private val _activeConnections = MutableStateFlow<List<ConnectionInfo>>(emptyList())
    override val activeConnections: StateFlow<List<ConnectionInfo>> =
        _activeConnections.asStateFlow()

    private val _closedConnections =
        MutableStateFlow(ConnectionHistoryManager.getClosedConnections())
    override val closedConnections: StateFlow<List<ConnectionInfo>> =
        _closedConnections.asStateFlow()

    private var monitorJob: Job? = null

    init {
        start()
    }

    private fun start() {
        if (monitorJob?.isActive == true) return
        monitorJob =
            scope.launch {
                runtimeStateReader.isRuntimeRunning.collectLatest { isRunning ->
                    if (!isRunning) {
                        _activeConnections.value = emptyList()
                        _closedConnections.value = ConnectionHistoryManager.getClosedConnections()
                        return@collectLatest
                    }

                    // Start a fresh recent-request session when runtime enters running state.
                    ConnectionHistoryManager.clear()
                    _closedConnections.value = emptyList()

                    while (runtimeStateReader.isRuntimeRunning.value) {
                        runCatching {
                                val connections = runtimeConnectionReader.queryConnections()
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

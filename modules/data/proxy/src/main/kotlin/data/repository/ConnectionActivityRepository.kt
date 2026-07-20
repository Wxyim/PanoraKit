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
        /** Snapshot connections every second so short-lived DNS / TCP handshake
         *  sockets are captured before they disappear from the Go core. */
        private const val POLL_INTERVAL_MS = 1000L
    }

    private val _activeConnections = MutableStateFlow<List<ConnectionInfo>>(emptyList())
    override val activeConnections: StateFlow<List<ConnectionInfo>> =
        _activeConnections.asStateFlow()

    private val _closedConnections =
        MutableStateFlow(ConnectionHistoryManager.getClosedConnections())
    override val closedConnections: StateFlow<List<ConnectionInfo>> =
        _closedConnections.asStateFlow()

    private var monitorJob: Job? = null

    /** Guard that ensures [ConnectionHistoryManager] is only cleared once per
     *  VPN session, not when the runtime is already running and polling restarts. */
    private var needsSessionClear = true

    init {
        start()
    }

    /** Start watching runtime state and poll connections while the VPN is running.
     *  Idempotent — safe to call when already active. */
    fun start() {
        if (monitorJob?.isActive == true) return
        monitorJob =
            scope.launch {
                runtimeStateReader.isRuntimeRunning.collectLatest { isRunning ->
                    if (!isRunning) {
                        _activeConnections.value = emptyList()
                        _closedConnections.value = ConnectionHistoryManager.getClosedConnections()
                        needsSessionClear = true
                        return@collectLatest
                    }

                    if (needsSessionClear) {
                        // Start a fresh recent-request session when runtime enters running state.
                        ConnectionHistoryManager.clear()
                        _closedConnections.value = emptyList()
                        needsSessionClear = false
                    }

                    while (runtimeStateReader.isRuntimeRunning.value) {
                        runCatching {
                                val connections = runtimeConnectionReader.queryConnections()
                                ConnectionHistoryManager.updateConnections(connections)
                                _activeConnections.value = connections
                                val closed = ConnectionHistoryManager.getClosedConnections()
                                // Only emit when the closed set actually changed
                                // (avoids driving combine recomputation every 1000 ms).
                                if (closedChanged(closed)) {
                                    _closedConnections.value = closed
                                }
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

    /** Returns true when [closed] differs from the currently-held snapshot by connection id set. */
    private fun closedChanged(closed: List<ConnectionInfo>): Boolean {
        val prev = _closedConnections.value
        if (prev.size != closed.size) return true
        val prevIds = prev.asSequence().map { it.id }.toSet()
        val closedIds = closed.asSequence().map { it.id }.toSet()
        return prevIds != closedIds
    }

    /** Stop connection polling. StateFlows retain their last-emitted values. */
    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
    }
}

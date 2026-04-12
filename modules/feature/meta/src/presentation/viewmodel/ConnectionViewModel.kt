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

package com.github.yumelira.yumebox.feature.meta.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.core.domain.pcap.PcapConnectionRecorder
import com.github.yumelira.yumebox.core.model.ConnectionInfo
import com.github.yumelira.yumebox.core.presentation.BaseViewModel
import com.github.yumelira.yumebox.data.repository.ConnectionActivityRepository
import com.github.yumelira.yumebox.domain.model.StructuredError
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonPrimitive

enum class ConnectionSort {
    Time,
    Upload,
    Download,
    Host,
}

enum class ConnectionTab {
    ACTIVE,
    CLOSED,
}

data class ConnectionState(
    val activeConnections: List<ConnectionInfo> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val sortBy: ConnectionSort = ConnectionSort.Time,
    val selectedTab: ConnectionTab = ConnectionTab.ACTIVE,
    val error: String? = null,
    val structuredError: StructuredError? = null,
    val isCapturing: Boolean = false,
    val capturedCount: Int = 0,
) {
    val totalConnections: Int
        get() = activeConnections.size
}

class ConnectionViewModel(private val connectionActivityRepository: ConnectionActivityRepository) :
    BaseViewModel<ConnectionState>(ConnectionState()) {

    val state: StateFlow<ConnectionState> = uiState

    val filteredConnections: StateFlow<List<ConnectionInfo>> =
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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            connectionActivityRepository.activeConnections.collect { activeConnections ->
                updateState {
                    it.copy(activeConnections = activeConnections, isLoading = false, error = null)
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        updateState { it.copy(searchQuery = query) }
    }

    fun setSortBy(sort: ConnectionSort) {
        updateState { it.copy(sortBy = sort) }
    }

    fun setTab(tab: ConnectionTab) {
        updateState { it.copy(selectedTab = tab) }
    }

    fun clearError() {
        updateState { it.copy(error = null) }
    }

    private val capturedConnections = mutableListOf<ConnectionInfo>()
    private val capturedIds = mutableSetOf<String>()
    private var captureJob: Job? = null

    fun startCapture() {
        if (captureJob?.isActive == true) return
        capturedConnections.clear()
        capturedIds.clear()
        updateState { it.copy(isCapturing = true, capturedCount = 0) }
        captureJob =
            viewModelScope.launch {
                connectionActivityRepository.activeConnections.collect { connections ->
                    val newConnections = connections.filter { it.id !in capturedIds }
                    if (newConnections.isNotEmpty()) {
                        capturedConnections.addAll(newConnections)
                        capturedIds.addAll(newConnections.map { it.id })
                        updateState { it.copy(capturedCount = capturedConnections.size) }
                    }
                }
            }
    }

    fun stopCapture() {
        captureJob?.cancel()
        captureJob = null
        updateState { it.copy(isCapturing = false) }
    }

    fun hasCapturedData(): Boolean = capturedConnections.isNotEmpty()

    fun suggestPcapFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        return "monadbox_capture_${sdf.format(Date())}.pcap"
    }

    suspend fun exportCaptureToPcap(outputStream: OutputStream): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                    PcapConnectionRecorder.writePcapFile(outputStream, capturedConnections.toList())
                    true
                }
                .getOrDefault(false)
        }
    }
}

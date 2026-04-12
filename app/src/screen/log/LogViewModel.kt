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

package com.github.yumelira.yumebox.screen.log

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.core.model.LogMessage
import com.github.yumelira.yumebox.data.repository.DebugExportBundleBuilder
import com.github.yumelira.yumebox.data.repository.LogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LogViewModel(
    private val repository: LogRepository,
    private val debugExportBundleBuilder: DebugExportBundleBuilder,
) : ViewModel() {

    private val refreshMutex = Mutex()
    private var autoRefreshJob: Job? = null

    private val _isRecording = MutableStateFlow(repository.isRecording())
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _tempLogEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val tempLogEntries: StateFlow<List<LogEntry>> = _tempLogEntries.asStateFlow()

    private val _historyFiles = MutableStateFlow<List<HistoryLogFile>>(emptyList())
    val historyFiles: StateFlow<List<HistoryLogFile>> = _historyFiles.asStateFlow()

    private val _startupFiles = MutableStateFlow<List<StartupLogFile>>(emptyList())
    val startupFiles: StateFlow<List<StartupLogFile>> = _startupFiles.asStateFlow()

    private val _selectedHistoryFileName = MutableStateFlow<String?>(null)
    val selectedHistoryFileName: StateFlow<String?> = _selectedHistoryFileName.asStateFlow()

    private val _selectedStartupFileName = MutableStateFlow<String?>(null)
    val selectedStartupFileName: StateFlow<String?> = _selectedStartupFileName.asStateFlow()

    private val _selectedHistoryEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val selectedHistoryEntries: StateFlow<List<LogEntry>> = _selectedHistoryEntries.asStateFlow()

    private val _selectedStartupEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val selectedStartupEntries: StateFlow<List<LogEntry>> = _selectedStartupEntries.asStateFlow()

    init {
        requestBrowserStateRefresh()
    }

    fun refreshRecordingState() {
        _isRecording.value = repository.isRecording()
    }

    fun startAutoRefresh(intervalMillis: Long = 500L) {
        if (autoRefreshJob?.isActive == true) return
        autoRefreshJob =
            viewModelScope.launch {
                while (isActive) {
                    refreshBrowserState()
                    delay(intervalMillis)
                }
            }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    fun requestBrowserStateRefresh() {
        viewModelScope.launch { refreshBrowserState() }
    }

    suspend fun refreshBrowserState() {
        refreshMutex.withLock {
            val browserState =
                withContext(Dispatchers.IO) {
                    val recording = repository.isRecording()
                    val history =
                        repository.listLogFiles().map {
                            HistoryLogFile(
                                name = it.name,
                                createdAt = it.createdAt,
                                size = it.size,
                                isRecording = it.isRecording,
                            )
                        }
                    val startup =
                        repository.listStartupLogFiles().map {
                            StartupLogFile(name = it.name, updatedAt = it.updatedAt, size = it.size)
                        }
                    val tempEntries =
                        if (recording) {
                            repository.readTempLogEntries().map {
                                LogEntry(time = it.time, level = it.level, message = it.message)
                            }
                        } else {
                            emptyList()
                        }
                    BrowserStateSnapshot(recording, history, startup, tempEntries)
                }

            _isRecording.value = browserState.isRecording
            _historyFiles.value = browserState.historyFiles
            _startupFiles.value = browserState.startupFiles
            _tempLogEntries.value = browserState.tempEntries

            val selectedHistory = _selectedHistoryFileName.value
            if (
                !selectedHistory.isNullOrBlank() &&
                    browserState.historyFiles.none { it.name == selectedHistory }
            ) {
                _selectedHistoryFileName.value = null
                _selectedHistoryEntries.value = emptyList()
            }

            val selectedStartup = _selectedStartupFileName.value
            if (
                !selectedStartup.isNullOrBlank() &&
                    browserState.startupFiles.none { it.name == selectedStartup }
            ) {
                _selectedStartupFileName.value = null
                _selectedStartupEntries.value = emptyList()
            }
        }
    }

    fun refreshHistoryFiles() {
        requestBrowserStateRefresh()
    }

    fun refreshStartupFiles() {
        requestBrowserStateRefresh()
    }

    fun openHistoryFile(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val entries = repository.readLogEntries(fileName)
            _selectedStartupFileName.value = null
            _selectedStartupEntries.value = emptyList()
            _selectedHistoryFileName.value = fileName
            _selectedHistoryEntries.value =
                entries.map { LogEntry(time = it.time, level = it.level, message = it.message) }
        }
    }

    fun openStartupFile(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val entries = repository.readStartupLogEntries(fileName)
            _selectedHistoryFileName.value = null
            _selectedHistoryEntries.value = emptyList()
            _selectedStartupFileName.value = fileName
            _selectedStartupEntries.value =
                entries.map { LogEntry(time = it.time, level = it.level, message = it.message) }
        }
    }

    fun closeHistoryViewer() {
        _selectedHistoryFileName.value = null
        _selectedHistoryEntries.value = emptyList()
        _selectedStartupFileName.value = null
        _selectedStartupEntries.value = emptyList()
    }

    suspend fun deleteHistoryFile(fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            val deleted = repository.deleteLogFile(fileName)
            if (deleted) {
                if (_selectedHistoryFileName.value == fileName) {
                    _selectedHistoryFileName.value = null
                    _selectedHistoryEntries.value = emptyList()
                }
                val files =
                    repository.listLogFiles().map {
                        HistoryLogFile(
                            name = it.name,
                            createdAt = it.createdAt,
                            size = it.size,
                            isRecording = it.isRecording,
                        )
                    }
                _historyFiles.value = files
            }
            deleted
        }

    suspend fun deleteStartupFile(fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            val deleted = repository.deleteStartupLogFile(fileName)
            if (deleted) {
                if (_selectedStartupFileName.value == fileName) {
                    _selectedStartupFileName.value = null
                    _selectedStartupEntries.value = emptyList()
                }
                val files =
                    repository.listStartupLogFiles().map {
                        StartupLogFile(name = it.name, updatedAt = it.updatedAt, size = it.size)
                    }
                _startupFiles.value = files
            }
            deleted
        }

    fun clearTempLog() {
        _tempLogEntries.value = emptyList()
    }

    suspend fun clearAllLogs(): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext runCatching {
                    repository.deleteAllLogs()
                    true
                }
                .onSuccess {
                    _tempLogEntries.value = emptyList()
                    _selectedHistoryFileName.value = null
                    _selectedStartupFileName.value = null
                    _selectedHistoryEntries.value = emptyList()
                    _selectedStartupEntries.value = emptyList()
                    requestBrowserStateRefresh()
                }
                .getOrDefault(false)
        }

    suspend fun saveTempLog(targetUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val entries = _tempLogEntries.value
            if (entries.isEmpty()) return@withContext false
            try {
                val repoEntries =
                    entries.map {
                        LogRepository.LogEntry(
                            time = it.time,
                            level = it.level,
                            message = it.message,
                        )
                    }
                repository.writeLogEntries(targetUri, repoEntries)
                true
            } catch (e: Exception) {
                false
            }
        }

    suspend fun saveCurrentViewLog(targetUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val historyName = _selectedHistoryFileName.value
            if (!historyName.isNullOrBlank()) {
                return@withContext repository.exportLogFile(historyName, targetUri)
            }
            val startupName = _selectedStartupFileName.value
            if (!startupName.isNullOrBlank()) {
                return@withContext repository.exportStartupLogFile(startupName, targetUri)
            }
            if (_isRecording.value && repository.currentRecordingFileName() != null) {
                return@withContext repository.exportCurrentRecording(targetUri)
            }
            val entries = _tempLogEntries.value
            if (entries.isEmpty()) return@withContext false
            val repoEntries =
                entries.map {
                    LogRepository.LogEntry(time = it.time, level = it.level, message = it.message)
                }
            repository.writeLogEntries(targetUri, repoEntries)
        }

    suspend fun exportDebugBundle(
        targetUri: Uri,
        appVersionName: String,
        appVersionCode: Int,
        buildType: String,
        runtimeStateSummary: Map<String, String> = emptyMap(),
        configVersionId: String? = null,
    ): Boolean =
        withContext(Dispatchers.IO) {
            debugExportBundleBuilder.exportToUri(
                targetUri = targetUri,
                appVersionName = appVersionName,
                appVersionCode = appVersionCode,
                buildType = buildType,
                runtimeStateSummary = runtimeStateSummary,
                configVersionId = configVersionId,
            )
        }

    fun suggestDebugBundleFileName(): String = debugExportBundleBuilder.suggestedFileName()

    data class LogEntry(val time: String, val level: LogMessage.Level, val message: String)

    data class HistoryLogFile(
        val name: String,
        val createdAt: Long,
        val size: Long,
        val isRecording: Boolean,
    )

    data class StartupLogFile(val name: String, val updatedAt: Long, val size: Long)

    private data class BrowserStateSnapshot(
        val isRecording: Boolean,
        val historyFiles: List<HistoryLogFile>,
        val startupFiles: List<StartupLogFile>,
        val tempEntries: List<LogEntry>,
    )
}

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

import android.app.Application
import android.net.Uri
import com.github.yumelira.yumebox.core.model.LogMessage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.enums.enumEntries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class LogRepository(
    private val application: Application,
    private val logRecordGateway: LogRecordGateway,
) {
    companion object {
        private const val STOP_WAIT_MS = 300L
        private const val DEFAULT_MAX_ENTRIES = 2000
        private const val CLEANUP_ARCHIVE_PREFIX = "cleanup_"
        private const val MAX_CLEANUP_ARCHIVES = 8
        private const val MAX_STARTUP_ARCHIVE_LINES_PER_FILE = 400
        private const val MAX_MANAGED_ARCHIVE_LINES = 1200
        private val STARTUP_LOG_FILES =
            setOf("local_tun_startup.log", "local_http_startup.log", "root_tun_startup.log")
        private val LOG_LINE_REGEX = """\[(.+?)] \[(.+?)] (.+)""".toRegex()
        private val LOG_LEVELS = enumEntries<LogMessage.Level>().associateBy { it.name }
    }

    private val logDir: File
        get() = logRecordGateway.getLogDir(application)

    private val filesDir: File
        get() = application.filesDir

    fun startRecording() {
        logRecordGateway.start(application)
    }

    fun stopRecording() {
        logRecordGateway.stop(application)
    }

    fun isRecording(): Boolean {
        return logRecordGateway.isRecording
    }

    fun isCurrentRecordingFile(fileName: String): Boolean {
        return isRecording() && logRecordGateway.currentLogFileName == fileName
    }

    fun listLogFiles(): List<LogFileInfo> {
        val currentlyRecording = isRecording()
        val currentFileName = logRecordGateway.currentLogFileName
        val files =
            logDir.listFiles(::isManagedLogFile)?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        return files.map { file ->
            LogFileInfo(
                name = file.name,
                createdAt = file.lastModified(),
                size = file.length(),
                isRecording = currentlyRecording && file.name == currentFileName,
            )
        }
    }

    fun listStartupLogFiles(): List<StartupLogFileInfo> {
        return STARTUP_LOG_FILES.map { name -> File(filesDir, name) }
            .filter { it.exists() && it.isFile }
            .sortedByDescending { it.lastModified() }
            .map { file ->
                StartupLogFileInfo(
                    name = file.name,
                    updatedAt = file.lastModified(),
                    size = file.length(),
                )
            }
    }

    suspend fun getLogFileSize(fileName: String): Long? =
        withContext(Dispatchers.IO) { resolveLogFile(fileName)?.length() }

    suspend fun readLogEntries(
        fileName: String,
        maxEntries: Int = DEFAULT_MAX_ENTRIES,
    ): List<LogEntry> =
        withContext(Dispatchers.IO) {
            val file = resolveLogFile(fileName) ?: return@withContext emptyList()
            if (maxEntries <= 0) return@withContext emptyList()
            try {
                file.useLines { lines ->
                    val ring = ArrayDeque<LogEntry>(maxEntries)
                    lines.forEach { line ->
                        val entry = parseLogLine(line) ?: return@forEach
                        if (ring.size == maxEntries) {
                            ring.removeFirst()
                        }
                        ring.addLast(entry)
                    }
                    ring.toList()
                }
            } catch (_: IOException) {
                emptyList()
            } catch (_: SecurityException) {
                emptyList()
            }
        }

    suspend fun exportLogFile(fileName: String, targetUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val source = resolveLogFile(fileName) ?: return@withContext false
            try {
                application.contentResolver.openOutputStream(targetUri)?.use { output ->
                    source.inputStream().use { input -> input.copyTo(output) }
                } ?: return@withContext false
                true
            } catch (_: IOException) {
                false
            } catch (_: SecurityException) {
                false
            }
        }

    suspend fun readStartupLogEntries(
        fileName: String,
        maxEntries: Int = DEFAULT_MAX_ENTRIES,
    ): List<LogEntry> =
        withContext(Dispatchers.IO) {
            val file = resolveStartupLogFile(fileName) ?: return@withContext emptyList()
            if (maxEntries <= 0) return@withContext emptyList()
            try {
                file.useLines { lines ->
                    val ring = ArrayDeque<LogEntry>(maxEntries)
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        if (trimmed.isEmpty()) return@forEach
                        if (ring.size == maxEntries) {
                            ring.removeFirst()
                        }
                        ring.addLast(
                            LogEntry(time = "", level = LogMessage.Level.Unknown, message = trimmed)
                        )
                    }
                    ring.toList()
                }
            } catch (_: IOException) {
                emptyList()
            } catch (_: SecurityException) {
                emptyList()
            }
        }

    suspend fun exportStartupLogFile(fileName: String, targetUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            val source = resolveStartupLogFile(fileName) ?: return@withContext false
            try {
                application.contentResolver.openOutputStream(targetUri)?.use { output ->
                    source.inputStream().use { input -> input.copyTo(output) }
                } ?: return@withContext false
                true
            } catch (_: IOException) {
                false
            } catch (_: SecurityException) {
                false
            }
        }

    suspend fun persistReadableLogsForCleanup(): String? =
        withContext(Dispatchers.IO) {
            val archiveLines = mutableListOf<String>()

            val startupLines =
                STARTUP_LOG_FILES.flatMap { fileName ->
                    val file = File(filesDir, fileName)
                    if (!file.exists() || !file.isFile) {
                        emptyList()
                    } else {
                        runCatching { file.readLines() }
                            .getOrDefault(emptyList())
                            .takeLast(MAX_STARTUP_ARCHIVE_LINES_PER_FILE)
                            .map { line -> formatArchiveLine("[STARTUP:$fileName] ${line.trim()}") }
                    }
                }
            archiveLines += startupLines

            resolveMostRecentManagedLogFile()?.let { managed ->
                archiveLines +=
                    readTailLines(managed, MAX_MANAGED_ARCHIVE_LINES).map { line ->
                        val parsed = parseLogLine(line)
                        if (parsed != null) {
                            line
                        } else {
                            formatArchiveLine("[LIVE] ${line.trim()}")
                        }
                    }
            }

            if (archiveLines.isEmpty()) {
                return@withContext null
            }

            val archiveFile = File(logDir, buildCleanupArchiveFileName(System.currentTimeMillis()))
            runCatching {
                    logDir.mkdirs()
                    archiveFile.writeText(
                        archiveLines.joinToString(separator = "\n", postfix = "\n")
                    )
                    pruneCleanupArchives()
                }
                .getOrElse {
                    return@withContext null
                }
            archiveFile.name
        }

    suspend fun readTempLogEntries(maxEntries: Int = 2000): List<LogEntry> =
        withContext(Dispatchers.IO) {
            val currentlyRecording = isRecording()
            val currentFileName = logRecordGateway.currentLogFileName
            if (!currentlyRecording || currentFileName == null) {
                return@withContext emptyList()
            }
            val file = resolveLogFile(currentFileName) ?: return@withContext emptyList()
            readLogFileEntries(file, maxEntries)
        }

    private fun readLogFileEntries(file: File, maxEntries: Int): List<LogEntry> {
        return try {
            if (maxEntries <= 0) return emptyList()
            file.useLines { lines ->
                val ring = ArrayDeque<LogEntry>(maxEntries)
                lines.forEach { line ->
                    val entry = parseLogLine(line) ?: return@forEach
                    if (ring.size == maxEntries) {
                        ring.removeFirst()
                    }
                    ring.addLast(entry)
                }
                ring.toList()
            }
        } catch (_: IOException) {
            emptyList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    suspend fun writeLogEntries(targetUri: Uri, entries: List<LogEntry>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                application.contentResolver.openOutputStream(targetUri)?.use { output ->
                    val sb = StringBuilder()
                    entries.forEach { entry ->
                        sb.append("[${entry.time}] [${entry.level.name}] ${entry.message}\n")
                    }
                    output.write(sb.toString().toByteArray())
                }
                true
            } catch (_: IOException) {
                false
            } catch (_: SecurityException) {
                false
            }
        }

    suspend fun deleteLogFile(fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            val file = resolveLogFile(fileName) ?: return@withContext false
            if (isCurrentRecordingFile(file.name)) {
                stopRecording()
                delay(STOP_WAIT_MS)
            }
            file.delete()
        }

    suspend fun deleteStartupLogFile(fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            val file = resolveStartupLogFile(fileName) ?: return@withContext false
            file.delete()
        }

    suspend fun deleteAllLogs() =
        withContext(Dispatchers.IO) {
            if (isRecording()) {
                stopRecording()
                delay(STOP_WAIT_MS)
            }
            val files = logDir.listFiles(::isManagedLogFile) ?: return@withContext
            files.forEach { it.delete() }
            STARTUP_LOG_FILES.forEach { name -> runCatching { File(filesDir, name).delete() } }
        }

    private fun parseLogLine(line: String): LogEntry? {
        if (line.isBlank()) return null
        val match = LOG_LINE_REGEX.find(line) ?: return null
        val (timeStr, levelStr, message) = match.destructured
        val level = LOG_LEVELS[levelStr] ?: LogMessage.Level.Unknown
        return LogEntry(time = timeStr, level = level, message = message)
    }

    private fun resolveLogFile(fileName: String): File? {
        if (!isSafeLogFileName(fileName)) return null
        val file = File(logDir, fileName)
        return file.takeIf(::isManagedLogFile)
    }

    private fun resolveMostRecentManagedLogFile(): File? {
        val current =
            logRecordGateway.currentLogFileName?.let(::resolveLogFile)?.takeIf { it.exists() }
        if (current != null) return current
        return logDir
            .listFiles(::isManagedLogFile)
            ?.sortedByDescending { it.lastModified() }
            ?.firstOrNull()
    }

    private fun readTailLines(file: File, maxLines: Int): List<String> {
        if (maxLines <= 0) return emptyList()
        return runCatching {
                file.useLines { lines ->
                    val ring = ArrayDeque<String>(maxLines)
                    lines.forEach { line ->
                        if (ring.size == maxLines) {
                            ring.removeFirst()
                        }
                        ring.addLast(line)
                    }
                    ring.toList()
                }
            }
            .getOrDefault(emptyList())
    }

    private fun buildCleanupArchiveFileName(now: Long): String {
        val prefix = logRecordGateway.logPrefix
        val suffix = logRecordGateway.logSuffix
        val namePart = "${CLEANUP_ARCHIVE_PREFIX}${now}"
        return if (prefix.isBlank()) {
            "$namePart$suffix"
        } else {
            "$prefix$namePart$suffix"
        }
    }

    private fun pruneCleanupArchives() {
        val files =
            logDir
                .listFiles(::isManagedLogFile)
                ?.filter { it.name.contains(CLEANUP_ARCHIVE_PREFIX) }
                ?.sortedByDescending { it.lastModified() } ?: return
        files.drop(MAX_CLEANUP_ARCHIVES).forEach { it.delete() }
    }

    private fun formatArchiveLine(message: String): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        return "[$timestamp] [Info] ${message.ifBlank { "cleanup snapshot" }}"
    }

    private fun resolveStartupLogFile(fileName: String): File? {
        if (!STARTUP_LOG_FILES.contains(fileName)) return null
        val file = File(filesDir, fileName)
        return file.takeIf { it.exists() && it.isFile }
    }

    private fun isManagedLogFile(file: File): Boolean {
        if (!file.exists() || !file.isFile) return false
        if (!file.name.endsWith(logRecordGateway.logSuffix)) return false
        val prefix = logRecordGateway.logPrefix
        return prefix.isBlank() || file.name.startsWith(prefix)
    }

    private fun isSafeLogFileName(fileName: String): Boolean {
        if (fileName.isBlank()) return false
        if (fileName.contains('/') || fileName.contains('\\') || fileName.contains(".."))
            return false
        if (!fileName.endsWith(logRecordGateway.logSuffix)) return false
        val prefix = logRecordGateway.logPrefix
        return prefix.isBlank() || fileName.startsWith(prefix)
    }

    data class LogFileInfo(
        val name: String,
        val createdAt: Long,
        val size: Long,
        val isRecording: Boolean,
    )

    data class LogEntry(val time: String, val level: LogMessage.Level, val message: String)

    data class StartupLogFileInfo(val name: String, val updatedAt: Long, val size: Long)
}

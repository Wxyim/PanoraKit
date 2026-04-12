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

package com.github.yumelira.yumebox.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.yumelira.yumebox.core.model.LogMessage
import com.github.yumelira.yumebox.runtime.service.R
import com.github.yumelira.yumebox.service.common.constants.Components
import com.github.yumelira.yumebox.service.common.constants.Intents
import com.github.yumelira.yumebox.service.remote.ILogObserver
import com.github.yumelira.yumebox.service.root.RootTunJson
import com.github.yumelira.yumebox.service.root.RootTunServiceBridge
import com.github.yumelira.yumebox.service.root.RootTunStateStore
import dev.oom_wg.purejoy.mlang.MLang
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.*
import kotlinx.serialization.serializer
import timber.log.Timber

class LogRecordService : Service() {

    companion object {
        private const val TAG = "LogRecordService"
        private const val NOTIFICATION_ID = 2001
        private const val CHANNEL_ID = "log_record_channel"

        private const val ACTION_START = "com.github.yumelira.yumebox.LOG_START"
        private const val ACTION_STOP = "com.github.yumelira.yumebox.LOG_STOP"

        const val LOG_DIR = "logs"
        const val LOG_PREFIX = ""
        const val LOG_SUFFIX = ".log"
        private const val MAX_LOG_FILES = 20
        private const val MAX_LOG_FILE_BYTES = 512L * 1024L
        private const val MAX_TOTAL_BYTES = 8L * 1024L * 1024L
        private const val MAX_IN_MEMORY_LOG_BYTES = 256L * 1024L
        private const val ROOT_LOG_POLL_INTERVAL_MS = 300L

        @Volatile
        var isRecording: Boolean = false
            private set

        @Volatile
        var currentLogFileName: String? = null
            private set

        private val liveLogLock = Any()
        private val liveLogLines = ArrayDeque<String>()
        @Volatile private var liveLogBytes: Long = 0L

        fun start(context: Context) {
            val intent =
                Intent(context, LogRecordService::class.java).apply { action = ACTION_START }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent =
                Intent(context, LogRecordService::class.java).apply { action = ACTION_STOP }
            context.startService(intent)
        }

        fun getLogDir(context: Context): File {
            return File(context.filesDir, LOG_DIR).apply { mkdirs() }
        }

        fun snapshotLiveLogLines(maxLines: Int): List<String> {
            synchronized(liveLogLock) {
                if (maxLines <= 0 || liveLogLines.isEmpty()) return emptyList()
                return liveLogLines.toList().takeLast(maxLines)
            }
        }

        private fun clearLiveLogBuffer() {
            synchronized(liveLogLock) {
                liveLogLines.clear()
                liveLogBytes = 0L
            }
        }

        private fun appendLiveLogLine(line: String): Boolean {
            synchronized(liveLogLock) {
                liveLogLines.addLast(line)
                liveLogBytes += line.toByteArray(StandardCharsets.UTF_8).size.toLong()
                return liveLogBytes >= MAX_IN_MEMORY_LOG_BYTES
            }
        }

        private fun drainLiveLogChunk(): String {
            synchronized(liveLogLock) {
                if (liveLogLines.isEmpty()) return ""
                val chunk = buildString { liveLogLines.forEach { append(it) } }
                liveLogLines.clear()
                liveLogBytes = 0L
                return chunk
            }
        }
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val stateLock = Any()
    private val writerLock = Any()

    private var logWriter: BufferedWriter? = null
    private var logCollectJob: Job? = null
    private var logFile: File? = null
    @Volatile private var runtimeReceiverRegistered = false
    private var localClashManager: ClashManager? = null
    private var rootLogSeq: Long = 0L
    private val rootTunStateStore by lazy { RootTunStateStore(applicationContext) }
    private val fileNameFormatter =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneId.systemDefault())
    private val dateFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault())
    private val runtimeLogObserver =
        object : ILogObserver {
            override fun newItem(log: LogMessage) {
                if (!isRecording) return
                val line =
                    "[${dateFormatter.format(log.time.toInstant())}] [${log.level.name}] ${log.message}\n"
                writeLogLine(line)
            }
        }
    private val runtimeEventsReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action ?: return) {
                    Intents.ACTION_CLASH_STARTED -> scheduleObserverAttach()
                    Intents.ACTION_CLASH_STOPPED -> scheduleObserverDetach()
                }
            }
        }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startRecording()
            ACTION_STOP -> stopRecording()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopRecordingInternal(stopService = false)
        serviceScope.cancel()
        isRecording = false
        currentLogFileName = null
        super.onDestroy()
    }

    private fun startRecording() {
        synchronized(stateLock) {
            if (isRecording) return

            runCatching {
                    clearLiveLogBuffer()
                    val file = createLogFile()
                    logFile = file
                    logWriter = BufferedWriter(FileWriter(file, true))

                    currentLogFileName = file.name
                    isRecording = true

                    startForeground(NOTIFICATION_ID, createNotification())
                    registerRuntimeReceiver()
                    scheduleObserverAttach()
                }
                .onFailure { e ->
                    Timber.tag(TAG).e(e, "Log recording start failed")
                    stopRecordingInternal(stopService = true)
                }
        }
    }

    private fun stopRecording() {
        stopRecordingInternal(stopService = true)
    }

    private fun stopRecordingInternal(stopService: Boolean) {
        synchronized(stateLock) {
            unregisterRuntimeReceiver()
            logCollectJob?.cancel()
            logCollectJob = null
            detachObserver()
            flushLiveLogsToDisk()
            closeLogWriter()
            clearLiveLogBuffer()

            isRecording = false
            currentLogFileName = null

            runCatching { stopForeground(STOP_FOREGROUND_REMOVE) }
            if (stopService) {
                stopSelf()
            }
        }
    }

    private fun writeLogLine(line: String) {
        synchronized(writerLock) {
            runCatching {
                    if (appendLiveLogLine(line)) {
                        flushLiveLogsToDisk()
                    }
                }
                .onFailure { e -> Timber.tag(TAG).e(e, "Log write failed") }
        }
    }

    private fun flushLiveLogsToDisk() {
        val chunk = drainLiveLogChunk()
        if (chunk.isBlank()) return
        rotateLogFileIfNeeded(chunk)
        val writer = logWriter ?: return
        writer.write(chunk)
        writer.flush()
    }

    private fun rotateLogFileIfNeeded(nextLine: String) {
        val file = logFile ?: return
        val nextBytes = nextLine.toByteArray(StandardCharsets.UTF_8).size.toLong()
        if (file.length() + nextBytes <= MAX_LOG_FILE_BYTES) return

        logWriter?.flush()
        logWriter?.close()

        val nextFile = createLogFile()
        logFile = nextFile
        logWriter = BufferedWriter(FileWriter(nextFile, true))
        currentLogFileName = nextFile.name
        pruneLogFiles(getLogDir(applicationContext))
        updateNotification()
    }

    private fun closeLogWriter() {
        synchronized(writerLock) {
            runCatching {
                    logWriter?.flush()
                    logWriter?.close()
                    logWriter = null
                    logFile = null
                }
                .onFailure { e -> Timber.tag(TAG).e(e, "Log writer close failed") }
        }
    }

    private fun pruneLogFiles(logDir: File) {
        val files =
            logDir
                .listFiles { file ->
                    file.isFile &&
                        file.name.endsWith(LOG_SUFFIX) &&
                        (LOG_PREFIX.isBlank() || file.name.startsWith(LOG_PREFIX))
                }
                ?.sortedByDescending { it.lastModified() }
                ?.toMutableList() ?: return

        var totalBytes = files.sumOf { it.length() }
        while (files.size > MAX_LOG_FILES || totalBytes > MAX_TOTAL_BYTES) {
            val oldest = files.removeLastOrNull() ?: break
            val size = oldest.length()
            runCatching { oldest.delete() }
            totalBytes -= size
        }
    }

    private fun createLogFile(): File {
        val logDir = getLogDir(applicationContext)
        pruneLogFiles(logDir)
        val timestamp = fileNameFormatter.format(Instant.now())
        var file = File(logDir, "$LOG_PREFIX$timestamp$LOG_SUFFIX")
        var suffix = 1
        while (file.exists()) {
            file = File(logDir, "$LOG_PREFIX${timestamp}_$suffix$LOG_SUFFIX")
            suffix += 1
        }
        return file
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                        CHANNEL_ID,
                        MLang.Service.LogRecord.ChannelName,
                        NotificationManager.IMPORTANCE_LOW,
                    )
                    .apply {
                        description = MLang.Service.LogRecord.ChannelDescription
                        setShowBadge(false)
                    }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent().setComponent(Components.MAIN_ACTIVITY)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val stopIntent = Intent(this, LogRecordService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent =
            PendingIntent.getService(
                this,
                1,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(MLang.Service.LogRecord.NotificationTitle)
            .setContentText(currentLogFileName ?: MLang.Service.LogRecord.NotificationContent)
            .setSmallIcon(R.drawable.ic_logo_service)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                MLang.Service.LogRecord.ActionStop,
                stopPendingIntent,
            )
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun updateNotification() {
        if (!isRecording) return
        if (!canPostNotifications()) return
        runCatching {
                NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, createNotification())
            }
            .onFailure { error -> Timber.tag(TAG).d(error, "Update log notification skipped") }
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun scheduleObserverAttach() {
        logCollectJob?.cancel()
        logCollectJob =
            serviceScope.launch {
                try {
                    attachObserver()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (t: Throwable) {
                    Timber.tag(TAG).e(t, "Attach runtime log observer failed")
                }
            }
    }

    private fun scheduleObserverDetach() {
        logCollectJob?.cancel()
        logCollectJob = serviceScope.launch { detachObserver() }
    }

    private suspend fun attachObserver() {
        detachObserver()
        if (isRootRuntimeActive()) {
            while (currentCoroutineContext().isActive && isRecording && isRootRuntimeActive()) {
                runCatching {
                        val chunk =
                            RootTunServiceBridge.queryRecentLogs(applicationContext, rootLogSeq)
                        if (chunk.items.isNotEmpty()) {
                            chunk.items.forEach { raw: String ->
                                runtimeLogObserver.newItem(
                                    RootTunJson.Default.decodeFromString(
                                        serializer<LogMessage>(),
                                        raw,
                                    )
                                )
                            }
                        }
                        rootLogSeq = chunk.nextSeq
                    }
                    .onFailure { error -> Timber.tag(TAG).d(error, "Root log polling skipped") }
                delay(ROOT_LOG_POLL_INTERVAL_MS)
            }
            return
        }

        val clash = ClashManager(applicationContext)
        localClashManager = clash
        clash.setLogObserver(runtimeLogObserver)
    }

    private fun detachObserver() {
        rootLogSeq = 0L
        runCatching { localClashManager?.setLogObserver(null) }
        runCatching { localClashManager?.close() }
        localClashManager = null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerRuntimeReceiver() {
        if (runtimeReceiverRegistered) return
        val filter =
            IntentFilter().apply {
                addAction(Intents.ACTION_CLASH_STARTED)
                addAction(Intents.ACTION_CLASH_STOPPED)
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(runtimeEventsReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(runtimeEventsReceiver, filter)
        }
        runtimeReceiverRegistered = true
    }

    private fun unregisterRuntimeReceiver() {
        if (!runtimeReceiverRegistered) return
        runCatching { unregisterReceiver(runtimeEventsReceiver) }
        runtimeReceiverRegistered = false
    }

    private fun isRootRuntimeActive(): Boolean {
        val status = rootTunStateStore.snapshot()
        return status.state.isActive || status.runtimeReady
    }
}

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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.startup

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Persists uncaught-exception stack traces into the managed log directory.
 *
 * Device-only defects (a text field that kills the app on one phone and not on the next one) used
 * to leave nothing behind except the system crash dialog: the process dies, the in-memory
 * [com.github.nomadboxlab.monadbox.domain.model.StructuredLogCollector] ring is lost, and pulling
 * `adb logcat` is not always an option for the person holding the phone. The recorder closes that
 * gap by writing the trace to `logs/crash_<timestamp>.log` - a name that the managed-log filter
 * already accepts, so the file shows up in 日志与诊断 with every line in the same `[time] [Level]
 * message` format the viewer parses, and it is shared/cleaned up like any other log.
 *
 * Every stack line gets its own log line because both the viewer and the debug bundle read the
 * format line-by-line; an embedded raw stack would otherwise be dropped as unparseable.
 *
 * This only sees JVM exceptions. A native crash (RenderThread SIGSEGV and friends) still needs the
 * platform tombstone via `adb`.
 */
internal object CrashLogRecorder {

    private const val TAG = "CrashLogRecorder"

    fun install(application: Application, logDir: File, prefix: String, suffix: String) {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Never let the recorder itself swallow the crash: the previous handler still gets to
            // run (and kill the process) even if writing the file fails.
            runCatching { record(application, logDir, prefix, suffix, thread, throwable) }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun record(
        application: Application,
        logDir: File,
        prefix: String,
        suffix: String,
        thread: Thread,
        throwable: Throwable,
    ) {
        val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        val timestamp = timestampFormat.format(Date())
        val configuration = application.resources.configuration
        val orientation =
            when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> "landscape"
                Configuration.ORIENTATION_PORTRAIT -> "portrait"
                else -> "undefined"
            }

        val name = "${prefix}crash_${System.currentTimeMillis()}$suffix"
        val file = File(logDir, name)
        logDir.mkdirs()
        file.bufferedWriter().use { writer ->
            fun line(message: String) = writer.appendLine("[$timestamp] [Error] $message")

            line(
                "FATAL EXCEPTION in thread '${thread.name}' on ${Build.MANUFACTURER} " +
                    "${Build.MODEL} / Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}), " +
                    "$orientation, screen ${configuration.screenWidthDp}x${configuration.screenHeightDp}dp " +
                    "@ ${configuration.densityDpi}dpi, app ${application.packageName}"
            )
            // `stackTraceToString()` starts with `ExceptionType: message` and already carries every
            // `Caused by` chain, so each of its lines is written as one log entry below.
            throwable.stackTraceToString().lineSequence().forEach { frame -> line(frame.trimEnd()) }
        }

        // Mirror the crash to logcat as well; the recording service captures logcat into its own
        // log file, so a recording session also carries the trace.
        Log.e(TAG, "FATAL EXCEPTION recorded to $name", throwable)
    }
}

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

package com.github.yumelira.yumebox.startup

import android.util.Log
import com.github.yumelira.yumebox.domain.model.StructuredLogCollector
import com.github.yumelira.yumebox.domain.model.StructuredLogEntry
import timber.log.Timber

/**
 * A [Timber.Tree] that captures warning/error-level log calls and converts them into
 * [StructuredLogEntry] instances for the [StructuredLogCollector].
 *
 * Only `WARN` and `ERROR` levels are forwarded since they represent the operational/failure tiers.
 * `DEBUG`/`INFO` are too noisy for structured persistence and are already written to logcat via
 * [Timber.DebugTree].
 */
class StructuredLoggingTree(private val collector: StructuredLogCollector) : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= Log.WARN
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val entry =
            when (priority) {
                Log.WARN ->
                    StructuredLogEntry.operational(
                        action = tag ?: "unknown",
                        status = "warning",
                        message = message,
                    )
                Log.ERROR ->
                    StructuredLogEntry.failure(
                        action = tag ?: "unknown",
                        message = message,
                        detail = t?.message,
                    )
                else -> return
            }
        collector.append(entry)
    }
}

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
 */

package com.github.nomadboxlab.monadbox.startup

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.repository.LogRecordGateway
import com.github.nomadboxlab.monadbox.service.StatusProvider
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.root.RootTunRuntimeRecovery
import com.github.nomadboxlab.monadbox.service.root.RootTunStateStore

class RuntimeLogRecordingCoordinator(
    private val application: Application,
    private val logRecordGateway: LogRecordGateway,
) {
    @Volatile private var started = false

    private val rootTunStateStore by lazy { RootTunStateStore(application) }

    private val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action ?: return) {
                    Intents.ACTION_CLASH_STARTED -> ensureRecordingForActiveRuntime()
                }
            }
        }

    fun start() {
        if (started) return
        synchronized(this) {
            if (started) return
            registerReceiver()
            started = true
        }
        ensureRecordingForActiveRuntime()
    }

    fun ensureRecordingForActiveRuntime() {
        if (!isAnyRuntimeActive()) return
        if (logRecordGateway.isRecording) return
        logRecordGateway.start(application)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        val filter = IntentFilter().apply { addAction(Intents.ACTION_CLASH_STARTED) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            application.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            application.registerReceiver(receiver, filter)
        }
    }

    private fun isAnyRuntimeActive(): Boolean {
        val rootStatus =
            RootTunRuntimeRecovery.recoverStaleTransition(
                context = application,
                status = rootTunStateStore.snapshot(),
            )
        return StatusProvider.isRuntimeActive(ProxyMode.Tun) ||
            StatusProvider.isRuntimeActive(ProxyMode.Http) ||
            rootStatus.state.isActive ||
            rootStatus.runtimeReady
    }
}

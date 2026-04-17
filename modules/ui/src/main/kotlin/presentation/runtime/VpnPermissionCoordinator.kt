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
 *
 */

package com.github.nomadboxlab.monadbox.presentation.runtime

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.presentation.component.GlobalDialogPresenter
import dev.oom_wg.purejoy.mlang.MLang
import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VpnPermissionLaunchRequest(val id: Long, val intent: Intent)

class VpnPermissionCoordinator(private val appScope: CoroutineScope) {
    private data class PendingRequest(
        val request: VpnPermissionLaunchRequest,
        val onGranted: suspend () -> Unit,
    )

    private val nextId = AtomicLong(1L)
    private val lock = Any()
    private val queue = ArrayDeque<PendingRequest>()
    private val callbacks = mutableMapOf<Long, suspend () -> Unit>()
    private val _currentRequest = MutableStateFlow<VpnPermissionLaunchRequest?>(null)
    val currentRequest: StateFlow<VpnPermissionLaunchRequest?> = _currentRequest.asStateFlow()

    fun requestPermission(intent: Intent, onGranted: suspend () -> Unit) {
        val request = VpnPermissionLaunchRequest(id = nextId.getAndIncrement(), intent = intent)
        val pending = PendingRequest(request = request, onGranted = onGranted)
        synchronized(lock) {
            if (_currentRequest.value == null) {
                callbacks[request.id] = onGranted
                _currentRequest.value = request
            } else {
                queue.addLast(pending)
            }
        }
    }

    fun resolve(requestId: Long, granted: Boolean) {
        val onGranted =
            synchronized(lock) {
                val callback = callbacks.remove(requestId)
                val next = if (queue.isEmpty()) null else queue.removeFirst()
                if (next == null) {
                    _currentRequest.value = null
                } else {
                    callbacks[next.request.id] = next.onGranted
                    _currentRequest.value = next.request
                }
                callback
            }

        if (granted && onGranted != null) {
            appScope.launch { onGranted() }
        }
    }
}

@Composable
fun VpnPermissionHost(coordinator: VpnPermissionCoordinator) {
    val request by coordinator.currentRequest.collectAsStateWithLifecycle()
    var activeRequestId by remember { mutableLongStateOf(0L) }
    var lastLaunchedRequestId by remember { mutableStateOf<Long?>(null) }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val requestId = activeRequestId
            if (requestId != 0L) {
                coordinator.resolve(
                    requestId = requestId,
                    granted = result.resultCode == Activity.RESULT_OK,
                )
                if (result.resultCode != Activity.RESULT_OK) {
                    GlobalDialogPresenter.showError(MLang.NetworkSettings.Error.VpnDenied)
                }
            }
            activeRequestId = 0L
            lastLaunchedRequestId = null
        }

    LaunchedEffect(request?.id) {
        val current = request ?: return@LaunchedEffect
        if (lastLaunchedRequestId == current.id) {
            return@LaunchedEffect
        }
        activeRequestId = current.id
        lastLaunchedRequestId = current.id
        launcher.launch(current.intent)
    }
}

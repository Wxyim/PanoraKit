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

import android.content.Intent
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

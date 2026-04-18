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
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeFailurePresenter
import dev.oom_wg.purejoy.mlang.MLang

@Composable
fun VpnPermissionHost(
    coordinator: VpnPermissionCoordinator,
    runtimeFailurePresenter: RuntimeFailurePresenter,
) {
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
                    runtimeFailurePresenter.showGlobalError(
                        message = MLang.NetworkSettings.Error.VpnDenied,
                        title = MLang.Component.Message.Error,
                    )
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

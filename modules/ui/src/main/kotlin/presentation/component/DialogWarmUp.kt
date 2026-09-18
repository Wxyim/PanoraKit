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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos

/**
 * One-time warm-up for the shared Miuix dialog machinery ([AppDialog] /
 * [SuperDialog]) used by every dialog in the app. On a cold start the first
 * dialog (e.g. the no-profile mode-switch failure on the home page) is
 * composed from scratch while other UI is still animating, which drops
 * frames. Pre-composing the dialog once, invisibly (its enter animation
 * slides it in from below the screen), warms the shared class-loading / JIT
 * path so all subsequent dialogs reuse warm render state.
 */
@Composable
fun DialogWarmUp() {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withFrameNanos {}
        withFrameNanos {}
        visible.value = true
        withFrameNanos {}
        visible.value = false
    }

    AppDialog(
        show = visible.value,
        onDismissRequest = {},
        renderInRootScaffold = true,
    ) {
        ToastDialogInfoContent(onConfirm = {})
    }
}

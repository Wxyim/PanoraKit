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

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color

/**
 * Per-process, so a warm-up is paid once even when the activity is recreated (rotation, theme
 * change) and always before the first user-triggered dialog of this process.
 */
private var dialogStackWarmedUp = false

/**
 * Composes the app's dialog stack once, invisibly, right after the main shell first draws.
 *
 * A dialog is drawn by `SuperDialog` -> `MiuixPopupUtils.DialogLayout`/`DialogEntry` ->
 * `DialogContentLayout`, a large composable tree with its own enter/exit transitions and gestures.
 * None of it runs until the user opens a dialog, and none of it is in the shipped baseline profile
 * (the committed `baseline-prof.txt` has no entries for any of those classes). The first dialog of
 * a process therefore runs interpreted/JIT-cold *while its enter animation is already on screen*,
 * which is the first-open jank: opening any dialog once warms the shared code and the next dialog
 * is smooth.
 *
 * Warming the same code path here moves that cost to startup, before any dialog is on screen, and
 * the throwaway dialog is invisible: the window dim is off, the surface is transparent and the whole
 * overlay is drawn at zero alpha. It is disposed after a couple of frames, then this composable
 * renders nothing for the rest of the process. The content mirrors what the toast/error dialogs draw
 * (see `ToastDialogHost`) so the confirmation button and its icon are warmed too.
 */
@Composable
fun DialogWarmUpHost() {
    if (dialogStackWarmedUp) return

    var warming by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Let the main shell's own first frames land before paying for the dialog tree, so the
        // warm-up does not sit on the startup critical path.
        withFrameNanos {}
        withFrameNanos {}
        warming = true
        // Compose, measure and draw the dialog tree (the part that needs to be JIT-compiled), then
        // give it a second frame to settle before the host is torn down.
        withFrameNanos {}
        withFrameNanos {}
        dialogStackWarmedUp = true
        warming = false
    }

    if (warming) {
        AppDialog(
            show = true,
            onDismissRequest = {},
            enableWindowDim = false,
            backgroundColor = Color.Transparent,
            modifier = Modifier.alpha(0f),
        ) {
            ToastDialogInfoContent(onConfirm = {})
        }
    }
}

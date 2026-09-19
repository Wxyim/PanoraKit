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
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * One-time warm-up for the shared Miuix dialog machinery ([AppDialog] /
 * [SuperDialog]) used by every dialog in the app. On a cold start the first
 * dialog (e.g. the no-profile mode-switch failure on the home page) is
 * composed from scratch while other UI is still animating, which drops
 * frames. Rendering an invisible warm-up dialog once at startup (no dim,
 * empty panel) forces the shared class-loading / JIT / first-draw path to run
 * so all subsequent dialogs reuse warm render state.
 */
private const val DIALOG_WARM_UP_DURATION_MS = 500L

@Composable
fun DialogWarmUp() {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Let the first frames of the home screen settle before warming up.
        withFrameNanos {}
        withFrameNanos {}
        visible.value = true
        // Keep the dialog composed and on-screen for a full enter cycle. A
        // one-frame flash starts off-screen (the enter animation slides it up
        // from below) so the panel is never actually drawn; the shader / JIT
        // warm-up must see the real first draw to matter.
        delay(DIALOG_WARM_UP_DURATION_MS)
        visible.value = false
    }

    AppDialog(
        show = visible.value,
        onDismissRequest = {},
        enableWindowDim = false,
        // Match the root Scaffold's container color so the warm-up panel is
        // invisible while still exercising the real first-draw path.
        backgroundColor = MiuixTheme.colorScheme.surface,
        renderInRootScaffold = true,
    ) {}
}

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
 * change) and always before the first user-triggered overlay of this process.
 */
private var overlayStackWarmedUp = false

/** Roughly one enter transition (~350ms at 60fps) plus a frame of slack. */
private const val WarmUpFrames = 24

/**
 * Opens one dialog and one bottom sheet, invisibly, right after the hosting shell first draws, so
 * the app's shared overlay machinery is already warm when the user opens the first real one.
 *
 * Every dialog and sheet goes through the same Miuix overlay host: [MiuixPopupHost] plus
 * `AnimatedVisibility`/`MutableTransitionState`, the dialog/popup transition specs, `nextZIndex`,
 * the `LocalDialogStates`/`LocalPopupStates` lists, `graphicsLayer`/RenderNode creation and the
 * `drawBehind` dim. None of it runs until the first overlay is shown, and none of it is in the
 * shipped baseline profile, so that first overlay composes its tree JIT/class-load-cold *while its
 * enter animation is already running*: the animation reaches its end before the content's first
 * frame, and the overlay pops in whole instead of sliding/fading - the reported first-dialog
 * flicker. Opening any one overlay fixes the rest for that process (the shared machinery is warm);
 * in the wizard, tapping the theme-mode row into its bottom sheet is a user-visible example of it.
 *
 * Both overlay kinds are warmed because either is enough to warm the shared half, and warming the
 * specific half means the app's very first dialog *and* its very first sheet are smooth. The
 * throwaway overlays are invisible: no window dim, transparent surface/handle and the whole thing
 * drawn at zero alpha. They are held until their enter transitions settle, then torn down, after
 * which this composable renders nothing for the rest of the process.
 *
 * This is hosted by every shell a user can reach first: `MainActivity` for an already-set-up
 * install, and the first-run wizard (`OnboardingBaseActivity`) for a clean one. A clean install
 * always walks the wizard before the main shell, so the warm-up lands before the first overlay that
 * install can open. It also keeps these classes in the sampled baseline profile even when a
 * collection never leaves the wizard (the shipped profile has no `NavHost` and no app screen
 * composables).
 */
@Composable
fun OverlayWarmUpHost() {
    if (overlayStackWarmedUp) return

    var warming by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Let the host shell's own first frames land before paying for the overlay trees, so the
        // warm-up does not sit on the startup critical path.
        withFrameNanos {}
        withFrameNanos {}
        warming = true
        // Hold both overlays composed through the whole enter transition: the layer blocks and the
        // transition/dim machinery only run every frame while the animations are live, so a couple
        // of frames would leave that half of the path cold.
        repeat(WarmUpFrames) { withFrameNanos {} }
        overlayStackWarmedUp = true
        warming = false
    }

    if (!warming) return

    // Dialog: SuperDialog -> MiuixPopupUtils.DialogLayout/DialogEntry -> DialogContentLayout. The
    // content mirrors what the toast/error dialogs draw (see `ToastDialogHost`) so the confirmation
    // button and its icon are warmed too.
    AppDialog(
        show = true,
        onDismissRequest = {},
        enableWindowDim = false,
        backgroundColor = Color.Transparent,
        modifier = Modifier.alpha(0f),
    ) {
        ToastDialogInfoContent(onConfirm = {})
    }

    // Bottom sheet: AppActionBottomSheet -> SuperBottomSheet -> BottomSheetContentLayout, the path
    // behind every `ConfigActionMenuRow` (theme mode, and the rest of the settings-style rows).
    AppActionBottomSheet(
        show = true,
        title = "",
        onDismissRequest = {},
        enableWindowDim = false,
        backgroundColor = Color.Transparent,
        dragHandleColor = Color.Transparent,
        modifier = Modifier.alpha(0f),
        content = {},
    )
}

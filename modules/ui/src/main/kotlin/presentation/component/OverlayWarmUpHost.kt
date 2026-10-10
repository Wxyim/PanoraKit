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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Palette
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Scaffold

/**
 * Per-process, so a warm-up is paid once even when the activity is recreated (rotation, theme
 * change) and always before the first user-triggered overlay of this process.
 */
private var overlayStackWarmedUp = false

/**
 * One full enter/exit transition at 60fps. The count matters more than the wall time: every frame
 * of the transition executes the per-frame path (layer block, dim draw, transition machinery) that
 * the first real overlay drops frames on, so the hold length is what makes that path warm.
 */
private const val EnterFrames = 36
private const val ExitFrames = 24

/**
 * Alpha for the throwaway overlays: about one 8-bit step of the framebuffer, i.e. not perceptible.
 *
 * It must not be zero (and the overlays must not be moved off-screen): both of those make the first
 * real overlay skip rasterising the warm-up entirely, so nothing is warmed. The first overlay a
 * process draws is slow precisely because the raster/composite path for it (rounded-corner clip,
 * full-size layer, transform) is being built for the first time - which only happens for content
 * that actually reaches the screen.
 */
private const val WarmUpAlpha = 0.004f

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
 * throwaway overlays draw exactly what the real ones draw - same surface colour, clip, layers and
 * content - and are hidden with [WarmUpAlpha] alone. Earlier versions hid them with `alpha = 0` or
 * by moving them off-screen, and both were no-ops: nothing reached the screen, so the raster path
 * that a first overlay actually pays for was never built. Only the window dim is skipped, because
 * it is a sibling of the content and would darken the screen for real. Both transitions run, then
 * the overlays are torn down, after which this composable renders nothing for the rest of the
 * process.
 *
 * This is hosted by every shell a user can reach first: `MainActivity` for an already-set-up
 * install, and the first-run wizard (`OnboardingBaseActivity`) for a clean one. A clean install
 * always walks the wizard before the main shell, so the warm-up lands before the first overlay that
 * install can open. It also keeps these classes in the sampled baseline profile even when a
 * collection never leaves the wizard (the shipped profile has no `NavHost` and no app screen
 * composables).
 *
 * On real devices the two overlays are rendered inside a dedicated, input-transparent window (a
 * compose `Dialog` whose window is `FLAG_NOT_TOUCHABLE | FLAG_NOT_FOCUSABLE`, invisible by window
 * alpha, with `renderInRootScaffold = false` so both register with the local `Scaffold`'s popup
 * host): the same overlay code is warmed, but at the window-manager level the warm-up can no longer
 * intercept a touch, a back gesture or the IME - before this it borrowed input from the user for
 * the whole warm-up. Emulators (including the software-rendered profile-collection AVD) keep the
 * original in-app-window shape, which is what every collection has run with.
 */
@Composable
fun OverlayWarmUpHost() {
    if (overlayStackWarmedUp) return

    var warming by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Let the host shell's own first frames land before paying for the overlay trees, so the
        // warm-up does not sit on the startup critical path.
        withFrameNanos {}
        withFrameNanos {}
        warming = true
        visible = true
        // Hold both overlays composed through the whole enter transition: the layer blocks and the
        // transition machinery only run every frame while the animations are live, so a couple of
        // frames would leave that half of the path cold.
        repeat(EnterFrames) { withFrameNanos {} }
        // Then run the exit transition as well: the user closes the first dialog they open, and the
        // teardown path is as cold as the open path.
        visible = false
        repeat(ExitFrames) { withFrameNanos {} }
        overlayStackWarmedUp = true
        warming = false
    }

    if (!warming) return

    if (isEmulator) {
        // Emulators, including the software-rendered profile-collection AVD, keep the original
        // in-app-window shape: it is what every collection has run with, and collections keep
        // sampling the overlay paths through it. It consumes input while up, which nobody is
        // around to notice on an emulator.
        OverlayWarmUpContent(
            show = visible,
            renderInRootScaffold = true,
            modifier = Modifier.alpha(WarmUpAlpha).clearAndSetSemantics {},
        )
    } else {
        // Real devices render the same overlays inside a dedicated, input-transparent window:
        // invisible by window alpha and, at the window-manager level, unable to intercept touches,
        // back gestures or the IME (see `InvisibleWarmUpWindow`). The drawing work - and therefore
        // the warming - is unchanged.
        Dialog(
            onDismissRequest = {},
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
        ) {
            InvisibleWarmUpWindow()
            Scaffold { _ ->
                OverlayWarmUpContent(
                    show = visible,
                    renderInRootScaffold = false,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
        }
    }
}

/**
 * The two throwaway overlays themselves - identical content in both hosts. Drawn for real (real
 * surface colour, real clip, real layers) and, in the in-app host, hidden by [WarmUpAlpha] alone;
 * the windowed host hides them by window alpha instead. Semantics are cleared either way: alpha
 * hides the pixels, not the accessibility tree, and a phantom dialog that screen readers can focus
 * (and that shows up in UI-automation surface digests) is a real regression, not a warm-up detail.
 */
@Composable
private fun OverlayWarmUpContent(show: Boolean, renderInRootScaffold: Boolean, modifier: Modifier) {
    // Dialog: SuperDialog -> MiuixPopupUtils.DialogLayout/DialogEntry -> DialogContentLayout. The
    // content mirrors what the toast/error dialogs draw (see `ToastDialogHost`) so the confirmation
    // button and its icon are warmed too.
    AppDialog(
        show = show,
        title = MLang.Component.Message.Hint,
        onDismissRequest = {},
        enableWindowDim = false,
        renderInRootScaffold = renderInRootScaffold,
        modifier = modifier,
    ) {
        ToastDialogInfoContent(onConfirm = {})
    }

    // Bottom sheet: AppActionBottomSheet -> SuperBottomSheet -> BottomSheetContentLayout, the path
    // behind every `ConfigActionMenuRow`. The title and rows mirror the theme-mode sheet that
    // surfaced this cost by hand: `ConfigSelectionBottomSheet` lays out exactly this shape.
    AppActionBottomSheet(
        show = show,
        title = MLang.AppSettings.Interface.ThemeModeTitle,
        onDismissRequest = {},
        enableWindowDim = false,
        renderInRootScaffold = renderInRootScaffold,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf(
                    MLang.AppSettings.Interface.ThemeModeSystem,
                    MLang.AppSettings.Interface.ThemeModeLight,
                    MLang.AppSettings.Interface.ThemeModeDark,
                )
                .forEach { label ->
                    AppActionTile(title = label, imageVector = MonadIcons.Palette, onClick = {})
                }
        }
    }
}

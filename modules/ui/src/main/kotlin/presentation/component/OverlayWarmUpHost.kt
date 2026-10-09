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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Palette
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.TextField

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

    // Drawn for real (real surface colour, real clip, real layers) and only hidden by alpha, so the
    // raster path the first user overlay pays for is exercised here instead. The semantics are
    // cleared on top: alpha hides the pixels, not the accessibility tree, and a phantom dialog that
    // screen readers can focus (and that shows up in UI-automation surface digests) is a real
    // regression, not a warm-up detail.
    val imperceptible = Modifier.alpha(WarmUpAlpha).clearAndSetSemantics {}

    // Dialog: SuperDialog -> MiuixPopupUtils.DialogLayout/DialogEntry -> DialogContentLayout. The
    // content mirrors what the toast/error dialogs draw (see `ToastDialogHost`) so the confirmation
    // button and its icon are warmed too.
    AppDialog(
        show = visible,
        title = MLang.Component.Message.Hint,
        onDismissRequest = {},
        enableWindowDim = false,
        modifier = imperceptible,
    ) {
        ToastDialogInfoContent(onConfirm = {})
    }

    // Bottom sheet: AppActionBottomSheet -> SuperBottomSheet -> BottomSheetContentLayout, the path
    // behind every `ConfigActionMenuRow`. The title and rows mirror the theme-mode sheet that
    // surfaced this cost by hand: `ConfigSelectionBottomSheet` lays out exactly this shape.
    AppActionBottomSheet(
        show = visible,
        title = MLang.AppSettings.Interface.ThemeModeTitle,
        onDismissRequest = {},
        enableWindowDim = false,
        modifier = imperceptible,
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

    // Settings -> theme colour picker sheet. Miuix's `ColorPicker` is an order of magnitude heavier
    // than the rows above (four colour models, gradients, sliders) and is followed by a hex field,
    // so the first user open of that sheet was still janky once the overlay machinery itself had
    // been warmed. The title, actions and content mirror `ThemeColorPickerSheet`.
    //
    // Unlike the two overlays above, this one is composed at zero alpha rather than drawn for real:
    // its first draw builds gradient/shader state, and the profile collection device renders in
    // software - rasterising that on every app start is a stability risk for the run (a collection
    // crashed its emulator the first time this content was ever composed). Its first-open cost is
    // dominated by composing it - class init, colour-model maths, brush construction - which zero
    // alpha still warms.
    val composedOnly = Modifier.alpha(0f).clearAndSetSemantics {}
    AppActionBottomSheet(
        show = visible,
        title = MLang.AppSettings.Interface.ColorThemePickerTitle,
        onDismissRequest = {},
        enableWindowDim = false,
        modifier = composedOnly,
        startAction = { AppBottomSheetCloseAction(onClick = {}) },
        endAction = { AppBottomSheetConfirmAction(onClick = {}) },
    ) {
        ColorPicker(
            color = WarmUpPickerColor,
            onColorChanged = {},
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = WarmUpPickerHex,
            onValueChange = {},
            label = MLang.AppSettings.Interface.ColorThemeCodeLabel,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
    }
}

/** A neutral seed for the warm-up picker: the value does not matter, only that it draws. */
private val WarmUpPickerColor = Color(0xFF3B82F6)
private const val WarmUpPickerHex = "#3B82F6"

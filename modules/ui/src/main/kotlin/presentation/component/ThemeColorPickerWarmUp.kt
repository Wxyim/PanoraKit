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

import android.os.SystemClock
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextField

/**
 * Per-process: the picker sheet's first open is paid once, on the first screen that shows its entry
 * point, and never again - even if the activity is recreated.
 */
private var pickerSheetWarmedUp = false

/** Frames to let the warm-up sheet's composition, layout and draw land before the hold starts. */
private const val DrawFrames = 3

/**
 * How long the sheet is held after those frames, in wall-clock time. The enter transition is
 * `folmeSpring(response = 0.38)` - time-based, ~0.6s to settle - so a frame count would under- or
 * over-hold depending on the device's frame rate. The hold covers the whole slide with the sheet
 * fully on screen, which is where the full-height layer gets recorded and rasterised.
 */
private const val HoldMillis = 600L

/**
 * Alpha of the throwaway window: about one 8-bit step of the framebuffer, i.e. not perceptible.
 *
 * It must not be zero: zero alpha would let the renderer skip the content, which is the whole point
 * here (the window has to draw for real - see the KDoc of [ThemeColorPickerSheetWarmUp]).
 */
private const val WarmUpWindowAlpha = 0.004f

/** A neutral seed for the warm-up picker: the value does not matter, only that it draws. */
private val WarmUpPickerColor = Color(0xFF3B82F6)
private const val WarmUpPickerHex = "#3B82F6"

/**
 * Opens the theme colour picker sheet once, invisibly, so that its first real open does not pay the
 * sheet's one-time setup while the slide-in animation is already running.
 *
 * What is cold on the first open is the sheet itself at full size plus the picker inside it: the
 * sheet's full-height layer being recorded and rasterised for the first time (`clip` +
 * `graphicsLayer` + background), the picker's first draw (per-slider gradients, the checkerboard
 * path, the indicator's radial gradient) and the enter spring. None of that runs until a sheet is
 * shown, so the warm-up shows the real sheet - same title, actions and content as
 * `ThemeColorPickerSheet` - and everything it draws is produced by exactly the same code.
 *
 * The sheet is hosted in a **dedicated, input-transparent window** (a compose `Dialog` whose window
 * gets [WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE] and
 * [WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE]) - not in the app's own window, where the overlay
 * host would consume every pointer event for the duration of the warm-up and steal fast taps on the
 * row, and not in the app's root popup host. At the window-manager level this window can never
 * intercept a touch, a back gesture or the IME; the app underneath behaves exactly as if it did not
 * exist. The window is hidden with [WarmUpWindowAlpha] alone (zero alpha would skip the draw), its
 * window dim is cleared, and its content clears semantics so no phantom dialog is published to
 * accessibility. `renderInRootScaffold = false` keeps the sheet inside this window: it registers
 * with the local `Scaffold`'s popup host instead of the activity's root one.
 *
 * Hosted by the screens that own the entry point (the settings page and the onboarding personalize
 * step, via [ThemeColorPickerItem]), not by app startup: the cost is paid a couple of frames after
 * the row is first shown - well before the user can reach it - and app start stays untouched. The
 * whole cycle runs once per process.
 */
@Composable
internal fun ThemeColorPickerSheetWarmUp() {
    if (pickerSheetWarmedUp) return

    var warming by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Let the hosting screen's own entrance land first: the warm-up must not sit on the path
        // that opens the page it lives on.
        withFrameNanos {}
        withFrameNanos {}
        warming = true
        repeat(DrawFrames) { withFrameNanos {} }
        val deadline = SystemClock.uptimeMillis() + HoldMillis
        while (SystemClock.uptimeMillis() < deadline) withFrameNanos {}
        // Tear down once the spring has settled: entrance and teardown of the sheet have both run
        // by now, so the first real open and close ride warmed paths.
        pickerSheetWarmedUp = true
        warming = false
    }

    if (!warming) return

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
            AppActionBottomSheet(
                show = true,
                title = MLang.AppSettings.Interface.ColorThemePickerTitle,
                onDismissRequest = {},
                enableWindowDim = false,
                renderInRootScaffold = false,
                modifier = Modifier.clearAndSetSemantics {},
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
    }
}

/**
 * Makes the hosting dialog window invisible and completely input-transparent.
 *
 * The dialog window is a real window of our own app, so without this it would sit above the
 * activity, dim it and swallow touches in its area. The flags turn it into a purely visual - and
 * here imperceptible - layer: touches and keys pass through to the activity as if the window did
 * not exist. Reapplied on every recomposition, so the values cannot be reset by the dialog's own
 * parameter updates.
 */
@Composable
private fun InvisibleWarmUpWindow() {
    val view = LocalView.current
    SideEffect {
        val window = (view.parent as? DialogWindowProvider)?.window ?: return@SideEffect
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(0f)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        window.setGravity(Gravity.TOP or Gravity.START)
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
        )
        window.attributes = window.attributes.apply { alpha = WarmUpWindowAlpha }
    }
}

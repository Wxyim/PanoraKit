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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.TextField

/**
 * Per-process: the picker's first real draw is paid once, on the first screen that shows its entry
 * point, and never again - even if the activity is recreated.
 */
private var pickerDrawWarmedUp = false

/**
 * How many frames the warm-up content stays in the tree. One frame is enough for the draw phase to
 * run; the hold is cheap insurance that composition, layout and draw all happen before teardown.
 */
private const val WarmUpFrames = 8

/**
 * Alpha for the throwaway draw: about one 8-bit step of the framebuffer, i.e. not perceptible.
 *
 * It must not be zero: zero alpha lets Compose skip the content's draw phase entirely (the layer's
 * paint is a no-op), so nothing gets warmed. The whole point here is the draw phase - see the KDoc
 * of [ThemeColorPickerDrawWarmUp].
 */
private const val WarmUpAlpha = 0.004f

/** A neutral seed for the warm-up picker: the value does not matter, only that it draws. */
private val WarmUpPickerColor = Color(0xFF3B82F6)
private const val WarmUpPickerHex = "#3B82F6"

/**
 * Close to a real picker sheet's content width, so the warm-up builds its gradients at a realistic
 * scale - hue strip, saturation/value/alpha strips and the checkerboard path are all width-derived.
 */
private val WarmUpPickerWidth = 320.dp

/**
 * Draws the theme colour picker once, invisibly, at zero interaction cost, so that its *first real
 * draw* is not the one the user waits for when the picker sheet opens.
 *
 * Composing the picker is not the expensive half: its per-frame cost lays in the draw phase - each
 * `ColorSlider` builds a `Brush.horizontalGradient` and a `Stroke` inside `drawWithCache`, the
 * alpha slider additionally builds a checkerboard `Path` from hundreds of rects, and the indicator
 * builds a `Brush.radialGradient` - all of it class-loading and shader-pipeline-cold the first
 * time. Warming only its composition (the previous `alpha = 0` approach) warms none of that,
 * because zero alpha skips the draw phase; the first user-visible open then pays shader/setup cost
 * while the sheet's enter animation is already running, which reads as jank.
 *
 * This is hosted by the screens that actually own the entry point (the settings page and the
 * onboarding personalize step), not by app startup: the cost is paid a couple of frames after the
 * row is first shown, which is still comfortably before the user taps it, and app start stays
 * untouched.
 *
 * The composable renders nothing until its host row exists, and nothing after the first run; the
 * content is hidden with [WarmUpAlpha] alone (never zero alpha, never drawn off-screen - both skip
 * the draw phase), with semantics cleared so no phantom row is published to accessibility.
 */
@Composable
internal fun ThemeColorPickerDrawWarmUp() {
    if (pickerDrawWarmedUp) return

    var warming by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Let the hosting screen's own entrance land first: the warm-up must not sit on the path
        // that opens the page it lives on.
        withFrameNanos {}
        withFrameNanos {}
        warming = true
        repeat(WarmUpFrames) { withFrameNanos {} }
        pickerDrawWarmedUp = true
        warming = false
    }

    if (!warming) return

    // The warm-up content mirrors the real sheet (`ColorPicker` plus the hex field) at its natural
    // height, but the host reports a 1x1 size to the parent layout: the picker keeps its full
    // height (a parent-bounded `matchParentSize` would squeeze the sliders), the row that hosts it
    // does not move, and 1x1 - not 0x0 - is the smallest size at which Compose still runs the draw
    // phase. Nothing is clipped on the way out: no ancestor up to the scroll container clips, and
    // the drawn pixels are invisible anyway (they only differ from the background by alpha 0.004).
    Layout(
        content = {
            Column(
                modifier =
                    Modifier.width(WarmUpPickerWidth).alpha(WarmUpAlpha).clearAndSetSemantics {}
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
    ) { measurables, _ ->
        val placeable = measurables.first().measure(Constraints())
        layout(1, 1) { placeable.place(0, 0) }
    }
}

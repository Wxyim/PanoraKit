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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.dp

/**
 * [MIUIX-WORKAROUND] Crash guard for Miuix `TextField`s on Miuix 0.8.8.
 *
 * A focused Miuix `TextField` draws its border via `DrawScope.inset(strokePx / 2f)`. Compose UI's
 * `DrawTransform.inset` rejects a negative result size, so when the field is drawn at a size below
 * its 2dp stroke — which happens when the IME opens on a short (landscape) screen and the enclosing
 * bottom sheet / dialog content area is squeezed towards zero — the draw pass throws
 * `IllegalArgumentException("Width and height must be greater than or equal to zero")` and the app
 * crashes.
 *
 * Upstream fixed this in `compose-miuix-ui/miuix` commit 858307e ("library: fix TextField border
 * drawing when size is too small", first shipped in 0.9.0). Until MonadBox upgrades Miuix, apply
 * this modifier to every Miuix `TextField`: it skips drawing on frames where the field collapses
 * below a drawable size (such frames are invisible anyway) and is a no-op at normal sizes. Remove
 * the applications once Miuix >= 0.9.2 is in use.
 */
fun Modifier.miuixTextFieldDrawGuard(): Modifier = drawWithContent {
    // The library stroke is 2dp; keep 1dp of headroom.
    val minDrawablePx = 3.dp.toPx()
    if (size.width > minDrawablePx && size.height > minDrawablePx) {
        drawContent()
    }
}

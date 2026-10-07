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

package com.github.nomadboxlab.monadbox.presentation.theme

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.max

/**
 * Returns the bottom inset that bottom-anchored content has to stay clear of.
 *
 * Devices disagree on which inset describes the bottom obstruction: with gesture navigation some
 * report a zero navigation bar inset while the gesture inset stays non-zero, and others do the
 * opposite. Taking the larger of both keeps every bottom-anchored element - the floating bottom
 * navigation bar, its page overlay padding and floating action buttons - aligned on the same value
 * on every device.
 */
@Composable
fun rememberSystemBottomInset(): Dp {
    val density = LocalDensity.current
    return with(density) {
        max(
                WindowInsets.navigationBars.getBottom(this),
                WindowInsets.systemGestures.getBottom(this),
            )
            .toDp()
    }
}

/** Pads the bottom edge by [rememberSystemBottomInset]. */
@Composable
fun Modifier.systemBottomInsetPadding(): Modifier =
    this.padding(bottom = rememberSystemBottomInset())

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

package com.github.nomadboxlab.monadbox.presentation.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.domain.model.ProxyDisplayMode

object ProxyAdaptiveDisplayModeDefaults {
    val DoubleDetailedMinWidth = 840.dp
    val DoubleSimpleMinWidth = 600.dp
    val SingleDetailedMinWidth = 420.dp
}

fun resolveAdaptiveProxyDisplayMode(maxWidth: Dp, prefersTwoPane: Boolean): ProxyDisplayMode {
    return when {
        maxWidth >= ProxyAdaptiveDisplayModeDefaults.DoubleDetailedMinWidth ->
            ProxyDisplayMode.DOUBLE_DETAILED
        prefersTwoPane || maxWidth >= ProxyAdaptiveDisplayModeDefaults.DoubleSimpleMinWidth ->
            ProxyDisplayMode.DOUBLE_SIMPLE
        maxWidth >= ProxyAdaptiveDisplayModeDefaults.SingleDetailedMinWidth ->
            ProxyDisplayMode.SINGLE_DETAILED
        else -> ProxyDisplayMode.SINGLE_SIMPLE
    }
}

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

package com.github.nomadboxlab.monadbox.presentation.screen.node

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.github.nomadboxlab.monadbox.presentation.component.CountryFlagCircle
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
internal fun CountryFlagFilledIcon(
    countryCode: String,
    size: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
) {
    // Privacy: render the country code as an emoji flag (offline) instead of fetching
    // a PNG from https://flagcdn.com on every node-card render. The third party would
    // otherwise learn which countries the user is viewing.
    CountryFlagCircle(
        countryCode = countryCode,
        modifier =
            modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        size = size,
    )
}

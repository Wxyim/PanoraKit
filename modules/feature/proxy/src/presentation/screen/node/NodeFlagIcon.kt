/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.presentation.screen.node

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.github.panpf.sketch.AsyncImage as SketchAsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.yumelira.yumebox.common.util.LocaleUtil
import com.github.yumelira.yumebox.presentation.component.CountryFlagCircle
import java.util.Locale
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
internal fun CountryFlagFilledIcon(
    countryCode: String,
    size: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
) {
    val normalized =
        remember(countryCode) {
            LocaleUtil.normalizeRegionCode(countryCode)
                ?.trim()
                ?.uppercase(Locale.ROOT)
                ?.takeIf { it.length == 2 && it.all(Char::isLetter) }
        }

    if (normalized == null) {
        CountryFlagCircle(countryCode = countryCode, size = size, modifier = modifier)
        return
    }

    val context = LocalContext.current
    val placeholderColorInt = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.10f).toArgb()
    val iconUri = remember(normalized) { "https://flagcdn.com/w80/${normalized.lowercase(Locale.ROOT)}.png" }
    val request =
        remember(context, iconUri, placeholderColorInt) {
            ImageRequest(context, iconUri) {
                placeholder(IntColorDrawableStateImage(placeholderColorInt))
                error(IntColorDrawableStateImage(placeholderColorInt))
                crossfade(true)
            }
        }

    SketchAsyncImage(
        request = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
    )
}

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

package com.github.yumelira.yumebox.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.common.util.LocaleUtil
import dev.oom_wg.purejoy.mlang.MLang
import java.util.Locale
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CountryFlagCircle(countryCode: String, modifier: Modifier = Modifier, size: Dp = 18.dp) {
    val normalizedCode =
        LocaleUtil.normalizeRegionCode(countryCode)?.trim()?.uppercase(Locale.ROOT).orEmpty()
    val displaySize = size.coerceIn(16.dp, 28.dp)
    val flagText = normalizedCode.toFlagEmoji() ?: normalizedCode.take(2).ifBlank { "??" }

    Box(
        modifier =
            modifier.size(displaySize)
                .clip(CircleShape)
                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Text(
            text = flagText,
            fontSize = (displaySize.value * 0.52f).sp,
            color = MiuixTheme.colorScheme.onSurface,
            lineHeight = (displaySize.value * 0.52f).sp,
        )
    }
}

private fun String.toFlagEmoji(): String? {
    if (length != 2 || any { it !in 'A'..'Z' }) return null
    val first = Character.toChars(0x1F1E6 + (this[0] - 'A'))
    val second = Character.toChars(0x1F1E6 + (this[1] - 'A'))
    return String(first) + String(second)
}

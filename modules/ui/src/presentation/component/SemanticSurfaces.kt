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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SemanticIconSurface(
    imageVector: ImageVector,
    tone: SemanticTone,
    modifier: Modifier = Modifier,
    highEmphasis: Boolean = true,
    containerSize: Dp = 46.dp,
    iconSize: Dp = 22.dp,
    cornerRadius: Dp = 16.dp,
) {
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = highEmphasis)
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier =
            modifier
                .size(containerSize)
                .clip(shape)
                .background(style.iconContainerColor, shape)
                .border(AppTheme.strokes.default, style.borderColor.copy(alpha = 0.72f), shape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = imageVector,
            tint = style.contentColor,
            contentDescription = null,
        )
    }
}

@Composable
fun ReadonlyInfoField(
    imageVector: ImageVector,
    value: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    tone: SemanticTone = SemanticTone.Neutral,
    compact: Boolean = false,
    maxLines: Int = 2,
) {
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = false)
    val shape = RoundedCornerShape(if (compact) 16.dp else 20.dp)
    val iconCorner = RoundedCornerShape(if (compact) 10.dp else 12.dp)
    val iconContainerSize = if (compact) 30.dp else 34.dp
    val iconSize = if (compact) 16.dp else 18.dp
    val horizontalPadding = if (compact) 10.dp else 12.dp
    val verticalPadding = if (compact) 9.dp else 12.dp
    val contentSpacing = if (compact) 10.dp else 12.dp
    val textSpacing = if (compact) 2.dp else 4.dp
    val semanticDescription =
        listOfNotNull(value.takeIf(String::isNotBlank), summary?.takeIf(String::isNotBlank))
            .joinToString(separator = ", ")

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) { contentDescription = semanticDescription }
                .clip(shape)
                .background(style.containerColor, shape)
                .border(AppTheme.strokes.thin, style.borderColor, shape)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(contentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(iconContainerSize)
                    .clip(iconCorner)
                    .background(style.iconContainerColor, iconCorner),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = imageVector,
                tint = style.contentColor,
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(textSpacing),
        ) {
            Text(
                text = value,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurface,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )

            if (!summary.isNullOrBlank()) {
                Text(
                    text = summary,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

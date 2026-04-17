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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    tone: SemanticTone = SemanticTone.Neutral,
    highEmphasis: Boolean = false,
    imageVector: ImageVector? = null,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = highEmphasis)
    val shape = RoundedCornerShape(24.dp)
    val semanticDescription =
        listOfNotNull(
                title.takeIf(String::isNotBlank),
                value.takeIf(String::isNotBlank),
                summary?.takeIf(String::isNotBlank),
            )
            .joinToString(separator = ", ")

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 116.dp)
                .semantics(mergeDescendants = true) { contentDescription = semanticDescription }
                .clip(shape)
                .background(style.containerColor, shape)
                .border(AppTheme.strokes.default, style.borderColor, shape)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (imageVector != null) {
                Box(
                    modifier =
                        Modifier.size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(style.iconContainerColor, RoundedCornerShape(14.dp))
                            .border(
                                AppTheme.strokes.default,
                                style.borderColor.copy(alpha = 0.72f),
                                RoundedCornerShape(14.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = imageVector,
                        tint = style.contentColor,
                        contentDescription = null,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = title,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    color = style.contentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = endContent,
            )
        }

        if (!summary.isNullOrBlank()) {
            Text(
                text = summary,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

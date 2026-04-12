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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import com.github.yumelira.yumebox.presentation.theme.LocalSemanticColors
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class DiffLine(val lineNumber: Int?, val content: String, val type: DiffLineType)

enum class DiffLineType {
    Context,
    Added,
    Removed,
    Header,
}

@Composable
fun DiffViewer(
    lines: List<DiffLine>,
    modifier: Modifier = Modifier,
    title: String? = null,
    maxHeight: Dp = 400.dp,
) {
    val semanticColors = LocalSemanticColors.current
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape)
                .border(
                    AppTheme.strokes.default,
                    MiuixTheme.colorScheme.outline.copy(alpha = 0.12f),
                    shape,
                )
    ) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
        ) {
            Column {
                for (line in lines) {
                    val (bgColor, textColor) =
                        when (line.type) {
                            DiffLineType.Added ->
                                semanticColors.success.container to
                                    semanticColors.success.foreground
                            DiffLineType.Removed ->
                                semanticColors.danger.container to semanticColors.danger.foreground
                            DiffLineType.Header ->
                                semanticColors.info.container to semanticColors.info.foreground
                            DiffLineType.Context ->
                                MiuixTheme.colorScheme.surface.copy(alpha = 0f) to
                                    MiuixTheme.colorScheme.onSurface
                        }

                    val prefix =
                        when (line.type) {
                            DiffLineType.Added -> "+"
                            DiffLineType.Removed -> "-"
                            DiffLineType.Header -> "@@"
                            DiffLineType.Context -> " "
                        }

                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .background(bgColor)
                                .semantics {
                                    contentDescription = "${line.type.name}: ${line.content}"
                                }
                                .padding(horizontal = 10.dp, vertical = 1.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (line.lineNumber != null) {
                            Text(
                                text = line.lineNumber.toString().padStart(4),
                                color =
                                    MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(
                                        alpha = 0.5f
                                    ),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                            )
                        }
                        Text(
                            text = "$prefix ${line.content}",
                            color = textColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

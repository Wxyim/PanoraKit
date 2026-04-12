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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class TraceEntry(
    val stage: String,
    val label: String,
    val detail: String? = null,
    val tone: SemanticTone = SemanticTone.Neutral,
    val isTerminal: Boolean = false,
)

@Composable
fun TracePanel(
    entries: List<TraceEntry>,
    modifier: Modifier = Modifier,
    title: String? = null,
    maxHeight: Dp = 360.dp,
) {
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), shape)
                .border(
                    AppTheme.strokes.default,
                    MiuixTheme.colorScheme.outline.copy(alpha = 0.10f),
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

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            for ((index, entry) in entries.withIndex()) {
                val style = SemanticActionDefaults.style(tone = entry.tone)
                val isLast = index == entries.lastIndex

                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .semantics(mergeDescendants = true) {
                                contentDescription = "${entry.stage}: ${entry.label}"
                            }
                            .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Step indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier =
                                Modifier.size(8.dp)
                                    .clip(CircleShape)
                                    .background(style.contentColor, CircleShape)
                        )
                        if (!isLast) {
                            Text(
                                text = "→",
                                color =
                                    MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(
                                        alpha = 0.32f
                                    ),
                                fontSize = 10.sp,
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = entry.stage,
                                color = style.contentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                            )
                            Text(
                                text = entry.label,
                                color = MiuixTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        if (!entry.detail.isNullOrBlank()) {
                            Text(
                                text = entry.detail,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

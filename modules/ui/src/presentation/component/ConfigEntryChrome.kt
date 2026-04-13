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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Settings-2`
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ConfigRowDividerStart = 70.dp
private val ConfigRowDividerEnd = 18.dp

data class ConfigEntryActionOption(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val summary: String? = null,
    val tone: SemanticTone = SemanticTone.Neutral,
    val selected: Boolean = false,
)

@Composable
fun ConfigSettingRow(
    title: String,
    imageVector: ImageVector = Yume.`Settings-2`,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    valueLabel: String? = null,
    tone: SemanticTone = SemanticTone.Neutral,
    badgeTone: SemanticTone = tone,
    badgeLeadingDot: Boolean = false,
    showDivider: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingsRow(
            title = title,
            summary = summary,
            imageVector = imageVector,
            tone = tone,
            onClick = onClick,
            endContent = {
                if (!valueLabel.isNullOrBlank()) {
                    StatusBadge(
                        text = valueLabel,
                        tone = badgeTone,
                        leadingDot = badgeLeadingDot,
                        compact = true,
                    )
                }
            },
        )

        if (showDivider) {
            HorizontalDivider(
                modifier =
                    Modifier.padding(start = ConfigRowDividerStart, end = ConfigRowDividerEnd),
                thickness = 0.5.dp,
                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.14f),
            )
        }
    }
}

@Composable
fun InfoSettingRow(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    valueLabel: String? = null,
    tone: SemanticTone = SemanticTone.Neutral,
    badgeTone: SemanticTone = tone,
    badgeLeadingDot: Boolean = false,
    showDivider: Boolean = true,
) {
    val semanticDescription =
        listOfNotNull(
                title.takeIf(String::isNotBlank),
                summary?.takeIf(String::isNotBlank),
                valueLabel?.takeIf(String::isNotBlank),
            )
            .joinToString(separator = ", ")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .semantics(mergeDescendants = true) { contentDescription = semanticDescription }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = title,
                    color = MiuixTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!summary.isNullOrBlank()) {
                    Text(
                        text = summary,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (!valueLabel.isNullOrBlank()) {
                StatusBadge(
                    text = valueLabel,
                    tone = badgeTone,
                    leadingDot = badgeLeadingDot,
                    compact = true,
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp, end = ConfigRowDividerEnd),
                thickness = 0.5.dp,
                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.14f),
            )
        }
    }
}

@Composable
fun ConfigActionMenuRow(
    title: String,
    options: List<ConfigEntryActionOption>,
    modifier: Modifier = Modifier,
    summary: String? = null,
    valueLabel: String? = null,
    imageVector: ImageVector = Yume.`Settings-2`,
    tone: SemanticTone = SemanticTone.Neutral,
    badgeTone: SemanticTone = tone,
    badgeLeadingDot: Boolean = false,
    sheetTitle: String = title,
    showDivider: Boolean = true,
) {
    var showOptionsSheet by rememberSaveable(title) { mutableStateOf(false) }

    ConfigSettingRow(
        title = title,
        summary = summary,
        valueLabel = valueLabel,
        imageVector = imageVector,
        tone = tone,
        badgeTone = badgeTone,
        badgeLeadingDot = badgeLeadingDot,
        showDivider = showDivider,
        modifier = modifier,
        onClick = { showOptionsSheet = true },
    )

    ConfigSelectionBottomSheet(
        show = showOptionsSheet,
        title = sheetTitle,
        options = options,
        onDismissRequest = { showOptionsSheet = false },
    )
}

@Composable
fun ConfigSelectionBottomSheet(
    show: Boolean,
    title: String,
    options: List<ConfigEntryActionOption>,
    onDismissRequest: () -> Unit,
) {
    if (!show) {
        return
    }

    AppActionBottomSheet(show = true, title = title, onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { option ->
                val surfaceColor = MiuixTheme.colorScheme.surfaceVariant
                val outlineColor = MiuixTheme.colorScheme.outline
                val selectedTextColor = MiuixTheme.colorScheme.onSurface
                val normalTextColor = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.88f)
                AppActionTile(
                    title = option.title,
                    summary = option.summary,
                    imageVector = option.icon,
                    onClick = {
                        option.onClick()
                        onDismissRequest()
                    },
                    tone = SemanticTone.Neutral,
                    highEmphasis = false,
                    containerColor =
                        if (option.selected) {
                            surfaceColor.copy(alpha = 0.78f)
                        } else {
                            surfaceColor.copy(alpha = 0.58f)
                        },
                    borderColor =
                        if (option.selected) {
                            outlineColor.copy(alpha = 0.24f)
                        } else {
                            outlineColor.copy(alpha = 0.14f)
                        },
                    contentColor = if (option.selected) selectedTextColor else normalTextColor,
                    iconContainerColor =
                        if (option.selected) {
                            MiuixTheme.colorScheme.primary.copy(alpha = 0.10f)
                        } else {
                            MiuixTheme.colorScheme.surface.copy(alpha = 0.62f)
                        },
                    endContent = {
                        if (option.selected) {
                            StatusBadge(
                                text = MLang.Proxy.Selection.Current,
                                tone = SemanticTone.Brand,
                                leadingDot = true,
                                compact = true,
                            )
                        }
                    },
                )
            }
        }
    }
}

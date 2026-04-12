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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.CircleCheckBig
import com.github.yumelira.yumebox.presentation.icon.yume.`List-chevrons-up-down`
import com.github.yumelira.yumebox.presentation.icon.yume.PowerOff
import com.github.yumelira.yumebox.presentation.icon.yume.Square
import dev.oom_wg.purejoy.mlang.MLang

@Composable
fun NullableBooleanSelector(
    title: String,
    summary: String? = null,
    value: Boolean?,
    imageVector: ImageVector = Yume.CircleCheckBig,
    unsetLabel: String = MLang.Component.Selector.NotModify,
    onValueChange: (Boolean?) -> Unit,
) {
    val items =
        listOf(
            unsetLabel,
            MLang.Component.Selector.Enable,
            MLang.Component.Selector.Disable,
        )
    val selectedIndex =
        when (value) {
            null -> 0
            true -> 1
            false -> 2
        }
    var showSheet by remember { mutableStateOf(false) }

    ConfigSettingRow(
        title = title,
        summary = summary,
        valueLabel = items[selectedIndex],
        imageVector = imageVector,
        tone =
            when (value) {
                true -> SemanticTone.Success
                false -> SemanticTone.Warning
                null -> SemanticTone.Neutral
            },
        badgeTone =
            when (value) {
                true -> SemanticTone.Success
                false -> SemanticTone.Warning
                null -> SemanticTone.Neutral
            },
        badgeLeadingDot = value == true,
        onClick = { showSheet = true },
    )

    ConfigSelectionBottomSheet(
        show = showSheet,
        title = title,
        options =
            listOf(
                ConfigEntryActionOption(
                    title = items[0],
                    icon = Yume.Square,
                    tone = SemanticTone.Neutral,
                    selected = selectedIndex == 0,
                    onClick = { onValueChange(null) },
                ),
                ConfigEntryActionOption(
                    title = items[1],
                    icon = Yume.CircleCheckBig,
                    tone = SemanticTone.Success,
                    selected = selectedIndex == 1,
                    onClick = { onValueChange(true) },
                ),
                ConfigEntryActionOption(
                    title = items[2],
                    icon = Yume.PowerOff,
                    tone = SemanticTone.Warning,
                    selected = selectedIndex == 2,
                    onClick = { onValueChange(false) },
                ),
            ),
        onDismissRequest = { showSheet = false },
    )
}

@Composable
fun <T> NullableEnumSelector(
    title: String,
    summary: String? = null,
    value: T?,
    items: List<String>,
    values: List<T?>,
    imageVector: ImageVector = Yume.`List-chevrons-up-down`,
    onValueChange: (T?) -> Unit,
) {
    val selectedIndex = values.indexOf(value).coerceAtLeast(0)
    val selectedLabel = items.getOrElse(selectedIndex) { items.firstOrNull().orEmpty() }
    var showSheet by remember { mutableStateOf(false) }

    ConfigSettingRow(
        title = title,
        summary = summary,
        valueLabel = selectedLabel,
        imageVector = imageVector,
        tone = if (selectedIndex == 0) SemanticTone.Neutral else SemanticTone.Brand,
        badgeTone = if (selectedIndex == 0) SemanticTone.Neutral else SemanticTone.Brand,
        onClick = { showSheet = true },
    )

    ConfigSelectionBottomSheet(
        show = showSheet,
        title = title,
        options =
            items.mapIndexed { index, item ->
                ConfigEntryActionOption(
                    title = item,
                    icon = if (index == 0) Yume.Square else imageVector,
                    tone = if (index == 0) SemanticTone.Neutral else SemanticTone.Brand,
                    selected = index == selectedIndex,
                    onClick = {
                        if (index >= 0 && index < values.size) {
                            onValueChange(values[index])
                        }
                    },
                )
            },
        onDismissRequest = { showSheet = false },
    )
}

enum class ListMergeStrategy {

    None,
    Replace,
    Start,
    End,
}

enum class MapMergeStrategy {

    None,
    Replace,
    Merge,
}

@Composable
fun ListMergeStrategySelector(
    title: String,
    summary: String? = null,
    value: ListMergeStrategy,
    onValueChange: (ListMergeStrategy) -> Unit,
) {
    val items =
        listOf(
            MLang.Component.Selector.NotModify,
            MLang.Component.Selector.Replace,
            MLang.Component.Selector.Prepend,
            MLang.Component.Selector.Append,
        )
    val values =
        listOf(
            ListMergeStrategy.None,
            ListMergeStrategy.Replace,
            ListMergeStrategy.Start,
            ListMergeStrategy.End,
        )

    NullableEnumSelector(
        title = title,
        summary = summary,
        value = value,
        items = items,
        values = values,
        onValueChange = { onValueChange(it ?: ListMergeStrategy.None) },
    )
}

@Composable
fun MapMergeStrategySelector(
    title: String,
    summary: String? = null,
    value: MapMergeStrategy,
    onValueChange: (MapMergeStrategy) -> Unit,
) {
    val items =
        listOf(
            MLang.Component.Selector.NotModify,
            MLang.Component.Selector.Replace,
            MLang.Component.Selector.Merge,
        )
    val values = listOf(MapMergeStrategy.None, MapMergeStrategy.Replace, MapMergeStrategy.Merge)

    NullableEnumSelector(
        title = title,
        summary = summary,
        value = value,
        items = items,
        values = values,
        onValueChange = { onValueChange(it ?: MapMergeStrategy.None) },
    )
}

/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`List-chevrons-up-down`

@Composable
fun <T> EnumSelector(
    title: String,
    summary: String? = null,
    currentValue: T,
    items: List<String>,
    values: List<T>,
    imageVector: ImageVector = MonadIcons.`List-chevrons-up-down`,
    showDivider: Boolean = true,
    onValueChange: (T) -> Unit,
) {
    val selectedIndex = values.indexOf(currentValue).coerceAtLeast(0)
    val selectedLabel = items.getOrElse(selectedIndex) { items.firstOrNull().orEmpty() }

    ConfigActionMenuRow(
        title = title,
        summary = summary,
        valueLabel = selectedLabel,
        imageVector = imageVector,
        tone = if (selectedIndex == 0) SemanticTone.Neutral else SemanticTone.Brand,
        badgeTone = if (selectedIndex == 0) SemanticTone.Neutral else SemanticTone.Brand,
        showDivider = showDivider,
        options =
            items.mapIndexed { index, item ->
                ConfigEntryActionOption(
                    title = item,
                    icon = imageVector,
                    tone = if (index == selectedIndex) SemanticTone.Brand else SemanticTone.Neutral,
                    selected = index == selectedIndex,
                    onClick = {
                        if (index >= 0 && index < values.size) {
                            onValueChange(values[index])
                        }
                    },
                )
            },
    )
}

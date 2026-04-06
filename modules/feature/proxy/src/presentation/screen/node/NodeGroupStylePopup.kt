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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.yumelira.yumebox.domain.model.ProxyGroupStyle
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.SuperListPopup

internal val NodeGroupStyles = listOf(
    ProxyGroupStyle.INLINE,
    ProxyGroupStyle.FLOATING,
)

@Composable
internal fun NodeGroupStylePopup(
    show: MutableState<Boolean>,
    onDismiss: () -> Unit,
    groupStyle: ProxyGroupStyle,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.Start,
    onStyleSelected: (ProxyGroupStyle) -> Unit,
) {
    val selectedIndex = NodeGroupStyles.indexOf(groupStyle).coerceAtLeast(0)
    SuperListPopup(
        show = show.value,
        alignment = alignment,
        onDismissRequest = onDismiss,
    ) {
        ListPopupColumn {
            NodeGroupStyles.forEachIndexed { index, style ->
                DropdownImpl(
                    text = style.displayName,
                    optionSize = NodeGroupStyles.size,
                    isSelected = selectedIndex == index,
                    onSelectedIndexChange = {
                        if (style != groupStyle) onStyleSelected(style)
                        onDismiss()
                    },
                    index = index,
                )
            }
        }
    }
}

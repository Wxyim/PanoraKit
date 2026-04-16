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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.theme.sectionHPadding
import top.yukonga.miuix.kmp.basic.Card

@Composable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    insideMargin: PaddingValues = PaddingValues(0.dp),
    applyHorizontalPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    Card(
        modifier =
            if (applyHorizontalPadding) {
                modifier.sectionHPadding()
            } else {
                modifier
            },
        cornerRadius = cornerRadius.dp,
        insideMargin = insideMargin,
    ) {
        content()
    }
}

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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField

@Composable
fun TextEditBottomSheet(
    show: MutableState<Boolean>,
    title: String,
    textFieldValue: MutableState<TextFieldValue>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit = { show.value = false },
    secondaryButtonText: String = MLang.Component.Button.Cancel,
    onSecondaryClick: () -> Unit = onDismiss,
) {
    AppActionBottomSheet(show = show.value, title = title, onDismissRequest = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TextField(
                value = textFieldValue.value,
                onValueChange = { textFieldValue.value = it },
                modifier = Modifier.fillMaxWidth(),
            )
            DialogButtonRow(
                onCancel = onSecondaryClick,
                onConfirm = {
                    onConfirm(textFieldValue.value.text)
                    show.value = false
                },
                cancelText = secondaryButtonText,
                confirmText = MLang.Component.Button.Confirm,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WarningBottomSheet(
    show: MutableState<Boolean>,
    title: String,
    messages: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = { show.value = false },
) {
    AppActionBottomSheet(show = show.value, title = title, onDismissRequest = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            messages.forEachIndexed { index, message ->
                Text(message)
                if (index < messages.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            AppCommandButton(
                title = MLang.Component.Button.Confirm,
                imageVector = MonadIcons.Check,
                onClick = {
                    onConfirm()
                    show.value = false
                },
                modifier = Modifier.fillMaxWidth(),
                tone = SemanticTone.Warning,
                highEmphasis = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.ToastDialogBridge
import com.github.nomadboxlab.monadbox.common.util.ToastDialogEvent
import com.github.nomadboxlab.monadbox.common.util.ToastMode
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Copy
import dev.oom_wg.purejoy.mlang.MLang

@Composable
fun ToastDialogHost() {
    val event by ToastDialogBridge.event.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var eventSnapshot by remember { mutableStateOf<ToastDialogEvent?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        if (event != null) {
            eventSnapshot = event
            showDialog.value = true
        }
    }

    eventSnapshot?.let { snapshot ->
        AppDialog(
            show = showDialog.value,
            title = snapshot.title,
            summary = snapshot.message,
            onDismissRequest = { showDialog.value = false },
            onDismissFinished = {
                ToastDialogBridge.dismiss(snapshot.id)
                eventSnapshot = null
                showDialog.value = false
            },
        ) {
            if (snapshot.mode == ToastMode.COPY) {
                DialogButtonRow(
                    onCancel = { showDialog.value = false },
                    onConfirm = {
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val textToCopy = snapshot.message.ifBlank { snapshot.title }
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(snapshot.title, textToCopy)
                        )
                        showDialog.value = false
                    },
                    cancelText = MLang.Component.Button.Cancel,
                    confirmText = MLang.Override.Card.Copy,
                    confirmTone = SemanticTone.Brand,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    AppCommandButton(
                        title = MLang.Component.Button.Confirm,
                        imageVector = MonadIcons.Check,
                        tone = SemanticTone.Brand,
                        highEmphasis = true,
                        onClick = { showDialog.value = false },
                    )
                }
            }
        }
    }
}

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

package com.github.nomadboxlab.monadbox.feature.editor.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.feature.editor.screen.ConfigPreviewSavePhase
import com.github.nomadboxlab.monadbox.presentation.component.AppCommandButton
import com.github.nomadboxlab.monadbox.presentation.component.AppDialog
import com.github.nomadboxlab.monadbox.presentation.component.DialogButtonRow
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Close
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val LONG_RUNNING_SAVE_DIALOG_DELAY_MS = 5_000L

@Composable
fun ConfigSaveProgressDialog(
    show: Boolean,
    phase: ConfigPreviewSavePhase?,
    isRuntimeRunning: Boolean,
    allowUndo: Boolean,
    allowDirectSave: Boolean,
    onUndo: () -> Unit,
    onDirectSave: () -> Unit,
) {
    var showLongRunningPrompt by remember { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (!show) {
            showLongRunningPrompt = false
            return@LaunchedEffect
        }

        showLongRunningPrompt = false
        delay(LONG_RUNNING_SAVE_DIALOG_DELAY_MS)
        if (show) {
            showLongRunningPrompt = true
        }
    }

    AppDialog(
        show = show,
        title = MLang.Component.Editor.Action.Save,
        summary = savePhaseSummary(phase),
        onDismissRequest = {},
        renderInRootScaffold = true,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            if (showLongRunningPrompt) {
                Text(
                    text = longRunningSummary(allowDirectSave = allowDirectSave),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    style = MiuixTheme.textStyles.body2,
                )

                when {
                    allowDirectSave -> {
                        DialogButtonRow(
                            onCancel = onUndo,
                            onConfirm = onDirectSave,
                            cancelText = MLang.Component.Editor.Action.Undo,
                            confirmText =
                                if (isRuntimeRunning) {
                                    MLang.Component.Editor.Action.SaveAndStop
                                } else {
                                    MLang.Component.Editor.Action.SaveLocally
                                },
                            confirmTone =
                                if (isRuntimeRunning) {
                                    SemanticTone.Danger
                                } else {
                                    SemanticTone.Brand
                                },
                        )
                    }

                    allowUndo -> {
                        AppCommandButton(
                            title = MLang.Component.Editor.Action.Undo,
                            imageVector = MonadIcons.Close,
                            onClick = onUndo,
                            modifier = Modifier.fillMaxWidth(),
                            tone = SemanticTone.Neutral,
                        )
                    }
                }
            }
        }
    }
}

private fun savePhaseSummary(phase: ConfigPreviewSavePhase?): String {
    return when (phase) {
        ConfigPreviewSavePhase.LocalSaving -> MLang.Component.Editor.Dialog.LocalSaving
        ConfigPreviewSavePhase.Validating -> MLang.Component.Editor.Dialog.ValidatingConfig
        ConfigPreviewSavePhase.FetchingRemoteResources ->
            MLang.Component.Editor.Dialog.FetchingRemoteResources
        ConfigPreviewSavePhase.ApplyingRuntime -> MLang.Component.Editor.Dialog.ApplyingRuntime
        null -> MLang.Component.Loading.Starting
    }
}

private fun longRunningSummary(allowDirectSave: Boolean): String {
    return if (allowDirectSave) {
        MLang.Component.Editor.Dialog.LongRunningRemoteInterruptionSummary
    } else {
        MLang.Component.Editor.Dialog.LongRunningUndoSummary
    }
}

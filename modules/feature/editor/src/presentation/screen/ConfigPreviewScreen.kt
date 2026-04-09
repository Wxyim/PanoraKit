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

package com.github.yumelira.yumebox.feature.editor.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.feature.editor.editor.CodeEditor
import com.github.yumelira.yumebox.feature.editor.editor.CodeEditorState
import com.github.yumelira.yumebox.feature.editor.format.CodeFormatter
import com.github.yumelira.yumebox.feature.editor.language.LanguageScope
import com.github.yumelira.yumebox.feature.editor.language.TextMateInitializer
import com.github.yumelira.yumebox.feature.editor.theme.EditorThemeManager
import com.github.yumelira.yumebox.presentation.component.AppDialog
import com.github.yumelira.yumebox.presentation.component.DialogButtonRow
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.ArrowLeft
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import com.github.yumelira.yumebox.presentation.theme.LocalSpacing
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.*

@Composable
fun ConfigPreviewScreen(
    navigator: DestinationsNavigator,
    title: String = MLang.Component.Editor.Dialog.ConfigPreviewTitle,
    initialContent: String = "",
    language: LanguageScope = LanguageScope.Yaml,
    isRuntimeRunning: Boolean = false,
    onSave: ConfigPreviewSaveCallback? = null,
) {
    val context = LocalContext.current
    val isReadOnly = onSave == null
    var isSaving by remember { mutableStateOf(false) }
    var savePhase by remember { mutableStateOf<ConfigPreviewSavePhase?>(null) }
    var saveDecision by remember { mutableStateOf(ConfigPreviewSaveDecision.Continue) }
    var showExitDialog by remember { mutableStateOf(false) }

    val formattedContent =
        remember(initialContent, language) {
            if (language == LanguageScope.Json) {
                CodeFormatter.format(initialContent, language) ?: initialContent
            } else {
                initialContent
            }
        }

    val editorState =
        remember(formattedContent) {
            CodeEditorState(
                initialContent = formattedContent,
                language = language,
                readOnly = isReadOnly,
            )
        }

    val editorThemeState = EditorThemeManager.rememberEditorTheme()
    val scrollBehavior = MiuixScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) { TextMateInitializer.initialize(context) }

    LaunchedEffect(editorThemeState.isDark) {
        editorState.editor?.let { TextMateInitializer.setTheme(editorThemeState.isDark) }
    }

    fun saveAndExit() {
        if (isSaving || onSave == null) return
        editorState.syncContentFromEditor()
        isSaving = true
        savePhase = ConfigPreviewSavePhase.LocalSaving
        saveDecision = ConfigPreviewSaveDecision.Continue
        coroutineScope.launch {
            try {
                onSave(
                    editorState.content,
                    { phase -> savePhase = phase },
                    { saveDecision },
                )
                    .onSuccess { outcome ->
                        when (outcome) {
                            ConfigPreviewSaveOutcome.Saved,
                            ConfigPreviewSaveOutcome.SavedLocally,
                            -> {
                                editorState.resetModified()
                                navigator.navigateUp()
                            }

                            ConfigPreviewSaveOutcome.ResumeEditing -> Unit
                        }
                    }
                    .onFailure {
                        context.toast(it.message ?: MLang.Component.Editor.Error.SaveFailed)
                    }
            } catch (e: Exception) {
                context.toast(e.message ?: MLang.Component.Editor.Error.SaveFailed)
            } finally {
                isSaving = false
                savePhase = null
                saveDecision = ConfigPreviewSaveDecision.Continue
            }
        }
    }

    fun requestExit() {
        if (isSaving) {
            return
        }
        if (isReadOnly) {
            navigator.navigateUp()
            return
        }
        editorState.syncContentFromEditor()
        if (editorState.isModified) {
            showExitDialog = true
        } else {
            navigator.navigateUp()
        }
    }

    BackHandler(enabled = !isSaving) { requestExit() }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 24.dp),
                        enabled = !isSaving,
                        onClick = { requestExit() },
                    ) {
                        Icon(Yume.ArrowLeft, contentDescription = MLang.Component.Navigation.Back)
                    }
                },
                actions =
                    if (onSave != null) {
                        {
                            IconButton(
                                modifier = Modifier.padding(end = 24.dp),
                                onClick = { saveAndExit() },
                                enabled = editorState.isModified && !isSaving,
                            ) {
                                Icon(
                                    Yume.Check,
                                    contentDescription = MLang.Component.Editor.Action.SaveAndExit,
                                )
                            }
                        }
                    } else {
                        {}
                    },
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeEditor(
                state = editorState,
                modifier = Modifier.fillMaxSize(),
                onTextChange = { editorState.syncContentFromEditor() },
            )
        }

        AppDialog(
            show = showExitDialog,
            title = MLang.Component.Editor.Dialog.DiscardTitle,
            summary = MLang.Component.Editor.Dialog.DiscardMessage,
            onDismissRequest = { showExitDialog = false },
        ) {
            DialogButtonRow(
                onCancel = { showExitDialog = false },
                onConfirm = {
                    showExitDialog = false
                    navigator.navigateUp()
                },
                cancelText = MLang.Component.Button.Cancel,
                confirmText = MLang.Component.Editor.Action.Discard,
            )
        }

        AppDialog(
            show = isSaving,
            title = MLang.Component.Editor.Action.Save,
            summary =
                when (savePhase) {
                    ConfigPreviewSavePhase.LocalSaving -> MLang.Component.Editor.Dialog.LocalSaving
                    ConfigPreviewSavePhase.Validating -> MLang.Component.Editor.Dialog.ValidatingConfig
                    ConfigPreviewSavePhase.FetchingRemoteResources ->
                        MLang.Component.Editor.Dialog.FetchingRemoteResources
                    null -> MLang.Component.Loading.Starting
                },
            onDismissRequest = {},
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.lg),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                if (savePhase == ConfigPreviewSavePhase.FetchingRemoteResources) {
                    DialogButtonRow(
                        onCancel = {
                            saveDecision = ConfigPreviewSaveDecision.ContinueEditing
                        },
                        onConfirm = {
                            saveDecision = ConfigPreviewSaveDecision.SaveLocally
                        },
                        cancelText = MLang.Component.Editor.Action.ContinueEditing,
                        confirmText =
                            if (isRuntimeRunning) {
                                MLang.Component.Editor.Action.SaveAndStop
                            } else {
                                MLang.Component.Editor.Action.SaveLocally
                            },
                    )
                }
            }
        }
    }
}

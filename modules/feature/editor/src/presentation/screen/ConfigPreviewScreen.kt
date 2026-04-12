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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.feature.editor.editor.CodeEditor
import com.github.yumelira.yumebox.feature.editor.editor.CodeEditorState
import com.github.yumelira.yumebox.feature.editor.format.CodeFormatter
import com.github.yumelira.yumebox.feature.editor.language.LanguageScope
import com.github.yumelira.yumebox.feature.editor.language.TextMateInitializer
import com.github.yumelira.yumebox.feature.editor.theme.EditorThemeManager
import com.github.yumelira.yumebox.presentation.component.AppCommandButton
import com.github.yumelira.yumebox.presentation.component.AppDialog
import com.github.yumelira.yumebox.presentation.component.DialogButtonRow
import com.github.yumelira.yumebox.presentation.component.SemanticActionDefaults
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.StatusBadge
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.ArrowLeft
import com.github.yumelira.yumebox.presentation.icon.yume.CircleCheckBig
import com.github.yumelira.yumebox.presentation.icon.yume.Save
import com.github.yumelira.yumebox.presentation.icon.yume.Sparkles
import com.github.yumelira.yumebox.presentation.theme.LocalSpacing
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
                onSave(editorState.content, { phase -> savePhase = phase }, { saveDecision })
                    .onSuccess { outcome ->
                        when (outcome) {
                            ConfigPreviewSaveOutcome.Saved,
                            ConfigPreviewSaveOutcome.SavedLocally -> {
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

    fun validateContent() {
        if (isSaving) return
        editorState.syncContentFromEditor()
        editorState.updateDiagnostics()
        if (!editorState.validate()) {
            context.toast(
                if (language == LanguageScope.Json) {
                    MLang.Component.Editor.Error.JsonSyntaxError
                } else {
                    MLang.Component.Editor.Error.Unknown
                }
            )
        }
    }

    fun formatContent() {
        if (isSaving || isReadOnly) return
        editorState.syncContentFromEditor()
        val formatted = CodeFormatter.format(editorState.content, language)
        when {
            formatted == null -> {
                context.toast(
                    if (language == LanguageScope.Json) {
                        MLang.Component.Editor.Error.JsonSyntaxError
                    } else {
                        MLang.Component.Editor.Error.Unknown
                    }
                )
            }

            formatted == editorState.content -> {
                context.toast(MLang.Override.Modifier.NotModified)
            }

            else -> {
                editorState.updateContent(formatted)
            }
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
                actions = {},
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
            val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
            val isWideLayout = !availableAdaptiveInfo.isCompactWidth

            Column(modifier = Modifier.fillMaxSize()) {
                CodeEditor(
                    state = editorState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    onTextChange = { editorState.syncContentFromEditor() },
                )

                if (onSave != null) {
                    EditorCommandBar(
                        language = language,
                        isModified = editorState.isModified,
                        isSaving = isSaving,
                        isWideLayout = isWideLayout,
                        onValidate = ::validateContent,
                        onFormat = ::formatContent,
                        onSave = ::saveAndExit,
                    )
                }
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
                        ConfigPreviewSavePhase.LocalSaving ->
                            MLang.Component.Editor.Dialog.LocalSaving
                        ConfigPreviewSavePhase.Validating ->
                            MLang.Component.Editor.Dialog.ValidatingConfig
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
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }

                    if (savePhase == ConfigPreviewSavePhase.FetchingRemoteResources) {
                        DialogButtonRow(
                            onCancel = { saveDecision = ConfigPreviewSaveDecision.ContinueEditing },
                            onConfirm = { saveDecision = ConfigPreviewSaveDecision.SaveLocally },
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
}

@Composable
private fun EditorCommandBar(
    language: LanguageScope,
    isModified: Boolean,
    isSaving: Boolean,
    isWideLayout: Boolean,
    onValidate: () -> Unit,
    onFormat: () -> Unit,
    onSave: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val surfaceShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val borderColor = MiuixTheme.colorScheme.outline.copy(alpha = 0.12f)

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .shadow(
                    elevation = 18.dp,
                    shape = surfaceShape,
                    ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.12f),
                    spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.08f),
                )
                .background(MiuixTheme.colorScheme.surface, surfaceShape)
                .border(0.8.dp, borderColor, surfaceShape)
    ) {
        if (isWideLayout) {
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = spacing.lg, vertical = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EditorStatusSummary(
                    language = language,
                    isModified = isModified,
                    modifier = Modifier.weight(1f),
                )

                Row(
                    modifier = Modifier.widthIn(max = 560.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    AppCommandButton(
                        title = MLang.Component.Editor.Action.Format,
                        imageVector = Yume.Sparkles,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        enabled = !isSaving,
                        onClick = onFormat,
                        tone = SemanticTone.Neutral,
                    )

                    AppCommandButton(
                        title = MLang.Component.Editor.Action.Check,
                        imageVector = Yume.CircleCheckBig,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        enabled = !isSaving,
                        onClick = onValidate,
                        tone = SemanticTone.Neutral,
                    )

                    AppCommandButton(
                        title = MLang.Component.Editor.Action.SaveAndExit,
                        imageVector = Yume.Save,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        enabled = isModified && !isSaving,
                        onClick = onSave,
                        tone = SemanticTone.Brand,
                        highEmphasis = true,
                    )
                }
            }
        } else {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = spacing.lg, vertical = spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                EditorStatusSummary(language = language, isModified = isModified)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    AppCommandButton(
                        title = MLang.Component.Editor.Action.Format,
                        imageVector = Yume.Sparkles,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        enabled = !isSaving,
                        onClick = onFormat,
                        tone = SemanticTone.Neutral,
                    )

                    AppCommandButton(
                        title = MLang.Component.Editor.Action.Check,
                        imageVector = Yume.CircleCheckBig,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        enabled = !isSaving,
                        onClick = onValidate,
                        tone = SemanticTone.Neutral,
                    )

                    AppCommandButton(
                        title = MLang.Component.Editor.Action.SaveAndExit,
                        imageVector = Yume.Save,
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        enabled = isModified && !isSaving,
                        onClick = onSave,
                        tone = SemanticTone.Brand,
                        highEmphasis = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorStatusSummary(
    language: LanguageScope,
    isModified: Boolean,
    modifier: Modifier = Modifier,
) {
    val tone =
        if (isModified) {
            SemanticTone.Warning
        } else {
            SemanticTone.Neutral
        }
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = isModified)

    Row(
        modifier =
            modifier
                .background(style.containerColor, RoundedCornerShape(22.dp))
                .border(0.8.dp, style.borderColor, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(10.dp).background(style.contentColor, CircleShape))

        Text(
            text =
                if (isModified) {
                    MLang.Component.Editor.Dialog.DiscardTitle
                } else {
                    MLang.Override.Modifier.NotModified
                },
            color = MiuixTheme.colorScheme.onSurface,
            style = MiuixTheme.textStyles.body1,
            modifier = Modifier.weight(1f),
        )

        StatusBadge(text = editorLanguageLabel(language), tone = SemanticTone.Info, compact = true)
    }
}

private fun editorLanguageLabel(language: LanguageScope): String {
    return when (language) {
        LanguageScope.Json -> "JSON"
        LanguageScope.Yaml -> "YAML"
        LanguageScope.Text -> "TEXT"
    }
}

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
import com.github.yumelira.yumebox.feature.editor.component.ConfigSaveProgressDialog
import com.github.yumelira.yumebox.feature.editor.editor.CodeEditor
import com.github.yumelira.yumebox.feature.editor.editor.CodeEditorState
import com.github.yumelira.yumebox.feature.editor.editor.EditorActionFeedback
import com.github.yumelira.yumebox.feature.editor.editor.EditorActionFeedbackLevel
import com.github.yumelira.yumebox.feature.editor.editor.EditorActionType
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import timber.log.Timber
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

    fun showSaveFailure(error: Throwable) {
        Timber.e(error, "Failed to save edited config preview")
        context.toast(
            error.message?.takeIf { it.isNotBlank() }
                ?: error.cause?.message?.takeIf { it.isNotBlank() }
                ?: MLang.Component.Editor.Error.SaveFailed
        )
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
                    .onFailure { error ->
                        if (error is CancellationException) {
                            throw error
                        }
                        showSaveFailure(error)
                    }
            } catch (error: Throwable) {
                if (error is CancellationException) {
                    throw error
                }
                showSaveFailure(error)
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
        context.toast(editorState.validateContent().message)
    }

    fun formatContent() {
        if (isSaving || isReadOnly) return
        editorState.syncContentFromEditor()
        context.toast(editorState.formatContent().message)
    }

    BackHandler(enabled = !isSaving) { requestExit() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val isWideLayout = !availableAdaptiveInfo.isCompactWidth

        Scaffold(
            modifier = Modifier.fillMaxSize().imePadding(),
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
                            Icon(
                                Yume.ArrowLeft,
                                contentDescription = MLang.Component.Navigation.Back,
                            )
                        }
                    },
                    actions = {},
                )
            },
            bottomBar = {
                if (onSave != null) {
                    EditorCommandBar(
                        language = language,
                        isModified = editorState.isModified,
                        feedback = editorState.lastActionFeedback,
                        isSaving = isSaving,
                        isWideLayout = isWideLayout,
                        onValidate = ::validateContent,
                        onFormat = ::formatContent,
                        onSave = ::saveAndExit,
                    )
                }
            },
        ) { paddingValues ->
            CodeEditor(
                state = editorState,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
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

        ConfigSaveProgressDialog(
            show = isSaving,
            phase = savePhase,
            isRuntimeRunning = isRuntimeRunning,
            allowUndo =
                savePhase == ConfigPreviewSavePhase.FetchingRemoteResources ||
                    savePhase == ConfigPreviewSavePhase.ApplyingRuntime,
            allowDirectSave = savePhase == ConfigPreviewSavePhase.FetchingRemoteResources,
            onUndo = { saveDecision = ConfigPreviewSaveDecision.ContinueEditing },
            onDirectSave = { saveDecision = ConfigPreviewSaveDecision.SaveLocally },
        )
    }
}

@Composable
private fun EditorCommandBar(
    language: LanguageScope,
    isModified: Boolean,
    feedback: EditorActionFeedback?,
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
                    feedback = feedback,
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
                        title = MLang.Component.Editor.Action.Save,
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
                EditorStatusSummary(
                    language = language,
                    isModified = isModified,
                    feedback = feedback,
                )

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
                        title = MLang.Component.Editor.Action.Save,
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
    feedback: EditorActionFeedback? = null,
    modifier: Modifier = Modifier,
) {
    val tone =
        if (isModified) {
            SemanticTone.Warning
        } else {
            SemanticTone.Neutral
        }
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = isModified)

    Column(
        modifier =
            modifier
                .background(style.containerColor, RoundedCornerShape(22.dp))
                .border(0.8.dp, style.borderColor, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
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

            StatusBadge(
                text = editorLanguageLabel(language),
                tone = SemanticTone.Info,
                compact = true,
            )
        }

        feedback?.let { currentFeedback ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusBadge(
                    text = currentFeedback.actionType.label(),
                    tone = currentFeedback.level.toSemanticTone(),
                    compact = true,
                )
                Text(
                    text = currentFeedback.message,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    style = MiuixTheme.textStyles.body2,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun editorLanguageLabel(language: LanguageScope): String {
    return when (language) {
        LanguageScope.Json -> "JSON"
        LanguageScope.Yaml -> "YAML"
        LanguageScope.Text -> "TEXT"
    }
}

private fun EditorActionType.label(): String {
    return when (this) {
        EditorActionType.Format -> MLang.Component.Editor.Action.Format
        EditorActionType.Validate -> MLang.Component.Editor.Action.Check
    }
}

private fun EditorActionFeedbackLevel.toSemanticTone(): SemanticTone {
    return when (this) {
        EditorActionFeedbackLevel.Success -> SemanticTone.Success
        EditorActionFeedbackLevel.Info -> SemanticTone.Info
        EditorActionFeedbackLevel.Error -> SemanticTone.Danger
    }
}

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

package com.github.nomadboxlab.monadbox.feature.editor.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.nomadboxlab.monadbox.feature.editor.diagnostic.CodeAnalysisResult
import com.github.nomadboxlab.monadbox.feature.editor.diagnostic.CodeLanguageDiagnostics
import com.github.nomadboxlab.monadbox.feature.editor.format.CodeFormatter
import com.github.nomadboxlab.monadbox.feature.editor.language.LanguageScope
import dev.oom_wg.purejoy.mlang.MLang
import dev.oom_wg.purejoy.mlang.MLangStatus
import io.github.rosemoe.sora.widget.CodeEditor

class CodeEditorState(
    initialContent: String = "",
    val language: LanguageScope = LanguageScope.Yaml,
    val readOnly: Boolean = false,
    val showLineNumbers: Boolean = true,
    val wordWrap: Boolean = false,
) {

    var editor: CodeEditor? by mutableStateOf(null)
        internal set

    var content: String by mutableStateOf(initialContent)
        private set

    var isModified: Boolean by mutableStateOf(false)
        private set

    var lastActionFeedback: EditorActionFeedback? by mutableStateOf(null)
        private set

    fun updateContent(newContent: String) {
        if (content != newContent) {
            content = newContent
            isModified = true
            lastActionFeedback = null
            editor?.setText(newContent)
        }
    }

    fun syncContentFromEditor() {
        editor?.text?.toString()?.let { editorContent ->
            if (content != editorContent) {
                content = editorContent
                isModified = true
                lastActionFeedback = null
            }
        }
    }

    fun resetModified() {
        isModified = false
    }

    fun validate(): Boolean {
        return CodeFormatter.validate(content, language)
    }

    fun updateDiagnostics(): CodeAnalysisResult {
        val analysis = CodeLanguageDiagnostics.analyze(content, language)
        applyDiagnostics(analysis)
        return analysis
    }

    fun validateContent(): EditorActionFeedback {
        val analysis = updateDiagnostics()
        val feedback =
            if (analysis.hasErrors) {
                EditorActionFeedback(
                    actionType = EditorActionType.Validate,
                    level = EditorActionFeedbackLevel.Error,
                    message =
                        analysis.primaryMessage
                            ?: MLang.Component.Editor.Error.ValidationFailed.format(
                                MLang.Component.Editor.Error.Unknown
                            ),
                )
            } else {
                EditorActionFeedback(
                    actionType = EditorActionType.Validate,
                    level = EditorActionFeedbackLevel.Success,
                    message = MLang.Component.Editor.Dialog.ValidationPassed,
                )
            }
        lastActionFeedback = feedback
        return feedback
    }

    fun formatContent(): EditorActionFeedback {
        val analysis = CodeLanguageDiagnostics.analyze(content, language)
        applyDiagnostics(analysis)

        val feedback =
            when {
                analysis.hasErrors -> {
                    EditorActionFeedback(
                        actionType = EditorActionType.Format,
                        level = EditorActionFeedbackLevel.Error,
                        message =
                            analysis.primaryMessage
                                ?: MLang.Component.Editor.Error.ValidationFailed.format(
                                    MLang.Component.Editor.Error.Unknown
                                ),
                    )
                }

                else -> {
                    val formatted = CodeFormatter.format(content, language)
                    when {
                        formatted == null -> {
                            EditorActionFeedback(
                                actionType = EditorActionType.Format,
                                level = EditorActionFeedbackLevel.Error,
                                message =
                                    MLang.Component.Editor.Error.ValidationFailed.format(
                                        MLang.Component.Editor.Error.Unknown
                                    ),
                            )
                        }

                        formatted == content -> {
                            EditorActionFeedback(
                                actionType = EditorActionType.Format,
                                level = EditorActionFeedbackLevel.Info,
                                message = MLang.Override.Modifier.NotModified,
                            )
                        }

                        else -> {
                            updateContent(formatted)
                            EditorActionFeedback(
                                actionType = EditorActionType.Format,
                                level = EditorActionFeedbackLevel.Success,
                                message = MLangStatus.Common.Applied,
                            )
                        }
                    }
                }
            }

        lastActionFeedback = feedback
        return feedback
    }

    fun clearDiagnostics() {
        editor?.diagnostics = null
    }

    private fun applyDiagnostics(analysis: CodeAnalysisResult) {
        when (language) {
            LanguageScope.Text -> editor?.diagnostics = null
            else -> editor?.diagnostics = analysis.diagnostics
        }
    }
}

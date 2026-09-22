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

package com.github.nomadboxlab.monadbox.feature.editor.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.nomadboxlab.monadbox.feature.editor.language.LanguageScope

object ConfigPreviewStore {
    var isReady: Boolean by mutableStateOf(false)
        private set

    var title: String by mutableStateOf("")
        private set

    var content: String by mutableStateOf("")
        private set

    /**
     * Working copy of the editor text. The route reads [content] as the
     * preview payload and must not recompose on every keystroke, so in-progress
     * edits are mirrored here instead. Null means "no edits yet"; the screen
     * falls back to [content].
     */
    var workingContent: String? by mutableStateOf(null)
        private set

    var isModified: Boolean by mutableStateOf(false)
        private set

    var language: LanguageScope by mutableStateOf(LanguageScope.Json)
        private set

    var runtimeRunning: Boolean by mutableStateOf(false)
        private set

    var onSave: ConfigPreviewSaveCallback? = null
        private set

    fun setup(
        title: String,
        content: String,
        language: LanguageScope = LanguageScope.Json,
        runtimeRunning: Boolean = false,
        onSave: ConfigPreviewSaveCallback? = null,
    ) {
        this.isReady = true
        this.title = title
        this.content = content
        this.workingContent = null
        this.isModified = false
        this.language = language
        this.runtimeRunning = runtimeRunning
        this.onSave = onSave
    }

    /**
     * Mirrors the working editor text and modified flag into the store so the
     * in-progress edits survive configuration changes (e.g. rotation). The
     * route does not read this field, so the editor can restore exactly what
     * was typed without serializing large configs into the saved state.
     */
    fun updateWorkingContent(content: String, isModified: Boolean) {
        this.workingContent = content
        this.isModified = isModified
    }

    fun clear() {
        isReady = false
        title = ""
        content = ""
        workingContent = null
        isModified = false
        language = LanguageScope.Json
        runtimeRunning = false
        onSave = null
    }
}

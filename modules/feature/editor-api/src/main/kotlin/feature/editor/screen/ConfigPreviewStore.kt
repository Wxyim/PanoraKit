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
    var title: String by mutableStateOf("")
        private set

    var content: String by mutableStateOf("")
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
        this.title = title
        this.content = content
        this.language = language
        this.runtimeRunning = runtimeRunning
        this.onSave = onSave
    }

    fun clear() {
        title = ""
        content = ""
        language = LanguageScope.Json
        runtimeRunning = false
        onSave = null
    }
}

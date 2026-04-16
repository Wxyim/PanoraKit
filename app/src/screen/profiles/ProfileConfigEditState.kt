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

package com.github.nomadboxlab.monadbox.screen.profiles

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverride
import com.github.nomadboxlab.monadbox.domain.model.ProductChangeState
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
import com.github.nomadboxlab.monadbox.feature.editor.screen.ConfigPreviewSaveDecision
import com.github.nomadboxlab.monadbox.feature.editor.screen.ConfigPreviewSavePhase

@Stable
class ProfileConfigEditState(val profileUuid: String) {
    var originalConfig by mutableStateOf<ConfigurationOverride?>(null)
        internal set

    var currentConfig by mutableStateOf<ConfigurationOverride?>(null)
        internal set

    var isLoading by mutableStateOf(true)
        internal set

    var loadError by mutableStateOf<String?>(null)
        internal set

    var loadStructuredError by mutableStateOf<StructuredError?>(null)
        internal set

    var isSaving by mutableStateOf(false)
        internal set

    var savePhase by mutableStateOf<ConfigPreviewSavePhase?>(null)
        internal set

    var saveDecision by mutableStateOf(ConfigPreviewSaveDecision.Continue)

    var showRuntimeStoppedDialog by mutableStateOf(false)

    var bindingSystemPresetEnabled by mutableStateOf(false)
        internal set

    var bindingSelectedOverrideIds by mutableStateOf(emptyList<String>())
        internal set

    val changeState: ProductChangeState
        get() =
            when {
                isSaving -> ProductChangeState.Applying
                loadError != null -> ProductChangeState.Invalid
                originalConfig == null -> ProductChangeState.Synced
                currentConfig != originalConfig -> ProductChangeState.Modified
                else -> ProductChangeState.Synced
            }

    val isModified: Boolean
        get() = changeState == ProductChangeState.Modified

    fun onConfigLoaded(config: ConfigurationOverride) {
        originalConfig = config
        currentConfig = config
        loadError = null
        loadStructuredError = null
    }

    fun onLoadFailed(error: String, structured: StructuredError? = null) {
        loadError = error
        loadStructuredError = structured
    }

    fun onLoadingComplete() {
        isLoading = false
    }

    fun onBindingLoaded(enabled: Boolean, overrideIds: List<String>) {
        bindingSystemPresetEnabled = enabled
        bindingSelectedOverrideIds = overrideIds
    }

    fun beginSave() {
        isSaving = true
        savePhase = ConfigPreviewSavePhase.LocalSaving
        saveDecision = ConfigPreviewSaveDecision.Continue
    }

    fun onSavePhaseChanged(phase: ConfigPreviewSavePhase) {
        savePhase = phase
    }

    fun onSaveSucceeded(updatedConfig: ConfigurationOverride) {
        originalConfig = updatedConfig
        currentConfig = updatedConfig
        endSave()
    }

    fun onSaveFailed(fallbackConfig: ConfigurationOverride) {
        currentConfig = fallbackConfig
        endSave()
    }

    fun endSave() {
        isSaving = false
        savePhase = null
        saveDecision = ConfigPreviewSaveDecision.Continue
    }

    fun updateConfig(config: ConfigurationOverride) {
        currentConfig = config
    }

    fun updateBindingState(enabled: Boolean, overrideIds: List<String>) {
        bindingSystemPresetEnabled = enabled
        bindingSelectedOverrideIds = overrideIds
    }
}

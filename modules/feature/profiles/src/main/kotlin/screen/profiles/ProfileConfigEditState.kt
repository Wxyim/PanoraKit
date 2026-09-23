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

package com.github.nomadboxlab.monadbox.feature.profiles

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

    var bindingSystemPresetEnabled by mutableStateOf(false)
        internal set

    var bindingSelectedOverrideIds by mutableStateOf(emptyList<String>())
        internal set

    var bindingBaselineSystemPresetEnabled by mutableStateOf(false)
        internal set

    var bindingBaselineOverrideIds by mutableStateOf(emptyList<String>())
        internal set

    /** True when the binding selection differs from what was loaded. */
    val bindingChanged: Boolean
        get() =
            bindingBaselineSystemPresetEnabled != bindingSystemPresetEnabled ||
                bindingBaselineOverrideIds != bindingSelectedOverrideIds

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

    fun beginLoad() {
        isLoading = true
        loadError = null
        loadStructuredError = null
    }

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
        bindingBaselineSystemPresetEnabled = enabled
        bindingBaselineOverrideIds = overrideIds
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

    fun markBindingSaved() {
        bindingBaselineSystemPresetEnabled = bindingSystemPresetEnabled
        bindingBaselineOverrideIds = bindingSelectedOverrideIds
    }
}

/**
 * Holds the in-memory edit session per profile across sub-editor navigation.
 *
 * The structured editor defers persistence until the editor is exited, so edits must survive
 * navigating into nested editors (rules, string lists, objects, ...). Those are separate
 * destinations that take the editor out of composition; a plain `remember` state would be
 * discarded and the sub-editor callback would update an orphaned instance, silently losing the
 * edit.
 */
object ProfileConfigEditSessionHolder {
    private val sessions = mutableMapOf<String, ProfileConfigEditState>()

    fun forProfile(profileUuid: String): ProfileConfigEditState {
        // A session stuck in isSaving can only be left behind by an abnormal exit (e.g. the save
        // coroutine was cancelled before clear() ran). Reuse it and the editor would restore a
        // stale in-memory snapshot or stay stuck in the saving state, so drop it and start fresh.
        if (sessions[profileUuid]?.isSaving == true) {
            sessions.remove(profileUuid)
        }
        return sessions.getOrPut(profileUuid) { ProfileConfigEditState(profileUuid) }
    }

    /**
     * Removes the session only if it is still [session]. A save coroutine that was orphaned by an
     * abnormal exit must not wipe a newer session that was created for the same profile.
     */
    fun clear(profileUuid: String, session: ProfileConfigEditState) {
        sessions.remove(profileUuid, session)
    }
}

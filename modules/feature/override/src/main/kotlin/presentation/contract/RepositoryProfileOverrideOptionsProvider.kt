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
 */

package com.github.nomadboxlab.monadbox.presentation.contract

import com.github.nomadboxlab.monadbox.data.repository.OverrideConfigProvider
import com.github.nomadboxlab.monadbox.domain.model.OverrideConfig
import com.github.nomadboxlab.monadbox.feature.override.api.ProfileOverrideOption
import com.github.nomadboxlab.monadbox.feature.override.api.ProfileOverrideOptionsProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RepositoryProfileOverrideOptionsProvider(
    private val overrideConfigProvider: OverrideConfigProvider
) : ProfileOverrideOptionsProvider {
    private val mutableSystemPreset = MutableStateFlow<ProfileOverrideOption?>(null)
    private val mutableUserOptions = MutableStateFlow<List<ProfileOverrideOption>>(emptyList())

    override val systemPreset: StateFlow<ProfileOverrideOption?> = mutableSystemPreset.asStateFlow()
    override val userOptions: StateFlow<List<ProfileOverrideOption>> =
        mutableUserOptions.asStateFlow()

    override suspend fun refreshOptions() {
        mutableSystemPreset.value =
            overrideConfigProvider.getSystemPresets().firstOrNull()?.toProfileOverrideOption()
        mutableUserOptions.value =
            overrideConfigProvider.getUserConfigs().map(OverrideConfig::toProfileOverrideOption)
    }
}

private fun OverrideConfig.toProfileOverrideOption(): ProfileOverrideOption =
    ProfileOverrideOption(id = id, name = name, description = description, isSystem = isSystem)

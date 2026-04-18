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

package com.github.nomadboxlab.monadbox.feature.home.usecase

import com.github.nomadboxlab.monadbox.feature.home.HomeProfilesState
import com.github.nomadboxlab.monadbox.runtime.client.ProfilesRepository
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RefreshHomeEntryDataUseCase(
    private val profilesRepository: ProfilesRepository,
    private val proxyFacade: ProxyFacade,
) {
    suspend fun refreshProfiles(): HomeProfilesState {
        val allProfiles = profilesRepository.queryAllProfiles()
        val active = profilesRepository.queryActiveProfile(ensureDefault = true)
        return HomeProfilesState(
            profiles = allProfiles,
            recommendedProfile = active,
            profilesLoaded = true,
        )
    }

    suspend operator fun invoke(): HomeProfilesState {
        val profilesState = refreshProfiles()
        refreshRuntimePreview()
        return profilesState
    }

    suspend fun refreshRuntimePreview() {
        withContext(Dispatchers.IO) {
            proxyFacade.refreshCurrentProfile()
            proxyFacade.refreshProxyGroups()
        }
    }
}

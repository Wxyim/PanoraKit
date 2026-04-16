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

package com.github.nomadboxlab.monadbox.runtime.client

import com.github.nomadboxlab.monadbox.service.remote.IFetchObserver
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import java.util.UUID

interface ProfilesProvider {

    suspend fun createProfile(type: Profile.Type, name: String, source: String = ""): UUID

    suspend fun cloneProfile(uuid: UUID): UUID

    suspend fun deleteProfile(uuid: UUID)

    suspend fun queryAllProfiles(): List<Profile>

    suspend fun queryActiveProfile(ensureDefault: Boolean = false): Profile?

    suspend fun queryProfileByUUID(uuid: UUID): Profile?

    suspend fun setActiveProfile(uuid: UUID)

    suspend fun clearActiveProfile(profile: Profile)

    suspend fun reorderProfiles(uuids: List<UUID>)

    suspend fun updateProfile(uuid: UUID, callback: IFetchObserver? = null)

    suspend fun patchProfile(uuid: UUID, name: String, source: String, interval: Long)
}

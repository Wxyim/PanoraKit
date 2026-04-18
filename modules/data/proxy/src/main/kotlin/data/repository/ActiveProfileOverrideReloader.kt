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

package com.github.nomadboxlab.monadbox.data.repository

import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeActiveProfileReader
import timber.log.Timber

class ActiveProfileOverrideReloader(
    private val activeProfileReader: RuntimeActiveProfileReader,
    private val bindingProvider: ProfileBindingProvider,
    private val overrideService: OverrideService,
) {
    suspend fun reapplyActiveProfileOverride(): Boolean {
        val activeProfile = activeProfileReader.queryActiveRuntimeProfile() ?: return true
        val applied = overrideService.applyOverride(activeProfile.id)
        if (!applied) {
            Timber.e("Failed to reapply active profile override: profile=%s", activeProfile.id)
        }
        return applied
    }

    suspend fun reapplyActiveProfileIfUsingOverride(overrideId: String): Boolean {
        val activeProfile = activeProfileReader.queryActiveRuntimeProfile() ?: return true
        val binding = bindingProvider.getBinding(activeProfile.id) ?: return true
        if (!binding.overrideIds.contains(overrideId)) {
            return true
        }

        val applied = overrideService.applyOverride(activeProfile.id)
        if (!applied) {
            Timber.e(
                "Failed to reapply active profile override after config change: profile=%s override=%s",
                activeProfile.id,
                overrideId,
            )
        }
        return applied
    }

    suspend fun reapplyIfActiveProfile(profileId: String): Boolean {
        val activeProfile = activeProfileReader.queryActiveRuntimeProfile() ?: return true
        if (activeProfile.id != profileId) {
            return true
        }

        val applied = overrideService.applyOverride(profileId)
        if (!applied) {
            Timber.e(
                "Failed to reapply override after binding change for active profile: profile=%s",
                profileId,
            )
        }
        return applied
    }

    suspend fun isActiveProfileUsingOverride(overrideId: String): Boolean {
        val activeProfile = activeProfileReader.queryActiveRuntimeProfile() ?: return false
        val binding = bindingProvider.getBinding(activeProfile.id) ?: return false
        return binding.overrideIds.contains(overrideId)
    }
}

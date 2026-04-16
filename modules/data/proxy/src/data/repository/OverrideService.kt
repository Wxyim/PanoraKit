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

import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationCoordinator
import timber.log.Timber

class OverrideService(
    private val resolver: OverrideResolver,
    private val mutationCoordinator: RuntimeMutationCoordinator,
) {

    suspend fun applyOverride(profileId: String): Boolean {
        return try {
            val overrideIds = resolver.resolveIds(profileId)
            val resolvedConfigs = resolver.resolveConfigs(overrideIds)
            val missingOverrideCount = overrideIds.size - resolvedConfigs.size

            Timber.i(
                "Apply override chain: profile=%s ids=%s resolved=%d missing=%d",
                profileId,
                overrideIds.joinToString(","),
                resolvedConfigs.size,
                missingOverrideCount,
            )

            if (missingOverrideCount > 0) {
                Timber.w(
                    "Override chain contains missing configs: profile=%s ids=%s",
                    profileId,
                    overrideIds.joinToString(","),
                )
                return false
            }

            mutationCoordinator.notifyOverrideBindingsChanged()

            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to apply override for profile: %s", profileId)
            false
        }
    }
}

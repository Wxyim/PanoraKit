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

import android.content.Context
import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverride
import com.github.nomadboxlab.monadbox.domain.model.OverrideConfig
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeActiveProfileReader
import dev.oom_wg.purejoy.mlang.MLang

class OverrideRepository(
    @Suppress("UNUSED_PARAMETER") context: Context,
    private val configRepository: OverrideConfigRepository,
    private val activeProfileReader: RuntimeActiveProfileReader,
) : OverrideProvider {
    override suspend fun updateProfile(
        transform: (ConfigurationOverride) -> ConfigurationOverride
    ): Result<ConfigurationOverride> = updateInternal(transform)

    private suspend fun loadInternal(): Result<ConfigurationOverride> = runCatching {
        val activeProfile = queryActiveProfile() ?: return@runCatching ConfigurationOverride()
        configRepository.getById(runtimeOverrideId(activeProfile.id))?.config
            ?: ConfigurationOverride()
    }

    private suspend fun saveInternal(override: ConfigurationOverride): Result<Unit> = runCatching {
        if (override == ConfigurationOverride()) {
            clearInternal().getOrThrow()
            return@runCatching
        }
        val activeProfile = requireActiveProfile()
        val configId = runtimeOverrideId(activeProfile.id)
        val existing = configRepository.getById(configId)
        configRepository.save(
            OverrideConfig(
                id = configId,
                name = INTERNAL_RUNTIME_NAME,
                description = "internal runtime override for ${activeProfile.id}",
                config = override,
                isSystem = false,
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    private suspend fun clearInternal(): Result<Unit> = runCatching {
        val activeProfile = queryActiveProfile() ?: return@runCatching
        configRepository.delete(runtimeOverrideId(activeProfile.id))
    }

    private suspend fun updateInternal(
        transform: (ConfigurationOverride) -> ConfigurationOverride
    ): Result<ConfigurationOverride> {
        val current =
            loadInternal().getOrElse {
                return Result.failure(it)
            }
        val updated = transform(current)
        val saveResult = saveInternal(updated)
        if (saveResult.isFailure) {
            return Result.failure(
                saveResult.exceptionOrNull()
                    ?: IllegalStateException(MLang.Override.Save.RuntimeSaveFailed)
            )
        }
        return Result.success(updated)
    }

    private suspend fun requireActiveProfile():
        com.github.nomadboxlab.monadbox.runtime.contract.RuntimeProfileRef {
        return queryActiveProfile() ?: error("No active profile selected")
    }

    private suspend fun queryActiveProfile():
        com.github.nomadboxlab.monadbox.runtime.contract.RuntimeProfileRef? {
        return activeProfileReader.queryActiveRuntimeProfile()
    }

    private fun runtimeOverrideId(profileId: String): String {
        return "${OverrideConfigRepository.INTERNAL_RUNTIME_PREFIX}-profile-$profileId"
    }

    private companion object {
        private val INTERNAL_RUNTIME_NAME: String
            get() = MLang.Override.Save.RuntimeOverrideName
    }
}

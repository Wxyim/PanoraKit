/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.data.repository

import com.github.yumelira.yumebox.domain.model.MetadataIndex
import com.github.yumelira.yumebox.domain.model.OverrideMetadata

internal fun MetadataIndex.sanitizePersistedOverrideState(
    hasPersistedUserOverride: (String) -> Boolean
): MetadataIndex {
    val sanitizedConfigs =
        configs.filter { (id, metadata) ->
            metadata.isSystem ||
                OverrideConfigRepository.isInternalRuntimeConfig(id) ||
                hasPersistedUserOverride(id)
        }

    val validUserOverrideIds =
        sanitizedConfigs.keys
            .filterNot { it.startsWith(OverrideMetadata.SYSTEM_PREFIX) }
            .filterNot(OverrideConfigRepository::isInternalRuntimeConfig)
            .toSet()

    val sanitizedProfileChains =
        profileChains.mapValues { (_, binding) ->
            binding.copy(overrideIds = binding.overrideIds.filter(validUserOverrideIds::contains))
        }

    return copy(configs = sanitizedConfigs, profileChains = sanitizedProfileChains)
}

internal fun MetadataIndex.removeUserOverrideReferences(overrideId: String): MetadataIndex {
    return copy(
        profileChains =
            profileChains.mapValues { (_, binding) ->
                binding.copy(overrideIds = binding.overrideIds - overrideId)
            }
    )
}
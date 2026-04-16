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

package com.github.nomadboxlab.monadbox.data.repository

import com.github.nomadboxlab.monadbox.domain.model.MetadataIndex
import com.github.nomadboxlab.monadbox.domain.model.OverrideMetadata
import com.github.nomadboxlab.monadbox.domain.model.ProfileBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OverrideMetadataSanitizerTest {
    @Test
    fun sanitizePersistedOverrideState_prunesMissingUserOverridesFromBindings() {
        val index =
            MetadataIndex(
                configs =
                    mapOf(
                        existingMetadata("cfg-existing"),
                        existingMetadata("cfg-missing"),
                        existingMetadata("__runtime__-profile-demo"),
                    ),
                profileChains =
                    mapOf(
                        "profile-1" to
                            ProfileBinding(
                                profileId = "profile-1",
                                overrideIds =
                                    listOf(
                                        "cfg-existing",
                                        "cfg-missing",
                                        OverrideMetadata.SYSTEM_PRESET_ID,
                                    ),
                            )
                    ),
            )

        val sanitized =
            index.sanitizePersistedOverrideState { overrideId -> overrideId == "cfg-existing" }

        assertEquals(setOf("cfg-existing", "__runtime__-profile-demo"), sanitized.configs.keys)
        assertEquals(
            listOf("cfg-existing"),
            sanitized.profileChains.getValue("profile-1").overrideIds,
        )
    }

    @Test
    fun removeUserOverrideReferences_dropsDeletedIdFromEveryProfileChain() {
        val index =
            MetadataIndex(
                profileChains =
                    mapOf(
                        "profile-1" to
                            ProfileBinding(
                                profileId = "profile-1",
                                overrideIds = listOf("cfg-a", "cfg-delete", "cfg-b"),
                            ),
                        "profile-2" to
                            ProfileBinding(
                                profileId = "profile-2",
                                overrideIds = listOf("cfg-delete"),
                                enabled = true,
                            ),
                    )
            )

        val sanitized = index.removeUserOverrideReferences("cfg-delete")

        assertEquals(
            listOf("cfg-a", "cfg-b"),
            sanitized.profileChains.getValue("profile-1").overrideIds,
        )
        assertTrue(sanitized.profileChains.getValue("profile-2").overrideIds.isEmpty())
        assertFalse(sanitized.profileChains.getValue("profile-2").enabled.not())
    }

    private fun existingMetadata(id: String): Pair<String, OverrideMetadata> {
        return id to OverrideMetadata(id = id, name = id, createdAt = 1L, updatedAt = 1L)
    }
}

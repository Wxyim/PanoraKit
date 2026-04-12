package com.github.yumelira.yumebox.data.repository

import com.github.yumelira.yumebox.domain.model.MetadataIndex
import com.github.yumelira.yumebox.domain.model.OverrideMetadata
import com.github.yumelira.yumebox.domain.model.ProfileBinding
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

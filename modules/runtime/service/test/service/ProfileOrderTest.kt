package com.github.yumelira.yumebox.service

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileOrderTest {
    @Test
    fun normalizeProfileOrder_keepsSavedUserOrderAndAppendsMissingProfiles() {
        val profileA = UUID.fromString("00000000-0000-0000-0000-00000000000a")
        val profileB = UUID.fromString("00000000-0000-0000-0000-00000000000b")
        val profileC = UUID.fromString("00000000-0000-0000-0000-00000000000c")
        val profileD = UUID.fromString("00000000-0000-0000-0000-00000000000d")
        val staleProfile = UUID.fromString("00000000-0000-0000-0000-00000000000e")

        val normalized =
            normalizeProfileOrder(
                existing = listOf(profileA, profileB, profileC, profileD),
                savedOrder = listOf(profileC, staleProfile, profileA, profileC),
            )

        assertEquals(listOf(profileC, profileA, profileB, profileD), normalized)
    }

    @Test
    fun normalizeProfileOrder_preservesImportedInsertionOrderForNewProfiles() {
        val profileA = UUID.fromString("10000000-0000-0000-0000-00000000000a")
        val profileB = UUID.fromString("10000000-0000-0000-0000-00000000000b")
        val profileC = UUID.fromString("10000000-0000-0000-0000-00000000000c")

        val normalized =
            normalizeProfileOrder(
                existing = listOf(profileB, profileA, profileC),
                savedOrder = listOf(profileA),
            )

        assertEquals(listOf(profileA, profileB, profileC), normalized)
    }
}

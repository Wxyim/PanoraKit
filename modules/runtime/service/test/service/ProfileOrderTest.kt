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

package com.github.nomadboxlab.monadbox.service

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

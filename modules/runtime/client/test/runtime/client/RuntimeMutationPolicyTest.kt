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

package com.github.nomadboxlab.monadbox.runtime.client

import java.util.UUID
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RuntimeMutationPolicyTest {
    @Test
    fun doesNotScheduleWhenRootTunInactive() {
        assertFalse(
            shouldScheduleRootTunReloadForActiveProfile(
                isRootTunActive = false,
                activeProfileId = UUID.randomUUID(),
                changedProfileId = UUID.randomUUID(),
            )
        )
    }

    @Test
    fun doesNotScheduleWhenActiveProfileMissing() {
        assertFalse(
            shouldScheduleRootTunReloadForActiveProfile(
                isRootTunActive = true,
                activeProfileId = null,
                changedProfileId = UUID.randomUUID(),
            )
        )
    }

    @Test
    fun schedulesOnlyForActiveProfileChanges() {
        val active = UUID.randomUUID()
        val other = UUID.randomUUID()

        assertFalse(
            shouldScheduleRootTunReloadForActiveProfile(
                isRootTunActive = true,
                activeProfileId = active,
                changedProfileId = other,
            )
        )

        assertTrue(
            shouldScheduleRootTunReloadForActiveProfile(
                isRootTunActive = true,
                activeProfileId = active,
                changedProfileId = active,
            )
        )
    }
}

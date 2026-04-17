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

package com.github.nomadboxlab.monadbox.startup

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppStartupPoliciesTest {
    @Test
    fun shouldInitializeDeferredStartup_requiresCompletedSetupAndFreshState() {
        assertTrue(
            shouldInitializeDeferredStartup(
                initialSetupCompleted = true,
                alreadyInitialized = false,
            )
        )
        assertFalse(
            shouldInitializeDeferredStartup(
                initialSetupCompleted = false,
                alreadyInitialized = false,
            )
        )
        assertFalse(
            shouldInitializeDeferredStartup(initialSetupCompleted = true, alreadyInitialized = true)
        )
    }

    @Test
    fun isStaleTempDownloadCandidate_matchesCoordinatorRules() {
        val now = 5_000L
        assertTrue(
            isStaleTempDownloadCandidate(
                fileName = "temp_profile.yaml",
                isRegularFile = true,
                lastModifiedAt = 0L,
                now = now,
                staleAfterMs = 1_000L,
            )
        )
        assertFalse(
            isStaleTempDownloadCandidate(
                fileName = "temp_profile.txt",
                isRegularFile = true,
                lastModifiedAt = 0L,
                now = now,
                staleAfterMs = 1_000L,
            )
        )
        assertFalse(
            isStaleTempDownloadCandidate(
                fileName = "profile.yaml",
                isRegularFile = true,
                lastModifiedAt = 0L,
                now = now,
                staleAfterMs = 1_000L,
            )
        )
    }
}

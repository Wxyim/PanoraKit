package com.github.yumelira.yumebox.startup

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

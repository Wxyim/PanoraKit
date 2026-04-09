package com.github.yumelira.yumebox.runtime.client

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

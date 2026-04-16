package com.github.nomadboxlab.monadbox.screen.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessControlPoliciesTest {
    @Test
    fun normalizeManualPackageName_acceptsWellFormedPackage() {
        assertEquals(
            "com.example.app",
            AccessControlSelection.normalizeManualPackageName(" com.example.app "),
        )
    }

    @Test
    fun normalizeManualPackageName_rejectsInvalidPackage() {
        assertTrue(AccessControlSelection.normalizeManualPackageName("not a package").isEmpty())
        assertTrue(AccessControlSelection.normalizeManualPackageName("example").isEmpty())
    }

    @Test
    fun updateSelection_keepsManualPackageInSelectedSet() {
        val state =
            AccessControlUiState(
                isLoading = false,
                apps = emptyList(),
                selectedPackages = emptySet(),
                canBrowseApps = false,
            )

        val updated = AccessControlSelection.updateSelection(state, setOf("com.example.manual"))

        assertEquals(setOf("com.example.manual"), updated.selectedPackages)
    }
}

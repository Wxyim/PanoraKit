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

package com.github.nomadboxlab.monadbox.feature.settings

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

    @Test
    fun filterApps_keepsSelectedFirstAndSortsRemainingByLabel() {
        val apps =
            listOf(
                AccessControlAppInfo("com.z.last", "Zulu", false, false, false),
                AccessControlAppInfo("com.a.selected", "Alpha", false, false, true),
                AccessControlAppInfo("com.b.selected", "Beta", false, false, true),
            )

        val result =
            AccessControlFilter.filterApps(
                apps = apps,
                query = "",
                showSystemApps = false,
                sortMode = AccessControlSortMode.LABEL,
                selectedFirst = true,
            )

        assertEquals(
            listOf("com.a.selected", "com.b.selected", "com.z.last"),
            result.map { it.packageName },
        )
    }

    @Test
    fun visiblePackages_doesNotDependOnSortSettings() {
        val apps =
            listOf(
                AccessControlAppInfo("com.example.b", "Beta", false, false, false),
                AccessControlAppInfo("com.example.a", "Alpha", false, false, false),
            )

        assertEquals(
            setOf("com.example.a", "com.example.b"),
            AccessControlFilter.visiblePackages(
                apps = apps,
                query = "example",
                showSystemApps = false,
            ),
        )
    }
}

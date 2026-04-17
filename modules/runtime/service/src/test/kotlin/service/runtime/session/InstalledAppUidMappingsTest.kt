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

package com.github.nomadboxlab.monadbox.service.runtime.session

import org.junit.Assert.assertEquals
import org.junit.Test

class InstalledAppUidMappingsTest {
    @Test
    fun `fromEntries keeps package name for single-package uid`() {
        val mappings =
            InstalledAppUidMappings.fromEntries(
                listOf(InstalledAppUidEntry(packageName = "com.example.app", uid = 12345))
            )

        assertEquals(listOf(12345 to "com.example.app"), mappings)
    }

    @Test
    fun `fromEntries prefers shared user id when multiple packages share uid`() {
        val mappings =
            InstalledAppUidMappings.fromEntries(
                listOf(
                    InstalledAppUidEntry(
                        packageName = "com.example.one",
                        uid = 12345,
                        sharedUserId = "shared.example",
                    ),
                    InstalledAppUidEntry(
                        packageName = "com.example.two",
                        uid = 12345,
                        sharedUserId = "shared.example",
                    ),
                )
            )

        assertEquals(listOf(12345 to "shared.example"), mappings)
    }

    @Test
    fun `fromRootPackageUidMap picks deterministic package per uid`() {
        val mappings =
            InstalledAppUidMappings.fromRootPackageUidMap(
                linkedMapOf(
                    "com.example.z" to 22345,
                    "com.example.a" to 22345,
                    "com.example.other" to 22346,
                )
            )

        assertEquals(listOf(22345 to "com.example.a", 22346 to "com.example.other"), mappings)
    }
}

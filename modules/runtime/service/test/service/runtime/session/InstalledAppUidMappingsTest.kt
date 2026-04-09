package com.github.yumelira.yumebox.service.runtime.session

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

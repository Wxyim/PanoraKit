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

import com.github.nomadboxlab.monadbox.service.runtime.util.directoryLastModified
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileDirectoryLastModifiedTest {
    @Test
    fun derivedRuntimeArtifactsDoNotAdvanceProfileTimestamp() {
        val dir = createTempDir()
        try {
            val config = File(dir, "config.yaml")
            config.writeText("proxies: []")
            assertTrue(config.setLastModified(1000L))

            val runtimeYaml = File(dir, "runtime.yaml")
            runtimeYaml.writeText("derived")
            assertTrue(runtimeYaml.setLastModified(2000L))

            val fingerprint = File(dir, "runtime.yaml.fingerprint")
            fingerprint.writeText("fingerprint")
            assertTrue(fingerprint.setLastModified(3000L))

            assertEquals(1000L, dir.directoryLastModified)
        } finally {
            dir.deleteRecursively()
        }
    }

    @Test
    fun realConfigFilesStillAdvanceProfileTimestamp() {
        val dir = createTempDir()
        try {
            val config = File(dir, "config.yaml")
            config.writeText("proxies: []")
            assertTrue(config.setLastModified(1000L))

            val provider = File(dir, "providers/provider.yaml")
            provider.parentFile.mkdirs()
            provider.writeText("proxies: []")
            assertTrue(provider.setLastModified(4000L))

            assertEquals(4000L, dir.directoryLastModified)
        } finally {
            dir.deleteRecursively()
        }
    }
}

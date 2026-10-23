/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * MonadBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.core.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectionSnapshotTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun systemFlagIsDecodedFromTheNativeSnapshot() {
        val snapshot =
            json.decodeFromString<ConnectionSnapshot>(
                """{"connections":[{"id":"system","metadata":{},"system":true}]}"""
            )

        assertTrue(snapshot.connections.orEmpty().single().system)
    }

    @Test
    fun absentSystemFlagDefaultsToFalse() {
        val snapshot =
            json.decodeFromString<ConnectionSnapshot>(
                """{"connections":[{"id":"app","metadata":{"uid":10123}}]}"""
            )

        assertFalse(snapshot.connections.orEmpty().single().system)
    }
}

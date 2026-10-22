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

package com.github.nomadboxlab.monadbox.core.domain

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConnectionHistoryManagerTest {

    @Before
    fun setUp() {
        ConnectionHistoryManager.clear()
    }

    @After
    fun tearDown() {
        ConnectionHistoryManager.clear()
    }

    @Test
    fun lateUid_upgradesAnAlreadyClosedConnection() {
        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "late-uid", closed = true, uid = null, closedAt = 1_000L))
        )
        val revisionBeforeUpgrade = ConnectionHistoryManager.closedConnectionsRevision()

        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "late-uid", closed = true, uid = 10_123, closedAt = 1_000L))
        )

        val row = ConnectionHistoryManager.getClosedConnections().single()
        assertEquals(10_123, row.metadata.getValue("uid").jsonPrimitive.int)
        assertTrue(ConnectionHistoryManager.closedConnectionsRevision() > revisionBeforeUpgrade)
    }

    @Test
    fun diffFallbackCanBeUpgradedByTheLaterCoreClosedSnapshot() {
        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "fallback", closed = false, uid = null))
        )
        // The core observed the close after the app's poll, so the app's diff records the stale
        // active copy first.
        ConnectionHistoryManager.updateConnections(emptyList())
        assertFalse(
            ConnectionHistoryManager.getClosedConnections().single().metadata.containsKey("uid")
        )

        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "fallback", closed = true, uid = 10_456, closedAt = 2_000L))
        )

        val row = ConnectionHistoryManager.getClosedConnections().single()
        assertEquals(10_456, row.metadata.getValue("uid").jsonPrimitive.int)
        assertEquals(1, ConnectionHistoryManager.closedConnectionCount())
    }

    @Test
    fun staleDuplicate_doesNotDowngradeAnAttributedRow() {
        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "no-downgrade", closed = false, uid = null))
        )

        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "no-downgrade", closed = true, uid = 10_789, closedAt = 3_000L))
        )
        val revisionBeforeStaleCopy = ConnectionHistoryManager.closedConnectionsRevision()

        // A later poll still cannot overwrite the enriched core copy with the old active copy.
        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "no-downgrade", closed = true, uid = null, closedAt = 3_000L))
        )

        val row = ConnectionHistoryManager.getClosedConnections().single()
        assertEquals(10_789, row.metadata.getValue("uid").jsonPrimitive.int)
        assertEquals(revisionBeforeStaleCopy, ConnectionHistoryManager.closedConnectionsRevision())
    }

    @Test
    fun lateSystemResolution_upgradesAnUnknownRowAndKeepsTheFlag() {
        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "system-late", closed = true, uid = null, closedAt = 4_000L))
        )
        val revisionBeforeUpgrade = ConnectionHistoryManager.closedConnectionsRevision()

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "system-late",
                    closed = true,
                    uid = 0,
                    closedAt = 4_000L,
                    system = true,
                )
            )
        )

        val row = ConnectionHistoryManager.getClosedConnections().single()
        assertTrue(row.system)
        assertTrue(ConnectionHistoryManager.closedConnectionsRevision() > revisionBeforeUpgrade)

        // A stale copy captured before the system lookup must not erase the resolved identity.
        ConnectionHistoryManager.updateConnections(
            listOf(connection(id = "system-late", closed = true, uid = null, closedAt = 4_000L))
        )
        assertTrue(ConnectionHistoryManager.getClosedConnections().single().system)
    }

    private fun connection(
        id: String,
        closed: Boolean,
        uid: Int?,
        closedAt: Long = 0L,
        system: Boolean = false,
    ): ConnectionInfo =
        ConnectionInfo(
            id = id,
            metadata =
                buildJsonObject {
                    put("network", "tcp")
                    put("sourceIP", "192.0.2.10")
                    put("destinationIP", "198.51.100.20")
                    uid?.let { put("uid", it) }
                },
            closed = closed,
            closedAt = closedAt,
            system = system,
        )
}

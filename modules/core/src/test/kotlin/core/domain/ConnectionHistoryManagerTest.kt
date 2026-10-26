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
import com.github.nomadboxlab.monadbox.core.model.TunNetworkDefaults
import java.time.Instant
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

    @Test
    fun startupLegacyUdp_isRetiredWhenTheSameAppReconnectsThroughTun() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        val legacy =
            connection(
                id = "legacy-udp",
                closed = true,
                uid = 10_123,
                closedAt = sessionStart + 1_000L,
                type = "Tun",
                network = "udp",
                sourceIp = "10.2.196.45",
                destinationIp = "122.193.7.205",
                destinationPort = 23_000,
                start = timestamp(sessionStart + 500L),
            )
        ConnectionHistoryManager.updateConnections(listOf(legacy))
        assertEquals(1, ConnectionHistoryManager.closedConnectionCount())

        val replacement =
            connection(
                id = "replacement-udp",
                closed = false,
                uid = 10_123,
                type = "Tun",
                network = "udp",
                sourceIp = "172.19.0.1",
                destinationIp = "122.193.7.205",
                destinationPort = 23_000,
                start = timestamp(sessionStart + 2_000L),
            )
        ConnectionHistoryManager.updateConnections(listOf(replacement))

        // The live replacement is enough to retire the startup row; no need to wait for it to
        // close before clearing the misleading physical-source entry.
        assertTrue(ConnectionHistoryManager.getClosedConnections().isEmpty())

        val closedReplacement = replacement.copy(closed = true, closedAt = sessionStart + 3_000L)
        ConnectionHistoryManager.updateConnections(listOf(closedReplacement))
        val rows = ConnectionHistoryManager.getClosedConnections()
        assertEquals(listOf("replacement-udp"), rows.map { it.id })
        assertEquals(
            "172.19.0.1",
            rows.single().metadata.getValue("sourceIP").jsonPrimitive.content,
        )
    }

    @Test
    fun startupLegacyUdp_withoutATunReplacementStaysVisible() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "legacy-only",
                    closed = true,
                    uid = 10_123,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "10.2.196.45",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        val rows = ConnectionHistoryManager.getClosedConnections()
        assertEquals(listOf("legacy-only"), rows.map { it.id })
        assertEquals(
            "10.2.196.45",
            rows.single().metadata.getValue("sourceIP").jsonPrimitive.content,
        )
    }

    @Test
    fun startupLegacy_withoutResolvedUid_canStillBeRetiredByDestination() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "legacy-unknown-owner",
                    closed = true,
                    uid = null,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "10.2.196.45",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "tun-replacement",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "172.19.0.1",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 2_000L),
                )
            )
        )

        assertTrue(ConnectionHistoryManager.getClosedConnections().isEmpty())
    }

    @Test
    fun startupLegacy_withoutResolvedUid_matchesTheSameDestinationOnANewPort() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "legacy-unknown-port",
                    closed = true,
                    uid = null,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "10.2.196.45",
                    destinationIp = "122.193.7.205",
                    destinationPort = 22_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "tun-replacement-new-port",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "172.19.0.1",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 2_000L),
                )
            )
        )

        assertTrue(ConnectionHistoryManager.getClosedConnections().isEmpty())
    }

    @Test
    fun startupLegacy_withResolvedUid_matchesTheSameDestinationOnANewPort() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "legacy-known-port",
                    closed = true,
                    uid = 10_123,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "10.2.196.45",
                    destinationIp = "122.193.7.205",
                    destinationPort = 22_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "known-owner-new-port",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "172.19.0.1",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 2_000L),
                )
            )
        )

        assertTrue(ConnectionHistoryManager.getClosedConnections().isEmpty())
    }

    @Test
    fun startupLegacyOutsideTheStartupWindow_isNotRetired() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "late-physical",
                    closed = true,
                    uid = 10_123,
                    closedAt = sessionStart + 21_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "10.2.196.45",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 20_000L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "later-tun",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "172.19.0.1",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 22_000L),
                )
            )
        )

        assertEquals(
            listOf("late-physical"),
            ConnectionHistoryManager.getClosedConnections().map { it.id },
        )
    }

    @Test
    fun startupPhysicalSourceFromAnotherDevice_isNotRetired() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "hotspot-device",
                    closed = true,
                    uid = 10_123,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "192.168.43.50",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "local-tun",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "172.19.0.1",
                    destinationIp = "122.193.7.205",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 2_000L),
                )
            )
        )

        assertEquals(
            listOf("hotspot-device"),
            ConnectionHistoryManager.getClosedConnections().map { it.id },
        )
    }

    @Test
    fun startupLegacyIpv6_isRetiredWhenTheSameAppReconnectsThroughTheIpv6TunPrefix() {
        val sessionStart = System.currentTimeMillis()
        val physicalIpv6 = "2001:db8::45"
        beginStartupSession(sessionStart, localSourceAddresses = setOf(physicalIpv6))
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "legacy-ipv6",
                    closed = true,
                    uid = 10_123,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = physicalIpv6,
                    destinationIp = "2400:3200::1",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "tun-ipv6",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "fdfe:dcba:9876::2",
                    destinationIp = "2400:3200::1",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 2_000L),
                )
            )
        )

        assertTrue(ConnectionHistoryManager.getClosedConnections().isEmpty())
    }

    @Test
    fun startupLegacyIpv6_supportsACustomTunPrefixPassedToTheSession() {
        val sessionStart = System.currentTimeMillis()
        val physicalIpv6 = "2001:db8::45"
        ConnectionHistoryManager.beginSession(
            startedAtMillis = sessionStart,
            tunPrefixes = listOf("fd00:1234::1/64"),
            localSourceAddresses = setOf(physicalIpv6),
        )
        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "legacy-custom-ipv6",
                    closed = true,
                    uid = 10_123,
                    closedAt = sessionStart + 1_000L,
                    type = "Tun",
                    network = "udp",
                    sourceIp = physicalIpv6,
                    destinationIp = "2400:3200::1",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 500L),
                )
            )
        )

        ConnectionHistoryManager.updateConnections(
            listOf(
                connection(
                    id = "custom-tun-ipv6",
                    closed = false,
                    uid = 10_123,
                    type = "Tun",
                    network = "udp",
                    sourceIp = "fd00:1234::2",
                    destinationIp = "2400:3200::1",
                    destinationPort = 23_000,
                    start = timestamp(sessionStart + 2_000L),
                )
            )
        )

        assertTrue(ConnectionHistoryManager.getClosedConnections().isEmpty())
    }

    @Test
    fun supersededLegacy_doesNotReappearFromARepeatedCloseSnapshot() {
        val sessionStart = System.currentTimeMillis()
        beginStartupSession(sessionStart)
        val legacy =
            connection(
                id = "legacy-repeat",
                closed = true,
                uid = 10_123,
                closedAt = sessionStart + 1_000L,
                type = "Tun",
                network = "udp",
                sourceIp = "10.2.196.45",
                destinationIp = "122.193.7.205",
                destinationPort = 23_000,
                start = timestamp(sessionStart + 500L),
            )
        ConnectionHistoryManager.updateConnections(listOf(legacy))

        val replacement =
            connection(
                id = "tun-repeat",
                closed = true,
                uid = 10_123,
                closedAt = sessionStart + 3_000L,
                type = "Tun",
                network = "udp",
                sourceIp = "172.19.0.1",
                destinationIp = "122.193.7.205",
                destinationPort = 23_000,
                start = timestamp(sessionStart + 2_000L),
            )
        ConnectionHistoryManager.updateConnections(listOf(replacement))

        // The core retains closed entries and can repeat the stale legacy row. The tombstone must
        // keep it from returning to the recent-request list.
        ConnectionHistoryManager.updateConnections(listOf(legacy, replacement))

        assertEquals(
            listOf("tun-repeat"),
            ConnectionHistoryManager.getClosedConnections().map { it.id },
        )
    }

    private fun connection(
        id: String,
        closed: Boolean,
        uid: Int?,
        closedAt: Long = 0L,
        system: Boolean = false,
        type: String? = null,
        network: String = "tcp",
        sourceIp: String = "192.0.2.10",
        destinationIp: String = "198.51.100.20",
        destinationPort: Int? = null,
        start: String = "",
    ): ConnectionInfo =
        ConnectionInfo(
            id = id,
            metadata =
                buildJsonObject {
                    type?.let { put("type", it) }
                    put("network", network)
                    put("sourceIP", sourceIp)
                    put("destinationIP", destinationIp)
                    destinationPort?.let { put("destinationPort", it) }
                    uid?.let { put("uid", it) }
                },
            closed = closed,
            closedAt = closedAt,
            system = system,
            start = start,
        )

    private fun timestamp(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis).toString()

    private fun beginStartupSession(
        startedAtMillis: Long,
        localSourceAddresses: Set<String> = setOf("10.2.196.45"),
    ) {
        ConnectionHistoryManager.beginSession(
            startedAtMillis = startedAtMillis,
            tunPrefixes = TunNetworkDefaults.CIDRS,
            localSourceAddresses = localSourceAddresses,
        )
    }
}

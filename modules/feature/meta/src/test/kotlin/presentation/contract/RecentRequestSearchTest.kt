/*
 * This file is part of MonadBox.
 *
 * Regression test for recent-request search: only fields that are actually shown in the
 * request list row and the connection detail sheet may match, so every search result
 * visibly contains the query.
 */

package com.github.nomadboxlab.monadbox.feature.meta.presentation.contract

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.feature.meta.api.RecentRequestRecord
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentRequestSearchTest {

    private val connection =
        ConnectionInfo(
            id = "conn-123",
            metadata =
                buildJsonObject {
                    put("host", "example.com")
                    put("destinationPort", "443")
                    put("sourcePort", "54321")
                    put("network", "tcp")
                    put("process", "com.example.app")
                    put("uid", "12345")
                    put("bytesReceived", "123456")
                },
            upload = 0L,
            download = 0L,
            start = "",
            chains = listOf("GROUP", "EXIT-123"),
            providerChains = listOf("PROVIDER-123"),
            rule = "MATCH",
            rulePayload = "example.com",
        )

    private val record =
        RecentRequestRecord(
            connection = connection,
            isActive = true,
            topLevelGroupName = "GROUP",
            bottomNodeName = "EXIT-123",
            sourceAppName = "Chrome",
            sourcePackageName = "com.android.chrome",
        )

    @Test
    fun matchesVisibleFields() {
        assertTrue(record.matchesQuery("example"))
        assertTrue(record.matchesQuery("443"))
        assertTrue(record.matchesQuery("tcp"))
        assertTrue(record.matchesQuery("com.example.app"))
        assertTrue(record.matchesQuery("chrome"))
        assertTrue(record.matchesQuery("MATCH"))
        assertTrue(record.matchesQuery("EXIT-123"))
        assertTrue(record.matchesQuery("EXAMPLE.COM"))
    }

    @Test
    fun doesNotMatchInvisibleFields() {
        assertFalse("connection id must not match", record.matchesQuery("conn-123"))
        assertFalse("uid must not match", record.matchesQuery("12345"))
        assertFalse("raw byte counters must not match", record.matchesQuery("123456"))
        assertFalse("provider chains must not match", record.matchesQuery("PROVIDER-123"))
    }
}

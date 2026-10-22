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

package com.github.nomadboxlab.monadbox.data.repository

import com.github.nomadboxlab.monadbox.core.domain.ConnectionHistoryManager
import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeConnectionReader
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeProfileRef
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConnectionActivityRepositoryTest {

    @Before
    fun setUp() {
        ConnectionHistoryManager.clear()
    }

    @After
    fun tearDown() {
        ConnectionHistoryManager.clear()
    }

    @Test
    fun lateUidUpgradeIsPublishedToClosedConnectionCollectors() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val reader =
            SequenceConnectionReader(
                listOf(
                    listOf(connection(id = "late-uid", closed = false, uid = null)),
                    emptyList(),
                    listOf(connection(id = "late-uid", closed = true, uid = 10_123)),
                )
            )
        val repository =
            ConnectionActivityRepository(
                runtimeStateReader = FakeRuntimeStateReader(),
                runtimeConnectionReader = reader,
                scope = scope,
            )

        try {
            val published =
                withTimeout(6_000L) {
                    repository.closedConnections.first { rows ->
                        rows.any {
                            it.id == "late-uid" && it.metadata["uid"]?.jsonPrimitive?.int == 10_123
                        }
                    }
                }

            val row = published.single { it.id == "late-uid" }
            assertEquals(10_123, row.metadata.getValue("uid").jsonPrimitive.int)
        } finally {
            repository.stop()
            scope.cancel()
        }
    }

    private fun connection(id: String, closed: Boolean, uid: Int?): ConnectionInfo =
        ConnectionInfo(
            id = id,
            metadata =
                buildJsonObject {
                    put("network", "tcp")
                    put("sourceIP", "192.0.2.10")
                    uid?.let { put("uid", it) }
                },
            closed = closed,
        )

    private class SequenceConnectionReader(private val snapshots: List<List<ConnectionInfo>>) :
        RuntimeConnectionReader {
        private val index = AtomicInteger()

        override suspend fun queryConnections(): List<ConnectionInfo> =
            snapshots[index.getAndIncrement().coerceAtMost(snapshots.lastIndex)]
    }

    private class FakeRuntimeStateReader : RuntimeStateReader {
        override val isRuntimeRunning: StateFlow<Boolean> = MutableStateFlow(true)
        override val isAppActive: StateFlow<Boolean> = MutableStateFlow(true)
        override val runtimeTrafficTotal: StateFlow<Long> = MutableStateFlow(0L)
        override val currentRuntimeProfile: StateFlow<RuntimeProfileRef?> = MutableStateFlow(null)

        override suspend fun refreshTrafficTotal() = Unit
    }
}

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

package com.github.nomadboxlab.monadbox.feature.meta.api

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

enum class ConnectionSort {
    Time,
    Upload,
    Download,
    Host,
}

enum class ConnectionTab {
    ACTIVE,
    CLOSED,
}

data class ConnectionState(
    val activeConnections: List<ConnectionInfo> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val sortBy: ConnectionSort = ConnectionSort.Time,
    val selectedTab: ConnectionTab = ConnectionTab.ACTIVE,
    val error: String? = null,
    val structuredError: StructuredError? = null,
) {
    val totalConnections: Int
        get() = activeConnections.size
}

interface ConnectionExplorer {
    val state: StateFlow<ConnectionState>
    val filteredConnections: StateFlow<List<ConnectionInfo>>

    fun setSearchQuery(query: String)

    fun setSortBy(sort: ConnectionSort)

    fun setTab(tab: ConnectionTab)

    fun clearError()
}

data class ConnectionDisplayAddress(
    val title: String,
    val sourceAddress: String,
    val destinationAddress: String,
)

fun ConnectionInfo.toConnectionDisplayAddress(): ConnectionDisplayAddress {
    val host = metadata["host"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val sourceIp = metadata["sourceIP"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val sourcePort = metadata["sourcePort"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val destinationIp = metadata["destinationIP"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
    val destinationPort =
        metadata["destinationPort"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()

    val sourceAddress = joinHostPort(sourceIp, sourcePort)
    val destinationAddress =
        if (host.isNotBlank()) {
            joinHostPort(host, destinationPort)
        } else {
            joinHostPort(destinationIp, destinationPort)
        }

    return ConnectionDisplayAddress(
        title = destinationAddress.ifBlank { sourceAddress },
        sourceAddress = sourceAddress,
        destinationAddress = destinationAddress,
    )
}

data class ConnectionSourceApp(val appName: String, val packageName: String?)

interface ConnectionAppIdentityLookup {
    fun resolve(connection: ConnectionInfo): ConnectionSourceApp
}

private fun joinHostPort(host: String, port: String): String {
    if (host.isBlank()) return ""
    return if (port.isBlank()) host else "$host:$port"
}

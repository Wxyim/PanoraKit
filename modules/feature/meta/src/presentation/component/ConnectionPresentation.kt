package com.github.yumelira.yumebox.feature.meta.presentation.component

import com.github.yumelira.yumebox.core.model.ConnectionInfo
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

data class ConnectionDisplayAddress(
    val title: String,
    val sourceAddress: String,
    val destinationAddress: String,
)

fun ConnectionInfo.toDisplayAddress(): ConnectionDisplayAddress {
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

private fun joinHostPort(host: String, port: String): String {
    if (host.isBlank()) return ""
    return if (port.isBlank()) host else "$host:$port"
}

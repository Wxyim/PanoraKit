/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.core.domain.pcap

import com.github.yumelira.yumebox.core.model.ConnectionInfo
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant
import kotlinx.serialization.json.jsonPrimitive

object PcapConnectionRecorder {

    private const val PCAP_MAGIC = 0xa1b2c3d4L
    private const val PCAP_VERSION_MAJOR: Short = 2
    private const val PCAP_VERSION_MINOR: Short = 4
    private const val PCAP_SNAPLEN = 65535
    private const val LINKTYPE_RAW = 101
    private const val IP_HEADER_LEN = 20
    private const val TCP_HEADER_LEN = 20
    private const val UDP_HEADER_LEN = 8
    private const val PROTOCOL_TCP: Byte = 6
    private const val PROTOCOL_UDP: Byte = 17

    fun writePcapFile(output: OutputStream, connections: List<ConnectionInfo>) {
        writeGlobalHeader(output)
        for (conn in connections) {
            writeConnectionPacket(output, conn)
        }
        output.flush()
    }

    private fun writeGlobalHeader(output: OutputStream) {
        val buf = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
        buf.putInt(PCAP_MAGIC.toInt())
        buf.putShort(PCAP_VERSION_MAJOR)
        buf.putShort(PCAP_VERSION_MINOR)
        buf.putInt(0) // thiszone
        buf.putInt(0) // sigfigs
        buf.putInt(PCAP_SNAPLEN)
        buf.putInt(LINKTYPE_RAW)
        output.write(buf.array())
    }

    private fun writeConnectionPacket(output: OutputStream, conn: ConnectionInfo) {
        val srcIp = conn.metadata["sourceIP"]?.jsonPrimitive?.content ?: "0.0.0.0"
        val dstIp = conn.metadata["destinationIP"]?.jsonPrimitive?.content ?: "0.0.0.0"
        val srcPort = conn.metadata["sourcePort"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        val dstPort = conn.metadata["destinationPort"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        val network = conn.metadata["network"]?.jsonPrimitive?.content ?: "tcp"

        val metadataPayload = buildMetadataPayload(conn)
        val isTcp = network.contains("tcp", ignoreCase = true)
        val transportHeaderLen = if (isTcp) TCP_HEADER_LEN else UDP_HEADER_LEN
        val totalLen = IP_HEADER_LEN + transportHeaderLen + metadataPayload.size

        val ipHeader = buildIpHeader(totalLen, isTcp, srcIp, dstIp)
        val transportHeader =
            if (isTcp) buildTcpHeader(srcPort, dstPort)
            else buildUdpHeader(srcPort, dstPort, UDP_HEADER_LEN + metadataPayload.size)

        val timestampMs = parseTimestamp(conn.start)
        val recordHeader = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
        recordHeader.putInt((timestampMs / 1000).toInt())
        recordHeader.putInt(((timestampMs % 1000) * 1000).toInt())
        recordHeader.putInt(totalLen)
        recordHeader.putInt(totalLen)

        output.write(recordHeader.array())
        output.write(ipHeader)
        output.write(transportHeader)
        output.write(metadataPayload)
    }

    private fun buildMetadataPayload(conn: ConnectionInfo): ByteArray {
        return buildString {
                append("rule=")
                append(conn.rule)
                if (conn.rulePayload.isNotEmpty()) {
                    append(" payload=")
                    append(conn.rulePayload)
                }
                if (conn.chains.isNotEmpty()) {
                    append(" chains=")
                    append(conn.chains.joinToString("->"))
                }
                conn.metadata["process"]?.jsonPrimitive?.content?.let {
                    append(" process=")
                    append(it)
                }
                conn.metadata["host"]?.jsonPrimitive?.content?.let {
                    append(" host=")
                    append(it)
                }
                append(" up=")
                append(conn.upload)
                append(" down=")
                append(conn.download)
            }
            .toByteArray(Charsets.UTF_8)
    }

    private fun buildIpHeader(
        totalLen: Int,
        isTcp: Boolean,
        srcIp: String,
        dstIp: String,
    ): ByteArray {
        val buf = ByteBuffer.allocate(IP_HEADER_LEN).order(ByteOrder.BIG_ENDIAN)
        buf.put(0x45.toByte()) // version=4, IHL=5
        buf.put(0) // DSCP/ECN
        buf.putShort(totalLen.toShort())
        buf.putShort(0) // identification
        buf.putShort(0x4000.toShort()) // Don't Fragment
        buf.put(64) // TTL
        buf.put(if (isTcp) PROTOCOL_TCP else PROTOCOL_UDP)
        buf.putShort(0) // checksum
        putIpv4Address(buf, srcIp)
        putIpv4Address(buf, dstIp)
        return buf.array()
    }

    private fun buildTcpHeader(srcPort: Int, dstPort: Int): ByteArray {
        val buf = ByteBuffer.allocate(TCP_HEADER_LEN).order(ByteOrder.BIG_ENDIAN)
        buf.putShort(srcPort.toShort())
        buf.putShort(dstPort.toShort())
        buf.putInt(0) // sequence
        buf.putInt(0) // ack
        buf.putShort(0x5002.toShort()) // data offset=5, SYN
        buf.putShort(65535.toShort()) // window
        buf.putShort(0) // checksum
        buf.putShort(0) // urgent
        return buf.array()
    }

    private fun buildUdpHeader(srcPort: Int, dstPort: Int, length: Int): ByteArray {
        val buf = ByteBuffer.allocate(UDP_HEADER_LEN).order(ByteOrder.BIG_ENDIAN)
        buf.putShort(srcPort.toShort())
        buf.putShort(dstPort.toShort())
        buf.putShort(length.toShort())
        buf.putShort(0) // checksum
        return buf.array()
    }

    private fun putIpv4Address(buf: ByteBuffer, ip: String) {
        val parts = ip.split(".")
        if (parts.size == 4) {
            parts.forEach { buf.put((it.toIntOrNull() ?: 0).toByte()) }
        } else {
            repeat(4) { buf.put(0) }
        }
    }

    private fun parseTimestamp(start: String): Long {
        return try {
            Instant.parse(start).toEpochMilli()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}

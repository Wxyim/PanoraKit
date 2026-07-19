/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.service.clash.module

import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Build
import androidx.core.content.getSystemService
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.util.parseInetSocketAddress
import com.github.nomadboxlab.monadbox.service.common.util.ProcFsUidResolver
import java.net.InetSocketAddress
import java.security.SecureRandom
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext

class TunModule(private val vpn: VpnService) : Module<Unit>(vpn) {
    data class TunDevice(
        val fd: Int,
        var stack: String,
        val gateway: String,
        val portal: String,
        val dns: String,
    )

    private val connectivity = service.getSystemService<ConnectivityManager>()
    private val close = Channel<Unit>(Channel.CONFLATED)

    private fun queryUid(protocol: Int, source: InetSocketAddress, target: InetSocketAddress): Int {
        if (Build.VERSION.SDK_INT < 29) return -1

        val manager = connectivity ?: return -1

        val uid =
            runCatching { manager.getConnectionOwnerUid(protocol, source, target) }
                .getOrElse { -1 }
        if (uid > 0) return uid

        // getConnectionOwnerUid may miss sockets that are already closed,
        // in TIME_WAIT, or otherwise untracked by the kernel. Fall back to
        // reading /proc/net/{tcp,udp}[6] with a protocol-specific resolver.
        if (uid <= 0) {
            val procUid = ProcFsUidResolver.resolveByProtocol(protocol, source)
            if (procUid > 0) return procUid
        }
        return -1
    }

    override suspend fun run() {
        try {
            return close.receive()
        } finally {
            withContext(NonCancellable) { requestStop() }
        }
    }

    fun listenHttp(): InetSocketAddress? {
        val r = { 1 + random.nextInt(199) }
        val listenAt = "127.${r()}.${r()}.${r()}:0"
        val address = Clash.startLocalProxyHttpListener(listenAt)

        return address?.let(::parseInetSocketAddress)
    }

    fun attach(device: TunDevice) {
        ProcFsUidResolver.startMonitoring()
        Clash.startTun(
            fd = device.fd,
            stack = device.stack,
            gateway = device.gateway,
            portal = device.portal,
            dns = device.dns,
            markSocket = vpn::protect,
            querySocketUid = this::queryUid,
        )
    }

    suspend fun close() {
        ProcFsUidResolver.stopMonitoring()
        close.send(Unit)
    }

    companion object {
        private val random = SecureRandom()

        fun requestStop() {
            Clash.stopLocalProxyHttpListener()
            Clash.stopTun()
        }
    }
}

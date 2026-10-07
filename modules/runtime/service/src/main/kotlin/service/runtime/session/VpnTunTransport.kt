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

package com.github.nomadboxlab.monadbox.service.runtime.session

import android.app.PendingIntent
import android.content.Intent
import android.net.ProxyInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import com.github.nomadboxlab.monadbox.core.util.parseInetSocketAddress
import com.github.nomadboxlab.monadbox.runtime.service.R
import com.github.nomadboxlab.monadbox.service.common.compat.pendingIntentFlags
import com.github.nomadboxlab.monadbox.service.common.constants.Components
import com.github.nomadboxlab.monadbox.service.common.util.ProcFsUidResolver
import com.github.nomadboxlab.monadbox.service.runtime.config.AccessControlMode
import com.github.nomadboxlab.monadbox.service.runtime.config.ServiceStore
import com.github.nomadboxlab.monadbox.service.runtime.util.parseCIDR
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import timber.log.Timber

class VpnTunTransport(
    private val vpnService: VpnService,
    private val store: ServiceStore = ServiceStore(),
) : RuntimeTransport {
    private val random = SecureRandom()
    private val uidCache = ConcurrentHashMap<UidQueryKey, UidCacheEntry>()
    private val packageNameCache = ConcurrentHashMap<Int, String>()
    private val startupLogStore =
        RuntimeStartupLogStore(vpnService, RuntimeStartupLogStore.Scope.LOCAL_TUN)
    @Volatile private var pendingDevice: TunDevice? = null
    @Volatile private var uidWarmupUntilElapsedMs = 0L
    private var uidWarmupStartedAtElapsedMs = 0L
    @Volatile private var uidWarmupMissLogged = false
    @Volatile private var uidMissLoggedAtElapsedMs = 0L
    private val deviceLock = Any()

    /**
     * Phase 1: build VPN parameters and call [VpnService.establish]. This involves Android IPC to
     * the system VPN service (~0.5-1s) and is independent of the Go runtime. It runs in parallel
     * with config compilation to overlap I/O.
     *
     * [VpnService.establish] makes the VPN session live immediately. The detached fd is tracked in
     * the process-wide [leakedTunFd] and held in [pendingDevice] until [start] hands it to the Go
     * stack. Any prior established-but-unconsumed session is closed first, so a re-prepare never
     * leaks a fd behind the new session.
     */
    override fun prepare(spec: RuntimeSpec) {
        uidCache.clear()
        packageNameCache.clear()
        refreshUidWarmup()
        startupLogStore.append("LOCAL_TUN transport prepare: begin")
        val device =
            with(vpnService.Builder()) {
                addAddress(TUN_GATEWAY, TUN_SUBNET_PREFIX)
                if (store.allowIpv6) {
                    addAddress(TUN_GATEWAY6, TUN_SUBNET_PREFIX6)
                }

                if (store.bypassPrivateNetwork) {
                    vpnService.resources
                        .getStringArray(R.array.bypass_private_route)
                        .map(::parseCIDR)
                        .forEach { addRoute(it.ip, it.prefix) }
                    if (store.allowIpv6) {
                        vpnService.resources
                            .getStringArray(R.array.bypass_private_route6)
                            .map(::parseCIDR)
                            .forEach { addRoute(it.ip, it.prefix) }
                    }
                    addRoute(TUN_DNS, 32)
                    if (store.allowIpv6) {
                        addRoute(TUN_DNS6, 128)
                    }
                } else {
                    addRoute(NET_ANY, 0)
                    if (store.allowIpv6) {
                        addRoute(NET_ANY6, 0)
                    }
                }

                when (store.accessControlMode) {
                    AccessControlMode.AcceptAll -> Unit
                    AccessControlMode.AcceptSelected -> {
                        (store.accessControlPackages + vpnService.packageName).forEach {
                            runCatching { addAllowedApplication(it) }
                        }
                    }
                    AccessControlMode.RejectAll -> {
                        // Reject every app from the tunnel: allow only the VPN's own package so all
                        // other apps bypass the proxy.
                        runCatching { addAllowedApplication(vpnService.packageName) }
                    }
                    AccessControlMode.RejectSelected -> {
                        (store.accessControlPackages - vpnService.packageName).forEach {
                            runCatching { addDisallowedApplication(it) }
                        }
                    }
                }

                setBlocking(false)
                setMtu(TUN_MTU)
                setSession("Clash")
                addDnsServer(TUN_DNS)
                if (store.allowIpv6) {
                    addDnsServer(TUN_DNS6)
                }
                setConfigureIntent(
                    PendingIntent.getActivity(
                        vpnService,
                        R.id.nf_vpn_status,
                        Intent()
                            .setComponent(Components.PROXY_SHEET_ACTIVITY)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                    Intent.FLAG_ACTIVITY_NO_ANIMATION
                            ),
                        pendingIntentFlags(PendingIntent.FLAG_UPDATE_CURRENT),
                    )
                )

                if (Build.VERSION.SDK_INT >= 29) {
                    setMetered(false)
                }

                if (Build.VERSION.SDK_INT >= 29 && store.systemProxy) {
                    listenHttp()?.let {
                        setHttpProxy(
                            ProxyInfo.buildDirectProxy(
                                it.address.hostAddress,
                                it.port,
                                HTTP_PROXY_BLACK_LIST +
                                    if (store.bypassPrivateNetwork) HTTP_PROXY_LOCAL_LIST
                                    else emptyList(),
                            )
                        )
                    }
                }

                if (store.allowBypass) {
                    allowBypass()
                }

                TunDevice(
                    fd = establish()?.detachFd() ?: error("Establish VPN rejected by system"),
                    stack = store.tunStackMode,
                    gateway =
                        "$TUN_GATEWAY/$TUN_SUBNET_PREFIX" +
                            if (store.allowIpv6) ",$TUN_GATEWAY6/$TUN_SUBNET_PREFIX6" else "",
                    portal = TUN_PORTAL + if (store.allowIpv6) ",$TUN_PORTAL6" else "",
                    dns =
                        if (store.dnsHijacking) NET_ANY
                        else (TUN_DNS + if (store.allowIpv6) ",$TUN_DNS6" else ""),
                )
            }
        synchronized(deviceLock) {
            // A previous prepare() may have established a session that was never
            // handed to the Go stack (start() interrupted); close it before
            // replacing so no fd leaks behind the new session.
            closePendingDeviceLocked()
            pendingDevice = device
            leakedTunFd.set(device.fd)
        }
        startupLogStore.append("LOCAL_TUN transport prepare: done fd=${pendingDevice?.fd}")
    }

    /**
     * Phase 2: hand the established VPN fd to the Go TUN stack. Requires [prepare] to have
     * completed (Go runtime must also be ready).
     *
     * The fd is claimed from the tracker atomically (compareAndSet on [pendingDevice].fd): if a
     * concurrent teardown ([closeLeakedTunFd]) already closed it, the claim fails and this aborts
     * instead of handing a closed descriptor to Go. On success the Go stack owns the fd exclusively
     * (tracker cleared), so any later close is a no-op.
     */
    override fun start(spec: RuntimeSpec) {
        val device =
            synchronized(deviceLock) {
                val pending =
                    pendingDevice
                        ?: error(
                            "transport.prepare() must be called before transport.start(), " +
                                "or the established device was closed by a concurrent stop()"
                        )
                // Atomically claim the fd: if an external teardown (closeLeakedTunFd) already
                // closed it, the tracker no longer matches, so abort instead of handing a closed
                // fd to the Go stack. Exactly one of this/closePendingDeviceLocked/external
                // teardown closes the fd.
                check(leakedTunFd.compareAndSet(pending.fd, -1)) {
                    "established device was closed by a concurrent teardown"
                }
                pendingDevice = null
                pending
            }
        startupLogStore.append("LOCAL_TUN transport start: begin fd=${device.fd}")
        // Packets only start flowing here, not at prepare(): in between, SessionRuntime runs the
        // config compilation, the core load, the app-uid publish and the GLOBAL selection
        // bootstrap, so a slow cold start can consume the whole warm-up window [prepare] opened
        // before a single packet arrives. That burst is exactly what needs the UID retries — a
        // miss there is negative-cached for 5s and the row can never resolve an app name.
        // Re-anchor the window on the moment the data path actually goes live.
        val sincePrepareMs = android.os.SystemClock.elapsedRealtime() - uidWarmupStartedAtElapsedMs
        refreshUidWarmup()
        startupLogStore.append(
            "LOCAL_TUN transport start: uid warmup re-anchored (${sincePrepareMs}ms after prepare)"
        )
        com.github.nomadboxlab.monadbox.core.Clash.startTun(
            fd = device.fd,
            stack = device.stack,
            gateway = device.gateway,
            portal = device.portal,
            dns = device.dns,
            markSocket = vpnService::protect,
            querySocketUid = this::queryUid,
            queryPackageName = this::queryPackageName,
        )
        startupLogStore.append("LOCAL_TUN transport start: done")
    }

    /**
     * Tears the transport down. A session established by [prepare] but never handed to Go is closed
     * here via [closePendingDeviceLocked]; otherwise the Go stack owns the fd and
     * [com.github.nomadboxlab.monadbox.core.Clash.stopTun] releases it. Both paths are idempotent
     * and gated by the tracker, so concurrent teardowns cannot double-close.
     */
    override fun stop() {
        uidCache.clear()
        packageNameCache.clear()
        com.github.nomadboxlab.monadbox.core.Clash.stopLocalProxyHttpListener()
        synchronized(deviceLock) { closePendingDeviceLocked() }
        com.github.nomadboxlab.monadbox.core.Clash.stopTun()
    }

    /**
     * Closes an established VPN fd that was never handed to the Go stack.
     *
     * [VpnService.establish] makes the Android VPN session live immediately, but
     * [com.github.nomadboxlab.monadbox.core.Clash.stopTun] only closes the fd once startTun()
     * actually ran (the Go rTun is nil otherwise). If the runtime is torn down between prepare()
     * and start() - a START_STICKY restart interrupted by a toggle-off, a failed cold start after
     * establish(), or a start/stop race - the established fd would otherwise leak and keep the VPN
     * session alive behind an already-"off" toggle (the status-bar VPN icon persists, and the first
     * traffic into the unread fd leaves a broken, zombie VPN). Must be called while holding
     * deviceLock.
     */
    private fun closePendingDeviceLocked() {
        val device = pendingDevice ?: return
        pendingDevice = null
        // Claim the fd atomically: if an external teardown (closeLeakedTunFd) already closed it,
        // the tracker no longer matches, so skip to avoid closing a reused fd number.
        if (leakedTunFd.compareAndSet(device.fd, -1)) {
            runCatching { ParcelFileDescriptor.adoptFd(device.fd).use {} }
        }
    }

    override fun onNetworkChanged() {
        uidCache.clear()
        if (Build.VERSION.SDK_INT in 22..28) {
            @Suppress("DEPRECATION") vpnService.setUnderlyingNetworks(null)
        }
    }

    private fun listenHttp(): java.net.InetSocketAddress? {
        val r = { 1 + random.nextInt(199) }
        val listenAt = "127.${r()}.${r()}.${r()}:0"
        val address =
            com.github.nomadboxlab.monadbox.core.Clash.startLocalProxyHttpListener(listenAt)
        return address?.let(::parseInetSocketAddress)
    }

    private fun queryUid(
        protocol: Int,
        source: java.net.InetSocketAddress,
        target: java.net.InetSocketAddress,
        force: Boolean,
    ): Int {
        val now = android.os.SystemClock.elapsedRealtime()
        val warmupActive = now < uidWarmupUntilElapsedMs
        val key =
            UidQueryKey(
                protocol = protocol,
                source = endpointKey(source),
                target = endpointKey(target),
            )
        val cached = uidCache[key]?.takeIf { it.expiresAt > now }
        // A forced (enrichment) lookup may reuse a positive answer but never a cached miss: the
        // native enrichment path asks precisely about the sockets that missed while they were
        // alive, and it is already rate-limited per connection, so the cached -1 would only hide
        // the one lookup that can still attribute a short-lived connection.
        if (cached != null && (cached.uid > 0 || !force)) {
            return cached.uid
        }

        val connectivity = vpnService.getSystemService(android.net.ConnectivityManager::class.java)
        var uid = queryConnectionOwnerUid(connectivity, protocol, source, target)
        // Android can publish the socket owner a few milliseconds after the
        // socket first sends, so the warm-up window (anchored at transport
        // start, see [start]) retries a few times. Outside it a single retry
        // still covers the narrow race against a short-lived socket going away
        // before this query runs; a miss is negative-cached, so retrying more
        // would only cost every later connection that reuses the 4-tuple.
        // getConnectionOwnerUid is API 29+; on older devices a retry would only repeat a lookup
        // that cannot succeed, and the procfs fallback below is the only source.
        val retries =
            when {
                Build.VERSION.SDK_INT < 29 -> 0
                warmupActive -> UID_WARMUP_RETRIES
                else -> UID_MISS_RETRIES
            }
        val retryDelayMs = if (warmupActive) UID_WARMUP_RETRY_DELAY_MS else UID_MISS_RETRY_DELAY_MS
        if (uid <= 0) {
            repeat(retries) {
                if (uid <= 0) {
                    android.os.SystemClock.sleep(retryDelayMs)
                    uid = queryConnectionOwnerUid(connectivity, protocol, source, target)
                }
            }
        }
        if (uid > 0) {
            cacheUid(key, uid, now + UID_CACHE_POSITIVE_TTL_MS)
            return uid
        }

        // getConnectionOwnerUid may miss sockets that are already closed,
        // in TIME_WAIT, or otherwise untracked by the kernel. Fall back to
        // reading /proc/net/{tcp,udp}[6] with a protocol-specific resolver.
        if (uid <= 0) {
            val procUid = ProcFsUidResolver.resolveByProtocol(protocol, source)
            if (procUid > 0) {
                cacheUid(key, procUid, now + UID_CACHE_POSITIVE_TTL_MS)
                return procUid
            }
        }
        // A miss is inherently transient during VPN startup and for a forced enrichment lookup:
        // Android may not have published the socket owner yet, and procfs may lag behind the first
        // packet. Only negative-cache once the warm-up window has elapsed and the caller is not
        // forcing a fresh lookup, otherwise the same socket can remain unattributed for the rest of
        // its short lifetime. A stable miss is cached briefly so retransmits and TIME_WAIT
        // re-queries stop hammering the ConnectivityService Binder and procfs with the same
        // 4-tuple.
        if (warmupActive || force) {
            uidCache.remove(key)
        } else {
            cacheUid(key, -1, now + UID_CACHE_NEGATIVE_TTL_MS)
        }
        if (warmupActive && !uidWarmupMissLogged) {
            uidWarmupMissLogged = true
            logUidMiss(source, target, protocol)
        } else if (!warmupActive && now - uidMissLoggedAtElapsedMs >= UID_MISS_LOG_INTERVAL_MS) {
            uidMissLoggedAtElapsedMs = now
            logUidMiss(source, target, protocol)
        }
        return -1
    }

    /**
     * One line per warm-up window, plus a rate-limited one outside it: tells an on-device log
     * whether a row rendered without an app name is a real UID miss (and what it looked like) or
     * something else.
     */
    private fun logUidMiss(
        source: java.net.InetSocketAddress,
        target: java.net.InetSocketAddress,
        protocol: Int,
    ) {
        Timber.d("UID unresolved after retries: %s -> %s (%d)", source, target, protocol)
    }

    private fun queryConnectionOwnerUid(
        connectivity: android.net.ConnectivityManager?,
        protocol: Int,
        source: java.net.InetSocketAddress,
        target: java.net.InetSocketAddress,
    ): Int {
        // getConnectionOwnerUid is API 29+; older devices are served by the procfs fallback.
        if (Build.VERSION.SDK_INT < 29) return -1
        return runCatching { connectivity?.getConnectionOwnerUid(protocol, source, target) ?: -1 }
            .getOrDefault(-1)
    }

    private fun queryPackageName(uid: Int): String {
        if (uid <= 0) return ""
        packageNameCache[uid]?.let {
            return it
        }
        return runCatching {
                vpnService.packageManager
                    .getPackagesForUid(uid)
                    ?.asSequence()
                    ?.firstOrNull { it.isNotBlank() }
                    .orEmpty()
            }
            .getOrDefault("")
            .also { packageName ->
                if (packageName.isNotBlank()) packageNameCache[uid] = packageName
            }
    }

    private fun endpointKey(endpoint: java.net.InetSocketAddress): String {
        return "${endpoint.address?.hostAddress ?: endpoint.hostString}:${endpoint.port}"
    }

    private fun cacheUid(key: UidQueryKey, uid: Int, expiresAt: Long) {
        uidCache[key] = UidCacheEntry(uid = uid, expiresAt = expiresAt)
        if (uidCache.size <= UID_CACHE_MAX_ENTRIES) {
            return
        }
        // Drop expired entries first; they are always safe to remove.
        val now = android.os.SystemClock.elapsedRealtime()
        uidCache.entries.forEach { entry ->
            if (entry.value.expiresAt <= now) {
                uidCache.remove(entry.key, entry.value)
            }
        }
        // Still over the cap (e.g. a burst of long-lived entries): evict the
        // entries that expire soonest, keeping the freshly cached one.
        if (uidCache.size > UID_CACHE_MAX_ENTRIES) {
            uidCache.entries
                .filterNot { it.key == key }
                .sortedBy { it.value.expiresAt }
                .take(uidCache.size - UID_CACHE_MAX_ENTRIES)
                .forEach { uidCache.remove(it.key, it.value) }
        }
    }

    /**
     * Opens (or re-opens) the warm-up window during which a UID miss is retried instead of being
     * negative-cached. Called from [prepare] (the session is live and the traffic burst may start)
     * and again from [start] (packets actually begin to flow).
     */
    private fun refreshUidWarmup() {
        val now = android.os.SystemClock.elapsedRealtime()
        uidWarmupStartedAtElapsedMs = now
        uidWarmupUntilElapsedMs = now + UID_WARMUP_WINDOW_MS
        uidWarmupMissLogged = false
        uidMissLoggedAtElapsedMs = 0L
    }

    private data class UidQueryKey(val protocol: Int, val source: String, val target: String)

    private data class UidCacheEntry(val uid: Int, val expiresAt: Long)

    private data class TunDevice(
        val fd: Int,
        val stack: String,
        val gateway: String,
        val portal: String,
        val dns: String,
    )

    companion object {
        private val leakedTunFd = AtomicInteger(-1)

        /**
         * Closes an established-but-unconsumed TUN fd from any process-local teardown path.
         *
         * The established fd is tracked process-wide so teardown code that cannot reach the
         * transport instance (e.g. the client's force-close when the service main looper is wedged
         * and onDestroy is deferred) can still close it and revoke the VPN. Idempotent; no-op when
         * no fd is pending. Closing an fd here that was already handed to the Go stack is prevented
         * by clearing the tracker on start and close.
         */
        fun closeLeakedTunFd() {
            val fd = leakedTunFd.getAndSet(-1)
            if (fd > 0) {
                runCatching { ParcelFileDescriptor.adoptFd(fd).use {} }
            }
        }

        private const val TUN_MTU = 1500
        private const val TUN_SUBNET_PREFIX = 30
        private const val TUN_GATEWAY = "172.19.0.1"
        private const val TUN_SUBNET_PREFIX6 = 126
        private const val TUN_GATEWAY6 = "fdfe:dcba:9876::1"
        private const val TUN_PORTAL = "172.19.0.2"
        private const val TUN_PORTAL6 = "fdfe:dcba:9876::2"
        private const val TUN_DNS = TUN_PORTAL
        private const val TUN_DNS6 = TUN_PORTAL6
        private const val NET_ANY = "0.0.0.0"
        private const val NET_ANY6 = "::"
        private const val UID_CACHE_POSITIVE_TTL_MS = 15_000L
        private const val UID_CACHE_NEGATIVE_TTL_MS = 5_000L
        private const val UID_CACHE_MAX_ENTRIES = 2048
        private const val UID_WARMUP_WINDOW_MS = 3_000L
        // The owner is published shortly after the socket first sends, and the startup burst runs
        // many lookups at once through the Go-side semaphore(4), so a couple of 2ms retries is not
        // enough to ride out the publication latency. 3 x 5ms bounds the added per-miss latency to
        // ~15ms and only ever applies inside the warm-up window.
        private const val UID_WARMUP_RETRIES = 3
        private const val UID_WARMUP_RETRY_DELAY_MS = 5L
        // Outside the warm-up window a single quick retry covers the narrow race against a
        // short-lived socket disappearing before this query runs. A non-forced miss is
        // negative-cached, so more retries would only cost every later connection that reuses the
        // same 4-tuple; a forced lookup is retried by the native enrichment budget instead (≤3
        // attempts, ≥500ms apart), which is where the late socket-owner publication is absorbed.
        private const val UID_MISS_RETRIES = 1
        private const val UID_MISS_RETRY_DELAY_MS = 10L
        // Steady-state misses are logged at most once per interval, so an unattributable socket
        // cannot flood the log while a single line still correlates a device report.
        private const val UID_MISS_LOG_INTERVAL_MS = 30_000L

        private val HTTP_PROXY_LOCAL_LIST =
            listOf(
                "localhost",
                "*.local",
                "127.*",
                "10.*",
                "172.16.*",
                "172.17.*",
                "172.18.*",
                "172.19.*",
                "172.2*",
                "172.30.*",
                "172.31.*",
                "192.168.*",
            )
        private val HTTP_PROXY_BLACK_LIST =
            listOf(
                "*zhihu.com",
                "*zhimg.com",
                "*jd.com",
                "100ime-iat-api.xfyun.cn",
                "*360buyimg.com",
            )
    }
}

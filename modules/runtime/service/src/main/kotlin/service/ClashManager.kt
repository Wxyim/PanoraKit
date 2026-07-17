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

package com.github.nomadboxlab.monadbox.service

import android.content.Context
import android.content.Intent
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.core.model.*
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.service.common.constants.Intents
import com.github.nomadboxlab.monadbox.service.common.log.Log
import com.github.nomadboxlab.monadbox.service.remote.IClashManager
import com.github.nomadboxlab.monadbox.service.remote.ILogObserver
import com.github.nomadboxlab.monadbox.service.runtime.config.ServiceStore
import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection
import com.github.nomadboxlab.monadbox.service.runtime.records.SelectionDao
import com.github.nomadboxlab.monadbox.service.runtime.session.CompiledConfigPipeline
import com.github.nomadboxlab.monadbox.service.runtime.session.SessionRuntimeSpecFactory
import com.github.nomadboxlab.monadbox.service.runtime.util.runSuspendBlocking
import com.github.nomadboxlab.monadbox.service.runtime.util.sendBroadcastSelf
import com.tencent.mmkv.MMKV
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber

class ClashManager(private val context: Context) : IClashManager, Closeable {
    private data class ExternalSelectionCandidate(val node: String, val firstSeenAt: Long)

    private companion object {
        const val EXTERNAL_SELECTION_CONFIRM_MS = 1200L
    }

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val store = ServiceStore()
    private val compiledConfigPipeline = CompiledConfigPipeline(context)
    private val runtimeSpecFactory = SessionRuntimeSpecFactory(context)
    private val networkSettings =
        MMKV.mmkvWithID(StoreIds.NETWORK_SETTINGS, MMKV.MULTI_PROCESS_MODE)
    private var logReceiver: ReceiveChannel<LogMessage>? = null
    private var logObserverJob: Job? = null
    private val externalCandidates = ConcurrentHashMap<String, ExternalSelectionCandidate>()

    override fun queryTunnelState(): TunnelState {
        return Clash.queryTunnelState()
    }

    override fun queryTrafficNow(): Long {
        if (!StatusProvider.serviceRunning) return 0L
        return Clash.queryTrafficNow()
    }

    override fun queryTrafficTotal(): Long {
        if (!StatusProvider.serviceRunning) return 0L
        return Clash.queryTrafficTotal()
    }

    override fun queryConnections(): ConnectionSnapshot {
        return Clash.queryConnections()
    }

    override fun queryProfileProxyGroupNames(excludeNotSelectable: Boolean): List<String> {
        return queryProfileProxyGroups(excludeNotSelectable).map(ProxyGroup::name)
    }

    override fun queryProfileProxyGroups(excludeNotSelectable: Boolean): List<ProxyGroup> {
        if (store.activeProfile == null) return emptyList()
        val spec =
            when (configuredProxyMode()) {
                ProxyMode.RootTun -> runtimeSpecFactory.createRootTunSpec()
                ProxyMode.Http -> runtimeSpecFactory.createHttpSpec()
                ProxyMode.Tun -> runtimeSpecFactory.createTunSpec()
            }
        return runSuspendBlocking {
            compiledConfigPipeline.previewGroups(spec, excludeNotSelectable)
        }
    }

    override fun queryAllProxyGroups(excludeNotSelectable: Boolean): List<ProxyGroup> {
        val groupNames = Clash.queryGroupNames(excludeNotSelectable)
        return groupNames.map { groupName ->
            Clash.queryGroup(groupName, ProxySort.Default).also { group ->
                syncSelectionSnapshotSafely(groupName, group)
            }
        }
    }

    override fun queryProxyGroupNames(excludeNotSelectable: Boolean): List<String> {
        return Clash.queryGroupNames(excludeNotSelectable)
    }

    override fun queryProxyGroup(name: String, proxySort: ProxySort): ProxyGroup {
        return Clash.queryGroup(name, proxySort).also { group ->
            syncSelectionSnapshotSafely(name, group)
        }
    }

    override fun queryConfiguration(): UiConfiguration {
        return Clash.queryConfiguration()
    }

    override fun queryProviders(): ProviderList {
        return ProviderList(Clash.queryProviders())
    }

    override fun patchSelector(group: String, name: String): Boolean {
        val ok = Clash.patchSelector(group, name)
        val current = store.activeProfile
        if (current == null) return ok

        if (ok) {
            SelectionDao.setSelected(Selection(current, group, name))
            externalCandidates.remove(selectionKey(current.toString(), group))
        } else if (!StatusProvider.serviceRunning) {
            // Core not running — persist selection so it applies when VPN starts.
            SelectionDao.setSelected(Selection(current, group, name))
            externalCandidates.remove(selectionKey(current.toString(), group))
            return true
        } else {
            SelectionDao.remove(current, group)
            externalCandidates.remove(selectionKey(current.toString(), group))
        }
        return ok
    }

    override fun closeConnection(id: String): Boolean {
        return Clash.closeConnection(id)
    }

    override fun closeAllConnections() {
        Clash.closeAllConnections()
    }

    override fun requestStop() {
        runCatching { context.sendBroadcastSelf(Intent(Intents.ACTION_CLASH_REQUEST_STOP)) }
    }

    override suspend fun healthCheck(group: String) {
        Timber.d("ClashManager healthCheck: group=%s", group)
        val request = Clash.healthCheck(group)
        managerScope.launch {
            runCatching { request.await() }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    Timber.w(error, "ClashManager healthCheck async failed: group=%s", group)
                }
        }
    }

    override suspend fun healthCheckProxy(proxyName: String): Int {
        Timber.d("ClashManager healthCheckProxy: proxy=%s", proxyName)
        val json = Clash.healthCheckProxy(proxyName).await()
        val obj = kotlinx.serialization.json.Json.parseToJsonElement(json)
        return obj.jsonObject["delay"]?.jsonPrimitive?.int ?: -1
    }

    override suspend fun updateProvider(type: Provider.Type, name: String) {
        return Clash.updateProvider(type, name).await()
    }

    private fun syncSelectionSnapshotSafely(group: String, proxyGroup: ProxyGroup) {
        val current = store.activeProfile ?: return
        val profileId = current.toString()
        val key = selectionKey(profileId, group)
        val node = proxyGroup.now.trim()
        val fallbackNode = proxyGroup.proxies.firstOrNull()?.name?.trim().orEmpty()
        val now = System.currentTimeMillis()
        if (node.isEmpty()) return

        val remembered = queryRememberedSelection(current, group)
        if (remembered == null) {
            SelectionDao.setSelected(Selection(current, group, node))
            externalCandidates.remove(key)
            return
        }
        if (remembered == node) {
            externalCandidates.remove(key)
            return
        }
        if (fallbackNode.isNotEmpty() && node == fallbackNode) {

            val proxyNames =
                proxyGroup.proxies.mapNotNull { it.name?.trim()?.takeIf { it.isNotEmpty() } }
            if (remembered in proxyNames) {
                managerScope.launch { Clash.patchSelector(group, remembered) }
            }
            return
        }

        val candidate = externalCandidates[key]
        if (candidate == null || candidate.node != node) {
            externalCandidates[key] = ExternalSelectionCandidate(node = node, firstSeenAt = now)
            return
        }
        if (now - candidate.firstSeenAt < EXTERNAL_SELECTION_CONFIRM_MS) {
            return
        }
        SelectionDao.setSelected(Selection(current, group, node))
        externalCandidates.remove(key)
    }

    private fun selectionKey(profileId: String, group: String): String {
        return "$profileId::$group"
    }

    private fun queryRememberedSelection(profileId: java.util.UUID, group: String): String? {
        return SelectionDao.querySelections(profileId)
            .firstOrNull { it.proxy == group }
            ?.selected
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun configuredProxyMode(): ProxyMode {
        val raw =
            networkSettings.decodeString("proxyMode", ProxyMode.Tun.name) ?: ProxyMode.Tun.name
        return runCatching { ProxyMode.valueOf(raw) }.getOrDefault(ProxyMode.Tun)
    }

    override fun setLogObserver(observer: ILogObserver?) {
        synchronized(this) {
            clearLogObserverLocked()

            if (observer == null) {
                return
            }

            if (!StatusProvider.serviceRunning) {
                Log.w("Ignore setLogObserver because runtime is not running")
                return
            }

            val channel =
                runCatching { Clash.subscribeLogcat() }
                    .onFailure { error -> Log.w("Subscribe runtime log stream failed", error) }
                    .getOrNull() ?: return

            logReceiver = channel
            logObserverJob =
                managerScope.launch {
                    try {
                        while (isActive) {
                            observer.newItem(channel.receive())
                        }
                    } catch (e: CancellationException) {
                        // No-op, lifecycle cancellation.
                    } catch (t: Throwable) {
                        Log.w("Runtime log observer stopped", t)
                    } finally {
                        withContext(NonCancellable) {
                            runCatching { channel.cancel() }
                            runCatching { Clash.forceGc() }
                        }
                    }
                }
        }
    }

    override fun close() {
        synchronized(this) { clearLogObserverLocked() }
        managerScope.cancel()
    }

    private fun clearLogObserverLocked() {
        logObserverJob?.cancel()
        logObserverJob = null

        logReceiver?.apply {
            cancel()
            Clash.forceGc()
        }
        logReceiver = null
    }
}

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



package com.github.yumelira.yumebox.runtime.client.root

import android.content.Context
import android.content.Intent
import com.github.yumelira.yumebox.core.model.*
import com.github.yumelira.yumebox.remote.RuntimeGatewayErrorCode
import com.github.yumelira.yumebox.remote.RuntimeGatewayException
import com.github.yumelira.yumebox.service.RootTunService
import com.github.yumelira.yumebox.service.common.util.appContextOrSelf
import com.github.yumelira.yumebox.service.root.*
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

object RootTunController {
    suspend fun start(context: Context): RootTunOperationResult {
        val appContext = context.appContextOrSelf
        val startupLogStore = RootTunStartupLogStore(appContext)
        val stateStore = RootTunStateStore(appContext)
        val startAt = System.currentTimeMillis()

        val request = RootTunStartRequest(source = "controller.start")
        startupLogStore.append("ROOT_TUN controller: prepare=${System.currentTimeMillis() - startAt}ms")
        val foregroundStartAt = System.currentTimeMillis()
        val current = stateStore.snapshot()
        stateStore.updateStatus(
            current.copy(
                state = RootTunState.Starting,
                running = true,
                runtimeReady = false,
                controllerReady = true,
                startedAt = startAt,
                lastError = null,
            ),
        )
        RootTunService.start(appContext)
        startupLogStore.append("ROOT_TUN controller: fgService=${System.currentTimeMillis() - foregroundStartAt}ms")
        return start(appContext, request, stateStore, startupLogStore, startAt)
    }

    private suspend fun start(
        context: Context,
        request: RootTunStartRequest,
        stateStore: RootTunStateStore,
        startupLogStore: RootTunStartupLogStore,
        startedAt: Long,
    ): RootTunOperationResult {
        val remoteStartAt = System.currentTimeMillis()
        val result = runCatching {
            RootTunRemoteClient.remoteCall(context) { service ->
                val resultJson = service.startRootTun(RootTunJson.encode(request))
                RootTunJson.decode<RootTunOperationResult>(resultJson)
            }
        }.getOrElse { error ->
            startupLogStore.append("ROOT_TUN controller: total=${System.currentTimeMillis() - startedAt}ms failed=${error.message}")
            return error.toRootTunOperationResult(
                fallbackCode = RuntimeGatewayErrorCode.ROOT_TUN_START_FAILED,
                fallbackMessage = "RootTun start failed",
            )
        }
        startupLogStore.append("ROOT_TUN controller: remoteStart=${System.currentTimeMillis() - remoteStartAt}ms")
        if (!result.success) {
            startupLogStore.append("ROOT_TUN controller: total=${System.currentTimeMillis() - startedAt}ms")
            return result
        }

        return try {
            runCatching { stateStore.updateStatus(queryStatus(context)) }
            startupLogStore.append("ROOT_TUN controller: total=${System.currentTimeMillis() - startedAt}ms")
            result
        } catch (error: Throwable) {
            val rollbackResult = withContext(Dispatchers.IO) {
                runCatching {
                    val resultJson = RootTunRemoteClient.bind(context).stopRootTun()
                    RootTunJson.decode<RootTunOperationResult>(resultJson)
                }.getOrNull()
            }
            val appContext = context.appContextOrSelf
            runCatching { RootTunService.stop(appContext) }
            runCatching { RootService.stop(createIntent(appContext)) }
            RootTunRemoteClient.disconnect()

            val message = buildString {
                append(error.message ?: "failed to start RootTun foreground service")
                rollbackResult?.error
                    ?.takeIf { it.isNotBlank() }
                    ?.let { append(" | rollback: ").append(it) }
            }
            startupLogStore.append("ROOT_TUN controller: total=${System.currentTimeMillis() - startedAt}ms failed=$message")
            RootTunOperationResult(
                success = false,
                errorCode = RuntimeGatewayErrorCode.ROOT_TUN_CONFIG_ROLLBACK_FAILED,
                error = message,
            )
        }
    }

    suspend fun reload(context: Context): RootTunOperationResult {
        if (!RootTunRemoteClient.isRuntimeActive(context)) {
            return RootTunOperationResult(success = true)
        }

        val appContext = context.appContextOrSelf
        val startupLogStore = RootTunStartupLogStore(appContext)
        val stateStore = RootTunStateStore(appContext)
        val currentStatus = stateStore.snapshot()
        val request = RootTunStartRequest(source = "controller.reload")
        startupLogStore.append("ROOT_TUN controller: reload request currentTransport=${currentStatus.transportFingerprint}")

        val result = runCatching {
            RootTunRemoteClient.remoteCall(appContext) { service ->
                startupLogStore.append("ROOT_TUN controller: reload branch=service")
                val resultJson = service.reloadActiveProfile(
                    RootTunJson.encode(request),
                )
                RootTunJson.decode<RootTunOperationResult>(resultJson)
            }
        }.getOrElse { error ->
            return error.toRootTunOperationResult(
                fallbackCode = RuntimeGatewayErrorCode.ROOT_TUN_RELOAD_FAILED,
                fallbackMessage = "RootTun reload failed",
            )
        }
        if (result.success) {
            runCatching { stateStore.updateStatus(queryStatus(appContext)) }
        }
        return result
    }

    suspend fun stop(context: Context): RootTunOperationResult {
        if (!RootTunRemoteClient.isRuntimeActive(context)) {
            return RootTunOperationResult(success = true)
        }

        val result = RootTunRemoteClient.remoteCall(
            context = context,
            onBinderFailure = { RootTunOperationResult(success = true) },
        ) { service ->
            val resultJson = service.stopRootTun()
            RootTunJson.decode<RootTunOperationResult>(resultJson)
        }
        RootTunRemoteClient.disconnect()
        return result
    }

    suspend fun requestStop(context: Context) {
        if (!RootTunRemoteClient.isRuntimeActive(context)) return
        RootTunRemoteClient.remoteCall(context = context, onBinderFailure = { Unit }) { service ->
            service.requestStop()
        }
        RootTunRemoteClient.disconnect()
    }

    suspend fun queryStatus(context: Context): RootTunStatus {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<RootTunStatus>(service.queryStatus())
        }
    }

    suspend fun queryTunnelState(context: Context): TunnelState {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<TunnelState>(service.queryTunnelStateJson())
        }
    }

    suspend fun queryTrafficNow(context: Context): Long {
        return RootTunRemoteClient.remoteCall(context) { service -> service.queryTrafficNow() }
    }

    suspend fun queryTrafficTotal(context: Context): Long {
        return RootTunRemoteClient.remoteCall(context) { service -> service.queryTrafficTotal() }
    }

    suspend fun queryConnections(context: Context): ConnectionSnapshot {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<ConnectionSnapshot>(service.queryConnectionsJson())
        }
    }

    suspend fun queryProxyGroupNames(context: Context, excludeNotSelectable: Boolean): List<String> {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<List<String>>(service.queryProxyGroupNamesJson(excludeNotSelectable))
        }
    }

    suspend fun queryAllProxyGroups(context: Context, excludeNotSelectable: Boolean): List<ProxyGroup> {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<List<ProxyGroup>>(service.queryAllProxyGroupsJson(excludeNotSelectable))
        }
    }

    suspend fun queryProxyGroup(context: Context, name: String, sort: ProxySort): ProxyGroup {
        return RootTunRemoteClient.remoteCall(context) { service ->
            val raw = service.queryProxyGroupJson(name, sort.name)
                ?: error("proxy group not found: $name")
            RootTunJson.decode<ProxyGroup>(raw)
        }
    }

    suspend fun queryConfiguration(context: Context): UiConfiguration {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<UiConfiguration>(service.queryConfigurationJson())
        }
    }

    suspend fun queryProviders(context: Context): List<Provider> {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<List<Provider>>(service.queryProvidersJson())
        }
    }

    suspend fun patchSelector(context: Context, group: String, name: String): Boolean {
        return RootTunRemoteClient.remoteCall(context) { service -> service.patchSelector(group, name) }
    }

    suspend fun closeConnection(context: Context, id: String): Boolean {
        return RootTunRemoteClient.remoteCall(context) { service -> service.closeConnection(id) }
    }

    suspend fun closeAllConnections(context: Context) {
        RootTunRemoteClient.remoteCall(context) { service -> service.closeAllConnections() }
    }

    suspend fun healthCheck(context: Context, group: String) {
        val error = RootTunRemoteClient.remoteCall(context) { service -> service.healthCheck(group) }
        if (!error.isNullOrBlank()) {
            error(error)
        }
    }

    suspend fun healthCheckProxy(context: Context, proxyName: String): String {
        return RootTunRemoteClient.remoteCall(context) { service -> service.healthCheckProxy(proxyName) }
    }

    suspend fun updateProvider(context: Context, type: Provider.Type, name: String) {
        val error = RootTunRemoteClient.remoteCall(context) { service -> service.updateProvider(type.name, name) }
        if (!error.isNullOrBlank()) {
            error(error)
        }
    }

    suspend fun queryRecentLogs(context: Context, sinceSeq: Long): RootTunLogChunk {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<RootTunLogChunk>(service.queryRecentLogsJson(sinceSeq))
        }
    }

    private fun createIntent(context: Context): Intent {
        return Intent(context, RootTunRootService::class.java)
    }

    private fun Throwable.toRootTunOperationResult(
        fallbackCode: RuntimeGatewayErrorCode,
        fallbackMessage: String,
    ): RootTunOperationResult {
        val gateway = this as? RuntimeGatewayException
        val message = gateway?.message?.takeIf { it.isNotBlank() }
            ?: message?.takeIf { it.isNotBlank() }
            ?: fallbackMessage
        return RootTunOperationResult(
            success = false,
            errorCode = gateway?.code ?: fallbackCode,
            error = message,
        )
    }
}

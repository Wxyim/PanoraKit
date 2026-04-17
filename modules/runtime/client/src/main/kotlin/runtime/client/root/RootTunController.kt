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
 *
 */

package com.github.nomadboxlab.monadbox.runtime.client.root

import android.content.Context
import com.github.nomadboxlab.monadbox.core.model.*
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayException
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.root.*

object RootTunController {
    suspend fun start(context: Context): RootTunOperationResult {
        return RootTunStartCoordinator.start(
            context = context.appContextOrSelf,
            request = RootTunStartRequest(source = "controller.start"),
            callerTag = "controller",
            fallbackCode =
                com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
                    .ROOT_TUN_START_FAILED,
            fallbackMessage = "RootTun start failed",
        )
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
        startupLogStore.append(
            "ROOT_TUN controller: reload request currentTransport=${currentStatus.transportFingerprint}"
        )

        val result =
            runCatching {
                    RootTunRemoteClient.remoteCall(appContext) { service ->
                        startupLogStore.append("ROOT_TUN controller: reload branch=service")
                        val resultJson = service.reloadActiveProfile(RootTunJson.encode(request))
                        RootTunJson.decode<RootTunOperationResult>(resultJson)
                    }
                }
                .getOrElse { error ->
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

        val result =
            RootTunRemoteClient.remoteCall(
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

    suspend fun queryProxyGroupNames(
        context: Context,
        excludeNotSelectable: Boolean,
    ): List<String> {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<List<String>>(service.queryProxyGroupNamesJson(excludeNotSelectable))
        }
    }

    suspend fun queryAllProxyGroups(
        context: Context,
        excludeNotSelectable: Boolean,
    ): List<ProxyGroup> {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<List<ProxyGroup>>(
                service.queryAllProxyGroupsJson(excludeNotSelectable)
            )
        }
    }

    suspend fun queryProxyGroup(context: Context, name: String, sort: ProxySort): ProxyGroup {
        return RootTunRemoteClient.remoteCall(context) { service ->
            val raw =
                service.queryProxyGroupJson(name, sort.name)
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
        return RootTunRemoteClient.remoteCall(context) { service ->
            service.patchSelector(group, name)
        }
    }

    suspend fun closeConnection(context: Context, id: String): Boolean {
        return RootTunRemoteClient.remoteCall(context) { service -> service.closeConnection(id) }
    }

    suspend fun closeAllConnections(context: Context) {
        RootTunRemoteClient.remoteCall(context) { service -> service.closeAllConnections() }
    }

    suspend fun healthCheck(context: Context, group: String) {
        val error =
            RootTunRemoteClient.remoteCall(context) { service -> service.healthCheck(group) }
        if (!error.isNullOrBlank()) {
            error(error)
        }
    }

    suspend fun healthCheckProxy(context: Context, proxyName: String): String {
        return RootTunRemoteClient.remoteCall(context) { service ->
            service.healthCheckProxy(proxyName)
        }
    }

    suspend fun updateProvider(context: Context, type: Provider.Type, name: String) {
        val error =
            RootTunRemoteClient.remoteCall(context) { service ->
                service.updateProvider(type.name, name)
            }
        if (!error.isNullOrBlank()) {
            error(error)
        }
    }

    suspend fun queryRecentLogs(context: Context, sinceSeq: Long): RootTunLogChunk {
        return RootTunRemoteClient.remoteCall(context) { service ->
            RootTunJson.decode<RootTunLogChunk>(service.queryRecentLogsJson(sinceSeq))
        }
    }

    private fun Throwable.toRootTunOperationResult(
        fallbackCode: RuntimeGatewayErrorCode,
        fallbackMessage: String,
    ): RootTunOperationResult {
        val gateway = this as? RuntimeGatewayException
        val message =
            gateway?.message?.takeIf { it.isNotBlank() }
                ?: message?.takeIf { it.isNotBlank() }
                ?: fallbackMessage
        return RootTunOperationResult(
            success = false,
            errorCode = gateway?.code ?: fallbackCode,
            error = message,
        )
    }
}

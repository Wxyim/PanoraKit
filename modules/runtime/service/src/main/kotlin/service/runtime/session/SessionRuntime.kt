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

import android.os.SystemClock
import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.controller.MihomoControllerEndpoint

import com.github.nomadboxlab.monadbox.core.model.*
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayErrorCode
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayException
import com.github.nomadboxlab.monadbox.service.ServiceNetworkObserver
import com.github.nomadboxlab.monadbox.service.common.util.appContextOrSelf
import com.github.nomadboxlab.monadbox.service.runtime.records.SelectionDao
import com.github.nomadboxlab.monadbox.service.runtime.records.SelectionRestoreExecutor
import com.github.nomadboxlab.monadbox.service.runtime.records.SelectionRestoreScope
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeOwner
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
import com.github.nomadboxlab.monadbox.service.runtime.util.runSuspendBlocking
import java.io.File
import java.security.MessageDigest
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayDeque
import kotlin.math.min
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.builtins.serializer
import timber.log.Timber

class SessionRuntime(
    private val host: RuntimeHost,
    private val transport: RuntimeTransport,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val compiledConfigPipeline = CompiledConfigPipeline(host.context.appContextOrSelf)
    private val operationMutex = Mutex()
    private var currentSpec: RuntimeSpec? = null
    private var currentSnapshot: RuntimeSnapshot = RuntimeSnapshot(targetMode = host.mode)
    private var lastCompiledFingerprint: String? = null
    private var networkObserver: ServiceNetworkObserver? = null
    private var installedAppsPublisher: RuntimeInstalledAppsPublisher? = null
    private var logJob: Job? = null

    private var runtimeSnapshot: RuntimeQuerySnapshot = RuntimeQuerySnapshot()
    private val logSeq = AtomicLong(0L)
    private val recentLogs = ArrayDeque<Pair<Long, String>>()
    private val recentLogsLock = Any()
    private var localLogObserver: ((LogMessage) -> Unit)? = null

    suspend fun start(spec: RuntimeSpec): RuntimeOperationResult {
        return withContext(Dispatchers.Default) {
            operationMutex.withLock {
                runCatching {
                        stopInternal(reason = null, notifyHost = false)
                        startupLog(spec, "session: start begin")
                        startInternal(spec)
                        RuntimeOperationResult.ok()
                    }
                    .getOrElse { error ->
                        val failure =
                            error.toRuntimeFailure(
                                fallbackCode = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                                fallbackMessage = "start runtime failed",
                            )
                        Timber.e(
                            error,
                            "SessionRuntime start failed: %s %s",
                            failure.code,
                            failure.message,
                        )
                        startupLog(spec, "failed=${failure.code.name}:${failure.message}")
                        rollback(spec, failure)
                        RuntimeOperationResult.fail(failure)
                    }
            }
        }
    }

    suspend fun reload(spec: RuntimeSpec): RuntimeOperationResult {
        return withContext(Dispatchers.Default) {
            operationMutex.withLock {
                runCatching {
                        startupLog(spec, "session: reload begin")
                        reloadInternal(spec)
                        RuntimeOperationResult.ok()
                    }
                    .getOrElse { error ->
                        val failure =
                            error.toRuntimeFailure(
                                fallbackCode = RuntimeGatewayErrorCode.RUNTIME_RELOAD_FAILED,
                                fallbackMessage = "reload runtime failed",
                            )
                        Timber.e(
                            error,
                            "SessionRuntime reload failed: %s %s",
                            failure.code,
                            failure.message,
                        )
                        startupLog(spec, "failed=${failure.message}")
                        RuntimeOperationResult.fail(failure)
                    }
            }
        }
    }

    suspend fun restart(spec: RuntimeSpec): RuntimeOperationResult {
        return withContext(Dispatchers.Default) {
            operationMutex.withLock {
                runCatching {
                        stopInternal(reason = null, notifyHost = false)
                        startupLog(spec, "session: restart begin")
                        startInternal(spec)
                        RuntimeOperationResult.ok()
                    }
                    .getOrElse { error ->
                        val failure =
                            error.toRuntimeFailure(
                                fallbackCode = RuntimeGatewayErrorCode.RUNTIME_RESTART_FAILED,
                                fallbackMessage = "restart runtime failed",
                            )
                        Timber.e(
                            error,
                            "SessionRuntime restart failed: %s %s",
                            failure.code,
                            failure.message,
                        )
                        startupLog(spec, "failed=${failure.code.name}:${failure.message}")
                        rollback(spec, failure)
                        RuntimeOperationResult.fail(failure)
                    }
            }
        }
    }

    fun stop(reason: String? = null): RuntimeOperationResult {
        return runSuspendBlocking {
            operationMutex.withLock {
                runCatching {
                        stopInternal(reason = reason, notifyHost = true)
                        RuntimeOperationResult.ok()
                    }
                    .getOrElse { error ->
                        RuntimeOperationResult.fail(
                            error.toRuntimeFailure(
                                fallbackCode = RuntimeGatewayErrorCode.RUNTIME_STOP_FAILED,
                                fallbackMessage = "stop runtime failed",
                            )
                        )
                    }
            }
        }
    }

    fun destroy() {
        runCatching {
            runSuspendBlocking {
                operationMutex.withLock {
                    stopInternal(reason = "runtime destroyed", notifyHost = false)
                }
            }
        }
        scope.cancel()
    }

    fun snapshot(): RuntimeSnapshot = currentSnapshot

    fun queryTunnelState(): TunnelState {
        return if (currentSnapshot.phase == RuntimePhase.Running) Clash.queryTunnelState()
        else TunnelState(TunnelState.Mode.Rule)
    }

    fun queryTrafficNow(): Long {
        return if (currentSnapshot.phase == RuntimePhase.Running) {
            Clash.queryTrafficNow().also {
                runtimeSnapshot = runtimeSnapshot.copy(trafficNow = it)
                publishSnapshot(currentSnapshot.copy(trafficReady = true))
            }
        } else {
            0L
        }
    }

    fun queryTrafficTotal(): Long {
        return if (currentSnapshot.phase == RuntimePhase.Running) {
            Clash.queryTrafficTotal().also {
                runtimeSnapshot = runtimeSnapshot.copy(trafficTotal = it)
                publishSnapshot(currentSnapshot.copy(trafficReady = true))
            }
        } else {
            0L
        }
    }

    fun queryConnections(): ConnectionSnapshot {
        if (currentSnapshot.phase != RuntimePhase.Running) return ConnectionSnapshot()
        return Clash.queryConnections()
    }

    fun queryAllProxyGroups(excludeNotSelectable: Boolean): List<ProxyGroup> {
        if (currentSnapshot.phase != RuntimePhase.Running) return emptyList()
        val groups =
            runCatching {
                    Clash.queryGroupNames(excludeNotSelectable).map {
                        Clash.queryGroup(it, ProxySort.Default)
                    }
                }
                .getOrElse {
                    if (excludeNotSelectable) {
                        val selectable = Clash.queryGroupNames(true).toSet()
                        ensureRuntimeSnapshot().proxyGroups.filter { selectable.contains(it.name) }
                    } else {
                        ensureRuntimeSnapshot().proxyGroups
                    }
                }
        runtimeSnapshot = runtimeSnapshot.copy(proxyGroups = groups)
        publishSnapshot(currentSnapshot.copy(groupsReady = groups.isNotEmpty()))
        return groups
    }

    fun queryProxyGroupNames(excludeNotSelectable: Boolean): List<String> {
        if (currentSnapshot.phase != RuntimePhase.Running) return emptyList()
        return runCatching { Clash.queryGroupNames(excludeNotSelectable) }
            .getOrElse { queryAllProxyGroups(excludeNotSelectable).map { it.name } }
    }

    fun queryProxyGroup(name: String, proxySort: ProxySort): ProxyGroup {
        if (currentSnapshot.phase != RuntimePhase.Running) {
            error("runtime not running")
        }
        val group = Clash.queryGroup(name, proxySort)
        if (proxySort == ProxySort.Default && group.name.isNotBlank()) {
            val groups = runtimeSnapshot.proxyGroups
            runtimeSnapshot =
                runtimeSnapshot.copy(
                    proxyGroups =
                        if (groups.any { it.name == name }) {
                            groups.map { if (it.name == name) group else it }
                        } else {
                            groups + group
                        }
                )
        }
        return group
    }

    fun queryConfiguration(): UiConfiguration {
        if (currentSnapshot.phase != RuntimePhase.Running) return UiConfiguration()
        return ensureRuntimeSnapshot().configuration
    }

    fun queryProviders(): List<Provider> {
        if (currentSnapshot.phase != RuntimePhase.Running) return emptyList()
        return ensureRuntimeSnapshot().providers
    }

    fun patchSelector(group: String, name: String): Boolean {
        return Clash.patchSelector(group, name).also { patched ->
            if (patched && currentSnapshot.phase == RuntimePhase.Running) {
                refreshRuntimeSnapshot()
            }
        }
    }

    fun closeConnection(id: String): Boolean {
        if (currentSnapshot.phase != RuntimePhase.Running) return false
        return Clash.closeConnection(id)
    }

    fun closeAllConnections() {
        if (currentSnapshot.phase != RuntimePhase.Running) return
        Clash.closeAllConnections()
    }

    suspend fun healthCheck(group: String): String? {
        Timber.d(
            "SessionRuntime healthCheck: group=%s phase=%s owner=%s",
            group,
            currentSnapshot.phase,
            currentSnapshot.owner,
        )
        return runCatching {
                val request = Clash.healthCheck(group)
                scope.launch(Dispatchers.IO) {
                    runCatching {
                            request.await()
                            refreshRuntimeSnapshot()
                        }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            Timber.w(
                                error,
                                "SessionRuntime healthCheck async failed: group=%s phase=%s owner=%s",
                                group,
                                currentSnapshot.phase,
                                currentSnapshot.owner,
                            )
                        }
                }
                null
            }
            .getOrElse { it.message ?: "health check failed" }
    }

    suspend fun healthCheckProxy(proxyName: String): String {
        Timber.d(
            "SessionRuntime healthCheckProxy: proxy=%s phase=%s owner=%s",
            proxyName,
            currentSnapshot.phase,
            currentSnapshot.owner,
        )
        return runCatching {
                withContext(Dispatchers.IO) { Clash.healthCheckProxy(proxyName).await() }
                    .also { refreshRuntimeSnapshot() }
            }
            .getOrElse {
                """{"delay":-1,"error":${com.github.nomadboxlab.monadbox.service.root.RootTunJson.Default.encodeToString(String.serializer(), it.message ?: "health check proxy failed")}}"""
            }
    }

    suspend fun updateProvider(type: String, name: String): String? {
        val providerType =
            runCatching { Provider.Type.valueOf(type) }
                .getOrElse {
                    return "invalid provider type: $type"
                }
        return runCatching {
                withContext(Dispatchers.IO) { Clash.updateProvider(providerType, name).await() }
                refreshRuntimeSnapshot()
                null
            }
            .getOrElse { it.message ?: "update provider failed" }
    }

    fun setLogObserver(observer: ((LogMessage) -> Unit)?) {
        localLogObserver = observer
    }

    fun queryRecentLogsJson(sinceSeq: Long): RuntimeLogChunk {
        synchronized(recentLogsLock) {
            val items = recentLogs.filter { it.first > sinceSeq }.map { it.second }
            return RuntimeLogChunk(nextSeq = logSeq.get(), items = items)
        }
    }

    private suspend fun startInternal(spec: RuntimeSpec) {
        validateStartupSpec(spec)
        val startedAt = System.currentTimeMillis()
        val wasIdle = currentSnapshot.phase == RuntimePhase.Idle
        currentSpec = spec
        publishSnapshot(
            RuntimeSnapshot(
                owner = spec.owner,
                phase = RuntimePhase.Starting,
                targetMode = host.mode,
                profileUuid = spec.profileUuid,
                profileName = spec.profileName,
                profileReady = true,
                startedAt = startedAt,
                effectiveFingerprint = spec.effectiveFingerprint,
            )
        )
        host.onStarting(spec)

        // Cold start: Go runtime is fresh from coreInit — no listeners or
        // connections to tear down. Skipping reset() + hub.ApplyConfig(empty)
        // saves ~200-500ms on first start.
        if (!wasIdle) {
            teardownCore()
        }
        // VPN establish (Android IPC) and config compilation (Go JNI) are
        // independent — overlap them to reduce total wall-clock time.
        coroutineScope {
            val prepareJob =
                async {
                    measureStartupStep(spec, "transport prepare") { transport.prepare(spec) }
                }
            val compileJob =
                async {
                    measureStartupStep(spec, "runtime compile/load") { compileAndLoad(spec) }
                }
            prepareJob.await()
            compileJob.await()
        }
        // App→UID mappings must be populated before transport.start() so Go's
        // QueryAppByUid resolves package names from the first packet. Kept
        // sequential after compileAndLoad to avoid concurrent JNI into Go.
        measureStartupStep(spec, "app mapping publish") { startInstalledAppsPublisher() }
        measureStartupStep(spec, "transport start") { transport.start(spec) }
        startupLog(spec, "runtime ready: awaiting proxy groups and selection restore")

        // Wait for proxy groups to be queryable, selections validated, AND
        // Android's VPN routing table to stabilise — all before signaling
        // Running so the node label and IP button are ready the moment the
        // UI transitions.
        //
        // ensureSelectionOverrideFile already injected the user's saved
        // selections into the compiled runtime.yaml as defaults, so
        // restoreSelections is a validation pass (no visual flash).
        //
        coroutineScope {
            launch {
                measureStartupStep(spec, "runtime selection restore") {
                    restoreSelections(spec)
                }
            }
            launch {
                measureStartupStep(spec, "runtime groups ready") {
                    awaitProxyGroupsReady(spec)
                }
            }
        }

        startObservers()
        notifyRuntimeSideEffects()
        measureStartupStep(spec, "runtime log stream") { startLogStream() }
        measureStartupStep(spec, "runtime snapshot refresh") { refreshRuntimeSnapshotWithLog(spec) }

        publishSnapshot(
            currentSnapshot.copy(
                phase = RuntimePhase.Running,
                profileReady = true,
                groupsReady = runtimeSnapshot.proxyGroups.isNotEmpty(),
                trafficReady = true,
                configReady = true,
                transportReady = true,
                logReady = logJob?.isActive == true,
                startedAt = startedAt,
                effectiveFingerprint = spec.effectiveFingerprint,
            )
        )
        host.onStarted(spec)
        host.onProfileLoaded(spec.profileUuid)
        startupLog(spec, "payload ready")
    }

    private suspend fun reloadInternal(spec: RuntimeSpec) {
        check(currentSpec != null) { "runtime not started" }
        publishSnapshot(
            currentSnapshot.copy(
                phase = RuntimePhase.Starting,
                profileUuid = spec.profileUuid,
                profileName = spec.profileName,
                effectiveFingerprint = spec.effectiveFingerprint,
                groupsReady = false,
                trafficReady = false,
            )
        )

        compileAndLoad(spec)
        notifyRuntimeSideEffects()
        awaitProxyGroupsReady(spec)
        restoreSelections(spec)
        currentSpec = spec
        refreshRuntimeSnapshotWithLog(spec)
        publishSnapshot(
            currentSnapshot.copy(
                phase = RuntimePhase.Running,
                profileReady = true,
                groupsReady = runtimeSnapshot.proxyGroups.isNotEmpty(),
                trafficReady = true,
                configReady = true,
                transportReady = true,
                logReady = logJob?.isActive == true,
                effectiveFingerprint = spec.effectiveFingerprint,
                lastError = null,
            )
        )
        host.onProfileLoaded(spec.profileUuid)
        startupLog(spec, "reload done")
    }

    private fun refreshRuntimeSnapshotWithLog(spec: RuntimeSpec) {
        startupLog(spec, "snapshot refresh: begin")
        refreshRuntimeSnapshot()
        startupLog(spec, "snapshot refresh: done")
    }

    private fun stopInternal(reason: String?, notifyHost: Boolean) {
        if (currentSnapshot.phase == RuntimePhase.Idle && currentSpec == null) {
            return
        }

        publishSnapshot(
            currentSnapshot.copy(
                phase =
                    if (currentSnapshot.phase == RuntimePhase.Idle) RuntimePhase.Idle
                    else RuntimePhase.Stopping,
                transportReady = false,
                groupsReady = false,
                trafficReady = false,
                configReady = false,
                logReady = false,
                lastError = reason,
            )
        )
        stopLogStream()
        stopInstalledAppsPublisher()
        stopObservers()
        runCatching { transport.stop() }
        teardownCore()
        currentSpec = null
        runtimeSnapshot = RuntimeQuerySnapshot()
        publishSnapshot(
            RuntimeSnapshot(
                owner = RuntimeOwner.None,
                phase = if (reason.isNullOrBlank()) RuntimePhase.Idle else RuntimePhase.Failed,
                targetMode = host.mode,
                lastError = reason,
            )
        )
        if (notifyHost) {
            host.onStopped(reason)
        }
    }

    private fun rollback(spec: RuntimeSpec, failure: RuntimeFailure) {
        stopLogStream()
        stopInstalledAppsPublisher()
        stopObservers()
        runCatching { transport.stop() }
        teardownCore()
        currentSpec = null
        runtimeSnapshot = RuntimeQuerySnapshot()
        publishSnapshot(
            RuntimeSnapshot(
                owner = spec.owner,
                phase = RuntimePhase.Failed,
                targetMode = host.mode,
                profileUuid = spec.profileUuid,
                profileName = spec.profileName,
                profileReady = false,
                lastError = failure.message,
                effectiveFingerprint = spec.effectiveFingerprint,
            )
        )
        startupLog(spec, "failed=${failure.message}")
        host.reportFailure(failure)
    }

    private suspend fun compileAndLoad(spec: RuntimeSpec) {
        // Inject persisted proxy group selections into the runtime internal
        // override file before compilation so that the selected nodes become
        // the defaults when mihomo starts. This eliminates the visual flash
        // where the UI briefly shows the config default before the async
        // selection restore takes effect.
        compiledConfigPipeline.ensureSelectionOverrideFile(
            spec.profileUuid,
            spec.profileDir,
        )
        // Re-resolve override paths to include the newly created (or updated)
        // runtime internal override file in the compilation.
        val updatedOverridePaths =
            compiledConfigPipeline.resolveOverridePaths(spec.profileUuid)
        val compiledSpec = spec.copy(overridePaths = updatedOverridePaths)

        val recompileFingerprint = buildRecompileFingerprint(compiledSpec)
        val runtimeFile = File(spec.runtimeConfigPath)
        val fingerprintFile = File("${spec.runtimeConfigPath}.fingerprint")

        if (runtimeFile.exists() && fingerprintFile.exists() &&
            fingerprintFile.readText().trim() == recompileFingerprint
        ) {
            // runtime.yaml matches current inputs — skip YAML merge, just load into Go
            startupLog(spec, "runtime override: skipped (output up-to-date)")
            startupLog(
                spec,
                "runtime load: loadCompiledConfig(${spec.runtimeConfigPath}) begin",
            )
            withContext(Dispatchers.IO) {
                Clash.loadCompiledConfig(runtimeFile).await()
            }
            startupLog(spec, "runtime load: loadCompiledConfig done")
            lastCompiledFingerprint = recompileFingerprint
            return
        }

        startupLog(
            spec,
            "runtime override: begin apply overrides -> runtime.yaml path=${spec.runtimeConfigPath}",
        )
        startupLog(
            spec,
            "runtime override: overridePaths=${compiledSpec.overridePaths.size} " +
                compiledSpec.overridePaths.joinToString(prefix = "[", postfix = "]"),
        )
        compiledConfigPipeline.applyOverrideToRuntimeFile(compiledSpec)
        startupLog(spec, "runtime override: done ${describeFile(File(spec.runtimeConfigPath))}")
        // Persist fingerprint sidecar so the next cold start can skip compilation
        fingerprintFile.writeText(recompileFingerprint)
        startupLog(spec, "runtime load: loadCompiledConfig(${spec.runtimeConfigPath}) begin")
        withContext(Dispatchers.IO) {
            Clash.loadCompiledConfig(runtimeFile).await()
        }
        startupLog(spec, "runtime load: loadCompiledConfig done")
        lastCompiledFingerprint = recompileFingerprint
        val runtimeConfiguration =
            runCatching { Clash.queryConfiguration() }.getOrDefault(UiConfiguration())
        startupLog(
            spec,
            "runtime load: uiConfiguration=${MihomoControllerEndpoint.diagnostics(runtimeConfiguration).summary()}",
        )
    }

    /** Fingerprint of all inputs to the YAML compilation step. */
    private fun buildRecompileFingerprint(spec: RuntimeSpec): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        digest.update(spec.profileUuid.toByteArray())
        digest.update(File(spec.profileDir).resolve("config.yaml").readBytes())
        spec.overridePaths.forEach { path ->
            digest.update(path.toByteArray())
            digest.update(File(path).readBytes())
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun validateStartupSpec(spec: RuntimeSpec) {
        ensureCoreAvailable()

        val profileDir = File(spec.profileDir)
        if (!profileDir.exists() || !profileDir.isDirectory) {
            throw RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
                message = "Profile directory missing or invalid: ${profileDir.absolutePath}",
            )
        }

        val profilePath = profileDir.resolve("config.yaml")
        if (!profilePath.exists() || !profilePath.isFile || !profilePath.canRead()) {
            throw RuntimeGatewayException(
                code = RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
                message = "Profile config missing or unreadable: ${profilePath.absolutePath}",
            )
        }

        spec.overridePaths.forEach { overridePath ->
            val file = File(overridePath)
            if (!file.exists() || !file.isFile || !file.canRead()) {
                throw RuntimeGatewayException(
                    code = RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
                    message = "Override file missing or unreadable: ${file.absolutePath}",
                )
            }
        }

        val runtimeConfigFile = File(spec.runtimeConfigPath)
        runtimeConfigFile.parentFile?.let { parent ->
            if (!parent.exists() && !parent.mkdirs()) {
                throw RuntimeGatewayException(
                    code = RuntimeGatewayErrorCode.RUNTIME_SPEC_BUILD_FAILED,
                    message = "Runtime config directory cannot be created: ${parent.absolutePath}",
                )
            }
        }
    }

    private fun ensureCoreAvailable() {
        runCatching { Clash.forceGc() }
            .getOrElse { error ->
                throw RuntimeGatewayException(
                    code = RuntimeGatewayErrorCode.RUNTIME_START_FAILED,
                    message = error.toDiagnosticMessage("Core bridge initialization failed"),
                    cause = error,
                )
            }
    }

    private suspend fun awaitProxyGroupsReady(spec: RuntimeSpec) {
        val expectedGroups = readExpectedGroupNames(spec)
        startupLog(
            spec,
            "runtime verify: expectedGroups=${expectedGroups.size}" +
                expectedGroups.takeIf { it.isNotEmpty() }?.let { " sample=${it.take(5)}" }.orEmpty(),
        )
        if (expectedGroups.isEmpty()) {
            return
        }

        repeat(PROXY_GROUP_READY_RETRY_COUNT) { attempt ->
            val names = runCatching { Clash.queryGroupNames(false) }.getOrDefault(emptyList())
            if (names.isNotEmpty()) {
                startupLog(
                    spec,
                    "runtime verify: actualGroups=${names.size} sample=${names.take(5)}",
                )
                return
            }
            if (attempt < PROXY_GROUP_READY_RETRY_COUNT - 1) {
                startupLog(spec, "runtime verify: actualGroups=0 retry=${attempt + 1}")
                delay(PROXY_GROUP_READY_RETRY_DELAY_MS)
            }
        }

        error(
            "runtime loaded but exposed 0 proxy groups; expected=${expectedGroups.size} " +
                "sample=${expectedGroups.take(min(5, expectedGroups.size))}"
        )
    }

    private fun readExpectedGroupNames(spec: RuntimeSpec): List<String> {
        val runtimeFile = File(spec.runtimeConfigPath)
        if (!runtimeFile.exists()) {
            startupLog(
                spec,
                "runtime verify: runtime.yaml missing path=${runtimeFile.absolutePath}",
            )
            return emptyList()
        }
        val yamlText = runtimeFile.readText()
        if (yamlText.isBlank()) {
            startupLog(spec, "runtime verify: runtime.yaml empty")
            return emptyList()
        }
        return runCatching {
                Clash.inspectCompiledGroups(
                        yamlText,
                        File(spec.profileDir),
                        excludeNotSelectable = false,
                    )
                    .map { it.name }
                    .filter { it.isNotBlank() }
            }
            .getOrElse { error ->
                startupLog(spec, "runtime verify: inspect failed=${error.message}")
                emptyList()
            }
    }

    private suspend fun restoreSelections(spec: RuntimeSpec) {
        val profileUuid = UUID.fromString(spec.profileUuid)
        val scopeKey =
            when (spec.owner) {
                RuntimeOwner.RootTun -> SelectionRestoreScope.rootScopeKey(spec.profileUuid)
                else -> SelectionRestoreScope.localScopeKey(profileUuid)
            }
        val restoreResult =
            SelectionDao.querySelectionsForRestore(
                profileUUID = profileUuid,
                currentScopeKey = scopeKey,
            )
        SelectionRestoreExecutor.restore(
            profileUuid = profileUuid,
            selections = restoreResult.selections,
            tag = spec.owner.name,
        )
    }

    private fun startObservers() {
        if (networkObserver == null) {
            networkObserver =
                ServiceNetworkObserver(host.context.appContextOrSelf) {
                        transport.onNetworkChanged()
                    }
                    .also { it.start() }
        }
    }

    private fun stopObservers() {
        runCatching { networkObserver?.stop() }
        networkObserver = null
    }

    private fun startInstalledAppsPublisher() {
        val publisher =
            installedAppsPublisher
                ?: RuntimeInstalledAppsPublisher(host.context, scope).also {
                    installedAppsPublisher = it
                }
        publisher.start()
    }

    private fun stopInstalledAppsPublisher() {
        installedAppsPublisher?.stop()
        installedAppsPublisher = null
    }

    private fun notifyRuntimeSideEffects() {
        notifyCurrentTimeZone()
    }

    private fun notifyCurrentTimeZone() {
        val timeZone = TimeZone.getDefault()
        Clash.notifyTimeZoneChanged(timeZone.id, timeZone.rawOffset / 1000)
    }

    private fun teardownCore() {
        runCatching { Clash.stopRootTun() }
        runCatching { Clash.stopTun() }
        runCatching { Clash.stopLocalProxyHttpListener() }
        runCatching { Clash.reset() }
    }

    private fun refreshRuntimeSnapshot() {
        if (
            currentSnapshot.phase != RuntimePhase.Running &&
                currentSnapshot.phase != RuntimePhase.Starting
        ) {
            runtimeSnapshot = RuntimeQuerySnapshot()
            return
        }

        val configuration =
            runCatching { Clash.queryConfiguration() }.getOrDefault(UiConfiguration())
        val providers = runCatching { Clash.queryProviders() }.getOrDefault(emptyList())
        val proxyGroups =
            runCatching {
                    Clash.queryGroupNames(false).map { Clash.queryGroup(it, ProxySort.Default) }
                }
                .getOrDefault(emptyList())
        val trafficNow = runCatching { Clash.queryTrafficNow() }.getOrDefault(0L)
        val trafficTotal = runCatching { Clash.queryTrafficTotal() }.getOrDefault(0L)
        runtimeSnapshot =
            RuntimeQuerySnapshot(
                configuration = configuration,
                providers = providers,
                proxyGroups = proxyGroups,
                trafficNow = trafficNow,
                trafficTotal = trafficTotal,
            )
    }

    private fun ensureRuntimeSnapshot(): RuntimeQuerySnapshot {
        if (runtimeSnapshot.proxyGroups.isNotEmpty()) {
            return runtimeSnapshot
        }
        refreshRuntimeSnapshot()
        return runtimeSnapshot
    }

    private fun startLogStream() {
        stopLogStream()
        host.onLogReady(false)
        logJob =
            scope.launch(Dispatchers.IO) {
                val receiver = Clash.subscribeLogcat()
                host.onLogReady(true)
                publishSnapshot(currentSnapshot.copy(logReady = true))
                try {
                    while (isActive) {
                        val item = receiver.receive()
                        localLogObserver?.invoke(item)
                        host.onLogItem(item)
                        val encoded =
                            com.github.nomadboxlab.monadbox.service.root.RootTunJson.Default
                                .encodeToString(LogMessage.serializer(), item)
                        val seq = logSeq.incrementAndGet()
                        synchronized(recentLogsLock) {
                            recentLogs.addLast(seq to encoded)
                            while (recentLogs.size > MAX_BUFFERED_LOGS) {
                                recentLogs.removeFirst()
                            }
                        }
                    }
                } finally {
                    receiver.cancel()
                    host.onLogReady(false)
                    publishSnapshot(currentSnapshot.copy(logReady = false))
                }
            }
    }

    private fun stopLogStream() {
        logJob?.cancel()
        logJob = null
        synchronized(recentLogsLock) { recentLogs.clear() }
        host.onLogReady(false)
    }

    private fun publishSnapshot(snapshot: RuntimeSnapshot) {
        val next = snapshot.copy(running = snapshot.phase.running)
        if (next == currentSnapshot) return
        currentSnapshot = next
        host.onSnapshotChanged(currentSnapshot)
    }

    private fun startupLog(spec: RuntimeSpec, message: String) {
        val scope =
            when (spec.owner) {
                RuntimeOwner.LocalTun -> RuntimeStartupLogStore.Scope.LOCAL_TUN
                RuntimeOwner.LocalHttp -> RuntimeStartupLogStore.Scope.LOCAL_HTTP
                RuntimeOwner.RootTun -> RuntimeStartupLogStore.Scope.ROOT_TUN
                RuntimeOwner.None -> return
            }
        RuntimeStartupLogStore(host.context.appContextOrSelf, scope)
            .append("${scope.tag} session: $message")
    }

    private suspend fun <T> measureStartupStep(
        spec: RuntimeSpec,
        label: String,
        block: suspend () -> T,
    ): T {
        val startedAt = SystemClock.elapsedRealtime()
        startupLog(spec, "$label: begin")
        return try {
            block()
        } finally {
            startupLog(spec, "$label: done cost=${SystemClock.elapsedRealtime() - startedAt}ms")
        }
    }

    private fun describeFile(file: File): String {
        if (!file.exists()) {
            return "path=${file.absolutePath} exists=false"
        }
        val content = file.readText()
        return buildString {
            append("path=")
            append(file.absolutePath)
            append(" exists=true size=")
            append(content.length)
            append(" sha=")
            append(content.sha256Short())
            content
                .lineSequence()
                .map(String::trim)
                .firstOrNull { it.isNotEmpty() }
                ?.let {
                    append(" firstLine=")
                    append(it.take(160))
                }
        }
    }

    private fun String.sha256Short(): String {
        if (isBlank()) return "empty"
        val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
        return digest.take(8).joinToString("") { "%02x".format(it) }
    }

    private data class RuntimeQuerySnapshot(
        val proxyGroups: List<ProxyGroup> = emptyList(),
        val configuration: UiConfiguration = UiConfiguration(),
        val providers: List<Provider> = emptyList(),
        val trafficNow: Long = 0L,
        val trafficTotal: Long = 0L,
    )

    private companion object {
        private const val MAX_BUFFERED_LOGS = 256
        private const val PROXY_GROUP_READY_RETRY_COUNT = 10
        private const val PROXY_GROUP_READY_RETRY_DELAY_MS = 200L
    }
}

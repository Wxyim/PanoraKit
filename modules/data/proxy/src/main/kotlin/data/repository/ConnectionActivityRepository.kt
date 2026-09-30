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

package com.github.nomadboxlab.monadbox.data.repository

import com.github.nomadboxlab.monadbox.core.domain.ConnectionHistoryManager
import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.remote.RuntimeGatewayException
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeConnectionReader
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

class ConnectionActivityRepository(
    private val runtimeStateReader: RuntimeStateReader,
    private val runtimeConnectionReader: RuntimeConnectionReader,
    private val scope: CoroutineScope,
) : ConnectionActivityProvider {
    companion object {
        /**
         * Snapshot connections every second while a screen is actually rendering the recent-request
         * list, so short-lived DNS / TCP handshake sockets are captured before they disappear from
         * the Go core.
         *
         * Polling only runs while the app is foregrounded with the screen on
         * ([RuntimeStateReader.isAppActive]); a backgrounded app has nothing that renders this
         * list, so it would otherwise pay a full connection-JSON query every second for no visible
         * result. The closes themselves are not lost by that: the Go core retains a close until a
         * poll has seen it (`recentClosedUndeliveredRetention` in
         * `lib/native/go/native/tunnel/conn.go`), so the first poll after the app comes back
         * replays what happened while it was away, each with its real close time. Only history
         * older than that bound is dropped.
         */
        private const val POLL_INTERVAL_MS = 1000L

        /**
         * Cadence used while nothing is subscribed to [activeConnections] / [closedConnections].
         *
         * The traffic-statistics screen is the only consumer that renders these lists, and it only
         * subscribes while it is on screen (roughly, plus the five-second grace of its
         * `WhileSubscribed`). While it is closed the poll exists purely to keep
         * [ConnectionHistoryManager]'s recent-request buffer seeded so opening the screen shows
         * history instead of an empty list.
         *
         * A 5x slower cadence cuts that warm-up cost accordingly without losing closes: the Go core
         * keeps closed connections in its snapshot for longer than this interval
         * (`recentClosedRetention` in `lib/native/go/native/tunnel/conn.go` has a 6s floor and then
         * follows the measured cadence), so every close still lands inside a poll window no matter
         * which cadence is running. Raising this value requires raising that floor too.
         */
        private const val IDLE_POLL_INTERVAL_MS = 5000L
    }

    /**
     * Number of live collectors of [activeConnections] / [closedConnections]. Both Flow properties
     * are consumed together by the same screen, so a plain count is enough to tell whether anything
     * can render the data right now. It is a true count (0, 1, 2, …) rather than a per-flow flag,
     * so one flow losing its collector while the other keeps one cannot be mistaken for "nothing is
     * watching".
     */
    private val subscriberCount = MutableStateFlow(0)

    private val _activeConnections = MutableStateFlow<List<ConnectionInfo>>(emptyList())
    override val activeConnections: StateFlow<List<ConnectionInfo>> =
        _activeConnections.countingCollectors(::onCollectorStarted, ::onCollectorStopped)

    private val _closedConnections =
        MutableStateFlow(ConnectionHistoryManager.getClosedConnections())
    override val closedConnections: StateFlow<List<ConnectionInfo>> =
        _closedConnections.countingCollectors(::onCollectorStarted, ::onCollectorStopped)

    private var monitorJob: Job? = null
    private var lastClosedRevision = ConnectionHistoryManager.closedConnectionsRevision()

    /**
     * Guard that ensures [ConnectionHistoryManager] is only cleared once per VPN session, not when
     * the runtime is already running and polling restarts.
     */
    private var needsSessionClear = true

    init {
        start()
    }

    /**
     * Start watching runtime state and poll connections while the VPN is running. Idempotent — safe
     * to call when already active.
     */
    fun start() {
        if (monitorJob?.isActive == true) return
        monitorJob =
            scope.launch {
                runtimeStateReader.isRuntimeRunning.collectLatest { isRunning ->
                    if (!isRunning) {
                        _activeConnections.value = emptyList()
                        _closedConnections.value = ConnectionHistoryManager.getClosedConnections()
                        lastClosedRevision = ConnectionHistoryManager.closedConnectionsRevision()
                        needsSessionClear = true
                        return@collectLatest
                    }

                    if (needsSessionClear) {
                        // Start a fresh recent-request session when runtime enters running state.
                        ConnectionHistoryManager.clear()
                        _closedConnections.value = emptyList()
                        lastClosedRevision = ConnectionHistoryManager.closedConnectionsRevision()
                        needsSessionClear = false
                    }

                    while (runtimeStateReader.isRuntimeRunning.value) {
                        if (!runtimeStateReader.isAppActive.value) {
                            // Nobody can see the recent-request list while the app is backgrounded
                            // or the screen is off, so stop querying the core. The outer
                            // collectLatest cancels this wait as soon as the runtime stops, and the
                            // first tick after the app returns re-seeds the history.
                            runtimeStateReader.isAppActive.first { it }
                        }
                        runCatching {
                                val connections = runtimeConnectionReader.queryConnections()
                                ConnectionHistoryManager.updateConnections(connections)
                                // Closed-flagged connections are retained briefly by the
                                // core so short-lived requests remain observable; keep them
                                // out of the active set so the UI renders them as closed.
                                _activeConnections.value = connections.filterNot { it.closed }
                                val revision = ConnectionHistoryManager.closedConnectionsRevision()
                                if (revision != lastClosedRevision) {
                                    val closed = ConnectionHistoryManager.getClosedConnections()
                                    // Only emit when the closed set actually changed
                                    // (avoids driving combine recomputation every 1000 ms).
                                    if (closedChanged(closed)) {
                                        _closedConnections.value = closed
                                    }
                                    lastClosedRevision = revision
                                }
                            }
                            .onFailure { error ->
                                if (error is CancellationException) throw error
                                if (error is RuntimeGatewayException) {
                                    Timber.d(
                                        "Connection polling skipped: ${error.runtimeGatewayMessage("runtime unavailable")}"
                                    )
                                } else {
                                    Timber.w(error, "Failed to refresh connection activity")
                                }
                            }
                        awaitNextPoll()
                    }
                }
            }
    }

    /**
     * Suspend until the next connection poll is due.
     *
     * While a consumer is rendering the lists this is a plain [POLL_INTERVAL_MS] delay. While
     * nothing is, it waits up to [IDLE_POLL_INTERVAL_MS] but wakes as soon as a collector attaches,
     * so the first frame after the traffic screen opens is not up to five seconds stale.
     */
    private suspend fun awaitNextPoll() {
        if (subscriberCount.value > 0) {
            delay(POLL_INTERVAL_MS)
            return
        }
        withTimeoutOrNull(IDLE_POLL_INTERVAL_MS) { subscriberCount.first { it > 0 } }
    }

    /** Re-publish the number of live collectors so [awaitNextPoll] can pick its cadence. */
    private fun onCollectorStarted() = subscriberCount.update { it + 1 }

    private fun onCollectorStopped() = subscriberCount.update { (it - 1).coerceAtLeast(0) }

    /** Returns true when [closed] differs from the currently-held snapshot by connection id set. */
    private fun closedChanged(closed: List<ConnectionInfo>): Boolean {
        val prev = _closedConnections.value
        if (prev.size != closed.size) return true
        val prevIds = prev.asSequence().map { it.id }.toSet()
        val closedIds = closed.asSequence().map { it.id }.toSet()
        return prevIds != closedIds
    }

    /** Stop connection polling. StateFlows retain their last-emitted values. */
    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
    }
}

/** [ObservedStateFlow] variant used to feed collector attach/detach events into the poll loop. */
private fun <T> StateFlow<T>.countingCollectors(
    onCollectorStarted: () -> Unit,
    onCollectorStopped: () -> Unit,
): StateFlow<T> = ObservedStateFlow(this, onCollectorStarted, onCollectorStopped)

/**
 * A [StateFlow] that reports how many collectors are attached.
 *
 * `StateFlow` has no built-in `subscriptionCount` (it is hot and replay-based), so the producer
 * cannot otherwise tell the difference between "a screen is rendering this list" and "this list is
 * only being kept warm for later". Counting here keeps that knowledge inside the repository instead
 * of leaking a visibility API onto [ConnectionActivityProvider].
 *
 * Implementing `StateFlow` itself is the point: it lets [value] stay the producer's own instance,
 * so callers that read the flow without collecting still see live updates (unlike a `stateIn`
 * wrapper, whose value only advances while the shared upstream is running).
 */
@OptIn(kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi::class)
private class ObservedStateFlow<T>(
    private val upstream: StateFlow<T>,
    private val onCollectorStarted: () -> Unit,
    private val onCollectorStopped: () -> Unit,
) : StateFlow<T> {
    override val value: T
        get() = upstream.value

    override val replayCache: List<T>
        get() = upstream.replayCache

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        onCollectorStarted()
        try {
            upstream.collect(collector)
        } finally {
            onCollectorStopped()
        }
    }
}

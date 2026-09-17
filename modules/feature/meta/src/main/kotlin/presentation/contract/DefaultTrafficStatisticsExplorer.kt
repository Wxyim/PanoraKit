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

package com.github.nomadboxlab.monadbox.feature.meta.presentation.contract

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.data.model.DailyTrafficSummary
import com.github.nomadboxlab.monadbox.data.model.TimeSlot
import com.github.nomadboxlab.monadbox.data.repository.ConnectionActivityRepository
import com.github.nomadboxlab.monadbox.data.store.TrafficStatisticsStore
import com.github.nomadboxlab.monadbox.domain.util.PollingTimerSpecs
import com.github.nomadboxlab.monadbox.domain.util.PollingTimers
import com.github.nomadboxlab.monadbox.feature.meta.api.RecentRequestRecord
import com.github.nomadboxlab.monadbox.feature.meta.api.TrafficChartPoint
import com.github.nomadboxlab.monadbox.feature.meta.api.TrafficStatisticsExplorer
import com.github.nomadboxlab.monadbox.feature.meta.api.TrafficStatisticsRange
import com.github.nomadboxlab.monadbox.runtime.client.AppIdentityResolver
import dev.oom_wg.purejoy.mlang.MLang
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

private data class StatisticsClockSnapshot(
    val dayKey: Long,
    val hourOfDay: Int,
    val minute: Int,
    val timeZoneId: String,
)

private val JsonElement.jsonPrimitiveOrNull: JsonPrimitive?
    get() = this as? JsonPrimitive

@OptIn(FlowPreview::class)
class DefaultTrafficStatisticsExplorer(
    private val trafficStatisticsStore: TrafficStatisticsStore,
    connectionActivityRepository: ConnectionActivityRepository,
    private val appIdentityResolver: AppIdentityResolver,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : TrafficStatisticsExplorer {
    private val _selectedTimeRange = MutableStateFlow(TrafficStatisticsRange.Today)
    override val selectedTimeRange: StateFlow<TrafficStatisticsRange> =
        _selectedTimeRange.asStateFlow()

    private val _selectedBarIndex = MutableStateFlow(-1)
    override val selectedBarIndex: StateFlow<Int> = _selectedBarIndex.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val statisticsClock: Flow<StatisticsClockSnapshot> =
        PollingTimers.ticks(PollingTimerSpecs.dynamic("traffic_statistics_clock", 60_000L, 0L))
            .onStart { emit(0L) }
            .map {
                val calendar = Calendar.getInstance()
                StatisticsClockSnapshot(
                    dayKey = getDayKey(calendar),
                    hourOfDay = calendar.get(Calendar.HOUR_OF_DAY),
                    minute = calendar.get(Calendar.MINUTE),
                    timeZoneId = calendar.timeZone.id,
                )
            }
            .distinctUntilChanged()

    override val todayTimeContext: StateFlow<String> =
        statisticsClock
            .map { clock ->
                val timeLabel =
                    String.format(Locale.ROOT, "%02d:%02d", clock.hourOfDay, clock.minute)
                "$timeLabel ${clock.timeZoneId} · ${TimeSlot.fromHour(clock.hourOfDay).label}"
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), "")

    override val recentRequests: StateFlow<List<RecentRequestRecord>> =
        combine(
                connectionActivityRepository.activeConnections,
                connectionActivityRepository.closedConnections,
                _searchQuery,
            ) { activeConnections, closedConnections, searchQuery ->
                val activeIds = activeConnections.asSequence().map(ConnectionInfo::id).toSet()
                val needle = searchQuery.trim()
                val closedRequests =
                    closedConnections
                        .asSequence()
                        .filterNot { it.id in activeIds }
                        .map { connection -> buildRecord(connection, isActive = false) }
                        .filter { record -> needle.isEmpty() || record.matchesQuery(needle) }
                val activeRequests =
                    activeConnections.asSequence().map { connection ->
                        buildRecord(connection, isActive = true)
                    }
                    .filter { record -> needle.isEmpty() || record.matchesQuery(needle) }

                (activeRequests + closedRequests)
                    .sortedByDescending { parseConnectionStartMillis(it.connection.start) }
                    .take(MAX_RECENT_REQUESTS)
                    .toList()
            }
            .sample(SAMPLE_INTERVAL_MS)
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val todaySummary: StateFlow<DailyTrafficSummary> =
        combine(trafficStatisticsStore.dailySummaries, statisticsClock) { _, _ ->
                trafficStatisticsStore.getTodaySummary()
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), DailyTrafficSummary.EMPTY)

    private val yesterdaySummary: StateFlow<DailyTrafficSummary> =
        combine(trafficStatisticsStore.dailySummaries, statisticsClock) { _, _ ->
                trafficStatisticsStore.getYesterdaySummary()
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), DailyTrafficSummary.EMPTY)

    override val todayTotalBytes: StateFlow<Long> =
        todaySummary
            .map { summary -> summary.total }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0L)

    override val weekTotalBytes: StateFlow<Long> =
        combine(trafficStatisticsStore.dailySummaries, statisticsClock) { _, _ ->
                val summaries = trafficStatisticsStore.getDailySummaries(7)
                summaries.sumOf { it.total }
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0L)

    override val trafficDifferenceBytes: StateFlow<Long> =
        combine(todaySummary, yesterdaySummary) { today, yesterday ->
                today.total - yesterday.total
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0L)

    override val chartItems: StateFlow<List<TrafficChartPoint>> =
        combine(_selectedTimeRange, trafficStatisticsStore.dailySummaries, statisticsClock) {
                timeRange,
                _,
                _ ->
                when (timeRange) {
                    TrafficStatisticsRange.Today -> getTodayHourlyChartItems()
                    TrafficStatisticsRange.Week -> getDailyChartItems()
                }
            }
            .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    override fun setTimeRange(range: TrafficStatisticsRange) {
        _selectedTimeRange.value = range
        _selectedBarIndex.value = -1
    }

    override fun setSelectedBarIndex(index: Int) {
        _selectedBarIndex.value = index
    }

    override fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
        }
    }

    private fun getTodayHourlyChartItems(): List<TrafficChartPoint> {
        val hourlyData = trafficStatisticsStore.getTodayHourlyData()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val currentSlot = TimeSlot.fromHour(currentHour)
        val visibleSlotCount = currentSlot.ordinal + 1

        return hourlyData.take(visibleSlotCount).mapIndexed { index, slotData ->
            val slot = TimeSlot.entries[index]
            TrafficChartPoint(
                label = slot.label,
                value = slotData.total,
                isCurrent = slot == currentSlot,
            )
        }
    }

    private fun getDailyChartItems(): List<TrafficChartPoint> {
        val summaries = trafficStatisticsStore.getDailySummaries(7)
        val dateFormat = SimpleDateFormat("M/d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val todayKey = getDayKey(calendar)

        return summaries.map { summary ->
            calendar.timeInMillis = summary.dateMillis
            val label =
                if (summary.dateMillis == todayKey) {
                    MLang.TrafficStatistics.TimeRange.Today
                } else {
                    dateFormat.format(calendar.time)
                }
            TrafficChartPoint(
                label = label,
                value = summary.total,
                isCurrent = summary.dateMillis == todayKey,
            )
        }
    }

    private fun getDayKey(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Extract the package name from mihomo metadata (with UID fallback) and resolve its display
     * label.
     */
    private fun resolveAppIdentity(connection: ConnectionInfo): Pair<String, String?> {
        val pkg = appIdentityResolver.resolvePackageFromMetadata(connection.metadata)
        if (pkg != null) return appIdentityResolver.resolveAppLabel(pkg) to pkg
        return appIdentityResolver.resolveFallbackDisplayName(connection.metadata) to null
    }

    private fun parseConnectionStartMillis(start: String): Long {
        if (start.isBlank()) return Long.MIN_VALUE
        return runCatching { OffsetDateTime.parse(start).toInstant().toEpochMilli() }
            .getOrDefault(Long.MIN_VALUE)
    }

    /**
     * Extract the top-level proxy group name from the connection's recorded
     * [ConnectionInfo.chains]. Uses the original chain data — independent of the current proxy
     * group state — so historical entries don't change when the user switches nodes.
     *
     * mihomo reports chains innermost-first (`[exit node, ..., outermost group]`), so the outermost
     * element is the proxy group that made the routing decision. Only hard built-in policies
     * (DIRECT / REJECT) are skipped; the "GLOBAL" pseudo-group is kept since it is the actual
     * routing group in global mode.
     */
    private fun resolveTopLevelGroupName(connection: ConnectionInfo): String? {
        val chains = connection.chains.map(String::trim).filter(String::isNotEmpty)
        if (chains.isEmpty()) return null
        val builtIn = setOf("DIRECT", "REJECT", "REJECT-DROP", "直连", "拦截")
        return chains.last().takeIf { it.uppercase(Locale.ROOT) !in builtIn }
    }

    /**
     * Extract the final (leaf) node name from the connection's recorded [ConnectionInfo.chains].
     * mihomo reports chains innermost-first, so the first chain element is the actual exit node
     * that handled the connection.
     */
    private fun resolveBottomNodeName(connection: ConnectionInfo): String? {
        val chains = connection.chains.map(String::trim).filter(String::isNotEmpty)
        if (chains.isEmpty()) return localizeBuiltInProxyName("DIRECT")
        return localizeBuiltInProxyName(chains.first())
    }

    private fun localizeBuiltInProxyName(name: String): String {
        val normalized = name.trim().uppercase(Locale.ROOT)
        return when (normalized) {
            "GLOBAL",
            "全局" -> MLang.Home.Profile.Global
            "DIRECT",
            "直连" -> MLang.Home.Profile.Direct
            "REJECT",
            "REJECT-DROP",
            "REJECTDROP",
            "拦截" -> MLang.Home.Profile.Reject
            "PROXY",
            "COMPATIBLE",
            "代理" -> MLang.Home.Profile.Proxy
            else -> name
        }
    }

    private fun buildRecord(connection: ConnectionInfo, isActive: Boolean): RecentRequestRecord {
        val (appName, packageName) = resolveAppIdentity(connection)
        return RecentRequestRecord(
            connection = connection,
            isActive = isActive,
            topLevelGroupName = resolveTopLevelGroupName(connection),
            bottomNodeName = resolveBottomNodeName(connection),
            sourceAppName = appName,
            sourcePackageName = packageName,
        )
    }

    /**
     * Case-insensitive substring match against every searchable field of a recent request:
     * connection metadata (source/destination IP, host/domain, ports, network, process, package,
     * uid), matched rule, matched chains (routing group + exit node), and the resolved source app.
     */
    private fun RecentRequestRecord.matchesQuery(query: String): Boolean {
        val needle = query.trim()
        if (needle.isEmpty()) return true
        val haystack =
            buildString {
                connection.metadata.forEach { (_, value) ->
                    append(value.jsonPrimitiveOrNull?.contentOrNull.orEmpty()).append(' ')
                }
                append(connection.id).append(' ')
                append(connection.rule).append(' ')
                append(connection.rulePayload).append(' ')
                append(connection.chains.joinToString(" ")).append(' ')
                append(connection.providerChains.joinToString(" ")).append(' ')
                append(sourceAppName).append(' ')
                sourcePackageName?.let { append(it).append(' ') }
                topLevelGroupName?.let { append(it).append(' ') }
                bottomNodeName?.let { append(it).append(' ') }
            }
        return haystack.contains(needle, ignoreCase = true)
    }

    companion object {
        /** Maximum number of recent requests to surface in the UI. */
        private const val MAX_RECENT_REQUESTS = 100

        /**
         * Sample interval for the recent-requests pipeline.
         *
         * Raw connection snapshots arrive every [POLL_INTERVAL_MS] (1 s) from
         * [ConnectionActivityRepository]; [sample] throttles downstream recompositions to a pace
         * that keeps the list smooth without jank.
         */
        private const val SAMPLE_INTERVAL_MS = 1000L
    }
}

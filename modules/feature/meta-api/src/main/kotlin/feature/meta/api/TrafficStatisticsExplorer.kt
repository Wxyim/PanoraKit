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
 */

package com.github.nomadboxlab.monadbox.feature.meta.api

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import kotlinx.coroutines.flow.StateFlow

enum class TrafficStatisticsRange {
    Today,
    Week,
}

data class TrafficChartPoint(val label: String, val value: Long, val isCurrent: Boolean)

data class RecentRequestRecord(
    val connection: ConnectionInfo,
    val isActive: Boolean,
    val topLevelGroupName: String?,
    val bottomNodeName: String?,
    val sourceAppName: String,
    val sourcePackageName: String?,
)

data class TargetSiteRecord(
    val siteKey: String,
    val displayName: String,
    val totalUpload: Long,
    val totalDownload: Long,
    val lastSeenAt: Long,
) {
    val totalBytes: Long
        get() = totalUpload + totalDownload
}

interface TrafficStatisticsExplorer {
    val selectedTimeRange: StateFlow<TrafficStatisticsRange>
    val selectedBarIndex: StateFlow<Int>
    val todayTimeContext: StateFlow<String>
    val recentRequests: StateFlow<List<RecentRequestRecord>>
    val todayTotalBytes: StateFlow<Long>
    val weekTotalBytes: StateFlow<Long>
    val targetSites: StateFlow<List<TargetSiteRecord>>
    val trafficDifferenceBytes: StateFlow<Long>
    val chartItems: StateFlow<List<TrafficChartPoint>>

    fun setTimeRange(range: TrafficStatisticsRange)

    fun setSelectedBarIndex(index: Int)
}

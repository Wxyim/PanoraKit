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

package com.github.nomadboxlab.monadbox.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.data.repository.IpMonitoringState
import com.github.nomadboxlab.monadbox.domain.model.TrafficData

@Composable
fun HomeRunningContent(
    trafficNow: TrafficData,
    isRunning: Boolean,
    profileName: String?,
    tunnelMode: TunnelState.Mode?,
    selectedServer: HomeSelectedServerState?,
    ipMonitoringState: IpMonitoringState,
    speedHistory: SpeedHistoryBuffer,
    onChartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            NodeInfoDisplay(
                selectedServer = selectedServer,
                tunnelMode = tunnelMode ?: TunnelState.Mode.Rule,
            )

            IpInfoDisplay(state = ipMonitoringState)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SpeedChart(speedHistory = speedHistory, isRunning = isRunning, onClick = onChartClick)
        }
    }
}

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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.core.model.TunnelState
import com.github.nomadboxlab.monadbox.presentation.component.CountryFlagCircle
import com.github.nomadboxlab.monadbox.presentation.util.extractFlaggedName
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val NODE_DELAY_WIDTH = 72.dp

@Composable
fun NodeInfoDisplay(
    selectedServer: HomeSelectedServerState?,
    tunnelMode: TunnelState.Mode,
    modifier: Modifier = Modifier,
) {
    val groupName = selectedServer?.groupName
    val serverName = selectedServer?.name
    val serverPing = selectedServer?.delay
    val normalizedGroupName =
        remember(groupName) { groupName?.trim()?.takeIf { it.isNotEmpty() && it != "-" } }
    val displayName =
        remember(serverName, tunnelMode) {
            when {
                tunnelMode == TunnelState.Mode.Direct -> MLang.Home.Profile.Direct
                tunnelMode == TunnelState.Mode.Global && serverName.isNullOrBlank() ->
                    MLang.Home.Profile.Global
                serverName.isNullOrBlank() -> null
                isDirectLikeNodeName(serverName) -> MLang.Home.Profile.Direct
                else -> serverName
            }
        }
    val flagged = remember(displayName) { displayName?.let(::extractFlaggedName) }
    val showRuleGroup = tunnelMode == TunnelState.Mode.Rule && !normalizedGroupName.isNullOrBlank()
    val hasKnownNode = flagged != null || !displayName.isNullOrBlank() || showRuleGroup

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f).padding(end = 16.dp),
        ) {
            Text(
                text = MLang.Home.NodeInfo.Node,
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = 12.sp),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (hasKnownNode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().height(INFO_TEXT_HEIGHT),
                ) {
                    val countryCode = flagged?.countryCode
                    if (showRuleGroup) {
                        Text(
                            text = "$normalizedGroupName · ",
                            style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (countryCode != null) {
                        CountryFlagCircle(countryCode = countryCode, size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = flagged?.displayName ?: displayName.orEmpty(),
                        style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                        color = MiuixTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else {
                Text(
                    text = MLang.Home.NodeInfo.Unknown,
                    style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.height(INFO_TEXT_HEIGHT),
                )
            }
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(NODE_DELAY_WIDTH)) {
            Text(
                text = MLang.Home.NodeInfo.Delay,
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = 12.sp),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            PingValue(
                ping =
                    if (tunnelMode == TunnelState.Mode.Direct) {
                        null
                    } else {
                        serverPing
                    }
            )
        }
    }
}

@Composable
private fun PingValue(ping: Int?) {
    when {
        ping == null || ping == 0 -> {
            Text(
                text = "--",
                style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.height(INFO_TEXT_HEIGHT),
            )
        }

        ping < 0 -> {
            Text(
                text = "TIMEOUT",
                style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.height(INFO_TEXT_HEIGHT),
            )
        }

        else -> {
            val color =
                when {
                    ping <= 300 -> Color(0xFF007906)
                    ping <= 1000 -> Color(0xFFFFB300)
                    else -> Color(0xFFE53935)
                }
            Text(
                text = "${ping}ms",
                style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                color = color,
                modifier = Modifier.height(INFO_TEXT_HEIGHT),
            )
        }
    }
}

private fun isDirectLikeNodeName(name: String): Boolean {
    val normalized = name.trim().uppercase()
    return normalized == "DIRECT" || normalized == "直连"
}

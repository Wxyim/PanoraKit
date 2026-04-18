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

package com.github.nomadboxlab.monadbox.feature.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionAppIdentityLookup
import com.github.nomadboxlab.monadbox.feature.meta.api.toConnectionDisplayAddress
import com.github.nomadboxlab.monadbox.presentation.component.AppActionBottomSheet
import com.github.nomadboxlab.monadbox.presentation.component.AppInteractionFeedbackDefaults
import com.github.nomadboxlab.monadbox.presentation.component.appClickable
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.delay
import kotlinx.serialization.json.jsonPrimitive
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SinkFeedback
import top.yukonga.miuix.kmp.utils.pressable

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ConnectionCard(
    connectionInfo: ConnectionInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    val backgroundColor = MiuixTheme.colorScheme.background
    val interactionSource = remember { MutableInteractionSource() }
    val network =
        remember(connectionInfo.metadata) {
            connectionInfo.metadata["network"]?.jsonPrimitive?.content ?: "TCP"
        }
    val displayAddress = remember(connectionInfo) { connectionInfo.toConnectionDisplayAddress() }
    val relativeTime = remember(connectionInfo.start) { formatRelativeTime(connectionInfo.start) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .pressable(interactionSource = interactionSource, indication = SinkFeedback())
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, MiuixTheme.colorScheme.surfaceVariant, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConnectionProtocolIcon(network = network)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = displayAddress.title,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        ConnectionTagChip(
                            label = network.uppercase(),
                            backgroundColor = getProtocolColor(network),
                        )
                        if (connectionInfo.rule.isNotEmpty()) {
                            ConnectionTagChip(label = connectionInfo.rule)
                        }
                        if (connectionInfo.chains.isNotEmpty()) {
                            ConnectionTagChip(label = "x${connectionInfo.chains.size}")
                        }
                    }
                    Text(
                        text = relativeTime,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun ConnectionDetailSheet(
    show: Boolean,
    connectionInfo: ConnectionInfo?,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit = {},
) {
    val appIdentityLookup: ConnectionAppIdentityLookup = koinInject()
    val network =
        remember(connectionInfo) {
            connectionInfo?.metadata?.get("network")?.jsonPrimitive?.content ?: "TCP"
        }
    val process =
        remember(connectionInfo) {
            connectionInfo?.metadata?.get("process")?.jsonPrimitive?.content ?: ""
        }
    val displayAddress = remember(connectionInfo) { connectionInfo?.toConnectionDisplayAddress() }
    val durationText by
        produceState(initialValue = "00:00:00", key1 = show, key2 = connectionInfo?.start) {
            while (show && connectionInfo?.start != null) {
                value = calculateDuration(connectionInfo.start)
                delay(1000L)
            }
        }
    val sourceApp =
        remember(connectionInfo, appIdentityLookup) {
            connectionInfo?.let(appIdentityLookup::resolve)
        }
    val sourceAppName =
        remember(sourceApp, process) {
            sourceApp?.appName?.takeIf { it.isNotBlank() } ?: process.ifBlank { "" }
        }
    val sourcePackageName =
        remember(sourceApp) { sourceApp?.packageName?.takeIf { it.isNotBlank() } }
    val distinctProcessName =
        remember(sourcePackageName, process) {
            process.takeIf {
                it.isNotBlank() && !it.equals(sourcePackageName.orEmpty(), ignoreCase = true)
            }
        }

    AppActionBottomSheet(
        show = show,
        title = displayAddress?.title.orEmpty(),
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
    ) {
        connectionInfo?.let { info ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    ConnectionInfoSection(
                        network = network,
                        sourceAppName = sourceAppName,
                        sourcePackageName = sourcePackageName,
                        process = distinctProcessName.orEmpty(),
                        sourceAddress = displayAddress?.sourceAddress.orEmpty(),
                        destinationAddress = displayAddress?.destinationAddress.orEmpty(),
                        duration = durationText,
                        upload = info.upload,
                        download = info.download,
                        chains = info.chains,
                    )
                }
                if (info.rule.isNotEmpty()) {
                    item { RuleInfoSection(rule = info.rule, rulePayload = info.rulePayload) }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
internal fun TabRowWithContour(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(100.dp)
    val backgroundColor = MiuixTheme.colorScheme.surfaceVariant
    val selectedColor = MiuixTheme.colorScheme.primary

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(shape)
                .background(backgroundColor)
                .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTabIndex == index
            val tabShape = RoundedCornerShape(100.dp)
            val tabBgColor = if (isSelected) selectedColor else Color.Transparent
            val textColor =
                if (isSelected) {
                    MiuixTheme.colorScheme.onPrimary
                } else {
                    MiuixTheme.colorScheme.onSurface
                }
            Box(
                modifier =
                    Modifier.weight(1f)
                        .clip(tabShape)
                        .background(tabBgColor)
                        .appClickable(
                            role = Role.Tab,
                            pressedAlpha = AppInteractionFeedbackDefaults.NavigationPressedAlpha,
                            onClick = { onTabSelected(index) },
                        )
                        .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = tab, style = MiuixTheme.textStyles.body2, color = textColor)
            }
        }
    }
}

@Composable
private fun ConnectionProtocolIcon(network: String) {
    val neutral = MiuixTheme.colorScheme.onSurface
    val protocolColor = getProtocolColor(network)

    Box(
        modifier =
            Modifier.size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(neutral.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = network.take(3).uppercase(),
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = 12.sp),
            color = protocolColor,
        )
    }
}

@Composable
private fun ConnectionTagChip(
    label: String,
    backgroundColor: Color = MiuixTheme.colorScheme.primary,
) {
    Text(
        text = label,
        style = MiuixTheme.textStyles.footnote1.copy(fontSize = 10.sp),
        color = backgroundColor,
        modifier =
            Modifier.clip(RoundedCornerShape(100.dp))
                .background(backgroundColor.copy(alpha = 0.1f))
                .padding(horizontal = 7.dp, vertical = 2.dp),
    )
}

@Composable
private fun ConnectionInfoSection(
    network: String,
    sourceAppName: String,
    sourcePackageName: String?,
    process: String,
    sourceAddress: String,
    destinationAddress: String,
    duration: String,
    upload: Long,
    download: Long,
    chains: List<String>,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(MLang.Connection.Detail.Info)
        InfoRow(label = MLang.Connection.Detail.Protocol, value = network.uppercase())
        if (sourceAppName.isNotEmpty()) {
            InfoRow(label = MLang.Connection.Detail.SourceApp, value = sourceAppName)
        }
        if (!sourcePackageName.isNullOrEmpty()) {
            InfoRow(label = MLang.Connection.Detail.PackageName, value = sourcePackageName)
        }
        if (process.isNotEmpty()) {
            InfoRow(label = MLang.Connection.Detail.Process, value = process)
        }
        if (sourceAddress.isNotEmpty()) {
            InfoRow(label = MLang.Connection.Detail.SourceAddress, value = sourceAddress)
        }
        if (destinationAddress.isNotEmpty()) {
            InfoRow(label = MLang.Connection.Detail.DestinationAddress, value = destinationAddress)
        }
        InfoRow(label = MLang.Connection.Detail.Duration, value = duration)
        InfoRow(
            label = MLang.Connection.Detail.Upload,
            value = formatBytes(upload),
            valueColor = Color(0xFF2196F3),
        )
        InfoRow(
            label = MLang.Connection.Detail.Download,
            value = formatBytes(download),
            valueColor = Color(0xFF4CAF50),
        )
        if (chains.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            ProxyChainRow(chains = chains)
        }
    }
}

@Composable
private fun ProxyChainRow(chains: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(chains.withIndex().toList()) { (index, chain) ->
            val isLast = index == chains.lastIndex
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                ChainNode(name = chain, isActive = isLast)
                if (!isLast) {
                    Text(
                        text = "->",
                        style = MiuixTheme.textStyles.footnote1,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(horizontal = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChainNode(name: String, isActive: Boolean) {
    val backgroundColor =
        if (isActive) {
            Color(0xFF00BFA5).copy(alpha = 0.12f)
        } else {
            MiuixTheme.colorScheme.surfaceVariant
        }
    val textColor =
        if (isActive) {
            Color(0xFF00BFA5)
        } else {
            Color(0xFF6B7280)
        }

    Row(
        modifier =
            Modifier.clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (isActive) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00BFA5)))
        }
        Text(
            text = name,
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = 11.sp),
            color = textColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun RuleInfoSection(rule: String, rulePayload: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(MLang.Connection.Detail.Rule)
        InfoRow(label = MLang.Connection.Detail.Type, value = rule)
        if (rulePayload.isNotEmpty()) {
            InfoRow(label = MLang.Connection.Detail.Content, value = rulePayload)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MiuixTheme.textStyles.body2.copy(fontWeight = FontWeight.Medium),
        color = MiuixTheme.colorScheme.onSurface,
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MiuixTheme.colorScheme.onSurface,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.width(64.dp),
        )
        Text(
            text = value,
            style = MiuixTheme.textStyles.footnote1,
            color = valueColor,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun getProtocolColor(network: String): Color =
    when (network.uppercase()) {
        "TCP" -> Color(0xFF2196F3)
        "UDP" -> Color(0xFF4CAF50)
        "HTTP" -> Color(0xFF9E9E9E)
        "HTTPS" -> Color(0xFF00BCD4)
        else -> Color(0xFF9E9E9E)
    }

private fun formatRelativeTime(start: String): String {
    if (start.isEmpty()) return ""
    return try {
        val startTime = java.time.OffsetDateTime.parse(start).toInstant()
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(startTime, now)
        val seconds = duration.seconds
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        when {
            seconds < 60 -> MLang.Connection.RelativeTime.JustNow
            minutes < 60 -> MLang.Connection.RelativeTime.MinutesAgo.format(minutes)
            hours < 24 -> MLang.Connection.RelativeTime.HoursAgo.format(hours)
            days < 7 -> MLang.Connection.RelativeTime.DaysAgo.format(days)
            else -> {
                val date =
                    java.time.LocalDateTime.ofInstant(startTime, java.time.ZoneId.systemDefault())
                "%02d-%02d".format(date.monthValue, date.dayOfMonth)
            }
        }
    } catch (e: Exception) {
        ""
    }
}

private fun calculateDuration(start: String): String {
    if (start.isEmpty()) return "00:00:00"
    return try {
        val startTime = java.time.OffsetDateTime.parse(start).toInstant()
        val now = java.time.Instant.now()
        val duration = java.time.Duration.between(startTime, now)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } catch (e: Exception) {
        "00:00:00"
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "${bytes}B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1fKB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1fMB".format(mb)
    val gb = mb / 1024.0
    return "%.2fGB".format(gb)
}

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

package com.github.nomadboxlab.monadbox.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.common.util.LocaleUtil
import com.github.nomadboxlab.monadbox.data.repository.IpMonitoringState
import com.github.nomadboxlab.monadbox.presentation.component.CountryFlagCircle
import com.github.nomadboxlab.monadbox.presentation.component.appClickable
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Redo-dot`
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val INFO_VALUE_CORNER_RADIUS = RoundedCornerShape(10.dp)
private val IP_VALUE_MIN_HEIGHT = 48.dp
private val IP_VALUE_HORIZONTAL_PADDING = 12.dp
private val IP_VALUE_VERTICAL_PADDING = 8.dp
private const val IP_VALUE_VISIBLE_MAX_LINES = 3
internal val INFO_TEXT_HEIGHT = 24.dp

@Composable
fun IpInfoDisplay(
    state: IpMonitoringState,
    modifier: Modifier = Modifier,
    isExternalIpLookupEnabled: Boolean = false,
    isExternalIpQuerying: Boolean = false,
    onQueryExternalIp: () -> Unit = {},
) {
    val externalIp = (state as? IpMonitoringState.Success)?.externalIp
    var isIpVisible by rememberSaveable(externalIp?.ip) { mutableStateOf(false) }

    when {
        externalIp != null -> {
            IpInfoRow(
                label = MLang.Home.IpInfo.ExitIp,
                value =
                    IpAddressDisplayFormatter.buildDisplayValue(
                        ipAddress = externalIp.ip,
                        isIpVisible = isIpVisible,
                    ),
                valueColor = MiuixTheme.colorScheme.onSurface,
                countryCode = externalIp.countryCode,
                isIpVisible = isIpVisible,
                isRevealable = true,
                onToggleVisibility = { isIpVisible = !isIpVisible },
                modifier = modifier,
                showRefreshButton = isExternalIpLookupEnabled,
                isRefreshing = isExternalIpQuerying,
                onRefresh = onQueryExternalIp,
            )
        }

        else -> {
            IpInfoRow(
                label = MLang.Home.IpInfo.ExitIp,
                value = "--",
                valueColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                countryCode = null,
                isIpVisible = false,
                isRevealable = false,
                onToggleVisibility = {},
                modifier = modifier,
                showRefreshButton = isExternalIpLookupEnabled,
                isRefreshing = isExternalIpQuerying,
                onRefresh = onQueryExternalIp,
            )
        }
    }
}

@Composable
private fun IpInfoRow(
    label: String,
    value: String,
    valueColor: Color,
    countryCode: String?,
    isIpVisible: Boolean,
    isRevealable: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    showRefreshButton: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
) {
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
                text = label,
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = 12.sp),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (isRevealable) {
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .heightIn(min = IP_VALUE_MIN_HEIGHT)
                            .clip(INFO_VALUE_CORNER_RADIUS)
                            .appClickable(role = Role.Button, onClick = onToggleVisibility)
                            .padding(
                                horizontal = IP_VALUE_HORIZONTAL_PADDING,
                                vertical = IP_VALUE_VERTICAL_PADDING,
                            ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = value,
                        style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                        fontFamily = FontFamily.Monospace,
                        color = valueColor,
                        maxLines = if (isIpVisible) IP_VALUE_VISIBLE_MAX_LINES else 1,
                        overflow = if (isIpVisible) TextOverflow.Clip else TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Text(
                    text = value,
                    style = MiuixTheme.textStyles.body1.copy(lineHeight = 20.sp),
                    fontFamily = FontFamily.Monospace,
                    color = valueColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(INFO_TEXT_HEIGHT),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showRefreshButton) {
                Box(
                    modifier =
                        Modifier.size(28.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            .appClickable(
                                role = Role.Button,
                                enabled = !isRefreshing,
                                onClick = onRefresh,
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = MonadIcons.`Redo-dot`,
                        contentDescription = null,
                        tint = if (isRefreshing) MiuixTheme.colorScheme.onSurfaceVariantSummary
                        else MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            CountryBadge(countryCode = countryCode)
        }
    }
}

@Composable
private fun CountryBadge(countryCode: String?) {
    if (countryCode != null) {
        val displayCountryCode = LocaleUtil.normalizeRegionCode(countryCode) ?: countryCode

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CountryFlagCircle(countryCode = countryCode, size = 20.dp)
            Text(
                text = displayCountryCode,
                style = MiuixTheme.textStyles.body1,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 40.dp),
            )
        }
    }
}

internal object IpAddressDisplayFormatter {
    fun buildDisplayValue(ipAddress: String, isIpVisible: Boolean): String {
        val normalized = extractDisplayableIp(ipAddress)
        return if (normalized.contains(":")) {
            formatIpv6Address(ipAddress = normalized, isIpVisible = isIpVisible)
        } else {
            if (isIpVisible) normalized else maskIpv4Address(normalized)
        }
    }

    fun maskIpAddress(ipAddress: String): String {
        val normalized = extractDisplayableIp(ipAddress)
        return if (normalized.contains(":")) {
            formatIpv6Address(ipAddress = normalized, isIpVisible = false)
        } else {
            maskIpv4Address(normalized)
        }
    }

    private fun extractDisplayableIp(rawValue: String): String {
        val normalized = rawValue.trim()
        if (normalized.isEmpty()) return normalized

        normalized.lineSequence().forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.startsWith("ip=", ignoreCase = true)) {
                return trimmedLine.substringAfter('=').trim()
            }
        }
        return normalized
    }

    private fun maskIpv4Address(ipAddress: String): String {
        val segments = ipAddress.split(".")
        if (segments.size != 4) {
            return "****"
        }
        return buildString {
            append(segments[0])
            append(".")
            append(segments[1])
            append(".")
            append("*".repeat(segments[2].length.coerceAtLeast(1)))
            append(".")
            append(segments[3])
        }
    }

    private fun formatIpv6Address(ipAddress: String, isIpVisible: Boolean): String {
        val trimmed = ipAddress.trim()
        if (trimmed.isEmpty()) return "****"
        if (isIpVisible) return trimmed

        val visibleSegments = trimmed.split(':').filter { it.isNotEmpty() }
        if (visibleSegments.isEmpty()) {
            return "****"
        }
        return when {
            visibleSegments.size == 1 -> "${visibleSegments.first()}:****"
            else -> "${visibleSegments[0]}:${visibleSegments[1]}:****"
        }
    }
}

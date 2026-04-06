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

package com.github.yumelira.yumebox.presentation.screen.node

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.AsyncImage as SketchAsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.domain.model.ProxyGroupInfo
import com.github.yumelira.yumebox.presentation.component.CountryFlagCircle
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.chevron
import com.github.yumelira.yumebox.presentation.util.extractFlaggedName
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SinkFeedback
import top.yukonga.miuix.kmp.utils.pressable

private data class GroupBadge(val label: String, val textColor: Color, val backgroundColor: Color)

private val GroupTypeBadgeTextColor = Color(0xFF178C7A)
private val GroupTypeBadgeBackgroundColor = Color(0xFFE5F4F1)

private fun groupBadge(type: Proxy.Type): GroupBadge =
    when (type) {
        Proxy.Type.URLTest,
        Proxy.Type.Fallback,
        Proxy.Type.Smart ->
            GroupBadge(
                label = type.name,
                textColor = GroupTypeBadgeTextColor,
                backgroundColor = GroupTypeBadgeBackgroundColor,
            )

        else ->
            GroupBadge(
                label = type.name,
                textColor = GroupTypeBadgeTextColor,
                backgroundColor = GroupTypeBadgeBackgroundColor,
            )
    }

internal fun LazyListScope.nodeGroupItems(
    groups: List<ProxyGroupInfo>,
    onGroupClick: (ProxyGroupInfo) -> Unit,
    testingGroupNames: Set<String> = emptySet(),
    testingProxyNames: Set<String> = emptySet(),
    expandedGroupName: String? = null,
    onGroupBoundsChanged: ((String, Rect) -> Unit)? = null,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)? = null,
    onTestDelay: ((groupName: String) -> Unit)? = null,
    onTestProxyDelay: ((String) -> Unit)? = null,
    singleNodeTestEnabled: Boolean = true,
    itemVerticalPadding: Dp = 6.dp,
) {
    items(
        items = groups,
        key = { group -> "${group.type.name}:${group.name}" },
        contentType = { "NodeGroupCard" },
    ) { group ->
        val isExpanded = expandedGroupName == group.name
        NodeGroupCard(
            group = group,
            isExpanded = isExpanded,
            isDelayTesting = testingGroupNames.contains(group.name),
            onClick = { onGroupClick(group) },
            modifier = Modifier.fillMaxWidth().padding(vertical = itemVerticalPadding),
            onBoundsChanged =
                onGroupBoundsChanged?.let { callback -> { rect -> callback(group.name, rect) } },
            expandedContent =
                if (isExpanded) {
                    {
                        ExpandedProxyGroupContent(
                            group = group,
                            isDelayTesting = testingGroupNames.contains(group.name),
                            testingProxyNames = testingProxyNames,
                            onSelectProxy = onSelectProxy,
                            onTestDelay = onTestDelay,
                            onTestProxyDelay = onTestProxyDelay,
                            singleNodeTestEnabled = singleNodeTestEnabled,
                        )
                    }
                } else {
                    null
                },
        )
    }
}

@Composable
internal fun NodeGroupCard(
    group: ProxyGroupInfo,
    isExpanded: Boolean,
    isDelayTesting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBoundsChanged: ((Rect) -> Unit)? = null,
    expandedContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val cardShape = RoundedCornerShape(26.dp)
    val interactionSource = remember { MutableInteractionSource() }

    val currentNode = remember(group.now) { extractFlaggedName(group.now) }
    val currentNodeName =
        remember(currentNode.displayName) {
            currentNode.displayName.ifBlank { MLang.Proxy.Mode.Direct }
        }
    val iconUri =
        remember(group.icon) {
            group.icon?.trim()?.takeIf { it.isNotEmpty() }?.let(::normalizeNodeGroupIconUri)
        }
    val currentDelay =
        remember(group.proxies, group.now) {
            group.proxies.firstOrNull { it.name == group.now }?.delay
        }
    val badge = remember(group.type) { groupBadge(group.type) }
    val delayLabel = remember(currentDelay) { nodeLatencyLabel(currentDelay) }
    val leadingShape = RoundedCornerShape(NodeCardDefaults.LeadingContainerCornerRadius)

    Column(
        modifier =
            modifier
                .let { base ->
                    if (onBoundsChanged != null) {
                        base.onGloballyPositioned { coords ->
                            onBoundsChanged(coords.boundsInWindow())
                        }
                    } else base
                }
                .shadow(
                    elevation = 4.dp,
                    shape = cardShape,
                    ambientColor = Color.Black.copy(alpha = 0.05f),
                    spotColor = Color.Black.copy(alpha = 0.05f),
                )
                .clip(cardShape)
                .background(MiuixTheme.colorScheme.background)
                .pressable(interactionSource = interactionSource, indication = SinkFeedback())
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (iconUri != null) {
                NodeGroupIcon(
                    iconUri = iconUri,
                    modifier =
                        Modifier.size(NodeCardDefaults.LeadingContainerSize).clip(leadingShape),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = group.name,
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )

                        NodeGroupBadgeChip(badge = badge)
                    }

                    Text(
                        text = MLang.Proxy.Selection.NodeCount.format(group.proxies.size),
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        val cc = currentNode.countryCode
                        if (cc != null) {
                            CountryFlagCircle(
                                countryCode = cc,
                                size = NodeCardDefaults.InlineFlagSize,
                            )
                        }
                        Text(
                            text = currentNodeName,
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Box(
                        modifier = Modifier.padding(start = 8.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        when {
                            delayLabel != null -> {
                                val (delayText, delayColor) = delayLabel
                                Text(
                                    text = delayText,
                                    style = MiuixTheme.textStyles.footnote1,
                                    color = delayColor,
                                )
                            }

                            isDelayTesting -> {
                                RotatingCircleGauge(
                                    isRotating = true,
                                    modifier = Modifier.size(NodeCardDefaults.ActionIconSize),
                                    tint = MiuixTheme.colorScheme.primary,
                                    contentDescription = null,
                                )
                            }

                            else ->
                                Icon(
                                    Yume.chevron,
                                    contentDescription = null,
                                    modifier =
                                        Modifier.size(NodeCardDefaults.ChevronIconSize)
                                            .rotate(if (isExpanded) 90f else 0f),
                                    tint = Color(0xFFC7C7CC),
                                )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded && expandedContent != null,
            enter =
                expandVertically(animationSpec = tween(durationMillis = 220)) +
                    fadeIn(animationSpec = tween(durationMillis = 180)),
            exit =
                shrinkVertically(animationSpec = tween(durationMillis = 180)) +
                    fadeOut(animationSpec = tween(durationMillis = 140)),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = expandedContent ?: {},
            )
        }
    }
}

@Composable
private fun NodeGroupBadgeChip(badge: GroupBadge, modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(100.dp))
                .background(badge.backgroundColor)
                .padding(
                    horizontal = NodeCardDefaults.ChipHorizontalPadding,
                    vertical = NodeCardDefaults.ChipVerticalPadding + 1.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = badge.label,
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = NodeCardDefaults.ChipFontSize),
            color = badge.textColor,
        )
    }
}

@Composable
private fun ExpandedProxyGroupContent(
    group: ProxyGroupInfo,
    isDelayTesting: Boolean,
    testingProxyNames: Set<String>,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)?,
    onTestDelay: ((groupName: String) -> Unit)?,
    onTestProxyDelay: ((String) -> Unit)?,
    singleNodeTestEnabled: Boolean,
) {
    group.proxies.forEach { proxy ->
        NodeCard(
            proxy = proxy,
            isSelected = proxy.name == group.now,
            onClick = { proxyName ->
                if (group.type == Proxy.Type.Selector) {
                    onSelectProxy?.invoke(group.name, proxyName)
                } else {
                    onTestDelay?.invoke(group.name)
                }
            },
            isDelayTesting = isDelayTesting,
            isThisProxyTesting = proxy.name in testingProxyNames,
            onSingleNodeTestClick = onTestProxyDelay?.let { { it(proxy.name) } },
            showCountryFlag = true,
            singleNodeTestEnabled = singleNodeTestEnabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun normalizeNodeGroupIconUri(raw: String): String {
    if (raw.startsWith("//")) return "https:$raw"
    if (raw.startsWith("www.", ignoreCase = true)) return "https://$raw"
    return raw
}

@Composable
private fun NodeGroupIcon(iconUri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val placeholderColorInt = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.10f).toArgb()
    val request =
        remember(context, iconUri, placeholderColorInt) {
            ImageRequest(context, iconUri) {
                placeholder(IntColorDrawableStateImage(placeholderColorInt))
                error(IntColorDrawableStateImage(placeholderColorInt))
                crossfade(true)
            }
        }
    SketchAsyncImage(
        request = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

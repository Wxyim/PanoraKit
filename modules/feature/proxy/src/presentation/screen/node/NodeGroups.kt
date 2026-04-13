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
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.github.panpf.sketch.AsyncImage as SketchAsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.domain.model.ProxyDisplayMode
import com.github.yumelira.yumebox.domain.model.ProxyGroupInfo
import com.github.yumelira.yumebox.presentation.component.CountryFlagCircle
import com.github.yumelira.yumebox.presentation.component.SemanticActionDefaults
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.chevron
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import com.github.yumelira.yumebox.presentation.theme.ProxyNodeGroupLayoutDefaults
import com.github.yumelira.yumebox.presentation.util.extractFlaggedName
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SinkFeedback
import top.yukonga.miuix.kmp.utils.pressable

private data class GroupBadge(val label: String, val tone: SemanticTone)

private fun groupBadge(type: Proxy.Type): GroupBadge =
    when (type) {
        Proxy.Type.Selector -> GroupBadge(label = type.name, tone = SemanticTone.Brand)
        Proxy.Type.URLTest,
        Proxy.Type.Fallback,
        Proxy.Type.Smart -> GroupBadge(label = type.name, tone = SemanticTone.Info)
        Proxy.Type.Direct -> GroupBadge(label = type.name, tone = SemanticTone.Success)
        else -> GroupBadge(label = type.name, tone = SemanticTone.Neutral)
    }

internal fun LazyListScope.nodeGroupItems(
    groups: List<ProxyGroupInfo>,
    displayMode: ProxyDisplayMode = ProxyDisplayMode.SINGLE_DETAILED,
    onGroupClick: (ProxyGroupInfo) -> Unit,
    testingGroupNames: Set<String> = emptySet(),
    testingProxyNames: Set<String> = emptySet(),
    expandedGroupName: String? = null,
    onGroupBoundsChanged: ((String, Rect) -> Unit)? = null,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)? = null,
    onTestDelay: ((groupName: String) -> Unit)? = null,
    onTestProxyDelay: ((String) -> Unit)? = null,
    singleNodeTestEnabled: Boolean = true,
    expandedContentMaxHeight: Dp? = null,
    itemVerticalPadding: Dp = ProxyNodeGroupLayoutDefaults.ItemVerticalPadding,
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
                            displayMode = displayMode,
                            isDelayTesting = testingGroupNames.contains(group.name),
                            testingProxyNames = testingProxyNames,
                            onSelectProxy = onSelectProxy,
                            onTestDelay = onTestDelay,
                            onTestProxyDelay = onTestProxyDelay,
                            singleNodeTestEnabled = singleNodeTestEnabled,
                            maxHeight = expandedContentMaxHeight,
                        )
                    }
                } else {
                    null
                },
        )
    }
}

internal fun LazyListScope.adaptiveNodeGroupItems(
    groups: List<ProxyGroupInfo>,
    columns: Int,
    displayMode: ProxyDisplayMode,
    onGroupClick: (ProxyGroupInfo) -> Unit,
    testingGroupNames: Set<String> = emptySet(),
    testingProxyNames: Set<String> = emptySet(),
    expandedGroupName: String? = null,
    onGroupBoundsChanged: ((String, Rect) -> Unit)? = null,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)? = null,
    onTestDelay: ((groupName: String) -> Unit)? = null,
    onTestProxyDelay: ((String) -> Unit)? = null,
    singleNodeTestEnabled: Boolean = true,
    expandedContentMaxHeight: Dp? = null,
    itemVerticalPadding: Dp = ProxyNodeGroupLayoutDefaults.ItemVerticalPadding,
) {
    if (columns <= 1) {
        nodeGroupItems(
            groups = groups,
            displayMode = displayMode,
            onGroupClick = onGroupClick,
            testingGroupNames = testingGroupNames,
            testingProxyNames = testingProxyNames,
            expandedGroupName = expandedGroupName,
            onGroupBoundsChanged = onGroupBoundsChanged,
            onSelectProxy = onSelectProxy,
            onTestDelay = onTestDelay,
            onTestProxyDelay = onTestProxyDelay,
            singleNodeTestEnabled = singleNodeTestEnabled,
            expandedContentMaxHeight = expandedContentMaxHeight,
            itemVerticalPadding = itemVerticalPadding,
        )
        return
    }

    val rows = groups.chunked(columns)
    items(
        items = rows,
        key = { row ->
            row.joinToString(separator = "|") { group -> "${group.type.name}:${group.name}" }
        },
        contentType = { "AdaptiveNodeGroupRow" },
    ) { rowGroups ->
        val expandedGroup = rowGroups.firstOrNull { it.name == expandedGroupName }
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = itemVerticalPadding),
            verticalArrangement =
                Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.AdaptiveInlineExpandedRowSpacing),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.AdaptiveColumnSpacing),
                verticalAlignment = Alignment.Top,
            ) {
                rowGroups.forEach { group ->
                    val isExpanded = expandedGroupName == group.name
                    NodeGroupCard(
                        group = group,
                        isExpanded = isExpanded,
                        isDelayTesting = testingGroupNames.contains(group.name),
                        onClick = { onGroupClick(group) },
                        modifier = Modifier.weight(1f),
                        onBoundsChanged =
                            onGroupBoundsChanged?.let { callback ->
                                { rect -> callback(group.name, rect) }
                            },
                    )
                }
                repeat(columns - rowGroups.size) { Spacer(modifier = Modifier.weight(1f)) }
            }

            AnimatedVisibility(
                visible = expandedGroup != null,
                enter =
                    expandVertically(animationSpec = tween(durationMillis = 220)) +
                        fadeIn(animationSpec = tween(durationMillis = 180)),
                exit =
                    shrinkVertically(animationSpec = tween(durationMillis = 180)) +
                        fadeOut(animationSpec = tween(durationMillis = 140)),
            ) {
                expandedGroup?.let { group ->
                    RowInlineExpandedGroupPanel(
                        group = group,
                        displayMode = displayMode,
                        isDelayTesting = testingGroupNames.contains(group.name),
                        testingProxyNames = testingProxyNames,
                        onSelectProxy = onSelectProxy,
                        onTestDelay = onTestDelay,
                        onTestProxyDelay = onTestProxyDelay,
                        singleNodeTestEnabled = singleNodeTestEnabled,
                        maxHeight = expandedContentMaxHeight,
                    )
                }
            }
        }
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
    val cardShape = RoundedCornerShape(ProxyNodeGroupLayoutDefaults.CardCornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val expandedBorderColor =
        if (isExpanded) MiuixTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent

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
    val latencyVisual = proxyLatencyVisual(delay = currentDelay, isTesting = isDelayTesting)
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
                    elevation = ProxyNodeGroupLayoutDefaults.CardElevation,
                    shape = cardShape,
                    ambientColor = Color.Black.copy(alpha = 0.05f),
                    spotColor = Color.Black.copy(alpha = 0.05f),
                )
                .clip(cardShape)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = ProxyNodeGroupLayoutDefaults.CardBorderWidth,
                    color = expandedBorderColor,
                    shape = cardShape,
                )
                .pressable(interactionSource = interactionSource, indication = SinkFeedback())
                .selectable(
                    selected = isExpanded,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(
                    horizontal = ProxyNodeGroupLayoutDefaults.CardPaddingHorizontal,
                    vertical = ProxyNodeGroupLayoutDefaults.CardPaddingVertical,
                ),
        verticalArrangement = Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.CardSectionSpacing),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NodeCardDefaults.ContentRowSpacing),
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
                verticalArrangement =
                    Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.CardContentColumnSpacing),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.HeaderTitleSpacing),
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
                        modifier =
                            Modifier.padding(
                                start = ProxyNodeGroupLayoutDefaults.HeaderMetaStartPadding
                            ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.HeaderMetaSpacing),
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

                    Row(
                        modifier =
                            Modifier.padding(
                                start = ProxyNodeGroupLayoutDefaults.HeaderMetaStartPadding
                            ),
                        horizontalArrangement =
                            Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.HeaderMetaSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isDelayTesting) {
                            RotatingCircleGauge(
                                isRotating = true,
                                modifier = Modifier.size(NodeCardDefaults.ActionIconSize),
                                tint = MiuixTheme.colorScheme.primary,
                                contentDescription = null,
                            )
                        }

                        Text(
                            text = latencyVisual.label,
                            style = MiuixTheme.textStyles.footnote1,
                            color = latencyVisual.color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

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
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(top = ProxyNodeGroupLayoutDefaults.ExpandedContentTopPadding)
                        .selectableGroup(),
                verticalArrangement =
                    Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.ExpandedContentSpacing),
                content = expandedContent ?: {},
            )
        }
    }
}

@Composable
private fun RowInlineExpandedGroupPanel(
    group: ProxyGroupInfo,
    displayMode: ProxyDisplayMode,
    isDelayTesting: Boolean,
    testingProxyNames: Set<String>,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)?,
    onTestDelay: ((groupName: String) -> Unit)?,
    onTestProxyDelay: ((String) -> Unit)?,
    singleNodeTestEnabled: Boolean,
    maxHeight: Dp? = null,
) {
    val panelShape =
        RoundedCornerShape(ProxyNodeGroupLayoutDefaults.InlineExpandedPanelCornerRadius)
    val badge = remember(group.type) { groupBadge(group.type) }
    val currentNode = remember(group.now) { extractFlaggedName(group.now) }
    val currentNodeName =
        remember(currentNode.displayName) {
            currentNode.displayName.ifBlank { MLang.Proxy.Mode.Direct }
        }

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .shadow(
                    elevation = ProxyNodeGroupLayoutDefaults.InlineExpandedPanelElevation,
                    shape = panelShape,
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = Color.Black.copy(alpha = 0.04f),
                )
                .clip(panelShape)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = ProxyNodeGroupLayoutDefaults.InlineExpandedPanelBorderWidth,
                    color = MiuixTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = panelShape,
                )
                .padding(
                    horizontal =
                        ProxyNodeGroupLayoutDefaults.InlineExpandedPanelContentHorizontalPadding,
                    vertical =
                        ProxyNodeGroupLayoutDefaults.InlineExpandedPanelContentVerticalPadding,
                ),
        verticalArrangement =
            Arrangement.spacedBy(ProxyNodeGroupLayoutDefaults.InlineExpandedPanelSectionSpacing),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(
                        ProxyNodeGroupLayoutDefaults.InlineExpandedPanelTitleSpacing
                    ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            ProxyNodeGroupLayoutDefaults.InlineExpandedPanelTitleSpacing
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = group.name,
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    NodeGroupBadgeChip(badge = badge)
                }

                Text(
                    text = currentNodeName,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier =
                    Modifier.padding(
                        start = ProxyNodeGroupLayoutDefaults.InlineExpandedPanelMetaStartPadding
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        ProxyNodeGroupLayoutDefaults.InlineExpandedPanelMetaSpacing
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isDelayTesting) {
                    RotatingCircleGauge(
                        isRotating = true,
                        modifier = Modifier.size(NodeCardDefaults.ActionIconSize),
                        tint = MiuixTheme.colorScheme.primary,
                        contentDescription = null,
                    )
                }

                Text(
                    text = MLang.Proxy.Selection.NodeCount.format(group.proxies.size),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        ExpandedProxyGroupContent(
            group = group,
            displayMode = displayMode,
            isDelayTesting = isDelayTesting,
            testingProxyNames = testingProxyNames,
            onSelectProxy = onSelectProxy,
            onTestDelay = onTestDelay,
            onTestProxyDelay = onTestProxyDelay,
            singleNodeTestEnabled = singleNodeTestEnabled,
            maxHeight = maxHeight,
        )
    }
}

@Composable
private fun NodeGroupBadgeChip(badge: GroupBadge, modifier: Modifier = Modifier) {
    val style = SemanticActionDefaults.style(badge.tone)
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(ProxyNodeGroupLayoutDefaults.BadgeCornerRadius))
                .background(style.containerColor)
                .padding(
                    horizontal = NodeCardDefaults.ChipHorizontalPadding,
                    vertical =
                        NodeCardDefaults.ChipVerticalPadding +
                            ProxyNodeGroupLayoutDefaults.BadgeExtraVerticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = badge.label,
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = NodeCardDefaults.ChipFontSize),
            color = style.contentColor,
        )
    }
}

@Composable
private fun ExpandedProxyGroupContent(
    group: ProxyGroupInfo,
    displayMode: ProxyDisplayMode,
    isDelayTesting: Boolean,
    testingProxyNames: Set<String>,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)?,
    onTestDelay: ((groupName: String) -> Unit)?,
    onTestProxyDelay: ((String) -> Unit)?,
    singleNodeTestEnabled: Boolean,
    maxHeight: Dp? = null,
) {
    val isSelectorGroup = group.type == Proxy.Type.Selector
    val interactionRole = if (isSelectorGroup) Role.RadioButton else Role.Button
    val clickLabel = if (isSelectorGroup) null else MLang.Proxy.Action.Test
    val actionChipLabel = if (isSelectorGroup) null else MLang.Proxy.Action.Test
    val onNodeClick: ((String) -> Unit)? =
        when {
            isSelectorGroup ->
                onSelectProxy?.let { selectProxy ->
                    { proxyName -> selectProxy(group.name, proxyName) }
                }

            else ->
                onTestProxyDelay?.let { testProxyDelay ->
                    { proxyName -> testProxyDelay(proxyName) }
                }
        }

    if (maxHeight != null) {
        NodeGrid(
            proxies = group.proxies,
            selectedProxyName = group.now,
            displayMode = displayMode,
            onProxyClick = onNodeClick,
            isDelayTesting = isDelayTesting,
            testingProxyNames = testingProxyNames,
            onSingleNodeTestClick = onTestProxyDelay,
            listStateKey = "inline_group_${group.name}",
            modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight),
            contentPadding = PaddingValues(AppTheme.spacing.none),
            singleNodeTestEnabled = singleNodeTestEnabled,
            interactionRole = interactionRole,
            onProxyClickLabel = clickLabel,
            actionChipLabel = actionChipLabel,
        )
        return
    }

    group.proxies.forEach { proxy ->
        NodeCard(
            proxy = proxy,
            isSelected = proxy.name == group.now,
            onClick = onNodeClick,
            isDelayTesting = isDelayTesting,
            isThisProxyTesting = proxy.name in testingProxyNames,
            onSingleNodeTestClick = onTestProxyDelay?.let { { it(proxy.name) } },
            showCountryFlag = true,
            singleNodeTestEnabled = singleNodeTestEnabled,
            interactionRole = interactionRole,
            onClickLabel = clickLabel,
            actionChipLabel = actionChipLabel,
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

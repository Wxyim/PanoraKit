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

package com.github.yumelira.yumebox.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.domain.model.ProxyGroupInfo
import com.github.yumelira.yumebox.domain.model.ProxyGroupStyle
import com.github.yumelira.yumebox.domain.model.ProxySortMode
import com.github.yumelira.yumebox.presentation.component.CenteredText
import com.github.yumelira.yumebox.presentation.component.LocalTopBarHazeState
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Close
import com.github.yumelira.yumebox.presentation.icon.yume.LayoutPanelLeft
import com.github.yumelira.yumebox.presentation.icon.yume.`List-chevrons-up-down`
import com.github.yumelira.yumebox.presentation.icon.yume.`Settings-2`
import com.github.yumelira.yumebox.presentation.icon.yume.Speed
import com.github.yumelira.yumebox.presentation.screen.node.NodeCard
import com.github.yumelira.yumebox.presentation.screen.node.NodeCardDefaults
import com.github.yumelira.yumebox.presentation.screen.node.NodeGroupStylePopup
import com.github.yumelira.yumebox.presentation.screen.node.NodeSortPopup
import com.github.yumelira.yumebox.presentation.screen.node.RotatingCircleGauge
import com.github.yumelira.yumebox.presentation.screen.node.nodeGroupItems
import com.github.yumelira.yumebox.presentation.screen.node.nodeLatencyLabel
import com.github.yumelira.yumebox.presentation.theme.LocalSpacing
import com.github.yumelira.yumebox.presentation.util.extractFlaggedName
import com.github.yumelira.yumebox.presentation.viewmodel.ProxyViewModel
import dev.chrisbanes.haze.hazeSource
import dev.oom_wg.purejoy.mlang.MLang
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperListPopup
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

private object ProxyPageSpacing {
    val ContentTop = 20.dp
    val ContentHorizontal = 12.dp
    val ItemVertical = 6.dp
}

private object FloatingPanelDefaults {
    val WidthFraction = 0.84f
    val MaxWidth = 520.dp
    val MaxHeight = 640.dp
    val ListMaxHeight = 460.dp
    val OuterPadding = 12.dp
    val CornerRadius = 22.dp
}

private object FloatingPanelMetrics {
    val HeaderCornerRadius = 18.dp
    val HeaderHorizontalPadding = 14.dp
    val HeaderVerticalPadding = 12.dp
    val HeaderSectionSpacing = 10.dp
    val HeaderMetaSpacing = 8.dp
    val HeaderMetaChipVerticalPadding = 3.dp
    val HeaderMetaCountFontSize = NodeCardDefaults.ChipFontSize
    val HeaderMetaChipFontSize = NodeCardDefaults.ChipFontSize
    val HeaderCloseIconSize = NodeCardDefaults.ChevronIconSize
    val HeaderActionSpacing = 4.dp
    val DetailLabelSpacing = 3.dp
    val CurrentBadgeTopPadding = 10.dp
    val CurrentBadgeEndPadding = 12.dp
    val CurrentBadgeHorizontalPadding = 8.dp
    val CurrentBadgeVerticalPadding = 3.dp
    val CurrentBadgeFontSize = NodeCardDefaults.ChipFontSize
}

@Composable
fun ProxyPager(
    mainInnerPadding: PaddingValues,
    onNavigateToProviders: () -> Unit,
    isActive: Boolean,
) {
    val proxyViewModel = koinViewModel<ProxyViewModel>()
    val context = LocalContext.current

    val proxyGroups by proxyViewModel.sortedProxyGroups.collectAsStateWithLifecycle()
    val uiState by proxyViewModel.uiState.collectAsStateWithLifecycle()
    val groupStyle by proxyViewModel.groupStyle.collectAsStateWithLifecycle()
    val testingGroupNames by proxyViewModel.testingGroupNames.collectAsStateWithLifecycle()
    val testingProxyNames by proxyViewModel.testingProxyNames.collectAsStateWithLifecycle()
    val sortMode by proxyViewModel.sortMode.collectAsStateWithLifecycle()
    val showHiddenGroups by proxyViewModel.showHiddenGroups.collectAsStateWithLifecycle()
    val singleNodeTest by proxyViewModel.singleNodeTest.collectAsStateWithLifecycle()
    val groupScrollBehavior = MiuixScrollBehavior()
    val topBarHazeState = LocalTopBarHazeState.current

    val showSortPopup = rememberSaveable { mutableStateOf(false) }
    val showStylePopup = rememberSaveable { mutableStateOf(false) }
    val showMorePopup = rememberSaveable { mutableStateOf(false) }

    var expandedGroupName by rememberSaveable { mutableStateOf<String?>(null) }
    var floatingGroupName by rememberSaveable { mutableStateOf<String?>(null) }
    var retainedFloatingGroup by remember { mutableStateOf<ProxyGroupInfo?>(null) }

    val expandedGroup by
        remember(expandedGroupName, proxyGroups) {
            derivedStateOf { proxyGroups.firstOrNull { it.name == expandedGroupName } }
        }
    val floatingGroup by
        remember(floatingGroupName, proxyGroups) {
            derivedStateOf { proxyGroups.firstOrNull { it.name == floatingGroupName } }
        }

    BackHandler(enabled = expandedGroupName != null || floatingGroupName != null) {
        expandedGroupName = null
        floatingGroupName = null
    }

    LaunchedEffect(groupStyle) {
        if (groupStyle == ProxyGroupStyle.FLOATING) {
            expandedGroupName = null
        } else {
            floatingGroupName = null
        }
    }

    LaunchedEffect(proxyGroups, floatingGroupName) {
        if (floatingGroupName != null && floatingGroup == null) {
            floatingGroupName = null
        }
    }

    LaunchedEffect(floatingGroup) {
        if (floatingGroup != null) {
            retainedFloatingGroup = floatingGroup
        }
    }

    LaunchedEffect(isActive) { proxyViewModel.ensureCoreLoaded(isActive) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            context.toast(error)
            proxyViewModel.clearError()
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            context.toast(message)
            proxyViewModel.clearMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ProxyTopBar(
                    title = MLang.Proxy.Title,
                    scrollBehavior = groupScrollBehavior,
                    showBack = false,
                    onBack = {},
                    onNavigateToProviders = onNavigateToProviders,
                    onTestDelay =
                        (floatingGroup ?: expandedGroup)?.let {
                            { proxyViewModel.testDelay(it.name) }
                        } ?: { proxyViewModel.testDelay() },
                    showSortPopup = showSortPopup,
                    showStylePopup = showStylePopup,
                    showMorePopup = showMorePopup,
                    sortMode = sortMode,
                    groupStyle = groupStyle,
                    showHiddenGroups = showHiddenGroups,
                    onSortSelected = proxyViewModel::setSortMode,
                    onGroupStyleSelected = proxyViewModel::setGroupStyle,
                    onShowHiddenGroupsChange = proxyViewModel::setShowHiddenGroups,
                )
            }
        ) {
            Box(
                modifier =
                    Modifier.fillMaxSize().let { mod ->
                        if (topBarHazeState != null) mod.hazeSource(state = topBarHazeState)
                        else mod
                    }
            ) {
                if (proxyGroups.isEmpty()) {
                    CenteredText(
                        firstLine = MLang.Proxy.Empty.NoNodes,
                        secondLine = MLang.Proxy.Empty.Hint,
                    )
                } else {
                    ProxyContent(
                        proxyGroups = proxyGroups,
                        groupStyle = groupStyle,
                        scrollBehavior = groupScrollBehavior,
                        innerPadding = it,
                        mainInnerPadding = mainInnerPadding,
                        testingGroupNames = testingGroupNames,
                        testingProxyNames = testingProxyNames,
                        expandedGroupName =
                            expandedGroupName.takeIf { groupStyle == ProxyGroupStyle.INLINE },
                        onGroupBoundsChanged = { _, _ -> },
                        onGroupClick = { group ->
                            when (groupStyle) {
                                ProxyGroupStyle.INLINE -> {
                                    floatingGroupName = null
                                    expandedGroupName =
                                        if (expandedGroupName == group.name) null else group.name
                                }

                                ProxyGroupStyle.FLOATING -> {
                                    expandedGroupName = null
                                    floatingGroupName =
                                        if (floatingGroupName == group.name) null else group.name
                                }
                            }
                        },
                        onSelectProxy = { groupName, proxyName ->
                            proxyViewModel.selectProxy(groupName, proxyName)
                            if (groupStyle == ProxyGroupStyle.FLOATING) {
                                floatingGroupName = null
                            }
                        },
                        onTestDelay = { groupName -> proxyViewModel.testDelay(groupName) },
                        onTestProxyDelay = { proxyName ->
                            proxyViewModel.testProxyDelay(proxyName)
                        },
                        singleNodeTestEnabled = singleNodeTest,
                    )
                }
            }
        }

        if (groupStyle == ProxyGroupStyle.FLOATING && retainedFloatingGroup != null) {
            FloatingGroupOverlay(
                visible = floatingGroup != null,
                group = retainedFloatingGroup!!,
                isDelayTesting = testingGroupNames.contains(retainedFloatingGroup!!.name),
                testingProxyNames = testingProxyNames,
                singleNodeTestEnabled = singleNodeTest,
                onDismiss = { floatingGroupName = null },
                onExited = {
                    if (floatingGroupName == null) {
                        retainedFloatingGroup = null
                    }
                },
                onTestDelay = { retainedFloatingGroup?.let { proxyViewModel.testDelay(it.name) } },
                onSelectProxy = { proxyName ->
                    val group = retainedFloatingGroup ?: return@FloatingGroupOverlay
                    if (group.type == Proxy.Type.Selector) {
                        proxyViewModel.selectProxy(group.name, proxyName)
                        floatingGroupName = null
                    } else {
                        proxyViewModel.testDelay(group.name)
                    }
                },
                onTestProxyDelay = proxyViewModel::testProxyDelay,
            )
        }
    }
}

@Composable
private fun ProxyTopBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    showBack: Boolean,
    onBack: () -> Unit,
    onNavigateToProviders: () -> Unit,
    onTestDelay: (() -> Unit)?,
    showSortPopup: MutableState<Boolean>,
    showStylePopup: MutableState<Boolean>,
    showMorePopup: MutableState<Boolean>,
    sortMode: ProxySortMode,
    groupStyle: ProxyGroupStyle,
    showHiddenGroups: Boolean,
    onSortSelected: (ProxySortMode) -> Unit,
    onGroupStyleSelected: (ProxyGroupStyle) -> Unit,
    onShowHiddenGroupsChange: (Boolean) -> Unit,
) {
    TopBar(
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (showBack) {
                    IconButton(modifier = Modifier.padding(start = 24.dp), onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = MLang.Proxy.Action.Back)
                    }
                }
            }
        },
        actions = {
            if (onTestDelay != null) {
                IconButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = { onTestDelay.invoke() },
                ) {
                    Icon(Yume.Speed, contentDescription = MLang.Proxy.Action.Test)
                }
            }
            Box {
                IconButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = { showStylePopup.value = true },
                ) {
                    Icon(Yume.LayoutPanelLeft, contentDescription = MLang.Proxy.Action.GroupStyle)
                }
                NodeGroupStylePopup(
                    show = showStylePopup,
                    onDismiss = { showStylePopup.value = false },
                    groupStyle = groupStyle,
                    alignment = PopupPositionProvider.Align.BottomEnd,
                    onStyleSelected = onGroupStyleSelected,
                )
            }
            Box {
                IconButton(
                    modifier = Modifier.padding(end = 16.dp),
                    onClick = { showSortPopup.value = true },
                ) {
                    Icon(
                        Yume.`List-chevrons-up-down`,
                        contentDescription = MLang.Proxy.Action.SortMode,
                    )
                }
                NodeSortPopup(
                    show = showSortPopup,
                    onDismiss = { showSortPopup.value = false },
                    sortMode = sortMode,
                    alignment = PopupPositionProvider.Align.BottomEnd,
                    onSortSelected = onSortSelected,
                )
            }
            Box {
                IconButton(
                    modifier = Modifier.padding(end = 24.dp),
                    onClick = { showMorePopup.value = true },
                ) {
                    Icon(Yume.`Settings-2`, contentDescription = MLang.Proxy.Action.More)
                }
                ProxyMorePopup(
                    show = showMorePopup,
                    onDismiss = { showMorePopup.value = false },
                    onOpenResources = onNavigateToProviders,
                    showHiddenGroups = showHiddenGroups,
                    onShowHiddenGroupsChange = onShowHiddenGroupsChange,
                )
            }
        },
    )
}

@Composable
private fun ProxyMorePopup(
    show: MutableState<Boolean>,
    onDismiss: () -> Unit,
    onOpenResources: () -> Unit,
    showHiddenGroups: Boolean,
    onShowHiddenGroupsChange: (Boolean) -> Unit,
) {
    SuperListPopup(
        show = show.value,
        alignment = PopupPositionProvider.Align.BottomEnd,
        onDismissRequest = onDismiss,
    ) {
        ListPopupColumn {
            DropdownImpl(
                text = MLang.Proxy.Action.Resources,
                optionSize = 2,
                isSelected = false,
                onSelectedIndexChange = {
                    onDismiss()
                    onOpenResources()
                },
                index = 0,
            )
            DropdownImpl(
                text = MLang.Proxy.Action.ShowHiddenGroups,
                optionSize = 2,
                isSelected = showHiddenGroups,
                onSelectedIndexChange = {
                    onShowHiddenGroupsChange(!showHiddenGroups)
                    onDismiss()
                },
                index = 1,
            )
        }
    }
}

@Composable
private fun ProxyContent(
    proxyGroups: List<ProxyGroupInfo>,
    groupStyle: ProxyGroupStyle,
    scrollBehavior: ScrollBehavior,
    innerPadding: PaddingValues,
    mainInnerPadding: PaddingValues,
    testingGroupNames: Set<String>,
    testingProxyNames: Set<String>,
    expandedGroupName: String?,
    onGroupBoundsChanged: (String, Rect) -> Unit,
    onGroupClick: (ProxyGroupInfo) -> Unit,
    onSelectProxy: (groupName: String, proxyName: String) -> Unit,
    onTestDelay: (groupName: String) -> Unit,
    onTestProxyDelay: (proxyName: String) -> Unit,
    singleNodeTestEnabled: Boolean,
) {
    val spacing = LocalSpacing.current
    ScreenLazyColumn(
        scrollBehavior = scrollBehavior,
        innerPadding = innerPadding,
        enableGlobalScroll = true,
        contentPadding =
            PaddingValues(
                start = ProxyPageSpacing.ContentHorizontal,
                end = ProxyPageSpacing.ContentHorizontal,
                top = innerPadding.calculateTopPadding() + ProxyPageSpacing.ContentTop,
                bottom = mainInnerPadding.calculateBottomPadding() + spacing.md,
            ),
    ) {
        nodeGroupItems(
            groups = proxyGroups,
            onGroupClick = onGroupClick,
            testingGroupNames = testingGroupNames,
            testingProxyNames = testingProxyNames,
            expandedGroupName = expandedGroupName,
            onGroupBoundsChanged =
                onGroupBoundsChanged.takeIf { groupStyle == ProxyGroupStyle.FLOATING },
            onSelectProxy = onSelectProxy,
            onTestDelay = onTestDelay,
            onTestProxyDelay = onTestProxyDelay,
            singleNodeTestEnabled = singleNodeTestEnabled,
            itemVerticalPadding = ProxyPageSpacing.ItemVertical,
        )
    }
}

@Composable
private fun FloatingGroupOverlay(
    visible: Boolean,
    group: ProxyGroupInfo,
    isDelayTesting: Boolean,
    testingProxyNames: Set<String>,
    singleNodeTestEnabled: Boolean,
    onDismiss: () -> Unit,
    onExited: () -> Unit,
    onTestDelay: () -> Unit,
    onSelectProxy: (String) -> Unit,
    onTestProxyDelay: (String) -> Unit,
) {
    val dismissInteraction = remember { MutableInteractionSource() }
    val visibilityState = remember { MutableTransitionState(false) }

    LaunchedEffect(visible) { visibilityState.targetState = visible }

    LaunchedEffect(visibilityState.currentState, visibilityState.targetState) {
        if (!visibilityState.currentState && !visibilityState.targetState) {
            onExited()
        }
    }

    val transition =
        rememberTransition(transitionState = visibilityState, label = "proxy_overlay_transition")
    val overlayAlpha by
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 180 else 320, easing = FastOutSlowInEasing)
            },
            label = "proxy_overlay_alpha",
        ) { shown ->
            if (shown) 0.18f else 0f
        }

    val panelAlpha by
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 200 else 220, easing = FastOutSlowInEasing)
            },
            label = "proxy_panel_alpha",
        ) { shown ->
            if (shown) 1f else 0f
        }

    val panelScale by
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 260 else 220, easing = FastOutSlowInEasing)
            },
            label = "proxy_panel_scale",
        ) { shown ->
            if (shown) 1f else 0.96f
        }

    val panelTranslationY by
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 280 else 220, easing = FastOutSlowInEasing)
            },
            label = "proxy_panel_translation_y",
        ) { shown ->
            if (shown) 0f else 96f
        }

    val contentAlpha by
        transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = if (targetState) 220 else 160,
                    delayMillis = if (targetState) 60 else 0,
                    easing = FastOutSlowInEasing,
                )
            },
            label = "proxy_panel_content_alpha",
        ) { shown ->
            if (shown) 1f else 0f
        }

    val panelShadowAlpha by
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 220 else 300, easing = FastOutSlowInEasing)
            },
            label = "proxy_panel_shadow_alpha",
        ) { shown ->
            if (shown) 0.08f else 0f
        }

    val panelBorderAlpha by
        transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = if (targetState) 200 else 260, easing = FastOutSlowInEasing)
            },
            label = "proxy_panel_border_alpha",
        ) { shown ->
            if (shown) 0.06f else 0f
        }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
                .clickable(
                    interactionSource = dismissInteraction,
                    indication = null,
                    onClick = onDismiss,
                )
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val panelShape = RoundedCornerShape(FloatingPanelDefaults.CornerRadius)
            Column(
                modifier =
                    Modifier.align(Alignment.Center)
                        .graphicsLayer {
                            alpha = panelAlpha
                            scaleX = panelScale
                            scaleY = panelScale
                            translationY = panelTranslationY
                        }
                        .widthIn(max = FloatingPanelDefaults.MaxWidth)
                        .fillMaxWidth(FloatingPanelDefaults.WidthFraction)
                        .heightIn(max = FloatingPanelDefaults.MaxHeight)
                        .shadow(
                            elevation = 16.dp,
                            shape = panelShape,
                            ambientColor = Color.Black.copy(alpha = panelShadowAlpha),
                            spotColor = Color.Black.copy(alpha = panelShadowAlpha),
                        )
                        .clip(panelShape)
                        .background(MiuixTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MiuixTheme.colorScheme.onSurface.copy(alpha = panelBorderAlpha),
                            shape = panelShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        )
                        .padding(FloatingPanelDefaults.OuterPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.graphicsLayer { alpha = contentAlpha },
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FloatingGroupHeader(
                        group = group,
                        isDelayTesting = isDelayTesting,
                        onTestDelay = onTestDelay,
                        onDismiss = onDismiss,
                    )

                    Spacer(
                        modifier =
                            Modifier.fillMaxWidth()
                                .heightIn(min = 1.dp, max = 1.dp)
                                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    )
                }

                LazyColumn(
                    modifier =
                        Modifier.graphicsLayer { alpha = contentAlpha }
                            .fillMaxWidth()
                            .heightIn(max = FloatingPanelDefaults.ListMaxHeight)
                            .overScrollVertical(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    overscrollEffect = null,
                ) {
                    items(group.proxies, key = { it.name }, contentType = { "FloatingNodeCard" }) {
                        proxy ->
                        FloatingProxyNodeCard(
                            proxy = proxy,
                            isSelected = proxy.name == group.now,
                            isDelayTesting = isDelayTesting,
                            isThisProxyTesting = proxy.name in testingProxyNames,
                            singleNodeTestEnabled = singleNodeTestEnabled,
                            onSelectProxy = onSelectProxy,
                            onTestProxyDelay = onTestProxyDelay,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingGroupHeader(
    group: ProxyGroupInfo,
    isDelayTesting: Boolean,
    onTestDelay: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentNode = remember(group.now) { extractFlaggedName(group.now) }
    val currentNodeName =
        remember(currentNode.displayName) {
            currentNode.displayName.ifBlank { MLang.Proxy.Mode.Direct }
        }
    val delayLabel =
        remember(group.proxies, group.now) {
            nodeLatencyLabel(group.proxies.firstOrNull { it.name == group.now }?.delay)
        }
    val primary = MiuixTheme.colorScheme.primary

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(FloatingPanelMetrics.HeaderCornerRadius))
                .background(MiuixTheme.colorScheme.background)
                .padding(
                    horizontal = FloatingPanelMetrics.HeaderHorizontalPadding,
                    vertical = FloatingPanelMetrics.HeaderVerticalPadding,
                ),
        verticalArrangement = Arrangement.spacedBy(FloatingPanelMetrics.HeaderSectionSpacing),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloatingPanelMetrics.HeaderSectionSpacing),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = group.name,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(FloatingPanelMetrics.HeaderMetaSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = group.type.name,
                        style =
                            MiuixTheme.textStyles.footnote1.copy(
                                fontSize = FloatingPanelMetrics.HeaderMetaChipFontSize
                            ),
                        color = primary,
                        modifier =
                            Modifier.clip(RoundedCornerShape(999.dp))
                                .background(primary.copy(alpha = 0.10f))
                                .padding(
                                    horizontal = NodeCardDefaults.ChipHorizontalPadding,
                                    vertical = FloatingPanelMetrics.HeaderMetaChipVerticalPadding,
                                ),
                    )
                    Text(
                        text = MLang.Proxy.Selection.NodeCount.format(group.proxies.size),
                        style =
                            MiuixTheme.textStyles.footnote1.copy(
                                fontSize = FloatingPanelMetrics.HeaderMetaCountFontSize
                            ),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(FloatingPanelMetrics.HeaderActionSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onTestDelay) {
                    if (isDelayTesting) {
                        RotatingCircleGauge(
                            isRotating = true,
                            tint = primary,
                            modifier = Modifier.size(FloatingPanelMetrics.HeaderCloseIconSize),
                            contentDescription = MLang.Proxy.Action.TestDelay,
                        )
                    } else {
                        Icon(
                            Yume.Speed,
                            contentDescription = MLang.Proxy.Action.TestDelay,
                            tint = primary,
                            modifier = Modifier.size(FloatingPanelMetrics.HeaderCloseIconSize),
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        Yume.Close,
                        contentDescription = MLang.Proxy.Action.Close,
                        tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.size(FloatingPanelMetrics.HeaderCloseIconSize),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(FloatingPanelMetrics.DetailLabelSpacing),
            ) {
                Text(
                    text = MLang.Proxy.Selection.CurrentNode,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Text(
                    text = currentNodeName,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                modifier =
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTestDelay,
                    ),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(FloatingPanelMetrics.DetailLabelSpacing),
            ) {
                Text(
                    text = MLang.Proxy.Selection.Latency,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                when {
                    delayLabel != null -> {
                        val (delayText, delayColor) = delayLabel
                        Text(
                            text = delayText,
                            style = MiuixTheme.textStyles.footnote1,
                            color = delayColor,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }

                    isDelayTesting -> {
                        Text(
                            text = MLang.Proxy.Testing.InProgress,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }

                    else -> {
                        Text(
                            text = MLang.Proxy.Selection.UnknownLatency,
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingProxyNodeCard(
    proxy: Proxy,
    isSelected: Boolean,
    isDelayTesting: Boolean,
    isThisProxyTesting: Boolean,
    singleNodeTestEnabled: Boolean,
    onSelectProxy: (String) -> Unit,
    onTestProxyDelay: (String) -> Unit,
) {
    val primary = MiuixTheme.colorScheme.primary
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isSelected) primary.copy(alpha = 0.09f) else Color.Transparent)
                .border(
                    width = if (isSelected) 1.dp else 0.dp,
                    color = if (isSelected) primary.copy(alpha = 0.24f) else Color.Transparent,
                    shape = RoundedCornerShape(24.dp),
                )
                .padding(2.dp)
    ) {
        NodeCard(
            proxy = proxy,
            isSelected = isSelected,
            onClick = { proxyName -> onSelectProxy(proxyName) },
            isDelayTesting = isDelayTesting,
            isThisProxyTesting = isThisProxyTesting,
            onSingleNodeTestClick = { onTestProxyDelay(proxy.name) },
            showCountryFlag = true,
            singleNodeTestEnabled = singleNodeTestEnabled,
            modifier = Modifier.fillMaxWidth(),
        )

        if (isSelected) {
            Text(
                text = MLang.Proxy.Selection.Current,
                style =
                    MiuixTheme.textStyles.footnote1.copy(
                        fontSize = FloatingPanelMetrics.CurrentBadgeFontSize
                    ),
                color = primary,
                modifier =
                    Modifier.align(Alignment.TopEnd)
                        .padding(
                            top = FloatingPanelMetrics.CurrentBadgeTopPadding,
                            end = FloatingPanelMetrics.CurrentBadgeEndPadding,
                        )
                        .clip(RoundedCornerShape(999.dp))
                        .background(primary.copy(alpha = 0.10f))
                        .padding(
                            horizontal = FloatingPanelMetrics.CurrentBadgeHorizontalPadding,
                            vertical = FloatingPanelMetrics.CurrentBadgeVerticalPadding,
                        ),
            )
        }
    }
}

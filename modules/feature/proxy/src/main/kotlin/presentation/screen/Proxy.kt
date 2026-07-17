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
 *
 */

package com.github.nomadboxlab.monadbox.presentation.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.domain.model.ProxyDisplayMode
import com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo
import com.github.nomadboxlab.monadbox.domain.model.ProxyGroupStyle
import com.github.nomadboxlab.monadbox.domain.model.ProxySortMode
import com.github.nomadboxlab.monadbox.presentation.component.AppActionTile
import com.github.nomadboxlab.monadbox.presentation.component.AppCircularIconAction
import com.github.nomadboxlab.monadbox.presentation.component.AppDialog
import com.github.nomadboxlab.monadbox.presentation.component.CenteredText
import com.github.nomadboxlab.monadbox.presentation.component.LocalTopBarHazeState
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.SemanticActionDefaults
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import com.github.nomadboxlab.monadbox.presentation.component.StatusBadge
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Close
import com.github.nomadboxlab.monadbox.presentation.icon.monad.LayoutPanelLeft
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`List-chevrons-up-down`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Scan-eye`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Settings-2`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Speed
import com.github.nomadboxlab.monadbox.presentation.screen.node.NodeCard
import com.github.nomadboxlab.monadbox.presentation.screen.node.RotatingCircleGauge
import com.github.nomadboxlab.monadbox.presentation.screen.node.adaptiveNodeGroupItems
import com.github.nomadboxlab.monadbox.presentation.screen.node.proxyLatencyVisual
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.LocalSpacing
import com.github.nomadboxlab.monadbox.presentation.theme.LocalWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.presentation.theme.ProxyDisplaySettingsLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.ProxyFloatingPanelHeaderLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.ProxyFloatingPanelLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.ProxyPageLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.ProxyRuntimePreviewLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.presentation.util.extractFlaggedName
import com.github.nomadboxlab.monadbox.presentation.util.resolveAdaptiveProxyDisplayMode
import com.github.nomadboxlab.monadbox.presentation.viewmodel.ProxyViewModel
import dev.chrisbanes.haze.hazeSource
import dev.oom_wg.purejoy.mlang.MLang
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

private object ProxyPageSpacing {
    val ContentTop = ProxyPageLayoutDefaults.ContentTop
    val ContentHorizontal = ProxyPageLayoutDefaults.ContentHorizontal
    val ItemVertical = ProxyPageLayoutDefaults.ItemVertical
}

private object FloatingPanelDefaults {
    val OuterPadding = ProxyFloatingPanelLayoutDefaults.OuterPadding
    val CornerRadius = ProxyFloatingPanelLayoutDefaults.CornerRadius
}

private object FloatingPanelMetrics {
    val HeaderCornerRadius = ProxyFloatingPanelHeaderLayoutDefaults.HeaderCornerRadius
    val HeaderHorizontalPadding = ProxyFloatingPanelHeaderLayoutDefaults.HeaderHorizontalPadding
    val HeaderVerticalPadding = ProxyFloatingPanelHeaderLayoutDefaults.HeaderVerticalPadding
    val HeaderSectionSpacing = ProxyFloatingPanelHeaderLayoutDefaults.HeaderSectionSpacing
    val HeaderTitleSpacing = ProxyFloatingPanelHeaderLayoutDefaults.HeaderTitleSpacing
    val HeaderMetaSpacing = ProxyFloatingPanelHeaderLayoutDefaults.HeaderMetaSpacing
    val HeaderMetaChipVerticalPadding =
        ProxyFloatingPanelHeaderLayoutDefaults.HeaderMetaChipVerticalPadding
    val HeaderMetaCountFontSize = ProxyFloatingPanelHeaderLayoutDefaults.HeaderMetaCountFontSize
    val HeaderMetaChipFontSize = ProxyFloatingPanelHeaderLayoutDefaults.HeaderMetaChipFontSize
    val HeaderCloseIconSize = ProxyFloatingPanelHeaderLayoutDefaults.HeaderCloseIconSize
    val HeaderCloseActionSize = ProxyFloatingPanelHeaderLayoutDefaults.HeaderCloseActionSize
    val HeaderActionSpacing = ProxyFloatingPanelHeaderLayoutDefaults.HeaderActionSpacing
    val DetailLabelSpacing = ProxyFloatingPanelHeaderLayoutDefaults.DetailLabelSpacing
    val DetailLatencyStartPadding = ProxyFloatingPanelHeaderLayoutDefaults.DetailLatencyStartPadding
    val CurrentBadgeCornerRadius = ProxyFloatingPanelHeaderLayoutDefaults.CurrentBadgeCornerRadius
    val CurrentBadgeTopPadding = ProxyFloatingPanelHeaderLayoutDefaults.CurrentBadgeTopPadding
    val CurrentBadgeEndPadding = ProxyFloatingPanelHeaderLayoutDefaults.CurrentBadgeEndPadding
    val CurrentBadgeHorizontalPadding =
        ProxyFloatingPanelHeaderLayoutDefaults.CurrentBadgeHorizontalPadding
    val CurrentBadgeVerticalPadding =
        ProxyFloatingPanelHeaderLayoutDefaults.CurrentBadgeVerticalPadding
    val CurrentBadgeFontSize = ProxyFloatingPanelHeaderLayoutDefaults.CurrentBadgeFontSize
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
    val isRunning by proxyViewModel.isRunning.collectAsStateWithLifecycle()
    val groupStyle by proxyViewModel.groupStyle.collectAsStateWithLifecycle()
    val testingGroupNames by proxyViewModel.testingGroupNames.collectAsStateWithLifecycle()
    val testingProxyNames by proxyViewModel.testingProxyNames.collectAsStateWithLifecycle()
    val sortMode by proxyViewModel.sortMode.collectAsStateWithLifecycle()
    val showHiddenGroups by proxyViewModel.showHiddenGroups.collectAsStateWithLifecycle()
    val singleNodeTest by proxyViewModel.singleNodeTest.collectAsStateWithLifecycle()
    val groupScrollBehavior = MiuixScrollBehavior()
    val topBarHazeState = LocalTopBarHazeState.current

    var showDisplaySettingsDialog by rememberSaveable { mutableStateOf(false) }

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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val adaptiveDisplayMode =
            remember(maxWidth, adaptiveInfo.prefersTwoPaneContent) {
                resolveAdaptiveProxyDisplayMode(
                    maxWidth = maxWidth,
                    prefersTwoPane = adaptiveInfo.prefersTwoPaneContent,
                )
            }

        Scaffold(
            topBar = {
                ProxyTopBar(
                    title = MLang.Proxy.Title,
                    scrollBehavior = groupScrollBehavior,
                    showBack = false,
                    onBack = {},
                    onTestDelay =
                        if (isRunning) {
                            floatingGroup?.let {
                                { proxyViewModel.testDelay(it.name, showStartMessage = false) }
                            }
                                ?: expandedGroup?.let { { proxyViewModel.testDelay(it.name) } }
                                ?: {
                                    proxyViewModel.testDelay()
                                }
                        } else {
                            null
                        },
                    showHiddenGroups = showHiddenGroups,
                    onOpenDisplaySettings = { showDisplaySettingsDialog = true },
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
                        displayMode = adaptiveDisplayMode,
                        groupStyle = groupStyle,
                        scrollBehavior = groupScrollBehavior,
                        innerPadding = it,
                        mainInnerPadding = mainInnerPadding,
                        isRunning = isRunning,
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
                        onTestDelay = { groupName ->
                                proxyViewModel.testDelay(
                                    groupName,
                                    showStartMessage = groupStyle != ProxyGroupStyle.FLOATING,
                                )
                            },
                        onTestProxyDelay = { proxyName -> proxyViewModel.testProxyDelay(proxyName) },
                        singleNodeTestEnabled = singleNodeTest && isRunning,
                    )
                }
            }
        }

        ProxyDisplaySettingsDialog(
            show = showDisplaySettingsDialog,
            groupStyle = groupStyle,
            sortMode = sortMode,
            showHiddenGroups = showHiddenGroups,
            onOpenResources = onNavigateToProviders,
            onGroupStyleSelected = proxyViewModel::setGroupStyle,
            onSortSelected = proxyViewModel::setSortMode,
            onShowHiddenGroupsChange = proxyViewModel::setShowHiddenGroups,
            onDismiss = { showDisplaySettingsDialog = false },
        )

        if (groupStyle == ProxyGroupStyle.FLOATING && retainedFloatingGroup != null) {
            FloatingGroupOverlay(
                visible = floatingGroup != null,
                group = retainedFloatingGroup!!,
                isDelayTesting = testingGroupNames.contains(retainedFloatingGroup!!.name),
                testingProxyNames = testingProxyNames,
                singleNodeTestEnabled = singleNodeTest && isRunning,
                onDismiss = { floatingGroupName = null },
                onExited = {
                    if (floatingGroupName == null) {
                        retainedFloatingGroup = null
                    }
                },
                onTestDelay = {
                        retainedFloatingGroup?.let {
                            proxyViewModel.testDelay(it.name, showStartMessage = false)
                        }
                    },
                onSelectProxy = { proxyName ->
                        val group = retainedFloatingGroup ?: return@FloatingGroupOverlay
                        if (group.type == Proxy.Type.Selector) {
                            proxyViewModel.selectProxy(group.name, proxyName)
                            floatingGroupName = null
                        } else {
                            proxyViewModel.testProxyDelay(proxyName)
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
    onTestDelay: (() -> Unit)?,
    showHiddenGroups: Boolean,
    onOpenDisplaySettings: () -> Unit,
) {
    TopBar(
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(ProxyPageLayoutDefaults.TopBarNavigationSpacing)
            ) {
                if (showBack) {
                    IconButton(
                        modifier =
                            Modifier.padding(
                                start = ProxyPageLayoutDefaults.TopBarBackStartPadding
                            ),
                        onClick = onBack,
                    ) {
                        Icon(MiuixIcons.Back, contentDescription = MLang.Proxy.Action.Back)
                    }
                }
            }
        },
        actions = {
            if (onTestDelay != null) {
                AppCircularIconAction(
                    imageVector = MonadIcons.Speed,
                    contentDescription = MLang.Proxy.Action.Test,
                    onClick = { onTestDelay.invoke() },
                    tone = SemanticTone.Info,
                    highEmphasis = true,
                    size = ProxyPageLayoutDefaults.TopBarActionSize,
                    iconSize = ProxyPageLayoutDefaults.TopBarActionIconSize,
                    modifier =
                        Modifier.padding(end = ProxyPageLayoutDefaults.TopBarInnerActionEndPadding),
                )
            }
            AppCircularIconAction(
                imageVector = MonadIcons.`Settings-2`,
                contentDescription = MLang.Proxy.Action.More,
                onClick = onOpenDisplaySettings,
                tone = if (showHiddenGroups) SemanticTone.Info else SemanticTone.Neutral,
                highEmphasis = showHiddenGroups,
                size = ProxyPageLayoutDefaults.TopBarActionSize,
                iconSize = ProxyPageLayoutDefaults.TopBarActionIconSize,
                modifier =
                    Modifier.padding(end = ProxyPageLayoutDefaults.TopBarOuterActionEndPadding),
            )
        },
    )
}

@Composable
private fun ProxyContent(
    proxyGroups: List<ProxyGroupInfo>,
    displayMode: ProxyDisplayMode,
    groupStyle: ProxyGroupStyle,
    scrollBehavior: ScrollBehavior,
    innerPadding: PaddingValues,
    mainInnerPadding: PaddingValues,
    isRunning: Boolean,
    testingGroupNames: Set<String>,
    testingProxyNames: Set<String>,
    expandedGroupName: String?,
    onGroupBoundsChanged: (String, Rect) -> Unit,
    onGroupClick: (ProxyGroupInfo) -> Unit,
    onSelectProxy: ((groupName: String, proxyName: String) -> Unit)?,
    onTestDelay: ((groupName: String) -> Unit)?,
    onTestProxyDelay: ((proxyName: String) -> Unit)?,
    singleNodeTestEnabled: Boolean,
) {
    val spacing = LocalSpacing.current
    val pageMetrics = AppTheme.pageMetrics
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val availableAdaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val groupColumns =
            when {
                maxWidth >= ProxyPageLayoutDefaults.ExpandedThreeColumnMinWidth -> 3
                !availableAdaptiveInfo.isCompactWidth -> 2
                else -> 1
            }
        val inlineExpandedMaxHeight =
            if (groupStyle == ProxyGroupStyle.INLINE) {
                pageMetrics.proxyFloatingPanelListMaxHeight
            } else {
                null
            }

        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = innerPadding,
            enableGlobalScroll = true,
            contentPadding =
                PaddingValues(
                    start = ProxyPageSpacing.ContentHorizontal,
                    end = ProxyPageSpacing.ContentHorizontal,
                    top = innerPadding.calculateTopPadding() + ProxyPageSpacing.ContentTop,
                    bottom =
                        mainInnerPadding.calculateBottomPadding() +
                            spacing.xl +
                            ProxyPageLayoutDefaults.ContentBottomExtra,
                ),
        ) {
            if (!isRunning) {
                item(key = "runtime_preview_notice", contentType = { "RuntimePreviewNotice" }) {
                    ProxyRuntimePreviewNotice(
                        modifier =
                            Modifier.fillMaxWidth().padding(bottom = ProxyPageSpacing.ItemVertical)
                    )
                }
            }

            adaptiveNodeGroupItems(
                groups = proxyGroups,
                columns = groupColumns,
                displayMode = displayMode,
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
                expandedContentMaxHeight = inlineExpandedMaxHeight,
                itemVerticalPadding = ProxyPageSpacing.ItemVertical,
            )
        }
    }
}

@Composable
private fun ProxyDisplaySettingsDialog(
    show: Boolean,
    groupStyle: ProxyGroupStyle,
    sortMode: ProxySortMode,
    showHiddenGroups: Boolean,
    onOpenResources: () -> Unit,
    onGroupStyleSelected: (ProxyGroupStyle) -> Unit,
    onSortSelected: (ProxySortMode) -> Unit,
    onShowHiddenGroupsChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val windowHeight =
        LocalWindowAdaptiveInfo.current.windowHeight.takeIf { it > AppTheme.spacing.none }
            ?: ProxyDisplaySettingsLayoutDefaults.FallbackWindowHeight
    val compactHeight = windowHeight < ProxyDisplaySettingsLayoutDefaults.CompactHeightThreshold
    val contentSpacing =
        if (compactHeight) {
            ProxyDisplaySettingsLayoutDefaults.ContentSpacingCompact
        } else {
            ProxyDisplaySettingsLayoutDefaults.ContentSpacingRegular
        }
    val sectionSpacing =
        if (compactHeight) {
            ProxyDisplaySettingsLayoutDefaults.SectionSpacingCompact
        } else {
            ProxyDisplaySettingsLayoutDefaults.SectionSpacingRegular
        }
    val dialogContentMaxHeight =
        (windowHeight * ProxyDisplaySettingsLayoutDefaults.ContentMaxHeightFraction).coerceAtMost(
            ProxyDisplaySettingsLayoutDefaults.ContentMaxHeightCap
        )

    AppDialog(show = show, title = MLang.Proxy.Action.More, onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(max = dialogContentMaxHeight)
                    .padding(bottom = sectionSpacing)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
        ) {
            AppActionTile(
                title = MLang.Proxy.Action.Resources,
                summary = MLang.Providers.Title,
                imageVector = MonadIcons.`Settings-2`,
                tone = SemanticTone.Info,
                onClick = {
                    onDismiss()
                    onOpenResources()
                },
                modifier = Modifier.fillMaxWidth(),
            )

            ProxyDialogSection(title = MLang.Proxy.Action.GroupStyle)
            Row(horizontalArrangement = Arrangement.spacedBy(sectionSpacing)) {
                ProxyGroupStyle.entries.forEach { style ->
                    ProxyChoiceTile(
                        title = style.displayName,
                        icon = MonadIcons.LayoutPanelLeft,
                        selected = style == groupStyle,
                        modifier = Modifier.weight(1f),
                        onClick = { onGroupStyleSelected(style) },
                    )
                }
            }

            ProxyDialogSection(
                title = MLang.Proxy.Action.SortMode,
                summary = MLang.Proxy.Action.SortModeSummary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(sectionSpacing)) {
                ProxySortMode.entries.forEach { mode ->
                    ProxyChoiceTile(
                        title = mode.displayName,
                        icon = MonadIcons.`List-chevrons-up-down`,
                        selected = mode == sortMode,
                        modifier = Modifier.fillMaxWidth(),
                        compact = false,
                        onClick = { onSortSelected(mode) },
                    )
                }
            }

            ProxyHiddenGroupsToggle(
                showHiddenGroups = showHiddenGroups,
                onClick = { onShowHiddenGroupsChange(!showHiddenGroups) },
            )
        }
    }
}

@Composable
private fun ProxyDialogSection(title: String, summary: String? = null) {
    Column(
        modifier = Modifier.padding(start = ProxyDisplaySettingsLayoutDefaults.SectionStartPadding),
        verticalArrangement =
            Arrangement.spacedBy(ProxyDisplaySettingsLayoutDefaults.SectionTextSpacing),
    ) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.primary,
        )
        if (!summary.isNullOrBlank()) {
            Text(
                text = summary,
                style = MiuixTheme.textStyles.footnote2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
    }
}

@Composable
private fun ProxyChoiceTile(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
) {
    AppActionTile(
        title = title,
        imageVector = icon,
        onClick = onClick,
        modifier = modifier,
        compact = compact,
        tone = if (selected) SemanticTone.Brand else SemanticTone.Neutral,
        highEmphasis = selected,
        minHeight =
            if (compact) {
                ProxyDisplaySettingsLayoutDefaults.ChoiceTileMinHeightCompact
            } else {
                ProxyDisplaySettingsLayoutDefaults.ChoiceTileMinHeightRegular
            },
    )
}

@Composable
private fun ProxyHiddenGroupsToggle(showHiddenGroups: Boolean, onClick: () -> Unit) {
    val spacing = AppTheme.spacing
    val strokes = AppTheme.strokes
    val tone = if (showHiddenGroups) SemanticTone.Info else SemanticTone.Neutral
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = showHiddenGroups)
    val shape = RoundedCornerShape(ProxyDisplaySettingsLayoutDefaults.ToggleContainerCornerRadius)

    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clip(shape)
                .background(style.containerColor, shape)
                .border(strokes.default, style.borderColor, shape)
                .toggleable(
                    value = showHiddenGroups,
                    role = Role.Switch,
                    onValueChange = { onClick() },
                )
                .padding(
                    horizontal = ProxyDisplaySettingsLayoutDefaults.ToggleContentHorizontalPadding,
                    vertical = ProxyDisplaySettingsLayoutDefaults.ToggleContentVerticalPadding,
                ),
        horizontalArrangement =
            Arrangement.spacedBy(ProxyDisplaySettingsLayoutDefaults.ToggleContentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(ProxyDisplaySettingsLayoutDefaults.ToggleIconContainerSize)
                    .clip(
                        RoundedCornerShape(
                            ProxyDisplaySettingsLayoutDefaults.ToggleIconCornerRadius
                        )
                    )
                    .background(style.iconContainerColor)
                    .border(
                        strokes.default,
                        style.contentColor.copy(alpha = 0.18f),
                        RoundedCornerShape(
                            ProxyDisplaySettingsLayoutDefaults.ToggleIconCornerRadius
                        ),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = MonadIcons.`Scan-eye`,
                contentDescription = null,
                tint = style.contentColor,
                modifier = Modifier.size(ProxyDisplaySettingsLayoutDefaults.ToggleIconSize),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(ProxyDisplaySettingsLayoutDefaults.ToggleLabelSpacing),
        ) {
            Text(
                text = MLang.Proxy.Action.ShowHiddenGroups,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text =
                    if (showHiddenGroups) {
                        MLang.Component.Selector.Enable
                    } else {
                        MLang.Component.Selector.Disable
                    },
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        StatusBadge(
            text =
                if (showHiddenGroups) {
                    MLang.Component.Selector.Enable
                } else {
                    MLang.Component.Selector.Disable
                },
            tone = tone,
            leadingDot = showHiddenGroups,
            compact = true,
        )
    }
}

@Composable
private fun ProxyRuntimePreviewNotice(modifier: Modifier = Modifier) {
    val primary = MiuixTheme.colorScheme.primary
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(ProxyRuntimePreviewLayoutDefaults.CornerRadius))
                .background(primary.copy(alpha = 0.08f))
                .border(
                    width = ProxyRuntimePreviewLayoutDefaults.BorderWidth,
                    color = primary.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(ProxyRuntimePreviewLayoutDefaults.CornerRadius),
                )
                .padding(
                    horizontal = ProxyRuntimePreviewLayoutDefaults.PaddingHorizontal,
                    vertical = ProxyRuntimePreviewLayoutDefaults.PaddingVertical,
                ),
        verticalArrangement = Arrangement.spacedBy(ProxyRuntimePreviewLayoutDefaults.ContentSpacing),
    ) {
        Text(
            text = MLang.Providers.Empty.NotRunning,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurface,
        )
        Text(
            text = MLang.Service.Tile.ClickToStartProxy,
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
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
    onTestDelay: (() -> Unit)?,
    onSelectProxy: ((String) -> Unit)?,
    onTestProxyDelay: ((String) -> Unit)?,
) {
    val pageMetrics = AppTheme.pageMetrics
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                        .widthIn(max = pageMetrics.proxyFloatingPanelMaxWidth)
                        .fillMaxWidth(pageMetrics.proxyFloatingPanelWidthFraction)
                        .heightIn(max = pageMetrics.proxyFloatingPanelMaxHeight)
                        .shadow(
                            elevation = ProxyFloatingPanelLayoutDefaults.ShadowElevation,
                            shape = panelShape,
                            ambientColor = Color.Black.copy(alpha = panelShadowAlpha),
                            spotColor = Color.Black.copy(alpha = panelShadowAlpha),
                        )
                        .clip(panelShape)
                        .background(MiuixTheme.colorScheme.surface)
                        .border(
                            width = ProxyFloatingPanelLayoutDefaults.BorderWidth,
                            color = MiuixTheme.colorScheme.onSurface.copy(alpha = panelBorderAlpha),
                            shape = panelShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        )
                        .padding(FloatingPanelDefaults.OuterPadding),
                verticalArrangement =
                    Arrangement.spacedBy(ProxyFloatingPanelLayoutDefaults.ContentSpacing),
            ) {
                Column(
                    modifier = Modifier.graphicsLayer { alpha = contentAlpha },
                    verticalArrangement =
                        Arrangement.spacedBy(ProxyFloatingPanelLayoutDefaults.ContentSpacing),
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
                                .heightIn(
                                    min = ProxyFloatingPanelLayoutDefaults.DividerThickness,
                                    max = ProxyFloatingPanelLayoutDefaults.DividerThickness,
                                )
                                .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    )
                }

                LazyColumn(
                    modifier =
                        Modifier.graphicsLayer { alpha = contentAlpha }
                            .fillMaxWidth()
                            .heightIn(max = pageMetrics.proxyFloatingPanelListMaxHeight)
                            .selectableGroup()
                            .overScrollVertical(),
                    verticalArrangement =
                        Arrangement.spacedBy(ProxyFloatingPanelLayoutDefaults.ListItemSpacing),
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
                            interactionRole =
                                if (group.type == Proxy.Type.Selector) Role.RadioButton
                                else Role.Button,
                            onClickLabel =
                                if (group.type == Proxy.Type.Selector) null
                                else MLang.Proxy.Action.Test,
                            actionChipLabel =
                                if (group.type == Proxy.Type.Selector) null
                                else MLang.Proxy.Action.Test,
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
    onTestDelay: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    val currentNode = remember(group.now) { extractFlaggedName(group.now) }
    val currentNodeName =
        remember(currentNode.displayName) {
            currentNode.displayName.ifBlank { MLang.Proxy.Mode.Direct }
        }
    val currentDelay =
        remember(group.proxies, group.now) {
            group.proxies.firstOrNull { it.name == group.now }?.delay
        }
    val latencyVisual = proxyLatencyVisual(delay = currentDelay, isTesting = isDelayTesting)
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
                verticalArrangement = Arrangement.spacedBy(FloatingPanelMetrics.HeaderTitleSpacing),
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
                    StatusBadge(
                        text = group.type.name,
                        tone =
                            if (group.type == Proxy.Type.Selector) {
                                SemanticTone.Brand
                            } else {
                                SemanticTone.Info
                            },
                        compact = true,
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
                if (onTestDelay != null) {
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
                                MonadIcons.Speed,
                                contentDescription = MLang.Proxy.Action.TestDelay,
                                tint = primary,
                                modifier = Modifier.size(FloatingPanelMetrics.HeaderCloseIconSize),
                            )
                        }
                    }
                }

                AppCircularIconAction(
                    imageVector = MonadIcons.Close,
                    contentDescription = MLang.Proxy.Action.Close,
                    onClick = onDismiss,
                    tone = SemanticTone.Neutral,
                    size = FloatingPanelMetrics.HeaderCloseActionSize,
                    iconSize = FloatingPanelMetrics.HeaderCloseIconSize,
                )
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
                    Modifier.let { base ->
                        if (onTestDelay != null) {
                            base.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onTestDelay,
                            )
                        } else {
                            base
                        }
                    },
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(FloatingPanelMetrics.DetailLabelSpacing),
            ) {
                Text(
                    text = MLang.Proxy.Selection.Latency,
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Text(
                    text = latencyVisual.label,
                    style = MiuixTheme.textStyles.footnote1,
                    color = latencyVisual.color,
                    modifier =
                        Modifier.padding(start = FloatingPanelMetrics.DetailLatencyStartPadding),
                )
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
    onSelectProxy: ((String) -> Unit)?,
    onTestProxyDelay: ((String) -> Unit)?,
    interactionRole: Role,
    onClickLabel: String?,
    actionChipLabel: String?,
) {
    val spacing = AppTheme.spacing
    val primary = MiuixTheme.colorScheme.primary
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(ProxyFloatingPanelLayoutDefaults.SelectedCardCornerRadius))
                .background(if (isSelected) primary.copy(alpha = 0.09f) else Color.Transparent)
                .border(
                    width =
                        if (isSelected) {
                            ProxyFloatingPanelLayoutDefaults.SelectedCardBorderWidth
                        } else {
                            spacing.none
                        },
                    color = if (isSelected) primary.copy(alpha = 0.24f) else Color.Transparent,
                    shape =
                        RoundedCornerShape(
                            ProxyFloatingPanelLayoutDefaults.SelectedCardCornerRadius
                        ),
                )
                .padding(ProxyFloatingPanelLayoutDefaults.SelectedCardInnerPadding)
    ) {
        NodeCard(
            proxy = proxy,
            isSelected = isSelected,
            onClick = onSelectProxy?.let { selectProxy -> { proxyName -> selectProxy(proxyName) } },
            isDelayTesting = isDelayTesting,
            isThisProxyTesting = isThisProxyTesting,
            onSingleNodeTestClick =
                onTestProxyDelay?.let { testProxyDelay -> { testProxyDelay(proxy.name) } },
            showCountryFlag = true,
            singleNodeTestEnabled = singleNodeTestEnabled,
            interactionRole = interactionRole,
            onClickLabel = onClickLabel,
            actionChipLabel = actionChipLabel,
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
                        .clip(RoundedCornerShape(FloatingPanelMetrics.CurrentBadgeCornerRadius))
                        .background(primary.copy(alpha = 0.10f))
                        .padding(
                            horizontal = FloatingPanelMetrics.CurrentBadgeHorizontalPadding,
                            vertical = FloatingPanelMetrics.CurrentBadgeVerticalPadding,
                        ),
            )
        }
    }
}

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

package com.github.nomadboxlab.monadbox.screen.connection

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.feature.meta.presentation.component.ConnectionCard
import com.github.nomadboxlab.monadbox.feature.meta.presentation.component.ConnectionDetailSheet
import com.github.nomadboxlab.monadbox.feature.meta.presentation.component.TabRowWithContour
import com.github.nomadboxlab.monadbox.feature.meta.presentation.viewmodel.ConnectionSort
import com.github.nomadboxlab.monadbox.feature.meta.presentation.viewmodel.ConnectionTab
import com.github.nomadboxlab.monadbox.feature.meta.presentation.viewmodel.ConnectionViewModel
import com.github.nomadboxlab.monadbox.presentation.component.NavigationBackIcon
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.ConnectionScreenLayoutDefaults
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperListPopup
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Sort
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val SortModes =
    listOf(ConnectionSort.Time, ConnectionSort.Upload, ConnectionSort.Download, ConnectionSort.Host)

private fun ConnectionSort.getDisplayName(): String =
    when (this) {
        ConnectionSort.Time -> MLang.Connection.Sort.Time
        ConnectionSort.Upload -> MLang.Connection.Sort.Upload
        ConnectionSort.Download -> MLang.Connection.Sort.Download
        ConnectionSort.Host -> MLang.Connection.Sort.Host
    }

@Destination<RootGraph>
@Composable
fun ConnectionScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<ConnectionViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filteredConnections by viewModel.filteredConnections.collectAsStateWithLifecycle()

    val scrollBehavior = MiuixScrollBehavior()
    var showSearchBar by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(state.searchQuery) }
    var showSortPopup by remember { mutableStateOf(false) }

    var selectedConnection by remember { mutableStateOf<ConnectionInfo?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }

    val tabs = listOf(MLang.Connection.Tab.Active, MLang.Connection.Tab.Closed)
    var selectedTabIndex by remember { mutableStateOf(0) }

    val spacing = AppTheme.spacing

    LaunchedEffect(selectedTabIndex) {
        val tab = if (selectedTabIndex == 0) ConnectionTab.ACTIVE else ConnectionTab.CLOSED
        viewModel.setTab(tab)
    }

    LaunchedEffect(searchText) {
        if (searchText != state.searchQuery) {
            viewModel.setSearchQuery(searchText)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.Connection.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                    )
                },
                actions = {
                    Box {
                        IconButton(
                            modifier = Modifier.padding(end = spacing.sm),
                            onClick = { showSortPopup = true },
                        ) {
                            Icon(
                                imageVector = MiuixIcons.Sort,
                                contentDescription = MLang.Component.Navigation.Sort,
                                tint = MiuixTheme.colorScheme.onSurface,
                            )
                        }
                        val selectedSortIndex = SortModes.indexOf(state.sortBy).coerceAtLeast(0)
                        SuperListPopup(
                            show = showSortPopup,
                            alignment = PopupPositionProvider.Align.BottomEnd,
                            onDismissRequest = { showSortPopup = false },
                        ) {
                            ListPopupColumn {
                                SortModes.forEachIndexed { index, mode ->
                                    DropdownImpl(
                                        text = mode.getDisplayName(),
                                        optionSize = SortModes.size,
                                        isSelected = selectedSortIndex == index,
                                        onSelectedIndexChange = {
                                            if (mode != state.sortBy) viewModel.setSortBy(mode)
                                            showSortPopup = false
                                        },
                                        index = index,
                                    )
                                }
                            }
                        }
                    }
                    IconButton(
                        modifier = Modifier.padding(end = spacing.xxxl),
                        onClick = { showSearchBar = !showSearchBar },
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Search,
                            contentDescription = MLang.Component.Navigation.Search,
                            tint = MiuixTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
            val contentMaxWidth = adaptiveInfo.preferredSinglePaneMaxWidth
            ScreenLazyColumn(
                modifier = Modifier.adaptiveContentWidth(contentMaxWidth),
                scrollBehavior = scrollBehavior,
                innerPadding = innerPadding,
                contentPadding =
                    PaddingValues(
                        start = spacing.md,
                        end = spacing.md,
                        top = innerPadding.calculateTopPadding() + spacing.xl,
                        bottom = innerPadding.calculateBottomPadding(),
                    ),
            ) {
                item {
                    TabRowWithContour(
                        tabs = tabs,
                        selectedTabIndex = selectedTabIndex,
                        onTabSelected = { selectedTabIndex = it },
                    )
                }

                item {
                    AnimatedVisibility(
                        visible = showSearchBar,
                        enter =
                            expandVertically(
                                animationSpec = tween(200, easing = FastOutSlowInEasing),
                                expandFrom = Alignment.Top,
                            ) + fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing)),
                        exit =
                            shrinkVertically(
                                animationSpec = tween(200, easing = FastOutSlowInEasing),
                                shrinkTowards = Alignment.Top,
                            ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier.weight(1f),
                                label = MLang.Connection.SearchHint,
                                singleLine = true,
                            )
                        }
                    }
                }

                if (filteredConnections.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(spacing.xxxl),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text =
                                    if (state.isLoading) {
                                        MLang.Connection.Loading
                                    } else if (state.searchQuery.isNotEmpty()) {
                                        MLang.Connection.NoResults
                                    } else {
                                        MLang.Connection.Empty
                                    },
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                    }
                } else {

                    items(items = filteredConnections, key = { it.id }) { connection ->
                        ConnectionCard(
                            connectionInfo = connection,
                            onClick = {
                                selectedConnection = connection
                                showDetailSheet = true
                            },
                            modifier =
                                Modifier.padding(
                                    vertical = ConnectionScreenLayoutDefaults.ItemVerticalPadding
                                ),
                        )
                    }
                }
            }
        }

        ConnectionDetailSheet(
            show = showDetailSheet,
            connectionInfo = selectedConnection,
            onDismiss = { showDetailSheet = false },
            onDismissFinished = { selectedConnection = null },
        )
    }
}

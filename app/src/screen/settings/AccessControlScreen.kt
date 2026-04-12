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

package com.github.yumelira.yumebox.screen.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.OemPermissionSettingsNavigator
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.presentation.component.AppActionBottomSheet
import com.github.yumelira.yumebox.presentation.component.AppCommandButton
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.ConfigActionMenuRow
import com.github.yumelira.yumebox.presentation.component.ConfigEntryActionOption
import com.github.yumelira.yumebox.presentation.component.DialogButtonRow
import com.github.yumelira.yumebox.presentation.component.EnumSelector
import com.github.yumelira.yumebox.presentation.component.NavigationBackIcon
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Badge-plus`
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import com.github.yumelira.yumebox.presentation.icon.yume.`Settings-2`
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object AccessControlMetrics {
    val SearchOverlayMaxWidth = 760.dp
    val SearchHorizontalPadding = 16.dp
    val SearchTopPadding = 12.dp
    val SearchResultCornerRadius = 16.dp
    val ListCardVerticalPadding = 4.dp
    val SearchEntryVerticalPadding = 6.dp
    val SearchResultAppIconSize = 40.dp
    val AppCardIconSize = 45.dp
    val AppIconBitmapBaseSize = 80
    val AppIconEndPadding = 12.dp
}

@Composable
@Destination<RootGraph>
fun AccessControlScreen(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    val viewModel = koinViewModel<AccessControlViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val isApplying by viewModel.isApplying.collectAsStateWithLifecycle()

    val showSettingsSheet = rememberSaveable { mutableStateOf(false) }
    val searchExpanded = rememberSaveable { mutableStateOf(false) }
    val sortedSelectedPackages =
        remember(uiState.selectedPackages) { uiState.selectedPackages.toList().sorted() }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    viewModel.onPermissionResult()
                }
            },
        )

    BackHandler(enabled = searchExpanded.value) {
        searchExpanded.value = false
        viewModel.onSearchQueryChange("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopBar(
                    title = MLang.AccessControl.Title,
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        NavigationBackIcon(
                            navigator = navigator,
                            contentDescription = MLang.Component.Navigation.Back,
                        )
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier.padding(end = 24.dp),
                            onClick = { showSettingsSheet.value = true },
                        ) {
                            Icon(
                                Yume.`Settings-2`,
                                contentDescription = MLang.Component.Navigation.Settings,
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        MLang.AccessControl.AppList.Loading,
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                }
            } else {
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
                        topPadding = 20.dp,
                    ) {
                        if (uiState.needsMiuiPermission) {
                            item {
                                Card {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text(MLang.Onboarding.Permission.AppList.Title)
                                        Text(
                                            MLang.Onboarding.Permission.AppList.SummaryNeed,
                                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                        )
                                        AppCommandButton(
                                            title = MLang.Onboarding.Permission.AppList.Title,
                                            imageVector = Yume.Check,
                                            onClick = {
                                                permissionLauncher.launch(
                                                    "com.android.permission.GET_INSTALLED_APPS"
                                                )
                                            },
                                            tone = SemanticTone.Brand,
                                            highEmphasis = true,
                                        )
                                        AppCommandButton(
                                            title =
                                                MLang.AccessControl.AppList.OpenPermissionSettings,
                                            imageVector = Yume.`Settings-2`,
                                            onClick = {
                                                val opened =
                                                    OemPermissionSettingsNavigator
                                                        .openBackgroundPermissionSettings(context)
                                                if (!opened) {
                                                    context.toast(
                                                        MLang.AccessControl.AppList
                                                            .PermissionSettingsUnavailable
                                                    )
                                                }
                                            },
                                            tone = SemanticTone.Info,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (!uiState.canBrowseApps) {
                            item {
                                Card {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text(
                                            if (uiState.needsMiuiPermission) {
                                                MLang.AccessControl.AppList
                                                    .BrowseUnavailablePermission
                                            } else {
                                                MLang.AccessControl.AppList.BrowseUnavailableManual
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        item {
                            if (uiState.canBrowseApps) {
                                Card(
                                    modifier =
                                        Modifier.padding(
                                            vertical =
                                                AccessControlMetrics.SearchEntryVerticalPadding
                                        )
                                ) {
                                    BasicComponent(
                                        title =
                                            uiState.searchQuery.ifBlank {
                                                MLang.AccessControl.Search.Placeholder
                                            },
                                        summary =
                                            MLang.AccessControl.AppList.Title.format(
                                                filteredApps.size
                                            ),
                                        onClick = { searchExpanded.value = true },
                                    )
                                }
                            }
                        }

                        item {
                            SmallTitle(
                                MLang.AccessControl.AppList.Title.format(
                                    uiState.selectedPackages.size
                                )
                            )
                        }

                        if (!uiState.canBrowseApps) {
                            item {
                                Card {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Text(MLang.AccessControl.AppList.ManualAddTitle)
                                        TextField(
                                            value = uiState.manualPackageName,
                                            onValueChange = viewModel::onManualPackageNameChange,
                                            label =
                                                MLang.AccessControl.AppList.ManualAddPlaceholder,
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions =
                                                androidx.compose.foundation.text.KeyboardOptions(
                                                    imeAction = ImeAction.Done
                                                ),
                                            keyboardActions =
                                                androidx.compose.foundation.text.KeyboardActions(
                                                    onDone = {
                                                        if (!viewModel.addManualPackage()) {
                                                            context.toast(
                                                                MLang.AccessControl.AppList
                                                                    .InvalidPackage
                                                            )
                                                        }
                                                    }
                                                ),
                                        )
                                        AppCommandButton(
                                            title = MLang.AccessControl.AppList.AddPackage,
                                            imageVector = Yume.`Badge-plus`,
                                            onClick = {
                                                if (!viewModel.addManualPackage()) {
                                                    context.toast(
                                                        MLang.AccessControl.AppList.InvalidPackage
                                                    )
                                                }
                                            },
                                            tone = SemanticTone.Brand,
                                            highEmphasis = true,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            item {
                                SmallTitle(
                                    MLang.AccessControl.AppList.SelectedPackagesTitle.format(
                                        uiState.selectedPackages.size
                                    )
                                )
                            }

                            if (sortedSelectedPackages.isEmpty()) {
                                item {
                                    Card {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            Text(
                                                MLang.AccessControl.AppList.NoSelectedPackages,
                                                color =
                                                    MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(items = sortedSelectedPackages, key = { it }) { packageName ->
                                    Card(
                                        modifier =
                                            Modifier.padding(
                                                vertical =
                                                    AccessControlMetrics.ListCardVerticalPadding
                                            )
                                    ) {
                                        BasicComponent(
                                            title = packageName,
                                            summary = MLang.Connection.Detail.PackageName,
                                            endActions = {
                                                Checkbox(
                                                    state = ToggleableState(true),
                                                    onClick = {
                                                        viewModel.onAppSelectionChange(
                                                            packageName,
                                                            false,
                                                        )
                                                    },
                                                )
                                            },
                                            onClick = {
                                                viewModel.onAppSelectionChange(packageName, false)
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        if (uiState.canBrowseApps) {
                            items(items = filteredApps, key = { it.packageName }) { app ->
                                AppCard(
                                    app = app,
                                    onSelectionChange = { checked ->
                                        viewModel.onAppSelectionChange(app.packageName, checked)
                                    },
                                    onClick = {
                                        viewModel.onAppSelectionChange(
                                            app.packageName,
                                            !app.isSelected,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }

            AppActionBottomSheet(
                show = showSettingsSheet.value,
                modifier = Modifier,
                title = MLang.AccessControl.Settings.Title,
                onDismissRequest = { showSettingsSheet.value = false },
                enableNestedScroll = true,
                content = {
                    val context = LocalContext.current
                    val clipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    Column {
                        top.yukonga.miuix.kmp.basic.Card {
                            SuperSwitch(
                                title = MLang.AccessControl.Settings.ShowSystemApps,
                                checked = uiState.showSystemApps,
                                onCheckedChange = { viewModel.onShowSystemAppsChange(it) },
                            )
                            SuperSwitch(
                                title = MLang.AccessControl.Settings.SelectedFirst,
                                checked = uiState.selectedFirst,
                                onCheckedChange = { viewModel.onSelectedFirstChange(it) },
                            )
                            EnumSelector(
                                title = MLang.AccessControl.Settings.SortMode,
                                currentValue = uiState.sortMode,
                                items = AccessControlSortMode.entries.map { it.displayName },
                                values = AccessControlSortMode.entries.toList(),
                                onValueChange = viewModel::onSortModeChange,
                            )
                            ConfigActionMenuRow(
                                title = MLang.AccessControl.Settings.BatchOperation,
                                summary =
                                    if (uiState.canBrowseApps) {
                                        MLang.AccessControl.Settings.BatchOperationSummaryBrowse
                                    } else {
                                        MLang.AccessControl.Settings.BatchOperationSummaryManual
                                    },
                                options =
                                    if (uiState.canBrowseApps) {
                                        listOf(
                                            ConfigEntryActionOption(
                                                title = MLang.AccessControl.Settings.SelectAll,
                                                icon = Yume.`Settings-2`,
                                                onClick = { viewModel.selectAll() },
                                            ),
                                            ConfigEntryActionOption(
                                                title = MLang.AccessControl.Settings.DeselectAll,
                                                icon = Yume.`Settings-2`,
                                                onClick = { viewModel.deselectAll() },
                                            ),
                                            ConfigEntryActionOption(
                                                title = MLang.AccessControl.Settings.Invert,
                                                icon = Yume.`Settings-2`,
                                                onClick = { viewModel.invertSelection() },
                                            ),
                                        )
                                    } else {
                                        listOf(
                                            ConfigEntryActionOption(
                                                title = MLang.AccessControl.Settings.CopySelected,
                                                icon = Yume.`Settings-2`,
                                                onClick = {
                                                    val exportText = viewModel.exportPackages()
                                                    val clip =
                                                        ClipData.newPlainText(
                                                            "packages",
                                                            exportText,
                                                        )
                                                    clipboardManager.setPrimaryClip(clip)
                                                    context.toast(
                                                        MLang.AccessControl.Settings.ExportSuccess
                                                            .format(uiState.selectedPackages.size)
                                                    )
                                                },
                                            ),
                                            ConfigEntryActionOption(
                                                title = MLang.AccessControl.Settings.ClearSelected,
                                                icon = Yume.`Settings-2`,
                                                onClick = {
                                                    val cleared = viewModel.clearSelectedPackages()
                                                    context.toast(
                                                        MLang.AccessControl.Settings
                                                            .ClearSelectedResult
                                                            .format(cleared)
                                                    )
                                                },
                                            ),
                                        )
                                    },
                            )
                            ConfigActionMenuRow(
                                title = MLang.AccessControl.Settings.RegionQuickSelect,
                                summary =
                                    if (uiState.canBrowseApps) {
                                        MLang.AccessControl.Settings.RegionQuickSelectSummaryBrowse
                                    } else {
                                        MLang.AccessControl.Settings.RegionQuickSelectSummaryManual
                                    },
                                options =
                                    listOf(
                                        ConfigEntryActionOption(
                                            title = MLang.AccessControl.Settings.ChinaApps,
                                            icon = Yume.`Settings-2`,
                                            onClick = {
                                                val selectedCount =
                                                    viewModel.selectChinaAppsInCurrentList()
                                                context.toast(
                                                    MLang.AccessControl.Settings.RegionSelectResult
                                                        .format(
                                                            MLang.AccessControl.Settings.ChinaApps,
                                                            selectedCount,
                                                        )
                                                )
                                            },
                                        ),
                                        ConfigEntryActionOption(
                                            title = MLang.AccessControl.Settings.OverseasApps,
                                            icon = Yume.`Settings-2`,
                                            onClick = {
                                                val selectedCount =
                                                    viewModel.selectNonChinaAppsInCurrentList()
                                                context.toast(
                                                    MLang.AccessControl.Settings.RegionSelectResult
                                                        .format(
                                                            MLang.AccessControl.Settings
                                                                .OverseasApps,
                                                            selectedCount,
                                                        )
                                                )
                                            },
                                        ),
                                    ),
                            )
                            ConfigActionMenuRow(
                                title = MLang.AccessControl.Settings.ImportExport,
                                summary =
                                    if (uiState.canBrowseApps) {
                                        MLang.AccessControl.Settings.ImportExportSummaryBrowse
                                    } else {
                                        MLang.AccessControl.Settings.ImportExportSummaryManual
                                    },
                                options =
                                    listOf(
                                        ConfigEntryActionOption(
                                            title = MLang.AccessControl.Settings.Import,
                                            icon = Yume.`Settings-2`,
                                            onClick = {
                                                val clipData = clipboardManager.primaryClip
                                                val text =
                                                    if (
                                                        clipData != null && clipData.itemCount > 0
                                                    ) {
                                                        clipData.getItemAt(0)?.text?.toString()
                                                            ?: ""
                                                    } else {
                                                        ""
                                                    }
                                                if (text.isNotEmpty()) {
                                                    val result = viewModel.importPackages(text)
                                                    val message =
                                                        if (result.ignoredCount > 0) {
                                                            MLang.AccessControl.Settings
                                                                .ImportPartial
                                                                .format(
                                                                    result.totalCount,
                                                                    result.addedCount,
                                                                    result.ignoredCount,
                                                                )
                                                        } else {
                                                            MLang.AccessControl.Settings
                                                                .ImportSuccess
                                                                .format(result.addedCount)
                                                        }
                                                    context.toast(message)
                                                } else {
                                                    context.toast(
                                                        MLang.AccessControl.Settings.ImportFailed
                                                    )
                                                }
                                            },
                                        ),
                                        ConfigEntryActionOption(
                                            title = MLang.AccessControl.Settings.Export,
                                            icon = Yume.`Settings-2`,
                                            onClick = {
                                                val exportText = viewModel.exportPackages()
                                                val clip =
                                                    ClipData.newPlainText("packages", exportText)
                                                clipboardManager.setPrimaryClip(clip)
                                                context.toast(
                                                    MLang.AccessControl.Settings.ExportSuccess
                                                        .format(uiState.selectedPackages.size)
                                                )
                                            },
                                        ),
                                    ),
                                showDivider = false,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    DialogButtonRow(
                        onCancel = { showSettingsSheet.value = false },
                        onConfirm = { showSettingsSheet.value = false },
                        cancelText = MLang.AccessControl.Button.Cancel,
                        confirmText = MLang.AccessControl.Button.Confirm,
                    )
                },
            )

            AnimatedVisibility(
                visible = isApplying,
                enter = fadeIn(tween(120)),
                exit = fadeOut(tween(120)),
                modifier = Modifier.fillMaxSize().zIndex(50f),
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.05f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {},
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    AnimatedVisibility(
        visible = searchExpanded.value,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300)),
        modifier = Modifier.fillMaxSize().zIndex(100f),
    ) {
        ExpandedSearchOverlay(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            filteredApps = filteredApps,
            onAppSelectionChange = { packageName, checked ->
                viewModel.onAppSelectionChange(packageName, checked)
            },
            onDismiss = {
                searchExpanded.value = false
                viewModel.onSearchQueryChange("")
            },
        )
    }
}

@Composable
private fun ExpandedSearchOverlay(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredApps: List<AccessControlAppInfo>,
    onAppSelectionChange: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier =
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onDismiss()
            }
    ) {
        Column(
            modifier =
                Modifier.align(Alignment.TopCenter)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .widthIn(max = AccessControlMetrics.SearchOverlayMaxWidth)
                    .statusBarsPadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {}
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = MLang.AccessControl.Search.Placeholder,
                singleLine = true,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(
                            horizontal = AccessControlMetrics.SearchHorizontalPadding,
                            vertical = AccessControlMetrics.SearchTopPadding,
                        ),
            )

            LazyColumn(
                modifier =
                    Modifier.fillMaxSize()
                        .padding(horizontal = AccessControlMetrics.SearchHorizontalPadding)
                        .clip(
                            RoundedCornerShape(
                                topStart = AccessControlMetrics.SearchResultCornerRadius,
                                topEnd = AccessControlMetrics.SearchResultCornerRadius,
                            )
                        )
                        .background(MiuixTheme.colorScheme.surface)
            ) {
                if (filteredApps.isEmpty()) {
                    item {
                        BasicComponent(
                            title = MLang.AccessControl.Search.EmptyResults,
                            summary = searchQuery,
                        )
                    }
                }
                items(items = filteredApps, key = { it.packageName }) { app ->
                    BasicComponent(
                        title = app.label,
                        summary = app.packageName,
                        startAction = {
                            AppIcon(
                                packageName = app.packageName,
                                contentDescription = app.label,
                                imageSize = AccessControlMetrics.SearchResultAppIconSize,
                                bitmapSize = AccessControlMetrics.AppIconBitmapBaseSize,
                            )
                        },
                        endActions = {
                            Checkbox(
                                state = ToggleableState(app.isSelected),
                                onClick = { onAppSelectionChange(app.packageName, !app.isSelected) },
                            )
                        },
                        onClick = { onAppSelectionChange(app.packageName, !app.isSelected) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppCard(
    app: AccessControlAppInfo,
    onSelectionChange: (Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Card(modifier = Modifier.padding(vertical = AccessControlMetrics.ListCardVerticalPadding)) {
        BasicComponent(
            title = app.label,
            summary = app.packageName,
            startAction = {
                AppIcon(
                    packageName = app.packageName,
                    contentDescription = app.label,
                    imageSize = AccessControlMetrics.AppCardIconSize,
                    bitmapSize = AccessControlMetrics.AppIconBitmapBaseSize,
                    modifier = Modifier.padding(end = AccessControlMetrics.AppIconEndPadding),
                )
            },
            endActions = {
                Checkbox(
                    state = ToggleableState(app.isSelected),
                    onClick = { onSelectionChange(!app.isSelected) },
                )
            },
            onClick = onClick,
        )
    }
}

@Composable
private fun AppIcon(
    packageName: String,
    contentDescription: String,
    imageSize: androidx.compose.ui.unit.Dp,
    bitmapSize: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val targetBitmapSize =
        remember(imageSize, bitmapSize, density) {
            maxOf(bitmapSize, (with(density) { imageSize.toPx() } * 1.35f).toInt())
                .coerceIn(64, 160)
        }
    val cacheKey = remember(packageName, targetBitmapSize) { "$packageName@$targetBitmapSize" }
    val iconBitmap by
        produceState<ImageBitmap?>(
            initialValue = null,
            key1 = packageName,
            key2 = targetBitmapSize,
        ) {
            value =
                withContext(Dispatchers.IO) {
                    AppIconMemoryCache.get(cacheKey)?.asImageBitmap()?.let {
                        return@withContext it
                    }
                    runCatching {
                            context.packageManager
                                .getApplicationIcon(packageName)
                                .toBitmap(width = targetBitmapSize, height = targetBitmapSize)
                                .also { bitmap -> AppIconMemoryCache.put(cacheKey, bitmap) }
                                .asImageBitmap()
                        }
                        .getOrNull()
                }
        }

    val bitmap = iconBitmap ?: return
    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier.size(imageSize),
    )
}

private object AppIconMemoryCache {
    private val cache =
        object : LruCache<String, Bitmap>(12 * 1024 * 1024) {
            override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
        }

    fun get(key: String): Bitmap? = cache.get(key)?.takeUnless { it.isRecycled }

    fun put(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }
}

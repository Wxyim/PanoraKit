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

package com.github.nomadboxlab.monadbox.feature.log

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.core.model.LogMessage
import com.github.nomadboxlab.monadbox.domain.app.AppInfo
import com.github.nomadboxlab.monadbox.presentation.component.AppDialog
import com.github.nomadboxlab.monadbox.presentation.component.AppDialogDefaults
import com.github.nomadboxlab.monadbox.presentation.component.Card
import com.github.nomadboxlab.monadbox.presentation.component.ConfigSettingRow
import com.github.nomadboxlab.monadbox.presentation.component.DialogButtonRow
import com.github.nomadboxlab.monadbox.presentation.component.InfoSettingRow
import com.github.nomadboxlab.monadbox.presentation.component.appClickable
import com.github.nomadboxlab.monadbox.presentation.component.NavigationBackIcon
import com.github.nomadboxlab.monadbox.presentation.component.ScreenLazyColumn
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import com.github.nomadboxlab.monadbox.presentation.component.SmallTitle
import com.github.nomadboxlab.monadbox.presentation.component.TopBar
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Delete
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Save
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.LocalPageMetrics
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import dev.oom_wg.purejoy.mlang.MLangStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.theme.MiuixTheme

private enum class LogContentMode {
    Browser,
    Detail,
}

@Composable
fun LogScreenBody(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<LogViewModel>()
    val appInfo = koinInject<AppInfo>()
    val scrollBehavior = MiuixScrollBehavior()
    val spacing = AppTheme.spacing
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val logEntries by viewModel.tempLogEntries.collectAsStateWithLifecycle()
    val historyFiles by viewModel.historyFiles.collectAsStateWithLifecycle()
    val startupFiles by viewModel.startupFiles.collectAsStateWithLifecycle()
    val selectedHistoryFileName by viewModel.selectedHistoryFileName.collectAsStateWithLifecycle()
    val selectedStartupFileName by viewModel.selectedStartupFileName.collectAsStateWithLifecycle()
    val selectedHistoryEntries by viewModel.selectedHistoryEntries.collectAsStateWithLifecycle()
    val selectedStartupEntries by viewModel.selectedStartupEntries.collectAsStateWithLifecycle()
    val viewingHistory = selectedHistoryFileName != null
    val viewingStartup = selectedStartupFileName != null
    val viewingSavedFile = viewingHistory || viewingStartup
    val displayEntries =
        when {
            viewingHistory -> selectedHistoryEntries
            viewingStartup -> selectedStartupEntries
            else -> logEntries
        }

    val listState = remember { LazyListState() }
    val dateFormatter = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    val debugBundleLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/zip")
        ) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            scope.launch(Dispatchers.IO) {
                val success =
                    viewModel.exportDebugBundle(
                        targetUri = uri,
                        appVersionName = appInfo.versionName,
                        appVersionCode = appInfo.versionCode,
                        buildType = appInfo.buildType,
                    )
                launch(Dispatchers.Main) {
                    if (success) {
                        context.toast(MLang.Log.Action.ExportDone)
                    } else {
                        context.toast(MLang.Util.Error.UnknownError)
                    }
                }
            }
        }

    val showExportConfirmDialog = rememberSaveable { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.startAutoRefresh()
        onDispose { viewModel.stopAutoRefresh() }
    }

    BackHandler(enabled = viewingSavedFile) { viewModel.closeHistoryViewer() }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.Log.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                        onClick = {
                            if (viewingSavedFile) {
                                viewModel.closeHistoryViewer()
                            } else {
                                navigator.popBackStack()
                            }
                        },
                    )
                },
                actions = {
                    if (!viewingSavedFile) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val success = viewModel.clearAllLogs()
                                    if (success) {
                                        context.toast(MLang.Log.Action.CleanupDone)
                                    } else {
                                        context.toast(MLang.Util.Error.UnknownError)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = MonadIcons.Delete,
                                contentDescription = MLang.Log.Action.Cleanup,
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {},
    ) { innerPadding ->
        val hasSavedLogs = historyFiles.isNotEmpty() || startupFiles.isNotEmpty()
        val showNoLogsEmptyState =
            !viewingSavedFile &&
                logEntries.isEmpty() &&
                !isRecording &&
                historyFiles.isEmpty() &&
                startupFiles.isEmpty()
        val showWaitingEmptyState =
            !viewingSavedFile && logEntries.isEmpty() && isRecording && !hasSavedLogs

        DebugBundleExportConfirmDialog(
            show = showExportConfirmDialog.value,
            onConfirm = {
                showExportConfirmDialog.value = false
                debugBundleLauncher.launch(viewModel.suggestDebugBundleFileName())
            },
            onDismiss = { showExportConfirmDialog.value = false },
        )

        if (showNoLogsEmptyState || showWaitingEmptyState) {
            LogEmptyStateContent(
                firstLine = MLang.Log.Empty.NoLogs,
                secondLine =
                    if (showWaitingEmptyState) {
                        MLang.Log.Detail.WillShowWhenGenerated
                    } else {
                        MLang.Log.Empty.AutoRecordHint
                    },
                innerPadding = innerPadding,
                onExportDebugBundle = { showExportConfirmDialog.value = true },
                liveCount = logEntries.size,
                isRecording = isRecording,
                onToggleRecording = {
                    if (isRecording) viewModel.stopRecording()
                    else viewModel.startRecording()
                },
            )
            return@Scaffold
        }

        AnimatedContent(
            targetState = if (viewingSavedFile) LogContentMode.Detail else LogContentMode.Browser,
            transitionSpec = {
                if (targetState == LogContentMode.Detail) {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    ) + fadeIn(tween(180)) togetherWith
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        ) + fadeOut(tween(120))
                } else {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    ) + fadeIn(tween(180)) togetherWith
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        ) + fadeOut(tween(120))
                }
            },
            label = "log_content_switch_animation",
        ) { contentMode ->
            val isDetailMode = contentMode == LogContentMode.Detail
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
                val logContentMaxWidth = adaptiveInfo.preferredSinglePaneMaxWidth
                ScreenLazyColumn(
                    modifier = Modifier.adaptiveContentWidth(logContentMaxWidth),
                    scrollBehavior = scrollBehavior,
                    innerPadding = innerPadding,
                    topPadding = spacing.xl,
                    lazyListState = listState,
                ) {
                    if (!isDetailMode) {
                        item(key = "diagnostic_title") { SmallTitle(MLang.Settings.More.Logs) }
                        item(key = "diagnostic_overview") {
                            LogOverviewSection(
                                liveCount = logEntries.size,
                                isRecording = isRecording,
                                onExportDebugBundle = { showExportConfirmDialog.value = true },
                                onToggleRecording = {
                                    if (isRecording) viewModel.stopRecording()
                                    else viewModel.startRecording()
                                },
                            )
                        }
                    }

                    if (isDetailMode) {
                        item(key = "history_header") {
                            Card(modifier = Modifier.padding(vertical = spacing.xs)) {
                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(
                                                horizontal = spacing.md,
                                                vertical = spacing.sm,
                                            ),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text =
                                            selectedHistoryFileName
                                                ?: selectedStartupFileName.orEmpty(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                val historyFileName = selectedHistoryFileName
                                                val startupFileName = selectedStartupFileName
                                                val success =
                                                    when {
                                                        historyFileName != null -> {
                                                            viewModel.deleteHistoryFile(
                                                                historyFileName
                                                            )
                                                        }

                                                        startupFileName != null -> {
                                                            viewModel.deleteStartupFile(
                                                                startupFileName
                                                            )
                                                        }

                                                        else -> false
                                                    }
                                                if (!success) {
                                                    context.toast(MLang.Util.Error.UnknownError)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = MonadIcons.Delete,
                                            contentDescription =
                                                MLang.Component.Editor.Action.Delete,
                                        )
                                    }
                                }
                            }
                        }
                    } else if (historyFiles.isNotEmpty()) {
                        item(key = "history_title") { SmallTitle(MLang.Log.History.Title) }
                        itemsIndexed(items = historyFiles, key = { _, item -> item.name }) { _, file
                            ->
                            Card(modifier = Modifier.padding(vertical = spacing.xs)) {
                                ConfigSettingRow(
                                    title = file.name,
                                    summary =
                                        MLang.Log.History.ItemSummary.format(
                                            dateFormatter.format(Date(file.createdAt)),
                                            formatFileSize(file.size),
                                        ) +
                                            if (file.isRecording)
                                                " · ${MLang.Log.History.Recording}"
                                            else "",
                                    showDivider = false,
                                    onClick = { viewModel.openHistoryFile(file.name) },
                                )
                            }
                        }
                    }

                    if (!isDetailMode && startupFiles.isNotEmpty()) {
                        item(key = "startup_title") { SmallTitle(MLang.Log.Startup.Title) }
                        itemsIndexed(items = startupFiles, key = { _, item -> item.name }) { _, file
                            ->
                            Card(modifier = Modifier.padding(vertical = spacing.xs)) {
                                ConfigSettingRow(
                                    title = file.name,
                                    summary =
                                        MLang.Log.Startup.ItemSummary.format(
                                            dateFormatter.format(Date(file.updatedAt)),
                                            formatFileSize(file.size),
                                        ),
                                    showDivider = false,
                                    onClick = { viewModel.openStartupFile(file.name) },
                                )
                            }
                        }
                    }

                    if (
                        !isDetailMode &&
                            (historyFiles.isNotEmpty() || startupFiles.isNotEmpty()) &&
                            displayEntries.isNotEmpty()
                    ) {
                        item(key = "live_title") { SmallTitle(MLang.Log.History.LiveSection) }
                    }

                    val reversed = displayEntries.asReversed()
                    itemsIndexed(items = reversed, key = { index, _ -> index }) { _, entry ->
                        LogEntryRow(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LogEmptyStateContent(
    firstLine: String,
    secondLine: String,
    innerPadding: PaddingValues,
    onExportDebugBundle: () -> Unit,
    liveCount: Int,
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val spacing = AppTheme.spacing
    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val adaptiveInfo = rememberAvailableWindowAdaptiveInfo(maxWidth, maxHeight)
        val logContentMaxWidth = adaptiveInfo.preferredSinglePaneMaxWidth
        ScreenLazyColumn(
            modifier = Modifier.adaptiveContentWidth(logContentMaxWidth),
            scrollBehavior = scrollBehavior,
            innerPadding = innerPadding,
            topPadding = spacing.xl,
        ) {
            item(key = "diagnostic_title") { SmallTitle(MLang.Settings.More.Logs) }
            item(key = "diagnostic_overview") {
                LogOverviewSection(
                    liveCount = liveCount,
                    isRecording = isRecording,
                    onExportDebugBundle = onExportDebugBundle,
                    onToggleRecording = onToggleRecording,
                )
            }
            item(key = "empty_state") {
                Card(modifier = Modifier.padding(vertical = spacing.xs)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(spacing.xxl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text(
                            text = firstLine,
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = secondLine,
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogOverviewSection(
    liveCount: Int,
    isRecording: Boolean,
    onExportDebugBundle: () -> Unit,
    onToggleRecording: () -> Unit,
) {
    Card {
        Box(modifier = Modifier.appClickable(onClick = onToggleRecording)) {
            InfoSettingRow(
                title = MLangStatus.Log.LiveLogs,
                summary =
                    if (isRecording) {
                        MLangStatus.Log.Recording
                    } else {
                        MLangStatus.Log.NotRecording
                    },
                valueLabel = liveCount.toString(),
                tone = if (isRecording) SemanticTone.Success else SemanticTone.Neutral,
                badgeLeadingDot = isRecording,
            )
        }
        ConfigSettingRow(
            title = MLang.Log.Action.ExportDebugBundle,
            summary = MLang.Log.Action.ExportDebugBundleWarning,
            imageVector = MonadIcons.Save,
            tone = SemanticTone.Info,
            showDivider = false,
            onClick = onExportDebugBundle,
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024L) return "${bytes}B"
    val kb = bytes / 1024.0
    if (kb < 1024.0) return String.format(Locale.US, "%.1fKB", kb)
    val mb = kb / 1024.0
    return String.format(Locale.US, "%.1fMB", mb)
}

@Composable
private fun LogEntryRow(entry: LogViewModel.LogEntry) {
    val spacing = AppTheme.spacing
    val pageMetrics = LocalPageMetrics.current
    val levelColor =
        when (entry.level) {
            LogMessage.Level.Debug -> Color(0xFF9E9E9E)
            LogMessage.Level.Info -> MiuixTheme.colorScheme.primary
            LogMessage.Level.Warning -> Color(0xFFFF9800)
            LogMessage.Level.Error -> Color(0xFFF44336)
            LogMessage.Level.Silent -> Color(0xFF9E9E9E)
            LogMessage.Level.Unknown -> Color(0xFF9E9E9E)
        }

    Card(modifier = Modifier.padding(vertical = spacing.xs)) {
        Column(
            modifier =
                Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.sm)
        ) {
            if (entry.time.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Text(
                        text = entry.time,
                        style =
                            MiuixTheme.textStyles.body2.copy(
                                fontSize = pageMetrics.logMetaFontSize,
                                fontFamily = FontFamily.Monospace,
                            ),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Text(
                        text = entry.level.name.uppercase().take(1),
                        style =
                            MiuixTheme.textStyles.body2.copy(
                                fontSize = pageMetrics.logMetaFontSize,
                                fontFamily = FontFamily.Monospace,
                            ),
                        color = levelColor,
                    )
                }
            }
            Spacer(modifier = Modifier.size(spacing.xs))
            Text(
                text = entry.message,
                style =
                    MiuixTheme.textStyles.body2.copy(
                        fontSize = pageMetrics.logMessageFontSize,
                        fontFamily = FontFamily.Monospace,
                    ),
                color = MiuixTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 8,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DebugBundleExportConfirmDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppDialog(
        show = show,
        modifier = Modifier,
        title = MLang.Log.Action.ExportDebugBundle,
        titleColor = AppDialogDefaults.titleColor(),
        summary = MLang.Log.Action.ExportDebugBundleWarning,
        summaryColor = AppDialogDefaults.summaryColor(),
        backgroundColor = AppDialogDefaults.backgroundColor(),
        enableWindowDim = true,
        onDismissRequest = onDismiss,
        outsideMargin = AppDialogDefaults.outsideMargin,
        insideMargin = AppDialogDefaults.insideMargin,
        defaultWindowInsetsPadding = true,
        content = {
            DialogButtonRow(
                onCancel = onDismiss,
                onConfirm = onConfirm,
                cancelText = MLang.Log.Action.Cancel,
                confirmText = MLang.Log.Action.Export,
            )
        },
    )
}

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

package com.github.yumelira.yumebox.screen.log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.LogMessage
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.CenteredText
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.ArrowLeft
import com.github.yumelira.yumebox.presentation.icon.yume.Delete
import com.github.yumelira.yumebox.presentation.icon.yume.Play
import com.github.yumelira.yumebox.presentation.icon.yume.PowerOff
import com.github.yumelira.yumebox.presentation.icon.yume.Share
import com.github.yumelira.yumebox.presentation.theme.AnimationSpecs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object LogScreenMetrics {
    val TopBarActionEndPadding = 24.dp
    val FabEndPadding = 20.dp
    val FabBottomPadding = 16.dp
    val CardVerticalPadding = 4.dp
    val CardHorizontalPadding = 12.dp
    val CardVerticalInnerPadding = 10.dp
    val MetaFontSize = 11.sp
    val MessageFontSize = 12.sp
    val MetaSpacing = 8.dp
    val MessageTopSpacing = 6.dp
}

private enum class LogContentMode {
    Browser,
    Detail,
}

@Composable
@Destination<RootGraph>
fun LogScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<LogViewModel>()
    val scrollBehavior = MiuixScrollBehavior()
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

    var fabHidden by rememberSaveable { mutableStateOf(false) }
    val listState = remember { LazyListState() }
    val dateFormatter = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    val saveFileLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("text/plain")
        ) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            scope.launch(Dispatchers.IO) {
                val success = viewModel.saveCurrentViewLog(uri)
                if (!success) {
                    launch(Dispatchers.Main) { context.toast(MLang.Util.Error.UnknownError) }
                }
            }
        }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
            .collect { firstVisibleItemIndex ->
                val previousFirstVisibleItemIndex =
                    listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                if (previousFirstVisibleItemIndex != null && firstVisibleItemIndex != null) {
                    fabHidden = firstVisibleItemIndex > previousFirstVisibleItemIndex
                }
            }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isActive) {
                viewModel.refreshTempLogEntries()
                viewModel.refreshHistoryFiles()
                viewModel.refreshStartupFiles()
                delay(500.milliseconds)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshHistoryFiles()
        viewModel.refreshStartupFiles()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.Log.Title,
                scrollBehavior = scrollBehavior,
                actions = {
                    if (displayEntries.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                val prefix =
                                    when {
                                        viewingHistory -> "history"
                                        viewingStartup -> "startup"
                                        else -> "live"
                                    }
                                saveFileLauncher.launch(
                                    "${prefix}_log_${System.currentTimeMillis()}.txt"
                                )
                            },
                            modifier =
                                Modifier.padding(end = LogScreenMetrics.TopBarActionEndPadding),
                        ) {
                            Icon(
                                imageVector = Yume.Share,
                                contentDescription = MLang.Component.Editor.Action.Save,
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !fabHidden,
                enter =
                    scaleIn(
                        animationSpec =
                            tween(
                                durationMillis = AnimationSpecs.Proxy.FabDuration,
                                easing = AnimationSpecs.EmphasizedDecelerate,
                            ),
                        initialScale = AnimationSpecs.Proxy.VisibilityInitialScale,
                    ) +
                        fadeIn(
                            animationSpec =
                                tween(
                                    durationMillis = AnimationSpecs.Proxy.FabFadeDuration,
                                    easing = AnimationSpecs.EmphasizedDecelerate,
                                )
                        ),
                exit =
                    scaleOut(
                        animationSpec =
                            tween(
                                durationMillis = AnimationSpecs.Proxy.FabDuration,
                                easing = AnimationSpecs.EmphasizedDecelerate,
                            ),
                        targetScale = AnimationSpecs.Proxy.VisibilityTargetScale,
                    ) +
                        fadeOut(
                            animationSpec =
                                tween(
                                    durationMillis = AnimationSpecs.Proxy.FabFadeDuration,
                                    easing = AnimationSpecs.EmphasizedDecelerate,
                                )
                        ),
                label = "log_record_fab_visibility",
            ) {
                FloatingActionButton(
                    modifier =
                        Modifier.navigationBarsPadding()
                            .padding(
                                end = LogScreenMetrics.FabEndPadding,
                                bottom = LogScreenMetrics.FabBottomPadding,
                            ),
                    onClick = {
                        if (isRecording) {
                            viewModel.stopRecording()
                        } else {
                            viewModel.startRecording()
                        }
                    },
                ) {
                    Icon(
                        imageVector = if (isRecording) Yume.PowerOff else Yume.Play,
                        contentDescription =
                            if (isRecording) MLang.Log.Action.StopRecording
                            else MLang.Log.Action.StartRecording,
                        tint = MiuixTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
    ) { innerPadding ->
        if (
            !viewingSavedFile &&
                logEntries.isEmpty() &&
                isRecording.not() &&
                historyFiles.isEmpty() &&
                startupFiles.isEmpty()
        ) {
            CenteredText(
                firstLine = MLang.Log.Empty.NoLogs,
                secondLine = MLang.Log.Empty.StartRecordingHint,
            )
            return@Scaffold
        }

        if (!viewingSavedFile && logEntries.isEmpty() && isRecording) {
            CenteredText(
                firstLine = MLang.Log.Detail.WaitingLog,
                secondLine = MLang.Log.Detail.WillShowWhenGenerated,
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
        ) {
            ScreenLazyColumn(
                scrollBehavior = scrollBehavior,
                innerPadding = innerPadding,
                topPadding = 20.dp,
                lazyListState = listState,
            ) {
                if (viewingSavedFile) {
                    item(key = "history_header") {
                        Card(
                            modifier =
                                Modifier.padding(vertical = LogScreenMetrics.CardVerticalPadding)
                        ) {
                            Row(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .clickable { viewModel.closeHistoryViewer() }
                                        .padding(
                                            horizontal = LogScreenMetrics.CardHorizontalPadding,
                                            vertical = LogScreenMetrics.CardVerticalInnerPadding,
                                        ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(
                                        imageVector = Yume.ArrowLeft,
                                        contentDescription = MLang.Component.Navigation.Back,
                                    )
                                    Text(
                                        text =
                                            selectedHistoryFileName
                                                ?: selectedStartupFileName.orEmpty(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            val historyFileName = selectedHistoryFileName
                                            val startupFileName = selectedStartupFileName
                                            val success =
                                                when {
                                                    historyFileName != null -> {
                                                        viewModel.deleteHistoryFile(historyFileName)
                                                    }

                                                    startupFileName != null -> {
                                                        viewModel.deleteStartupFile(startupFileName)
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
                                        imageVector = Yume.Delete,
                                        contentDescription = MLang.Component.Editor.Action.Delete,
                                    )
                                }
                            }
                        }
                    }
                } else if (historyFiles.isNotEmpty()) {
                    item(key = "history_title") { SmallTitle(MLang.Log.History.Title) }
                    itemsIndexed(items = historyFiles, key = { _, item -> item.name }) { _, file ->
                        Card(
                            modifier =
                                Modifier.padding(vertical = LogScreenMetrics.CardVerticalPadding)
                        ) {
                            SuperArrow(
                                title = file.name,
                                summary =
                                    MLang.Log.History.ItemSummary.format(
                                        dateFormatter.format(Date(file.createdAt)),
                                        formatFileSize(file.size),
                                    ) +
                                        if (file.isRecording) " · ${MLang.Log.History.Recording}"
                                        else "",
                                onClick = { viewModel.openHistoryFile(file.name) },
                            )
                        }
                    }
                }

                if (!viewingSavedFile && startupFiles.isNotEmpty()) {
                    item(key = "startup_title") { SmallTitle(MLang.Log.Startup.Title) }
                    itemsIndexed(items = startupFiles, key = { _, item -> item.name }) { _, file ->
                        Card(
                            modifier =
                                Modifier.padding(vertical = LogScreenMetrics.CardVerticalPadding)
                        ) {
                            SuperArrow(
                                title = file.name,
                                summary =
                                    MLang.Log.Startup.ItemSummary.format(
                                        dateFormatter.format(Date(file.updatedAt)),
                                        formatFileSize(file.size),
                                    ),
                                onClick = { viewModel.openStartupFile(file.name) },
                            )
                        }
                    }
                }

                if (
                    !viewingSavedFile &&
                        (historyFiles.isNotEmpty() || startupFiles.isNotEmpty()) &&
                        displayEntries.isNotEmpty()
                ) {
                    item(key = "live_title") { SmallTitle(MLang.Log.History.LiveSection) }
                }

                val reversed = displayEntries.asReversed()
                itemsIndexed(
                    items = reversed,
                    key = { index, item -> "${item.time}_${item.level}_${item.message}_$index" },
                ) { _, entry ->
                    LogEntryRow(entry = entry)
                }
            }
        }
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
    val levelColor =
        when (entry.level) {
            LogMessage.Level.Debug -> Color(0xFF9E9E9E)
            LogMessage.Level.Info -> MiuixTheme.colorScheme.primary
            LogMessage.Level.Warning -> Color(0xFFFF9800)
            LogMessage.Level.Error -> Color(0xFFF44336)
            LogMessage.Level.Silent -> Color(0xFF9E9E9E)
            LogMessage.Level.Unknown -> Color(0xFF9E9E9E)
        }

    Card(modifier = Modifier.padding(vertical = LogScreenMetrics.CardVerticalPadding)) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        horizontal = LogScreenMetrics.CardHorizontalPadding,
                        vertical = LogScreenMetrics.CardVerticalInnerPadding,
                    )
        ) {
            if (entry.time.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LogScreenMetrics.MetaSpacing),
                ) {
                    Text(
                        text = entry.time,
                        style =
                            MiuixTheme.textStyles.body2.copy(
                                fontSize = LogScreenMetrics.MetaFontSize,
                                fontFamily = FontFamily.Monospace,
                            ),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Text(
                        text = entry.level.name.uppercase().take(1),
                        style =
                            MiuixTheme.textStyles.body2.copy(
                                fontSize = LogScreenMetrics.MetaFontSize,
                                fontFamily = FontFamily.Monospace,
                            ),
                        color = levelColor,
                    )
                }
            }
            Spacer(modifier = Modifier.size(LogScreenMetrics.MessageTopSpacing))
            Text(
                text = entry.message,
                style =
                    MiuixTheme.textStyles.body2.copy(
                        fontSize = LogScreenMetrics.MessageFontSize,
                        fontFamily = FontFamily.Monospace,
                    ),
                color = MiuixTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

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

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.state.ToggleableState
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.GeoFileType
import com.github.yumelira.yumebox.core.model.GeoXItem
import com.github.yumelira.yumebox.core.model.geoXItems
import com.github.yumelira.yumebox.feature.editor.language.LanguageScope
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.*
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.yumelira.yumebox.presentation.util.OverrideStructuredEditorStore
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ConnectionScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ExplanationChainDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideConfigPreviewRouteDestination
import com.ramcosta.composedestinations.generated.destinations.ProvidersScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RawTraceDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RuleSetInspectorScreenDestination
import com.ramcosta.composedestinations.generated.destinations.RuntimeHealthDetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SourceRegistryOverviewScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SnapshotHistoryScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold

@Composable
@Destination<RootGraph>
fun MetaFeatureScreen(navigator: DestinationsNavigator) {
    val viewModel = koinViewModel<MetaFeatureViewModel>()
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val externalResourceBanner = remember(uiState.externalResources) {
        uiState.externalResources.toBannerState()
    }
    val recentFailureSummary =
        remember(uiState.recentFailures) {
            if (uiState.recentFailures.isEmpty()) {
                DiagnosticLang.NoActiveIssues
            } else {
                DiagnosticLang.RecentFailureItems.format(uiState.recentFailures.size)
            }
        }
    val sourceSummary =
        remember(externalResourceBanner.subtitle) {
            externalResourceBanner.subtitle ?: MLang.Providers.Empty.NoProvidersHint
        }

    val showGeoXDownloadSheet = remember { mutableStateOf(false) }
    var runtimeConfigLoading by remember { mutableStateOf(false) }
    val openRuntimeConfigPreview: () -> Unit = {
        if (!runtimeConfigLoading) scope.launch {
            runtimeConfigLoading = true
            try {
                ServiceClient.connect(context)
                val activeProfile = ServiceClient.profile().queryActive()
                val runtimeConfig = ServiceClient.clash().queryConfiguration()
                if (activeProfile == null) {
                    context.toast(MLang.MetaFeature.RuntimeConfig.NoActiveProfile)
                    return@launch
                }

                val configPath = runtimeConfig.configPath?.trim().orEmpty()
                if (configPath.isBlank()) {
                    context.toast(MLang.MetaFeature.RuntimeConfig.RuntimeConfigNotRunning)
                    return@launch
                }

                val runtimeYaml =
                    withContext(Dispatchers.IO) {
                        val file = File(configPath)
                        if (!file.exists() || !file.isFile) null else file.readText()
                    }

                if (runtimeYaml.isNullOrBlank()) {
                    context.toast(MLang.MetaFeature.RuntimeConfig.ConfigNotFound)
                    return@launch
                }

                val previewTitle =
                    activeProfile.name
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            MLang.MetaFeature.RuntimeConfig.PreviewTitleWithProfile.format(it)
                        }
                        ?: MLang.MetaFeature.RuntimeConfig.PreviewTitle

                OverrideStructuredEditorStore.setupConfigPreview(
                    title = previewTitle,
                    content = decodeEscapedUnicode(runtimeYaml),
                    language = LanguageScope.Yaml,
                    callback = null,
                )
                navigator.navigate(OverrideConfigPreviewRouteDestination) {
                    launchSingleTop = true
                }
            } catch (error: Throwable) {
                val message =
                    when {
                        error.message?.contains("unauthorized", ignoreCase = true) == true -> {
                            MLang.MetaFeature.RuntimeConfig.RuntimeConfigUnauthorized
                        }

                        else -> {
                            MLang.MetaFeature.RuntimeConfig.RuntimeConfigFetchFailed.format(
                                error.runtimeGatewayMessage(
                                    MLang.MetaFeature.RuntimeConfig.LoadFailed
                                )
                            )
                        }
                    }
                context.toast(message)
            } finally {
                runtimeConfigLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.MetaFeature.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
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
                ) {
                    item {
                        MetaOverviewSection(
                            runtimeSnapshot = uiState.runtimeSnapshot,
                            failureCount = uiState.recentFailures.size,
                            sourceCount = uiState.externalResources.subscriptionSources.size,
                            staleCount = uiState.externalResources.staleCount,
                        )
                    }
                    item {
                        MetaCapabilitySection(
                            title = DiagnosticLang.DetailPages.Console.RepairAndTriage,
                            entries =
                                listOf(
                                    MetaCapabilityEntry(
                                        title = DiagnosticLang.DetailPages.RuntimeHealth.Title,
                                        summary = recentFailureSummary,
                                        imageVector = Yume.Activity,
                                        tone = buildRuntimeTone(uiState.runtimeSnapshot),
                                        tier = DiagnosticLang.DetailPages.Console.BeginnerTier,
                                        onClick = {
                                            navigator.navigate(RuntimeHealthDetailScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.Settings.More.Logs,
                                        summary = recentFailureSummary,
                                        imageVector = Yume.Message,
                                        tone =
                                            if (uiState.recentFailures.isEmpty()) {
                                                SemanticTone.Success
                                            } else {
                                                SemanticTone.Danger
                                            },
                                        tier = DiagnosticLang.DetailPages.Console.BeginnerTier,
                                        onClick = {
                                            navigator.navigate(LogScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = DiagnosticLang.DetailPages.SourceRegistry.Title,
                                        summary = sourceSummary,
                                        imageVector = Yume.Folders,
                                        tone = externalResourceBanner.tone,
                                        tier = DiagnosticLang.DetailPages.Console.BeginnerTier,
                                        onClick = {
                                            navigator.navigate(SourceRegistryOverviewScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                ),
                        )
                    }
                    item {
                        MetaCapabilitySection(
                            title = DiagnosticLang.DetailPages.Console.ExplainAndValidate,
                            entries =
                                listOf(
                                    MetaCapabilityEntry(
                                        title = DiagnosticLang.DetailPages.ExplanationChain.Title,
                                        summary =
                                            uiState.recentFailures.lastOrNull()?.message
                                                ?: DiagnosticLang.DetailPages.ExplanationChain.Summary,
                                        imageVector = Yume.Sparkles,
                                        tone = SemanticTone.Info,
                                        tier = DiagnosticLang.DetailPages.Console.IntermediateTier,
                                        onClick = {
                                            navigator.navigate(ExplanationChainDetailScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = DiagnosticLang.DetailPages.RuleSetInspector.Title,
                                        summary = DiagnosticLang.DetailPages.RuleSetInspector.Summary,
                                        imageVector = Yume.ListCollapse,
                                        tone = SemanticTone.Info,
                                        tier = DiagnosticLang.DetailPages.Console.IntermediateTier,
                                        onClick = {
                                            navigator.navigate(RuleSetInspectorScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = DiagnosticLang.DetailPages.SnapshotHistory.Title,
                                        summary = DiagnosticLang.DetailPages.SnapshotHistory.Summary,
                                        imageVector = Yume.Save,
                                        tone = SemanticTone.Brand,
                                        tier = DiagnosticLang.DetailPages.Console.IntermediateTier,
                                        onClick = {
                                            navigator.navigate(SnapshotHistoryScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                ),
                        )
                    }
                    item {
                        MetaCapabilitySection(
                            title = DiagnosticLang.DetailPages.Console.AdvancedAndUtilities,
                            entries =
                                listOf(
                                    MetaCapabilityEntry(
                                        title = DiagnosticLang.DetailPages.RawTrace.Title,
                                        summary =
                                            if (uiState.recentFailures.isEmpty()) {
                                                DiagnosticLang.DetailPages.RawTrace.Summary
                                            } else {
                                                recentFailureSummary
                                            },
                                        imageVector = Yume.ClipboardCopy,
                                        tone =
                                            if (uiState.recentFailures.isEmpty()) {
                                                SemanticTone.Neutral
                                            } else {
                                                SemanticTone.Warning
                                            },
                                        tier = DiagnosticLang.DetailPages.Console.AdvancedTier,
                                        onClick = {
                                            navigator.navigate(RawTraceDetailScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.Connection.Title,
                                        summary = MLang.Connection.Summary,
                                        imageVector = Yume.Link,
                                        tone = SemanticTone.Info,
                                        tier = DiagnosticLang.DetailPages.Console.AdvancedTier,
                                        onClick = {
                                            navigator.navigate(ConnectionScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.Providers.Title,
                                        summary = sourceSummary,
                                        imageVector = Yume.`Settings-2`,
                                        tone = externalResourceBanner.tone,
                                        tier = DiagnosticLang.DetailPages.Console.AdvancedTier,
                                        onClick = {
                                            navigator.navigate(ProvidersScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.MetaFeature.RuntimeConfig.Title,
                                        summary = MLang.MetaFeature.RuntimeConfig.Summary,
                                        imageVector = Yume.`Scroll-text`,
                                        tone = SemanticTone.Info,
                                        tier =
                                            if (runtimeConfigLoading) {
                                                DiagnosticLang.DetailPages.Remediation.Pending
                                            } else {
                                                DiagnosticLang.DetailPages.Console.AdvancedTier
                                            },
                                        onClick = openRuntimeConfigPreview,
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.MetaFeature.GeoX.OnlineUpdateTitle,
                                        summary = MLang.MetaFeature.GeoX.OnlineUpdateSummary,
                                        imageVector = Yume.Cloud,
                                        tone = SemanticTone.Info,
                                        tier = DiagnosticLang.DetailPages.Console.AdvancedTier,
                                        onClick = { showGeoXDownloadSheet.value = true },
                                    ),
                                ),
                        )
                    }
                }
            }
        }
        GeoXDownloadSheet(show = showGeoXDownloadSheet, context = context, scope = scope)
    }
}

private fun decodeEscapedUnicode(text: String): String {
    val out = StringBuilder(text.length)
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        if (ch == '\\' && i + 1 < text.length) {
            val marker = text[i + 1]
            val hexLen =
                when (marker) {
                    'u' -> 4
                    'U' -> 8
                    else -> 0
                }
            if (hexLen > 0 && i + 2 + hexLen <= text.length) {
                val hex = text.substring(i + 2, i + 2 + hexLen)
                val codePoint = hex.toIntOrNull(16)
                if (codePoint != null && codePoint in 0..0x10FFFF) {
                    out.append(String(Character.toChars(codePoint)))
                    i += 2 + hexLen
                    continue
                }
            }
        }
        out.append(ch)
        i += 1
    }
    return out.toString()
}

private data class MetaCapabilityEntry(
    val title: String,
    val summary: String?,
    val imageVector: ImageVector,
    val tone: SemanticTone,
    val tier: String,
    val onClick: () -> Unit,
)

@Composable
private fun MetaOverviewSection(
    runtimeSnapshot: RuntimeSnapshot,
    failureCount: Int,
    sourceCount: Int,
    staleCount: Int,
) {
    val readyPayloadCount = runtimeSnapshot.payloadReadyCount()
    val failureTone = if (failureCount == 0) SemanticTone.Success else SemanticTone.Danger
    val sourceTone = if (staleCount > 0) SemanticTone.Warning else SemanticTone.Info

    SmallTitle(DiagnosticLang.DetailPages.Common.StateSummary)
    Card {
        InfoSettingRow(
            title =
                runtimeSnapshot.profileName?.takeIf(String::isNotBlank)
                    ?: DiagnosticLang.DetailPages.Console.Headline,
            summary = buildRuntimeSummary(runtimeSnapshot, failureCount),
            valueLabel = runtimeSnapshot.phase.toMetaDisplayLabel(),
            tone = buildRuntimeTone(runtimeSnapshot, failureCount),
            badgeLeadingDot = failureCount > 0 || runtimeSnapshot.phase == RuntimePhase.Failed,
        )
        InfoSettingRow(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Payload,
            summary =
                DiagnosticLang.DetailPages.RuntimeHealth.PayloadReadyFormat.format(
                    readyPayloadCount,
                    3,
                ),
            valueLabel = "$readyPayloadCount/3",
            tone = if (runtimeSnapshot.payloadReady) SemanticTone.Success else SemanticTone.Warning,
            badgeLeadingDot = !runtimeSnapshot.payloadReady,
        )
        InfoSettingRow(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Failures,
            summary =
                if (failureCount == 0) {
                    DiagnosticLang.NoActiveIssues
                } else {
                    DiagnosticLang.RecentFailureItems.format(failureCount)
                },
            valueLabel = failureCount.toString(),
            tone = failureTone,
            badgeLeadingDot = failureCount > 0,
        )
        InfoSettingRow(
            title = DiagnosticLang.DetailPages.RuntimeHealth.Sources,
            summary =
                if (staleCount > 0) {
                    DiagnosticLang.SourceStaleItems.format(staleCount)
                } else {
                    DiagnosticLang.SourceReadyItems.format(sourceCount)
                },
            valueLabel = sourceCount.toString(),
            tone = sourceTone,
            badgeLeadingDot = staleCount > 0,
            showDivider = false,
        )
    }
}

@Composable
private fun MetaCapabilitySection(
    title: String,
    entries: List<MetaCapabilityEntry>,
) {
    SmallTitle(title)
    Card {
        entries.forEachIndexed { index, entry ->
            ConfigSettingRow(
                title = entry.title,
                summary = entry.summary,
                imageVector = entry.imageVector,
                tone = entry.tone,
                valueLabel = entry.tier,
                badgeTone = entry.tone,
                badgeLeadingDot = entry.tone != SemanticTone.Neutral,
                showDivider = index != entries.lastIndex,
                onClick = entry.onClick,
            )
        }
    }
}

private fun buildRuntimeTone(
    runtimeSnapshot: RuntimeSnapshot,
    failureCount: Int = 0,
): SemanticTone {
    return when {
        failureCount > 0 || runtimeSnapshot.phase == RuntimePhase.Failed -> SemanticTone.Danger
        runtimeSnapshot.phase == RuntimePhase.Running && runtimeSnapshot.payloadReady -> SemanticTone.Success
        runtimeSnapshot.phase == RuntimePhase.Running -> SemanticTone.Warning
        runtimeSnapshot.phase == RuntimePhase.Starting || runtimeSnapshot.phase == RuntimePhase.Stopping ->
            SemanticTone.Info
        else -> SemanticTone.Neutral
    }
}

private fun buildRuntimeSummary(runtimeSnapshot: RuntimeSnapshot, failureCount: Int): String {
    return when {
        failureCount > 0 || runtimeSnapshot.phase == RuntimePhase.Failed ->
            DiagnosticLang.DetailPages.Console.RuntimeAttention
        runtimeSnapshot.phase == RuntimePhase.Running && runtimeSnapshot.payloadReady ->
            DiagnosticLang.DetailPages.Console.RuntimeStable
        runtimeSnapshot.phase == RuntimePhase.Running -> DiagnosticLang.DetailPages.RuntimeHealth.RunningDegraded
        else -> DiagnosticLang.DetailPages.Console.RuntimeIdle
    }
}

private fun RuntimeSnapshot.payloadReadyCount(): Int {
    return listOf(profileReady, groupsReady, trafficReady).count { it }
}

private fun RuntimePhase.toMetaDisplayLabel(): String {
    return when (this) {
        RuntimePhase.Idle -> DiagnosticLang.DetailPages.RuntimeHealth.IdleShort
        RuntimePhase.Starting -> DiagnosticLang.DetailPages.RuntimeHealth.StartingShort
        RuntimePhase.Running -> DiagnosticLang.Phase.Running
        RuntimePhase.Stopping -> DiagnosticLang.DetailPages.RuntimeHealth.StoppingShort
        RuntimePhase.Failed -> DiagnosticLang.DetailPages.RuntimeHealth.FailedShort
    }
}

@Composable
private fun GeoXDownloadSheet(
    show: MutableState<Boolean>,
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    val selectedItems = remember { mutableStateMapOf<GeoFileType, Boolean>() }

    AppActionBottomSheet(
        show = show.value,
        title = MLang.MetaFeature.Download.DialogTitle,
        onDismissRequest = { show.value = false },
        startAction = { AppBottomSheetCloseAction(onClick = { show.value = false }) },
        endAction = {
            AppBottomSheetConfirmAction(
                enabled = selectedItems.values.any { it },
                onClick = {
                    val itemsToDownload = geoXItems.filter { selectedItems[it.type] == true }
                    if (itemsToDownload.isEmpty()) {
                        context.toast(MLang.MetaFeature.Download.SelectFiles)
                        return@AppBottomSheetConfirmAction
                    }
                    show.value = false
                    downloadGeoXFiles(context, scope, itemsToDownload)
                },
            )
        },
        content = {
            Column {
                geoXItems.forEach { item ->
                    BasicComponent(
                        title = item.title,
                        endActions = {
                            Checkbox(
                                state = ToggleableState(selectedItems[item.type] ?: false),
                                onClick = {
                                    selectedItems[item.type] = !(selectedItems[item.type] ?: false)
                                },
                            )
                        },
                        onClick = {
                            selectedItems[item.type] = !(selectedItems[item.type] ?: false)
                        },
                    )
                }
            }
        },
    )
}

private fun downloadGeoXFiles(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    items: List<GeoXItem>,
) {
    scope.launch {
        var successCount = 0
        withContext(Dispatchers.IO) {
            val clashDir = context.filesDir.resolve("clash")
            clashDir.mkdirs()
            val client = OkHttpClient.Builder().build()

            items.forEach { item ->
                val targetFile = File(clashDir, item.fileName)
                val request =
                    Request.Builder()
                        .url(item.url)
                        // You could add a generic User-Agent array here if required, though OkHttp
                        // sets a default.
                        .header(
                            "User-Agent",
                            com.github.yumelira.yumebox.core.NetworkConstants.DEFAULT_USER_AGENT,
                        )
                        .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.byteStream()?.use { input ->
                                targetFile.outputStream().use { output -> input.copyTo(output) }
                            }
                            successCount++
                        }
                    }
                } catch (e: Exception) {
                    // Fail silently inside the background thread, just relying on successCount
                }
            }
        }
        context.toast(MLang.MetaFeature.Download.DownloadComplete.format(successCount, items.size))
    }
}

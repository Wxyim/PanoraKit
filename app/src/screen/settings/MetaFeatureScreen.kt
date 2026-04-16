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

package com.github.nomadboxlab.monadbox.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.nomadboxlab.monadbox.common.util.toast
import com.github.nomadboxlab.monadbox.core.model.GeoFileType
import com.github.nomadboxlab.monadbox.core.model.GeoXItem
import com.github.nomadboxlab.monadbox.core.model.geoXItems
import com.github.nomadboxlab.monadbox.feature.editor.language.LanguageScope
import com.github.nomadboxlab.monadbox.presentation.component.*
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.*
import com.github.nomadboxlab.monadbox.presentation.theme.adaptiveContentWidth
import com.github.nomadboxlab.monadbox.presentation.theme.rememberAvailableWindowAdaptiveInfo
import com.github.nomadboxlab.monadbox.presentation.util.OverrideStructuredEditorStore
import com.github.nomadboxlab.monadbox.remote.ServiceClient
import com.github.nomadboxlab.monadbox.remote.runtimeGatewayMessage
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase
import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimeSnapshot
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ConnectionScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LogScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideConfigPreviewRouteDestination
import com.ramcosta.composedestinations.generated.destinations.ProvidersScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import dev.oom_wg.purejoy.mlang.MLangStatus
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
    val externalResourceBanner =
        remember(uiState.externalResources) { uiState.externalResources.toBannerState() }
    val sourceSummary =
        remember(externalResourceBanner.subtitle) {
            externalResourceBanner.subtitle ?: MLang.Providers.Empty.NoProvidersHint
        }

    val showGeoXDownloadSheet = remember { mutableStateOf(false) }
    var runtimeConfigLoading by remember { mutableStateOf(false) }
    val openRuntimeConfigPreview: () -> Unit = {
        if (!runtimeConfigLoading)
            scope.launch {
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
                            } ?: MLang.MetaFeature.RuntimeConfig.PreviewTitle

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
                            effectiveRules = uiState.effectiveRules,
                            sourceCount = uiState.externalResources.subscriptionSources.size,
                            staleCount = uiState.externalResources.staleCount,
                        )
                    }
                    item {
                        MetaCapabilitySection(
                            title = MLangStatus.Meta.SectionTitle,
                            entries =
                                listOf(
                                    MetaCapabilityEntry(
                                        title = MLang.Settings.More.Logs,
                                        summary = MLang.Settings.More.LogsSummary,
                                        imageVector = MonadIcons.Message,
                                        tone = SemanticTone.Neutral,
                                        iconTone = SemanticTone.Warning,
                                        onClick = {
                                            navigator.navigate(LogScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.Connection.Title,
                                        summary = MLang.Connection.Summary,
                                        imageVector = MonadIcons.Link,
                                        tone = SemanticTone.Neutral,
                                        iconTone = SemanticTone.Brand,
                                        onClick = {
                                            navigator.navigate(ConnectionScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.Providers.Title,
                                        summary = sourceSummary,
                                        imageVector = MonadIcons.`Settings-2`,
                                        tone = externalResourceBanner.tone,
                                        iconTone = SemanticTone.Info,
                                        onClick = {
                                            navigator.navigate(ProvidersScreenDestination) {
                                                launchSingleTop = true
                                            }
                                        },
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.MetaFeature.RuntimeConfig.Title,
                                        summary = MLang.MetaFeature.RuntimeConfig.Summary,
                                        imageVector = MonadIcons.`Scroll-text`,
                                        tone = SemanticTone.Neutral,
                                        iconTone = SemanticTone.Brand,
                                        onClick = openRuntimeConfigPreview,
                                    ),
                                    MetaCapabilityEntry(
                                        title = MLang.MetaFeature.GeoX.OnlineUpdateTitle,
                                        summary = MLang.MetaFeature.GeoX.OnlineUpdateSummary,
                                        imageVector = MonadIcons.Cloud,
                                        tone = SemanticTone.Neutral,
                                        iconTone = SemanticTone.Success,
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
    val iconTone: SemanticTone,
    val onClick: () -> Unit,
)

@Composable
private fun MetaOverviewSection(
    runtimeSnapshot: RuntimeSnapshot,
    effectiveRules: EffectiveRuleSummaryState,
    sourceCount: Int,
    staleCount: Int,
) {
    val runtimeTone = buildRuntimeTone(runtimeSnapshot)
    val ruleTone = if ((effectiveRules.count ?: 0) > 0) SemanticTone.Info else SemanticTone.Neutral
    val sourceTone = if (staleCount > 0) SemanticTone.Warning else SemanticTone.Info

    SmallTitle(MLangStatus.Meta.StateSummaryTitle)
    Card {
        InfoSettingRow(
            title =
                runtimeSnapshot.profileName?.takeIf(String::isNotBlank)
                    ?: MLangStatus.Meta.RuntimeTitle,
            summary = buildRuntimeSummary(runtimeSnapshot),
            valueLabel = runtimeSnapshot.phase.toMetaDisplayLabel(),
            tone = runtimeTone,
            badgeLeadingDot =
                runtimeSnapshot.phase == RuntimePhase.Failed ||
                    (runtimeSnapshot.phase == RuntimePhase.Running && !runtimeSnapshot.payloadReady),
        )
        InfoSettingRow(
            title = MLangStatus.Meta.EffectiveRules,
            summary = effectiveRules.summary,
            valueLabel = effectiveRules.count?.toString() ?: MLangStatus.Common.NotAvailable,
            tone = ruleTone,
        )
        InfoSettingRow(
            title = MLangStatus.Meta.Sources,
            summary =
                if (staleCount > 0) {
                    MLangStatus.SourceStaleItems.format(staleCount)
                } else {
                    MLangStatus.SourceReadyItems.format(sourceCount)
                },
            valueLabel = sourceCount.toString(),
            tone = sourceTone,
            badgeLeadingDot = staleCount > 0,
            showDivider = false,
        )
    }
}

@Composable
private fun MetaCapabilitySection(title: String, entries: List<MetaCapabilityEntry>) {
    SmallTitle(title)
    Card {
        entries.forEachIndexed { index, entry ->
            ConfigSettingRow(
                title = entry.title,
                summary = entry.summary,
                imageVector = entry.imageVector,
                tone = entry.tone,
                iconTone = entry.iconTone,
                showDivider = index != entries.lastIndex,
                onClick = entry.onClick,
            )
        }
    }
}

private fun buildRuntimeTone(runtimeSnapshot: RuntimeSnapshot): SemanticTone {
    return when (runtimeSnapshot.phase) {
        RuntimePhase.Failed -> SemanticTone.Danger
        RuntimePhase.Running ->
            if (runtimeSnapshot.payloadReady) SemanticTone.Success else SemanticTone.Warning
        RuntimePhase.Starting,
        RuntimePhase.Stopping -> SemanticTone.Info
        RuntimePhase.Idle -> SemanticTone.Neutral
    }
}

private fun buildRuntimeSummary(runtimeSnapshot: RuntimeSnapshot): String {
    return when (runtimeSnapshot.phase) {
        RuntimePhase.Idle -> MLangStatus.Meta.RuntimeIdle
        RuntimePhase.Starting -> MLangStatus.Meta.RuntimeStarting
        RuntimePhase.Running ->
            if (runtimeSnapshot.payloadReady) {
                MLangStatus.Meta.RuntimeStable
            } else {
                MLangStatus.Meta.RuntimeRunningDegraded
            }
        RuntimePhase.Stopping -> MLangStatus.Meta.RuntimeStopping
        RuntimePhase.Failed -> MLangStatus.Meta.RuntimeAttention
    }
}

private fun RuntimePhase.toMetaDisplayLabel(): String {
    return when (this) {
        RuntimePhase.Idle -> MLangStatus.Meta.IdleShort
        RuntimePhase.Starting -> MLangStatus.Meta.StartingShort
        RuntimePhase.Running -> MLangStatus.Phase.Running
        RuntimePhase.Stopping -> MLangStatus.Meta.StoppingShort
        RuntimePhase.Failed -> MLangStatus.Meta.FailedShort
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
                            com.github.nomadboxlab.monadbox.core.NetworkConstants.DEFAULT_USER_AGENT,
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

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

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.state.ToggleableState
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.GeoFileType
import com.github.yumelira.yumebox.core.model.GeoXItem
import com.github.yumelira.yumebox.core.model.geoXItems
import com.github.yumelira.yumebox.feature.editor.language.LanguageScope
import com.github.yumelira.yumebox.presentation.util.OverrideStructuredEditorStore
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import okhttp3.OkHttpClient
import okhttp3.Request
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ConnectionScreenDestination
import com.ramcosta.composedestinations.generated.destinations.OverrideConfigPreviewRouteDestination
import com.ramcosta.composedestinations.generated.destinations.TrafficStatisticsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.extra.SuperArrow
import java.io.File

@Composable
@Destination<RootGraph>
fun MetaFeatureScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val showGeoXDownloadSheet = remember { mutableStateOf(false) }
    var runtimeConfigLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.MetaFeature.Title,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ScreenLazyColumn(scrollBehavior = scrollBehavior, innerPadding = innerPadding) {
            item {
                SmallTitle(MLang.MetaFeature.Title)
                Card {
                    SuperArrow(
                        title = MLang.Connection.Title,
                        summary = MLang.Connection.Summary,
                        onClick = {
                            navigator.navigate(ConnectionScreenDestination) {
                                launchSingleTop = true
                            }
                        },
                    )
                    SuperArrow(
                        title = MLang.MetaFeature.RecentRequests.Title,
                        summary = MLang.MetaFeature.RecentRequests.Summary,
                        onClick = {
                            navigator.navigate(TrafficStatisticsScreenDestination) {
                                launchSingleTop = true
                            }
                        },
                    )
                }
            }
            item {
                SmallTitle(MLang.MetaFeature.RuntimeConfig.Title)
                Card {
                    SuperArrow(
                        title = MLang.MetaFeature.RuntimeConfig.Title,
                        summary = MLang.MetaFeature.RuntimeConfig.Summary,
                        onClick = {
                            if (runtimeConfigLoading) return@SuperArrow
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

                                    val runtimeYaml = withContext(Dispatchers.IO) {
                                        val file = File(configPath)
                                        if (!file.exists() || !file.isFile) {
                                            null
                                        } else {
                                            file.readText()
                                        }
                                    }

                                    if (runtimeYaml.isNullOrBlank()) {
                                        context.toast(MLang.MetaFeature.RuntimeConfig.ConfigNotFound)
                                        return@launch
                                    }

                                    val previewTitle = activeProfile
                                        .name
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { MLang.MetaFeature.RuntimeConfig.PreviewTitleWithProfile.format(it) }
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
                                    val message = when {
                                        error.message?.contains("unauthorized", ignoreCase = true) == true -> {
                                            MLang.MetaFeature.RuntimeConfig.RuntimeConfigUnauthorized
                                        }

                                        else -> {
                                            MLang.MetaFeature.RuntimeConfig.RuntimeConfigFetchFailed
                                                .format(error.runtimeGatewayMessage(MLang.MetaFeature.RuntimeConfig.LoadFailed))
                                        }
                                    }
                                    context.toast(message)
                                } finally {
                                    runtimeConfigLoading = false
                                }
                            }
                        },
                    )
                }
            }
            item {
                SmallTitle(MLang.MetaFeature.GeoX.OnlineUpdateTitle)
                Card {
                    SuperArrow(
                        title = MLang.MetaFeature.GeoX.OnlineUpdateTitle,
                        summary = MLang.MetaFeature.GeoX.OnlineUpdateSummary,
                        onClick = { showGeoXDownloadSheet.value = true },
                    )
                }
            }
        }
        GeoXDownloadSheet(
            show = showGeoXDownloadSheet,
            context = context,
            scope = scope,
        )
    }
}

private fun decodeEscapedUnicode(text: String): String {
    val out = StringBuilder(text.length)
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        if (ch == '\\' && i + 1 < text.length) {
            val marker = text[i + 1]
            val hexLen = when (marker) {
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
        startAction = {
            AppBottomSheetCloseAction(
                onClick = { show.value = false },
            )
        },
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
                                }
                            )
                        },
                        onClick = {
                            selectedItems[item.type] = !(selectedItems[item.type] ?: false)
                        }
                    )
                }
            }
        })
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
                val request = Request.Builder()
                    .url(item.url)
                    // You could add a generic User-Agent array here if required, though OkHttp sets a default.
                    .header("User-Agent", com.github.yumelira.yumebox.core.NetworkConstants.DEFAULT_USER_AGENT)
                    .build()
                
                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.byteStream()?.use { input ->
                                targetFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
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

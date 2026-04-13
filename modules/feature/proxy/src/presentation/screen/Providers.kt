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

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.domain.model.ErrorCategory
import com.github.yumelira.yumebox.domain.model.ErrorImpact
import com.github.yumelira.yumebox.domain.model.ErrorPhase
import com.github.yumelira.yumebox.domain.model.ErrorRetryability
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import com.github.yumelira.yumebox.presentation.component.AppActionBottomSheet
import com.github.yumelira.yumebox.presentation.component.AppCircularIconAction
import com.github.yumelira.yumebox.presentation.component.AppCommandButton
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.CenteredText
import com.github.yumelira.yumebox.presentation.component.DiagnosticBannerState
import com.github.yumelira.yumebox.presentation.component.InfoSettingRow
import com.github.yumelira.yumebox.presentation.component.NavigationBackIcon
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.component.orFallback
import com.github.yumelira.yumebox.presentation.component.toDisplayLabel
import com.github.yumelira.yumebox.presentation.component.toSemanticTone
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Circle-fading-arrow-up`
import com.github.yumelira.yumebox.presentation.icon.yume.Edit
import com.github.yumelira.yumebox.presentation.icon.yume.Folders
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.util.ExternalResourceDiagnostics
import com.github.yumelira.yumebox.presentation.util.buildExternalResourceDiagnostics
import com.github.yumelira.yumebox.presentation.viewmodel.ProvidersViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import dev.oom_wg.purejoy.mlang.MLang
import java.text.SimpleDateFormat
import java.util.*
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class ProviderSection(val title: String, val providers: List<Provider>)

private data class RemoteOverrideSection(
    val title: String,
    val resources: List<RemoteOverrideResource>,
)

@Composable
fun ProvidersContent(
    navigator: DestinationsNavigator,
    viewModel: ProvidersViewModel = koinViewModel(),
    onRefreshSourcesRequest: () -> Unit = {},
    refreshSourcesInProgress: Boolean = false,
    diagnosticContent:
        @Composable
        (
            ExternalResourceDiagnostics, com.github.yumelira.yumebox.domain.model.StructuredError?,
        ) -> Unit =
        { externalDiagnostics, structuredError ->
            ProvidersDiagnosticSummaryCard(
                banner = externalDiagnostics.toBannerState(),
                structuredError = structuredError,
            )
        },
) {
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current

    val providers by viewModel.providers.collectAsStateWithLifecycle()
    val remoteOverrides by viewModel.remoteOverrides.collectAsStateWithLifecycle()
    val isRunning by viewModel.isRunning.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isRunning) { viewModel.refreshProviders() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            context.toast(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            context.toast(it, Toast.LENGTH_LONG)
            viewModel.clearError()
        }
    }

    val sections =
        remember(providers) {
            buildList {
                val proxyProviders = providers.filter { it.type == Provider.Type.Proxy }
                if (proxyProviders.isNotEmpty()) {
                    add(
                        ProviderSection(
                            title = MLang.Providers.Type.ProxyProviders.format(proxyProviders.size),
                            providers = proxyProviders,
                        )
                    )
                }
                val ruleProviders = providers.filter { it.type == Provider.Type.Rule }
                if (ruleProviders.isNotEmpty()) {
                    add(
                        ProviderSection(
                            title = MLang.Providers.Type.RuleProviders.format(ruleProviders.size),
                            providers = ruleProviders,
                        )
                    )
                }
            }
        }

    val remoteSections =
        remember(remoteOverrides) {
            buildList {
                if (remoteOverrides.isNotEmpty()) {
                    add(
                        RemoteOverrideSection(
                            title =
                                MLang.Providers.Type.OverrideResources.format(remoteOverrides.size),
                            resources = remoteOverrides,
                        )
                    )
                }
            }
        }
    val externalDiagnostics =
        remember(providers, remoteOverrides) {
            buildExternalResourceDiagnostics(
                providers = providers,
                remoteOverrides = remoteOverrides,
            )
        }
    val structuredError =
        remember(uiState.structuredError, uiState.error) {
            uiState.structuredError.orFallback(
                message = uiState.error,
                category = ErrorCategory.Runtime,
                phase = ErrorPhase.Running,
                impact = ErrorImpact.FeatureUnavailable,
                retryability = ErrorRetryability.Retryable,
            )
        }

    Scaffold(
        topBar = {
            TopBar(
                title = MLang.Providers.Title,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    NavigationBackIcon(
                        navigator = navigator,
                        contentDescription = MLang.Component.Navigation.Back,
                    )
                },
                actions = {
                    if (
                        providers.any { it.vehicleType == Provider.VehicleType.HTTP } ||
                            remoteOverrides.isNotEmpty()
                    ) {
                        AppCircularIconAction(
                            imageVector = Yume.`Circle-fading-arrow-up`,
                            contentDescription = MLang.Providers.Action.UpdateAll,
                            onClick = onRefreshSourcesRequest,
                            enabled = !refreshSourcesInProgress,
                            tone = SemanticTone.Info,
                            highEmphasis = true,
                            size = 44.dp,
                            iconSize = 20.dp,
                            modifier = Modifier.padding(end = 24.dp),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        if (!isRunning && remoteOverrides.isEmpty()) {
            CenteredText(
                firstLine = MLang.Providers.Empty.NotRunning,
                secondLine = MLang.Providers.Empty.NotRunningHint,
            )
        } else if (providers.isEmpty() && remoteOverrides.isEmpty() && !uiState.isLoading) {
            CenteredText(
                firstLine = MLang.Providers.Empty.NoProviders,
                secondLine = MLang.Providers.Empty.NoProvidersHint,
            )
        } else {
            ScreenLazyColumn(scrollBehavior = scrollBehavior, innerPadding = innerPadding) {
                item(key = "providers_diagnostics") {
                    ProvidersCenteredContent {
                        diagnosticContent(externalDiagnostics, structuredError)
                    }
                }
                sections.forEach { section ->
                    providerSection(
                        section = section,
                        isUpdating = { providerKey ->
                            uiState.updatingProviders.contains(providerKey)
                        },
                        onUpdate = { provider -> viewModel.updateProvider(provider) },
                        onUpload = { provider, uri ->
                            viewModel.uploadProviderFile(context, provider, uri)
                        },
                    )
                }
                remoteSections.forEach { section ->
                    remoteOverrideSection(
                        section = section,
                        isUpdating = { resourceId ->
                            uiState.updatingProviders.contains("override_$resourceId")
                        },
                        onUpdate = { resource -> viewModel.updateRemoteOverride(resource) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProvidersDiagnosticSummaryCard(
    banner: DiagnosticBannerState,
    structuredError: com.github.yumelira.yumebox.domain.model.StructuredError?,
) {
    Card {
        InfoSettingRow(
            title = banner.headline,
            summary = banner.subtitle,
            valueLabel = banner.tone.toProviderStateLabel(),
            tone = banner.tone,
            badgeLeadingDot =
                banner.tone == SemanticTone.Warning || banner.tone == SemanticTone.Danger,
            showDivider = structuredError != null,
        )
        structuredError?.let { error ->
            InfoSettingRow(
                title = error.userVisibleMessage,
                summary = error.technicalDetail ?: error.rawCause,
                valueLabel = error.impact.toDisplayLabel(),
                tone = error.toSemanticTone(),
                badgeLeadingDot = true,
                showDivider = false,
            )
        }
    }
}

private fun SemanticTone.toProviderStateLabel(): String {
    return when (this) {
        SemanticTone.Success,
        SemanticTone.Brand -> DiagnosticLang.DetailPages.Common.Ready
        SemanticTone.Info -> DiagnosticLang.DetailPages.Common.Waiting
        SemanticTone.Warning -> DiagnosticLang.DetailPages.Common.Attention
        SemanticTone.Danger -> DiagnosticLang.DetailPages.Remediation.Failed
        SemanticTone.Neutral -> DiagnosticLang.DetailPages.Common.NotAvailable
    }
}

@Composable
private fun RemoteOverrideCard(
    resource: RemoteOverrideResource,
    isUpdating: Boolean,
    onUpdate: () -> Unit,
) {
    Card(modifier = Modifier.padding(vertical = 4.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = resource.name,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = resource.sourceUrl,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text =
                        MLang.Providers.Summary.OverrideIntervalAndCount.format(
                            resource.updateIntervalSeconds,
                            resource.ruleCount,
                        ),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }

            AppCommandButton(
                title = MLang.Providers.Action.Update,
                imageVector = Yume.`Circle-fading-arrow-up`,
                enabled = !isUpdating,
                onClick = onUpdate,
                tone = SemanticTone.Info,
                highEmphasis = true,
            )
        }
    }
}

@Composable
private fun ProviderCard(
    provider: Provider,
    isUpdating: Boolean,
    onUpdate: () -> Unit,
    onUpload: (Uri) -> Unit,
) {
    var showActionsSheet by remember { mutableStateOf(false) }

    val filePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            uri: Uri? ->
            uri?.let { onUpload(it) }
        }

    Card(modifier = Modifier.padding(vertical = 4.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = provider.name,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.size(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = provider.vehicleType.name,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    if (provider.updatedAt > 0) {
                        Text(
                            text = "•",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                        Text(
                            text = formatTimestamp(provider.updatedAt),
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }

            if (provider.path.isNotBlank()) {
                AppCommandButton(
                    title = MLang.Providers.Action.Operation,
                    imageVector = Yume.Edit,
                    enabled = !isUpdating,
                    onClick = { showActionsSheet = true },
                    tone = SemanticTone.Info,
                )
            }
        }
    }

    AppActionBottomSheet(
        show = showActionsSheet,
        title = provider.name,
        onDismissRequest = { showActionsSheet = false },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppCommandButton(
                title = MLang.Providers.Action.Update,
                imageVector = Yume.`Circle-fading-arrow-up`,
                enabled = !isUpdating,
                onClick = {
                    showActionsSheet = false
                    onUpdate()
                },
                tone = SemanticTone.Info,
                highEmphasis = true,
            )
            AppCommandButton(
                title = MLang.Providers.Action.Upload,
                imageVector = Yume.Folders,
                enabled = !isUpdating,
                onClick = {
                    showActionsSheet = false
                    filePicker.launch("*/*")
                },
                tone = SemanticTone.Neutral,
            )
        }
    }
}

private fun LazyListScope.providerSection(
    section: ProviderSection,
    isUpdating: (String) -> Boolean,
    onUpdate: (Provider) -> Unit,
    onUpload: (Provider, Uri) -> Unit,
) {
    item(key = "title_${section.title}") { ProvidersCenteredContent { SmallTitle(section.title) } }
    items(
        items = section.providers,
        key = { provider -> "${provider.type}_${provider.name}" },
        contentType = { "ProviderCard" },
    ) { provider ->
        val providerKey = "${provider.type}_${provider.name}"
        ProvidersCenteredContent {
            ProviderCard(
                provider = provider,
                isUpdating = isUpdating(providerKey),
                onUpdate = { onUpdate(provider) },
                onUpload = { uri -> onUpload(provider, uri) },
            )
        }
    }
}

private fun LazyListScope.remoteOverrideSection(
    section: RemoteOverrideSection,
    isUpdating: (String) -> Boolean,
    onUpdate: (RemoteOverrideResource) -> Unit,
) {
    item(key = "title_${section.title}") { ProvidersCenteredContent { SmallTitle(section.title) } }
    items(
        items = section.resources,
        key = { resource -> "override_${resource.id}" },
        contentType = { "RemoteOverrideCard" },
    ) { resource ->
        ProvidersCenteredContent {
            RemoteOverrideCard(
                resource = resource,
                isUpdating = isUpdating(resource.id),
                onUpdate = { onUpdate(resource) },
            )
        }
    }
}

@Composable
private fun ProvidersCenteredContent(content: @Composable () -> Unit) {
    val pageMetrics = AppTheme.pageMetrics

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.adaptiveContentWidth(pageMetrics.contentMaxWidth)) { content() }
    }
}

private fun formatTimestamp(ts: Long): String {
    return SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(ts))
}

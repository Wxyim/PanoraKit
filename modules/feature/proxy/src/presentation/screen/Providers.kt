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

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.yumelira.yumebox.common.util.toast
import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import com.github.yumelira.yumebox.presentation.component.AppCircularIconAction
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.CenteredText
import com.github.yumelira.yumebox.presentation.component.NavigationBackIcon
import com.github.yumelira.yumebox.presentation.component.ReadonlyInfoField
import com.github.yumelira.yumebox.presentation.component.ScreenLazyColumn
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.SmallTitle
import com.github.yumelira.yumebox.presentation.component.StatusBadge
import com.github.yumelira.yumebox.presentation.component.TopBar
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Circle-fading-arrow-up`
import com.github.yumelira.yumebox.presentation.icon.yume.Link
import com.github.yumelira.yumebox.presentation.theme.AppTheme
import com.github.yumelira.yumebox.presentation.theme.adaptiveContentWidth
import com.github.yumelira.yumebox.presentation.util.canUpdateFromRemote
import com.github.yumelira.yumebox.presentation.util.itemCountLabel
import com.github.yumelira.yumebox.presentation.util.statusLabel
import com.github.yumelira.yumebox.presentation.util.statusTone
import com.github.yumelira.yumebox.presentation.util.transportTone
import com.github.yumelira.yumebox.presentation.util.updatedAtLabel
import com.github.yumelira.yumebox.presentation.viewmodel.ProvidersViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    val canRefreshAll =
        remember(providers, remoteOverrides) {
            providers.any(Provider::canUpdateFromRemote) || remoteOverrides.isNotEmpty()
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
                    if (canRefreshAll) {
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
                sections.forEach { section ->
                    providerSection(
                        section = section,
                        isUpdating = { providerKey ->
                            uiState.updatingProviders.contains(providerKey)
                        },
                        onUpdate = { provider -> viewModel.updateProvider(provider) },
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
private fun RemoteOverrideCard(
    resource: RemoteOverrideResource,
    isUpdating: Boolean,
    onUpdate: () -> Unit,
) {
    val statusTone = resource.statusTone(isUpdating = isUpdating)

    Card(modifier = Modifier.padding(vertical = 4.dp), cornerRadius = 28) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = resource.name,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                StatusBadge(
                    text = resource.statusLabel(isUpdating = isUpdating),
                    tone = statusTone,
                    leadingDot = true,
                    compact = true,
                )
            }

            ReadonlyInfoField(
                imageVector = Yume.Link,
                value = resource.sourceUrl,
                summary = null,
                tone = resource.transportTone(),
                compact = true,
                maxLines = 1,
            )

            ResourceCardFooter(
                badges = {
                    resource.updatedAtLabel(::formatTimestamp)?.let { label ->
                        StatusBadge(text = label, tone = SemanticTone.Neutral, compact = true)
                    }
                    resource.itemCountLabel()?.let { label ->
                        StatusBadge(text = label, tone = SemanticTone.Neutral, compact = true)
                    }
                },
                canUpdate = true,
                isUpdating = isUpdating,
                onUpdate = onUpdate,
            )
        }
    }
}

@Composable
private fun ProviderCard(provider: Provider, isUpdating: Boolean, onUpdate: () -> Unit) {
    val statusTone = provider.statusTone(isUpdating = isUpdating)
    val canUpdate = provider.canUpdateFromRemote()

    Card(modifier = Modifier.padding(vertical = 4.dp), cornerRadius = 24) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = provider.name,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                StatusBadge(
                    text = provider.statusLabel(isUpdating = isUpdating),
                    tone = statusTone,
                    leadingDot = true,
                    compact = true,
                )
            }

            ResourceCardFooter(
                badges = {
                    provider.updatedAtLabel(::formatTimestamp)?.let { label ->
                        StatusBadge(text = label, tone = SemanticTone.Neutral, compact = true)
                    }
                    provider.itemCountLabel()?.let { label ->
                        StatusBadge(text = label, tone = SemanticTone.Neutral, compact = true)
                    }
                },
                canUpdate = canUpdate,
                isUpdating = isUpdating,
                onUpdate = onUpdate,
            )
        }
    }
}

@Composable
private fun ResourceCardFooter(
    badges: @Composable () -> Unit,
    canUpdate: Boolean,
    isUpdating: Boolean,
    onUpdate: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            badges()
        }

        if (canUpdate) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp),
            ) {
                AppCircularIconAction(
                    imageVector = Yume.`Circle-fading-arrow-up`,
                    contentDescription = MLang.Providers.Action.Update,
                    onClick = onUpdate,
                    enabled = !isUpdating,
                    tone = SemanticTone.Info,
                    highEmphasis = true,
                    chromeTone = SemanticTone.Info,
                    chromeHighEmphasis = true,
                    size = 40.dp,
                    iconSize = 18.dp,
                )
            }
        }
    }
}

private fun LazyListScope.providerSection(
    section: ProviderSection,
    isUpdating: (String) -> Boolean,
    onUpdate: (Provider) -> Unit,
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

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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import com.github.yumelira.yumebox.presentation.util.OverrideExtraFieldDraft
import com.github.yumelira.yumebox.presentation.util.OverrideProxyGroupDraft
import com.github.yumelira.yumebox.presentation.util.OverrideProxyGroupTypePresets
import com.github.yumelira.yumebox.presentation.util.OverrideStructuredEditorStore
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun OverrideProxyGroupDraftEditorScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val listState = rememberLazyListState()
    val title = remember {
        OverrideStructuredEditorStore.proxyGroupDraftEditorTitle.ifBlank {
            MLang.Override.Editor.ProxyGroup
        }
    }
    val initialValue = remember { OverrideStructuredEditorStore.proxyGroupDraftEditorValue }
    val saveFabController = rememberOverrideFabController()

    var name by remember { mutableStateOf(initialValue?.name.orEmpty()) }
    var type by remember { mutableStateOf(initialValue?.type?.ifBlank { "select" } ?: "select") }
    var proxies by remember { mutableStateOf(initialValue?.proxies.orEmpty()) }
    var useText by remember { mutableStateOf(initialValue?.use.orEmpty().joinToString("\n")) }
    var url by remember { mutableStateOf(initialValue?.url.orEmpty()) }
    var intervalText by remember { mutableStateOf(initialValue?.interval?.toString().orEmpty()) }
    var timeoutText by remember { mutableStateOf(initialValue?.timeout?.toString().orEmpty()) }
    var maxFailedTimesText by remember {
        mutableStateOf(initialValue?.maxFailedTimes?.toString().orEmpty())
    }
    var interfaceName by remember { mutableStateOf(initialValue?.interfaceName.orEmpty()) }
    var routingMarkText by remember {
        mutableStateOf(initialValue?.routingMark?.toString().orEmpty())
    }
    var filter by remember { mutableStateOf(initialValue?.filter.orEmpty()) }
    var excludeFilter by remember { mutableStateOf(initialValue?.excludeFilter.orEmpty()) }
    var excludeType by remember { mutableStateOf(initialValue?.excludeType.orEmpty()) }
    var expectedStatus by remember { mutableStateOf(initialValue?.expectedStatus.orEmpty()) }
    var icon by remember { mutableStateOf(initialValue?.icon.orEmpty()) }
    var lazy by remember { mutableStateOf(initialValue?.lazy) }
    var disableUdp by remember { mutableStateOf(initialValue?.disableUdp) }
    var includeAll by remember { mutableStateOf(initialValue?.includeAll) }
    var includeAllProxies by remember { mutableStateOf(initialValue?.includeAllProxies) }
    var includeAllProviders by remember { mutableStateOf(initialValue?.includeAllProviders) }
    var hidden by remember { mutableStateOf(initialValue?.hidden) }
    var extraFields by remember { mutableStateOf(initialValue?.extraFields.orEmpty()) }
    var editingExtraKey by remember { mutableStateOf<String?>(null) }
    var showExtraFieldDialog by remember { mutableStateOf(false) }
    var showProxySelector by remember { mutableStateOf(false) }
    var showTypeSelector by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val referenceCatalog = OverrideStructuredEditorStore.currentReferenceCatalog()
    val excludedGroupNames =
        remember(name, initialValue?.name) {
            buildSet {
                name.trim().takeIf(String::isNotBlank)?.let(::add)
                initialValue?.name?.trim()?.takeIf(String::isNotBlank)?.let(::add)
            }
        }
    val availableProxyGroupNames =
        remember(referenceCatalog.proxyGroupNames, excludedGroupNames) {
            referenceCatalog.proxyGroupNames.filterNot(excludedGroupNames::contains)
        }
    val proxySelectionGroups =
        remember(referenceCatalog.proxyNames, availableProxyGroupNames) {
            listOfNotNull(
                referenceCatalog.proxyNames
                    .takeIf { it.isNotEmpty() }
                    ?.let { values ->
                        OverrideSelectionGroup(
                            title = MLang.Override.Editor.ProxyNode,
                            items = values,
                        )
                    },
                availableProxyGroupNames
                    .takeIf { it.isNotEmpty() }
                    ?.let { values ->
                        OverrideSelectionGroup(
                            title = MLang.Override.Editor.ProxyGroup,
                            items = values,
                        )
                    },
            )
        }

    DisposableEffect(Unit) {
        onDispose { OverrideStructuredEditorStore.clearProxyGroupDraftEditor() }
    }

    Scaffold(
        floatingActionButton = {
            OverrideAnimatedFab(
                controller = saveFabController,
                visible = true,
                imageVector = Yume.Check,
                contentDescription = MLang.Override.Editor.SaveProxyGroup,
                label = MLang.Override.Draft.Save,
                onClick = {
                    if (name.trim().isBlank()) {
                        errorText = MLang.Override.Draft.NameRequired
                        return@OverrideAnimatedFab
                    }
                    if (type.trim().isBlank()) {
                        errorText = MLang.Override.Editor.TypeEmpty
                        return@OverrideAnimatedFab
                    }
                    OverrideStructuredEditorStore.submitProxyGroupDraft(
                        OverrideProxyGroupDraft(
                            name = name.trim(),
                            type = type.trim(),
                            proxies = proxies,
                            use = parseMultilineValues(useText),
                            url = url.trim(),
                            interval = intervalText.trim().toIntOrNull(),
                            lazy = lazy,
                            timeout = timeoutText.trim().toIntOrNull(),
                            maxFailedTimes = maxFailedTimesText.trim().toIntOrNull(),
                            disableUdp = disableUdp,
                            interfaceName = interfaceName.trim(),
                            routingMark = routingMarkText.trim().toIntOrNull(),
                            includeAll = includeAll,
                            includeAllProxies = includeAllProxies,
                            includeAllProviders = includeAllProviders,
                            filter = filter.trim(),
                            excludeFilter = excludeFilter.trim(),
                            excludeType = excludeType.trim(),
                            expectedStatus = expectedStatus.trim(),
                            hidden = hidden,
                            icon = icon.trim(),
                            extraFields = extraFields,
                            uiId = initialValue?.uiId ?: OverrideProxyGroupDraft().uiId,
                        )
                    )
                    navigator.navigateUp()
                },
            )
        },
        topBar = { TopBar(title = title, scrollBehavior = scrollBehavior) },
    ) { innerPadding ->
        ScreenLazyColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = innerPadding,
            bottomPadding = OverrideFloatingActionContentBottomPadding,
            lazyListState = listState,
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(OverrideSectionSpacing),
                ) {
                    OverrideCardSection(MLang.Override.Draft.BasicInfo) {
                        ConfigSettingRow(
                            title = MLang.Override.Editor.RuleType,
                            valueLabel = type.ifBlank { MLang.Component.Selector.UseDefault },
                            tone = SemanticTone.Info,
                            badgeTone = SemanticTone.Info,
                            onClick = {
                                showTypeSelector = true
                                errorText = null
                            },
                        )
                        StringInputContent(
                            title = MLang.Override.Draft.Name,
                            value = name.takeIf(String::isNotBlank),
                            placeholder = MLang.Override.Draft.Name,
                            unsetLabel = "",
                            onValueChange = {
                                name = it.orEmpty()
                                errorText = null
                            },
                        )
                        errorText
                            ?.takeIf { it.contains(MLang.Override.Draft.Name) }
                            ?.let { message ->
                                OverrideFieldAssistText(
                                    text = message,
                                    color = MiuixTheme.colorScheme.error,
                                )
                            }
                        StringInputContent(
                            title = "url",
                            value = url.takeIf(String::isNotBlank),
                            placeholder = "url",
                            unsetLabel = "",
                            onValueChange = { url = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "interval",
                            value = intervalText.takeIf(String::isNotBlank),
                            placeholder = "interval",
                            unsetLabel = "",
                            onValueChange = { intervalText = it?.filter(Char::isDigit).orEmpty() },
                        )
                        StringInputContent(
                            title = "timeout",
                            value = timeoutText.takeIf(String::isNotBlank),
                            placeholder = "timeout",
                            unsetLabel = "",
                            onValueChange = { timeoutText = it?.filter(Char::isDigit).orEmpty() },
                        )
                        StringInputContent(
                            title = "max-failed-times",
                            value = maxFailedTimesText.takeIf(String::isNotBlank),
                            placeholder = "max-failed-times",
                            unsetLabel = "",
                            onValueChange = {
                                maxFailedTimesText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                    }
                    OverrideCardSection(MLang.Override.Editor.MemberSource) {
                        ConfigSettingRow(
                            title = "proxies",
                            onClick = {
                                showProxySelector = true
                                errorText = null
                            },
                        )
                        StringInputContent(
                            title = "use",
                            value = useText.takeIf(String::isNotBlank),
                            placeholder = "use",
                            unsetLabel = "",
                            onValueChange = { useText = it.orEmpty() },
                        )
                        OverrideFieldAssistText(
                            text = MLang.Override.Edit.OneProviderPerLineHint,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                    OverrideCardSection(MLang.Override.Editor.HealthCheckAndFilter) {
                        StringInputContent(
                            title = "interface-name",
                            value = interfaceName.takeIf(String::isNotBlank),
                            placeholder = "interface-name",
                            unsetLabel = "",
                            onValueChange = { interfaceName = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "routing-mark",
                            value = routingMarkText.takeIf(String::isNotBlank),
                            placeholder = "routing-mark",
                            unsetLabel = "",
                            onValueChange = {
                                routingMarkText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                        StringInputContent(
                            title = "filter",
                            value = filter.takeIf(String::isNotBlank),
                            placeholder = "filter",
                            unsetLabel = "",
                            onValueChange = { filter = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "exclude-filter",
                            value = excludeFilter.takeIf(String::isNotBlank),
                            placeholder = "exclude-filter",
                            unsetLabel = "",
                            onValueChange = { excludeFilter = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "exclude-type",
                            value = excludeType.takeIf(String::isNotBlank),
                            placeholder = "exclude-type",
                            unsetLabel = "",
                            onValueChange = { excludeType = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "expected-status",
                            value = expectedStatus.takeIf(String::isNotBlank),
                            placeholder = "expected-status",
                            unsetLabel = "",
                            onValueChange = { expectedStatus = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "icon",
                            value = icon.takeIf(String::isNotBlank),
                            placeholder = "icon",
                            unsetLabel = "",
                            onValueChange = { icon = it.orEmpty() },
                        )
                    }
                    OverrideCardSection(MLang.Override.Structured.Proxies.Title) {
                        NullableBooleanSelector(
                            title = "lazy",
                            value = lazy,
                            onValueChange = { lazy = it },
                        )
                        NullableBooleanSelector(
                            title = "disable-udp",
                            value = disableUdp,
                            onValueChange = { disableUdp = it },
                        )
                        NullableBooleanSelector(
                            title = "include-all",
                            value = includeAll,
                            onValueChange = { includeAll = it },
                        )
                        NullableBooleanSelector(
                            title = "include-all-proxies",
                            value = includeAllProxies,
                            onValueChange = { includeAllProxies = it },
                        )
                        NullableBooleanSelector(
                            title = "include-all-providers",
                            value = includeAllProviders,
                            onValueChange = { includeAllProviders = it },
                        )
                        NullableBooleanSelector(
                            title = "hidden",
                            value = hidden,
                            onValueChange = { hidden = it },
                        )
                    }
                    OverrideSection(MLang.Override.Draft.ExtraFields) {
                        OverrideExtraFieldsCard(
                            title = MLang.Override.Draft.ExtraFields,
                            fields = extraFields,
                            onAddClick = {
                                editingExtraKey = null
                                showExtraFieldDialog = true
                            },
                            onEditClick = { key, _ ->
                                editingExtraKey = key
                                showExtraFieldDialog = true
                            },
                            onDeleteClick = { key -> extraFields = extraFields - key },
                        )
                    }
                    Spacer(modifier = Modifier.height(OverrideSectionBottomSpacing))
                }
            }
        }
        OverrideSingleValueSelectionSheet(
            show = showTypeSelector,
            title = MLang.Override.Editor.RuleType,
            value = type,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Editor.RuleType,
                        items = OverrideProxyGroupTypePresets,
                    )
                ),
            customInputLabel = "",
            allowCustomValue = false,
            onDismiss = { showTypeSelector = false },
            onConfirm = { selectedValue ->
                type = selectedValue.trim().ifBlank { type }
                errorText = null
                showTypeSelector = false
            },
        )
        OverrideExtraFieldDialog(
            show = showExtraFieldDialog,
            title =
                if (editingExtraKey == null) MLang.Override.Draft.AddExtraField
                else MLang.Override.Draft.EditExtraField,
            initialValue = editingExtraKey?.let(extraFields::toExtraFieldDraft),
            onConfirm = { draft: OverrideExtraFieldDraft ->
                extraFields = extraFields.updateExtraField(editingExtraKey, draft)
                editingExtraKey = null
                showExtraFieldDialog = false
            },
            onDismiss = {
                editingExtraKey = null
                showExtraFieldDialog = false
            },
        )
        OverrideMultiValueSelectionSheet(
            show = showProxySelector,
            title = MLang.Override.Editor.SelectProxyGroupMember,
            values = proxies,
            groups = proxySelectionGroups,
            customInputLabel = MLang.Override.Editor.CustomMember,
            onDismiss = { showProxySelector = false },
            onConfirm = { selectedValues ->
                proxies = selectedValues
                errorText = null
                showProxySelector = false
            },
        )
    }
}

private fun parseMultilineValues(rawValue: String): List<String> {
    return rawValue.lines().map(String::trim).filter(String::isNotBlank)
}

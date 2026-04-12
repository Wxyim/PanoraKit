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
import com.github.yumelira.yumebox.presentation.util.OverrideProxyDraft
import com.github.yumelira.yumebox.presentation.util.OverrideProxyTypePresets
import com.github.yumelira.yumebox.presentation.util.OverrideStructuredEditorStore
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun OverrideProxyDraftEditorScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val listState = rememberLazyListState()
    val title = remember {
        OverrideStructuredEditorStore.proxyDraftEditorTitle.ifBlank {
            MLang.Override.Editor.ProxyNode
        }
    }
    val initialValue = remember { OverrideStructuredEditorStore.proxyDraftEditorValue }
    val saveFabController = rememberOverrideFabController()

    var name by remember { mutableStateOf(initialValue?.name.orEmpty()) }
    var type by remember { mutableStateOf(initialValue?.type?.ifBlank { "ss" } ?: "ss") }
    var server by remember { mutableStateOf(initialValue?.server.orEmpty()) }
    var portText by remember { mutableStateOf(initialValue?.port?.toString().orEmpty()) }
    var ipVersion by remember { mutableStateOf(initialValue?.ipVersion.orEmpty()) }
    var interfaceName by remember { mutableStateOf(initialValue?.interfaceName.orEmpty()) }
    var routingMarkText by remember {
        mutableStateOf(initialValue?.routingMark?.toString().orEmpty())
    }
    var dialerProxy by remember { mutableStateOf(initialValue?.dialerProxy.orEmpty()) }
    var udp by remember { mutableStateOf(initialValue?.udp) }
    var tfo by remember { mutableStateOf(initialValue?.tfo) }
    var mptcp by remember { mutableStateOf(initialValue?.mptcp) }
    var extraFields by remember { mutableStateOf(initialValue?.extraFields.orEmpty()) }
    var editingExtraKey by remember { mutableStateOf<String?>(null) }
    var showExtraFieldDialog by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var showTypeSelector by remember { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { OverrideStructuredEditorStore.clearProxyDraftEditor() } }

    Scaffold(
        floatingActionButton = {
            OverrideAnimatedFab(
                controller = saveFabController,
                visible = true,
                imageVector = Yume.Check,
                contentDescription = MLang.Override.Editor.SaveProxyNode,
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
                    OverrideStructuredEditorStore.submitProxyDraft(
                        OverrideProxyDraft(
                            name = name.trim(),
                            type = type.trim(),
                            server = server.trim(),
                            port = portText.trim().toIntOrNull(),
                            ipVersion = ipVersion.trim(),
                            udp = udp,
                            interfaceName = interfaceName.trim(),
                            routingMark = routingMarkText.trim().toIntOrNull(),
                            tfo = tfo,
                            mptcp = mptcp,
                            dialerProxy = dialerProxy.trim(),
                            extraFields = extraFields,
                            uiId = initialValue?.uiId ?: OverrideProxyDraft().uiId,
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
                    OverrideCardSection(MLang.Override.Editor.BasicConnection) {
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
                            title = "server",
                            value = server.takeIf(String::isNotBlank),
                            placeholder = "server",
                            unsetLabel = "",
                            onValueChange = { server = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "port",
                            value = portText.takeIf(String::isNotBlank),
                            placeholder = "port",
                            unsetLabel = "",
                            onValueChange = { portText = it?.filter(Char::isDigit).orEmpty() },
                        )
                    }
                    OverrideCardSection(MLang.Override.Editor.NetworkAndRoute) {
                        StringInputContent(
                            title = "ip-version",
                            value = ipVersion.takeIf(String::isNotBlank),
                            placeholder = "ip-version",
                            unsetLabel = "",
                            onValueChange = { ipVersion = it.orEmpty() },
                        )
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
                            title = "dialer-proxy",
                            value = dialerProxy.takeIf(String::isNotBlank),
                            placeholder = "dialer-proxy",
                            unsetLabel = "",
                            onValueChange = { dialerProxy = it.orEmpty() },
                        )
                    }
                    OverrideCardSection(MLang.Override.Structured.Proxies.Title) {
                        NullableBooleanSelector(
                            title = "udp",
                            value = udp,
                            onValueChange = { udp = it },
                        )
                        NullableBooleanSelector(
                            title = "tfo",
                            value = tfo,
                            onValueChange = { tfo = it },
                        )
                        NullableBooleanSelector(
                            title = "mptcp",
                            value = mptcp,
                            onValueChange = { mptcp = it },
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
                        items = OverrideProxyTypePresets,
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
    }
}

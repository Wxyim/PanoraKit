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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.presentation.component.*
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import com.github.yumelira.yumebox.presentation.util.*
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.serialization.json.*
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ProviderKnownKeys =
    setOf(
        "type",
        "path",
        "url",
        "proxy",
        "behavior",
        "format",
        "vehicle",
        "interval",
        "size-limit",
        "header",
        "health-check",
        "override",
        "filter",
        "enable",
        "disable-udp",
    )

private val ProviderHealthCheckKnownKeys =
    setOf("enable", "url", "interval", "timeout", "lazy", "expected-status")

private object OverrideDraftMetrics {
    val HeaderFieldHeight = 120.dp
}

private val ProviderOverrideKnownKeys =
    setOf(
        "tfo",
        "mptcp",
        "udp",
        "udp-over-tcp",
        "down",
        "up",
        "skip-cert-verify",
        "dialer-proxy",
        "interface-name",
        "routing-mark",
        "ip-version",
        "additional-prefix",
        "additional-suffix",
    )

private val ProviderTypePresets = listOf("http", "file", "inline")
private val ProviderVehiclePresets = listOf("http", "file")
private val ProviderBehaviorPresets = listOf("domain", "ipcidr", "classical")
private val ProviderFormatPresets = listOf("yaml", "text", "mrs")

@Composable
fun OverrideKeyedObjectDraftEditorScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val listState = rememberLazyListState()
    val title = remember {
        OverrideStructuredEditorStore.keyedObjectDraftEditorTitle.ifBlank {
            MLang.Override.Draft.Object
        }
    }
    val editorType = remember { OverrideStructuredEditorStore.keyedObjectDraftEditorType }
    val initialValue = remember { OverrideStructuredEditorStore.keyedObjectDraftEditorValue }
    val saveFabController = rememberOverrideFabController()
    val initialFields = initialValue?.fields.orEmpty()

    var key by remember { mutableStateOf(initialValue?.key.orEmpty()) }
    var type by remember { mutableStateOf(initialFields.stringField("type")) }
    var path by remember { mutableStateOf(initialFields.stringField("path")) }
    var url by remember { mutableStateOf(initialFields.stringField("url")) }
    var proxy by remember { mutableStateOf(initialFields.stringField("proxy")) }
    var behavior by remember { mutableStateOf(initialFields.stringField("behavior")) }
    var format by remember { mutableStateOf(initialFields.stringField("format")) }
    var vehicle by remember { mutableStateOf(initialFields.stringField("vehicle")) }
    var intervalText by remember {
        mutableStateOf(initialFields.intField("interval")?.toString().orEmpty())
    }
    var sizeLimitText by remember {
        mutableStateOf(initialFields.intField("size-limit")?.toString().orEmpty())
    }
    var headerText by remember { mutableStateOf(initialFields.headerField("header")) }
    val initialHealthCheckFields = remember { initialFields.objectField("health-check").orEmpty() }
    val initialOverrideFields = remember { initialFields.objectField("override").orEmpty() }
    var healthCheckEnable by remember {
        mutableStateOf(initialHealthCheckFields.booleanField("enable"))
    }
    var healthCheckUrl by remember { mutableStateOf(initialHealthCheckFields.stringField("url")) }
    var healthCheckIntervalText by remember {
        mutableStateOf(initialHealthCheckFields.intField("interval")?.toString().orEmpty())
    }
    var healthCheckTimeoutText by remember {
        mutableStateOf(initialHealthCheckFields.intField("timeout")?.toString().orEmpty())
    }
    var healthCheckLazy by remember {
        mutableStateOf(initialHealthCheckFields.booleanField("lazy"))
    }
    var healthCheckExpectedStatus by remember {
        mutableStateOf(initialHealthCheckFields.stringField("expected-status"))
    }
    var healthCheckExtraFields by remember {
        mutableStateOf(initialHealthCheckFields.filterKeys { it !in ProviderHealthCheckKnownKeys })
    }
    var overrideTfo by remember { mutableStateOf(initialOverrideFields.booleanField("tfo")) }
    var overrideMptcp by remember { mutableStateOf(initialOverrideFields.booleanField("mptcp")) }
    var overrideUdp by remember { mutableStateOf(initialOverrideFields.booleanField("udp")) }
    var overrideUdpOverTcp by remember {
        mutableStateOf(initialOverrideFields.booleanField("udp-over-tcp"))
    }
    var overrideDown by remember { mutableStateOf(initialOverrideFields.stringField("down")) }
    var overrideUp by remember { mutableStateOf(initialOverrideFields.stringField("up")) }
    var overrideSkipCertVerify by remember {
        mutableStateOf(initialOverrideFields.booleanField("skip-cert-verify"))
    }
    var overrideDialerProxy by remember {
        mutableStateOf(initialOverrideFields.stringField("dialer-proxy"))
    }
    var overrideInterfaceName by remember {
        mutableStateOf(initialOverrideFields.stringField("interface-name"))
    }
    var overrideRoutingMarkText by remember {
        mutableStateOf(initialOverrideFields.intField("routing-mark")?.toString().orEmpty())
    }
    var overrideIpVersion by remember {
        mutableStateOf(initialOverrideFields.stringField("ip-version"))
    }
    var additionalPrefix by remember {
        mutableStateOf(initialOverrideFields.stringField("additional-prefix"))
    }
    var additionalSuffix by remember {
        mutableStateOf(initialOverrideFields.stringField("additional-suffix"))
    }
    var providerOverrideExtraFields by remember {
        mutableStateOf(initialOverrideFields.filterKeys { it !in ProviderOverrideKnownKeys })
    }
    var filter by remember { mutableStateOf(initialFields.stringField("filter")) }
    var enable by remember { mutableStateOf(initialFields.booleanField("enable")) }
    var disableUdp by remember { mutableStateOf(initialFields.booleanField("disable-udp")) }
    var extraFields by remember {
        mutableStateOf(initialFields.filterKeys { it !in ProviderKnownKeys })
    }
    var editingExtraKey by remember { mutableStateOf<String?>(null) }
    var editingHealthCheckExtraKey by remember { mutableStateOf<String?>(null) }
    var editingOverrideExtraKey by remember { mutableStateOf<String?>(null) }
    var showExtraFieldDialog by remember { mutableStateOf(false) }
    var showHealthCheckExtraFieldDialog by remember { mutableStateOf(false) }
    var showOverrideExtraFieldDialog by remember { mutableStateOf(false) }
    var showTypeSelector by remember { mutableStateOf(false) }
    var showVehicleSelector by remember { mutableStateOf(false) }
    var showBehaviorSelector by remember { mutableStateOf(false) }
    var showFormatSelector by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val keyLabel = MLang.Override.Draft.Name

    DisposableEffect(Unit) {
        onDispose { OverrideStructuredEditorStore.clearKeyedObjectDraftEditor() }
    }

    Scaffold(
        floatingActionButton = {
            OverrideAnimatedFab(
                controller = saveFabController,
                visible = true,
                imageVector = Yume.Check,
                contentDescription = MLang.Override.Draft.Save + editorType.itemLabel,
                label = MLang.Override.Draft.Save,
                onClick = {
                    if (key.trim().isBlank()) {
                        errorText = MLang.Override.Draft.NameRequired
                        return@OverrideAnimatedFab
                    }
                    OverrideStructuredEditorStore.submitKeyedObjectDraft(
                        OverrideKeyedObjectDraft(
                            key = key.trim(),
                            fields =
                                linkedMapOf<String, JsonElement>().apply {
                                    putAll(extraFields)
                                    putStringField("type", type)
                                    putStringField("path", path)
                                    putStringField("url", url)
                                    putStringField("proxy", proxy)
                                    putStringField("behavior", behavior)
                                    putStringField("format", format)
                                    putStringField("vehicle", vehicle)
                                    putIntField("interval", intervalText.trim().toIntOrNull())
                                    putIntField("size-limit", sizeLimitText.trim().toIntOrNull())
                                    putObjectField("header", parseHeaderEditorText(headerText))
                                    putStringField("filter", filter)
                                    putBooleanField("enable", enable)
                                    putBooleanField("disable-udp", disableUdp)
                                    putObjectField(
                                        "health-check",
                                        linkedMapOf<String, JsonElement>().apply {
                                            putAll(healthCheckExtraFields)
                                            putBooleanField("enable", healthCheckEnable)
                                            putStringField("url", healthCheckUrl)
                                            putIntField(
                                                "interval",
                                                healthCheckIntervalText.trim().toIntOrNull(),
                                            )
                                            putIntField(
                                                "timeout",
                                                healthCheckTimeoutText.trim().toIntOrNull(),
                                            )
                                            putBooleanField("lazy", healthCheckLazy)
                                            putStringField(
                                                "expected-status",
                                                healthCheckExpectedStatus,
                                            )
                                        },
                                    )
                                    putObjectField(
                                        "override",
                                        linkedMapOf<String, JsonElement>().apply {
                                            putAll(providerOverrideExtraFields)
                                            putBooleanField("tfo", overrideTfo)
                                            putBooleanField("mptcp", overrideMptcp)
                                            putBooleanField("udp", overrideUdp)
                                            putBooleanField("udp-over-tcp", overrideUdpOverTcp)
                                            putStringField("down", overrideDown)
                                            putStringField("up", overrideUp)
                                            putBooleanField(
                                                "skip-cert-verify",
                                                overrideSkipCertVerify,
                                            )
                                            putStringField("dialer-proxy", overrideDialerProxy)
                                            putStringField("interface-name", overrideInterfaceName)
                                            putIntField(
                                                "routing-mark",
                                                overrideRoutingMarkText.trim().toIntOrNull(),
                                            )
                                            putStringField("ip-version", overrideIpVersion)
                                            putStringField("additional-prefix", additionalPrefix)
                                            putStringField("additional-suffix", additionalSuffix)
                                        },
                                    )
                                },
                            uiId = initialValue?.uiId ?: OverrideKeyedObjectDraft().uiId,
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
                OverrideCardSection(MLang.Override.Draft.BasicIdentity) {
                    ConfigSettingRow(
                        title = MLang.Override.Draft.Type,
                        valueLabel = type.ifBlank { MLang.Component.Selector.UseDefault },
                        tone = SemanticTone.Info,
                        badgeTone = SemanticTone.Info,
                        onClick = {
                            showTypeSelector = true
                            errorText = null
                        },
                    )
                    ConfigSettingRow(
                        title = MLang.Override.Draft.Vehicle,
                        valueLabel = vehicle.ifBlank { MLang.Component.Selector.UseDefault },
                        tone = SemanticTone.Info,
                        badgeTone = SemanticTone.Info,
                        onClick = {
                            showVehicleSelector = true
                            errorText = null
                        },
                    )
                    StringInputContent(
                        title = keyLabel,
                        value = key.takeIf(String::isNotBlank),
                        placeholder = keyLabel,
                        unsetLabel = "",
                        onValueChange = {
                            key = it.orEmpty()
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
                }
            }
            item {
                OverrideCardSection(MLang.Override.Draft.CoreSource) {
                    StringInputContent(
                        title = "path",
                        value = path.takeIf(String::isNotBlank),
                        placeholder = "path",
                        unsetLabel = "",
                        onValueChange = { path = it.orEmpty() },
                    )
                    StringInputContent(
                        title = "url",
                        value = url.takeIf(String::isNotBlank),
                        placeholder = "url",
                        unsetLabel = "",
                        onValueChange = { url = it.orEmpty() },
                    )
                    StringInputContent(
                        title = "proxy",
                        value = proxy.takeIf(String::isNotBlank),
                        placeholder = "proxy",
                        unsetLabel = "",
                        onValueChange = { proxy = it.orEmpty() },
                    )
                    StringInputContent(
                        title = "interval",
                        value = intervalText.takeIf(String::isNotBlank),
                        placeholder = "interval",
                        unsetLabel = "",
                        onValueChange = { intervalText = it?.filter(Char::isDigit).orEmpty() },
                    )
                    StringInputContent(
                        title = "size-limit",
                        value = sizeLimitText.takeIf(String::isNotBlank),
                        placeholder = "size-limit",
                        unsetLabel = "",
                        onValueChange = { sizeLimitText = it?.filter(Char::isDigit).orEmpty() },
                    )
                }
            }
            item {
                OverrideCardSection(MLang.Override.Draft.NetworkAuth) {
                    ConfigSettingRow(
                        title = MLang.Override.Draft.Behavior,
                        valueLabel = behavior.ifBlank { MLang.Component.Selector.UseDefault },
                        tone = SemanticTone.Info,
                        badgeTone = SemanticTone.Info,
                        onClick = { showBehaviorSelector = true },
                    )
                    ConfigSettingRow(
                        title = MLang.Override.Draft.Format,
                        valueLabel = format.ifBlank { MLang.Component.Selector.UseDefault },
                        tone = SemanticTone.Info,
                        badgeTone = SemanticTone.Info,
                        onClick = { showFormatSelector = true },
                    )
                    StringInputContent(
                        title = "filter",
                        value = filter.takeIf(String::isNotBlank),
                        placeholder = "filter",
                        unsetLabel = "",
                        onValueChange = { filter = it.orEmpty() },
                    )
                    StringInputContent(
                        title = "header",
                        value = headerText.takeIf(String::isNotBlank),
                        placeholder = "header",
                        unsetLabel = "",
                        onValueChange = { headerText = it.orEmpty() },
                    )
                    OverrideFieldAssistText(
                        text = MLang.Override.Draft.HeaderHint,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
            if (editorType == OverrideStructuredMapType.ProxyProviders) {
                item {
                    OverrideCardSection(MLang.Override.Draft.HealthCheck) {
                        StringInputContent(
                            title = "health-check.url",
                            value = healthCheckUrl.takeIf(String::isNotBlank),
                            placeholder = "health-check.url",
                            unsetLabel = "",
                            onValueChange = { healthCheckUrl = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "health-check.interval",
                            value = healthCheckIntervalText.takeIf(String::isNotBlank),
                            placeholder = "health-check.interval",
                            unsetLabel = "",
                            onValueChange = {
                                healthCheckIntervalText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                        StringInputContent(
                            title = "health-check.timeout",
                            value = healthCheckTimeoutText.takeIf(String::isNotBlank),
                            placeholder = "health-check.timeout",
                            unsetLabel = "",
                            onValueChange = {
                                healthCheckTimeoutText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                        StringInputContent(
                            title = "health-check.expected-status",
                            value = healthCheckExpectedStatus.takeIf(String::isNotBlank),
                            placeholder = "health-check.expected-status",
                            unsetLabel = "",
                            onValueChange = { healthCheckExpectedStatus = it.orEmpty() },
                        )
                    }
                }
                item {
                    OverrideCardSection(MLang.Override.Draft.HealthCheckSwitch) {
                        NullableBooleanSelector(
                            title = "health-check.enable",
                            value = healthCheckEnable,
                            onValueChange = { healthCheckEnable = it },
                        )
                        NullableBooleanSelector(
                            title = "health-check.lazy",
                            value = healthCheckLazy,
                            onValueChange = { healthCheckLazy = it },
                        )
                    }
                }
                item {
                    OverrideSection(MLang.Override.Draft.HealthCheckFields) {
                        OverrideExtraFieldsCard(
                            title = MLang.Override.Draft.HealthCheckFields,
                            fields = healthCheckExtraFields,
                            onAddClick = {
                                editingHealthCheckExtraKey = null
                                showHealthCheckExtraFieldDialog = true
                            },
                            onEditClick = { entryKey, _ ->
                                editingHealthCheckExtraKey = entryKey
                                showHealthCheckExtraFieldDialog = true
                            },
                            onDeleteClick = { entryKey ->
                                healthCheckExtraFields = healthCheckExtraFields - entryKey
                            },
                        )
                    }
                }
                item {
                    OverrideCardSection(MLang.Override.Draft.Override) {
                        StringInputContent(
                            title = "override.down",
                            value = overrideDown.takeIf(String::isNotBlank),
                            placeholder = "override.down",
                            unsetLabel = "",
                            onValueChange = { overrideDown = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "override.up",
                            value = overrideUp.takeIf(String::isNotBlank),
                            placeholder = "override.up",
                            unsetLabel = "",
                            onValueChange = { overrideUp = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "override.dialer-proxy",
                            value = overrideDialerProxy.takeIf(String::isNotBlank),
                            placeholder = "override.dialer-proxy",
                            unsetLabel = "",
                            onValueChange = { overrideDialerProxy = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "override.interface-name",
                            value = overrideInterfaceName.takeIf(String::isNotBlank),
                            placeholder = "override.interface-name",
                            unsetLabel = "",
                            onValueChange = { overrideInterfaceName = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "override.routing-mark",
                            value = overrideRoutingMarkText.takeIf(String::isNotBlank),
                            placeholder = "override.routing-mark",
                            unsetLabel = "",
                            onValueChange = {
                                overrideRoutingMarkText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                        StringInputContent(
                            title = "override.ip-version",
                            value = overrideIpVersion.takeIf(String::isNotBlank),
                            placeholder = "override.ip-version",
                            unsetLabel = "",
                            onValueChange = { overrideIpVersion = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "override.additional-prefix",
                            value = additionalPrefix.takeIf(String::isNotBlank),
                            placeholder = "override.additional-prefix",
                            unsetLabel = "",
                            onValueChange = { additionalPrefix = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "override.additional-suffix",
                            value = additionalSuffix.takeIf(String::isNotBlank),
                            placeholder = "override.additional-suffix",
                            unsetLabel = "",
                            onValueChange = { additionalSuffix = it.orEmpty() },
                        )
                    }
                }
                item {
                    OverrideCardSection(MLang.Override.Draft.OverrideSwitch) {
                        NullableBooleanSelector(
                            title = "override.tfo",
                            value = overrideTfo,
                            onValueChange = { overrideTfo = it },
                        )
                        NullableBooleanSelector(
                            title = "override.mptcp",
                            value = overrideMptcp,
                            onValueChange = { overrideMptcp = it },
                        )
                        NullableBooleanSelector(
                            title = "override.udp",
                            value = overrideUdp,
                            onValueChange = { overrideUdp = it },
                        )
                        NullableBooleanSelector(
                            title = "override.udp-over-tcp",
                            value = overrideUdpOverTcp,
                            onValueChange = { overrideUdpOverTcp = it },
                        )
                        NullableBooleanSelector(
                            title = "override.skip-cert-verify",
                            value = overrideSkipCertVerify,
                            onValueChange = { overrideSkipCertVerify = it },
                        )
                    }
                }
                item {
                    OverrideSection(MLang.Override.Draft.OverrideFields) {
                        OverrideExtraFieldsCard(
                            title = MLang.Override.Draft.OverrideFields,
                            fields = providerOverrideExtraFields,
                            onAddClick = {
                                editingOverrideExtraKey = null
                                showOverrideExtraFieldDialog = true
                            },
                            onEditClick = { entryKey, _ ->
                                editingOverrideExtraKey = entryKey
                                showOverrideExtraFieldDialog = true
                            },
                            onDeleteClick = { entryKey ->
                                providerOverrideExtraFields = providerOverrideExtraFields - entryKey
                            },
                        )
                    }
                }
            } else {
                item {
                    OverrideCardSection(MLang.Override.Draft.HealthCheck) {
                        StringInputContent(
                            title = "health-check.url",
                            value = healthCheckUrl.takeIf(String::isNotBlank),
                            placeholder = "health-check.url",
                            unsetLabel = "",
                            onValueChange = { healthCheckUrl = it.orEmpty() },
                        )
                        StringInputContent(
                            title = "health-check.interval",
                            value = healthCheckIntervalText.takeIf(String::isNotBlank),
                            placeholder = "health-check.interval",
                            unsetLabel = "",
                            onValueChange = {
                                healthCheckIntervalText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                        StringInputContent(
                            title = "health-check.timeout",
                            value = healthCheckTimeoutText.takeIf(String::isNotBlank),
                            placeholder = "health-check.timeout",
                            unsetLabel = "",
                            onValueChange = {
                                healthCheckTimeoutText = it?.filter(Char::isDigit).orEmpty()
                            },
                        )
                        StringInputContent(
                            title = "health-check.expected-status",
                            value = healthCheckExpectedStatus.takeIf(String::isNotBlank),
                            placeholder = "health-check.expected-status",
                            unsetLabel = "",
                            onValueChange = { healthCheckExpectedStatus = it.orEmpty() },
                        )
                    }
                }
                item {
                    OverrideCardSection(MLang.Override.Draft.HealthCheckSwitch) {
                        NullableBooleanSelector(
                            title = "health-check.enable",
                            value = healthCheckEnable,
                            onValueChange = { healthCheckEnable = it },
                        )
                        NullableBooleanSelector(
                            title = "health-check.lazy",
                            value = healthCheckLazy,
                            onValueChange = { healthCheckLazy = it },
                        )
                    }
                }
            }
            item {
                OverrideCardSection(MLang.Override.Draft.BooleanOptions) {
                    NullableBooleanSelector(
                        title = "enable",
                        value = enable,
                        onValueChange = { enable = it },
                    )
                    NullableBooleanSelector(
                        title = "disable-udp",
                        value = disableUdp,
                        onValueChange = { disableUdp = it },
                    )
                }
            }
            item {
                OverrideSection(MLang.Override.Draft.ExtraFields) {
                    OverrideExtraFieldsCard(
                        title = MLang.Override.Draft.ExtraFields,
                        fields = extraFields,
                        onAddClick = {
                            editingExtraKey = null
                            showExtraFieldDialog = true
                        },
                        onEditClick = { entryKey, _ ->
                            editingExtraKey = entryKey
                            showExtraFieldDialog = true
                        },
                        onDeleteClick = { entryKey -> extraFields = extraFields - entryKey },
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(OverrideSectionBottomSpacing)) }
        }
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
        OverrideSingleValueSelectionSheet(
            show = showTypeSelector,
            title = MLang.Override.Draft.Type,
            value = type,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Draft.Type,
                        items = ProviderTypePresets,
                    )
                ),
            customInputLabel = MLang.Override.Draft.Type,
            allowCustomValue = true,
            onDismiss = { showTypeSelector = false },
            onConfirm = { selectedValue ->
                type = selectedValue.trim()
                showTypeSelector = false
            },
        )
        OverrideSingleValueSelectionSheet(
            show = showVehicleSelector,
            title = MLang.Override.Draft.Vehicle,
            value = vehicle,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Draft.Vehicle,
                        items = ProviderVehiclePresets,
                    )
                ),
            customInputLabel = MLang.Override.Draft.Vehicle,
            allowCustomValue = true,
            onDismiss = { showVehicleSelector = false },
            onConfirm = { selectedValue ->
                vehicle = selectedValue.trim()
                showVehicleSelector = false
            },
        )
        OverrideSingleValueSelectionSheet(
            show = showBehaviorSelector,
            title = MLang.Override.Draft.Behavior,
            value = behavior,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Draft.Behavior,
                        items = ProviderBehaviorPresets,
                    )
                ),
            customInputLabel = MLang.Override.Draft.Behavior,
            allowCustomValue = true,
            onDismiss = { showBehaviorSelector = false },
            onConfirm = { selectedValue ->
                behavior = selectedValue.trim()
                showBehaviorSelector = false
            },
        )
        OverrideSingleValueSelectionSheet(
            show = showFormatSelector,
            title = MLang.Override.Draft.Format,
            value = format,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Draft.Format,
                        items = ProviderFormatPresets,
                    )
                ),
            customInputLabel = MLang.Override.Draft.Format,
            allowCustomValue = true,
            onDismiss = { showFormatSelector = false },
            onConfirm = { selectedValue ->
                format = selectedValue.trim()
                showFormatSelector = false
            },
        )
        OverrideExtraFieldDialog(
            show = showHealthCheckExtraFieldDialog,
            title =
                if (editingHealthCheckExtraKey == null) {
                    MLang.Override.Draft.AddHealthCheckField
                } else {
                    MLang.Override.Draft.EditHealthCheckField
                },
            initialValue =
                editingHealthCheckExtraKey?.let(healthCheckExtraFields::toExtraFieldDraft),
            onConfirm = { draft: OverrideExtraFieldDraft ->
                healthCheckExtraFields =
                    healthCheckExtraFields.updateExtraField(editingHealthCheckExtraKey, draft)
                editingHealthCheckExtraKey = null
                showHealthCheckExtraFieldDialog = false
            },
            onDismiss = {
                editingHealthCheckExtraKey = null
                showHealthCheckExtraFieldDialog = false
            },
        )
        OverrideExtraFieldDialog(
            show = showOverrideExtraFieldDialog,
            title =
                if (editingOverrideExtraKey == null) {
                    MLang.Override.Draft.AddOverrideField
                } else {
                    MLang.Override.Draft.EditOverrideField
                },
            initialValue =
                editingOverrideExtraKey?.let(providerOverrideExtraFields::toExtraFieldDraft),
            onConfirm = { draft: OverrideExtraFieldDraft ->
                providerOverrideExtraFields =
                    providerOverrideExtraFields.updateExtraField(editingOverrideExtraKey, draft)
                editingOverrideExtraKey = null
                showOverrideExtraFieldDialog = false
            },
            onDismiss = {
                editingOverrideExtraKey = null
                showOverrideExtraFieldDialog = false
            },
        )
    }
}

private fun Map<String, JsonElement>.headerField(key: String): String {
    return objectField(key)
        ?.entries
        ?.joinToString("\n") { (headerKey, headerValue) ->
            val values =
                when (headerValue) {
                    is JsonArray ->
                        headerValue.jsonArray.mapNotNull { item ->
                            (item as? JsonPrimitive)?.content?.trim()?.takeIf(String::isNotBlank)
                        }

                    is JsonPrimitive -> listOf(headerValue.content).filter(String::isNotBlank)
                    else -> emptyList()
                }
            if (values.isEmpty()) {
                headerKey
            } else {
                "$headerKey: ${values.joinToString(" | ")}"
            }
        }
        .orEmpty()
}

private fun parseHeaderEditorText(rawValue: String): Map<String, JsonElement>? {
    val headerEntries = linkedMapOf<String, JsonElement>()
    rawValue.lines().map(String::trim).filter(String::isNotBlank).forEach { line ->
        val separatorIndex = line.indexOf(':')
        if (separatorIndex < 0) {
            return@forEach
        }
        val headerKey = line.substring(0, separatorIndex).trim()
        if (headerKey.isBlank()) {
            return@forEach
        }
        val headerValues =
            line
                .substring(separatorIndex + 1)
                .split('|')
                .map(String::trim)
                .filter(String::isNotBlank)
        headerEntries[headerKey] = JsonArray(headerValues.map(::JsonPrimitive))
    }

    return headerEntries.ifEmpty { null }
}

private fun Map<String, JsonElement>.stringField(key: String): String {
    val element = get(key) ?: return ""
    return if (element is JsonPrimitive && element.isString) {
        element.content
    } else {
        element.toString()
    }
}

private fun Map<String, JsonElement>.intField(key: String): Int? {
    return get(key)?.jsonPrimitive?.intOrNull
}

private fun Map<String, JsonElement>.booleanField(key: String): Boolean? {
    return get(key)?.jsonPrimitive?.booleanOrNull
}

private fun MutableMap<String, JsonElement>.putStringField(key: String, value: String) {
    value.trim().takeIf(String::isNotBlank)?.let { put(key, JsonPrimitive(it)) }
}

private fun MutableMap<String, JsonElement>.putIntField(key: String, value: Int?) {
    value?.let { put(key, JsonPrimitive(it)) }
}

private fun MutableMap<String, JsonElement>.putBooleanField(key: String, value: Boolean?) {
    value?.let { put(key, JsonPrimitive(it)) }
}

private fun Map<String, JsonElement>.objectField(key: String): Map<String, JsonElement>? {
    return get(key)?.jsonObject?.let(::toOrderedJsonElementMap)
}

private fun MutableMap<String, JsonElement>.putObjectField(
    key: String,
    value: Map<String, JsonElement>?,
) {
    value?.takeIf(Map<String, JsonElement>::isNotEmpty)?.let { put(key, JsonObject(it)) }
}

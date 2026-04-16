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

package com.github.nomadboxlab.monadbox.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.nomadboxlab.monadbox.presentation.component.*
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import com.github.nomadboxlab.monadbox.presentation.util.OverrideRuleDraft
import com.github.nomadboxlab.monadbox.presentation.util.OverrideRuleTypePresets
import com.github.nomadboxlab.monadbox.presentation.util.OverrideStructuredEditorStore
import com.github.nomadboxlab.monadbox.presentation.util.supportsRuleExtra
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun OverrideRuleDraftEditorScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = MiuixScrollBehavior()
    val listState = rememberLazyListState()
    val title = remember {
        OverrideStructuredEditorStore.ruleDraftEditorTitle.ifBlank {
            MLang.Override.Editor.RuleEdit
        }
    }
    val initialValue = remember { OverrideStructuredEditorStore.ruleDraftEditorValue }
    val saveFabController = rememberOverrideFabController()

    var ruleType by remember {
        mutableStateOf(initialValue?.type?.ifBlank { "DOMAIN-SUFFIX" } ?: "DOMAIN-SUFFIX")
    }
    var payload by remember { mutableStateOf(initialValue?.payload.orEmpty()) }
    var target by remember { mutableStateOf(initialValue?.target.orEmpty()) }
    var useSrc by remember {
        mutableStateOf(initialValue?.extras.orEmpty().any { it.equals("src", ignoreCase = true) })
    }
    var useNoResolve by remember {
        mutableStateOf(
            initialValue?.extras.orEmpty().any { it.equals("no-resolve", ignoreCase = true) }
        )
    }
    var extraText by remember {
        mutableStateOf(
            initialValue
                ?.extras
                .orEmpty()
                .filterNot {
                    it.equals("src", ignoreCase = true) ||
                        it.equals("no-resolve", ignoreCase = true)
                }
                .joinToString(",")
        )
    }
    var errorText by remember { mutableStateOf<String?>(null) }
    var showTypeSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }
    var showRuleProviderSelector by remember { mutableStateOf(false) }
    val canUseExtraSwitches = supportsRuleExtra(ruleType)
    val referenceCatalog = OverrideStructuredEditorStore.currentReferenceCatalog()
    val isSubRuleTarget = ruleType.equals("SUB-RULE", ignoreCase = true)
    val isRuleSetType = ruleType.equals("RULE-SET", ignoreCase = true)
    val targetCandidates =
        if (isSubRuleTarget) {
            referenceCatalog.subRuleNames
        } else {
            referenceCatalog.proxyGroupNames
        }
    val ruleProviderCandidates = referenceCatalog.ruleProviderNames
    var selectedRuleProvider by
        remember(initialValue?.payload, ruleProviderCandidates) {
            mutableStateOf(
                initialValue
                    ?.payload
                    ?.takeIf { candidate ->
                        candidate.isNotBlank() && candidate in ruleProviderCandidates
                    }
                    .orEmpty()
            )
        }
    val normalizedPayloadInput = payload.trim()
    val selectedRuleProviderValue =
        normalizedPayloadInput.takeIf { candidate ->
            candidate.isNotBlank() && candidate in ruleProviderCandidates
        } ?: selectedRuleProvider.trim()
    val targetLabel =
        if (isSubRuleTarget) MLang.Override.Editor.SubRuleTarget
        else MLang.Override.Editor.ProxyGroupTarget
    val typeErrorText = errorText?.takeIf { it.contains(MLang.Override.Editor.RuleType) }
    val payloadErrorText = errorText?.takeIf { it.contains(MLang.Override.Editor.Payload) }
    val targetErrorText =
        errorText?.takeIf {
            it.contains(MLang.Override.Draft.Name) || it.contains(MLang.Override.Editor.MatchResult)
        }

    DisposableEffect(Unit) { onDispose { OverrideStructuredEditorStore.clearRuleDraftEditor() } }

    Scaffold(
        floatingActionButton = {
            OverrideAnimatedFab(
                controller = saveFabController,
                visible = true,
                imageVector = MonadIcons.Check,
                contentDescription = MLang.Override.Editor.SaveRule,
                label = MLang.Override.Draft.Save,
                onClick = {
                    val normalizedType = ruleType.trim().uppercase()
                    val normalizedPayload = payload.trim()
                    val resolvedPayload =
                        if (normalizedPayload.isNotBlank()) {
                            normalizedPayload
                        } else if (normalizedType == "RULE-SET") {
                            selectedRuleProvider.trim()
                        } else {
                            normalizedPayload
                        }
                    val normalizedTarget = target.trim()
                    val extraValues =
                        extraText
                            .split(',')
                            .map(String::trim)
                            .filter(String::isNotBlank)
                            .toMutableList()

                    if (normalizedType.isBlank()) {
                        errorText = MLang.Override.Editor.RuleTypeEmpty
                        return@OverrideAnimatedFab
                    }
                    if (
                        !normalizedType.equals("MATCH", ignoreCase = true) &&
                            resolvedPayload.isBlank()
                    ) {
                        errorText = MLang.Override.Editor.PayloadEmpty
                        return@OverrideAnimatedFab
                    }
                    if (normalizedTarget.isBlank()) {
                        errorText = MLang.Override.Editor.TargetEmpty
                        return@OverrideAnimatedFab
                    }
                    if (canUseExtraSwitches) {
                        if (useSrc) {
                            extraValues += "src"
                        }
                        if (useNoResolve) {
                            extraValues += "no-resolve"
                        }
                    }
                    OverrideStructuredEditorStore.submitRuleDraft(
                        OverrideRuleDraft(
                            type = normalizedType,
                            payload = resolvedPayload,
                            target = normalizedTarget,
                            extras = extraValues.distinct(),
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
                    OverrideCardSection(MLang.Override.Editor.RuleBody) {
                        ConfigSettingRow(
                            title = MLang.Override.Editor.RuleType,
                            valueLabel = ruleType.ifBlank { MLang.Component.Selector.UseDefault },
                            tone = SemanticTone.Info,
                            badgeTone = SemanticTone.Info,
                            onClick = {
                                showTypeSelector = true
                                errorText = null
                            },
                        )
                        if (isRuleSetType) {
                            ConfigSettingRow(
                                title = MLang.Override.Form.RuleProviders,
                                valueLabel =
                                    selectedRuleProviderValue.ifBlank {
                                        MLang.Component.Selector.UseDefault
                                    },
                                tone = SemanticTone.Info,
                                badgeTone = SemanticTone.Info,
                                onClick = {
                                    showRuleProviderSelector = true
                                    errorText = null
                                },
                            )
                        }
                        if (!ruleType.equals("MATCH", ignoreCase = true)) {
                            StringInputContent(
                                title = MLang.Override.Editor.Payload,
                                value = payload.takeIf(String::isNotBlank),
                                placeholder = MLang.Override.Editor.Payload,
                                unsetLabel = "",
                                onValueChange = {
                                    payload = it.orEmpty()
                                    errorText = null
                                },
                            )
                            OverrideFieldAssistText(
                                text =
                                    if (isRuleSetType) {
                                        MLang.Override.Editor.RuleProviderInputHint
                                    } else {
                                        MLang.Override.Editor.LogicalRuleHint
                                    },
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            payloadErrorText?.let { message ->
                                OverrideFieldAssistText(
                                    text = message,
                                    color = MiuixTheme.colorScheme.error,
                                )
                            }
                        }
                        ConfigSettingRow(
                            title =
                                if (ruleType.equals("MATCH", ignoreCase = true))
                                    MLang.Override.Editor.MatchResult
                                else targetLabel,
                            valueLabel = target.ifBlank { MLang.Component.Selector.UseDefault },
                            tone = SemanticTone.Info,
                            badgeTone = SemanticTone.Info,
                            onClick = {
                                showTargetSelector = true
                                errorText = null
                            },
                        )
                        targetErrorText?.let { message ->
                            OverrideFieldAssistText(
                                text = message,
                                color = MiuixTheme.colorScheme.error,
                            )
                        }
                    }
                    if (canUseExtraSwitches) {
                        OverrideCardSection(MLang.Override.Structured.Proxies.Title) {
                            RuleExtraSwitchRow(
                                title = "src",
                                checked = useSrc,
                                onCheckedChange = { useSrc = it },
                            )
                            RuleExtraSwitchRow(
                                title = "no-resolve",
                                checked = useNoResolve,
                                onCheckedChange = { useNoResolve = it },
                            )
                        }
                    }
                    OverrideCardSection(MLang.Override.Editor.AdditionalParams) {
                        StringInputContent(
                            title = MLang.Override.Editor.OtherExtraParams,
                            value = extraText.takeIf(String::isNotBlank),
                            placeholder = MLang.Override.Editor.OtherExtraParams,
                            unsetLabel = "",
                            onValueChange = {
                                extraText = it.orEmpty()
                                errorText = null
                            },
                        )
                        OverrideFieldAssistText(
                            text = MLang.Override.Editor.ExtraParamsHint,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                    Spacer(modifier = Modifier.height(OverrideSectionBottomSpacing))
                }
            }
        }
        OverrideSingleValueSelectionSheet(
            show = showTypeSelector,
            title = MLang.Override.Editor.RuleType,
            value = ruleType,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Editor.RuleType,
                        items = OverrideRuleTypePresets,
                    )
                ),
            customInputLabel = "",
            allowCustomValue = false,
            onDismiss = { showTypeSelector = false },
            onConfirm = { selectedValue ->
                ruleType = selectedValue.trim().ifBlank { ruleType }
                errorText = null
                showTypeSelector = false
            },
        )
        OverrideSingleValueSelectionSheet(
            show = showTargetSelector,
            title =
                if (ruleType.equals("MATCH", ignoreCase = true))
                    MLang.Override.Editor.SelectMatchResult
                else MLang.Override.Editor.SelectSubRuleTarget,
            value = target,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title =
                            if (isSubRuleTarget) MLang.Override.Structured.SubRules.Title
                            else MLang.Override.Editor.ProxyGroup,
                        items = targetCandidates,
                    )
                ),
            customInputLabel =
                if (ruleType.equals("MATCH", ignoreCase = true))
                    MLang.Override.Editor.CustomMatchResult
                else MLang.Override.Editor.CustomSubRuleTarget,
            onDismiss = { showTargetSelector = false },
            onConfirm = { selectedValue ->
                target = selectedValue
                errorText = null
                showTargetSelector = false
            },
        )
        OverrideSingleValueSelectionSheet(
            show = showRuleProviderSelector,
            title = MLang.Override.Editor.SelectRuleProvider,
            value = selectedRuleProviderValue,
            groups =
                listOf(
                    OverrideSelectionGroup(
                        title = MLang.Override.Form.RuleProviders,
                        items = ruleProviderCandidates,
                    )
                ),
            customInputLabel = "",
            allowCustomValue = false,
            onDismiss = { showRuleProviderSelector = false },
            onConfirm = { selectedValue ->
                selectedRuleProvider = selectedValue.trim()
                errorText = null
                showRuleProviderSelector = false
            },
        )
    }
}

@Composable
private fun RuleExtraSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SuperSwitch(title = title, checked = checked, onCheckedChange = onCheckedChange)
}

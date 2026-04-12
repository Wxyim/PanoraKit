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

package com.github.yumelira.yumebox.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Bolt
import com.github.yumelira.yumebox.presentation.icon.yume.Cloud
import com.github.yumelira.yumebox.presentation.icon.yume.Folders
import com.github.yumelira.yumebox.presentation.icon.yume.`Git-merge`
import com.github.yumelira.yumebox.presentation.icon.yume.LayoutPanelLeft
import com.github.yumelira.yumebox.presentation.icon.yume.Link
import com.github.yumelira.yumebox.presentation.icon.yume.Rocket
import com.github.yumelira.yumebox.presentation.icon.yume.`Scan-eye`
import com.github.yumelira.yumebox.presentation.icon.yume.`Scroll-text`
import com.github.yumelira.yumebox.presentation.icon.yume.Tun
import com.github.yumelira.yumebox.presentation.icon.yume.`Wifi-cog`
import com.github.yumelira.yumebox.presentation.util.*
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.theme.MiuixTheme

private enum class OverrideSectionHierarchyEmphasis {
    Flat,
    Nested,
}

private data class OverrideSectionVisualSpec(
    val icon: ImageVector,
    val tone: SemanticTone,
)

private fun OverrideEditorSection.visualSpec(): OverrideSectionVisualSpec {
    return when (this) {
        OverrideEditorSection.General -> OverrideSectionVisualSpec(Yume.Bolt, SemanticTone.Brand)
        OverrideEditorSection.Dns -> OverrideSectionVisualSpec(Yume.Cloud, SemanticTone.Info)
        OverrideEditorSection.Sniffer -> OverrideSectionVisualSpec(Yume.`Scan-eye`, SemanticTone.Info)
        OverrideEditorSection.Inbound -> OverrideSectionVisualSpec(Yume.`Wifi-cog`, SemanticTone.Info)
        OverrideEditorSection.Tun -> OverrideSectionVisualSpec(Yume.Tun, SemanticTone.Success)
        OverrideEditorSection.Rules -> OverrideSectionVisualSpec(Yume.`Scroll-text`, SemanticTone.Warning)
        OverrideEditorSection.Proxies -> OverrideSectionVisualSpec(Yume.Rocket, SemanticTone.Info)
        OverrideEditorSection.ProxyProviders -> OverrideSectionVisualSpec(Yume.Link, SemanticTone.Info)
        OverrideEditorSection.ProxyGroups -> OverrideSectionVisualSpec(Yume.LayoutPanelLeft, SemanticTone.Brand)
        OverrideEditorSection.RuleProviders -> OverrideSectionVisualSpec(Yume.`Git-merge`, SemanticTone.Warning)
        OverrideEditorSection.SubRules -> OverrideSectionVisualSpec(Yume.Folders, SemanticTone.Warning)
    }
}

fun LazyListScope.OverrideEditContent(
    name: String,
    description: String,
    config: ConfigurationOverride,
    currentConfigProvider: () -> ConfigurationOverride,
    expandedSections: Set<OverrideEditorSection>,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    remoteSourceUrl: String?,
    remoteUpdateIntervalSeconds: Long?,
    isRemoteResource: Boolean,
    onRemoteSourceUrlChange: (String?) -> Unit,
    onRemoteIntervalSecondsChange: (String?) -> Unit,
    onConfigChange: (ConfigurationOverride) -> Unit,
    onSectionToggle: (OverrideEditorSection) -> Unit,
    onOpenPresetTemplate: () -> Unit,
    onEditStringList: OpenStringListModifiersEditor,
    onEditRuleList: OpenRuleListEditor,
    onEditStringMap: OpenStringMapEditor,
    onEditJson: OpenJsonEditor,
    onEditObjectList: OpenStructuredObjectListEditor,
    onEditObjectMap: OpenObjectMapEditor,
    onEditSubRules: OpenSubRulesEditor,
) {
    item(key = "override-basic-info-section") {
        OverrideCardSection(title = MLang.Override.Draft.BasicInfo) {
            StringInputContent(
                title = MLang.Override.Draft.ConfigName,
                value = name,
                placeholder = MLang.Override.Draft.ConfigName,
                onValueChange = { onNameChange(it.orEmpty()) },
            )
            StringInputContent(
                title = MLang.Override.Draft.ConfigDescription,
                value = description,
                placeholder = MLang.Override.Draft.ConfigDescription,
                onValueChange = { onDescriptionChange(it.orEmpty()) },
            )
            if (isRemoteResource) {
                StringInputContent(
                    title = MLang.Override.Draft.RemoteSourceUrl,
                    value = remoteSourceUrl,
                    placeholder = "https://example.com/rules.sgmodule",
                    onValueChange = onRemoteSourceUrlChange,
                )
                StringInputContent(
                    title = MLang.Override.Draft.RemoteUpdateInterval,
                    value = remoteUpdateIntervalSeconds?.toString(),
                    placeholder = MLang.Override.Draft.RemoteUpdateIntervalPlaceholder,
                    onValueChange = onRemoteIntervalSecondsChange,
                )
            }
        }
    }

    if (!isRemoteResource) {
        item(key = "override-preset-template-section") {
            OverrideCardSection(title = MLang.Override.Draft.PresetTemplate) {
                BasicComponent(
                    title = MLang.Override.Draft.OfficialMrs,
                    summary = MLang.Override.Draft.OfficialMrsSummary,
                    onClick = onOpenPresetTemplate,
                )
            }
        }
        OverrideSectionListContent(
            config = config,
            currentConfigProvider = currentConfigProvider,
            expandedSections = expandedSections,
            hierarchyEmphasis = OverrideSectionHierarchyEmphasis.Nested,
            onConfigChange = onConfigChange,
            onSectionToggle = onSectionToggle,
            onEditStringList = onEditStringList,
            onEditRuleList = onEditRuleList,
            onEditStringMap = onEditStringMap,
            onEditJson = onEditJson,
            onEditObjectList = onEditObjectList,
            onEditObjectMap = onEditObjectMap,
            onEditSubRules = onEditSubRules,
        )
    } else {
        item(key = "override-bottom-spacer") {
            Spacer(modifier = Modifier.height(OverrideSectionBottomSpacing))
        }
    }
}

fun LazyListScope.LocalProfileConfigEditContent(
    config: ConfigurationOverride,
    currentConfigProvider: () -> ConfigurationOverride,
    expandedSections: Set<OverrideEditorSection>,
    contentMaxWidth: Dp = Dp.Unspecified,
    contentTopSpacing: Dp = 12.dp,
    onConfigChange: (ConfigurationOverride) -> Unit,
    onSectionToggle: (OverrideEditorSection) -> Unit,
    onEditStringList: OpenStringListModifiersEditor,
    onEditRuleList: OpenRuleListEditor,
    onEditStringMap: OpenStringMapEditor,
    onEditJson: OpenJsonEditor,
    onEditObjectList: OpenStructuredObjectListEditor,
    onEditObjectMap: OpenObjectMapEditor,
    onEditSubRules: OpenSubRulesEditor,
) {
    OverrideSectionListContent(
        config = config,
        currentConfigProvider = currentConfigProvider,
        expandedSections = expandedSections,
        sectionListTitle = null,
        sectionListContentMaxWidth = contentMaxWidth,
        sectionListTopSpacing = contentTopSpacing,
        editSemantics = OverrideEditorSemantics.LocalConfig,
        hierarchyEmphasis = OverrideSectionHierarchyEmphasis.Nested,
        onConfigChange = onConfigChange,
        onSectionToggle = onSectionToggle,
        onEditStringList = onEditStringList,
        onEditRuleList = onEditRuleList,
        onEditStringMap = onEditStringMap,
        onEditJson = onEditJson,
        onEditObjectList = onEditObjectList,
        onEditObjectMap = onEditObjectMap,
        onEditSubRules = onEditSubRules,
    )
}

private fun LazyListScope.OverrideSectionListContent(
    config: ConfigurationOverride,
    currentConfigProvider: () -> ConfigurationOverride,
    expandedSections: Set<OverrideEditorSection>,
    sectionListTitle: String? = MLang.Override.Draft.ConfigSections,
    sectionListContentMaxWidth: Dp = Dp.Unspecified,
    sectionListTopSpacing: Dp = 0.dp,
    editSemantics: OverrideEditorSemantics = OverrideEditorSemantics.Override,
    hierarchyEmphasis: OverrideSectionHierarchyEmphasis = OverrideSectionHierarchyEmphasis.Flat,
    onConfigChange: (ConfigurationOverride) -> Unit,
    onSectionToggle: (OverrideEditorSection) -> Unit,
    onEditStringList: OpenStringListModifiersEditor,
    onEditRuleList: OpenRuleListEditor,
    onEditStringMap: OpenStringMapEditor,
    onEditJson: OpenJsonEditor,
    onEditObjectList: OpenStructuredObjectListEditor,
    onEditObjectMap: OpenObjectMapEditor,
    onEditSubRules: OpenSubRulesEditor,
) {
    item(key = "override-section-list") {
        val editorOverview = remember(config) { OverrideEditorSummaryBuilder.build(config) }
        val unsetLabel =
            when (editSemantics) {
                OverrideEditorSemantics.Override -> MLang.Component.Selector.NotModify
                OverrideEditorSemantics.LocalConfig -> MLang.Component.Selector.UseDefault
            }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            val contentModifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .let { modifier ->
                        if (sectionListContentMaxWidth != Dp.Unspecified) {
                            modifier.widthIn(max = sectionListContentMaxWidth)
                        } else {
                            modifier
                        }
                    }

            val content: @Composable () -> Unit = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(OverrideSectionSpacing),
                ) {
                    if (sectionListTopSpacing > 0.dp) {
                        Spacer(modifier = Modifier.height(sectionListTopSpacing))
                    }
                    OverrideEditorSection.entries.forEach { section ->
                        val directEntry = section.isDirectEntry()
                        val sectionSummary = editorOverview.sectionSummaries[section]
                        val configuredCount =
                            resolveDisplayedConfiguredCount(
                                section = section,
                                config = config,
                                editSemantics = editSemantics,
                                fallbackCount = sectionSummary?.modifiedCount ?: 0,
                            )
                        val sectionStyle = section.visualSpec()
                        val isSectionActive = configuredCount > 0
                        val displaySummary =
                            when (editSemantics) {
                                OverrideEditorSemantics.Override ->
                                    if (isSectionActive) {
                                        sectionSummary?.summaryText(editSemantics)
                                    } else {
                                        section.resolveSummary(editSemantics)
                                    }

                                OverrideEditorSemantics.LocalConfig ->
                                    buildSectionDescriptionSummary(
                                        description = section.resolveSummary(editSemantics),
                                        configuredCount = configuredCount,
                                    )
                            }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(OverrideSectionTitleSpacing),
                        ) {
                            OverrideSelectorCard {
                                OverrideSectionCardHeader(
                                    title = section.title,
                                    summary = displaySummary,
                                    expanded = !directEntry && section in expandedSections,
                                    imageVector = sectionStyle.icon,
                                    tone = sectionStyle.tone,
                                    active = isSectionActive,
                                    onClick = {
                                        if (directEntry) {
                                            openDirectSectionEditor(
                                                section = section,
                                                editSemantics = editSemantics,
                                                config = config,
                                                currentConfigProvider = currentConfigProvider,
                                                onConfigChange = onConfigChange,
                                                onEditRuleList = onEditRuleList,
                                                onEditObjectList = onEditObjectList,
                                                onEditObjectMap = onEditObjectMap,
                                                onEditSubRules = onEditSubRules,
                                            )
                                        } else {
                                            onSectionToggle(section)
                                        }
                                    },
                                    showIndicator = true,
                                )
                            }
                            if (!directEntry) {
                                OverrideSectionVisibility(visible = section in expandedSections) {
                                    if (hierarchyEmphasis == OverrideSectionHierarchyEmphasis.Nested) {
                                        OverrideNestedSectionContainer(tone = sectionStyle.tone) {
                                            OverrideSectionContent(
                                                section = section,
                                                config = config,
                                                unsetLabel = unsetLabel,
                                                onConfigChange = onConfigChange,
                                                onEditStringList = onEditStringList,
                                                onEditRuleList = onEditRuleList,
                                                onEditStringMap = onEditStringMap,
                                                onEditJson = onEditJson,
                                                onEditObjectList = onEditObjectList,
                                                onEditObjectMap = onEditObjectMap,
                                                onEditSubRules = onEditSubRules,
                                            )
                                        }
                                    } else {
                                        OverrideSectionContent(
                                            section = section,
                                            config = config,
                                            unsetLabel = unsetLabel,
                                            onConfigChange = onConfigChange,
                                            onEditStringList = onEditStringList,
                                            onEditRuleList = onEditRuleList,
                                            onEditStringMap = onEditStringMap,
                                            onEditJson = onEditJson,
                                            onEditObjectList = onEditObjectList,
                                            onEditObjectMap = onEditObjectMap,
                                            onEditSubRules = onEditSubRules,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (sectionListTitle.isNullOrBlank()) {
                Box(modifier = contentModifier) { content() }
            } else {
                OverrideSection(title = sectionListTitle, modifier = contentModifier) { content() }
            }
        }
    }

    item(key = "override-bottom-spacer") {
        Spacer(modifier = Modifier.height(OverrideSectionBottomSpacing))
    }
}

private fun buildSectionDescriptionSummary(description: String, configuredCount: Int): String {
    val configuredSummary = MLang.Override.Form.ItemsConfigured.format(configuredCount)
    return listOf(description, configuredSummary).filterNotNull().filter { it.isNotBlank() }.joinToString(" · ")
}

private fun resolveDisplayedConfiguredCount(
    section: OverrideEditorSection,
    config: ConfigurationOverride,
    editSemantics: OverrideEditorSemantics,
    fallbackCount: Int,
): Int {
    if (editSemantics != OverrideEditorSemantics.LocalConfig) {
        return fallbackCount
    }

    return when (section) {
        OverrideEditorSection.Rules ->
            localConfigListValues(config.rules, config.rulesStart, config.rulesEnd).replaceValue
                ?.size ?: 0

        OverrideEditorSection.Proxies ->
            localConfigListValues(config.proxies, config.proxiesStart, config.proxiesEnd)
                .replaceValue
                ?.size ?: 0

        OverrideEditorSection.ProxyGroups ->
            localConfigListValues(
                config.proxyGroups,
                config.proxyGroupsStart,
                config.proxyGroupsEnd,
            ).replaceValue?.size ?: 0

        OverrideEditorSection.ProxyProviders ->
            localConfigMapValues(config.proxyProviders, config.proxyProvidersMerge).replaceValue
                ?.size ?: 0

        OverrideEditorSection.RuleProviders ->
            localConfigMapValues(config.ruleProviders, config.ruleProvidersMerge).replaceValue
                ?.size ?: 0

        OverrideEditorSection.SubRules ->
            localConfigMapValues(config.subRules, config.subRulesMerge).replaceValue?.size ?: 0

        OverrideEditorSection.General,
        OverrideEditorSection.Dns,
        OverrideEditorSection.Sniffer,
        OverrideEditorSection.Inbound,
        OverrideEditorSection.Tun -> fallbackCount
    }
}

@Composable
private fun OverrideNestedSectionContainer(tone: SemanticTone, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = false)
    val containerColor = lerp(MiuixTheme.colorScheme.surface, MiuixTheme.colorScheme.surfaceVariant, 0.22f)
    val borderColor =
        lerp(
            MiuixTheme.colorScheme.outline.copy(alpha = 0.12f),
            style.contentColor.copy(alpha = 0.10f),
            0.18f,
        )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 4.dp, top = 4.dp)
                .clip(shape)
                .background(
                    color = containerColor,
                    shape = shape,
                )
                .border(
                    width = 0.8.dp,
                    color = borderColor,
                    shape = shape,
                )
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 12.dp),
    ) {
        content()
    }
}

@Composable
private fun OverrideSectionContent(
    section: OverrideEditorSection,
    config: ConfigurationOverride,
    unsetLabel: String,
    onConfigChange: (ConfigurationOverride) -> Unit,
    onEditStringList: OpenStringListModifiersEditor,
    onEditRuleList: OpenRuleListEditor,
    onEditStringMap: OpenStringMapEditor,
    onEditJson: OpenJsonEditor,
    onEditObjectList: OpenStructuredObjectListEditor,
    onEditObjectMap: OpenObjectMapEditor,
    onEditSubRules: OpenSubRulesEditor,
) {
    when (section) {
        OverrideEditorSection.General ->
            GeneralEditor(config, onConfigChange, onEditStringList, unsetLabel)
        OverrideEditorSection.Dns ->
            DnsEditor(
                config = config,
                onConfigChange = onConfigChange,
                onEditStringList = onEditStringList,
                onEditStringMap = onEditStringMap,
                unsetLabel = unsetLabel,
            )
        OverrideEditorSection.Sniffer ->
            SnifferEditor(config, onConfigChange, onEditStringList, unsetLabel)
        OverrideEditorSection.Inbound ->
            InboundEditor(config, onConfigChange, onEditStringList, unsetLabel)
        OverrideEditorSection.Tun -> TunEditor(config, onConfigChange, onEditStringList, unsetLabel)
        OverrideEditorSection.Proxies,
        OverrideEditorSection.ProxyProviders,
        OverrideEditorSection.ProxyGroups,
        OverrideEditorSection.Rules,
        OverrideEditorSection.RuleProviders,
        OverrideEditorSection.SubRules -> Unit
    }
}

private fun OverrideEditorSection.isDirectEntry(): Boolean {
    return when (this) {
        OverrideEditorSection.General,
        OverrideEditorSection.Dns,
        OverrideEditorSection.Sniffer,
        OverrideEditorSection.Inbound,
        OverrideEditorSection.Tun -> false

        OverrideEditorSection.Rules,
        OverrideEditorSection.Proxies,
        OverrideEditorSection.ProxyProviders,
        OverrideEditorSection.ProxyGroups,
        OverrideEditorSection.RuleProviders,
        OverrideEditorSection.SubRules -> true
    }
}

private fun openDirectSectionEditor(
    section: OverrideEditorSection,
    editSemantics: OverrideEditorSemantics,
    config: ConfigurationOverride,
    currentConfigProvider: () -> ConfigurationOverride,
    onConfigChange: (ConfigurationOverride) -> Unit,
    onEditRuleList: OpenRuleListEditor,
    onEditObjectList: OpenStructuredObjectListEditor,
    onEditObjectMap: OpenObjectMapEditor,
    onEditSubRules: OpenSubRulesEditor,
) {
    when (section) {
        OverrideEditorSection.Rules -> {
            val values =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    localConfigListValues(
                        replaceValue = config.rules,
                        startValue = config.rulesStart,
                        endValue = config.rulesEnd,
                    )
                } else {
                    OverrideListModeValues(
                        replaceValue = config.rules,
                        startValue = config.rulesStart,
                        endValue = config.rulesEnd,
                    )
                }
            val referenceCatalog = buildOverrideReferenceCatalog(currentConfigProvider())
            val availableModes =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    listOf(OverrideListEditorMode.Replace)
                } else {
                    listOf(
                        OverrideListEditorMode.Replace,
                        OverrideListEditorMode.Start,
                        OverrideListEditorMode.End,
                    )
                }
            onEditRuleList(
                "Rules",
                values,
                availableModes,
                resolveInitialEditorMode(availableModes, values),
                referenceCatalog,
            ) { updatedValues ->
                onConfigChange(
                    currentConfigProvider()
                        .copy(
                            rules = updatedValues.replaceValue,
                            rulesStart =
                                updatedValues.startValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                            rulesEnd =
                                updatedValues.endValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                        )
                )
            }
        }

        OverrideEditorSection.Proxies -> {
            val values =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    localConfigListValues(
                        replaceValue = config.proxies,
                        startValue = config.proxiesStart,
                        endValue = config.proxiesEnd,
                    )
                } else {
                    OverrideListModeValues(
                        replaceValue = config.proxies,
                        startValue = config.proxiesStart,
                        endValue = config.proxiesEnd,
                    )
                }
            val referenceCatalog = buildOverrideReferenceCatalog(currentConfigProvider())
            val availableModes =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    listOf(OverrideListEditorMode.Replace)
                } else {
                    listOf(
                        OverrideListEditorMode.Replace,
                        OverrideListEditorMode.Start,
                        OverrideListEditorMode.End,
                    )
                }
            onEditObjectList(
                OverrideStructuredObjectType.Proxies,
                MLang.Override.Form.ProxyNodes,
                values,
                availableModes,
                resolveInitialEditorMode(availableModes, values),
                referenceCatalog,
            ) { updatedValues ->
                onConfigChange(
                    currentConfigProvider()
                        .copy(
                            proxies = updatedValues.replaceValue,
                            proxiesStart =
                                updatedValues.startValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                            proxiesEnd =
                                updatedValues.endValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                        )
                )
            }
        }

        OverrideEditorSection.ProxyGroups -> {
            val values =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    localConfigListValues(
                        replaceValue = config.proxyGroups,
                        startValue = config.proxyGroupsStart,
                        endValue = config.proxyGroupsEnd,
                    )
                } else {
                    OverrideListModeValues(
                        replaceValue = config.proxyGroups,
                        startValue = config.proxyGroupsStart,
                        endValue = config.proxyGroupsEnd,
                    )
                }
            val referenceCatalog = buildOverrideReferenceCatalog(currentConfigProvider())
            val availableModes =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    listOf(OverrideListEditorMode.Replace)
                } else {
                    listOf(
                        OverrideListEditorMode.Replace,
                        OverrideListEditorMode.Start,
                        OverrideListEditorMode.End,
                    )
                }
            onEditObjectList(
                OverrideStructuredObjectType.ProxyGroups,
                MLang.Override.Form.ProxyGroups,
                values,
                availableModes,
                resolveInitialEditorMode(availableModes, values),
                referenceCatalog,
            ) { updatedValues ->
                onConfigChange(
                    currentConfigProvider()
                        .copy(
                            proxyGroups = updatedValues.replaceValue,
                            proxyGroupsStart =
                                updatedValues.startValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                            proxyGroupsEnd =
                                updatedValues.endValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                        )
                )
            }
        }

        OverrideEditorSection.ProxyProviders -> {
            val values =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    localConfigMapValues(
                        replaceValue = config.proxyProviders,
                        mergeValue = config.proxyProvidersMerge,
                    )
                } else {
                    OverrideListModeValues(
                        replaceValue = config.proxyProviders,
                        mergeValue = config.proxyProvidersMerge,
                    )
                }
            val availableModes =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    listOf(OverrideListEditorMode.Replace)
                } else {
                    listOf(OverrideListEditorMode.Replace, OverrideListEditorMode.Merge)
                }
            onEditObjectMap(
                OverrideStructuredMapType.ProxyProviders,
                MLang.Override.Form.ProxyProviders,
                values,
                availableModes,
                resolveInitialEditorMode(availableModes, values),
            ) { updatedValues ->
                onConfigChange(
                    currentConfigProvider()
                        .copy(
                            proxyProviders = updatedValues.replaceValue,
                            proxyProvidersMerge =
                                updatedValues.mergeValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                        )
                )
            }
        }

        OverrideEditorSection.RuleProviders -> {
            val values =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    localConfigMapValues(
                        replaceValue = config.ruleProviders,
                        mergeValue = config.ruleProvidersMerge,
                    )
                } else {
                    OverrideListModeValues(
                        replaceValue = config.ruleProviders,
                        mergeValue = config.ruleProvidersMerge,
                    )
                }
            val availableModes =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    listOf(OverrideListEditorMode.Replace)
                } else {
                    listOf(OverrideListEditorMode.Replace, OverrideListEditorMode.Merge)
                }
            onEditObjectMap(
                OverrideStructuredMapType.RuleProviders,
                MLang.Override.Form.RuleProviders,
                values,
                availableModes,
                resolveInitialEditorMode(availableModes, values),
            ) { updatedValues ->
                onConfigChange(
                    currentConfigProvider()
                        .copy(
                            ruleProviders = updatedValues.replaceValue,
                            ruleProvidersMerge =
                                updatedValues.mergeValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                        )
                )
            }
        }

        OverrideEditorSection.SubRules -> {
            val values =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    localConfigMapValues(
                        replaceValue = config.subRules,
                        mergeValue = config.subRulesMerge,
                    )
                } else {
                    OverrideListModeValues(
                        replaceValue = config.subRules,
                        mergeValue = config.subRulesMerge,
                    )
                }
            val referenceCatalog = buildOverrideReferenceCatalog(currentConfigProvider())
            val availableModes =
                if (editSemantics == OverrideEditorSemantics.LocalConfig) {
                    listOf(OverrideListEditorMode.Replace)
                } else {
                    listOf(OverrideListEditorMode.Replace, OverrideListEditorMode.Merge)
                }
            onEditSubRules(
                MLang.Override.Form.SubRules,
                values,
                availableModes,
                resolveInitialEditorMode(availableModes, values),
                referenceCatalog,
            ) { updatedValues ->
                onConfigChange(
                    currentConfigProvider()
                        .copy(
                            subRules = updatedValues.replaceValue,
                            subRulesMerge =
                                updatedValues.mergeValue.takeIf {
                                    editSemantics == OverrideEditorSemantics.Override
                                },
                        )
                )
            }
        }

        OverrideEditorSection.General,
        OverrideEditorSection.Dns,
        OverrideEditorSection.Sniffer,
        OverrideEditorSection.Inbound,
        OverrideEditorSection.Tun -> Unit
    }
}

private fun <T> localConfigListValues(
    replaceValue: List<T>?,
    startValue: List<T>?,
    endValue: List<T>?,
): OverrideListModeValues<List<T>> {
    val combined = buildList {
        addAll(startValue.orEmpty())
        addAll(replaceValue.orEmpty())
        addAll(endValue.orEmpty())
    }
    return OverrideListModeValues(replaceValue = combined.takeIf { it.isNotEmpty() })
}

private fun <T> localConfigMapValues(
    replaceValue: Map<String, T>?,
    mergeValue: Map<String, T>?,
): OverrideListModeValues<Map<String, T>> {
    val combined = linkedMapOf<String, T>()
    mergeValue.orEmpty().forEach { (key, value) ->
        combined[key] = value
    }
    replaceValue.orEmpty().forEach { (key, value) ->
        if (key !in combined) {
            combined[key] = value
        }
    }
    return OverrideListModeValues(replaceValue = combined.takeIf { it.isNotEmpty() })
}

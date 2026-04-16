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

package com.github.nomadboxlab.monadbox.presentation.util

import dev.oom_wg.purejoy.mlang.MLang

enum class OverrideEditorSection {
    General {
        override val title: String
            get() = MLang.Override.Section.General.Title

        override val summary: String
            get() = MLang.Override.Section.General.Summary
    },
    Dns {
        override val title: String
            get() = MLang.Override.Section.Dns.Title

        override val summary: String
            get() = MLang.Override.Section.Dns.Summary
    },
    Sniffer {
        override val title: String
            get() = MLang.Override.Section.Sniffer.Title

        override val summary: String
            get() = MLang.Override.Section.Sniffer.Summary
    },
    Inbound {
        override val title: String
            get() = MLang.Override.Section.Inbound.Title

        override val summary: String
            get() = MLang.Override.Section.Inbound.Summary
    },
    Tun {
        override val title: String
            get() = MLang.Override.Section.Tun.Title

        override val summary: String
            get() = MLang.Override.Section.Tun.Summary
    },
    Rules {
        override val title: String
            get() = MLang.Override.Section.Rules.Title

        override val summary: String
            get() = MLang.Override.Section.Rules.Summary
    },
    Proxies {
        override val title: String
            get() = MLang.Override.Section.Proxies.Title

        override val summary: String
            get() = MLang.Override.Section.Proxies.Summary
    },
    ProxyProviders {
        override val title: String
            get() = MLang.Override.Section.ProxyProviders.Title

        override val summary: String
            get() = MLang.Override.Section.ProxyProviders.Summary
    },
    ProxyGroups {
        override val title: String
            get() = MLang.Override.Section.ProxyGroups.Title

        override val summary: String
            get() = MLang.Override.Section.ProxyGroups.Summary
    },
    RuleProviders {
        override val title: String
            get() = MLang.Override.Section.RuleProviders.Title

        override val summary: String
            get() = MLang.Override.Section.RuleProviders.Summary
    },
    SubRules {
        override val title: String
            get() = MLang.Override.Section.SubRules.Title

        override val summary: String
            get() = MLang.Override.Section.SubRules.Summary
    };

    abstract val title: String
    abstract val summary: String
}

enum class OverrideEditorSemantics {
    Override,
    LocalConfig,
}

fun OverrideEditorSection.resolveSummary(semantics: OverrideEditorSemantics): String {
    return summary
}

enum class OverrideModifierVisualMode {
    Replace {
        override val label: String
            get() = MLang.Override.Modifier.Replace
    },
    Start {
        override val label: String
            get() = MLang.Override.Modifier.Start
    },
    End {
        override val label: String
            get() = MLang.Override.Modifier.End
    },
    Merge {
        override val label: String
            get() = MLang.Override.Modifier.Merge
    },
    Force {
        override val label: String
            get() = MLang.Override.Modifier.Force
    };

    abstract val label: String
}

fun OverrideModifierVisualMode.resolveLabel(semantics: OverrideEditorSemantics): String {
    return when (semantics) {
        OverrideEditorSemantics.Override -> label
        OverrideEditorSemantics.LocalConfig ->
            when (this) {
                OverrideModifierVisualMode.Replace -> MLang.Override.Editor.Mode.DirectEdit
                OverrideModifierVisualMode.Start -> MLang.Override.Modifier.Start
                OverrideModifierVisualMode.End -> MLang.Override.Modifier.End
                OverrideModifierVisualMode.Merge -> MLang.Override.Modifier.Merge
                OverrideModifierVisualMode.Force -> label
            }
    }
}

sealed interface OverrideSaveState {
    data object Idle : OverrideSaveState

    data object Saving : OverrideSaveState
}

sealed interface OverrideSaveEvent {
    data class Saved(val configId: String) : OverrideSaveEvent

    data class Failed(val error: com.github.nomadboxlab.monadbox.domain.model.StructuredError) :
        OverrideSaveEvent
}

data class OverrideSectionSummary(
    val modifiedCount: Int,
    val visualModes: Set<OverrideModifierVisualMode>,
) {
    fun summaryText(semantics: OverrideEditorSemantics = OverrideEditorSemantics.Override): String {
        if (modifiedCount == 0) {
            return MLang.Override.Modifier.NotModified
        }
        val modeSummary = visualModes.joinToString(" / ") { it.resolveLabel(semantics) }
        return buildString {
            if (modeSummary.isNotEmpty()) {
                append(modeSummary)
                append(" · ")
            }
            append(MLang.Override.Modifier.ItemsCount.format(modifiedCount))
        }
    }
}

data class OverrideEditorOverview(
    val changedFieldCount: Int,
    val activeSectionCount: Int,
    val replaceCount: Int,
    val appendCount: Int,
    val mergeCount: Int,
    val forceCount: Int,
    val sectionSummaries: Map<OverrideEditorSection, OverrideSectionSummary>,
    val warnings: List<String>,
) {
    val modifierSummary: String
        get() =
            buildList {
                    if (replaceCount > 0) add("${MLang.Override.Modifier.Replace} $replaceCount")
                    if (appendCount > 0) add("${MLang.Override.Modifier.Start} $appendCount")
                    if (mergeCount > 0) add("${MLang.Override.Modifier.Merge} $mergeCount")
                    if (forceCount > 0) add("${MLang.Override.Modifier.Force} $forceCount")
                }
                .joinToString(" · ")
                .ifEmpty { MLang.Override.Modifier.NotModified }

    val sectionSummary: String
        get() =
            sectionSummaries
                .filterValues { it.modifiedCount > 0 }
                .keys
                .take(3)
                .joinToString(" · ") { it.title }
                .ifEmpty { MLang.Override.Modifier.NoChanges }
}

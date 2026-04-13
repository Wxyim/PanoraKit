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

package com.github.yumelira.yumebox.presentation.diagnostic

import com.github.yumelira.yumebox.data.model.ProxyMode
import dev.oom_wg.purejoy.mlang.DiagnosticLang

fun buildDiagnosticRemediationPlan(
    needsAttention: Boolean,
    actions: List<DiagnosticRemediationAction>,
): DiagnosticRemediationPlan {
    return DiagnosticRemediationPlan(
        headline = DiagnosticLang.DetailPages.Remediation.Title,
        summary =
            if (needsAttention) {
                DiagnosticLang.DetailPages.Remediation.AttentionSummary
            } else {
                DiagnosticLang.DetailPages.Remediation.HealthySummary
            },
        actions = actions.distinctBy(DiagnosticRemediationAction::command),
    )
}

fun buildRuntimeEntryRemediationPlan(
    hasAttention: Boolean,
    runtimeRunning: Boolean,
): DiagnosticRemediationPlan {
    val actions =
        buildList {
            if (!hasAttention) {
                return@buildList
            }

            if (runtimeRunning) {
                add(reloadRuntimeAction(isPrimary = true))
            } else {
                add(startRuntimeAction(isPrimary = true))
            }
            add(openRuntimeHealthAction())
        }

    return buildDiagnosticRemediationPlan(needsAttention = hasAttention, actions = actions)
}

fun buildSourceEntryRemediationPlan(
    hasSources: Boolean,
    needsAttention: Boolean,
): DiagnosticRemediationPlan {
    val actions =
        buildList {
            if (hasSources) {
                add(refreshSourcesAction(isPrimary = needsAttention))
                add(openSourceRegistryAction())
            } else if (needsAttention) {
                add(openSourceRegistryAction(isPrimary = true))
            }
        }

    return buildDiagnosticRemediationPlan(needsAttention = needsAttention, actions = actions)
}

fun startRuntimeAction(
    isPrimary: Boolean = false,
    profileId: String? = null,
    requestedMode: ProxyMode? = null,
): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "start-runtime",
        title = DiagnosticLang.DetailPages.Remediation.StartRuntime,
        summary = DiagnosticLang.DetailPages.Remediation.StartRuntimeSummary,
        command = DiagnosticActionCommand.StartRuntime,
        effect = DiagnosticActionEffect.Execute,
        risk = DiagnosticActionRisk.Low,
        isPrimary = isPrimary,
        profileId = profileId,
        requestedMode = requestedMode,
    )
}

fun reloadRuntimeAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "reload-runtime",
        title = DiagnosticLang.DetailPages.Remediation.ReloadRuntime,
        summary = DiagnosticLang.DetailPages.Remediation.ReloadRuntimeSummary,
        command = DiagnosticActionCommand.ReloadRuntime,
        effect = DiagnosticActionEffect.Execute,
        risk = DiagnosticActionRisk.Medium,
        isPrimary = isPrimary,
    )
}

fun restartRuntimeAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "restart-runtime",
        title = DiagnosticLang.DetailPages.Remediation.RestartRuntime,
        summary = DiagnosticLang.DetailPages.Remediation.RestartRuntimeSummary,
        command = DiagnosticActionCommand.RestartRuntime,
        effect = DiagnosticActionEffect.Execute,
        risk = DiagnosticActionRisk.High,
        isPrimary = isPrimary,
        requiresConfirmation = true,
        confirmationMessage = DiagnosticLang.DetailPages.Remediation.RestartRuntimeConfirm,
    )
}

fun refreshSourcesAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "refresh-sources",
        title = DiagnosticLang.DetailPages.Remediation.RefreshSources,
        summary = DiagnosticLang.DetailPages.Remediation.RefreshSourcesSummary,
        command = DiagnosticActionCommand.RefreshSources,
        effect = DiagnosticActionEffect.Execute,
        risk = DiagnosticActionRisk.Medium,
        isPrimary = isPrimary,
    )
}

fun openRuntimeHealthAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-runtime-health",
        title = DiagnosticLang.DetailPages.Remediation.OpenRuntimeHealth,
        summary = DiagnosticLang.DetailPages.Remediation.OpenRuntimeHealthSummary,
        command = DiagnosticActionCommand.OpenRuntimeHealth,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun openRuleSetInspectorAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-rule-set-inspector",
        title = DiagnosticLang.DetailPages.Remediation.OpenRuleSetInspector,
        summary = DiagnosticLang.DetailPages.Remediation.OpenRuleSetInspectorSummary,
        command = DiagnosticActionCommand.OpenRuleSetInspector,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun openExplanationChainAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-explanation-chain",
        title = DiagnosticLang.DetailPages.Remediation.OpenExplanationChain,
        summary = DiagnosticLang.DetailPages.Remediation.OpenExplanationChainSummary,
        command = DiagnosticActionCommand.OpenExplanationChain,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun openRawTraceAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-raw-trace",
        title = DiagnosticLang.DetailPages.Remediation.OpenRawTrace,
        summary = DiagnosticLang.DetailPages.Remediation.OpenRawTraceSummary,
        command = DiagnosticActionCommand.OpenRawTrace,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun openSourceRegistryAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-source-registry",
        title = DiagnosticLang.DetailPages.Remediation.OpenSourceRegistry,
        summary = DiagnosticLang.DetailPages.Remediation.OpenSourceRegistrySummary,
        command = DiagnosticActionCommand.OpenSourceRegistry,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun openProvidersAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-providers",
        title = DiagnosticLang.DetailPages.Remediation.OpenProviders,
        summary = DiagnosticLang.DetailPages.Remediation.OpenProvidersSummary,
        command = DiagnosticActionCommand.OpenProviders,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun openLogsAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "open-logs",
        title = DiagnosticLang.DetailPages.Remediation.OpenLogs,
        summary = DiagnosticLang.DetailPages.Remediation.OpenLogsSummary,
        command = DiagnosticActionCommand.OpenLogs,
        effect = DiagnosticActionEffect.Inspect,
        isPrimary = isPrimary,
    )
}

fun copyRawTraceAction(isPrimary: Boolean = false): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "copy-raw-trace",
        title = DiagnosticLang.DetailPages.Remediation.CopyRawTrace,
        summary = DiagnosticLang.DetailPages.Remediation.CopyRawTraceSummary,
        command = DiagnosticActionCommand.CopyRawTrace,
        effect = DiagnosticActionEffect.Export,
        isPrimary = isPrimary,
    )
}

fun refreshPageAction(): DiagnosticRemediationAction {
    return DiagnosticRemediationAction(
        actionId = "refresh-page",
        title = DiagnosticLang.DetailPages.Remediation.RefreshPage,
        summary = DiagnosticLang.DetailPages.Remediation.RefreshPageSummary,
        command = DiagnosticActionCommand.RefreshPage,
        effect = DiagnosticActionEffect.Inspect,
    )
}
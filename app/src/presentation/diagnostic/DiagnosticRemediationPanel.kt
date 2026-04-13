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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.yumelira.yumebox.presentation.component.Card
import com.github.yumelira.yumebox.presentation.component.ConfigSettingRow
import com.github.yumelira.yumebox.presentation.component.ConfirmDialogSimple
import com.github.yumelira.yumebox.presentation.component.InfoSettingRow
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Activity
import com.github.yumelira.yumebox.presentation.icon.yume.Cloud
import com.github.yumelira.yumebox.presentation.icon.yume.Copy
import com.github.yumelira.yumebox.presentation.icon.yume.Folders
import com.github.yumelira.yumebox.presentation.icon.yume.ListCollapse
import com.github.yumelira.yumebox.presentation.icon.yume.Message
import com.github.yumelira.yumebox.presentation.icon.yume.Play
import com.github.yumelira.yumebox.presentation.icon.yume.PowerOff
import com.github.yumelira.yumebox.presentation.icon.yume.Save
import com.github.yumelira.yumebox.presentation.icon.yume.Sparkles
import com.github.yumelira.yumebox.presentation.icon.yume.Undo
import dev.oom_wg.purejoy.mlang.DiagnosticLang

@Composable
fun DiagnosticRemediationPanel(
    plan: DiagnosticRemediationPlan,
    actionUiState: DiagnosticActionUiState,
    onAction: (DiagnosticRemediationAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (plan.actions.isEmpty() && actionUiState.feedback == null) {
        return
    }

    var pendingConfirmation by remember(plan.actions) {
        mutableStateOf<DiagnosticRemediationAction?>(null)
    }
    val prioritizedActions =
        plan.actions.sortedWith(
            compareByDescending<DiagnosticRemediationAction> { it.isPrimaryAction() }
        )
    val feedback = actionUiState.feedback

    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (feedback != null) {
                InfoSettingRow(
                    title = feedback.title,
                    summary = feedback.message,
                    valueLabel = feedback.status.toDisplayLabel(),
                    tone = feedback.status.toSemanticTone(),
                    badgeLeadingDot = feedback.status != DiagnosticActionFeedbackStatus.Success,
                    showDivider = prioritizedActions.isNotEmpty(),
                )
            }

            prioritizedActions.forEachIndexed { index, action ->
                val isActive = actionUiState.activeActionId == action.actionId
                ConfigSettingRow(
                    title =
                        if (isActive) {
                            DiagnosticLang.DetailPages.Remediation.InProgress
                        } else {
                            action.title
                        },
                    summary = action.summary,
                    imageVector = action.command.toIcon(),
                    tone = action.toRowTone(),
                    valueLabel =
                        if (isActive) {
                            DiagnosticLang.DetailPages.Remediation.Pending
                        } else {
                            action.effect.toDisplayLabel()
                        },
                    badgeTone = if (isActive) SemanticTone.Brand else action.effect.toSemanticTone(),
                    badgeLeadingDot = isActive || action.effect != DiagnosticActionEffect.Inspect,
                    showDivider = index != prioritizedActions.lastIndex,
                    onClick = {
                        if (action.requiresConfirmation) {
                            pendingConfirmation = action
                        } else {
                            onAction(action)
                        }
                    },
                )
            }
        }
    }

    pendingConfirmation?.let { action ->
        ConfirmDialogSimple(
            title = action.title,
            message = action.confirmationMessage ?: action.summary,
            onConfirm = {
                pendingConfirmation = null
                onAction(action)
            },
            onDismiss = { pendingConfirmation = null },
        )
    }
}

private fun DiagnosticRemediationAction.isPrimaryAction(): Boolean {
    return isPrimary || effect == DiagnosticActionEffect.Execute || risk != DiagnosticActionRisk.Low
}

private fun DiagnosticActionFeedbackStatus.toDisplayLabel(): String {
    return when (this) {
        DiagnosticActionFeedbackStatus.Success -> DiagnosticLang.DetailPages.Remediation.Success
        DiagnosticActionFeedbackStatus.Info -> DiagnosticLang.DetailPages.Remediation.Info
        DiagnosticActionFeedbackStatus.Warning -> DiagnosticLang.DetailPages.Remediation.Warning
        DiagnosticActionFeedbackStatus.Pending -> DiagnosticLang.DetailPages.Remediation.Pending
        DiagnosticActionFeedbackStatus.Failed -> DiagnosticLang.DetailPages.Remediation.Failed
    }
}

private fun DiagnosticActionFeedbackStatus.toSemanticTone(): SemanticTone {
    return when (this) {
        DiagnosticActionFeedbackStatus.Success -> SemanticTone.Success
        DiagnosticActionFeedbackStatus.Info -> SemanticTone.Info
        DiagnosticActionFeedbackStatus.Warning -> SemanticTone.Warning
        DiagnosticActionFeedbackStatus.Pending -> SemanticTone.Brand
        DiagnosticActionFeedbackStatus.Failed -> SemanticTone.Danger
    }
}

private fun DiagnosticActionEffect.toDisplayLabel(): String {
    return when (this) {
        DiagnosticActionEffect.Execute -> DiagnosticLang.DetailPages.Remediation.Execute
        DiagnosticActionEffect.Inspect -> DiagnosticLang.DetailPages.Remediation.Inspect
        DiagnosticActionEffect.Export -> DiagnosticLang.DetailPages.Remediation.Export
    }
}

private fun DiagnosticActionEffect.toSemanticTone(): SemanticTone {
    return when (this) {
        DiagnosticActionEffect.Execute -> SemanticTone.Brand
        DiagnosticActionEffect.Inspect -> SemanticTone.Info
        DiagnosticActionEffect.Export -> SemanticTone.Neutral
    }
}

private fun DiagnosticRemediationAction.toRowTone(): SemanticTone {
    return when {
        effect == DiagnosticActionEffect.Export -> SemanticTone.Neutral
        effect == DiagnosticActionEffect.Inspect && isPrimary -> SemanticTone.Brand
        effect == DiagnosticActionEffect.Inspect -> SemanticTone.Info
        risk == DiagnosticActionRisk.High -> SemanticTone.Danger
        risk == DiagnosticActionRisk.Medium -> SemanticTone.Warning
        else -> SemanticTone.Brand
    }
}

private fun DiagnosticActionCommand.toIcon() =
    when (this) {
        DiagnosticActionCommand.RefreshPage,
        DiagnosticActionCommand.OpenRuntimeHealth -> Yume.Activity
        DiagnosticActionCommand.StartRuntime -> Yume.Play
        DiagnosticActionCommand.ReloadRuntime -> Yume.Undo
        DiagnosticActionCommand.RestartRuntime -> Yume.PowerOff
        DiagnosticActionCommand.RefreshSources,
        DiagnosticActionCommand.OpenProviders -> Yume.Cloud
        DiagnosticActionCommand.OpenRuleSetInspector -> Yume.ListCollapse
        DiagnosticActionCommand.OpenSnapshotHistory -> Yume.Save
        DiagnosticActionCommand.OpenExplanationChain -> Yume.Sparkles
        DiagnosticActionCommand.OpenRawTrace,
        DiagnosticActionCommand.CopyRawTrace -> Yume.Copy
        DiagnosticActionCommand.OpenSourceRegistry -> Yume.Folders
        DiagnosticActionCommand.OpenLogs -> Yume.Message
    }

/*
 * This file is part of MonadBox.
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.presentation.diagnostic

import com.github.yumelira.yumebox.domain.model.ExplanationChain
import com.github.yumelira.yumebox.domain.model.HealthReport
import com.github.yumelira.yumebox.domain.model.RuleSet
import com.github.yumelira.yumebox.domain.model.StructuredError
import com.github.yumelira.yumebox.domain.model.StructuredLogEntry
import com.github.yumelira.yumebox.domain.model.SubscriptionSource
import com.github.yumelira.yumebox.domain.model.TraceStep
import com.github.yumelira.yumebox.domain.model.WorkspaceSnapshot
import com.github.yumelira.yumebox.presentation.util.ExternalResourceDiagnostics
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import dev.oom_wg.purejoy.mlang.DiagnosticLang

data class RuntimeHealthDetail(
    val generatedAtMillis: Long = 0L,
    val profileId: String? = null,
    val profileName: String? = null,
    val runtimeSnapshot: RuntimeSnapshot = RuntimeSnapshot(),
    val healthReport: HealthReport = HealthReport.empty(reportId = "runtime-health-empty"),
    val structuredError: StructuredError? = null,
    val recentFailures: List<StructuredLogEntry> = emptyList(),
    val externalResources: ExternalResourceDiagnostics = ExternalResourceDiagnostics(),
    val sourcePath: String? = null,
    val remediationPlan: DiagnosticRemediationPlan = DiagnosticRemediationPlan(),
)

data class RuleSetInspectorDetail(
    val generatedAtMillis: Long = 0L,
    val profileId: String? = null,
    val profileName: String? = null,
    val sourceLabel: String = "",
    val sourcePath: String? = null,
    val configHash: String? = null,
    val ruleSets: List<RuleSet> = emptyList(),
    val structuredError: StructuredError? = null,
    val overlayRuleCount: Int = 0,
    val remediationPlan: DiagnosticRemediationPlan = DiagnosticRemediationPlan(),
)

data class SnapshotHistoryDetail(
    val generatedAtMillis: Long = 0L,
    val profileId: String? = null,
    val profileName: String? = null,
    val currentSnapshotId: String? = null,
    val snapshots: List<WorkspaceSnapshot> = emptyList(),
    val structuredError: StructuredError? = null,
    val remediationPlan: DiagnosticRemediationPlan = DiagnosticRemediationPlan(),
)

data class ExplanationChainDetail(
    val generatedAtMillis: Long = 0L,
    val profileId: String? = null,
    val profileName: String? = null,
    val chain: ExplanationChain =
        ExplanationChain(
            chainId = "explanation-empty",
            steps = emptyList(),
            conclusion = DiagnosticLang.DetailPages.ExplanationChain.NoChain,
            isSuccess = false,
        ),
    val structuredError: StructuredError? = null,
    val sourcePath: String? = null,
    val configHash: String? = null,
    val recentEvents: List<StructuredLogEntry> = emptyList(),
    val remediationPlan: DiagnosticRemediationPlan = DiagnosticRemediationPlan(),
)

data class RawTraceSection(
    val sectionId: String,
    val title: String,
    val lines: List<String> = emptyList(),
)

data class RawTraceDetail(
    val generatedAtMillis: Long = 0L,
    val profileId: String? = null,
    val profileName: String? = null,
    val traceId: String = "trace-empty",
    val runtimePhase: RuntimePhase = RuntimePhase.Idle,
    val steps: List<TraceStep> = emptyList(),
    val rawSections: List<RawTraceSection> = emptyList(),
    val recentEvents: List<StructuredLogEntry> = emptyList(),
    val structuredError: StructuredError? = null,
    val configHash: String? = null,
    val remediationPlan: DiagnosticRemediationPlan = DiagnosticRemediationPlan(),
)

enum class SourceRegistryRole {
    RuntimeConfig,
    ProfileConfig,
    RuleProvider,
    ProxyProvider,
    RemoteOverride,
}

data class SourceRegistryItem(
    val registryId: String,
    val role: SourceRegistryRole,
    val source: SubscriptionSource,
    val isEffective: Boolean = false,
    val fingerprint: String? = null,
    val itemCount: Int? = null,
    val note: String? = null,
)

data class SourceRegistryOverviewDetail(
    val generatedAtMillis: Long = 0L,
    val profileId: String? = null,
    val profileName: String? = null,
    val effectiveFingerprint: String? = null,
    val items: List<SourceRegistryItem> = emptyList(),
    val structuredError: StructuredError? = null,
    val remediationPlan: DiagnosticRemediationPlan = DiagnosticRemediationPlan(),
)

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

import android.content.Context
import com.github.yumelira.yumebox.core.Clash
import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.data.model.ProxyMode
import com.github.yumelira.yumebox.data.repository.OverrideConfigRepository
import com.github.yumelira.yumebox.data.repository.ProvidersRepository
import com.github.yumelira.yumebox.domain.model.ErrorCategory
import com.github.yumelira.yumebox.domain.model.ErrorImpact
import com.github.yumelira.yumebox.domain.model.ErrorPhase
import com.github.yumelira.yumebox.domain.model.ErrorRetryability
import com.github.yumelira.yumebox.domain.model.ExplanationChain
import com.github.yumelira.yumebox.domain.model.ExplanationStep
import com.github.yumelira.yumebox.domain.model.HealthCheckItem
import com.github.yumelira.yumebox.domain.model.HealthCheckSeverity
import com.github.yumelira.yumebox.domain.model.HealthReport
import com.github.yumelira.yumebox.domain.model.MatcherType
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import com.github.yumelira.yumebox.domain.model.RuleMatcher
import com.github.yumelira.yumebox.domain.model.RuleSet
import com.github.yumelira.yumebox.domain.model.RuleSetOrigin
import com.github.yumelira.yumebox.domain.model.SnapshotType
import com.github.yumelira.yumebox.domain.model.SourceType
import com.github.yumelira.yumebox.domain.model.StructuredError
import com.github.yumelira.yumebox.domain.model.StructuredLogCollector
import com.github.yumelira.yumebox.domain.model.StructuredLogEntry
import com.github.yumelira.yumebox.domain.model.SubscriptionSource
import com.github.yumelira.yumebox.domain.model.SyncState
import com.github.yumelira.yumebox.domain.model.TracePhase
import com.github.yumelira.yumebox.domain.model.TraceStep
import com.github.yumelira.yumebox.domain.model.WorkspaceSnapshot
import com.github.yumelira.yumebox.presentation.util.ExternalResourceDiagnostics
import com.github.yumelira.yumebox.presentation.util.buildExternalResourceDiagnostics
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.runtime.client.RuntimeStateMapper
import com.github.yumelira.yumebox.runtime.client.root.RootTunController
import com.github.yumelira.yumebox.service.runtime.state.RuntimeOwner
import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import java.io.File
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.yaml.snakeyaml.Yaml

private enum class ConfigSourceKind {
    Runtime,
    Profile,
}

private data class EffectiveConfigSource(
    val sourceKind: ConfigSourceKind,
    val sourcePath: String,
    val yamlText: String,
    val configSizeBytes: Long,
    val configHash: String,
    val inspectedConfig: ConfigurationOverride?,
)

private data class DiagnosticDetailContext(
    val capturedAtMillis: Long,
    val profileId: String?,
    val profileName: String?,
    val runtimeSnapshot: RuntimeSnapshot,
    val recentFailures: List<StructuredLogEntry>,
    val recentEvents: List<StructuredLogEntry>,
    val providers: List<Provider>,
    val remoteOverrides: List<RemoteOverrideResource>,
    val externalResources: ExternalResourceDiagnostics,
    val runtimeOverride: ConfigurationOverride?,
    val configSource: EffectiveConfigSource?,
    val configSources: List<EffectiveConfigSource>,
)

class DiagnosticDetailRepository(
    private val context: Context,
    private val proxyFacade: ProxyFacade,
    private val structuredLogCollector: StructuredLogCollector,
    private val providersRepository: ProvidersRepository,
    private val overrideConfigRepository: OverrideConfigRepository,
    private val profilesRepository: ProfilesRepository,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
    }
    private val rawTraceJson = Json {
        encodeDefaults = true
        explicitNulls = false
    }
    private val snapshotStore = DiagnosticSnapshotStore(context)

    suspend fun loadRuntimeHealthDetail(): RuntimeHealthDetail = withContext(Dispatchers.IO) {
        runCatching {
                buildRuntimeHealthDetail(loadContext())
            }
            .getOrElse { error ->
                RuntimeHealthDetail(
                    generatedAtMillis = System.currentTimeMillis(),
                    structuredError =
                        StructuredError.fromThrowable(
                            throwable = error,
                            phase = ErrorPhase.Validating,
                            category = ErrorCategory.Runtime,
                            impact = ErrorImpact.Degraded,
                            retryability = ErrorRetryability.Retryable,
                            userVisibleMessage = DiagnosticLang.DetailPages.RuntimeHealth.Title,
                        ),
                )
            }
    }

    suspend fun loadRuleSetInspectorDetail(): RuleSetInspectorDetail =
        withContext(Dispatchers.IO) {
            runCatching {
                    buildRuleSetInspectorDetail(loadContext())
                }
                .getOrElse { error ->
                    RuleSetInspectorDetail(
                        generatedAtMillis = System.currentTimeMillis(),
                        sourceLabel = DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable,
                        structuredError =
                            StructuredError.fromThrowable(
                                throwable = error,
                                phase = ErrorPhase.Validating,
                                category = ErrorCategory.Configuration,
                                impact = ErrorImpact.FeatureUnavailable,
                                retryability = ErrorRetryability.RetryableAfterAction,
                                userVisibleMessage = DiagnosticLang.DetailPages.RuleSetInspector.Title,
                            ),
                    )
                }
        }

    suspend fun loadSnapshotHistoryDetail(): SnapshotHistoryDetail = withContext(Dispatchers.IO) {
        runCatching {
                buildSnapshotHistoryDetail(loadContext())
            }
            .getOrElse { error ->
                SnapshotHistoryDetail(
                    generatedAtMillis = System.currentTimeMillis(),
                    structuredError =
                        StructuredError.fromThrowable(
                            throwable = error,
                            phase = ErrorPhase.Validating,
                            category = ErrorCategory.Storage,
                            impact = ErrorImpact.Degraded,
                            retryability = ErrorRetryability.Retryable,
                            userVisibleMessage = DiagnosticLang.DetailPages.SnapshotHistory.Title,
                        ),
                )
            }
    }

    suspend fun loadExplanationChainDetail(): ExplanationChainDetail = withContext(Dispatchers.IO) {
        runCatching {
                buildExplanationChainDetail(loadContext())
            }
            .getOrElse { error ->
                ExplanationChainDetail(
                    generatedAtMillis = System.currentTimeMillis(),
                    structuredError =
                        StructuredError.fromThrowable(
                            throwable = error,
                            phase = ErrorPhase.Validating,
                            category = ErrorCategory.Runtime,
                            impact = ErrorImpact.Degraded,
                            retryability = ErrorRetryability.Retryable,
                            userVisibleMessage = DiagnosticLang.DetailPages.ExplanationChain.Title,
                        ),
                )
            }
    }

    suspend fun loadRawTraceDetail(): RawTraceDetail = withContext(Dispatchers.IO) {
        runCatching {
                val detailContext = loadContext()
                buildRawTraceDetail(
                    context = detailContext,
                    runtimeLogLines = loadRawRuntimeLogs(detailContext.runtimeSnapshot),
                )
            }
            .getOrElse { error ->
                RawTraceDetail(
                    generatedAtMillis = System.currentTimeMillis(),
                    structuredError =
                        StructuredError.fromThrowable(
                            throwable = error,
                            phase = ErrorPhase.Validating,
                            category = ErrorCategory.Runtime,
                            impact = ErrorImpact.Degraded,
                            retryability = ErrorRetryability.Retryable,
                            userVisibleMessage = DiagnosticLang.DetailPages.RawTrace.Title,
                        ),
                )
            }
    }

    suspend fun loadSourceRegistryOverviewDetail(): SourceRegistryOverviewDetail =
        withContext(Dispatchers.IO) {
            runCatching {
                    buildSourceRegistryOverviewDetail(loadContext())
                }
                .getOrElse { error ->
                    SourceRegistryOverviewDetail(
                        generatedAtMillis = System.currentTimeMillis(),
                        structuredError =
                            StructuredError.fromThrowable(
                                throwable = error,
                                phase = ErrorPhase.Validating,
                                category = ErrorCategory.Configuration,
                                impact = ErrorImpact.FeatureUnavailable,
                                retryability = ErrorRetryability.RetryableAfterAction,
                                userVisibleMessage = DiagnosticLang.DetailPages.SourceRegistry.Title,
                            ),
                    )
                }
        }

    private fun buildRuntimeHealthDetail(context: DiagnosticDetailContext): RuntimeHealthDetail {
        val structuredError = buildRuntimeStructuredError(context)
        val reportItems = buildRuntimeHealthItems(context, structuredError)
        val report =
            HealthReport(
                reportId = "runtime-health:${context.profileId ?: "none"}",
                generatedAtMillis = context.capturedAtMillis,
                overallSeverity = reportItems.highestSeverity(),
                items = reportItems,
                profileId = context.profileId,
                runtimePhase = context.runtimeSnapshot.phase.name,
                configVersion = context.runtimeSnapshot.effectiveFingerprint,
            )

        val detail = RuntimeHealthDetail(
            generatedAtMillis = context.capturedAtMillis,
            profileId = context.profileId,
            profileName = context.profileName,
            runtimeSnapshot = context.runtimeSnapshot,
            healthReport = report,
            structuredError = structuredError,
            recentFailures = context.recentFailures,
            externalResources = context.externalResources,
            sourcePath = context.configSource?.sourcePath,
        )
        return detail.copy(remediationPlan = buildRuntimeHealthRemediation(context, detail))
    }

    private fun buildRuleSetInspectorDetail(context: DiagnosticDetailContext): RuleSetInspectorDetail {
        if (context.profileId == null) {
            return RuleSetInspectorDetail(
                generatedAtMillis = context.capturedAtMillis,
                sourceLabel = DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable,
                structuredError =
                    StructuredError.configuration(
                        phase = ErrorPhase.Validating,
                        userVisibleMessage = DiagnosticLang.DetailPages.Common.NoActiveProfile,
                        impact = ErrorImpact.FeatureUnavailable,
                        retryability = ErrorRetryability.RetryableAfterAction,
                    ),
                overlayRuleCount = countOverlayEntries(context.runtimeOverride),
            )
        }

        val source = context.configSource
        if (source == null) {
            return RuleSetInspectorDetail(
                generatedAtMillis = context.capturedAtMillis,
                profileId = context.profileId,
                profileName = context.profileName,
                sourceLabel = DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable,
                structuredError =
                    StructuredError.configuration(
                        phase = ErrorPhase.Validating,
                        userVisibleMessage = DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable,
                        impact = ErrorImpact.FeatureUnavailable,
                        retryability = ErrorRetryability.RetryableAfterAction,
                    ),
                overlayRuleCount = countOverlayEntries(context.runtimeOverride),
            )
        }

        val ruleSets = buildRuleSets(context, source)
        val structuredError =
            if (source.inspectedConfig == null) {
                StructuredError.configuration(
                    phase = ErrorPhase.Validating,
                    userVisibleMessage = DiagnosticLang.DetailPages.RuleSetInspector.SourceUnavailable,
                    rawCause = source.sourcePath,
                    impact = ErrorImpact.FeatureUnavailable,
                    retryability = ErrorRetryability.RetryableAfterAction,
                )
            } else {
                null
            }

        val detail = RuleSetInspectorDetail(
            generatedAtMillis = context.capturedAtMillis,
            profileId = context.profileId,
            profileName = context.profileName,
            sourceLabel = source.sourceKind.toRuleSetSourceLabel(),
            sourcePath = source.sourcePath,
            configHash = source.configHash,
            ruleSets = ruleSets,
            structuredError = structuredError,
            overlayRuleCount = countOverlayEntries(context.runtimeOverride),
        )
        return detail.copy(remediationPlan = buildRuleSetInspectorRemediation(context, detail))
    }

    private fun buildSnapshotHistoryDetail(context: DiagnosticDetailContext): SnapshotHistoryDetail {
        val storedSnapshots = loadStoredSnapshots().sortedByDescending(WorkspaceSnapshot::createdAtMillis)
        val (currentSnapshot, updatedSnapshots) = captureCurrentSnapshot(context, storedSnapshots)
        val detail = SnapshotHistoryDetail(
            generatedAtMillis = context.capturedAtMillis,
            profileId = context.profileId,
            profileName = context.profileName,
            currentSnapshotId = currentSnapshot?.snapshotId,
            snapshots = updatedSnapshots,
            structuredError =
                if (updatedSnapshots.isEmpty() && context.profileId == null) {
                    StructuredError.configuration(
                        phase = ErrorPhase.Validating,
                        userVisibleMessage = DiagnosticLang.DetailPages.Common.NoActiveProfile,
                        impact = ErrorImpact.FeatureUnavailable,
                        retryability = ErrorRetryability.RetryableAfterAction,
                    )
                } else {
                    null
                },
        )
        return detail.copy(remediationPlan = buildSnapshotHistoryRemediation(context, detail))
    }

    private fun buildExplanationChainDetail(context: DiagnosticDetailContext): ExplanationChainDetail {
        val structuredError = buildRuntimeStructuredError(context)
        val detail = ExplanationChainDetail(
            generatedAtMillis = context.capturedAtMillis,
            profileId = context.profileId,
            profileName = context.profileName,
            chain = buildExplanationChain(context, structuredError),
            structuredError = structuredError,
            sourcePath = context.configSource?.sourcePath,
            configHash = context.configSource?.configHash ?: context.runtimeSnapshot.effectiveFingerprint,
            recentEvents = context.recentEvents.takeLast(MAX_EXPLANATION_EVENTS),
        )
        return detail.copy(remediationPlan = buildExplanationChainRemediation(context, detail))
    }

    private fun buildRawTraceDetail(
        context: DiagnosticDetailContext,
        runtimeLogLines: List<String>,
    ): RawTraceDetail {
        val structuredError = buildRuntimeStructuredError(context)
        val recentEvents = context.recentEvents.takeLast(MAX_TRACE_ENTRIES)
        val detail = RawTraceDetail(
            generatedAtMillis = context.capturedAtMillis,
            profileId = context.profileId,
            profileName = context.profileName,
            traceId =
                recentEvents.lastOrNull()?.correlationId?.takeIf(String::isNotBlank)
                    ?: "runtime-trace:${context.profileId ?: "none"}:${context.runtimeSnapshot.generation}",
            runtimePhase = context.runtimeSnapshot.phase,
            steps =
                if (recentEvents.isNotEmpty()) {
                    recentEvents.map { entry -> entry.toTraceStep() }
                } else {
                    buildSyntheticTraceSteps(context, structuredError)
                },
            rawSections = buildRawTraceSections(context, recentEvents, runtimeLogLines),
            recentEvents = recentEvents,
            structuredError = structuredError,
            configHash = context.configSource?.configHash ?: context.runtimeSnapshot.effectiveFingerprint,
        )
        return detail.copy(remediationPlan = buildRawTraceRemediation(context, detail))
    }

    private fun buildSourceRegistryOverviewDetail(
        context: DiagnosticDetailContext,
    ): SourceRegistryOverviewDetail {
        val items = buildSourceRegistryItems(context)
        val detail = SourceRegistryOverviewDetail(
            generatedAtMillis = context.capturedAtMillis,
            profileId = context.profileId,
            profileName = context.profileName,
            effectiveFingerprint =
                context.configSource?.configHash ?: context.runtimeSnapshot.effectiveFingerprint,
            items = items,
            structuredError =
                when {
                    items.isNotEmpty() -> null
                    context.profileId == null ->
                        StructuredError.configuration(
                            phase = ErrorPhase.Validating,
                            userVisibleMessage = DiagnosticLang.DetailPages.Common.NoActiveProfile,
                            impact = ErrorImpact.FeatureUnavailable,
                            retryability = ErrorRetryability.RetryableAfterAction,
                        )
                    else ->
                        StructuredError.configuration(
                            phase = ErrorPhase.Validating,
                            userVisibleMessage = DiagnosticLang.DetailPages.SourceRegistry.NoSources,
                            impact = ErrorImpact.FeatureUnavailable,
                            retryability = ErrorRetryability.RetryableAfterAction,
                        )
                },
        )
                return detail.copy(remediationPlan = buildSourceRegistryRemediation(context, detail))
    }

    private fun buildExplanationChain(
        context: DiagnosticDetailContext,
        structuredError: StructuredError?,
    ): ExplanationChain {
        val source = context.configSource
        val profileMatched = context.profileId != null
        val sourceMatched = source != null
        val resourceMatched =
            context.externalResources.subscriptionSources.isEmpty() ||
                (context.externalResources.staleCount == 0 &&
                    context.externalResources.pendingCount == 0)
        val runtimeMatched =
            structuredError == null &&
                context.runtimeSnapshot.phase == RuntimePhase.Running &&
                context.runtimeSnapshot.payloadReady &&
                context.runtimeSnapshot.transportReady

        val conclusion =
            when {
                structuredError != null -> structuredError.userVisibleMessage
                runtimeMatched -> DiagnosticLang.DetailPages.ExplanationChain.HealthyConclusion
                context.runtimeSnapshot.phase == RuntimePhase.Starting ->
                    DiagnosticLang.DetailPages.ExplanationChain.PreparingConclusion
                else -> DiagnosticLang.DetailPages.ExplanationChain.AttentionConclusion
            }

        val steps =
            buildList {
                add(
                    ExplanationStep(
                        stage = DiagnosticLang.DetailPages.ExplanationChain.Profile,
                        label = context.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
                        output = context.profileId,
                        matched = profileMatched,
                        detail = context.profileId,
                    )
                )
                add(
                    ExplanationStep(
                        stage = DiagnosticLang.DetailPages.ExplanationChain.ConfigSource,
                        label =
                            source?.sourceKind?.toExplanationSourceLabel()
                                ?: DiagnosticLang.DetailPages.Common.NotAvailable,
                        input = source?.sourcePath,
                        output = source?.configHash?.let(::shortFingerprint),
                        matched = sourceMatched,
                        detail = source?.sourcePath,
                    )
                )
                add(
                    ExplanationStep(
                        stage = DiagnosticLang.DetailPages.ExplanationChain.Resources,
                        label = buildResourceStatusLabel(context),
                        input = context.externalResources.subscriptionSources.size.toString(),
                        output = buildSourceRegistryOverviewLabel(context),
                        matched = resourceMatched,
                        detail = buildSourceRegistryOverviewLabel(context),
                    )
                )
                add(
                    ExplanationStep(
                        stage = DiagnosticLang.DetailPages.ExplanationChain.RuntimeState,
                        label = context.runtimeSnapshot.phase.toDisplayLabel(),
                        input = context.runtimeSnapshot.owner.toDisplayLabel(),
                        output = context.runtimeSnapshot.targetMode.toDisplayLabel(),
                        matched = runtimeMatched,
                        detail = buildRuntimePipelineSummary(context),
                    )
                )
                structuredError?.let { error ->
                    add(
                        ExplanationStep(
                            stage = DiagnosticLang.DetailPages.ExplanationChain.RootCause,
                            label = error.userVisibleMessage,
                            input = error.phase.name,
                            output = error.impact.name,
                            matched = false,
                            detail = error.technicalDetail ?: error.rawCause,
                        )
                    )
                }
                add(
                    ExplanationStep(
                        stage = DiagnosticLang.DetailPages.ExplanationChain.Conclusion,
                        label = conclusion,
                        matched = structuredError == null && runtimeMatched,
                        detail = buildExplanationConclusionDetail(context, structuredError),
                    )
                )
            }

        return ExplanationChain(
            chainId = buildExplanationChainId(context),
            steps = steps,
            conclusion = conclusion,
            isSuccess = steps.none { !it.matched },
        )
    }

    private fun buildRuntimeHealthRemediation(
        context: DiagnosticDetailContext,
        detail: RuntimeHealthDetail,
    ): DiagnosticRemediationPlan {
        val needsAttention =
            detail.structuredError != null ||
                detail.healthReport.overallSeverity >= HealthCheckSeverity.Warning
        val actions =
            buildList {
                if (context.profileId != null) {
                    when {
                        !RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) ->
                            add(startRuntimeAction(isPrimary = true))
                        detail.structuredError != null && context.runtimeSnapshot.phase == RuntimePhase.Failed ->
                            add(restartRuntimeAction(isPrimary = true))
                        needsAttention -> add(reloadRuntimeAction(isPrimary = true))
                    }
                }

                if (hasSourceAttention(detail.externalResources)) {
                    add(refreshSourcesAction(isPrimary = isEmpty()))
                }
                if (detail.structuredError != null || detail.recentFailures.isNotEmpty()) {
                    add(openExplanationChainAction())
                }
                if (detail.recentFailures.isNotEmpty()) {
                    add(openRawTraceAction())
                }
                if (detail.externalResources.subscriptionSources.isNotEmpty()) {
                    add(openSourceRegistryAction())
                }
                add(refreshPageAction())
            }
        return buildDiagnosticRemediationPlan(needsAttention = needsAttention, actions = actions)
    }

    private fun buildRuleSetInspectorRemediation(
        context: DiagnosticDetailContext,
        detail: RuleSetInspectorDetail,
    ): DiagnosticRemediationPlan {
        val needsAttention = detail.structuredError != null || detail.ruleSets.isEmpty()
        val actions =
            buildList {
                if (context.profileId != null) {
                    when {
                        !RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) && needsAttention ->
                            add(startRuntimeAction(isPrimary = true))
                        needsAttention -> add(reloadRuntimeAction(isPrimary = true))
                    }
                }

                if (hasSourceAttention(context.externalResources)) {
                    add(refreshSourcesAction(isPrimary = isEmpty()))
                }
                if (context.externalResources.subscriptionSources.isNotEmpty()) {
                    add(openSourceRegistryAction())
                }
                if (detail.structuredError != null) {
                    add(openExplanationChainAction())
                }
                add(refreshPageAction())
            }
        return buildDiagnosticRemediationPlan(needsAttention = needsAttention, actions = actions)
    }

    private fun buildSnapshotHistoryRemediation(
        context: DiagnosticDetailContext,
        detail: SnapshotHistoryDetail,
    ): DiagnosticRemediationPlan {
        val needsAttention = detail.structuredError != null || detail.snapshots.isEmpty()
        val actions =
            buildList {
                if (context.profileId != null) {
                    when {
                        !RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) && needsAttention ->
                            add(startRuntimeAction(isPrimary = true))
                        needsAttention && RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) ->
                            add(reloadRuntimeAction(isPrimary = true))
                    }
                }

                add(openRuntimeHealthAction(isPrimary = isEmpty()))
                if (context.recentFailures.isNotEmpty()) {
                    add(openRawTraceAction())
                }
                add(refreshPageAction())
            }
        return buildDiagnosticRemediationPlan(needsAttention = needsAttention, actions = actions)
    }

    private fun buildExplanationChainRemediation(
        context: DiagnosticDetailContext,
        detail: ExplanationChainDetail,
    ): DiagnosticRemediationPlan {
        val blockedStages = detail.chain.steps.count { !it.matched }
        val needsAttention = detail.structuredError != null || blockedStages > 0
        val actions =
            buildList {
                if (context.profileId != null) {
                    when {
                        !RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) && needsAttention ->
                            add(startRuntimeAction(isPrimary = true))
                        detail.structuredError != null && context.runtimeSnapshot.phase == RuntimePhase.Failed ->
                            add(restartRuntimeAction(isPrimary = true))
                        needsAttention -> add(reloadRuntimeAction(isPrimary = true))
                    }
                }

                if (hasSourceAttention(context.externalResources)) {
                    add(refreshSourcesAction(isPrimary = isEmpty()))
                }
                if (blockedStages > 0 && context.configSource != null) {
                    add(openRuleSetInspectorAction())
                }
                if (blockedStages > 0 && context.externalResources.subscriptionSources.isNotEmpty()) {
                    add(openSourceRegistryAction())
                }
                if (detail.recentEvents.isNotEmpty()) {
                    add(openRawTraceAction())
                }
                add(refreshPageAction())
            }
        return buildDiagnosticRemediationPlan(needsAttention = needsAttention, actions = actions)
    }

    private fun buildRawTraceRemediation(
        context: DiagnosticDetailContext,
        detail: RawTraceDetail,
    ): DiagnosticRemediationPlan {
        val hasFailures = detail.structuredError != null || detail.steps.any { it.phase == TracePhase.Failed }
        val actions =
            buildList {
                if (context.profileId != null) {
                    when {
                        !RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) && hasFailures ->
                            add(startRuntimeAction(isPrimary = true))
                        detail.structuredError != null && context.runtimeSnapshot.phase == RuntimePhase.Failed ->
                            add(restartRuntimeAction(isPrimary = true))
                        hasFailures -> add(reloadRuntimeAction(isPrimary = true))
                    }
                }

                add(copyRawTraceAction(isPrimary = isEmpty()))
                if (detail.recentEvents.isNotEmpty()) {
                    add(openLogsAction())
                }
                add(openExplanationChainAction())
                add(refreshPageAction())
            }
        return buildDiagnosticRemediationPlan(needsAttention = hasFailures, actions = actions)
    }

    private fun buildSourceRegistryRemediation(
        context: DiagnosticDetailContext,
        detail: SourceRegistryOverviewDetail,
    ): DiagnosticRemediationPlan {
        val needsAttention = detail.structuredError != null || detail.items.any { it.source.needsSync }
        val actions =
            buildList {
                if (detail.items.any { it.source.needsSync || it.source.syncState == SyncState.Failed }) {
                    add(refreshSourcesAction(isPrimary = true))
                }

                if (context.profileId != null) {
                    when {
                        !RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) && needsAttention ->
                            add(startRuntimeAction(isPrimary = isEmpty()))
                        RuntimeStateMapper.isActuallyRunning(context.runtimeSnapshot) ->
                            add(reloadRuntimeAction(isPrimary = isEmpty()))
                    }
                }

                if (
                    detail.items.any {
                        it.role == SourceRegistryRole.RuleProvider ||
                            it.role == SourceRegistryRole.ProxyProvider ||
                            it.role == SourceRegistryRole.RemoteOverride
                    }
                ) {
                    add(openProvidersAction())
                }
                if (context.configSource != null) {
                    add(openRuleSetInspectorAction())
                }
                add(refreshPageAction())
            }
        return buildDiagnosticRemediationPlan(needsAttention = needsAttention, actions = actions)
    }

    private suspend fun loadContext(): DiagnosticDetailContext {
        val capturedAtMillis = System.currentTimeMillis()
        val runtimeSnapshot = proxyFacade.runtimeSnapshot.value
        val activeProfile = runCatching { profilesRepository.queryActiveProfile(ensureDefault = false) }.getOrNull()
        val profileId = activeProfile?.uuid?.toString() ?: runtimeSnapshot.profileUuid?.takeIf(String::isNotBlank)
        val profileName =
            activeProfile?.name?.takeIf(String::isNotBlank)
                ?: runtimeSnapshot.profileName?.takeIf(String::isNotBlank)
        val structuredEvents = structuredLogCollector.snapshot()
        val recentEvents = structuredEvents.takeLast(MAX_TRACE_ENTRIES)

        val providers = providersRepository.queryProviders().getOrDefault(emptyList())
        val remoteOverrides = runCatching { overrideConfigRepository.listRemoteResources() }.getOrDefault(emptyList())
        val externalResources = buildExternalResourceDiagnostics(providers, remoteOverrides)
        val runtimeOverride =
            profileId
                ?.let(::runtimeOverrideId)
                ?.let { overrideConfigRepository.getById(it) }
                ?.config
        val configSources = resolveConfigSources(profileId = profileId, runtimeSnapshot = runtimeSnapshot)

        return DiagnosticDetailContext(
            capturedAtMillis = capturedAtMillis,
            profileId = profileId,
            profileName = profileName,
            runtimeSnapshot = runtimeSnapshot,
            recentFailures = structuredEvents.filter { it.isFailure }.takeLast(8),
            recentEvents = recentEvents,
            providers = providers,
            remoteOverrides = remoteOverrides,
            externalResources = externalResources,
            runtimeOverride = runtimeOverride,
            configSource = configSources.firstOrNull(),
            configSources = configSources,
        )
    }

    private fun buildRuntimeHealthItems(
        context: DiagnosticDetailContext,
        structuredError: StructuredError?,
    ): List<HealthCheckItem> {
        val runtime = context.runtimeSnapshot
        val payloadChannelsReady = listOf(runtime.profileReady, runtime.groupsReady, runtime.trafficReady).count { it }
        val sourceCount = context.externalResources.subscriptionSources.size
        val sourceDetail =
            when {
                sourceCount == 0 -> DiagnosticLang.DetailPages.RuntimeHealth.SourcesEmpty
                context.externalResources.staleCount > 0 || context.externalResources.pendingCount > 0 ->
                    DiagnosticLang.DetailPages.RuntimeHealth.SourcesAttention.format(
                        context.externalResources.staleCount,
                        context.externalResources.pendingCount,
                    )
                else -> DiagnosticLang.DetailPages.RuntimeHealth.SourcesHealthy.format(sourceCount)
            }

        val lifecycleDetail =
            when (runtime.phase) {
                RuntimePhase.Running -> {
                    if (runtime.payloadReady && runtime.transportReady) {
                        DiagnosticLang.DetailPages.RuntimeHealth.RunningHealthy
                    } else {
                        DiagnosticLang.DetailPages.RuntimeHealth.RunningDegraded
                    }
                }
                RuntimePhase.Starting -> DiagnosticLang.DetailPages.RuntimeHealth.Starting
                RuntimePhase.Stopping -> DiagnosticLang.DetailPages.RuntimeHealth.Stopping
                RuntimePhase.Failed -> DiagnosticLang.DetailPages.RuntimeHealth.Failed
                RuntimePhase.Idle -> DiagnosticLang.DetailPages.RuntimeHealth.Idle
            }

        return listOf(
            HealthCheckItem(
                checkId = "active-profile",
                label = DiagnosticLang.DetailPages.RuntimeHealth.ActiveProfile,
                severity = if (context.profileId == null) HealthCheckSeverity.Warning else HealthCheckSeverity.Ok,
                detail = context.profileName ?: DiagnosticLang.DetailPages.Common.NoActiveProfile,
                category = "profile",
            ),
            HealthCheckItem(
                checkId = "runtime-lifecycle",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Lifecycle,
                severity =
                    when (runtime.phase) {
                        RuntimePhase.Running -> {
                            if (runtime.payloadReady && runtime.transportReady) {
                                HealthCheckSeverity.Ok
                            } else {
                                HealthCheckSeverity.Warning
                            }
                        }
                        RuntimePhase.Starting,
                        RuntimePhase.Stopping,
                        RuntimePhase.Idle -> HealthCheckSeverity.Info
                        RuntimePhase.Failed -> HealthCheckSeverity.Error
                    },
                detail = lifecycleDetail,
                category = "runtime",
            ),
            HealthCheckItem(
                checkId = "payload-readiness",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Payload,
                severity =
                    when {
                        runtime.payloadReady -> HealthCheckSeverity.Ok
                        runtime.phase == RuntimePhase.Running || runtime.phase == RuntimePhase.Starting ->
                            HealthCheckSeverity.Warning
                        else -> HealthCheckSeverity.Info
                    },
                detail =
                    DiagnosticLang.DetailPages.RuntimeHealth.PayloadReadyFormat.format(
                        payloadChannelsReady,
                        3,
                    ),
                category = "payload",
            ),
            HealthCheckItem(
                checkId = "transport-path",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Transport,
                severity =
                    when {
                        runtime.transportReady -> HealthCheckSeverity.Ok
                        runtime.phase == RuntimePhase.Running -> HealthCheckSeverity.Warning
                        else -> HealthCheckSeverity.Info
                    },
                detail = runtime.owner.toDisplayLabel(),
                category = "transport",
            ),
            HealthCheckItem(
                checkId = "config-pipeline",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Config,
                severity =
                    when {
                        runtime.configReady && context.configSource != null -> HealthCheckSeverity.Ok
                        context.configSource != null -> HealthCheckSeverity.Warning
                        else -> HealthCheckSeverity.Info
                    },
                detail =
                    if (context.configSource != null) {
                        DiagnosticLang.DetailPages.RuntimeHealth.ConfigReady
                    } else {
                        DiagnosticLang.DetailPages.RuntimeHealth.ConfigMissing
                    },
                category = "config",
            ),
            HealthCheckItem(
                checkId = "log-pipeline",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Logs,
                severity =
                    when {
                        runtime.logReady -> HealthCheckSeverity.Ok
                        runtime.phase == RuntimePhase.Running -> HealthCheckSeverity.Warning
                        else -> HealthCheckSeverity.Info
                    },
                detail =
                    if (runtime.logReady) {
                        DiagnosticLang.DetailPages.RuntimeHealth.LogReady
                    } else {
                        DiagnosticLang.DetailPages.RuntimeHealth.LogWaiting
                    },
                category = "logs",
            ),
            HealthCheckItem(
                checkId = "source-freshness",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Sources,
                severity =
                    when {
                        sourceCount == 0 -> HealthCheckSeverity.Info
                        context.externalResources.staleCount > 0 -> HealthCheckSeverity.Warning
                        context.externalResources.pendingCount > 0 -> HealthCheckSeverity.Info
                        else -> HealthCheckSeverity.Ok
                    },
                detail = sourceDetail,
                category = "sources",
            ),
            HealthCheckItem(
                checkId = "recent-failures",
                label = DiagnosticLang.DetailPages.RuntimeHealth.Failures,
                severity =
                    when {
                        structuredError?.impact == ErrorImpact.DataLoss -> HealthCheckSeverity.Critical
                        structuredError?.impact == ErrorImpact.ServiceDown -> HealthCheckSeverity.Error
                        structuredError != null -> HealthCheckSeverity.Warning
                        context.recentFailures.isNotEmpty() -> HealthCheckSeverity.Warning
                        else -> HealthCheckSeverity.Ok
                    },
                detail =
                    if (context.recentFailures.isEmpty()) {
                        DiagnosticLang.DetailPages.RuntimeHealth.FailuresClear
                    } else {
                        DiagnosticLang.DetailPages.RuntimeHealth.FailuresAttention.format(
                            context.recentFailures.size
                        )
                    },
                category = "failures",
            ),
        )
    }

    private fun buildRuntimeStructuredError(context: DiagnosticDetailContext): StructuredError? {
        context.runtimeSnapshot.lastStructuredError?.let { return it }

        val fallbackMessage =
            context.runtimeSnapshot.lastError?.takeIf(String::isNotBlank)
                ?: context.recentFailures.lastOrNull()?.message?.takeIf(String::isNotBlank)
                ?: return null

        return StructuredError.runtime(
            phase = context.runtimeSnapshot.phase.toErrorPhase(),
            userVisibleMessage = fallbackMessage,
            rawCause = context.recentFailures.lastOrNull()?.detail ?: context.runtimeSnapshot.lastError,
            impact =
                if (context.runtimeSnapshot.phase == RuntimePhase.Failed) {
                    ErrorImpact.ServiceDown
                } else {
                    ErrorImpact.Degraded
                },
            retryability = ErrorRetryability.RetryableAfterAction,
        )
    }

    private fun buildRuleSets(
        context: DiagnosticDetailContext,
        source: EffectiveConfigSource,
    ): List<RuleSet> {
        val profileId = context.profileId ?: return emptyList()
        val inspectedConfig = source.inspectedConfig ?: return emptyList()
        val generatedAtMillis = context.capturedAtMillis
        val allRules =
            buildList {
                addAll(inspectedConfig.rules.orEmpty())
                addAll(inspectedConfig.rulesStart.orEmpty())
                addAll(inspectedConfig.rulesEnd.orEmpty())
            }
        val parsedRules = allRules.map(RuleMatcher::parse)
        val providerRulesByName =
            parsedRules
                .filter { it.type == MatcherType.RuleSet && it.payload.isNotBlank() }
                .groupBy(RuleMatcher::payload)
        val providerDefinitions = inspectedConfig.ruleProviders.orEmpty()
        val providersByName =
            context.providers.filter { it.type == Provider.Type.Rule }.associateBy(Provider::name)

        val providerSets =
            (providerDefinitions.keys + providerRulesByName.keys)
                .filter(String::isNotBlank)
                .distinct()
                .sorted()
                .map { providerName ->
                    val definition = providerDefinitions[providerName].orEmpty()
                    RuleSet(
                        ruleSetId = "provider:$profileId:$providerName",
                        name = providerName,
                        origin = RuleSetOrigin.Provider,
                        matchers = providerRulesByName[providerName].orEmpty(),
                        profileId = profileId,
                        providerName = providerName,
                        providerUrl = definition.stringValue("url") ?: definition.stringValue("path"),
                        providerBehavior = definition.stringValue("behavior"),
                        updatedAtMillis =
                            providersByName[providerName]?.updatedAt?.takeIf { it > 0L }
                                ?: generatedAtMillis,
                    )
                }

        val subRuleSets =
            inspectedConfig.subRules.orEmpty().toSortedMap().map { (name, rawRules) ->
                RuleSet(
                    ruleSetId = "subrule:$profileId:$name",
                    name = name,
                    origin = RuleSetOrigin.SubRule,
                    matchers = rawRules.map(RuleMatcher::parse),
                    profileId = profileId,
                    updatedAtMillis = generatedAtMillis,
                )
            }

        val inlineMatchers = parsedRules.filterNot { it.type == MatcherType.RuleSet }
        val inlineSets =
            if (inlineMatchers.isEmpty()) {
                emptyList()
            } else {
                listOf(
                    RuleSet(
                        ruleSetId = "inline:$profileId",
                        name = DiagnosticLang.DetailPages.RuleSetInspector.Inline,
                        origin = RuleSetOrigin.Inline,
                        matchers = inlineMatchers,
                        profileId = profileId,
                        updatedAtMillis = generatedAtMillis,
                    )
                )
            }

        return (providerSets + subRuleSets + inlineSets).sortedWith(
            compareBy<RuleSet> { it.origin.sortOrder() }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )
    }

    private fun captureCurrentSnapshot(
        context: DiagnosticDetailContext,
        snapshots: List<WorkspaceSnapshot>,
    ): Pair<WorkspaceSnapshot?, List<WorkspaceSnapshot>> {
        val source = context.configSource ?: return null to snapshots
        val profileId = context.profileId ?: return null to snapshots
        val existing = snapshots.firstOrNull { it.profileId == profileId && it.configHash == source.configHash }
        if (existing != null) {
            return existing to snapshots
        }

        val currentSnapshot =
            WorkspaceSnapshot(
                snapshotId = buildSnapshotId(profileId, source.configHash, context.capturedAtMillis),
                profileId = profileId,
                snapshotType = SnapshotType.AutoSave,
                createdAtMillis = context.runtimeSnapshot.startedAt ?: context.capturedAtMillis,
                label =
                    DiagnosticLang.DetailPages.SnapshotHistory.CaptureLabel.format(
                        context.profileName ?: DiagnosticLang.DetailPages.Common.UnnamedProfile,
                        context.runtimeSnapshot.phase.toDisplayLabel(),
                    ),
                configHash = source.configHash,
                configSizeBytes = source.configSizeBytes,
                parentSnapshotId = snapshots.firstOrNull { it.profileId == profileId }?.snapshotId,
                metadata =
                    buildMap {
                        put("phase", context.runtimeSnapshot.phase.name)
                        put("owner", context.runtimeSnapshot.owner.name)
                        put("mode", context.runtimeSnapshot.targetMode.name)
                        put("generation", context.runtimeSnapshot.generation.toString())
                        put("payload_ready", context.runtimeSnapshot.payloadReady.toString())
                        put("source_kind", source.sourceKind.name)
                        put("source_path", source.sourcePath)
                    },
            )

        val updatedSnapshots =
            (listOf(currentSnapshot) + snapshots)
                .sortedByDescending(WorkspaceSnapshot::createdAtMillis)
                .take(MAX_SNAPSHOTS)

        persistSnapshots(updatedSnapshots)
        return currentSnapshot to updatedSnapshots
    }

    private suspend fun resolveConfigSources(
        profileId: String?,
        runtimeSnapshot: RuntimeSnapshot,
    ): List<EffectiveConfigSource> {
        val runtimeConfigPath =
            runCatching {
                    ServiceClient.connect(context)
                    ServiceClient.clash().queryConfiguration().configPath?.trim().orEmpty()
                }
                .getOrDefault("")

        val candidateFiles =
            buildList {
                runtimeConfigPath.takeIf(String::isNotBlank)?.let { add(File(it)) }
                if (!profileId.isNullOrBlank()) {
                    add(File(context.filesDir, "imported/$profileId/runtime.yaml"))
                    add(File(context.filesDir, "clash/profiles/$profileId/runtime.yaml"))
                    add(File(context.filesDir, "imported/$profileId/config.yaml"))
                    add(File(context.filesDir, "clash/profiles/$profileId/config.yaml"))
                }
            }.distinctBy { it.absolutePath }

        return candidateFiles.mapNotNull { selectedFile ->
            if (!selectedFile.exists() || !selectedFile.isFile) {
                return@mapNotNull null
            }
            val yamlText =
                runCatching { selectedFile.readText() }
                    .getOrNull()
                    ?.takeIf(String::isNotBlank)
                    ?: return@mapNotNull null
            val sourceKind =
                when {
                    runtimeConfigPath.isNotBlank() &&
                        selectedFile.absolutePath == File(runtimeConfigPath).absolutePath -> ConfigSourceKind.Runtime
                    selectedFile.name.equals("runtime.yaml", ignoreCase = true) -> ConfigSourceKind.Runtime
                    else -> ConfigSourceKind.Profile
                }
            val effectiveFingerprint =
                runtimeSnapshot.effectiveFingerprint
                    ?.takeIf(String::isNotBlank)
                    ?.takeIf { sourceKind == ConfigSourceKind.Runtime }

            EffectiveConfigSource(
                sourceKind = sourceKind,
                sourcePath = selectedFile.absolutePath,
                yamlText = yamlText,
                configSizeBytes = selectedFile.length(),
                configHash = effectiveFingerprint ?: sha256(yamlText),
                inspectedConfig = Clash.inspectCompiledConfig(yamlText) ?: parseProfileConfigYaml(yamlText),
            )
        }
    }

    private suspend fun loadRawRuntimeLogs(runtimeSnapshot: RuntimeSnapshot): List<String> {
        if (runtimeSnapshot.owner != RuntimeOwner.RootTun) {
            return emptyList()
        }
        return runCatching { RootTunController.queryRecentLogs(context, 0L).items.takeLast(MAX_RAW_TRACE_LINES) }
            .getOrDefault(emptyList())
    }

    private fun buildRawTraceSections(
        context: DiagnosticDetailContext,
        recentEvents: List<StructuredLogEntry>,
        runtimeLogLines: List<String>,
    ): List<RawTraceSection> {
        return buildList {
            add(
                RawTraceSection(
                    sectionId = "runtime-snapshot",
                    title = DiagnosticLang.DetailPages.RawTrace.RuntimeSnapshot,
                    lines = listOf(rawTraceJson.encodeToString(RuntimeSnapshot.serializer(), context.runtimeSnapshot)),
                )
            )
            if (recentEvents.isNotEmpty()) {
                add(
                    RawTraceSection(
                        sectionId = "structured-events",
                        title = DiagnosticLang.DetailPages.RawTrace.StructuredEvents,
                        lines = recentEvents.map(::encodeStructuredEvent),
                    )
                )
            }
            if (runtimeLogLines.isNotEmpty()) {
                add(
                    RawTraceSection(
                        sectionId = "runtime-buffer",
                        title = DiagnosticLang.DetailPages.RawTrace.RuntimeBuffer,
                        lines = runtimeLogLines.takeLast(MAX_RAW_TRACE_LINES),
                    )
                )
            }
        }
    }

    private fun buildSyntheticTraceSteps(
        context: DiagnosticDetailContext,
        structuredError: StructuredError?,
    ): List<TraceStep> {
        return buildList {
            context.configSource?.let { source ->
                add(
                    TraceStep(
                        phase = TracePhase.PolicySelect,
                        label = source.sourceKind.toRuleSetSourceLabel(),
                        detail = source.sourcePath,
                    )
                )
            }
            add(
                TraceStep(
                    phase =
                        when {
                            structuredError != null -> TracePhase.Failed
                            context.runtimeSnapshot.phase == RuntimePhase.Running -> TracePhase.Complete
                            else -> TracePhase.Transport
                        },
                    label = context.runtimeSnapshot.phase.toDisplayLabel(),
                    detail = buildRuntimePipelineSummary(context),
                )
            )
            structuredError?.let { error ->
                add(
                    TraceStep(
                        phase = TracePhase.Failed,
                        label = error.userVisibleMessage,
                        detail = error.technicalDetail ?: error.rawCause,
                    )
                )
            }
        }
    }

    private fun buildSourceRegistryItems(context: DiagnosticDetailContext): List<SourceRegistryItem> {
        val remoteOverridesById = context.remoteOverrides.associateBy { "override:${it.id}" }
        val configItems = context.configSources.map { source -> buildConfigSourceRegistryItem(context, source) }
        val externalItems =
            context.externalResources.subscriptionSources.map { source ->
                val role =
                    when {
                        source.sourceId.startsWith("override:") -> SourceRegistryRole.RemoteOverride
                        source.sourceType == SourceType.RuleProvider -> SourceRegistryRole.RuleProvider
                        source.sourceType == SourceType.ProxyProvider -> SourceRegistryRole.ProxyProvider
                        else -> SourceRegistryRole.RemoteOverride
                    }

                SourceRegistryItem(
                    registryId = source.sourceId,
                    role = role,
                    source = source,
                    fingerprint = source.contentHash,
                    itemCount = remoteOverridesById[source.sourceId]?.ruleCount,
                )
            }

        return (configItems + externalItems).sortedWith(
            compareBy<SourceRegistryItem> { if (it.isEffective) 0 else 1 }
                .thenBy { it.role.sortOrder() }
                .thenBy { if (it.source.syncState == SyncState.Stale || it.source.syncState == SyncState.Failed) 0 else 1 }
                .thenByDescending { it.source.updatedAtMillis }
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.source.name }
        )
    }

    private fun buildConfigSourceRegistryItem(
        context: DiagnosticDetailContext,
        source: EffectiveConfigSource,
    ): SourceRegistryItem {
        val file = File(source.sourcePath)
        val updatedAtMillis = file.lastModified().takeIf { it > 0L } ?: context.capturedAtMillis
        val subscriptionSource =
            SubscriptionSource(
                sourceId = "config:${source.sourceKind.name.lowercase()}:${shortFingerprint(source.configHash)}",
                name = file.name,
                sourceType = SourceType.LocalFile,
                localPath = source.sourcePath,
                syncState = SyncState.Succeeded,
                profileId = context.profileId,
                contentHash = source.configHash,
                contentSizeBytes = source.configSizeBytes,
                createdAtMillis = updatedAtMillis,
                updatedAtMillis = updatedAtMillis,
            )

        return SourceRegistryItem(
            registryId = subscriptionSource.sourceId,
            role = source.sourceKind.toRegistryRole(),
            source = subscriptionSource,
            isEffective = context.configSource?.sourcePath == source.sourcePath,
            fingerprint = source.configHash,
            itemCount = source.inspectedConfig?.let(::countOverlayEntries),
            note = source.sourceKind.toRegistryRoleLabel(),
        )
    }

    private fun buildResourceStatusLabel(context: DiagnosticDetailContext): String {
        val sourceCount = context.externalResources.subscriptionSources.size
        return when {
            sourceCount == 0 -> DiagnosticLang.DetailPages.RuntimeHealth.SourcesEmpty
            context.externalResources.staleCount > 0 || context.externalResources.pendingCount > 0 ->
                DiagnosticLang.DetailPages.RuntimeHealth.SourcesAttention.format(
                    context.externalResources.staleCount,
                    context.externalResources.pendingCount,
                )
            else -> DiagnosticLang.DetailPages.RuntimeHealth.SourcesHealthy.format(sourceCount)
        }
    }

    private fun buildSourceRegistryOverviewLabel(context: DiagnosticDetailContext): String {
        val total = context.configSources.size + context.externalResources.subscriptionSources.size
        val remote = context.externalResources.subscriptionSources.count { it.isRemote }
        return DiagnosticLang.DetailPages.SourceRegistry.RegistryOverview.format(total, remote)
    }

    private fun buildRuntimePipelineSummary(context: DiagnosticDetailContext): String {
        val payloadReady =
            listOf(
                context.runtimeSnapshot.profileReady,
                context.runtimeSnapshot.groupsReady,
                context.runtimeSnapshot.trafficReady,
            ).count { it }
        return listOf(
                context.runtimeSnapshot.owner.toDisplayLabel(),
                context.runtimeSnapshot.targetMode.toDisplayLabel(),
                DiagnosticLang.DetailPages.RuntimeHealth.PayloadReadyFormat.format(payloadReady, 3),
            )
            .joinToString(separator = " · ")
    }

    private fun buildExplanationConclusionDetail(
        context: DiagnosticDetailContext,
        structuredError: StructuredError?,
    ): String {
        return structuredError?.technicalDetail
            ?: structuredError?.rawCause
            ?: listOfNotNull(
                    context.configSource?.sourcePath,
                    context.runtimeSnapshot.effectiveFingerprint?.let(::shortFingerprint),
                )
                .joinToString(separator = " · ")
                .ifBlank { buildRuntimePipelineSummary(context) }
    }

    private fun buildExplanationChainId(context: DiagnosticDetailContext): String {
        return "runtime-explanation:${context.profileId ?: "none"}:${context.capturedAtMillis}"
    }

    private fun encodeStructuredEvent(entry: StructuredLogEntry): String {
        return rawTraceJson.encodeToString(StructuredLogEntry.serializer(), entry)
    }

    private fun StructuredLogEntry.toTraceStep(): TraceStep {
        val detailText =
            listOfNotNull(
                    phase?.takeIf { it.isNotBlank() && !it.equals(action, ignoreCase = true) },
                    status.takeIf { !it.equals("ok", ignoreCase = true) },
                    message.takeIf { !it.equals(action, ignoreCase = true) },
                    detail?.takeIf { it.isNotBlank() },
                    errorCategory?.takeIf { it.isNotBlank() },
                    objectId?.takeIf { it.isNotBlank() },
                    configVersion?.takeIf { it.isNotBlank() }?.let { "config=$it" },
                )
                .distinct()
                .joinToString(separator = " · ")
                .takeIf(String::isNotBlank)

        return TraceStep(
            phase = toTracePhase(),
            label = action,
            detail = detailText,
        )
    }

    private fun StructuredLogEntry.toTracePhase(): TracePhase {
        return when {
            isFailure -> TracePhase.Failed
            phase?.contains("dns", ignoreCase = true) == true || action.contains("dns", ignoreCase = true) ->
                TracePhase.DnsResolve
            phase?.contains("rule", ignoreCase = true) == true || action.contains("rule", ignoreCase = true) ->
                TracePhase.RuleMatch
            phase?.contains("policy", ignoreCase = true) == true || action.contains("policy", ignoreCase = true) ->
                TracePhase.PolicySelect
            action.contains("outbound", ignoreCase = true) || message.contains("outbound", ignoreCase = true) ->
                TracePhase.OutboundConnect
            status.equals("complete", ignoreCase = true) ||
                status.equals("completed", ignoreCase = true) ||
                status.equals("success", ignoreCase = true) -> TracePhase.Complete
            else -> TracePhase.Transport
        }
    }

    private fun parseProfileConfigYaml(yamlText: String): ConfigurationOverride? {
        if (yamlText.isBlank()) {
            return ConfigurationOverride()
        }

        val loaded = runCatching { Yaml().load<Any?>(yamlText) }.getOrNull() ?: return null
        val rootElement = yamlValueToJsonElement(loaded)
        val rootObject = rootElement as? JsonObject ?: JsonObject(emptyMap())
        return runCatching {
                json.decodeFromJsonElement(ConfigurationOverride.serializer(), rootObject)
            }
            .getOrNull()
    }

    private fun yamlValueToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is Map<*, *> -> {
                val content =
                    buildMap<String, JsonElement> {
                        value.forEach { (key, childValue) ->
                            key?.toString()?.let { put(it, yamlValueToJsonElement(childValue)) }
                        }
                    }
                JsonObject(content)
            }
            is List<*> -> JsonArray(value.map(::yamlValueToJsonElement))
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value.toInt())
            is Byte -> JsonPrimitive(value.toInt())
            else -> JsonPrimitive(value.toString())
        }
    }

    private fun loadStoredSnapshots(): List<WorkspaceSnapshot> {
        return snapshotStore.load()
    }

    private fun persistSnapshots(snapshots: List<WorkspaceSnapshot>) {
        snapshotStore.save(snapshots)
    }

    private fun countOverlayEntries(config: ConfigurationOverride?): Int {
        if (config == null) {
            return 0
        }
        return config.rules.orEmpty().size +
            config.rulesStart.orEmpty().size +
            config.rulesEnd.orEmpty().size +
            config.subRules.orEmpty().values.sumOf(List<String>::size) +
            config.ruleProviders.orEmpty().size
    }

    private fun sha256(content: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(content.toByteArray())
            .joinToString(separator = "") { "%02x".format(it) }
    }

    private fun shortFingerprint(hash: String): String {
        return if (hash.length <= 10) hash else hash.take(10)
    }

    private fun buildSnapshotId(profileId: String, configHash: String, capturedAtMillis: Long): String {
        return "snapshot-${profileId.takeLast(8)}-${configHash.take(10)}-$capturedAtMillis"
    }

    private fun runtimeOverrideId(profileId: String): String {
        return "${OverrideConfigRepository.INTERNAL_RUNTIME_PREFIX}-profile-$profileId"
    }

    private fun Map<String, JsonElement>.stringValue(key: String): String? {
        return this[key]?.jsonPrimitive?.contentOrNull?.takeIf(String::isNotBlank)
    }

    private fun hasSourceAttention(externalResources: ExternalResourceDiagnostics): Boolean {
        return externalResources.staleCount > 0 || externalResources.pendingCount > 0
    }

    private fun List<HealthCheckItem>.highestSeverity(): HealthCheckSeverity {
        return maxByOrNull(HealthCheckItem::severity)?.severity ?: HealthCheckSeverity.Ok
    }

    private fun RuntimePhase.toErrorPhase(): ErrorPhase {
        return when (this) {
            RuntimePhase.Idle,
            RuntimePhase.Starting -> ErrorPhase.Preparing
            RuntimePhase.Running -> ErrorPhase.Running
            RuntimePhase.Stopping -> ErrorPhase.Stopping
            RuntimePhase.Failed -> ErrorPhase.Running
        }
    }

    private fun RuntimePhase.toDisplayLabel(): String {
        return when (this) {
            RuntimePhase.Idle -> DiagnosticLang.DetailPages.RuntimeHealth.Idle
            RuntimePhase.Starting -> DiagnosticLang.DetailPages.RuntimeHealth.Starting
            RuntimePhase.Running -> DiagnosticLang.Phase.Running
            RuntimePhase.Stopping -> DiagnosticLang.DetailPages.RuntimeHealth.Stopping
            RuntimePhase.Failed -> DiagnosticLang.DetailPages.RuntimeHealth.Failed
        }
    }

    private fun RuntimeOwner.toDisplayLabel(): String {
        return when (this) {
            RuntimeOwner.None -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerNone
            RuntimeOwner.LocalTun -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerLocalTun
            RuntimeOwner.LocalHttp -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerLocalHttp
            RuntimeOwner.RootTun -> DiagnosticLang.DetailPages.RuntimeHealth.OwnerRootTun
        }
    }

    private fun ProxyMode.toDisplayLabel(): String {
        return when (this) {
            ProxyMode.Tun -> DiagnosticLang.DetailPages.RuntimeHealth.ModeTun
            ProxyMode.RootTun -> DiagnosticLang.DetailPages.RuntimeHealth.ModeRootTun
            ProxyMode.Http -> DiagnosticLang.DetailPages.RuntimeHealth.ModeHttp
        }
    }

    private fun ConfigSourceKind.toRuleSetSourceLabel(): String {
        return when (this) {
            ConfigSourceKind.Runtime -> DiagnosticLang.DetailPages.RuleSetInspector.SourceRuntime
            ConfigSourceKind.Profile -> DiagnosticLang.DetailPages.RuleSetInspector.SourceProfile
        }
    }

    private fun ConfigSourceKind.toExplanationSourceLabel(): String {
        return when (this) {
            ConfigSourceKind.Runtime -> DiagnosticLang.DetailPages.SourceRegistry.RuntimeConfig
            ConfigSourceKind.Profile -> DiagnosticLang.DetailPages.SourceRegistry.ProfileConfig
        }
    }

    private fun ConfigSourceKind.toRegistryRole(): SourceRegistryRole {
        return when (this) {
            ConfigSourceKind.Runtime -> SourceRegistryRole.RuntimeConfig
            ConfigSourceKind.Profile -> SourceRegistryRole.ProfileConfig
        }
    }

    private fun ConfigSourceKind.toRegistryRoleLabel(): String {
        return when (this) {
            ConfigSourceKind.Runtime -> DiagnosticLang.DetailPages.SourceRegistry.RuntimeConfig
            ConfigSourceKind.Profile -> DiagnosticLang.DetailPages.SourceRegistry.ProfileConfig
        }
    }

    private fun RuleSetOrigin.sortOrder(): Int {
        return when (this) {
            RuleSetOrigin.Provider -> 0
            RuleSetOrigin.SubRule -> 1
            RuleSetOrigin.Override -> 2
            RuleSetOrigin.Inline -> 3
        }
    }

    private fun SourceRegistryRole.sortOrder(): Int {
        return when (this) {
            SourceRegistryRole.RuntimeConfig -> 0
            SourceRegistryRole.ProfileConfig -> 1
            SourceRegistryRole.RuleProvider -> 2
            SourceRegistryRole.ProxyProvider -> 3
            SourceRegistryRole.RemoteOverride -> 4
        }
    }

    companion object {
        private const val MAX_SNAPSHOTS = 32
        private const val MAX_TRACE_ENTRIES = 48
        private const val MAX_RAW_TRACE_LINES = 64
        private const val MAX_EXPLANATION_EVENTS = 12
    }
}

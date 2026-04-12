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

package com.github.yumelira.yumebox.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TracePhase {
    DnsResolve,
    RuleMatch,
    PolicySelect,
    OutboundConnect,
    Transport,
    Complete,
    Failed,
}

@Serializable
data class TraceStep(
    val phase: TracePhase,
    val label: String,
    val detail: String? = null,
    val durationMs: Long? = null,
    val matchedRule: String? = null,
    val matchedPolicy: String? = null,
)

@Serializable
data class SessionTrace(
    val traceId: String,
    val connectionId: String,
    val sourceApp: String? = null,
    val sourceIp: String? = null,
    val destinationHost: String,
    val destinationIp: String? = null,
    val destinationPort: Int,
    val protocol: String,
    val startedAtMillis: Long,
    val completedAtMillis: Long? = null,
    val steps: List<TraceStep> = emptyList(),
    val finalOutbound: String? = null,
    val finalRule: String? = null,
    val bytesUploaded: Long = 0L,
    val bytesDownloaded: Long = 0L,
    val error: String? = null,
    val profileId: String? = null,
) : ProductObjectContract {

    override val stableId: String
        get() = traceId

    override val displayName: String
        get() = "$protocol ${destinationHost}:${destinationPort}"

    override val lifecycleState: ProductLifecycleState
        get() =
            when {
                isFailed -> ProductLifecycleState.Failed
                isComplete -> ProductLifecycleState.Stopped
                steps.any { it.phase == TracePhase.Transport } -> ProductLifecycleState.Active
                steps.isNotEmpty() -> ProductLifecycleState.Preparing
                else -> ProductLifecycleState.Idle
            }

    override val updatedAtMillis: Long
        get() = completedAtMillis ?: startedAtMillis

    override val owner: ProductObjectOwner
        get() =
            ProductObjectOwner(
                id = sourceApp ?: sourceIp ?: "unknown",
                label = sourceApp ?: sourceIp ?: "Unknown",
                kind = if (sourceApp != null) "app" else "network",
            )

    override val editable: Boolean
        get() = false

    override val riskLevel: ProductRiskLevel
        get() =
            when {
                isFailed -> ProductRiskLevel.Medium
                finalOutbound?.equals("REJECT", ignoreCase = true) == true -> ProductRiskLevel.Low
                finalOutbound?.equals("DIRECT", ignoreCase = true) == true -> ProductRiskLevel.Low
                else -> ProductRiskLevel.Low
            }

    override val effectiveRelation: EffectiveStateRelation
        get() =
            when {
                isComplete -> EffectiveStateRelation.Inactive
                else -> EffectiveStateRelation.Active
            }

    override val changeState: ProductChangeState
        get() = ProductChangeState.Synced

    val durationMs: Long?
        get() = completedAtMillis?.let { it - startedAtMillis }

    val isComplete: Boolean
        get() = completedAtMillis != null

    val isFailed: Boolean
        get() = steps.any { it.phase == TracePhase.Failed } || error != null

    val explanationChain: String
        get() = buildString {
            append(sourceApp ?: sourceIp ?: "?")
            append(" → ")
            append(destinationHost)
            append(":")
            append(destinationPort)
            for (step in steps) {
                when (step.phase) {
                    TracePhase.DnsResolve -> {
                        append(" → DNS(")
                        append(step.detail ?: "resolved")
                        append(")")
                    }
                    TracePhase.RuleMatch -> {
                        append(" → Rule(")
                        append(step.matchedRule ?: step.label)
                        append(")")
                    }
                    TracePhase.PolicySelect -> {
                        append(" → Policy(")
                        append(step.matchedPolicy ?: step.label)
                        append(")")
                    }
                    TracePhase.OutboundConnect -> {
                        append(" → Outbound(")
                        append(step.label)
                        append(")")
                    }
                    TracePhase.Complete -> {
                        append(" → OK")
                    }
                    TracePhase.Failed -> {
                        append(" → FAILED(")
                        append(step.detail ?: "unknown")
                        append(")")
                    }
                    TracePhase.Transport -> {
                        // omit transport detail in summary chain
                    }
                }
            }
        }
}

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
enum class HealthCheckSeverity {
    Ok,
    Info,
    Warning,
    Error,
    Critical,
}

@Serializable
data class HealthCheckItem(
    val checkId: String,
    val label: String,
    val severity: HealthCheckSeverity,
    val detail: String? = null,
    val suggestedAction: String? = null,
    val category: String = "general",
)

@Serializable
data class HealthReport(
    val reportId: String,
    val generatedAtMillis: Long,
    val overallSeverity: HealthCheckSeverity,
    val items: List<HealthCheckItem>,
    val profileId: String? = null,
    val runtimePhase: String? = null,
    val configVersion: String? = null,
) : ProductObjectContract {

    override val stableId: String
        get() = reportId

    override val displayName: String
        get() = "Health Report"

    override val lifecycleState: ProductLifecycleState
        get() =
            when (overallSeverity) {
                HealthCheckSeverity.Ok -> ProductLifecycleState.Active
                HealthCheckSeverity.Info -> ProductLifecycleState.Active
                HealthCheckSeverity.Warning -> ProductLifecycleState.Degraded
                HealthCheckSeverity.Error -> ProductLifecycleState.Failed
                HealthCheckSeverity.Critical -> ProductLifecycleState.Failed
            }

    override val updatedAtMillis: Long
        get() = generatedAtMillis

    override val owner: ProductObjectOwner
        get() = ProductObjectOwner(id = "system", label = "System", kind = "diagnostic")

    override val editable: Boolean
        get() = false

    override val riskLevel: ProductRiskLevel
        get() =
            when (overallSeverity) {
                HealthCheckSeverity.Ok -> ProductRiskLevel.Low
                HealthCheckSeverity.Info -> ProductRiskLevel.Low
                HealthCheckSeverity.Warning -> ProductRiskLevel.Medium
                HealthCheckSeverity.Error -> ProductRiskLevel.High
                HealthCheckSeverity.Critical -> ProductRiskLevel.Critical
            }

    override val effectiveRelation: EffectiveStateRelation
        get() = EffectiveStateRelation.Active

    override val changeState: ProductChangeState
        get() = ProductChangeState.Synced

    val okCount: Int
        get() = items.count { it.severity == HealthCheckSeverity.Ok }

    val warningCount: Int
        get() = items.count { it.severity == HealthCheckSeverity.Warning }

    val errorCount: Int
        get() =
            items.count {
                it.severity == HealthCheckSeverity.Error ||
                    it.severity == HealthCheckSeverity.Critical
            }

    companion object {
        fun empty(reportId: String = "empty") =
            HealthReport(
                reportId = reportId,
                generatedAtMillis = System.currentTimeMillis(),
                overallSeverity = HealthCheckSeverity.Ok,
                items = emptyList(),
            )
    }
}

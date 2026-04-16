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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProductLifecycleState {
    Idle,
    Preparing,
    Active,
    Degraded,
    Failed,
    Stopping,
    Stopped,
}

@Serializable
enum class ProductChangeState {
    Synced,
    Modified,
    Conflicted,
    Applying,
    Applied,
    Invalid,
    Reverted,
}

@Serializable
enum class ProductRiskLevel {
    Low,
    Medium,
    High,
    Critical,
}

@Serializable
enum class EffectiveStateRelation {
    Inactive,
    Candidate,
    Active,
    Superseded,
}

@Serializable data class ProductObjectOwner(val id: String, val label: String, val kind: String)

interface ProductObjectContract {
    val stableId: String
    val displayName: String
    val lifecycleState: ProductLifecycleState
    val updatedAtMillis: Long
    val owner: ProductObjectOwner
    val editable: Boolean
    val riskLevel: ProductRiskLevel
    val effectiveRelation: EffectiveStateRelation
    val changeState: ProductChangeState
}

@Serializable
data class ProductProfileObject(
    override val stableId: String,
    override val displayName: String,
    override val lifecycleState: ProductLifecycleState,
    override val updatedAtMillis: Long,
    override val owner: ProductObjectOwner,
    override val editable: Boolean,
    override val riskLevel: ProductRiskLevel,
    override val effectiveRelation: EffectiveStateRelation,
    override val changeState: ProductChangeState,
    val sourceKind: String,
    val sourceUri: String,
    val trafficUsedBytes: Long,
    val trafficTotalBytes: Long,
    val expiresAtMillis: Long,
    val configSaved: Boolean,
) : ProductObjectContract

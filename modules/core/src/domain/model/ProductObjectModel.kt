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

object ProductLifecycleTransitions {
    private val allowedTransitions: Map<ProductLifecycleState, Set<ProductLifecycleState>> =
        mapOf(
            ProductLifecycleState.Idle to
                setOf(
                    ProductLifecycleState.Preparing,
                    ProductLifecycleState.Active,
                    ProductLifecycleState.Failed,
                    ProductLifecycleState.Stopped,
                ),
            ProductLifecycleState.Preparing to
                setOf(
                    ProductLifecycleState.Active,
                    ProductLifecycleState.Degraded,
                    ProductLifecycleState.Failed,
                    ProductLifecycleState.Stopping,
                ),
            ProductLifecycleState.Active to
                setOf(
                    ProductLifecycleState.Degraded,
                    ProductLifecycleState.Failed,
                    ProductLifecycleState.Stopping,
                ),
            ProductLifecycleState.Degraded to
                setOf(
                    ProductLifecycleState.Active,
                    ProductLifecycleState.Failed,
                    ProductLifecycleState.Stopping,
                ),
            ProductLifecycleState.Failed to
                setOf(
                    ProductLifecycleState.Idle,
                    ProductLifecycleState.Preparing,
                    ProductLifecycleState.Stopped,
                ),
            ProductLifecycleState.Stopping to
                setOf(
                    ProductLifecycleState.Idle,
                    ProductLifecycleState.Failed,
                    ProductLifecycleState.Stopped,
                ),
            ProductLifecycleState.Stopped to
                setOf(ProductLifecycleState.Idle, ProductLifecycleState.Preparing),
        )

    fun canTransition(from: ProductLifecycleState, to: ProductLifecycleState): Boolean {
        return from == to || allowedTransitions[from].orEmpty().contains(to)
    }

    fun requireTransition(from: ProductLifecycleState, to: ProductLifecycleState) {
        require(canTransition(from, to)) { "Invalid product lifecycle transition: $from -> $to" }
    }
}

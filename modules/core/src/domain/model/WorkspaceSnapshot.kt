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
enum class SnapshotType {
    AutoSave,
    ManualSave,
    PreChange,
    PostImport,
    Rollback,
}

@Serializable
data class WorkspaceSnapshot(
    val snapshotId: String,
    val profileId: String,
    val snapshotType: SnapshotType,
    val createdAtMillis: Long,
    val label: String,
    val configHash: String,
    val configSizeBytes: Long = 0L,
    val parentSnapshotId: String? = null,
    val metadata: Map<String, String> = emptyMap(),
) : ProductObjectContract {

    override val stableId: String
        get() = snapshotId

    override val displayName: String
        get() = label

    override val lifecycleState: ProductLifecycleState
        get() = ProductLifecycleState.Idle

    override val updatedAtMillis: Long
        get() = createdAtMillis

    override val owner: ProductObjectOwner
        get() = ProductObjectOwner(id = profileId, label = profileId, kind = "profile")

    override val editable: Boolean
        get() = false

    override val riskLevel: ProductRiskLevel
        get() = ProductRiskLevel.Low

    override val effectiveRelation: EffectiveStateRelation
        get() = EffectiveStateRelation.Inactive

    override val changeState: ProductChangeState
        get() = ProductChangeState.Synced

    companion object {
        fun preChange(
            snapshotId: String,
            profileId: String,
            label: String,
            configHash: String,
            configSizeBytes: Long = 0L,
        ) =
            WorkspaceSnapshot(
                snapshotId = snapshotId,
                profileId = profileId,
                snapshotType = SnapshotType.PreChange,
                createdAtMillis = System.currentTimeMillis(),
                label = label,
                configHash = configHash,
                configSizeBytes = configSizeBytes,
            )
    }
}

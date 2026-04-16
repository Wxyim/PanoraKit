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
enum class SourceType {
    RemoteUrl,
    LocalFile,
    InlineContent,
    RuleProvider,
    ProxyProvider,
}

@Serializable
enum class SyncState {
    Idle,
    Syncing,
    Succeeded,
    Failed,
    Stale,
}

@Serializable
data class SubscriptionSource(
    val sourceId: String,
    val name: String,
    val sourceType: SourceType,
    val url: String? = null,
    val localPath: String? = null,
    val syncIntervalSeconds: Long = 0L,
    val lastSyncAtMillis: Long? = null,
    val lastSyncError: String? = null,
    val syncState: SyncState = SyncState.Idle,
    val profileId: String? = null,
    val overrideId: String? = null,
    val contentHash: String? = null,
    val contentSizeBytes: Long = 0L,
    val createdAtMillis: Long,
    override val updatedAtMillis: Long,
) : ProductObjectContract {

    override val stableId: String
        get() = sourceId

    override val displayName: String
        get() = name

    override val lifecycleState: ProductLifecycleState
        get() =
            when (syncState) {
                SyncState.Idle -> ProductLifecycleState.Idle
                SyncState.Syncing -> ProductLifecycleState.Preparing
                SyncState.Succeeded -> ProductLifecycleState.Active
                SyncState.Failed -> ProductLifecycleState.Failed
                SyncState.Stale -> ProductLifecycleState.Degraded
            }

    override val owner: ProductObjectOwner
        get() =
            ProductObjectOwner(
                id = profileId ?: overrideId ?: "system",
                label = profileId ?: overrideId ?: "System",
                kind =
                    when {
                        profileId != null -> "profile"
                        overrideId != null -> "override"
                        else -> "system"
                    },
            )

    override val editable: Boolean
        get() = sourceType != SourceType.InlineContent

    override val riskLevel: ProductRiskLevel
        get() =
            when {
                sourceType == SourceType.RemoteUrl && url?.startsWith("http://") == true ->
                    ProductRiskLevel.High
                sourceType == SourceType.RemoteUrl -> ProductRiskLevel.Medium
                sourceType == SourceType.RuleProvider -> ProductRiskLevel.Medium
                sourceType == SourceType.ProxyProvider -> ProductRiskLevel.Medium
                else -> ProductRiskLevel.Low
            }

    override val effectiveRelation: EffectiveStateRelation
        get() =
            when {
                syncState == SyncState.Succeeded -> EffectiveStateRelation.Active
                syncState == SyncState.Stale -> EffectiveStateRelation.Candidate
                else -> EffectiveStateRelation.Inactive
            }

    override val changeState: ProductChangeState
        get() =
            when (syncState) {
                SyncState.Syncing -> ProductChangeState.Applying
                SyncState.Failed -> ProductChangeState.Invalid
                else -> ProductChangeState.Synced
            }

    val isRemote: Boolean
        get() = sourceType == SourceType.RemoteUrl

    val isStale: Boolean
        get() {
            if (syncIntervalSeconds <= 0L || lastSyncAtMillis == null) return false
            return System.currentTimeMillis() - lastSyncAtMillis > syncIntervalSeconds * 1000L
        }

    val needsSync: Boolean
        get() =
            isRemote &&
                (syncState == SyncState.Idle ||
                    syncState == SyncState.Failed ||
                    syncState == SyncState.Stale ||
                    isStale)

    companion object {
        fun fromProfile(
            sourceId: String,
            name: String,
            profileId: String,
            url: String,
            syncIntervalSeconds: Long = 0L,
        ): SubscriptionSource {
            val now = System.currentTimeMillis()
            return SubscriptionSource(
                sourceId = sourceId,
                name = name,
                sourceType = SourceType.RemoteUrl,
                url = url,
                syncIntervalSeconds = syncIntervalSeconds,
                profileId = profileId,
                createdAtMillis = now,
                updatedAtMillis = now,
            )
        }

        fun fromOverride(
            sourceId: String,
            name: String,
            overrideId: String,
            url: String,
            syncIntervalSeconds: Long = 0L,
        ): SubscriptionSource {
            val now = System.currentTimeMillis()
            return SubscriptionSource(
                sourceId = sourceId,
                name = name,
                sourceType = SourceType.RemoteUrl,
                url = url,
                syncIntervalSeconds = syncIntervalSeconds,
                overrideId = overrideId,
                createdAtMillis = now,
                updatedAtMillis = now,
            )
        }

        fun localFile(
            sourceId: String,
            name: String,
            localPath: String,
            profileId: String? = null,
        ): SubscriptionSource {
            val now = System.currentTimeMillis()
            return SubscriptionSource(
                sourceId = sourceId,
                name = name,
                sourceType = SourceType.LocalFile,
                localPath = localPath,
                profileId = profileId,
                createdAtMillis = now,
                updatedAtMillis = now,
            )
        }
    }
}

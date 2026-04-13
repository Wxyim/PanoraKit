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

package com.github.yumelira.yumebox.presentation.util

import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import com.github.yumelira.yumebox.domain.model.SourceType
import com.github.yumelira.yumebox.domain.model.SubscriptionSource
import com.github.yumelira.yumebox.domain.model.SyncState
import com.github.yumelira.yumebox.presentation.component.DiagnosticBannerState
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import dev.oom_wg.purejoy.mlang.DiagnosticLang
import dev.oom_wg.purejoy.mlang.MLang

data class ExternalResourceDiagnostics(
    val subscriptionSources: List<SubscriptionSource> = emptyList(),
    val staleCount: Int = 0,
    val pendingCount: Int = 0,
) {
    fun toBannerState(): DiagnosticBannerState {
        if (subscriptionSources.isEmpty()) {
            return DiagnosticBannerState(
                headline = MLang.Providers.Empty.NoProviders,
                subtitle = MLang.Providers.Empty.NoProvidersHint,
                tone = SemanticTone.Neutral,
            )
        }

        val primarySource =
            subscriptionSources.firstOrNull { it.syncState == SyncState.Stale }
                ?: subscriptionSources.firstOrNull { it.isRemote && it.lastSyncAtMillis == null }
                ?: subscriptionSources.first()

        val subtitle =
            when {
                staleCount > 0 -> DiagnosticLang.SourceStaleItems.format(staleCount)
                pendingCount > 0 -> DiagnosticLang.SourcePendingItems.format(pendingCount)
                else -> DiagnosticLang.SourceReadyItems.format(subscriptionSources.size)
            }

        val tone =
            when {
                staleCount > 0 -> SemanticTone.Warning
                pendingCount > 0 -> SemanticTone.Info
                else -> SemanticTone.Success
            }

        return DiagnosticBannerState(
            headline = primarySource.name,
            subtitle = subtitle,
            tone = tone,
        )
    }
}

fun buildExternalResourceDiagnostics(
    providers: List<Provider>,
    remoteOverrides: List<RemoteOverrideResource>,
): ExternalResourceDiagnostics {
    val now = System.currentTimeMillis()
    val providerSources = providers.map { provider -> provider.toSubscriptionSource(now) }
    val overrideSources = remoteOverrides.map { resource -> resource.toSubscriptionSource(now) }
    val sources =
        (overrideSources + providerSources).sortedWith(
            compareBy<SubscriptionSource> { source ->
                    when (source.syncState) {
                        SyncState.Stale -> 0
                        SyncState.Failed -> 0
                        SyncState.Idle -> 1
                        SyncState.Syncing -> 1
                        SyncState.Succeeded -> 2
                    }
                }
                .thenByDescending(SubscriptionSource::updatedAtMillis)
                .thenBy(SubscriptionSource::name)
        )

    return ExternalResourceDiagnostics(
        subscriptionSources = sources,
        staleCount = sources.count { it.syncState == SyncState.Stale },
        pendingCount = sources.count { it.isRemote && it.lastSyncAtMillis == null },
    )
}

private fun Provider.toSubscriptionSource(now: Long): SubscriptionSource {
    val updatedAtMillis = updatedAt.takeIf { it > 0L } ?: now
    return SubscriptionSource(
        sourceId = "provider:${type.name.lowercase()}:$name",
        name = name,
        sourceType =
            when (type) {
                Provider.Type.Proxy -> SourceType.ProxyProvider
                Provider.Type.Rule -> SourceType.RuleProvider
            },
        localPath = path.takeIf { it.isNotBlank() },
        lastSyncAtMillis = updatedAt.takeIf { it > 0L },
        syncState = if (updatedAt > 0L) SyncState.Succeeded else SyncState.Idle,
        createdAtMillis = updatedAtMillis,
        updatedAtMillis = updatedAtMillis,
    )
}

private fun RemoteOverrideResource.toSubscriptionSource(now: Long): SubscriptionSource {
    val lastSyncAtMillis = lastUpdatedAt.takeIf { it > 0L }
    val syncState =
        when {
            lastSyncAtMillis == null -> SyncState.Idle
            updateIntervalSeconds > 0L &&
                now - lastSyncAtMillis > updateIntervalSeconds * 1_000L -> SyncState.Stale
            else -> SyncState.Succeeded
        }

    return SubscriptionSource(
        sourceId = "override:$id",
        name = name,
        sourceType = SourceType.RemoteUrl,
        url = sourceUrl,
        syncIntervalSeconds = updateIntervalSeconds,
        lastSyncAtMillis = lastSyncAtMillis,
        syncState = syncState,
        overrideId = id,
        createdAtMillis = updatedAt.takeIf { it > 0L } ?: now,
        updatedAtMillis = updatedAt.takeIf { it > 0L } ?: now,
    )
}
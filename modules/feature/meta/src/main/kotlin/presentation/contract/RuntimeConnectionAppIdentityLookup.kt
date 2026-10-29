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
 */

package com.github.nomadboxlab.monadbox.feature.meta.presentation.contract

import com.github.nomadboxlab.monadbox.core.model.ConnectionInfo
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionAppIdentityLookup
import com.github.nomadboxlab.monadbox.feature.meta.api.ConnectionSourceApp
import com.github.nomadboxlab.monadbox.runtime.client.AppIdentityResolver
import dev.oom_wg.purejoy.mlang.MLang
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.JsonObject

class RuntimeConnectionAppIdentityLookup(private val appIdentityResolver: AppIdentityResolver) :
    ConnectionAppIdentityLookup {
    private data class CacheKey(val metadata: JsonObject, val system: Boolean)

    private data class CachedIdentity(val cachedAtMillis: Long, val value: ConnectionSourceApp)

    private val cache = ConcurrentHashMap<CacheKey, CachedIdentity>()

    override fun resolve(connection: ConnectionInfo): ConnectionSourceApp {
        val key = CacheKey(connection.metadata, connection.system)
        val now = System.currentTimeMillis()
        cache[key]
            ?.takeIf { now - it.cachedAtMillis < CACHE_TTL_MS }
            ?.let {
                return it.value
            }

        val resolved = resolveUncached(connection)
        if (cache.size >= MAX_CACHE_SIZE) {
            cache.clear()
        }
        cache[key] = CachedIdentity(now, resolved)
        return resolved
    }

    private fun resolveUncached(connection: ConnectionInfo): ConnectionSourceApp {
        if (connection.system && !appIdentityResolver.hasRemoteSource(connection.metadata)) {
            return ConnectionSourceApp(
                appName = MLang.TrafficStatistics.RecentRequests.System,
                packageName = null,
            )
        }
        val identity = appIdentityResolver.resolve(connection.metadata)
        return ConnectionSourceApp(appName = identity.appName, packageName = identity.packageName)
    }

    private companion object {
        /**
         * Shared by the recent-request list and the detail sheet so both surfaces resolve a given
         * metadata/system pair from the same result instead of letting the list's older row cache
         * diverge from a live detail lookup. Metadata changes (including UID enrichment) use a new
         * key immediately; the TTL only bounds package-label/local-address drift.
         */
        const val CACHE_TTL_MS = 5_000L
        const val MAX_CACHE_SIZE = 1_024
    }
}

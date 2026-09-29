/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.service.root

import com.github.nomadboxlab.monadbox.core.model.RuntimeDataSnapshot

/**
 * Client-side cache for the root runtime payload.
 *
 * The payload is every proxy group plus providers and configuration, and the client asks for it on
 * each refresh tick (2 s on the proxy page, 4 s elsewhere). The root process memoizes the payload
 * and reports a stamp for it, but an unchanged payload would still cost a full binder transfer and
 * a full JSON decode per tick. Holding the decoded copy while the stamp stands removes both.
 *
 * The stamp pairs the runtime instance's anchor with a revision that advances whenever the payload
 * changes, so a stamp can never repeat across runtimes: a restarted root process (or a recreated
 * runtime) always reports a different anchor and the client falls back to a real fetch. The cache
 * is also dropped with the binder, so a payload from a previous root process is never served.
 */
internal class RootTunPayloadCache {
    private val lock = Any()
    private var cachedStamp: LongArray? = null
    private var cachedSnapshot: RuntimeDataSnapshot? = null

    /** The payload cached for [stamp], or null when the stamp moved on (or nothing is cached). */
    fun get(stamp: LongArray): RuntimeDataSnapshot? =
        synchronized(lock) { if (cachedStamp.contentEquals(stamp)) cachedSnapshot else null }

    fun put(stamp: LongArray, snapshot: RuntimeDataSnapshot) {
        synchronized(lock) {
            cachedStamp = stamp
            cachedSnapshot = snapshot
        }
    }

    fun clear() {
        synchronized(lock) {
            cachedStamp = null
            cachedSnapshot = null
        }
    }
}

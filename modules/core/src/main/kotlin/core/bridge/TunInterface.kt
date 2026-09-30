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

package com.github.nomadboxlab.monadbox.core.bridge

import androidx.annotation.Keep

@Keep
interface TunInterface {
    fun markSocket(fd: Int)

    /**
     * Resolves the Android app that owns the socket [source] → [target].
     *
     * [force] is set by the native UID-enrichment path, which asks about a connection the core has
     * already seen close (or could not attribute in time). Such a lookup must not be answered from a
     * cached miss recorded while the socket was still alive: it performs a real query and leaves the
     * negative cache untouched. The caller rate-limits it (≤3 attempts, ≥500ms apart, per connection).
     */
    fun querySocketUid(protocol: Int, source: String, target: String, force: Boolean): Int

    fun queryPackageName(uid: Int): String
}

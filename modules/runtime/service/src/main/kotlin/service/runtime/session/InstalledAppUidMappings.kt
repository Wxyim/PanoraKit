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

package com.github.nomadboxlab.monadbox.service.runtime.session

internal data class InstalledAppUidEntry(
    val packageName: String,
    val uid: Int,
    val sharedUserId: String? = null,
)

internal object InstalledAppUidMappings {
    fun merge(vararg mappings: List<Pair<Int, String>>): List<Pair<Int, String>> {
        return mappings
            .asSequence()
            .flatten()
            .filter { (uid, packageName) -> uid > 0 && packageName.isNotBlank() }
            .groupBy { it.first }
            .map { (uid, entries) -> uid to entries.first().second }
            .sortedBy { it.first }
    }

    fun fromEntries(entries: List<InstalledAppUidEntry>): List<Pair<Int, String>> {
        if (entries.isEmpty()) return emptyList()

        return entries
            .asSequence()
            .filter { it.packageName.isNotBlank() && it.uid > 0 }
            .groupBy { entry ->
                entry.sharedUserId?.takeIf(String::isNotBlank) ?: entry.packageName
            }
            .map { (_, groupedEntries) ->
                val primary = groupedEntries.first()
                val sharedIdentity = primary.sharedUserId?.takeIf(String::isNotBlank)
                val identity =
                    if (groupedEntries.size == 1) {
                        primary.packageName
                    } else {
                        sharedIdentity ?: primary.packageName
                    }
                primary.uid to identity
            }
            .sortedBy { it.first }
    }

    fun fromRootPackageUidMap(packageUidMap: Map<String, Int>): List<Pair<Int, String>> {
        if (packageUidMap.isEmpty()) return emptyList()

        return packageUidMap.entries
            .asSequence()
            .filter { it.key.isNotBlank() && it.value > 0 }
            .groupBy { it.value }
            .map { (uid, entries) -> uid to entries.map { it.key }.sorted().first() }
            .sortedBy { it.first }
    }
}

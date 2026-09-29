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

package com.github.nomadboxlab.monadbox.service.runtime.records

import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection
import java.util.*

object SelectionDao {
    data class RestoreSelectionsResult(val selections: List<Selection>)

    fun queryAll(): List<Selection> {
        return ProfileStore.loadSelections()
    }

    fun querySelections(profileUUID: UUID): List<Selection> {
        return ProfileStore.loadSelections().filter { it.uuid == profileUUID }
    }

    fun setSelected(selection: Selection) {
        val list = ProfileStore.loadSelections().toMutableList()
        SelectionLookup.mergeInto(list, listOf(selection))
        ProfileStore.saveSelections(list)
    }

    /**
     * Applies [selections] in order with a single load/encode/store pass; equivalent to calling
     * [setSelected] once per entry, but it encodes the list once instead of once per entry.
     *
     * The first runtime snapshot after a profile switch persists one selection per proxy group, so
     * applying them one by one re-serialized the whole growing list once per group (O(n²) encoding
     * plus one MMKV write per group) on a path that runs roughly once per second.
     *
     * Returns the persisted list, which is [ProfileStore]'s cached instance and must be treated as
     * read-only.
     */
    fun setSelectedAll(selections: List<Selection>): List<Selection> {
        if (selections.isEmpty()) return ProfileStore.loadSelections()
        val list = ProfileStore.loadSelections().toMutableList()
        SelectionLookup.mergeInto(list, selections)
        ProfileStore.saveSelections(list)
        return list
    }

    fun querySelectionScopeKey(profileUUID: UUID): String? {
        return ProfileStore.loadSelectionScopeKey(profileUUID)
    }

    fun setSelectionScopeKey(profileUUID: UUID, scopeKey: String) {
        ProfileStore.saveSelectionScopeKey(profileUUID, scopeKey)
    }

    fun removeSelectionScopeKey(profileUUID: UUID) {
        ProfileStore.removeSelectionScopeKey(profileUUID)
    }

    fun querySelectionsForRestore(
        profileUUID: UUID,
        currentScopeKey: String,
    ): RestoreSelectionsResult {
        val selections = querySelections(profileUUID)
        setSelectionScopeKey(profileUUID, currentScopeKey)

        return RestoreSelectionsResult(selections = selections)
    }

    fun clear(profileUUID: UUID) {
        val list = ProfileStore.loadSelections().toMutableList()
        list.removeAll { it.uuid == profileUUID }
        ProfileStore.saveSelections(list)
    }

    fun clearAll() {
        ProfileStore.saveSelections(emptyList())
        ProfileStore.removeAllSelectionScopeKeys()
    }

    fun remove(profileUUID: UUID, proxy: String) {
        val list = ProfileStore.loadSelections().toMutableList()
        list.removeAll { it.uuid == profileUUID && it.proxy == proxy }
        ProfileStore.saveSelections(list)
    }

    fun removeSelections(profileUUID: UUID, proxies: List<String>) {
        val list = ProfileStore.loadSelections().toMutableList()
        list.removeAll { it.uuid == profileUUID && it.proxy in proxies }
        ProfileStore.saveSelections(list)
    }
}

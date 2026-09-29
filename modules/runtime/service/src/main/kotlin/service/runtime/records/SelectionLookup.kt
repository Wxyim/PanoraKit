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

/**
 * Pure helpers over the persisted selection list.
 *
 * The runtime resolves a remembered selection for every proxy group on each snapshot (roughly once
 * per second) and seeds one selection per group the first time a profile is seen. Both used to call
 * into [SelectionDao] once per group, which re-read and re-encoded the whole list every time; these
 * helpers keep the lookup and the merge in one place so they are testable without the store.
 */
internal object SelectionLookup {
    /**
     * Indexes [selections] by group name.
     *
     * Equivalent to the previous per-group lookup (`querySelections(profile).firstOrNull { it.proxy
     * == group }?.selected?.trim() ?.takeIf { it.isNotEmpty() }`): the first entry for a group wins
     * — even when its selection is blank, which reads as "nothing remembered" and must not be
     * filled in by a later duplicate.
     */
    fun indexOf(selections: List<Selection>): Map<String, String> {
        if (selections.isEmpty()) return emptyMap()
        val index = HashMap<String, String>(selections.size)
        val seen = HashSet<String>(selections.size)
        selections.forEach { selection ->
            if (!seen.add(selection.proxy)) return@forEach
            val selected = selection.selected.trim()
            if (selected.isEmpty()) return@forEach
            index[selection.proxy] = selected
        }
        return index
    }

    /**
     * Applies [additions] to [target] in order, replacing an existing `(uuid, proxy)` entry or
     * appending it: the result of calling [SelectionDao.setSelected] once per addition.
     */
    fun mergeInto(target: MutableList<Selection>, additions: List<Selection>) {
        additions.forEach { addition ->
            val index =
                target.indexOfFirst { it.uuid == addition.uuid && it.proxy == addition.proxy }
            if (index >= 0) {
                target[index] = addition
            } else {
                target.add(addition)
            }
        }
    }
}

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

package com.github.yumelira.yumebox.service.runtime.records

import com.github.yumelira.yumebox.core.Clash
import com.github.yumelira.yumebox.core.model.ProxySort
import com.github.yumelira.yumebox.service.common.log.Log
import com.github.yumelira.yumebox.service.runtime.entity.Selection
import java.util.*
import kotlinx.coroutines.delay

internal object SelectionRestoreExecutor {
    private const val queryRetryCount = 3
    private const val queryRetryDelayMs = 150L

    suspend fun restore(profileUuid: UUID, selections: List<Selection>, tag: String) {
        var removedAny = false
        selections.forEach { selection ->
            val groupName = selection.proxy.trim()
            val targetNode = selection.selected.trim()

            if (groupName.isEmpty() || targetNode.isEmpty()) {
                removedAny = clearInvalidSelection(profileUuid, selection, tag) || removedAny
                return@forEach
            }

            val group = queryGroupWithRetry(groupName)
            if (group == null) {
                Log.w("$tag restore selector query failed: profile=$profileUuid group=$groupName")
                return@forEach
            }

            val currentNodes =
                group
                    ?.proxies
                    ?.mapNotNull { proxy -> proxy.name.trim().takeIf { it.isNotEmpty() } }
                    .orEmpty()
            if (targetNode !in currentNodes) {
                removedAny = clearInvalidSelection(profileUuid, selection, tag) || removedAny
                return@forEach
            }

            if (!patchSelectorWithRetry(groupName, targetNode)) {
                Log.w(
                    "$tag restore selector patch failed: profile=$profileUuid group=$groupName node=$targetNode"
                )
            }
        }

        if (removedAny && SelectionDao.querySelections(profileUuid).isEmpty()) {
            SelectionDao.removeSelectionScopeKey(profileUuid)
            Log.i("$tag cleared selection scope key after restoring: profile=$profileUuid")
        }
    }

    private suspend fun queryGroupWithRetry(
        group: String
    ): com.github.yumelira.yumebox.core.model.ProxyGroup? {
        repeat(queryRetryCount) { attempt ->
            val result = runCatching { Clash.queryGroup(group, ProxySort.Default) }.getOrNull()
            if (result != null) {
                return result
            }
            if (attempt < queryRetryCount - 1) {
                delay(queryRetryDelayMs)
            }
        }
        return null
    }

    private suspend fun patchSelectorWithRetry(group: String, node: String): Boolean {
        repeat(queryRetryCount) { attempt ->
            if (Clash.patchSelector(group, node)) {
                return true
            }
            if (attempt < queryRetryCount - 1) {
                delay(queryRetryDelayMs)
            }
        }
        return false
    }

    private fun clearInvalidSelection(
        profileUuid: UUID,
        selection: Selection,
        tag: String,
    ): Boolean {
        val groupName = selection.proxy.trim()
        val targetNode = selection.selected.trim()
        Log.w(
            "$tag restore selector invalid: profile=$profileUuid group=$groupName node=$targetNode"
        )
        SelectionDao.remove(profileUuid, selection.proxy)
        return true
    }
}

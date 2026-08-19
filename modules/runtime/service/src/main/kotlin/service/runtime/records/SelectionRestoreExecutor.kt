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

import com.github.nomadboxlab.monadbox.core.Clash
import com.github.nomadboxlab.monadbox.core.model.ProxySort
import com.github.nomadboxlab.monadbox.service.common.log.Log
import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection
import java.util.*
import kotlinx.coroutines.delay

internal object SelectionRestoreExecutor {
    private const val queryRetryCount = 8
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

            // A selector restore may run while proxy providers are still
            // resolving. Do not treat an empty proxy list as an invalid
            // selection and delete the user's choice in that window.
            if (!isStillSelected(profileUuid, selection)) {
                return@forEach
            }

            val group = queryGroupWithRetry(groupName, targetNode)
            if (group == null) {
                Log.w("$tag restore selector query failed: profile=$profileUuid group=$groupName")
                return@forEach
            }

            val currentNodes =
                group.proxies.mapNotNull { proxy -> proxy.name.trim().takeIf { it.isNotEmpty() } }
            if (currentNodes.isEmpty()) {
                Log.w(
                    "$tag restore selector deferred: profile=$profileUuid group=$groupName " +
                        "node=$targetNode providers not ready"
                )
                return@forEach
            }
            if (targetNode !in currentNodes) {
                // A provider may expose a partial list while it is still
                // downloading. Keeping the durable choice is safer than
                // deleting it; a later refresh/start can restore it once the
                // provider exposes the node.
                Log.w(
                    "$tag restore selector deferred: profile=$profileUuid group=$groupName " +
                        "node=$targetNode not present in current provider snapshot"
                )
                return@forEach
            }

            // CompiledConfigPipeline normally puts the persisted node first,
            // so mihomo may already have the desired selector. Avoid issuing
            // a second patch when the runtime state is already converged.
            if (group.now.trim() == targetNode) {
                return@forEach
            }

            // The proxy page can be used while this background restore is in
            // flight. Re-check the durable value immediately before patching
            // so an old restore task cannot overwrite a newer user choice.
            if (!isStillSelected(profileUuid, selection)) {
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
        group: String,
        targetNode: String,
    ): com.github.nomadboxlab.monadbox.core.model.ProxyGroup? {
        var lastResult: com.github.nomadboxlab.monadbox.core.model.ProxyGroup? = null
        repeat(queryRetryCount) { attempt ->
            val result = runCatching { Clash.queryGroup(group, ProxySort.Default) }.getOrNull()
            lastResult = result
            val targetVisible =
                result?.proxies?.any { proxy -> proxy.name.trim() == targetNode } == true
            if (result != null && (targetVisible || result.now.trim() == targetNode)) {
                return result
            }
            if (attempt < queryRetryCount - 1) {
                delay(queryRetryDelayMs)
            }
        }
        return lastResult
    }

    private fun isStillSelected(profileUuid: UUID, selection: Selection): Boolean {
        return SelectionDao.querySelections(profileUuid).any {
            it.proxy == selection.proxy && it.selected == selection.selected
        }
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

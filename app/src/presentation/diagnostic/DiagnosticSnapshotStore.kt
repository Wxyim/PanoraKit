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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.presentation.diagnostic

import android.content.Context
import com.github.yumelira.yumebox.domain.model.WorkspaceSnapshot
import java.io.File
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class DiagnosticSnapshotStore(context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
    }
    private val snapshotSerializer = ListSerializer(WorkspaceSnapshot.serializer())
    private val snapshotsFile = File(context.filesDir, "diagnostics/workspace_snapshots.json")

    fun load(): List<WorkspaceSnapshot> {
        if (!snapshotsFile.exists()) {
            return emptyList()
        }
        return runCatching { json.decodeFromString(snapshotSerializer, snapshotsFile.readText()) }
            .getOrDefault(emptyList())
    }

    fun save(snapshots: List<WorkspaceSnapshot>) {
        snapshotsFile.parentFile?.mkdirs()
        snapshotsFile.writeText(json.encodeToString(snapshotSerializer, snapshots))
    }
}

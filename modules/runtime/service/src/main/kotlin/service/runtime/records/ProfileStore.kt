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

@file:UseSerializers(UUIDSerializer::class)

package com.github.nomadboxlab.monadbox.service.runtime.records

import com.github.nomadboxlab.monadbox.core.StoreIds
import com.github.nomadboxlab.monadbox.service.runtime.entity.Imported
import com.github.nomadboxlab.monadbox.service.runtime.entity.Selection
import com.github.nomadboxlab.monadbox.service.runtime.util.UUIDSerializer
import com.tencent.mmkv.MMKV
import java.util.*
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Cross-process MMKV-backed store (`MMKV.MULTI_PROCESS_MODE`).
 *
 * Read and written from both the app process and the `:runtime:service` VPN process. Must NOT be
 * migrated to Room or single-process Preferences DataStore — neither offers multi-process
 * coherence. Any future replacement requires a ContentProvider or AIDL shim in front of the storage
 * engine.
 */
object ProfileStore {
    private const val IMPORTED_KEY = "imported"
    private const val SELECTIONS_KEY = "selections"
    private const val PROFILE_ORDER_KEY = "profile_order"
    private const val SELECTION_SCOPE_KEY_PREFIX = "selection_scope_key:"

    private val mmkv by lazy { MMKV.mmkvWithID(StoreIds.PROFILES, MMKV.MULTI_PROCESS_MODE) }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // ── Decode caches ────────────────────────────────────────────
    //
    // Both lists are read far more often than they are written: the runtime asks for the active
    // profile on every payload refresh, and the service notification resolves the profile name on
    // every 2s tick. Decoding the whole list each time is pure waste while nothing changed, so the
    // raw MMKV payload is kept as the cache key (same trick as RootTunStateStore.snapshot()) and
    // the parse only reruns when the stored string actually differs. A cross-process write always
    // changes the string, so the cache can never serve stale data.
    private val importedCacheLock = Any()
    private var importedCacheKey: String? = null
    private var importedCache: List<Imported> = emptyList()

    private val selectionsCacheLock = Any()
    private var selectionsCacheKey: String? = null
    private var selectionsCache: List<Selection> = emptyList()

    fun saveImported(list: List<Imported>) {
        val jsonString = json.encodeToString(ListSerializer(Imported.serializer()), list)
        mmkv.encode(IMPORTED_KEY, jsonString)
        synchronized(importedCacheLock) {
            importedCacheKey = jsonString
            importedCache = list
        }
    }

    /**
     * Returns the imported-profile list. The returned list is the cached instance and must be
     * treated as read-only — copy it before mutating (see [ImportedDao]).
     */
    fun loadImported(): List<Imported> {
        val jsonString = mmkv.decodeString(IMPORTED_KEY) ?: return emptyList()
        synchronized(importedCacheLock) {
            if (importedCacheKey == jsonString) return importedCache
            val decoded =
                try {
                    json.decodeFromString(ListSerializer(Imported.serializer()), jsonString)
                } catch (e: Exception) {
                    emptyList()
                }
            importedCacheKey = jsonString
            importedCache = decoded
            return decoded
        }
    }

    fun saveSelections(list: List<Selection>) {
        val jsonString = json.encodeToString(ListSerializer(Selection.serializer()), list)
        mmkv.encode(SELECTIONS_KEY, jsonString)
        synchronized(selectionsCacheLock) {
            selectionsCacheKey = jsonString
            selectionsCache = list
        }
    }

    /**
     * Returns the selection list. The returned list is the cached instance and must be treated as
     * read-only — copy it before mutating (see [SelectionDao]).
     */
    fun loadSelections(): List<Selection> {
        val jsonString = mmkv.decodeString(SELECTIONS_KEY) ?: return emptyList()
        synchronized(selectionsCacheLock) {
            if (selectionsCacheKey == jsonString) return selectionsCache
            val decoded =
                try {
                    json.decodeFromString(ListSerializer(Selection.serializer()), jsonString)
                } catch (e: Exception) {
                    emptyList()
                }
            selectionsCacheKey = jsonString
            selectionsCache = decoded
            return decoded
        }
    }

    fun saveSelectionScopeKey(profileUUID: UUID, scopeKey: String) {
        mmkv.encode("$SELECTION_SCOPE_KEY_PREFIX$profileUUID", scopeKey)
    }

    fun loadSelectionScopeKey(profileUUID: UUID): String? {
        return mmkv.decodeString("$SELECTION_SCOPE_KEY_PREFIX$profileUUID")
    }

    fun removeSelectionScopeKey(profileUUID: UUID) {
        mmkv.removeValueForKey("$SELECTION_SCOPE_KEY_PREFIX$profileUUID")
    }

    fun removeAllSelectionScopeKeys() {
        mmkv
            .allKeys()
            ?.filter { it.startsWith(SELECTION_SCOPE_KEY_PREFIX) }
            ?.forEach(mmkv::removeValueForKey)
    }

    fun saveProfileOrder(order: List<UUID>) {
        val jsonString = json.encodeToString(ListSerializer(UUIDSerializer()), order)
        mmkv.encode("profile_order", jsonString)
    }

    fun loadProfileOrder(): List<UUID> {
        val jsonString = mmkv.decodeString(PROFILE_ORDER_KEY) ?: return emptyList()
        return try {
            json.decodeFromString(ListSerializer(UUIDSerializer()), jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun countStoredKeys(): Int {
        var count = 0
        if (mmkv.decodeString(IMPORTED_KEY) != null) count++
        if (mmkv.decodeString(SELECTIONS_KEY) != null) count++
        if (mmkv.decodeString(PROFILE_ORDER_KEY) != null) count++
        count +=
            loadImported().count { imported ->
                mmkv.decodeString("$SELECTION_SCOPE_KEY_PREFIX${imported.uuid}") != null
            }
        return count
    }

    fun clear() {
        mmkv.clearAll()
        synchronized(importedCacheLock) {
            importedCacheKey = null
            importedCache = emptyList()
        }
        synchronized(selectionsCacheLock) {
            selectionsCacheKey = null
            selectionsCache = emptyList()
        }
    }
}

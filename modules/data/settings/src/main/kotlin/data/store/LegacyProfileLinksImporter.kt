/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.nomadboxlab.monadbox.data.persistence.ProfileLinkDao
import com.github.nomadboxlab.monadbox.data.persistence.ProfileLinkEntity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

/**
 * One-shot import of legacy profile-links state from MMKV into the new DataStore-plus-Room layout.
 * Scalars (`linkOpenMode`, `defaultLinkId`) go to [dataStore]; the JSON-blob list goes to [dao].
 * Idempotent, gated on a boolean flag stored in [dataStore].
 */
class LegacyProfileLinksImporter(
    private val dataStore: DataStore<Preferences>,
    private val mmkv: MMKV,
    private val dao: ProfileLinkDao,
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun importIfNeeded() {
        val alreadyImported = dataStore.data.first()[IMPORTED_FLAG] == true
        if (alreadyImported) return

        val linksJson = mmkv.decodeString(KEY_LINKS)
        val openMode = mmkv.decodeString(KEY_OPEN_MODE)
        val defaultLinkId = mmkv.decodeString(KEY_DEFAULT_LINK_ID)

        val entities: List<ProfileLinkEntity> =
            linksJson
                ?.takeIf { it.isNotBlank() }
                ?.let { blob ->
                    runCatching { json.decodeFromString<List<ProfileLink>>(blob) }.getOrNull()
                }
                ?.mapIndexed { index, link ->
                    ProfileLinkEntity(
                        id = link.id,
                        name = link.name,
                        url = link.url,
                        position = index,
                    )
                }
                .orEmpty()

        if (entities.isNotEmpty()) {
            dao.replaceAll(entities)
        }

        dataStore.edit { prefs ->
            if (openMode != null) prefs[stringPreferencesKey("linkOpenMode")] = openMode
            if (defaultLinkId != null) prefs[stringPreferencesKey("defaultLinkId")] = defaultLinkId
            prefs[IMPORTED_FLAG] = true
        }
    }

    companion object {
        private const val KEY_LINKS = "links"
        private const val KEY_OPEN_MODE = "linkOpenMode"
        private const val KEY_DEFAULT_LINK_ID = "defaultLinkId"
        private val IMPORTED_FLAG = booleanPreferencesKey("__profile_links_imported_from_mmkv")
    }
}

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

package com.github.nomadboxlab.monadbox.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.nomadboxlab.monadbox.data.persistence.ProfileLinkDao
import com.github.nomadboxlab.monadbox.data.persistence.ProfileLinkEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable data class ProfileLink(val id: String, val name: String, val url: String)

enum class LinkOpenMode {
    IN_APP,
    EXTERNAL_BROWSER,
}

/**
 * Profile-links store. Scalars (`linkOpenMode`, `defaultLinkId`) live in Preferences DataStore; the
 * record list (`links`) is backed by a Room table via [ProfileLinkDao]. Public API preserves the
 * [Preference] shape so call sites keep using the same DSL.
 *
 * Single-process only; not shared with `runtime:service`.
 */
class ProfileLinksStorage(
    dataStore: DataStore<Preferences>,
    writeScope: CoroutineScope,
    dao: ProfileLinkDao,
) : DataStorePreference(dataStore, writeScope) {

    val linkOpenMode by enumFlow(LinkOpenMode.IN_APP)

    val defaultLinkId by strFlow(default = "")

    val links: Preference<List<ProfileLink>> = buildLinksPreference(dao, writeScope)

    private fun buildLinksPreference(
        dao: ProfileLinkDao,
        writeScope: CoroutineScope,
    ): Preference<List<ProfileLink>> {
        val initial = runBlocking { dao.getAll() }.map { it.toModel() }
        val flow = MutableStateFlow(initial)
        // Mirror DAO updates into the in-memory flow so external writers
        // (e.g. the legacy importer) are observable.
        writeScope.launch {
            dao.observeAll().collect { rows -> flow.value = rows.map { it.toModel() } }
        }
        return Preference(
            state = flow.asStateFlow(),
            update = { value ->
                if (flow.value != value) {
                    flow.value = value
                    writeScope.launch { dao.replaceAll(value.toEntities()) }
                }
            },
            get = { flow.value },
        )
    }
}

private fun ProfileLinkEntity.toModel(): ProfileLink = ProfileLink(id = id, name = name, url = url)

private fun List<ProfileLink>.toEntities(): List<ProfileLinkEntity> = mapIndexed { index, link ->
    ProfileLinkEntity(id = link.id, name = link.name, url = link.url, position = index)
}

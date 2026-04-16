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

package dev.oom_wg.purejoy.fyl.fytxt

import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface FYTxtGroup {
    val stats: Map<out FYTxtTag, Double>
}

interface FYTxtTag {
    val pattern: String?
}

object FYTxtConfig {
    private val registeredTags = linkedMapOf<String, FYTxtTag>()
    private var preferredTagNames: List<String>? = null

    private val _activeTags = MutableStateFlow<List<FYTxtTag>>(emptyList())
    val activeTags = _activeTags.asStateFlow()

    fun init(group: FYTxtGroup, tags: Iterable<FYTxtTag>) {
        group.stats.keys.forEach(::registerTag)
        tags.forEach(::registerTag)
        refreshActiveTags()
    }

    fun updateTags(tags: List<String>?, force: Boolean) {
        preferredTagNames = tags?.map(String::uppercase)
        if (force || activeTags.value.isEmpty()) {
            refreshActiveTags()
        }
    }

    private fun registerTag(tag: FYTxtTag) {
        registeredTags[tag.tagName()] = tag
    }

    private fun refreshActiveTags() {
        val resolvedTags =
            resolvedTagNames().mapNotNull(registeredTags::get).ifEmpty {
                registeredTags.values.toList()
            }
        _activeTags.value = resolvedTags
    }

    private fun resolvedTagNames(): List<String> {
        val preferred = preferredTagNames ?: defaultTagNames()
        val fallbacks = registeredTags.keys.filterNot(preferred::contains)
        return preferred + fallbacks
    }

    private fun defaultTagNames(): List<String> {
        val language = Locale.getDefault().language.lowercase(Locale.ROOT)
        return if (language.startsWith("en")) {
            listOf("EN", "ZH")
        } else {
            listOf("ZH", "EN")
        }
    }
}

private fun FYTxtTag.tagName(): String =
    when (this) {
        is Enum<*> -> name
        else -> toString().uppercase(Locale.ROOT)
    }

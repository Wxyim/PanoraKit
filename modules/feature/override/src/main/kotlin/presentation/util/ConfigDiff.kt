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

package com.github.nomadboxlab.monadbox.presentation.util

import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverride
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

private val OverrideConfigDiffJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

fun hasOrderSensitiveConfigChanges(
    original: ConfigurationOverride,
    updated: ConfigurationOverride,
): Boolean {
    return encodeOverrideConfigForDiff(original) != encodeOverrideConfigForDiff(updated)
}

fun encodeOverrideConfigForDiff(config: ConfigurationOverride): String {
    return OverrideConfigDiffJson.encodeToString(ConfigurationOverride.serializer(), config)
}

data class ConfigFieldChange(val fieldName: String, val kind: ChangeKind) {
    enum class ChangeKind {
        Added,
        Removed,
        Modified,
    }
}

data class ConfigChangeSummary(val changes: List<ConfigFieldChange>) {
    val changedFieldCount: Int
        get() = changes.size

    val hasChanges: Boolean
        get() = changes.isNotEmpty()

    val addedCount: Int
        get() = changes.count { it.kind == ConfigFieldChange.ChangeKind.Added }

    val removedCount: Int
        get() = changes.count { it.kind == ConfigFieldChange.ChangeKind.Removed }

    val modifiedCount: Int
        get() = changes.count { it.kind == ConfigFieldChange.ChangeKind.Modified }
}

fun computeConfigChangeSummary(
    original: ConfigurationOverride,
    updated: ConfigurationOverride,
): ConfigChangeSummary {
    val originalElement =
        OverrideConfigDiffJson.encodeToJsonElement(ConfigurationOverride.serializer(), original)
    val updatedElement =
        OverrideConfigDiffJson.encodeToJsonElement(ConfigurationOverride.serializer(), updated)

    val changes = mutableListOf<ConfigFieldChange>()
    if (originalElement is JsonObject && updatedElement is JsonObject) {
        collectTopLevelChanges(originalElement, updatedElement, changes)
    }
    return ConfigChangeSummary(changes)
}

private fun collectTopLevelChanges(
    original: JsonObject,
    updated: JsonObject,
    out: MutableList<ConfigFieldChange>,
) {
    val allKeys = original.keys + updated.keys
    for (key in allKeys) {
        val orig = original[key]
        val upd = updated[key]
        val kind = classifyFieldChange(orig, upd) ?: continue
        out.add(ConfigFieldChange(fieldName = key, kind = kind))
    }
}

private fun classifyFieldChange(
    original: JsonElement?,
    updated: JsonElement?,
): ConfigFieldChange.ChangeKind? {
    val origIsEmpty = original == null || original is JsonNull || isEmptyDefault(original)
    val updIsEmpty = updated == null || updated is JsonNull || isEmptyDefault(updated)

    return when {
        origIsEmpty && updIsEmpty -> null
        origIsEmpty && !updIsEmpty -> ConfigFieldChange.ChangeKind.Added
        !origIsEmpty && updIsEmpty -> ConfigFieldChange.ChangeKind.Removed
        original == updated -> null
        else -> ConfigFieldChange.ChangeKind.Modified
    }
}

private fun isEmptyDefault(element: JsonElement): Boolean {
    return when (element) {
        is JsonArray -> element.isEmpty()
        is JsonObject -> element.isEmpty()
        else -> false
    }
}

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

import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.serialization.json.*

val OverrideEditorJson = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    ignoreUnknownKeys = true
}

enum class OverrideExtraFieldValueType {
    String,
    Boolean,
    Int,
    Double,
    Null,
    JsonFragment,
}

data class OverrideExtraFieldDraft(
    val key: String = "",
    val valueType: OverrideExtraFieldValueType = OverrideExtraFieldValueType.String,
    val value: String = "",
)

fun jsonElementToExtraFieldDraft(key: String, value: JsonElement): OverrideExtraFieldDraft {
    return when (value) {
        JsonNull -> OverrideExtraFieldDraft(key = key, valueType = OverrideExtraFieldValueType.Null)

        is JsonPrimitive ->
            when {
                value.isString ->
                    OverrideExtraFieldDraft(
                        key = key,
                        valueType = OverrideExtraFieldValueType.String,
                        value = value.content,
                    )

                value.booleanOrNull != null ->
                    OverrideExtraFieldDraft(
                        key = key,
                        valueType = OverrideExtraFieldValueType.Boolean,
                        value = value.content,
                    )

                value.intOrNull != null ->
                    OverrideExtraFieldDraft(
                        key = key,
                        valueType = OverrideExtraFieldValueType.Int,
                        value = value.content,
                    )

                value.doubleOrNull != null ->
                    OverrideExtraFieldDraft(
                        key = key,
                        valueType = OverrideExtraFieldValueType.Double,
                        value = value.content,
                    )

                else ->
                    OverrideExtraFieldDraft(
                        key = key,
                        valueType = OverrideExtraFieldValueType.String,
                        value = value.content,
                    )
            }

        else ->
            OverrideExtraFieldDraft(
                key = key,
                valueType = OverrideExtraFieldValueType.JsonFragment,
                value = OverrideEditorJson.encodeToString(JsonElement.serializer(), value),
            )
    }
}

fun extraFieldDraftToJsonElement(draft: OverrideExtraFieldDraft): JsonElement? {
    return when (draft.valueType) {
        OverrideExtraFieldValueType.String -> JsonPrimitive(draft.value)
        OverrideExtraFieldValueType.Boolean ->
            draft.value.trim().toBooleanStrictOrNull()?.let(::JsonPrimitive)
        OverrideExtraFieldValueType.Int -> draft.value.trim().toIntOrNull()?.let(::JsonPrimitive)
        OverrideExtraFieldValueType.Double ->
            draft.value.trim().toDoubleOrNull()?.let(::JsonPrimitive)
        OverrideExtraFieldValueType.Null -> JsonNull
        OverrideExtraFieldValueType.JsonFragment ->
            runCatching { OverrideEditorJson.parseToJsonElement(draft.value.trim()) }.getOrNull()
    }
}

fun summarizeExtraFieldValue(element: JsonElement): String {
    return when (element) {
        JsonNull -> "Null"
        is JsonPrimitive ->
            when {
                element.isString -> element.content.ifBlank { MLang.Override.Editor.EmptyString }
                else -> element.content
            }

        is JsonArray -> MLang.Override.Editor.ArrayItems.format(element.size)
        is JsonObject -> MLang.Override.Editor.ObjectFields.format(element.size)
    }
}

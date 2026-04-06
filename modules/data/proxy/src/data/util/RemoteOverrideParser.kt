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

package com.github.yumelira.yumebox.data.util

import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

enum class RemoteOverrideContentKind {
    Config,
    PluginRules,
}

data class ParsedRemoteOverride(
    val kind: RemoteOverrideContentKind,
    val config: ConfigurationOverride,
    val count: Int,
)

private val unsupportedRuleTypes = setOf(
    "USER-AGENT",
    "URL-REGEX",
    "HEADER",
    "HTTP-METHOD",
)

private val ruleTypeAliases = mapOf(
    "DEST-PORT" to "DST-PORT",
)

private val surgeRejectVariants = setOf(
    "REJECT-TINYGIF",
    "REJECT-200",
    "REJECT-IMG",
    "REJECT-DICT",
    "REJECT-ARRAY",
)

@Serializable
private data class OverrideConfigEnvelope(
    val config: ConfigurationOverride? = null,
)

object RemoteOverrideParser {
    fun parse(
        json: Json,
        text: String,
    ): ParsedRemoteOverride {
        val normalized = text.trim()
        require(normalized.isNotEmpty()) { "empty content" }

        runCatching {
            val rootElement = json.parseToJsonElement(normalized)
            val firstElement = when (rootElement) {
                is JsonArray -> rootElement.firstOrNull()
                else -> rootElement
            } ?: error("empty json array")

            val envelope = runCatching {
                json.decodeFromJsonElement(OverrideConfigEnvelope.serializer(), firstElement)
            }.getOrNull()
            val decodedConfig = envelope?.config ?: runCatching {
                json.decodeFromJsonElement(ConfigurationOverride.serializer(), firstElement)
            }.getOrNull()

            if (decodedConfig != null) {
                return ParsedRemoteOverride(
                    kind = RemoteOverrideContentKind.Config,
                    config = decodedConfig,
                    count = 1,
                )
            }
        }

        val rules = parsePluginRules(normalized)
        require(rules.isNotEmpty()) { "no importable content" }
        return ParsedRemoteOverride(
            kind = RemoteOverrideContentKind.PluginRules,
            config = ConfigurationOverride(rulesStart = rules),
            count = rules.size,
        )
    }

    private fun parsePluginRules(text: String): List<String> {
        var inRuleSection = false
        val result = mutableListOf<String>()

        for (rawLine in text.lineSequence()) {
            val line = rawLine.trim()

            if (line.startsWith('[')) {
                inRuleSection = line.equals("[Rule]", ignoreCase = true)
                continue
            }

            if (!inRuleSection || line.isEmpty() || line.startsWith('#') || line.startsWith(';')) {
                continue
            }

            val ruleLine = line.substringAfter('=', line).trim()
            if (ruleLine.isEmpty()) continue

            val parts = ruleLine.split(',').map { it.trim() }
            if (parts.size < 2) continue

            val rawType = parts[0].uppercase()
            if (rawType in unsupportedRuleTypes) continue

            val normalizedType = ruleTypeAliases[rawType] ?: rawType
            val normalizedParts = parts.toMutableList()
            normalizedParts[0] = normalizedType

            val policyIndex = if (normalizedType == "MATCH") 1 else 2
            if (policyIndex < normalizedParts.size && normalizedParts[policyIndex].uppercase() in surgeRejectVariants) {
                normalizedParts[policyIndex] = "REJECT"
            }

            result += normalizedParts.joinToString(",")
        }

        return result
    }
}

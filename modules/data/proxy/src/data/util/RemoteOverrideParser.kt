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
import com.github.yumelira.yumebox.core.model.ConfigurationOverrideRuleSanitizer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

enum class RemoteOverrideContentKind {
    Config,
    PluginRules,
}

data class ParsedRemoteOverride(
    val kind: RemoteOverrideContentKind,
    val config: ConfigurationOverride,
    val count: Int,
)

@Serializable private data class OverrideConfigEnvelope(val config: ConfigurationOverride? = null)

object RemoteOverrideParser {
    fun parse(json: Json, text: String): ParsedRemoteOverride {
        val normalized = text.trim()
        require(normalized.isNotEmpty()) { "empty content" }

        runCatching {
            val rootElement = json.parseToJsonElement(normalized)
            val firstElement =
                when (rootElement) {
                    is JsonArray -> rootElement.firstOrNull()
                    else -> rootElement
                } ?: error("empty json array")

            val envelope =
                runCatching {
                        json.decodeFromJsonElement(
                            OverrideConfigEnvelope.serializer(),
                            firstElement,
                        )
                    }
                    .getOrNull()
            val decodedConfig =
                envelope?.config
                    ?: runCatching {
                            json.decodeFromJsonElement(
                                ConfigurationOverride.serializer(),
                                firstElement,
                            )
                        }
                        .getOrNull()

            if (decodedConfig != null) {
                return ParsedRemoteOverride(
                    kind = RemoteOverrideContentKind.Config,
                    config = ConfigurationOverrideRuleSanitizer.sanitize(decodedConfig),
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

            val normalizedRule = ConfigurationOverrideRuleSanitizer.sanitizeRuleLine(ruleLine)
            if (normalizedRule != null) {
                result += normalizedRule
            }
        }

        return result
    }
}

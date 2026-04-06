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

package com.github.yumelira.yumebox.presentation.viewmodel

/**
 * Parses Surge/Loon plugin text and extracts rules from the [Rule] section.
 *
 * Rules with types unsupported by mihomo/Clash are dropped. Surge/Loon-specific aliases and
 * built-in REJECT variants are normalized to Clash equivalents.
 */
internal object PluginRuleParser {

    /**
     * Surge-only rule types with no equivalent in mihomo/Clash. Lines using these types are dropped
     * during import.
     */
    private val UNSUPPORTED_RULE_TYPES = setOf("USER-AGENT", "URL-REGEX", "HEADER", "HTTP-METHOD")

    /**
     * Surge rule type aliases -> mihomo/Clash rule type names. Applied before writing the
     * normalized rule.
     */
    private val RULE_TYPE_ALIASES = mapOf("DEST-PORT" to "DST-PORT")

    /**
     * Surge built-in REJECT variants with no direct equivalent in Clash. Normalized to plain
     * REJECT.
     */
    private val SURGE_REJECT_VARIANTS =
        setOf("REJECT-TINYGIF", "REJECT-200", "REJECT-IMG", "REJECT-DICT", "REJECT-ARRAY")

    /**
     * Parse [text] and return all rules from the [Rule] section that are compatible with
     * mihomo/Clash.
     *
     * Supports both direct rule lines and keyed rule fields like RULE-1 =
     * DOMAIN-SUFFIX,example.com,REJECT used in some Loon plugins.
     *
     * The caller is responsible for non-empty validation of the result.
     */
    fun parseRules(text: String): List<String> {
        var inRuleSection = false
        val result = mutableListOf<String>()

        for (rawLine in text.lineSequence()) {
            val line = rawLine.trim()

            if (line.startsWith('[')) {
                inRuleSection = line.equals("[Rule]", ignoreCase = true)
                continue
            }

            if (!inRuleSection || line.isEmpty() || line.startsWith('#') || line.startsWith(';'))
                continue

            val ruleLine = line.substringAfter('=', line).trim()
            if (ruleLine.isEmpty()) continue

            val parts = ruleLine.split(',').map { it.trim() }
            if (parts.size < 2) continue

            val rawType = parts[0].uppercase()
            if (rawType in UNSUPPORTED_RULE_TYPES) continue

            val normalizedType = RULE_TYPE_ALIASES[rawType] ?: rawType
            val normalizedParts = parts.toMutableList()
            normalizedParts[0] = normalizedType

            // Normalize Surge-specific REJECT variants.
            // For MATCH the policy is at index 1; for all other rules it is at index 2.
            val policyIndex = if (normalizedType == "MATCH") 1 else 2
            if (
                policyIndex < normalizedParts.size &&
                    normalizedParts[policyIndex].uppercase() in SURGE_REJECT_VARIANTS
            ) {
                normalizedParts[policyIndex] = "REJECT"
            }

            result.add(normalizedParts.joinToString(","))
        }

        return result
    }
}

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

package com.github.yumelira.yumebox.core.model

object ConfigurationOverrideRuleSanitizer {
    private val unsupportedRuleTypes = setOf("USER-AGENT", "URL-REGEX", "HEADER", "HTTP-METHOD")

    private val ruleTypeAliases = mapOf("DEST-PORT" to "DST-PORT")

    private val surgeRejectVariants =
        setOf("REJECT-TINYGIF", "REJECT-200", "REJECT-IMG", "REJECT-DICT", "REJECT-ARRAY")

    private val nestedUnsupportedRuleTypePattern =
        Regex(
            """[(,]\s*(USER-AGENT|URL-REGEX|HEADER|HTTP-METHOD)\s*,""",
            RegexOption.IGNORE_CASE,
        )

    private val aliasPatterns =
        ruleTypeAliases.mapValues { (rawType, _) ->
            Regex("""(^|[(,])\s*${Regex.escape(rawType)}(?=\s*,)""", RegexOption.IGNORE_CASE)
        }

    fun sanitize(config: ConfigurationOverride): ConfigurationOverride {
        return config.copy(
            rules = sanitizeRules(config.rules),
            rulesStart = sanitizeRules(config.rulesStart),
            rulesEnd = sanitizeRules(config.rulesEnd),
            subRules = sanitizeRuleMap(config.subRules),
            subRulesMerge = sanitizeRuleMap(config.subRulesMerge),
        )
    }

    fun sanitizeRules(rules: List<String>?): List<String>? {
        val sanitized = rules?.mapNotNull(::sanitizeRuleLine).orEmpty()
        return sanitized.takeIf { it.isNotEmpty() }
    }

    fun sanitizeRuleLine(ruleLine: String): String? {
        val trimmed = ruleLine.trim()
        val separatorIndex = trimmed.indexOf(',')
        if (trimmed.isEmpty() || separatorIndex <= 0) return null

        val rawType = trimmed.substring(0, separatorIndex).trim().uppercase()
        if (rawType in unsupportedRuleTypes) return null
        if (nestedUnsupportedRuleTypePattern.containsMatchIn(trimmed)) return null

        val normalizedParts = trimmed.split(',').map(String::trim).toMutableList()
        if (normalizedParts.size < 2) return null

        val normalizedType = ruleTypeAliases[rawType] ?: rawType
        normalizedParts[0] = normalizedType

        val policyIndex = if (normalizedType == "MATCH") 1 else 2
        if (
            policyIndex < normalizedParts.size &&
                normalizedParts[policyIndex].uppercase() in surgeRejectVariants
        ) {
            normalizedParts[policyIndex] = "REJECT"
        }

        return normalizeRuleAliases(normalizedParts.joinToString(","))
    }

    private fun sanitizeRuleMap(ruleMap: Map<String, List<String>>?): Map<String, List<String>>? {
        val sanitized =
            ruleMap
                ?.mapNotNull { (key, rules) -> sanitizeRules(rules)?.let { key to it } }
                ?.toMap()
                .orEmpty()
        return sanitized.takeIf { it.isNotEmpty() }
    }

    private fun normalizeRuleAliases(ruleLine: String): String {
        var normalized = ruleLine
        ruleTypeAliases.forEach { (rawType, normalizedType) ->
            val regex = aliasPatterns.getValue(rawType)
            normalized = regex.replace(normalized) { match ->
                "${match.groupValues[1]}$normalizedType"
            }
        }
        return normalized
    }
}
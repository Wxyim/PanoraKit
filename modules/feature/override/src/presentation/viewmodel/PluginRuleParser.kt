/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.viewmodel

import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverrideRuleSanitizer

/**
 * Parses Surge/Loon plugin text and extracts rules from the [Rule] section.
 *
 * Rules with types unsupported by mihomo/Clash are dropped. Surge/Loon-specific aliases and
 * built-in REJECT variants are normalized to Clash equivalents.
 */
internal object PluginRuleParser {
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

            val normalizedRule = ConfigurationOverrideRuleSanitizer.sanitizeRuleLine(ruleLine)
            if (normalizedRule != null) {
                result += normalizedRule
            }
        }

        return result
    }
}

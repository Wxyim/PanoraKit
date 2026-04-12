package com.github.yumelira.yumebox.data.util

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteOverrideParserTest {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    @Test
    fun parse_pluginRules_dropsCompositeRulesContainingUnsupportedTypes() {
        val text =
            """
            [Rule]
            AND,((URL-REGEX,"^http:\/\/.+\/amdc\/mobileDispatch"),(USER-AGENT,"Alibaba*")),REJECT
            DOMAIN,example.com,REJECT
            """.trimIndent()

        val result = RemoteOverrideParser.parse(json, text)

        assertEquals(RemoteOverrideContentKind.PluginRules, result.kind)
        assertEquals(listOf("DOMAIN,example.com,REJECT"), result.config.rulesStart)
        assertEquals(1, result.count)
    }

    @Test
    fun parse_jsonConfig_sanitizesCompositeRulesContainingUnsupportedTypes() {
        val text =
            """
            {
              "rules-start": [
                "AND,((URL-REGEX,\"^http:\\/\\/.+\\/amdc\\/mobileDispatch\"),(USER-AGENT,\"Alibaba*\")),REJECT",
                "DOMAIN,example.com,REJECT"
              ]
            }
            """.trimIndent()

        val result = RemoteOverrideParser.parse(json, text)

        assertEquals(RemoteOverrideContentKind.Config, result.kind)
        assertEquals(listOf("DOMAIN,example.com,REJECT"), result.config.rulesStart)
    }

    @Test
    fun parse_pluginRules_returnsNullRulesWhenAllRulesAreDropped() {
        val text =
            """
            [Rule]
            AND,((URL-REGEX,"^http:\/\/.+\/amdc\/mobileDispatch"),(USER-AGENT,"Alibaba*")),REJECT
            """.trimIndent()

        val error = runCatching { RemoteOverrideParser.parse(json, text) }.exceptionOrNull()

        assertEquals("no importable content", error?.message)
    }

    @Test
    fun parse_pluginRules_normalizesNestedDestPortAliasInsideCompositeRule() {
        val text =
            """
            [Rule]
            AND,((DOMAIN,example.com),(DEST-PORT,8008)),REJECT
            """.trimIndent()

        val result = RemoteOverrideParser.parse(json, text)

        assertEquals(
            listOf("AND,((DOMAIN,example.com),(DST-PORT,8008)),REJECT"),
            result.config.rulesStart,
        )
        assertNull(result.config.rules)
    }
}
/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.presentation.usecase

import com.github.nomadboxlab.monadbox.presentation.viewmodel.OverrideImportKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImportOverrideConfigUseCaseTest {
    private val useCase = ImportOverrideConfigUseCase(messages = FakeImportMessages)

    @Test
    fun planFromTextAutoDetect_acceptsConfigurationOverrideJson() {
        val plan =
            useCase
                .planFromTextAutoDetect(rawText = "{}", sourceName = "example-profile.json")
                .getOrThrow()

        assertEquals(OverrideImportKind.Config, plan.result.kind)
        assertEquals(1, plan.result.count)
        assertEquals("example-profile", plan.configs.single().name)
    }

    @Test
    fun planRulesFromSurgePlugin_extractsRulesIntoOverrideConfig() {
        val plan =
            useCase
                .planRulesFromSurgePlugin(
                    pluginText =
                        """
                        [Rule]
                        DOMAIN-SUFFIX,example.com,Proxy
                        IP-CIDR,10.0.0.0/8,DIRECT
                        """,
                    sourceName = "rules.sgmodule",
                )
                .getOrThrow()

        val config = plan.configs.single()
        assertEquals(OverrideImportKind.PluginRules, plan.result.kind)
        assertEquals(2, plan.result.count)
        assertEquals("rules", config.name)
        assertTrue(config.config.rulesStart.orEmpty().contains("DOMAIN-SUFFIX,example.com,Proxy"))
    }

    private object FakeImportMessages : OverrideConfigImportMessages {
        override val pluginNoRules: String = "No rules"
        override val pluginImportDescription: String = "Imported plugin rules"
        override val importEmpty: String = "Empty import"
        override val autoDetectFailed: String = "Auto detect failed"
        override val urlInvalidScheme: String = "Invalid scheme"
        override val urlHttpsRequired: String = "HTTPS required"
        override val urlRedirectInvalid: String = "Invalid redirect"
        override val urlTooManyRedirects: String = "Too many redirects"

        override fun urlHttpError(code: Int): String = "HTTP $code"

        override fun urlContentTooLarge(maxMegabytes: Int): String = "Too large $maxMegabytes"
    }
}

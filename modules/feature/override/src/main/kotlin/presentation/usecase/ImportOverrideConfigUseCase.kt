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

package com.github.nomadboxlab.monadbox.presentation.usecase

import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverride
import com.github.nomadboxlab.monadbox.data.repository.OverrideConfigRepository
import com.github.nomadboxlab.monadbox.data.util.RemoteContentFetcher
import com.github.nomadboxlab.monadbox.data.util.RemoteFetchException
import com.github.nomadboxlab.monadbox.data.util.RemoteFetchFailureReason
import com.github.nomadboxlab.monadbox.domain.model.OverrideConfig
import com.github.nomadboxlab.monadbox.domain.model.OverrideMetadata
import com.github.nomadboxlab.monadbox.presentation.viewmodel.OverrideImportKind
import com.github.nomadboxlab.monadbox.presentation.viewmodel.OverrideImportResult
import com.github.nomadboxlab.monadbox.presentation.viewmodel.PluginRuleParser
import com.github.nomadboxlab.monadbox.presentation.viewmodel.buildImportedConfigName
import com.github.nomadboxlab.monadbox.presentation.viewmodel.parseImportedOverrideConfigs
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

data class OverrideConfigImportPlan(
    val configs: List<OverrideConfig>,
    val result: OverrideImportResult,
)

interface OverrideConfigImportMessages {
    val pluginNoRules: String
    val pluginImportDescription: String
    val importEmpty: String
    val autoDetectFailed: String
    val urlInvalidScheme: String
    val urlHttpsRequired: String

    fun urlHttpError(code: Int): String

    fun urlContentTooLarge(maxMegabytes: Int): String

    val urlRedirectInvalid: String
    val urlTooManyRedirects: String
}

object MLangOverrideConfigImportMessages : OverrideConfigImportMessages {
    override val pluginNoRules: String
        get() = MLang.Override.Import.PluginNoRules

    override val pluginImportDescription: String
        get() = MLang.Override.Import.PluginImportDescription

    override val importEmpty: String
        get() = MLang.Override.Save.ImportEmpty

    override val autoDetectFailed: String
        get() = MLang.Override.Import.AutoDetectFailed

    override val urlInvalidScheme: String
        get() = MLang.Override.Import.UrlInvalidScheme

    override val urlHttpsRequired: String
        get() = MLang.Override.Import.UrlHttpsRequired

    override val urlRedirectInvalid: String
        get() = MLang.Override.Import.UrlRedirectInvalid

    override val urlTooManyRedirects: String
        get() = MLang.Override.Import.UrlTooManyRedirects

    override fun urlHttpError(code: Int): String = MLang.Override.Import.UrlHttpError.format(code)

    override fun urlContentTooLarge(maxMegabytes: Int): String =
        MLang.Override.Import.UrlContentTooLarge.format(maxMegabytes)
}

class ImportOverrideConfigUseCase(
    private val configRepo: OverrideConfigRepository? = null,
    private val messages: OverrideConfigImportMessages = MLangOverrideConfigImportMessages,
) {
    private companion object {
        const val DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS = 86_400L
        const val MAX_REMOTE_CONTENT_BYTES = 2 * 1024 * 1024
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    fun planConfigsFromJson(
        jsonString: String,
        sourceName: String? = null,
    ): Result<OverrideConfigImportPlan> = runCatching {
        val importedConfigs =
            parseImportedOverrideConfigs(
                json = json,
                jsonString = jsonString,
                sourceName = sourceName,
            )
        OverrideConfigImportPlan(
            configs = importedConfigs,
            result = OverrideImportResult(OverrideImportKind.Config, importedConfigs.size),
        )
    }

    fun planRulesFromSurgePlugin(
        pluginText: String,
        sourceName: String? = null,
    ): Result<OverrideConfigImportPlan> = runCatching {
        val rules = PluginRuleParser.parseRules(pluginText)
        require(rules.isNotEmpty()) { messages.pluginNoRules }
        val name = buildImportedConfigName(sourceName, 0, false)
        val now = System.currentTimeMillis()
        val config =
            OverrideConfig(
                id = OverrideMetadata.generateId(),
                name = name,
                description = messages.pluginImportDescription,
                config = ConfigurationOverride(rulesStart = rules),
                isSystem = false,
                createdAt = now,
                updatedAt = now,
            )
        OverrideConfigImportPlan(
            configs = listOf(config),
            result = OverrideImportResult(OverrideImportKind.PluginRules, rules.size),
        )
    }

    fun planFromTextAutoDetect(
        rawText: String,
        sourceName: String? = null,
    ): Result<OverrideConfigImportPlan> = runCatching {
        val text = rawText.trim()
        require(text.isNotEmpty()) { messages.importEmpty }

        val jsonResult =
            runCatching {
                    parseImportedOverrideConfigs(
                        json = json,
                        jsonString = text,
                        sourceName = sourceName,
                    )
                }
                .getOrNull()

        if (!jsonResult.isNullOrEmpty()) {
            return@runCatching OverrideConfigImportPlan(
                configs = jsonResult,
                result = OverrideImportResult(OverrideImportKind.Config, jsonResult.size),
            )
        }

        planRulesFromSurgePlugin(text, sourceName).getOrElse {
            throw IllegalArgumentException(messages.autoDetectFailed)
        }
    }

    suspend fun savePlan(plan: OverrideConfigImportPlan) {
        val repository = requireConfigRepository()
        for (config in plan.configs) {
            repository.save(config)
        }
    }

    suspend fun fetchAndImportConfigFromUrl(url: String): Result<Int> = runCatching {
        val text = fetchUrlText(url)
        val plan = planConfigsFromJson(text, extractUrlFileName(url)).getOrThrow()
        savePlan(plan)
        plan.result.count
    }

    suspend fun fetchAndImportSurgePluginFromUrl(url: String): Result<Int> = runCatching {
        val text = fetchUrlText(url)
        val plan = planRulesFromSurgePlugin(text, extractUrlFileName(url)).getOrThrow()
        savePlan(plan)
        plan.result.count
    }

    suspend fun fetchAndImportAutoFromUrl(url: String): Result<OverrideImportResult> = runCatching {
        val text = fetchUrlText(url)
        val sourceName = extractUrlFileName(url)
        val now = System.currentTimeMillis()
        val plan = planFromRemoteText(text, sourceName, now)
        val config = plan.configs.single()
        val repository = requireConfigRepository()
        repository.save(config)
        repository.updateMetadata(config.id) { metadata ->
            metadata.copy(
                remoteSourceUrl = url.trim(),
                remoteUpdateIntervalSeconds = DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS,
                remoteLastUpdatedAt = now,
                updatedAt = now,
            )
        }
        plan.result
    }

    private fun planFromRemoteText(
        text: String,
        sourceName: String?,
        now: Long,
    ): OverrideConfigImportPlan {
        val jsonResult =
            runCatching {
                    parseImportedOverrideConfigs(
                        json = json,
                        jsonString = text,
                        sourceName = sourceName,
                    )
                }
                .getOrNull()

        if (!jsonResult.isNullOrEmpty()) {
            val imported = jsonResult.first()
            return OverrideConfigImportPlan(
                configs =
                    listOf(
                        imported.copy(
                            id = OverrideMetadata.generateId(),
                            name =
                                imported.name.ifBlank {
                                    buildImportedConfigName(sourceName, 0, false)
                                },
                            description = imported.description ?: messages.pluginImportDescription,
                            isSystem = false,
                            createdAt = now,
                            updatedAt = now,
                        )
                    ),
                result = OverrideImportResult(OverrideImportKind.Config, 1),
            )
        }

        val rules = PluginRuleParser.parseRules(text)
        require(rules.isNotEmpty()) { messages.autoDetectFailed }
        return OverrideConfigImportPlan(
            configs =
                listOf(
                    OverrideConfig(
                        id = OverrideMetadata.generateId(),
                        name = buildImportedConfigName(sourceName, 0, false),
                        description = messages.pluginImportDescription,
                        config = ConfigurationOverride(rulesStart = rules),
                        isSystem = false,
                        createdAt = now,
                        updatedAt = now,
                    )
                ),
            result = OverrideImportResult(OverrideImportKind.PluginRules, rules.size),
        )
    }

    private suspend fun fetchUrlText(urlString: String): String =
        withContext(Dispatchers.IO) {
            try {
                RemoteContentFetcher.fetchText(
                    urlString = urlString,
                    maxBytes = MAX_REMOTE_CONTENT_BYTES,
                    userAgent = "MonadBox",
                )
            } catch (e: RemoteFetchException) {
                throw IllegalArgumentException(
                    when (e.reason) {
                        RemoteFetchFailureReason.InvalidScheme -> messages.urlInvalidScheme
                        RemoteFetchFailureReason.HttpsRequired -> messages.urlHttpsRequired
                        RemoteFetchFailureReason.HttpError ->
                            messages.urlHttpError(e.httpCode ?: -1)
                        RemoteFetchFailureReason.ContentTooLarge ->
                            messages.urlContentTooLarge(e.maxMegabytes ?: 0)
                        RemoteFetchFailureReason.RedirectMissingLocation ->
                            messages.urlRedirectInvalid
                        RemoteFetchFailureReason.TooManyRedirects -> messages.urlTooManyRedirects
                    },
                    e,
                )
            }
        }

    private fun extractUrlFileName(urlString: String): String? {
        return RemoteContentFetcher.extractDecodedFileName(urlString)
    }

    private fun requireConfigRepository(): OverrideConfigRepository {
        return configRepo ?: error("OverrideConfigRepository is required for persistence")
    }
}

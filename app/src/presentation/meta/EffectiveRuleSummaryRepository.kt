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

package com.github.yumelira.yumebox.presentation.meta

import android.content.Context
import com.github.yumelira.yumebox.core.Clash
import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import com.github.yumelira.yumebox.remote.ServiceClient
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.runtime.client.ProxyFacade
import com.github.yumelira.yumebox.service.runtime.state.RuntimeSnapshot
import dev.oom_wg.purejoy.mlang.MLangStatus
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.yaml.snakeyaml.Yaml

data class EffectiveRuleSummary(
    val count: Int? = null,
    val summary: String = MLangStatus.Common.NotAvailable,
)

private enum class ConfigSourceKind {
    Runtime,
    Profile,
}

private data class EffectiveConfigSource(
    val sourceKind: ConfigSourceKind,
    val inspectedConfig: ConfigurationOverride?,
)

class EffectiveRuleSummaryRepository(
    private val context: Context,
    private val proxyFacade: ProxyFacade,
    private val profilesRepository: ProfilesRepository,
) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
    }

    suspend fun load(): EffectiveRuleSummary =
        withContext(Dispatchers.IO) {
            runCatching {
                    val runtimeSnapshot = proxyFacade.runtimeSnapshot.value
                    val activeProfile =
                        runCatching { profilesRepository.queryActiveProfile(ensureDefault = false) }
                            .getOrNull()
                    val profileId =
                        activeProfile?.uuid?.toString()
                            ?: runtimeSnapshot.profileUuid?.takeIf(String::isNotBlank)
                    val configSource =
                        resolveConfigSources(
                                profileId = profileId,
                                runtimeSnapshot = runtimeSnapshot,
                            )
                            .firstOrNull() ?: return@runCatching EffectiveRuleSummary()
                    EffectiveRuleSummary(
                        count = configSource.inspectedConfig?.let(::countEffectiveRules) ?: 0,
                        summary = configSource.sourceKind.toDisplayLabel(),
                    )
                }
                .getOrElse { error ->
                    EffectiveRuleSummary(
                        summary =
                            error.message?.takeIf(String::isNotBlank)
                                ?: MLangStatus.Common.NotAvailable
                    )
                }
        }

    private suspend fun resolveConfigSources(
        profileId: String?,
        runtimeSnapshot: RuntimeSnapshot,
    ): List<EffectiveConfigSource> {
        val runtimeConfigPath =
            runCatching {
                    ServiceClient.connect(context)
                    ServiceClient.clash().queryConfiguration().configPath?.trim().orEmpty()
                }
                .getOrDefault("")

        val candidateFiles =
            buildList {
                    runtimeConfigPath.takeIf(String::isNotBlank)?.let { add(File(it)) }
                    if (!profileId.isNullOrBlank()) {
                        add(File(context.filesDir, "imported/$profileId/runtime.yaml"))
                        add(File(context.filesDir, "clash/profiles/$profileId/runtime.yaml"))
                        add(File(context.filesDir, "imported/$profileId/config.yaml"))
                        add(File(context.filesDir, "clash/profiles/$profileId/config.yaml"))
                    }
                }
                .distinctBy(File::getAbsolutePath)

        return candidateFiles.mapNotNull { selectedFile ->
            if (!selectedFile.exists() || !selectedFile.isFile) {
                return@mapNotNull null
            }
            val yamlText =
                runCatching { selectedFile.readText() }.getOrNull()?.takeIf(String::isNotBlank)
                    ?: return@mapNotNull null
            val sourceKind =
                when {
                    runtimeConfigPath.isNotBlank() &&
                        selectedFile.absolutePath == File(runtimeConfigPath).absolutePath ->
                        ConfigSourceKind.Runtime
                    selectedFile.name.equals("runtime.yaml", ignoreCase = true) ->
                        ConfigSourceKind.Runtime
                    else -> ConfigSourceKind.Profile
                }

            EffectiveConfigSource(
                sourceKind = sourceKind,
                inspectedConfig =
                    Clash.inspectCompiledConfig(yamlText) ?: parseProfileConfigYaml(yamlText),
            )
        }
    }

    private fun countEffectiveRules(config: ConfigurationOverride): Int {
        return config.rules.orEmpty().size +
            config.rulesStart.orEmpty().size +
            config.rulesEnd.orEmpty().size +
            config.subRules.orEmpty().values.sumOf(List<String>::size)
    }

    private fun parseProfileConfigYaml(yamlText: String): ConfigurationOverride? {
        if (yamlText.isBlank()) {
            return ConfigurationOverride()
        }

        val loaded = runCatching { Yaml().load<Any?>(yamlText) }.getOrNull() ?: return null
        val rootElement = yamlValueToJsonElement(loaded)
        val rootObject = rootElement as? JsonObject ?: JsonObject(emptyMap())
        return runCatching {
                json.decodeFromJsonElement(ConfigurationOverride.serializer(), rootObject)
            }
            .getOrNull()
    }

    private fun yamlValueToJsonElement(value: Any?): JsonElement {
        return when (value) {
            null -> JsonNull
            is Map<*, *> -> {
                val content =
                    buildMap<String, JsonElement> {
                        value.forEach { (key, childValue) ->
                            key?.toString()?.let { put(it, yamlValueToJsonElement(childValue)) }
                        }
                    }
                JsonObject(content)
            }
            is List<*> -> JsonArray(value.map(::yamlValueToJsonElement))
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value.toInt())
            is Byte -> JsonPrimitive(value.toInt())
            else -> JsonPrimitive(value.toString())
        }
    }

    private fun ConfigSourceKind.toDisplayLabel(): String {
        return when (this) {
            ConfigSourceKind.Runtime -> MLangStatus.Meta.EffectiveRulesRuntimeSource
            ConfigSourceKind.Profile -> MLangStatus.Meta.EffectiveRulesProfileSource
        }
    }
}

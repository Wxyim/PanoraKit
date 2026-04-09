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

package com.github.yumelira.yumebox.data.repository

import android.content.Context
import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import com.github.yumelira.yumebox.data.util.RemoteContentFetcher
import com.github.yumelira.yumebox.data.util.RemoteOverrideParser
import com.github.yumelira.yumebox.domain.model.MetadataIndex
import com.github.yumelira.yumebox.domain.model.OverrideConfig
import com.github.yumelira.yumebox.domain.model.OverrideMetadata
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

class OverrideConfigRepository(private val context: Context) : OverrideConfigProvider {
    companion object {
        const val INTERNAL_RUNTIME_PREFIX = "__runtime__"
        private const val DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS = 86_400L
        private const val MAX_REMOTE_CONTENT_BYTES = 2 * 1024 * 1024

        fun isInternalRuntimeConfig(id: String): Boolean = id.startsWith(INTERNAL_RUNTIME_PREFIX)
    }

    private val overridesDir = File(context.filesDir, "overrides")
    private val configsDir = File(overridesDir, "configs")
    private val metadataFile = File(overridesDir, "metadata.json")

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
    }

    private val _configsFlow = MutableStateFlow<List<OverrideConfig>>(emptyList())

    @Volatile private var systemPresetsCache: List<OverrideConfig>? = null

    private suspend fun refreshConfigsFlow() {
        _configsFlow.value = getAll()
    }

    private suspend fun updateConfigsFlowSnapshot(
        metadataIndex: MetadataIndex,
        userConfigsById: Map<String, OverrideConfig>,
    ) {
        val currentSnapshot = _configsFlow.value
        if (currentSnapshot.isEmpty()) {
            refreshConfigsFlow()
            return
        }
        val systemPresets =
            currentSnapshot.filter(OverrideConfig::isSystem).ifEmpty { getSystemPresets() }
        _configsFlow.value =
            systemPresets +
                metadataIndex.sortedUserMetadata().mapNotNull { metadata ->
                    userConfigsById[metadata.id]
                }
    }

    override suspend fun getAll(): List<OverrideConfig> =
        withContext(Dispatchers.IO) {
            val systemPresets = getSystemPresets()
            val userConfigs = loadUserConfigs()
            systemPresets + userConfigs
        }

    override fun getAllFlow(): Flow<List<OverrideConfig>> = _configsFlow.asStateFlow()

    override suspend fun getById(id: String): OverrideConfig? =
        withContext(Dispatchers.IO) {
            if (isSystemPreset(id)) {
                return@withContext getSystemPresets().find { it.id == id }
            }
            val metadata = loadMetadataIndex().getById(id) ?: return@withContext null
            val config = loadConfigContent(id) ?: return@withContext null
            OverrideConfig(
                id = metadata.id,
                name = metadata.name,
                description = metadata.description,
                config = config,
                isSystem = false,
                createdAt = metadata.createdAt,
                updatedAt = metadata.updatedAt,
            )
        }

    override suspend fun getSystemPresets(): List<OverrideConfig> =
        withContext(Dispatchers.IO) {
            systemPresetsCache?.let {
                return@withContext it
            }

            try {
                val jsonContent =
                    context.assets.open("override.json").bufferedReader().use { it.readText() }
                val configOverride = json.decodeFromString<ConfigurationOverride>(jsonContent)
                val metadata = OverrideMetadata.createSystemPreset()
                val presets =
                    listOf(
                        OverrideConfig(
                            id = metadata.id,
                            name = metadata.name,
                            description = metadata.description,
                            config = configOverride,
                            isSystem = true,
                            createdAt = metadata.createdAt,
                            updatedAt = metadata.updatedAt,
                        )
                    )
                systemPresetsCache = presets
                presets
            } catch (e: Exception) {
                emptyList()
            }
        }

    override suspend fun getUserConfigs(): List<OverrideConfig> = loadUserConfigs()

    override fun getUserConfigsFlow(): Flow<List<OverrideConfig>> =
        flow { emit(loadUserConfigs()) }.flowOn(Dispatchers.IO)

    override suspend fun save(config: OverrideConfig) =
        withContext(Dispatchers.IO) {
            if (isSystemPreset(config.id)) return@withContext

            configsDir.mkdirs()

            val configFile = configsDir.resolve("${config.id}.json")
            configFile.writeText(encodeConfigContent(config.config))

            val metadataIndex = loadMetadataIndex()
            val existingMetadata = metadataIndex.getById(config.id)
            val metadata =
                OverrideMetadata(
                    id = config.id,
                    name = config.name,
                    description = config.description,
                    isSystem = false,
                    remoteSourceUrl = existingMetadata?.remoteSourceUrl,
                    remoteUpdateIntervalSeconds = existingMetadata?.remoteUpdateIntervalSeconds,
                    remoteLastUpdatedAt = existingMetadata?.remoteLastUpdatedAt,
                    createdAt = config.createdAt,
                    updatedAt = config.updatedAt,
                    sortOrder = existingMetadata?.sortOrder ?: metadataIndex.nextUserSortOrder(),
                )
            val index = metadataIndex.upsert(metadata)
            saveMetadataIndex(index)
            val userConfigsById =
                _configsFlow.value
                    .filterNot(OverrideConfig::isSystem)
                    .associateBy(OverrideConfig::id)
                    .toMutableMap()
                    .apply { put(config.id, config) }
            updateConfigsFlowSnapshot(metadataIndex = index, userConfigsById = userConfigsById)
        }

    override suspend fun delete(id: String): Boolean =
        withContext(Dispatchers.IO) {
            if (isSystemPreset(id)) return@withContext false

            val configFile = configsDir.resolve("$id.json")
            val fileDeleted = !configFile.exists() || configFile.delete()
            val metadataExists = loadMetadataIndex().getById(id) != null
            val deleted = fileDeleted && metadataExists
            if (fileDeleted && metadataExists) {
                val index = loadMetadataIndex().remove(id)
                saveMetadataIndex(index)
                val userConfigsById =
                    _configsFlow.value
                        .filterNot(OverrideConfig::isSystem)
                        .associateBy(OverrideConfig::id)
                        .toMutableMap()
                        .apply { remove(id) }
                updateConfigsFlowSnapshot(metadataIndex = index, userConfigsById = userConfigsById)
                return@withContext true
            }
            if (fileDeleted && !metadataExists) {
                refreshConfigsFlow()
            }
            false
        }

    override suspend fun duplicate(id: String): OverrideConfig? =
        withContext(Dispatchers.IO) {
            val original = getById(id) ?: return@withContext null
            val newMetadata =
                OverrideMetadata(
                    id = OverrideMetadata.generateId(),
                    name = "${original.name} (副本)",
                    description = original.description,
                    isSystem = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                )
            val newConfig =
                OverrideConfig(
                    id = newMetadata.id,
                    name = newMetadata.name,
                    description = newMetadata.description,
                    config = original.config,
                    isSystem = false,
                    createdAt = newMetadata.createdAt,
                    updatedAt = newMetadata.updatedAt,
                )
            save(newConfig)
            newConfig
        }

    override suspend fun exists(id: String): Boolean =
        withContext(Dispatchers.IO) {
            if (isSystemPreset(id)) return@withContext true
            configsDir.resolve("$id.json").exists()
        }

    suspend fun getMetadata(id: String): OverrideMetadata? =
        withContext(Dispatchers.IO) { loadMetadataIndex().getById(id) }

    suspend fun updateMetadata(
        id: String,
        transform: (OverrideMetadata) -> OverrideMetadata,
    ): OverrideMetadata? =
        withContext(Dispatchers.IO) {
            val metadataIndex = loadMetadataIndex()
            val original = metadataIndex.getById(id) ?: return@withContext null
            val updated = transform(original)
            val nextIndex = metadataIndex.upsert(updated)
            saveMetadataIndex(nextIndex)

            val userConfigsById =
                _configsFlow.value
                    .filterNot(OverrideConfig::isSystem)
                    .associateBy(OverrideConfig::id)
            updateConfigsFlowSnapshot(metadataIndex = nextIndex, userConfigsById = userConfigsById)
            updated
        }

    suspend fun listRemoteResources(): List<RemoteOverrideResource> =
        withContext(Dispatchers.IO) {
            val metadataIndex = loadMetadataIndex()
            metadataIndex
                .sortedUserMetadata()
                .filter(OverrideMetadata::isRemoteResource)
                .mapNotNull { metadata ->
                    val sourceUrl =
                        metadata.remoteSourceUrl?.takeIf(String::isNotBlank)
                            ?: return@mapNotNull null
                    val interval =
                        metadata.remoteUpdateIntervalSeconds
                            ?: DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS
                    val config = loadConfigContent(metadata.id)
                    RemoteOverrideResource(
                        id = metadata.id,
                        name = metadata.name,
                        description = metadata.description,
                        sourceUrl = sourceUrl,
                        updateIntervalSeconds = interval,
                        lastUpdatedAt = metadata.remoteLastUpdatedAt ?: metadata.updatedAt,
                        updatedAt = metadata.updatedAt,
                        ruleCount = countOverrideRules(config),
                    )
                }
        }

    suspend fun refreshRemoteResource(id: String): RemoteOverrideResource =
        withContext(Dispatchers.IO) {
            val metadata =
                loadMetadataIndex().getById(id) ?: error("override metadata not found: $id")
            val sourceUrl =
                metadata.remoteSourceUrl?.takeIf(String::isNotBlank)
                    ?: error("remote source url is empty: $id")
            val existingConfig = getById(id) ?: error("override config not found: $id")

            val content = fetchUrlText(urlString = sourceUrl)
            val parsed = RemoteOverrideParser.parse(json, content)
            val now = System.currentTimeMillis()

            save(existingConfig.copy(config = parsed.config, updatedAt = now))
            val updatedMetadata =
                updateMetadata(id) { original ->
                    original.copy(remoteLastUpdatedAt = now, updatedAt = now)
                } ?: error("override metadata update failed: $id")

            RemoteOverrideResource(
                id = id,
                name = existingConfig.name,
                description = existingConfig.description,
                sourceUrl = sourceUrl,
                updateIntervalSeconds =
                    updatedMetadata.remoteUpdateIntervalSeconds
                        ?: DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS,
                lastUpdatedAt = now,
                updatedAt = now,
                ruleCount = countOverrideRules(parsed.config),
            )
        }

    suspend fun restoreConfigState(
        config: OverrideConfig,
        metadata: OverrideMetadata? = null,
    ): OverrideConfig =
        withContext(Dispatchers.IO) {
            save(config)
            metadata?.let { restoreMetadata ->
                val metadataIndex = loadMetadataIndex()
                if (metadataIndex.getById(restoreMetadata.id) != null) {
                    saveMetadataIndex(metadataIndex.upsert(restoreMetadata))
                    val userConfigsById =
                        _configsFlow.value
                            .filterNot(OverrideConfig::isSystem)
                            .associateBy(OverrideConfig::id)
                            .toMutableMap()
                            .apply { put(config.id, config) }
                    updateConfigsFlowSnapshot(
                        metadataIndex = loadMetadataIndex(),
                        userConfigsById = userConfigsById,
                    )
                }
            }
            config
        }

    override fun isSystemPreset(id: String): Boolean {
        return id.startsWith(OverrideMetadata.SYSTEM_PREFIX)
    }

    suspend fun export(config: OverrideConfig): String =
        withContext(Dispatchers.IO) { encodeConfigContent(config.config) }

    suspend fun import(jsonString: String, name: String): OverrideConfig? =
        withContext(Dispatchers.IO) {
            try {
                val configOverride =
                    json.decodeFromString(ConfigurationOverride.serializer(), jsonString)
                val now = System.currentTimeMillis()
                val metadata =
                    OverrideMetadata(
                        id = OverrideMetadata.generateId(),
                        name = name,
                        description = null,
                        isSystem = false,
                        createdAt = now,
                        updatedAt = now,
                    )
                val config =
                    OverrideConfig(
                        id = metadata.id,
                        name = metadata.name,
                        description = metadata.description,
                        config = configOverride,
                        isSystem = false,
                        createdAt = metadata.createdAt,
                        updatedAt = metadata.updatedAt,
                    )
                save(config)
                config
            } catch (e: Exception) {
                null
            }
        }

    fun getConfigFilePath(id: String): File = configsDir.resolve("$id.json")

    fun getConfigsDirectory(): File = configsDir

    suspend fun reorderUserConfigs(orderedIds: List<String>) =
        withContext(Dispatchers.IO) {
            if (orderedIds.isEmpty()) return@withContext

            val metadataIndex = loadMetadataIndex()
            val sortedUserMetadata = metadataIndex.sortedUserMetadata()
            if (sortedUserMetadata.isEmpty()) return@withContext

            val userMetadataById = sortedUserMetadata.associateBy(OverrideMetadata::id)
            val reorderedIds = orderedIds.filter(userMetadataById::containsKey)
            if (reorderedIds.isEmpty()) return@withContext

            val remainingIds =
                sortedUserMetadata.map(OverrideMetadata::id).filterNot(reorderedIds::contains)
            val finalOrder = reorderedIds + remainingIds
            val updatedConfigs = metadataIndex.configs.toMutableMap()
            var hasChanges = false

            finalOrder.forEachIndexed { index, id ->
                val metadata = userMetadataById[id] ?: return@forEachIndexed
                val newSortOrder = index.toLong() + 1L
                if (metadata.sortOrder != newSortOrder) {
                    updatedConfigs[id] = metadata.copy(sortOrder = newSortOrder)
                    hasChanges = true
                }
            }

            if (hasChanges) {
                val updatedIndex = metadataIndex.copy(configs = updatedConfigs)
                saveMetadataIndex(updatedIndex)
                val userConfigsById =
                    _configsFlow.value
                        .filterNot(OverrideConfig::isSystem)
                        .associateBy(OverrideConfig::id)
                updateConfigsFlowSnapshot(
                    metadataIndex = updatedIndex,
                    userConfigsById = userConfigsById,
                )
            }
        }

    private fun loadUserConfigs(): List<OverrideConfig> {
        if (!configsDir.exists()) return emptyList()
        val index = loadMetadataIndex()
        return index.sortedUserMetadata().mapNotNull { metadata ->
            if (isInternalRuntimeConfig(metadata.id)) {
                return@mapNotNull null
            }
            loadConfigContent(metadata.id)?.let { config ->
                OverrideConfig(
                    id = metadata.id,
                    name = metadata.name,
                    description = metadata.description,
                    config = config,
                    isSystem = false,
                    createdAt = metadata.createdAt,
                    updatedAt = metadata.updatedAt,
                )
            }
        }
    }

    fun getConfigJsonContent(id: String): String? {
        val file = configsDir.resolve("$id.json")
        if (!file.exists()) return null
        return try {
            file.readText()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveConfigJsonContent(id: String, content: String): OverrideConfig? =
        withContext(Dispatchers.IO) {
            if (isSystemPreset(id)) return@withContext null

            val metadataIndex = loadMetadataIndex()
            val metadata = metadataIndex.getById(id) ?: return@withContext null
            val decoded = json.decodeFromString(ConfigurationOverride.serializer(), content)
            val updatedAt = System.currentTimeMillis()

            configsDir.mkdirs()
            configsDir.resolve("$id.json").writeText(content)

            val updatedMetadata = metadata.copy(updatedAt = updatedAt)
            val nextIndex = metadataIndex.upsert(updatedMetadata)
            saveMetadataIndex(nextIndex)

            val updatedConfig =
                OverrideConfig(
                    id = id,
                    name = updatedMetadata.name,
                    description = updatedMetadata.description,
                    config = decoded,
                    isSystem = false,
                    createdAt = updatedMetadata.createdAt,
                    updatedAt = updatedMetadata.updatedAt,
                )

            val userConfigsById =
                _configsFlow.value
                    .filterNot(OverrideConfig::isSystem)
                    .associateBy(OverrideConfig::id)
                    .toMutableMap()
                    .apply { put(id, updatedConfig) }
            updateConfigsFlowSnapshot(metadataIndex = nextIndex, userConfigsById = userConfigsById)

            updatedConfig
        }

    private fun loadConfigContent(id: String): ConfigurationOverride? {
        val file = configsDir.resolve("$id.json")
        if (!file.exists()) return null
        return try {
            val raw = file.readText()
            val decoded = json.decodeFromString(ConfigurationOverride.serializer(), raw)
            val cleaned = encodeConfigContent(decoded)
            if (cleaned != raw) {
                file.writeText(cleaned)
            }
            decoded
        } catch (e: Exception) {
            null
        }
    }

    private fun encodeConfigContent(config: ConfigurationOverride): String {
        val element = json.encodeToJsonElement(ConfigurationOverride.serializer(), config)
        val cleaned = pruneJson(element) ?: JsonObject(emptyMap())
        return json.encodeToString(JsonElement.serializer(), cleaned)
    }

    private fun pruneJson(element: JsonElement): JsonElement? {
        return when (element) {
            JsonNull -> null
            is JsonObject -> {
                val cleaned =
                    element.entries
                        .mapNotNull { (key, value) -> pruneJson(value)?.let { key to it } }
                        .toMap()
                if (cleaned.isEmpty()) null else JsonObject(cleaned)
            }
            is JsonArray -> JsonArray(element.mapNotNull(::pruneJson))
            else -> element
        }
    }

    private fun loadMetadataIndex(): MetadataIndex {
        val metadataIndex =
            if (!metadataFile.exists()) {
                MetadataIndex()
            } else {
                try {
                    json.decodeFromString(MetadataIndex.serializer(), metadataFile.readText())
                } catch (e: Exception) {
                    MetadataIndex()
                }
            }
        val normalizedIndex = metadataIndex.normalizeUserSortOrders()
        if (normalizedIndex != metadataIndex) {
            saveMetadataIndex(normalizedIndex)
        }
        return normalizedIndex
    }

    private fun saveMetadataIndex(index: MetadataIndex) {
        overridesDir.mkdirs()
        metadataFile.writeText(json.encodeToString(MetadataIndex.serializer(), index))
    }

    private fun fetchUrlText(urlString: String): String {
        return RemoteContentFetcher.fetchText(
            urlString = urlString,
            maxBytes = MAX_REMOTE_CONTENT_BYTES,
            userAgent = "MonadBox",
        )
    }

    private fun countOverrideRules(config: ConfigurationOverride?): Int {
        if (config == null) return 0
        return config.rules.orEmpty().size +
            config.rulesStart.orEmpty().size +
            config.rulesEnd.orEmpty().size
    }
}

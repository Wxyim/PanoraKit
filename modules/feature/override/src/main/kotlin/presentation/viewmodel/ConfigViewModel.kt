/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.nomadboxlab.monadbox.core.model.ConfigurationOverride
import com.github.nomadboxlab.monadbox.data.repository.ActiveProfileOverrideReloader
import com.github.nomadboxlab.monadbox.data.repository.OverrideConfigRepository
import com.github.nomadboxlab.monadbox.data.repository.OverrideResolver
import com.github.nomadboxlab.monadbox.data.repository.ProfileBindingProvider
import com.github.nomadboxlab.monadbox.data.util.OverridePresetTemplateSelection
import com.github.nomadboxlab.monadbox.data.util.applyPresetTemplateToConfig
import com.github.nomadboxlab.monadbox.domain.model.OverrideConfig
import com.github.nomadboxlab.monadbox.domain.model.OverrideMetadata
import com.github.nomadboxlab.monadbox.presentation.usecase.ImportOverrideConfigUseCase
import com.github.nomadboxlab.monadbox.presentation.util.OverrideSaveEvent
import com.github.nomadboxlab.monadbox.presentation.util.OverrideSaveState
import com.github.nomadboxlab.monadbox.presentation.util.encodeOverrideConfigForDiff
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import timber.log.Timber

data class OverrideEditSession(
    val routeConfigId: String,
    val targetConfigId: String,
    val persistedId: String?,
    val createdAt: Long,
    val name: String,
    val description: String,
    val config: ConfigurationOverride,
    val draftSnapshot: String,
    val persistedName: String,
    val persistedDescription: String,
    val persistedSnapshot: String,
    val remoteSourceUrl: String?,
    val remoteUpdateIntervalSeconds: Long?,
    val remoteLastUpdatedAt: Long?,
) {
    val canSave: Boolean
        get() = name.isNotBlank()

    val hasPersistedChanges: Boolean
        get() =
            name != persistedName ||
                description != persistedDescription ||
                draftSnapshot != persistedSnapshot

    val hasUnsavedInvalidChanges: Boolean
        get() = hasPersistedChanges && !canSave

    val isRemoteResource: Boolean
        get() = !remoteSourceUrl.isNullOrBlank()
}

enum class OverrideImportKind {
    Config,
    PluginRules,
}

data class OverrideImportResult(val kind: OverrideImportKind, val count: Int)

class OverrideConfigViewModel(
    private val configRepo: OverrideConfigRepository,
    private val resolver: OverrideResolver,
    private val bindingProvider: ProfileBindingProvider,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
    private val structuredLogCollector:
        com.github.nomadboxlab.monadbox.domain.model.StructuredLogCollector,
    private val importOverrideConfigUseCase: ImportOverrideConfigUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "OverrideConfigViewModel"
        private const val TEXT_AUTOSAVE_DELAY_MILLIS = 220L
        private const val DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS = 86_400L
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    private val _configs = MutableStateFlow<List<OverrideConfig>>(emptyList())
    val configs: StateFlow<List<OverrideConfig>> = _configs.asStateFlow()

    private val _systemPresets = MutableStateFlow<List<OverrideConfig>>(emptyList())
    val systemPresets: StateFlow<List<OverrideConfig>> = _systemPresets.asStateFlow()

    private val _userConfigs = MutableStateFlow<List<OverrideConfig>>(emptyList())
    val userConfigs: StateFlow<List<OverrideConfig>> = _userConfigs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedConfig = MutableStateFlow<OverrideConfig?>(null)
    val selectedConfig: StateFlow<OverrideConfig?> = _selectedConfig.asStateFlow()

    private val _editSession = MutableStateFlow<OverrideEditSession?>(null)
    val editSession: StateFlow<OverrideEditSession?> = _editSession.asStateFlow()

    private val _saveState = MutableStateFlow<OverrideSaveState>(OverrideSaveState.Idle)
    val saveState: StateFlow<OverrideSaveState> = _saveState.asStateFlow()

    private val _events = MutableSharedFlow<OverrideSaveEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<OverrideSaveEvent> = _events.asSharedFlow()

    private val _usageCountMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val usageCountMap: StateFlow<Map<String, Int>> = _usageCountMap.asStateFlow()
    private val _metadataMap = MutableStateFlow<Map<String, OverrideMetadata>>(emptyMap())
    val metadataMap: StateFlow<Map<String, OverrideMetadata>> = _metadataMap.asStateFlow()
    private var bindingObserverJob: Job? = null
    private val silentSaveLock = Any()
    private val pendingSilentConfigs = mutableMapOf<String, OverrideConfig>()
    private val pendingSilentCallbacks = mutableMapOf<String, ((OverrideConfig) -> Unit)?>()
    private val activeSilentSaveIds = mutableSetOf<String>()
    private var pendingEditSaveJob: Job? = null

    init {
        loadConfigs()
        observeBindingChanges()
    }

    private fun loadConfigs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                val presets = configRepo.getSystemPresets()
                _systemPresets.value = presets

                val userConfigs = configRepo.getUserConfigs()
                _userConfigs.value = userConfigs

                _metadataMap.value =
                    userConfigs.associate { userConfig ->
                        userConfig.id to
                            (configRepo.getMetadata(userConfig.id)
                                ?: OverrideMetadata.create(
                                        name = userConfig.name,
                                        description = userConfig.description,
                                        isSystem = false,
                                    )
                                    .copy(
                                        id = userConfig.id,
                                        createdAt = userConfig.createdAt,
                                        updatedAt = userConfig.updatedAt,
                                    ))
                    }

                _configs.value = presets + userConfigs

                loadUsageCounts()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to load configs")
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadUsageCounts() {
        val countMap = mutableMapOf<String, Int>()
        for (config in _configs.value) {
            countMap[config.id] = resolver.getOverrideUsageCount(config.id)
        }
        _usageCountMap.value = countMap
    }

    fun refresh() {
        loadConfigs()
    }

    fun getConfigJsonContent(configId: String): String? {
        return configRepo.getConfigJsonContent(configId)
    }

    suspend fun saveConfigJsonContent(configId: String, content: String): Result<Unit> {
        if (isCodeEditorReadOnly(configId)) {
            return Result.failure(IllegalStateException(MLang.Override.Save.Failed))
        }
        return try {
            val previousConfig =
                configRepo.getById(configId)
                    ?: return Result.failure(IllegalStateException(MLang.Override.Save.Failed))
            val previousMetadata = configRepo.getMetadata(configId)
            val updatedConfig =
                configRepo.saveConfigJsonContent(configId, content)
                    ?: return Result.failure(IllegalStateException(MLang.Override.Save.Failed))

            updateLocalCacheAfterSave(updatedConfig)
            _metadataMap.update { current ->
                current.toMutableMap().apply {
                    current[configId]?.let { metadata ->
                        put(configId, metadata.copy(updatedAt = updatedConfig.updatedAt))
                    }
                }
            }

            val syncResult = syncActiveOverrideAfterMutation(configId)
            if (syncResult.isFailure) {
                rollbackPersistedConfigMutation(previousConfig, previousMetadata)
            }
            syncResult
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to save config JSON content: %s", configId)
            Result.failure(e)
        }
    }

    fun isCodeEditorReadOnly(configId: String): Boolean {
        return metadataMap.value[configId]?.isRemoteResource == true
    }

    private fun observeBindingChanges() {
        bindingObserverJob?.cancel()
        bindingObserverJob =
            viewModelScope.launch {
                bindingProvider.getAllBindingsFlow().drop(1).collectLatest { loadUsageCounts() }
            }
    }

    fun createConfig(
        name: String,
        description: String? = null,
        onCreated: ((String) -> Unit)? = null,
    ) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val config =
                    OverrideConfig(
                        id = OverrideMetadata.generateId(),
                        name = name,
                        description = description,
                        config = ConfigurationOverride(),
                        isSystem = false,
                        createdAt = now,
                        updatedAt = now,
                    )
                configRepo.save(config)
                loadConfigs()
                onCreated?.invoke(config.id)
                Timber.tag(TAG).i("Created config: ${config.id}")
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to create config")
            }
        }
    }

    fun updateConfig(config: OverrideConfig) {
        saveConfig(config)
    }

    fun saveConfig(config: OverrideConfig) {
        viewModelScope.launch {
            persistConfig(
                config = config,
                emitSuccessEvent = true,
                refreshAfterSave = true,
                updateSaveState = true,
            )
        }
    }

    fun saveConfigSilently(config: OverrideConfig, onSaved: ((OverrideConfig) -> Unit)? = null) {
        val shouldLaunchWorker =
            synchronized(silentSaveLock) {
                pendingSilentConfigs[config.id] = config
                if (onSaved != null) {
                    pendingSilentCallbacks[config.id] = onSaved
                } else {
                    pendingSilentCallbacks.remove(config.id)
                }
                activeSilentSaveIds.add(config.id)
            }

        if (!shouldLaunchWorker) {
            return
        }

        viewModelScope.launch { drainSilentSaveQueue(config.id) }
    }

    private suspend fun persistConfig(
        config: OverrideConfig,
        emitSuccessEvent: Boolean,
        refreshAfterSave: Boolean,
        updateSaveState: Boolean,
        onSaved: ((OverrideConfig) -> Unit)? = null,
    ) {
        if (updateSaveState) {
            _saveState.value = OverrideSaveState.Saving
        }
        try {

            if (configRepo.isSystemPreset(config.id)) {
                Timber.tag(TAG).w("Cannot update system preset: ${config.id}")
                if (updateSaveState) {
                    _saveState.value = OverrideSaveState.Idle
                }
                val error =
                    com.github.nomadboxlab.monadbox.domain.model.StructuredError.configuration(
                        phase = com.github.nomadboxlab.monadbox.domain.model.ErrorPhase.Saving,
                        userVisibleMessage = MLang.Override.Save.PresetNotModifiable,
                        impact =
                            com.github.nomadboxlab.monadbox.domain.model.ErrorImpact
                                .FeatureUnavailable,
                        retryability =
                            com.github.nomadboxlab.monadbox.domain.model.ErrorRetryability
                                .NonRetryable,
                    )
                structuredLogCollector.append(
                    com.github.nomadboxlab.monadbox.domain.model.StructuredLogEntry.failure(
                        action = "override.save",
                        message = error.userVisibleMessage,
                        objectId = config.id,
                        phase = "Saving",
                        errorCategory = "Configuration",
                    )
                )
                _events.tryEmit(OverrideSaveEvent.Failed(error))
                return
            }
            configRepo.save(config)
            val runtimeSynced =
                activeProfileOverrideReloader.reapplyActiveProfileIfUsingOverride(config.id)
            if (refreshAfterSave) {
                loadConfigs()
            } else {
                updateLocalCacheAfterSave(config)
            }
            Timber.tag(TAG).i("Updated config: ${config.id}")
            onSaved?.invoke(config)
            if (emitSuccessEvent) {
                if (runtimeSynced) {
                    _events.emit(OverrideSaveEvent.Saved(config.id))
                } else {
                    val error =
                        com.github.nomadboxlab.monadbox.domain.model.StructuredError.runtime(
                            phase =
                                com.github.nomadboxlab.monadbox.domain.model.ErrorPhase.Reloading,
                            userVisibleMessage = MLang.Override.Save.ApplyFailed,
                            impact =
                                com.github.nomadboxlab.monadbox.domain.model.ErrorImpact.Degraded,
                        )
                    structuredLogCollector.append(
                        com.github.nomadboxlab.monadbox.domain.model.StructuredLogEntry.failure(
                            action = "override.save.apply",
                            message = error.userVisibleMessage,
                            objectId = config.id,
                            phase = "Reloading",
                            errorCategory = "Runtime",
                        )
                    )
                    _events.emit(OverrideSaveEvent.Failed(error))
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to update config")
            val error =
                com.github.nomadboxlab.monadbox.domain.model.StructuredError.fromThrowable(
                    throwable = e,
                    phase = com.github.nomadboxlab.monadbox.domain.model.ErrorPhase.Saving,
                    category = com.github.nomadboxlab.monadbox.domain.model.ErrorCategory.Storage,
                    impact =
                        com.github.nomadboxlab.monadbox.domain.model.ErrorImpact.FeatureUnavailable,
                    userVisibleMessage = e.message ?: MLang.Override.Save.Failed,
                )
            structuredLogCollector.append(
                com.github.nomadboxlab.monadbox.domain.model.StructuredLogEntry.failure(
                    action = "override.save",
                    message = error.userVisibleMessage,
                    objectId = config.id,
                    phase = "Saving",
                    errorCategory = "Storage",
                    detail = e.message,
                )
            )
            _events.emit(OverrideSaveEvent.Failed(error))
        } finally {
            if (updateSaveState) {
                _saveState.value = OverrideSaveState.Idle
            }
        }
    }

    private suspend fun syncActiveOverrideAfterMutation(configId: String): Result<Unit> {
        val runtimeSynced =
            activeProfileOverrideReloader.reapplyActiveProfileIfUsingOverride(configId)
        return if (runtimeSynced) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException(MLang.Override.Save.ApplyFailed))
        }
    }

    private suspend fun rollbackPersistedConfigMutation(
        previousConfig: OverrideConfig,
        previousMetadata: OverrideMetadata?,
    ) {
        configRepo.restoreConfigState(previousConfig, previousMetadata)
        updateLocalCacheAfterSave(previousConfig)
        previousMetadata?.let { metadata ->
            _metadataMap.update { current ->
                current.toMutableMap().apply { put(previousConfig.id, metadata) }
            }
        }
        _selectedConfig.value = previousConfig
        val restoredSnapshot = encodeOverrideConfigForDiff(previousConfig.config)
        _editSession.update { session ->
            if (session == null || session.targetConfigId != previousConfig.id)
                return@update session
            session.copy(
                name = previousConfig.name,
                description = previousConfig.description.orEmpty(),
                config = previousConfig.config,
                draftSnapshot = restoredSnapshot,
                persistedName = previousConfig.name,
                persistedDescription = previousConfig.description.orEmpty(),
                persistedSnapshot = restoredSnapshot,
                remoteSourceUrl = previousMetadata?.remoteSourceUrl,
                remoteUpdateIntervalSeconds = previousMetadata?.remoteUpdateIntervalSeconds,
                remoteLastUpdatedAt = previousMetadata?.remoteLastUpdatedAt,
            )
        }
    }

    private suspend fun drainSilentSaveQueue(configId: String) {
        while (true) {
            val nextRequest =
                synchronized(silentSaveLock) {
                    val nextConfig = pendingSilentConfigs.remove(configId)
                    if (nextConfig == null) {
                        activeSilentSaveIds.remove(configId)
                        null
                    } else {
                        SilentSaveRequest(
                            config = nextConfig,
                            onSaved = pendingSilentCallbacks.remove(configId),
                        )
                    }
                } ?: return

            persistConfig(
                config = nextRequest.config,
                emitSuccessEvent = false,
                refreshAfterSave = false,
                updateSaveState = false,
                onSaved = nextRequest.onSaved,
            )
        }
    }

    private fun createEditSession(
        config: OverrideConfig,
        metadata: OverrideMetadata?,
    ): OverrideEditSession {
        val snapshot = encodeOverrideConfigForDiff(config.config)
        return OverrideEditSession(
            routeConfigId = config.id,
            targetConfigId = config.id,
            persistedId = config.id,
            createdAt = config.createdAt,
            name = config.name,
            description = config.description.orEmpty(),
            config = config.config,
            draftSnapshot = snapshot,
            persistedName = config.name,
            persistedDescription = config.description.orEmpty(),
            persistedSnapshot = snapshot,
            remoteSourceUrl = metadata?.remoteSourceUrl,
            remoteUpdateIntervalSeconds = metadata?.remoteUpdateIntervalSeconds,
            remoteLastUpdatedAt = metadata?.remoteLastUpdatedAt,
        )
    }

    private fun updateEditSession(
        saveDelayMillis: Long,
        transform: (OverrideEditSession) -> OverrideEditSession,
    ) {
        val currentSession = _editSession.value ?: return
        val updatedSession = transform(currentSession)
        if (updatedSession == currentSession) {
            return
        }
        _editSession.value = updatedSession
        scheduleDraftSave(updatedSession, saveDelayMillis)
    }

    private fun scheduleDraftSave(session: OverrideEditSession, delayMillis: Long) {
        pendingEditSaveJob?.cancel()
        pendingEditSaveJob =
            viewModelScope.launch {
                if (delayMillis > 0L) {
                    delay(delayMillis)
                }
                val latestSession = _editSession.value
                if (
                    latestSession == null || latestSession.targetConfigId != session.targetConfigId
                ) {
                    return@launch
                }
                saveDraftSession(latestSession)
            }
    }

    private fun saveDraftSession(session: OverrideEditSession, onSaved: (() -> Unit)? = null) {
        if (!session.canSave || !session.hasPersistedChanges) {
            onSaved?.invoke()
            return
        }

        saveConfigSilently(
            config =
                OverrideConfig(
                    id = session.targetConfigId,
                    name = session.name,
                    description = session.description.takeIf(String::isNotBlank),
                    config = session.config,
                    isSystem = false,
                    createdAt = session.createdAt,
                    updatedAt = System.currentTimeMillis(),
                )
        ) { savedConfig ->
            syncPersistedEditSession(savedConfig)
            onSaved?.invoke()
        }
    }

    private fun syncPersistedEditSession(savedConfig: OverrideConfig) {
        val persistedSnapshot = encodeOverrideConfigForDiff(savedConfig.config)
        _editSession.update { currentSession ->
            if (currentSession == null || currentSession.targetConfigId != savedConfig.id) {
                return@update currentSession
            }
            currentSession.copy(
                persistedId = savedConfig.id,
                createdAt = savedConfig.createdAt,
                persistedName = savedConfig.name,
                persistedDescription = savedConfig.description.orEmpty(),
                persistedSnapshot = persistedSnapshot,
            )
        }
        _selectedConfig.value = savedConfig
    }

    private fun updateLocalCacheAfterSave(config: OverrideConfig) {
        val updatedUserConfigs = _userConfigs.value.toMutableList()
        val existingIndex = updatedUserConfigs.indexOfFirst { it.id == config.id }
        if (existingIndex >= 0) {
            updatedUserConfigs[existingIndex] = config
        } else {
            updatedUserConfigs += config
        }
        _userConfigs.value = updatedUserConfigs
        _configs.value = _systemPresets.value + updatedUserConfigs
        if (_selectedConfig.value?.id == config.id) {
            _selectedConfig.value = config
        }
    }

    fun deleteConfig(id: String) {
        viewModelScope.launch {
            try {
                val shouldResyncRuntime =
                    activeProfileOverrideReloader.isActiveProfileUsingOverride(id)
                val deleted = configRepo.delete(id)
                if (deleted) {
                    if (
                        shouldResyncRuntime &&
                            !activeProfileOverrideReloader.reapplyActiveProfileOverride()
                    ) {
                        Timber.tag(TAG)
                            .w("Override deleted but failed to reapply active profile: $id")
                    }
                    loadConfigs()
                    Timber.tag(TAG).i("Deleted config: $id")
                } else {
                    Timber.tag(TAG).w("Cannot delete config: $id (system preset or not found)")
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to delete config")
            }
        }
    }

    fun duplicateConfig(id: String) {
        viewModelScope.launch {
            try {
                val duplicated = configRepo.duplicate(id)
                if (duplicated != null) {
                    loadConfigs()
                    Timber.tag(TAG).i("Duplicated config: $id -> ${duplicated.id}")
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to duplicate config")
            }
        }
    }

    fun reorderUserConfigs(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val currentConfigs = _userConfigs.value
            if (fromIndex !in currentConfigs.indices || fromIndex == toIndex) {
                return@launch
            }

            val reorderedConfigs =
                currentConfigs.toMutableList().also { configs ->
                    val movingConfig = configs.removeAt(fromIndex)
                    val targetIndex = toIndex.coerceIn(0, configs.size)
                    configs.add(targetIndex, movingConfig)
                }

            _userConfigs.value = reorderedConfigs
            _configs.value = _systemPresets.value + reorderedConfigs

            try {
                configRepo.reorderUserConfigs(reorderedConfigs.map(OverrideConfig::id))
                loadConfigs()
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to reorder configs")
                loadConfigs()
            }
        }
    }

    fun selectConfig(id: String) {
        viewModelScope.launch {
            val config = configRepo.getById(id)
            _selectedConfig.value = config
        }
    }

    fun clearSelectedConfig() {
        _selectedConfig.value = null
    }

    fun startEditSession(configId: String) {
        viewModelScope.launch {
            val currentSession = _editSession.value
            if (currentSession?.routeConfigId == configId) {
                return@launch
            }

            pendingEditSaveJob?.cancel()

            if (configId == "new") {
                _selectedConfig.value = null
                val emptyConfig = ConfigurationOverride()
                _editSession.value =
                    OverrideEditSession(
                        routeConfigId = configId,
                        targetConfigId = OverrideMetadata.generateId(),
                        persistedId = null,
                        createdAt = System.currentTimeMillis(),
                        name = "",
                        description = "",
                        config = emptyConfig,
                        draftSnapshot = encodeOverrideConfigForDiff(emptyConfig),
                        persistedName = "",
                        persistedDescription = "",
                        persistedSnapshot = encodeOverrideConfigForDiff(ConfigurationOverride()),
                        remoteSourceUrl = null,
                        remoteUpdateIntervalSeconds = null,
                        remoteLastUpdatedAt = null,
                    )
                return@launch
            }

            val config = configRepo.getById(configId)
            val metadata = config?.id?.let { configRepo.getMetadata(it) }
            _selectedConfig.value = config
            _editSession.value = config?.let { createEditSession(it, metadata) }
        }
    }

    fun updateDraftRemoteSourceUrl(value: String?) {
        val session = _editSession.value ?: return
        if (!session.isRemoteResource || session.persistedId.isNullOrBlank()) {
            return
        }
        val normalizedUrl = value?.trim()?.takeIf(String::isNotBlank)
        _editSession.value = session.copy(remoteSourceUrl = normalizedUrl)

        viewModelScope.launch {
            val persistedId = session.persistedId ?: return@launch
            val now = System.currentTimeMillis()
            configRepo.updateMetadata(persistedId) { metadata ->
                metadata.copy(remoteSourceUrl = normalizedUrl, updatedAt = now)
            }
            loadConfigs()
        }
    }

    fun updateDraftRemoteIntervalSeconds(value: String?) {
        val session = _editSession.value ?: return
        if (!session.isRemoteResource || session.persistedId.isNullOrBlank()) {
            return
        }
        val parsedInterval =
            value?.trim()?.toLongOrNull()?.coerceAtLeast(60L)
                ?: DEFAULT_REMOTE_UPDATE_INTERVAL_SECONDS
        _editSession.value = session.copy(remoteUpdateIntervalSeconds = parsedInterval)

        viewModelScope.launch {
            val persistedId = session.persistedId ?: return@launch
            val now = System.currentTimeMillis()
            configRepo.updateMetadata(persistedId) { metadata ->
                metadata.copy(remoteUpdateIntervalSeconds = parsedInterval, updatedAt = now)
            }
            loadConfigs()
        }
    }

    fun updateDraftName(value: String) {
        updateEditSession(TEXT_AUTOSAVE_DELAY_MILLIS) { session -> session.copy(name = value) }
    }

    fun updateDraftDescription(value: String) {
        updateEditSession(TEXT_AUTOSAVE_DELAY_MILLIS) { session ->
            session.copy(description = value)
        }
    }

    fun updateDraftConfig(updatedConfig: ConfigurationOverride, saveImmediately: Boolean = true) {
        updateEditSession(
            saveDelayMillis = if (saveImmediately) 0L else TEXT_AUTOSAVE_DELAY_MILLIS
        ) { session ->
            session.copy(
                config = updatedConfig,
                draftSnapshot = encodeOverrideConfigForDiff(updatedConfig),
            )
        }
    }

    fun mutateDraftConfig(
        saveImmediately: Boolean = true,
        transform: (ConfigurationOverride) -> ConfigurationOverride,
    ) {
        val currentSession = _editSession.value ?: return
        updateDraftConfig(
            updatedConfig = transform(currentSession.config),
            saveImmediately = saveImmediately,
        )
    }

    fun applyPresetTemplate(selection: OverridePresetTemplateSelection) {
        mutateDraftConfig(saveImmediately = true) { currentConfig ->
            applyPresetTemplateToConfig(base = currentConfig, selection = selection)
        }
    }

    fun saveDraftNow(onSaved: (() -> Unit)? = null) {
        pendingEditSaveJob?.cancel()
        val session = _editSession.value
        if (session == null || !session.canSave || !session.hasPersistedChanges) {
            onSaved?.invoke()
            return
        }
        saveDraftSession(session, onSaved)
    }

    fun flushDraftSave(onSaved: (() -> Unit)? = null) {
        pendingEditSaveJob?.cancel()
        saveDraftNow(onSaved)
    }

    fun clearEditSession() {
        pendingEditSaveJob?.cancel()
        pendingEditSaveJob = null
        _editSession.value = null
        clearSelectedConfig()
    }

    fun importConfigsFromJson(jsonString: String, sourceName: String? = null): Result<Int> {
        val plan =
            importOverrideConfigUseCase
                .planConfigsFromJson(jsonString = jsonString, sourceName = sourceName)
                .getOrElse { error ->
                    Timber.tag(TAG).e(error, "Failed to import config")
                    return Result.failure(error)
                }
        saveImportPlan(plan)
        return Result.success(plan.result.count)
    }

    fun importRulesFromSurgePlugin(pluginText: String, sourceName: String? = null): Result<Int> {
        val plan =
            importOverrideConfigUseCase
                .planRulesFromSurgePlugin(pluginText = pluginText, sourceName = sourceName)
                .getOrElse { error ->
                    Timber.tag(TAG).e(error, "Failed to import Surge plugin rules")
                    return Result.failure(error)
                }
        saveImportPlan(plan)
        return Result.success(plan.result.count)
    }

    fun importFromTextAutoDetect(
        rawText: String,
        sourceName: String? = null,
    ): Result<OverrideImportResult> {
        val plan =
            importOverrideConfigUseCase
                .planFromTextAutoDetect(rawText = rawText, sourceName = sourceName)
                .getOrElse { error ->
                    Timber.tag(TAG).e(error, "Failed to auto-detect imported content")
                    return Result.failure(error)
                }
        saveImportPlan(plan)
        return Result.success(plan.result)
    }

    private fun saveImportPlan(
        plan: com.github.nomadboxlab.monadbox.presentation.usecase.OverrideConfigImportPlan
    ) {
        viewModelScope.launch {
            runCatching {
                    importOverrideConfigUseCase.savePlan(plan)
                    loadConfigs()
                }
                .onFailure { error -> Timber.tag(TAG).e(error, "Failed to persist import") }
        }
    }

    fun exportAllConfigs(): String {
        return try {
            val configs = _userConfigs.value
            if (configs.isEmpty()) {
                "[]"
            } else {
                json.encodeToString(configs)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to export configs")
            "[]"
        }
    }

    fun exportConfig(id: String): String? {
        return try {
            val config = _configs.value.find { it.id == id } ?: return null
            json.encodeToString(config)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to export config")
            null
        }
    }

    fun getUsageCount(id: String): Int {
        return usageCountMap.value[id] ?: 0
    }

    suspend fun isConfigInUse(id: String): Boolean {
        return resolver.isOverrideInUse(id)
    }

    suspend fun getProfilesUsingConfig(id: String): List<String> {
        return resolver.getProfilesUsingOverride(id)
    }

    fun isSystemPreset(id: String): Boolean {
        return configRepo.isSystemPreset(id)
    }

    suspend fun fetchAndImportConfigFromUrl(url: String): Result<Int> {
        return importOverrideConfigUseCase
            .fetchAndImportConfigFromUrl(url)
            .onSuccess { loadConfigs() }
            .onFailure { error -> Timber.tag(TAG).e(error, "Failed to import config from URL") }
    }

    suspend fun fetchAndImportSurgePluginFromUrl(url: String): Result<Int> {
        return importOverrideConfigUseCase
            .fetchAndImportSurgePluginFromUrl(url)
            .onSuccess { loadConfigs() }
            .onFailure { error ->
                Timber.tag(TAG).e(error, "Failed to import Surge plugin from URL")
            }
    }

    suspend fun fetchAndImportAutoFromUrl(url: String): Result<OverrideImportResult> {
        return importOverrideConfigUseCase
            .fetchAndImportAutoFromUrl(url)
            .onSuccess { loadConfigs() }
            .onFailure { error ->
                Timber.tag(TAG).e(error, "Failed to auto-import content from URL")
            }
    }

    suspend fun refreshRemoteOverride(configId: String): Result<Unit> {
        return try {
            val previousConfig =
                configRepo.getById(configId)
                    ?: return Result.failure(IllegalStateException(MLang.Override.Save.Failed))
            val previousMetadata = configRepo.getMetadata(configId)
            configRepo.refreshRemoteResource(id = configId)
            loadConfigs()
            val syncResult = syncActiveOverrideAfterMutation(configId)
            if (syncResult.isFailure) {
                rollbackPersistedConfigMutation(previousConfig, previousMetadata)
                loadConfigs()
            }
            syncResult
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to refresh remote override: $configId")
            Result.failure(e)
        }
    }
}

private data class SilentSaveRequest(
    val config: OverrideConfig,
    val onSaved: ((OverrideConfig) -> Unit)? = null,
)

@Serializable
private data class OverrideConfigImportEnvelope(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val config: ConfigurationOverride? = null,
    val isSystem: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)

internal fun parseImportedOverrideConfigs(
    json: Json,
    jsonString: String,
    sourceName: String? = null,
    nowProvider: () -> Long = System::currentTimeMillis,
): List<OverrideConfig> {
    val normalizedJsonString = jsonString.trim()
    require(normalizedJsonString.isNotEmpty()) { MLang.Override.Save.ImportEmpty }

    val rootElement = json.parseToJsonElement(normalizedJsonString)
    val importedElements =
        when (rootElement) {
            is JsonArray -> rootElement
            else -> JsonArray(listOf(rootElement))
        }
    val hasMultipleEntries = importedElements.size > 1

    return importedElements.mapIndexed { index, element ->
        parseImportedOverrideConfigEntry(
            json = json,
            element = element,
            sourceName = sourceName,
            index = index,
            hasMultipleEntries = hasMultipleEntries,
            now = nowProvider(),
        )
    }
}

private fun parseImportedOverrideConfigEntry(
    json: Json,
    element: JsonElement,
    sourceName: String?,
    index: Int,
    hasMultipleEntries: Boolean,
    now: Long,
): OverrideConfig {
    runCatching {
        return json.decodeFromJsonElement(OverrideConfig.serializer(), element)
    }

    val importEnvelope =
        runCatching {
                json.decodeFromJsonElement(OverrideConfigImportEnvelope.serializer(), element)
            }
            .getOrNull()

    if (importEnvelope?.config != null) {
        return OverrideConfig(
            id = importEnvelope.id?.takeIf(String::isNotBlank) ?: OverrideMetadata.generateId(),
            name =
                importEnvelope.name?.takeIf(String::isNotBlank)
                    ?: buildImportedConfigName(sourceName, index, hasMultipleEntries),
            description = importEnvelope.description?.takeIf(String::isNotBlank),
            config = importEnvelope.config,
            isSystem = false,
            createdAt = importEnvelope.createdAt ?: now,
            updatedAt = importEnvelope.updatedAt ?: now,
        )
    }

    val configurationOverride =
        json.decodeFromJsonElement(ConfigurationOverride.serializer(), element)
    return OverrideConfig(
        id = OverrideMetadata.generateId(),
        name = buildImportedConfigName(sourceName, index, hasMultipleEntries),
        description = null,
        config = configurationOverride,
        isSystem = false,
        createdAt = now,
        updatedAt = now,
    )
}

internal fun buildImportedConfigName(
    sourceName: String?,
    index: Int,
    hasMultipleEntries: Boolean,
): String {
    val baseName =
        normalizeImportedConfigSourceName(sourceName) ?: MLang.Override.Save.ImportDefaultName
    return if (hasMultipleEntries) {
        "$baseName ${index + 1}"
    } else {
        baseName
    }
}

internal fun normalizeImportedConfigSourceName(sourceName: String?): String? {
    var normalizedName =
        sourceName
            ?.substringAfterLast('/')
            ?.substringAfterLast('\\')
            ?.trim()
            ?.takeIf(String::isNotBlank) ?: return null

    val removableSuffixes = listOf(".json", ".yaml", ".yml", ".sgmodule", ".lpx")
    while (true) {
        val matchedSuffix =
            removableSuffixes.firstOrNull { suffix ->
                normalizedName.length > suffix.length &&
                    normalizedName.endsWith(suffix, ignoreCase = true)
            } ?: break
        normalizedName = normalizedName.dropLast(matchedSuffix.length).trimEnd()
    }

    return normalizedName.takeIf(String::isNotBlank)
}

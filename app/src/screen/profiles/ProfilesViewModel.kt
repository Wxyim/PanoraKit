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

package com.github.yumelira.yumebox.screen.profiles

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.yumelira.yumebox.core.Clash
import com.github.yumelira.yumebox.core.model.CompileRequest
import com.github.yumelira.yumebox.core.model.ConfigurationOverride
import com.github.yumelira.yumebox.core.model.FetchStatus
import com.github.yumelira.yumebox.data.repository.ActiveProfileOverrideReloader
import com.github.yumelira.yumebox.data.repository.ProfileBindingProvider
import com.github.yumelira.yumebox.data.store.LinkOpenMode
import com.github.yumelira.yumebox.data.store.Preference
import com.github.yumelira.yumebox.data.store.ProfileLink
import com.github.yumelira.yumebox.data.store.ProfileLinksStorage
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSaveDecision
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSaveOutcome
import com.github.yumelira.yumebox.feature.editor.screen.ConfigPreviewSavePhase
import com.github.yumelira.yumebox.presentation.runtime.HandledRuntimeActionFailure
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionExecutor
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.yumelira.yumebox.presentation.runtime.RuntimeActionOutcome
import com.github.yumelira.yumebox.presentation.runtime.VpnPermissionCoordinator
import com.github.yumelira.yumebox.presentation.runtime.getOrThrowHandled
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.service.remote.IFetchObserver
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.github.yumelira.yumebox.service.runtime.util.sendProfileChanged
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import java.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import timber.log.Timber

class ProfilesViewModel(
    application: Application,
    private val profilesRepository: ProfilesRepository,
    profileLinksStorage: ProfileLinksStorage,
    private val bindingProvider: ProfileBindingProvider,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
    private val runtimeActionExecutor: RuntimeActionExecutor,
    private val vpnPermissionCoordinator: VpnPermissionCoordinator,
) : AndroidViewModel(application) {
    companion object {
        private const val BLANK_PROFILE_SOURCE = "blank://local-config"
        private const val MAX_LOCAL_FILE_BYTES = 50L * 1024 * 1024 // 50 MiB
        private const val REMOTE_FETCH_POLL_INTERVAL_MS = 120L
        private const val LOCAL_UNVALIDATED_MARKER_FILE = ".local-unvalidated-profile"
    }

    val linkOpenMode: Preference<LinkOpenMode> = profileLinksStorage.linkOpenMode
    val links: Preference<List<ProfileLink>> = profileLinksStorage.links
    val defaultLinkId: Preference<String> = profileLinksStorage.defaultLinkId

    private val profileGuiJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    fun setOpenMode(mode: LinkOpenMode) = linkOpenMode.set(mode)

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<Profile?>(null)
    val activeProfile: StateFlow<Profile?> = _activeProfile.asStateFlow()

    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState.asStateFlow()

    private val _downloadProgress = MutableStateFlow<DownloadProgress?>(null)
    val downloadProgress: StateFlow<DownloadProgress?> = _downloadProgress.asStateFlow()

    init {
        refreshProfiles()
    }

    private fun unknownErrorMessage(): String = MLang.ProfilesVM.Error.Unknown

    private fun Throwable.uiErrorMessage(): String = runtimeGatewayMessage(unknownErrorMessage())

    private fun launchWithLoading(
        failureLog: String,
        failureMessage: (Exception) -> String,
        onFailure: (() -> Unit)? = null,
        block: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)
                block()
            } catch (_: HandledRuntimeActionFailure) {
                onFailure?.invoke()
            } catch (e: Exception) {
                Timber.e(e, failureLog)
                showError(failureMessage(e))
                onFailure?.invoke()
            } finally {
                setLoading(false)
            }
        }
    }

    fun refreshProfiles() {
        launchWithLoading(
            failureLog = "Failed to refresh profiles",
            failureMessage = { e ->
                MLang.ProfilesVM.Message.UpdateFailed.format(e.uiErrorMessage())
            },
        ) {
            refreshProfilesNow()
        }
    }

    suspend fun refreshProfilesNow() {
        val allProfiles = profilesRepository.queryAllProfiles()
        val active = profilesRepository.queryActiveProfile(ensureDefault = true)
        _profiles.value = allProfiles
        _activeProfile.value = active
    }

    fun createProfile(
        type: Profile.Type,
        name: String,
        source: String = "",
        interval: Long = 0L,
        fileUri: Uri? = null,
    ) {
        launchWithLoading(
            failureLog = "Failed to create profile",
            failureMessage = { e -> MLang.ProfilesVM.Message.AddFailed.format(e.uiErrorMessage()) },
            onFailure = { _downloadProgress.value = null },
        ) {
            var createdUuid: UUID? = null

            try {
                val uuid = profilesRepository.createProfile(type, name, source)
                createdUuid = uuid

                _downloadProgress.value =
                    DownloadProgress(percent = 0, message = MLang.ProfilesVM.Progress.Preparing)

                val observer = IFetchObserver { status ->
                    _downloadProgress.value = status.toDownloadProgress()
                }

                if (type == Profile.Type.File && fileUri != null) {
                    copyFileToImportedDir(fileUri, uuid)
                }

                profilesRepository.updateProfile(uuid, observer)
                _downloadProgress.value =
                    DownloadProgress(
                        percent = 100,
                        message = MLang.ProfilesVM.Progress.ImportComplete,
                        isCompleted = true,
                    )

                showMessage(MLang.ProfilesVM.Message.ProfileAdded.format(name))
                refreshProfiles()
                Timber.i("Profile created: $uuid")
            } catch (error: Exception) {
                if (createdUuid != null) {
                    rollbackCreatedProfile(createdUuid)
                }
                throw error
            }
        }
    }

    fun createBlankProfile(name: String, initialContent: String, onCreated: (UUID) -> Unit) {
        launchWithLoading(
            failureLog = "Failed to create blank profile",
            failureMessage = { e -> MLang.ProfilesVM.Message.AddFailed.format(e.uiErrorMessage()) },
        ) {
            val profileName = name.ifBlank { MLang.ProfilesPage.Input.NewProfile }
            val uuid =
                profilesRepository.createProfile(
                    type = Profile.Type.File,
                    name = profileName,
                    source = BLANK_PROFILE_SOURCE,
                )

            writeBlankProfileContent(uuid, initialContent)
            showMessage(MLang.ProfilesVM.Message.ProfileAdded.format(profileName))
            refreshProfiles()
            onCreated(uuid)
            Timber.i("Blank profile created: $uuid")
        }
    }

    private suspend fun copyFileToImportedDir(uri: Uri, uuid: UUID) {
        withContext(Dispatchers.IO) {
            val context = getApplication<Application>()
            val importedDir = File(context.filesDir, "imported/${uuid}")
            importedDir.mkdirs()

            val inputFile =
                context.contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("Failed to open file: $uri")

            val outputFile = File(importedDir, "config.yaml")
            var totalBytesWritten = 0L
            inputFile.use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalBytesWritten += bytesRead
                        if (totalBytesWritten > MAX_LOCAL_FILE_BYTES) {
                            error(
                                "Imported file exceeds ${MAX_LOCAL_FILE_BYTES / 1024 / 1024} MiB limit"
                            )
                        }
                    }
                }
            }
            Timber.d("File copied: ${outputFile.absolutePath} ($totalBytesWritten bytes)")
        }
    }

    private suspend fun writeBlankProfileContent(uuid: UUID, content: String) {
        withContext(Dispatchers.IO) {
            val context = getApplication<Application>()
            val importedDir = File(context.filesDir, "imported/${uuid}")
            importedDir.mkdirs()

            val outputFile = File(importedDir, "config.yaml")
            outputFile.writeText(content)
            Timber.d("Blank profile initialized: ${outputFile.absolutePath}")
        }
    }

    fun cloneProfile(uuid: UUID) {
        launchWithLoading(
            failureLog = "Failed to clone profile",
            failureMessage = { e -> MLang.ProfilesVM.Message.AddFailed.format(e.uiErrorMessage()) },
        ) {
            val newUuid = profilesRepository.cloneProfile(uuid)
            showMessage(MLang.ProfilesVM.Message.ProfileAdded.format("Clone"))
            refreshProfiles()
            Timber.i("Profile cloned: from=$uuid to=$newUuid")
        }
    }

    fun deleteProfile(uuid: UUID) {
        launchWithLoading(
            failureLog = "Failed to delete profile",
            failureMessage = { e ->
                MLang.ProfilesVM.Message.DeleteFailed.format(e.uiErrorMessage())
            },
        ) {
            profilesRepository
                .queryProfileByUUID(uuid)
                ?.takeIf { it.active }
                ?.let { activeProfile ->
                    runtimeActionExecutor
                        .activateProfile(
                            operation = "profiles:delete-active",
                            profileId = activeProfile.uuid,
                            enabled = false,
                            presentation =
                                RuntimeActionFailurePresentation.Global(message = { reason ->
                                    MLang.ProfilesVM.Message.DeleteFailed.format(reason)
                                }),
                        ).getOrThrowHandled()
                }
            profilesRepository.deleteProfile(uuid)
            runCatching { bindingProvider.removeBinding(uuid.toString()) }
                .onFailure { error ->
                    Timber.w(error, "Failed to remove profile binding after delete: %s", uuid)
                }
            showMessage(MLang.ProfilesVM.Message.ProfileDeleted)
            refreshProfiles()
            Timber.i("Profile deleted: $uuid")
        }
    }

    fun activateProfile(uuid: UUID) {
        launchWithLoading(
            failureLog = "Failed to activate profile",
            failureMessage = { e ->
                MLang.ProfilesVM.Message.ToggleFailed.format(e.uiErrorMessage())
            },
        ) {
            val outcome =
                runtimeActionExecutor.activateProfile(
                    operation = "profiles:activate",
                    profileId = uuid,
                    enabled = true,
                    presentation =
                        RuntimeActionFailurePresentation.Global(message = { reason ->
                            MLang.ProfilesVM.Message.ToggleFailed.format(reason)
                        }),
                )
            when (outcome) {
                is RuntimeActionOutcome.Success -> Unit
                is RuntimeActionOutcome.PermissionRequired -> {
                    vpnPermissionCoordinator.requestPermission(outcome.intent) {
                        activateProfile(uuid)
                    }
                    throw HandledRuntimeActionFailure()
                }
                RuntimeActionOutcome.FailureHandled -> throw HandledRuntimeActionFailure()
            }
            showMessage(MLang.ProfilesVM.Message.ProfileUpdated.format("Active"))
            refreshProfiles()
            Timber.i("Profile activated: $uuid")
        }
    }

    fun updateProfile(uuid: UUID) {
        launchWithLoading(
            failureLog = "Failed to update profile",
            failureMessage = { e ->
                MLang.ProfilesVM.Message.UpdateFailed.format(e.uiErrorMessage())
            },
            onFailure = { _downloadProgress.value = null },
        ) {
            _downloadProgress.value =
                DownloadProgress(percent = 0, message = MLang.ProfilesVM.Progress.Preparing)

            val observer = IFetchObserver { status ->
                _downloadProgress.value = status.toDownloadProgress()
            }

            updateProfileNow(uuid, observer)

            _downloadProgress.value =
                DownloadProgress(
                    percent = 100,
                    message = MLang.ProfilesVM.Progress.ImportComplete,
                    isCompleted = true,
                )
            showMessage(MLang.ProfilesVM.Message.ProfileUpdated.format(uuid.toString()))
            refreshProfiles()
            Timber.i("Profile updated: $uuid")
        }
    }

    fun patchProfile(uuid: UUID, name: String, source: String, interval: Long) {
        launchWithLoading(
            failureLog = "Failed to patch profile",
            failureMessage = { e ->
                MLang.ProfilesVM.Message.UpdateFailed.format(e.uiErrorMessage())
            },
        ) {
            patchProfileNow(uuid, name, source, interval)
            showMessage(MLang.ProfilesVM.Message.ProfileUpdated.format(name))
            refreshProfiles()
            Timber.i("Profile patched: $uuid")
        }
    }

    fun importProfileFromFile(uri: Uri, name: String) {
        launchWithLoading(
            failureLog = "Failed to import profile",
            failureMessage = { e ->
                MLang.ProfilesVM.Message.ImportFailed.format(e.uiErrorMessage())
            },
            onFailure = { _downloadProgress.value = null },
        ) {
            _downloadProgress.value =
                DownloadProgress(percent = 0, message = MLang.ProfilesVM.Progress.ImportPreparing)

            var createdUuid: UUID? = null
            try {
                val uuid = profilesRepository.createProfile(Profile.Type.File, name, uri.toString())
                createdUuid = uuid
                copyFileToImportedDir(uri, uuid)

                _downloadProgress.value =
                    DownloadProgress(percent = 50, message = MLang.ProfilesVM.Progress.Verifying)

                val observer = IFetchObserver { status ->
                    _downloadProgress.value = status.toDownloadProgress()
                }
                profilesRepository.updateProfile(uuid, observer)

                _downloadProgress.value =
                    DownloadProgress(
                        percent = 100,
                        message = MLang.ProfilesVM.Progress.ImportComplete,
                        isCompleted = true,
                    )
                showMessage(MLang.ProfilesVM.Message.ProfileImported.format(name))
                refreshProfiles()
                Timber.i("Profile imported from file: $uuid")

                _downloadProgress.value = null
            } catch (error: Exception) {
                if (createdUuid != null) {
                    rollbackCreatedProfile(createdUuid)
                }
                throw error
            }
        }
    }

    suspend fun updateProfileNow(uuid: UUID, observer: IFetchObserver? = null): Profile {
        profilesRepository.updateProfile(uuid, observer)
        runtimeActionExecutor
            .reloadIfActiveProfile(
                operation = "profiles:update-profile",
                profileId = uuid,
                presentation =
                    RuntimeActionFailurePresentation.Runtime(
                        fallbackMessage = MLang.ProfilesVM.Error.Unknown,
                    ),
            ).getOrThrowHandled()
        refreshProfilesNow()
        return profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
    }

    suspend fun patchProfileNow(uuid: UUID, name: String, source: String, interval: Long): Profile {
        val previousProfile =
            profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
        try {
            profilesRepository.patchProfile(uuid, name, source, interval)
            runtimeActionExecutor
                .reloadIfActiveProfile(
                    operation = "profiles:patch-profile",
                    profileId = uuid,
                    presentation =
                        RuntimeActionFailurePresentation.Runtime(
                            fallbackMessage = MLang.ProfilesVM.Error.Unknown,
                        ),
                ).getOrThrowHandled()
        } catch (error: Exception) {
            runCatching {
                profilesRepository.patchProfile(
                    uuid,
                    previousProfile.name,
                    previousProfile.source,
                    previousProfile.interval,
                )
            }
            throw error
        }
        refreshProfilesNow()
        return profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
    }

    suspend fun setProfileEnabledNow(uuid: UUID, enabled: Boolean): Profile {
        runtimeActionExecutor
            .activateProfile(
                operation = "profiles:set-active",
                profileId = uuid,
                enabled = enabled,
                presentation =
                    RuntimeActionFailurePresentation.Global(message = { reason ->
                        MLang.ProfilesVM.Message.ToggleFailed.format(reason)
                    }),
            ).getOrThrowHandled()
        refreshProfilesNow()
        return profilesRepository.queryProfileByUUID(uuid)
            ?: error("Profile not found after profile activation: $uuid")
    }

    suspend fun saveProfileConfigContent(
        uuid: UUID,
        content: String,
        onPhaseChanged: (ConfigPreviewSavePhase) -> Unit = {},
        decisionProvider: () -> ConfigPreviewSaveDecision = { ConfigPreviewSaveDecision.Continue },
        stopRuntime: suspend () -> Unit = {},
    ): ConfigPreviewSaveOutcome {
        val profile = profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
        val liveConfigFile = resolveProfileConfigFile(uuid)
        val liveProfileDir = liveConfigFile.parentFile ?: error("Profile directory not found: $uuid")
        val stagingDir = createProfileSaveStagingDirectory(uuid)
        var remoteFetchStarted = false
        var shouldDeleteStagingDir = true

        try {
            cleanupOldProfileSaveStagingDirectories(uuid, keep = stagingDir)

            withContext(Dispatchers.IO) {
                if (liveProfileDir.exists()) {
                    liveProfileDir.copyRecursively(stagingDir, overwrite = true)
                } else {
                    stagingDir.mkdirs()
                }
            }

            val stagedConfigFile = stagingDir.resolve(liveConfigFile.name)

            onPhaseChanged(ConfigPreviewSavePhase.LocalSaving)
            withContext(Dispatchers.IO) {
                stagedConfigFile.parentFile?.mkdirs()
                stagedConfigFile.writeText(content)
            }

            onPhaseChanged(ConfigPreviewSavePhase.Validating)
            validateStagedProfileConfig(uuid, stagingDir, stagedConfigFile)

            onPhaseChanged(ConfigPreviewSavePhase.FetchingRemoteResources)
            when (decisionProvider()) {
                ConfigPreviewSaveDecision.ContinueEditing -> {
                    return ConfigPreviewSaveOutcome.ResumeEditing
                }

                ConfigPreviewSaveDecision.SaveLocally -> {
                    commitLocalConfigContent(liveConfigFile, content)
                    setLocalUnvalidatedMarker(uuid, true)
                    stopRuntime()
                    getApplication<Application>().sendProfileChanged(uuid)
                    refreshProfilesNow()
                    return ConfigPreviewSaveOutcome.SavedLocally
                }

                ConfigPreviewSaveDecision.Continue -> Unit
            }

            val interruptedOutcome: ConfigPreviewSaveOutcome? = coroutineScope {
                val remoteFetch = async(Dispatchers.IO) {
                    remoteFetchStarted = true
                    Clash.fetchAndValid(stagingDir, profile.source, false) { }.await()
                }

                while (!remoteFetch.isCompleted) {
                    when (decisionProvider()) {
                        ConfigPreviewSaveDecision.ContinueEditing -> {
                            shouldDeleteStagingDir = false
                            remoteFetch.cancel()
                            return@coroutineScope ConfigPreviewSaveOutcome.ResumeEditing
                        }

                        ConfigPreviewSaveDecision.SaveLocally -> {
                            shouldDeleteStagingDir = false
                            remoteFetch.cancel()
                            commitLocalConfigContent(liveConfigFile, content)
                            setLocalUnvalidatedMarker(uuid, true)
                            stopRuntime()
                            getApplication<Application>().sendProfileChanged(uuid)
                            refreshProfilesNow()
                            return@coroutineScope ConfigPreviewSaveOutcome.SavedLocally
                        }

                        ConfigPreviewSaveDecision.Continue -> delay(REMOTE_FETCH_POLL_INTERVAL_MS)
                    }
                }

                remoteFetch.await()
                null
            }
            if (interruptedOutcome != null) {
                return interruptedOutcome
            }

            commitStagedProfileDirectory(stagingDir, liveProfileDir)
            getApplication<Application>().sendProfileChanged(uuid)

            if (!activeProfileOverrideReloader.reapplyIfActiveProfile(uuid.toString())) {
                Timber.w("Override reapply skipped for profile (missing configs?): %s", uuid)
            }

            runtimeActionExecutor
                .reloadIfActiveProfile(
                    operation = "profiles:save-config",
                    profileId = uuid,
                    presentation =
                        RuntimeActionFailurePresentation.Runtime(
                            fallbackMessage = MLang.ProfilesVM.Error.Unknown,
                        ),
                ).getOrThrowHandled()
            setLocalUnvalidatedMarker(uuid, false)
            refreshProfilesNow()
            return ConfigPreviewSaveOutcome.Saved
        } finally {
            if (shouldDeleteStagingDir && stagingDir.exists()) {
                stagingDir.deleteRecursively()
            }
        }
    }

    suspend fun loadProfileConfigForGui(uuid: UUID): ConfigurationOverride {
        val liveConfigFile = resolveProfileConfigFile(uuid)
        if (!liveConfigFile.exists()) {
            error(MLang.ProfilesPage.Message.ProfileFileNotExist)
        }

        return withContext(Dispatchers.IO) {
            parseProfileConfigYaml(liveConfigFile.readText())
        }
    }

    fun hasProfileGuiConfigChanges(
        originalConfig: ConfigurationOverride,
        updatedConfig: ConfigurationOverride,
    ): Boolean {
        return buildProfileGuiOverrideContent(originalConfig, updatedConfig) != "{}"
    }

    suspend fun saveProfileConfigGuiContent(
        uuid: UUID,
        originalConfig: ConfigurationOverride,
        updatedConfig: ConfigurationOverride,
        onPhaseChanged: (ConfigPreviewSavePhase) -> Unit = {},
        decisionProvider: () -> ConfigPreviewSaveDecision = { ConfigPreviewSaveDecision.Continue },
        stopRuntime: suspend () -> Unit = {},
    ): ConfigPreviewSaveOutcome {
        if (!hasProfileGuiConfigChanges(originalConfig, updatedConfig)) {
            return ConfigPreviewSaveOutcome.Saved
        }

        val liveConfigFile = resolveProfileConfigFile(uuid)
        if (!liveConfigFile.exists()) {
            error(MLang.ProfilesPage.Message.ProfileFileNotExist)
        }

        val overrideContent = buildProfileGuiOverrideContent(originalConfig, updatedConfig)
        val updatedYaml =
            withContext(Dispatchers.IO) {
                applyProfileGuiDiffToYaml(
                    originalYaml = liveConfigFile.readText(),
                    diffJson = overrideContent,
                )
            }

        return saveProfileConfigContent(
            uuid = uuid,
            content = updatedYaml,
            onPhaseChanged = onPhaseChanged,
            decisionProvider = decisionProvider,
            stopRuntime = stopRuntime,
        )
    }

    private suspend fun validateStagedProfileConfig(
        uuid: UUID,
        profileDir: File,
        configFile: File,
    ) {
        val result =
            withContext(Dispatchers.Default) {
                Clash.compilePreview(
                    CompileRequest(
                        profileUuid = uuid.toString(),
                        profileDir = profileDir.absolutePath,
                        profilePath = configFile.absolutePath,
                        outputPath = profileDir.resolve("runtime.yaml").absolutePath,
                    )
                )
            }
        if (!result.success) {
            error(result.error?.takeIf { it.isNotBlank() } ?: MLang.Component.Editor.Error.SaveFailed)
        }
    }

    private suspend fun commitLocalConfigContent(configFile: File, content: String) {
        withContext(Dispatchers.IO) {
            configFile.parentFile?.mkdirs()
            configFile.writeText(content)
        }
    }

    private suspend fun commitStagedProfileDirectory(stagingDir: File, liveProfileDir: File) {
        withContext(Dispatchers.IO) {
            if (liveProfileDir.exists()) {
                liveProfileDir.deleteRecursively()
            }
            stagingDir.copyRecursively(liveProfileDir, overwrite = true)
        }
    }

    private fun buildProfileGuiOverrideContent(
        originalConfig: ConfigurationOverride,
        updatedConfig: ConfigurationOverride,
    ): String {
        val originalElement =
            profileGuiJson.encodeToJsonElement(ConfigurationOverride.serializer(), originalConfig)
        val updatedElement =
            profileGuiJson.encodeToJsonElement(ConfigurationOverride.serializer(), updatedConfig)
        val diffElement = diffJsonElement(originalElement, updatedElement) ?: JsonObject(emptyMap())
        return profileGuiJson.encodeToString(JsonElement.serializer(), diffElement)
    }

    private fun diffJsonElement(
        original: JsonElement?,
        updated: JsonElement?,
    ): JsonElement? {
        if (original == updated) {
            return null
        }

        return when {
            original is JsonObject && updated is JsonObject -> {
                val keys = original.keys + updated.keys
                val changed =
                    buildMap<String, JsonElement> {
                        keys.forEach { key ->
                            diffJsonElement(original[key], updated[key])?.let { put(key, it) }
                        }
                    }
                changed.takeIf(Map<String, JsonElement>::isNotEmpty)?.let(::JsonObject)
            }

            original is JsonArray && updated is JsonArray -> updated
            updated == null -> JsonNull
            else -> updated
        }
    }

    private fun parseProfileConfigYaml(yamlText: String): ConfigurationOverride {
        val rootElement = yamlValueToJsonElement(loadProfileConfigYamlRoot(yamlText))
        val configElement =
            if (rootElement is JsonObject) {
                sanitizeJsonElementForSerializer(rootElement, ConfigurationOverride.serializer())
                    as? JsonObject
                    ?: JsonObject(emptyMap())
            } else {
                JsonObject(emptyMap())
            }

        return runCatching {
                profileGuiJson.decodeFromJsonElement(
                    ConfigurationOverride.serializer(),
                    configElement,
                )
            }
            .getOrElse { error ->
                Clash.inspectCompiledConfig(yamlText)
                    ?: throw IllegalStateException(
                        error.message ?: MLang.ProfilesPage.Message.ReadProfileFailed,
                        error,
                    )
            }
    }

    private fun <T> sanitizeJsonElementForSerializer(
        element: JsonElement,
        serializer: KSerializer<T>,
    ): JsonElement? = sanitizeJsonElementForDescriptor(element, serializer.descriptor)

    private fun sanitizeJsonElementForDescriptor(
        element: JsonElement,
        descriptor: SerialDescriptor,
    ): JsonElement? {
        if (element is JsonNull) {
            return JsonNull
        }
        if (descriptor.serialName.startsWith("kotlinx.serialization.json.")) {
            return element
        }

        return when (descriptor.kind) {
            StructureKind.CLASS,
            StructureKind.OBJECT,
            -> sanitizeJsonObject(element as? JsonObject, descriptor)

            StructureKind.LIST ->
                sanitizeJsonArray(
                    element = element as? JsonArray,
                    elementDescriptor = descriptor.getElementDescriptor(0),
                )

            StructureKind.MAP ->
                sanitizeJsonMap(
                    element = element as? JsonObject,
                    valueDescriptor = descriptor.getElementDescriptor(1),
                )

            SerialKind.ENUM -> (element as? JsonPrimitive)?.let { JsonPrimitive(it.content) }
            PrimitiveKind.STRING -> (element as? JsonPrimitive)?.let { JsonPrimitive(it.content) }
            PrimitiveKind.BOOLEAN ->
                (element as? JsonPrimitive)?.booleanOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.BYTE,
            PrimitiveKind.SHORT,
            PrimitiveKind.INT,
            -> (element as? JsonPrimitive)?.intOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.LONG ->
                (element as? JsonPrimitive)?.longOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.FLOAT,
            PrimitiveKind.DOUBLE,
            -> (element as? JsonPrimitive)?.doubleOrNull?.let { value -> JsonPrimitive(value) }

            PrimitiveKind.CHAR ->
                (element as? JsonPrimitive)?.content?.singleOrNull()?.let { value ->
                    JsonPrimitive(value.toString())
                }

            else -> element.takeIf { it is JsonPrimitive }
        }
    }

    private fun sanitizeJsonObject(
        element: JsonObject?,
        descriptor: SerialDescriptor,
    ): JsonElement? {
        if (element == null) {
            return null
        }

        val content =
            buildMap<String, JsonElement> {
                for (index in 0 until descriptor.elementsCount) {
                    val key = descriptor.getElementName(index)
                    val childElement = element[key] ?: continue
                    sanitizeJsonElementForDescriptor(
                        element = childElement,
                        descriptor = descriptor.getElementDescriptor(index),
                    )?.let { sanitizedChild -> put(key, sanitizedChild) }
                }
            }

        if (content.isEmpty() && element.isNotEmpty()) {
            return null
        }
        return JsonObject(content)
    }

    private fun sanitizeJsonArray(
        element: JsonArray?,
        elementDescriptor: SerialDescriptor,
    ): JsonElement? {
        if (element == null) {
            return null
        }

        val content =
            element.mapNotNull { childElement ->
                sanitizeJsonElementForDescriptor(childElement, elementDescriptor)
            }

        if (content.isEmpty() && element.isNotEmpty()) {
            return null
        }
        return JsonArray(content)
    }

    private fun sanitizeJsonMap(
        element: JsonObject?,
        valueDescriptor: SerialDescriptor,
    ): JsonElement? {
        if (element == null) {
            return null
        }

        val content =
            buildMap<String, JsonElement> {
                element.forEach { (key, childElement) ->
                    sanitizeJsonElementForDescriptor(childElement, valueDescriptor)?.let {
                        put(key, it)
                    }
                }
            }

        if (content.isEmpty() && element.isNotEmpty()) {
            return null
        }
        return JsonObject(content)
    }

    private fun applyProfileGuiDiffToYaml(
        originalYaml: String,
        diffJson: String,
    ): String {
        val diffElement = profileGuiJson.parseToJsonElement(diffJson)
        if (diffElement !is JsonObject || diffElement.isEmpty()) {
            return originalYaml
        }

        val root = loadProfileConfigYamlRoot(originalYaml)
        applyJsonDiffToYamlMap(root, diffElement)
        return dumpProfileConfigYaml(root)
    }

    private fun loadProfileConfigYamlRoot(yamlText: String): MutableMap<String, Any?> {
        val loaded =
            yamlText.takeIf { it.isNotBlank() }?.let { Yaml().load<Any?>(it) }
                ?: emptyMap<String, Any?>()
        val mutableValue = toMutableYamlValue(loaded)
        @Suppress("UNCHECKED_CAST")
        return when (mutableValue) {
            is MutableMap<*, *> -> mutableValue as MutableMap<String, Any?>
            else -> linkedMapOf()
        }
    }

    private fun toMutableYamlValue(value: Any?): Any? {
        return when (value) {
            is Map<*, *> -> {
                linkedMapOf<String, Any?>().apply {
                    value.forEach { (key, childValue) ->
                        key?.toString()?.let { put(it, toMutableYamlValue(childValue)) }
                    }
                }
            }

            is List<*> -> value.mapTo(mutableListOf()) { childValue -> toMutableYamlValue(childValue) }
            else -> value
        }
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

    private fun applyJsonDiffToYamlMap(
        target: MutableMap<String, Any?>,
        diff: JsonObject,
    ) {
        diff.forEach { (key, value) ->
            when (value) {
                JsonNull -> target.remove(key)

                is JsonObject -> {
                    val currentValue = toMutableYamlValue(target[key])
                    @Suppress("UNCHECKED_CAST")
                    val childMap =
                        when (currentValue) {
                            is MutableMap<*, *> -> currentValue as MutableMap<String, Any?>
                            else -> linkedMapOf()
                        }
                    applyJsonDiffToYamlMap(childMap, value)
                    if (childMap.isEmpty()) {
                        target.remove(key)
                    } else {
                        target[key] = childMap
                    }
                }

                else -> target[key] = jsonElementToYamlValue(value)
            }
        }
    }

    private fun jsonElementToYamlValue(element: JsonElement): Any? {
        return when (element) {
            JsonNull -> null
            is JsonObject -> {
                linkedMapOf<String, Any?>().apply {
                    element.forEach { (key, childElement) ->
                        put(key, jsonElementToYamlValue(childElement))
                    }
                }
            }

            is JsonArray -> {
                element.mapTo(mutableListOf()) { childElement ->
                    jsonElementToYamlValue(childElement)
                }
            }
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.booleanOrNull != null -> element.booleanOrNull
                    element.longOrNull != null -> {
                        val longValue = element.longOrNull!!
                        if (longValue in Int.MIN_VALUE..Int.MAX_VALUE) {
                            longValue.toInt()
                        } else {
                            longValue
                        }
                    }

                    element.doubleOrNull != null -> element.doubleOrNull
                    else -> element.content
                }
            }
        }
    }

    private fun dumpProfileConfigYaml(root: MutableMap<String, Any?>): String {
        if (root.isEmpty()) {
            return ""
        }

        val dumperOptions =
            DumperOptions().apply {
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                indent = 4
                indicatorIndent = 2
                isPrettyFlow = true
                splitLines = false
            }
        return Yaml(dumperOptions).dump(root).trimEnd() + "\n"
    }

    private fun createProfileSaveStagingDirectory(uuid: UUID): File {
        val root = getApplication<Application>().cacheDir.resolve("profile-save-staging")
        val dir = root.resolve("${uuid}-${System.currentTimeMillis()}")
        dir.mkdirs()
        return dir
    }

    private fun cleanupOldProfileSaveStagingDirectories(uuid: UUID, keep: File) {
        val root = getApplication<Application>().cacheDir.resolve("profile-save-staging")
        root.listFiles()
            ?.filter { it != keep && it.name.startsWith("$uuid-") }
            ?.forEach { runCatching { it.deleteRecursively() } }
    }

    fun reorderProfiles(from: Int, to: Int) {
        viewModelScope.launch {
            try {
                val current = _profiles.value
                if (from !in current.indices || to !in current.indices || from == to) return@launch

                val reordered = current.toMutableList()
                val moved = reordered.removeAt(from)
                reordered.add(to, moved)

                _profiles.value = reordered
                profilesRepository.reorderProfiles(reordered.map { it.uuid })
                Timber.d("Profiles reordered: $from->$to")
            } catch (e: Exception) {
                Timber.e(e, "Failed to reorder profiles")
                refreshProfiles()
            }
        }
    }

    fun toggleProfileEnabled(uuid: UUID) {
        viewModelScope.launch {
            try {
                val profile =
                    profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")

                val outcome =
                    runtimeActionExecutor.activateProfile(
                        operation = "profiles:toggle-active",
                        profileId = uuid,
                        enabled = !profile.active,
                        presentation =
                            RuntimeActionFailurePresentation.Global(message = { reason ->
                                MLang.ProfilesVM.Message.ToggleFailed.format(reason)
                            }),
                    )
                when (outcome) {
                    is RuntimeActionOutcome.Success -> Unit
                    is RuntimeActionOutcome.PermissionRequired -> {
                        vpnPermissionCoordinator.requestPermission(outcome.intent) {
                            toggleProfileEnabled(uuid)
                        }
                        return@launch
                    }
                    RuntimeActionOutcome.FailureHandled -> return@launch
                }
                showMessage(MLang.ProfilesVM.Message.ProfileUpdated.format(profile.name))
                refreshProfiles()
                Timber.d("Profile toggled: $uuid, active=${!profile.active}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle profile")
                if (e !is HandledRuntimeActionFailure) {
                    showError(MLang.ProfilesVM.Message.ToggleFailed.format(e.uiErrorMessage()))
                }
            }
        }
    }

    fun clearDownloadProgress() {
        _downloadProgress.value = null
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun setLoading(loading: Boolean) {
        _uiState.update { current ->
            if (current.isLoading == loading) current else current.copy(isLoading = loading)
        }
    }

    private fun showError(message: String) {
        _uiState.update { current ->
            if (current.error == message) current else current.copy(error = message)
        }
    }

    private fun showMessage(message: String) {
        _uiState.update { current ->
            if (current.message == message) current else current.copy(message = message)
        }
    }

    private suspend fun rollbackCreatedProfile(uuid: UUID) {
        runCatching { profilesRepository.deleteProfile(uuid) }
        runCatching {
            getImportedProfileDirectory(uuid).deleteRecursively()
            getClashProfileDirectory(uuid).deleteRecursively()
        }
    }

    private fun getImportedProfileDirectory(uuid: UUID): File {
        return File(getApplication<Application>().filesDir, "imported/$uuid")
    }

    private fun getClashProfileDirectory(uuid: UUID): File {
        return File(getApplication<Application>().filesDir, "clash/profiles/$uuid")
    }

    private fun resolveProfileConfigFile(uuid: UUID): File {
        val importedFile = getImportedProfileDirectory(uuid).resolve("config.yaml")
        return if (importedFile.exists()) {
            importedFile
        } else {
            getClashProfileDirectory(uuid).resolve("config.yaml")
        }
    }

    private fun resolveLocalUnvalidatedMarkerFile(uuid: UUID): File {
        val configFile = resolveProfileConfigFile(uuid)
        val profileDir = configFile.parentFile ?: getImportedProfileDirectory(uuid)
        return profileDir.resolve(LOCAL_UNVALIDATED_MARKER_FILE)
    }

    private suspend fun setLocalUnvalidatedMarker(uuid: UUID, needed: Boolean) {
        withContext(Dispatchers.IO) {
            val markerFile = resolveLocalUnvalidatedMarkerFile(uuid)
            if (needed) {
                markerFile.parentFile?.mkdirs()
                if (!markerFile.exists()) {
                    markerFile.writeText("pending-remote-validation")
                }
            } else if (markerFile.exists()) {
                markerFile.delete()
            }
        }
    }
}

data class ProfilesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
)

data class DownloadProgress(
    val percent: Int?,
    val message: String,
    val isCompleted: Boolean = false,
)

private fun FetchStatus.toDownloadProgress(): DownloadProgress {
    val percent = if (max > 0) ((progress * 100) / max).coerceIn(0, 100) else null
    val detail = args.firstOrNull().orEmpty().trim()

    val message =
        when (action) {
            FetchStatus.Action.FetchConfiguration -> {
                if (percent == null || percent <= 5) {
                    MLang.ProfilesVM.Progress.Preparing
                } else {
                    detail.ifBlank { MLang.ProfilesPage.Progress.Downloading }
                }
            }

            FetchStatus.Action.FetchProviders -> {
                if (detail.isNotBlank()) detail else ""
            }

            FetchStatus.Action.Verifying -> {
                detail.ifBlank { MLang.ProfilesVM.Progress.Verifying }
            }
        }

    return DownloadProgress(percent = percent, message = message)
}

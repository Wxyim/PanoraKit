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
import com.github.yumelira.yumebox.core.model.FetchStatus
import com.github.yumelira.yumebox.data.repository.ActiveProfileOverrideReloader
import com.github.yumelira.yumebox.data.repository.ProfileBindingProvider
import com.github.yumelira.yumebox.data.store.LinkOpenMode
import com.github.yumelira.yumebox.data.store.Preference
import com.github.yumelira.yumebox.data.store.ProfileLink
import com.github.yumelira.yumebox.data.store.ProfileLinksStorage
import com.github.yumelira.yumebox.remote.runtimeGatewayMessage
import com.github.yumelira.yumebox.runtime.client.ProfilesRepository
import com.github.yumelira.yumebox.runtime.client.RuntimeControlCoordinator
import com.github.yumelira.yumebox.service.remote.IFetchObserver
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ProfilesViewModel(
    application: Application,
    private val profilesRepository: ProfilesRepository,
    profileLinksStorage: ProfileLinksStorage,
    private val bindingProvider: ProfileBindingProvider,
    private val activeProfileOverrideReloader: ActiveProfileOverrideReloader,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
) : AndroidViewModel(application) {
    companion object {
        private const val BLANK_PROFILE_SOURCE = "blank://local-config"
        private const val MAX_LOCAL_FILE_BYTES = 50L * 1024 * 1024 // 50 MiB
    }

    val linkOpenMode: Preference<LinkOpenMode> = profileLinksStorage.linkOpenMode
    val links: Preference<List<ProfileLink>> = profileLinksStorage.links
    val defaultLinkId: Preference<String> = profileLinksStorage.defaultLinkId

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
                    runtimeControlCoordinator.activateProfile(
                        operation = "profiles:delete-active",
                        profileId = activeProfile.uuid,
                        enabled = false,
                    )
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
            runtimeControlCoordinator.activateProfile(
                operation = "profiles:activate",
                profileId = uuid,
                enabled = true,
            )
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
        runtimeControlCoordinator.reloadIfActiveProfile("profiles:update-profile", uuid)
        refreshProfilesNow()
        return profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
    }

    suspend fun patchProfileNow(uuid: UUID, name: String, source: String, interval: Long): Profile {
        val previousProfile =
            profilesRepository.queryProfileByUUID(uuid) ?: error("Profile not found: $uuid")
        try {
            profilesRepository.patchProfile(uuid, name, source, interval)
            runtimeControlCoordinator.reloadIfActiveProfile("profiles:patch-profile", uuid)
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
        runtimeControlCoordinator.activateProfile(
            operation = "profiles:set-active",
            profileId = uuid,
            enabled = enabled,
        )
        refreshProfilesNow()
        return profilesRepository.queryProfileByUUID(uuid)
            ?: error("Profile not found after profile activation: $uuid")
    }

    suspend fun saveProfileConfigContent(uuid: UUID, content: String) {
        val configFile = resolveProfileConfigFile(uuid)
        val previousContent = configFile.takeIf(File::exists)?.readText()
        runCatching {
                configFile.parentFile?.mkdirs()
                configFile.writeText(content)
                profilesRepository.updateProfile(uuid)
                check(activeProfileOverrideReloader.reapplyIfActiveProfile(uuid.toString())) {
                    MLang.Override.Save.ApplyFailed
                }
                runtimeControlCoordinator.reloadIfActiveProfile(
                    operation = "profiles:save-config",
                    profileId = uuid,
                )
                refreshProfilesNow()
            }
            .onFailure { error ->
                if (previousContent == null) {
                    runCatching { configFile.delete() }
                } else {
                    runCatching { configFile.writeText(previousContent) }
                }
                throw error
            }
            .getOrThrow()
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

                runtimeControlCoordinator.activateProfile(
                    operation = "profiles:toggle-active",
                    profileId = uuid,
                    enabled = !profile.active,
                )
                showMessage(MLang.ProfilesVM.Message.ProfileUpdated.format(profile.name))
                refreshProfiles()
                Timber.d("Profile toggled: $uuid, active=${!profile.active}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle profile")
                showError(MLang.ProfilesVM.Message.ToggleFailed.format(e.uiErrorMessage()))
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

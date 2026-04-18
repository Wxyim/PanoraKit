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

package com.github.nomadboxlab.monadbox.feature.profiles.usecase

import android.app.Application
import android.net.Uri
import com.github.nomadboxlab.monadbox.core.model.FetchStatus
import com.github.nomadboxlab.monadbox.feature.profiles.DownloadProgress
import com.github.nomadboxlab.monadbox.runtime.client.ProfilesRepository
import com.github.nomadboxlab.monadbox.service.remote.IFetchObserver
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class ImportProfileUseCase(
    private val application: Application,
    private val profilesRepository: ProfilesRepository,
) {
    private companion object {
        const val MAX_LOCAL_FILE_BYTES = 50L * 1024 * 1024
    }

    suspend operator fun invoke(
        uri: Uri,
        name: String,
        onProgressChanged: (DownloadProgress?) -> Unit,
    ): UUID {
        var createdUuid: UUID? = null
        try {
            onProgressChanged(
                DownloadProgress(percent = 0, message = MLang.ProfilesVM.Progress.ImportPreparing)
            )

            val uuid = profilesRepository.createProfile(Profile.Type.File, name, uri.toString())
            createdUuid = uuid
            copyFileToImportedDir(uri, uuid)

            onProgressChanged(
                DownloadProgress(percent = 50, message = MLang.ProfilesVM.Progress.Verifying)
            )

            val observer = IFetchObserver { status ->
                onProgressChanged(status.toDownloadProgress())
            }
            profilesRepository.updateProfile(uuid, observer)

            onProgressChanged(
                DownloadProgress(
                    percent = 100,
                    message = MLang.ProfilesVM.Progress.ImportComplete,
                    isCompleted = true,
                )
            )
            return uuid
        } catch (error: Exception) {
            createdUuid?.let { rollbackCreatedProfile(it) }
            throw error
        }
    }

    private suspend fun copyFileToImportedDir(uri: Uri, uuid: UUID) {
        withContext(Dispatchers.IO) {
            val importedDir = File(application.filesDir, "imported/${uuid}")
            importedDir.mkdirs()

            val inputFile =
                application.contentResolver.openInputStream(uri)
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

    private suspend fun rollbackCreatedProfile(uuid: UUID) {
        runCatching { profilesRepository.deleteProfile(uuid) }
        runCatching {
            File(application.filesDir, "imported/$uuid").deleteRecursively()
            File(application.filesDir, "clash/profiles/$uuid").deleteRecursively()
        }
    }

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
}

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

package com.github.nomadboxlab.monadbox.feature.home.usecase

import android.app.Application
import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.repository.LogRecordGateway
import com.github.nomadboxlab.monadbox.data.store.AppSettingsStorage
import com.github.nomadboxlab.monadbox.data.store.NetworkSettingsStorage
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionOutcome
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationResult
import dev.oom_wg.purejoy.mlang.MLang
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class StartHomeProxyUseCase(
    private val application: Application,
    private val networkSettingsStorage: NetworkSettingsStorage,
    private val logRecordGateway: LogRecordGateway,
    private val runtimeActionExecutor: RuntimeActionExecutor,
    private val appSettings: AppSettingsStorage,
) {
    fun resolveMode(mode: ProxyMode?): ProxyMode {
        return mode ?: networkSettingsStorage.proxyMode.value
    }

    suspend operator fun invoke(
        profileId: String,
        mode: ProxyMode,
    ): RuntimeActionOutcome<RuntimeMutationResult> {
        if (appSettings.autoStartLogRecording.value && !logRecordGateway.isRecording) {
            runCatching { logRecordGateway.start(application) }
                .onFailure { error -> Timber.d(error, "Skipped eager log recording start") }
        }
        Timber.d("Home startProxy kickoff: mode=$mode profileId=$profileId")
        return withContext(Dispatchers.IO) {
            runtimeActionExecutor.startProxy(
                operation = "home:start-proxy",
                mode = mode,
                profileId = profileId.takeIf(String::isNotBlank)?.let(UUID::fromString),
                fallbackMessage = MLang.Home.Message.StartFailed,
            )
        }
    }
}

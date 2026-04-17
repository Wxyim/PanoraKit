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
 */

package com.github.nomadboxlab.monadbox.runtime.client.usecase

import com.github.nomadboxlab.monadbox.runtime.client.ProfilesRepository
import com.github.nomadboxlab.monadbox.runtime.client.ProxyFacade
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeControlCoordinator
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import timber.log.Timber

/**
 * Outcome of a single [AutoStartProxyUseCase] invocation.
 * - [Skipped]: runtime was already up, or auto-restart preference is off.
 * - [NoProfile]: no active profile could be determined / created.
 * - [Started]: proxy started successfully.
 * - [Failed]: start attempt threw; [cause] carries the original throwable.
 */
sealed interface AutoStartProxyOutcome {
    data object Skipped : AutoStartProxyOutcome

    data object NoProfile : AutoStartProxyOutcome

    data class Started(val profile: Profile) : AutoStartProxyOutcome

    data class Failed(val profile: Profile, val cause: Throwable) : AutoStartProxyOutcome
}

/**
 * Inputs captured from app-level settings and the optional pre-refreshed profile. Keeping this as a
 * data class lets callers compose it independently of the use case.
 */
data class AutoStartProxyInputs(
    val automaticRestartEnabled: Boolean,
    val runtimeAlreadyRunning: Boolean,
    val preRefreshedProfile: Profile?,
)

/**
 * Cold-start use case extracted from `MainActivity.onCreate.LaunchedEffect(Unit)`.
 *
 * Rules:
 * - If [AutoStartProxyInputs.automaticRestartEnabled] is false, or the runtime is already running,
 *   this is a [AutoStartProxyOutcome.Skipped].
 * - Otherwise prefer [AutoStartProxyInputs.preRefreshedProfile]; fall back to
 *   [ProfilesRepository.queryActiveProfile] with `ensureDefault = true`.
 * - Start via [RuntimeControlCoordinator.startProxy] with operation id `"autostart:startup"` for
 *   traceability.
 *
 * All Throwables from the underlying repository / coordinator are caught and mapped to
 * [AutoStartProxyOutcome.Failed] so callers never have to handle control-flow exceptions.
 */
class AutoStartProxyUseCase(
    private val profilesRepository: ProfilesRepository,
    private val runtimeControlCoordinator: RuntimeControlCoordinator,
    private val proxyFacade: ProxyFacade,
    private val isServiceRunning: () -> Boolean,
) {
    suspend fun execute(
        automaticRestartEnabled: Boolean,
        preRefreshedProfile: Profile? = null,
    ): AutoStartProxyOutcome {
        val runtimeAlreadyRunning = proxyFacade.runtimeSnapshot.value.running || isServiceRunning()
        return execute(
            AutoStartProxyInputs(
                automaticRestartEnabled = automaticRestartEnabled,
                runtimeAlreadyRunning = runtimeAlreadyRunning,
                preRefreshedProfile = preRefreshedProfile,
            )
        )
    }

    suspend fun execute(inputs: AutoStartProxyInputs): AutoStartProxyOutcome {
        if (!inputs.automaticRestartEnabled || inputs.runtimeAlreadyRunning) {
            return AutoStartProxyOutcome.Skipped
        }

        val profile =
            inputs.preRefreshedProfile
                ?: runCatching { profilesRepository.queryActiveProfile(ensureDefault = true) }
                    .onFailure { Timber.w(it, "Failed to load active profile for auto start") }
                    .getOrNull()

        if (profile == null) {
            Timber.w("No active profile for auto start")
            return AutoStartProxyOutcome.NoProfile
        }

        return runCatching {
                runtimeControlCoordinator.startProxy(
                    operation = "autostart:startup",
                    profileId = profile.uuid,
                )
            }
            .fold(
                onSuccess = {
                    Timber.i("Auto start ok: profile=%s", profile.uuid)
                    AutoStartProxyOutcome.Started(profile)
                },
                onFailure = { error ->
                    Timber.e(error, "Auto start failed: ${error.message}")
                    AutoStartProxyOutcome.Failed(profile, error)
                },
            )
    }
}

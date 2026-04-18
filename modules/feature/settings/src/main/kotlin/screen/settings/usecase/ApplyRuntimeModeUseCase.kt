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

package com.github.nomadboxlab.monadbox.feature.settings.usecase

import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.store.Preference
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionOutcome
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationResult

class ApplyRuntimeModeUseCase(
    private val proxyMode: Preference<ProxyMode>,
    private val runtimeActionExecutor: RuntimeActionExecutor,
) {
    suspend operator fun invoke(mode: ProxyMode): RuntimeActionOutcome<RuntimeMutationResult> {
        val previousMode = proxyMode.value
        return runtimeActionExecutor.applyConfigChange(
            operation = "network:proxy-mode",
            persist = { proxyMode.set(mode) },
            rollback = { proxyMode.set(previousMode) },
            presentation =
                RuntimeActionFailurePresentation.Runtime(
                    fallbackMessage = "Failed to switch proxy mode",
                    targetMode = mode,
                ),
        )
    }
}

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

package com.github.nomadboxlab.monadbox.runtime.client

import com.github.nomadboxlab.monadbox.service.runtime.state.RuntimePhase

internal enum class RuntimeStopResolution {
    IgnoreAsStale,
    SkipAsRedundant,
    TransitionToIdle,
}

internal fun resolveRuntimeStopResolution(
    currentPhase: RuntimePhase,
    reason: String?,
): RuntimeStopResolution {
    return when {
        currentPhase == RuntimePhase.Starting && reason.isNullOrBlank() ->
            RuntimeStopResolution.IgnoreAsStale
        currentPhase == RuntimePhase.Idle && reason.isNullOrBlank() ->
            RuntimeStopResolution.SkipAsRedundant
        else -> RuntimeStopResolution.TransitionToIdle
    }
}

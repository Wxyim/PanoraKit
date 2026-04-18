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

package com.github.nomadboxlab.monadbox.presentation.runtime

import android.content.Intent
import com.github.nomadboxlab.monadbox.domain.model.ProxyMode
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationResult
import java.util.UUID
import kotlinx.coroutines.flow.StateFlow

sealed interface RuntimeActionOutcome<out T> {
    data class Success<T>(val value: T) : RuntimeActionOutcome<T>

    data class PermissionRequired(val intent: Intent) : RuntimeActionOutcome<Nothing>

    data object FailureHandled : RuntimeActionOutcome<Nothing>
}

class HandledRuntimeActionFailure : IllegalStateException("Runtime action failure already handled")

sealed interface RuntimeActionFailurePresentation {
    data class Start(val targetMode: ProxyMode, val fallbackMessage: String) :
        RuntimeActionFailurePresentation

    data class Runtime(val fallbackMessage: String, val targetMode: ProxyMode? = null) :
        RuntimeActionFailurePresentation

    data class Global(val message: (String) -> String, val title: String = "Error") :
        RuntimeActionFailurePresentation

    data object None : RuntimeActionFailurePresentation
}

interface RuntimeActionExecutor {
    val isMutating: StateFlow<Boolean>

    val activeOperation: StateFlow<String?>

    suspend fun startProxy(
        operation: String,
        mode: ProxyMode,
        profileId: UUID? = null,
        fallbackMessage: String,
    ): RuntimeActionOutcome<RuntimeMutationResult>

    suspend fun stopProxy(
        operation: String,
        mode: ProxyMode? = null,
        presentation: RuntimeActionFailurePresentation.Global,
    ): RuntimeActionOutcome<RuntimeMutationResult>

    suspend fun reloadCurrentProfile(
        operation: String,
        presentation: RuntimeActionFailurePresentation.Global,
    ): RuntimeActionOutcome<RuntimeMutationResult>

    suspend fun reloadIfActiveProfile(
        operation: String,
        profileId: UUID,
        presentation: RuntimeActionFailurePresentation =
            RuntimeActionFailurePresentation.Runtime(fallbackMessage = "Unknown error"),
    ): RuntimeActionOutcome<RuntimeMutationResult>

    suspend fun activateProfile(
        operation: String,
        profileId: UUID,
        enabled: Boolean,
        presentation: RuntimeActionFailurePresentation,
    ): RuntimeActionOutcome<RuntimeMutationResult>

    suspend fun applyConfigChange(
        operation: String,
        persist: suspend () -> Unit,
        rollback: suspend () -> Unit = {},
        shouldRestart: (ProxyMode) -> Boolean = { true },
        presentation: RuntimeActionFailurePresentation,
    ): RuntimeActionOutcome<RuntimeMutationResult>

    fun resolveDialogMode(): ProxyMode

    fun showGlobalError(message: String, title: String = "Error")
}

fun <T> RuntimeActionOutcome<T>.getOrThrowHandled(): T {
    return when (this) {
        is RuntimeActionOutcome.Success -> value
        is RuntimeActionOutcome.PermissionRequired -> throw HandledRuntimeActionFailure()
        RuntimeActionOutcome.FailureHandled -> throw HandledRuntimeActionFailure()
    }
}

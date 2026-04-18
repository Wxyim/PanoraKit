/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.feature.settings.usecase

import com.github.nomadboxlab.monadbox.data.model.ProxyMode
import com.github.nomadboxlab.monadbox.data.store.Preference
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionOutcome
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationResult
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationStatus
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplyRuntimeModeUseCaseTest {
    @Test
    fun invoke_persistsRequestedModeThroughRuntimeExecutor() = runTest {
        val proxyMode = preferenceOf(ProxyMode.Tun)
        val executor = RecordingRuntimeActionExecutor()
        val useCase = ApplyRuntimeModeUseCase(proxyMode, executor)

        val outcome = useCase(ProxyMode.RootTun)

        assertTrue(outcome is RuntimeActionOutcome.Success)
        assertEquals(ProxyMode.RootTun, proxyMode.value)
        assertEquals("network:proxy-mode", executor.operation)
        assertEquals(
            RuntimeActionFailurePresentation.Runtime(
                fallbackMessage = "Failed to switch proxy mode",
                targetMode = ProxyMode.RootTun,
            ),
            executor.presentation,
        )
    }

    @Test
    fun invoke_rollsPreferenceBackWhenExecutorRequestsRollback() = runTest {
        val proxyMode = preferenceOf(ProxyMode.Tun)
        val executor = RecordingRuntimeActionExecutor(RuntimeActionOutcome.FailureHandled)
        val useCase = ApplyRuntimeModeUseCase(proxyMode, executor)

        val outcome = useCase(ProxyMode.Http)

        assertEquals(RuntimeActionOutcome.FailureHandled, outcome)
        assertEquals(ProxyMode.Tun, proxyMode.value)
    }

    private fun <T> preferenceOf(initialValue: T): Preference<T> {
        val state = MutableStateFlow(initialValue)
        return Preference(
            state = state,
            update = { value -> state.value = value },
            get = { state.value },
        )
    }

    private class RecordingRuntimeActionExecutor(
        private val outcome: RuntimeActionOutcome<RuntimeMutationResult> =
            RuntimeActionOutcome.Success(
                RuntimeMutationResult(
                    status = RuntimeMutationStatus.Updated,
                    effectiveMode = ProxyMode.RootTun,
                    runtimeRunning = true,
                )
            )
    ) : RuntimeActionExecutor {
        override val isMutating: StateFlow<Boolean> = MutableStateFlow(false)
        override val activeOperation: StateFlow<String?> = MutableStateFlow(null)

        var operation: String? = null
            private set

        var presentation: RuntimeActionFailurePresentation? = null
            private set

        override suspend fun applyConfigChange(
            operation: String,
            persist: suspend () -> Unit,
            rollback: suspend () -> Unit,
            shouldRestart: (ProxyMode) -> Boolean,
            presentation: RuntimeActionFailurePresentation,
        ): RuntimeActionOutcome<RuntimeMutationResult> {
            this.operation = operation
            this.presentation = presentation
            persist()
            if (outcome == RuntimeActionOutcome.FailureHandled) {
                rollback()
            }
            return outcome
        }

        override suspend fun startProxy(
            operation: String,
            mode: ProxyMode,
            profileId: UUID?,
            fallbackMessage: String,
        ): RuntimeActionOutcome<RuntimeMutationResult> = error("Not used")

        override suspend fun stopProxy(
            operation: String,
            mode: ProxyMode?,
            presentation: RuntimeActionFailurePresentation.Global,
        ): RuntimeActionOutcome<RuntimeMutationResult> = error("Not used")

        override suspend fun reloadCurrentProfile(
            operation: String,
            presentation: RuntimeActionFailurePresentation.Global,
        ): RuntimeActionOutcome<RuntimeMutationResult> = error("Not used")

        override suspend fun reloadIfActiveProfile(
            operation: String,
            profileId: UUID,
            presentation: RuntimeActionFailurePresentation,
        ): RuntimeActionOutcome<RuntimeMutationResult> = error("Not used")

        override suspend fun activateProfile(
            operation: String,
            profileId: UUID,
            enabled: Boolean,
            presentation: RuntimeActionFailurePresentation,
        ): RuntimeActionOutcome<RuntimeMutationResult> = error("Not used")

        override fun resolveDialogMode(): ProxyMode = ProxyMode.Tun

        override fun showGlobalError(message: String, title: String) = Unit
    }
}

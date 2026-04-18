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

package com.github.nomadboxlab.monadbox.feature.home.usecase

import com.github.nomadboxlab.monadbox.data.model.ProxyMode
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

class StopHomeProxyUseCaseTest {
    @Test
    fun invoke_delegatesToRuntimeExecutorWithHomeOperation() = runTest {
        val executor = RecordingRuntimeActionExecutor()
        val useCase = StopHomeProxyUseCase(executor)

        val outcome = useCase()

        assertTrue(outcome is RuntimeActionOutcome.Success)
        assertEquals("home:stop-proxy", executor.stopOperation)
        assertEquals("Error", executor.stopPresentation?.title)
    }

    private class RecordingRuntimeActionExecutor : RuntimeActionExecutor {
        override val isMutating: StateFlow<Boolean> = MutableStateFlow(false)
        override val activeOperation: StateFlow<String?> = MutableStateFlow(null)

        var stopOperation: String? = null
            private set

        var stopPresentation: RuntimeActionFailurePresentation.Global? = null
            private set

        override suspend fun stopProxy(
            operation: String,
            mode: ProxyMode?,
            presentation: RuntimeActionFailurePresentation.Global,
        ): RuntimeActionOutcome<RuntimeMutationResult> {
            stopOperation = operation
            stopPresentation = presentation
            return RuntimeActionOutcome.Success(
                RuntimeMutationResult(
                    status = RuntimeMutationStatus.Stopped,
                    effectiveMode = null,
                    runtimeRunning = false,
                )
            )
        }

        override suspend fun startProxy(
            operation: String,
            mode: ProxyMode,
            profileId: UUID?,
            fallbackMessage: String,
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

        override suspend fun applyConfigChange(
            operation: String,
            persist: suspend () -> Unit,
            rollback: suspend () -> Unit,
            shouldRestart: (ProxyMode) -> Boolean,
            presentation: RuntimeActionFailurePresentation,
        ): RuntimeActionOutcome<RuntimeMutationResult> = error("Not used")

        override fun resolveDialogMode(): ProxyMode = ProxyMode.Tun

        override fun showGlobalError(message: String, title: String) = Unit
    }
}

package com.github.yumelira.yumebox.runtime.client

import com.github.yumelira.yumebox.service.runtime.state.RuntimePhase

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
        currentPhase == RuntimePhase.Starting -> RuntimeStopResolution.IgnoreAsStale
        currentPhase == RuntimePhase.Idle && reason.isNullOrBlank() ->
            RuntimeStopResolution.SkipAsRedundant
        else -> RuntimeStopResolution.TransitionToIdle
    }
}

package com.github.yumelira.yumebox.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class ContractViewModel<Effect : Any> : ViewModel() {
    private val _effect =
        MutableSharedFlow<Effect>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    protected suspend fun emitEffect(effect: Effect) {
        _effect.emit(effect)
    }

    protected fun tryEmitEffect(effect: Effect) {
        _effect.tryEmit(effect)
    }
}

abstract class ContractStateViewModel<State : LoadableState<State>, Effect : Any>(
    initialState: State
) : ContractViewModel<Effect>() {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    protected fun updateState(transform: (State) -> State) {
        _uiState.update(transform)
    }

    protected fun setLoading(loading: Boolean) {
        updateState { it.withLoading(loading) }
    }

    protected fun postError(error: String, effect: Effect? = null) {
        updateState { it.withError(error).withLoading(false) }
        effect?.let(::tryEmitEffect)
    }

    protected fun postMessage(message: String, effect: Effect? = null) {
        updateState { it.withMessage(message) }
        effect?.let(::tryEmitEffect)
    }
}

package com.github.yumelira.yumebox.core.presentation

import androidx.lifecycle.ViewModel
import com.github.yumelira.yumebox.domain.model.ErrorCategory
import com.github.yumelira.yumebox.domain.model.ErrorPhase
import com.github.yumelira.yumebox.domain.model.StructuredError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State>(initialState: State) : ViewModel() {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    protected fun updateState(transform: (State) -> State) {
        _uiState.update(transform)
    }

    protected val currentState: State
        get() = _uiState.value
}

interface LoadableState<T : LoadableState<T>> {
    val isLoading: Boolean
    val error: String?
    val structuredError: StructuredError?
        get() = null

    val message: String?

    fun withLoading(loading: Boolean): T

    fun withError(error: String?): T

    fun withStructuredError(error: StructuredError?): T = withError(error?.userVisibleMessage)

    fun withMessage(message: String?): T
}

abstract class BaseLoadableViewModel<State : LoadableState<State>>(initialState: State) :
    BaseViewModel<State>(initialState) {

    protected fun setLoading(loading: Boolean) {
        updateState { it.withLoading(loading) }
    }

    protected fun showError(error: String) {
        updateState { it.withError(error).withLoading(false) }
    }

    protected fun showStructuredError(error: StructuredError) {
        updateState { it.withStructuredError(error).withLoading(false) }
    }

    protected fun showError(
        throwable: Throwable,
        phase: ErrorPhase = ErrorPhase.Running,
        category: ErrorCategory = ErrorCategory.Unknown,
    ) {
        showStructuredError(
            StructuredError.fromThrowable(throwable, phase = phase, category = category)
        )
    }

    protected fun showMessage(message: String) {
        updateState { it.withMessage(message) }
    }

    protected fun clearError() {
        updateState { it.withError(null) }
    }

    protected fun clearMessage() {
        updateState { it.withMessage(null) }
    }
}

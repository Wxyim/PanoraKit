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

package com.github.nomadboxlab.monadbox.core.presentation

import androidx.lifecycle.ViewModel
import com.github.nomadboxlab.monadbox.domain.model.ErrorCategory
import com.github.nomadboxlab.monadbox.domain.model.ErrorPhase
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
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

    protected fun postStructuredError(error: StructuredError, effect: Effect? = null) {
        updateState { it.withStructuredError(error).withLoading(false) }
        effect?.let(::tryEmitEffect)
    }

    protected fun postError(
        throwable: Throwable,
        phase: ErrorPhase = ErrorPhase.Running,
        category: ErrorCategory = ErrorCategory.Unknown,
        effect: Effect? = null,
    ) {
        postStructuredError(
            StructuredError.fromThrowable(throwable, phase = phase, category = category),
            effect,
        )
    }

    protected fun postMessage(message: String, effect: Effect? = null) {
        updateState { it.withMessage(message) }
        effect?.let(::tryEmitEffect)
    }
}

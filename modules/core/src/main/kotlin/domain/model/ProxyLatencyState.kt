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

package com.github.nomadboxlab.monadbox.domain.model

sealed interface ProxyLatencyState {
    data object Unknown : ProxyLatencyState

    data object Timeout : ProxyLatencyState

    data class Available(val delayMs: Int) : ProxyLatencyState

    val isObserved: Boolean
        get() = this != Unknown

    val sortBucket: Int
        get() =
            when (this) {
                is Available -> 0
                Timeout -> 1
                Unknown -> 2
            }

    val sortValue: Int
        get() =
            when (this) {
                is Available -> delayMs
                Timeout -> Int.MAX_VALUE - 1
                Unknown -> Int.MAX_VALUE
            }

    companion object {
        const val TIMEOUT_SENTINEL_DELAY_MS = 65_535
        const val DISPLAY_TIMEOUT_DELAY_MS = -1

        fun fromResolvedDelay(delay: Int): ProxyLatencyState =
            when {
                delay > 0 -> Available(delay)
                delay < 0 -> Timeout
                else -> Unknown
            }

        fun fromObservedDelay(delay: Int): ProxyLatencyState =
            when {
                delay == TIMEOUT_SENTINEL_DELAY_MS -> Timeout
                delay > 0 -> Available(delay)
                delay < 0 -> Timeout
                else -> Unknown
            }

        fun normalizeSnapshotDelay(delay: Int): Int =
            if (delay == TIMEOUT_SENTINEL_DELAY_MS) 0 else delay
    }
}

fun Int.toProxyLatencyState(): ProxyLatencyState = ProxyLatencyState.fromResolvedDelay(this)

fun Int.toObservedProxyLatencyState(): ProxyLatencyState = ProxyLatencyState.fromObservedDelay(this)

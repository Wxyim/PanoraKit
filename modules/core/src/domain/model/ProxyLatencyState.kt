package com.github.yumelira.yumebox.domain.model

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

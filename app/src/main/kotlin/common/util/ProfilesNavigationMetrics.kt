/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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

package com.github.nomadboxlab.monadbox.common.util

import com.tencent.mmkv.MMKV

object ProfilesNavigationMetrics {
    private const val PREFIX = "metrics.profiles.nav"

    private const val KEY_CLICK_TOTAL = "$PREFIX.click.total"
    private const val KEY_CLICK_GUI = "$PREFIX.click.gui"
    private const val KEY_CLICK_TEXT = "$PREFIX.click.text"

    private const val KEY_NAV_ATTEMPT_TOTAL = "$PREFIX.nav.attempt.total"
    private const val KEY_NAV_DISPATCH_SUCCESS_TOTAL = "$PREFIX.nav.dispatch.success.total"
    private const val KEY_NAV_DISPATCH_FAILURE_TOTAL = "$PREFIX.nav.dispatch.failure.total"
    private const val KEY_NAV_DISPATCH_FAILURE_RATE = "$PREFIX.nav.dispatch.failure_rate_percent"

    private const val KEY_NAV_ATTEMPT_GUI = "$PREFIX.nav.attempt.gui"
    private const val KEY_NAV_DISPATCH_SUCCESS_GUI = "$PREFIX.nav.dispatch.success.gui"
    private const val KEY_NAV_DISPATCH_FAILURE_GUI = "$PREFIX.nav.dispatch.failure.gui"

    private const val KEY_NAV_ATTEMPT_TEXT = "$PREFIX.nav.attempt.text"
    private const val KEY_NAV_DISPATCH_SUCCESS_TEXT = "$PREFIX.nav.dispatch.success.text"
    private const val KEY_NAV_DISPATCH_FAILURE_TEXT = "$PREFIX.nav.dispatch.failure.text"

    private const val KEY_ROUTE_ENTER_GUI = "$PREFIX.route_enter.gui"
    private const val KEY_ROUTE_ENTER_TEXT = "$PREFIX.route_enter.text"

    private const val KEY_LAST_FAILURE_EVENT = "$PREFIX.last_failure.event"
    private const val KEY_LAST_FAILURE_REASON = "$PREFIX.last_failure.reason"
    private const val KEY_LAST_FAILURE_TIME = "$PREFIX.last_failure.time_millis"

    internal interface Store {
        fun getInt(key: String, defaultValue: Int = 0): Int

        fun putInt(key: String, value: Int)

        fun putFloat(key: String, value: Float)

        fun putString(key: String, value: String)

        fun putLong(key: String, value: Long)
    }

    private class MmkvStore(private val mmkv: MMKV) : Store {
        override fun getInt(key: String, defaultValue: Int): Int = mmkv.decodeInt(key, defaultValue)

        override fun putInt(key: String, value: Int) {
            mmkv.encode(key, value)
        }

        override fun putFloat(key: String, value: Float) {
            mmkv.encode(key, value)
        }

        override fun putString(key: String, value: String) {
            mmkv.encode(key, value)
        }

        override fun putLong(key: String, value: Long) {
            mmkv.encode(key, value)
        }
    }

    fun onGuiEditClick(mmkv: MMKV) = onGuiEditClick(MmkvStore(mmkv))

    internal fun onGuiEditClick(store: Store) {
        increment(store, KEY_CLICK_TOTAL)
        increment(store, KEY_CLICK_GUI)
        increment(store, KEY_NAV_ATTEMPT_TOTAL)
        increment(store, KEY_NAV_ATTEMPT_GUI)
    }

    fun onTextEditorClick(mmkv: MMKV) = onTextEditorClick(MmkvStore(mmkv))

    internal fun onTextEditorClick(store: Store) {
        increment(store, KEY_CLICK_TOTAL)
        increment(store, KEY_CLICK_TEXT)
        increment(store, KEY_NAV_ATTEMPT_TOTAL)
        increment(store, KEY_NAV_ATTEMPT_TEXT)
    }

    fun onGuiEditNavigationDispatched(mmkv: MMKV) = onGuiEditNavigationDispatched(MmkvStore(mmkv))

    internal fun onGuiEditNavigationDispatched(store: Store) {
        increment(store, KEY_NAV_DISPATCH_SUCCESS_TOTAL)
        increment(store, KEY_NAV_DISPATCH_SUCCESS_GUI)
        updateDispatchFailureRate(store)
    }

    fun onTextEditorNavigationDispatched(mmkv: MMKV) =
        onTextEditorNavigationDispatched(MmkvStore(mmkv))

    internal fun onTextEditorNavigationDispatched(store: Store) {
        increment(store, KEY_NAV_DISPATCH_SUCCESS_TOTAL)
        increment(store, KEY_NAV_DISPATCH_SUCCESS_TEXT)
        updateDispatchFailureRate(store)
    }

    fun onGuiEditNavigationDispatchFailure(mmkv: MMKV, throwable: Throwable?) =
        onGuiEditNavigationDispatchFailure(MmkvStore(mmkv), throwable)

    internal fun onGuiEditNavigationDispatchFailure(store: Store, throwable: Throwable?) {
        increment(store, KEY_NAV_DISPATCH_FAILURE_TOTAL)
        increment(store, KEY_NAV_DISPATCH_FAILURE_GUI)
        recordLastFailure(store, "gui", throwable)
        updateDispatchFailureRate(store)
    }

    fun onTextEditorNavigationDispatchFailure(mmkv: MMKV, throwable: Throwable?) =
        onTextEditorNavigationDispatchFailure(MmkvStore(mmkv), throwable)

    internal fun onTextEditorNavigationDispatchFailure(store: Store, throwable: Throwable?) {
        increment(store, KEY_NAV_DISPATCH_FAILURE_TOTAL)
        increment(store, KEY_NAV_DISPATCH_FAILURE_TEXT)
        recordLastFailure(store, "text", throwable)
        updateDispatchFailureRate(store)
    }

    fun onGuiEditRouteEntered(mmkv: MMKV) = onGuiEditRouteEntered(MmkvStore(mmkv))

    internal fun onGuiEditRouteEntered(store: Store) {
        increment(store, KEY_ROUTE_ENTER_GUI)
    }

    fun onTextEditorRouteEntered(mmkv: MMKV) = onTextEditorRouteEntered(MmkvStore(mmkv))

    internal fun onTextEditorRouteEntered(store: Store) {
        increment(store, KEY_ROUTE_ENTER_TEXT)
    }

    private fun increment(store: Store, key: String) {
        store.putInt(key, store.getInt(key, 0) + 1)
    }

    private fun updateDispatchFailureRate(store: Store) {
        val attempts = store.getInt(KEY_NAV_ATTEMPT_TOTAL, 0)
        val failures = store.getInt(KEY_NAV_DISPATCH_FAILURE_TOTAL, 0)
        val ratePercent =
            if (attempts <= 0) {
                0f
            } else {
                (failures.toFloat() / attempts.toFloat()) * 100f
            }
        store.putFloat(KEY_NAV_DISPATCH_FAILURE_RATE, ratePercent)
    }

    private fun recordLastFailure(store: Store, event: String, throwable: Throwable?) {
        val reason =
            throwable?.message?.trim()?.takeIf { it.isNotEmpty() }?.take(300)
                ?: throwable?.javaClass?.simpleName
                ?: "unknown"
        store.putString(KEY_LAST_FAILURE_EVENT, event)
        store.putString(KEY_LAST_FAILURE_REASON, reason)
        store.putLong(KEY_LAST_FAILURE_TIME, System.currentTimeMillis())
    }
}

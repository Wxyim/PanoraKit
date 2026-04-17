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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfilesNavigationMetricsTest {
    @Test
    fun clickMetrics_shouldIncrementClickAndAttemptCounters() {
        val store = InMemoryStore()

        ProfilesNavigationMetrics.onGuiEditClick(store)
        ProfilesNavigationMetrics.onTextEditorClick(store)

        assertEquals(2, store.getInt("metrics.profiles.nav.click.total"))
        assertEquals(1, store.getInt("metrics.profiles.nav.click.gui"))
        assertEquals(1, store.getInt("metrics.profiles.nav.click.text"))
        assertEquals(2, store.getInt("metrics.profiles.nav.nav.attempt.total"))
        assertEquals(1, store.getInt("metrics.profiles.nav.nav.attempt.gui"))
        assertEquals(1, store.getInt("metrics.profiles.nav.nav.attempt.text"))
    }

    @Test
    fun dispatchMetrics_shouldTrackSuccessFailureAndRate() {
        val store = InMemoryStore()

        ProfilesNavigationMetrics.onGuiEditClick(store)
        ProfilesNavigationMetrics.onTextEditorClick(store)
        ProfilesNavigationMetrics.onGuiEditNavigationDispatched(store)
        ProfilesNavigationMetrics.onTextEditorNavigationDispatchFailure(
            store,
            IllegalStateException("nav graph missing"),
        )

        assertEquals(1, store.getInt("metrics.profiles.nav.nav.dispatch.success.total"))
        assertEquals(1, store.getInt("metrics.profiles.nav.nav.dispatch.success.gui"))
        assertEquals(1, store.getInt("metrics.profiles.nav.nav.dispatch.failure.total"))
        assertEquals(1, store.getInt("metrics.profiles.nav.nav.dispatch.failure.text"))
        assertEquals(
            50f,
            store.getFloat("metrics.profiles.nav.nav.dispatch.failure_rate_percent"),
            0.0001f,
        )
    }

    @Test
    fun dispatchFailure_shouldRecordLastFailureMetadata() {
        val store = InMemoryStore()

        ProfilesNavigationMetrics.onGuiEditNavigationDispatchFailure(
            store,
            RuntimeException("boom"),
        )

        assertEquals("gui", store.getString("metrics.profiles.nav.last_failure.event"))
        assertEquals("boom", store.getString("metrics.profiles.nav.last_failure.reason"))
        assertTrue(store.getLong("metrics.profiles.nav.last_failure.time_millis") > 0L)
    }

    @Test
    fun routeEnter_shouldIncrementCountersOncePerCallback() {
        val store = InMemoryStore()

        ProfilesNavigationMetrics.onGuiEditRouteEntered(store)
        ProfilesNavigationMetrics.onTextEditorRouteEntered(store)

        assertEquals(1, store.getInt("metrics.profiles.nav.route_enter.gui"))
        assertEquals(1, store.getInt("metrics.profiles.nav.route_enter.text"))
    }

    private class InMemoryStore : ProfilesNavigationMetrics.Store {
        private val values = mutableMapOf<String, Any>()

        override fun getInt(key: String, defaultValue: Int): Int =
            values[key] as? Int ?: defaultValue

        override fun putInt(key: String, value: Int) {
            values[key] = value
        }

        override fun putFloat(key: String, value: Float) {
            values[key] = value
        }

        override fun putString(key: String, value: String) {
            values[key] = value
        }

        override fun putLong(key: String, value: Long) {
            values[key] = value
        }

        fun getFloat(key: String): Float = values[key] as? Float ?: 0f

        fun getString(key: String): String? = values[key] as? String

        fun getLong(key: String): Long = values[key] as? Long ?: 0L
    }
}

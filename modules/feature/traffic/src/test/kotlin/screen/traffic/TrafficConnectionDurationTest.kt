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

package com.github.nomadboxlab.monadbox.feature.traffic

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class TrafficConnectionDurationTest {
    @Test
    fun negativeDurationIsClampedToZero() {
        val start = Instant.ofEpochMilli(2_000L).toString()

        assertEquals("00:00:00", calculateDuration(start, closeTimeMs = 1_000L))
    }

    @Test
    fun formatsHoursMinutesAndSeconds() {
        val start = Instant.ofEpochMilli(1_000L).toString()
        val close = 1_000L + 3_661_000L

        assertEquals("01:01:01", calculateDuration(start, closeTimeMs = close))
    }

    @Test
    fun measuredMonotonicDurationWinsOverWallClockSkew() {
        val start = Instant.ofEpochMilli(2_000L).toString()

        assertEquals(
            "01:01:01",
            calculateDuration(start, closeTimeMs = 1_000L, measuredDurationMs = 3_661_000L),
        )
    }

    @Test
    fun invalidStartIsZero() {
        assertEquals("00:00:00", calculateDuration("not-a-timestamp"))
        assertEquals("00:00:00", calculateDuration(""))
    }
}

/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * MonadBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.service.common.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ProcFsUidResolverTest {

    @Test
    fun portTier_acceptsUidZeroOnlyWithoutAPositiveCompetitor() {
        assertEquals(0, ProcFsUidResolver.resolvePortUid(-1, portSystem = true, ambiguous = false))
        assertEquals(
            10_123,
            ProcFsUidResolver.resolvePortUid(10_123, portSystem = false, ambiguous = false),
        )
        assertEquals(
            -1,
            ProcFsUidResolver.resolvePortUid(10_123, portSystem = true, ambiguous = false),
        )
        assertEquals(
            -1,
            ProcFsUidResolver.resolvePortUid(10_123, portSystem = false, ambiguous = true),
        )
        assertEquals(
            -1,
            ProcFsUidResolver.resolvePortUid(-1, portSystem = false, ambiguous = false),
        )
    }
}

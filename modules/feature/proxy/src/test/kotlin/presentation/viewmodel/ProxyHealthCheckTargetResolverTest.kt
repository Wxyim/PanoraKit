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
 *
 */

package com.github.nomadboxlab.monadbox.presentation.viewmodel

import com.github.nomadboxlab.monadbox.core.model.Proxy
import com.github.nomadboxlab.monadbox.domain.model.ProxyGroupInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class ProxyHealthCheckTargetResolverTest {
    @Test
    fun groupRequest_skipsAlreadyTestingGroup() {
        val groups = listOf(sampleGroup("HK"), sampleGroup("JP"))

        val targets =
            resolveHealthCheckTargets(
                currentGroups = groups,
                requestedGroupName = "HK",
                activeTestingGroupNames = setOf("HK"),
            )

        assertEquals(emptySet<String>(), targets)
    }

    @Test
    fun allGroupsRequest_keepsOrderAndFiltersActiveGroups() {
        val groups = listOf(sampleGroup("HK"), sampleGroup("JP"), sampleGroup("SG"))

        val targets =
            resolveHealthCheckTargets(
                currentGroups = groups,
                requestedGroupName = null,
                activeTestingGroupNames = setOf("JP"),
            )

        assertEquals(linkedSetOf("HK", "SG"), targets)
    }

    private fun sampleGroup(name: String): ProxyGroupInfo {
        return ProxyGroupInfo(
            name = name,
            type = Proxy.Type.Selector,
            proxies = emptyList(),
            now = "DIRECT",
        )
    }
}

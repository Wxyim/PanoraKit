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

package com.github.nomadboxlab.monadbox.presentation.runtime

import com.github.nomadboxlab.monadbox.feature.home.api.HomeRuntimeController
import com.github.nomadboxlab.monadbox.runtime.contract.RuntimeStateReader
import dev.oom_wg.purejoy.mlang.MLang
import java.util.UUID
import kotlinx.coroutines.flow.StateFlow

class DefaultHomeRuntimeController(
    private val runtimeStateReader: RuntimeStateReader,
    private val runtimeActionExecutor: RuntimeActionExecutor,
) : HomeRuntimeController {
    override val isRunning: StateFlow<Boolean> = runtimeStateReader.isRuntimeRunning

    override fun isCurrentProfile(profileId: UUID): Boolean {
        return runtimeStateReader.currentRuntimeProfile.value?.id == profileId.toString()
    }

    override suspend fun stopProxy() {
        runtimeActionExecutor.stopProxy(
            operation = "home-api:stop-proxy",
            presentation =
                RuntimeActionFailurePresentation.Global(
                    message = { reason -> MLang.Home.Message.StopFailed.format(reason) }
                ),
        )
    }
}

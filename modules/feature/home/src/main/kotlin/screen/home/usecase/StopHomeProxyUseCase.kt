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

package com.github.nomadboxlab.monadbox.feature.home.usecase

import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionExecutor
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionFailurePresentation
import com.github.nomadboxlab.monadbox.presentation.runtime.RuntimeActionOutcome
import com.github.nomadboxlab.monadbox.runtime.client.RuntimeMutationResult
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StopHomeProxyUseCase(private val runtimeActionExecutor: RuntimeActionExecutor) {
    suspend operator fun invoke(): RuntimeActionOutcome<RuntimeMutationResult> {
        return withContext(Dispatchers.IO) {
            runtimeActionExecutor.stopProxy(
                operation = "home:stop-proxy",
                presentation =
                    RuntimeActionFailurePresentation.Global(
                        message = { reason -> MLang.Home.Message.StopFailed.format(reason) }
                    ),
            )
        }
    }
}

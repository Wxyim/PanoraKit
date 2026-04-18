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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.service.runtime.state

import com.github.nomadboxlab.monadbox.domain.model.ProductLifecycleState
import com.github.nomadboxlab.monadbox.domain.model.ProxyMode
import com.github.nomadboxlab.monadbox.domain.model.StructuredError
import kotlinx.serialization.Serializable

@Serializable
enum class RuntimeOwner {
    None,
    LocalTun,
    LocalHttp,
    RootTun,
}

@Serializable
enum class RuntimePhase {
    Idle,
    Starting,
    Running,
    Stopping,
    Failed;

    val running: Boolean
        get() = this == Running

    fun toProductLifecycleState(): ProductLifecycleState =
        when (this) {
            Idle -> ProductLifecycleState.Idle
            Starting -> ProductLifecycleState.Preparing
            Running -> ProductLifecycleState.Active
            Stopping -> ProductLifecycleState.Stopping
            Failed -> ProductLifecycleState.Failed
        }
}

@Serializable
data class RuntimeSnapshot(
    val owner: RuntimeOwner = RuntimeOwner.None,
    val phase: RuntimePhase = RuntimePhase.Idle,
    val targetMode: ProxyMode = ProxyMode.Tun,
    val profileReady: Boolean = false,
    val groupsReady: Boolean = false,
    val trafficReady: Boolean = false,
    val configReady: Boolean = false,
    val transportReady: Boolean = false,
    val logReady: Boolean = false,
    val profileUuid: String? = null,
    val profileName: String? = null,
    val lastError: String? = null,
    val lastStructuredError: StructuredError? = null,
    val startedAt: Long? = null,
    val effectiveFingerprint: String? = null,
    val generation: Long = 0L,
    val running: Boolean = phase.running,
) {
    val payloadReady: Boolean
        get() = profileReady && groupsReady && trafficReady

    val lifecycleState: ProductLifecycleState
        get() {
            val base = phase.toProductLifecycleState()
            if (base == ProductLifecycleState.Active && !payloadReady) {
                return ProductLifecycleState.Degraded
            }
            return base
        }
}

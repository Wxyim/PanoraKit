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

package com.github.nomadboxlab.monadbox.service.runtime.session

import com.github.nomadboxlab.monadbox.core.Clash

class RootTunTransport : RuntimeTransport {
    private val startupLogStore =
        RuntimeStartupLogStore(
            com.github.nomadboxlab.monadbox.core.Global.application,
            RuntimeStartupLogStore.Scope.ROOT_TUN,
        )

    override fun start(spec: RuntimeSpec) {
        val config = spec.rootTunConfig ?: error("root tun config missing")
        startupLogStore.append("ROOT_TUN transport start: begin")
        Clash.startRootTun(config)?.let { error(it) }
        startupLogStore.append("ROOT_TUN transport start: done")
    }

    override fun stop() {
        Clash.stopRootTun()
        Clash.stopHttp()
        Clash.stopTun()
    }
}

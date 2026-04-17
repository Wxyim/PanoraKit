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

package com.github.nomadboxlab.monadbox.service.clash.module

import android.app.Service
import android.content.Intent
import com.github.nomadboxlab.monadbox.core.Clash
import java.util.*

class TimeZoneModule(service: Service) : Module<Unit>(service) {
    override suspend fun run() {
        val timeZones = receiveBroadcast { addAction(Intent.ACTION_TIMEZONE_CHANGED) }

        while (true) {
            val timeZone = TimeZone.getDefault()

            Clash.notifyTimeZoneChanged(timeZone.id, timeZone.rawOffset / 1000)

            timeZones.receive()
        }
    }
}

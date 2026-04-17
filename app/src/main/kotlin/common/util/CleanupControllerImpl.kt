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

package com.github.nomadboxlab.monadbox.common.util

import com.github.nomadboxlab.monadbox.feature.settings.CleanupController
import com.github.nomadboxlab.monadbox.startup.StorageCleanupScheduler

class CleanupControllerImpl(
    private val manager: StorageCleanupManager,
    private val scheduler: StorageCleanupScheduler,
) : CleanupController {

    override fun syncSchedule(enabled: Boolean) {
        scheduler.sync(enabled)
    }

    override suspend fun runCleanupNow(): CleanupController.CleanupResult {
        val r = manager.runCleanupNow()
        return CleanupController.CleanupResult(
            executed = r.executed,
            beforeBytes = r.beforeBytes,
            afterBytes = r.afterBytes,
            freedBytes = r.freedBytes,
            thresholdBytes = r.thresholdBytes,
            archiveFileName = r.archiveFileName,
            orphanImportedDirsRemoved = r.orphanImportedDirsRemoved,
            processingArtifactsRemoved = r.processingArtifactsRemoved,
        )
    }
}

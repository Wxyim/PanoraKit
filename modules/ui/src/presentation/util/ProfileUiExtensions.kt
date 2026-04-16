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

package com.github.nomadboxlab.monadbox.presentation.util

import com.github.nomadboxlab.monadbox.common.util.ByteFormatter
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val Profile.enabled: Boolean
    get() = active

val Profile.provider: String?
    get() = null

val Profile.usedBytes: Long
    get() = upload + download

val Profile.totalBytes: Long?
    get() = if (total > 0) total else null

val Profile.expireAt: Long?
    get() = if (expire > 0) expire else null

val Profile.lastUpdatedAt: Long?
    get() = if (updatedAt > 0) updatedAt else null

fun Profile.getDisplayProvider(): String =
    when (type) {
        Profile.Type.Url -> provider ?: MLang.Component.ProfileCard.RemoteSubscription
        Profile.Type.File -> MLang.Component.ProfileCard.LocalFile
        Profile.Type.External -> MLang.Component.ProfileCard.LocalConfig
    }

fun Profile.getInfoText(): String =
    when (type) {
        Profile.Type.Url -> {
            buildString {
                val totalBytesValue = totalBytes
                if (totalBytesValue != null && totalBytesValue > 0) {
                    val usedPercent = usedBytes * 100 / totalBytesValue
                    append(
                        MLang.Component.ProfileCard.Traffic.format(
                            ByteFormatter.format(usedBytes),
                            ByteFormatter.format(totalBytesValue),
                            usedPercent.toInt(),
                        )
                    )
                } else if (usedBytes > 0) {
                    append(
                        MLang.Component.ProfileCard.UsedTraffic.format(
                            ByteFormatter.format(usedBytes)
                        )
                    )
                } else {
                    append(MLang.Component.ProfileCard.ClickToUpdate)
                }

                expireAt?.let { expireTime ->
                    val expireDate =
                        java.time.Instant.ofEpochMilli(expireTime)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                    val now = java.time.LocalDate.now()
                    val daysLeft = java.time.temporal.ChronoUnit.DAYS.between(now, expireDate)

                    if (isNotEmpty()) append("\n")

                    if (daysLeft > 0) {
                        append(
                            MLang.Component.ProfileCard.ExpireAt.format(
                                expireDate,
                                daysLeft.toInt(),
                            )
                        )
                    } else if (daysLeft == 0L) {
                        append(MLang.Component.ProfileCard.ExpireToday)
                    } else {
                        append(MLang.Component.ProfileCard.Expired.format(expireDate))
                    }
                }
            }
        }

        Profile.Type.File -> MLang.Component.ProfileCard.LocalConfig
        Profile.Type.External -> MLang.Component.ProfileCard.LocalConfig
    }

fun Profile.shouldShowUpdateButton(): Boolean = type == Profile.Type.Url

fun Profile.isConfigSaved(workDir: File): Boolean {
    return File(workDir, "${uuid}/config.yaml").exists()
}

fun formatProfileTimestamp(timestamp: Long): String {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()))
}

/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.presentation.util

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.yumelira.yumebox.core.model.Provider
import com.github.yumelira.yumebox.domain.model.RemoteOverrideResource
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Link
import com.github.yumelira.yumebox.presentation.icon.yume.Rocket
import dev.oom_wg.purejoy.mlang.MLang
import dev.oom_wg.purejoy.mlang.MLangStatus

data class ExternalResourceBadgeUi(
    val label: String,
    val tone: SemanticTone,
    val leadingDot: Boolean = false,
)

fun Provider.headerIcon(): ImageVector {
    return when (type) {
        Provider.Type.Proxy -> Yume.Rocket
        Provider.Type.Rule -> Yume.Link
    }
}

fun Provider.headerTone(): SemanticTone {
    return when (type) {
        Provider.Type.Proxy -> SemanticTone.Brand
        Provider.Type.Rule -> SemanticTone.Warning
    }
}

fun Provider.VehicleType.transportTone(): SemanticTone {
    return when (this) {
        Provider.VehicleType.HTTP -> SemanticTone.Info
        Provider.VehicleType.File -> SemanticTone.Neutral
        Provider.VehicleType.Inline -> SemanticTone.Warning
        Provider.VehicleType.Compatible -> SemanticTone.Brand
    }
}

fun Provider.VehicleType.displayLabel(): String {
    return when (this) {
        Provider.VehicleType.HTTP -> MLang.Providers.Transport.Http
        Provider.VehicleType.File -> MLang.Providers.Transport.File
        Provider.VehicleType.Inline -> MLang.Providers.Transport.Inline
        Provider.VehicleType.Compatible -> MLang.Providers.Transport.Compatible
    }
}

fun Provider.statusTone(isUpdating: Boolean): SemanticTone {
    return when {
        isUpdating -> SemanticTone.Info
        updatedAt > 0L -> SemanticTone.Success
        vehicleType == Provider.VehicleType.HTTP -> SemanticTone.Info
        else -> SemanticTone.Success
    }
}

fun Provider.statusLabel(isUpdating: Boolean): String {
    return when {
        isUpdating -> MLangStatus.Common.Waiting
        updatedAt > 0L -> MLangStatus.Common.Ready
        vehicleType == Provider.VehicleType.HTTP -> MLangStatus.Common.Waiting
        else -> MLangStatus.Common.Ready
    }
}

fun Provider.freshnessBadge(
    isUpdating: Boolean,
    formatTimestamp: (Long) -> String,
): ExternalResourceBadgeUi? {
    return when {
        isUpdating ->
            ExternalResourceBadgeUi(
                label = MLangStatus.Common.Waiting,
                tone = SemanticTone.Info,
                leadingDot = true,
            )
        updatedAt > 0L ->
            ExternalResourceBadgeUi(
                label = formatTimestamp(updatedAt),
                tone = statusTone(isUpdating),
            )
        vehicleType == Provider.VehicleType.HTTP ->
            ExternalResourceBadgeUi(
                label = MLangStatus.Common.Waiting,
                tone = SemanticTone.Info,
                leadingDot = true,
            )
        else -> null
    }
}

fun Provider.canUpdateFromRemote(): Boolean = vehicleType == Provider.VehicleType.HTTP

fun Provider.canUploadFromFile(): Boolean = path.isNotBlank()

fun Provider.itemCountLabel(): String? =
    if (count > 0) MLang.Providers.Summary.ItemCount.format(count) else null

fun Provider.updatedAtLabel(formatTimestamp: (Long) -> String): String? =
    if (updatedAt > 0L) formatTimestamp(updatedAt) else null

fun RemoteOverrideResource.itemCountLabel(): String? =
    if (ruleCount > 0) MLang.Providers.Summary.ItemCount.format(ruleCount) else null

fun RemoteOverrideResource.updatedAtLabel(formatTimestamp: (Long) -> String): String? =
    if (lastUpdatedAt > 0L) formatTimestamp(lastUpdatedAt) else null

fun RemoteOverrideResource.transportLabel(): String = MLang.Providers.Transport.Http

fun RemoteOverrideResource.transportTone(): SemanticTone = SemanticTone.Info

fun RemoteOverrideResource.statusTone(isUpdating: Boolean): SemanticTone {
    return when {
        isUpdating -> SemanticTone.Info
        lastUpdatedAt <= 0L -> SemanticTone.Info
        isStale() -> SemanticTone.Warning
        else -> SemanticTone.Success
    }
}

fun RemoteOverrideResource.statusLabel(isUpdating: Boolean): String {
    return when {
        isUpdating -> MLangStatus.Common.Waiting
        lastUpdatedAt <= 0L -> MLangStatus.Common.Waiting
        isStale() -> MLangStatus.Common.Attention
        else -> MLangStatus.Common.Ready
    }
}

fun RemoteOverrideResource.freshnessBadge(
    isUpdating: Boolean,
    formatTimestamp: (Long) -> String,
): ExternalResourceBadgeUi {
    return when {
        isUpdating ->
            ExternalResourceBadgeUi(
                label = MLangStatus.Common.Waiting,
                tone = SemanticTone.Info,
                leadingDot = true,
            )
        lastUpdatedAt > 0L ->
            ExternalResourceBadgeUi(
                label = formatTimestamp(lastUpdatedAt),
                tone = statusTone(isUpdating),
            )
        else ->
            ExternalResourceBadgeUi(
                label = MLangStatus.Common.Waiting,
                tone = SemanticTone.Info,
                leadingDot = true,
            )
    }
}

fun RemoteOverrideResource.isStale(now: Long = System.currentTimeMillis()): Boolean {
    if (lastUpdatedAt <= 0L || updateIntervalSeconds <= 0L) {
        return false
    }
    return now - lastUpdatedAt > updateIntervalSeconds * 1000L
}

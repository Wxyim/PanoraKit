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

package com.github.yumelira.yumebox.service.runtime.entity

import com.github.yumelira.yumebox.domain.model.EffectiveStateRelation
import com.github.yumelira.yumebox.domain.model.ProductChangeState
import com.github.yumelira.yumebox.domain.model.ProductLifecycleState
import com.github.yumelira.yumebox.domain.model.ProductObjectOwner
import com.github.yumelira.yumebox.domain.model.ProductProfileObject
import com.github.yumelira.yumebox.domain.model.ProductRiskLevel

fun Profile.toProductProfileObject(configSaved: Boolean = true): ProductProfileObject {
    return ProductProfileObject(
        stableId = uuid.toString(),
        displayName = name,
        lifecycleState =
            if (active) {
                ProductLifecycleState.Active
            } else {
                ProductLifecycleState.Idle
            },
        updatedAtMillis = updatedAt,
        owner = resolveOwner(),
        editable = true,
        riskLevel = resolveRiskLevel(configSaved),
        effectiveRelation =
            if (active) {
                EffectiveStateRelation.Active
            } else {
                EffectiveStateRelation.Candidate
            },
        changeState =
            if (configSaved) {
                ProductChangeState.Synced
            } else {
                ProductChangeState.Invalid
            },
        sourceKind = type.name,
        sourceUri = source,
        trafficUsedBytes = (upload + download).coerceAtLeast(0L),
        trafficTotalBytes = total.coerceAtLeast(0L),
        expiresAtMillis = expire.coerceAtLeast(0L),
        configSaved = configSaved,
    )
}

private fun Profile.resolveOwner(): ProductObjectOwner {
    val normalizedSource = source.trim()
    val label =
        when {
            normalizedSource.isBlank() -> type.name
            type == Profile.Type.Url -> normalizedSource.toUriHostLabel()
            else -> normalizedSource
        }
    return ProductObjectOwner(
        id = normalizedSource.ifBlank { type.name },
        label = label,
        kind = type.name,
    )
}

private fun Profile.resolveRiskLevel(configSaved: Boolean): ProductRiskLevel {
    return when {
        !configSaved -> ProductRiskLevel.High
        type == Profile.Type.External -> ProductRiskLevel.Medium
        type == Profile.Type.File -> ProductRiskLevel.Medium
        else -> ProductRiskLevel.Low
    }
}

private fun String.toUriHostLabel(): String {
    return runCatching { android.net.Uri.parse(this).host?.takeIf { it.isNotBlank() } }.getOrNull()
        ?: this
}

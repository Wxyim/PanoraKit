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

package com.github.yumelira.yumebox.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class PolicyGroupType {
    Select,
    UrlTest,
    Fallback,
    LoadBalance,
    Relay,
}

@Serializable data class PolicyGroupMember(val name: String, val kind: PolicyGroupMemberKind)

@Serializable
enum class PolicyGroupMemberKind {
    Proxy,
    NestedGroup,
    Provider,
}

@Serializable
data class PolicyGroup(
    val groupId: String,
    val name: String,
    val type: PolicyGroupType,
    val members: List<PolicyGroupMember> = emptyList(),
    val selectedMember: String? = null,
    val profileId: String,
    val overrideSourceId: String? = null,
    override val updatedAtMillis: Long,
    val hidden: Boolean = false,
    val icon: String? = null,
    val testUrl: String? = null,
    val testIntervalSeconds: Int? = null,
) : ProductObjectContract {

    override val stableId: String
        get() = groupId

    override val displayName: String
        get() = name

    override val lifecycleState: ProductLifecycleState
        get() =
            when {
                members.isEmpty() -> ProductLifecycleState.Degraded
                selectedMember != null -> ProductLifecycleState.Active
                else -> ProductLifecycleState.Idle
            }

    override val owner: ProductObjectOwner
        get() =
            ProductObjectOwner(
                id = profileId,
                label = overrideSourceId ?: profileId,
                kind = if (overrideSourceId != null) "override" else "profile",
            )

    override val editable: Boolean
        get() = type == PolicyGroupType.Select

    override val riskLevel: ProductRiskLevel
        get() =
            when {
                members.isEmpty() -> ProductRiskLevel.High
                type == PolicyGroupType.Relay -> ProductRiskLevel.Medium
                else -> ProductRiskLevel.Low
            }

    override val effectiveRelation: EffectiveStateRelation
        get() =
            when {
                selectedMember != null -> EffectiveStateRelation.Active
                else -> EffectiveStateRelation.Candidate
            }

    override val changeState: ProductChangeState
        get() = ProductChangeState.Synced

    val memberCount: Int
        get() = members.size

    val proxyMembers: List<PolicyGroupMember>
        get() = members.filter { it.kind == PolicyGroupMemberKind.Proxy }

    val nestedGroupMembers: List<PolicyGroupMember>
        get() = members.filter { it.kind == PolicyGroupMemberKind.NestedGroup }
}

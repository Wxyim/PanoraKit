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
enum class RuleSetOrigin {
    Inline,
    Override,
    Provider,
    SubRule,
}

@Serializable
enum class MatcherType {
    Domain,
    DomainSuffix,
    DomainKeyword,
    DomainRegex,
    IpCidr,
    IpCidr6,
    SrcIpCidr,
    SrcPort,
    DstPort,
    ProcessName,
    ProcessPath,
    GeoIp,
    GeoSite,
    RuleSet,
    Network,
    And,
    Or,
    Not,
    InType,
    Match,
    Unknown,
}

@Serializable
data class RuleMatcher(
    val type: MatcherType,
    val payload: String,
    val target: String,
    val noResolve: Boolean = false,
    val rawText: String,
) {
    val isCompound: Boolean
        get() = type == MatcherType.And || type == MatcherType.Or || type == MatcherType.Not

    companion object {
        fun parse(raw: String): RuleMatcher {
            val parts = raw.split(",").map { it.trim() }
            val type =
                if (parts.isNotEmpty()) {
                    MatcherType.entries.firstOrNull {
                        it.name.equals(parts[0].replace("-", ""), ignoreCase = true)
                    } ?: MatcherType.Unknown
                } else {
                    MatcherType.Unknown
                }
            val payload = parts.getOrElse(1) { "" }
            val target = parts.getOrElse(2) { "" }
            val noResolve = parts.any { it.equals("no-resolve", ignoreCase = true) }
            return RuleMatcher(
                type = type,
                payload = payload,
                target = target,
                noResolve = noResolve,
                rawText = raw,
            )
        }
    }
}

@Serializable
data class RuleSet(
    val ruleSetId: String,
    val name: String,
    val origin: RuleSetOrigin,
    val matchers: List<RuleMatcher> = emptyList(),
    val profileId: String,
    val overrideSourceId: String? = null,
    val providerName: String? = null,
    val providerUrl: String? = null,
    val providerBehavior: String? = null,
    override val updatedAtMillis: Long,
) : ProductObjectContract {

    override val stableId: String
        get() = ruleSetId

    override val displayName: String
        get() = name

    override val lifecycleState: ProductLifecycleState
        get() =
            when {
                origin == RuleSetOrigin.Provider && providerUrl.isNullOrBlank() ->
                    ProductLifecycleState.Failed
                matchers.isEmpty() && origin == RuleSetOrigin.Inline -> ProductLifecycleState.Idle
                else -> ProductLifecycleState.Active
            }

    override val owner: ProductObjectOwner
        get() =
            ProductObjectOwner(
                id = profileId,
                label = overrideSourceId ?: providerName ?: profileId,
                kind =
                    when (origin) {
                        RuleSetOrigin.Provider -> "provider"
                        RuleSetOrigin.Override -> "override"
                        RuleSetOrigin.SubRule -> "subrule"
                        RuleSetOrigin.Inline -> "profile"
                    },
            )

    override val editable: Boolean
        get() = origin == RuleSetOrigin.Inline || origin == RuleSetOrigin.Override

    override val riskLevel: ProductRiskLevel
        get() {
            val hasRejectRules = matchers.any { it.target.equals("REJECT", ignoreCase = true) }
            val hasDirectRules = matchers.any { it.target.equals("DIRECT", ignoreCase = true) }
            return when {
                hasRejectRules && matchers.size > 100 -> ProductRiskLevel.High
                hasRejectRules -> ProductRiskLevel.Medium
                hasDirectRules -> ProductRiskLevel.Medium
                else -> ProductRiskLevel.Low
            }
        }

    override val effectiveRelation: EffectiveStateRelation
        get() =
            when {
                matchers.isNotEmpty() -> EffectiveStateRelation.Active
                else -> EffectiveStateRelation.Inactive
            }

    override val changeState: ProductChangeState
        get() = ProductChangeState.Synced

    val matcherCount: Int
        get() = matchers.size

    val compoundMatcherCount: Int
        get() = matchers.count { it.isCompound }

    val targetDistribution: Map<String, Int>
        get() = matchers.groupBy { it.target.uppercase() }.mapValues { it.value.size }
}

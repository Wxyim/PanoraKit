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
data class ExplanationStep(
    val stage: String,
    val label: String,
    val input: String? = null,
    val output: String? = null,
    val matched: Boolean = true,
    val detail: String? = null,
)

@Serializable
data class ExplanationChain(
    val chainId: String,
    val steps: List<ExplanationStep>,
    val conclusion: String,
    val isSuccess: Boolean,
) {
    val summary: String
        get() = steps.joinToString(" → ") { it.label }

    val failedStep: ExplanationStep?
        get() = steps.firstOrNull { !it.matched }

    companion object {
        fun decisionChain(
            chainId: String,
            app: String?,
            dns: String?,
            ruleSet: String?,
            rule: String?,
            policyGroup: String?,
            outbound: String?,
            result: String,
        ) =
            ExplanationChain(
                chainId = chainId,
                steps =
                    buildList {
                        if (app != null) add(ExplanationStep(stage = "App", label = app))
                        if (dns != null) add(ExplanationStep(stage = "DNS", label = dns))
                        if (ruleSet != null)
                            add(ExplanationStep(stage = "RuleSet", label = ruleSet))
                        if (rule != null) add(ExplanationStep(stage = "Rule", label = rule))
                        if (policyGroup != null)
                            add(ExplanationStep(stage = "PolicyGroup", label = policyGroup))
                        if (outbound != null)
                            add(ExplanationStep(stage = "Outbound", label = outbound))
                        add(ExplanationStep(stage = "Result", label = result))
                    },
                conclusion = result,
                isSuccess = true,
            )

        fun failureChain(
            chainId: String,
            failedStage: String,
            rootCause: String,
            impact: String,
            suggestedAction: String,
        ) =
            ExplanationChain(
                chainId = chainId,
                steps =
                    listOf(
                        ExplanationStep(
                            stage = "FailedStage",
                            label = failedStage,
                            matched = false,
                        ),
                        ExplanationStep(stage = "RootCause", label = rootCause),
                        ExplanationStep(stage = "Impact", label = impact),
                        ExplanationStep(stage = "SuggestedAction", label = suggestedAction),
                    ),
                conclusion = suggestedAction,
                isSuccess = false,
            )
    }
}

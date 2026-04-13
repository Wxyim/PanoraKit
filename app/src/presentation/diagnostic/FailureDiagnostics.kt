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

package com.github.yumelira.yumebox.presentation.diagnostic

import com.github.yumelira.yumebox.domain.model.StructuredLogEntry
import com.github.yumelira.yumebox.presentation.component.DiagnosticBannerState
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.component.TraceEntry
import com.github.yumelira.yumebox.presentation.component.toTraceEntry
import dev.oom_wg.purejoy.mlang.DiagnosticLang

data class FailureDiagnostics(
    val banner: DiagnosticBannerState,
    val traceEntries: List<TraceEntry> = emptyList(),
)

fun buildFailureDiagnostics(
    failures: List<StructuredLogEntry>,
    emptyHeadline: String,
): FailureDiagnostics {
    val latestFailure = failures.lastOrNull()
    return if (latestFailure == null) {
        FailureDiagnostics(
            banner =
                DiagnosticBannerState(
                    headline = emptyHeadline,
                    subtitle = DiagnosticLang.NoActiveIssues,
                    tone = SemanticTone.Success,
                )
        )
    } else {
        FailureDiagnostics(
            banner =
                DiagnosticBannerState(
                    headline = latestFailure.message,
                    subtitle = DiagnosticLang.RecentFailureItems.format(failures.size),
                    tone = SemanticTone.Danger,
                ),
            traceEntries = failures.asReversed().map(StructuredLogEntry::toTraceEntry),
        )
    }
}
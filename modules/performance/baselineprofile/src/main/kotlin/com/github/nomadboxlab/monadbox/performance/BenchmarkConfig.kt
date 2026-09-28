/*
 * This file is part of MonadBox.
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
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

package com.github.nomadboxlab.monadbox.performance

internal object BenchmarkConfig {
    const val TargetPackage = "com.github.nomadboxlab.monadbox"
    const val StartupTimeoutMs = 8_000L
    const val UiWaitTimeoutMs = 1_000L

    /** Budget for confirming that a journey reached its surface (tab switches need more slack). */
    const val UiVerifyTimeoutMs = 3_000L

    /**
     * The generator writes its per-journey report here (app-specific external files dir) so CI can
     * pull it and tell whether every recorded journey actually exercised its surface. Without this
     * a reworded UI silently reduces profile coverage while the run still looks green.
     */
    const val JourneyReportFileName = "journey-report.txt"
    const val JourneyReportTag = "MonadBoxBaselineProfile"
    const val JourneyReportBeginMarker = "MONADBOX_BASELINE_JOURNEY_REPORT_BEGIN"
    const val JourneyReportEndMarker = "MONADBOX_BASELINE_JOURNEY_REPORT_END"
}

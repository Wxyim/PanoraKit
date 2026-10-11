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
     * One cold start in front of a collection, settled for this long before the collection starts.
     *
     * `collect` force-stops and cold-starts the app for every collection, and on the CI guest that
     * moment - the 50 MB app being dex2oated and started again with the IME and the system UI
     * live - is where the emulator has died most often. Doing one cold start here moves the
     * first-run work (profile-guided compiling, asset and database creation) outside the fragile
     * window instead of leaving it inside it.
     */
    const val JourneyWarmUpSettleMs = 20_000L

    /**
     * The generator writes its per-journey report here (app-specific external files dir) so CI can
     * pull it and tell whether every recorded journey actually exercised its surface. Without this
     * a reworded UI silently reduces profile coverage while the run still looks green.
     */
    const val JourneyReportFileName = "journey-report.txt"

    /**
     * Clipboard directory under the app's external media dir that the connected-test engine pulls
     * to the host (AGP additional test output) - the same channel the benchmark's profile fragments
     * arrive through. Keep in sync with the adoption path in
     * `.github/workflows/baseline-profile.yml`.
     */
    const val AdditionalTestOutputDirName = "additional_test_output"

    const val JourneyReportTag = "MonadBoxBaselineProfile"
    const val JourneyReportBeginMarker = "MONADBOX_BASELINE_JOURNEY_REPORT_BEGIN"
    const val JourneyReportEndMarker = "MONADBOX_BASELINE_JOURNEY_REPORT_END"
}

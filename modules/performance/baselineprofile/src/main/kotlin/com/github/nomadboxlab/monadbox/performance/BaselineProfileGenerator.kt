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

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule val rule = BaselineProfileRule()

    /**
     * Startup profile: the cold-start path alone, collected with `includeInStartupProfile = true`.
     *
     * This has to be its own test method, not a second `collect` call next to the journeys one. AGP
     * records the additional test output of a test case once per case, so the profile file written
     * by the second collection in a method replaces the first one's and only the last fragment
     * reaches the build: the app side then merges either the baseline fragment or the startup
     * fragment (see `MergeBaselineProfileTask`), and with no startup rules to write it *deletes*
     * the committed `startup-prof.txt` instead of leaving it alone. One collection per test method
     * keeps both fragments, which is also what the upstream samples do.
     */
    @Test
    fun startup() {
        val report = JourneyReport(BenchmarkConfig.TargetPackage)

        try {
            // `includeInStartupProfile` does not add a journey to the startup profile, it labels
            // the *whole* collection as the startup profile, so collecting every journey under it
            // produced a startup-prof.txt that was a byte-for-byte copy of baseline-prof.txt and
            // claimed the entire journey set is startup-critical. The startup-typed fragment also
            // feeds `baseline-prof.txt` (the merge task accepts both file kinds for the baseline
            // output), so `journeys()` below does not repeat the cold start.
            rule.collect(
                packageName = BenchmarkConfig.TargetPackage,
                includeInStartupProfile = true,
            ) {
                report.run("startup", JourneyKind.Required) { startupJourney() }
            }
        } finally {
            // Published from a `finally`, so a collection that aborts on the harness' own checks
            // still leaves the per-journey evidence behind for CI.
            report.publish(InstrumentationRegistry.getInstrumentation().targetContext)
        }

        // Throws after the report is published, so a required journey cannot degrade silently.
        report.throwIfRequiredFailed()
    }

    /** Baseline profile: everything the app does once the cold start is over. */
    @Test
    fun journeys() {
        val report = JourneyReport(BenchmarkConfig.TargetPackage)

        try {
            rule.collect(packageName = BenchmarkConfig.TargetPackage) {
                // `collect` kills the package before every collection and the harness never starts
                // it again, so this block has to launch the app itself. Without that the journeys
                // below only see a dead app, and the collection itself then aborts with
                // "Process ... never flushed profiles in any process" because the app process it
                // expects a profile from never started.
                report.run("launch", JourneyKind.Required) { startupJourney() }
                // Runs first: a fresh install shows the first-run wizard until this has walked it,
                // and the Home-facing journeys below have nothing to exercise before that. See
                // `onboardingJourney`.
                report.run("onboarding", JourneyKind.BestEffort) { onboardingJourney() }
                // Promote to Required once a run has shown this leg really exercising its surface:
                // the mode panel only exists on Home, with a profile and a reached main shell.
                report.run("home_mode_switch", JourneyKind.BestEffort) { homeModeSwitchJourney() }
                report.run("configuration_import", JourneyKind.BestEffort) {
                    configurationImportJourney()
                }
                report.run("start_stop_proxy", JourneyKind.BestEffort) { startStopProxyJourney() }
                report.run("edit_save", JourneyKind.BestEffort) { editSaveJourney() }
                report.run("open_next_main_page", JourneyKind.BestEffort) { nextMainPageJourney() }
                report.run("bottom_navigation", JourneyKind.BestEffort) {
                    bottomNavigationJourney()
                }
                report.run("settings_scroll", JourneyKind.BestEffort) { settingsScrollJourney() }
            }
        } finally {
            // Published from a `finally`, so a collection that aborts on the harness' own checks
            // still leaves the per-journey evidence behind for CI.
            report.publish(InstrumentationRegistry.getInstrumentation().targetContext)
        }

        // Throws after the report is published, so a required journey cannot degrade silently.
        report.throwIfRequiredFailed()
    }
}

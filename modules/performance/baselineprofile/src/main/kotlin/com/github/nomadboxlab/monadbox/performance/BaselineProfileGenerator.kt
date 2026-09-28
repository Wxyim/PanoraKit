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

    @Test
    fun generate() {
        val report = JourneyReport(BenchmarkConfig.TargetPackage)

        rule.collect(packageName = BenchmarkConfig.TargetPackage, includeInStartupProfile = true) {
            report.run("startup", JourneyKind.Required) { startupJourney() }
            report.run("configuration_import", JourneyKind.BestEffort) {
                configurationImportJourney()
            }
            report.run("start_stop_proxy", JourneyKind.BestEffort) { startStopProxyJourney() }
            report.run("edit_save", JourneyKind.BestEffort) { editSaveJourney() }
            report.run("open_next_main_page", JourneyKind.BestEffort) {
                openNextMainPage()
                JourneyResult.exercised("swiped to the neighbouring main page")
            }
            report.run("bottom_navigation", JourneyKind.BestEffort) { bottomNavigationJourney() }
            report.run("settings_scroll", JourneyKind.BestEffort) { settingsScrollJourney() }
        }

        // Publish before asserting so a failed collection still leaves the per-journey evidence
        // behind for CI, and so a required journey can never degrade the profile silently.
        report.publish(InstrumentationRegistry.getInstrumentation().targetContext)
        report.throwIfRequiredFailed()
    }
}

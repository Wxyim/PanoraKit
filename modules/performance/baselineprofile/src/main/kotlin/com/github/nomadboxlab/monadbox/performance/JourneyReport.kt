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

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

/**
 * The result of one benchmark journey: whether it really reached the surface it claims to cover.
 */
internal class JourneyResult private constructor(val exercised: Boolean, val detail: String) {
    companion object {
        fun exercised(detail: String) = JourneyResult(exercised = true, detail = detail)

        fun skipped(detail: String) = JourneyResult(exercised = false, detail = detail)

        fun fromMatched(matched: String?, surface: String, attempts: List<String>) =
            if (matched == null) {
                skipped("no match for $surface among $attempts")
            } else {
                exercised("$surface matched '$matched'")
            }
    }
}

internal enum class JourneyKind(val label: String) {
    /** Fails the run when the journey does not exercise its surface. */
    Required("required"),

    /** Data-dependent (needs a seeded profile/config); reported, never fatal on its own. */
    BestEffort("best-effort"),
}

/**
 * Collects per-journey outcomes for the baseline profile generator.
 *
 * Every journey used to fail silently (`if (!attempted) return`), so a reworded UI label reduced
 * profile coverage without any signal. This report makes each leg explicit: required legs fail the
 * run, best-effort legs are listed in the report and in the CI job summary so a shrinking journey
 * count is visible instead of silent.
 */
internal class JourneyReport(private val targetPackage: String) {
    private enum class Outcome(val label: String) {
        Exercised("exercised"),
        Skipped("skipped"),
        Failed("failed"),
    }

    private class Leg(
        val name: String,
        val kind: JourneyKind,
        val outcome: Outcome,
        val detail: String,
    )

    private val legs = mutableListOf<Leg>()

    fun run(name: String, kind: JourneyKind, journey: () -> JourneyResult) {
        val result =
            try {
                journey()
            } catch (error: Throwable) {
                // Journey failures are part of the report: throwing here would abort the collection
                // run and hide why coverage shrank.
                record(
                    Leg(
                        name = name,
                        kind = kind,
                        outcome = Outcome.Failed,
                        detail = "${error.javaClass.simpleName}: ${error.message}",
                    )
                )
                return
            }

        record(
            Leg(
                name = name,
                kind = kind,
                outcome = if (result.exercised) Outcome.Exercised else Outcome.Skipped,
                detail = result.detail,
            )
        )
    }

    private fun record(leg: Leg) {
        legs += leg
        Log.i(
            BenchmarkConfig.JourneyReportTag,
            "journey ${leg.name} ${leg.outcome.label}: ${leg.detail}",
        )
    }

    fun render(): String = buildString {
        appendLine("${BenchmarkConfig.JourneyReportBeginMarker}")
        appendLine("target=$targetPackage")
        legs.forEach { leg ->
            appendLine(
                "leg=${leg.name} kind=${leg.kind.label} result=${leg.outcome.label} detail=${leg.detail}"
            )
        }
        val required = legs.filter { it.kind == JourneyKind.Required }
        val bestEffort = legs.filter { it.kind == JourneyKind.BestEffort }
        appendLine(
            "summary" +
                " required_total=${required.size}" +
                " required_failed=${required.count { it.outcome == Outcome.Failed }}" +
                " best_effort_total=${bestEffort.size}" +
                " best_effort_exercised=${bestEffort.count { it.outcome == Outcome.Exercised }}" +
                " best_effort_skipped=${bestEffort.count { it.outcome != Outcome.Exercised }}"
        )
        appendLine("${BenchmarkConfig.JourneyReportEndMarker}")
    }

    /**
     * Writes the report to the app-specific external files dir and mirrors it to logcat.
     *
     * The generator runs one collection per test method (see `BaselineProfileGenerator`), so the
     * file accumulates one block per method: CI pulls a single file and has to see both leg sets.
     * The first publish of an instrumentation run truncates, which also drops a file left behind by
     * an earlier run on the same device instead of mixing the two runs' legs together.
     */
    fun publish(context: Context): File? {
        val rendered = render()
        Log.i(BenchmarkConfig.JourneyReportTag, rendered)
        val directory = context.getExternalFilesDir(null) ?: context.filesDir
        return runCatching {
                File(directory, BenchmarkConfig.JourneyReportFileName).apply {
                    if (publishedInThisRun) appendText(rendered) else writeText(rendered)
                }
            }
            .onSuccess { file ->
                publishedInThisRun = true
                Log.i(BenchmarkConfig.JourneyReportTag, "journey report file: ${file.absolutePath}")
                stageReportForCi(file)
            }
            .onFailure { error ->
                Log.w(BenchmarkConfig.JourneyReportTag, "journey report not written: $error")
            }
            .getOrNull()
    }

    /**
     * Stage a copy of [file] at [BenchmarkConfig.JourneyReportStagedPath] for the CI workflow.
     *
     * The connected-test cleanup uninstalls the app and the test package when the run ends - the
     * same cleanup whose `Failed to uninstall package ...` lines appear when a device dies - and an
     * uninstall wipes the external files dir of the app: by the time the post-Gradle adb work of
     * the workflow runs, the original under /sdcard/Android/data is already gone, which is why the
     * runs kept recovering the report from logcat. `/sdcard/Download` survives the cleanup, and the
     * copy is made from inside the run, before it. Best-effort: the workflow still has the logcat
     * fallback, so a failed copy only has to be visible in the logcat trail.
     */
    private fun stageReportForCi(file: File) {
        val command =
            "mkdir -p /sdcard/Download && cp -f ${file.absolutePath} ${BenchmarkConfig.JourneyReportStagedPath}"
        val output =
            runCatching { shellOutput(command) }.getOrElse { "executeShellCommand failed: $it" }
        Log.i(
            BenchmarkConfig.JourneyReportTag,
            if (output.isBlank()) {
                "journey report staged at ${BenchmarkConfig.JourneyReportStagedPath}"
            } else {
                "journey report staging output: ${output.trim()}"
            },
        )
    }

    /** Runs [command] through the instrumentation shell and returns its combined output. */
    private fun shellOutput(command: String): String =
        ParcelFileDescriptor.AutoCloseInputStream(
                InstrumentationRegistry.getInstrumentation()
                    .uiAutomation
                    .executeShellCommand(command)
            )
            .use { it.readBytes().decodeToString() }

    /** Throws after the report is published, so a required journey cannot degrade silently. */
    fun throwIfRequiredFailed() {
        val failed =
            legs.filter { it.kind == JourneyKind.Required && it.outcome != Outcome.Exercised }
        if (failed.isEmpty()) return

        throw AssertionError(
            "Required baseline profile journey(s) did not run: " +
                failed.joinToString(", ") { "${it.name} (${it.detail})" }
        )
    }

    private companion object {
        /**
         * Process-scoped: every test method shares the file, but only the first one truncates it.
         */
        var publishedInThisRun = false
    }
}

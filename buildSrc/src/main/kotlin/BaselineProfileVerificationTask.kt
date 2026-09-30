import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * Guards the baseline profile that ships inside the release artifact.
 *
 * A baseline profile silently stops helping as soon as its recorded journeys no longer match the
 * real startup/navigation paths, and nothing in a normal build notices. This task makes that
 * visible: it parses every profile AGP consumes (see `baselineProfileSourceDirPaths` in
 * `app/build.gradle.kts`), requires that it still covers the app package and the launcher
 * activity's startup path, checks that `startup-prof.txt` is present and smaller than
 * `baseline-prof.txt` (AGP feeds the former to dex startup optimization and the latter to the
 * shipped profile), and compares entry/class coverage with the profile committed to the repository.
 *
 * Wiring: `verifyBaselineProfile` in `app/build.gradle.kts`. When no profile has been generated yet
 * the task reports `SKIPPED` unless `-PbaselineProfile.require=true` asks for a hard requirement.
 */
abstract class BaselineProfileVerificationTask : DefaultTask() {
    private companion object {
        const val BASELINE_PROFILE_FILE_NAME = "baseline-prof.txt"
        const val STARTUP_PROFILE_FILE_NAME = "startup-prof.txt"
    }

    /** Directories searched for profile fragments; used for reporting only. */
    @get:Internal abstract val profileDirs: ListProperty<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val profileFiles: ConfigurableFileCollection

    @get:Input abstract val appPackage: Property<String>

    @get:Input abstract val launcherClass: Property<String>

    /** Committed profile (`git show HEAD:<path>`), used for coverage-regression comparison. */
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val baselineProfile: RegularFileProperty

    /**
     * Fails the build when no profile exists at all. Off for plain `check`, on for CI generation.
     */
    @get:Input abstract val requireProfile: Property<Boolean>

    @get:Input abstract val minEntryRatio: Property<Double>

    @get:Input abstract val minClassRatio: Property<Double>

    @get:OutputFile abstract val reportFile: RegularFileProperty

    /** Markdown summary consumed by `$GITHUB_STEP_SUMMARY` in the baseline profile workflow. */
    @get:OutputFile abstract val summaryFile: RegularFileProperty

    init {
        group = "verification"
        description =
            "Verifies that the shipped baseline profile still covers the app startup path and did not regress."
    }

    @TaskAction
    fun verifyBaselineProfile() {
        val candidates =
            profileFiles.files.filter { it.isFile && it.extension == "txt" }.sortedBy(File::getName)
        val sources = candidates.map { BaselineProfileSource(it.name, it.readText()) }

        if (sources.isEmpty()) {
            val note = "No baseline profile found under any of: ${searchedDirs()}."
            if (requireProfile.get()) {
                writeOutputs(
                    status = "FAIL",
                    summaryLines = listOf(note),
                    issues =
                        listOf("$note Run `:app:generateBaselineProfile` and commit the result."),
                    baseline = null,
                    delta = null,
                )
                throw GradleException(
                    "$note Run `:app:generateBaselineProfile` and commit the result. See ${reportFile.get().asFile.invariantSeparatorsPath}"
                )
            }
            writeOutputs(
                status = "SKIPPED",
                summaryLines = listOf(note, "Generate one with `:app:generateBaselineProfile`."),
                issues = emptyList(),
                baseline = null,
                delta = null,
            )
            logger.lifecycle("baseline profile verification skipped: $note")
            return
        }

        val summary =
            BaselineProfileAnalyzer.analyze(
                sources = sources,
                appPackage = appPackage.get(),
                launcherClass = launcherClass.get(),
            )

        val baseline =
            baselineProfile.orNull
                ?.asFile
                ?.takeIf { it.isFile }
                ?.let { file ->
                    BaselineProfileAnalyzer.analyze(
                        sources = listOf(BaselineProfileSource(file.name, file.readText())),
                        appPackage = appPackage.get(),
                        launcherClass = launcherClass.get(),
                    )
                }

        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        if (summary.entries == 0) {
            issues +=
                "Baseline profile is empty or unparsable (unparsed lines: ${summary.unparsedEntries})."
        }
        if (summary.appEntries == 0) {
            issues +=
                "Baseline profile contains no entry for the app package ${appPackage.get()}; the recorded journeys no longer match the app's code."
        }
        if (summary.appStartupEntries == 0) {
            issues +=
                "Baseline profile contains no startup entry for the app package ${appPackage.get()}; the startup journey did not contribute to the startup profile."
        }
        if (summary.launcherEntries == 0) {
            issues +=
                "Baseline profile does not mention the launcher activity ${launcherClass.get()}; the startup journey never reached the main screen."
        }
        // AGP embeds `baseline-prof.txt` in the APK and feeds `startup-prof.txt` to dex startup
        // optimization, so the startup file is never merged into the shipped profile: a missing one
        // disables that optimization without failing anything, and one that is not smaller than the
        // baseline is a copy of the whole profile that claims every journey is startup-critical.
        val baselineSource = sources.firstOrNull { it.name == BASELINE_PROFILE_FILE_NAME }
        val startupSource = sources.firstOrNull { it.name == STARTUP_PROFILE_FILE_NAME }
        if (baselineSource != null && startupSource == null) {
            issues +=
                "No `$STARTUP_PROFILE_FILE_NAME` next to `$BASELINE_PROFILE_FILE_NAME`; dex startup optimization reads only that file and would silently do nothing."
        }
        if (baselineSource != null && startupSource != null) {
            val startupEntries = entriesOf(startupSource)
            val baselineEntries = entriesOf(baselineSource)
            if (startupEntries >= baselineEntries) {
                val note =
                    "`$STARTUP_PROFILE_FILE_NAME` has $startupEntries entries and `$BASELINE_PROFILE_FILE_NAME` has $baselineEntries, so the startup profile is not a subset of the baseline profile; the generator is labelling the whole journey set as startup-critical."
                // A heuristic: it fails the build only where a profile is a hard requirement
                // (the baseline profile workflow); a plain `check` reports it as a warning.
                if (requireProfile.get()) {
                    issues += note
                } else {
                    warnings += note
                }
            }
        }
        if (baseline != null) {
            issues +=
                BaselineProfileAnalyzer.compareToBaseline(
                    current = summary,
                    baseline = baseline,
                    minEntryRatio = minEntryRatio.get(),
                    minClassRatio = minClassRatio.get(),
                )
        }

        val delta = baseline?.let { BaselineProfileAnalyzer.renderDelta(summary, it) }
        writeOutputs(
            status =
                if (issues.isNotEmpty()) "FAIL" else if (warnings.isEmpty()) "PASS" else "WARN",
            summaryLines = summary.renderLines() + listOfNotNull(delta),
            issues = issues,
            warnings = warnings,
            baseline = baseline,
            delta = delta,
        )

        logger.lifecycle(
            "baseline profile: ${summary.entries} entries / ${summary.classes} classes across ${sources.size} file(s)"
        )
        if (warnings.isNotEmpty()) {
            logger.warn("Baseline profile warnings: ${warnings.joinToString(" ")}")
        }
        if (issues.isNotEmpty()) {
            throw GradleException(
                "Baseline profile verification failed with ${issues.size} issue(s). See ${reportFile.get().asFile.invariantSeparatorsPath}"
            )
        }
    }

    private fun entriesOf(source: BaselineProfileSource): Int =
        BaselineProfileAnalyzer.analyze(
                sources = listOf(source),
                appPackage = appPackage.get(),
                launcherClass = launcherClass.get(),
            )
            .entries

    private fun writeOutputs(
        status: String,
        summaryLines: List<String>,
        issues: List<String>,
        warnings: List<String> = emptyList(),
        baseline: BaselineProfileSummary?,
        delta: String?,
    ) {
        val output = reportFile.get().asFile
        output.parentFile.mkdirs()
        output.writeText(
            buildString {
                appendLine("Baseline Profile Verification Report")
                appendLine()
                appendLine("Profile directories searched: ${searchedDirs()}")
                appendLine("App package: ${appPackage.get()}")
                appendLine("Launcher class: ${launcherClass.get()}")
                appendLine()
                summaryLines.forEach { appendLine(it) }
                if (baseline != null) {
                    appendLine()
                    appendLine("Committed profile baseline:")
                    appendLine("Entries: ${baseline.entries}")
                    appendLine("Distinct classes: ${baseline.classes}")
                    appendLine(
                        "Minimum ratios: entries ${minEntryRatio.get()}, classes ${minClassRatio.get()}"
                    )
                }
                appendLine()
                appendLine("Result: $status")
                if (warnings.isNotEmpty()) {
                    appendLine()
                    appendLine("Warnings:")
                    warnings.forEach { warning -> appendLine("- $warning") }
                }
                if (issues.isNotEmpty()) {
                    appendLine()
                    issues.forEach { issue -> appendLine("- $issue") }
                }
            }
        )

        val summaryOutput = summaryFile.get().asFile
        summaryOutput.parentFile.mkdirs()
        summaryOutput.writeText(
            buildString {
                appendLine("### Baseline profile: $status")
                appendLine()
                summaryLines.forEach { appendLine("- $it") }
                if (delta != null) appendLine("- $delta")
                if (warnings.isNotEmpty()) {
                    appendLine()
                    appendLine("Warnings:")
                    warnings.forEach { warning -> appendLine("- $warning") }
                }
                if (issues.isNotEmpty()) {
                    appendLine()
                    appendLine("Issues:")
                    issues.forEach { issue -> appendLine("- $issue") }
                }
            }
        )
    }

    private fun searchedDirs(): String = profileDirs.get().joinToString(", ")
}

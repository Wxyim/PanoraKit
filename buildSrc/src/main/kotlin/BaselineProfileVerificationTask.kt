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
 * `app/build.gradle.kts`), requires a valid `baseline-prof.txt` that covers the app package and
 * launcher activity, checks that `startup-prof.txt` contains startup coverage and is a strict rule
 * subset of the baseline profile (AGP feeds the former to dex startup optimization and the latter
 * to the shipped profile), and compares baseline entry/class coverage with the committed profile.
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

        val baselineSources = sources.filter { it.name != STARTUP_PROFILE_FILE_NAME }
        val startupSources = sources.filter { it.name == STARTUP_PROFILE_FILE_NAME }
        val summary =
            BaselineProfileAnalyzer.analyze(
                sources = baselineSources,
                appPackage = appPackage.get(),
                launcherClass = launcherClass.get(),
            )
        val startupSummary =
            startupSources
                .takeIf { it.isNotEmpty() }
                ?.let { startupFiles ->
                    BaselineProfileAnalyzer.analyze(
                        sources = startupFiles,
                        appPackage = appPackage.get(),
                        launcherClass = launcherClass.get(),
                    )
                }

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
        val baselineSource = sources.firstOrNull { it.name == BASELINE_PROFILE_FILE_NAME }
        if (baselineSource == null) {
            issues +=
                "No `$BASELINE_PROFILE_FILE_NAME` found; startup-only or unrelated profile files cannot replace the profile shipped in the APK."
        }
        if (summary.entries == 0) {
            issues +=
                "Baseline profile is empty or unparsable (unparsed lines: ${summary.unparsedEntries})."
        }
        if (summary.unparsedEntries > 0) {
            issues +=
                "Baseline profile contains ${summary.unparsedEntries} unrecognized line(s); expected ART class or method rules."
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
        // optimization. The startup collection is also folded into the generated baseline profile,
        // so validate each artifact separately: a missing startup file disables dex startup
        // optimization, while counting both files together masks a missing baseline and inflates
        // the committed-profile comparison.
        if (baselineSource != null && startupSources.isEmpty()) {
            issues +=
                "No `$STARTUP_PROFILE_FILE_NAME` next to `$BASELINE_PROFILE_FILE_NAME`; dex startup optimization reads only that file and would silently do nothing."
        }
        if (startupSummary != null) {
            if (startupSummary.entries == 0) {
                issues +=
                    "`$STARTUP_PROFILE_FILE_NAME` is empty or contains no valid ART profile rules."
            }
            if (startupSummary.unparsedEntries > 0) {
                issues +=
                    "`$STARTUP_PROFILE_FILE_NAME` contains ${startupSummary.unparsedEntries} unrecognized line(s); expected ART class or method rules."
            }
            if (startupSummary.appStartupEntries == 0) {
                issues +=
                    "`$STARTUP_PROFILE_FILE_NAME` contains no startup rule for app package ${appPackage.get()}; dex startup optimization would miss app code."
            }
            if (startupSummary.launcherStartupEntries == 0) {
                issues +=
                    "`$STARTUP_PROFILE_FILE_NAME` contains no startup rule for launcher activity ${launcherClass.get()}."
            }

            val startupOnlyRules = startupSummary.ruleIdentities - summary.ruleIdentities
            if (startupOnlyRules.isNotEmpty()) {
                val note =
                    "`$STARTUP_PROFILE_FILE_NAME` has ${startupOnlyRules.size} class/method rule(s) absent from `$BASELINE_PROFILE_FILE_NAME`; the startup fragment is not fully represented in the shipped baseline profile."
                // The generated startup fragment is expected to merge into the baseline profile.
                // Keep local checks usable while making the required generation workflow strict.
                if (requireProfile.get()) issues += note else warnings += note
            } else if (
                baselineSource != null &&
                    startupSummary.ruleIdentities.size >= summary.ruleIdentities.size
            ) {
                val note =
                    "`$STARTUP_PROFILE_FILE_NAME` covers all ${summary.ruleIdentities.size} baseline rules, so the generator is labelling the whole profile as startup-critical."
                // Fail only where a profile is a hard requirement; a plain `check` reports the
                // unusually broad startup profile as a warning.
                if (requireProfile.get()) issues += note else warnings += note
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
            // A class-count dip above the floor passes the gate, so nothing else would surface the
            // trend; the warning keeps it visible in the report and the step summary.
            BaselineProfileAnalyzer.renderClassTrendWarning(current = summary, baseline = baseline)
                ?.let { warnings += it }
        }

        val summaryLines =
            summary.renderLines() +
                startupSummary?.renderLines()?.map { "Startup profile: $it" }.orEmpty()
        val delta = baseline?.let { BaselineProfileAnalyzer.renderDelta(summary, it) }
        writeOutputs(
            status =
                if (issues.isNotEmpty()) "FAIL" else if (warnings.isEmpty()) "PASS" else "WARN",
            summaryLines = summaryLines + listOfNotNull(delta),
            issues = issues,
            warnings = warnings,
            baseline = baseline,
            delta = delta,
        )

        logger.lifecycle(
            "baseline profile: ${summary.entries} entries / ${summary.classes} classes across ${baselineSources.size} file(s); startup profile: ${startupSummary?.entries ?: 0} entries"
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

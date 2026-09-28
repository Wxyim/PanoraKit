import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
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
 * visible: it parses the profile in `app/src/main/baselineProfiles`, requires that it still covers
 * the app package and the launcher activity's startup path, and compares entry/class coverage with
 * the profile committed to the repository.
 *
 * Wiring: `verifyBaselineProfile` in `app/build.gradle.kts`. When no profile has been generated yet
 * the task reports `SKIPPED` unless `-PbaselineProfile.require=true` asks for a hard requirement.
 */
abstract class BaselineProfileVerificationTask : DefaultTask() {
    /** Directory that holds the generated profile fragments; used for reporting only. */
    @get:Internal abstract val profileDir: DirectoryProperty

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
            val note =
                "No baseline profile found under ${profileDir.asFile.get().invariantSeparatorsPath}."
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
            status = if (issues.isEmpty()) "PASS" else "FAIL",
            summaryLines = summary.renderLines() + listOfNotNull(delta),
            issues = issues,
            baseline = baseline,
            delta = delta,
        )

        logger.lifecycle(
            "baseline profile: ${summary.entries} entries / ${summary.classes} classes across ${sources.size} file(s)"
        )
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
        baseline: BaselineProfileSummary?,
        delta: String?,
    ) {
        val output = reportFile.get().asFile
        output.parentFile.mkdirs()
        output.writeText(
            buildString {
                appendLine("Baseline Profile Verification Report")
                appendLine()
                appendLine("Profile directory: ${profileDir.asFile.get().invariantSeparatorsPath}")
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
                if (issues.isNotEmpty()) {
                    appendLine()
                    appendLine("Issues:")
                    issues.forEach { issue -> appendLine("- $issue") }
                }
            }
        )
    }
}

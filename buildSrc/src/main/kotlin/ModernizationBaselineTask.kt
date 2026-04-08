import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class ModernizationBaselineTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceRoots: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val moduleBuildFiles: ConfigurableFileCollection

    @get:Input
    abstract val requiredDependencyTokens: MapProperty<String, String>

    @get:Internal abstract val repoRootDir: DirectoryProperty

    @get:OutputFile abstract val reportFile: RegularFileProperty

    init {
        group = "verification"
        description =
            "Validates modernization baseline guards for Compose lifecycle collection and startup architecture."
        doNotTrackState("Scans workspace sources directly; avoid lock-file hashing issues under repo root on Windows.")
    }

    @TaskAction
    fun validateBaseline() {
        val root = repoRootDir.asFile.get()
        val issues = mutableListOf<String>()

        val composeFiles =
            sourceRoots.files
                .asSequence()
                .flatMap { rootFile ->
                    when {
                        rootFile.isDirectory ->
                            rootFile.walkTopDown().filter { it.isFile && it.extension == "kt" }.asSequence()
                        rootFile.isFile && rootFile.extension == "kt" -> sequenceOf(rootFile)
                        else -> emptySequence()
                    }
                }
                .toList()

        val modernizationAnalysis =
            ModernizationSourceAnalyzer.analyzeComposeSources(
                composeFiles.map { file ->
                    KotlinSourceFile(
                        relativePath = file.relativeTo(root).invariantSeparatorsPath,
                        content = file.readText(),
                    )
                }
            )

        if (modernizationAnalysis.collectAsStateViolations.isNotEmpty()) {
            issues +=
                "Found non-lifecycle collectAsState() usage:\n" +
                    modernizationAnalysis.collectAsStateViolations.joinToString(separator = "\n") {
                        "- ${it.relativePath}:${it.lineNumber}"
                    }
        }

        if (modernizationAnalysis.launchedCollectViolations.isNotEmpty()) {
            issues +=
                "Found LaunchedEffect + collect patterns that should use lifecycle-aware collectors:\n" +
                    modernizationAnalysis.launchedCollectViolations.joinToString(separator = "\n") {
                        "- ${it.relativePath}:${it.lineNumber}"
                    }
        }

        val coordinatorFile = root.resolve("app/src/startup/AppStartupCoordinator.kt")
        if (!coordinatorFile.isFile) {
            issues +=
                "Missing startup coordinator: app/src/startup/AppStartupCoordinator.kt"
        }

        val appFile = root.resolve("app/src/App.kt")
        if (!appFile.isFile) {
            issues += "Missing application file: app/src/App.kt"
        } else {
            val appStartupAnalysis =
                ModernizationSourceAnalyzer.analyzeAppStartup(
                    KotlinSourceFile(
                        relativePath = appFile.relativeTo(root).invariantSeparatorsPath,
                        content = appFile.readText(),
                    )
                )
            if (!appStartupAnalysis.delegatesDeferredStartupToCoordinator) {
                issues +=
                    "App.kt must delegate deferred startup to startupCoordinator.ensureDeferredStartupInitialized()."
            }

            appStartupAnalysis.forbiddenCalls.forEach { issue -> issues += issue.message }
        }

        val moduleBuildFilesByPath =
            moduleBuildFiles.files.associateBy { file -> file.relativeTo(root).invariantSeparatorsPath }
        requiredDependencyTokens.get().toSortedMap().forEach { (filePath, token) ->
            val buildFile = moduleBuildFilesByPath[filePath]
            if (buildFile == null || !buildFile.isFile) {
                issues += "Missing module build file input: $filePath"
                return@forEach
            }

            if (!buildFile.readText().contains(token)) {
                issues += "$filePath must include $token"
            }
        }

        writeReport(root, issues)

        if (issues.isNotEmpty()) {
            throw GradleException(
                "Modernization baseline validation failed with ${issues.size} issue(s). See ${reportFile.asFile.get().invariantSeparatorsPath}"
            )
        }
    }

    private fun writeReport(root: File, issues: List<String>) {
        val output = reportFile.asFile.get()
        output.parentFile.mkdirs()
        output.writeText(
            buildString {
                appendLine("Modernization Baseline Report")
                appendLine()
                appendLine("Scope: app/src + modules/feature")
                appendLine("Checks:")
                appendLine("- collectAsState() forbidden in Compose app/feature sources")
                appendLine("- LaunchedEffect(Unit) + collect anti-pattern forbidden")
                appendLine("- Thin Application delegation to AppStartupCoordinator")
                appendLine("- lifecycle-runtime-compose dependency in key feature modules")
                appendLine()
                if (issues.isEmpty()) {
                    appendLine("Result: PASS")
                } else {
                    appendLine("Result: FAIL")
                    appendLine()
                    issues.forEach { issue -> appendLine("- $issue") }
                }
                appendLine()
                appendLine("Repo root: ${root.invariantSeparatorsPath}")
            }
        )
    }
}
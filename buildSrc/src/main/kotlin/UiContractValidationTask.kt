import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class UiContractValidationTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val registryFile: RegularFileProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceRoots: ConfigurableFileCollection

    @get:Internal abstract val repoRootDir: DirectoryProperty

    @get:OutputFile abstract val reportFile: RegularFileProperty

    init {
        group = "verification"
        description =
            "Validates page reachability, capability registry coverage, and UI-to-implementation correspondence."
    }

    @TaskAction
    fun validateContracts() {
        val repoRoot = repoRootDir.asFile.get()
        val registryFileHandle = registryFile.asFile.get()
        val registryEntries =
            UiContractAnalyzer.parseRegistry(
                lines = registryFileHandle.readLines(),
                fileLabel = registryFileHandle.invariantSeparatorsPath,
            )
        val sourceFiles =
            sourceRoots.files
                .flatMap { root ->
                    if (root.isDirectory) {
                        root.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
                    } else {
                        listOf(root).filter { it.isFile && it.extension == "kt" }
                    }
                }
                .sortedBy { it.invariantSeparatorsPath }
        val sourceAnalysis =
            UiContractAnalyzer.analyzeSources(
                sourceFiles.map { file ->
                    KotlinSourceFile(
                        relativePath = file.relativeTo(repoRoot).invariantSeparatorsPath,
                        content = file.readText(),
                    )
                }
            )
        val issues = mutableListOf<String>()

        val duplicateDestinations =
            registryEntries
                .groupBy(UiCapabilityEntry::destination)
                .filterValues { it.size > 1 }
                .keys
        if (duplicateDestinations.isNotEmpty()) {
            issues +=
                "Duplicate registry destinations: ${duplicateDestinations.sorted().joinToString()}"
        }

        val duplicateCapabilityIds =
            registryEntries
                .groupBy(UiCapabilityEntry::capabilityId)
                .filterValues { it.size > 1 }
                .keys
        if (duplicateCapabilityIds.isNotEmpty()) {
            issues += "Duplicate capability ids: ${duplicateCapabilityIds.sorted().joinToString()}"
        }

        registryEntries.forEach { entry ->
            if (entry.uiType !in SUPPORTED_UI_TYPES) {
                issues += "${entry.destination}: unsupported uiType '${entry.uiType}'"
            }
            if (entry.entryMode !in SUPPORTED_ENTRY_MODES) {
                issues += "${entry.destination}: unsupported entryMode '${entry.entryMode}'"
            }
            if (
                !entry.settingsSection.isNullOrBlank() &&
                    entry.settingsSection !in SUPPORTED_SETTINGS_SECTIONS
            ) {
                issues +=
                    "${entry.destination}: unsupported settingsSection '${entry.settingsSection}'"
            }

            val declared = sourceAnalysis.declaredDestinations[entry.destination]
            if (declared == null) {
                issues += "${entry.destination}: not declared as a @Destination route"
            } else {
                if (declared.screenFile != entry.screenFile) {
                    issues +=
                        "${entry.destination}: registry screen '${entry.screenFile}' does not match declaration '${declared.screenFile}'"
                }
                if (entry.entryMode == "start" && !declared.isStartDestination) {
                    issues +=
                        "${entry.destination}: entryMode=start but destination is not marked start=true"
                }
                if (entry.entryMode != "start" && declared.isStartDestination) {
                    issues += "${entry.destination}: start destination must use entryMode=start"
                }
            }

            val screenFile = repoRoot.resolve(entry.screenFile)
            if (!screenFile.isFile) {
                issues += "${entry.destination}: screen file missing '${entry.screenFile}'"
            }

            if (entry.implementationFiles.isEmpty()) {
                issues += "${entry.destination}: implementationFiles must not be empty"
            }

            entry.implementationFiles.forEach { implPath ->
                if (!repoRoot.resolve(implPath).isFile) {
                    issues += "${entry.destination}: implementation file missing '${implPath}'"
                }
            }

            if (entry.entryMode == "navigation") {
                val refName = "${entry.destination}Destination"
                if ((sourceAnalysis.destinationReferences[refName] ?: 0) == 0) {
                    issues += "${entry.destination}: no navigation reference found for ${refName}"
                }
            }

            if (!entry.settingsSection.isNullOrBlank()) {
                if (entry.uiType != "top-level") {
                    issues += "${entry.destination}: settingsSection requires uiType=top-level"
                }
                if (entry.entryMode != "navigation") {
                    issues += "${entry.destination}: settingsSection requires entryMode=navigation"
                }
            }
        }

        val registeredSettingsDestinations =
            registryEntries
                .asSequence()
                .filter { !it.settingsSection.isNullOrBlank() }
                .map(UiCapabilityEntry::destination)
                .toSet()

        val missingFromSettings =
            registeredSettingsDestinations - sourceAnalysis.settingsEntryDestinations
        if (missingFromSettings.isNotEmpty()) {
            issues +=
                "Registry top-level settings pages missing from SettingPager: ${missingFromSettings.sorted().joinToString()}"
        }

        val missingFromRegistry =
            sourceAnalysis.settingsEntryDestinations - registeredSettingsDestinations
        if (missingFromRegistry.isNotEmpty()) {
            issues +=
                "SettingPager destinations missing from registry settingsSection mapping: ${missingFromRegistry.sorted().joinToString()}"
        }

        val orphanedDestinations =
            sourceAnalysis.rootGraphDestinations -
                registryEntries.map(UiCapabilityEntry::destination).toSet()
        if (orphanedDestinations.isNotEmpty()) {
            issues +=
                "Unregistered RootGraph destinations under app/src or modules/feature: ${orphanedDestinations.sorted().joinToString()}"
        }

        val registeredDestinations = registryEntries.map(UiCapabilityEntry::destination).toSet()
        val unregisteredNestedDestinations =
            sourceAnalysis.nestedGraphDestinations - registeredDestinations
        if (unregisteredNestedDestinations.isNotEmpty()) {
            issues +=
                "Unregistered nested graph destinations under app/src or modules/feature: ${unregisteredNestedDestinations.sorted().joinToString()}"
        }

        writeReport(
            reportFile = reportFile.asFile.get(),
            registryEntries = registryEntries,
            declaredDestinations = sourceAnalysis.declaredDestinations,
            rootGraphDestinations = sourceAnalysis.rootGraphDestinations,
            nestedGraphDestinations = sourceAnalysis.nestedGraphDestinations,
            destinationRefs = sourceAnalysis.destinationReferences,
            issues = issues,
        )

        if (issues.isNotEmpty()) {
            throw GradleException(
                "UI contract validation failed with ${issues.size} issue(s). See ${reportFile.asFile.get().invariantSeparatorsPath}"
            )
        }
    }

    companion object {
        private val SUPPORTED_UI_TYPES = setOf("top-level", "detail", "editor", "system")
        private val SUPPORTED_ENTRY_MODES = setOf("start", "navigation", "implicit")
        private val SUPPORTED_SETTINGS_SECTIONS = setOf("ui-settings", "more")

        private fun writeReport(
            reportFile: File,
            registryEntries: List<UiCapabilityEntry>,
            declaredDestinations: Map<String, DeclaredDestination>,
            rootGraphDestinations: Set<String>,
            nestedGraphDestinations: Set<String>,
            destinationRefs: Map<String, Int>,
            issues: List<String>,
        ) {
            reportFile.parentFile.mkdirs()
            reportFile.writeText(
                buildString {
                    appendLine("UI Contract Validation Report")
                    appendLine()
                    appendLine("Registry entries: ${registryEntries.size}")
                    appendLine("Declared @Destination routes: ${declaredDestinations.size}")
                    appendLine("Declared RootGraph destinations: ${rootGraphDestinations.size}")
                    appendLine(
                        "Declared nested-graph destinations: ${nestedGraphDestinations.size}"
                    )
                    appendLine("Navigation references found: ${destinationRefs.size}")
                    appendLine()
                    appendLine("Reachability Checklist")
                    registryEntries.sortedBy(UiCapabilityEntry::destination).forEach { entry ->
                        val declared = declaredDestinations[entry.destination]
                        val refName = "${entry.destination}Destination"
                        val refCount = destinationRefs[refName] ?: 0
                        val status =
                            when {
                                declared == null -> "MISSING_DESTINATION"
                                entry.entryMode == "navigation" && refCount == 0 -> "UNREACHABLE"
                                else -> "OK"
                            }
                        appendLine(
                            "- ${entry.destination} | capability=${entry.capabilityId} | type=${entry.uiType} | entry=${entry.entryMode} | refs=${refCount} | status=${status}"
                        )
                    }
                    appendLine()
                    if (issues.isEmpty()) {
                        appendLine("Result: PASS")
                    } else {
                        appendLine("Result: FAIL")
                        appendLine()
                        issues.forEach { issue -> appendLine("- $issue") }
                    }
                }
            )
        }
    }
}

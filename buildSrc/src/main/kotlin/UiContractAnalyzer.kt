data class UiCapabilityEntry(
    val destination: String,
    val screenFile: String,
    val capabilityId: String,
    val ownerModule: String,
    val uiType: String,
    val entryMode: String,
    val settingsSection: String?,
    val implementationFiles: List<String>,
)

data class DeclaredDestination(
    val destination: String,
    val screenFile: String,
    val lineNumber: Int,
    val graphName: String?,
    val isStartDestination: Boolean,
)

data class UiSourceAnalysis(
    val declaredDestinations: Map<String, DeclaredDestination>,
    val rootGraphDestinations: Set<String>,
    val nestedGraphDestinations: Set<String>,
    val destinationReferences: Map<String, Int>,
    val settingsEntryDestinations: Set<String>,
)

internal object UiContractAnalyzer {
    private const val SETTINGS_PAGER_PATH = "app/src/main/kotlin/screen/settings/SettingPager.kt"
    private val destinationAnnotationRegex = Regex("""@Destination<([^>]+)>(?:\(([^)]*)\))?""")
    private val functionRegex = Regex("""fun\s+([A-Za-z0-9_]+)\s*\(""")
    private val destinationReferenceRegex = Regex("""\b([A-Za-z0-9_]+Destination)\b""")
    private val navigateRegex = Regex("""navigate\(\s*([A-Za-z0-9_]+Destination)\b""")

    fun parseRegistry(lines: List<String>, fileLabel: String): List<UiCapabilityEntry> {
        return lines.mapIndexedNotNull { index, rawLine ->
            val line = rawLine.trim()
            if (line.isBlank() || line.startsWith("#")) {
                return@mapIndexedNotNull null
            }

            val parts = line.split('|').map(String::trim)
            require(parts.size == 8) {
                "Invalid registry format at $fileLabel:${index + 1}; expected 8 pipe-separated columns"
            }

            UiCapabilityEntry(
                destination = parts[0],
                screenFile = parts[1],
                capabilityId = parts[2],
                ownerModule = parts[3],
                uiType = parts[4],
                entryMode = parts[5],
                settingsSection = parts[6].ifBlank { null },
                implementationFiles =
                    parts[7].split(';').map(String::trim).filter(String::isNotBlank),
            )
        }
    }

    fun analyzeSources(sources: Collection<KotlinSourceFile>): UiSourceAnalysis {
        val declaredDestinations = linkedMapOf<String, DeclaredDestination>()
        val destinationReferences = linkedMapOf<String, Int>()
        val settingsEntryDestinations = linkedSetOf<String>()

        sources.forEach { source ->
            val lines = source.content.lines()
            lines.forEachIndexed { index, line ->
                val annotationMatch = destinationAnnotationRegex.find(line) ?: return@forEachIndexed
                val functionMatch =
                    ((index + 1)..minOf(index + 4, lines.lastIndex)).firstNotNullOfOrNull {
                        candidateIndex ->
                        functionRegex.find(lines[candidateIndex])
                    } ?: return@forEachIndexed

                val functionName = functionMatch.groupValues[1]
                val graphName = annotationMatch.groupValues[1].trim()
                val isStartDestination = "start = true" in annotationMatch.groupValues[2]

                declaredDestinations[functionName] =
                    DeclaredDestination(
                        destination = functionName,
                        screenFile = source.relativePath,
                        lineNumber = index + 1,
                        graphName = graphName,
                        isStartDestination = isStartDestination,
                    )
            }

            destinationReferenceRegex.findAll(source.content).forEach { reference ->
                val name = reference.groupValues[1]
                if (name.isNotBlank()) {
                    destinationReferences[name] = (destinationReferences[name] ?: 0) + 1
                }
            }

            if (source.relativePath == SETTINGS_PAGER_PATH) {
                navigateRegex.findAll(source.content).forEach { match ->
                    settingsEntryDestinations += match.groupValues[1].removeSuffix("Destination")
                }
            }
        }

        return UiSourceAnalysis(
            declaredDestinations = declaredDestinations,
            rootGraphDestinations =
                declaredDestinations.values
                    .filter { it.graphName == "RootGraph" }
                    .map { it.destination }
                    .toSet(),
            nestedGraphDestinations =
                declaredDestinations.values
                    .filter { !it.graphName.isNullOrBlank() && it.graphName != "RootGraph" }
                    .map { it.destination }
                    .toSet(),
            destinationReferences = destinationReferences,
            settingsEntryDestinations = settingsEntryDestinations,
        )
    }
}

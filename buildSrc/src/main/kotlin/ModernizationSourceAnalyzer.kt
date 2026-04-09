data class SourceIssue(val relativePath: String, val lineNumber: Int, val message: String)

data class AppStartupAnalysis(
    val delegatesDeferredStartupToCoordinator: Boolean,
    val forbiddenCalls: List<SourceIssue>,
)

data class ModernizationSourceAnalysis(
    val collectAsStateViolations: List<SourceIssue>,
    val launchedCollectViolations: List<SourceIssue>,
)

internal object ModernizationSourceAnalyzer {
    private val collectAsStateRegex = Regex("""(?<![A-Za-z0-9_])collectAsState\s*\(""")
    private val launchedCollectFallbackRegex =
        Regex(
            """LaunchedEffect\(\s*Unit\s*\)\s*\{[\s\S]{0,4000}?\.(collect|collectLatest)\s*(\(|\{)"""
        )
    private val forbiddenAppCalls =
        setOf(
            "warmUpProxyGroups",
            "extractGeoFiles",
            "runColdStartStorageCleanup",
            "ensureCleanupSchedulerStarted",
            "cleanupLegacyData",
        )

    fun analyzeComposeSources(sources: Collection<KotlinSourceFile>): ModernizationSourceAnalysis {
        val collectAsStateViolations = mutableListOf<SourceIssue>()
        val launchedCollectViolations = mutableListOf<SourceIssue>()

        sources.forEach { source ->
            collectAsStateRegex.findAll(source.content).forEach { match ->
                collectAsStateViolations +=
                    SourceIssue(
                        relativePath = source.relativePath,
                        lineNumber = lineNumberForOffset(source.content, match.range.first),
                        message =
                            "collectAsState() should be replaced with collectAsStateWithLifecycle()",
                    )
            }

            launchedCollectFallbackRegex.find(source.content)?.let { match ->
                if (launchedCollectViolations.none { it.relativePath == source.relativePath }) {
                    launchedCollectViolations +=
                        SourceIssue(
                            relativePath = source.relativePath,
                            lineNumber = lineNumberForOffset(source.content, match.range.first),
                            message = "LaunchedEffect(Unit) should not directly collect Flow values",
                        )
                }
            }
        }

        return ModernizationSourceAnalysis(
            collectAsStateViolations = collectAsStateViolations,
            launchedCollectViolations = launchedCollectViolations,
        )
    }

    fun analyzeAppStartup(source: KotlinSourceFile): AppStartupAnalysis {
        val forbiddenCalls = mutableListOf<SourceIssue>()
        val delegatesDeferredStartupToCoordinator =
            source.content.contains("startupCoordinator.ensureDeferredStartupInitialized(")

        forbiddenAppCalls.forEach { callName ->
            Regex("""\b${Regex.escape(callName)}\s*\(""").findAll(source.content).forEach { match ->
                forbiddenCalls +=
                    SourceIssue(
                        relativePath = source.relativePath,
                        lineNumber = lineNumberForOffset(source.content, match.range.first),
                        message =
                            "App.kt should not contain deferred startup implementation detail '$callName()'",
                    )
            }
        }

        return AppStartupAnalysis(
            delegatesDeferredStartupToCoordinator = delegatesDeferredStartupToCoordinator,
            forbiddenCalls = forbiddenCalls,
        )
    }
}

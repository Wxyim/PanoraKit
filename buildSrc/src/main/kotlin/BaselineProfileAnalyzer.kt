/** One `*.txt` baseline profile source, as read from `app/src/main/baselineProfiles`. */
internal data class BaselineProfileSource(val name: String, val text: String)

/** Aggregated facts about the baseline profile that ships inside the release artifact. */
internal data class BaselineProfileSummary(
    val sources: List<String>,
    val entries: Int,
    val classes: Int,
    val startupEntries: Int,
    val appEntries: Int,
    val appStartupEntries: Int,
    val launcherEntries: Int,
    val launcherStartupEntries: Int,
    val unparsedEntries: Int,
    val topPackages: List<Pair<String, Int>>,
) {
    fun renderLines(): List<String> =
        listOf(
            "Sources: ${if (sources.isEmpty()) "<none>" else sources.joinToString(", ")}",
            "Entries: $entries",
            "Distinct classes: $classes",
            "Startup entries (S flag): $startupEntries",
            "App-package entries: $appEntries",
            "App-package startup entries: $appStartupEntries",
            "Launcher-class entries: $launcherEntries",
            "Launcher-class startup entries: $launcherStartupEntries",
            "Unparsed lines: $unparsedEntries",
        ) +
            topPackages.map { (packageName, count) ->
                "  top package ${packageName.ifEmpty { "<root>" }}: $count"
            }
}

/**
 * Parses `baseline-prof.txt` style artifacts (AGP / R8 output) and derives the coverage facts that
 * the verification task asserts on.
 *
 * Profile lines look like `HSPLcom/example/Foo;->bar()V`, where the leading letters are ART profile
 * flags (hot / startup / post-startup) and `S` marks a startup entry. Parsing is intentionally
 * tolerant: unknown flag combinations still resolve the class, and anything unparseable is counted
 * so a format change shows up in the report instead of silently reporting full coverage.
 */
internal object BaselineProfileAnalyzer {
    private val entryPattern = Regex("""^([A-Z]*)L([^;]+);->(.+)$""")

    const val STARTUP_FLAG = 'S'

    fun analyze(
        sources: List<BaselineProfileSource>,
        appPackage: String,
        launcherClass: String,
    ): BaselineProfileSummary {
        val appPrefix = appPackage.replace('.', '/') + "/"
        val launcherPath = launcherClass.replace('.', '/')

        val classNames = mutableSetOf<String>()
        val packageCounts = mutableMapOf<String, Int>()
        var entries = 0
        var startupEntries = 0
        var appEntries = 0
        var appStartupEntries = 0
        var launcherEntries = 0
        var launcherStartupEntries = 0
        var unparsedEntries = 0

        // A profile can arrive as several fragments, and the same rule can legitimately appear in
        // both `baseline-prof.txt` and `startup-prof.txt`: the baseline merge folds the
        // startup-typed fragments in as well. Counting duplicate lines would inflate coverage and
        // hide a shrunken profile, so the union is de-duplicated by line.
        val seenLines = mutableSetOf<String>()

        sources.forEach { source ->
            source.text.lineSequence().forEach { rawLine ->
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("#")) return@forEach
                if (!seenLines.add(line)) return@forEach

                val match = entryPattern.matchEntire(line)
                if (match == null) {
                    unparsedEntries++
                    return@forEach
                }

                val flags = match.groupValues[1]
                val className = match.groupValues[2]
                val isStartup = flags.contains(STARTUP_FLAG)
                val isAppClass = className.startsWith(appPrefix)
                val isLauncherClass = className == launcherPath

                entries++
                classNames += className
                if (isStartup) startupEntries++
                if (isAppClass) appEntries++
                if (isAppClass && isStartup) appStartupEntries++
                if (isLauncherClass) launcherEntries++
                if (isLauncherClass && isStartup) launcherStartupEntries++

                val packageName = className.substringBeforeLast('/', "")
                packageCounts[packageName] = (packageCounts[packageName] ?: 0) + 1
            }
        }

        return BaselineProfileSummary(
            sources = sources.map(BaselineProfileSource::name),
            entries = entries,
            classes = classNames.size,
            startupEntries = startupEntries,
            appEntries = appEntries,
            appStartupEntries = appStartupEntries,
            launcherEntries = launcherEntries,
            launcherStartupEntries = launcherStartupEntries,
            unparsedEntries = unparsedEntries,
            topPackages =
                packageCounts.entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key }
                    )
                    .take(TOP_PACKAGE_LIMIT)
                    .map { it.key to it.value },
        )
    }

    /**
     * Compares a freshly collected profile against the profile currently committed to the repo so
     * coverage drift fails the build instead of shipping silently degraded startup performance.
     */
    fun compareToBaseline(
        current: BaselineProfileSummary,
        baseline: BaselineProfileSummary,
        minEntryRatio: Double,
        minClassRatio: Double,
    ): List<String> {
        val issues = mutableListOf<String>()
        if (baseline.entries == 0) return issues

        val entryFloor = (baseline.entries * minEntryRatio).toInt()
        if (current.entries < entryFloor) {
            issues +=
                "Baseline profile coverage regressed: ${current.entries} entries < floor $entryFloor " +
                    "(committed profile has ${baseline.entries} entries, minimum ratio $minEntryRatio)."
        }

        val classFloor = (baseline.classes * minClassRatio).toInt()
        if (baseline.classes > 0 && current.classes < classFloor) {
            issues +=
                "Baseline profile class coverage regressed: ${current.classes} classes < floor $classFloor " +
                    "(committed profile has ${baseline.classes} classes, minimum ratio $minClassRatio)."
        }

        if (baseline.appEntries > 0 && current.appEntries == 0) {
            issues +=
                "Baseline profile no longer contains any entry for the app's own package " +
                    "(committed profile has ${baseline.appEntries})."
        }

        return issues
    }

    fun renderDelta(current: BaselineProfileSummary, baseline: BaselineProfileSummary): String {
        if (baseline.entries == 0) return "No committed profile to compare against."
        val entryDelta = current.entries - baseline.entries
        val classDelta = current.classes - baseline.classes
        val sign = { value: Int -> if (value >= 0) "+$value" else value.toString() }
        return "Delta vs committed profile: entries ${sign(entryDelta)}, classes ${sign(classDelta)}."
    }

    private const val TOP_PACKAGE_LIMIT = 8
}

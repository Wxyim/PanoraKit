/** One `*.txt` baseline profile source, as read from the app's registered profile source dirs. */
internal data class BaselineProfileSource(val name: String, val text: String)

/** Aggregated facts about one or more baseline profile sources. */
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
    /** Rule identities deliberately omit profile flags, which may differ between source files. */
    val ruleIdentities: Set<BaselineProfileRuleIdentity>,
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

/** A class rule or a method rule. A null [member] represents a class-only rule. */
internal data class BaselineProfileRuleIdentity(val className: String, val member: String?)

/**
 * Parses `baseline-prof.txt` style artifacts (AGP / R8 output) and derives the coverage facts that
 * the verification task asserts on.
 *
 * Profile lines can be class-only rules such as `Lcom/example/Foo;` or method rules such as
 * `HSPLcom/example/Foo;->bar()V`, where the leading letters are ART profile flags (hot / startup /
 * post-startup) and `S` marks a startup entry. Parsing is intentionally tolerant: unknown flag
 * combinations still resolve the class, and anything unparseable is counted so a format change
 * shows up in the report instead of silently reporting full coverage.
 */
internal object BaselineProfileAnalyzer {
    // ART profile files contain both class-only rules (`Lpkg/Type;`) and method rules
    // (`SPLpkg/Type;->method()V`). Both contribute to the compiled profile and must be counted.
    private val entryPattern = Regex("""^([A-Z]*)L([^;]+);(?:->(.+))?$""")

    const val STARTUP_FLAG = 'S'

    fun analyze(
        sources: List<BaselineProfileSource>,
        appPackage: String,
        launcherClass: String,
    ): BaselineProfileSummary {
        val appPrefix = appPackage.replace('.', '/') + "/"
        val launcherPath = launcherClass.replace('.', '/')

        // Profiles from separate source files can name the same rule with different flags. Count
        // the rule once and union its flags, matching the profile merge semantics and preventing
        // the startup file from inflating the baseline coverage comparison.
        val startupByRule = linkedMapOf<BaselineProfileRuleIdentity, Boolean>()
        var unparsedEntries = 0

        sources.forEach { source ->
            source.text.lineSequence().forEach { rawLine ->
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("#")) return@forEach

                val match = entryPattern.matchEntire(line)
                if (match == null) {
                    unparsedEntries++
                    return@forEach
                }

                val flags = match.groupValues[1]
                val className = match.groupValues[2]
                val member = match.groupValues[3].takeIf(String::isNotEmpty)
                val identity = BaselineProfileRuleIdentity(className, member)
                val isStartup = flags.contains(STARTUP_FLAG)
                startupByRule[identity] = startupByRule[identity] == true || isStartup
            }
        }

        val rules = startupByRule.keys
        val classNames = rules.mapTo(mutableSetOf(), BaselineProfileRuleIdentity::className)
        val packageCounts =
            rules.groupingBy { it.className.substringBeforeLast('/', "") }.eachCount()
        val appRules = rules.filter { it.className.startsWith(appPrefix) }
        val launcherRules = rules.filter { it.className == launcherPath }
        val startupRules = startupByRule.filterValues { it }.keys
        val appStartupRules = startupRules.filter { it.className.startsWith(appPrefix) }
        val launcherStartupRules = startupRules.filter { it.className == launcherPath }

        return BaselineProfileSummary(
            sources = sources.map(BaselineProfileSource::name),
            entries = rules.size,
            classes = classNames.size,
            startupEntries = startupRules.size,
            appEntries = appRules.size,
            appStartupEntries = appStartupRules.size,
            launcherEntries = launcherRules.size,
            launcherStartupEntries = launcherStartupRules.size,
            unparsedEntries = unparsedEntries,
            topPackages =
                packageCounts.entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key }
                    )
                    .take(TOP_PACKAGE_LIMIT)
                    .map { it.key to it.value },
            ruleIdentities = rules,
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

    /**
     * A non-fatal heads-up for class coverage that slipped below the committed profile.
     *
     * The ratio floors in [compareToBaseline] only catch a profile that lost a serious share of its
     * coverage; a smaller shrink still passes and stays invisible across dispatches. Reporting
     * every decrease as a warning keeps the trend on the dashboard without failing the build over a
     * class count that legitimately wobbles between collections.
     */
    fun renderClassTrendWarning(
        current: BaselineProfileSummary,
        baseline: BaselineProfileSummary,
    ): String? {
        if (baseline.entries == 0 || baseline.classes == 0 || current.classes >= baseline.classes) {
            return null
        }
        val entryDelta = current.entries - baseline.entries
        val entryNote = if (entryDelta >= 0) "+$entryDelta" else entryDelta.toString()
        return "Distinct classes decreased by ${baseline.classes - current.classes} vs the committed " +
            "profile (${baseline.classes} -> ${current.classes}) while entries changed by $entryNote; " +
            "still above the regression floor, but worth watching across dispatches."
    }

    private const val TOP_PACKAGE_LIMIT = 8
}

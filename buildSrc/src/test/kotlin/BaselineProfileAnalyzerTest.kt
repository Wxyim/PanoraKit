import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaselineProfileAnalyzerTest {
    private val appPackage = "com.github.nomadboxlab.monadbox"
    private val launcherClass = "$appPackage.MainActivity"

    private val profile =
        """
        HSPLcom/github/nomadboxlab/monadbox/MainActivity;-><init>()V
        HSPLcom/github/nomadboxlab/monadbox/MainActivity;->onCreate(Landroid/os/Bundle;)V
        HSPLandroidx/compose/runtime/ComposerImpl;->changedInstance(Ljava/lang/Object;)Z
        PLcom/github/nomadboxlab/monadbox/App;-><clinit>()V
        Lcom/github/nomadboxlab/monadbox/App;-><init>()V
        """
            .trimIndent()

    private fun analyze(text: String) =
        BaselineProfileAnalyzer.analyze(
            sources = listOf(BaselineProfileSource("baseline-prof.txt", text)),
            appPackage = appPackage,
            launcherClass = launcherClass,
        )

    @Test
    fun countsEntriesClassesAndStartupCoverage() {
        val summary = analyze(profile)

        assertEquals(5, summary.entries)
        assertEquals(3, summary.classes)
        assertEquals(3, summary.startupEntries)
        assertEquals(4, summary.appEntries)
        assertEquals(2, summary.appStartupEntries)
        assertEquals(2, summary.launcherEntries)
        assertEquals(2, summary.launcherStartupEntries)
        assertEquals(0, summary.unparsedEntries)
        assertEquals("com/github/nomadboxlab/monadbox", summary.topPackages.first().first)
        assertEquals(4, summary.topPackages.first().second)
    }

    @Test
    fun toleratesCommentsBlankLinesAndReportsUnparsedEntries() {
        val summary =
            analyze(
                """
                # ART profile dump

                HSPLcom/github/nomadboxlab/monadbox/MainActivity;-><init>()V
                not-a-profile-line
                """
                    .trimIndent()
            )

        assertEquals(1, summary.entries)
        assertEquals(1, summary.unparsedEntries)
        assertEquals(1, summary.launcherEntries)
    }

    @Test
    fun flagsCoverageRegressionBelowFloor() {
        val baseline = analyze(profile)
        val shrunken = analyze("HSPLcom/github/nomadboxlab/monadbox/MainActivity;-><init>()V")

        val issues =
            BaselineProfileAnalyzer.compareToBaseline(
                current = shrunken,
                baseline = baseline,
                minEntryRatio = 0.9,
                minClassRatio = 0.9,
            )

        assertEquals(2, issues.size)
        assertTrue(issues.any { it.contains("entries < floor 4") }, issues.toString())
        assertTrue(issues.any { it.contains("classes < floor 2") }, issues.toString())
    }

    @Test
    fun acceptsCoverageWithinFloor() {
        val baseline = analyze(profile)
        val current =
            analyze(
                profile.replace(
                    "HSPLandroidx/compose/runtime/ComposerImpl;->changedInstance(Ljava/lang/Object;)Z\n",
                    "",
                )
            )

        val issues =
            BaselineProfileAnalyzer.compareToBaseline(
                current = current,
                baseline = baseline,
                minEntryRatio = 0.8,
                minClassRatio = 0.6,
            )

        assertTrue(issues.isEmpty(), issues.toString())
        assertEquals(4, current.entries)
    }

    @Test
    fun detectsProfileThatLostAllAppCoverage() {
        val baseline = analyze(profile)
        val current =
            analyze(
                "HSPLandroidx/compose/runtime/ComposerImpl;->changedInstance(Ljava/lang/Object;)Z"
            )

        val issues =
            BaselineProfileAnalyzer.compareToBaseline(
                current = current,
                baseline = baseline,
                minEntryRatio = 0.1,
                minClassRatio = 0.1,
            )

        assertTrue(issues.any { it.contains("no longer contains any entry") }, issues.toString())
    }

    @Test
    fun reportsDeltaAgainstBaseline() {
        val baseline = analyze(profile)
        val current = analyze(profile)

        assertEquals(
            "Delta vs committed profile: entries +0, classes +0.",
            BaselineProfileAnalyzer.renderDelta(current, baseline),
        )
    }
}

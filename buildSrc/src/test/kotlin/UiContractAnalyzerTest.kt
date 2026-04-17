import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UiContractAnalyzerTest {
    @Test
    fun parsesDeclaredDestinationsAcrossGraphsAndSettingsReferences() {
        val analysis =
            UiContractAnalyzer.analyzeSources(
                listOf(
                    KotlinSourceFile(
                        relativePath = "app/src/screen/Home.kt",
                        content =
                            """
                            import com.ramcosta.composedestinations.annotation.Destination
                            import com.ramcosta.composedestinations.annotation.RootGraph

                            @Destination<RootGraph>(start = true)
                            fun HomeScreen() {}
                            """
                                .trimIndent(),
                    ),
                    KotlinSourceFile(
                        relativePath = "modules/feature/override/src/Edit.kt",
                        content =
                            """
                            import com.ramcosta.composedestinations.annotation.Destination

                            @Destination<OverrideEditorNavGraph>
                            fun OverrideEditRoute() {}
                            """
                                .trimIndent(),
                    ),
                    KotlinSourceFile(
                        relativePath = "app/src/main/kotlin/screen/settings/SettingPager.kt",
                        content =
                            """
                            fun SettingPager(navigator: Any) {
                                navigator.navigate(HomeScreenDestination)
                            }
                            """
                                .trimIndent(),
                    ),
                )
            )

        assertEquals(setOf("HomeScreen"), analysis.rootGraphDestinations)
        assertEquals(setOf("OverrideEditRoute"), analysis.nestedGraphDestinations)
        assertTrue(analysis.declaredDestinations.containsKey("OverrideEditRoute"))
        assertEquals(setOf("HomeScreen"), analysis.settingsEntryDestinations)
        assertEquals(1, analysis.destinationReferences["HomeScreenDestination"])
    }

    @Test
    fun parsesRegistryRows() {
        val entries =
            UiContractAnalyzer.parseRegistry(
                lines =
                    listOf("Foo|app/src/Foo.kt|foo|app|top-level|navigation|more|app/src/Foo.kt"),
                fileLabel = "config/ui-capability-registry.txt",
            )

        assertEquals(1, entries.size)
        assertEquals("Foo", entries.single().destination)
    }
}

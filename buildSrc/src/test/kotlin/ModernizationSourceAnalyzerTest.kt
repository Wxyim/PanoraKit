import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModernizationSourceAnalyzerTest {
    @Test
    fun detectsCollectAsStateAndLaunchedEffectCollectViolations() {
        val analysis =
            ModernizationSourceAnalyzer.analyzeComposeSources(
                listOf(
                    KotlinSourceFile(
                        relativePath = "app/src/Foo.kt",
                        content =
                            """
                            import androidx.compose.runtime.Composable
                            import androidx.compose.runtime.LaunchedEffect

                            @Composable
                            fun Foo(flow: kotlinx.coroutines.flow.Flow<Int>, state: kotlinx.coroutines.flow.StateFlow<Int>) {
                                val value = state.collectAsState().value
                                LaunchedEffect(Unit) {
                                    flow.collect { println(it) }
                                }
                            }
                            """.trimIndent(),
                    )
                )
            )

        assertEquals(1, analysis.collectAsStateViolations.size)
        assertEquals("app/src/Foo.kt", analysis.collectAsStateViolations.single().relativePath)
        assertEquals(1, analysis.launchedCollectViolations.size)
    }

    @Test
    fun acceptsLifecycleAwarePatterns() {
        val analysis =
            ModernizationSourceAnalyzer.analyzeComposeSources(
                listOf(
                    KotlinSourceFile(
                        relativePath = "app/src/Bar.kt",
                        content =
                            """
                            import androidx.compose.runtime.Composable

                            @Composable
                            fun Bar(state: kotlinx.coroutines.flow.StateFlow<Int>) {
                                val value = state.collectAsStateWithLifecycle().value
                                CollectFlowWithLifecycle(flow = state) { println(it) }
                            }
                            """.trimIndent(),
                    )
                )
            )

        assertTrue(analysis.collectAsStateViolations.isEmpty())
        assertTrue(analysis.launchedCollectViolations.isEmpty())
    }

    @Test
    fun detectsThinApplicationDelegation() {
        val analysis =
            ModernizationSourceAnalyzer.analyzeAppStartup(
                KotlinSourceFile(
                    relativePath = "app/src/App.kt",
                    content =
                        """
                        class App {
                            fun ensureDeferredStartupInitialized() {
                                startupCoordinator.ensureDeferredStartupInitialized()
                            }
                        }
                        """.trimIndent(),
                )
            )

        assertTrue(analysis.delegatesDeferredStartupToCoordinator)
        assertFalse(analysis.forbiddenCalls.isNotEmpty())
    }
}
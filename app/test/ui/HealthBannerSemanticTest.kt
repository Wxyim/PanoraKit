package com.github.nomadboxlab.monadbox.ui

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.github.nomadboxlab.monadbox.presentation.component.HealthBanner
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class HealthBannerSemanticTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun healthBanner_exposesHeadlineInContentDescription() {
        composeTestRule.setContent {
            HealthBanner(headline = "Connection healthy", tone = SemanticTone.Success)
        }

        composeTestRule
            .onNodeWithContentDescription("Connection healthy", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun healthBanner_includesSubtitleInContentDescription() {
        composeTestRule.setContent {
            HealthBanner(
                headline = "Degraded",
                subtitle = "Latency spike detected",
                tone = SemanticTone.Warning,
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Latency spike detected", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun healthBanner_exposesToneAsStateDescription() {
        composeTestRule.setContent {
            HealthBanner(headline = "Service error", tone = SemanticTone.Danger)
        }

        composeTestRule.onNode(hasStateDescription("danger")).assertIsDisplayed()
    }

    @Test
    fun healthBanner_successToneStateDescription() {
        composeTestRule.setContent {
            HealthBanner(headline = "All good", tone = SemanticTone.Success)
        }

        composeTestRule.onNode(hasStateDescription("success")).assertIsDisplayed()
    }

    private fun hasStateDescription(value: String): SemanticsMatcher {
        return SemanticsMatcher("stateDescription = '$value'") { node ->
            node.config.getOrElseNullable(SemanticsProperties.StateDescription) { null } == value
        }
    }
}

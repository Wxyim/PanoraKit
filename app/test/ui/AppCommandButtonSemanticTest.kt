package com.github.yumelira.yumebox.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.github.yumelira.yumebox.presentation.component.AppCommandButton
import com.github.yumelira.yumebox.presentation.component.SemanticTone
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.Check
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class AppCommandButtonSemanticTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun commandButton_enabledState_hasClickAction() {
        composeTestRule.setContent {
            AppCommandButton(title = "Save", imageVector = Yume.Check, onClick = {}, enabled = true)
        }

        composeTestRule
            .onNodeWithContentDescription("Save", substring = true)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun commandButton_disabledState_exposesDisabledSemantics() {
        composeTestRule.setContent {
            AppCommandButton(
                title = "Save",
                imageVector = Yume.Check,
                onClick = {},
                enabled = false,
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Save", substring = true)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun commandButton_dangerTone_includesToneInDescription() {
        composeTestRule.setContent {
            AppCommandButton(
                title = "Delete",
                imageVector = Yume.Check,
                onClick = {},
                tone = SemanticTone.Danger,
            )
        }

        composeTestRule.onNodeWithContentDescription("Delete", substring = true).assertIsDisplayed()
    }
}

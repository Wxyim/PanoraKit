package com.github.yumelira.yumebox.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.yumelira.yumebox.presentation.component.DialogButtonRow
import com.github.yumelira.yumebox.presentation.component.TestTags
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class DialogButtonRowTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun dialogButtonRow_displaysDefaultCancelAndConfirm() {
        composeTestRule.setContent { DialogButtonRow(onCancel = {}, onConfirm = {}) }

        composeTestRule.onNodeWithTag(TestTags.Dialog.CancelButton).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.Dialog.ConfirmButton).assertIsDisplayed()
    }

    @Test
    fun dialogButtonRow_cancelButtonTriggersCallback() {
        var cancelled = false
        composeTestRule.setContent {
            DialogButtonRow(onCancel = { cancelled = true }, onConfirm = {})
        }

        composeTestRule
            .onNodeWithTag(TestTags.Dialog.CancelButton)
            .assertHasClickAction()
            .performClick()

        assertTrue("Cancel callback should have been invoked", cancelled)
    }

    @Test
    fun dialogButtonRow_confirmButtonTriggersCallback() {
        var confirmed = false
        composeTestRule.setContent {
            DialogButtonRow(onCancel = {}, onConfirm = { confirmed = true })
        }

        composeTestRule
            .onNodeWithTag(TestTags.Dialog.ConfirmButton)
            .assertHasClickAction()
            .performClick()

        assertTrue("Confirm callback should have been invoked", confirmed)
    }

    @Test
    fun dialogButtonRow_customTextIsDisplayed() {
        composeTestRule.setContent {
            DialogButtonRow(
                onCancel = {},
                onConfirm = {},
                cancelText = "Discard",
                confirmText = "Save",
            )
        }

        composeTestRule.onNodeWithText("Discard", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Save", substring = true).assertIsDisplayed()
    }
}

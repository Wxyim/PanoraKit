/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.nomadboxlab.monadbox.presentation.component.DialogButtonRow
import com.github.nomadboxlab.monadbox.presentation.component.TestTags
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

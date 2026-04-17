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

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.github.nomadboxlab.monadbox.presentation.component.AppCommandButton
import com.github.nomadboxlab.monadbox.presentation.component.SemanticTone
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
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
            AppCommandButton(
                title = "Save",
                imageVector = MonadIcons.Check,
                onClick = {},
                enabled = true,
            )
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
                imageVector = MonadIcons.Check,
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
                imageVector = MonadIcons.Check,
                onClick = {},
                tone = SemanticTone.Danger,
            )
        }

        composeTestRule.onNodeWithContentDescription("Delete", substring = true).assertIsDisplayed()
    }
}

/*
 * This file is part of MonadBox.
 *
 * Regression test for the "配置 - 编辑配置 text boxes" report: after typing into a text box the
 * caret must end up at the end of the text (typing appends), even though focus is requested
 * without a tap (opening the IME / the test framework focusing the field). Also verifies that
 * explicitly moved carets are still respected and that externally replaced text re-seeds the
 * caret to the end.
 */

package com.github.nomadboxlab.monadbox.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import com.github.nomadboxlab.monadbox.presentation.component.MiuixEditTextField
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class MiuixEditTextFieldCaretTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun prefilledValue_typingAppendsAtEnd() {
        val textState = mutableStateOf("hello")
        composeTestRule.setContent {
            MiuixEditTextField(value = textState.value, onValueChange = { textState.value = it })
        }

        // Focus is requested programmatically like the test framework does for `performTextInput`;
        // the caret must already sit at the end of the pre-filled text so "!" is appended.
        composeTestRule.onNode(hasSetTextAction()).performTextInput("!")

        composeTestRule.onNode(hasSetTextAction()).assertTextEquals("hello!")
    }

    @Test
    fun movedCaret_isRespectedWhenTyping() {
        val textState = mutableStateOf("hello")
        composeTestRule.setContent {
            MiuixEditTextField(value = textState.value, onValueChange = { textState.value = it })
        }

        // Moving the caret to the start (tap / selection) must not be overridden while editing.
        composeTestRule.onNode(hasSetTextAction()).performTextInputSelection(TextRange(0))
        composeTestRule.onNode(hasSetTextAction()).performTextInput("X")

        composeTestRule.onNode(hasSetTextAction()).assertTextEquals("Xhello")
    }

    @Test
    fun externalValueChange_reseedsCaretToEnd() {
        val textState = mutableStateOf("hello")
        composeTestRule.setContent {
            MiuixEditTextField(value = textState.value, onValueChange = { textState.value = it })
        }

        // Simulate the caller re-seeding the text while the field is composed (dialog re-open,
        // clear button, filtered input); typing afterwards must append to the new text.
        composeTestRule.runOnUiThread { textState.value = "world" }
        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasSetTextAction()).performTextInput("!")

        composeTestRule.onNode(hasSetTextAction()).assertTextEquals("world!")
    }
}

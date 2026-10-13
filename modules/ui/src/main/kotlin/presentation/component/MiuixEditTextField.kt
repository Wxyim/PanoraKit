/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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

package com.github.nomadboxlab.monadbox.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import top.yukonga.miuix.kmp.basic.TextField

/**
 * Miuix [TextField] for editing a value that is pre-filled when the surrounding dialog or sheet
 * opens (e.g. "编辑配置" text rows, key/value entries, profile names).
 *
 * The `String` overload of Miuix [TextField] seeds its internal [TextFieldValue] with the caret at
 * position 0, and Foundation only moves the caret to the tapped position once the field is already
 * focused. For a pre-filled value this leaves the caret in front of the existing text, so typing
 * inserts at the beginning and the caret "never goes to the end" as expected when editing.
 *
 * This wrapper keeps a [TextFieldValue] whose initial selection is at the END of [value]: opening a
 * dialog starts with the caret after the existing text, so typing appends. Taps, long-presses and
 * drags still move the caret/selection normally while the field is focused. When the caller
 * replaces [value] with a different text (dialog re-opened with a new seed, value cleared, keyboard
 * input filtered, ...) the field re-syncs and places the caret at the end of the new text again.
 *
 * Note for the Miuix upgrade checklist: the upstream (latest) `String` overload still seeds the
 * selection at position 0 through `BasicTextField(value: String)`, so this wrapper cannot be
 * dropped just by bumping Miuix; only drop it once Miuix seeds the selection at the end itself.
 */
@Composable
fun MiuixEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
) {
    // The caret/selection is part of the field state and must survive recompositions while the
    // user edits, so this state must not be keyed on `value`.
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // Re-seed only when the text is replaced from outside. Text changes caused by typing through
    // this field never trigger a reset: `onValueChange` below updates the caller synchronously
    // before the next recomposition.
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(text = value, selection = TextRange(value.length))
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { updatedValue ->
            textFieldValue = updatedValue
            onValueChange(updatedValue.text)
        },
        modifier = modifier,
        label = label,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
    )
}

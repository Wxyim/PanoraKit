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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Check
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Close
import dev.oom_wg.purejoy.mlang.MLang

@Composable
fun DialogButtonRow(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    cancelText: String = MLang.Component.Button.Cancel,
    confirmText: String = MLang.Component.Button.Confirm,
    cancelTone: SemanticTone = SemanticTone.Neutral,
    confirmTone: SemanticTone = SemanticTone.Brand,
    cancelHighEmphasis: Boolean = false,
    confirmHighEmphasis: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        AppCommandButton(
            title = cancelText,
            imageVector = MonadIcons.Close,
            onClick = onCancel,
            modifier = Modifier.weight(1f).testTag(TestTags.Dialog.CancelButton),
            tone = cancelTone,
            highEmphasis = cancelHighEmphasis,
        )
        AppCommandButton(
            title = confirmText,
            imageVector = MonadIcons.Check,
            onClick = onConfirm,
            modifier = Modifier.weight(1f).testTag(TestTags.Dialog.ConfirmButton),
            tone = confirmTone,
            highEmphasis = confirmHighEmphasis,
        )
    }
}

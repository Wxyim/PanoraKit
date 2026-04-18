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

package com.github.nomadboxlab.monadbox.presentation.component

import com.github.nomadboxlab.monadbox.common.util.ToastDialogBridge
import dev.oom_wg.purejoy.mlang.MLang

object GlobalDialogPresenter {
    fun showError(message: String, title: String = MLang.Component.Message.Error) {
        if (message.isBlank()) return
        ToastDialogBridge.show(message = message, title = title)
    }
}

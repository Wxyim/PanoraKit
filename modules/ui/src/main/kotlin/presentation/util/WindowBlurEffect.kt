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

package com.github.nomadboxlab.monadbox.presentation.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView

@Composable
fun WindowBlurEffect(useBlur: Boolean, blurRadius: Int = 30) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

    val view = LocalView.current
    val activity = view.context.findActivity() ?: return
    val window = activity.window

    // Every assignment to `window.attributes` is a WindowManager transaction that makes the
    // system re-composite and re-blur the whole window behind this one, so only push an update
    // when the requested blur actually changes instead of on every recomposition.
    var appliedEnabled by remember { mutableStateOf<Boolean?>(null) }
    var appliedBlurRadius by remember { mutableIntStateOf(Int.MIN_VALUE) }

    SideEffect {
        val targetRadius = if (useBlur) blurRadius else 0
        if (appliedEnabled == useBlur && appliedBlurRadius == targetRadius) return@SideEffect
        appliedEnabled = useBlur
        appliedBlurRadius = targetRadius
        if (useBlur) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        }
        window.attributes.blurBehindRadius = targetRadius
        window.attributes = window.attributes
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

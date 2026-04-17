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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role

object AppInteractionFeedbackDefaults {
    const val PressedAlpha = 0.82f
    const val DisabledAlpha = 0.48f
    const val NavigationPressedAlpha = 0.86f
}

@Composable
fun Modifier.appClickable(
    enabled: Boolean = true,
    role: Role? = Role.Button,
    onClickLabel: String? = null,
    pressedAlpha: Float = AppInteractionFeedbackDefaults.PressedAlpha,
    disabledAlpha: Float = AppInteractionFeedbackDefaults.DisabledAlpha,
    useIndication: Boolean = true,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val feedbackAlpha by
        animateFloatAsState(
            targetValue =
                when {
                    !enabled -> disabledAlpha
                    pressed -> pressedAlpha
                    else -> 1f
                },
            label = "app_clickable_feedback_alpha",
        )
    val indication = if (useIndication) LocalIndication.current else null

    return alpha(feedbackAlpha)
        .clickable(
            enabled = enabled,
            role = role,
            onClickLabel = onClickLabel,
            interactionSource = interactionSource,
            indication = indication,
            onClick = onClick,
        )
}

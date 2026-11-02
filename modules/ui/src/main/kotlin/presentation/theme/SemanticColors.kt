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

package com.github.nomadboxlab.monadbox.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SemanticColorToken(
    val foreground: Color,
    val container: Color,
    val highEmphasisContainer: Color,
    val iconContainer: Color,
    val highEmphasisIconContainer: Color,
    val border: Color,
    val highEmphasisBorder: Color,
)

@Immutable
data class AppSemanticColors(
    val brand: SemanticColorToken,
    val success: SemanticColorToken,
    val info: SemanticColorToken,
    val warning: SemanticColorToken,
    val danger: SemanticColorToken,
    val neutral: SemanticColorToken,
    val global: SemanticColorToken,
)

val LocalSemanticColors = staticCompositionLocalOf {
    appSemanticColors(
        isDark = false,
        primary = Color.Black,
        error = Color(0xFFBA1A1A),
        errorContainer = Color(0xFFFFDAD6),
        surfaceVariant = Color(0xFFE8E8E8),
        onSurface = Color.Black,
    )
}

fun appSemanticColors(
    isDark: Boolean,
    primary: Color,
    error: Color,
    errorContainer: Color,
    surfaceVariant: Color,
    onSurface: Color,
): AppSemanticColors {
    fun token(
        foreground: Color,
        containerAlpha: Float = if (isDark) 0.16f else 0.10f,
        highEmphasisContainerAlpha: Float = if (isDark) 0.24f else 0.16f,
    ) =
        SemanticColorToken(
            foreground = foreground,
            container = foreground.copy(alpha = containerAlpha),
            highEmphasisContainer = foreground.copy(alpha = highEmphasisContainerAlpha),
            iconContainer = foreground.copy(alpha = 0.10f),
            highEmphasisIconContainer = foreground.copy(alpha = 0.14f),
            border = foreground.copy(alpha = 0.12f),
            highEmphasisBorder = foreground.copy(alpha = 0.22f),
        )

    val neutralForeground = onSurface.copy(alpha = if (isDark) 0.86f else 0.92f)
    val neutralContainer = surfaceVariant.copy(alpha = if (isDark) 0.76f else 0.82f)
    val neutralHighEmphasisContainer = surfaceVariant.copy(alpha = if (isDark) 0.94f else 0.96f)

    return AppSemanticColors(
        brand = token(primary),
        success = token(if (isDark) Color(0xFF76D395) else Color(0xFF1B7F3A)),
        info = token(if (isDark) Color(0xFF8AB4F8) else Color(0xFF1558A6)),
        warning = token(if (isDark) Color(0xFFF2C66D) else Color(0xFF855A00)),
        danger =
            SemanticColorToken(
                foreground = error,
                container = errorContainer.copy(alpha = 0.54f),
                highEmphasisContainer = errorContainer.copy(alpha = 0.82f),
                iconContainer = error.copy(alpha = 0.10f),
                highEmphasisIconContainer = error.copy(alpha = 0.14f),
                border = error.copy(alpha = 0.12f),
                highEmphasisBorder = error.copy(alpha = 0.22f),
            ),
        neutral =
            SemanticColorToken(
                foreground = neutralForeground,
                container = neutralContainer,
                highEmphasisContainer = neutralHighEmphasisContainer,
                iconContainer = neutralForeground.copy(alpha = 0.10f),
                highEmphasisIconContainer = neutralForeground.copy(alpha = 0.14f),
                border = neutralForeground.copy(alpha = 0.12f),
                highEmphasisBorder = neutralForeground.copy(alpha = 0.22f),
            ),
        global = token(if (isDark) Color(0xFFC9B6F2) else Color(0xFF5E35B1)),
    )
}

fun AppSemanticColors.latencyForeground(delayMs: Int): Color =
    when (delayMs) {
        in 1..300 -> success.foreground
        in 301..1000 -> warning.foreground
        else -> danger.foreground
    }

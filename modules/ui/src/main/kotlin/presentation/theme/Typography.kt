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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Application typography scale.
 *
 * Semantic text styles matching the component library's actual usage:
 * - **micro** (9 sp): chart axis labels
 * - **mini** (10 sp): trace timestamps, secondary indicators
 * - **caption** (11 sp): navigation labels, compact badges, timeline timestamps
 * - **footnote** (12 sp): summaries, descriptions, badge text, settings row summaries
 * - **label** (13 sp): subtitles, command bar labels, panel section titles
 * - **body** (14 sp): body text, info text, timeline titles
 * - **bodyLarge** (15 sp): banner headlines, button labels
 * - **title** (16 sp): settings row titles, row titles
 * - **titleLarge** (17 sp): card titles
 * - **headline** (18 sp): metric values, primary numeric displays
 */
@Immutable
data class AppTypography(
    val micro: TextStyle = TextStyle(fontSize = 9.sp),
    val mini: TextStyle = TextStyle(fontSize = 10.sp),
    val caption: TextStyle = TextStyle(fontSize = 11.sp),
    val footnote: TextStyle = TextStyle(fontSize = 12.sp),
    val label: TextStyle = TextStyle(fontSize = 13.sp),
    val body: TextStyle = TextStyle(fontSize = 14.sp),
    val bodyLarge: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
    val title: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    val titleLarge: TextStyle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    val headline: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
)

val LocalAppTypography = staticCompositionLocalOf { AppTypography() }

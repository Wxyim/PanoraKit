/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
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
 * Copyright (c)  YumeLira 2025 - Present
 *
 */

package com.github.yumelira.yumebox.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.github.yumelira.yumebox.presentation.theme.HomeIdleLayoutDefaults
import com.github.yumelira.yumebox.presentation.theme.rememberHomeIdleLayoutMetrics
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeIdleContent(oneWord: String, author: String, modifier: Modifier = Modifier) {
    val accentColor = MiuixTheme.colorScheme.primary
    val authorColor = MiuixTheme.colorScheme.onSurfaceVariantSummary

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val layoutMetrics = rememberHomeIdleLayoutMetrics(maxHeight)

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = layoutMetrics.bottomSpacing),
            verticalArrangement = Arrangement.spacedBy(HomeIdleLayoutDefaults.SectionSpacing),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(layoutMetrics.topSpacing))

            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(
                                top = HomeIdleLayoutDefaults.TitleTopPadding,
                                start = HomeIdleLayoutDefaults.TitleStartPadding,
                            )
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(HomeIdleLayoutDefaults.TitleRowSpacing),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(
                            modifier =
                                Modifier.padding(top = HomeIdleLayoutDefaults.AccentBarTopPadding)
                                    .width(HomeIdleLayoutDefaults.AccentBarWidth)
                                    .height(HomeIdleLayoutDefaults.AccentBarHeight)
                                    .background(
                                        Brush.verticalGradient(
                                            colors =
                                                listOf(
                                                    accentColor.copy(alpha = 0.58f),
                                                    Color.Transparent,
                                                )
                                        )
                                    )
                        )
                        Text(
                            text = oneWord,
                            style =
                                MiuixTheme.textStyles.headline1.copy(
                                    fontSize = HomeIdleLayoutDefaults.TitleFontSize,
                                    lineHeight = HomeIdleLayoutDefaults.TitleLineHeight,
                                    letterSpacing = HomeIdleLayoutDefaults.TitleLetterSpacing,
                                    fontWeight = FontWeight.Medium,
                                ),
                            color = MiuixTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier.width(HomeIdleLayoutDefaults.AuthorLineWidth)
                            .height(HomeIdleLayoutDefaults.AuthorLineHeight)
                            .background(authorColor.copy(alpha = 0.35f))
                )
                Spacer(modifier = Modifier.width(HomeIdleLayoutDefaults.AuthorSpacing))
                Text(
                    text = author,
                    style =
                        MiuixTheme.textStyles.title3.copy(
                            fontSize = HomeIdleLayoutDefaults.AuthorFontSize,
                            letterSpacing = HomeIdleLayoutDefaults.AuthorLetterSpacing,
                            fontWeight = FontWeight.Medium,
                        ),
                    color = authorColor,
                )
            }
        }
    }
}

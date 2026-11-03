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

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Badge-plus`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Settings-2`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.chevron
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

val OverrideSectionSpacing = 12.dp
val OverrideSectionTitleSpacing = 8.dp
val OverrideSectionBottomSpacing = 32.dp
val OverrideFloatingActionContentBottomPadding = 112.dp
private val OverrideFieldAssistIndent = 14.dp
private val OverrideFieldAssistVerticalPadding = 8.dp
private val OverrideActionButtonSize = 35.dp
private val OverrideActionIconSize = 18.dp
private val OverrideStatusBadgeSize = 32.dp
private val OverrideStatusBadgeIconSize = 18.dp

val LocalOverrideCardHorizontalPadding = staticCompositionLocalOf { true }

@Composable
fun Modifier.overrideCardOutline(shape: Shape = RoundedCornerShape(24.dp)): Modifier =
    border(
        width = 0.8.dp,
        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.28f),
        shape = shape,
    )

@Composable
fun Modifier.overrideCardSurface(shape: Shape = RoundedCornerShape(24.dp)): Modifier =
    background(MiuixTheme.colorScheme.surface, shape).overrideCardOutline(shape)

enum class OverrideActionTone {
    Neutral,
    Primary,
    Danger,
}

@Composable
fun OverrideSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OverrideSectionTitleSpacing),
        content = {
            SmallTitle(title)
            content()
        },
    )
}

@Composable
fun OverrideCardSection(
    title: String,
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean? = null,
    content: @Composable () -> Unit,
) {
    OverrideSection(title = title, modifier = modifier) {
        OverrideSelectorCard(applyHorizontalPadding = applyHorizontalPadding, content = content)
    }
}

@Composable
fun OverrideFormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    OverrideSection(title = title, modifier = modifier) { OverrideSelectorCard(content = content) }
}

@Composable
fun OverrideFieldAssistText(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MiuixTheme.textStyles.body2,
        color = color,
        modifier =
            modifier.padding(
                start = OverrideFieldAssistIndent,
                top = OverrideFieldAssistVerticalPadding,
                bottom = OverrideFieldAssistVerticalPadding,
            ),
    )
}

@Composable
fun OverrideSelectorCard(
    modifier: Modifier = Modifier,
    applyHorizontalPadding: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val cardModifier = modifier.overrideCardSurface(shape)
    Card(
        modifier = cardModifier,
        insideMargin = PaddingValues(vertical = 6.dp),
        applyHorizontalPadding =
            applyHorizontalPadding ?: LocalOverrideCardHorizontalPadding.current,
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = { content() })
    }
}

@Composable
fun OverrideEmptyStateCard(
    title: String,
    hint: String,
    actionLabel: String? = null,
    actionIcon: ImageVector = MonadIcons.`Badge-plus`,
    modifier: Modifier = Modifier,
    onAction: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        modifier = modifier.overrideCardSurface(shape),
        insideMargin = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        applyHorizontalPadding = LocalOverrideCardHorizontalPadding.current,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = hint,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            if (onAction != null && !actionLabel.isNullOrBlank()) {
                AppCommandButton(
                    title = actionLabel,
                    imageVector = actionIcon,
                    tone = SemanticTone.Brand,
                    highEmphasis = true,
                    modifier = Modifier.widthIn(max = 240.dp),
                    onClick = onAction,
                )
            }
        }
    }
}

@Composable
fun OverrideSectionCardHeader(
    title: String,
    summary: String? = null,
    expanded: Boolean,
    onClick: () -> Unit,
    showIndicator: Boolean = true,
    imageVector: ImageVector = MonadIcons.`Settings-2`,
    tone: SemanticTone = SemanticTone.Neutral,
    active: Boolean = false,
    horizontalInset: Dp = 10.dp,
) {
    val indicatorRotation =
        animateFloatAsState(
            targetValue = if (expanded) 90f else 0f,
            animationSpec = tween(durationMillis = 180),
            label = "override_section_indicator_rotation",
        )
    val emphasized = active || expanded
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = emphasized)
    val shape = RoundedCornerShape(26.dp)
    val containerColor = MiuixTheme.colorScheme.surface
    val borderColor = MiuixTheme.colorScheme.outline.copy(alpha = 0.28f)
    val iconTint = style.contentColor

    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = horizontalInset)
                .clip(shape)
                .background(color = containerColor, shape = shape)
                .border(width = 0.8.dp, color = borderColor, shape = shape)
                .appClickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OverrideStatusBadge(
            imageVector = imageVector,
            contentDescription = title,
            tint = iconTint,
            backgroundColor = style.iconContainerColor,
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!summary.isNullOrBlank()) {
                Text(
                    text = summary,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (showIndicator) {
            Icon(
                imageVector = MonadIcons.chevron,
                contentDescription = null,
                tint =
                    if (emphasized) {
                        lerp(
                            MiuixTheme.colorScheme.onSurfaceVariantActions,
                            style.contentColor,
                            0.72f,
                        )
                    } else {
                        MiuixTheme.colorScheme.onSurfaceVariantActions
                    },
                modifier = Modifier.rotate(indicatorRotation.value),
            )
        }
    }
}

@Composable
fun OverrideSectionVisibility(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter =
            expandVertically(
                animationSpec = tween(durationMillis = 260),
                expandFrom = Alignment.Top,
            ) + fadeIn(animationSpec = tween(durationMillis = 180)),
        exit =
            shrinkVertically(
                animationSpec = tween(durationMillis = 220),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(animationSpec = tween(durationMillis = 120)),
        label = "override_section_visibility",
    ) {
        content()
    }
}

@Composable
fun OverrideCardActionIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: OverrideActionTone = OverrideActionTone.Neutral,
    enabled: Boolean = true,
) {
    val semanticTone =
        when (tone) {
            OverrideActionTone.Neutral -> SemanticTone.Neutral
            OverrideActionTone.Primary -> SemanticTone.Brand
            OverrideActionTone.Danger -> SemanticTone.Danger
        }

    AppCircularIconAction(
        imageVector = imageVector,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        tone = semanticTone,
        highEmphasis = tone != OverrideActionTone.Neutral,
        size = OverrideActionButtonSize,
        iconSize = OverrideActionIconSize,
        enabled = enabled,
    )
}

@Composable
fun OverrideStatusBadge(
    imageVector: ImageVector,
    contentDescription: String,
    tint: Color,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.78f),
) {
    Box(
        modifier =
            modifier
                .size(OverrideStatusBadgeSize)
                .background(color = backgroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(OverrideStatusBadgeIconSize),
        )
    }
}

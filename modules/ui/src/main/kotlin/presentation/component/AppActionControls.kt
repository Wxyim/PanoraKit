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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Activity
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Chart-column`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Cloud
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Edit
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Folders
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Git-merge`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Github
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Link
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Message
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Meta
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Play
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Rocket
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Scan-eye`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Scroll-text`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Settings-2`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Tun
import com.github.nomadboxlab.monadbox.presentation.icon.monad.UserKey
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Wifi-cog`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.chevron
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.LocalSemanticColors
import com.github.nomadboxlab.monadbox.presentation.theme.SemanticColorToken
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

enum class SemanticTone {
    Brand,
    Success,
    Info,
    Warning,
    Danger,
    Neutral,
}

data class SemanticActionStyle(
    val containerColor: Color,
    val contentColor: Color,
    val iconContainerColor: Color,
    val borderColor: Color,
)

object SemanticActionDefaults {
    @Composable
    fun style(tone: SemanticTone, highEmphasis: Boolean = false): SemanticActionStyle {
        val semanticColors = LocalSemanticColors.current
        val token =
            when (tone) {
                SemanticTone.Brand -> semanticColors.brand
                SemanticTone.Success -> semanticColors.success
                SemanticTone.Info -> semanticColors.info
                SemanticTone.Warning -> semanticColors.warning
                SemanticTone.Danger -> semanticColors.danger
                SemanticTone.Neutral -> semanticColors.neutral
            }

        return token.toActionStyle(highEmphasis)
    }

    private fun SemanticColorToken.toActionStyle(highEmphasis: Boolean): SemanticActionStyle {
        return SemanticActionStyle(
            containerColor = if (highEmphasis) highEmphasisContainer else container,
            contentColor = foreground,
            iconContainerColor = if (highEmphasis) highEmphasisIconContainer else iconContainer,
            borderColor = if (highEmphasis) highEmphasisBorder else border,
        )
    }
}

private object CircularIconActionDefaults {
    val TouchTargetSize = 52.dp
    val IconSize = 22.dp
}

@Composable
fun AppCircularIconAction(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = CircularIconActionDefaults.TouchTargetSize,
    iconSize: Dp = CircularIconActionDefaults.IconSize,
    tone: SemanticTone = SemanticTone.Neutral,
    highEmphasis: Boolean = false,
    chromeTone: SemanticTone = SemanticTone.Neutral,
    chromeHighEmphasis: Boolean = false,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
) {
    val contentStyle = SemanticActionDefaults.style(tone = tone, highEmphasis = highEmphasis)
    val chromeStyle =
        SemanticActionDefaults.style(tone = chromeTone, highEmphasis = chromeHighEmphasis)
    val resolvedContainer =
        if (containerColor == Color.Unspecified) {
            chromeStyle.containerColor
        } else {
            containerColor
        }
    val resolvedContent =
        if (contentColor == Color.Unspecified) {
            contentStyle.contentColor
        } else {
            contentColor
        }
    val resolvedBorder =
        if (borderColor == Color.Unspecified) {
            chromeStyle.borderColor
        } else {
            borderColor
        }
    val resolvedIconTint =
        if (enabled) {
            resolvedContent
        } else {
            resolvedContent.copy(
                alpha = resolvedContent.alpha * AppInteractionFeedbackDefaults.DisabledAlpha
            )
        }
    val toneDescription = tone.accessibilityDescription()

    Box(
        modifier =
            modifier
                .size(size)
                .semantics {
                    this.contentDescription =
                        buildSemanticDescription(contentDescription, toneDescription)
                    stateDescription = if (enabled) "enabled" else "disabled"
                    if (!enabled) disabled()
                }
                .shadow(
                    elevation = if (enabled) 3.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.05f),
                )
                .clip(CircleShape)
                .background(resolvedContainer, CircleShape)
                .border(AppTheme.strokes.default, resolvedBorder, CircleShape)
                .appClickable(
                    enabled = enabled,
                    pressedAlpha = 1f,
                    disabledAlpha = 1f,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = imageVector,
            tint = resolvedIconTint,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun AppActionTile(
    title: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    compact: Boolean = false,
    tone: SemanticTone = SemanticTone.Neutral,
    highEmphasis: Boolean = false,
    minHeight: Dp = if (compact) 78.dp else 68.dp,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    iconContainerColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    val colorScheme = MiuixTheme.colorScheme
    val actionStyle = SemanticActionDefaults.style(tone = tone, highEmphasis = highEmphasis)
    val shape = RoundedCornerShape(if (compact) 22.dp else 24.dp)
    val resolvedContainer =
        if (containerColor == Color.Unspecified) {
            actionStyle.containerColor
        } else {
            containerColor
        }
    val resolvedContent =
        if (contentColor == Color.Unspecified) {
            actionStyle.contentColor
        } else {
            contentColor
        }
    val resolvedIconContainer =
        if (iconContainerColor == Color.Unspecified) {
            actionStyle.iconContainerColor
        } else {
            iconContainerColor
        }
    val resolvedBorder =
        if (borderColor == Color.Unspecified) {
            actionStyle.borderColor
        } else {
            borderColor
        }
    val semanticDescription =
        buildSemanticDescription(title, summary, tone.accessibilityDescription())

    if (compact) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .semantics(mergeDescendants = true) {
                        contentDescription = semanticDescription
                        stateDescription = if (enabled) "enabled" else "disabled"
                        if (!enabled) disabled()
                    }
                    .clip(shape)
                    .background(resolvedContainer, shape)
                    .border(AppTheme.strokes.default, resolvedBorder, shape)
                    .appClickable(enabled = enabled, onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterVertically),
        ) {
            ActionTileIcon(
                imageVector = imageVector,
                contentColor = resolvedContent,
                containerColor = resolvedIconContainer,
                size = 34.dp,
                iconSize = 19.dp,
            )

            Text(
                text = title,
                color = resolvedContent,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .semantics(mergeDescendants = true) {
                        contentDescription = semanticDescription
                        stateDescription = if (enabled) "enabled" else "disabled"
                        if (!enabled) disabled()
                    }
                    .clip(shape)
                    .background(resolvedContainer, shape)
                    .border(AppTheme.strokes.default, resolvedBorder, shape)
                    .appClickable(enabled = enabled, onClick = onClick)
                    .padding(horizontal = 15.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionTileIcon(
                imageVector = imageVector,
                contentColor = resolvedContent,
                containerColor = resolvedIconContainer,
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = resolvedContent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!summary.isNullOrBlank()) {
                    Text(
                        text = summary,
                        color = colorScheme.onSurfaceVariantSummary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = endContent,
            )
        }
    }
}

@Composable
fun AppCommandButton(
    title: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: SemanticTone = SemanticTone.Neutral,
    highEmphasis: Boolean = false,
) {
    val actionStyle = SemanticActionDefaults.style(tone = tone, highEmphasis = highEmphasis)
    val shape = RoundedCornerShape(22.dp)
    val semanticDescription = buildSemanticDescription(title, tone.accessibilityDescription())

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = semanticDescription
                    stateDescription = if (enabled) "enabled" else "disabled"
                    if (!enabled) disabled()
                }
                .clip(shape)
                .background(actionStyle.containerColor, shape)
                .border(AppTheme.strokes.default, actionStyle.borderColor, shape)
                .appClickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActionTileIcon(
            imageVector = imageVector,
            contentColor = actionStyle.contentColor,
            containerColor = actionStyle.iconContainerColor,
            size = 32.dp,
            iconSize = 18.dp,
        )
        Text(
            text = title,
            color = actionStyle.contentColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun StatusBadge(
    text: String,
    tone: SemanticTone,
    modifier: Modifier = Modifier,
    leadingDot: Boolean = false,
    compact: Boolean = false,
) {
    val style = SemanticActionDefaults.style(tone = tone, highEmphasis = false)
    val shape = RoundedCornerShape(999.dp)
    val semanticDescription = buildSemanticDescription(text, tone.accessibilityDescription())

    Row(
        modifier =
            modifier
                .semantics(mergeDescendants = true) {
                    contentDescription = semanticDescription
                    stateDescription = text
                }
                .clip(shape)
                .background(style.containerColor, shape)
                .border(AppTheme.strokes.thin, style.borderColor, shape)
                .padding(
                    horizontal = if (compact) 9.dp else 11.dp,
                    vertical = if (compact) 4.dp else 6.dp,
                ),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingDot) {
            Box(modifier = Modifier.size(7.dp).background(style.contentColor, CircleShape))
        }
        Text(
            text = text,
            color = style.contentColor,
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SettingsRow(
    title: String,
    summary: String?,
    imageVector: ImageVector,
    tone: SemanticTone,
    iconTone: SemanticTone? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    endContent: @Composable RowScope.() -> Unit = {},
) {
    val resolvedIconTone = iconTone ?: imageVector.defaultSettingsIconTone(tone)
    val style = SemanticActionDefaults.style(tone = resolvedIconTone, highEmphasis = true)
    val shape = RoundedCornerShape(22.dp)
    val semanticDescription =
        buildSemanticDescription(title, summary, tone.accessibilityDescription())

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = semanticDescription
                    stateDescription = tone.accessibilityDescription()
                }
                .clip(shape)
                .appClickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActionTileIcon(
            imageVector = imageVector,
            contentColor = style.contentColor,
            containerColor = style.containerColor,
            size = 40.dp,
            iconSize = 21.dp,
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                color = MiuixTheme.colorScheme.onSurface,
                fontSize = 16.sp,
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = endContent,
        )
        Icon(
            imageVector = MonadIcons.chevron,
            contentDescription = null,
            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.72f),
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun SemanticTone.accessibilityDescription(): String =
    when (this) {
        SemanticTone.Brand -> "brand"
        SemanticTone.Success -> "success"
        SemanticTone.Info -> "info"
        SemanticTone.Warning -> "warning"
        SemanticTone.Danger -> "danger"
        SemanticTone.Neutral -> "neutral"
    }

private fun ImageVector.defaultSettingsIconTone(rowTone: SemanticTone): SemanticTone {
    return when (this) {
        MonadIcons.Message,
        MonadIcons.Activity,
        MonadIcons.`Git-merge` -> SemanticTone.Warning

        MonadIcons.Link,
        MonadIcons.`Scan-eye`,
        MonadIcons.Meta,
        MonadIcons.`Scroll-text`,
        MonadIcons.Github,
        MonadIcons.UserKey -> SemanticTone.Brand

        MonadIcons.Cloud,
        MonadIcons.Rocket,
        MonadIcons.Tun,
        MonadIcons.Play -> SemanticTone.Success

        MonadIcons.`Settings-2`,
        MonadIcons.`Wifi-cog`,
        MonadIcons.`Chart-column`,
        MonadIcons.Edit,
        MonadIcons.Folders -> SemanticTone.Info

        else ->
            when (rowTone) {
                SemanticTone.Neutral -> SemanticTone.Brand
                else -> rowTone
            }
    }
}

private fun buildSemanticDescription(vararg parts: String?): String {
    return parts
        .mapNotNull { part -> part?.trim()?.takeIf(String::isNotBlank) }
        .distinct()
        .joinToString(separator = ", ")
}

@Composable
private fun ActionTileIcon(
    imageVector: ImageVector,
    contentColor: Color,
    containerColor: Color,
    size: Dp = 38.dp,
    iconSize: Dp = 20.dp,
) {
    Box(
        modifier =
            Modifier.size(size)
                .clip(CircleShape)
                .background(containerColor, CircleShape)
                .border(AppTheme.strokes.default, contentColor.copy(alpha = 0.18f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = imageVector,
            tint = contentColor,
            contentDescription = null,
        )
    }
}

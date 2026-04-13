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

package com.github.yumelira.yumebox.presentation.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.`Circle-fading-arrow-up`
import com.github.yumelira.yumebox.presentation.icon.yume.Delete
import com.github.yumelira.yumebox.presentation.icon.yume.Edit
import com.github.yumelira.yumebox.presentation.icon.yume.`Settings-2`
import com.github.yumelira.yumebox.presentation.icon.yume.Share
import com.github.yumelira.yumebox.presentation.theme.DefaultRadii
import com.github.yumelira.yumebox.presentation.theme.DefaultSpacing
import com.github.yumelira.yumebox.presentation.util.*
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import com.github.yumelira.yumebox.service.runtime.entity.toProductProfileObject
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object ProfileCardMetrics {
    val OuterBottomPadding = DefaultSpacing.sm
    val InnerPadding = DefaultSpacing.lg
    val HeaderSpacing = DefaultSpacing.sm
    val TrailingActionSpacing = 10.dp
    val TrailingColumnSpacing = 12.dp
    val TrailingColumnStartPadding = DefaultSpacing.sm
    val HeaderEndPaddingTwoActions = 126.dp
    val HeaderEndPaddingThreeActions = 188.dp
    val InfoEndPadding = 18.dp
    val ProviderTopPadding = 2.dp
    val ContentTopPadding = 10.dp
    val ActiveContentStartPadding = DefaultSpacing.md
    val ActiveRailWidth = DefaultSpacing.xs
    val ActiveRailHeight = 48.dp
    val ActiveRailCornerRadius = DefaultRadii.pill
    val SmallActionSize = 52.dp
    val ActionIconSize = 22.dp
    val ActionLabelFontSize = 12.sp
    val TitleFontSize = 17.sp
    val ProviderFontSize = 12.sp
    val InfoFontSize = 14.sp
    val InfoTrailingFontSize = 12.sp
    val SwipeRailWidth = 196.dp
    val SwipeActionHeight = 96.dp
    val SwipeActionCornerRadius = 26.dp
    val SwipeActionSpacing = 10.dp
    val SwipeActionEndPadding = DefaultSpacing.md
    val CardBorderRadius = 28.dp
}

@Composable
fun ProfileCard(
    profile: Profile,
    workDir: File,
    isSelected: Boolean,
    isDownloading: Boolean = false,
    playSwipeHint: Boolean = false,
    onSwipeHintConsumed: () -> Unit = {},
    onSelect: (Profile) -> Unit,
    onExport: (Profile) -> Unit,
    onUpdate: (Profile) -> Unit,
    onDelete: (Profile) -> Unit,
    onEdit: (Profile) -> Unit,
    onMoreActions: (Profile) -> Unit,
    onOverrideSettings: ((Profile) -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier,
) {
    val colorScheme = MiuixTheme.colorScheme
    val activeStyle = SemanticActionDefaults.style(SemanticTone.Success, highEmphasis = true)
    val isConfigSaved = remember(profile.uuid, profile.updatedAt) { profile.isConfigSaved(workDir) }
    val profileObject =
        remember(profile, isConfigSaved) { profile.toProductProfileObject(isConfigSaved) }
    val infoText = remember(profile) { profile.getInfoText() }
    val profileSemanticDescription =
        remember(profileObject, infoText, isSelected, isDownloading) {
            listOfNotNull(
                    profileObject.displayName,
                    profileObject.owner.label,
                    profileObject.lifecycleState.name.lowercase(),
                    profileObject.effectiveRelation.name.lowercase(),
                    profileObject.riskLevel.name.lowercase(),
                    infoText.replace('\n', ' '),
                    if (isSelected) MLang.Proxy.Selection.Current else null,
                    if (!profileObject.configSaved) "not exportable" else null,
                    if (isDownloading) "downloading" else null,
                )
                .map(String::trim)
                .filter(String::isNotBlank)
                .distinct()
                .joinToString(separator = ", ")
        }
    val showUpdateButton = profile.shouldShowUpdateButton()
    val headerEndPadding =
        if (showUpdateButton) {
            ProfileCardMetrics.HeaderEndPaddingThreeActions
        } else {
            ProfileCardMetrics.HeaderEndPaddingTwoActions
        }
    val revealWidthPx =
        with(LocalDensity.current) {
            (ProfileCardMetrics.SwipeRailWidth + ProfileCardMetrics.SwipeActionEndPadding * 2)
                .toPx()
        }
    var swipeOffsetPx by remember(profile.uuid) { mutableFloatStateOf(0f) }
    val animatedOffsetPx by
        animateFloatAsState(targetValue = swipeOffsetPx, label = "profile_card_swipe")
    val revealProgress = (-animatedOffsetPx / revealWidthPx).coerceIn(0f, 1f)
    val actionsInteractable = revealProgress > 0.06f && !isDownloading
    val selectionProgress by
        animateFloatAsState(
            targetValue = if (isSelected) 1f else 0f,
            label = "profile_card_selection",
        )
    val selectionShape = RoundedCornerShape(ProfileCardMetrics.CardBorderRadius)
    val selectionShadowElevation = (10f * selectionProgress).dp

    fun closeActions() {
        swipeOffsetPx = 0f
    }

    fun settleActions(velocity: Float) {
        val shouldOpen = swipeOffsetPx < -revealWidthPx * 0.35f || velocity < -1200f
        swipeOffsetPx = if (shouldOpen) -revealWidthPx else 0f
    }

    LaunchedEffect(playSwipeHint, isDownloading, revealWidthPx) {
        if (!playSwipeHint || isDownloading) return@LaunchedEffect

        delay(240)
        swipeOffsetPx = -revealWidthPx * 0.26f
        delay(320)
        swipeOffsetPx = 0f
        delay(200)
        onSwipeHintConsumed()
    }

    val swipeNestedScrollConnection =
        remember(isDownloading) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (isDownloading || source != NestedScrollSource.UserInput) return Offset.Zero
                    if (abs(available.x) <= abs(available.y)) return Offset.Zero
                    return Offset(available.x, 0f)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (isDownloading) return Velocity.Zero
                    if (abs(available.x) <= abs(available.y)) return Velocity.Zero
                    return Velocity(available.x, 0f)
                }
            }
        }

    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = ProfileCardMetrics.OuterBottomPadding)
    ) {
        Box(
            modifier = Modifier.matchParentSize().zIndex(0f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Row(
                modifier =
                    Modifier.width(ProfileCardMetrics.SwipeRailWidth)
                        .padding(end = ProfileCardMetrics.SwipeActionEndPadding)
                        .alpha(revealProgress.coerceIn(0f, 1f)),
                horizontalArrangement = Arrangement.spacedBy(ProfileCardMetrics.SwipeActionSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileSwipeAction(
                    modifier = Modifier.weight(1f),
                    label = MLang.Component.ProfileCard.Export,
                    icon = Yume.Share,
                    enabled = actionsInteractable && isConfigSaved,
                    tone = SemanticTone.Info,
                    onClick = {
                        closeActions()
                        onExport(profile)
                    },
                )
                ProfileSwipeAction(
                    modifier = Modifier.weight(1f),
                    label = MLang.Component.ProfileCard.Delete,
                    icon = Yume.Delete,
                    enabled = actionsInteractable,
                    tone = SemanticTone.Danger,
                    onClick = {
                        closeActions()
                        onDelete(profile)
                    },
                )
            }
        }

        Card(
            modifier =
                modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .offset { IntOffset(animatedOffsetPx.roundToInt(), 0) }
                    .shadow(
                        elevation = selectionShadowElevation,
                        shape = selectionShape,
                        ambientColor =
                            activeStyle.contentColor.copy(alpha = 0.08f * selectionProgress),
                        spotColor = activeStyle.borderColor.copy(alpha = 0.12f * selectionProgress),
                    )
                    .border(
                        width = if (isSelected) 0.8.dp else 0.dp,
                        color =
                            if (isSelected) {
                                activeStyle.borderColor.copy(alpha = 0.62f)
                            } else {
                                Color.Transparent
                            },
                        shape = selectionShape,
                    )
                    .nestedScroll(swipeNestedScrollConnection),
            insideMargin = PaddingValues(ProfileCardMetrics.InnerPadding),
            applyHorizontalPadding = false,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (isSelected) {
                    ProfileActiveRail(
                        modifier = Modifier.align(Alignment.CenterStart),
                        color = activeStyle.contentColor,
                    )
                }

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .then(dragHandleModifier)
                            .semantics(mergeDescendants = true) {
                                contentDescription = profileSemanticDescription
                                stateDescription =
                                    if (isSelected) {
                                        MLang.Proxy.Selection.Current
                                    } else {
                                        ""
                                    }
                                selected = isSelected
                            }
                            .draggable(
                                enabled = !isDownloading,
                                orientation = Orientation.Horizontal,
                                state =
                                    rememberDraggableState { delta ->
                                        swipeOffsetPx =
                                            (swipeOffsetPx + delta).coerceIn(-revealWidthPx, 0f)
                                    },
                                onDragStopped = { velocity -> settleActions(velocity) },
                            )
                            .appClickable(enabled = !isDownloading) {
                                if (swipeOffsetPx < -1f) {
                                    closeActions()
                                } else {
                                    onSelect(profile)
                                }
                            }
                            .padding(
                                start =
                                    if (isSelected) {
                                        ProfileCardMetrics.ActiveContentStartPadding
                                    } else {
                                        0.dp
                                    }
                            )
                ) {
                    Column(modifier = Modifier.padding(end = headerEndPadding)) {
                        Text(
                            text = profile.name,
                            fontSize = ProfileCardMetrics.TitleFontSize,
                            fontWeight = FontWeight(550),
                            color = colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Row(
                            modifier =
                                Modifier.padding(top = ProfileCardMetrics.ProviderTopPadding),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = profile.getDisplayProvider(),
                                fontSize = ProfileCardMetrics.ProviderFontSize,
                                fontWeight = FontWeight(550),
                                color = colorScheme.onSurfaceVariantSummary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )

                            if (isSelected) {
                                StatusBadge(
                                    text = MLang.Proxy.Selection.Current,
                                    tone = SemanticTone.Success,
                                    leadingDot = true,
                                    compact = true,
                                )
                            }
                        }
                    }

                    Column(
                        modifier =
                            Modifier.padding(
                                top = ProfileCardMetrics.ContentTopPadding,
                                end = ProfileCardMetrics.InfoEndPadding,
                            )
                    ) {
                        val lines = infoText.split('\n')

                        lines.forEachIndexed { _, line ->
                            when {
                                line.contains('|') -> {
                                    val parts = line.split('|')
                                    val expireText = parts.getOrNull(0) ?: ""
                                    val timeText = parts.getOrNull(1) ?: ""

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = expireText,
                                            fontSize = ProfileCardMetrics.InfoFontSize,
                                            color = colorScheme.onSurfaceVariantSummary,
                                            lineHeight = 20.sp,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f),
                                        )

                                        if (timeText.isNotEmpty()) {
                                            Text(
                                                text = timeText,
                                                fontSize = ProfileCardMetrics.InfoTrailingFontSize,
                                                color =
                                                    colorScheme.onTertiaryContainer.copy(
                                                        alpha = 0.8f
                                                    ),
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                modifier = Modifier.padding(start = 12.dp),
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    Text(
                                        text = line,
                                        fontSize = ProfileCardMetrics.InfoFontSize,
                                        color = colorScheme.onSurfaceVariantSummary,
                                        lineHeight = 20.sp,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }

                ProfileActionCluster(
                    modifier = Modifier.align(Alignment.TopEnd).zIndex(1f),
                    showUpdateButton = showUpdateButton,
                    enabled = !isDownloading,
                    onEdit = {
                        closeActions()
                        onEdit(profile)
                    },
                    onUpdate = {
                        closeActions()
                        onUpdate(profile)
                    },
                    onMoreActions = {
                        closeActions()
                        onMoreActions(profile)
                    },
                )
            }
        }
    }
}

@Composable
private fun ProfileActiveRail(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier =
            modifier
                .width(ProfileCardMetrics.ActiveRailWidth)
                .height(ProfileCardMetrics.ActiveRailHeight)
                .clip(RoundedCornerShape(ProfileCardMetrics.ActiveRailCornerRadius))
                .background(color.copy(alpha = 0.82f))
    )
}

@Composable
private fun ProfileActionCluster(
    modifier: Modifier = Modifier,
    showUpdateButton: Boolean,
    enabled: Boolean,
    onEdit: () -> Unit,
    onUpdate: () -> Unit,
    onMoreActions: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ProfileCardMetrics.TrailingActionSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showUpdateButton) {
            AppCircularIconAction(
                imageVector = Yume.`Circle-fading-arrow-up`,
                contentDescription = MLang.Component.ProfileCard.Update,
                tone = SemanticTone.Info,
                highEmphasis = true,
                size = ProfileCardMetrics.SmallActionSize,
                iconSize = ProfileCardMetrics.ActionIconSize,
                enabled = enabled,
                onClick = {
                    if (enabled) {
                        onUpdate()
                    }
                },
            )
        }

        AppCircularIconAction(
            imageVector = Yume.Edit,
            contentDescription = MLang.Component.ProfileCard.Edit,
            tone = SemanticTone.Neutral,
            size = ProfileCardMetrics.SmallActionSize,
            iconSize = ProfileCardMetrics.ActionIconSize,
            enabled = enabled,
            onClick = {
                if (enabled) {
                    onEdit()
                }
            },
        )

        AppCircularIconAction(
            imageVector = Yume.`Settings-2`,
            contentDescription = MLang.Settings.Section.More,
            tone = SemanticTone.Neutral,
            size = ProfileCardMetrics.SmallActionSize,
            iconSize = ProfileCardMetrics.ActionIconSize,
            enabled = enabled,
            onClick = {
                if (enabled) {
                    onMoreActions()
                }
            },
        )
    }
}

@Composable
private fun ProfileSwipeAction(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    tone: SemanticTone,
    onClick: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f
    val shape = RoundedCornerShape(ProfileCardMetrics.SwipeActionCornerRadius)
    val style = SemanticActionDefaults.style(tone, highEmphasis = true)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(style.containerColor.copy(alpha = if (enabled) 1f else 0.55f), shape)
                .border(
                    width = 0.8.dp,
                    color = style.borderColor.copy(alpha = if (enabled) 1f else 0.55f),
                    shape = shape,
                )
                .appClickable(enabled = enabled, onClick = onClick)
                .height(ProfileCardMetrics.SwipeActionHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            Box(
                modifier =
                    Modifier.size(34.dp)
                        .clip(CircleShape)
                        .background(
                            style.iconContainerColor.copy(alpha = if (enabled) 1f else 0.5f),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(ProfileCardMetrics.ActionIconSize),
                    imageVector = icon,
                    tint = style.contentColor.copy(alpha = alpha),
                    contentDescription = label,
                )
            }

            Text(
                text = label,
                color = style.contentColor.copy(alpha = alpha),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                maxLines = 1,
            )
        }
    }
}

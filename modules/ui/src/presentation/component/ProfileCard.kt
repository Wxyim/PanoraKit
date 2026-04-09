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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
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
import com.github.yumelira.yumebox.presentation.icon.yume.Share
import com.github.yumelira.yumebox.presentation.theme.horizontalPadding
import com.github.yumelira.yumebox.presentation.util.*
import com.github.yumelira.yumebox.service.runtime.entity.Profile
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private object ProfileCardMetrics {
    val OuterBottomPadding = 12.dp
    val InnerPadding = 16.dp
    val HeaderSpacing = 8.dp
    val HeaderTrailingSpacing = 10.dp
    val ProviderTopPadding = 2.dp
    val ContentTopPadding = 8.dp
    val SmallActionSize = 48.dp
    val ActionIconSize = 20.dp
    val ActionLabelFontSize = 12.sp
    val TitleFontSize = 17.sp
    val ProviderFontSize = 12.sp
    val InfoFontSize = 14.sp
    val InfoTrailingFontSize = 12.sp
    val SwipeActionWidth = 72.dp
    val SwipeActionSpacing = 8.dp
    val SwipeActionEndPadding = 12.dp
    val SelectionDotSize = 10.dp
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
    onOverrideSettings: ((Profile) -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val colorScheme = MiuixTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    val secondaryContainer = colorScheme.secondaryContainer.copy(alpha = 0.8f)
    val actionIconTint =
        remember(isDark) { colorScheme.onSurface.copy(alpha = if (isDark) 0.7f else 0.9f) }
    val deleteContainer = colorScheme.errorContainer.copy(alpha = 0.92f)
    val deleteTint = colorScheme.onErrorContainer
    val updateBg = remember(colorScheme) { colorScheme.primary.copy(alpha = 0.1f) }
    val updateTint = remember(colorScheme) { colorScheme.primary }
    val isConfigSaved = remember(profile.uuid, profile.updatedAt) { profile.isConfigSaved(workDir) }
    val infoText = remember(profile) { profile.getInfoText() }
    val revealWidthPx =
        with(LocalDensity.current) {
            (
                ProfileCardMetrics.SwipeActionWidth * 3 +
                    ProfileCardMetrics.SwipeActionSpacing * 2 +
                    ProfileCardMetrics.SwipeActionEndPadding * 2
            )
                .toPx()
        }
    val interactionSource = remember { MutableInteractionSource() }

    var swipeOffsetPx by remember(profile.uuid) { mutableFloatStateOf(0f) }
    val animatedOffsetPx by animateFloatAsState(targetValue = swipeOffsetPx, label = "profile_card_swipe")
    val revealProgress = (-animatedOffsetPx / revealWidthPx).coerceIn(0f, 1f)
    val affordanceAlpha = ((1f - revealProgress * 1.6f).coerceIn(0f, 1f)) * 0.7f
    val actionsInteractable = revealProgress > 0.06f && !isDownloading

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
        swipeOffsetPx = -revealWidthPx * 0.2f
        delay(260)
        swipeOffsetPx = 0f
        delay(220)
        onSwipeHintConsumed()
    }

    val swipeNestedScrollConnection =
        remember(isDownloading) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
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
        modifier = Modifier.fillMaxWidth().horizontalPadding().padding(bottom = ProfileCardMetrics.OuterBottomPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().zIndex(1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Row(
                modifier =
                    Modifier.padding(end = ProfileCardMetrics.SwipeActionEndPadding)
                        .alpha(revealProgress.coerceIn(0f, 1f)),
                horizontalArrangement = Arrangement.spacedBy(ProfileCardMetrics.SwipeActionSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileSwipeAction(
                    label = MLang.Component.ProfileCard.Export,
                    icon = Yume.Share,
                    enabled = actionsInteractable && isConfigSaved,
                    backgroundColor = secondaryContainer,
                    tint = actionIconTint,
                    onClick = {
                        closeActions()
                        onExport(profile)
                    },
                )
                ProfileSwipeAction(
                    label = MLang.Component.ProfileCard.Edit,
                    icon = Yume.Edit,
                    enabled = actionsInteractable,
                    backgroundColor = secondaryContainer,
                    tint = actionIconTint,
                    onClick = {
                        closeActions()
                        onEdit(profile)
                    },
                )
                ProfileSwipeAction(
                    label = MLang.Component.ProfileCard.Delete,
                    icon = Yume.Delete,
                    enabled = actionsInteractable,
                    backgroundColor = deleteContainer,
                    tint = deleteTint,
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
                    .zIndex(0f)
                    .nestedScroll(swipeNestedScrollConnection)
                    .offset { IntOffset(animatedOffsetPx.roundToInt(), 0) }
                    .draggable(
                        enabled = !isDownloading,
                        orientation = Orientation.Horizontal,
                        state =
                            rememberDraggableState { delta ->
                                swipeOffsetPx = (swipeOffsetPx + delta).coerceIn(-revealWidthPx, 0f)
                            },
                        onDragStopped = { velocity -> settleActions(velocity) },
                    )
                    .clickable(
                        enabled = !isDownloading,
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        if (swipeOffsetPx < -1f) {
                            closeActions()
                        } else {
                            onSelect(profile)
                        }
                    },
            insideMargin = PaddingValues(ProfileCardMetrics.InnerPadding),
            applyHorizontalPadding = false,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ProfileCardMetrics.HeaderSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                            Text(
                                text = profile.name,
                                fontSize = ProfileCardMetrics.TitleFontSize,
                                fontWeight = FontWeight(550),
                                color = colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = profile.getDisplayProvider(),
                                fontSize = ProfileCardMetrics.ProviderFontSize,
                                modifier = Modifier.padding(top = ProfileCardMetrics.ProviderTopPadding),
                                fontWeight = FontWeight(550),
                                color = colorScheme.onSurfaceVariantSummary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ProfileCardMetrics.HeaderTrailingSpacing),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (profile.shouldShowUpdateButton()) {
                                IconButton(
                                    backgroundColor = updateBg,
                                    minHeight = ProfileCardMetrics.SmallActionSize,
                                    minWidth = ProfileCardMetrics.SmallActionSize,
                                    enabled = !isDownloading,
                                    onClick = { if (!isDownloading) onUpdate(profile) },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(ProfileCardMetrics.ActionIconSize),
                                        imageVector = Yume.`Circle-fading-arrow-up`,
                                        tint = updateTint,
                                        contentDescription = MLang.Component.ProfileCard.Update,
                                    )
                                }
                            }

                            Box(
                                modifier =
                                    Modifier.size(ProfileCardMetrics.SelectionDotSize)
                                        .background(
                                            color =
                                                if (isSelected) colorScheme.primary
                                                else colorScheme.outline.copy(alpha = 0.35f),
                                            shape = CircleShape,
                                        )
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(top = ProfileCardMetrics.ContentTopPadding)) {
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
                                                color = colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                modifier = Modifier.padding(end = 13.dp),
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

                ProfileSwipeAffordance(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    tint = colorScheme.onSurfaceVariantSummary,
                    alpha = affordanceAlpha,
                )
            }
        }
    }
}

@Composable
private fun ProfileSwipeAffordance(modifier: Modifier = Modifier, tint: Color, alpha: Float) {
    if (alpha <= 0.01f) return

    Box(modifier = modifier.width(18.dp).height(34.dp).alpha(alpha), contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier.width(1.dp).height(30.dp)
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, tint.copy(alpha = 0.45f), Color.Transparent)
                            ),
                        shape = CircleShape,
                    )
        )

        Column(
            modifier = Modifier.padding(start = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            repeat(3) {
                Box(
                    modifier =
                        Modifier.size(2.dp)
                            .background(color = tint.copy(alpha = 0.38f), shape = CircleShape)
                )
            }
        }
    }
}

@Composable
private fun ProfileSwipeAction(
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    backgroundColor: Color,
    tint: Color,
    onClick: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f

    Column(
        modifier = Modifier.width(ProfileCardMetrics.SwipeActionWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(
            backgroundColor = backgroundColor,
            minHeight = ProfileCardMetrics.SmallActionSize,
            minWidth = ProfileCardMetrics.SmallActionSize,
            enabled = enabled,
            onClick = { if (enabled) onClick() },
        ) {
            Icon(
                modifier = Modifier.size(ProfileCardMetrics.ActionIconSize).alpha(alpha),
                imageVector = icon,
                tint = tint.copy(alpha = alpha),
                contentDescription = label,
            )
        }

        Text(
            modifier = Modifier.padding(top = 6.dp).alpha(alpha),
            text = label,
            color = tint.copy(alpha = alpha),
            fontWeight = FontWeight.Medium,
            fontSize = ProfileCardMetrics.ActionLabelFontSize,
            maxLines = 1,
        )
    }
}

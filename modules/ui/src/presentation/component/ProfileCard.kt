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

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Circle-fading-arrow-up`
import com.github.nomadboxlab.monadbox.presentation.icon.monad.Edit
import com.github.nomadboxlab.monadbox.presentation.icon.monad.`Settings-2`
import com.github.nomadboxlab.monadbox.presentation.theme.DefaultRadii
import com.github.nomadboxlab.monadbox.presentation.theme.DefaultSpacing
import com.github.nomadboxlab.monadbox.presentation.util.*
import com.github.nomadboxlab.monadbox.service.runtime.entity.Profile
import com.github.nomadboxlab.monadbox.service.runtime.entity.toProductProfileObject
import dev.oom_wg.purejoy.mlang.MLang
import java.io.File
import top.yukonga.miuix.kmp.basic.Card
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
    val FooterTopPadding = 8.dp
    val FooterSpacing = 10.dp
    val FooterDateTopPadding = 6.dp
    val ActiveContentStartPadding = DefaultSpacing.md
    val ActiveRailWidth = DefaultSpacing.xs
    val ActiveRailHeight = 48.dp
    val ActiveRailCornerRadius = DefaultRadii.pill
    val SmallActionSize = 52.dp
    val ActionIconSize = 22.dp
    val TitleFontSize = 17.sp
    val ProviderFontSize = 12.sp
    val InfoFontSize = 14.sp
    val InfoTrailingFontSize = 12.sp
    val CardBorderRadius = 28.dp
}

enum class ProfileUpdateFeedbackState {
    Updating,
    Success,
    Failure,
}

data class ProfileUpdateFeedbackUi(
    val state: ProfileUpdateFeedbackState,
    val label: String,
    val timestampMillis: Long? = null,
)

@Composable
fun ProfileCard(
    profile: Profile,
    workDir: File,
    isSelected: Boolean,
    isDownloading: Boolean = false,
    updateFeedback: ProfileUpdateFeedbackUi? = null,
    onSelect: (Profile) -> Unit,
    onUpdate: (Profile) -> Unit,
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
    val footerTimestampText =
        remember(profile.lastUpdatedAt, updateFeedback) {
            when {
                updateFeedback?.timestampMillis != null ->
                    formatProfileTimestamp(updateFeedback.timestampMillis)
                updateFeedback?.state == ProfileUpdateFeedbackState.Failure -> null
                updateFeedback?.state == ProfileUpdateFeedbackState.Updating -> null
                else -> profile.lastUpdatedAt?.let(::formatProfileTimestamp)
            }
        }
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
    val feedbackOverlayInfoEndPadding =
        if (profile.type == Profile.Type.Url && updateFeedback != null) {
            (headerEndPadding - ProfileCardMetrics.InfoEndPadding).coerceAtLeast(0.dp)
        } else {
            0.dp
        }
    val selectionShape = RoundedCornerShape(ProfileCardMetrics.CardBorderRadius)

    Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = ProfileCardMetrics.OuterBottomPadding)
    ) {
        Card(
            modifier =
                modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 0.8.dp else 0.dp,
                        color =
                            if (isSelected) {
                                activeStyle.borderColor.copy(alpha = 0.62f)
                            } else {
                                Color.Transparent
                            },
                        shape = selectionShape,
                    ),
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
                            .appClickable(
                                enabled = !isDownloading,
                                pressedAlpha = 1f,
                                useIndication = false,
                            ) {
                                onSelect(profile)
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
                        val showFooterTimestamp =
                            profile.type == Profile.Type.Url && footerTimestampText != null

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(end = feedbackOverlayInfoEndPadding),
                                horizontalArrangement =
                                    Arrangement.spacedBy(ProfileCardMetrics.FooterSpacing),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                ProfileInfoTextBlock(lines = lines, modifier = Modifier.weight(1f))

                                if (showFooterTimestamp) {
                                    ProfileUpdateTimestampFooter(
                                        timestampText = footerTimestampText.orEmpty()
                                    )
                                }
                            }

                            if (profile.type == Profile.Type.Url && updateFeedback != null) {
                                ProfileUpdateFeedbackBadge(
                                    feedback = updateFeedback,
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                )
                            }
                        }
                    }
                }

                ProfileActionCluster(
                    modifier = Modifier.align(Alignment.TopEnd),
                    showUpdateButton = showUpdateButton,
                    enabled = !isDownloading,
                    onEdit = { onEdit(profile) },
                    onUpdate = { onUpdate(profile) },
                    onMoreActions = { onMoreActions(profile) },
                )
            }
        }
    }
}

@Composable
private fun ProfileUpdateFeedbackBadge(
    feedback: ProfileUpdateFeedbackUi,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        StatusBadge(
            text = feedback.label,
            tone =
                when (feedback.state) {
                    ProfileUpdateFeedbackState.Updating -> SemanticTone.Info
                    ProfileUpdateFeedbackState.Success -> SemanticTone.Success
                    ProfileUpdateFeedbackState.Failure -> SemanticTone.Danger
                },
            compact = true,
        )
    }
}

@Composable
private fun ProfileInfoTextBlock(lines: List<String>, modifier: Modifier = Modifier) {
    val colorScheme = MiuixTheme.colorScheme

    Column(modifier = modifier) {
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

@Composable
private fun ProfileUpdateTimestampFooter(timestampText: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        Text(
            text = timestampText,
            fontSize = ProfileCardMetrics.InfoTrailingFontSize,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
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
                imageVector = MonadIcons.`Circle-fading-arrow-up`,
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
            imageVector = MonadIcons.Edit,
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
            imageVector = MonadIcons.`Settings-2`,
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

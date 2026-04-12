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

package com.github.yumelira.yumebox.presentation.screen.node

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.AsyncImage as SketchAsyncImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.IntColorDrawableStateImage
import com.github.yumelira.yumebox.core.model.Proxy
import com.github.yumelira.yumebox.presentation.icon.Yume
import com.github.yumelira.yumebox.presentation.icon.yume.ArrowRight
import com.github.yumelira.yumebox.presentation.icon.yume.BadgeDollarSign
import com.github.yumelira.yumebox.presentation.icon.yume.Cancel
import com.github.yumelira.yumebox.presentation.icon.yume.CircleGauge
import com.github.yumelira.yumebox.presentation.icon.yume.Rocket
import com.github.yumelira.yumebox.presentation.icon.yume.ShieldMinus
import com.github.yumelira.yumebox.presentation.icon.yume.Speed
import com.github.yumelira.yumebox.presentation.util.extractFlaggedName
import com.github.yumelira.yumebox.presentation.util.extractNodeTags
import dev.oom_wg.purejoy.mlang.MLang
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SinkFeedback
import top.yukonga.miuix.kmp.utils.pressable

internal object NodeCardDefaults {
    val CornerRadius = 24.dp
    val PaddingHorizontal = 16.dp
    val PaddingVertical = 16.dp
    val TextSpacing = 8.dp
    val LeadingContainerSize = 44.dp
    val LeadingContainerCornerRadius = 14.dp
    val LargeFlagSize = 28.dp
    val InlineFlagSize = 20.dp
    val ActionIconSize = 16.dp
    val ChevronIconSize = 18.dp
    val ChipFontSize = 10.sp
    val ChipHorizontalPadding = 7.dp
    val ChipVerticalPadding = 2.dp
    val MultiplierIconSize = 9.dp
}

private data class BuiltInNodeVisual(
    val icon: ImageVector,
    val iconTint: Color,
    val containerColor: Color,
)

@Composable
internal fun RotatingCircleGauge(
    isRotating: Boolean,
    modifier: Modifier = Modifier,
    tint: Color = MiuixTheme.colorScheme.primary,
    contentDescription: String? = MLang.Proxy.Action.Test,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "circle_gauge_rotation")
    val rotation by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(animation = tween(durationMillis = 1000, easing = LinearEasing)),
            label = "circle_gauge_rotation_value",
        )

    Icon(
        imageVector = Yume.CircleGauge,
        contentDescription = contentDescription,
        tint = tint,
        modifier = if (isRotating) modifier.rotate(rotation) else modifier,
    )
}

@Composable
internal fun NodeSelectableCard(
    isSelected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    paddingVertical: Dp = NodeCardDefaults.PaddingVertical,
    interactionRole: Role = Role.RadioButton,
    onClickLabel: String? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(NodeCardDefaults.CornerRadius)
    val primary = MiuixTheme.colorScheme.primary
    val backgroundColor = MiuixTheme.colorScheme.background
    val borderColor = if (isSelected) primary.copy(alpha = 0.38f) else Color.Transparent

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .let {
                    if (onClick != null)
                        it.pressable(
                            interactionSource = interactionSource,
                            indication = SinkFeedback(),
                        )
                    else it
                }
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .let {
                    when {
                        onClick == null -> it
                        interactionRole == Role.RadioButton ->
                            it.selectable(
                                selected = isSelected,
                                role = interactionRole,
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onClick,
                            )
                        else ->
                            it.clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                role = interactionRole,
                                onClickLabel = onClickLabel,
                                onClick = onClick,
                            )
                    }
                }
                .padding(
                    horizontal = NodeCardDefaults.PaddingHorizontal,
                    vertical = paddingVertical,
                ),
        content = content,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun NodeCard(
    proxy: Proxy,
    isSelected: Boolean,
    onClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
    isDelayTesting: Boolean = false,
    isThisProxyTesting: Boolean = false,
    onSingleNodeTestClick: (() -> Unit)? = null,
    showCountryFlag: Boolean = true,
    singleNodeTestEnabled: Boolean = true,
    interactionRole: Role = Role.RadioButton,
    onClickLabel: String? = null,
    actionChipLabel: String? = null,
) {
    val onCardClick =
        remember(proxy.name, onClick) { onClick?.let { click -> { click(proxy.name) } } }

    NodeSelectableCard(
        isSelected = isSelected,
        onClick = onCardClick,
        modifier = modifier,
        paddingVertical = 12.dp,
        interactionRole = interactionRole,
        onClickLabel = onClickLabel,
    ) {
        val flagged = remember(proxy.name) { extractFlaggedName(proxy.name) }
        val tags = remember(proxy.name) { extractNodeTags(proxy.name) }
        val latencyVisual = proxyLatencyVisual(delay = proxy.delay, isTesting = isThisProxyTesting)
        val primary = MiuixTheme.colorScheme.primary
        val iconUri =
            remember(proxy.icon) {
                proxy.icon?.trim()?.takeIf { it.isNotEmpty() }?.let(::normalizeNodeIconUri)
            }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NodeLargeIcon(
                countryCode = flagged.countryCode.takeIf { showCountryFlag },
                proxyName = proxy.name,
                typeName = proxy.type.name,
                iconUri = iconUri,
                isSelected = isSelected,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = flagged.displayName,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        actionChipLabel?.let { label ->
                            NodeTagChip(
                                label = label,
                                textColor = primary,
                                backgroundColor = primary.copy(alpha = 0.16f),
                            )
                        }
                        NodeTagChip(label = proxy.type.name)
                        tags.keywords.forEach { kw -> NodeTagChip(label = kw) }
                        tags.multiplier?.let { m -> if (m > 0f) NodeMultiplierChip(multiplier = m) }
                    }
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = latencyVisual.label,
                            style = MiuixTheme.textStyles.footnote1,
                            color = latencyVisual.color,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        if (onSingleNodeTestClick != null && singleNodeTestEnabled) {
                            if (isThisProxyTesting) {
                                RotatingCircleGauge(
                                    isRotating = true,
                                    modifier = Modifier.size(NodeCardDefaults.ActionIconSize),
                                    tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                            } else {
                                Icon(
                                    imageVector = Yume.Speed,
                                    contentDescription = MLang.Proxy.Action.TestDelay,
                                    tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    modifier =
                                        Modifier.size(NodeCardDefaults.ActionIconSize)
                                            .clickable(
                                                interactionSource =
                                                    remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = onSingleNodeTestClick,
                                            ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun NodeLargeIcon(
    countryCode: String?,
    proxyName: String,
    typeName: String,
    iconUri: String?,
    isSelected: Boolean,
) {
    val neutral = MiuixTheme.colorScheme.onSurface
    val builtInVisual =
        remember(proxyName, typeName) {
            resolveBuiltInNodeVisual(proxyName = proxyName, typeName = typeName)
        }
    Box(
        modifier =
            Modifier.size(NodeCardDefaults.LeadingContainerSize)
                .clip(RoundedCornerShape(NodeCardDefaults.LeadingContainerCornerRadius))
                .background(
                    (builtInVisual?.containerColor ?: neutral).copy(
                        alpha = if (builtInVisual != null) 0.12f else 0.06f
                    )
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (iconUri != null) {
            RemoteNodeIcon(
                iconUri = iconUri,
                modifier = Modifier.size(NodeCardDefaults.LargeFlagSize),
            )
        } else if (countryCode != null) {
            CountryFlagFilledIcon(
                countryCode = countryCode,
                size = NodeCardDefaults.LargeFlagSize,
                cornerRadius = 8.dp,
            )
        } else if (builtInVisual != null) {
            Icon(
                imageVector = builtInVisual.icon,
                contentDescription = proxyName,
                tint = builtInVisual.iconTint,
                modifier = Modifier.size(NodeCardDefaults.LargeFlagSize),
            )
        } else {
            Text(
                text = typeName.take(2),
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }
    }
}

private fun normalizeNodeIconUri(raw: String): String {
    if (raw.startsWith("//")) return "https:$raw"
    if (raw.startsWith("www.", ignoreCase = true)) return "https://$raw"
    return raw
}

@Composable
private fun RemoteNodeIcon(iconUri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val placeholderColorInt = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.10f).toArgb()
    val request =
        remember(context, iconUri, placeholderColorInt) {
            ImageRequest(context, iconUri) {
                placeholder(IntColorDrawableStateImage(placeholderColorInt))
                error(IntColorDrawableStateImage(placeholderColorInt))
                crossfade(true)
            }
        }

    SketchAsyncImage(
        request = request,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

private fun resolveBuiltInNodeVisual(proxyName: String, typeName: String): BuiltInNodeVisual? {
    val normalizedName = proxyName.trim().uppercase()
    val normalizedType = typeName.trim().uppercase()
    return when {
        normalizedType == "DIRECT" || normalizedName == "DIRECT" || normalizedName == "直连" -> {
            BuiltInNodeVisual(
                icon = Yume.ArrowRight,
                iconTint = Color(0xFF0A8F6A),
                containerColor = Color(0xFF0A8F6A),
            )
        }

        normalizedType == "REJECT" ||
            normalizedType == "REJECTDROP" ||
            normalizedName == "REJECT" ||
            normalizedName == "REJECT-DROP" ||
            normalizedName == "拦截" -> {
            BuiltInNodeVisual(
                icon = Yume.ShieldMinus,
                iconTint = Color(0xFFE53935),
                containerColor = Color(0xFFE53935),
            )
        }

        normalizedType == "COMPATIBLE" ||
            normalizedName == "COMPATIBLE" ||
            normalizedName == "PROXY" -> {
            BuiltInNodeVisual(
                icon = Yume.Rocket,
                iconTint = Color(0xFF2E6FF2),
                containerColor = Color(0xFF2E6FF2),
            )
        }

        normalizedType == "PASS" || normalizedName == "PASS" -> {
            BuiltInNodeVisual(
                icon = Yume.Cancel,
                iconTint = Color(0xFF7A7A7A),
                containerColor = Color(0xFF7A7A7A),
            )
        }

        else -> null
    }
}

@Composable
private fun NodeTagChip(label: String, textColor: Color? = null, backgroundColor: Color? = null) {
    val primary = MiuixTheme.colorScheme.primary
    val resolvedTextColor = textColor ?: primary
    val resolvedBackgroundColor = backgroundColor ?: primary.copy(alpha = 0.1f)
    Text(
        text = label,
        style = MiuixTheme.textStyles.footnote1.copy(fontSize = NodeCardDefaults.ChipFontSize),
        color = resolvedTextColor,
        modifier =
            Modifier.clip(RoundedCornerShape(100.dp))
                .background(resolvedBackgroundColor)
                .padding(
                    horizontal = NodeCardDefaults.ChipHorizontalPadding,
                    vertical = NodeCardDefaults.ChipVerticalPadding,
                ),
    )
}

@Composable
private fun NodeMultiplierChip(multiplier: Float) {
    val isHigh = multiplier >= 2.0f
    val primary = MiuixTheme.colorScheme.primary
    val chipBg = if (isHigh) Color(0x1AFF3B30) else primary.copy(alpha = 0.1f)
    val chipColor = if (isHigh) Color(0xFFFF3B30) else primary
    val label =
        if (multiplier == multiplier.toLong().toFloat()) "x${multiplier.toLong()}"
        else "x$multiplier"

    Row(
        modifier =
            Modifier.clip(RoundedCornerShape(100.dp))
                .background(chipBg)
                .padding(
                    horizontal = NodeCardDefaults.ChipHorizontalPadding,
                    vertical = NodeCardDefaults.ChipVerticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = Yume.BadgeDollarSign,
            contentDescription = null,
            tint = chipColor,
            modifier = Modifier.size(NodeCardDefaults.MultiplierIconSize),
        )
        Text(
            text = label,
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = NodeCardDefaults.ChipFontSize),
            color = chipColor,
        )
    }
}

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

package com.github.nomadboxlab.monadbox.screen.onboarding

import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.nomadboxlab.monadbox.common.AppConstants
import com.github.nomadboxlab.monadbox.data.model.ThemeMode
import com.github.nomadboxlab.monadbox.presentation.component.appClickable
import com.github.nomadboxlab.monadbox.presentation.icon.MonadIcons
import com.github.nomadboxlab.monadbox.presentation.icon.monad.*
import com.github.nomadboxlab.monadbox.presentation.theme.AppTheme
import com.github.nomadboxlab.monadbox.presentation.theme.DefaultRadii
import com.github.nomadboxlab.monadbox.presentation.theme.LocalPageMetrics
import com.github.nomadboxlab.monadbox.screen.settings.component.ThemeColorPickerItem
import com.github.nomadboxlab.monadbox.screen.settings.component.ThemeModeSelectorItem
import dev.oom_wg.purejoy.mlang.MLang
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val PagePadding = AppConstants.UI.DEFAULT_HORIZONTAL_PADDING
private val SectionShape = RoundedCornerShape(DefaultRadii.display)
private const val RevealDurationMs = 420
private const val LinkPolicyTag = "policy"

private val StartupTypewriterPhrases = listOf("MonadBox", "Hello Word")

@Composable
internal fun DreamBackdrop(modifier: Modifier = Modifier, boosted: Boolean = true) {
    val surface = MiuixTheme.colorScheme.surface
    val primary = MiuixTheme.colorScheme.primary
    val baseTint =
        remember(surface, boosted) { lerp(surface, primary, if (boosted) 0.035f else 0.02f) }
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush =
                Brush.linearGradient(
                    colors = listOf(surface, baseTint, lerp(surface, primary, 0.02f)),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                )
        )
        drawRect(
            brush =
                Brush.verticalGradient(
                    colors =
                        listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.015f),
                        )
                )
        )
    }
}

@Composable
internal fun DetailBackdrop(modifier: Modifier = Modifier) {
    val surface = MiuixTheme.colorScheme.surface
    val primary = MiuixTheme.colorScheme.primary
    val accent = remember(surface) { lerp(surface, primary, 0.045f) }

    Box(modifier = modifier.fillMaxSize().background(surface)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush =
                    Brush.verticalGradient(
                        colors = listOf(accent.copy(alpha = 0.12f), Color.Transparent),
                        startY = 0f,
                        endY = size.height * 0.28f,
                    )
            )
        }
    }
}

@Composable
internal fun RevealBlock(
    delayMillis: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter =
            fadeIn(
                animationSpec =
                    tween(durationMillis = RevealDurationMs, easing = LinearOutSlowInEasing)
            ) +
                slideInVertically(
                    animationSpec =
                        tween(durationMillis = RevealDurationMs, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 7 },
                ),
    ) {
        content()
    }
}

@Composable
internal fun RevealScaleBlock(
    delayMillis: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter =
            fadeIn(
                animationSpec =
                    tween(durationMillis = RevealDurationMs, easing = LinearOutSlowInEasing)
            ) +
                scaleIn(
                    animationSpec =
                        tween(durationMillis = RevealDurationMs, easing = FastOutSlowInEasing),
                    initialScale = 0.92f,
                ),
    ) {
        content()
    }
}

@Composable
internal fun StartupHeroShell(enabled: Boolean, onStart: (View) -> Unit) {
    val spacing = AppTheme.spacing
    val pageMetrics = LocalPageMetrics.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        DetailBackdrop()

        val heroTopSpacer =
            (maxHeight * pageMetrics.onboardingTopSpacerFraction).coerceIn(
                pageMetrics.onboardingTopSpacerMin,
                pageMetrics.onboardingTopSpacerMax,
            )

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = PagePadding, vertical = spacing.md)
        ) {
            Spacer(modifier = Modifier.height(heroTopSpacer))

            RevealBlock(delayMillis = 0, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                StartupTypewriterWord(
                    phrases = StartupTypewriterPhrases,
                    modifier = Modifier.widthIn(max = pageMetrics.onboardingWordmarkMaxWidth),
                )
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier =
                        Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(spacing.xl))
                }
            }

            RevealScaleBlock(
                delayMillis = 680,
                modifier =
                    Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = spacing.xxl),
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    HeroStartButton(enabled = enabled, onStart = onStart)
                }
            }
        }
    }
}

@Composable
private fun StartupTypewriterWord(phrases: List<String>, modifier: Modifier = Modifier) {
    var phraseIndex by remember(phrases) { mutableStateOf(0) }
    var visibleLength by remember(phrases) { mutableStateOf(0) }
    var deleting by remember(phrases) { mutableStateOf(false) }

    LaunchedEffect(phrases) {
        while (true) {
            val currentText = phrases.getOrElse(phraseIndex) { "" }
            if (!deleting) {
                if (visibleLength < currentText.length) {
                    delay(95)
                    visibleLength += 1
                } else {
                    delay(1850)
                    deleting = true
                }
            } else {
                if (visibleLength > 0) {
                    delay(65)
                    visibleLength -= 1
                } else {
                    delay(700)
                    deleting = false
                    phraseIndex = (phraseIndex + 1) % phrases.size
                }
            }
        }
    }

    val currentText = phrases.getOrElse(phraseIndex) { "" }
    val displayText = remember(currentText, visibleLength) { currentText.take(visibleLength) }
    val showCursor = visibleLength < currentText.length || deleting

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = displayText,
            style =
                MiuixTheme.textStyles.title1.copy(
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.2.sp,
                ),
            color = MiuixTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        if (showCursor) {
            Box(
                modifier =
                    Modifier.padding(start = 4.dp, top = 3.dp)
                        .width(1.2.dp)
                        .height(46.dp)
                        .background(
                            color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(50),
                        )
            )
        }
    }
}

@Composable
internal fun ProvisionDetailShell(
    previewIcon: ImageVector,
    title: String,
    subtitle: String,
    primaryText: String,
    primaryEnabled: Boolean,
    onPrimaryClick: () -> Unit,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val pageMetrics = LocalPageMetrics.current

    Box(modifier = Modifier.fillMaxSize()) {
        DetailBackdrop()

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = PagePadding, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(88.dp))

            RevealBlock(delayMillis = 0, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                DetailPreviewBadge(icon = previewIcon)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier =
                    Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RevealBlock(delayMillis = 50) {
                    Text(
                        text = title,
                        style =
                            MiuixTheme.textStyles.title2.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MiuixTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
                RevealBlock(delayMillis = 110) {
                    Text(
                        text = subtitle,
                        style = MiuixTheme.textStyles.body2.copy(lineHeight = 22.sp),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier =
                        Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    RevealBlock(delayMillis = 160) {
                        Column(verticalArrangement = Arrangement.spacedBy(18.dp), content = content)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            RevealBlock(
                delayMillis = 220,
                modifier =
                    Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SecondaryFooterAction(
                        text = MLang.Onboarding.Navigation.Back,
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryFooterAction(
                        text = primaryText,
                        enabled = primaryEnabled,
                        onClick = onPrimaryClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
internal fun PermissionContent(state: PermissionState) {
    val notificationSummary =
        when {
            state.notificationGranted -> MLang.Onboarding.Permission.Common.Granted
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ->
                MLang.Onboarding.Permission.Notification.SummaryNeed
            else -> MLang.Onboarding.Permission.Notification.SummaryNotRequired
        }

    val appListSummary =
        when {
            state.appListGranted -> MLang.Onboarding.Permission.Common.Granted
            state.miuiDynamicSupported -> MLang.Onboarding.Permission.AppList.SummaryNeed
            else -> MLang.Onboarding.Permission.AppList.SummaryNotRequired
        }

    DetailGroup {
        PermissionRow(
            icon = MonadIcons.Message,
            title = MLang.Onboarding.Permission.Notification.Title,
            summary = notificationSummary,
            granted = state.notificationGranted,
            onClick = {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                    state.onRequestNotification()
                    return@PermissionRow
                }
                if (!state.notificationGranted) {
                    state.onRequestNotification()
                }
            },
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 18.dp))

        PermissionRow(
            icon = MonadIcons.List,
            title = MLang.Onboarding.Permission.AppList.Title,
            summary = appListSummary,
            granted = state.appListGranted,
            onClick = {
                if (!state.appListGranted) {
                    state.onRequestAppList()
                }
            },
        )
    }
}

@Composable
internal fun TermsContent(
    accepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit,
    onPrivacySheetRequest: () -> Unit,
) {
    val colorScheme = MiuixTheme.colorScheme
    val linkStyle =
        remember(colorScheme.primary) {
            SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    val linkStyles = remember(linkStyle) { TextLinkStyles(style = linkStyle) }
    val annotatedText =
        remember(linkStyles, onPrivacySheetRequest) {
            buildAnnotatedString {
                append(MLang.Onboarding.Privacy.RichTextLead)
                append(" ")
                append(MLang.Onboarding.Privacy.RichTextPrefix)
                withLink(
                    LinkAnnotation.Clickable(
                        tag = LinkPolicyTag,
                        styles = linkStyles,
                        linkInteractionListener = { onPrivacySheetRequest() },
                    )
                ) {
                    withStyle(linkStyle) { append(MLang.Onboarding.Privacy.PolicyLink) }
                }
                append(MLang.Onboarding.Privacy.RichTextSuffix)
            }
        }

    DetailGroup {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = annotatedText,
                style =
                    MiuixTheme.textStyles.body2.copy(
                        color = MiuixTheme.colorScheme.onSurface,
                        lineHeight = 24.sp,
                    ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Checkbox(
                    state = androidx.compose.ui.state.ToggleableState(accepted),
                    onClick = { onAcceptedChange(!accepted) },
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = MLang.Onboarding.Privacy.Accept.Title,
                        style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium),
                        color = MiuixTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
internal fun PersonalizeContent(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    themeSeedColorArgb: Long,
    onShowThemeColorPickerChange: (Boolean) -> Unit,
) {
    DetailGroup {
        ThemeModeSelectorItem(themeMode = themeMode, onThemeModeChange = onThemeModeChange)
        DetailDivider()
        ThemeColorPickerItem(
            themeSeedColorArgb = themeSeedColorArgb,
            onThemeSeedColorChange = {},
            showBottomSheetInPlace = false,
            onOpenPickerRequest = { onShowThemeColorPickerChange(true) },
        )
    }
}

@Composable
internal fun FinishHeroShell(
    enabled: Boolean,
    onPrimaryClick: () -> Unit,
    onGithubClick: () -> Unit,
) {
    val pageMetrics = LocalPageMetrics.current

    Box(modifier = Modifier.fillMaxSize()) {
        DetailBackdrop()

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = PagePadding, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(88.dp))

            RevealBlock(delayMillis = 0, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                DetailPreviewBadge(icon = MonadIcons.CircleCheckBig)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier =
                    Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RevealBlock(delayMillis = 50) {
                    Text(
                        text = MLang.Onboarding.Finish.Title,
                        style =
                            MiuixTheme.textStyles.title2.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MiuixTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
                RevealBlock(delayMillis = 110) {
                    Text(
                        text = MLang.Onboarding.Finish.Subtitle,
                        style = MiuixTheme.textStyles.body2.copy(lineHeight = 22.sp),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier =
                        Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    RevealBlock(delayMillis = 160) {
                        DetailGroup {
                            ProjectLinkRow(
                                icon = MonadIcons.Github,
                                title = MLang.Onboarding.Project.Github.Title,
                                summary = MLang.Onboarding.Project.Github.Summary,
                                onClick = onGithubClick,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            RevealBlock(
                delayMillis = 220,
                modifier =
                    Modifier.widthIn(max = pageMetrics.onboardingDetailMaxWidth)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 12.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PrimaryFooterAction(
                        text = MLang.Onboarding.Navigation.Enter,
                        enabled = enabled,
                        onClick = onPrimaryClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroStartButton(
    enabled: Boolean,
    onStart: (View) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulseTransition = rememberInfiniteTransition(label = "startup_button_pulse")
    val pulseScale by
        pulseTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.045f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "startup_button_scale",
        )

    Box(
        modifier =
            modifier
                .size(72.dp)
                .graphicsLayer(
                    alpha = if (enabled) 1f else 0.45f,
                    scaleX = if (enabled) pulseScale else 1f,
                    scaleY = if (enabled) pulseScale else 1f,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier.matchParentSize()
                    .clip(CircleShape)
                    .background(MiuixTheme.colorScheme.primary)
        )
        Icon(
            imageVector = MonadIcons.ArrowRight,
            contentDescription = MLang.Component.Button.Start,
            tint = MiuixTheme.colorScheme.onPrimary,
            modifier = Modifier.size(22.dp),
        )
        AndroidView(
            factory = { context -> View(context) },
            modifier = Modifier.matchParentSize(),
            update = { view ->
                view.isClickable = enabled
                view.isEnabled = enabled
                view.setOnClickListener {
                    if (enabled) {
                        onStart(view)
                    }
                }
            },
        )
    }
}

@Composable
private fun DetailPreviewBadge(icon: ImageVector) {
    val pageMetrics = LocalPageMetrics.current

    Box(
        modifier = Modifier.size(pageMetrics.onboardingDetailPreviewBadgeSize),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MiuixTheme.colorScheme.primary,
            modifier = Modifier.size(pageMetrics.onboardingDetailPreviewIconSize),
        )
    }
}

@Composable
private fun DetailGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(SectionShape)
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f)),
        content = content,
    )
}

@Composable
private fun DetailDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 18.dp),
        thickness = 0.5.dp,
        color = MiuixTheme.colorScheme.outline.copy(alpha = 0.24f),
    )
}

@Composable
private fun PermissionRow(
    icon: ImageVector,
    title: String,
    summary: String,
    granted: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .appClickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier =
                Modifier.size(42.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium),
                color = MiuixTheme.colorScheme.onSurface,
            )
            Text(
                text = summary,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }

        if (granted) {
            Text(
                text = MLang.Onboarding.Permission.Common.Granted,
                style = MiuixTheme.textStyles.footnote1.copy(fontWeight = FontWeight.SemiBold),
                color = MiuixTheme.colorScheme.primary,
            )
        } else {
            Icon(
                imageVector = MonadIcons.ArrowRight,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun ProjectLinkRow(icon: ImageVector, title: String, summary: String, onClick: () -> Unit) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .appClickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MiuixTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium),
                color = MiuixTheme.colorScheme.onSurface,
            )
            Text(
                text = summary,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }

        Icon(
            imageVector = MonadIcons.ArrowRight,
            contentDescription = null,
            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun PrimaryFooterAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        if (enabled) {
            MiuixTheme.colorScheme.primary
        } else {
            MiuixTheme.colorScheme.primary.copy(alpha = 0.45f)
        }
    val contentColor =
        if (enabled) {
            MiuixTheme.colorScheme.onPrimary
        } else {
            MiuixTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
        }

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(24.dp))
                .background(containerColor)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
        )
    }
}

@Composable
private fun SecondaryFooterAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = 0.84f))
                .appClickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium),
            color = MiuixTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SecondaryLinkAction(text: String, onClick: () -> Unit) {
    Box(
        modifier =
            Modifier.clip(RoundedCornerShape(18.dp))
                .appClickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.footnote1.copy(fontWeight = FontWeight.Medium),
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

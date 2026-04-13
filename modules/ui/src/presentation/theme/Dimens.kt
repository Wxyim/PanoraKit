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

package com.github.yumelira.yumebox.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Returns the shared app [Spacing] with only viewport-level insets adapted to window size.
 *
 * The rhythm scale itself remains centralized in [DefaultSpacing]. This function only adjusts the
 * outer page frame so wide layouts gain stronger horizontal containment without introducing a
 * second spacing system.
 */
@Composable
fun rememberAdaptiveSpacing(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    pageScale: Float = 1f,
): Spacing {
    val normalizedScale = pageScale.coerceIn(0.8f, 1.2f)
    return remember(
        windowAdaptiveInfo.widthSizeClass,
        windowAdaptiveInfo.windowWidth,
        normalizedScale,
    ) {
        val baseSpacing =
            when (windowAdaptiveInfo.widthSizeClass) {
                androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Expanded -> {
                    val effectiveWidth =
                        if (windowAdaptiveInfo.windowWidth > 0.dp) {
                            windowAdaptiveInfo.windowWidth
                        } else {
                            840.dp
                        }
                    val inset = ((effectiveWidth - 680.dp) / 2f).coerceAtLeast(32.dp)
                    DefaultSpacing.copy(screenH = inset, gutter = 24.dp)
                }
                androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Medium ->
                    DefaultSpacing.copy(screenH = 24.dp, gutter = 20.dp)
                else -> DefaultSpacing
            }
        baseSpacing.scaleBy(normalizedScale)
    }
}

/**
 * Returns adaptive page metrics for viewport-dependent caps and sheet/list constraints.
 *
 * This does not redefine shared spacing or radii semantics. It only adapts page-specific bounds
 * that should respond to available width and height.
 */
@Composable
fun rememberAdaptivePageMetrics(windowAdaptiveInfo: WindowAdaptiveInfo): PageMetrics {
    return remember(
        windowAdaptiveInfo.widthSizeClass,
        windowAdaptiveInfo.heightSizeClass,
        windowAdaptiveInfo.windowWidth,
        windowAdaptiveInfo.windowHeight,
    ) {
        val singlePaneMaxWidth = windowAdaptiveInfo.preferredSinglePaneMaxWidth
        val heightAwareMetrics =
            when (windowAdaptiveInfo.heightSizeClass) {
                androidx.compose.material3.windowsizeclass.WindowHeightSizeClass.Compact ->
                    DefaultPageMetrics.copy(
                        openSourceLicenseSheetMaxHeight = 360.dp,
                        profileAddSheetFallbackHeight = 560.dp,
                        profileAddSheetQrPreviewMinHeight = 160.dp,
                        profileAddSheetQrPreviewMaxHeight = 220.dp,
                        profileSettingsMaxHeightFraction = 0.8f,
                        profileLinkSettingsLinkListMaxHeight = 280.dp,
                        overrideBindingListMaxHeight = 320.dp,
                        overridePresetTemplateListMaxHeight = 420.dp,
                        proxyFloatingPanelMaxHeight = 560.dp,
                        proxyFloatingPanelListMaxHeight = 360.dp,
                    )

                androidx.compose.material3.windowsizeclass.WindowHeightSizeClass.Expanded ->
                    DefaultPageMetrics.copy(
                        openSourceLicenseSheetMaxHeight = 520.dp,
                        profileAddSheetFallbackHeight = 720.dp,
                        profileAddSheetQrPreviewMinHeight = 208.dp,
                        profileAddSheetQrPreviewMaxHeight = 320.dp,
                        profileLinkSettingsLinkListMaxHeight = 420.dp,
                        overrideBindingListMaxHeight = 520.dp,
                        overridePresetTemplateListMaxHeight = 680.dp,
                        proxyFloatingPanelMaxHeight = 720.dp,
                        proxyFloatingPanelListMaxHeight = 520.dp,
                    )

                else -> DefaultPageMetrics
            }

        heightAwareMetrics.copy(
            contentMaxWidth = singlePaneMaxWidth,
            accessControlSearchOverlayMaxWidth = windowAdaptiveInfo.preferredBottomSheetMaxWidth,
        )
    }
}

/**
 * Shared spacing tokens.
 *
 * `xxs..xxxl` define the global rhythm scale. `gutter` and `screenH/screenV` are page-frame insets,
 * not an additional semantic spacing ladder.
 */
data class Spacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val gutter: Dp = 16.dp,
    val screenH: Dp = 12.dp,
    val screenV: Dp = 12.dp,
)

data class Radii(
    val none: Dp = 0.dp,
    val sm: Dp = 4.dp,
    val md: Dp = 8.dp,
    val lg: Dp = 12.dp,
    val xl: Dp = 16.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp,
    val display: Dp = 36.dp,
    val pill: Dp = 999.dp,
)

data class Strokes(
    val thin: Dp = 0.35.dp,
    val default: Dp = 0.8.dp,
    val medium: Dp = 1.dp,
    val thick: Dp = 1.5.dp,
)

data class Elevations(
    val none: Dp = 0.dp,
    val low: Dp = 1.dp,
    val medium: Dp = 3.dp,
    val high: Dp = 6.dp,
    val overlay: Dp = 12.dp,
    val navigationBar: Dp = 22.dp,
)

val DefaultSpacing = Spacing()
val DefaultRadii = Radii()
val DefaultStrokes = Strokes()
val DefaultElevations = Elevations()

val LocalSpacing = staticCompositionLocalOf { DefaultSpacing }
val LocalRadii = staticCompositionLocalOf { DefaultRadii }
val LocalStrokes = staticCompositionLocalOf { DefaultStrokes }
val LocalElevations = staticCompositionLocalOf { DefaultElevations }

object AppTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current

    val radii: Radii
        @Composable get() = LocalRadii.current

    val strokes: Strokes
        @Composable get() = LocalStrokes.current

    val elevations: Elevations
        @Composable get() = LocalElevations.current

    val pageMetrics: PageMetrics
        @Composable get() = LocalPageMetrics.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current
}

/**
 * Exceptional page-level metrics.
 *
 * This is intentionally not a second generic spacing/radius system. Shared layout semantics MUST
 * come from [Spacing], [Radii], [Strokes], or [Elevations]. [PageMetrics] is reserved for values
 * that cannot be expressed as shared tokens without losing meaning, such as page-specific viewport
 * caps, hero artwork sizes, chart heights, or sheet sizing rules.
 */
data class PageMetrics(
    // Shared page viewport/layout caps.
    val contentMaxWidth: Dp = 840.dp,

    // About / licenses.
    val aboutHeroIconSize: Dp = 120.dp,
    val openSourceLicenseSheetMaxHeight: Dp = 450.dp,

    // Traffic statistics.
    val trafficSummaryBlockHeight: Dp = 66.dp,
    val trafficSummaryValueFontSize: androidx.compose.ui.unit.TextUnit = 26.sp,
    val trafficChartHeight: Dp = 132.dp,
    val trafficSelectedLabelHeight: Dp = 24.dp,
    val trafficRecentRequestChipCorner: Dp = 100.dp,

    // Editors.
    val editorIndexWidth: Dp = 40.dp,
    val editorDeleteButtonSize: Dp = 40.dp,

    // Logs.
    val logMetaFontSize: androidx.compose.ui.unit.TextUnit = 11.sp,
    val logMessageFontSize: androidx.compose.ui.unit.TextUnit = 12.sp,

    // Onboarding.
    val onboardingTopSpacerMin: Dp = 128.dp,
    val onboardingTopSpacerMax: Dp = 212.dp,
    val onboardingTopSpacerFraction: Float = 0.18f,
    val onboardingDetailMaxWidth: Dp = 560.dp,
    val onboardingWordmarkMaxWidth: Dp = 320.dp,
    val onboardingDetailPreviewBadgeSize: Dp = 108.dp,
    val onboardingDetailPreviewIconSize: Dp = 68.dp,

    // Access control.
    val accessControlSearchOverlayMaxWidth: Dp = 760.dp,
    val accessControlSearchResultAppIconSize: Dp = 40.dp,
    val accessControlAppCardIconSize: Dp = 45.dp,
    val accessControlAppIconBitmapBaseSize: Int = 80,

    // Profiles.
    val profileAddSheetFallbackHeight: Dp = 640.dp,
    val profileAddSheetProgressIconContainer: Dp = 48.dp,
    val profileAddSheetProgressIndicatorSize: Dp = 32.dp,
    val profileAddSheetQrPreviewMinHeight: Dp = 188.dp,
    val profileAddSheetQrPreviewMaxHeight: Dp = 256.dp,
    val profileAddSheetQrPreviewHeightFraction: Float = 0.28f,
    val profileSettingsMinHeightFraction: Float = 0.5f,
    val profileSettingsMaxHeightFraction: Float = 0.7f,
    val profileLinkSettingsLinkListMaxHeight: Dp = 360.dp,
    val overrideBindingListMaxHeight: Dp = 420.dp,

    // Override.
    val overridePresetTemplateListMaxHeight: Dp = 560.dp,

    // Proxy.
    val proxyNotificationSheetHeightFraction: Float = 0.55f,
    val proxyFloatingPanelWidthFraction: Float = 0.84f,
    val proxyFloatingPanelMaxWidth: Dp = 520.dp,
    val proxyFloatingPanelMaxHeight: Dp = 640.dp,
    val proxyFloatingPanelListMaxHeight: Dp = 460.dp,
)

val DefaultPageMetrics = PageMetrics()

private fun Spacing.scaleBy(scale: Float): Spacing {
    if (scale == 1f) return this
    return copy(
        xxs = xxs * scale,
        xs = xs * scale,
        sm = sm * scale,
        md = md * scale,
        lg = lg * scale,
        xl = xl * scale,
        xxl = xxl * scale,
        xxxl = xxxl * scale,
        gutter = gutter * scale,
        screenH = screenH * scale,
        screenV = screenV * scale,
    )
}

val LocalPageMetrics = staticCompositionLocalOf { DefaultPageMetrics }

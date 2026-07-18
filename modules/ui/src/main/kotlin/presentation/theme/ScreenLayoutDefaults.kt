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
 */

package com.github.nomadboxlab.monadbox.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object TrafficStatisticsScreenLayoutDefaults {
    val CardSpacing = 14.dp
    val ChartInfoSpacing = 10.dp
    val DetailSectionSpacing = 14.dp
    val SelectorWideOverviewMinWidth = 200.dp
    val SelectorWideOverviewMaxWidth = 248.dp
    val SelectorWideDetailMinWidth = 240.dp
    val SelectorWideDetailMaxWidth = 320.dp
    val SelectorCompactOverviewMinWidth = 180.dp
    val SelectorCompactOverviewMaxWidth = 220.dp
    val SelectorCompactDetailMinWidth = 220.dp
    val SelectorCompactDetailMaxWidth = 280.dp
    val RequestCardCornerRadius = 20.dp
    val RequestChipRowSpacing = 6.dp
    val RequestChipVerticalPadding = 3.dp
}

object ConnectionScreenLayoutDefaults {
    val ItemVerticalPadding = 6.dp
}

object ProxyPageLayoutDefaults {
    val ContentTop = DefaultSpacing.xl
    val ContentHorizontal = DefaultSpacing.md
    val ItemVertical = 6.dp
    val ExpandedThreeColumnMinWidth = 1280.dp
    val ContentBottomExtra = DefaultSpacing.lg
    val TopBarNavigationSpacing = DefaultSpacing.lg
    val TopBarBackStartPadding = DefaultSpacing.xxl
    val TopBarActionSize = 44.dp
    val TopBarActionIconSize = 20.dp
    val TopBarInnerActionEndPadding = DefaultSpacing.md
    val TopBarOuterActionEndPadding = DefaultSpacing.xxl
}

object ProxyDisplaySettingsLayoutDefaults {
    val FallbackWindowHeight = 640.dp
    val CompactHeightThreshold = 560.dp
    val ContentSpacingCompact = DefaultSpacing.md
    val ContentSpacingRegular = DefaultSpacing.lg
    val SectionSpacingCompact = DefaultSpacing.sm
    val SectionSpacingRegular = 10.dp
    const val ContentMaxHeightFraction = 0.7f
    val ContentMaxHeightCap = 520.dp
    val SectionStartPadding = DefaultSpacing.xs
    val SectionTextSpacing = DefaultSpacing.xs
    val ChoiceTileMinHeightCompact = 76.dp
    val ChoiceTileMinHeightRegular = 58.dp
    val ToggleContainerCornerRadius = DefaultRadii.xxl
    val ToggleContentHorizontalPadding = 15.dp
    val ToggleContentVerticalPadding = 13.dp
    val ToggleContentSpacing = DefaultSpacing.md
    val ToggleIconContainerSize = 38.dp
    val ToggleIconCornerRadius = 14.dp
    val ToggleIconSize = 20.dp
    val ToggleLabelSpacing = DefaultSpacing.xxs
}

object ProxyRuntimePreviewLayoutDefaults {
    val CornerRadius = DefaultRadii.xxl
    val BorderWidth = DefaultStrokes.medium
    val PaddingHorizontal = 18.dp
    val PaddingVertical = DefaultSpacing.lg
    val ContentSpacing = DefaultSpacing.xs
}

object ProxyNodeCardLayoutDefaults {
    val CornerRadius = DefaultRadii.xxl
    val PaddingHorizontal = DefaultSpacing.lg
    val PaddingVertical = DefaultSpacing.lg
    val CompactPaddingVertical = DefaultSpacing.md
    val TextSpacing = DefaultSpacing.sm
    val LeadingContainerSize = 44.dp
    val LeadingContainerCornerRadius = 14.dp
    val LargeFlagSize = 28.dp
    val InlineFlagSize = 20.dp
    val ActionIconSize = DefaultSpacing.lg
    val ChevronIconSize = 18.dp
    val ChipCornerRadius = DefaultRadii.pill
    val ChipFontSize = 10.sp
    val ChipHorizontalPadding = 7.dp
    val ChipVerticalPadding = 2.dp
    val MultiplierIconSize = 9.dp
    val SelectionBorderWidth = DefaultStrokes.medium
    val ContentRowSpacing = DefaultSpacing.lg
    val TextColumnSpacing = 6.dp
    val TagRowHorizontalSpacing = 5.dp
    val TagRowVerticalSpacing = DefaultSpacing.xs
    val MetricStartPadding = DefaultSpacing.sm
    val MetricSpacing = DefaultSpacing.sm
    val CountryFlagCornerRadius = DefaultSpacing.sm
    val MultiplierChipSpacing = DefaultSpacing.xxs
}

object ProxyNodeGroupLayoutDefaults {
    val ItemVerticalPadding = 6.dp
    val AdaptiveColumnSpacing = DefaultSpacing.md
    val CardCornerRadius = 26.dp
    val CardElevation = 4.dp
    val CardBorderWidth = DefaultStrokes.medium
    val CardPaddingHorizontal = DefaultSpacing.lg
    val CardPaddingVertical = 14.dp
    val CardSectionSpacing = 10.dp
    val CardContentColumnSpacing = 10.dp
    val HeaderTitleSpacing = DefaultSpacing.sm
    val HeaderMetaStartPadding = DefaultSpacing.sm
    val HeaderMetaSpacing = DefaultSpacing.sm
    val ExpandedContentTopPadding = 6.dp
    val ExpandedContentSpacing = DefaultSpacing.sm
    val BadgeCornerRadius = DefaultRadii.pill
    val BadgeExtraVerticalPadding = 1.dp
    val AdaptiveInlineExpandedRowSpacing = 10.dp
    val InlineExpandedPanelCornerRadius = 26.dp
    val InlineExpandedPanelElevation = 4.dp
    val InlineExpandedPanelBorderWidth = 1.dp
    val InlineExpandedPanelContentHorizontalPadding = 16.dp
    val InlineExpandedPanelContentVerticalPadding = 14.dp
    val InlineExpandedPanelSectionSpacing = 12.dp
    val InlineExpandedPanelTitleSpacing = 8.dp
    val InlineExpandedPanelMetaStartPadding = 12.dp
    val InlineExpandedPanelMetaSpacing = 10.dp
}

object ProxyNodeGridLayoutDefaults {
    val ContentPadding = DefaultSpacing.none
    val ItemSpacing = DefaultSpacing.md
    val ColumnSpacing = DefaultSpacing.md
    val AdaptiveTwoColumnMinWidth = 600.dp
    val AdaptiveThreeColumnMinWidth = 900.dp
}

object ProxyFloatingPanelLayoutDefaults {
    val OuterPadding = DefaultSpacing.md
    val CornerRadius = 22.dp
    val ShadowElevation = 16.dp
    val BorderWidth = DefaultStrokes.medium
    val ContentSpacing = DefaultSpacing.md
    val DividerThickness = DefaultStrokes.medium
    val ListItemSpacing = DefaultSpacing.sm
    val SelectedCardCornerRadius = DefaultRadii.xxl
    val SelectedCardBorderWidth = DefaultStrokes.medium
    val SelectedCardInnerPadding = DefaultSpacing.xxs
}

object ProxyFloatingPanelHeaderLayoutDefaults {
    val HeaderCornerRadius = 18.dp
    val HeaderHorizontalPadding = 14.dp
    val HeaderVerticalPadding = DefaultSpacing.md
    val HeaderSectionSpacing = 10.dp
    val HeaderTitleSpacing = 6.dp
    val HeaderMetaSpacing = DefaultSpacing.sm
    val HeaderMetaChipVerticalPadding = 3.dp
    val HeaderMetaCountFontSize = 10.sp
    val HeaderMetaChipFontSize = 10.sp
    val HeaderCloseIconSize = 18.dp
    val HeaderCloseActionSize = 34.dp
    val HeaderActionSpacing = DefaultSpacing.xs
    val DetailLabelSpacing = 3.dp
    val DetailLatencyStartPadding = DefaultSpacing.md
    val CurrentBadgeCornerRadius = DefaultRadii.pill
    val CurrentBadgeTopPadding = 10.dp
    val CurrentBadgeEndPadding = DefaultSpacing.md
    val CurrentBadgeHorizontalPadding = DefaultSpacing.sm
    val CurrentBadgeVerticalPadding = 3.dp
    val CurrentBadgeFontSize = 10.sp
}

object HomePagerLayoutDefaults {
    val ContentMaxWidth = 1080.dp
    val ContentScaleReferenceWidth = 390.dp
    const val ContentScaleMin = 0.92f
    const val ContentScaleMax = 1.04f
    val WideSectionSpacing = DefaultSpacing.xxl
    val CompactSectionSpacingMin = 18.dp
    val InfoSpacingWide = 18.dp
    val InfoSpacingMin = DefaultSpacing.md
    val InfoSpacingBase = DefaultSpacing.lg
    val RunningChartHeightMin = 112.dp
    val HistoryChartHeightMin = 88.dp
    val HistoryChartHeightMax = 104.dp
    val IdleChartHeight = 72.dp
    val ChartSectionSpacing = 10.dp
}

object HomeModeSwitchOverlayLayoutDefaults {
    val HorizontalMargin = 14.dp
    val FallbackAnchorWidth = 188.dp
    const val PanelWidthFactor = 1.06f
    val PanelMinWidth = 208.dp
    val PanelMaxWidth = 244.dp
    val FallbackAnchorHeight = 44.dp
    const val PanelSpacingFactor = 0.24f
    val PanelSpacingMin = DefaultSpacing.sm
    val PanelSpacingMax = DefaultSpacing.md
    val PanelFallbackTop = 0.dp
    val PanelCornerRadius = 30.dp
    val PanelVerticalPadding = 10.dp
    val ItemCornerRadius = 22.dp
    val ItemOuterHorizontalPadding = 10.dp
    val ItemOuterVerticalPadding = DefaultSpacing.xs
    val ItemMinHeight = 56.dp
    val ItemInnerHorizontalPadding = 18.dp
    val ItemInnerVerticalPadding = DefaultSpacing.xs
    val ItemTextSize = 16.sp
    val ItemIconSize = 20.dp
}

@Immutable data class HomeIdleLayoutMetrics(val topSpacing: Dp, val bottomSpacing: Dp)

object HomeIdleLayoutDefaults {
    const val TopSpacingFraction = 0.14f
    val TopSpacingMin = 72.dp
    val TopSpacingMax = 122.dp
    const val BottomSpacingFraction = 0.05f
    val BottomSpacingMin = DefaultSpacing.xxl
    val BottomSpacingMax = 40.dp
    val SectionSpacing = DefaultSpacing.lg
    val TitleTopPadding = 18.dp
    val TitleStartPadding = DefaultSpacing.sm
    val TitleRowSpacing = DefaultSpacing.md
    val AccentBarTopPadding = DefaultSpacing.md
    val AccentBarWidth = DefaultSpacing.xxs
    val AccentBarHeight = 36.dp
    val TitleFontSize = 28.sp
    val TitleLineHeight = 50.sp
    val TitleLetterSpacing = 0.8.sp
    val AuthorLineWidth = 30.dp
    val AuthorLineHeight = 1.dp
    val AuthorSpacing = DefaultSpacing.md
    val AuthorFontSize = 15.sp
    val AuthorLetterSpacing = 1.6.sp
}

@Composable
fun rememberHomeIdleLayoutMetrics(maxHeight: Dp): HomeIdleLayoutMetrics {
    return remember(maxHeight) {
        HomeIdleLayoutMetrics(
            topSpacing =
                (maxHeight * HomeIdleLayoutDefaults.TopSpacingFraction).coerceIn(
                    HomeIdleLayoutDefaults.TopSpacingMin,
                    HomeIdleLayoutDefaults.TopSpacingMax,
                ),
            bottomSpacing =
                (maxHeight * HomeIdleLayoutDefaults.BottomSpacingFraction).coerceIn(
                    HomeIdleLayoutDefaults.BottomSpacingMin,
                    HomeIdleLayoutDefaults.BottomSpacingMax,
                ),
        )
    }
}

@Immutable
data class HomeTrafficMetrics(
    val topPadding: Dp,
    val bottomPadding: Dp,
    val sectionSpacing: Dp,
    val capsuleSectionSpacing: Dp,
    val controlStackSpacing: Dp,
    val labelFontSize: TextUnit,
    val trafficFontSize: TextUnit,
    val trafficLetterSpacing: TextUnit,
    val trafficUnitFontSize: TextUnit,
    val uploadSectionSpacing: Dp,
    val uploadValueFontSize: TextUnit,
    val capsuleHeight: Dp,
    val capsuleHorizontalPadding: Dp,
    val capsuleInnerSpacing: Dp,
    val capsuleIconSpacing: Dp,
    val capsuleIconSize: Dp,
    val capsuleDotSize: Dp,
    val capsuleTextSize: TextUnit,
    val controlTouchTargetHeight: Dp,
    val controlCornerRadius: Dp,
    val controlHorizontalPadding: Dp,
    val controlVerticalPadding: Dp,
    val controlInnerSpacing: Dp,
    val controlIconSize: Dp,
    val controlChevronSize: Dp,
    val controlTextSize: TextUnit,
    val modeCaptionTextSize: TextUnit,
    val controlPressedScale: Float,
    val modeBadgeHeight: Dp,
    val modeBadgeMinWidth: Dp,
    val modeBadgeMaxWidth: Dp,
    val statusCapsuleHeight: Dp,
    val statusCapsuleMinWidth: Dp,
)

object HomeTrafficMetricsDefaults {
    val WidthReference = 390.dp
    const val ScaleMin = 0.9f
    const val ScaleMax = 1f
    val TopPaddingMin = 30.dp
    val TopPaddingMax = 36.dp
    val BottomPaddingMin = 6.dp
    val BottomPaddingMax = DefaultSpacing.sm
    val SectionSpacingMin = DefaultSpacing.md
    val SectionSpacingMax = DefaultSpacing.lg
    val CapsuleSectionSpacingMin = DefaultSpacing.sm
    val CapsuleSectionSpacingMax = DefaultSpacing.md
    val ControlStackSpacingMin = 10.dp
    val ControlStackSpacingMax = DefaultSpacing.md
    val LabelFontSizeMin = 13.sp
    val LabelFontSizeMax = 14.sp
    val TrafficFontSizeMin = 84.sp
    val TrafficFontSizeMax = 100.sp
    val TrafficLetterSpacing = 0.sp
    val TrafficUnitFontSizeMin = 22.sp
    val TrafficUnitFontSizeMax = 26.sp
    val UploadSectionSpacingMin = DefaultSpacing.sm
    val UploadSectionSpacingMax = 10.dp
    val UploadValueFontSizeMin = 22.sp
    val UploadValueFontSizeMax = 24.sp
    val CapsuleHeightMin = 26.dp
    val CapsuleHeightMax = 28.dp
    val CapsuleHorizontalPaddingMin = 10.dp
    val CapsuleHorizontalPaddingMax = DefaultSpacing.md
    val CapsuleInnerSpacingMin = 6.dp
    val CapsuleInnerSpacingMax = DefaultSpacing.sm
    val CapsuleIconSpacingMin = 5.dp
    val CapsuleIconSpacingMax = 6.dp
    val CapsuleIconSizeMin = 11.dp
    val CapsuleIconSizeMax = 12.dp
    val CapsuleDotSizeMin = 3.dp
    val CapsuleDotSizeMax = DefaultSpacing.xs
    val CapsuleTextSizeMin = 11.sp
    val CapsuleTextSizeMax = 12.sp
    val ControlTouchTargetHeightMin = 52.dp
    val ControlTouchTargetHeightMax = 56.dp
    val ControlCornerRadiusMin = 20.dp
    val ControlCornerRadiusMax = 22.dp
    val ControlHorizontalPaddingMin = DefaultSpacing.lg
    val ControlHorizontalPaddingMax = 18.dp
    val ControlVerticalPaddingMin = DefaultSpacing.sm
    val ControlVerticalPaddingMax = 10.dp
    val ControlInnerSpacingMin = DefaultSpacing.sm
    val ControlInnerSpacingMax = 10.dp
    val ControlIconSizeMin = DefaultSpacing.lg
    val ControlIconSizeMax = 18.dp
    val ControlChevronSizeMin = 14.dp
    val ControlChevronSizeMax = 16.dp
    val ControlTextSizeMin = 14.sp
    val ControlTextSizeMax = 15.sp
    val ModeCaptionTextSizeMin = 11.sp
    val ModeCaptionTextSizeMax = 12.sp
    const val ControlPressedScale = 0.985f
    val ModeBadgeHeightMin = 48.dp
    val ModeBadgeHeightMax = 52.dp
    val ModeBadgeMinWidthMin = 170.dp
    val ModeBadgeMinWidthMax = 184.dp
    val ModeBadgeMaxWidthMin = 220.dp
    val ModeBadgeMaxWidthMax = 248.dp
    val StatusCapsuleHeightMin = 48.dp
    val StatusCapsuleHeightMax = 52.dp
    val StatusCapsuleMinWidthMin = 148.dp
    val StatusCapsuleMinWidthMax = 164.dp
    val ControlColumnExpandedMin = 224.dp
    val ControlColumnExpandedMax = 292.dp
    val ControlColumnMediumMin = 196.dp
    val ControlColumnMediumMax = 244.dp
    val ControlColumnCompactMin = 176.dp
    val ControlColumnCompactMax = 212.dp
}

@Composable
fun rememberHomeTrafficMetrics(width: Dp): HomeTrafficMetrics {
    return remember(width) {
        val scale =
            (width / HomeTrafficMetricsDefaults.WidthReference).coerceIn(
                HomeTrafficMetricsDefaults.ScaleMin,
                HomeTrafficMetricsDefaults.ScaleMax,
            )
        HomeTrafficMetrics(
            topPadding =
                (HomeTrafficMetricsDefaults.TopPaddingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.TopPaddingMin,
                    HomeTrafficMetricsDefaults.TopPaddingMax,
                ),
            bottomPadding =
                (HomeTrafficMetricsDefaults.BottomPaddingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.BottomPaddingMin,
                    HomeTrafficMetricsDefaults.BottomPaddingMax,
                ),
            sectionSpacing =
                (HomeTrafficMetricsDefaults.SectionSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.SectionSpacingMin,
                    HomeTrafficMetricsDefaults.SectionSpacingMax,
                ),
            capsuleSectionSpacing =
                (HomeTrafficMetricsDefaults.CapsuleSectionSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleSectionSpacingMin,
                    HomeTrafficMetricsDefaults.CapsuleSectionSpacingMax,
                ),
            controlStackSpacing =
                (HomeTrafficMetricsDefaults.ControlStackSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlStackSpacingMin,
                    HomeTrafficMetricsDefaults.ControlStackSpacingMax,
                ),
            labelFontSize =
                (HomeTrafficMetricsDefaults.LabelFontSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.LabelFontSizeMin.value,
                        HomeTrafficMetricsDefaults.LabelFontSizeMax.value,
                    )
                    .sp,
            trafficFontSize =
                (HomeTrafficMetricsDefaults.TrafficFontSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.TrafficFontSizeMin.value,
                        HomeTrafficMetricsDefaults.TrafficFontSizeMax.value,
                    )
                    .sp,
            trafficLetterSpacing = HomeTrafficMetricsDefaults.TrafficLetterSpacing,
            trafficUnitFontSize =
                (HomeTrafficMetricsDefaults.TrafficUnitFontSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.TrafficUnitFontSizeMin.value,
                        HomeTrafficMetricsDefaults.TrafficUnitFontSizeMax.value,
                    )
                    .sp,
            uploadSectionSpacing =
                (HomeTrafficMetricsDefaults.UploadSectionSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.UploadSectionSpacingMin,
                    HomeTrafficMetricsDefaults.UploadSectionSpacingMax,
                ),
            uploadValueFontSize =
                (HomeTrafficMetricsDefaults.UploadValueFontSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.UploadValueFontSizeMin.value,
                        HomeTrafficMetricsDefaults.UploadValueFontSizeMax.value,
                    )
                    .sp,
            capsuleHeight =
                (HomeTrafficMetricsDefaults.CapsuleHeightMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleHeightMin,
                    HomeTrafficMetricsDefaults.CapsuleHeightMax,
                ),
            capsuleHorizontalPadding =
                (HomeTrafficMetricsDefaults.CapsuleHorizontalPaddingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleHorizontalPaddingMin,
                    HomeTrafficMetricsDefaults.CapsuleHorizontalPaddingMax,
                ),
            capsuleInnerSpacing =
                (HomeTrafficMetricsDefaults.CapsuleInnerSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleInnerSpacingMin,
                    HomeTrafficMetricsDefaults.CapsuleInnerSpacingMax,
                ),
            capsuleIconSpacing =
                (HomeTrafficMetricsDefaults.CapsuleIconSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleIconSpacingMin,
                    HomeTrafficMetricsDefaults.CapsuleIconSpacingMax,
                ),
            capsuleIconSize =
                (HomeTrafficMetricsDefaults.CapsuleIconSizeMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleIconSizeMin,
                    HomeTrafficMetricsDefaults.CapsuleIconSizeMax,
                ),
            capsuleDotSize =
                (HomeTrafficMetricsDefaults.CapsuleDotSizeMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.CapsuleDotSizeMin,
                    HomeTrafficMetricsDefaults.CapsuleDotSizeMax,
                ),
            capsuleTextSize =
                (HomeTrafficMetricsDefaults.CapsuleTextSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.CapsuleTextSizeMin.value,
                        HomeTrafficMetricsDefaults.CapsuleTextSizeMax.value,
                    )
                    .sp,
            controlTouchTargetHeight =
                (HomeTrafficMetricsDefaults.ControlTouchTargetHeightMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlTouchTargetHeightMin,
                    HomeTrafficMetricsDefaults.ControlTouchTargetHeightMax,
                ),
            controlCornerRadius =
                (HomeTrafficMetricsDefaults.ControlCornerRadiusMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlCornerRadiusMin,
                    HomeTrafficMetricsDefaults.ControlCornerRadiusMax,
                ),
            controlHorizontalPadding =
                (HomeTrafficMetricsDefaults.ControlHorizontalPaddingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlHorizontalPaddingMin,
                    HomeTrafficMetricsDefaults.ControlHorizontalPaddingMax,
                ),
            controlVerticalPadding =
                (HomeTrafficMetricsDefaults.ControlVerticalPaddingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlVerticalPaddingMin,
                    HomeTrafficMetricsDefaults.ControlVerticalPaddingMax,
                ),
            controlInnerSpacing =
                (HomeTrafficMetricsDefaults.ControlInnerSpacingMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlInnerSpacingMin,
                    HomeTrafficMetricsDefaults.ControlInnerSpacingMax,
                ),
            controlIconSize =
                (HomeTrafficMetricsDefaults.ControlIconSizeMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlIconSizeMin,
                    HomeTrafficMetricsDefaults.ControlIconSizeMax,
                ),
            controlChevronSize =
                (HomeTrafficMetricsDefaults.ControlChevronSizeMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ControlChevronSizeMin,
                    HomeTrafficMetricsDefaults.ControlChevronSizeMax,
                ),
            controlTextSize =
                (HomeTrafficMetricsDefaults.ControlTextSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.ControlTextSizeMin.value,
                        HomeTrafficMetricsDefaults.ControlTextSizeMax.value,
                    )
                    .sp,
            modeCaptionTextSize =
                (HomeTrafficMetricsDefaults.ModeCaptionTextSizeMax.value * scale)
                    .coerceIn(
                        HomeTrafficMetricsDefaults.ModeCaptionTextSizeMin.value,
                        HomeTrafficMetricsDefaults.ModeCaptionTextSizeMax.value,
                    )
                    .sp,
            controlPressedScale = HomeTrafficMetricsDefaults.ControlPressedScale,
            modeBadgeHeight =
                (HomeTrafficMetricsDefaults.ModeBadgeHeightMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ModeBadgeHeightMin,
                    HomeTrafficMetricsDefaults.ModeBadgeHeightMax,
                ),
            modeBadgeMinWidth =
                (HomeTrafficMetricsDefaults.ModeBadgeMinWidthMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ModeBadgeMinWidthMin,
                    HomeTrafficMetricsDefaults.ModeBadgeMinWidthMax,
                ),
            modeBadgeMaxWidth =
                (HomeTrafficMetricsDefaults.ModeBadgeMaxWidthMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.ModeBadgeMaxWidthMin,
                    HomeTrafficMetricsDefaults.ModeBadgeMaxWidthMax,
                ),
            statusCapsuleHeight =
                (HomeTrafficMetricsDefaults.StatusCapsuleHeightMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.StatusCapsuleHeightMin,
                    HomeTrafficMetricsDefaults.StatusCapsuleHeightMax,
                ),
            statusCapsuleMinWidth =
                (HomeTrafficMetricsDefaults.StatusCapsuleMinWidthMax * scale).coerceIn(
                    HomeTrafficMetricsDefaults.StatusCapsuleMinWidthMin,
                    HomeTrafficMetricsDefaults.StatusCapsuleMinWidthMax,
                ),
        )
    }
}

object SpeedChartLayoutDefaults {
    val BarGap = 5.dp
    val BarCornerRadius = 6.dp
}

object PrivacyPolicySheetLayoutDefaults {
    val MaxHeight = 450.dp
}

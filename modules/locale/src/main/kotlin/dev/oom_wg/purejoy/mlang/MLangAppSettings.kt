/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */

@file:Suppress(
    "PackageDirectoryMismatch",
    "PackageName",
    "ClassName",
    "ObjectPropertyName",
    "PropertyName",
    "FunctionName",
    "NonAsciiCharacters",
    "RemoveRedundantBackticks",
    "unused",
)

package dev.oom_wg.purejoy.mlang

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.core.locale.R

object MLangAppSettings {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.app_settings_title)

    @Composable
    fun `Title`(vararg args: Any): String = stringResource(R.string.app_settings_title, *args)

    object `Section` {
        val `Behavior`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_section_behavior)

        @Composable
        fun `Behavior`(vararg args: Any): String =
            stringResource(R.string.app_settings_section_behavior, *args)

        val `Interface`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_section_interface)

        @Composable
        fun `Interface`(vararg args: Any): String =
            stringResource(R.string.app_settings_section_interface, *args)

        val `Service`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_section_service)

        @Composable
        fun `Service`(vararg args: Any): String =
            stringResource(R.string.app_settings_section_service, *args)

        val `Network`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_section_network)

        @Composable
        fun `Network`(vararg args: Any): String =
            stringResource(R.string.app_settings_section_network, *args)

        val `Cleanup`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_section_cleanup)

        @Composable
        fun `Cleanup`(vararg args: Any): String =
            stringResource(R.string.app_settings_section_cleanup, *args)
    }

    object `Behavior` {
        val `AutoStartTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_behavior_auto_start_title)

        @Composable
        fun `AutoStartTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_behavior_auto_start_title, *args)

        val `AutoStartSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_behavior_auto_start_summary)

        @Composable
        fun `AutoStartSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_behavior_auto_start_summary, *args)

        val `AutoUpdateOnStartTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_behavior_auto_update_on_start_title)

        @Composable
        fun `AutoUpdateOnStartTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_behavior_auto_update_on_start_title, *args)

        val `AutoUpdateOnStartSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_behavior_auto_update_on_start_summary
                )

        @Composable
        fun `AutoUpdateOnStartSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_behavior_auto_update_on_start_summary, *args)

        val `OneChinaTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_behavior_one_china_title)

        @Composable
        fun `OneChinaTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_behavior_one_china_title, *args)

        val `OneChinaSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_behavior_one_china_summary)

        @Composable
        fun `OneChinaSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_behavior_one_china_summary, *args)
    }

    object `Interface` {
        val `LanguageTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_language_title)

        @Composable
        fun `LanguageTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_language_title, *args)

        val `LanguageSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_language_summary)

        @Composable
        fun `LanguageSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_language_summary, *args)

        val `LanguageSystem`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_language_system)

        @Composable
        fun `LanguageSystem`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_language_system, *args)

        val `LanguageChinese`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_language_chinese)

        @Composable
        fun `LanguageChinese`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_language_chinese, *args)

        val `LanguageEnglish`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_language_english)

        @Composable
        fun `LanguageEnglish`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_language_english, *args)

        val `ThemeModeTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_theme_mode_title)

        @Composable
        fun `ThemeModeTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_theme_mode_title, *args)

        val `ThemeModeSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_theme_mode_summary)

        @Composable
        fun `ThemeModeSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_theme_mode_summary, *args)

        val `ThemeModeSystem`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_theme_mode_system)

        @Composable
        fun `ThemeModeSystem`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_theme_mode_system, *args)

        val `ThemeModeLight`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_theme_mode_light)

        @Composable
        fun `ThemeModeLight`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_theme_mode_light, *args)

        val `ThemeModeDark`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_theme_mode_dark)

        @Composable
        fun `ThemeModeDark`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_theme_mode_dark, *args)

        val `ColorThemeTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_color_theme_title)

        @Composable
        fun `ColorThemeTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_color_theme_title, *args)

        val `ColorThemeSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_color_theme_summary)

        @Composable
        fun `ColorThemeSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_color_theme_summary, *args)

        val `ColorThemePickerTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_interface_color_theme_picker_title)

        @Composable
        fun `ColorThemePickerTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_color_theme_picker_title, *args)

        val `ColorThemeCodeLabel`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_interface_color_theme_code_label)

        @Composable
        fun `ColorThemeCodeLabel`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_color_theme_code_label, *args)

        val `ColorThemeCustomSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_interface_color_theme_custom_summary
                )

        @Composable
        fun `ColorThemeCustomSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_color_theme_custom_summary, *args)

        val `TopBarBlurTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_top_bar_blur_title)

        @Composable
        fun `TopBarBlurTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_top_bar_blur_title, *args)

        val `TopBarBlurSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_top_bar_blur_summary)

        @Composable
        fun `TopBarBlurSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_top_bar_blur_summary, *args)

        val `BottomBarLiquidGlassTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_interface_bottom_bar_liquid_glass_title
                )

        @Composable
        fun `BottomBarLiquidGlassTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_bottom_bar_liquid_glass_title, *args)

        val `BottomBarLiquidGlassSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_interface_bottom_bar_liquid_glass_summary
                )

        @Composable
        fun `BottomBarLiquidGlassSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_bottom_bar_liquid_glass_summary, *args)

        val `AutoHideNavbarTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_interface_auto_hide_navbar_title)

        @Composable
        fun `AutoHideNavbarTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_auto_hide_navbar_title, *args)

        val `AutoHideNavbarSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_interface_auto_hide_navbar_summary)

        @Composable
        fun `AutoHideNavbarSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_auto_hide_navbar_summary, *args)

        val `PageScaleTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_page_scale_title)

        @Composable
        fun `PageScaleTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_page_scale_title, *args)

        val `PageScaleSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_page_scale_summary)

        @Composable
        fun `PageScaleSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_page_scale_summary, *args)

        val `PageScaleRange`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_page_scale_range)

        @Composable
        fun `PageScaleRange`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_page_scale_range, *args)

        val `HideIconTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_hide_icon_title)

        @Composable
        fun `HideIconTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_hide_icon_title, *args)

        val `HideIconSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_interface_hide_icon_summary)

        @Composable
        fun `HideIconSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_hide_icon_summary, *args)

        val `HideFromRecentsTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_interface_hide_from_recents_title)

        @Composable
        fun `HideFromRecentsTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_hide_from_recents_title, *args)

        val `HideFromRecentsSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.app_settings_interface_hide_from_recents_summary)

        @Composable
        fun `HideFromRecentsSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_interface_hide_from_recents_summary, *args)
    }

    object `ServiceSection` {
        val `TrafficNotificationTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_service_section_traffic_notification_title
                )

        @Composable
        fun `TrafficNotificationTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_service_section_traffic_notification_title, *args)

        val `TrafficNotificationSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_service_section_traffic_notification_summary
                )

        @Composable
        fun `TrafficNotificationSummary`(vararg args: Any): String =
            stringResource(
                R.string.app_settings_service_section_traffic_notification_summary,
                *args,
            )

        val `SingleNodeTestTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_service_section_single_node_test_title
                )

        @Composable
        fun `SingleNodeTestTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_service_section_single_node_test_title, *args)

        val `SingleNodeTestSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_service_section_single_node_test_summary
                )

        @Composable
        fun `SingleNodeTestSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_service_section_single_node_test_summary, *args)

        val `AutoStartLogRecordingTitle`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_service_section_auto_start_log_recording_title
                )

        @Composable
        fun `AutoStartLogRecordingTitle`(vararg args: Any): String =
            stringResource(
                R.string.app_settings_service_section_auto_start_log_recording_title,
                *args,
            )

        val `AutoStartLogRecordingSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_service_section_auto_start_log_recording_summary
                )

        @Composable
        fun `AutoStartLogRecordingSummary`(vararg args: Any): String =
            stringResource(
                R.string.app_settings_service_section_auto_start_log_recording_summary,
                *args,
            )
    }

    object `Network` {
        val `CustomUserAgentTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_network_custom_user_agent_title)

        @Composable
        fun `CustomUserAgentTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_network_custom_user_agent_title, *args)

        val `CustomUserAgentSummaryDefault`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.app_settings_network_custom_user_agent_summary_default
                )

        @Composable
        fun `CustomUserAgentSummaryDefault`(vararg args: Any): String =
            stringResource(R.string.app_settings_network_custom_user_agent_summary_default, *args)
    }

    object `Cleanup` {
        val `AutoEnabledTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_auto_enabled_title)

        @Composable
        fun `AutoEnabledTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_auto_enabled_title, *args)

        val `AutoEnabledSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_auto_enabled_summary)

        @Composable
        fun `AutoEnabledSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_auto_enabled_summary, *args)

        val `PolicyTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_policy_title)

        @Composable
        fun `PolicyTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_policy_title, *args)

        val `PolicySummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_policy_summary)

        @Composable
        fun `PolicySummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_policy_summary, *args)

        val `PolicyAggressive`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_policy_aggressive)

        @Composable
        fun `PolicyAggressive`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_policy_aggressive, *args)

        val `PolicyBalanced`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_policy_balanced)

        @Composable
        fun `PolicyBalanced`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_policy_balanced, *args)

        val `PolicyConservative`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_policy_conservative)

        @Composable
        fun `PolicyConservative`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_policy_conservative, *args)

        val `ThresholdTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_threshold_title)

        @Composable
        fun `ThresholdTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_threshold_title, *args)

        val `ThresholdSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_threshold_summary)

        @Composable
        fun `ThresholdSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_threshold_summary, *args)

        val `ThresholdRange`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_threshold_range)

        @Composable
        fun `ThresholdRange`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_threshold_range, *args)

        val `IntervalTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_interval_title)

        @Composable
        fun `IntervalTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_interval_title, *args)

        val `IntervalSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_interval_summary)

        @Composable
        fun `IntervalSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_interval_summary, *args)

        val `IntervalRange`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_interval_range)

        @Composable
        fun `IntervalRange`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_interval_range, *args)

        val `IntervalUnit`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_interval_unit)

        @Composable
        fun `IntervalUnit`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_interval_unit, *args)

        val `CleanupNowTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_cleanup_now_title)

        @Composable
        fun `CleanupNowTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_cleanup_now_title, *args)

        val `LastRunSummary`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_last_run_summary)

        @Composable
        fun `LastRunSummary`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_last_run_summary, *args)

        val `LastRunNever`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_last_run_never)

        @Composable
        fun `LastRunNever`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_last_run_never, *args)

        val `ArchiveSkipped`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_archive_skipped)

        @Composable
        fun `ArchiveSkipped`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_archive_skipped, *args)

        val `CleanupNowSuccess`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_cleanup_now_success)

        @Composable
        fun `CleanupNowSuccess`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_cleanup_now_success, *args)

        val `CleanupNowSkipped`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_cleanup_cleanup_now_skipped)

        @Composable
        fun `CleanupNowSkipped`(vararg args: Any): String =
            stringResource(R.string.app_settings_cleanup_cleanup_now_skipped, *args)
    }

    object `WarningDialog` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_warning_dialog_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.app_settings_warning_dialog_title, *args)

        val `HideIconMsg1`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_warning_dialog_hide_icon_msg1)

        @Composable
        fun `HideIconMsg1`(vararg args: Any): String =
            stringResource(R.string.app_settings_warning_dialog_hide_icon_msg1, *args)

        val `HideIconMsg2`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_warning_dialog_hide_icon_msg2)

        @Composable
        fun `HideIconMsg2`(vararg args: Any): String =
            stringResource(R.string.app_settings_warning_dialog_hide_icon_msg2, *args)
    }

    object `EditDialog` {
        val `UserAgentTitle`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_edit_dialog_user_agent_title)

        @Composable
        fun `UserAgentTitle`(vararg args: Any): String =
            stringResource(R.string.app_settings_edit_dialog_user_agent_title, *args)
    }

    object `Button` {
        val `Cancel`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_button_cancel)

        @Composable
        fun `Cancel`(vararg args: Any): String =
            stringResource(R.string.app_settings_button_cancel, *args)

        val `Apply`: String
            get() = LocaleBootstrap.getString(R.string.app_settings_button_apply)

        @Composable
        fun `Apply`(vararg args: Any): String =
            stringResource(R.string.app_settings_button_apply, *args)
    }
}

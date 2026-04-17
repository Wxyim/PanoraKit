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

object MLangSettings {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.settings_title)

    @Composable
    fun `Title`(vararg args: Any): String = stringResource(R.string.settings_title, *args)

    object `Section` {
        val `UiSettings`: String
            get() = LocaleBootstrap.getString(R.string.settings_section_ui_settings)

        @Composable
        fun `UiSettings`(vararg args: Any): String =
            stringResource(R.string.settings_section_ui_settings, *args)

        val `More`: String
            get() = LocaleBootstrap.getString(R.string.settings_section_more)

        @Composable
        fun `More`(vararg args: Any): String = stringResource(R.string.settings_section_more, *args)

        val `Advanced`: String
            get() = LocaleBootstrap.getString(R.string.settings_section_advanced)

        @Composable
        fun `Advanced`(vararg args: Any): String =
            stringResource(R.string.settings_section_advanced, *args)
    }

    object `UiSettings` {
        val `App`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_app)

        @Composable
        fun `App`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_app, *args)

        val `AppSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_app_summary)

        @Composable
        fun `AppSummary`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_app_summary, *args)

        val `Network`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_network)

        @Composable
        fun `Network`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_network, *args)

        val `NetworkSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_network_summary)

        @Composable
        fun `NetworkSummary`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_network_summary, *args)

        val `Override`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_override)

        @Composable
        fun `Override`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_override, *args)

        val `OverrideSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_override_summary)

        @Composable
        fun `OverrideSummary`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_override_summary, *args)

        val `MetaFeatures`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_meta_features)

        @Composable
        fun `MetaFeatures`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_meta_features, *args)

        val `MetaFeaturesSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_ui_settings_meta_features_summary)

        @Composable
        fun `MetaFeaturesSummary`(vararg args: Any): String =
            stringResource(R.string.settings_ui_settings_meta_features_summary, *args)
    }

    object `More` {
        val `Lab`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_lab)

        @Composable
        fun `Lab`(vararg args: Any): String = stringResource(R.string.settings_more_lab, *args)

        val `LabSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_lab_summary)

        @Composable
        fun `LabSummary`(vararg args: Any): String =
            stringResource(R.string.settings_more_lab_summary, *args)

        val `TrafficStatistics`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_traffic_statistics)

        @Composable
        fun `TrafficStatistics`(vararg args: Any): String =
            stringResource(R.string.settings_more_traffic_statistics, *args)

        val `TrafficStatisticsSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_traffic_statistics_summary)

        @Composable
        fun `TrafficStatisticsSummary`(vararg args: Any): String =
            stringResource(R.string.settings_more_traffic_statistics_summary, *args)

        val `Logs`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_logs)

        @Composable
        fun `Logs`(vararg args: Any): String = stringResource(R.string.settings_more_logs, *args)

        val `LogsSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_logs_summary)

        @Composable
        fun `LogsSummary`(vararg args: Any): String =
            stringResource(R.string.settings_more_logs_summary, *args)

        val `About`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_about)

        @Composable
        fun `About`(vararg args: Any): String = stringResource(R.string.settings_more_about, *args)

        val `AboutSummary`: String
            get() = LocaleBootstrap.getString(R.string.settings_more_about_summary)

        @Composable
        fun `AboutSummary`(vararg args: Any): String =
            stringResource(R.string.settings_more_about_summary, *args)
    }

    object `Error` {
        val `WebviewFailed`: String
            get() = LocaleBootstrap.getString(R.string.settings_error_webview_failed)

        @Composable
        fun `WebviewFailed`(vararg args: Any): String =
            stringResource(R.string.settings_error_webview_failed, *args)
    }
}

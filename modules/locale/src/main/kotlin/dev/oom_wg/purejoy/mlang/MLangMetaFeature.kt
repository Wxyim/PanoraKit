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
import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.core.locale.R

object MLangMetaFeature {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.meta_feature_title)

    @Composable
    fun `Title`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.meta_feature_title, *args)

    object `RecentRequests` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_recent_requests_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_recent_requests_title, *args)

        val `Summary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_recent_requests_summary)

        @Composable
        fun `Summary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_recent_requests_summary, *args)
    }

    object `RuntimeConfig` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_runtime_config_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_runtime_config_title, *args)

        val `Summary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_runtime_config_summary)

        @Composable
        fun `Summary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_runtime_config_summary, *args)

        val `LoadFailed`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_runtime_config_load_failed)

        @Composable
        fun `LoadFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_runtime_config_load_failed, *args)

        val `NoActiveProfile`: String
            get() =
                LocaleBootstrap.getString(R.string.meta_feature_runtime_config_no_active_profile)

        @Composable
        fun `NoActiveProfile`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_runtime_config_no_active_profile, *args)

        val `ConfigNotFound`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_runtime_config_config_not_found)

        @Composable
        fun `ConfigNotFound`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_runtime_config_config_not_found, *args)

        val `PreviewTitle`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_runtime_config_preview_title)

        @Composable
        fun `PreviewTitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_runtime_config_preview_title, *args)

        val `PreviewTitleWithProfile`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.meta_feature_runtime_config_preview_title_with_profile
                )

        @Composable
        fun `PreviewTitleWithProfile`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.meta_feature_runtime_config_preview_title_with_profile,
                *args,
            )

        val `RuntimeConfigFetchFailed`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.meta_feature_runtime_config_runtime_config_fetch_failed
                )

        @Composable
        fun `RuntimeConfigFetchFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.meta_feature_runtime_config_runtime_config_fetch_failed,
                *args,
            )

        val `RuntimeConfigUnauthorized`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.meta_feature_runtime_config_runtime_config_unauthorized
                )

        @Composable
        fun `RuntimeConfigUnauthorized`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.meta_feature_runtime_config_runtime_config_unauthorized,
                *args,
            )

        val `RuntimeConfigNotRunning`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.meta_feature_runtime_config_runtime_config_not_running
                )

        @Composable
        fun `RuntimeConfigNotRunning`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.meta_feature_runtime_config_runtime_config_not_running,
                *args,
            )
    }

    object `GeoX` {
        val `OnlineUpdateTitle`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_geo_x_online_update_title)

        @Composable
        fun `OnlineUpdateTitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_geo_x_online_update_title, *args)

        val `OnlineUpdateSummary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_geo_x_online_update_summary)

        @Composable
        fun `OnlineUpdateSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_geo_x_online_update_summary, *args)
    }

    object `Download` {
        val `DialogTitle`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_download_dialog_title)

        @Composable
        fun `DialogTitle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_download_dialog_title, *args)

        val `SelectFiles`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_download_select_files)

        @Composable
        fun `SelectFiles`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_download_select_files, *args)

        val `DownloadComplete`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_download_download_complete)

        @Composable
        fun `DownloadComplete`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_download_download_complete, *args)
    }

    object `Dashboard` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_title, *args)

        val `Port`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_port)

        @Composable
        fun `Port`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_port, *args)

        val `PortSummary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_port_summary)

        @Composable
        fun `PortSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_port_summary, *args)

        val `Secret`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_secret)

        @Composable
        fun `Secret`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_secret, *args)

        val `SecretSummary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_secret_summary)

        @Composable
        fun `SecretSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_secret_summary, *args)

        val `ShowSecret`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_show_secret)

        @Composable
        fun `ShowSecret`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_show_secret, *args)

        val `Download`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_download)

        @Composable
        fun `Download`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_download, *args)

        val `DownloadSummary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_download_summary)

        @Composable
        fun `DownloadSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_download_summary, *args)

        val `DownloadOk`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_download_ok)

        @Composable
        fun `DownloadOk`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_download_ok, *args)

        val `DownloadFailed`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_download_failed)

        @Composable
        fun `DownloadFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_download_failed, *args)

        val `Launch`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_launch)

        @Composable
        fun `Launch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_launch, *args)

        val `LaunchSummary`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_launch_summary)

        @Composable
        fun `LaunchSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_launch_summary, *args)

        val `LaunchDisabled`: String
            get() = LocaleBootstrap.getString(R.string.meta_feature_dashboard_launch_disabled)

        @Composable
        fun `LaunchDisabled`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.meta_feature_dashboard_launch_disabled, *args)
    }
}

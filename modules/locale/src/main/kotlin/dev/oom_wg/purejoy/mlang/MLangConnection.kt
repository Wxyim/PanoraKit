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

object MLangConnection {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.connection_title)

    @Composable
    fun `Title`(vararg args: Any): String = stringResource(R.string.connection_title, *args)

    val `Summary`: String
        get() = LocaleBootstrap.getString(R.string.connection_summary)

    @Composable
    fun `Summary`(vararg args: Any): String = stringResource(R.string.connection_summary, *args)

    object `Tab` {
        val `Active`: String
            get() = LocaleBootstrap.getString(R.string.connection_tab_active)

        @Composable
        fun `Active`(vararg args: Any): String =
            stringResource(R.string.connection_tab_active, *args)

        val `Closed`: String
            get() = LocaleBootstrap.getString(R.string.connection_tab_closed)

        @Composable
        fun `Closed`(vararg args: Any): String =
            stringResource(R.string.connection_tab_closed, *args)
    }

    val `Search`: String
        get() = LocaleBootstrap.getString(R.string.connection_search)

    @Composable
    fun `Search`(vararg args: Any): String = stringResource(R.string.connection_search, *args)

    val `SearchHint`: String
        get() = LocaleBootstrap.getString(R.string.connection_search_hint)

    @Composable
    fun `SearchHint`(vararg args: Any): String =
        stringResource(R.string.connection_search_hint, *args)

    val `SortBy`: String
        get() = LocaleBootstrap.getString(R.string.connection_sort_by)

    @Composable
    fun `SortBy`(vararg args: Any): String = stringResource(R.string.connection_sort_by, *args)

    object `Sort` {
        val `Time`: String
            get() = LocaleBootstrap.getString(R.string.connection_sort_time)

        @Composable
        fun `Time`(vararg args: Any): String = stringResource(R.string.connection_sort_time, *args)

        val `Upload`: String
            get() = LocaleBootstrap.getString(R.string.connection_sort_upload)

        @Composable
        fun `Upload`(vararg args: Any): String =
            stringResource(R.string.connection_sort_upload, *args)

        val `Download`: String
            get() = LocaleBootstrap.getString(R.string.connection_sort_download)

        @Composable
        fun `Download`(vararg args: Any): String =
            stringResource(R.string.connection_sort_download, *args)

        val `Host`: String
            get() = LocaleBootstrap.getString(R.string.connection_sort_host)

        @Composable
        fun `Host`(vararg args: Any): String = stringResource(R.string.connection_sort_host, *args)
    }

    val `Loading`: String
        get() = LocaleBootstrap.getString(R.string.connection_loading)

    @Composable
    fun `Loading`(vararg args: Any): String = stringResource(R.string.connection_loading, *args)

    val `Empty`: String
        get() = LocaleBootstrap.getString(R.string.connection_empty)

    @Composable
    fun `Empty`(vararg args: Any): String = stringResource(R.string.connection_empty, *args)

    val `NoResults`: String
        get() = LocaleBootstrap.getString(R.string.connection_no_results)

    @Composable
    fun `NoResults`(vararg args: Any): String =
        stringResource(R.string.connection_no_results, *args)

    object `RelativeTime` {
        val `JustNow`: String
            get() = LocaleBootstrap.getString(R.string.connection_relative_time_just_now)

        @Composable
        fun `JustNow`(vararg args: Any): String =
            stringResource(R.string.connection_relative_time_just_now, *args)

        val `MinutesAgo`: String
            get() = LocaleBootstrap.getString(R.string.connection_relative_time_minutes_ago)

        @Composable
        fun `MinutesAgo`(vararg args: Any): String =
            stringResource(R.string.connection_relative_time_minutes_ago, *args)

        val `HoursAgo`: String
            get() = LocaleBootstrap.getString(R.string.connection_relative_time_hours_ago)

        @Composable
        fun `HoursAgo`(vararg args: Any): String =
            stringResource(R.string.connection_relative_time_hours_ago, *args)

        val `DaysAgo`: String
            get() = LocaleBootstrap.getString(R.string.connection_relative_time_days_ago)

        @Composable
        fun `DaysAgo`(vararg args: Any): String =
            stringResource(R.string.connection_relative_time_days_ago, *args)
    }

    object `Detail` {
        val `Info`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_info)

        @Composable
        fun `Info`(vararg args: Any): String =
            stringResource(R.string.connection_detail_info, *args)

        val `Protocol`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_protocol)

        @Composable
        fun `Protocol`(vararg args: Any): String =
            stringResource(R.string.connection_detail_protocol, *args)

        val `SourceApp`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_source_app)

        @Composable
        fun `SourceApp`(vararg args: Any): String =
            stringResource(R.string.connection_detail_source_app, *args)

        val `PackageName`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_package_name)

        @Composable
        fun `PackageName`(vararg args: Any): String =
            stringResource(R.string.connection_detail_package_name, *args)

        val `Process`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_process)

        @Composable
        fun `Process`(vararg args: Any): String =
            stringResource(R.string.connection_detail_process, *args)

        val `SourceAddress`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_source_address)

        @Composable
        fun `SourceAddress`(vararg args: Any): String =
            stringResource(R.string.connection_detail_source_address, *args)

        val `DestinationAddress`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_destination_address)

        @Composable
        fun `DestinationAddress`(vararg args: Any): String =
            stringResource(R.string.connection_detail_destination_address, *args)

        val `Duration`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_duration)

        @Composable
        fun `Duration`(vararg args: Any): String =
            stringResource(R.string.connection_detail_duration, *args)

        val `Upload`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_upload)

        @Composable
        fun `Upload`(vararg args: Any): String =
            stringResource(R.string.connection_detail_upload, *args)

        val `Download`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_download)

        @Composable
        fun `Download`(vararg args: Any): String =
            stringResource(R.string.connection_detail_download, *args)

        val `Rule`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_rule)

        @Composable
        fun `Rule`(vararg args: Any): String =
            stringResource(R.string.connection_detail_rule, *args)

        val `Type`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_type)

        @Composable
        fun `Type`(vararg args: Any): String =
            stringResource(R.string.connection_detail_type, *args)

        val `Content`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_content)

        @Composable
        fun `Content`(vararg args: Any): String =
            stringResource(R.string.connection_detail_content, *args)
    }
}

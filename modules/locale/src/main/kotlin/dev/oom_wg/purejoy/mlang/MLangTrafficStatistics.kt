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

object MLangTrafficStatistics {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.traffic_statistics_title)

    @Composable
    fun `Title`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.traffic_statistics_title, *args)

    val `OverviewTitle`: String
        get() = LocaleBootstrap.getString(R.string.traffic_statistics_overview_title)

    @Composable
    fun `OverviewTitle`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.traffic_statistics_overview_title, *args)


    object `RecentRequests` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_title, *args)

        val `Summary`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_summary)

        @Composable
        fun `Summary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_summary, *args)

        val `Empty`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_empty)

        @Composable
        fun `Empty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_empty, *args)

        val `UnknownRequest`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.traffic_statistics_recent_requests_unknown_request
                )

        @Composable
        fun `UnknownRequest`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.traffic_statistics_recent_requests_unknown_request,
                *args,
            )

        val `Count`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_count)

        @Composable
        fun `Count`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_recent_requests_count, *args)
    }

    object `Status` {
        val `Active`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_status_active)

        @Composable
        fun `Active`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_status_active, *args)

        val `Closed`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_status_closed)

        @Composable
        fun `Closed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_status_closed, *args)
    }

    object `RelativeTime` {
        val `JustNow`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_relative_time_just_now)

        @Composable
        fun `JustNow`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_relative_time_just_now, *args)

        val `MinutesAgo`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_relative_time_minutes_ago)

        @Composable
        fun `MinutesAgo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_relative_time_minutes_ago, *args)

        val `HoursAgo`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_relative_time_hours_ago)

        @Composable
        fun `HoursAgo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_relative_time_hours_ago, *args)
    }

    object `TimeRange` {
        val `Today`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_time_range_today)

        @Composable
        fun `Today`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_time_range_today, *args)

        val `Week`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_time_range_week)

        @Composable
        fun `Week`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_time_range_week, *args)
    }

    object `Summary` {
        val `TodayTraffic`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_summary_today_traffic)

        @Composable
        fun `TodayTraffic`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_summary_today_traffic, *args)

        val `WeekTraffic`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_summary_week_traffic)

        @Composable
        fun `WeekTraffic`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_summary_week_traffic, *args)
    }

    object `Compare` {
        val `MoreThanYesterday`: String
            get() =
                LocaleBootstrap.getString(R.string.traffic_statistics_compare_more_than_yesterday)

        @Composable
        fun `MoreThanYesterday`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.traffic_statistics_compare_more_than_yesterday,
                *args,
            )

        val `LessThanYesterday`: String
            get() =
                LocaleBootstrap.getString(R.string.traffic_statistics_compare_less_than_yesterday)

        @Composable
        fun `LessThanYesterday`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.traffic_statistics_compare_less_than_yesterday,
                *args,
            )

        val `SameAsYesterday`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_compare_same_as_yesterday)

        @Composable
        fun `SameAsYesterday`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_compare_same_as_yesterday, *args)

        val `WeekStats`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_compare_week_stats)

        @Composable
        fun `WeekStats`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_compare_week_stats, *args)
    }

    object `Chart` {
        val `Hourly`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_chart_hourly)

        @Composable
        fun `Hourly`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_chart_hourly, *args)

        val `Daily`: String
            get() = LocaleBootstrap.getString(R.string.traffic_statistics_chart_daily)

        @Composable
        fun `Daily`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.traffic_statistics_chart_daily, *args)
    }
}

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

object MLangService {
    object `Notification` {
        val `Running`: String
            get() = LocaleBootstrap.getString(R.string.service_notification_running)

        @Composable
        fun `Running`(vararg args: Any): String =
            stringResource(R.string.service_notification_running, *args)

        val `TrafficFormat`: String
            get() = LocaleBootstrap.getString(R.string.service_notification_traffic_format)

        @Composable
        fun `TrafficFormat`(vararg args: Any): String =
            stringResource(R.string.service_notification_traffic_format, *args)

        val `UnknownProfile`: String
            get() = LocaleBootstrap.getString(R.string.service_notification_unknown_profile)

        @Composable
        fun `UnknownProfile`(vararg args: Any): String =
            stringResource(R.string.service_notification_unknown_profile, *args)
    }

    object `Tile` {
        val `ClickToOpen`: String
            get() = LocaleBootstrap.getString(R.string.service_tile_click_to_open)

        @Composable
        fun `ClickToOpen`(vararg args: Any): String =
            stringResource(R.string.service_tile_click_to_open, *args)

        val `ClickToStartProxy`: String
            get() = LocaleBootstrap.getString(R.string.service_tile_click_to_start_proxy)

        @Composable
        fun `ClickToStartProxy`(vararg args: Any): String =
            stringResource(R.string.service_tile_click_to_start_proxy, *args)

        val `ClickToStopProxy`: String
            get() = LocaleBootstrap.getString(R.string.service_tile_click_to_stop_proxy)

        @Composable
        fun `ClickToStopProxy`(vararg args: Any): String =
            stringResource(R.string.service_tile_click_to_stop_proxy, *args)

        val `Connecting`: String
            get() = LocaleBootstrap.getString(R.string.service_tile_connecting)

        @Composable
        fun `Connecting`(vararg args: Any): String =
            stringResource(R.string.service_tile_connecting, *args)

        val `Disconnecting`: String
            get() = LocaleBootstrap.getString(R.string.service_tile_disconnecting)

        @Composable
        fun `Disconnecting`(vararg args: Any): String =
            stringResource(R.string.service_tile_disconnecting, *args)
    }

    object `AutoRestart` {
        val `ChannelName`: String
            get() = LocaleBootstrap.getString(R.string.service_auto_restart_channel_name)

        @Composable
        fun `ChannelName`(vararg args: Any): String =
            stringResource(R.string.service_auto_restart_channel_name, *args)

        val `ChannelDescription`: String
            get() = LocaleBootstrap.getString(R.string.service_auto_restart_channel_description)

        @Composable
        fun `ChannelDescription`(vararg args: Any): String =
            stringResource(R.string.service_auto_restart_channel_description, *args)

        val `Checking`: String
            get() = LocaleBootstrap.getString(R.string.service_auto_restart_checking)

        @Composable
        fun `Checking`(vararg args: Any): String =
            stringResource(R.string.service_auto_restart_checking, *args)
    }

    object `LogRecord` {
        val `ChannelName`: String
            get() = LocaleBootstrap.getString(R.string.service_log_record_channel_name)

        @Composable
        fun `ChannelName`(vararg args: Any): String =
            stringResource(R.string.service_log_record_channel_name, *args)

        val `ChannelDescription`: String
            get() = LocaleBootstrap.getString(R.string.service_log_record_channel_description)

        @Composable
        fun `ChannelDescription`(vararg args: Any): String =
            stringResource(R.string.service_log_record_channel_description, *args)

        val `NotificationTitle`: String
            get() = LocaleBootstrap.getString(R.string.service_log_record_notification_title)

        @Composable
        fun `NotificationTitle`(vararg args: Any): String =
            stringResource(R.string.service_log_record_notification_title, *args)

        val `NotificationContent`: String
            get() = LocaleBootstrap.getString(R.string.service_log_record_notification_content)

        @Composable
        fun `NotificationContent`(vararg args: Any): String =
            stringResource(R.string.service_log_record_notification_content, *args)

        val `ActionStop`: String
            get() = LocaleBootstrap.getString(R.string.service_log_record_action_stop)

        @Composable
        fun `ActionStop`(vararg args: Any): String =
            stringResource(R.string.service_log_record_action_stop, *args)
    }
}

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

object MLangHome {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.home_title)

    @Composable fun `Title`(vararg args: Any): String = stringResource(R.string.home_title, *args)

    object `Message` {
        val `ConfigSwitched`: String
            get() = LocaleBootstrap.getString(R.string.home_message_config_switched)

        @Composable
        fun `ConfigSwitched`(vararg args: Any): String =
            stringResource(R.string.home_message_config_switched, *args)

        val `ConfigSwitchFailed`: String
            get() = LocaleBootstrap.getString(R.string.home_message_config_switch_failed)

        @Composable
        fun `ConfigSwitchFailed`(vararg args: Any): String =
            stringResource(R.string.home_message_config_switch_failed, *args)

        val `Preparing`: String
            get() = LocaleBootstrap.getString(R.string.home_message_preparing)

        @Composable
        fun `Preparing`(vararg args: Any): String =
            stringResource(R.string.home_message_preparing, *args)

        val `StartFailed`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed)

        @Composable
        fun `StartFailed`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed, *args)

        val `StartFailedDialogTitle`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_dialog_title)

        @Composable
        fun `StartFailedDialogTitle`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_dialog_title, *args)

        val `StartFailedSyntaxReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_syntax_reason)

        @Composable
        fun `StartFailedSyntaxReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_syntax_reason, *args)

        val `StartFailedRemoteReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_remote_reason)

        @Composable
        fun `StartFailedRemoteReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_remote_reason, *args)

        val `StartFailedNetworkReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_network_reason)

        @Composable
        fun `StartFailedNetworkReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_network_reason, *args)

        val `StartFailedPermissionReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_permission_reason)

        @Composable
        fun `StartFailedPermissionReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_permission_reason, *args)

        val `StartFailedProfileReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_profile_reason)

        @Composable
        fun `StartFailedProfileReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_profile_reason, *args)

        val `StartFailedRuntimeServiceReason`: String
            get() =
                LocaleBootstrap.getString(R.string.home_message_start_failed_runtime_service_reason)

        @Composable
        fun `StartFailedRuntimeServiceReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_runtime_service_reason, *args)

        val `StartFailedRuntimeControlReason`: String
            get() =
                LocaleBootstrap.getString(R.string.home_message_start_failed_runtime_control_reason)

        @Composable
        fun `StartFailedRuntimeControlReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_runtime_control_reason, *args)

        val `StartFailedEnvironmentReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_environment_reason)

        @Composable
        fun `StartFailedEnvironmentReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_environment_reason, *args)

        val `StartFailedUnknownReason`: String
            get() = LocaleBootstrap.getString(R.string.home_message_start_failed_unknown_reason)

        @Composable
        fun `StartFailedUnknownReason`(vararg args: Any): String =
            stringResource(R.string.home_message_start_failed_unknown_reason, *args)

        val `StopFailed`: String
            get() = LocaleBootstrap.getString(R.string.home_message_stop_failed)

        @Composable
        fun `StopFailed`(vararg args: Any): String =
            stringResource(R.string.home_message_stop_failed, *args)
    }

    object `Control` {
        val `HintAddProfile`: String
            get() = LocaleBootstrap.getString(R.string.home_control_hint_add_profile)

        @Composable
        fun `HintAddProfile`(vararg args: Any): String =
            stringResource(R.string.home_control_hint_add_profile, *args)

        val `HintEnableProfile`: String
            get() = LocaleBootstrap.getString(R.string.home_control_hint_enable_profile)

        @Composable
        fun `HintEnableProfile`(vararg args: Any): String =
            stringResource(R.string.home_control_hint_enable_profile, *args)
    }

    object `Profile` {
        val `NoProfile`: String
            get() = LocaleBootstrap.getString(R.string.home_profile_no_profile)

        @Composable
        fun `NoProfile`(vararg args: Any): String =
            stringResource(R.string.home_profile_no_profile, *args)

        val `Direct`: String
            get() = LocaleBootstrap.getString(R.string.home_profile_direct)

        @Composable
        fun `Direct`(vararg args: Any): String = stringResource(R.string.home_profile_direct, *args)

        val `Proxy`: String
            get() = LocaleBootstrap.getString(R.string.home_profile_proxy)

        @Composable
        fun `Proxy`(vararg args: Any): String = stringResource(R.string.home_profile_proxy, *args)

        val `Reject`: String
            get() = LocaleBootstrap.getString(R.string.home_profile_reject)

        @Composable
        fun `Reject`(vararg args: Any): String = stringResource(R.string.home_profile_reject, *args)

        val `Global`: String
            get() = LocaleBootstrap.getString(R.string.home_profile_global)

        @Composable
        fun `Global`(vararg args: Any): String = stringResource(R.string.home_profile_global, *args)

        val `Rule`: String
            get() = LocaleBootstrap.getString(R.string.home_profile_rule)

        @Composable
        fun `Rule`(vararg args: Any): String = stringResource(R.string.home_profile_rule, *args)
    }

    object `NodeInfo` {
        val `Node`: String
            get() = LocaleBootstrap.getString(R.string.home_node_info_node)

        @Composable
        fun `Node`(vararg args: Any): String = stringResource(R.string.home_node_info_node, *args)

        val `Delay`: String
            get() = LocaleBootstrap.getString(R.string.home_node_info_delay)

        @Composable
        fun `Delay`(vararg args: Any): String = stringResource(R.string.home_node_info_delay, *args)

        val `Unknown`: String
            get() = LocaleBootstrap.getString(R.string.home_node_info_unknown)

        @Composable
        fun `Unknown`(vararg args: Any): String =
            stringResource(R.string.home_node_info_unknown, *args)
    }

    object `IpInfo` {
        val `ExitIp`: String
            get() = LocaleBootstrap.getString(R.string.home_ip_info_exit_ip)

        @Composable
        fun `ExitIp`(vararg args: Any): String =
            stringResource(R.string.home_ip_info_exit_ip, *args)
    }

    object `Status` {
        val `Starting`: String
            get() = LocaleBootstrap.getString(R.string.home_status_starting)

        @Composable
        fun `Starting`(vararg args: Any): String =
            stringResource(R.string.home_status_starting, *args)

        val `Running`: String
            get() = LocaleBootstrap.getString(R.string.home_status_running)

        @Composable
        fun `Running`(vararg args: Any): String =
            stringResource(R.string.home_status_running, *args)

        val `Stopping`: String
            get() = LocaleBootstrap.getString(R.string.home_status_stopping)

        @Composable
        fun `Stopping`(vararg args: Any): String =
            stringResource(R.string.home_status_stopping, *args)

        val `TapToStart`: String
            get() = LocaleBootstrap.getString(R.string.home_status_tap_to_start)

        @Composable
        fun `TapToStart`(vararg args: Any): String =
            stringResource(R.string.home_status_tap_to_start, *args)
    }
}

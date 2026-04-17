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

object MLangProviders {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.providers_title)

    @Composable
    fun `Title`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.providers_title, *args)

    object `Action` {
        val `UpdateAll`: String
            get() = LocaleBootstrap.getString(R.string.providers_action_update_all)

        @Composable
        fun `UpdateAll`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_action_update_all, *args)

        val `Update`: String
            get() = LocaleBootstrap.getString(R.string.providers_action_update)

        @Composable
        fun `Update`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_action_update, *args)

        val `Upload`: String
            get() = LocaleBootstrap.getString(R.string.providers_action_upload)

        @Composable
        fun `Upload`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_action_upload, *args)

        val `Operation`: String
            get() = LocaleBootstrap.getString(R.string.providers_action_operation)

        @Composable
        fun `Operation`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_action_operation, *args)
    }

    object `Empty` {
        val `NotRunning`: String
            get() = LocaleBootstrap.getString(R.string.providers_empty_not_running)

        @Composable
        fun `NotRunning`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_empty_not_running, *args)

        val `NotRunningHint`: String
            get() = LocaleBootstrap.getString(R.string.providers_empty_not_running_hint)

        @Composable
        fun `NotRunningHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_empty_not_running_hint, *args)

        val `NoProviders`: String
            get() = LocaleBootstrap.getString(R.string.providers_empty_no_providers)

        @Composable
        fun `NoProviders`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_empty_no_providers, *args)

        val `NoProvidersHint`: String
            get() = LocaleBootstrap.getString(R.string.providers_empty_no_providers_hint)

        @Composable
        fun `NoProvidersHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_empty_no_providers_hint, *args)
    }

    object `Type` {
        val `ProxyProviders`: String
            get() = LocaleBootstrap.getString(R.string.providers_type_proxy_providers)

        @Composable
        fun `ProxyProviders`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_type_proxy_providers, *args)

        val `RuleProviders`: String
            get() = LocaleBootstrap.getString(R.string.providers_type_rule_providers)

        @Composable
        fun `RuleProviders`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_type_rule_providers, *args)

        val `OverrideResources`: String
            get() = LocaleBootstrap.getString(R.string.providers_type_override_resources)

        @Composable
        fun `OverrideResources`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_type_override_resources, *args)
    }

    object `Transport` {
        val `Http`: String
            get() = LocaleBootstrap.getString(R.string.providers_transport_http)

        @Composable
        fun `Http`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_transport_http, *args)

        val `File`: String
            get() = LocaleBootstrap.getString(R.string.providers_transport_file)

        @Composable
        fun `File`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_transport_file, *args)

        val `Inline`: String
            get() = LocaleBootstrap.getString(R.string.providers_transport_inline)

        @Composable
        fun `Inline`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_transport_inline, *args)

        val `Compatible`: String
            get() = LocaleBootstrap.getString(R.string.providers_transport_compatible)

        @Composable
        fun `Compatible`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_transport_compatible, *args)
    }

    object `Summary` {
        val `OverrideIntervalAndCount`: String
            get() =
                LocaleBootstrap.getString(R.string.providers_summary_override_interval_and_count)

        @Composable
        fun `OverrideIntervalAndCount`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_summary_override_interval_and_count, *args)

        val `ItemCount`: String
            get() = LocaleBootstrap.getString(R.string.providers_summary_item_count)

        @Composable
        fun `ItemCount`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_summary_item_count, *args)
    }

    object `Message` {
        val `FetchFailed`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_fetch_failed)

        @Composable
        fun `FetchFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_fetch_failed, *args)

        val `UpdateSuccess`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_update_success)

        @Composable
        fun `UpdateSuccess`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_update_success, *args)

        val `UpdateFailed`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_update_failed)

        @Composable
        fun `UpdateFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_update_failed, *args)

        val `UpdateFailedResources`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_update_failed_resources)

        @Composable
        fun `UpdateFailedResources`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_update_failed_resources, *args)

        val `AllUpdated`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_all_updated)

        @Composable
        fun `AllUpdated`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_all_updated, *args)

        val `UploadSuccess`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_upload_success)

        @Composable
        fun `UploadSuccess`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_upload_success, *args)

        val `UploadFailed`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_upload_failed)

        @Composable
        fun `UploadFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_upload_failed, *args)

        val `ReadFileFailed`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_read_file_failed)

        @Composable
        fun `ReadFileFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_read_file_failed, *args)

        val `UploadSizeExceeded`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_upload_size_exceeded)

        @Composable
        fun `UploadSizeExceeded`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_upload_size_exceeded, *args)

        val `UnknownError`: String
            get() = LocaleBootstrap.getString(R.string.providers_message_unknown_error)

        @Composable
        fun `UnknownError`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.providers_message_unknown_error, *args)
    }
}

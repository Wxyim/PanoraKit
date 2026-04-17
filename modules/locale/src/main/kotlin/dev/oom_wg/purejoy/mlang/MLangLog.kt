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

object MLangLog {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.log_title)

    @Composable
    fun `Title`(vararg args: Any): String = LocaleBootstrap.getString(R.string.log_title, *args)

    object `Action` {
        val `StopRecording`: String
            get() = LocaleBootstrap.getString(R.string.log_action_stop_recording)

        @Composable
        fun `StopRecording`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_stop_recording, *args)

        val `StartRecording`: String
            get() = LocaleBootstrap.getString(R.string.log_action_start_recording)

        @Composable
        fun `StartRecording`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_start_recording, *args)

        val `Save`: String
            get() = LocaleBootstrap.getString(R.string.log_action_save)

        @Composable
        fun `Save`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_save, *args)

        val `Cleanup`: String
            get() = LocaleBootstrap.getString(R.string.log_action_cleanup)

        @Composable
        fun `Cleanup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_cleanup, *args)

        val `CleanupDone`: String
            get() = LocaleBootstrap.getString(R.string.log_action_cleanup_done)

        @Composable
        fun `CleanupDone`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_cleanup_done, *args)

        val `ExportDebugBundle`: String
            get() = LocaleBootstrap.getString(R.string.log_action_export_debug_bundle)

        @Composable
        fun `ExportDebugBundle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_export_debug_bundle, *args)

        val `ExportDebugBundleWarning`: String
            get() = LocaleBootstrap.getString(R.string.log_action_export_debug_bundle_warning)

        @Composable
        fun `ExportDebugBundleWarning`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_export_debug_bundle_warning, *args)

        val `Export`: String
            get() = LocaleBootstrap.getString(R.string.log_action_export)

        @Composable
        fun `Export`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_export, *args)

        val `Cancel`: String
            get() = LocaleBootstrap.getString(R.string.log_action_cancel)

        @Composable
        fun `Cancel`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_cancel, *args)

        val `ExportDone`: String
            get() = LocaleBootstrap.getString(R.string.log_action_export_done)

        @Composable
        fun `ExportDone`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_action_export_done, *args)
    }

    object `Empty` {
        val `NoLogs`: String
            get() = LocaleBootstrap.getString(R.string.log_empty_no_logs)

        @Composable
        fun `NoLogs`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_empty_no_logs, *args)

        val `StartRecordingHint`: String
            get() = LocaleBootstrap.getString(R.string.log_empty_start_recording_hint)

        @Composable
        fun `StartRecordingHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_empty_start_recording_hint, *args)

        val `AutoRecordHint`: String
            get() = LocaleBootstrap.getString(R.string.log_empty_auto_record_hint)

        @Composable
        fun `AutoRecordHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_empty_auto_record_hint, *args)
    }

    object `Detail` {
        val `WaitingLog`: String
            get() = LocaleBootstrap.getString(R.string.log_detail_waiting_log)

        @Composable
        fun `WaitingLog`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_detail_waiting_log, *args)

        val `WillShowWhenGenerated`: String
            get() = LocaleBootstrap.getString(R.string.log_detail_will_show_when_generated)

        @Composable
        fun `WillShowWhenGenerated`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_detail_will_show_when_generated, *args)
    }

    object `History` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.log_history_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_history_title, *args)

        val `LiveSection`: String
            get() = LocaleBootstrap.getString(R.string.log_history_live_section)

        @Composable
        fun `LiveSection`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_history_live_section, *args)

        val `Recording`: String
            get() = LocaleBootstrap.getString(R.string.log_history_recording)

        @Composable
        fun `Recording`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_history_recording, *args)

        val `ItemSummary`: String
            get() = LocaleBootstrap.getString(R.string.log_history_item_summary)

        @Composable
        fun `ItemSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_history_item_summary, *args)
    }

    object `Startup` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.log_startup_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_startup_title, *args)

        val `LiveSection`: String
            get() = LocaleBootstrap.getString(R.string.log_startup_live_section)

        @Composable
        fun `LiveSection`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_startup_live_section, *args)

        val `ItemSummary`: String
            get() = LocaleBootstrap.getString(R.string.log_startup_item_summary)

        @Composable
        fun `ItemSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.log_startup_item_summary, *args)
    }
}

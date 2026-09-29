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

object MLangComponent {
    object `ProfileCard` {
        val `Export`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_export)

        @Composable
        fun `Export`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_export, *args)

        val `Edit`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_edit)

        @Composable
        fun `Edit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_edit, *args)

        val `Update`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_update)

        @Composable
        fun `Update`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_update, *args)

        val `Delete`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_delete)

        @Composable
        fun `Delete`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_delete, *args)

        val `RemoteSubscription`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_remote_subscription)

        @Composable
        fun `RemoteSubscription`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_remote_subscription, *args)

        val `LocalFile`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_local_file)

        @Composable
        fun `LocalFile`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_local_file, *args)

        val `LocalConfig`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_local_config)

        @Composable
        fun `LocalConfig`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_local_config, *args)

        val `ClickToUpdate`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_click_to_update)

        @Composable
        fun `ClickToUpdate`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_click_to_update, *args)

        val `LocalUnvalidated`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_local_unvalidated)

        @Composable
        fun `LocalUnvalidated`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_local_unvalidated, *args)

        val `Traffic`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_traffic)

        @Composable
        fun `Traffic`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_traffic, *args)

        val `UsedTraffic`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_used_traffic)

        @Composable
        fun `UsedTraffic`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_used_traffic, *args)

        val `ExpireAt`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_expire_at)

        @Composable
        fun `ExpireAt`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_expire_at, *args)

        val `ExpireToday`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_expire_today)

        @Composable
        fun `ExpireToday`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_expire_today, *args)

        val `Expired`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_expired)

        @Composable
        fun `Expired`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_expired, *args)
    }

    object `Selector` {
        val `NotModify`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_not_modify)

        @Composable
        fun `NotModify`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_not_modify, *args)

        val `UseDefault`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_use_default)

        @Composable
        fun `UseDefault`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_use_default, *args)

        val `Enable`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_enable)

        @Composable
        fun `Enable`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_enable, *args)

        val `Disable`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_disable)

        @Composable
        fun `Disable`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_disable, *args)

        val `Replace`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_replace)

        @Composable
        fun `Replace`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_replace, *args)

        val `Prepend`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_prepend)

        @Composable
        fun `Prepend`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_prepend, *args)

        val `Append`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_append)

        @Composable
        fun `Append`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_append, *args)

        val `Merge`: String
            get() = LocaleBootstrap.getString(R.string.component_selector_merge)

        @Composable
        fun `Merge`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_selector_merge, *args)
    }

    object `Navigation` {
        val `Back`: String
            get() = LocaleBootstrap.getString(R.string.component_navigation_back)

        @Composable
        fun `Back`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_navigation_back, *args)

        val `Settings`: String
            get() = LocaleBootstrap.getString(R.string.component_navigation_settings)

        @Composable
        fun `Settings`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_navigation_settings, *args)
    }

    object `Message` {

        val `Hint`: String
            get() = LocaleBootstrap.getString(R.string.component_message_hint)

        @Composable
        fun `Hint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_message_hint, *args)

        val `Error`: String
            get() = LocaleBootstrap.getString(R.string.component_message_error)

        @Composable
        fun `Error`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_message_error, *args)
    }

    object `Button` {
        val `Cancel`: String
            get() = LocaleBootstrap.getString(R.string.component_button_cancel)

        @Composable
        fun `Cancel`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_button_cancel, *args)

        val `Confirm`: String
            get() = LocaleBootstrap.getString(R.string.component_button_confirm)

        @Composable
        fun `Confirm`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_button_confirm, *args)

        val `Clear`: String
            get() = LocaleBootstrap.getString(R.string.component_button_clear)

        @Composable
        fun `Clear`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_button_clear, *args)

        val `Edit`: String
            get() = LocaleBootstrap.getString(R.string.component_button_edit)

        @Composable
        fun `Edit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_button_edit, *args)

        val `Start`: String
            get() = LocaleBootstrap.getString(R.string.component_button_start)

        @Composable
        fun `Start`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_button_start, *args)
    }

    object `Loading` {
        val `Starting`: String
            get() = LocaleBootstrap.getString(R.string.component_loading_starting)

        @Composable
        fun `Starting`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_loading_starting, *args)
    }

    object `ConfigInput` {
        val `PortLabel`: String
            get() = LocaleBootstrap.getString(R.string.component_config_input_port_label)

        @Composable
        fun `PortLabel`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_config_input_port_label, *args)

        val `CountItems`: String
            get() = LocaleBootstrap.getString(R.string.component_config_input_count_items)

        @Composable
        fun `CountItems`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_config_input_count_items, *args)

        val `ReplaceHelper`: String
            get() = LocaleBootstrap.getString(R.string.component_config_input_replace_helper)

        @Composable
        fun `ReplaceHelper`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_config_input_replace_helper, *args)

        val `MergeHelper`: String
            get() = LocaleBootstrap.getString(R.string.component_config_input_merge_helper)

        @Composable
        fun `MergeHelper`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_config_input_merge_helper, *args)

        val `MergeNotice`: String
            get() = LocaleBootstrap.getString(R.string.component_config_input_merge_notice)

        @Composable
        fun `MergeNotice`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_config_input_merge_notice, *args)
    }

    object `BottomBar` {
        val `Home`: String
            get() = LocaleBootstrap.getString(R.string.component_bottom_bar_home)

        @Composable
        fun `Home`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_bottom_bar_home, *args)

        val `Proxy`: String
            get() = LocaleBootstrap.getString(R.string.component_bottom_bar_proxy)

        @Composable
        fun `Proxy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_bottom_bar_proxy, *args)

        val `Config`: String
            get() = LocaleBootstrap.getString(R.string.component_bottom_bar_config)

        @Composable
        fun `Config`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_bottom_bar_config, *args)

        val `Setting`: String
            get() = LocaleBootstrap.getString(R.string.component_bottom_bar_setting)

        @Composable
        fun `Setting`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_bottom_bar_setting, *args)
    }

    object `Editor` {

        object `Action` {

            val `Delete`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_delete)

            @Composable
            fun `Delete`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_delete, *args)

            val `Undo`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_undo)

            @Composable
            fun `Undo`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_undo, *args)

            val `Format`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_format)

            @Composable
            fun `Format`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_format, *args)

            val `Save`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_save)

            @Composable
            fun `Save`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_save, *args)

            val `SaveLocally`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_save_locally)

            @Composable
            fun `SaveLocally`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_save_locally, *args)

            val `SaveAndStop`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_save_and_stop)

            @Composable
            fun `SaveAndStop`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_save_and_stop, *args)

            val `Discard`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_discard)

            @Composable
            fun `Discard`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_discard, *args)

            val `Check`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_check)

            @Composable
            fun `Check`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_check, *args)
        }

        object `Dialog` {

            val `DiscardTitle`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_discard_title)

            @Composable
            fun `DiscardTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_discard_title, *args)

            val `DiscardMessage`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_discard_message)

            @Composable
            fun `DiscardMessage`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_discard_message, *args)

            val `JsonSubtitle`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_json_subtitle)

            @Composable
            fun `JsonSubtitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_json_subtitle, *args)

            val `ConfigPreviewTitle`: String
                get() =
                    LocaleBootstrap.getString(R.string.component_editor_dialog_config_preview_title)

            @Composable
            fun `ConfigPreviewTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_dialog_config_preview_title,
                    *args,
                )

            val `LocalSaving`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_local_saving)

            @Composable
            fun `LocalSaving`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_local_saving, *args)

            val `ValidatingConfig`: String
                get() =
                    LocaleBootstrap.getString(R.string.component_editor_dialog_validating_config)

            @Composable
            fun `ValidatingConfig`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_validating_config, *args)

            val `ValidationPassed`: String
                get() =
                    LocaleBootstrap.getString(R.string.component_editor_dialog_validation_passed)

            @Composable
            fun `ValidationPassed`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_validation_passed, *args)

            val `FetchingRemoteResources`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.component_editor_dialog_fetching_remote_resources
                    )

            @Composable
            fun `FetchingRemoteResources`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_dialog_fetching_remote_resources,
                    *args,
                )

            val `ApplyingRuntime`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_applying_runtime)

            @Composable
            fun `ApplyingRuntime`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_applying_runtime, *args)

            val `LongRunningUndoSummary`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.component_editor_dialog_long_running_undo_summary
                    )

            @Composable
            fun `LongRunningUndoSummary`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_dialog_long_running_undo_summary,
                    *args,
                )

            val `LongRunningRemoteInterruptionSummary`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.component_editor_dialog_long_running_remote_interruption_summary
                    )

            @Composable
            fun `LongRunningRemoteInterruptionSummary`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_dialog_long_running_remote_interruption_summary,
                    *args,
                )

            val `DirectSaveStoppedRuntimeSummary`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.component_editor_dialog_direct_save_stopped_runtime_summary
                    )

            @Composable
            fun `DirectSaveStoppedRuntimeSummary`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_dialog_direct_save_stopped_runtime_summary,
                    *args,
                )
        }

        object `Error` {
            val `KeyEmpty`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_key_empty)

            @Composable
            fun `KeyEmpty`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_key_empty, *args)

            val `SaveFailed`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_save_failed)

            @Composable
            fun `SaveFailed`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_save_failed, *args)

            val `JsonSyntaxError`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_json_syntax_error)

            @Composable
            fun `JsonSyntaxError`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_json_syntax_error, *args)

            val `ValidationFailed`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_validation_failed)

            @Composable
            fun `ValidationFailed`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_validation_failed, *args)

            val `JsonRootExpected`: String
                get() =
                    LocaleBootstrap.getString(R.string.component_editor_error_json_root_expected)

            @Composable
            fun `JsonRootExpected`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_json_root_expected, *args)

            val `YamlSyntaxError`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_yaml_syntax_error)

            @Composable
            fun `YamlSyntaxError`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_yaml_syntax_error, *args)

            val `Unterminated`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_unterminated)

            @Composable
            fun `Unterminated`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_unterminated, *args)

            val `Expected`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_expected)

            @Composable
            fun `Expected`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_expected, *args)

            val `Unknown`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_unknown)

            @Composable
            fun `Unknown`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_unknown, *args)

            val `MissingValue`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_missing_value)

            @Composable
            fun `MissingValue`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_missing_value, *args)

            val `DuplicateKey`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_duplicate_key)

            @Composable
            fun `DuplicateKey`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_duplicate_key, *args)
        }
    }
}

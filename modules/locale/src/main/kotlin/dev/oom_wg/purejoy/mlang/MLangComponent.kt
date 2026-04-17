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

        val `JustNow`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_just_now)

        @Composable
        fun `JustNow`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_just_now, *args)

        val `MinutesAgo`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_minutes_ago)

        @Composable
        fun `MinutesAgo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_minutes_ago, *args)

        val `HoursAgo`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_hours_ago)

        @Composable
        fun `HoursAgo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_hours_ago, *args)

        val `DaysAgo`: String
            get() = LocaleBootstrap.getString(R.string.component_profile_card_days_ago)

        @Composable
        fun `DaysAgo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_profile_card_days_ago, *args)
    }

    object `WebView` {
        val `InvalidUrl`: String
            get() = LocaleBootstrap.getString(R.string.component_web_view_invalid_url)

        @Composable
        fun `InvalidUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_web_view_invalid_url, *args)
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

        val `Refresh`: String
            get() = LocaleBootstrap.getString(R.string.component_navigation_refresh)

        @Composable
        fun `Refresh`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_navigation_refresh, *args)

        val `Search`: String
            get() = LocaleBootstrap.getString(R.string.component_navigation_search)

        @Composable
        fun `Search`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_navigation_search, *args)

        val `Sort`: String
            get() = LocaleBootstrap.getString(R.string.component_navigation_sort)

        @Composable
        fun `Sort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_navigation_sort, *args)

        val `Settings`: String
            get() = LocaleBootstrap.getString(R.string.component_navigation_settings)

        @Composable
        fun `Settings`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_navigation_settings, *args)
    }

    object `Message` {
        val `Confirm`: String
            get() = LocaleBootstrap.getString(R.string.component_message_confirm)

        @Composable
        fun `Confirm`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_message_confirm, *args)

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

        val `Success`: String
            get() = LocaleBootstrap.getString(R.string.component_message_success)

        @Composable
        fun `Success`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_message_success, *args)
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

    object `Update` {
        object `Title` {
            val `Available`: String
                get() = LocaleBootstrap.getString(R.string.component_update_title_available)

            @Composable
            fun `Available`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_title_available, *args)

            val `ForceCancel`: String
                get() = LocaleBootstrap.getString(R.string.component_update_title_force_cancel)

            @Composable
            fun `ForceCancel`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_title_force_cancel, *args)

            val `Install`: String
                get() = LocaleBootstrap.getString(R.string.component_update_title_install)

            @Composable
            fun `Install`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_title_install, *args)
        }

        object `Message` {
            val `Available`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_available)

            @Composable
            fun `Available`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_available, *args)

            val `CoverDesc`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_cover_desc)

            @Composable
            fun `CoverDesc`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_cover_desc, *args)

            val `CurrentVersion`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_current_version)

            @Composable
            fun `CurrentVersion`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_current_version, *args)

            val `RemoteVersion`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_remote_version)

            @Composable
            fun `RemoteVersion`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_remote_version, *args)

            val `Updating`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_updating)

            @Composable
            fun `Updating`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_updating, *args)

            val `Preparing`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_preparing)

            @Composable
            fun `Preparing`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_preparing, *args)

            val `DownloadingWithProgress`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.component_update_message_downloading_with_progress
                    )

            @Composable
            fun `DownloadingWithProgress`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_update_message_downloading_with_progress,
                    *args,
                )

            val `Verifying`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_verifying)

            @Composable
            fun `Verifying`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_verifying, *args)

            val `Finished`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_finished)

            @Composable
            fun `Finished`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_finished, *args)

            val `Downloading`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_downloading)

            @Composable
            fun `Downloading`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_downloading, *args)

            val `DownloadReady`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_download_ready)

            @Composable
            fun `DownloadReady`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_download_ready, *args)

            val `VerifyFailed`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_verify_failed)

            @Composable
            fun `VerifyFailed`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_verify_failed, *args)

            val `DownloadErrorWithCode`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.component_update_message_download_error_with_code
                    )

            @Composable
            fun `DownloadErrorWithCode`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_update_message_download_error_with_code,
                    *args,
                )

            val `Error`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_error)

            @Composable
            fun `Error`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_error, *args)

            val `Close`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_close)

            @Composable
            fun `Close`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_close, *args)

            val `Waiting`: String
                get() = LocaleBootstrap.getString(R.string.component_update_message_waiting)

            @Composable
            fun `Waiting`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_update_message_waiting, *args)
        }
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

    object `Accessibility` {
        val `CountryFlag`: String
            get() = LocaleBootstrap.getString(R.string.component_accessibility_country_flag)

        @Composable
        fun `CountryFlag`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_accessibility_country_flag, *args)
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
        val `CountItems`: String
            get() = LocaleBootstrap.getString(R.string.component_editor_count_items)

        @Composable
        fun `CountItems`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.component_editor_count_items, *args)

        object `Action` {
            val `Reset`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_reset)

            @Composable
            fun `Reset`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_reset, *args)

            val `Add`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_add)

            @Composable
            fun `Add`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_add, *args)

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

            val `Redo`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_redo)

            @Composable
            fun `Redo`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_redo, *args)

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

            val `SaveAndExit`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_save_and_exit)

            @Composable
            fun `SaveAndExit`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_save_and_exit, *args)

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

            val `ContinueEditing`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_action_continue_editing)

            @Composable
            fun `ContinueEditing`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_action_continue_editing, *args)

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
            val `AddTitle`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_add_title)

            @Composable
            fun `AddTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_add_title, *args)

            val `EditTitle`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_edit_title)

            @Composable
            fun `EditTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_edit_title, *args)

            val `ResetTitle`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_reset_title)

            @Composable
            fun `ResetTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_reset_title, *args)

            val `ResetMessage`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_dialog_reset_message)

            @Composable
            fun `ResetMessage`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_dialog_reset_message, *args)

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

        object `Empty` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_empty_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_empty_title, *args)

            val `Hint`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_empty_hint)

            @Composable
            fun `Hint`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_empty_hint, *args)
        }

        object `Error` {
            val `KeyEmpty`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_key_empty)

            @Composable
            fun `KeyEmpty`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_key_empty, *args)

            val `KeyExists`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_error_key_exists)

            @Composable
            fun `KeyExists`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_error_key_exists, *args)

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

        object `Rule` {
            val `Type`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_type)

            @Composable
            fun `Type`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_type, *args)

            val `Target`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_target)

            @Composable
            fun `Target`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_target, *args)

            val `Content`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_content)

            @Composable
            fun `Content`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_content, *args)

            val `Src`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_src)

            @Composable
            fun `Src`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_src, *args)

            val `NoResolve`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_no_resolve)

            @Composable
            fun `NoResolve`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_no_resolve, *args)

            val `TargetReject`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_target_reject)

            @Composable
            fun `TargetReject`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_target_reject, *args)

            val `TargetDirect`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_target_direct)

            @Composable
            fun `TargetDirect`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_target_direct, *args)

            val `TargetMatch`: String
                get() = LocaleBootstrap.getString(R.string.component_editor_rule_target_match)

            @Composable
            fun `TargetMatch`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.component_editor_rule_target_match, *args)

            val `ErrorTargetRequired`: String
                get() =
                    LocaleBootstrap.getString(R.string.component_editor_rule_error_target_required)

            @Composable
            fun `ErrorTargetRequired`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_rule_error_target_required,
                    *args,
                )

            val `ErrorContentRequired`: String
                get() =
                    LocaleBootstrap.getString(R.string.component_editor_rule_error_content_required)

            @Composable
            fun `ErrorContentRequired`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.component_editor_rule_error_content_required,
                    *args,
                )
        }
    }
}

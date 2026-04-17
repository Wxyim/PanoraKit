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

object MLangProfilesPage {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.profiles_page_title)

    @Composable
    fun `Title`(vararg args: Any): String = stringResource(R.string.profiles_page_title, *args)

    object `Action` {
        val `UpdateAll`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_action_update_all)

        @Composable
        fun `UpdateAll`(vararg args: Any): String =
            stringResource(R.string.profiles_page_action_update_all, *args)

        val `AddProfile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_action_add_profile)

        @Composable
        fun `AddProfile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_action_add_profile, *args)
    }

    object `Empty` {
        val `NoProfiles`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_empty_no_profiles)

        @Composable
        fun `NoProfiles`(vararg args: Any): String =
            stringResource(R.string.profiles_page_empty_no_profiles, *args)

        val `Hint`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_empty_hint)

        @Composable
        fun `Hint`(vararg args: Any): String =
            stringResource(R.string.profiles_page_empty_hint, *args)
    }

    object `Sheet` {
        val `AddTitle`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_sheet_add_title)

        @Composable
        fun `AddTitle`(vararg args: Any): String =
            stringResource(R.string.profiles_page_sheet_add_title, *args)

        val `EditTitle`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_sheet_edit_title)

        @Composable
        fun `EditTitle`(vararg args: Any): String =
            stringResource(R.string.profiles_page_sheet_edit_title, *args)
    }

    object `Type` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_type_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.profiles_page_type_title, *args)

        val `Subscription`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_type_subscription)

        @Composable
        fun `Subscription`(vararg args: Any): String =
            stringResource(R.string.profiles_page_type_subscription, *args)

        val `LocalFile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_type_local_file)

        @Composable
        fun `LocalFile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_type_local_file, *args)

        val `QrScan`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_type_qr_scan)

        @Composable
        fun `QrScan`(vararg args: Any): String =
            stringResource(R.string.profiles_page_type_qr_scan, *args)

        val `BlankConfig`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_type_blank_config)

        @Composable
        fun `BlankConfig`(vararg args: Any): String =
            stringResource(R.string.profiles_page_type_blank_config, *args)
    }

    object `Input` {
        val `ProfileName`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_input_profile_name)

        @Composable
        fun `ProfileName`(vararg args: Any): String =
            stringResource(R.string.profiles_page_input_profile_name, *args)

        val `SubscriptionUrl`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_input_subscription_url)

        @Composable
        fun `SubscriptionUrl`(vararg args: Any): String =
            stringResource(R.string.profiles_page_input_subscription_url, *args)

        val `SelectFile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_input_select_file)

        @Composable
        fun `SelectFile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_input_select_file, *args)

        val `NewProfile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_input_new_profile)

        @Composable
        fun `NewProfile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_input_new_profile, *args)

        val `BlankConfigHint`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_input_blank_config_hint)

        @Composable
        fun `BlankConfigHint`(vararg args: Any): String =
            stringResource(R.string.profiles_page_input_blank_config_hint, *args)
    }

    object `QrScanner` {
        val `NeedPermission`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_need_permission)

        @Composable
        fun `NeedPermission`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_need_permission, *args)

        val `NeedCamera`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_need_camera)

        @Composable
        fun `NeedCamera`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_need_camera, *args)

        val `SelectFromAlbum`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_select_from_album)

        @Composable
        fun `SelectFromAlbum`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_select_from_album, *args)

        val `ScanSuccess`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_scan_success)

        @Composable
        fun `ScanSuccess`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_scan_success, *args)

        val `RecognizeSuccess`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_recognize_success)

        @Composable
        fun `RecognizeSuccess`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_recognize_success, *args)

        val `RecognizeFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_recognize_failed)

        @Composable
        fun `RecognizeFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_recognize_failed, *args)

        val `RecognizeError`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_qr_scanner_recognize_error)

        @Composable
        fun `RecognizeError`(vararg args: Any): String =
            stringResource(R.string.profiles_page_qr_scanner_recognize_error, *args)
    }

    object `Message` {
        val `UnknownFile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_message_unknown_file)

        @Composable
        fun `UnknownFile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_message_unknown_file, *args)

        val `ReadProfileFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_message_read_profile_failed)

        @Composable
        fun `ReadProfileFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_page_message_read_profile_failed, *args)

        val `ProfileFileNotExist`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_message_profile_file_not_exist)

        @Composable
        fun `ProfileFileNotExist`(vararg args: Any): String =
            stringResource(R.string.profiles_page_message_profile_file_not_exist, *args)

        val `ShareFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_message_share_failed)

        @Composable
        fun `ShareFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_page_message_share_failed, *args)
    }

    object `Validation` {
        val `EnterUrl`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_validation_enter_url)

        @Composable
        fun `EnterUrl`(vararg args: Any): String =
            stringResource(R.string.profiles_page_validation_enter_url, *args)

        val `SelectFile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_validation_select_file)

        @Composable
        fun `SelectFile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_validation_select_file, *args)

        val `YamlOnly`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_validation_yaml_only)

        @Composable
        fun `YamlOnly`(vararg args: Any): String =
            stringResource(R.string.profiles_page_validation_yaml_only, *args)
    }

    object `Progress` {
        val `Downloading`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_progress_downloading)

        @Composable
        fun `Downloading`(vararg args: Any): String =
            stringResource(R.string.profiles_page_progress_downloading, *args)
    }

    object `Button` {
        val `Cancel`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_button_cancel)

        @Composable
        fun `Cancel`(vararg args: Any): String =
            stringResource(R.string.profiles_page_button_cancel, *args)

        val `Confirm`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_button_confirm)

        @Composable
        fun `Confirm`(vararg args: Any): String =
            stringResource(R.string.profiles_page_button_confirm, *args)
    }

    object `DeleteDialog` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_delete_dialog_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.profiles_page_delete_dialog_title, *args)

        val `Message`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_delete_dialog_message)

        @Composable
        fun `Message`(vararg args: Any): String =
            stringResource(R.string.profiles_page_delete_dialog_message, *args)

        val `Confirm`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_delete_dialog_confirm)

        @Composable
        fun `Confirm`(vararg args: Any): String =
            stringResource(R.string.profiles_page_delete_dialog_confirm, *args)
    }

    object `EditDialog` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_edit_dialog_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.profiles_page_edit_dialog_title, *args)
    }

    object `LinkSettings` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_title, *args)

        val `OpenMode`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_open_mode)

        @Composable
        fun `OpenMode`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_open_mode, *args)

        val `OpenModeInApp`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_open_mode_in_app)

        @Composable
        fun `OpenModeInApp`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_open_mode_in_app, *args)

        val `OpenModeExternal`: String
            get() =
                LocaleBootstrap.getString(R.string.profiles_page_link_settings_open_mode_external)

        @Composable
        fun `OpenModeExternal`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_open_mode_external, *args)

        val `DefaultLink`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_default_link)

        @Composable
        fun `DefaultLink`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_default_link, *args)

        val `DefaultLinkSummary`: String
            get() =
                LocaleBootstrap.getString(R.string.profiles_page_link_settings_default_link_summary)

        @Composable
        fun `DefaultLinkSummary`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_default_link_summary, *args)

        val `AddLink`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_add_link)

        @Composable
        fun `AddLink`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_add_link, *args)

        val `EditLink`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_edit_link)

        @Composable
        fun `EditLink`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_edit_link, *args)

        val `Name`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_name)

        @Composable
        fun `Name`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_name, *args)

        val `Url`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_url)

        @Composable
        fun `Url`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_url, *args)

        val `Close`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_link_settings_close)

        @Composable
        fun `Close`(vararg args: Any): String =
            stringResource(R.string.profiles_page_link_settings_close, *args)

        object `Validation` {
            val `EnterName`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.profiles_page_link_settings_validation_enter_name
                    )

            @Composable
            fun `EnterName`(vararg args: Any): String =
                stringResource(R.string.profiles_page_link_settings_validation_enter_name, *args)

            val `EnterUrl`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.profiles_page_link_settings_validation_enter_url
                    )

            @Composable
            fun `EnterUrl`(vararg args: Any): String =
                stringResource(R.string.profiles_page_link_settings_validation_enter_url, *args)

            val `InvalidUrl`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.profiles_page_link_settings_validation_invalid_url
                    )

            @Composable
            fun `InvalidUrl`(vararg args: Any): String =
                stringResource(R.string.profiles_page_link_settings_validation_invalid_url, *args)
        }
    }

    object `ShareDialog` {
        val `ShareFile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_share_dialog_share_file)

        @Composable
        fun `ShareFile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_share_dialog_share_file, *args)
    }

    object `Misc` {
        val `Complete`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_misc_complete)

        @Composable
        fun `Complete`(vararg args: Any): String =
            stringResource(R.string.profiles_page_misc_complete, *args)

        val `Error`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_misc_error)

        @Composable
        fun `Error`(vararg args: Any): String =
            stringResource(R.string.profiles_page_misc_error, *args)
    }

    object `SettingsDialog` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_title, *args)

        val `ChangeLink`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_change_link)

        @Composable
        fun `ChangeLink`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_change_link, *args)

        val `SystemPreset`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_system_preset)

        @Composable
        fun `SystemPreset`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_system_preset, *args)

        val `SystemPresetSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.profiles_page_settings_dialog_system_preset_summary
                )

        @Composable
        fun `SystemPresetSummary`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_system_preset_summary, *args)

        val `NoDescription`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_no_description)

        @Composable
        fun `NoDescription`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_no_description, *args)

        val `LocalConfigEditorSummary`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.profiles_page_settings_dialog_local_config_editor_summary
                )

        @Composable
        fun `LocalConfigEditorSummary`(vararg args: Any): String =
            stringResource(
                R.string.profiles_page_settings_dialog_local_config_editor_summary,
                *args,
            )

        val `LocalSource`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_local_source)

        @Composable
        fun `LocalSource`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_local_source, *args)

        val `LocalSourceBlank`: String
            get() =
                LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_local_source_blank)

        @Composable
        fun `LocalSourceBlank`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_local_source_blank, *args)

        val `LocalSourceImported`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.profiles_page_settings_dialog_local_source_imported
                )

        @Composable
        fun `LocalSourceImported`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_local_source_imported, *args)

        val `EditProfile`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_edit_profile)

        @Composable
        fun `EditProfile`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_edit_profile, *args)

        val `OpenConfig`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_open_config)

        @Composable
        fun `OpenConfig`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_open_config, *args)

        val `EditSettings`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_edit_settings)

        @Composable
        fun `EditSettings`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_edit_settings, *args)

        val `SaveFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_save_failed)

        @Composable
        fun `SaveFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_save_failed, *args)

        val `RuleOverrides`: String
            get() = LocaleBootstrap.getString(R.string.profiles_page_settings_dialog_rule_overrides)

        @Composable
        fun `RuleOverrides`(vararg args: Any): String =
            stringResource(R.string.profiles_page_settings_dialog_rule_overrides, *args)

        val `RuleOverridesPriorityHint`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.profiles_page_settings_dialog_rule_overrides_priority_hint
                )

        @Composable
        fun `RuleOverridesPriorityHint`(vararg args: Any): String =
            stringResource(
                R.string.profiles_page_settings_dialog_rule_overrides_priority_hint,
                *args,
            )
    }
}

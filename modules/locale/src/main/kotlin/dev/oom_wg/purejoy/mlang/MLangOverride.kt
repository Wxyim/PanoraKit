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

object MLangOverride {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.override_title)

    @Composable
    fun `Title`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.override_title, *args)

    object `Action` {
        val `Create`: String
            get() = LocaleBootstrap.getString(R.string.override_action_create)

        @Composable
        fun `Create`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_create, *args)

        val `New`: String
            get() = LocaleBootstrap.getString(R.string.override_action_new)

        @Composable
        fun `New`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_new, *args)

        val `Import`: String
            get() = LocaleBootstrap.getString(R.string.override_action_import)

        @Composable
        fun `Import`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_import, *args)

        val `ImportFile`: String
            get() = LocaleBootstrap.getString(R.string.override_action_import_file)

        @Composable
        fun `ImportFile`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_import_file, *args)

        val `ImportFromUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_action_import_from_url)

        @Composable
        fun `ImportFromUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_import_from_url, *args)

        val `ImportSurge`: String
            get() = LocaleBootstrap.getString(R.string.override_action_import_surge)

        @Composable
        fun `ImportSurge`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_import_surge, *args)

        val `ImportJsonUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_action_import_json_url)

        @Composable
        fun `ImportJsonUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_import_json_url, *args)

        val `ImportSurgeUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_action_import_surge_url)

        @Composable
        fun `ImportSurgeUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_action_import_surge_url, *args)
    }

    object `Empty` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.override_empty_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_empty_title, *args)

        val `Hint`: String
            get() = LocaleBootstrap.getString(R.string.override_empty_hint)

        @Composable
        fun `Hint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_empty_hint, *args)
    }

    object `Status` {
        val `InUse`: String
            get() = LocaleBootstrap.getString(R.string.override_status_in_use)

        @Composable
        fun `InUse`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_status_in_use, *args)

        val `NotInUse`: String
            get() = LocaleBootstrap.getString(R.string.override_status_not_in_use)

        @Composable
        fun `NotInUse`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_status_not_in_use, *args)
    }

    object `Card` {
        val `Copy`: String
            get() = LocaleBootstrap.getString(R.string.override_card_copy)

        @Composable
        fun `Copy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_copy, *args)

        val `Export`: String
            get() = LocaleBootstrap.getString(R.string.override_card_export)

        @Composable
        fun `Export`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_export, *args)

        val `Edit`: String
            get() = LocaleBootstrap.getString(R.string.override_card_edit)

        @Composable
        fun `Edit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_edit, *args)

        val `Delete`: String
            get() = LocaleBootstrap.getString(R.string.override_card_delete)

        @Composable
        fun `Delete`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_delete, *args)

        val `EditButton`: String
            get() = LocaleBootstrap.getString(R.string.override_card_edit_button)

        @Composable
        fun `EditButton`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_edit_button, *args)

        val `DeleteButton`: String
            get() = LocaleBootstrap.getString(R.string.override_card_delete_button)

        @Composable
        fun `DeleteButton`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_delete_button, *args)

        val `NoDescription`: String
            get() = LocaleBootstrap.getString(R.string.override_card_no_description)

        @Composable
        fun `NoDescription`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_card_no_description, *args)
    }

    object `Import` {
        val `ReadError`: String
            get() = LocaleBootstrap.getString(R.string.override_import_read_error)

        @Composable
        fun `ReadError`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_read_error, *args)

        val `Success`: String
            get() = LocaleBootstrap.getString(R.string.override_import_success)

        @Composable
        fun `Success`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_success, *args)

        val `SuccessDefault`: String
            get() = LocaleBootstrap.getString(R.string.override_import_success_default)

        @Composable
        fun `SuccessDefault`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_success_default, *args)

        val `Failed`: String
            get() = LocaleBootstrap.getString(R.string.override_import_failed)

        @Composable
        fun `Failed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_failed, *args)

        val `FileError`: String
            get() = LocaleBootstrap.getString(R.string.override_import_file_error)

        @Composable
        fun `FileError`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_file_error, *args)

        val `SurgeSuccess`: String
            get() = LocaleBootstrap.getString(R.string.override_import_surge_success)

        @Composable
        fun `SurgeSuccess`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_surge_success, *args)

        val `SurgeSuccessDefault`: String
            get() = LocaleBootstrap.getString(R.string.override_import_surge_success_default)

        @Composable
        fun `SurgeSuccessDefault`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_surge_success_default, *args)

        val `SurgeNoRules`: String
            get() = LocaleBootstrap.getString(R.string.override_import_surge_no_rules)

        @Composable
        fun `SurgeNoRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_surge_no_rules, *args)

        val `SurgeImportDescription`: String
            get() = LocaleBootstrap.getString(R.string.override_import_surge_import_description)

        @Composable
        fun `SurgeImportDescription`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_surge_import_description, *args)

        val `PluginNoRules`: String
            get() = LocaleBootstrap.getString(R.string.override_import_plugin_no_rules)

        @Composable
        fun `PluginNoRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_plugin_no_rules, *args)

        val `PluginImportDescription`: String
            get() = LocaleBootstrap.getString(R.string.override_import_plugin_import_description)

        @Composable
        fun `PluginImportDescription`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_plugin_import_description, *args)

        val `AutoDetectFailed`: String
            get() = LocaleBootstrap.getString(R.string.override_import_auto_detect_failed)

        @Composable
        fun `AutoDetectFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_auto_detect_failed, *args)

        val `UrlLabel`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_label)

        @Composable
        fun `UrlLabel`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_label, *args)

        val `UrlDownloading`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_downloading)

        @Composable
        fun `UrlDownloading`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_downloading, *args)

        val `UrlInvalidScheme`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_invalid_scheme)

        @Composable
        fun `UrlInvalidScheme`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_invalid_scheme, *args)

        val `UrlHttpsRequired`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_https_required)

        @Composable
        fun `UrlHttpsRequired`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_https_required, *args)

        val `UrlHttpError`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_http_error)

        @Composable
        fun `UrlHttpError`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_http_error, *args)

        val `UrlContentTooLarge`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_content_too_large)

        @Composable
        fun `UrlContentTooLarge`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_content_too_large, *args)

        val `UrlRedirectInvalid`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_redirect_invalid)

        @Composable
        fun `UrlRedirectInvalid`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_redirect_invalid, *args)

        val `UrlTooManyRedirects`: String
            get() = LocaleBootstrap.getString(R.string.override_import_url_too_many_redirects)

        @Composable
        fun `UrlTooManyRedirects`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_import_url_too_many_redirects, *args)

        object `UrlSheet` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_import_url_sheet_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_import_url_sheet_title, *args)

            val `JsonTitle`: String
                get() = LocaleBootstrap.getString(R.string.override_import_url_sheet_json_title)

            @Composable
            fun `JsonTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_import_url_sheet_json_title, *args)

            val `SurgeTitle`: String
                get() = LocaleBootstrap.getString(R.string.override_import_url_sheet_surge_title)

            @Composable
            fun `SurgeTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_import_url_sheet_surge_title, *args)
        }
    }

    object `Export` {
        val `Failed`: String
            get() = LocaleBootstrap.getString(R.string.override_export_failed)

        @Composable
        fun `Failed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_export_failed, *args)

        val `Success`: String
            get() = LocaleBootstrap.getString(R.string.override_export_success)

        @Composable
        fun `Success`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_export_success, *args)
    }

    object `Dialog` {
        object `Create` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_create_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_create_title, *args)

            val `Name`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_create_name)

            @Composable
            fun `Name`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_create_name, *args)

            val `Description`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_create_description)

            @Composable
            fun `Description`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_create_description, *args)

            val `ImportHint`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_create_import_hint)

            @Composable
            fun `ImportHint`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_create_import_hint, *args)

            val `ImportFromUrlHint`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_dialog_create_import_from_url_hint)

            @Composable
            fun `ImportFromUrlHint`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_dialog_create_import_from_url_hint,
                    *args,
                )

            val `ImportSurgeHint`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_create_import_surge_hint)

            @Composable
            fun `ImportSurgeHint`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_create_import_surge_hint, *args)

            val `ImportJsonUrlHint`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_dialog_create_import_json_url_hint)

            @Composable
            fun `ImportJsonUrlHint`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_dialog_create_import_json_url_hint,
                    *args,
                )

            val `ImportSurgeUrlHint`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_dialog_create_import_surge_url_hint)

            @Composable
            fun `ImportSurgeUrlHint`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_dialog_create_import_surge_url_hint,
                    *args,
                )
        }

        object `Delete` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_delete_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_delete_title, *args)

            val `InUseMessage`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_delete_in_use_message)

            @Composable
            fun `InUseMessage`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_delete_in_use_message, *args)

            val `Message`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_delete_message)

            @Composable
            fun `Message`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_delete_message, *args)
        }

        object `EditOptions` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_edit_options_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_edit_options_title, *args)

            val `CodeEditor`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_edit_options_code_editor)

            @Composable
            fun `CodeEditor`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_edit_options_code_editor, *args)

            val `VisualEditor`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_dialog_edit_options_visual_editor)

            @Composable
            fun `VisualEditor`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_dialog_edit_options_visual_editor,
                    *args,
                )
        }

        object `Button` {
            val `Cancel`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_button_cancel)

            @Composable
            fun `Cancel`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_button_cancel, *args)

            val `Delete`: String
                get() = LocaleBootstrap.getString(R.string.override_dialog_button_delete)

            @Composable
            fun `Delete`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_dialog_button_delete, *args)
        }
    }

    object `Edit` {
        val `TitleNew`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_title_new)

        @Composable
        fun `TitleNew`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_title_new, *args)

        val `JsonEditHint`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_json_edit_hint)

        @Composable
        fun `JsonEditHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_json_edit_hint, *args)

        val `StructuredObjectListHint`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_structured_object_list_hint)

        @Composable
        fun `StructuredObjectListHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_structured_object_list_hint, *args)

        val `StructuredProviderDictHint`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_structured_provider_dict_hint)

        @Composable
        fun `StructuredProviderDictHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_structured_provider_dict_hint, *args)

        val `SubRuleGroupHint`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_sub_rule_group_hint)

        @Composable
        fun `SubRuleGroupHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_sub_rule_group_hint, *args)

        val `JsonKeyValueHint`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_json_key_value_hint)

        @Composable
        fun `JsonKeyValueHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_json_key_value_hint, *args)

        val `OneProviderPerLineHint`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_one_provider_per_line_hint)

        @Composable
        fun `OneProviderPerLineHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_one_provider_per_line_hint, *args)

        val `TitleEdit`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_title_edit)

        @Composable
        fun `TitleEdit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_title_edit, *args)

        object `EmptyName` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_edit_empty_name_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_edit_empty_name_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_edit_empty_name_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_edit_empty_name_summary, *args)
        }

        object `Button` {
            val `Cancel`: String
                get() = LocaleBootstrap.getString(R.string.override_edit_button_cancel)

            @Composable
            fun `Cancel`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_edit_button_cancel, *args)

            val `Discard`: String
                get() = LocaleBootstrap.getString(R.string.override_edit_button_discard)

            @Composable
            fun `Discard`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_edit_button_discard, *args)
        }

        val `PresetApplied`: String
            get() = LocaleBootstrap.getString(R.string.override_edit_preset_applied)

        @Composable
        fun `PresetApplied`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_edit_preset_applied, *args)
    }

    object `Section` {
        object `General` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_general_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_general_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_general_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_general_summary, *args)
        }

        object `Dns` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_dns_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_dns_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_dns_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_dns_summary, *args)
        }

        object `Sniffer` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_sniffer_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_sniffer_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_sniffer_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_sniffer_summary, *args)
        }

        object `Inbound` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_inbound_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_inbound_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_inbound_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_inbound_summary, *args)
        }

        object `Tun` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_tun_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_tun_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_tun_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_tun_summary, *args)
        }

        object `Rules` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_rules_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_rules_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_rules_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_rules_summary, *args)
        }

        object `Proxies` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_proxies_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_proxies_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_proxies_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_proxies_summary, *args)
        }

        object `ProxyProviders` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_proxy_providers_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_proxy_providers_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_proxy_providers_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_proxy_providers_summary, *args)
        }

        object `ProxyGroups` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_proxy_groups_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_proxy_groups_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_proxy_groups_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_proxy_groups_summary, *args)
        }

        object `RuleProviders` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_rule_providers_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_rule_providers_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_rule_providers_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_rule_providers_summary, *args)
        }

        object `SubRules` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_section_sub_rules_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_sub_rules_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_section_sub_rules_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_section_sub_rules_summary, *args)
        }
    }

    object `Modifier` {
        val `Replace`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_replace)

        @Composable
        fun `Replace`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_replace, *args)

        val `Start`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_start)

        @Composable
        fun `Start`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_start, *args)

        val `End`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_end)

        @Composable
        fun `End`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_end, *args)

        val `Merge`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_merge)

        @Composable
        fun `Merge`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_merge, *args)

        val `Force`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_force)

        @Composable
        fun `Force`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_force, *args)

        val `NotModified`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_not_modified)

        @Composable
        fun `NotModified`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_not_modified, *args)

        val `ItemsCount`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_items_count)

        @Composable
        fun `ItemsCount`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_items_count, *args)

        val `NoChanges`: String
            get() = LocaleBootstrap.getString(R.string.override_modifier_no_changes)

        @Composable
        fun `NoChanges`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_modifier_no_changes, *args)
    }

    object `Structured` {
        object `Proxies` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_proxies_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_proxies_title, *args)

            val `ItemLabel`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_proxies_item_label)

            @Composable
            fun `ItemLabel`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_proxies_item_label, *args)

            val `EmptyHint`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_proxies_empty_hint)

            @Composable
            fun `EmptyHint`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_proxies_empty_hint, *args)
        }

        object `ProxyGroups` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_proxy_groups_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_proxy_groups_title, *args)

            val `ItemLabel`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_structured_proxy_groups_item_label)

            @Composable
            fun `ItemLabel`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_structured_proxy_groups_item_label,
                    *args,
                )

            val `EmptyHint`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_structured_proxy_groups_empty_hint)

            @Composable
            fun `EmptyHint`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_structured_proxy_groups_empty_hint,
                    *args,
                )
        }

        object `RuleProviders` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_rule_providers_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_rule_providers_title, *args)

            val `ItemLabel`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.override_structured_rule_providers_item_label
                    )

            @Composable
            fun `ItemLabel`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_structured_rule_providers_item_label,
                    *args,
                )
        }

        object `ProxyProviders` {
            val `Title`: String
                get() =
                    LocaleBootstrap.getString(R.string.override_structured_proxy_providers_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_proxy_providers_title, *args)

            val `ItemLabel`: String
                get() =
                    LocaleBootstrap.getString(
                        R.string.override_structured_proxy_providers_item_label
                    )

            @Composable
            fun `ItemLabel`(vararg args: Any): String =
                LocaleBootstrap.getString(
                    R.string.override_structured_proxy_providers_item_label,
                    *args,
                )
        }

        object `SubRules` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_sub_rules_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_sub_rules_title, *args)

            val `ItemLabel`: String
                get() = LocaleBootstrap.getString(R.string.override_structured_sub_rules_item_label)

            @Composable
            fun `ItemLabel`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_structured_sub_rules_item_label, *args)
        }
    }

    object `Editor` {
        object `Mode` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_editor_mode_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_editor_mode_title, *args)

            val `EditTitle`: String
                get() = LocaleBootstrap.getString(R.string.override_editor_mode_edit_title)

            @Composable
            fun `EditTitle`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_editor_mode_edit_title, *args)

            val `DirectEdit`: String
                get() = LocaleBootstrap.getString(R.string.override_editor_mode_direct_edit)

            @Composable
            fun `DirectEdit`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_editor_mode_direct_edit, *args)
        }

        object `ClearDialog` {
            val `Title`: String
                get() = LocaleBootstrap.getString(R.string.override_editor_clear_dialog_title)

            @Composable
            fun `Title`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_editor_clear_dialog_title, *args)

            val `Summary`: String
                get() = LocaleBootstrap.getString(R.string.override_editor_clear_dialog_summary)

            @Composable
            fun `Summary`(vararg args: Any): String =
                LocaleBootstrap.getString(R.string.override_editor_clear_dialog_summary, *args)
        }

        val `New`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_new)

        @Composable
        fun `New`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_new, *args)

        val `AddNamedItem`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_add_named_item)

        @Composable
        fun `AddNamedItem`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_add_named_item, *args)

        val `NewRule`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_new_rule)

        @Composable
        fun `NewRule`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_new_rule, *args)

        val `EditRule`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit_rule)

        @Composable
        fun `EditRule`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit_rule, *args)

        val `NewSubRuleGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_new_sub_rule_group)

        @Composable
        fun `NewSubRuleGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_new_sub_rule_group, *args)

        val `UnnamedSubRuleGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_unnamed_sub_rule_group)

        @Composable
        fun `UnnamedSubRuleGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_unnamed_sub_rule_group, *args)

        val `EditSubRuleGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit_sub_rule_group)

        @Composable
        fun `EditSubRuleGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit_sub_rule_group, *args)

        val `ClearSubRules`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_clear_sub_rules)

        @Composable
        fun `ClearSubRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_clear_sub_rules, *args)

        val `Unnamed`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_unnamed)

        @Composable
        fun `Unnamed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_unnamed, *args)

        val `UnnamedRule`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_unnamed_rule)

        @Composable
        fun `UnnamedRule`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_unnamed_rule, *args)

        val `Edit`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit)

        @Composable
        fun `Edit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit, *args)

        val `DragToSort`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_drag_to_sort)

        @Composable
        fun `DragToSort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_drag_to_sort, *args)

        val `CancelDelete`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_cancel_delete)

        @Composable
        fun `CancelDelete`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_cancel_delete, *args)

        val `DeleteSelected`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_delete_selected)

        @Composable
        fun `DeleteSelected`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_delete_selected, *args)

        val `DeleteSelectedNamedItem`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_delete_selected_named_item)

        @Composable
        fun `DeleteSelectedNamedItem`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_delete_selected_named_item, *args)

        val `DeleteSelectedRules`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_delete_selected_rules)

        @Composable
        fun `DeleteSelectedRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_delete_selected_rules, *args)

        val `ClearMode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_clear_mode)

        @Composable
        fun `ClearMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_clear_mode, *args)

        val `EnterDeleteMode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_enter_delete_mode)

        @Composable
        fun `EnterDeleteMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_enter_delete_mode, *args)

        val `EmptyString`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_empty_string)

        @Composable
        fun `EmptyString`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_empty_string, *args)

        val `ArrayItems`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_array_items)

        @Composable
        fun `ArrayItems`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_array_items, *args)

        val `ObjectFields`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_object_fields)

        @Composable
        fun `ObjectFields`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_object_fields, *args)

        val `Clear`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_clear)

        @Composable
        fun `Clear`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_clear, *args)

        val `Rules`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rules)

        @Composable
        fun `Rules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rules, *args)

        val `AddCustom`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_add_custom)

        @Composable
        fun `AddCustom`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_add_custom, *args)

        val `ContentEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_content_empty)

        @Composable
        fun `ContentEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_content_empty, *args)

        val `Confirm`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_confirm)

        @Composable
        fun `Confirm`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_confirm, *args)

        val `OneItemPerLine`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_one_item_per_line)

        @Composable
        fun `OneItemPerLine`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_one_item_per_line, *args)

        val `AddItem`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_add_item)

        @Composable
        fun `AddItem`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_add_item, *args)

        val `DeleteLastItem`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_delete_last_item)

        @Composable
        fun `DeleteLastItem`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_delete_last_item, *args)

        val `Copy`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_copy)

        @Composable
        fun `Copy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_copy, *args)

        val `Delete`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_delete)

        @Composable
        fun `Delete`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_delete, *args)

        val `MoveUp`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_move_up)

        @Composable
        fun `MoveUp`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_move_up, *args)

        val `MoveDown`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_move_down)

        @Composable
        fun `MoveDown`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_move_down, *args)

        val `AddObject`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_add_object)

        @Composable
        fun `AddObject`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_add_object, *args)

        val `SubRuleName`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_sub_rule_name)

        @Composable
        fun `SubRuleName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_sub_rule_name, *args)

        val `NoRules`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_no_rules)

        @Composable
        fun `NoRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_no_rules, *args)

        val `RulesConfiguredInline`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rules_configured_inline)

        @Composable
        fun `RulesConfiguredInline`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rules_configured_inline, *args)

        val `AddSubRuleGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_add_sub_rule_group)

        @Composable
        fun `AddSubRuleGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_add_sub_rule_group, *args)

        val `EditSubRule`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit_sub_rule)

        @Composable
        fun `EditSubRule`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit_sub_rule, *args)

        val `KeyName`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_key_name)

        @Composable
        fun `KeyName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_key_name, *args)

        val `List`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_list)

        @Composable
        fun `List`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_list, *args)

        val `EditItem`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit_item)

        @Composable
        fun `EditItem`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit_item, *args)

        val `ClearCurrentMode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_clear_current_mode)

        @Composable
        fun `ClearCurrentMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_clear_current_mode, *args)

        val `NewProxyNode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_new_proxy_node)

        @Composable
        fun `NewProxyNode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_new_proxy_node, *args)

        val `NewProxyGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_new_proxy_group)

        @Composable
        fun `NewProxyGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_new_proxy_group, *args)

        val `EditProxyNode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit_proxy_node)

        @Composable
        fun `EditProxyNode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit_proxy_node, *args)

        val `EditProxyGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_edit_proxy_group)

        @Composable
        fun `EditProxyGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_edit_proxy_group, *args)

        val `UnnamedProxyNode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_unnamed_proxy_node)

        @Composable
        fun `UnnamedProxyNode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_unnamed_proxy_node, *args)

        val `UnnamedProxyGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_unnamed_proxy_group)

        @Composable
        fun `UnnamedProxyGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_unnamed_proxy_group, *args)

        val `ProxyNode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_proxy_node)

        @Composable
        fun `ProxyNode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_proxy_node, *args)

        val `ProxyGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_proxy_group)

        @Composable
        fun `ProxyGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_proxy_group, *args)

        val `RuleEdit`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rule_edit)

        @Composable
        fun `RuleEdit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rule_edit, *args)

        val `SubRuleTarget`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_sub_rule_target)

        @Composable
        fun `SubRuleTarget`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_sub_rule_target, *args)

        val `ProxyGroupTarget`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_proxy_group_target)

        @Composable
        fun `ProxyGroupTarget`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_proxy_group_target, *args)

        val `RuleTypeEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rule_type_empty)

        @Composable
        fun `RuleTypeEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rule_type_empty, *args)

        val `PayloadEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_payload_empty)

        @Composable
        fun `PayloadEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_payload_empty, *args)

        val `TargetEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_target_empty)

        @Composable
        fun `TargetEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_target_empty, *args)

        val `MatchResult`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_match_result)

        @Composable
        fun `MatchResult`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_match_result, *args)

        val `SelectMatchResult`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_select_match_result)

        @Composable
        fun `SelectMatchResult`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_select_match_result, *args)

        val `CustomMatchResult`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_custom_match_result)

        @Composable
        fun `CustomMatchResult`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_custom_match_result, *args)

        val `SelectSubRuleTarget`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_select_sub_rule_target)

        @Composable
        fun `SelectSubRuleTarget`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_select_sub_rule_target, *args)

        val `SelectProxyGroupTarget`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_select_proxy_group_target)

        @Composable
        fun `SelectProxyGroupTarget`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_select_proxy_group_target, *args)

        val `CustomSubRuleTarget`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_custom_sub_rule_target)

        @Composable
        fun `CustomSubRuleTarget`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_custom_sub_rule_target, *args)

        val `CustomProxyGroupTarget`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_custom_proxy_group_target)

        @Composable
        fun `CustomProxyGroupTarget`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_custom_proxy_group_target, *args)

        val `SelectRuleProvider`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_select_rule_provider)

        @Composable
        fun `SelectRuleProvider`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_select_rule_provider, *args)

        val `RuleBody`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rule_body)

        @Composable
        fun `RuleBody`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rule_body, *args)

        val `RuleType`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rule_type)

        @Composable
        fun `RuleType`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rule_type, *args)

        val `Payload`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_payload)

        @Composable
        fun `Payload`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_payload, *args)

        val `AdditionalParams`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_additional_params)

        @Composable
        fun `AdditionalParams`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_additional_params, *args)

        val `BasicConnection`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_basic_connection)

        @Composable
        fun `BasicConnection`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_basic_connection, *args)

        val `NetworkAndRoute`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_network_and_route)

        @Composable
        fun `NetworkAndRoute`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_network_and_route, *args)

        val `PortEmptyHint`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_port_empty_hint)

        @Composable
        fun `PortEmptyHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_port_empty_hint, *args)

        val `TypeEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_type_empty)

        @Composable
        fun `TypeEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_type_empty, *args)

        val `MemberSource`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_member_source)

        @Composable
        fun `MemberSource`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_member_source, *args)

        val `HealthCheckAndFilter`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_health_check_and_filter)

        @Composable
        fun `HealthCheckAndFilter`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_health_check_and_filter, *args)

        val `SelectProxyGroupMember`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_select_proxy_group_member)

        @Composable
        fun `SelectProxyGroupMember`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_select_proxy_group_member, *args)

        val `CustomMember`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_custom_member)

        @Composable
        fun `CustomMember`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_custom_member, *args)

        val `SaveProxyNode`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_save_proxy_node)

        @Composable
        fun `SaveProxyNode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_save_proxy_node, *args)

        val `SaveProxyGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_save_proxy_group)

        @Composable
        fun `SaveProxyGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_save_proxy_group, *args)

        val `SaveRule`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_save_rule)

        @Composable
        fun `SaveRule`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_save_rule, *args)

        val `RuleProviderInputHint`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_rule_provider_input_hint)

        @Composable
        fun `RuleProviderInputHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_rule_provider_input_hint, *args)

        val `LogicalRuleHint`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_logical_rule_hint)

        @Composable
        fun `LogicalRuleHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_logical_rule_hint, *args)

        val `OtherExtraParams`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_other_extra_params)

        @Composable
        fun `OtherExtraParams`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_other_extra_params, *args)

        val `ExtraParamsHint`: String
            get() = LocaleBootstrap.getString(R.string.override_editor_extra_params_hint)

        @Composable
        fun `ExtraParamsHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_editor_extra_params_hint, *args)
    }

    object `Draft` {
        val `Object`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_object)

        @Composable
        fun `Object`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_object, *args)

        val `SubRuleGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_sub_rule_group)

        @Composable
        fun `SubRuleGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_sub_rule_group, *args)

        val `Name`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_name)

        @Composable
        fun `Name`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_name, *args)

        val `NameRequired`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_name_required)

        @Composable
        fun `NameRequired`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_name_required, *args)

        val `Save`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_save)

        @Composable
        fun `Save`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_save, *args)

        val `BasicInfo`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_basic_info)

        @Composable
        fun `BasicInfo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_basic_info, *args)

        val `BasicIdentity`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_basic_identity)

        @Composable
        fun `BasicIdentity`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_basic_identity, *args)

        val `CoreSource`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_core_source)

        @Composable
        fun `CoreSource`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_core_source, *args)

        val `NetworkAuth`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_network_auth)

        @Composable
        fun `NetworkAuth`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_network_auth, *args)

        val `Type`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_type)

        @Composable
        fun `Type`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_type, *args)

        val `Vehicle`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_vehicle)

        @Composable
        fun `Vehicle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_vehicle, *args)

        val `Behavior`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_behavior)

        @Composable
        fun `Behavior`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_behavior, *args)

        val `Format`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_format)

        @Composable
        fun `Format`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_format, *args)

        val `HealthCheck`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_health_check)

        @Composable
        fun `HealthCheck`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_health_check, *args)

        val `Override`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_override)

        @Composable
        fun `Override`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_override, *args)

        val `RuleList`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_rule_list)

        @Composable
        fun `RuleList`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_rule_list, *args)

        val `NoRules`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_no_rules)

        @Composable
        fun `NoRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_no_rules, *args)

        val `RulesConfigured`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_rules_configured)

        @Composable
        fun `RulesConfigured`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_rules_configured, *args)

        val `EditSubRules`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_edit_sub_rules)

        @Composable
        fun `EditSubRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_edit_sub_rules, *args)

        val `ExtraFields`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_extra_fields)

        @Composable
        fun `ExtraFields`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_extra_fields, *args)

        val `AddExtraField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_add_extra_field)

        @Composable
        fun `AddExtraField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_add_extra_field, *args)

        val `EditExtraField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_edit_extra_field)

        @Composable
        fun `EditExtraField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_edit_extra_field, *args)

        val `AddHealthCheckField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_add_health_check_field)

        @Composable
        fun `AddHealthCheckField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_add_health_check_field, *args)

        val `EditHealthCheckField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_edit_health_check_field)

        @Composable
        fun `EditHealthCheckField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_edit_health_check_field, *args)

        val `AddOverrideField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_add_override_field)

        @Composable
        fun `AddOverrideField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_add_override_field, *args)

        val `EditOverrideField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_edit_override_field)

        @Composable
        fun `EditOverrideField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_edit_override_field, *args)

        val `HealthCheckSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_health_check_switch)

        @Composable
        fun `HealthCheckSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_health_check_switch, *args)

        val `HealthCheckFields`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_health_check_fields)

        @Composable
        fun `HealthCheckFields`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_health_check_fields, *args)

        val `OverrideSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_override_switch)

        @Composable
        fun `OverrideSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_override_switch, *args)

        val `OverrideFields`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_override_fields)

        @Composable
        fun `OverrideFields`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_override_fields, *args)

        val `BooleanOptions`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_boolean_options)

        @Composable
        fun `BooleanOptions`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_boolean_options, *args)

        val `HeaderHint`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_header_hint)

        @Composable
        fun `HeaderHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_header_hint, *args)

        val `ConfigName`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_config_name)

        @Composable
        fun `ConfigName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_config_name, *args)

        val `ConfigDescription`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_config_description)

        @Composable
        fun `ConfigDescription`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_config_description, *args)

        val `PresetTemplate`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_preset_template)

        @Composable
        fun `PresetTemplate`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_preset_template, *args)

        val `OfficialMrs`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_official_mrs)

        @Composable
        fun `OfficialMrs`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_official_mrs, *args)

        val `OfficialMrsSummary`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_official_mrs_summary)

        @Composable
        fun `OfficialMrsSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_official_mrs_summary, *args)

        val `ConfigSections`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_config_sections)

        @Composable
        fun `ConfigSections`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_config_sections, *args)

        val `Apply`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_apply)

        @Composable
        fun `Apply`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_apply, *args)

        val `RegionalAutoGroup`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_regional_auto_group)

        @Composable
        fun `RegionalAutoGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_regional_auto_group, *args)

        val `BasicRouting`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_basic_routing)

        @Composable
        fun `BasicRouting`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_basic_routing, *args)

        val `ServiceRouting`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_service_routing)

        @Composable
        fun `ServiceRouting`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_service_routing, *args)

        val `RegionHongKong`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_region_hong_kong)

        @Composable
        fun `RegionHongKong`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_region_hong_kong, *args)

        val `RegionTaiwan`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_region_taiwan)

        @Composable
        fun `RegionTaiwan`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_region_taiwan, *args)

        val `RegionJapan`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_region_japan)

        @Composable
        fun `RegionJapan`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_region_japan, *args)

        val `RegionSingapore`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_region_singapore)

        @Composable
        fun `RegionSingapore`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_region_singapore, *args)

        val `RegionUnitedStates`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_region_united_states)

        @Composable
        fun `RegionUnitedStates`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_region_united_states, *args)

        val `ItemAds`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_ads)

        @Composable
        fun `ItemAds`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_ads, *args)

        val `ItemPrivate`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_private)

        @Composable
        fun `ItemPrivate`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_private, *args)

        val `ItemGoogle`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_google)

        @Composable
        fun `ItemGoogle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_google, *args)

        val `ItemTelegram`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_telegram)

        @Composable
        fun `ItemTelegram`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_telegram, *args)

        val `ItemGitHub`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_git_hub)

        @Composable
        fun `ItemGitHub`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_git_hub, *args)

        val `ItemMicrosoft`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_microsoft)

        @Composable
        fun `ItemMicrosoft`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_microsoft, *args)

        val `ItemApple`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_apple)

        @Composable
        fun `ItemApple`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_apple, *args)

        val `ItemYouTube`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_you_tube)

        @Composable
        fun `ItemYouTube`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_you_tube, *args)

        val `ItemNetflix`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_netflix)

        @Composable
        fun `ItemNetflix`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_netflix, *args)

        val `ItemSpotify`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_spotify)

        @Composable
        fun `ItemSpotify`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_spotify, *args)

        val `ItemOpenAI`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_open_a_i)

        @Composable
        fun `ItemOpenAI`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_open_a_i, *args)

        val `ItemSteam`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_steam)

        @Composable
        fun `ItemSteam`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_steam, *args)

        val `ItemCn`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_cn)

        @Composable
        fun `ItemCn`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_cn, *args)

        val `ItemProxy`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_proxy)

        @Composable
        fun `ItemProxy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_proxy, *args)

        val `ItemGeolocationNotCn`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_geolocation_not_cn)

        @Composable
        fun `ItemGeolocationNotCn`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_geolocation_not_cn, *args)

        val `ItemMatch`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_item_match)

        @Composable
        fun `ItemMatch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_item_match, *args)

        val `RemoteSourceUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_remote_source_url)

        @Composable
        fun `RemoteSourceUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_remote_source_url, *args)

        val `RemoteUpdateInterval`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_remote_update_interval)

        @Composable
        fun `RemoteUpdateInterval`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_remote_update_interval, *args)

        val `RemoteUpdateIntervalPlaceholder`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.override_draft_remote_update_interval_placeholder
                )

        @Composable
        fun `RemoteUpdateIntervalPlaceholder`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.override_draft_remote_update_interval_placeholder,
                *args,
            )

        val `ClickToAddExtraField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_click_to_add_extra_field)

        @Composable
        fun `ClickToAddExtraField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_click_to_add_extra_field, *args)

        val `DeleteExtraField`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_delete_extra_field)

        @Composable
        fun `DeleteExtraField`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_delete_extra_field, *args)

        val `ValueType`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_value_type)

        @Composable
        fun `ValueType`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_value_type, *args)

        val `StringValue`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_string_value)

        @Composable
        fun `StringValue`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_string_value, *args)

        val `IntValue`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_int_value)

        @Composable
        fun `IntValue`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_int_value, *args)

        val `DoubleValue`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_double_value)

        @Composable
        fun `DoubleValue`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_double_value, *args)

        val `JsonFragment`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_json_fragment)

        @Composable
        fun `JsonFragment`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_json_fragment, *args)

        val `KeyNameEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_key_name_empty)

        @Composable
        fun `KeyNameEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_key_name_empty, *args)

        val `ValueTypeMismatch`: String
            get() = LocaleBootstrap.getString(R.string.override_draft_value_type_mismatch)

        @Composable
        fun `ValueTypeMismatch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_draft_value_type_mismatch, *args)
    }

    object `Form` {
        val `RuleChain`: String
            get() = LocaleBootstrap.getString(R.string.override_form_rule_chain)

        @Composable
        fun `RuleChain`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_rule_chain, *args)

        val `RuleChainNotSet`: String
            get() = LocaleBootstrap.getString(R.string.override_form_rule_chain_not_set)

        @Composable
        fun `RuleChainNotSet`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_rule_chain_not_set, *args)

        val `SubRules`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sub_rules)

        @Composable
        fun `SubRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sub_rules, *args)

        val `SubRulesHint`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sub_rules_hint)

        @Composable
        fun `SubRulesHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sub_rules_hint, *args)

        val `SubRulesAdvanced`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sub_rules_advanced)

        @Composable
        fun `SubRulesAdvanced`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sub_rules_advanced, *args)

        val `RuleProviders`: String
            get() = LocaleBootstrap.getString(R.string.override_form_rule_providers)

        @Composable
        fun `RuleProviders`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_rule_providers, *args)

        val `RuleProvidersHint`: String
            get() = LocaleBootstrap.getString(R.string.override_form_rule_providers_hint)

        @Composable
        fun `RuleProvidersHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_rule_providers_hint, *args)

        val `RuleProvidersAdvanced`: String
            get() = LocaleBootstrap.getString(R.string.override_form_rule_providers_advanced)

        @Composable
        fun `RuleProvidersAdvanced`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_rule_providers_advanced, *args)

        val `ProxyNodes`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_nodes)

        @Composable
        fun `ProxyNodes`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_nodes, *args)

        val `ProxyNodesHint`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_nodes_hint)

        @Composable
        fun `ProxyNodesHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_nodes_hint, *args)

        val `ProxyProviders`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_providers)

        @Composable
        fun `ProxyProviders`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_providers, *args)

        val `ProxyProvidersHint`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_providers_hint)

        @Composable
        fun `ProxyProvidersHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_providers_hint, *args)

        val `ProxyProvidersAdvanced`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_providers_advanced)

        @Composable
        fun `ProxyProvidersAdvanced`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_providers_advanced, *args)

        val `ProxyGroups`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_groups)

        @Composable
        fun `ProxyGroups`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_groups, *args)

        val `ProxyGroupsHint`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_groups_hint)

        @Composable
        fun `ProxyGroupsHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_groups_hint, *args)

        val `StructuredEdit`: String
            get() = LocaleBootstrap.getString(R.string.override_form_structured_edit)

        @Composable
        fun `StructuredEdit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_structured_edit, *args)

        val `AdvancedJson`: String
            get() = LocaleBootstrap.getString(R.string.override_form_advanced_json)

        @Composable
        fun `AdvancedJson`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_advanced_json, *args)

        val `OpenAdvancedEdit`: String
            get() = LocaleBootstrap.getString(R.string.override_form_open_advanced_edit)

        @Composable
        fun `OpenAdvancedEdit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_open_advanced_edit, *args)

        val `OpenAdvancedEditSummary`: String
            get() = LocaleBootstrap.getString(R.string.override_form_open_advanced_edit_summary)

        @Composable
        fun `OpenAdvancedEditSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_open_advanced_edit_summary, *args)

        val `ItemsConfigured`: String
            get() = LocaleBootstrap.getString(R.string.override_form_items_configured)

        @Composable
        fun `ItemsConfigured`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_items_configured, *args)

        val `ProxyPorts`: String
            get() = LocaleBootstrap.getString(R.string.override_form_proxy_ports)

        @Composable
        fun `ProxyPorts`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_proxy_ports, *args)

        val `RunAndLog`: String
            get() = LocaleBootstrap.getString(R.string.override_form_run_and_log)

        @Composable
        fun `RunAndLog`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_run_and_log, *args)

        val `ProcessMode`: String
            get() = LocaleBootstrap.getString(R.string.override_form_process_mode)

        @Composable
        fun `ProcessMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_process_mode, *args)

        val `NotModify`: String
            get() = LocaleBootstrap.getString(R.string.override_form_not_modify)

        @Composable
        fun `NotModify`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_not_modify, *args)

        val `UnifiedDelay`: String
            get() = LocaleBootstrap.getString(R.string.override_form_unified_delay)

        @Composable
        fun `UnifiedDelay`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_unified_delay, *args)

        val `TcpConcurrent`: String
            get() = LocaleBootstrap.getString(R.string.override_form_tcp_concurrent)

        @Composable
        fun `TcpConcurrent`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_tcp_concurrent, *args)

        val `GeodataMode`: String
            get() = LocaleBootstrap.getString(R.string.override_form_geodata_mode)

        @Composable
        fun `GeodataMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_geodata_mode, *args)

        val `RunAndLogExtra`: String
            get() = LocaleBootstrap.getString(R.string.override_form_run_and_log_extra)

        @Composable
        fun `RunAndLogExtra`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_run_and_log_extra, *args)

        val `Seconds`: String
            get() = LocaleBootstrap.getString(R.string.override_form_seconds)

        @Composable
        fun `Seconds`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_seconds, *args)

        val `ConnectionNetwork`: String
            get() = LocaleBootstrap.getString(R.string.override_form_connection_network)

        @Composable
        fun `ConnectionNetwork`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_connection_network, *args)

        val `OutboundInterface`: String
            get() = LocaleBootstrap.getString(R.string.override_form_outbound_interface)

        @Composable
        fun `OutboundInterface`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_outbound_interface, *args)

        val `RoutingMark`: String
            get() = LocaleBootstrap.getString(R.string.override_form_routing_mark)

        @Composable
        fun `RoutingMark`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_routing_mark, *args)

        val `GeositeMatcher`: String
            get() = LocaleBootstrap.getString(R.string.override_form_geosite_matcher)

        @Composable
        fun `GeositeMatcher`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_geosite_matcher, *args)

        val `GlobalClientFingerprint`: String
            get() = LocaleBootstrap.getString(R.string.override_form_global_client_fingerprint)

        @Composable
        fun `GlobalClientFingerprint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_global_client_fingerprint, *args)

        val `LanAccess`: String
            get() = LocaleBootstrap.getString(R.string.override_form_lan_access)

        @Composable
        fun `LanAccess`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_lan_access, *args)

        val `AllowedIPs`: String
            get() = LocaleBootstrap.getString(R.string.override_form_allowed_i_ps)

        @Composable
        fun `AllowedIPs`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_allowed_i_ps, *args)

        val `DisallowedIPs`: String
            get() = LocaleBootstrap.getString(R.string.override_form_disallowed_i_ps)

        @Composable
        fun `DisallowedIPs`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_disallowed_i_ps, *args)

        val `LanAddress`: String
            get() = LocaleBootstrap.getString(R.string.override_form_lan_address)

        @Composable
        fun `LanAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_lan_address, *args)

        val `BindAddress`: String
            get() = LocaleBootstrap.getString(R.string.override_form_bind_address)

        @Composable
        fun `BindAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_bind_address, *args)

        val `UserAuth`: String
            get() = LocaleBootstrap.getString(R.string.override_form_user_auth)

        @Composable
        fun `UserAuth`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_user_auth, *args)

        val `SkipAuthIPs`: String
            get() = LocaleBootstrap.getString(R.string.override_form_skip_auth_i_ps)

        @Composable
        fun `SkipAuthIPs`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_skip_auth_i_ps, *args)

        val `ExternalControl`: String
            get() = LocaleBootstrap.getString(R.string.override_form_external_control)

        @Composable
        fun `ExternalControl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_external_control, *args)

        val `ExternalController`: String
            get() = LocaleBootstrap.getString(R.string.override_form_external_controller)

        @Composable
        fun `ExternalController`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_external_controller, *args)

        val `ExternalControllerHttps`: String
            get() = LocaleBootstrap.getString(R.string.override_form_external_controller_https)

        @Composable
        fun `ExternalControllerHttps`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_external_controller_https, *args)

        val `ExternalDoH`: String
            get() = LocaleBootstrap.getString(R.string.override_form_external_do_h)

        @Composable
        fun `ExternalDoH`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_external_do_h, *args)

        val `ApiSecret`: String
            get() = LocaleBootstrap.getString(R.string.override_form_api_secret)

        @Composable
        fun `ApiSecret`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_api_secret, *args)

        val `ControllerCors`: String
            get() = LocaleBootstrap.getString(R.string.override_form_controller_cors)

        @Composable
        fun `ControllerCors`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_controller_cors, *args)

        val `CorsAllowOrigins`: String
            get() = LocaleBootstrap.getString(R.string.override_form_cors_allow_origins)

        @Composable
        fun `CorsAllowOrigins`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_cors_allow_origins, *args)

        val `AllowPrivateNetwork`: String
            get() = LocaleBootstrap.getString(R.string.override_form_allow_private_network)

        @Composable
        fun `AllowPrivateNetwork`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_allow_private_network, *args)

        val `ConfigPersistence`: String
            get() = LocaleBootstrap.getString(R.string.override_form_config_persistence)

        @Composable
        fun `ConfigPersistence`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_config_persistence, *args)

        val `SaveGroupSelection`: String
            get() = LocaleBootstrap.getString(R.string.override_form_save_group_selection)

        @Composable
        fun `SaveGroupSelection`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_save_group_selection, *args)

        val `SaveFakeIpMapping`: String
            get() = LocaleBootstrap.getString(R.string.override_form_save_fake_ip_mapping)

        @Composable
        fun `SaveFakeIpMapping`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_save_fake_ip_mapping, *args)

        val `GeoResources`: String
            get() = LocaleBootstrap.getString(R.string.override_form_geo_resources)

        @Composable
        fun `GeoResources`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_geo_resources, *args)

        val `AutoUpdateGeo`: String
            get() = LocaleBootstrap.getString(R.string.override_form_auto_update_geo)

        @Composable
        fun `AutoUpdateGeo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_auto_update_geo, *args)

        val `GeoUpdateInterval`: String
            get() = LocaleBootstrap.getString(R.string.override_form_geo_update_interval)

        @Composable
        fun `GeoUpdateInterval`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_geo_update_interval, *args)

        val `Hours`: String
            get() = LocaleBootstrap.getString(R.string.override_form_hours)

        @Composable
        fun `Hours`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_hours, *args)

        val `GeoipUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_form_geoip_url)

        @Composable
        fun `GeoipUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_geoip_url, *args)

        val `GeositeUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_form_geosite_url)

        @Composable
        fun `GeositeUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_geosite_url, *args)

        val `MmdbUrl`: String
            get() = LocaleBootstrap.getString(R.string.override_form_mmdb_url)

        @Composable
        fun `MmdbUrl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_mmdb_url, *args)

        val `TunBasicSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_form_tun_basic_switch)

        @Composable
        fun `TunBasicSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_tun_basic_switch, *args)

        val `Stack`: String
            get() = LocaleBootstrap.getString(R.string.override_form_stack)

        @Composable
        fun `Stack`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_stack, *args)

        val `AutoRoute`: String
            get() = LocaleBootstrap.getString(R.string.override_form_auto_route)

        @Composable
        fun `AutoRoute`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_auto_route, *args)

        val `AutoRedirect`: String
            get() = LocaleBootstrap.getString(R.string.override_form_auto_redirect)

        @Composable
        fun `AutoRedirect`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_auto_redirect, *args)

        val `AutoDetectInterface`: String
            get() = LocaleBootstrap.getString(R.string.override_form_auto_detect_interface)

        @Composable
        fun `AutoDetectInterface`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_auto_detect_interface, *args)

        val `StrictRoute`: String
            get() = LocaleBootstrap.getString(R.string.override_form_strict_route)

        @Composable
        fun `StrictRoute`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_strict_route, *args)

        val `EndpointIndependentNat`: String
            get() = LocaleBootstrap.getString(R.string.override_form_endpoint_independent_nat)

        @Composable
        fun `EndpointIndependentNat`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_endpoint_independent_nat, *args)

        val `NetworkPerfSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_form_network_perf_switch)

        @Composable
        fun `NetworkPerfSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_network_perf_switch, *args)

        val `EnableGso`: String
            get() = LocaleBootstrap.getString(R.string.override_form_enable_gso)

        @Composable
        fun `EnableGso`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_enable_gso, *args)

        val `DisableIcmpForward`: String
            get() = LocaleBootstrap.getString(R.string.override_form_disable_icmp_forward)

        @Composable
        fun `DisableIcmpForward`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_disable_icmp_forward, *args)

        val `NetworkPerfParams`: String
            get() = LocaleBootstrap.getString(R.string.override_form_network_perf_params)

        @Composable
        fun `NetworkPerfParams`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_network_perf_params, *args)

        val `Mtu`: String
            get() = LocaleBootstrap.getString(R.string.override_form_mtu)

        @Composable
        fun `Mtu`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_mtu, *args)

        val `GsoMaxSize`: String
            get() = LocaleBootstrap.getString(R.string.override_form_gso_max_size)

        @Composable
        fun `GsoMaxSize`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_gso_max_size, *args)

        val `DnsBasicSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_basic_switch)

        @Composable
        fun `DnsBasicSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_basic_switch, *args)

        val `DnsBasicParams`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_basic_params)

        @Composable
        fun `DnsBasicParams`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_basic_params, *args)

        val `DnsFakeIpRange`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_fake_ip_range)

        @Composable
        fun `DnsFakeIpRange`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_fake_ip_range, *args)

        val `FakeIpMode`: String
            get() = LocaleBootstrap.getString(R.string.override_form_fake_ip_mode)

        @Composable
        fun `FakeIpMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_fake_ip_mode, *args)

        val `FakeIpParams`: String
            get() = LocaleBootstrap.getString(R.string.override_form_fake_ip_params)

        @Composable
        fun `FakeIpParams`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_fake_ip_params, *args)

        val `DnsUpstream`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_upstream)

        @Composable
        fun `DnsUpstream`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_upstream, *args)

        val `DnsPolicyMode`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_policy_mode)

        @Composable
        fun `DnsPolicyMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_policy_mode, *args)

        val `SnifferSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sniffer_switch)

        @Composable
        fun `SnifferSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sniffer_switch, *args)

        val `SnifferPorts`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sniffer_ports)

        @Composable
        fun `SnifferPorts`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sniffer_ports, *args)

        val `SnifferOverride`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sniffer_override)

        @Composable
        fun `SnifferOverride`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sniffer_override, *args)

        val `SnifferSkipDomain`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sniffer_skip_domain)

        @Composable
        fun `SnifferSkipDomain`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sniffer_skip_domain, *args)

        val `SnifferForceDomain`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sniffer_force_domain)

        @Composable
        fun `SnifferForceDomain`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sniffer_force_domain, *args)

        val `SnifferParsePureIp`: String
            get() = LocaleBootstrap.getString(R.string.override_form_sniffer_parse_pure_ip)

        @Composable
        fun `SnifferParsePureIp`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_sniffer_parse_pure_ip, *args)

        val `TunRouteAndApps`: String
            get() = LocaleBootstrap.getString(R.string.override_form_tun_route_and_apps)

        @Composable
        fun `TunRouteAndApps`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_tun_route_and_apps, *args)

        val `DnsHijack`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_hijack)

        @Composable
        fun `DnsHijack`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_hijack, *args)

        val `RouteAddress`: String
            get() = LocaleBootstrap.getString(R.string.override_form_route_address)

        @Composable
        fun `RouteAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_route_address, *args)

        val `RouteExcludeAddress`: String
            get() = LocaleBootstrap.getString(R.string.override_form_route_exclude_address)

        @Composable
        fun `RouteExcludeAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_route_exclude_address, *args)

        val `IncludePackage`: String
            get() = LocaleBootstrap.getString(R.string.override_form_include_package)

        @Composable
        fun `IncludePackage`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_include_package, *args)

        val `ExcludePackage`: String
            get() = LocaleBootstrap.getString(R.string.override_form_exclude_package)

        @Composable
        fun `ExcludePackage`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_exclude_package, *args)

        val `CacheLimit`: String
            get() = LocaleBootstrap.getString(R.string.override_form_cache_limit)

        @Composable
        fun `CacheLimit`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_cache_limit, *args)

        val `DnsUpstreamServers`: String
            get() = LocaleBootstrap.getString(R.string.override_form_dns_upstream_servers)

        @Composable
        fun `DnsUpstreamServers`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_dns_upstream_servers, *args)

        val `NameserverPolicySection`: String
            get() = LocaleBootstrap.getString(R.string.override_form_nameserver_policy_section)

        @Composable
        fun `NameserverPolicySection`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_nameserver_policy_section, *args)

        val `FilterList`: String
            get() = LocaleBootstrap.getString(R.string.override_form_filter_list)

        @Composable
        fun `FilterList`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_filter_list, *args)

        val `FallbackSwitch`: String
            get() = LocaleBootstrap.getString(R.string.override_form_fallback_switch)

        @Composable
        fun `FallbackSwitch`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_fallback_switch, *args)

        val `FallbackParams`: String
            get() = LocaleBootstrap.getString(R.string.override_form_fallback_params)

        @Composable
        fun `FallbackParams`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_fallback_params, *args)

        val `FallbackFilter`: String
            get() = LocaleBootstrap.getString(R.string.override_form_fallback_filter)

        @Composable
        fun `FallbackFilter`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_fallback_filter, *args)

        val `BasicPolicy`: String
            get() = LocaleBootstrap.getString(R.string.override_form_basic_policy)

        @Composable
        fun `BasicPolicy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_basic_policy, *args)

        val `SkipAndForce`: String
            get() = LocaleBootstrap.getString(R.string.override_form_skip_and_force)

        @Composable
        fun `SkipAndForce`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_skip_and_force, *args)

        val `SkipSrcAddress`: String
            get() = LocaleBootstrap.getString(R.string.override_form_skip_src_address)

        @Composable
        fun `SkipSrcAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_skip_src_address, *args)

        val `SkipDstAddress`: String
            get() = LocaleBootstrap.getString(R.string.override_form_skip_dst_address)

        @Composable
        fun `SkipDstAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_form_skip_dst_address, *args)
    }

    object `Rule` {
        val `EmptyWarning`: String
            get() = LocaleBootstrap.getString(R.string.override_rule_empty_warning)

        @Composable
        fun `EmptyWarning`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_rule_empty_warning, *args)

        val `InvalidFormatWarning`: String
            get() = LocaleBootstrap.getString(R.string.override_rule_invalid_format_warning)

        @Composable
        fun `InvalidFormatWarning`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_rule_invalid_format_warning, *args)

        val `MissingTargetWarning`: String
            get() = LocaleBootstrap.getString(R.string.override_rule_missing_target_warning)

        @Composable
        fun `MissingTargetWarning`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_rule_missing_target_warning, *args)
    }

    object `Save` {
        val `PresetNotModifiable`: String
            get() = LocaleBootstrap.getString(R.string.override_save_preset_not_modifiable)

        @Composable
        fun `PresetNotModifiable`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_preset_not_modifiable, *args)

        val `ApplyFailed`: String
            get() = LocaleBootstrap.getString(R.string.override_save_apply_failed)

        @Composable
        fun `ApplyFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_apply_failed, *args)

        val `Failed`: String
            get() = LocaleBootstrap.getString(R.string.override_save_failed)

        @Composable
        fun `Failed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_failed, *args)

        val `RuntimeSaveFailed`: String
            get() = LocaleBootstrap.getString(R.string.override_save_runtime_save_failed)

        @Composable
        fun `RuntimeSaveFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_runtime_save_failed, *args)

        val `RuntimeOverrideName`: String
            get() = LocaleBootstrap.getString(R.string.override_save_runtime_override_name)

        @Composable
        fun `RuntimeOverrideName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_runtime_override_name, *args)

        val `ImportEmpty`: String
            get() = LocaleBootstrap.getString(R.string.override_save_import_empty)

        @Composable
        fun `ImportEmpty`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_import_empty, *args)

        val `ImportDefaultName`: String
            get() = LocaleBootstrap.getString(R.string.override_save_import_default_name)

        @Composable
        fun `ImportDefaultName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_save_import_default_name, *args)
    }

    object `Dns` {
        val `Policy`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_policy)

        @Composable
        fun `Policy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_policy, *args)

        val `PolicyNotModify`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_policy_not_modify)

        @Composable
        fun `PolicyNotModify`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_policy_not_modify, *args)

        val `PolicyForceEnable`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_policy_force_enable)

        @Composable
        fun `PolicyForceEnable`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_policy_force_enable, *args)

        val `PolicyUseBuiltin`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_policy_use_builtin)

        @Composable
        fun `PolicyUseBuiltin`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_policy_use_builtin, *args)

        val `PreferH3`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_prefer_h3)

        @Composable
        fun `PreferH3`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_prefer_h3, *args)

        val `Listen`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_listen)

        @Composable
        fun `Listen`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_listen, *args)

        val `ListenHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_listen_hint)

        @Composable
        fun `ListenHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_listen_hint, *args)

        val `Ipv6`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_ipv6)

        @Composable
        fun `Ipv6`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_ipv6, *args)

        val `UseHosts`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_use_hosts)

        @Composable
        fun `UseHosts`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_use_hosts, *args)

        val `AppendSystem`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_append_system)

        @Composable
        fun `AppendSystem`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_append_system, *args)

        val `EnhancedMode`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_enhanced_mode)

        @Composable
        fun `EnhancedMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_enhanced_mode, *args)

        val `EnhancedNotModify`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_enhanced_not_modify)

        @Composable
        fun `EnhancedNotModify`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_enhanced_not_modify, *args)

        val `EnhancedDisable`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_enhanced_disable)

        @Composable
        fun `EnhancedDisable`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_enhanced_disable, *args)

        val `EnhancedFakeip`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_enhanced_fakeip)

        @Composable
        fun `EnhancedFakeip`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_enhanced_fakeip, *args)

        val `EnhancedMapping`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_enhanced_mapping)

        @Composable
        fun `EnhancedMapping`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_enhanced_mapping, *args)

        val `DirectFollowPolicy`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_direct_follow_policy)

        @Composable
        fun `DirectFollowPolicy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_direct_follow_policy, *args)

        val `Ipv6Timeout`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_ipv6_timeout)

        @Composable
        fun `Ipv6Timeout`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_ipv6_timeout, *args)

        val `Servers`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_servers)

        @Composable
        fun `Servers`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_servers, *args)

        val `ServersHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_servers_hint)

        @Composable
        fun `ServersHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_servers_hint, *args)

        val `Fallback`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback)

        @Composable
        fun `Fallback`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback, *args)

        val `FallbackHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_hint)

        @Composable
        fun `FallbackHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_hint, *args)

        val `Default`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_default)

        @Composable
        fun `Default`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_default, *args)

        val `DefaultHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_default_hint)

        @Composable
        fun `DefaultHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_default_hint, *args)

        val `FakeipFilter`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_filter)

        @Composable
        fun `FakeipFilter`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_filter, *args)

        val `FakeipFilterHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_filter_hint)

        @Composable
        fun `FakeipFilterHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_filter_hint, *args)

        val `FakeipFilterMode`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_filter_mode)

        @Composable
        fun `FakeipFilterMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_filter_mode, *args)

        val `FakeipBlacklist`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_blacklist)

        @Composable
        fun `FakeipBlacklist`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_blacklist, *args)

        val `FakeipWhitelist`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_whitelist)

        @Composable
        fun `FakeipWhitelist`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_whitelist, *args)

        val `FallbackGeoip`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_geoip)

        @Composable
        fun `FallbackGeoip`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_geoip, *args)

        val `FallbackGeoipCode`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_geoip_code)

        @Composable
        fun `FallbackGeoipCode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_geoip_code, *args)

        val `FallbackGeoipCodeHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_geoip_code_hint)

        @Composable
        fun `FallbackGeoipCodeHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_geoip_code_hint, *args)

        val `FallbackDomain`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_domain)

        @Composable
        fun `FallbackDomain`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_domain, *args)

        val `FallbackDomainHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_domain_hint)

        @Composable
        fun `FallbackDomainHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_domain_hint, *args)

        val `FallbackIpcidr`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_ipcidr)

        @Composable
        fun `FallbackIpcidr`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_ipcidr, *args)

        val `FallbackIpcidrHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_ipcidr_hint)

        @Composable
        fun `FallbackIpcidrHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_ipcidr_hint, *args)

        val `FallbackGeosite`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_geosite)

        @Composable
        fun `FallbackGeosite`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_geosite, *args)

        val `FallbackGeositeHint`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fallback_geosite_hint)

        @Composable
        fun `FallbackGeositeHint`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fallback_geosite_hint, *args)

        val `NameserverPolicy`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_nameserver_policy)

        @Composable
        fun `NameserverPolicy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_nameserver_policy, *args)

        val `NameserverPolicyKey`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_nameserver_policy_key)

        @Composable
        fun `NameserverPolicyKey`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_nameserver_policy_key, *args)

        val `NameserverPolicyValue`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_nameserver_policy_value)

        @Composable
        fun `NameserverPolicyValue`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_nameserver_policy_value, *args)

        val `FakeipRange6`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_range6)

        @Composable
        fun `FakeipRange6`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_range6, *args)

        val `FakeipTtl`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_fakeip_ttl)

        @Composable
        fun `FakeipTtl`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_fakeip_ttl, *args)

        val `ProxyServerNameserver`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_proxy_server_nameserver)

        @Composable
        fun `ProxyServerNameserver`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_proxy_server_nameserver, *args)

        val `DirectNameserver`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_direct_nameserver)

        @Composable
        fun `DirectNameserver`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_direct_nameserver, *args)

        val `ProxyServerNameserverPolicy`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_proxy_server_nameserver_policy)

        @Composable
        fun `ProxyServerNameserverPolicy`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_proxy_server_nameserver_policy, *args)

        val `ProxyServerNameserverPolicyKey`: String
            get() =
                LocaleBootstrap.getString(R.string.override_dns_proxy_server_nameserver_policy_key)

        @Composable
        fun `ProxyServerNameserverPolicyKey`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.override_dns_proxy_server_nameserver_policy_key,
                *args,
            )

        val `ProxyServerNameserverPolicyValue`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.override_dns_proxy_server_nameserver_policy_value
                )

        @Composable
        fun `ProxyServerNameserverPolicyValue`(vararg args: Any): String =
            LocaleBootstrap.getString(
                R.string.override_dns_proxy_server_nameserver_policy_value,
                *args,
            )

        val `Hosts`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_hosts)

        @Composable
        fun `Hosts`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_hosts, *args)

        val `HostsKey`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_hosts_key)

        @Composable
        fun `HostsKey`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_hosts_key, *args)

        val `HostsValue`: String
            get() = LocaleBootstrap.getString(R.string.override_dns_hosts_value)

        @Composable
        fun `HostsValue`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_dns_hosts_value, *args)
    }

    object `General` {
        val `HttpPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_http_port)

        @Composable
        fun `HttpPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_http_port, *args)

        val `TlsPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_tls_port)

        @Composable
        fun `TlsPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_tls_port, *args)

        val `QuicPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_quic_port)

        @Composable
        fun `QuicPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_quic_port, *args)

        val `SocksPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_socks_port)

        @Composable
        fun `SocksPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_socks_port, *args)

        val `MixedPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_mixed_port)

        @Composable
        fun `MixedPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_mixed_port, *args)

        val `RedirectPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_redirect_port)

        @Composable
        fun `RedirectPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_redirect_port, *args)

        val `TproxyPort`: String
            get() = LocaleBootstrap.getString(R.string.override_general_tproxy_port)

        @Composable
        fun `TproxyPort`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_tproxy_port, *args)

        val `AllowLan`: String
            get() = LocaleBootstrap.getString(R.string.override_general_allow_lan)

        @Composable
        fun `AllowLan`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_allow_lan, *args)

        val `Ipv6`: String
            get() = LocaleBootstrap.getString(R.string.override_general_ipv6)

        @Composable
        fun `Ipv6`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_ipv6, *args)

        val `ProxyMode`: String
            get() = LocaleBootstrap.getString(R.string.override_general_proxy_mode)

        @Composable
        fun `ProxyMode`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_proxy_mode, *args)

        val `LogLevel`: String
            get() = LocaleBootstrap.getString(R.string.override_general_log_level)

        @Composable
        fun `LogLevel`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_general_log_level, *args)
    }

    object `Label` {
        val `CacheAlgorithm`: String
            get() = LocaleBootstrap.getString(R.string.override_label_cache_algorithm)

        @Composable
        fun `CacheAlgorithm`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_cache_algorithm, *args)

        val `Enable`: String
            get() = LocaleBootstrap.getString(R.string.override_label_enable)

        @Composable
        fun `Enable`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_enable, *args)

        val `FakeIpRange`: String
            get() = LocaleBootstrap.getString(R.string.override_label_fake_ip_range)

        @Composable
        fun `FakeIpRange`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_fake_ip_range, *args)

        val `ForceDnsMapping`: String
            get() = LocaleBootstrap.getString(R.string.override_label_force_dns_mapping)

        @Composable
        fun `ForceDnsMapping`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_force_dns_mapping, *args)

        val `ForceDomain`: String
            get() = LocaleBootstrap.getString(R.string.override_label_force_domain)

        @Composable
        fun `ForceDomain`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_force_domain, *args)

        val `HttpOverride`: String
            get() = LocaleBootstrap.getString(R.string.override_label_http_override)

        @Composable
        fun `HttpOverride`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_http_override, *args)

        val `KeepAliveIdle`: String
            get() = LocaleBootstrap.getString(R.string.override_label_keep_alive_idle)

        @Composable
        fun `KeepAliveIdle`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_keep_alive_idle, *args)

        val `KeepAliveInterval`: String
            get() = LocaleBootstrap.getString(R.string.override_label_keep_alive_interval)

        @Composable
        fun `KeepAliveInterval`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_keep_alive_interval, *args)

        val `OverrideDestination`: String
            get() = LocaleBootstrap.getString(R.string.override_label_override_destination)

        @Composable
        fun `OverrideDestination`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_override_destination, *args)

        val `ParsePureIp`: String
            get() = LocaleBootstrap.getString(R.string.override_label_parse_pure_ip)

        @Composable
        fun `ParsePureIp`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_parse_pure_ip, *args)

        val `QuicOverride`: String
            get() = LocaleBootstrap.getString(R.string.override_label_quic_override)

        @Composable
        fun `QuicOverride`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_quic_override, *args)

        val `RespectRules`: String
            get() = LocaleBootstrap.getString(R.string.override_label_respect_rules)

        @Composable
        fun `RespectRules`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_respect_rules, *args)

        val `RulesReplace`: String
            get() = LocaleBootstrap.getString(R.string.override_label_rules_replace)

        @Composable
        fun `RulesReplace`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_rules_replace, *args)

        val `SkipDomain`: String
            get() = LocaleBootstrap.getString(R.string.override_label_skip_domain)

        @Composable
        fun `SkipDomain`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_skip_domain, *args)

        val `TlsOverride`: String
            get() = LocaleBootstrap.getString(R.string.override_label_tls_override)

        @Composable
        fun `TlsOverride`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_tls_override, *args)

        val `UseSystemHosts`: String
            get() = LocaleBootstrap.getString(R.string.override_label_use_system_hosts)

        @Composable
        fun `UseSystemHosts`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.override_label_use_system_hosts, *args)
    }
}

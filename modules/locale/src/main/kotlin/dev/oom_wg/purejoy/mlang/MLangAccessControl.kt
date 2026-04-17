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

object MLangAccessControl {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.access_control_title)

    @Composable
    fun `Title`(vararg args: Any): String = stringResource(R.string.access_control_title, *args)

    object `Search` {
        val `Placeholder`: String
            get() = LocaleBootstrap.getString(R.string.access_control_search_placeholder)

        @Composable
        fun `Placeholder`(vararg args: Any): String =
            stringResource(R.string.access_control_search_placeholder, *args)

        val `EmptyResults`: String
            get() = LocaleBootstrap.getString(R.string.access_control_search_empty_results)

        @Composable
        fun `EmptyResults`(vararg args: Any): String =
            stringResource(R.string.access_control_search_empty_results, *args)
    }

    object `AppList` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.access_control_app_list_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_title, *args)

        val `Loading`: String
            get() = LocaleBootstrap.getString(R.string.access_control_app_list_loading)

        @Composable
        fun `Loading`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_loading, *args)

        val `BrowseUnavailablePermission`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_app_list_browse_unavailable_permission
                )

        @Composable
        fun `BrowseUnavailablePermission`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_browse_unavailable_permission, *args)

        val `BrowseUnavailableManual`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_app_list_browse_unavailable_manual
                )

        @Composable
        fun `BrowseUnavailableManual`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_browse_unavailable_manual, *args)

        val `OpenPermissionSettings`: String
            get() =
                LocaleBootstrap.getString(R.string.access_control_app_list_open_permission_settings)

        @Composable
        fun `OpenPermissionSettings`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_open_permission_settings, *args)

        val `PermissionSettingsUnavailable`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_app_list_permission_settings_unavailable
                )

        @Composable
        fun `PermissionSettingsUnavailable`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_permission_settings_unavailable, *args)

        val `ManualAddTitle`: String
            get() = LocaleBootstrap.getString(R.string.access_control_app_list_manual_add_title)

        @Composable
        fun `ManualAddTitle`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_manual_add_title, *args)

        val `ManualAddPlaceholder`: String
            get() =
                LocaleBootstrap.getString(R.string.access_control_app_list_manual_add_placeholder)

        @Composable
        fun `ManualAddPlaceholder`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_manual_add_placeholder, *args)

        val `AddPackage`: String
            get() = LocaleBootstrap.getString(R.string.access_control_app_list_add_package)

        @Composable
        fun `AddPackage`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_add_package, *args)

        val `InvalidPackage`: String
            get() = LocaleBootstrap.getString(R.string.access_control_app_list_invalid_package)

        @Composable
        fun `InvalidPackage`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_invalid_package, *args)

        val `SelectedPackagesTitle`: String
            get() =
                LocaleBootstrap.getString(R.string.access_control_app_list_selected_packages_title)

        @Composable
        fun `SelectedPackagesTitle`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_selected_packages_title, *args)

        val `NoSelectedPackages`: String
            get() = LocaleBootstrap.getString(R.string.access_control_app_list_no_selected_packages)

        @Composable
        fun `NoSelectedPackages`(vararg args: Any): String =
            stringResource(R.string.access_control_app_list_no_selected_packages, *args)
    }

    object `Settings` {
        val `Title`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_title)

        @Composable
        fun `Title`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_title, *args)

        val `ShowSystemApps`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_show_system_apps)

        @Composable
        fun `ShowSystemApps`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_show_system_apps, *args)

        val `DescendingOrder`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_descending_order)

        @Composable
        fun `DescendingOrder`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_descending_order, *args)

        val `SelectedFirst`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_selected_first)

        @Composable
        fun `SelectedFirst`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_selected_first, *args)

        val `SortMode`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_sort_mode)

        @Composable
        fun `SortMode`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_sort_mode, *args)

        val `SortModeCurrent`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_sort_mode_current)

        @Composable
        fun `SortModeCurrent`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_sort_mode_current, *args)

        val `BatchOperation`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_batch_operation)

        @Composable
        fun `BatchOperation`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_batch_operation, *args)

        val `BatchOperationSummaryBrowse`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_settings_batch_operation_summary_browse
                )

        @Composable
        fun `BatchOperationSummaryBrowse`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_batch_operation_summary_browse, *args)

        val `BatchOperationSummaryManual`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_settings_batch_operation_summary_manual
                )

        @Composable
        fun `BatchOperationSummaryManual`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_batch_operation_summary_manual, *args)

        val `SelectAll`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_select_all)

        @Composable
        fun `SelectAll`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_select_all, *args)

        val `DeselectAll`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_deselect_all)

        @Composable
        fun `DeselectAll`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_deselect_all, *args)

        val `Invert`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_invert)

        @Composable
        fun `Invert`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_invert, *args)

        val `CopySelected`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_copy_selected)

        @Composable
        fun `CopySelected`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_copy_selected, *args)

        val `ClearSelected`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_clear_selected)

        @Composable
        fun `ClearSelected`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_clear_selected, *args)

        val `ClearSelectedResult`: String
            get() =
                LocaleBootstrap.getString(R.string.access_control_settings_clear_selected_result)

        @Composable
        fun `ClearSelectedResult`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_clear_selected_result, *args)

        val `ImportExport`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_import_export)

        @Composable
        fun `ImportExport`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import_export, *args)

        val `ImportExportSummaryBrowse`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_settings_import_export_summary_browse
                )

        @Composable
        fun `ImportExportSummaryBrowse`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import_export_summary_browse, *args)

        val `ImportExportSummaryManual`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_settings_import_export_summary_manual
                )

        @Composable
        fun `ImportExportSummaryManual`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import_export_summary_manual, *args)

        val `Import`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_import)

        @Composable
        fun `Import`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import, *args)

        val `Export`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_export)

        @Composable
        fun `Export`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_export, *args)

        val `ImportSuccess`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_import_success)

        @Composable
        fun `ImportSuccess`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import_success, *args)

        val `ImportPartial`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_import_partial)

        @Composable
        fun `ImportPartial`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import_partial, *args)

        val `ExportSuccess`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_export_success)

        @Composable
        fun `ExportSuccess`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_export_success, *args)

        val `ImportFailed`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_import_failed)

        @Composable
        fun `ImportFailed`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_import_failed, *args)

        val `RegionQuickSelect`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_region_quick_select)

        @Composable
        fun `RegionQuickSelect`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_region_quick_select, *args)

        val `RegionQuickSelectSummaryBrowse`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_settings_region_quick_select_summary_browse
                )

        @Composable
        fun `RegionQuickSelectSummaryBrowse`(vararg args: Any): String =
            stringResource(
                R.string.access_control_settings_region_quick_select_summary_browse,
                *args,
            )

        val `RegionQuickSelectSummaryManual`: String
            get() =
                LocaleBootstrap.getString(
                    R.string.access_control_settings_region_quick_select_summary_manual
                )

        @Composable
        fun `RegionQuickSelectSummaryManual`(vararg args: Any): String =
            stringResource(
                R.string.access_control_settings_region_quick_select_summary_manual,
                *args,
            )

        val `ChinaApps`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_china_apps)

        @Composable
        fun `ChinaApps`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_china_apps, *args)

        val `OverseasApps`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_overseas_apps)

        @Composable
        fun `OverseasApps`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_overseas_apps, *args)

        val `RegionSelectResult`: String
            get() = LocaleBootstrap.getString(R.string.access_control_settings_region_select_result)

        @Composable
        fun `RegionSelectResult`(vararg args: Any): String =
            stringResource(R.string.access_control_settings_region_select_result, *args)
    }

    object `SortMode` {
        val `PackageName`: String
            get() = LocaleBootstrap.getString(R.string.access_control_sort_mode_package_name)

        @Composable
        fun `PackageName`(vararg args: Any): String =
            stringResource(R.string.access_control_sort_mode_package_name, *args)

        val `Label`: String
            get() = LocaleBootstrap.getString(R.string.access_control_sort_mode_label)

        @Composable
        fun `Label`(vararg args: Any): String =
            stringResource(R.string.access_control_sort_mode_label, *args)

        val `InstallTime`: String
            get() = LocaleBootstrap.getString(R.string.access_control_sort_mode_install_time)

        @Composable
        fun `InstallTime`(vararg args: Any): String =
            stringResource(R.string.access_control_sort_mode_install_time, *args)

        val `UpdateTime`: String
            get() = LocaleBootstrap.getString(R.string.access_control_sort_mode_update_time)

        @Composable
        fun `UpdateTime`(vararg args: Any): String =
            stringResource(R.string.access_control_sort_mode_update_time, *args)
    }

    object `Button` {
        val `Cancel`: String
            get() = LocaleBootstrap.getString(R.string.access_control_button_cancel)

        @Composable
        fun `Cancel`(vararg args: Any): String =
            stringResource(R.string.access_control_button_cancel, *args)

        val `Confirm`: String
            get() = LocaleBootstrap.getString(R.string.access_control_button_confirm)

        @Composable
        fun `Confirm`(vararg args: Any): String =
            stringResource(R.string.access_control_button_confirm, *args)
    }
}

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

object MLangAbout {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.about_title)

    @Composable
    fun `Title`(vararg args: Any): String = LocaleBootstrap.getString(R.string.about_title, *args)

    object `App` {
        val `Description`: String
            get() = LocaleBootstrap.getString(R.string.about_app_description)

        @Composable
        fun `Description`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_app_description, *args)

        val `VersionLoading`: String
            get() = LocaleBootstrap.getString(R.string.about_app_version_loading)

        @Composable
        fun `VersionLoading`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_app_version_loading, *args)

        val `VersionFailed`: String
            get() = LocaleBootstrap.getString(R.string.about_app_version_failed)

        @Composable
        fun `VersionFailed`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_app_version_failed, *args)

        val `VersionWithMihomo`: String
            get() = LocaleBootstrap.getString(R.string.about_app_version_with_mihomo)

        @Composable
        fun `VersionWithMihomo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_app_version_with_mihomo, *args)

        val `Name`: String
            get() = LocaleBootstrap.getString(R.string.about_app_name)

        @Composable
        fun `Name`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_app_name, *args)
    }

    object `Section` {
        val `ProjectLinks`: String
            get() = LocaleBootstrap.getString(R.string.about_section_project_links)

        @Composable
        fun `ProjectLinks`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_section_project_links, *args)

        val `Credits`: String
            get() = LocaleBootstrap.getString(R.string.about_section_credits)

        @Composable
        fun `Credits`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_section_credits, *args)

        val `More`: String
            get() = LocaleBootstrap.getString(R.string.about_section_more)

        @Composable
        fun `More`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_section_more, *args)

        val `License`: String
            get() = LocaleBootstrap.getString(R.string.about_section_license)

        @Composable
        fun `License`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_section_license, *args)
    }

    object `Link` {
        val `Repository`: String
            get() = LocaleBootstrap.getString(R.string.about_link_repository)

        @Composable
        fun `Repository`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_link_repository, *args)

        val `Releases`: String
            get() = LocaleBootstrap.getString(R.string.about_link_releases)

        @Composable
        fun `Releases`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_link_releases, *args)

        val `Mihomo`: String
            get() = LocaleBootstrap.getString(R.string.about_link_mihomo)

        @Composable
        fun `Mihomo`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_link_mihomo, *args)

        val `Upstream`: String
            get() = LocaleBootstrap.getString(R.string.about_link_upstream)

        @Composable
        fun `Upstream`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_link_upstream, *args)

        val `TelegramGroup`: String
            get() = LocaleBootstrap.getString(R.string.about_link_telegram_group)

        @Composable
        fun `TelegramGroup`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_link_telegram_group, *args)

        val `TelegramChannel`: String
            get() = LocaleBootstrap.getString(R.string.about_link_telegram_channel)

        @Composable
        fun `TelegramChannel`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_link_telegram_channel, *args)
    }

    object `License` {
        val `CheckUpdate`: String
            get() = LocaleBootstrap.getString(R.string.about_license_check_update)

        @Composable
        fun `CheckUpdate`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_license_check_update, *args)

        val `CheckUpdateSummary`: String
            get() = LocaleBootstrap.getString(R.string.about_license_check_update_summary)

        @Composable
        fun `CheckUpdateSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_license_check_update_summary, *args)

        val `Libraries`: String
            get() = LocaleBootstrap.getString(R.string.about_license_libraries)

        @Composable
        fun `Libraries`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_license_libraries, *args)

        val `LibrariesSummary`: String
            get() = LocaleBootstrap.getString(R.string.about_license_libraries_summary)

        @Composable
        fun `LibrariesSummary`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_license_libraries_summary, *args)

        val `AgplName`: String
            get() = LocaleBootstrap.getString(R.string.about_license_agpl_name)

        @Composable
        fun `AgplName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_license_agpl_name, *args)

        val `AgplDescription`: String
            get() = LocaleBootstrap.getString(R.string.about_license_agpl_description)

        @Composable
        fun `AgplDescription`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.about_license_agpl_description, *args)
    }

    val `Copyright`: String
        get() = LocaleBootstrap.getString(R.string.about_copyright)

    @Composable
    fun `Copyright`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.about_copyright, *args)
}

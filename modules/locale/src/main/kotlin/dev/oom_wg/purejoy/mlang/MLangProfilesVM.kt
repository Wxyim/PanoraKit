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

object MLangProfilesVM {
    object `Message` {
        val `ProfileAdded`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_profile_added)

        @Composable
        fun `ProfileAdded`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_profile_added, *args)

        val `AddFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_add_failed)

        @Composable
        fun `AddFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_add_failed, *args)

        val `ProfileDeleted`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_profile_deleted)

        @Composable
        fun `ProfileDeleted`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_profile_deleted, *args)

        val `DeleteFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_delete_failed)

        @Composable
        fun `DeleteFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_delete_failed, *args)

        val `ProfileUpdated`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_profile_updated)

        @Composable
        fun `ProfileUpdated`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_profile_updated, *args)

        val `UpdateFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_update_failed)

        @Composable
        fun `UpdateFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_update_failed, *args)

        val `ToggleFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_toggle_failed)

        @Composable
        fun `ToggleFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_toggle_failed, *args)

        val `ProfileImported`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_profile_imported)

        @Composable
        fun `ProfileImported`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_profile_imported, *args)

        val `ImportFailed`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_message_import_failed)

        @Composable
        fun `ImportFailed`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_message_import_failed, *args)
    }

    object `Progress` {
        val `Preparing`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_progress_preparing)

        @Composable
        fun `Preparing`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_progress_preparing, *args)

        val `ImportPreparing`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_progress_import_preparing)

        @Composable
        fun `ImportPreparing`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_progress_import_preparing, *args)

        val `Verifying`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_progress_verifying)

        @Composable
        fun `Verifying`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_progress_verifying, *args)

        val `ImportComplete`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_progress_import_complete)

        @Composable
        fun `ImportComplete`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_progress_import_complete, *args)
    }

    object `Error` {
        val `ProfileNotExist`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_error_profile_not_exist)

        @Composable
        fun `ProfileNotExist`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_error_profile_not_exist, *args)

        val `Unknown`: String
            get() = LocaleBootstrap.getString(R.string.profiles_v_m_error_unknown)

        @Composable
        fun `Unknown`(vararg args: Any): String =
            stringResource(R.string.profiles_v_m_error_unknown, *args)
    }
}

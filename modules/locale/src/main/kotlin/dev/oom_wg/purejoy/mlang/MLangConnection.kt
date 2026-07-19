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

object MLangConnection {
    object `Detail` {
        val `Info`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_info)

        @Composable
        fun `Info`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_info, *args)

        val `Protocol`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_protocol)

        @Composable
        fun `Protocol`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_protocol, *args)

        val `SourceApp`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_source_app)

        @Composable
        fun `SourceApp`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_source_app, *args)

        val `PackageName`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_package_name)

        @Composable
        fun `PackageName`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_package_name, *args)

        val `Process`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_process)

        @Composable
        fun `Process`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_process, *args)

        val `SourceAddress`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_source_address)

        @Composable
        fun `SourceAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_source_address, *args)

        val `DestinationAddress`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_destination_address)

        @Composable
        fun `DestinationAddress`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_destination_address, *args)

        val `Duration`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_duration)

        @Composable
        fun `Duration`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_duration, *args)

        val `Upload`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_upload)

        @Composable
        fun `Upload`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_upload, *args)

        val `Download`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_download)

        @Composable
        fun `Download`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_download, *args)

        val `Rule`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_rule)

        @Composable
        fun `Rule`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_rule, *args)

        val `Type`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_type)

        @Composable
        fun `Type`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_type, *args)

        val `Content`: String
            get() = LocaleBootstrap.getString(R.string.connection_detail_content)

        @Composable
        fun `Content`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.connection_detail_content, *args)
    }
}

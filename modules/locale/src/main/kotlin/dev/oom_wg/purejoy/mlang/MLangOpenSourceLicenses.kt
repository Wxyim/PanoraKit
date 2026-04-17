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

object MLangOpenSourceLicenses {
    val `Title`: String
        get() = LocaleBootstrap.getString(R.string.open_source_licenses_title)

    @Composable
    fun `Title`(vararg args: Any): String =
        LocaleBootstrap.getString(R.string.open_source_licenses_title, *args)

    object `LicenseSheet` {
        val `NoContent`: String
            get() =
                LocaleBootstrap.getString(R.string.open_source_licenses_license_sheet_no_content)

        @Composable
        fun `NoContent`(vararg args: Any): String =
            LocaleBootstrap.getString(R.string.open_source_licenses_license_sheet_no_content, *args)
    }
}

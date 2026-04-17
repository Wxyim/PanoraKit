/*
 * This file is part of MonadBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) MonadBox Contributors 2026 - Present
 */
package com.github.nomadboxlab.monadbox.domain.app

/**
 * Application identity/version metadata, injected via Koin so that feature modules can reference it
 * without depending on the app module's generated BuildConfig class.
 */
data class AppInfo(
    val versionName: String,
    val versionCode: Int,
    val buildType: String,
    val mihomoVersion: String,
    val isDebug: Boolean,
)

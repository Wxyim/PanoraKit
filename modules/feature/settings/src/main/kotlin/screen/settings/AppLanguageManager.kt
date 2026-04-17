/*
 * This file is part of MonadBox - A customized edition of YumeBox.
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
 * Copyright (c) YumeLira 2025 - 2026
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */

package com.github.nomadboxlab.monadbox.feature.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.github.nomadboxlab.monadbox.core.locale.LocaleBootstrap
import com.github.nomadboxlab.monadbox.data.model.AppLanguage

object AppLanguageManager {

    fun apply(language: AppLanguage) {
        val languageTags = language.toLanguageTags()
        val currentTags =
            AppCompatDelegate.getApplicationLocales().toLanguageTags().ifBlank { null }

        if (LocaleBootstrap.currentLanguageTags() != languageTags) {
            LocaleBootstrap.setLanguageTags(languageTags)
        }
        if (currentTags != languageTags) {
            AppCompatDelegate.setApplicationLocales(
                languageTags?.let(LocaleListCompat::forLanguageTags)
                    ?: LocaleListCompat.getEmptyLocaleList()
            )
        }
    }

    private fun AppLanguage.toLanguageTags(): String? =
        when (this) {
            AppLanguage.System -> null
            AppLanguage.Zh -> "zh-CN"
            AppLanguage.En -> "en"
        }
}

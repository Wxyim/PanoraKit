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

package com.github.nomadboxlab.monadbox.domain.deeplink

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide bus carrying pending deep-link payloads that originated in
 * [android.app.Activity.onNewIntent] or launcher intents and need to be consumed by a Composable
 * screen once it is recomposed.
 *
 * Kept as a class so it can be provided via Koin and swapped in tests, rather than static state on
 * an Activity.
 */
class DeepLinkBus {
    private val _pendingImportUrl = MutableStateFlow<String?>(null)
    val pendingImportUrl: StateFlow<String?> = _pendingImportUrl.asStateFlow()

    fun postImportUrl(url: String) {
        _pendingImportUrl.value = url
    }

    fun clearImportUrl() {
        _pendingImportUrl.value = null
    }
}

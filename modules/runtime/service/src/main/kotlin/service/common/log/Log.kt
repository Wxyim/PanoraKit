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

package com.github.nomadboxlab.monadbox.service.common.log

import timber.log.Timber

object Log {
    private const val TAG = "MonadBox"

    fun d(message: String, throwable: Throwable? = null) = Timber.tag(TAG).d(throwable, message)

    fun i(message: String, throwable: Throwable? = null) = Timber.tag(TAG).i(throwable, message)

    fun w(message: String, throwable: Throwable? = null) = Timber.tag(TAG).w(throwable, message)

    fun e(message: String, throwable: Throwable? = null) = Timber.tag(TAG).e(throwable, message)
}

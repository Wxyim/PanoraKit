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
 *
 */

package com.github.nomadboxlab.monadbox.core.locale

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.annotation.StringRes

/**
 * Process-wide holder for the application Context so non-Composable MLang getters can resolve
 * string resources without threading a Context through every call site.
 *
 * Normally installed from `Application.onCreate` via [install]. As a defensive fallback (used by
 * Robolectric-based unit tests that set Compose content without booting the real Application
 * class), [getString] will reflectively resolve `ActivityThread.currentApplication()` on first call
 * if nothing was installed yet.
 */
object LocaleBootstrap {
    @SuppressLint("StaticFieldLeak") @Volatile private var appContext: Context? = null
    @Volatile private var overrideLanguageTags: String? = null
    @SuppressLint("StaticFieldLeak") @Volatile private var cachedLocalizedContext: Context? = null
    @Volatile private var cachedLanguageTags: String? = null
    private val cacheLock = Any()

    fun install(context: Context) {
        val applicationContext = context.applicationContext
        if (appContext !== applicationContext) {
            appContext = applicationContext
            invalidateLocalizedContextCache()
        }
    }

    fun setLanguageTags(languageTags: String?) {
        val normalized = languageTags.normalizeLanguageTags()
        if (overrideLanguageTags != normalized) {
            overrideLanguageTags = normalized
            invalidateLocalizedContextCache()
        }
    }

    fun currentLanguageTags(): String? = overrideLanguageTags

    private fun resolveContext(): Context {
        appContext?.let {
            return it
        }
        val fallback =
            tryReflectionAppContext()
                ?: error(
                    "LocaleBootstrap not initialised — call LocaleBootstrap.install(context) from Application.onCreate."
                )
        appContext = fallback
        return fallback
    }

    private fun tryReflectionAppContext(): Context? =
        runCatching {
                val atClass = Class.forName("android.app.ActivityThread")
                val method = atClass.getMethod("currentApplication")
                method.invoke(null) as? Context
            }
            .getOrNull()

    private fun invalidateLocalizedContextCache() {
        synchronized(cacheLock) {
            cachedLocalizedContext = null
            cachedLanguageTags = null
        }
    }

    private fun resolveLocalizedContext(): Context {
        val context = resolveContext()
        val languageTags = overrideLanguageTags
        if (languageTags == null) {
            return context
        }

        cachedLocalizedContext
            ?.takeIf { cachedLanguageTags == languageTags }
            ?.let {
                return it
            }

        return synchronized(cacheLock) {
            cachedLocalizedContext
                ?.takeIf { cachedLanguageTags == languageTags }
                ?.let {
                    return@synchronized it
                }

            val configuration = Configuration(context.resources.configuration)
            configuration.setLocales(LocaleList.forLanguageTags(languageTags))
            context.createConfigurationContext(configuration).also { localizedContext ->
                cachedLanguageTags = languageTags
                cachedLocalizedContext = localizedContext
            }
        }
    }

    fun getString(@StringRes id: Int): String = resolveLocalizedContext().getString(id)

    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String =
        resolveLocalizedContext().getString(id, *formatArgs)
}

private fun String?.normalizeLanguageTags(): String? = this?.trim()?.takeIf { it.isNotBlank() }

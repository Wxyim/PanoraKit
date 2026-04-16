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

package com.github.nomadboxlab.monadbox.service.runtime.config

import kotlin.reflect.KProperty

class Store(val provider: StoreProvider) {
    interface Delegate<T> {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    }

    private fun <T> delegate(getter: () -> T, setter: (T) -> Unit): Delegate<T> {
        return object : Delegate<T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                return getter()
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                setter(value)
            }
        }
    }

    fun int(key: String, defaultValue: Int): Delegate<Int> {
        return delegate(
            getter = { provider.getInt(key, defaultValue) },
            setter = { value -> provider.setInt(key, value) },
        )
    }

    fun long(key: String, defaultValue: Long): Delegate<Long> {
        return delegate(
            getter = { provider.getLong(key, defaultValue) },
            setter = { value -> provider.setLong(key, value) },
        )
    }

    fun string(key: String, defaultValue: String): Delegate<String> {
        return delegate(
            getter = { provider.getString(key, defaultValue) },
            setter = { value -> provider.setString(key, value) },
        )
    }

    fun stringSet(key: String, defaultValue: Set<String>): Delegate<Set<String>> {
        return delegate(
            getter = { provider.getStringSet(key, defaultValue) },
            setter = { value -> provider.setStringSet(key, value) },
        )
    }

    fun boolean(key: String, defaultValue: Boolean): Delegate<Boolean> {
        return delegate(
            getter = { provider.getBoolean(key, defaultValue) },
            setter = { value -> provider.setBoolean(key, value) },
        )
    }

    fun <T : Enum<T>> enum(key: String, defaultValue: T, values: Array<T>): Delegate<T> {
        return object : Delegate<T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                val name = provider.getString(key, defaultValue.name)
                return values.find { name == it.name } ?: defaultValue
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                provider.setString(key, value.name)
            }
        }
    }

    fun <T> typedString(key: String, from: (String) -> T?, to: (T?) -> String): Delegate<T?> {
        return object : Delegate<T?> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
                val value = provider.getString(key, to(null))
                return from(value)
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                provider.setString(key, to(value))
            }
        }
    }
}

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

package com.github.nomadboxlab.monadbox.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Preferences-DataStore counterpart to [MMKVPreference]. Preserves the same delegate surface
 * (`boolFlow`, `intFlow`, `strFlow`, `longFlow`, `floatFlow`, `enumFlow`) so call sites keep using
 * `Preference<T>` unchanged.
 *
 * Synchronous reads are satisfied from an in-memory [MutableStateFlow] that is seeded via a one-off
 * blocking read of the DataStore on first access, matching MMKV's synchronous semantics. Writes
 * update the state-flow immediately and dispatch an async `edit { }` on [writeScope].
 *
 * Intended scope: app-process-only stores. Cross-process stores (used by `runtime:service`) must
 * stay on MMKV — DataStore has no multi-process guarantees.
 */
abstract class DataStorePreference(
    @PublishedApi internal val dataStore: DataStore<Preferences>,
    @PublishedApi internal val writeScope: CoroutineScope,
) {

    @PublishedApi
    internal val snapshot: Preferences by lazy { runBlocking { dataStore.data.first() } }

    protected fun boolFlow(default: Boolean = false): ReadOnlyProperty<Any?, Preference<Boolean>> =
        prefDelegate(default, ::booleanPreferencesKey)

    protected fun strFlow(default: String = ""): ReadOnlyProperty<Any?, Preference<String>> =
        prefDelegate(default, ::stringPreferencesKey)

    protected fun intFlow(default: Int = 0): ReadOnlyProperty<Any?, Preference<Int>> =
        prefDelegate(default, ::intPreferencesKey)

    protected fun longFlow(default: Long = 0L): ReadOnlyProperty<Any?, Preference<Long>> =
        prefDelegate(default, ::longPreferencesKey)

    protected fun floatFlow(default: Float = 0f): ReadOnlyProperty<Any?, Preference<Float>> =
        prefDelegate(default, ::floatPreferencesKey)

    protected inline fun <reified T : Enum<T>> enumFlow(
        default: T
    ): ReadOnlyProperty<Any?, Preference<T>> =
        object : ReadOnlyProperty<Any?, Preference<T>> {
            private var cached: Preference<T>? = null

            override fun getValue(thisRef: Any?, property: KProperty<*>): Preference<T> {
                cached?.let {
                    return it
                }
                val prefKey = stringPreferencesKey(property.name)
                val initial =
                    snapshot[prefKey]?.let { name ->
                        runCatching { java.lang.Enum.valueOf(T::class.java, name) }
                            .getOrDefault(default)
                    } ?: default
                val flow = MutableStateFlow(initial)
                val pref =
                    Preference(
                        state = flow.asStateFlow(),
                        update = { value ->
                            if (flow.value != value) {
                                flow.value = value
                                writeScope.launch { dataStore.edit { it[prefKey] = value.name } }
                            }
                        },
                        get = { flow.value },
                    )
                cached = pref
                return pref
            }
        }

    private fun <T> prefDelegate(
        default: T,
        keyFactory: (String) -> Preferences.Key<T>,
    ): ReadOnlyProperty<Any?, Preference<T>> =
        object : ReadOnlyProperty<Any?, Preference<T>> {
            private var cached: Preference<T>? = null

            override fun getValue(thisRef: Any?, property: KProperty<*>): Preference<T> {
                cached?.let {
                    return it
                }
                val prefKey = keyFactory(property.name)
                val initial: T = snapshot[prefKey] ?: default
                val flow = MutableStateFlow(initial)
                val pref =
                    Preference(
                        state = flow.asStateFlow(),
                        update = { value ->
                            if (flow.value != value) {
                                flow.value = value
                                writeScope.launch { dataStore.edit { it[prefKey] = value } }
                            }
                        },
                        get = { flow.value },
                    )
                cached = pref
                return pref
            }
        }
}

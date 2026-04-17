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
import com.tencent.mmkv.MMKV

/**
 * One-shot import of legacy MMKV-backed keys into a Preferences [DataStore].
 *
 * Copies every key present in [mmkv] into [dataStore] using best-effort type inference (MMKV stores
 * values as raw bytes and does not expose types via its public API, so we probe each of the
 * supported types in turn). The importer is keyed on a boolean flag in the target DataStore
 * (`__imported_from_mmkv`), so it only runs on first launch after upgrade.
 *
 * Safe to call from any coroutine scope. Idempotent.
 */
class LegacyMmkvImporter(private val dataStore: DataStore<Preferences>, private val mmkv: MMKV) {

    suspend fun importIfNeeded() {
        dataStore.edit { prefs ->
            if (prefs[IMPORTED_FLAG] == true) return@edit
            val keys = mmkv.allKeys() ?: emptyArray()
            for (key in keys) {
                copyKey(prefs, key)
            }
            prefs[IMPORTED_FLAG] = true
        }
    }

    private fun copyKey(
        prefs: androidx.datastore.preferences.core.MutablePreferences,
        key: String,
    ) {
        // MMKV's public API does not expose the stored type. Probe in decreasing
        // specificity. Numeric-like keys are written as long/int; boolean-like as
        // bool. A typed MMKV decode that returns the default for a mismatched key
        // cannot be distinguished from a true default write, so we only copy when
        // the decoded value is non-default for integer/long and always copy for
        // strings (which MMKV reports null for a missing key).
        val asString = mmkv.decodeString(key)
        if (asString != null) {
            prefs[stringPreferencesKey(key)] = asString
            return
        }
        val asLong = mmkv.decodeLong(key, Long.MIN_VALUE)
        if (asLong != Long.MIN_VALUE) {
            prefs[longPreferencesKey(key)] = asLong
            return
        }
        val asInt = mmkv.decodeInt(key, Int.MIN_VALUE)
        if (asInt != Int.MIN_VALUE) {
            prefs[intPreferencesKey(key)] = asInt
            return
        }
        val asFloat = mmkv.decodeFloat(key, Float.NaN)
        if (!asFloat.isNaN()) {
            prefs[floatPreferencesKey(key)] = asFloat
            return
        }
        // Fall back to boolean — false here is indistinguishable from absent but
        // harmless since consumers use their own defaults.
        prefs[booleanPreferencesKey(key)] = mmkv.decodeBool(key, false)
    }

    companion object {
        private val IMPORTED_FLAG = booleanPreferencesKey("__imported_from_mmkv")
    }
}

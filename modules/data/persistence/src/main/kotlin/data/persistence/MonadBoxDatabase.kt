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

package com.github.nomadboxlab.monadbox.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Single-process Room database backing record-shaped stores that have outgrown MMKV's JSON-blob
 * pattern. Currently holds profile links only; add more DAOs here as sibling stores migrate.
 */
@Database(entities = [ProfileLinkEntity::class], version = 1, exportSchema = false)
abstract class MonadBoxDatabase : RoomDatabase() {

    abstract fun profileLinkDao(): ProfileLinkDao

    companion object {
        /** File name for the on-disk database. Stable — do not rename. */
        const val NAME = "monadbox.db"

        fun create(context: Context): MonadBoxDatabase =
            Room.databaseBuilder(context.applicationContext, MonadBoxDatabase::class.java, NAME)
                .fallbackToDestructiveMigration(dropAllTables = false)
                .build()
    }
}

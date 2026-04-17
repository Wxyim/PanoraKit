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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileLinkDao {

    @Query("SELECT * FROM profile_links ORDER BY position ASC")
    fun observeAll(): Flow<List<ProfileLinkEntity>>

    @Query("SELECT * FROM profile_links ORDER BY position ASC")
    suspend fun getAll(): List<ProfileLinkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ProfileLinkEntity>)

    @Query("DELETE FROM profile_links") suspend fun deleteAll()

    /** Replace every row so the table matches [entities]' order and content. */
    @Transaction
    suspend fun replaceAll(entities: List<ProfileLinkEntity>) {
        deleteAll()
        if (entities.isNotEmpty()) insertAll(entities)
    }
}

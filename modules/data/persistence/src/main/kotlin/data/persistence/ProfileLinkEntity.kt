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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Row shape for the profile links table. Ordering is preserved via [position], mirroring the former
 * JSON list index in MMKV. The `position` index backs the `ORDER BY position ASC` access path used
 * by [ProfileLinkDao.observeAll] / [ProfileLinkDao.getAll].
 */
@Entity(
    tableName = "profile_links",
    indices = [Index(value = ["position"], name = "index_profile_links_position")],
)
data class ProfileLinkEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "position") val position: Int,
)

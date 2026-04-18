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

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MonadBoxDatabaseMigrationTest {
    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            MonadBoxDatabase::class.java,
            emptyList(),
            FrameworkSQLiteOpenHelperFactory(),
        )

    @Test
    fun migration1To2_preservesProfileLinksAndValidatesSchema() = runBlocking {
        helper.createDatabase(TestDatabaseName, 1).apply {
            execSQL(
                """
                    INSERT INTO profile_links (id, name, url, position)
                    VALUES ('default', 'Default', 'https://example.com/config.yaml', 0)
                    """
                    .trimIndent()
            )
            close()
        }

        helper
            .runMigrationsAndValidate(
                TestDatabaseName,
                2,
                true,
                MonadBoxDatabaseMigrations.MIGRATION_1_2,
            )
            .use { migrated ->
                migrated.query("PRAGMA index_list(`profile_links`)").use { cursor ->
                    val nameColumn = cursor.getColumnIndexOrThrow("name")
                    val indexNames = buildList {
                        while (cursor.moveToNext()) {
                            add(cursor.getString(nameColumn))
                        }
                    }
                    assertTrue(
                        "expected index_profile_links_position in $indexNames",
                        indexNames.contains("index_profile_links_position"),
                    )
                }
            }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val migratedDatabase =
            Room.databaseBuilder(context, MonadBoxDatabase::class.java, TestDatabaseName)
                .addMigrations(MonadBoxDatabaseMigrations.MIGRATION_1_2)
                .build()

        try {
            assertEquals(
                listOf(
                    ProfileLinkEntity(
                        id = "default",
                        name = "Default",
                        url = "https://example.com/config.yaml",
                        position = 0,
                    )
                ),
                migratedDatabase.profileLinkDao().getAll(),
            )
        } finally {
            migratedDatabase.close()
            context.deleteDatabase(TestDatabaseName)
        }
    }

    private companion object {
        const val TestDatabaseName = "monadbox-migration-test.db"
    }
}

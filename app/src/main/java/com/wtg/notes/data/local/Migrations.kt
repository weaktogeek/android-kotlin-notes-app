package com.wtg.notes.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    private const val VERSION_1: Int = 1
    private const val VERSION_2: Int = 2

    val MIGRATION_1_2 = object : Migration(VERSION_1, VERSION_2) {
        override fun migrate(db: SupportSQLiteDatabase) {

            // Create the new table
            db.execSQL("CREATE TABLE notes_new (note_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL,content TEXT NOT NULL,category INTEGER NOT NULL, favorite INTEGER NOT NULL, password TEXT NOT NULL, color INTEGER NOT NULL DEFAULT 0, created INTEGER NOT NULL, modified INTEGER NOT NULL)")
            // Copy the data
            db.execSQL("INSERT INTO notes_new  (note_id, title, content, category, favorite, password, created, modified) SELECT note_id, title, content, category, favorite, password, created, modified FROM note")
            // Remove the old table
            db.execSQL("DROP TABLE note")
            // Change the table name to the correct one
            db.execSQL("ALTER TABLE notes_new RENAME TO note")
        }
    }
}
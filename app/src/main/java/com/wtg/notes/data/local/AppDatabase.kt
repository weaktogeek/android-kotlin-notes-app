package com.wtg.notes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wtg.notes.DATABASE_NAME
import com.wtg.notes.data.local.Migrations.MIGRATION_1_2
import com.wtg.notes.model.NoteModel

@Database(
    entities = [NoteModel::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(), NoteDatabase {

    abstract override fun getNoteDao(): NoteDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        // Check for DB instance if not null then get or create new DB Instance
        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: createDatabase(context)
                .also { instance = it }
        }

        // create db instance
        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .setJournalMode(JournalMode.TRUNCATE) // will work with one file to DB for export and import
            .addMigrations(MIGRATION_1_2) // for migrations
            .build()
    }
}
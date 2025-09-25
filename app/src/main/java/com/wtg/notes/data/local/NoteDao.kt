package com.wtg.notes.data.local

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.wtg.notes.model.NoteModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // return id of insert or update note
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteModel): Long

    // get all note from db
    @Query("SELECT * FROM note")
    fun getAllNotes(): Flow<List<NoteModel>>

    // get all note from db
    @Query("SELECT * FROM note ORDER BY created DESC")
    fun getAllNotesByCreatedDate(): Flow<List<NoteModel>>

    @Query("SELECT * FROM note WHERE modified IN (:modified)")
    fun getAllByDate(modified: Int): Flow<List<NoteModel>>

    // delete note from db
    @Delete
    suspend fun deleteNote(note: NoteModel)

    // delete all notes from table
    @Query("DELETE FROM note")
    suspend fun deleteAllNotes()

    @RawQuery
    suspend fun checkPoint(supportSQLiteQuery: SupportSQLiteQuery):Int
}
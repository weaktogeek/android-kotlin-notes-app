package com.wtg.notes.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.wtg.notes.data.local.NoteDatabase
import com.wtg.notes.model.NoteModel
import kotlinx.coroutines.flow.Flow

class Repo(private val db: NoteDatabase) : Repository {
    override suspend fun upsertNote(note: NoteModel): Long = db.getNoteDao().upsert(note)
    override fun getAllNotes(): Flow<List<NoteModel>> = db.getNoteDao().getAllNotes()
    override fun getAllNotesByCreatedDate(): Flow<List<NoteModel>> =
        db.getNoteDao().getAllNotesByCreatedDate()

    override suspend fun deleteNote(note: NoteModel) = db.getNoteDao().deleteNote(note)
    override suspend fun deleteAllNotes() = db.getNoteDao().deleteAllNotes()

    override suspend fun checkpoint() {
        db.getNoteDao().checkPoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }
}

interface Repository {
    suspend fun upsertNote(note: NoteModel): Long
    fun getAllNotes(): Flow<List<NoteModel>>
    fun getAllNotesByCreatedDate(): Flow<List<NoteModel>>
    suspend fun deleteNote(note: NoteModel)
    suspend fun deleteAllNotes()
    suspend fun checkpoint()
}
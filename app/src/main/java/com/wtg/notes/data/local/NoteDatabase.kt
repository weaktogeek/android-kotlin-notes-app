package com.wtg.notes.data.local

interface NoteDatabase {
    fun getNoteDao(): NoteDao
}
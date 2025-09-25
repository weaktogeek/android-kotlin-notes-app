package com.wtg.notes.di

import com.wtg.notes.data.Repo
import com.wtg.notes.data.Repository
import com.wtg.notes.data.local.NoteDao
import com.wtg.notes.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceResolver {

    @Provides
    @Singleton
    fun provideNoteDao(noteDatabase: NoteDatabase): NoteDao {
        return noteDatabase.getNoteDao()
    }

    @Provides
    @Singleton
    fun provideRepository(noteDatabase: NoteDatabase): Repository {
        return Repo(noteDatabase)
    }
}
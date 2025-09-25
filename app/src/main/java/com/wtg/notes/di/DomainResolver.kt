package com.wtg.notes.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.wtg.notes.data.local.AppDatabase
import com.wtg.notes.data.local.NoteDatabase
import com.wtg.notes.utils.IReadDataStore
import com.wtg.notes.utils.IWriteDataStore
import com.wtg.notes.utils.PrefsDataStore
import javax.inject.Singleton

// Transforms dependencies to android free logic objects
@Module
@InstallIn(SingletonComponent::class)
abstract class DomainResolver {

    //Database
    @Binds
    @Singleton
    abstract fun bindNoteDatabase(appDatabase: AppDatabase): NoteDatabase

    //get interface IWriteDataStore
    @Binds
    @Singleton
    abstract fun bindIWriteDataStore(prefsDataStore: PrefsDataStore): IWriteDataStore

    //get interface IReadDataStore
    @Binds
    @Singleton
    abstract fun bindIReadDataStore(prefsDataStore: PrefsDataStore): IReadDataStore

}
package com.wtg.notes.di

import android.content.Context
import com.wtg.notes.data.local.AppDatabase
import com.wtg.notes.utils.PrefsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FrameworkResolver {

    //Initialize AppDatabase.
    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }

    // Initialize DataStore
    @Singleton
    @Provides
    fun providesPrefDataStore(@ApplicationContext context: Context): PrefsDataStore {
        return PrefsDataStore(context)
    }
}
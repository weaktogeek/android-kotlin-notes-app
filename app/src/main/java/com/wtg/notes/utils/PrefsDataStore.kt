package com.wtg.notes.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

abstract class PrefsStore(fileName: String) {
    internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = fileName
    )
}

class PrefsDataStore @Inject constructor(private val context: Context) :
    PrefsStore(PREF_FILENAME_GENERAL), IReadDataStore, IWriteDataStore {

    // Set Night Mode
    override suspend fun setDarkMode(isNightMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isNightMode
        }
    }

    // Set Ads Mode
    override suspend fun setAdsMode(enableAds: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ADS_MODE_KEY] = enableAds
        }
    }

    // Set Stepper
    override suspend fun setStepper(hideStepper: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[STEPPER_KEY] = hideStepper
        }
    }

    override suspend fun setSwitchView(isSwitched: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SWITCH_VIEW_KEY] = isSwitched
        }
    }

    //Get Night Mode
    override val getDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    // Get Ads Mode
    override val getAdsMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ADS_MODE_KEY] ?: true
        }

    //Get Stepper
    override val getStepper: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[STEPPER_KEY] ?: false
        }

    override val getSwitchView: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SWITCH_VIEW_KEY] ?: false
        }

    companion object {
        private const val PREF_FILENAME_GENERAL = "prefs_general"

        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val ADS_MODE_KEY = booleanPreferencesKey("ads_mode")
        private val STEPPER_KEY = booleanPreferencesKey("stepper")
        private val SWITCH_VIEW_KEY = booleanPreferencesKey("switch_view")
    }
}

interface IReadDataStore {
    val getDarkMode: Flow<Boolean>
    val getAdsMode: Flow<Boolean>
    val getStepper: Flow<Boolean>
    val getSwitchView: Flow<Boolean>
}

interface IWriteDataStore {
    suspend fun setDarkMode(isNightMode: Boolean)
    suspend fun setAdsMode(enableAds: Boolean)
    suspend fun setStepper(hideStepper: Boolean)
    suspend fun setSwitchView(isSwitched: Boolean)
}
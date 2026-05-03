package com.tunalex.sportmap.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sportmap_prefs")

class UserPreferences(private val context: Context) {

    private val keyUserId = longPreferencesKey("current_user_id")
    private val keyDarkMode = booleanPreferencesKey("dark_mode")
    private val keyGpsEnabled = booleanPreferencesKey("gps_enabled")

    val currentUserId: Flow<Long> = context.dataStore.data.map { it[keyUserId] ?: -1L }
    val darkMode: Flow<Boolean?> = context.dataStore.data.map { it[keyDarkMode] }
    val gpsEnabled: Flow<Boolean> = context.dataStore.data.map { it[keyGpsEnabled] ?: true }

    suspend fun setCurrentUserId(id: Long) {
        context.dataStore.edit { it[keyUserId] = id }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[keyDarkMode] = enabled }
    }

    suspend fun setGpsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[keyGpsEnabled] = enabled }
    }
}

package com.makelick.anytime.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AnyTimeDataStore")

    suspend fun <T> saveToDataStore(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun <T> getFromDataStore(key: Preferences.Key<T>): Flow<T?> {
        val data = context.dataStore.data.map { preferences ->
            preferences[key]
        }
        return data
    }

    companion object {
        val KEY_TIMER_MODE = stringPreferencesKey("mode")
        val KEY_TIMER_BREAKS_COUNT = intPreferencesKey("breaks_count")
    }

}
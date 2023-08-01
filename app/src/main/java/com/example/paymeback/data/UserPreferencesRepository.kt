package com.example.paymeback.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val recordsSortedBy: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[RECORDS_SORTED_BY] ?: 0
        }

    private companion object {
        val RECORDS_SORTED_BY = intPreferencesKey("records_sorted_by")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveRecordSortingPreference(sortedBy: Int) {
        dataStore.edit { preferences ->
            preferences[RECORDS_SORTED_BY] = sortedBy
        }
    }
}
package me.ako.yts.domain.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Utils(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")
    private val MOVIES_SIZE = intPreferencesKey("movies_size")
    val moviesSizeFlow: Flow<Int> = context.dataStore.data.map {
        it[MOVIES_SIZE] ?: 0
    }

    suspend fun updateMoviesSize(size: Int) {
        context.dataStore.edit {
            it[MOVIES_SIZE] = size
            Log.d("Utils", "updateMoviesSize: ${it[MOVIES_SIZE]}")
        }
    }
}
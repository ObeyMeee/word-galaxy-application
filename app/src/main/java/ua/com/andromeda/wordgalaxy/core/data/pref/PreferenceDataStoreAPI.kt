package ua.com.andromeda.wordgalaxy.core.data.pref

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferenceDataStoreAPI {
    suspend fun <T> get(key: Preferences.Key<T>, defaultValue: T): Flow<T>
    suspend fun <T> first(key: Preferences.Key<T>, defaultValue: T): T
    suspend fun <T> put(key: Preferences.Key<T>, value: T)
    suspend fun <T> remove(key: Preferences.Key<T>)
    suspend fun clearAll()
}
package ua.com.andromeda.wordgalaxy.core.data.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ua.com.andromeda.wordgalaxy.utils.TAG
import java.io.IOException
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

@Singleton
class PreferenceDataStoreHelper(
    private val dataStore: DataStore<Preferences>
) : PreferenceDataStoreAPI {

    /* This returns us a flow of data from DataStore.
    Basically as soon we update the value in Datastore,
    the values returned by it also changes. */
    override suspend fun <T> get(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading user preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[key] ?: defaultValue
        }

    /* This returns the last saved value of the key. If we change the value,
        it wont effect the values produced by this function */
    override suspend fun <T> first(key: Preferences.Key<T>, defaultValue: T): T =
        dataStore.data.first()[key] ?: defaultValue

    // This Sets the value based on the value passed in value parameter.
    override suspend fun <T> put(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    // This Function removes the Key Value pair from the datastore, hereby removing it completely.
    override suspend fun <T> remove(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    // This function clears the entire Preference Datastore.
    override suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
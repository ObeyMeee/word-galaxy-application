package ua.com.andromeda.wordgalaxy.data.repository.preferences

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

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val amountWordsToLearnPerDay: Flow<Int> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading user preferences", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[AMOUNT_WORDS_TO_LEARN_PER_DAY] ?: DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY
    }

    private companion object {
        val AMOUNT_WORDS_TO_LEARN_PER_DAY = intPreferencesKey(KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY)
        const val DEFAULT_AMOUNT_WORDS_TO_LEARN_PER_DAY = 5
        const val TAG = "UserPreferencesDataStore"
    }

    suspend fun saveAmountWordsToLearnPreferences(value: Int) {
        dataStore.edit { preferences ->
            preferences[AMOUNT_WORDS_TO_LEARN_PER_DAY] = value
        }
    }
}
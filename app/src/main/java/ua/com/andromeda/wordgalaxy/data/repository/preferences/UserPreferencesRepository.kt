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
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.TimePeriodChartOptions
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val timePeriodChartOptions: Flow<Int> = dataStore.data.catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading user preferences", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[TIME_PERIOD_DAYS] ?: DEFAULT_TIME_PERIOD_DAYS
    }

    private companion object {
        const val TAG = "UserPreferencesDataStore"

        val TIME_PERIOD_DAYS = intPreferencesKey(KEY_TIME_PERIOD_DAYS)
        val DEFAULT_TIME_PERIOD_DAYS = TimePeriodChartOptions.WEEK.days
    }

    suspend fun saveTimePeriod(timePeriodChartOptions: TimePeriodChartOptions) {
        dataStore.edit { preferences ->
            preferences[TIME_PERIOD_DAYS] = timePeriodChartOptions.days
        }
    }
}
package ua.com.andromeda.wordgalaxy

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import ua.com.andromeda.wordgalaxy.data.AppDatabase
import ua.com.andromeda.wordgalaxy.data.repository.preferences.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository

class WordGalaxyApplication : Application() {
    val appDatabase: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
)
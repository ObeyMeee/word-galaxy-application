package ua.com.andromeda.wordgalaxy.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.data.repository.preferences.KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
import ua.com.andromeda.wordgalaxy.data.repository.preferences.UserPreferencesRepository

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    @Provides
    fun bindsUserPreferencesRepository(@ApplicationContext appContext: Context) =
        UserPreferencesRepository(appContext.dataStore)
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = KEY_AMOUNT_WORDS_TO_LEARN_PER_DAY
)
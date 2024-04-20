package ua.com.andromeda.wordgalaxy.core.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.core.data.pref.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.core.data.pref.dataStore

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    @Provides
    fun providesPreferenceDataStoreHelper(@ApplicationContext appContext: Context) =
        PreferenceDataStoreHelper(appContext.dataStore)
}
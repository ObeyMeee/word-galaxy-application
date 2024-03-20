package ua.com.andromeda.wordgalaxy.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.andromeda.wordgalaxy.data.local.PreferenceDataStoreHelper
import ua.com.andromeda.wordgalaxy.data.local.dataStore

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    @Provides
    fun providesPreferenceDataStoreHelper(@ApplicationContext appContext: Context) =
        PreferenceDataStoreHelper(appContext.dataStore)
}
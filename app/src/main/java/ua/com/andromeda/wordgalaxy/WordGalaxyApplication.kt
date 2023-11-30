package ua.com.andromeda.wordgalaxy

import android.app.Application
import ua.com.andromeda.wordgalaxy.data.AppDatabase

class WordGalaxyApplication : Application() {
    val appDatabase: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
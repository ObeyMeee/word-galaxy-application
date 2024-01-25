package ua.com.andromeda.wordgalaxy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.com.andromeda.wordgalaxy.data.dao.CategoryDao
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordAndCategoryCrossRef
import ua.com.andromeda.wordgalaxy.utils.Converters

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        Word::class,
        Category::class,
        WordAndCategoryCrossRef::class,
        Phonetic::class,
        Example::class
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "word-galaxy.db"
                )
                    .fallbackToDestructiveMigration()
                    .createFromAsset("database/word-galaxy.db")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
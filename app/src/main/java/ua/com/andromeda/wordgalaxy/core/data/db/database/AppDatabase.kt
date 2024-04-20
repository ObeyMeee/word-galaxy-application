package ua.com.andromeda.wordgalaxy.core.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.com.andromeda.wordgalaxy.core.data.db.converter.Converters
import ua.com.andromeda.wordgalaxy.core.data.db.dao.CategoryDao
import ua.com.andromeda.wordgalaxy.core.data.db.dao.WordDao
import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import ua.com.andromeda.wordgalaxy.core.domain.model.Example
import ua.com.andromeda.wordgalaxy.core.domain.model.Phonetic
import ua.com.andromeda.wordgalaxy.core.domain.model.Word
import ua.com.andromeda.wordgalaxy.core.domain.model.WordAndCategoryCrossRef

@Database(
    version = 3,
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
        const val DATABASE_NAME = "word-galaxy.db"
    }
}
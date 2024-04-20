package ua.com.andromeda.wordgalaxy.core.domain.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ua.com.andromeda.wordgalaxy.R

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true),
    ],
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val materialIconId: String? = null,
    @DrawableRes val customIconId: Int? = null,
)

val MY_WORDS_CATEGORY =
    Category(name = "My words", customIconId = R.drawable.my_words_category_icon)
val EMPTY_CATEGORY = Category(name = "")

typealias Percentage = Float

data class VocabularyCategory(
    @Embedded
    val category: Category,

    @ColumnInfo(name = "total_words")
    val totalWords: Int,

    @ColumnInfo(name = "completed_words")
    val completedWords: Percentage,
)
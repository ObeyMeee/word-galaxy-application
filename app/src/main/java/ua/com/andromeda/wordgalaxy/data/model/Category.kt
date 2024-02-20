package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["parent_category_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["parent_category_id"],
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,

    @ColumnInfo(name = "parent_category_id")
    val parentCategoryId: Long? = null
)

val MY_WORDS_CATEGORY = Category(name = "My words")
val EMPTY_CATEGORY = Category(name = "")

typealias Percentage = Float

data class VocabularyCategory(
    @Embedded
    val category: Category,

    @ColumnInfo(name = "total_words")
    val totalWords: Int,

    @ColumnInfo(name = "completed_words")
    val completedWords: Percentage,

    @Ignore
    val subcategories: List<VocabularyCategory>
) {
    constructor(
        category: Category,
        totalWords: Int,
        completedWords: Percentage
    ) : this(
        category, totalWords, completedWords, emptyList()
    )
}

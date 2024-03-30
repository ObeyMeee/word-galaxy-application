package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.SET_NULL
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "words_categories",
    primaryKeys = ["word_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onUpdate = CASCADE,
            onDelete = SET_NULL
        )
    ]
)
data class WordAndCategoryCrossRef(
    @ColumnInfo(name = "word_id")
    val wordId: Long,

    @ColumnInfo(name = "category_id", index = true)
    val categoryId: Long,
)

data class WordWithCategories(
    @Embedded
    val word: Word,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",

        associateBy = Junction(
            value = WordAndCategoryCrossRef::class,
            parentColumn = "word_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<Category>
)
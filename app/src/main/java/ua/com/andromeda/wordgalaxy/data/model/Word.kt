package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDateTime

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String,
    val transcription: String,
    val translate: String,
    val status: WordStatus,

    @ColumnInfo("amount_repetition")
    val amountRepetition: Int?,

    @ColumnInfo("memorized_at")
    val memorizedAt: LocalDateTime?,

    @ColumnInfo("repeat_at")
    val repeatAt: LocalDateTime?
)

data class WordWithCategories (
    @Embedded
    val word: Word,

    @Relation(
        parentColumn = "id",
        entityColumn = "word_id"
    )
    val categories: List<Category>
)
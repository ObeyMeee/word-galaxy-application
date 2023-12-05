package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDateTime
import kotlin.math.pow

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String,
    val translate: String,
    val status: WordStatus,

    @ColumnInfo("amount_repetition")
    val amountRepetition: Int?,

    @ColumnInfo("memorized_at")
    val memorizedAt: LocalDateTime?,

    @ColumnInfo("repeated_at")
    val repeatedAt: LocalDateTime?,

    @ColumnInfo("next_repeat_at")
    val nextRepeatAt: LocalDateTime?,
)

data class EmbeddedWord(
    @Embedded
    val word: Word,

    @Relation(
        parentColumn = "id",
        entityColumn = "word_id"
    )
    val categories: List<Category>,

    @Relation(
        parentColumn = "id",
        entityColumn = "word_id"
    )
    val phonetics: List<Phonetic>,

    @Relation(
        parentColumn = "id",
        entityColumn = "word_id"
    )
    val examples: List<Example>

)

private const val FIRST_HOURS_INTERVAL: Int = 4
private const val INTERVAL_MULTIPLIER: Double = 2.0

fun calculateNextRepeatAt(
    amountRepetition: Int,
    initialRepetitionTime: LocalDateTime = LocalDateTime.now()
): LocalDateTime {
    val hoursIntervalToRepeat =
        if (amountRepetition == 0)
            FIRST_HOURS_INTERVAL
        else
            FIRST_HOURS_INTERVAL * INTERVAL_MULTIPLIER.pow(amountRepetition)
    return initialRepetitionTime.plusHours(hoursIntervalToRepeat.toLong())
}
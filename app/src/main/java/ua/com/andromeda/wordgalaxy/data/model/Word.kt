package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ua.com.andromeda.wordgalaxy.exception.WordNotMemorized
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
    val memorizedAt: LocalDateTime? = null,

    @ColumnInfo("repeated_at")
    val repeatedAt: LocalDateTime? = null,

    @ColumnInfo("next_repeat_at")
    val nextRepeatAt: LocalDateTime? = null,
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

fun Word.memorize(): Word {
    val newAmountRepetition = 0
    return copy(
        memorizedAt = LocalDateTime.now(),
        amountRepetition = newAmountRepetition,
        status = WordStatus.Memorized,
        nextRepeatAt = calculateNextRepeatAt(newAmountRepetition)
    )
}

fun Word.repeat(): Word {
    val amountRepetition = amountRepetition ?: throw WordNotMemorized(value)
    return copy(
        amountRepetition = amountRepetition + 1,
        repeatedAt = LocalDateTime.now(),
        nextRepeatAt = calculateNextRepeatAt(amountRepetition)
    )
}

fun calculateNextRepeatAt(amountRepetition: Int): LocalDateTime {
    val initialRepetitionTime = LocalDateTime.now()
    val hoursIntervalToRepeat =
        if (amountRepetition == 0)
            FIRST_HOURS_INTERVAL
        else
            FIRST_HOURS_INTERVAL * INTERVAL_MULTIPLIER.pow(amountRepetition)
    // TODO: For developing purpose, change to plusHours
    return initialRepetitionTime.plusMinutes(hoursIntervalToRepeat.toLong())
}
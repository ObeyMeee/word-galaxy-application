package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ua.com.andromeda.wordgalaxy.exception.WordNotMemorized
import java.time.LocalDateTime
import kotlin.math.pow

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val value: String,
    val translation: String,
    val status: WordStatus = WordStatus.New,

    @ColumnInfo("amount_repetition")
    val amountRepetition: Int? = null,

    @ColumnInfo("status_changed_at")
    val statusChangedAt: LocalDateTime? = null,

    @ColumnInfo("repeated_at")
    val repeatedAt: LocalDateTime? = null,

    @ColumnInfo("next_repeat_at")
    val nextRepeatAt: LocalDateTime? = null,
)

val Word.isNew: Boolean
    get() = this.status == WordStatus.New


private const val FIRST_HOURS_INTERVAL: Int = 12
private const val INTERVAL_MULTIPLIER: Double = 2.0
private const val MASTERED_BOUND = 8

fun Word.memorize(): Word {
    val newAmountRepetition = 0
    return copy(
        statusChangedAt = LocalDateTime.now(),
        amountRepetition = newAmountRepetition,
        status = WordStatus.Memorized,
        nextRepeatAt = calculateNextRepeatAt(newAmountRepetition)
    )
}

fun Word.repeat(): Word {
    val amountRepetition = amountRepetition ?: throw WordNotMemorized(value)
    val now = LocalDateTime.now()
    val newAmountRepetition = amountRepetition + 1
    if (amountRepetition == MASTERED_BOUND) {
        return copy(
            amountRepetition = newAmountRepetition,
            repeatedAt = now,
            nextRepeatAt = null,
            status = WordStatus.Mastered,
            statusChangedAt = now,
        )
    }
    return copy(
        amountRepetition = newAmountRepetition,
        repeatedAt = now,
        nextRepeatAt = calculateNextRepeatAt(amountRepetition)
    )
}

fun Word.reset(): Word {
    return copy(
        status = WordStatus.New,
        amountRepetition = 0,
        statusChangedAt = null,
        repeatedAt = null,
        nextRepeatAt = null
    )
}

fun Word.updateStatus(status: WordStatus): Word {
    return copy(
        status = status,
        statusChangedAt = LocalDateTime.now()
    )
}

fun calculateNextRepeatAt(amountRepetition: Int): LocalDateTime {
    val initialRepetitionTime = LocalDateTime.now()
    val hoursIntervalToRepeat =
        if (amountRepetition == 0)
            FIRST_HOURS_INTERVAL
        else
            FIRST_HOURS_INTERVAL * INTERVAL_MULTIPLIER.pow(amountRepetition)
    return initialRepetitionTime.plusHours(hoursIntervalToRepeat.toLong())
}
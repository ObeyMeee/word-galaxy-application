package ua.com.andromeda.wordgalaxy.utils

import java.time.LocalDateTime
import java.time.temporal.TemporalUnit


fun getLastNDates(amountDates: Int, unit: TemporalUnit): List<LocalDateTime> {
    if (!unit.isDateBased) throw IllegalArgumentException("Unit must be date based")

    val today = LocalDateTime.now()
    return (amountDates - 1 downTo 0).map { days ->
        today.minus(days.toLong(), unit)
    }
}
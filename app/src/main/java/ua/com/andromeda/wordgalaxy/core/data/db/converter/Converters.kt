package ua.com.andromeda.wordgalaxy.core.data.db.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.Temporal

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Temporal?): String? {
        return date?.toString()
    }
}
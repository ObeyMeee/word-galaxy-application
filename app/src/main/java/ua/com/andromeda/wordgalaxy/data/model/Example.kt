package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Example (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val translation: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)

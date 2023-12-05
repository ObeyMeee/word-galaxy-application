package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Phonetic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val audio: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)
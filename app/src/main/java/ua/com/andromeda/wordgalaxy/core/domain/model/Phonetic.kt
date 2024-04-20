package ua.com.andromeda.wordgalaxy.core.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

typealias AudioUrl = String

@Entity(
    tableName = "phonetics",
    indices = [
        Index(value = ["word_id"])
    ],

    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Phonetic(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val audio: AudioUrl,

    @ColumnInfo(name = "word_id")
    val wordId: Long = 0
)
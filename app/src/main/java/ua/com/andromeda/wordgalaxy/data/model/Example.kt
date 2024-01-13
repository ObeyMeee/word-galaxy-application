package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "examples",
    indices = [
        Index(value = ["word_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onUpdate = CASCADE
        )
    ]
)
data class Example(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val translation: String,

    @ColumnInfo(name = "word_id")
    val wordId: Long
)

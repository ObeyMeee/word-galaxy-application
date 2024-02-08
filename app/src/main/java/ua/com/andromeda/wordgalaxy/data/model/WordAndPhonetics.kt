package ua.com.andromeda.wordgalaxy.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class WordAndPhonetics(
    @Embedded
    val word: Word,

    @Relation(
        parentColumn = "id",
        entityColumn = "word_id"
    )
    val phonetics: List<Phonetic>
)
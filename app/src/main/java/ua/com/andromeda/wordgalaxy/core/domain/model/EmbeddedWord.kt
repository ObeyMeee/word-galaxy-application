package ua.com.andromeda.wordgalaxy.core.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EmbeddedWord(
    @Embedded
    val word: Word,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",

        associateBy = Junction(
            value = WordAndCategoryCrossRef::class,
            parentColumn = "word_id",
            entityColumn = "category_id"
        )
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

fun EmbeddedWord.toWordWithCategories() = WordWithCategories(word, categories)
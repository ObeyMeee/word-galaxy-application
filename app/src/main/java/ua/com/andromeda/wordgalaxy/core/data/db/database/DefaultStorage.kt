package ua.com.andromeda.wordgalaxy.core.data.db.database

import ua.com.andromeda.wordgalaxy.core.domain.model.Category
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.Example
import ua.com.andromeda.wordgalaxy.core.domain.model.Phonetic
import ua.com.andromeda.wordgalaxy.core.domain.model.Word
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus

object DefaultStorage {
    val embeddedWord = EmbeddedWord(
        word = Word(
            value = "table",
            translation = "стіл",
            status = WordStatus.Memorized,
            amountRepetition = 0,
        ),
        categories = listOf(Category(name = "A1")),
        phonetics = listOf(Phonetic(text = "[ˈteɪbl̩]", audio = "", wordId = 0)),
        examples = listOf(
            Example(
                text = "Corner table for the student can profitably save space in the apartment.",
                translation = "Кутовий стіл для школяра може вигідно зекономити місце в квартирі.",
                wordId = 0
            )
        )
    )

    val embeddedWords = listOf(
        EmbeddedWord(
            word = Word(
                value = "table",
                translation = "стіл",
                status = WordStatus.New,
                amountRepetition = 0
            ),
            categories = listOf(),
            phonetics = listOf(),
            examples = listOf()
        ),
        EmbeddedWord(
            word = Word(
                value = "ability",
                translation = "зді́бність , спромо́жність(на́вички)",
                status = WordStatus.Memorized,
                amountRepetition = 0
            ),
            categories = listOf(),
            phonetics = listOf(),
            examples = listOf()
        ),
        EmbeddedWord(
            word = Word(
                value = "actual",
                translation = "реа́льний, ді́йсний",
                status = WordStatus.AlreadyKnown,
                amountRepetition = 0
            ),
            categories = listOf(),
            phonetics = listOf(),
            examples = listOf()
        )
    )
}
package ua.com.andromeda.wordgalaxy.data

import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordAndPhonetics
import ua.com.andromeda.wordgalaxy.data.model.WordStatus

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

    val wordAndPhoneticsList = listOf(
        WordAndPhonetics(
            word = Word(
                value = "table",
                translation = "стіл",
                status = WordStatus.New,
                amountRepetition = 0
            ),
            phonetics = listOf()
        ),
        WordAndPhonetics(
            word = Word(
                value = "ability",
                translation = "зді́бність , спромо́жність(на́вички)",
                status = WordStatus.Memorized,
                amountRepetition = 0
            ),
            phonetics = listOf()
        ),
        WordAndPhonetics(
            word = Word(
                value = "actual",
                translation = "реа́льний, ді́йсний",
                status = WordStatus.AlreadyKnown,
                amountRepetition = 0
            ),
            phonetics = listOf()
        )
    )
}
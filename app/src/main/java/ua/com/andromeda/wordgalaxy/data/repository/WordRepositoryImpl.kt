package ua.com.andromeda.wordgalaxy.data.repository

import androidx.room.Transaction
import kotlinx.coroutines.flow.map
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.WordStatus.AlreadyKnown
import ua.com.andromeda.wordgalaxy.data.model.WordStatus.InProgress
import ua.com.andromeda.wordgalaxy.data.model.WordStatus.Mastered
import ua.com.andromeda.wordgalaxy.data.model.WordStatus.Memorized
import ua.com.andromeda.wordgalaxy.data.model.WordStatus.New
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.utils.getLastNDates
import java.time.temporal.TemporalUnit

private const val TAG = "WordRepositoryImpl"

class WordRepositoryImpl(
    private val wordDao: WordDao
) : WordRepository {
    override fun findOneRandomWordWhereStatusEquals(status: WordStatus) =
        wordDao.findOneRandomWordWhereStatusEquals(status)
            .map { it.first() }

    override fun findWordToReview() =
        wordDao.findRandomWordToReview().map { it.firstOrNull() }

    override fun countLearnedWordsToday() =
        wordDao.countMemorizedWordsToday()

    override fun countWordsWhereStatusEquals(status: WordStatus) =
        wordDao.countWordsWhereStatusEquals(status)

    override fun countWordsToReview() =
        wordDao.countWordsToReview()

    override fun countReviewedWordsToday() =
        wordDao.countReviewedWordsToday()

    override fun countWordsByStatusLast(
        value: Int,
        unit: TemporalUnit
    ): List<Map<WordStatus, Int>> {
        val lastNDates = getLastNDates(value, unit)
        val words = wordDao.findAllWhereStatusNotIn(listOf(New, InProgress))

        return lastNDates.map { dateTime ->
            val localDate = dateTime.toLocalDate()
            val countByWordStatus = linkedMapOf(
                AlreadyKnown to 0,
                InProgress to 0,
                Memorized to 0,
                Mastered to 0
            )

            words.forEach { word ->
                val statusChangeAt = word.statusChangedAt!!.toLocalDate()

                when (val status = word.status) {
                    AlreadyKnown, Mastered -> {
                        countByWordStatus.incrementCount(status) {
                            statusChangeAt.isEqual(localDate)
                        }
                    }

                    Memorized -> {
                        countByWordStatus.incrementCount(InProgress) {
                            statusChangeAt.isEqual(localDate)
                        }
                        countByWordStatus.incrementCount(Memorized) {
                            val repeatedAt = word.repeatedAt?.toLocalDate()
                            repeatedAt?.isEqual(localDate) == true && word.amountRepetition!! > 0
                        }
                    }

                    New, InProgress -> {
                        // No action for New and InProgress
                    }
                }
            }
            countByWordStatus
        }
    }

    override suspend fun update(word: Word) =
        wordDao.updateWord(word)

    @Transaction
    override suspend fun insert(embeddedWord: EmbeddedWord) {
        val wordId = wordDao.insertWordWithCategories(embeddedWord.toWordWithCategories())
        insertSecondaryEntities(embeddedWord, wordId)
    }

    override suspend fun remove(embeddedWord: EmbeddedWord) =
        wordDao.remove(embeddedWord)

    private suspend fun insertSecondaryEntities(embeddedWord: EmbeddedWord, wordId: Long) {
        insertExamples(embeddedWord.examples, wordId)
        insertPhonetics(embeddedWord.phonetics, wordId)
    }

    private suspend fun insertPhonetics(phonetics: List<Phonetic>, wordId: Long) {
        val updatedPhonetics = phonetics.map { phonetic ->
            phonetic.copy(wordId = wordId)
        }
        wordDao.insertPhonetics(updatedPhonetics)
    }

    private suspend fun insertExamples(examples: List<Example>, wordId: Long) {
        val updatedExamples = examples.map { example ->
            example.copy(wordId = wordId)
        }
        wordDao.insertExamples(updatedExamples)
    }

    @Transaction
    override suspend fun insertAll(embeddedWords: List<EmbeddedWord>) {
        val wordsWordWithCategories = embeddedWords.map(EmbeddedWord::toWordWithCategories)
        val wordIds = wordDao.insertAllWords(wordsWordWithCategories)
        embeddedWords.forEachIndexed { i, embeddedWord ->
            insertSecondaryEntities(embeddedWord, wordIds[i])
        }
    }

    override suspend fun updateWordWithCategories(wordWithCategories: WordWithCategories) =
        wordDao.updateWordWithCategories(wordWithCategories)

}

private fun LinkedHashMap<WordStatus, Int>.incrementCount(
    status: WordStatus,
    predicate: () -> Boolean
) {
    if (predicate()) {
        this[status] = getOrDefault(status, 0) + 1
    }
}
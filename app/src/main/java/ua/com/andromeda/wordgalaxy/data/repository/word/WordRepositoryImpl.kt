package ua.com.andromeda.wordgalaxy.data.repository.word

import androidx.room.Transaction
import kotlinx.coroutines.flow.map
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
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
        val words = wordDao.findAllWhereStatusNotIn(listOf(WordStatus.New, WordStatus.InProgress))

        return lastNDates.map { dateTime ->
            val localDate = dateTime.toLocalDate()
            val countByWordStatus = linkedMapOf(
                WordStatus.AlreadyKnown to 0,
                WordStatus.InProgress to 0,
                WordStatus.Memorized to 0,
                WordStatus.Mastered to 0
            )

            words.forEach { word ->
                val statusChangeAt = word.statusChangedAt!!.toLocalDate()

                when (val status = word.status) {
                    WordStatus.AlreadyKnown, WordStatus.Mastered -> {
                        countByWordStatus.incrementCount(status) {
                            statusChangeAt.isEqual(localDate)
                        }
                    }

                    WordStatus.Memorized -> {
                        countByWordStatus.incrementCount(WordStatus.InProgress) {
                            statusChangeAt.isEqual(localDate)
                        }
                        countByWordStatus.incrementCount(WordStatus.Memorized) {
                            val repeatedAt = word.repeatedAt?.toLocalDate()
                            repeatedAt?.isEqual(localDate) == true && word.amountRepetition!! > 0
                        }
                    }

                    WordStatus.New, WordStatus.InProgress -> {
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
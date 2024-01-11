package ua.com.andromeda.wordgalaxy.data.repository

import androidx.room.Transaction
import kotlinx.coroutines.flow.map
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.utils.getLastNDates
import java.time.temporal.TemporalUnit


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
        return lastNDates.map { datetime ->
            val countWordsByStatus = wordDao.countWordsByStatusAt(datetime.toLocalDate())
            addMissingStatusesExceptNew(countWordsByStatus)
        }
    }

    override suspend fun update(word: Word) =
        wordDao.updateWord(word)

    @Transaction
    override suspend fun insert(embeddedWord: EmbeddedWord) {
        val wordId = wordDao.insertWord(embeddedWord.word)
        insertSecondaryEntities(embeddedWord, wordId)
    }

    private suspend fun insertSecondaryEntities(embeddedWord: EmbeddedWord, wordId: Long) {
        insertCategories(embeddedWord.categories, wordId)
        insertExamples(embeddedWord.examples, wordId)
        insertPhonetics(embeddedWord.phonetics, wordId)
    }

    private suspend fun insertCategories(categories: List<Category>, wordId: Long) {
        val updatedCategories = categories.map { category ->
            category.copy(wordId = wordId)
        }
        wordDao.insertCategories(updatedCategories)
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
        val words = embeddedWords.map { it.word }
        val wordIds = wordDao.insertAllWords(words)
        embeddedWords.forEachIndexed { i, embeddedWord ->
            insertSecondaryEntities(embeddedWord, wordIds[i])
        }
    }
}

private fun addMissingStatusesExceptNew(
    wordsCountByStatus: Map<WordStatus, Int>,
    defaultValue: Int = 0
): Map<WordStatus, Int> {
    return WordStatus
        .entries
        .filter { it != WordStatus.New }
        .associateWith { status ->
            wordsCountByStatus.getOrDefault(status, defaultValue)
        }
}

package ua.com.andromeda.wordgalaxy.data.repository.word

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories
import ua.com.andromeda.wordgalaxy.data.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.ui.common.wordform.ExistingWord
import ua.com.andromeda.wordgalaxy.utils.getLastNDates
import java.time.LocalDate
import java.time.temporal.TemporalUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {
    override fun findRandomWordsWhereStatusEquals(status: WordStatus, limit: Int) =
        wordDao.findRandomWordsWhereStatusEquals(status, limit)

    override fun findWordToReview() =
        wordDao.findRandomWordToReview().map { it.firstOrNull() }

    override fun findWordById(id: Long) =
        wordDao.findWordById(id)

    override fun findEmbeddedWordById(id: Long) =
        wordDao.findEmbeddedWordById(id)

    override fun findWordsByValue(value: String): Flow<List<ExistingWord>> =
        wordDao.findWordByValue(value)
            .map {
                it.map { embeddedWord ->
                    ExistingWord(
                        translation = embeddedWord.word.translation,
                        categories = embeddedWord.categories
                    )
                }
            }

    override fun findWordsByValueOrTranslation(searchQuery: String) =
        wordDao.findLikeValueOrTranslationIgnoreCase(searchQuery)

    override fun findWordsByCategoryId(categoryId: Long) =
        wordDao.findWordsByCategoryId(categoryId)


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
        var words: List<Word>
        runBlocking {
            words = wordDao.findAllWhereStatusNotIn(listOf(WordStatus.New, WordStatus.InProgress))
                .first()
        }
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

    private fun calculateStreak(
        getLocalDates: List<Word>.() -> List<LocalDate>,
        transform: (List<LocalDate>) -> Int
    ): Flow<Int> {
        return wordDao.findAllWhereStatusNotIn(listOf(WordStatus.New))
            .map { wordsWithoutNew ->
                val learningDates = wordsWithoutNew.getLocalDates()
                transform(learningDates)
            }
    }

    override fun countCurrentStreak(): Flow<Int> {
        return calculateStreak(
            getLocalDates = List<Word>::getDistinctLocalDates,
            transform = { learningDates ->
                generateSequence(LocalDate.now()) { it.minusDays(1) }
                    .takeWhile { it in learningDates }
                    .count()
            }
        )
    }

    override fun countBestStreak(): Flow<Int> {
        return calculateStreak(
            getLocalDates = {
                getDistinctLocalDates().sorted()
            },
            transform = { learningDates ->
                var currentDate = learningDates.firstOrNull() ?: return@calculateStreak 0
                var currentStreak = 0
                var longestStreak = 0
                learningDates.forEach {
                    currentStreak = if (currentDate == it) currentStreak + 1 else 1
                    currentDate = it.plusDays(1)
                    if (currentStreak > longestStreak) {
                        longestStreak = currentStreak
                    }
                }
                longestStreak
            }
        )
    }

    override suspend fun update(vararg words: Word) =
        wordDao.updateWord(*words)

    override suspend fun update(embeddedWord: EmbeddedWord) =
        wordDao.updateEmbeddedWord(embeddedWord)


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

private fun List<Word>.getDistinctLocalDates(): List<LocalDate> =
    this.flatMap { listOf(it.statusChangedAt?.toLocalDate(), it.repeatedAt?.toLocalDate()) }
        .filterNotNull()
        .distinct()


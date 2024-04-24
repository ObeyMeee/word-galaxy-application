package ua.com.andromeda.wordgalaxy.core.data.repository.word

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ua.com.andromeda.wordgalaxy.core.data.db.dao.WordDao
import ua.com.andromeda.wordgalaxy.core.domain.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.core.domain.model.Example
import ua.com.andromeda.wordgalaxy.core.domain.model.Phonetic
import ua.com.andromeda.wordgalaxy.core.domain.model.Word
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.core.domain.model.WordWithCategories
import ua.com.andromeda.wordgalaxy.core.domain.model.toWordWithCategories
import ua.com.andromeda.wordgalaxy.core.presentation.ui.wordform.ExistingWord
import ua.com.andromeda.wordgalaxy.home.presentation.components.AMOUNT_X_AXIS_LABELS
import ua.com.andromeda.wordgalaxy.home.presentation.components.ChartPeriodRangeStart
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao
) : WordRepository {
    override fun findRandomWordsWhereStatusEquals(status: WordStatus, limit: Int) =
        wordDao.findRandomWordsWhereStatusEquals(status, limit)

    override fun findWordsToReview(limit: Int) =
        wordDao.findRandomWordToReview(limit)

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

    override fun countWordsByStatusInRange(
        range: Pair<LocalDate, LocalDate>
    ): Map<LocalDate, Map<WordStatus, Int>> {
        val words = runBlocking {
            wordDao.findAllWhereStatusNotIn(listOf(WordStatus.New, WordStatus.InProgress))
                .first()
        }

        val dates = generateDatesSequence(range, words)
        val result = linkedMapOf<LocalDate, Map<WordStatus, Int>>()
        for (i in dates.indices) {
            val countByWordStatus = linkedMapOf(
                WordStatus.AlreadyKnown to 0,
                WordStatus.InProgress to 0,
                WordStatus.Memorized to 0,
                WordStatus.Mastered to 0
            )
            val currentDate = dates[i]
            val prevDate = dates.getOrNull(i - 1)

            incrementResult(words, countByWordStatus, prevDate, currentDate)
            result[currentDate] = countByWordStatus
        }
        return result
    }

    private fun incrementResult(
        words: List<Word>,
        countByWordStatus: LinkedHashMap<WordStatus, Int>,
        prevDate: LocalDate?,
        currentDate: LocalDate
    ) {
        words.forEach { word ->
            val statusChangeAt = word.statusChangedAt!!.toLocalDate()

            when (val status = word.status) {
                WordStatus.AlreadyKnown, WordStatus.Mastered -> {
                    countByWordStatus.incrementCount(status) {
                        (prevDate == null || statusChangeAt.isAfter(prevDate)) && (statusChangeAt.isBefore(
                            currentDate
                        ) || statusChangeAt.isEqual(currentDate))
                    }
                }

                WordStatus.Memorized -> {
                    countByWordStatus.incrementCount(WordStatus.InProgress) {
                        (prevDate == null || statusChangeAt.isAfter(prevDate)) && (statusChangeAt.isBefore(
                            currentDate
                        ) || statusChangeAt.isEqual(currentDate))
                    }
                    countByWordStatus.incrementCount(WordStatus.Memorized) {
                        val repeatedAt = word.repeatedAt?.toLocalDate()
                        (prevDate == null || repeatedAt?.isAfter(prevDate) == true) && (repeatedAt?.isBefore(
                            currentDate
                        ) == true || repeatedAt?.isEqual(currentDate) == true) && word.amountRepetition!! > 0
                    }
                }

                WordStatus.New, WordStatus.InProgress -> {
                    // No action for New and InProgress
                }
            }
        }
    }

    private fun generateDatesSequence(
        range: Pair<LocalDate, LocalDate>,
        words: List<Word>,
    ): List<LocalDate> {
        val (from, to) = range
        val seed = calculateStartDate(from, words)
        val amountDaysToCount = to.toEpochDay() - seed.toEpochDay() + 1
        return generateSequence(seed) {
            it.plusDays(amountDaysToCount / AMOUNT_X_AXIS_LABELS)
        }.take(AMOUNT_X_AXIS_LABELS - 1)
            .toList() + LocalDate.now()
    }

    private fun calculateStartDate(
        from: LocalDate,
        words: List<Word>,
    ): LocalDate {
        return if (from == ChartPeriodRangeStart.ALL_TIME) {
            val firstStatusChangedAt = words.mapNotNull {
                it.statusChangedAt
            }
                .minOrNull()
                ?.toLocalDate()
            if (firstStatusChangedAt == null || firstStatusChangedAt.isAfter(ChartPeriodRangeStart.LAST_WEEK)) {
                ChartPeriodRangeStart.LAST_WEEK
            } else {
                firstStatusChangedAt
            }
        } else {
            from
        }
    }

    override fun countCurrentStreak(): Flow<Int> =
        calculateStreak(
            getLocalDates = List<Word>::getDistinctLocalDates,
            transform = ::calculateCurrentStreak
        )

    override fun countBestStreak(): Flow<Int> =
        calculateStreak(
            getLocalDates = { getDistinctLocalDates().sorted() },
            transform = ::calculateBestStreak
        )

    private fun calculateStreak(
        getLocalDates: List<Word>.() -> List<LocalDate>,
        transform: (List<LocalDate>) -> Int
    ): Flow<Int> =
        wordDao.findAllWhereStatusNotIn(listOf(WordStatus.New))
            .map { wordsWithoutNew ->
                val learningDates = wordsWithoutNew.getLocalDates()
                transform(learningDates)
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

private fun MutableMap<WordStatus, Int>.incrementCount(
    status: WordStatus,
    predicate: () -> Boolean
) {
    if (predicate()) {
        this[status] = getOrDefault(status, 0) + 1
    }
}

private fun List<Word>.getDistinctLocalDates(): List<LocalDate> =
    flatMap { listOf(it.statusChangedAt?.toLocalDate(), it.repeatedAt?.toLocalDate()) }
        .filterNotNull()
        .distinct()

private fun calculateCurrentStreak(learningDates: List<LocalDate>): Int {
    val today = LocalDate.now()
    val seed = if (today in learningDates) today else today.minusDays(1)

    return generateSequence(seed) { it.minusDays(1) }
        .takeWhile { it in learningDates }
        .count()
}

private fun calculateBestStreak(learningDates: List<LocalDate>): Int {
    if (learningDates.isEmpty()) return 0
    var currentDate = learningDates.first()
    var currentStreak = 0
    var longestStreak = 0
    learningDates.forEach {
        currentStreak = if (currentDate == it) currentStreak + 1 else 1
        currentDate = it.plusDays(1)
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak
        }
    }
    return longestStreak
}

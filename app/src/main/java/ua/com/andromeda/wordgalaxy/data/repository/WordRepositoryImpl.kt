package ua.com.andromeda.wordgalaxy.data.repository

import androidx.room.Transaction
import kotlinx.coroutines.flow.map
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus


class WordRepositoryImpl(
    private val wordDao: WordDao
) : WordRepository {
    override fun findOneRandomWordWhereStatusEquals(status: WordStatus) =
        wordDao.findOneRandomWordWhereStatusEquals(status)
            .map { it.first() }


    override fun countLearnedWordsToday() =
        wordDao.countLearnedWordsToday()

    override fun countWordsWhereStatusEquals(status: WordStatus) =
        wordDao.countWordsWhereStatusEquals(status)

    override fun countWordsToReview() =
        wordDao.countWordsToReview()

    override suspend fun update(word: Word) =
        wordDao.updateWord(word)

    @Transaction
    override suspend fun insert(embeddedWord: EmbeddedWord) {
        val wordId = wordDao.insertWord(embeddedWord.word)
        insertSecondaeyEntites(embeddedWord, wordId)
    }

    private suspend fun insertSecondaeyEntites(
        embeddedWord: EmbeddedWord,
        wordId: Long
    ) {

        val updatedCategories = embeddedWord.categories.map { category ->
            category.copy(wordId = wordId)
        }
        val updatedExamples = embeddedWord.examples.map { example ->
            example.copy(wordId = wordId)
        }

        val updatedPhonetics = embeddedWord.phonetics.map { phonetic ->
            phonetic.copy(wordId = wordId)
        }
        wordDao.insertCategories(updatedCategories)
        wordDao.insertExamples(updatedExamples)
        wordDao.insertPhonetics(updatedPhonetics)
    }

    @Transaction
    override suspend fun insertAll(embeddedWords: List<EmbeddedWord>) {
        val words = embeddedWords.map { it.word }
        val wordIds = wordDao.insertAllWords(words)
        embeddedWords.forEachIndexed { i, embeddedWord ->
            insertSecondaeyEntites(embeddedWord, wordIds[i])
        }
    }
}
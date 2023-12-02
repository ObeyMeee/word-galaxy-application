package ua.com.andromeda.wordgalaxy.data.repository

import kotlinx.coroutines.flow.map
import ua.com.andromeda.wordgalaxy.data.dao.WordDao
import ua.com.andromeda.wordgalaxy.data.model.Word


class WordRepositoryImpl(
    private val wordDao: WordDao
) : WordRepository {
    override fun findOneRandomNewWord() =
        wordDao.findOneRandomNewWord()
            .map { it.first() }

    override fun countLearnedWordsToday() =
        wordDao.countLearnedWordsToday()

    override suspend fun update(word: Word) =
        wordDao.updateWord(word)
}
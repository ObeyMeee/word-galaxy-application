package ua.com.andromeda.wordgalaxy.data.repository

import ua.com.andromeda.wordgalaxy.data.dao.WordDao

class WordRepositoryImpl(
    private val wordDao: WordDao
) : WordRepository {
    override fun findOneRandomNewWord() = wordDao.findOneRandomNewWord()

}
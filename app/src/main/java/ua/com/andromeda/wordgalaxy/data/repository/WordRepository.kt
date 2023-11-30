package ua.com.andromeda.wordgalaxy.data.repository

import kotlinx.coroutines.flow.Flow
import ua.com.andromeda.wordgalaxy.data.model.WordWithCategories

interface WordRepository {
    fun findOneRandomNewWord(): Flow<List<WordWithCategories>>
}
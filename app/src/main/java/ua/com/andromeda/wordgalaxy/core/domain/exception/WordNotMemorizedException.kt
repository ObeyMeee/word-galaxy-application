package ua.com.andromeda.wordgalaxy.core.domain.exception

class WordNotMemorizedException(word: String) : RuntimeException("'$word' is not memorized")

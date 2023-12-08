package ua.com.andromeda.wordgalaxy.exception

class WordNotMemorized(word: String) : RuntimeException("'$word' is not memorized")

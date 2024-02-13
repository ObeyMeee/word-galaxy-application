package ua.com.andromeda.wordgalaxy.ui.navigation

sealed class Destination(protected val route: String, vararg params: String) {
    val fullRoute: String = if (params.isEmpty()) route else {
        route + params.joinToString(separator = "/", prefix = "/") { "{$it}" }
    }

    sealed class NoArgumentsDestination(route: String) : Destination(route) {
        operator fun invoke() = route
    }

    data object Start : NoArgumentsDestination("start") {
        data object HomeScreen : NoArgumentsDestination("home")

        data object VocabularyScreen : NoArgumentsDestination("vocabulary") {
            data object CategoriesScreen : NoArgumentsDestination("categories")
            data object NewWordScreen : NoArgumentsDestination("new_word")
            data object NewCategoryScreen : NoArgumentsDestination("new_category")
            data object CategoryDetailsScreen : Destination("categories/{id}?word={word}") {
                const val ID_KEY = "id"
                const val WORD_KEY = "word"
                operator fun invoke(id: Long, word: String? = null): String =
                    route.appendParams(
                        ID_KEY to id,
                        WORD_KEY to word
                    )
            }
        }

        data object Settings : NoArgumentsDestination("settings")
    }

    data object Study : NoArgumentsDestination("study") {
        data object LearnWordsScreen : NoArgumentsDestination("learn_words")
        data object ReviewWordsScreen : NoArgumentsDestination("review_words")
    }
}

internal fun String.appendParams(vararg params: Pair<String, Any?>): String {
    return params.fold(this) { acc, (key, value) ->
        acc.replace("{$key}", value.toString())
    }
}
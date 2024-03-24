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
            data object CategoriesScreen : NoArgumentsDestination("vocabulary/categories")
            data object NewWord : NoArgumentsDestination("vocabulary/new_word") {
                data object Screen : NoArgumentsDestination("vocabulary/word/new")
                data object ExamplesScreen : NoArgumentsDestination("vocabulary/word/new/examples")
            }

            data object NewCategoryScreen : NoArgumentsDestination("new_category")
            data object CategoryDetailsScreen : Destination("categories/{id}?word={word_id}") {
                const val ID_KEY = "id"
                const val WORD_ID_KEY = "word_id"
                operator fun invoke(id: Long, wordId: Long? = null): String =
                    route.appendParams(
                        ID_KEY to id,
                        WORD_ID_KEY to wordId
                    )
            }
        }

        data object MenuScreen : NoArgumentsDestination("menu") {
            data object SettingsScreen : NoArgumentsDestination("settings")
            data object AboutScreen : NoArgumentsDestination("about")
        }
    }

    data object Study : NoArgumentsDestination("study") {
        data object LearnWordsScreen : NoArgumentsDestination("learn_words")
        data object ReviewWordsScreen : NoArgumentsDestination("review_words")
    }

    data object ReportMistakeScreen : Destination("report_mistake/{wordId}") {
        const val WORD_ID_KEY = "wordId"
        operator fun invoke(wordId: Long): String =
            route.appendParams(WORD_ID_KEY to wordId)
    }

    data object EditWord : Destination("words/{id}/edit") {
        const val ID_KEY = "id"
        operator fun invoke(wordId: Long): String =
            route.appendParams(ID_KEY to wordId)

        data object Screen : NoArgumentsDestination("edit")
        data object ExamplesScreen : NoArgumentsDestination("examples/edit")
    }
}

internal fun String.appendParams(vararg params: Pair<String, Any?>): String {
    return params.fold(this) { acc, (key, value) ->
        acc.replace("{$key}", value.toString())
    }
}
package ua.com.andromeda.wordgalaxy.ui.navigation

sealed class Destination(val route: String) {

    operator fun invoke() = route

    data object Start : Destination("start") {
        data object HomeScreen : Destination("home")

        data object VocabularyScreen : Destination("vocabulary") {
            data object CategoriesScreen : Destination("categories")
            data object NewWordScreen : Destination("new_word")
            data object NewCategoryScreen : Destination("new_category")
            data object CategoryDetailsScreen : Destination("categories/{id}")

        }

        data object Settings : Destination("settings")
    }

    data object Study : Destination("study") {
        data object LearnWordsScreen : Destination("learn_words")
        data object ReviewWordsScreen : Destination("review_words")
    }
}
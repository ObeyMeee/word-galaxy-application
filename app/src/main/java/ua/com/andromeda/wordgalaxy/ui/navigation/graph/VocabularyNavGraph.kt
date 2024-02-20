package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.navigation.sharedViewModel
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories.VocabularyScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.NewCategoryScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.ExamplesScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordViewModel

fun NavGraphBuilder.vocabularyGraph(
    navController: NavController,
    backStackEntry: NavBackStackEntry?,
    snackbarHostState: SnackbarHostState,
    categoriesState: LazyListState,
    modifier: Modifier = Modifier
) {
    val categoriesScreenRoute = Destination.Start.VocabularyScreen.CategoriesScreen()
    val categoryDetailsScreen = Destination.Start.VocabularyScreen.CategoryDetailsScreen
    val newWordStartDestination = Destination.Start.VocabularyScreen.NewWord.Screen()
    val newWordRoute = Destination.Start.VocabularyScreen.NewWord()

    navigation(
        startDestination = categoriesScreenRoute,
        route = Destination.Start.VocabularyScreen()
    ) {
        composable(categoriesScreenRoute) {
            VocabularyScreen(
                listState = categoriesState,
                navigateTo = {
                    navController.navigate(it)
                },
                snackbarHostState = snackbarHostState,
            )
        }

        val navigateTo = { route: String -> navController.navigate(route) }
        navigation(
            startDestination = newWordStartDestination,
            route = newWordRoute
        ) {
            composable(newWordStartDestination) {
                val viewModel = it.sharedViewModel<NewWordViewModel>(navController)
                NewWordScreen(
                    navigateUp = {
                        navController.navigateUp()
                    },
                    navigateTo = navigateTo,
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            composable(Destination.Start.VocabularyScreen.NewWord.ExamplesScreen()) {
                val viewModel = it.sharedViewModel<NewWordViewModel>(navController)
                ExamplesScreen(
                    navigateUp = {
                        navController.navigateUp()
                    },
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
        }
        composable(Destination.Start.VocabularyScreen.NewCategoryScreen()) {
            NewCategoryScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                modifier = modifier
            )
        }
        composable(
            route = categoryDetailsScreen.fullRoute,
            arguments = listOf(
                navArgument(
                    categoryDetailsScreen.ID_KEY
                ) {
                    type = NavType.LongType
                },
                navArgument(categoryDetailsScreen.WORD_KEY) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val word = backStackEntry?.arguments?.getString(categoryDetailsScreen.WORD_KEY)
            CategoryDetailsScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                firstShownWord = word,
                snackbarHostState = snackbarHostState,
                navigateTo = navigateTo,
                modifier = modifier
            )
        }
    }
}
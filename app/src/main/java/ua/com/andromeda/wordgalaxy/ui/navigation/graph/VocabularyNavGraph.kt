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
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories.VocabularyScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.NewCategoryScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordScreen

fun NavGraphBuilder.vocabularyGraph(
    navController: NavController,
    backStackEntry: NavBackStackEntry?,
    snackbarHostState: SnackbarHostState,
    categoriesState: LazyListState,
    modifier: Modifier = Modifier
) {
    val categoriesScreenRoute = Destination.Start.VocabularyScreen.CategoriesScreen()
    val categoryDetailsScreen = Destination.Start.VocabularyScreen.CategoryDetailsScreen
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
        composable(Destination.Start.VocabularyScreen.NewWordScreen()) {
            NewWordScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                modifier = modifier
            )
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
                modifier = modifier
            )
        }
    }
}
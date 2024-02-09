package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories.VocabularyScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.NewCategoryScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordScreen

fun NavGraphBuilder.vocabularyGraph(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    categoriesState: LazyListState,
    modifier: Modifier = Modifier
) {
    val categoriesScreenRoute = Destination.Start.VocabularyScreen.CategoriesScreen()
    navigation(
        startDestination = categoriesScreenRoute,
        route = Destination.Start.VocabularyScreen()
    ) {
        composable(categoriesScreenRoute) {
            VocabularyScreen(
                listState = categoriesState,
                navController = navController,
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
        composable(Destination.Start.VocabularyScreen.CategoryDetailsScreen()) {
            CategoryDetailsScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                modifier = modifier
            )
        }
    }
}
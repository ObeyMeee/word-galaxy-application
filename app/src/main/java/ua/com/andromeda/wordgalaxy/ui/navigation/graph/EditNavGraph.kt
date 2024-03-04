package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.navigation.sharedViewModel
import ua.com.andromeda.wordgalaxy.ui.screens.editword.EditWordScreen
import ua.com.andromeda.wordgalaxy.ui.screens.editword.EditWordViewModel
import ua.com.andromeda.wordgalaxy.ui.screens.editword.ExamplesScreen

fun NavGraphBuilder.editNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val navigateUp: () -> Unit = { navController.navigateUp() }
    navigation(
        startDestination = Destination.EditWord.Screen(),
        route = Destination.EditWord.fullRoute,
        arguments = listOf(
            navArgument(Destination.EditWord.ID_KEY) {
                type = NavType.LongType
            }
        )
    ) {
        composable(route = Destination.EditWord.Screen()) {
            val viewModel = it.sharedViewModel<EditWordViewModel>(navController)
            EditWordScreen(
                navigateTo = navController::navigate,
                navigateUp = navigateUp,
                modifier = modifier,
                viewModel = viewModel
            )
        }
        composable(route = Destination.EditWord.ExamplesScreen()) {
            val viewModel = it.sharedViewModel<EditWordViewModel>(navController)
            ExamplesScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                modifier = modifier,
                viewModel = viewModel
            )
        }
    }
}
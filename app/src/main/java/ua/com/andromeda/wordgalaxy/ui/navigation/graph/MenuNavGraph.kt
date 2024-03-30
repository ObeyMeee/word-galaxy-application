package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.start.menu.MenuScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.menu.SettingsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.menu.about.AboutScreen

fun NavGraphBuilder.menuNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    composable(
        route = Destination.Start.MenuScreen(),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left
            )
        },
    ) {
        MenuScreen(
            navigateTo = navController::navigate,
            modifier = modifier,
        )
    }
    composable(
        route = Destination.Start.MenuScreen.SettingsScreen(),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right
            )
        },
    ) {
        SettingsScreen(
            navigateUp = navController::navigateUp,
            modifier = modifier,
        )
    }
    composable(
        route = Destination.Start.MenuScreen.AboutScreen(),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right
            )
        },
    ) {
        AboutScreen(navigateUp = navController::navigateUp)
    }

}
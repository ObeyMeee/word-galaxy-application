package ua.com.andromeda.wordgalaxy.core.presentation.navigation.graph

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.INITIAL_ALPHA
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.TARGET_ALPHA
import ua.com.andromeda.wordgalaxy.menu.presentation.about.AboutScreen
import ua.com.andromeda.wordgalaxy.menu.presentation.root.MenuScreen
import ua.com.andromeda.wordgalaxy.menu.presentation.settings.SettingsScreen

private val enterTransition: EnterTransition = slideInHorizontally(
    animationSpec = tween(ANIMATION_DURATION_MILLIS),
    initialOffsetX = { it / 2 },
) + fadeIn(tween(ANIMATION_DURATION_MILLIS), INITIAL_ALPHA)

private val exitTransition: ExitTransition = slideOutHorizontally(
    animationSpec = tween(ANIMATION_DURATION_MILLIS),
    targetOffsetX = { it },
) + fadeOut(tween(ANIMATION_DURATION_MILLIS), TARGET_ALPHA)

fun NavGraphBuilder.menuNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    composable(
        route = Destination.Start.MenuScreen(),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
    ) {
        MenuScreen(
            navigateTo = navController::navigate,
            modifier = modifier,
        )
    }
    composable(
        route = Destination.Start.MenuScreen.SettingsScreen(),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
    ) {
        SettingsScreen(
            navigateUp = navController::navigateUp,
            modifier = modifier,
        )
    }
    composable(
        route = Destination.Start.MenuScreen.AboutScreen(),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
    ) {
        AboutScreen(navigateUp = navController::navigateUp)
    }
}
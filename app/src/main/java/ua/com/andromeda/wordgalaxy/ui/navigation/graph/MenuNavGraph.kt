package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.start.menu.MenuScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.menu.SettingsScreen

fun NavGraphBuilder.menuNavGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    navigation(
        startDestination = Destination.Start.Menu(),
        route = Destination.Start.Menu.MainScreen()
    ) {
        composable(Destination.Start.Menu()) {
            MenuScreen(
                navigateTo = navController::navigate,
                modifier = modifier,
            )
        }
        composable(Destination.Start.Menu.SettingsScreen()) {
            SettingsScreen(modifier = modifier)
        }
    }
}
package ua.com.andromeda.wordgalaxy.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.screens.home.HomeScreen

@Composable
fun WordGalaxyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HomeScreen()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(startDestination) {
            HomeScreen(modifier.padding(dimensionResource(R.dimen.padding_small)))
        }
    }
}
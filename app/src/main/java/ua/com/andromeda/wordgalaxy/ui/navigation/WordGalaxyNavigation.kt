package ua.com.andromeda.wordgalaxy.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.screens.browsecards.BrowseCardsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.home.HomeScreen
import ua.com.andromeda.wordgalaxy.ui.screens.home.HomeViewModel

@Composable
fun WordGalaxyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HomeScreen()
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory)
    val homeUiState by homeViewModel.uiState.collectAsState()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(startDestination) {
            HomeScreen(
                homeUiState = homeUiState,
                modifier = modifier.padding(dimensionResource(R.dimen.padding_small)),
                navController = navController
            )
        }

        composable(Destination.BrowseCardsScreen()) {
            BrowseCardsScreen(
                modifier = modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    }
}
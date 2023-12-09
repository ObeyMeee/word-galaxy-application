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
import ua.com.andromeda.wordgalaxy.ui.screens.home.HomeScreen
import ua.com.andromeda.wordgalaxy.ui.screens.home.HomeViewModel
import ua.com.andromeda.wordgalaxy.ui.screens.learnwords.LearnWordsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.reviewwords.ReviewWordsScreen

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

        composable(Destination.LearnWordsScreen()) {
            LearnWordsScreen(
                navigateToNextCard = {
                    navController.navigate(Destination.LearnWordsScreen())
                },
                modifier = modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }

        composable(Destination.ReviewWordsScreen()) {
            ReviewWordsScreen(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                navigateUp = { navController.navigateUp() }
            )
        }
    }
}
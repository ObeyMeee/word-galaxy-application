package ua.com.andromeda.wordgalaxy.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.HomeScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.HomeViewModel
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.VocabularyScreen
import ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords.LearnWordsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords.ReviewWordsScreen

@Composable
fun WordGalaxyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startScreenRoute = Destination.Start.HomeScreen()
    val start = Destination.Start()

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory)
    val homeUiState by homeViewModel.uiState.collectAsState()

    val modifierWithSmallPadding = modifier.padding(dimensionResource(R.dimen.padding_small))
    NavHost(navController = navController, startDestination = start) {
        navigation(
            startDestination = startScreenRoute,
            route = start
        ) {
            composable(startScreenRoute) {
                HomeScreen(
                    homeUiState = homeUiState,
                    modifier = modifierWithSmallPadding,
                    navController = navController
                )
            }

            composable(Destination.Start.VocabularyScreen()) {
                VocabularyScreen(
                    modifier = modifierWithSmallPadding,
                    navController = navController
                )
            }

            composable(Destination.Start.Settings()) {
                Message(message = "SETTINGS", backgroundColor = MaterialTheme.colorScheme.primary) {
                    Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                }
            }
        }

        navigation(
            startDestination = Destination.Study.LearnWordsScreen(),
            route = Destination.Study()
        ) {
            composable(Destination.Study.LearnWordsScreen()) {
                LearnWordsScreen(
                    navController = navController,
                    modifier = modifierWithSmallPadding
                )
            }

            composable(Destination.Study.ReviewWordsScreen()) {
                ReviewWordsScreen(
                    navController = navController,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController) : T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
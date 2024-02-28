package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords.LearnWordsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords.ReviewWordsScreen

fun NavGraphBuilder.studyNavGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    navigation(
        startDestination = Destination.Study.LearnWordsScreen(),
        route = Destination.Study()
    ) {
        composable(Destination.Study.LearnWordsScreen()) {
            LearnWordsScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                modifier = modifier
            )
        }

        composable(Destination.Study.ReviewWordsScreen()) {
            ReviewWordsScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                modifier = modifier
            )
        }
    }
}
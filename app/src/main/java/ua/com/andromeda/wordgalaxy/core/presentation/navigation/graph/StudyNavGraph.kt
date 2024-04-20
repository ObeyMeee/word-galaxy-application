package ua.com.andromeda.wordgalaxy.core.presentation.navigation.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination
import ua.com.andromeda.wordgalaxy.study.learnwords.presentation.LearnWordsScreen
import ua.com.andromeda.wordgalaxy.study.reviewwords.presentation.ReviewWordsScreen

fun NavGraphBuilder.studyNavGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    navigation(
        startDestination = Destination.Study.LearnWordsScreen(),
        route = Destination.Study(),
        enterTransition = {
            slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left)
        },
        exitTransition = {
            slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right)
        }
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
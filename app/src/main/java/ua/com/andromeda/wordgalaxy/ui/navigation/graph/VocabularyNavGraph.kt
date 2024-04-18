package ua.com.andromeda.wordgalaxy.ui.navigation.graph

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import ua.com.andromeda.wordgalaxy.ui.navigation.ANIMATION_DURATION_MILLIS
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.navigation.sharedViewModel
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories.VocabularyScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categorydetails.CategoryDetailsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newcategory.NewCategoryScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.ExamplesScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordViewModel

fun NavGraphBuilder.vocabularyGraph(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    categoriesState: LazyListState,
    modifier: Modifier = Modifier
) {
    val categoriesScreenRoute = Destination.Start.VocabularyScreen.CategoriesScreen()
    val categoryDetailsScreen = Destination.Start.VocabularyScreen.CategoryDetailsScreen
    val newWordStartDestination = Destination.Start.VocabularyScreen.NewWord.Screen()
    val newWordRoute = Destination.Start.VocabularyScreen.NewWord()
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val previousRoute = navController.previousBackStackEntry?.destination?.route

    val navigateTo = { route: String -> navController.navigate(route) }
    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }

    navigation(
        startDestination = categoriesScreenRoute,
        route = Destination.Start.VocabularyScreen(),
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(ANIMATION_DURATION_MILLIS),
                initialOffsetX = {
                    if (previousRoute == Destination.Start.HomeScreen()) it / 2 else -it / 2
                }
            )
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(ANIMATION_DURATION_MILLIS),
                targetOffsetX = {
                    if (currentRoute == Destination.Start.HomeScreen()) it else -it
                }
            )
        }
    ) {
        composable(categoriesScreenRoute) {
            VocabularyScreen(
                listState = categoriesState,
                navigateTo = navigateTo,
                snackbarHostState = snackbarHostState,
                modifier = modifier,
            )
        }

        navigation(
            startDestination = newWordStartDestination,
            route = newWordRoute
        ) {
            composable(
                route = newWordStartDestination,
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(ANIMATION_DURATION_MILLIS),
                        initialOffsetX = {
                            if (previousRoute == Destination.Start.HomeScreen()) it / 2 else -it / 2
                        }
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(ANIMATION_DURATION_MILLIS),
                        targetOffsetX = {
                            if (currentRoute == Destination.Start.HomeScreen()) it else -it
                        }
                    )
                },
            ) {
                val viewModel = it.sharedViewModel<NewWordViewModel>(navController)
                NewWordScreen(
                    navigateUp = navigateUp,
                    navigateTo = navigateTo,
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            composable(
                route = Destination.Start.VocabularyScreen.NewWord.ExamplesScreen(),
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(ANIMATION_DURATION_MILLIS),
                        initialOffsetX = {
                            if (previousRoute == Destination.Start.HomeScreen()) it / 2 else -it / 2
                        }
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(ANIMATION_DURATION_MILLIS),
                        targetOffsetX = {
                            if (currentRoute == Destination.Start.HomeScreen()) it else -it
                        }
                    )
                },
            ) {
                val viewModel = it.sharedViewModel<NewWordViewModel>(navController)
                ExamplesScreen(
                    navigateUp = navigateUp,
                    navigateTo = navigateTo,
                    snackbarHostState = snackbarHostState,
                    viewModel = viewModel,
                    modifier = modifier,
                )
            }
        }
        composable(
            route = Destination.Start.VocabularyScreen.NewCategoryScreen(),
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(ANIMATION_DURATION_MILLIS),
                    initialOffsetX = {
                        if (previousRoute == Destination.Start.HomeScreen()) it / 2 else -it / 2
                    }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION_MILLIS),
                    targetOffsetX = {
                        if (currentRoute == Destination.Start.HomeScreen()) it else -it
                    }
                )
            },
        ) {
            NewCategoryScreen(
                navigateUp = navigateUp,
                modifier = modifier
            )
        }
        composable(
            route = categoryDetailsScreen.fullRoute,
            arguments = listOf(
                navArgument(
                    categoryDetailsScreen.ID_KEY
                ) {
                    type = NavType.LongType
                },
                navArgument(categoryDetailsScreen.WORD_ID_KEY) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    animationSpec = tween(ANIMATION_DURATION_MILLIS),
                    initialOffsetX = {
                        if (previousRoute == Destination.Start.HomeScreen()) it / 2 else -it / 2
                    }
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(ANIMATION_DURATION_MILLIS),
                    targetOffsetX = {
                        if (currentRoute == Destination.Start.HomeScreen()) it else -it
                    }
                )
            },
        ) {
            CategoryDetailsScreen(
                navigateUp = navigateUp,
                snackbarHostState = snackbarHostState,
                navigateTo = navigateTo,
                modifier = modifier
            )
        }
    }
}
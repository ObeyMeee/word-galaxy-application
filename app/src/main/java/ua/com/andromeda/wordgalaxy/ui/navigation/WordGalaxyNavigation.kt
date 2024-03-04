package ua.com.andromeda.wordgalaxy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.graph.editNavGraph
import ua.com.andromeda.wordgalaxy.ui.navigation.graph.studyNavGraph
import ua.com.andromeda.wordgalaxy.ui.navigation.graph.vocabularyGraph
import ua.com.andromeda.wordgalaxy.ui.screens.reportmistake.ReportMistakeScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.HomeScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.menu.MenuScreen

@Composable
fun WordGalaxyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startScreenRoute = Destination.Start.HomeScreen()
    val start = Destination.Start()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val categoriesState = rememberLazyListState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val navigateUp: () -> Unit = { navController.navigateUp() }
    Scaffold(
        topBar = {
            StartContent(currentRoute) {
                StartTopAppBar(
                    modifier = Modifier.padding(
                        dimensionResource(R.dimen.padding_small)
                    )
                )
            }
        },
        bottomBar = {
            StartContent(currentRoute) {
                StartBottomAppBar(navController = navController)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            AddWordFloatingActionButton(currentRoute, categoriesState, navController)
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        val modifierWithSmallPadding = modifier.padding(dimensionResource(R.dimen.padding_small))
        NavHost(
            navController = navController,
            startDestination = start,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation(
                startDestination = startScreenRoute,
                route = start
            ) {
                composable(startScreenRoute) {
                    HomeScreen(
                        modifier = modifierWithSmallPadding,
                        navController = navController
                    )
                }
                vocabularyGraph(
                    navController = navController,
                    backStackEntry = navBackStackEntry,
                    snackbarHostState = snackbarHostState,
                    categoriesState = categoriesState,
                    modifier = modifierWithSmallPadding
                )
                studyNavGraph(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    modifier = modifierWithSmallPadding
                )
                composable(Destination.Start.MenuScreen()) {
                    MenuScreen(
                        navigateTo = navController::navigate,
                        modifier = modifierWithSmallPadding,
                    )
                }

                composable(
                    route = Destination.ReportMistakeScreen.fullRoute,
                    arguments = listOf(
                        navArgument(Destination.ReportMistakeScreen.WORD_ID_KEY) {
                            type = NavType.LongType
                        }
                    )
                ) {
                    ReportMistakeScreen(
                        modifier = modifierWithSmallPadding,
                        navigateUp = navigateUp
                    )
                }
                editNavGraph(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    modifier = modifierWithSmallPadding,
                )
            }
        }
    }
}

@Composable
private fun AddWordFloatingActionButton(
    currentRoute: String?,
    categoriesState: LazyListState,
    navController: NavHostController
) {
    if (Destination.Start.VocabularyScreen.CategoriesScreen() == currentRoute) {
        val fabVisible by remember {
            derivedStateOf {
                categoriesState.firstVisibleItemIndex == 0
            }
        }

        val offsetSpringSpec = spring<IntOffset>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
        val offsetY: (Int) -> Int = { it * 4 }
        AnimatedVisibility(
            visible = fabVisible,
            enter = slideInVertically(
                animationSpec = offsetSpringSpec,
                initialOffsetY = offsetY
            ),
            exit = slideOutVertically(
                animationSpec = offsetSpringSpec,
                targetOffsetY = offsetY
            ),
        ) {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.word))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                onClick = {
                    navController.navigate(Destination.Start.VocabularyScreen.NewWord.Screen())
                }
            )
        }
    }
}

@Composable
private fun StartContent(
    currentRoute: String?,
    content: @Composable () -> Unit
) {
    val startDestinations = listOf(
        Destination.Start.HomeScreen(),
        Destination.Start.VocabularyScreen.CategoriesScreen(),
        Destination.Start.MenuScreen()
    )
    if (currentRoute in startDestinations) {
        content()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}
package ua.com.andromeda.wordgalaxy.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.HomeScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.categories.VocabularyScreen
import ua.com.andromeda.wordgalaxy.ui.screens.start.vocabulary.newword.NewWordScreen
import ua.com.andromeda.wordgalaxy.ui.screens.study.learnwords.LearnWordsScreen
import ua.com.andromeda.wordgalaxy.ui.screens.study.reviewwords.ReviewWordsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordGalaxyNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startScreenRoute = Destination.Start.HomeScreen()
    val start = Destination.Start()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val vocabularyScreenRoute = Destination.Start.VocabularyScreen()
    val newWordScreenRoute = Destination.Start.VocabularyScreen.NewWordScreen()
    val vocabularyCategoriesScreenRoute = Destination.Start.VocabularyScreen.CategoriesScreen()

    val categoriesState = rememberLazyListState()
    val fabVisible by remember {
        derivedStateOf {
            categoriesState.firstVisibleItemIndex == 0
        }
    }

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
        floatingActionButton = {
            if (vocabularyCategoriesScreenRoute == currentRoute) {
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
                            navController.navigate(newWordScreenRoute)
                        }
                    )
                }
            }
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

                navigation(
                    startDestination = vocabularyCategoriesScreenRoute,
                    route = vocabularyScreenRoute
                ) {
                    composable(vocabularyCategoriesScreenRoute) {
                        VocabularyScreen(
                            listState = categoriesState,
                            navController = navController
                        )
                    }
                    composable(newWordScreenRoute) {
                        NewWordScreen(
                            navigateUp = {
                                navController.navigateUp()
                            },
                            modifier = modifierWithSmallPadding
                        )
                    }
                    composable(Destination.Start.VocabularyScreen.NewCategoryScreen()) {
                        Message(
                            message = "New category screen",
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        }
                    }
                }

                composable(Destination.Start.Settings()) {
                    Message(
                        message = "SETTINGS",
                        backgroundColor = MaterialTheme.colorScheme.primary
                    ) {
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
}

@Composable
private fun StartContent(
    currentRoute: String?,
    content: @Composable () -> Unit
) {
    val startDestinations = listOf(
        Destination.Start.HomeScreen(),
        Destination.Start.VocabularyScreen.CategoriesScreen(),
        Destination.Start.Settings()
    )
    if (currentRoute in startDestinations) {
        content()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
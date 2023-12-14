package ua.com.andromeda.wordgalaxy.ui.screens.start.home

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

class NavigationBarItemInfo(
    val icon: Painter,
    @StringRes val labelResId: Int,
    val onClick: () -> Unit,
    val selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    Scaffold(
        topBar = { HomeScreenTopAppBar() },
        bottomBar = {
            HomeScreenBottomAppBar(
                navController = navController,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        MainScreen(
            homeUiState = homeUiState,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(120.dp)
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningTab(
    icon: Painter,
    @StringRes textRes: Int,
    @StringRes labelRes: Int,
    labelParams: Array<Int>,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .height(dimensionResource(R.dimen.icon_size_large))
                    .padding(end = dimensionResource(R.dimen.padding_small)),
                tint = iconColor
            )
            Column {
                Text(text = stringResource(textRes))
                Text(
                    text = stringResource(labelRes, *labelParams),
                    color = MaterialTheme.colorScheme.secondary,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


@Composable
fun HomeScreenBottomAppBar(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val navBarItemsInfos = listOf(
        NavigationBarItemInfo(
            icon = painterResource(R.drawable.flash_cards_icon),
            labelResId = R.string.learn,
            onClick = { navController.navigate(Destination.Start.HomeScreen()) },
            selected = true
        ),
        NavigationBarItemInfo(
            icon = rememberVectorPainter(Icons.Default.Book),
            labelResId = R.string.vocabulary,
            onClick = { navController.navigate(Destination.Start.VocabularyScreen()) },
        ),
        NavigationBarItemInfo(
            icon = rememberVectorPainter(Icons.Default.Settings),
            labelResId = R.string.settings,
            onClick = { navController.navigate(Destination.Start.Settings()) },
        )
    )
    BottomAppBar(modifier) {
        NavigationBar {
            navBarItemsInfos.forEach {
                val label = stringResource(id = it.labelResId)
                NavigationBarItem(
                    selected = it.selected,
                    onClick = it.onClick,
                    icon = {
                        Icon(
                            painter = it.icon,
                            contentDescription = label,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_default))
                        )
                    },
                    alwaysShowLabel = true,
                    label = {
                        Text(text = label)
                    }
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    homeUiState: HomeUiState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    when (homeUiState) {
        is HomeUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is HomeUiState.Error -> {
            Message(
                message = homeUiState.message,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        is HomeUiState.Success -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                LearningTab(
                    icon = painterResource(R.drawable.bulb_icon),
                    textRes = R.string.learn_new_words,
                    labelRes = R.string.learned_today,
                    iconColor = Color.Yellow,
                    labelParams = arrayOf(
                        homeUiState.learnedWordsToday,
                        homeUiState.amountWordsToLearnPerDay
                    ),
                ) {
                    navController.navigate(Destination.Study.LearnWordsScreen())
                }
                LearningTab(
                    icon = rememberVectorPainter(image = Icons.Outlined.Refresh),
                    textRes = R.string.review_words,
                    labelRes = R.string.words_to_review,
                    iconColor = Color.Green,
                    labelParams = arrayOf(homeUiState.amountWordsToReview),
                ) {
                    navController.navigate(Destination.Study.ReviewWordsScreen())
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WordGalaxyTheme {
        Surface {
            HomeScreen(
                homeUiState = HomeUiState.Success(),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenBottomAppBarPreview() {
    WordGalaxyTheme {
        Surface {
            HomeScreenBottomAppBar()
        }
    }
}
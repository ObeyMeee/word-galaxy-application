package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.ErrorMessage
import ua.com.andromeda.wordgalaxy.ui.screens.common.card.ReviewWordCard

private const val TAG = "ReviewWordsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWordsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    Scaffold(
        topBar = {
            ReviewWordsTopAppBar(
                navigateUp = { navController.navigate(Destination.HomeScreen()) },
                navigateToLearnNewWords = { navController.navigate(Destination.LearnWordsScreen()) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ReviewWordsMain(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWordsTopAppBar(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToLearnNewWords: () -> Unit = {},
) {
    TopAppBar(
        title = { TopAppNavigationBar(navigateToLearnNewWords) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@Composable
private fun TopAppNavigationBar(
    navigateToLearnNewWords: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = false,
            onClick = navigateToLearnNewWords,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bulb_icon),
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text(text = stringResource(R.string.learn_new_words)) }
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color.Green
                )
            },
            label = { Text(text = stringResource(R.string.review_words)) }
        )
    }
}

@Composable
fun ReviewWordsMain(modifier: Modifier = Modifier) {
    val viewModel: ReviewWordsViewModel = viewModel(factory = ReviewWordsViewModel.factory)
    val reviewWordsUiState by viewModel.uiState.collectAsState()

    when (val uiState = reviewWordsUiState) {
        is ReviewWordsUiState.Default -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ReviewWordsUiState.Error -> {
            ErrorMessage(message = uiState.message, modifier = Modifier.fillMaxSize())
        }

        is ReviewWordsUiState.Success -> {
            val reviewWordCard = ReviewWordCard(
                uiState = uiState,
                updateReviewMode = viewModel::updateReviewMode,
                updateInputValue = viewModel::updateUserGuess,
                revealOneLetter = viewModel::revealOneLetter,
                checkAnswer = viewModel::checkAnswer,
                onLeftClick = viewModel::repeatWord,
                onRightClick = viewModel::skipWord
            )
            Column(modifier = modifier) {
                Text(
                    text = stringResource(R.string.words_reviewed, uiState.reviewedToday),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
                LinearProgressIndicator(
                    progress = .35f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                )
                reviewWordCard.MainContent(Modifier)
            }
        }
    }
}


//@Preview
//@Composable
//fun ReviewWordsTopAppBarPreview() {
//    WordGalaxyTheme {
//        Surface {
//            ReviewWordsTopAppBar(navigateUp = { })
//        }
//    }
//}
//
//@Preview
//@Composable
//fun ReviewWordsReviewCardDefaultModePreview() {
//    WordGalaxyTheme {
//        Surface {
//            ReviewCard(
//                WordCard.Review(onRightClick = {}, onLeftClick = {}),
//                ReviewWordsUiState.Success(DefaultStorage.embeddedWord)
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun ReviewWordsReviewCardShowAnswerModePreview() {
//    WordGalaxyTheme {
//        Surface {
//            ReviewCard(
//                WordCard.Review(onRightClick = {}, onLeftClick = {}),
//                ReviewWordsUiState.Success(
//                    wordToReview = DefaultStorage.embeddedWord,
//                    reviewMode = ReviewMode.ShowAnswer
//                )
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun ReviewWordsReviewCardTypeAnswerModePreview() {
//    WordGalaxyTheme {
//        Surface {
//            ReviewCard(
//                WordCard.Review(onRightClick = {}, onLeftClick = {}),
//                ReviewWordsUiState.Success(
//                    wordToReview = DefaultStorage.embeddedWord,
//                    reviewMode = ReviewMode.TypeAnswer
//                )
//            )
//        }
//    }
//}
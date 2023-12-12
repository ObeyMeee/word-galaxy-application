package ua.com.andromeda.wordgalaxy.ui.screens.learnwords

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.ErrorMessage
import ua.com.andromeda.wordgalaxy.ui.screens.common.card.InProgressWordCard
import ua.com.andromeda.wordgalaxy.ui.screens.common.card.NewWordCard
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnWordsScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
    val viewModel: LearnWordsViewModel = viewModel(factory = LearnWordsViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    val amountWordsToReview =
        if (uiState is LearnWordsUiState.Success)
            (uiState as LearnWordsUiState.Success).amountWordsToReview
        else
            0

    Scaffold(
        topBar = {
            LearnWordsTopAppBar(
                amountWordsToReview = amountWordsToReview,
                navigateUp = { navController.navigate(Destination.HomeScreen()) },
                navigateToReviewWords = { navController.navigate(Destination.ReviewWordsScreen()) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LearnWordsMain(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun LearnWordsMain(modifier: Modifier = Modifier) {
    val viewModel: LearnWordsViewModel = viewModel(factory = LearnWordsViewModel.factory)
    val learnWordsUiState by viewModel.uiState.collectAsState()

    when (val uiState = learnWordsUiState) {
        is LearnWordsUiState.Default -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LearnWordsUiState.Error -> {
            ErrorMessage(message = uiState.message, modifier = Modifier.fillMaxSize())
        }

        is LearnWordsUiState.Success -> {
            val isWordStatusNew = uiState.embeddedWord.word.status == WordStatus.New
            val wordCard = if (isWordStatusNew) {
                NewWordCard(
                    uiState = uiState,
                    updateCardMode = viewModel::updateCardMode,
                    onLeftClick = viewModel::alreadyKnowWord,
                    onRightClick = viewModel::startLearningWord
                )
            } else {
                InProgressWordCard(
                    uiState = uiState,
                    updateInputValue = viewModel::updateUserGuess,
                    checkAnswer = viewModel::checkAnswer,
                    updateCardMode = viewModel::updateCardMode,
                    revealOneLetter = viewModel::revealOneLetter,
                    onLeftClick = viewModel::memorizeWord,
                    onRightClick = viewModel::skipWord
                )
            }
            val learnedWordsToday = uiState.learnedWordsToday
            Column(modifier = modifier) {
                Text(
                    text = stringResource(R.string.new_words_memorized, learnedWordsToday),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.padding_small))
                ) {
                    (1..learnedWordsToday).forEach { _ ->
                        Icon(imageVector = Icons.Filled.Rectangle, contentDescription = null)
                    }

                    (learnedWordsToday..<uiState.amountWordsLearnPerDay).forEach { _ ->
                        Icon(imageVector = Icons.Outlined.Rectangle, contentDescription = null)
                    }
                }
                wordCard.MainContent(modifier = Modifier)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnWordsTopAppBar(
    amountWordsToReview: Int,
    navigateUp: () -> Unit,
    navigateToReviewWords: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            TopAppNavigationBar(navigateToReviewWords, amountWordsToReview)
        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppNavigationBar(
    navigateToReviewWords: () -> Unit,
    amountWordsToReview: Int,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
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
            selected = false,
            onClick = navigateToReviewWords,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color.Green
                )
            },
            label = {
                BadgedBox(badge = {
                    Badge {
                        Text(text = amountWordsToReview.toString())
                    }
                }) {
                    Text(text = stringResource(R.string.review_words))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BrowseCardsScreenPreview() {
    WordGalaxyTheme {
        LearnWordsScreen(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun EnglishCardPreview() {
//    WordGalaxyTheme {
//        EnglishCard(modifier = Modifier.fillMaxSize())
//    }
//}
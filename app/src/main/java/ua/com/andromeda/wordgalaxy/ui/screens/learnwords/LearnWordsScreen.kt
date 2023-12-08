package ua.com.andromeda.wordgalaxy.ui.screens.learnwords

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.screens.common.BrowseCard
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardState
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun LearnWordsScreen(
    navigateToNextCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LearnWordsViewModel = viewModel(factory = LearnWordsViewModel.factory)
    val browseCardUiState by viewModel.uiState.collectAsState()

    when (val uiState = browseCardUiState) {
        is LearnWordsUiState.Default -> {
            Text(text = "Loading...")
        }

        is LearnWordsUiState.Error -> {
            Text(text = "Unexpected error occurred")
        }

        is LearnWordsUiState.Success -> {
            val isWordStatusNew = uiState.embeddedWord.word.status == WordStatus.New
            val cardState = if (isWordStatusNew) {
                CardState.NewWord(
                    onLeftClick = {
                        viewModel.updateWordStatus(WordStatus.AlreadyKnown)
                        navigateToNextCard()
                    },
                    onRightClick = {
                        viewModel.updateWordStatus(WordStatus.InProgress)
                        navigateToNextCard()
                    })
            } else {
                CardState.InProgress(
                    onLeftClick = {
                        viewModel.memorizeWord()
                        navigateToNextCard()
                    },
                    onRightClick = navigateToNextCard
                )
            }
            BrowseCard(
                cardState = cardState,
                uiState = uiState,
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrowseCardsScreenPreview() {
    WordGalaxyTheme {
        LearnWordsScreen(
            navigateToNextCard = {},
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
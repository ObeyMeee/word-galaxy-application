package ua.com.andromeda.wordgalaxy.ui.screens.browsecards

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun BrowseCardsScreen(
    navigateToNextCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: BrowseCardsViewModel = viewModel(factory = BrowseCardsViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()
    BrowseCard(
        cardState = CardState.NewWord(
            onLeftClick = {
                viewModel.updateWordStatus(WordStatus.AlreadyKnown)
                navigateToNextCard()
            },
            onRightClick = {
                viewModel.updateWordStatus(WordStatus.InProgress)
                navigateToNextCard()
            }
        ),
        uiState = uiState,
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun BrowseCardsScreenPreview() {
    WordGalaxyTheme {
        BrowseCardsScreen(
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
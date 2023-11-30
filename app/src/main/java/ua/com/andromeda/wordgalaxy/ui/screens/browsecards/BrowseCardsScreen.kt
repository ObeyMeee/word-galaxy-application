package ua.com.andromeda.wordgalaxy.ui.screens.browsecards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

@Composable
fun BrowseCardsScreen(modifier: Modifier = Modifier) {
    val viewModel: BrowseCardsViewModel = viewModel(factory = BrowseCardsViewModel.factory)
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.new_words_memorized, 12),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_small))
        ) {
            (1..5).forEach { _ ->
                Icon(imageVector = Icons.Filled.Rectangle, contentDescription = null)
            }
        }
        EnglishCard(
            viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EnglishCard(viewModel: BrowseCardsViewModel, modifier: Modifier = Modifier) {
    val browseCardUiState by viewModel.uiState.collectAsState()
    when (val uiState = browseCardUiState) {
        is BrowseCardUiState.Error -> {
            Text(text = "An error occurred while loading")
        }

        is BrowseCardUiState.Default -> {
            Text(text = "Loading...")
        }

        is BrowseCardUiState.Success -> {
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimensionResource(R.dimen.padding_small))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Square,
                            contentDescription = null,
                            modifier = Modifier.padding(
                                end = dimensionResource(R.dimen.padding_small)
                            ),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(text = stringResource(R.string.learning_new_word))
                    }
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = stringResource(R.string.show_more),
                        modifier = Modifier
                            .padding(end = dimensionResource(R.dimen.padding_small))
                            .size(32.dp)
                    )
                }
                Text(
                    text = uiState.wordWithCategories
                        .categories
                        .map(Category::name)
                        .joinToString(separator = ", "),
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = uiState.wordWithCategories.word.value,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    modifier = Modifier.fillMaxSize(.88f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Keyboard,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.RemoveRedEye,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
                CardAction(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small))
                )
            }

        }
    }
}

@Composable
private fun CardAction(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.i_have_memorized_this_word),
                modifier = Modifier.fillMaxWidth(.75f)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
            Text(
                text = stringResource(R.string.keep_learning_this_word),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrowseCardsScreenPreview() {
    WordGalaxyTheme {
        BrowseCardsScreen(
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
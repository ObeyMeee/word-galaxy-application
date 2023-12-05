package ua.com.andromeda.wordgalaxy.ui.screens.common

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Rectangle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.ui.screens.browsecards.BrowseCardUiState

@Composable
fun BrowseCard(
    cardState: CardState,
    uiState: BrowseCardUiState.Success,
    modifier: Modifier = Modifier
) {
    val newWordsMemorized = uiState.learnedWordsToday
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.new_words_memorized, newWordsMemorized),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.padding_small))
        ) {
            (1..newWordsMemorized).forEach { _ ->
                Icon(imageVector = Icons.Filled.Rectangle, contentDescription = null)
            }
            (newWordsMemorized..uiState.amountWordsLearnPerDay).forEach { _ ->
                Icon(imageVector = Icons.Outlined.Rectangle, contentDescription = null)
            }
        }
        EnglishCard(
            cardState = cardState,
            uiState = uiState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EnglishCard(
    cardState: CardState,
    uiState: BrowseCardUiState.Success,
    modifier: Modifier = Modifier
) {
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
                    tint = cardState.iconColor
                )
                Text(text = stringResource(cardState.headerLabelRes))
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
            text = uiState.embeddedWord
                .categories
                .map(Category::name)
                .joinToString(separator = ", "),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = uiState.embeddedWord.word.value,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = uiState.embeddedWord.phonetics.joinToString(separator = ", ") { it.text },
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Icon(
                imageVector = Icons.Filled.PlayCircleFilled, contentDescription = null,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_large)),
            )
        }

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
            cardState = cardState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
private fun CardAction(
    cardState: CardState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = cardState.onLeftClick) {
            Row(
                modifier = Modifier.fillMaxWidth(.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(cardState.actionLabelResLeft),
                    modifier = Modifier.fillMaxWidth(.75f)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        Button(onClick = cardState.onRightClick) {
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
                    text = stringResource(cardState.actionLabelResRight),
                )
            }
        }
    }
}
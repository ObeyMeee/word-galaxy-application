package ua.com.andromeda.wordgalaxy.ui.screens.common.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.utils.playPronunciation
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.screens.learnwords.LearnWordsUiState
import ua.com.andromeda.wordgalaxy.ui.theme.md_theme_light_primary

class NewWordCard(
    private val uiState: LearnWordsUiState.Success,
    private val updateCardMode: (CardMode) -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
) : WordCard(
    headerLabelRes = R.string.new_word,
    iconColor = md_theme_light_primary,
    actionLabelResLeft = R.string.i_already_know_this_word,
    actionLabelResRight = R.string.start_learning_this_word,
    onLeftClick = onLeftClick,
    onRightClick = onRightClick
) {
    @Composable
    override fun MainContent(modifier: Modifier) {
        val wordToLearn = uiState.embeddedWord
        val word = wordToLearn.word

        Text(
            text = wordToLearn.categories
                .map(Category::name)
                .joinToString(separator = ", "),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = word.translate,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_largest),
                bottom = dimensionResource(R.dimen.padding_medium)
            ),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge
        )

        Card(
            modifier = modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            CardHeader(
                squareColor = iconColor,
                label = stringResource(headerLabelRes),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = wordToLearn.categories
                    .map(Category::name)
                    .joinToString(separator = ", "),
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = word.value,
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.padding_largest),
                    bottom = dimensionResource(R.dimen.padding_medium)
                ),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge
            )
            CardModeContent()
            Spacer(modifier = Modifier.weight(1f))
            CardAction()
        }
    }

    @Composable
    private fun ColumnScope.CardModeContent() {
        val wordToLearn = uiState.embeddedWord
        val word = wordToLearn.word
        val phonetics = wordToLearn.phonetics
        when (uiState.cardMode) {
            CardMode.ShowAnswer -> {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                )
                WordAnswer(
                    answer = word.translate,
                    phonetics = phonetics,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_largest),
                        vertical = dimensionResource(R.dimen.padding_medium)
                    )
                )
                ExampleList(wordToLearn.examples)
            }

            CardMode.Default -> {
                Spacer(modifier = Modifier.weight(1f))
                ReviewModeSelectorRow(
                    updateReviewMode = updateCardMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> {
                throw IllegalArgumentException("Invalid card mode: ${uiState.cardMode}")
            }
        }
    }

    @Composable
    private fun WordAnswer(
        answer: String,
        phonetics: List<Phonetic>,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        Row(
            modifier = modifier.clickable {
                context.playPronunciation(audioUrls = phonetics.map { it.audio })
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = phonetics.joinToString(separator = ", ") { it.text },
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.Default.PlayCircleFilled,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    private fun ReviewModeSelectorRow(
        updateReviewMode: (CardMode) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReviewModeIconButton(Icons.Default.RemoveRedEye) {
                updateReviewMode(CardMode.ShowAnswer)
            }
        }
    }

    @Composable
    private fun ReviewModeIconButton(
        icon: ImageVector,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier
                .border(
                    BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.round_medium))
                )
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
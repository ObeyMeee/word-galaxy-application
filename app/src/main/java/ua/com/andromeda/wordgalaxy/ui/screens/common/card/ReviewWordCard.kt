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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.utils.playPronunciation
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode
import ua.com.andromeda.wordgalaxy.ui.screens.reviewwords.ReviewWordsUiState
import ua.com.andromeda.wordgalaxy.ui.theme.md_theme_light_tertiary

class ReviewWordCard(
    private val uiState: ReviewWordsUiState.Success,
    private val updateReviewMode: (CardMode) -> Unit,
    private val updateInputValue: (String) -> Unit,
    private val revealOneLetter: () -> Unit,
    private val checkAnswer: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
) : WordCard(
    headerLabelRes = R.string.memorized_word,
    iconColor = md_theme_light_tertiary,
    actionLabelResLeft = R.string.got_it,
    actionLabelResRight = R.string.forgot_it,
    onLeftClick = onLeftClick,
    onRightClick = onRightClick
) {
    @Composable
    override fun MainContent(modifier: Modifier) {
        val wordToReview = uiState.wordToReview
        val word = wordToReview.word
        val amountRepetition = word.amountRepetition ?: 0
        val numberReview = amountRepetition + 1

        Card(
            modifier = modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            ReviewCardHeader(
                squareColor = iconColor,
                label = stringResource(headerLabelRes, numberReview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = wordToReview.categories
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
            ReviewModeContent()
            Spacer(modifier = Modifier.weight(1f))
            CardAction(modifier = Modifier.fillMaxWidth())
        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun ColumnScope.ReviewModeContent() {
        val wordToReview = uiState.wordToReview
        val word = wordToReview.word
        val phonetics = wordToReview.phonetics
        val focusRequester = remember { FocusRequester() }
        when (uiState.cardMode) {
            CardMode.ShowAnswer -> {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                )
                WordAnswer(
                    answer = word.value,
                    phonetics = phonetics,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_largest),
                        vertical = dimensionResource(R.dimen.padding_medium)
                    )
                )
                ExampleList(wordToReview.examples)
            }

            CardMode.TypeAnswer -> {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_larger))
                ) {
                    TextField(
                        value = uiState.userGuess,
                        onValueChange = updateInputValue,
                        modifier = Modifier.focusRequester(focusRequester),
                        placeholder = { Text(text = stringResource(R.string.type_here)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions { checkAnswer() },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                    )

                    // autofocus the text field
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    RowWithWordControls(
                        revealOneLetter = revealOneLetter,
                        checkAnswer = checkAnswer,
                        amountAttempts = uiState.amountAttempts,
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                    )
                }
            }

            CardMode.Default -> {
                Spacer(modifier = Modifier.weight(1f))
                ReviewModeSelectorRow(
                    updateReviewMode = updateReviewMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }


    @Composable
    private fun ReviewCardHeader(
        squareColor: Color,
        label: String,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Square,
                contentDescription = null,
                modifier = Modifier.padding(
                    end = dimensionResource(R.dimen.padding_small)
                ),
                tint = squareColor
            )
            Text(text = label, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = stringResource(R.string.show_more),
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    .size(32.dp)
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
            ReviewModeIconButton(Icons.Default.Keyboard) {
                updateReviewMode(CardMode.TypeAnswer)
            }
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
}

package ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardMode
import ua.com.andromeda.wordgalaxy.utils.playPronunciation

object FlashcardScope {
    @Composable
    fun WordWithTranscription(
        value: String,
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
                    text = value,
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
    fun ExampleList(
        examples: List<Example>,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = modifier
        ) {
            items(examples, key = { it.id }) {
                ExampleItem(it)
            }
        }
    }

    @Composable
    private fun ExampleItem(
        example: Example,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expand)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
            Text(
                text = example.text,
                modifier = Modifier.weight(.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Outlined.PlayCircleOutline,
                contentDescription = stringResource(R.string.play_example)
            )
        }
    }

    @Composable
    fun CardModeSelectorRow(
        iconsToCardModes: List<Pair<ImageVector, CardMode>>,
        updateCardMode: (CardMode) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconsToCardModes.forEach { (icon, cardMode) ->
                this@FlashcardScope.CardModeIconButton(icon) {
                    updateCardMode(cardMode)
                }
            }
        }
    }

    @Composable
    private fun CardModeIconButton(
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
    fun RowWithWordControls(
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            this@FlashcardScope.RevealOneLetterOutlinedButton(onClick = revealOneLetter)
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
            this@FlashcardScope.CheckAnswerButton(
                onclick = checkAnswer,
                amountAttempts = amountAttempts,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun RevealOneLetterOutlinedButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_smaller))
        ) {
            Icon(
                imageVector = Icons.Default.QuestionMark,
                contentDescription = null
            )
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun CheckAnswerButton(
        onclick: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = onclick,
            modifier = modifier,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            BadgedBox(badge = {
                Badge {
                    Text(
                        text = amountAttempts.toString(),
                        modifier = Modifier.semantics {
                            contentDescription = "$amountAttempts amount attempts left"
                        }
                    )
                }
            }) {
                Text(
                    text = stringResource(R.string.check),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

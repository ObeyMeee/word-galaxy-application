package ua.com.andromeda.wordgalaxy.ui.screens.common.flashcard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.DefaultStorage
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord

@Composable
fun Flashcard(
    embeddedWord: EmbeddedWord,
    flashcardState: FlashcardState,
    screenHeader: @Composable () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
    modifier: Modifier = Modifier,
) {
    val word = embeddedWord.word
    val amountRepetition = word.amountRepetition ?: 0
    val numberReview = amountRepetition + 1
    Column(modifier = modifier) {
        screenHeader()
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            FlashcardHeader(
                squareColor = flashcardState.iconColor,
                label = stringResource(flashcardState.headerLabelRes, numberReview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            )
            CategoriesText(
                categories = embeddedWord.categories,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_largest))
            )
            if (flashcardState is FlashcardState.New) {
                FlashcardScope.WordWithTranscription(
                    value = word.value,
                    phonetics = embeddedWord.phonetics,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_largest),
                        vertical = dimensionResource(R.dimen.padding_medium)
                    )
                )
            } else {
                Text(
                    text = word.translate,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.padding_largest),
                        bottom = dimensionResource(R.dimen.padding_medium)
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            content()
            Spacer(modifier = Modifier.weight(1f))
            FlashcardActionRow(
                flashcardState = flashcardState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Composable
private fun CategoriesText(
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    Text(
        text = categories
            .map(Category::name)
            .joinToString(separator = ", "),
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun FlashcardHeader(
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
private fun FlashcardActionRow(flashcardState: FlashcardState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            onClick = flashcardState.onLeftClick,
            icon = Icons.Filled.KeyboardArrowLeft,
            labelRes = flashcardState.actionLabelResLeft,
            isTextBeforeIcon = true
        )
        ActionButton(
            onClick = flashcardState.onRightClick,
            icon = Icons.Filled.KeyboardArrowRight,
            labelRes = flashcardState.actionLabelResRight
        )
    }
}

@Composable
private fun RowScope.ActionButton(
    icon: ImageVector,
    @StringRes labelRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTextBeforeIcon: Boolean = false
) {
    val labelText: @Composable () -> Unit = {
        Text(
            text = stringResource(labelRes),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .height(dimensionResource(R.dimen.action_button_height))
            .weight(1f),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isTextBeforeIcon) {
                labelText()
            }
            Icon(imageVector = icon, contentDescription = null)
            if (!isTextBeforeIcon) {
                labelText()
            }
        }
    }
}


@Preview
@Composable
fun FlashcardNewPreview() {
    Surface {
        MaterialTheme {
            Flashcard(
                embeddedWord = DefaultStorage.embeddedWord,
                flashcardState = FlashcardState.New({}, {}),
                screenHeader = {},
                content = {}
            )
        }
    }
}

@Preview
@Composable
fun FlashcardInProgressPreview() {
    Surface {
        MaterialTheme {
            Flashcard(
                embeddedWord = DefaultStorage.embeddedWord,
                flashcardState = FlashcardState.InProgress({}, {}),
                screenHeader = {},
                content = {}
            )
        }
    }
}

@Preview
@Composable
fun FlashcardReviewPreview() {
    Surface {
        MaterialTheme {
            Flashcard(
                embeddedWord = DefaultStorage.embeddedWord,
                flashcardState = FlashcardState.Review({}, {}),
                screenHeader = {},
                content = {}
            )
        }
    }
}

@Preview
@Composable
fun FlashcardHeaderPreview() {
    Surface {
        MaterialTheme {
            FlashcardHeader(
                squareColor = MaterialTheme.colorScheme.primary,
                label = "Memorized word (review 1)"
            )
        }
    }
}

@Preview
@Composable
fun FlashcardActionRowPreview() {
    Surface {
        MaterialTheme {
            FlashcardActionRow(
                FlashcardState.New({}, {})
            )
        }
    }
}
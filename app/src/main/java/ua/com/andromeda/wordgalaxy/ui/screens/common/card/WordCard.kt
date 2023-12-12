package ua.com.andromeda.wordgalaxy.ui.screens.common.card

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Example

sealed class WordCard(
    @StringRes val headerLabelRes: Int,
    val iconColor: Color,
    val actionLabelResLeft: Int,
    val actionLabelResRight: Int,
    val onLeftClick: () -> Unit,
    val onRightClick: () -> Unit,
) {
    @Composable
    abstract fun MainContent(modifier: Modifier)

    @Composable
    protected fun CardHeader(
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
    protected fun ExampleList(
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
    protected fun ExampleItem(
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
    protected fun RowWithWordControls(
        revealOneLetter: () -> Unit,
        checkAnswer: () -> Unit,
        amountAttempts: Int,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            RevealOneLetterOutlinedButton(onClick = revealOneLetter)
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
            CheckAnswerButton(
                onclick = checkAnswer,
                amountAttempts = amountAttempts,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    protected fun RevealOneLetterOutlinedButton(
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
    protected fun CheckAnswerButton(
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

    @Composable
    protected fun CardAction(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                onClick = onLeftClick,
                icon = Icons.Filled.KeyboardArrowLeft,
                labelRes = actionLabelResLeft,
                isTextBeforeIcon = true
            )
            ActionButton(
                onClick = onRightClick,
                icon = Icons.Filled.KeyboardArrowRight,
                labelRes = actionLabelResRight
            )
        }
    }

    @Composable
    protected fun RowScope.ActionButton(
        icon: ImageVector,
        @StringRes labelRes: Int,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        isTextBeforeIcon: Boolean = false
    ) {
        val LabelText: @Composable () -> Unit = {
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
                    LabelText()
                }
                Icon(imageVector = icon, contentDescription = null)
                if (!isTextBeforeIcon) {
                    LabelText()
                }
            }
        }
    }
}
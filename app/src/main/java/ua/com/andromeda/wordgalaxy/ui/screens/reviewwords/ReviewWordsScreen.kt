package ua.com.andromeda.wordgalaxy.ui.screens.reviewwords

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.Category
import ua.com.andromeda.wordgalaxy.data.model.EmbeddedWord
import ua.com.andromeda.wordgalaxy.data.model.Example
import ua.com.andromeda.wordgalaxy.data.model.Phonetic
import ua.com.andromeda.wordgalaxy.data.model.Word
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.data.utils.playPronunciation
import ua.com.andromeda.wordgalaxy.ui.screens.common.CardState
import ua.com.andromeda.wordgalaxy.ui.screens.common.ReviewMode
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

private const val TAG = "ReviewWordsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWordsScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ReviewWordsTopAppBar(
                navigateUp = navigateUp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ReviewWordsMain(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWordsTopAppBar(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = false,
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
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = Color.Green
                        )
                    },
                    label = { Text(text = stringResource(R.string.review_words)) }
                )
            }
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

@Composable
fun ReviewWordsMain(modifier: Modifier = Modifier) {
    val viewModel: ReviewWordsViewModel = viewModel(factory = ReviewWordsViewModel.factory)
    val reviewWordsUiState by viewModel.uiState.collectAsState()
    val cardState = CardState.Review(
        onLeftClick = viewModel::repeatWord,
        onRightClick = viewModel::skipWord
    )

    when (val uiState = reviewWordsUiState) {
        is ReviewWordsUiState.Default -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ReviewWordsUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val shape = ShapeDefaults.Large
                Row(
                    modifier = Modifier
                        .border(
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary
                            ),
                            shape = shape
                        )
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
                    Text(text = uiState.message)
                }
            }
        }

        is ReviewWordsUiState.Success -> {
            Column(modifier = modifier) {
                Text(
                    text = stringResource(R.string.words_reviewed, uiState.reviewedToday),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
                )
                LinearProgressIndicator(
                    progress = .35f, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(R.dimen.padding_medium))
                )
                EnglishCard(
                    cardState = cardState,
                    uiState = uiState,
                    updateReviewMode = viewModel::updateReviewMode
                )
            }
        }
    }
}

@Composable
private fun EnglishCard(
    cardState: CardState,
    uiState: ReviewWordsUiState.Success,
    modifier: Modifier = Modifier,
    updateReviewMode: (ReviewMode) -> Unit = {}
) {
    val context = LocalContext.current
    val phonetics = uiState.wordToReview.phonetics
    Card(
        modifier = modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
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
                val amountRepetition = uiState.wordToReview.word.amountRepetition ?: 0
                Text(text = stringResource(cardState.headerLabelRes, amountRepetition + 1))
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
            text = uiState.wordToReview.categories.map(Category::name)
                .joinToString(separator = ", "),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = uiState.wordToReview.word.translate,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_largest)),
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.91f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    updateReviewMode(ReviewMode.TypeAnswer)
                },
                modifier = Modifier
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
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = {
                    updateReviewMode(ReviewMode.ShowAnswer)
                },
                modifier = Modifier
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
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        CardAction(
            cardState = cardState,
            modifier = Modifier.fillMaxWidth()
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
        Button(
            onClick = cardState.onLeftClick,
            modifier = Modifier
                .height(50.dp)
                .weight(1f),
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_small)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(.8f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(cardState.actionLabelResLeft))
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )

            }
        }
        Button(
            onClick = cardState.onRightClick,
            modifier = Modifier
                .height(50.dp)
                .weight(1f),
            shape = RoundedCornerShape(dimensionResource(R.dimen.round_small)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(cardState.actionLabelResRight),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ReviewWordsTopAppBarPreview() {
    WordGalaxyTheme {
        Surface {
            ReviewWordsTopAppBar(navigateUp = { })
        }
    }
}

@Preview
@Composable
fun ReviewWordsEnglishCardPreview() {
    WordGalaxyTheme {
        Surface {
            EnglishCard(
                CardState.Review(onRightClick = {}, onLeftClick = {}),
                ReviewWordsUiState.Success(
                    EmbeddedWord(
                        word = Word(
                            value = "table",
                            translate = "стіл",
                            status = WordStatus.Memorized,
                            amountRepetition = 0,
                        ),
                        categories = listOf(Category(name = "A1", wordId = 0)),
                        phonetics = listOf(Phonetic(text = "[ˈteɪbl̩]", audio = "", wordId = 0)),
                        examples = listOf(
                            Example(
                                text = "Corner table for the student can profitably save space in the apartment.",
                                wordId = 0
                            )
                        )
                    )
                )
            )
        }
    }
}
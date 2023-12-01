package ua.com.andromeda.wordgalaxy.ui.screens.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination

@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    when (val uiState = homeUiState) {
        is HomeUiState.Default -> {

        }

        is HomeUiState.Error -> {

        }

        is HomeUiState.Success -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                LearningTab(
                    icon = painterResource(R.drawable.bulb_icon),
                    textRes = R.string.learn_new_words,
                    labelRes = R.string.learned_today,
                    iconColor = Color.Yellow,
                    labelParams = arrayOf(uiState.learnedWordsToday, uiState.amountWordsLearnPerDay),
                ) {
                    navController.navigate(Destination.BrowseCardsScreen())
                }
                LearningTab(
                    icon = rememberVectorPainter(image = Icons.Outlined.Refresh),
                    textRes = R.string.review_words,
                    labelRes = R.string.words_to_review,
                    iconColor = Color.Green,
                    labelParams = arrayOf(uiState.wordsToReview),
                ) {
                    navController.navigate(Destination.BrowseCardsScreen())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningTab(
    icon: Painter,
    @StringRes textRes: Int,
    @StringRes labelRes: Int,
    labelParams: Array<Int>,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .height(40.dp)
                    .padding(end = dimensionResource(R.dimen.padding_small)),
                tint = iconColor
            )
            Column {
                Text(text = stringResource(textRes))
                Text(
                    text = stringResource(labelRes, *labelParams),
                    color = MaterialTheme.colorScheme.secondary,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
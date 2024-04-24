package ua.com.andromeda.wordgalaxy.home.presentation.components.sections

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.navigation.Destination
import ua.com.andromeda.wordgalaxy.home.presentation.Section


@Composable
fun RepetitionSection(
    learnedWordsToday: Int,
    amountWordsToLearnPerDay: Int,
    amountWordsToReview: Int,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Section(modifier) {
        Text(
            text = stringResource(R.string.repetition),
            style = MaterialTheme.typography.labelMedium
        )
        LearningTab(
            icon = painterResource(R.drawable.bulb_icon),
            textRes = R.string.learn_new_words,
            label = stringResource(
                id = R.string.learned_today,
                learnedWordsToday,
                amountWordsToLearnPerDay
            ),
            iconColor = Color.Yellow,
        ) {
            navigateTo(Destination.Study.LearnWordsScreen())
        }
        LearningTab(
            icon = rememberVectorPainter(image = Icons.Outlined.Refresh),
            textRes = R.string.review_words,
            label = stringResource(R.string.words_to_review, amountWordsToReview),
            iconColor = Color.Green,
        ) {
            navigateTo(Destination.Study.ReviewWordsScreen())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LearningTab(
    icon: Painter,
    @StringRes textRes: Int,
    label: String,
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
                    .size(dimensionResource(R.dimen.icon_size_large))
                    .padding(end = dimensionResource(R.dimen.padding_small)),
                tint = iconColor
            )
            Column {
                Text(text = stringResource(textRes))
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.secondary,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
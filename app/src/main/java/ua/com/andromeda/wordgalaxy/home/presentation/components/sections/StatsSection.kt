package ua.com.andromeda.wordgalaxy.home.presentation.components.sections

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.home.presentation.Section
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import java.time.DayOfWeek
import java.time.LocalDateTime

@Composable
fun StatsSection(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    val fillMaxWidthModifier = Modifier.fillMaxWidth()
    Section(modifier) {
        Text(
            text = stringResource(R.string.stats),
            style = MaterialTheme.typography.labelMedium
        )
        Card {
            Column(
                modifier = Modifier.padding(
                    dimensionResource(R.dimen.padding_medium)
                ),
            ) {
                DaysOfWeekRow(modifier = fillMaxWidthModifier)
                ActiveDayOfWeekArrow(modifier = fillMaxWidthModifier)
                StreakRow(
                    currentStreak = currentStreak,
                    bestStreak = bestStreak,
                    modifier = fillMaxWidthModifier
                )
            }
        }
    }
}

@Composable
private fun DaysOfWeekRow(modifier: Modifier = Modifier) {
    val dayOfWeeks = DayOfWeek.entries
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        dayOfWeeks.forEach { dayOfWeek ->
            DayOfWeekItem(dayOfWeek)
        }
    }
}

@Composable
private fun DayOfWeekItem(dayOfWeek: DayOfWeek) {
    val currentDayOfWeek = LocalDateTime.now().dayOfWeek
    Box(
        modifier = Modifier
            .border(
                border = BorderStroke(
                    width = dimensionResource(R.dimen.border_smallest),
                    color = if (currentDayOfWeek == dayOfWeek)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                ),
                shape = MaterialTheme.shapes.extraLarge
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(dimensionResource(R.dimen.padding_smaller))
            .size(dimensionResource(R.dimen.day_of_week_size_box))
    ) {
        Text(
            text = dayOfWeek.name.first().toString(),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ActiveDayOfWeekArrow(modifier: Modifier = Modifier) {
    val dayOfWeeks = DayOfWeek.entries
    val currentDayOfWeek = LocalDateTime.now().dayOfWeek
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        dayOfWeeks.forEach { dayOfWeek ->
            Box(modifier = Modifier.size(dimensionResource(R.dimen.day_of_week_size_box))) {
                if (currentDayOfWeek == dayOfWeek) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = dayOfWeek.name,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakRow(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StreakCard(
            labelResId = R.string.current_streak,
            valueResId = R.plurals.days,
            count = currentStreak
        )
        StreakCard(
            labelResId = R.string.best_streak,
            valueResId = R.plurals.days_in_a_row,
            count = bestStreak
        )
    }
}

@Composable
private fun StreakCard(
    @StringRes labelResId: Int,
    @PluralsRes valueResId: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_mediumish))) {
            Text(text = stringResource(labelResId))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = count.toString(), style = MaterialTheme.typography.titleMedium)
                HorizontalSpacer(R.dimen.padding_smallest)
                Text(
                    text = pluralStringResource(valueResId, count),
                    modifier = Modifier.offset(y = (-3).dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


@Preview
@Composable
fun StatsSectionPreview() {
    WordGalaxyTheme {
        Surface {
            StatsSection(
                currentStreak = 4,
                bestStreak = 5
            )
        }
    }
}
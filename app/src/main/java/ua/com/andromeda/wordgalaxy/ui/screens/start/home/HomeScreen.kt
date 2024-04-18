package ua.com.andromeda.wordgalaxy.ui.screens.start.home

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.common.HorizontalSpacer
import ua.com.andromeda.wordgalaxy.ui.common.Message
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.graphics.ResultBarChart
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import java.time.DayOfWeek
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val homeUiState by viewModel.uiState.collectAsState()
    when (val state = homeUiState) {
        is HomeUiState.Default -> CenteredLoadingSpinner(modifier)
        is HomeUiState.Error -> Message(state.message, modifier)

        is HomeUiState.Success -> {
            val scrollState = rememberScrollState()

            Column(
                modifier = modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                RepetitionSection(
                    learnedWordsToday = state.learnedWordsToday,
                    amountWordsToLearnPerDay = state.amountWordsToLearnPerDay,
                    amountWordsToReview = state.amountWordsToReview,
                    navigateTo = navigateTo,
                )
                StatsSection(
                    currentStreak = state.currentStreak,
                    bestStreak = state.bestStreak,
                )
                ChartSection(
                    timePeriod = state.timePeriod,
                    listOfWordsCountOfStatus = state.listOfWordsCountOfStatus,
                    showTimePeriodDialog = state.showTimePeriodDialog,
                    updateTimePeriod = viewModel::updateTimePeriod,
                    showDialog = viewModel::updateShowTimePeriodDialog,
                )
            }
        }
    }
}

@Composable
private fun RepetitionSection(
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

@Composable
private inline fun Section(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_small)
        ),
    ) {
        content()
    }
}

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

@Composable
fun ChartSection(
    timePeriod: TimePeriodChartOptions,
    showTimePeriodDialog: Boolean,
    listOfWordsCountOfStatus: List<Map<WordStatus, Int>>,
    modifier: Modifier = Modifier,
    showDialog: (Boolean) -> Unit = {},
    updateTimePeriod: (TimePeriodChartOptions) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val chartHeight = configuration.screenHeightDp / 2.5
    Section(modifier) {
        Text(
            text = stringResource(R.string.chart),
            style = MaterialTheme.typography.labelMedium
        )
        Card {
            Row(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                Text(text = stringResource(id = R.string.time_period))
                Text(
                    text = timePeriod.label,
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = dimensionResource(R.dimen.padding_smaller))
                        .clickable {
                            showDialog(true)
                        }
                )
                TimePeriodDialog(
                    currentOption = timePeriod,
                    visible = showTimePeriodDialog,
                    showDialog = showDialog,
                    onOptionSelected = updateTimePeriod
                )
            }
            ResultBarChart(
                data = listOfWordsCountOfStatus,
                days = timePeriod.days,
                modifier = Modifier
                    .height(chartHeight.dp)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun TimePeriodDialog(
    currentOption: TimePeriodChartOptions,
    visible: Boolean,
    modifier: Modifier = Modifier,
    showDialog: (Boolean) -> Unit = {},
    onOptionSelected: (TimePeriodChartOptions) -> Unit = {}
) {
    val options = TimePeriodChartOptions.entries
    if (visible) {
        AlertDialog(
            onDismissRequest = { showDialog(false) },
            confirmButton = {},
            modifier = modifier,
            title = {
                Text(
                    text = stringResource(R.string.select_time_period),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            dismissButton = {
                Button(onClick = { showDialog(false) }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            text = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        options.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOptionSelected(option)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentOption == option,
                                    onClick = {
                                        onOptionSelected(option)
                                    }
                                )
                                Text(text = option.label)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    WordGalaxyTheme {
        Surface {
            HomeScreen(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                navigateTo = {},
            )
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
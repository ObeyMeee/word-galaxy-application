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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.ui.navigation.Destination
import ua.com.andromeda.wordgalaxy.ui.screens.common.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.ui.screens.common.Message
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.getLastNDates
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale


private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
) {
    when (homeUiState) {
        is HomeUiState.Default -> {
            CenteredLoadingSpinner()
        }

        is HomeUiState.Error -> {
            Message(
                message = homeUiState.message,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        is HomeUiState.Success -> {
            val spacer = @Composable {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            }
            val scrollState = rememberScrollState()
            Column(modifier = modifier.verticalScroll(scrollState)) {
                RepetitionSection(homeUiState, navController)
                spacer()
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                StatsSection(homeUiState, modifier = Modifier.fillMaxWidth())
                spacer()
                ChartSection(homeUiState, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun RepetitionSection(
    homeUiState: HomeUiState.Success,
    navController: NavController = rememberNavController()
) {
    Text(
        text = stringResource(R.string.repetition),
        style = MaterialTheme.typography.labelMedium
    )
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))) {
        LearningTab(
            icon = painterResource(R.drawable.bulb_icon),
            textRes = R.string.learn_new_words,
            labelRes = R.string.learned_today,
            iconColor = Color.Yellow,
            labelParams = arrayOf(
                homeUiState.learnedWordsToday,
                homeUiState.amountWordsToLearnPerDay
            ),
        ) {
            navController.navigate(Destination.Study.LearnWordsScreen())
        }
        LearningTab(
            icon = rememberVectorPainter(image = Icons.Outlined.Refresh),
            textRes = R.string.review_words,
            labelRes = R.string.words_to_review,
            iconColor = Color.Green,
            labelParams = arrayOf(homeUiState.amountWordsToReview),
        ) {
            navController.navigate(Destination.Study.ReviewWordsScreen())
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
                    .height(dimensionResource(R.dimen.icon_size_large))
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

@Composable
fun StatsSection(
    homeUiState: HomeUiState.Success,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.stats),
        style = MaterialTheme.typography.labelMedium
    )
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
    val fillMaxWidthModifier = Modifier.fillMaxWidth()
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            DaysOfWeekRow(modifier = fillMaxWidthModifier)
            ActiveDayOfWeekArrow(modifier = fillMaxWidthModifier)
            StreakRow(modifier = fillMaxWidthModifier)
            ShareRow(modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth()
                .clickable { })
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
private fun StreakRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StreakCard(
            labelResId = R.string.current_streak,
            valueResId = R.plurals.days,
            count = 2
        )
        StreakCard(
            labelResId = R.string.best_streak,
            valueResId = R.plurals.days_in_a_row,
            count = 13
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
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_smallest)))
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
private fun ShareRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null)
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_medium)))
        Text(text = stringResource(R.string.share))
    }
}

@Composable
fun ChartSection(homeUiState: HomeUiState.Success, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val quarterScreenHeight = configuration.screenHeightDp / 4
    Text(
        text = stringResource(R.string.chart),
        style = MaterialTheme.typography.labelMedium
    )
    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Text(
                text = "Time period 7 days",
            )
        }
        ResultBarChart(
            homeUiState = homeUiState,
            modifier = Modifier
                .height(quarterScreenHeight.dp)
                .fillMaxSize()
        )
    }
}

@Composable
fun ResultBarChart(homeUiState: HomeUiState.Success, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val lastTimePeriodDays = getLastNDates(homeUiState.timePeriod, ChronoUnit.DAYS)
    val lastTimePeriodDaysFormatted = lastTimePeriodDays.map { date ->
        val shortMonth = date.month.getDisplayName(
            TextStyle.SHORT,
            Locale.UK
        )
        "$shortMonth ${date.dayOfMonth}"
    }
    val entries = lastTimePeriodDays.indices.map { i ->
        val wordsCountOfStatus = homeUiState.listOfWordsCountOfStatus[i]
        BarEntry(
            i.toFloat(),
            wordsCountOfStatus
                .values
                .map {count -> count.toFloat() }
                .toFloatArray()
        )
    }

    AndroidView(factory = {
        BarChart(context).apply {
            // Creating dataset for each stack
            val dataset = BarDataSet(entries, "")
            dataset.stackLabels =
                arrayOf("Already known", "Reviewed", "New words learning", "Mastered")
            dataset.setDrawIcons(true)

            // Setting colors for each stack
            dataset.colors = ColorTemplate.MATERIAL_COLORS.asList()

            // Creating BarData object and adding datasets
            val barData = BarData(dataset)

            // Configure the BarChart
            data = barData
            setDrawValueAboveBar(false)
            description.isEnabled = false
            setFitBars(true)
            this.setScaleEnabled(true)
            // Y-axis configuration
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 5f // Adjust as needed
            }
            axisRight.isEnabled = false

            // X-axis configuration
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(lastTimePeriodDaysFormatted)
                setDrawGridLines(false)
            }

            // Legend configuration
            legend.apply {
                form = Legend.LegendForm.SQUARE
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                formToTextSpace = 4f
                xEntrySpace = 12f // space between legend items
            }
        }
    }, modifier = modifier)
}

@Preview
@Composable
fun HomeScreenPreview() {
    WordGalaxyTheme {
        Surface {
            HomeScreen(
                homeUiState = HomeUiState.Success(),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Preview
@Composable
fun StatsSectionPreview() {
    WordGalaxyTheme {
        Surface {
            StatsSection(HomeUiState.Success())
        }
    }
}

@Preview
@Composable
fun ResultBarChartPreview() {
    WordGalaxyTheme {
        Surface {
            ResultBarChart(
                homeUiState = HomeUiState.Success(),
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxSize()
            )
        }
    }
}
package ua.com.andromeda.wordgalaxy.ui.screens.start.home.graphics

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.data.model.WordStatus
import ua.com.andromeda.wordgalaxy.ui.screens.start.home.HomeUiState
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme
import ua.com.andromeda.wordgalaxy.utils.NonZeroChartValueFormatter
import ua.com.andromeda.wordgalaxy.utils.getLastNDates
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private typealias ArgbColor = Int

@Composable
fun ResultBarChart(homeUiState: HomeUiState.Success, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val labelColorArgb = MaterialTheme.colorScheme.secondary.toArgb()

    val lastTimePeriodDays = getLastNDates(homeUiState.timePeriod.days, ChronoUnit.DAYS)
    val lastTimePeriodDaysFormatted = formatXAxisLabels(lastTimePeriodDays)
    val listOfWordsCountOfStatus = homeUiState.listOfWordsCountOfStatus
    val entries = buildBarEntries(lastTimePeriodDays, listOfWordsCountOfStatus)
    val dataset = createDataset(context, entries)

    AndroidView(factory = {
        BarChart(context).apply {
            config(dataset)
            configXAxis(lastTimePeriodDaysFormatted, labelColorArgb)
            configYAxis(labelColorArgb)
            axisRight.isEnabled = false
            configLegend(labelColorArgb)
        }
    }, modifier = modifier)
}

private fun BarChart.config(barData: BarData) {
    data = barData
    data.setValueFormatter(NonZeroChartValueFormatter())
    setDrawValueAboveBar(false)
    description.isEnabled = false
    setFitBars(true)
    setScaleEnabled(true)
    animateXY(ANIMATE_XY_MILLIS, ANIMATE_XY_MILLIS, Easing.EaseInOutCubic)
}

private fun createDataset(
    context: Context,
    entries: List<BarEntry>,
): BarData {
    val dataset = BarDataSet(entries, "")
    val resources = context.resources
    dataset.setDrawIcons(true)
    dataset.stackLabels = arrayOf(
        resources.getString(R.string.stack_label_already_known),
        resources.getString(R.string.stack_label_new_words_memorization),
        resources.getString(R.string.stack_label_reviewed_unique_words),
        resources.getString(R.string.stack_label_mastered),
    )
    dataset.colors = listOf(
        Color(0xFF777777),
        Color(0xFFD342B0),
        Color(0xFFFFC806),
        Color(0xFF6FFF72)
    ).map { it.toArgb() }

    return BarData(dataset)
}

private fun BarChart.configYAxis(textColor: ArgbColor) {
    axisLeft.apply {
        axisMinimum = Y_AXIS_MINIMUM
        granularity = Y_AXIS_GRANULARITY
        setTextColor(textColor)
    }
}

private fun BarChart.configXAxis(xAxisValues: List<String>, textColor: ArgbColor) {
    xAxis.apply {
        setDrawGridLines(false)
        position = XAxis.XAxisPosition.BOTTOM
        valueFormatter = IndexAxisValueFormatter(xAxisValues)
        setTextColor(textColor)
        labelCount = X_AXIS_LABEL_COUNT
    }
}

private fun BarChart.configLegend(textColor: ArgbColor) {
    legend.apply {
        form = Legend.LegendForm.SQUARE
        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        orientation = Legend.LegendOrientation.VERTICAL
        formToTextSpace = LEGEND_FORM_TO_TEXT_SPACE
        setTextColor(textColor)
        isWordWrapEnabled = true
        textSize = LEGEND_TEXT_SIZE
        maxSizePercent = LEGEND_MAX_SIZE_PERCENT
        yOffset = LEGEND_Y_OFFSET
        yEntrySpace = LEGEND_Y_ENTRY_SPACE
    }
}

private fun formatXAxisLabels(lastTimePeriodDays: List<LocalDateTime>) =
    lastTimePeriodDays.map { date ->
        val shortMonth = date.month.getDisplayName(
            TextStyle.SHORT,
            Locale.UK
        )
        "$shortMonth ${date.dayOfMonth}"
    }

private fun buildBarEntries(
    timestamps: List<LocalDateTime>,
    listOfWordsCountOfStatus: List<Map<WordStatus, Int>>
): List<BarEntry> {
    return timestamps
        .indices
        .map { i -> BarEntry(i.toFloat(), getFloatArrayValues(listOfWordsCountOfStatus[i])) }
}

private fun getFloatArrayValues(wordsCountOfStatus: Map<WordStatus, Int>) =
    wordsCountOfStatus
        .filterKeys { wordStatus -> wordStatus != WordStatus.New }
        .values
        .map { count -> count.toFloat() }
        .toFloatArray()

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
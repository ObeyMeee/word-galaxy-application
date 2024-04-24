package ua.com.andromeda.wordgalaxy.home.presentation.components.graphics

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.utils.NonZeroChartValueFormatter
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private typealias ArgbColor = Int

@Composable
fun ResultBarChart(
    data: Map<LocalDate, Map<WordStatus, Int>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val labelColorArgb = MaterialTheme.colorScheme.secondary.toArgb()
    val xAxisValues = formatXAxisLabels(data.keys)
    val entries = buildBarEntries(data.values)

    // trigger recomposition when new data arrives
    key(data) {
        AndroidView(factory = { context ->
            val barChart = BarChart(context)
            barChart.apply {
                val dataset = context.createDataset(entries)
                notifyDataSetChanged()
                invalidate()
                config(dataset)
                configXAxis(xAxisValues, labelColorArgb)
                configYAxis(labelColorArgb)
                axisRight.isEnabled = false
                configLegend(labelColorArgb)
            }
        }, modifier = modifier)
    }
}

private fun BarChart.config(barData: BarData) {
    data = barData
    data.setValueFormatter(NonZeroChartValueFormatter())
    setDrawValueAboveBar(false)
    description.isEnabled = false
    setFitBars(true)
    setScaleEnabled(true)
    isDoubleTapToZoomEnabled = true

    animateXY(ANIMATE_XY_MILLIS, ANIMATE_XY_MILLIS, Easing.EaseInOutCubic)
}

private fun Context.createDataset(entries: List<BarEntry>): BarData {
    val dataset = BarDataSet(entries, "")
    dataset.apply {
        setDrawIcons(true)
        stackLabels = arrayOf(
            resources.getString(R.string.stack_label_already_known),
            resources.getString(R.string.stack_label_new_words_memorization),
            resources.getString(R.string.stack_label_reviewed_unique_words),
            resources.getString(R.string.stack_label_mastered),
        )
        colors = WordStatus
            .entries
            .filter { it != WordStatus.New }
            .map { it.iconColor.toArgb() }
    }
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

private fun formatXAxisLabels(lastTimePeriodDays: Collection<LocalDate>) =
    lastTimePeriodDays.map { date ->
        val shortMonth = date.month.getDisplayName(
            TextStyle.SHORT,
            Locale.UK
        )
        "$shortMonth ${date.dayOfMonth}"
    }

private fun buildBarEntries(
    listOfWordsCountOfStatus: Collection<Map<WordStatus, Int>>,
): List<BarEntry> {
    return listOfWordsCountOfStatus.mapIndexed { i, wordsCountOfStatus ->
        BarEntry(i.toFloat(), getFloatArrayValues(wordsCountOfStatus))
    }
}

private fun getFloatArrayValues(wordsCountOfStatus: Map<WordStatus, Int>) =
    wordsCountOfStatus
        .filterKeys { wordStatus -> wordStatus != WordStatus.New }
        .values
        .map { count -> count.toFloat() }
        .toFloatArray()

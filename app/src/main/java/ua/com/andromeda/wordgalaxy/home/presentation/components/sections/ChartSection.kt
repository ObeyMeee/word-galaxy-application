package ua.com.andromeda.wordgalaxy.home.presentation.components.sections

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.domain.model.WordStatus
import ua.com.andromeda.wordgalaxy.home.presentation.Section
import ua.com.andromeda.wordgalaxy.home.presentation.components.ChartPeriodRangeStart
import ua.com.andromeda.wordgalaxy.home.presentation.components.TimePeriodDialog
import ua.com.andromeda.wordgalaxy.home.presentation.components.graphics.ResultBarChart
import java.time.LocalDate

@Composable
fun ChartSection(
    period: Pair<LocalDate, LocalDate>,
    isPeriodDialogOpen: Boolean,
    listOfWordsCountOfStatus: Map<LocalDate, Map<WordStatus, Int>>,
    toggleDialog: (Boolean) -> Unit,
    updateTimePeriod: (LocalDate, LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val chartHeight = configuration.screenHeightDp / 2
    Section(modifier) {
        Text(
            text = stringResource(R.string.chart),
            style = MaterialTheme.typography.labelMedium
        )
        Card {
            Row(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .clickable {
                        toggleDialog(true)
                    }
            ) {
                Text(text = stringResource(id = R.string.time_period))
                Text(
                    text = getLabel(period, context),
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_smaller))
                )
                TimePeriodDialog(
                    visible = isPeriodDialogOpen,
                    toggleDialog = toggleDialog,
                    updateTimePeriod = updateTimePeriod
                )
            }

            ResultBarChart(
                data = listOfWordsCountOfStatus,
                modifier = Modifier
                    .height(chartHeight.dp)
                    .fillMaxSize()
            )
        }
    }
}

private fun getLabel(period: Pair<LocalDate, LocalDate>, context: Context): String {
    val (start, end) = period
    val now = LocalDate.now()
    return when {
        start == ChartPeriodRangeStart.LAST_WEEK && end == now -> context.getString(R.string.last_week)
        start == ChartPeriodRangeStart.THIS_MONTH && end == now -> context.getString(R.string.this_month)
        start == ChartPeriodRangeStart.THIS_YEAR && end == now -> context.getString(R.string.this_year)
        start == ChartPeriodRangeStart.ALL_TIME && end == now -> context.getString(R.string.all_time)
        else -> "$start - $end"
    }
}
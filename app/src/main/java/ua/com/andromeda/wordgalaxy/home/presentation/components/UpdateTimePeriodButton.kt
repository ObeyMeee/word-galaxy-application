package ua.com.andromeda.wordgalaxy.home.presentation.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R
import java.time.LocalDate


data class UpdateTimePeriodState(
    val label: String,
    val startDate: LocalDate,
    val endDate: LocalDate = LocalDate.now(),
    val onClick: (LocalDate, LocalDate) -> Unit,
)

@Composable
fun getButtonStates(updateTimePeriod: (LocalDate, LocalDate) -> Unit) =
    listOf(
        UpdateTimePeriodState(
            label = stringResource(R.string.last_week),
            startDate = ChartPeriodRangeStart.LAST_WEEK,
            onClick = updateTimePeriod,
        ),
        UpdateTimePeriodState(
            label = stringResource(R.string.this_month),
            startDate = ChartPeriodRangeStart.THIS_MONTH,
            onClick = updateTimePeriod,
        ),
        UpdateTimePeriodState(
            label = stringResource(R.string.this_year),
            startDate = ChartPeriodRangeStart.THIS_YEAR,
            onClick = updateTimePeriod,
        ),
        UpdateTimePeriodState(
            label = stringResource(R.string.all_time),
            startDate = ChartPeriodRangeStart.ALL_TIME,
            onClick = updateTimePeriod,
        ),
    )

@Composable
fun UpdateTimePeriodButton(
    state: UpdateTimePeriodState,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = {
            state.onClick(state.startDate, state.endDate)
        },
        modifier = modifier,
    ) {
        Text(text = state.label)
    }
}
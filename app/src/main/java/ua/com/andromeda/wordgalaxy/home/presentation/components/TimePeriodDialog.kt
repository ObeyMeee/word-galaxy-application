package ua.com.andromeda.wordgalaxy.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import ua.com.andromeda.wordgalaxy.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private const val APP_RELEASE_YEAR = 2024

const val AMOUNT_X_AXIS_LABELS = 7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePeriodDialog(
    visible: Boolean,
    toggleDialog: (Boolean) -> Unit,
    updateTimePeriod: (LocalDate, LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val now = LocalDate.now()
    val state = rememberDateRangePickerState(
        yearRange = APP_RELEASE_YEAR..now.year
    )
    val buttonStates = getButtonStates(updateTimePeriod)

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
    ) {
        DatePickerDialog(
            onDismissRequest = { toggleDialog(false) },
            confirmButton = {
                val startDate = state.selectedStartDateMillis?.toLocalDate()
                val endDate = state.selectedEndDateMillis?.toLocalDate()
                val rangeSelected = startDate != null && endDate != null

                Button(
                    onClick = {
                        updateTimePeriod(startDate!!, endDate!!)
                    },
                    enabled = rangeSelected && endDate!!.toEpochDay() - startDate!!.toEpochDay() >= AMOUNT_X_AXIS_LABELS
                ) {
                    Text(text = stringResource(R.string.select))
                }
            },
        ) {
            DateRangePicker(
                modifier = Modifier.weight(1f), // Important to display the button
                state = state,
                dateValidator = { Instant.now() >= Instant.ofEpochMilli(it) },
                headline = {
                    Text(
                        text = stringResource(R.string.select_time_period),
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                },
                title = null,
                showModeToggle = false,
            )
            Row(
                modifier = Modifier.padding(
                    dimensionResource(R.dimen.padding_small)
                ),
            ) {
                buttonStates.forEach { buttonState ->
                    UpdateTimePeriodButton(state = buttonState)
                }
            }
        }
    }
}

private fun Long.toLocalDate() = LocalDate.ofInstant(
    Instant.ofEpochMilli(this),
    ZoneId.systemDefault()
)
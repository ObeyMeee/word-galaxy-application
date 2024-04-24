package ua.com.andromeda.wordgalaxy.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ua.com.andromeda.wordgalaxy.R
import ua.com.andromeda.wordgalaxy.core.presentation.components.CenteredLoadingSpinner
import ua.com.andromeda.wordgalaxy.core.presentation.components.Message
import ua.com.andromeda.wordgalaxy.home.presentation.components.sections.ChartSection
import ua.com.andromeda.wordgalaxy.home.presentation.components.sections.RepetitionSection
import ua.com.andromeda.wordgalaxy.home.presentation.components.sections.StatsSection
import ua.com.andromeda.wordgalaxy.ui.theme.WordGalaxyTheme

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
                    period = state.startPeriod to state.endPeriod,
                    listOfWordsCountOfStatus = state.chartData,
                    isPeriodDialogOpen = state.isPeriodDialogOpen,
                    updateTimePeriod = viewModel::updatePeriod,
                    toggleDialog = viewModel::updateShowTimePeriodDialog,
                )
            }
        }
    }
}


@Composable
inline fun Section(
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

package ua.com.andromeda.wordgalaxy.ui.screens.reportmistake

import ua.com.andromeda.wordgalaxy.data.model.Word

sealed interface ReportMistakeUiState {
    data object Default : ReportMistakeUiState
    data class Error(val message: String = "Something went wrong") : ReportMistakeUiState

    data class Success(
        val mistakenWord: Word,
        val report: Report = Report(mistakenWord.value, mistakenWord.translation),
        val isFormValid: Boolean = false,
        val isReportSending: Boolean = false
    ) : ReportMistakeUiState
}
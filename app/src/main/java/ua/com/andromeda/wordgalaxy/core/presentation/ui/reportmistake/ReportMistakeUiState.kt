package ua.com.andromeda.wordgalaxy.core.presentation.ui.reportmistake

import ua.com.andromeda.wordgalaxy.core.domain.model.Word

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
package ua.com.andromeda.wordgalaxy.home.presentation.components

enum class TimePeriodChartOptions(val days: Int, val label: String) {
    WEEK(7, "7 days"),
    MONTH(30, "30 days"),
    SEASON(90, "90 days"),
    YEAR(365, "1 year"),
}
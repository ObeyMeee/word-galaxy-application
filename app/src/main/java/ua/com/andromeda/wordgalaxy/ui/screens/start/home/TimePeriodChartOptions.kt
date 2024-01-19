package ua.com.andromeda.wordgalaxy.ui.screens.start.home

enum class TimePeriodChartOptions(val days: Int, val label: String) {
    WEEK(7, "7 days"),
    MONTH(30, "30 days"),
    SEASON(90, "90 days"),
    YEAR(365, "1 year"),
    ALL_TIME(Integer.MAX_VALUE, "All time");
}
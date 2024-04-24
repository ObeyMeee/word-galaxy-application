package ua.com.andromeda.wordgalaxy.home.presentation.components

import java.time.LocalDate

object ChartPeriodRangeStart {
    val LAST_WEEK: LocalDate
        get() = LocalDate.now().minusDays(6)

    val THIS_MONTH: LocalDate
        get() = LocalDate.now().withDayOfMonth(1)

    val THIS_YEAR: LocalDate
        get() = LocalDate.now().withMonth(1).withDayOfMonth(1)

    val ALL_TIME: LocalDate
        get() = LocalDate.MIN
}
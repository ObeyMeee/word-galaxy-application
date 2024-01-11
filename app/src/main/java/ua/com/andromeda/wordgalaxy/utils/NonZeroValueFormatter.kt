package ua.com.andromeda.wordgalaxy.utils

import com.github.mikephil.charting.formatter.ValueFormatter

class NonZeroChartValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String =
        if (value > 0)
            value.toInt().toString()
        else
            ""
}
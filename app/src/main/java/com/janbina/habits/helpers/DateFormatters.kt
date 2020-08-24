package com.janbina.habits.helpers

import android.content.res.Resources
import com.janbina.habits.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatters @Inject constructor(
    private val resources: Resources
) {

    private val locale = Locale.US

    val dayNumFormatter: DateTimeFormatter = patternFormatter("dd")
    val shortDayNameFormatter: DateTimeFormatter = patternFormatter("EEE")

    private val namedDateFormatter = patternFormatter("EEE, MMMM dd")
    private val namedDateAndYearFormatter = patternFormatter("EEE, MMMM dd yyyy")

    fun formatRelative(date: LocalDate): String {
        val now = LocalDate.now()
        val dayDiff = ChronoUnit.DAYS.between(date, now)

        return when {
            dayDiff == 0L -> resources.getString(R.string.today)
            dayDiff == 1L -> resources.getString(R.string.yesterday)
            dayDiff == -1L -> resources.getString(R.string.tomorrow)
            dayDiff in 2L..5L -> resources.getString(R.string.days_ago, dayDiff)
            date.year == now.year -> date.format(namedDateFormatter)
            else -> date.format(namedDateAndYearFormatter)
        }
    }

    private val monthNameFormatter = patternFormatter("MMMM")
    private val monthNameYearFormatter = patternFormatter("MMMM yyyy")

    fun formatMonthNameOptionalYear(date: YearMonth): String {
        return if (date.year == LocalDate.now().year) {
            monthNameFormatter.format(date)
        } else {
            monthNameYearFormatter.format(date)
        }
    }

    private fun patternFormatter(pattern: String) = DateTimeFormatter.ofPattern(pattern, locale)

}
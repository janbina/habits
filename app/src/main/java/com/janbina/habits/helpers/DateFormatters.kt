package com.janbina.habits.helpers

import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatters @Inject constructor() {

    private val locale = Locale.US

    val dayNumFormatter = DateTimeFormatter.ofPattern("dd").withLocale(locale)
    val dayNameFormatter = DateTimeFormatter.ofPattern("EEE").withLocale(locale)
    val fullDateFormatter = DateTimeFormatter.ofPattern("EEE, MMMM dd").withLocale(locale)
    val fullDateFormatter2 = DateTimeFormatter.ofPattern("EEE, MMMM dd yyyy").withLocale(locale)

    val monthNameFormatter = DateTimeFormatter.ofPattern("MMMM").withLocale(locale)
    val monthNameYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale)

    fun formatRelative(date: LocalDate): String {
        val now = LocalDate.now()
        val dayDiff = ChronoUnit.DAYS.between(date, now)

        return when {
            dayDiff == 0L -> "Today"
            dayDiff == 1L -> "Yesterday"
            dayDiff == -1L -> "Tomorrow"
            dayDiff in 2L..5L -> "$dayDiff days ago"
            date.year == now.year -> date.format(fullDateFormatter)
            else -> date.format(fullDateFormatter2)
        }
    }

    fun formatMonthNameOptionalYear(date: YearMonth): String {
        return if (date.year == LocalDate.now().year) {
            monthNameFormatter.format(date)
        } else {
            monthNameYearFormatter.format(date)
        }
    }

}
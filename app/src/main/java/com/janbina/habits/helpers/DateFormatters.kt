package com.janbina.habits.helpers

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatters @Inject constructor() {

    val dayNumFormatter = DateTimeFormatter.ofPattern("dd")
    val dayNameFormatter = DateTimeFormatter.ofPattern("EEE")
    val fullDateFormatter = DateTimeFormatter.ofPattern("EEE, MMMM dd")
    val fullDateFormatter2 = DateTimeFormatter.ofPattern("EEE, MMMM dd yyyy")

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

}
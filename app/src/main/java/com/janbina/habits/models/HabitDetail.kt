package com.janbina.habits.models

import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

data class HabitDetail(
    val habit: Habit,
    val days: List<Long>
) {
    fun thisYearCount(): Int {
        val year = YearMonth.now().year
        val firstDay = LocalDate.of(year, Month.JANUARY, 1).toEpochDay()
        val lastDay = LocalDate.of(year, Month.DECEMBER, 31).toEpochDay()
        return days.count { it in firstDay..lastDay }
    }

    fun yearToDateCount(): Int {
        val today = LocalDate.now()
        val lastYear = today.minusYears(1)
        return days.count { it in lastYear.toEpochDay()..today.toEpochDay() }
    }
}
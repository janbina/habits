package com.janbina.habits.data.repository

import com.janbina.habits.data.preferences.Preferences
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DaysRepository @Inject constructor(
    private val prefs: Preferences
) {

    fun getFirstDayOfWeek(): DayOfWeek {
        return when (Preferences.FirstDayOfWeek.find(prefs.firstDayOfWeek)) {
            Preferences.FirstDayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            Preferences.FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
            Preferences.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
            Preferences.FirstDayOfWeek.SYSTEM -> WeekFields.of(Locale.getDefault()).firstDayOfWeek
        }
    }

    fun getDaysOfWeekSorted(): List<DayOfWeek> {
        val firstDayOfWeek = getFirstDayOfWeek()
        val daysOfWeek = DayOfWeek.values()
        val start = daysOfWeek.slice(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val end = daysOfWeek.slice(0 until firstDayOfWeek.ordinal)

        return start + end
    }
}
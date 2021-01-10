package com.janbina.habits.models.firestore

import com.janbina.habits.models.RawDay

data class DayFirestore(
    val day: Long = 0,
    val completed: List<String> = emptyList(),
) {
    fun toRawDay() = RawDay(day = day, completed)
}
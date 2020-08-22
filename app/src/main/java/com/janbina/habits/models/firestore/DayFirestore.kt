package com.janbina.habits.models.firestore

data class DayFirestore(
    val day: Int = 0,
    val completed: List<String> = emptyList(),
)
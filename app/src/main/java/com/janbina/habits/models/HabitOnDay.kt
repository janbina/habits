package com.janbina.habits.models

data class HabitOnDay(
    val id: String,
    val name: String,
    val completed: Boolean,
    val archived: Boolean,
)
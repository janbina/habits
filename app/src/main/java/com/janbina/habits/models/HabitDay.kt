package com.janbina.habits.models

data class HabitDay(
    val id: String,
    val name: String,
    val completed: Boolean,
    val archived: Boolean,
)
package com.janbina.habits.models

data class Day(
    val active: List<HabitOnDay>,
    val archived: List<HabitOnDay>,
)
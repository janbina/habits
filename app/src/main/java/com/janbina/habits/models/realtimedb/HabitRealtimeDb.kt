package com.janbina.habits.models.realtimedb

import com.google.firebase.database.IgnoreExtraProperties
import com.janbina.habits.models.Habit

@IgnoreExtraProperties
data class HabitRealtimeDb(
    val id: String = "",
    val name: String = "",
    val archived: Boolean = false,
) {
    constructor(habit: Habit): this(
        habit.id,
        habit.name,
        habit.archived,
    )

    fun toHabit() = Habit(
        id = id,
        name = name,
        archived = archived,
    )
}
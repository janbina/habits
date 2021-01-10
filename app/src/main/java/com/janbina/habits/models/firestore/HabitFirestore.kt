package com.janbina.habits.models.firestore

import com.google.firebase.firestore.DocumentId
import com.janbina.habits.models.Habit

data class HabitFirestore(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val archived: Boolean = false,
) {
    constructor(habit: Habit): this(
        id = habit.id,
        name = habit.name,
        archived = habit.archived
    )

    fun toHabit() = Habit(
        id = id,
        name = name,
        archived = archived,
    )
}
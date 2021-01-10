package com.janbina.habits.data.database

import com.janbina.habits.data.repository.Res
import com.janbina.habits.models.Habit
import com.janbina.habits.models.RawDay
import kotlinx.coroutines.flow.Flow

interface Database {
    fun saveHabit(habit: Habit)

    fun deleteHabit(id: String)

    fun changeHabitCompletionForDay(id: String, day: Long, completed: Boolean)

    fun getHabit(id:String): MappedResult<Habit>

    fun getDaysWhenHabitCompleted(id: String): MappedResult<List<Long>>

    fun getDay(day: Long): MappedResult<RawDay>

    fun getAllHabits(): MappedResult<List<Habit>>
}
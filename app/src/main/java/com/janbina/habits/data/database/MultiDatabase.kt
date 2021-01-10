package com.janbina.habits.data.database

import com.janbina.habits.models.Habit
import com.janbina.habits.models.RawDay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultiDatabase @Inject constructor(
    private val firestoreDb: FirestoreDb,
    private val realtimeDb: RealtimeDb,
) : Database {

    override fun saveHabit(habit: Habit) {
        firestoreDb.saveHabit(habit)
        realtimeDb.saveHabit(habit)
    }

    override fun deleteHabit(id: String) {
        firestoreDb.deleteHabit(id)
        realtimeDb.deleteHabit(id)
    }

    override fun changeHabitCompletionForDay(id: String, day: Long, completed: Boolean) {
        firestoreDb.changeHabitCompletionForDay(id, day, completed)
        realtimeDb.changeHabitCompletionForDay(id, day, completed)
    }

    override fun getHabit(id: String): MappedResult<Habit> {
        return firestoreDb.getHabit(id)
    }

    override fun getDaysWhenHabitCompleted(id: String): MappedResult<List<Long>> {
        return firestoreDb.getDaysWhenHabitCompleted(id)
    }

    override fun getDay(day: Long): MappedResult<RawDay> {
        return firestoreDb.getDay(day)
    }

    override fun getAllHabits(): MappedResult<List<Habit>> {
        return firestoreDb.getAllHabits()
    }
}
package com.janbina.habits.data.repository

import com.github.kittinunf.result.Result
import com.janbina.habits.data.database.FirestoreDb
import com.janbina.habits.models.HabitDay
import com.janbina.habits.models.firestore.DayFirestore
import com.janbina.habits.models.firestore.HabitFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

typealias Res<T> = Result<T, Exception>

class HabitsRepository @Inject constructor(
    private val firestoreDb: FirestoreDb,
) {

    fun saveHabit(id: String?, name: String) {
        firestoreDb.saveHabit(id, HabitFirestore(name = name))
    }

    fun deleteHabit(id: String) {
        firestoreDb.deleteHabit(id)
    }

    fun setHabitComplete(id: String, day: Int, completed: Boolean) {
        firestoreDb.changeHabitCompletionForDay(id, day, completed)
    }

    suspend fun getHabitInfo(id: String): Res<HabitFirestore> {
        return firestoreDb.getHabit(id).get()
    }

    fun getHabitDetail(id: String): Flow<Res<HabitDetail>> = callbackFlow {
        val subs = TupleQuery(
            firestoreDb.getHabit(id),
            firestoreDb.getDaysWhenHabitCompleted(id)
        ).addListener { habit, days ->
            try {
                offer(Res.success(HabitDetail(habit.get(), days.get())))
            } catch (e: Exception) {
                offer(Res.error(e))
            }
        }

        awaitClose {
            subs.forEach { it.remove() }
        }
    }

    data class HabitDetail(
        val habit: HabitFirestore,
        val days: List<Long>
    )

    fun getHabitsForDay(day: Int): Flow<Res<List<HabitDay>>> = callbackFlow {
        val subs = TupleQuery(
            firestoreDb.getDays(day - 14, day),
            firestoreDb.getAllHabits()
        ).addListener { days, habits ->
            try {
                offer(Res.success(createHabitDays(habits.get(), createDaysResponse(days.get(), day))))
            } catch (e: Exception) {
                offer(Res.error(e))
            }
        }

        awaitClose {
            subs.forEach { it.remove() }
        }
    }

    private fun createDaysResponse(days: List<DayFirestore>, day: Int): DaysResponse {
        val completed = mutableSetOf<String>()
        val counts = mutableMapOf<String, Int>()

        days.forEach {
            if (it.day == day) {
                completed.addAll(it.completed)
            } else {
                it.completed.forEach { id ->
                    counts.merge(id, 14 - day - it.day, Integer::sum)
                }
            }
        }

        return DaysResponse(completed, counts)
    }

    private fun createHabitDays(
        habits: List<HabitFirestore>,
        daysResponse: DaysResponse
    ): List<HabitDay> {
        return habits.map {
            HabitDay(it.id, it.name, daysResponse.completed.contains(it.id))
        }.sortedWith(Comparator { a, b ->
            when {
                a.completed && b.completed -> a.name.compareTo(b.name)
                a.completed -> -1
                b.completed -> 1
                else -> {
                    val aCount = daysResponse.counts.getOrDefault(a.id, 0)
                    val bCount = daysResponse.counts.getOrDefault(b.id, 0)
                    bCount.compareTo(aCount)
                }
            }
        })
    }

    data class DaysResponse(
        val completed: Set<String>,
        val counts: Map<String, Int>
    )

}
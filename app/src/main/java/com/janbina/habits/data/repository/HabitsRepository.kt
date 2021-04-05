package com.janbina.habits.data.repository

import com.github.kittinunf.result.Result
import com.janbina.habits.data.database.Database
import com.janbina.habits.data.database.IdGenerator
import com.janbina.habits.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

typealias Res<T> = Result<T, Exception>

@Singleton
class HabitsRepository @Inject constructor(
    private val database: Database,
) {

    fun saveHabit(id: String?, name: String) {
        database.saveHabit(Habit(
            id = id ?: IdGenerator.generate(),
            name = name,
            archived = false,
        ))
    }

    fun saveHabit(habit: Habit) {
        database.saveHabit(habit)
    }

    fun deleteHabit(id: String) {
        database.deleteHabit(id)
    }

    fun setHabitComplete(id: String, day: Long, completed: Boolean) {
        database.changeHabitCompletionForDay(id, day, completed)
    }

    suspend fun getHabitInfo(id: String): Res<Habit> {
        return database.getHabit(id).get()
    }

    fun getHabitDetail(id: String): Flow<Res<HabitDetail>> {
        return combine(
            database.getHabit(id).asFlow(),
            database.getDaysWhenHabitCompleted(id).asFlow()
        ) { habit, days ->
            try {
                Res.success(HabitDetail(habit.get(), days.get()))
            } catch (e: Exception) {
                Res.error(e)
            }
        }
    }



    fun getHabitsForDay(day: Long): Flow<Res<Day>>  {
        return combine(
            database.getDay(day).asFlow(),
            database.getAllHabits().asFlow(),
        ) { day, habits ->
            try {
                Res.success(createHabitsForDay(habits.get(), day.get()))
            } catch (e: Exception) {
                Res.error(e)
            }
        }
    }

    private fun createHabitsForDay(
        allHabits: List<Habit>,
        day: RawDay,
    ): Day {
        val completed = day.completed.toSet()
        val active = mutableListOf<HabitOnDay>()
        val archived = mutableListOf<HabitOnDay>()
        allHabits.forEach {
            val mapped = HabitOnDay(it.id, it.name, completed.contains(it.id), it.archived)
            if (mapped.archived && mapped.completed.not()) {
                archived.add(mapped)
            } else {
                active.add(mapped)
            }
        }
        return Day(
            active.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })),
            archived.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name }))
        )
    }



//    private fun createDaysResponse(days: List<DayFirestore>, day: Int): DaysResponse {
//        val completed = mutableSetOf<String>()
//        val counts = mutableMapOf<String, Int>()
//
//        days.forEach {
//            if (it.day == day) {
//                completed.addAll(it.completed)
//            } else {
//                it.completed.forEach { id ->
//                    counts.merge(id, 14 - day - it.day, Integer::sum)
//                }
//            }
//        }
//
//        return DaysResponse(completed, counts)
//    }
//
//    private fun createHabitDays(
//        habits: List<HabitFirestore>,
//        daysResponse: DaysResponse
//    ): List<HabitDay> {
//        return habits.map {
//            HabitDay(it.id, it.name, daysResponse.completed.contains(it.id))
//        }.sortedBy { it.name }
//
////        .sortedWith(Comparator { a, b ->
////            when {
////                a.completed && b.completed -> a.name.compareTo(b.name)
////                a.completed -> -1
////                b.completed -> 1
////                else -> {
////                    val aCount = daysResponse.counts.getOrDefault(a.id, 0)
////                    val bCount = daysResponse.counts.getOrDefault(b.id, 0)
////                    bCount.compareTo(aCount)
////                }
////            }
////        })
//    }

//    data class DaysResponse(
//        val completed: Set<String>,
//        val counts: Map<String, Int>
//    )

}
package com.janbina.habits.data.repository

import com.github.kittinunf.result.Result
import com.janbina.habits.data.database.FirestoreDb
import com.janbina.habits.models.HabitDay
import com.janbina.habits.models.firestore.DayFirestore
import com.janbina.habits.models.firestore.HabitFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import javax.inject.Inject

typealias Res<T> = Result<T, Exception>

class HabitsRepository @Inject constructor(
    private val firestoreDb: FirestoreDb,
) {

    fun saveHabit(id: String?, name: String) {
        firestoreDb.saveHabit(id, HabitFirestore(name = name))
    }

    fun saveHabit(habit: HabitFirestore) {
        firestoreDb.saveHabit(habit.id, habit)
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

    fun getHabitDetail(id: String): Flow<Res<HabitDetail>> {
        return combine(
            firestoreDb.getHabit(id).asFlow(),
            firestoreDb.getDaysWhenHabitCompleted(id).asFlow()
        ) { habit, days ->
            try {
                Res.success(HabitDetail(habit.get(), days.get()))
            } catch (e: Exception) {
                Res.error(e)
            }
        }
    }

    data class HabitDetail(
        val habit: HabitFirestore,
        val days: List<Long>
    ) {
        fun thisYearCount(): Int {
            val year = YearMonth.now().year
            val firstDay = LocalDate.of(year, Month.JANUARY, 1).toEpochDay()
            val lastDay = LocalDate.of(year, Month.DECEMBER, 31).toEpochDay()
            return days.count { it in firstDay..lastDay }
        }

        fun yearToDateCount(): Int {
            val today = LocalDate.now()
            val lastYear = today.minusYears(1)
            return days.count { it in lastYear.toEpochDay()..today.toEpochDay() }
        }
    }

    fun getHabitsForDay(day: Int): Flow<Res<HabitsForDay>>  {
        return combine(
            firestoreDb.getDay(day).asFlow(),
            firestoreDb.getAllHabits().asFlow()
        ) { day, habits ->
            try {
                Res.success(createHabitsForDay(habits.get(), day.get()))
            } catch (e: Exception) {
                Res.error(e)
            }
        }
    }

    private fun createHabitsForDay(
        allHabits: List<HabitFirestore>,
        day: DayFirestore?
    ): HabitsForDay {
        val completed = day?.completed?.toSet() ?: emptySet()
        val active = mutableListOf<HabitDay>()
        val archived = mutableListOf<HabitDay>()
        allHabits.forEach {
            val mapped = HabitDay(it.id, it.name, completed.contains(it.id), it.archived)
            if (mapped.archived && mapped.completed.not()) {
                archived.add(mapped)
            } else {
                active.add(mapped)
            }
        }
        return HabitsForDay(
            active.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })),
            archived.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name }))
        )
    }

    data class HabitsForDay(
        val active: List<HabitDay>,
        val archived: List<HabitDay>,
    )

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
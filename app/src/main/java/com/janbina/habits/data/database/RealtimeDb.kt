package com.janbina.habits.data.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.models.Habit
import com.janbina.habits.models.RawDay
import com.janbina.habits.models.realtimedb.HabitRealtimeDb
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Database structure:
 *     users = {
 *         USER123 = {
 *             name = "xxxx"
 *             ...
 *         }
 *         USER456 = { ... }
 *     }
 *
 *     habitsInfo = {
 *         HABIT123 = {
 *             name = ""
 *             archived = false
 *         }
 *         HABIT456 = { ... }
 *     }
 *
 *     habitsDays = {
 *         HABIT123 = {
 *             2398572 = true
 *             3453984 = true
 *         }
 *         HABIT456 = { ... }
 *     }
 *
 *     days = {
 *         2398572 = {
 *             HABIT123 = true
 *         }
 *         3453984 = { ... }
 *     }
 *
 */


@Singleton
class RealtimeDb @Inject constructor(
    private val db: FirebaseDatabase,
    private val authRepository: AuthRepository,
) : Database {

    init {
        db.setPersistenceEnabled(true)
    }

    private val reference get() = db.reference
    private val userId get() = authRepository.getUser()?.uid ?: "dummy"

    private val user get() = reference.child("users").child(userId)
    private val userHabitsInfo get() = user.child("habitsInfo")
    private val userDays get() = user.child("days")
    private val userHabitsDays get() = user.child("habitsDays")

    override fun saveHabit(habit: Habit) {
        userHabitsInfo.child(habit.id).setValue(HabitRealtimeDb(habit))
    }

    override fun deleteHabit(id: String) {
        userHabitsInfo.child(id).removeValue()
        userHabitsDays.child(id).removeValue()
        // TODO - remove from days???
    }

    override fun getHabit(id:String): MappedResult<Habit> {
        return userHabitsInfo.child(id).withMapper {
            it.toObject<HabitRealtimeDb>()?.toHabit() ?: error("Missing/invalid habit object")
        }
    }

    override fun changeHabitCompletionForDay(id: String, day: Long, completed: Boolean) {
        if (completed) {
            userDays.child(day.toString()).child(id).setValue(true)
            userHabitsDays.child(id).child(day.toString()).setValue(true)
        } else {
            userDays.child(day.toString()).child(id).removeValue()
            userHabitsDays.child(id).child(day.toString()).removeValue()
        }
    }

    override fun getDaysWhenHabitCompleted(id: String): MappedResult<List<Long>> {
        Timber.e("GET DAYS WHEN HABIT COMPLETED")
        return userHabitsDays.child(id).withMapper {
            Timber.e("MAPPER ${it.children}")
            it.children.map { it.key!!.toLong() }
        }
    }

    override fun getDay(day: Long): MappedResult<RawDay> {
        return userDays.child(day.toString()).withMapper {
            val ids = it.children.map { it.key!! }
            RawDay(day, ids)
        }
    }

    override fun getAllHabits(): MappedResult<List<Habit>> {
        return userHabitsInfo.withMapper {
            it.children.map {
                it.toObject<HabitRealtimeDb>()?.toHabit() ?: error("Invalid habit object")
            }
        }
    }
}

inline fun <reified T> DataSnapshot.toObject(): T? {
    return getValue(object : GenericTypeIndicator<T>() {})
}

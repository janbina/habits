package com.janbina.habits.data.database

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.models.Habit
import com.janbina.habits.models.RawDay
import com.janbina.habits.models.firestore.DayFirestore
import com.janbina.habits.models.firestore.HabitFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDb @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) : Database {

    private val userId get() = authRepository.getUser()?.uid ?: "dummy"

    private val userDocument get() = firestore.collection("users").document(userId)
    private val userHabitsCollection get() = userDocument.collection("habits")
    private val userDaysCollection get() = userDocument.collection("days")

    private fun userDayDocument(day: Long) = userDaysCollection.document(day.toString())

    override fun saveHabit(habit: Habit) {
        userHabitsCollection.document(habit.id).set(HabitFirestore(habit))
    }

    override fun deleteHabit(id: String) {
        userHabitsCollection.document(id).delete()
    }

    override fun getHabit(id: String): MappedResult<Habit> {
        return userHabitsCollection.document(id).withMapper {
            it.toObject<HabitFirestore>()?.toHabit() ?: error("Missing/invalid habit")
        }
    }

    override fun changeHabitCompletionForDay(id: String, day: Long, completed: Boolean) {
        val fieldValue = if (completed) {
            FieldValue.arrayUnion(id)
        } else {
            FieldValue.arrayRemove(id)
        }
        userDayDocument(day).set(
            mapOf(
                FIELD_DAY_COMPLETED to fieldValue
            ),
            SetOptions.merge()
        )
    }

    override fun getDaysWhenHabitCompleted(id: String): MappedResult<List<Long>> {
        return userDaysCollection
            .whereArrayContains(FIELD_DAY_COMPLETED, id)
            .withMapper {
                it.map { it.id.toLong() }
            }
    }

    override fun getDay(day: Long): MappedResult<RawDay> {
        return userDaysCollection.document(day.toString())
            .withMapper {
                it.toObject<DayFirestore>()?.toRawDay() ?: RawDay(day, emptyList())
            }
    }

    override fun getAllHabits(): MappedResult<List<Habit>> {
        return userHabitsCollection.withMapper {
            it.toObjects<HabitFirestore>().map { it.toHabit() }
        }
    }

    companion object {
        private const val FIELD_DAY_COMPLETED = "completed"
    }
}

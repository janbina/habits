package com.janbina.habits.data.database

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.janbina.habits.data.repository.AuthRepository
import com.janbina.habits.models.firestore.DayFirestore
import com.janbina.habits.models.firestore.HabitFirestore
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDb @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) {

    private val userId = authRepository.currentUser().uid

    private val userDocument get() = firestore.collection(PATH_USERS).document(userId)
    private val userHabitsCollection get() = userDocument.collection(PATH_HABITS)
    private val userDaysCollection get() = userDocument.collection(PATH_DAYS)

    private fun userDayDocument(day: Int) = userDaysCollection.document(day.toString())

    fun saveHabit(id: String?, habit: HabitFirestore): Task<Void> {
        val ref = if (id != null) {
            userHabitsCollection.document(id)
        } else {
            userHabitsCollection.document()
        }

        return ref.set(habit)
    }

    fun deleteHabit(id: String): Task<Void> {
        return userHabitsCollection.document(id).delete()
    }

    fun getHabit(id:String): MappedDocument<HabitFirestore> {
        return userHabitsCollection.document(id).withMapper { it.toObject()!! }
    }

    fun changeHabitCompletionForDay(id: String, day: Int, completed: Boolean): Task<Void> {
        val fieldValue = if (completed) {
            FieldValue.arrayUnion(id)
        } else {
            FieldValue.arrayRemove(id)
        }
        return userDayDocument(day).set(
            mapOf(
                FIELD_DAY_DAY to day,
                FIELD_DAY_COMPLETED to fieldValue
            ),
            SetOptions.merge()
        )
    }

    fun getDaysWhenHabitCompleted(id: String): MappedQuery<List<Long>> {
        return userDaysCollection
            .whereArrayContains(FIELD_DAY_COMPLETED, id)
            .withMapper {
                Timber.e("ID: $id")
                Timber.e("SIZE: ${it.size()}")
                it.map {
                    it.getLong(FIELD_DAY_DAY)!!
                }
            }
    }

    fun getDays(from: Int, to: Int): MappedQuery<List<DayFirestore>> {
        return userDaysCollection
            .whereGreaterThanOrEqualTo(FIELD_DAY_DAY, from)
            .whereLessThanOrEqualTo(FIELD_DAY_DAY, to)
            .withMapper {
                it.toObjects()
            }
    }

    fun getAllHabits(): MappedQuery<List<HabitFirestore>> {
        return userHabitsCollection.withMapper { it.toObjects() }
    }

    companion object {
        private const val PATH_USERS = "users"
        private const val PATH_HABITS = "habits"
        private const val PATH_DAYS = "days"

        private const val FIELD_DAY_DAY = "day"
        private const val FIELD_DAY_COMPLETED = "completed"
    }
}
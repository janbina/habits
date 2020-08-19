package com.janbina.habits.data.repository

import com.github.kittinunf.result.Result
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObjects
import com.janbina.habits.models.HabitDay
import com.janbina.habits.models.firestore.HabitFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

typealias Res<T> = Result<T, Exception>

class HabitsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) {

    private val userCollection: DocumentReference
        get() {
            return firestore.collection("users").document(authRepository.currentUser().uid)
        }

    private val usersHabits: CollectionReference
        get() {
            return userCollection.collection("habits")
        }

    private val userDays: CollectionReference
        get() {
            return userCollection.collection("days")
        }

    fun createTask() {
        usersHabits.add(mapOf("name" to "Fukin cold shower"))
            .addOnFailureListener { Timber.e("FAIL ${it.message}") }
            .addOnSuccessListener { Timber.e("SUCCC") }
        userDays.document("2020-08-18")
            .update("completed", FieldValue.arrayUnion(System.currentTimeMillis()))

    }

    fun createHabit(name: String) {
        usersHabits.add(mapOf("name" to name))
    }

    suspend fun setHabitComplete(id: String, day: String, completed: Boolean) {
//        delay(1000)
//        Timber.e("SET HABIT COMPLETE $id, $completed, $day")
        val value = if (completed) {
            FieldValue.arrayUnion(id)
        } else {
            FieldValue.arrayRemove(id)
        }
        userDays.document(day)
            .set(mapOf("day" to day.toInt(), "completed" to value), SetOptions.merge())
    }


    @ExperimentalCoroutinesApi
    suspend fun getHabitsForDay(day: Int): Flow<Res<List<HabitDay>>> = callbackFlow {
        var daysResponse: DaysResponse? = null
        var habits: List<HabitFirestore>? = null

        fun combine() {
            val dr = daysResponse ?: return
            val h = habits ?: return

            offer(Res.success(createHabitDays(h, dr)))
        }

        val sub1 = userDays
            .whereLessThanOrEqualTo("day", day)
            .whereGreaterThan("day", day - 14)
            .orderBy("day")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    offer(Res.error(error))
                } else if (value == null) {
                    offer(Res.error(IllegalStateException()))
                } else {
                    daysResponse = createDaysResponse(value, day)
                    combine()
                }
            }

        val sub2 = usersHabits.addSnapshotListener { snapshot, error ->
            when {
                error != null -> {
                    offer(Res.error(error))
                }
                snapshot == null -> {
                    offer(Res.error(IllegalStateException()))
                }
                else -> {
                    habits = snapshot.toObjects()
                    combine()
                }
            }
        }

        awaitClose {
            sub1.remove()
            sub2.remove()
        }
    }

    private fun createDaysResponse(snap: QuerySnapshot, day: Int): DaysResponse {
        val completed = mutableSetOf<String>()
        val counts = mutableMapOf<String, Int>()

        snap.documents.forEach {
            val compl = (it.get("completed") as? List<String>) ?: emptyList()
            val d = it.getField<Int>("day") ?: 0

            if (d == day) {
                completed.addAll(compl)
            } else {
                compl.forEach {
                    counts.merge(it, 14 - day + d) { a, b -> a + b }
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
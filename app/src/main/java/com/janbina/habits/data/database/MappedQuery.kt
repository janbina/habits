package com.janbina.habits.data.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.janbina.habits.data.repository.Res
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

fun interface Closable {
    fun close()
}

abstract class MappedResult<T> {
    abstract suspend fun get(): Res<T>

    fun asFlow(): Flow<Res<T>> = callbackFlow {
        val sub = addListener { offer(it) }
        awaitClose {
            sub.close()
        }
    }

    protected abstract fun addListener(action: (Res<T>) -> Unit): Closable
}

class MappedQuery<T>(
    private val query: Query,
    private val mapper: (QuerySnapshot) -> T
) : MappedResult<T>() {

    override suspend fun get(): Res<T> {
        return try {
            Res.success(mapper(query.get().await()))
        } catch (e: Exception) {
            Res.error(e)
        }
    }

    override fun addListener(action: (Res<T>) -> Unit): Closable {
        val sub = query.addSnapshotListener { value, error ->
            if (error != null) {
                action(Res.error(error))
            } else {
                requireNotNull(value)
                action(Res.success(mapper(value)))
            }
        }
        return Closable { sub.remove() }
    }
}

class MappedDocument<T>(
    private val document: DocumentReference,
    private val mapper: (DocumentSnapshot) -> T
) : MappedResult<T>() {

    override suspend fun get(): Res<T> {
        return try {
            Res.success(mapper(document.get().await()))
        } catch (e: Exception) {
            Res.error(e)
        }
    }

    override fun addListener(action: (Res<T>) -> Unit): Closable {
        val sub = document.addSnapshotListener { value, error ->
            if (error != null) {
                action(Res.error(error))
            } else {
                requireNotNull(value)
                action(Res.success(mapper(value)))
            }
        }
        return Closable { sub.remove() }
    }
}

class MappedDatabaseReference<T>(
    private val reference: DatabaseReference,
    private val mapper: (DataSnapshot) -> T,
) : MappedResult<T>() {
    override suspend fun get(): Res<T> {
        return try {
            Res.success(reference.get().await().let(mapper))
        } catch (e: Exception) {
            Res.error(e)
        }
    }

    override fun addListener(action: (Res<T>) -> Unit): Closable  {
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                action(Res.success(mapper(snapshot)))
            }
            override fun onCancelled(error: DatabaseError) {
                action(Res.error(error.toException()))
            }
        }
        reference.addValueEventListener(listener)
        return Closable { reference.removeEventListener(listener) }
    }

}

internal fun <T> Query.withMapper(
    mapper: (QuerySnapshot) -> T
) = MappedQuery(this, mapper)

internal fun <T> DocumentReference.withMapper(
    mapper: (DocumentSnapshot) -> T
) = MappedDocument(this, mapper)

internal fun <T> DatabaseReference.withMapper(
    mapper: (DataSnapshot) -> T
) = MappedDatabaseReference(this, mapper)

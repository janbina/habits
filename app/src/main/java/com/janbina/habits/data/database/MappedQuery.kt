package com.janbina.habits.data.database

import com.google.firebase.firestore.*
import com.janbina.habits.data.repository.Res
import kotlinx.coroutines.tasks.await

interface MappedResult<T> {
    suspend fun get(): Res<T>
    fun addListener(action: (Res<T>) -> Unit): ListenerRegistration
}

class MappedQuery<T>(
    private val query: Query,
    private val mapper: (QuerySnapshot) -> T
) : MappedResult<T> {

    override suspend fun get(): Res<T> {
        return try {
            Res.success(mapper(query.get().await()))
        } catch (e: Exception) {
            Res.error(e)
        }
    }

    override fun addListener(action: (Res<T>) -> Unit): ListenerRegistration {
        return query.addSnapshotListener { value, error ->
            if (error != null) {
                action(Res.error(error))
            } else {
                requireNotNull(value)
                action(Res.success(mapper(value)))
            }
        }
    }
}

class MappedDocument<T>(
    private val document: DocumentReference,
    private val mapper: (DocumentSnapshot) -> T
) : MappedResult<T> {

    override suspend fun get(): Res<T> {
        return try {
            Res.success(mapper(document.get().await()))
        } catch (e: Exception) {
            Res.error(e)
        }
    }

    override fun addListener(action: (Res<T>) -> Unit): ListenerRegistration {
        return document.addSnapshotListener { value, error ->
            if (error != null) {
                action(Res.error(error))
            } else {
                requireNotNull(value)
                action(Res.success(mapper(value)))
            }
        }
    }
}

internal fun <T> Query.withMapper(mapper: (QuerySnapshot) -> T) = MappedQuery(this, mapper)

internal fun <T> DocumentReference.withMapper(mapper: (DocumentSnapshot) -> T) = MappedDocument(this, mapper)
package com.janbina.habits.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.janbina.habits.data.database.MappedResult

class TupleQuery<A: Any, B: Any>(
    private val aQ: MappedResult<A>,
    private val bQ: MappedResult<B>,
) {

    private var aV: Res<A>? = null
    private var bV: Res<B>? = null

    private fun combine(action: (Res<A>, Res<B>) -> Unit) {
        val a = aV ?: return
        val b = bV ?: return
        action(a, b)
    }

    fun addListener(action: (Res<A>, Res<B>) -> Unit): List<ListenerRegistration> {
        return listOf(
            aQ.addListener {
                aV = it
                combine(action)
            },
            bQ.addListener {
                bV = it
                combine(action)
            },
        )
    }
}
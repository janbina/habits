package com.janbina.habits.models.firestore

import com.google.firebase.firestore.DocumentId

data class HabitFirestore(
    @DocumentId
    val id: String = "",
    val name: String = ""
)
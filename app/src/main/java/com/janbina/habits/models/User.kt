package com.janbina.habits.models

import com.google.firebase.auth.FirebaseUser

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val photo: String,
    val anonymous: Boolean
) {
    companion object {
        fun fromFirebase(user: FirebaseUser) = User(
            user.uid,
            user.displayName.orEmpty(),
            user.email.orEmpty(),
            user.photoUrl?.toString().orEmpty(),
            user.isAnonymous
        )
    }
}
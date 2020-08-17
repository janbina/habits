package com.janbina.habits.repository

import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun loginAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.e("signInAnonymously:success ${auth.currentUser}")
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.e("signInAnonymously:failure ${task.exception}")
                }

                // ...
            }
    }

}